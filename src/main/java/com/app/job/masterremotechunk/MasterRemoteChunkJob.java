package com.app.job.masterremotechunk;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.transaction.PlatformTransactionManager;

import com.app.tools.dummy.JobMonitoringListener;
import com.app.tools.dummy.Person;

import lombok.RequiredArgsConstructor;

@Profile("master")
@Configuration
@ConditionalOnProperty(
		name = "spring.batch.job.names", 
		havingValue = MasterRemoteChunkJob.MASTERCHUNK
		)
@RequiredArgsConstructor
public class MasterRemoteChunkJob {
	public static final String MASTERCHUNK = "masterchunk";

    
    
    @Bean
    public ItemReader<Person> itemReader() {
        List<Person> data = new ArrayList<>();

        for (Integer i = 1; i <= 10000; i++) {
        	System.out.println("reader begin oii"+i);
            data.add(new Person(i, "Patar"+i,i));
        }

        return new ListItemReader<>(data);
    }
    
    
    @Bean
    public DirectChannel requests() {
        return new DirectChannel();   // ✅ OK
    }
    

    @Bean
    public IntegrationFlow masterOutboundFlow(KafkaTemplate<String, Object> kafkaTemplate) {
       
    	KafkaProducerMessageHandler producerMessageHandler = new KafkaProducerMessageHandler<String, Object>(kafkaTemplate);
        producerMessageHandler.setTopicExpression(new LiteralExpression("chunk.requests"));
        
        
        
    	
    	return IntegrationFlow
                .from(requests())
                .log(LoggingHandler.Level.WARN)
                .handle(producerMessageHandler)
                .get();
    }
    
    
    
    @Bean
    public QueueChannel replies() {
        return new QueueChannel();    // ✅ REQUIRED
    }
    
    @Bean
    public IntegrationFlow masterInboundReplies(
            ConsumerFactory<String, Object> consumerFactory) {

        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(
                        consumerFactory, "chunk.replies"))
                .log(LoggingHandler.Level.WARN)
                .channel(replies())
                .get();
    }
    
    

    
 

        @Bean
        public Step masterStep(
                JobRepository jobRepository,
                ItemReader<Person> itemReader,
                PlatformTransactionManager resourcelessTransactionManager
              ) {

            return new RemoteChunkingManagerStepBuilder<Person, Person>(MASTERCHUNK,
                    jobRepository)
            		.chunk(500)
            	    .reader(itemReader)
            	    .outputChannel(requests())
            	    .inputChannel(replies())
            	    .transactionManager(resourcelessTransactionManager)
            	    .build();
        }
        
        
        @Bean
        public PlatformTransactionManager resourcelessTransactionManager() {
            return new ResourcelessTransactionManager();
        }
     

      
 

        
        
        @Bean
        public Job masterJob(
                JobRepository jobRepository,
                Step masterStep,
                JobMonitoringListener listener) {

            return new JobBuilder(MASTERCHUNK, jobRepository)
                    .start(masterStep)
                    .listener(listener)
                    .build();
        }

      
    
    @Bean
    public ProducerFactory<String, Object> producerFactory(
            KafkaProperties kafkaProperties) {

        return new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildProducerProperties()
        );
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {
    	KafkaTemplate<String, Object> template =
                new KafkaTemplate<>(producerFactory);
        return template;
    }
}
