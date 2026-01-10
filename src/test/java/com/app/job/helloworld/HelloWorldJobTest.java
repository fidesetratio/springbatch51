package com.app.job.helloworld;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.app.job.BatchTestConfiguration;


@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest(classes = { BatchTestConfiguration.class,HelloWorldJobConfig.class}, 
properties = { "spring.batch.job.enabled=false",
				"spring.batch.job.names=helloWorldJob" })
class HelloWorldJobTest {
	 @Autowired
	    private JobLauncherTestUtils jobLauncherTestUtils;

	    @Autowired
	    private JobRepositoryTestUtils jobRepositoryTestUtils;

	    @BeforeEach
	    void cleanUp() {
	        jobRepositoryTestUtils.removeJobExecutions();
	    }

	    @Test
	    void helloWorldJob_shouldCompleteSuccessfully() throws Exception {

	        JobExecution execution =
	                jobLauncherTestUtils.launchJob(
	                        new JobParametersBuilder()
	                                .addLong("time", System.currentTimeMillis())
	                                .toJobParameters()
	                );

	        assertThat(execution.getStatus())
	                .isEqualTo(BatchStatus.COMPLETED);
	    }
  
}
