package com.app.job.wokerremotechunk;

import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

import com.app.tools.dummy.Person;

import lombok.RequiredArgsConstructor;


@Profile("worker")
@Configuration
@ConditionalOnProperty(
		name = "spring.batch.job.names", 
		havingValue = WorkerRemoteChunkJob.WORKERCHUNK
		)
@RequiredArgsConstructor
public class WorkerRemoteChunkJob {
	
	@Autowired
	private BeanFactory beanFactory;

    public static final String WORKERCHUNK = "workerchunk";

    @Bean
    public PollableChannel requests() {
        return new QueueChannel();
    }

    @Bean
    public MessageChannel replies() {
        return new DirectChannel();
    }

    // ===== KAFKA INBOUND =====
    @Bean
    public IntegrationFlow workerInboundFlow(
            ConsumerFactory<String, Object> consumerFactory) {

        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(
                        consumerFactory, "chunk.requests"))
                .log(LoggingHandler.Level.WARN)
                .channel(requests())
                .get();
    }
    
 

    // ===== WORKER BUILDER (INI KUNCI) =====
    @Bean
    public IntegrationFlow workerChunkProcessingFlow() {
        return new RemoteChunkingWorkerBuilder<Person, Person>()
        	 
                .inputChannel(requests())
                .outputChannel(replies())
                .itemProcessor(itemProcessor())
                .itemWriter(itemWriter())
                .build();
    }

    // ===== KAFKA OUTBOUND =====
    @Bean
    public IntegrationFlow workerOutboundFlow(
            KafkaTemplate<String, Object> kafkaTemplate) {
    	KafkaProducerMessageHandler producerMessageHandler = new KafkaProducerMessageHandler<String, Object>(kafkaTemplate);
         producerMessageHandler.setTopicExpression(new LiteralExpression("chunk.replies"));
        return IntegrationFlow
                .from(replies())
                .log(LoggingHandler.Level.WARN)
                .handle(producerMessageHandler)
                .get();
    }

    // ===== BUSINESS LOGIC =====
    @Bean
    public ItemProcessor<Person, Person> itemProcessor() {
        return item -> {
            System.out.println("Worker processing: " + item.getName());
            return item;
        };
    }

    @Bean
    public ItemWriter<Person> itemWriter() {
        return items -> {
            // NO-OP
            // hasil akan dikirim balik ke master
        };
    }
}
