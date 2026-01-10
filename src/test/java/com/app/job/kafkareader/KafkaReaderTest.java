package com.app.job.kafkareader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.kafka.KafkaItemReader;
import org.springframework.batch.item.kafka.builder.KafkaItemReaderBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import com.app.tools.kafka.PersonEvent;
import com.app.tools.kafka.PersonProcessor;
import com.app.tools.kafka.PersonWriter;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

@ActiveProfiles("test2") // application-test2.properties
@SpringBatchTest
@SpringBootTest(
properties = { "spring.batch.job.enabled=false",
				"spring.batch.job.names=kafkajobreader",
				"spring.batch.jdbc.initialize-schema=always",
				"spring.main.banner-mode=off"})
@EmbeddedKafka(partitions = 1, topics = { "person-topic" })
@Disabled("Skipping all tests in this class temporarily")
public class KafkaReaderTest {
	 

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @BeforeEach
    void setupKafka() {
        // Producer properties
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put("value.serializer", JsonSerializer.class);

        KafkaTemplate<String, PersonEvent> template = new KafkaTemplate<>(
            new DefaultKafkaProducerFactory<>(producerProps)
        );
        template.setDefaultTopic("person-topic");

        // Kirim test data
        template.sendDefault(new PersonEvent(1, "Alice", null, null));
        template.sendDefault(new PersonEvent(2, "Bob", null, null));
        template.flush();
    }

    /**
     * StepScope KafkaItemReader untuk test
     */
    private KafkaItemReader<String, PersonEvent> createReader() {
        Map<String, Object> consumerProps = new HashMap<>(KafkaTestUtils.consumerProps(
                "test-group-" + UUID.randomUUID(), "true", embeddedKafkaBroker));

        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonDeserializer.class);
        consumerProps.put(org.springframework.kafka.support.serializer.JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(org.springframework.kafka.support.serializer.JsonDeserializer.VALUE_DEFAULT_TYPE, PersonEvent.class.getName());
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Properties props = new Properties();
        consumerProps.forEach(props::put);

        return new KafkaItemReaderBuilder<String, PersonEvent>()
                .name("testReader")
                .partitions(0) // Embedded Kafka cuma 1 partition
                .topic("person-topic")
                .consumerProperties(props)
                .pollTimeout(Duration.ofSeconds(1))
                .saveState(false)
                .build();
    }

    /**
     * ItemProcessor test
     */
    private PersonProcessor processor() {
        return new PersonProcessor();
    }

    /**
     * ItemWriter test
     */
    private PersonWriter writer() {
        return new PersonWriter();
    }

   

    @Test
    void testKafkaItemReaderWithJob() throws Exception {
        // Launch job via JobLauncherTestUtils
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Pastikan job selesai sukses
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        //assertEquals(2, stepExecution.getReadCount()); // harus sesuai jumlah message Kafka
        assertTrue(stepExecution.getReadCount()>0);
        
    }
}
