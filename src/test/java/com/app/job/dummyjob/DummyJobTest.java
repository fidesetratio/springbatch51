package com.app.job.dummyjob;


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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.app.job.BatchTestConfiguration;
import com.app.tools.dummy.JobMonitoringListener;

@ActiveProfiles("test2")
@SpringBatchTest
@SpringBootTest(
properties = { "spring.batch.job.enabled=false",
				"spring.batch.job.names=dummyJob",
				"spring.batch.jdbc.initialize-schema=always",
				"spring.main.banner-mode=off"})
public class DummyJobTest {
	@Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

  
    
    @MockitoBean
    private JobMonitoringListener jobMonitoringListener; // ✅ FIX



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
