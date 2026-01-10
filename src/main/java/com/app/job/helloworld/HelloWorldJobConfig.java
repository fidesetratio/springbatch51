package com.app.job.helloworld;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnProperty(
		name = "spring.batch.job.names", 
		havingValue = HelloWorldJobConfig.HELLOWORLDJOB
		)
public class HelloWorldJobConfig {
	  public static final String HELLOWORLDJOB = "helloWorldJob";
	  
	  
	  	@Bean
	    public Job helloWorldJob(
	            JobRepository jobRepository,
	            Step helloWorldStep) {
	        return new JobBuilder(HELLOWORLDJOB, jobRepository)
	                .start(helloWorldStep)
	                .build();
	    }
	  	
	  	 @Bean
	     public Step helloWorldStep(
	             JobRepository jobRepository,
	             PlatformTransactionManager transactionManager) {

	         return new StepBuilder("helloWorldStep", jobRepository)
	                 .tasklet((contribution, chunkContext) -> {
	                     System.out.println("👋 Hello World from Spring Batch 5!");
	                     return org.springframework.batch.repeat.RepeatStatus.FINISHED;
	                 }, transactionManager)
	                 .build();
	     }
	  
	  
	  
	

}
