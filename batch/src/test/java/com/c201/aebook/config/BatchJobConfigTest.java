package com.c201.aebook.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.c201.aebook.api.batch.AladinBatchItemReader;
import com.c201.aebook.api.batch.AladinBatchItemWriter;
import com.c201.aebook.api.listener.job.BookIntegrationJobListener;
import com.c201.aebook.api.listener.step.BookIntegrationStepListener;

@ExtendWith(MockitoExtension.class)
@SpringBatchTest
public class BatchJobConfigTest {

	// @Mock
	// private JobBuilderFactory jobBuilderFactory;
	//
	// @Mock
	// private StepBuilderFactory stepBuilderFactory;
	//
	// @Mock
	// private BookIntegrationJobListener bookIntegrationJobListener;
	//
	// @Mock
	// private BookIntegrationStepListener bookIntegrationStepListener;
	//
	// @Mock
	// private AladinBatchItemReader aladinBatchItemReader;
	//
	// @Mock
	// private AladinBatchItemWriter aladinBatchItemWriter;
	//
	// @Autowired
	// private JobRepository jobRepository;
	//
	// @InjectMocks
	// private BatchJobConfig batchJobConfig;
	//
	// @Autowired
	// private JobLauncherTestUtils jobLauncherTestUtils;
	//
	// @BeforeEach
	// public void setUp() throws Exception {
	// 	MockitoAnnotations.openMocks(this);
	// 	jobLauncherTestUtils.setJobRepository(jobRepository);
	// }
	//
	// @Test
	// void job() throws Exception {
	//
	// 	//given
	// 	Job job = batchJobConfig.job();
	// 	JobParameters jobParameters = new JobParameters();
	//
	// 	//when
	// 	JobExecution jobExecution = this.jobLauncherTestUtils.launchJob();
	//
	// 	//then
	// 	Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
	// }
	//
	// @Test
	// void step() throws Exception {
	// 	// given
	// 	JobExecution jobExecution = this.jobLauncherTestUtils.launchStep("bookIntegrationStep");
	//
	// 	// when
	// 	StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
	//
	// 	// then
	// 	Assertions.assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());
	// }

}