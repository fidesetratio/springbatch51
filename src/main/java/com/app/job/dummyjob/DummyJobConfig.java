package com.app.job.dummyjob;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.app.tools.dummy.JobMonitoringListener;
import com.app.tools.dummy.Person;
import com.app.tools.dummy.PersonItemProcessor;
import com.app.tools.dummy.PersonItemReader;
import com.app.tools.dummy.PersonItemWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@ConditionalOnProperty(
		name = "spring.batch.job.names", 
		havingValue = DummyJobConfig.DUMMYJOB
		)
@RequiredArgsConstructor
public class DummyJobConfig {
	 public static final String DUMMYJOB = "dummyJob";
	
	    private final JobRepository jobRepository;
	    private final PlatformTransactionManager transactionManager;
	 

	    
	    
	    @Bean
	    public Step personStep() {
	        return new StepBuilder(DUMMYJOB, jobRepository)
	                .<Person, Person>chunk(2, transactionManager)
	                .reader(personReader())
	                .processor(personProcessor())
	                .writer(personWriter())
	                .faultTolerant()
	                .skip(RuntimeException.class)
	                .skipLimit(5)
	                .retry(RuntimeException.class)
	                .retryLimit(3)
	                .build();
	    }
	 
	    @Bean
	    public Job personJob(JobMonitoringListener listener) {
	        return new JobBuilder("personJob", jobRepository)
	                .listener(listener)
	                .start(personStep())
	                .build();
	    }
	 
	 
	    @Bean
	    @StepScope
	    public ItemReader<Person> personReader() {
	        return new PersonItemReader(List.of(
	                new Person(1, "Andi", 25),
	                new Person(2, "Budi", 17),
	                new Person(3, "Citra", 30)
	        ));
	    }

	    @Bean
	    public ItemProcessor<Person, Person> personProcessor() {
	        return new PersonItemProcessor();
	    }

	    @Bean
	    public ItemWriter<Person> personWriter() {
	        return new PersonItemWriter();
	    }
}
