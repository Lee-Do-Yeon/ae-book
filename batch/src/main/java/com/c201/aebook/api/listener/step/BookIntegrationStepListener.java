package com.c201.aebook.api.listener.step;

import java.util.Date;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BookIntegrationStepListener implements StepExecutionListener {
	
	
	/**
	 * 스텝 구동 전에 도는 리스너
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		String stepName = stepExecution.getStepName();
		
		log.info(" ================= [ STEP START ] ================ ");
		log.info(stepName + " : before step execution");
		log.info(" ================= [ STEP START ] ================ ");
		
	}

	/**
	 * 스텝 구동 후에 도는 리스너
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		String stepName = stepExecution.getStepName();
		int commitCount = stepExecution.getCommitCount();
		int readCount = stepExecution.getReadCount();
		int writeCount = stepExecution.getWriteCount();
		int readSkipCount = stepExecution.getReadSkipCount();
		int processSkipCount = stepExecution.getProcessSkipCount();
		int writeSkipCount = stepExecution.getWriteSkipCount();
		Date startDate = stepExecution.getStartTime();
		Date endDate = stepExecution.getEndTime();
		int rollbackCount = stepExecution.getRollbackCount();
		
		log.info(" ================= [ STEP END ] ================ ");
		log.info("after step execution = " + stepExecution);
		log.info(" ================= [ STEP END ] ================ ");
		log.info(stepName + " : before step execution");
		log.info("commitCount of stepExecution : " + commitCount);
		log.info("readCount of stepExecution : " + readCount);
		log.info("writeCount of stepExecution : " + writeCount);
		log.info("readSkipCount of stepExecution(exception was thrown) : " + readSkipCount);
		log.info("processSkipCount of stepExecution(exception was thrown) : " + processSkipCount);
		log.info("writeSkipCount of stepExecution(exception was thrown) : " + writeSkipCount);
		log.info("rollbackCount of stepExecution : " + rollbackCount);
		log.info("startDate of stepExecution : " + startDate);
		log.info("endDate of stepExecution : " + endDate);
		log.info(" ================= [ STEP END ] ================ ");

		return ExitStatus.COMPLETED;
	}

}
