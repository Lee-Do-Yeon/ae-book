package com.c201.aebook.config;

import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;

import com.c201.aebook.api.batch.AladinBatchItemReader;
import com.c201.aebook.api.batch.AladinBatchItemWriter;
import com.c201.aebook.api.book.persistence.entity.BookEntity;
import com.c201.aebook.api.listener.job.BookIntegrationJobListener;
import com.c201.aebook.api.listener.step.BookIntegrationStepListener;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchJobConfig {

	private static final int CHUNK_SIZE = 1000;
	// job, step naming
	private static final String BOOK_INTEGRATION_JOB = "bookIntegrationJob";
	private static final String BOOK_INTEGRATION_STEP = "bookIntegrationStep";
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final BookIntegrationJobListener bookIntegrationJobListener;
	private final BookIntegrationStepListener bookIntegrationStepListener;
	private final AladinBatchItemReader aladinBatchItemReader;
	private final AladinBatchItemWriter aladinBatchItemWriter;

	@Bean
	public Job job() {
		return jobBuilderFactory.get(BOOK_INTEGRATION_JOB)
			.incrementer(new RunIdIncrementer())
			.start(step())
			.listener(bookIntegrationJobListener)
			.build();
	}

	@Bean
	public Step step() {
		return stepBuilderFactory.get(BOOK_INTEGRATION_STEP)
			.startLimit(5)//재시작 5번 가능
			.<BookEntity, BookEntity>chunk(CHUNK_SIZE)
			.reader(aladinBatchItemReader)
			.writer(aladinBatchItemWriter)
			.faultTolerant()
			.retryLimit(3) //재시도 3번 가능
			.retry(Exception.class)
			.skip(Exception.class)
			.skipLimit(100)
			.listener(bookIntegrationStepListener)
			.build();
	}

}
