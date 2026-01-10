package com.app.job.kafkareader;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.kafka.KafkaItemReader;
import org.springframework.batch.item.kafka.builder.KafkaItemReaderBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.transaction.PlatformTransactionManager;

import com.app.tools.dummy.Person;
import com.app.tools.kafka.PersonEvent;
import com.app.tools.kafka.PersonProcessor;
import com.app.tools.kafka.PersonWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@ConditionalOnProperty(
		name = "spring.batch.job.names", 
		havingValue = KafkaReaderJobConfig.KAFKAJOB
		)
@RequiredArgsConstructor
public class KafkaReaderJobConfig {
	
	  public static final String KAFKAJOB = "kafkajobreader";

	    private final JobRepository jobRepository;
	    private final PlatformTransactionManager transactionManager;

	    /**
	     * KafkaItemReader bean with step scope. Spring injects KafkaProperties automatically.
	     */
	    @Bean
	    @StepScope
	    public KafkaItemReader<String, PersonEvent> personKafkaReader(KafkaProperties kafkaProperties) {

	        // Convert KafkaProperties to Properties for KafkaItemReader
	        Map<String, Object> map = new HashMap<>(kafkaProperties.buildConsumerProperties());
	        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
	        map.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
	        map.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
	        map.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
	        map.put(ConsumerConfig.GROUP_ID_CONFIG, "batch-person-consumer-" + UUID.randomUUID());
	        

	        Properties props = new Properties();
	        map.forEach(props::put);
	        
	     // ⚡ Provide partitions explicitly
	    
	        System.out.println("jalan ngak sih ini");
	        
	        try (org.apache.kafka.clients.consumer.KafkaConsumer<String, PersonEvent> consumer =
	                new org.apache.kafka.clients.consumer.KafkaConsumer<>(props)) {
	           int[] partitions = consumer.partitionsFor("person-topic").stream()
	                                      .mapToInt(p -> p.partition())
	                                      .toArray();
	          
	           List<Integer> i = new ArrayList<>();
	           for(int k=0; k<partitions.length;k++) {
	        	   i.add(partitions[k]);
	           }
	   

	        return new KafkaItemReaderBuilder<String, PersonEvent>()
	                .name(KAFKAJOB)
	                .partitions(i)
	                .topic("person-topic")
	                .consumerProperties(props)
	                .pollTimeout(Duration.ofSeconds(1))
	                .saveState(true) // required for restart
	                .build();
	        
	    }
	    }
	    /**
	     * ItemProcessor bean
	     */
	    @Bean
	    public ItemProcessor<PersonEvent, Person> personProcessor() {
	        return new PersonProcessor();
	    }

	    /**
	     * ItemWriter bean
	     */
	    @Bean
	    public ItemWriter<Person> personWriter() {
	        return new PersonWriter();
	    }

	    /**
	     * Step bean
	     * Inject the step-scoped KafkaItemReader properly (do not call the method directly)
	     */
	    @Bean
	    public Step personKafkaStep(KafkaItemReader<String, PersonEvent> personKafkaReader) {
	        return new StepBuilder("personKafkaStep", jobRepository)
	                .<PersonEvent, Person>chunk(1, transactionManager)
	                .reader(personKafkaReader)
	                .processor(personProcessor())
	                .writer(personWriter())
	                .build();
	    }
	
	    
	    @Bean
	    public Job personKafkaJob(Step personKafkaStep) {
	        return new JobBuilder("personKafkaJob", jobRepository)
	                .start(personKafkaStep)
	                .build();
	    }
	 

}
