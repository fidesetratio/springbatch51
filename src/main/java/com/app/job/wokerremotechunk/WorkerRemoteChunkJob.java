package com.app.job.wokerremotechunk;

import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

import lombok.RequiredArgsConstructor;

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
                .channel(requests())
                .get();
    }
    
 

    // ===== WORKER BUILDER (INI KUNCI) =====
    @Bean
    public IntegrationFlow workerChunkProcessingFlow() {
        return new RemoteChunkingWorkerBuilder<Object, Object>()
        	 
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

        return IntegrationFlow
                .from(replies())
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate)
                        .topic("chunk.replies"))
                .get();
    }

    // ===== BUSINESS LOGIC =====
    @Bean
    public ItemProcessor<Object, Object> itemProcessor() {
        return item -> {
            System.out.println("Worker processing: " + item);
            return item.toString().toUpperCase();
        };
    }

    @Bean
    public ItemWriter<Object> itemWriter() {
        return items -> {
            // NO-OP
            // hasil akan dikirim balik ke master
        };
    }
}
