package com.c201.aebook.api.listener.job;

import java.util.Date;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BookIntegrationJobListener implements JobExecutionListener {
	
	/**
	 * 잡의 구동 전에 도는 리스너
	 */
	@Override
	public void beforeJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName();
		log.info(" ================= [ JOB START ] ================ ");
		log.info(jobName + " : before job execution");
		log.info(" ================= [ JOB START ] ================ ");
	}

	/**
	 * 잡의 구동 이후에 도는 리스너
	 */
	@Override
	public void afterJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName();
		long jobInstanceId = jobExecution.getJobInstance().getInstanceId();
		Date jobExecutionCreateTime = jobExecution.getCreateTime();
		Date jobExecutionStartTime = jobExecution.getStartTime();
		Date jobExecutionEndTime = jobExecution.getEndTime();
		
		log.info(" ================= [ JOB END ] ================ ");
		log.info("after job execution = " + jobExecution);
		log.info(" ================= [ JOB END ] ================ ");
		log.info("jobName : " + jobName);
		log.info("jobInstanceId : " + jobInstanceId);
		log.info("jobExecutionCreateTime : " + jobExecutionCreateTime);
		log.info("jobExecutionStartTime : " + jobExecutionStartTime);
		log.info("jobExecutionEndTime : " + jobExecutionEndTime);
		if(jobExecution.getStatus() == BatchStatus.COMPLETED){
			//job success
			log.info("job success => " + jobExecution.getJobInstance().getJobName());
		}else{
			//job failure
			log.info("job failed =>" + jobExecution.getJobInstance().getJobName());
		}
		log.info(" ================= [ JOB END ] ================ ");
		System.exit(0);
	}

}
