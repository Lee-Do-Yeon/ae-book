package com.c201.aebook.api.listener.step;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SuccessStepListener implements StepExecutionListener {
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
	}

	/**
	 * 이 리스너를 이용해 스텝을 구성하면 스텝이 실패하지 않음
	 * 다만, 스텝이 실패하더라도 배치 잡이 중단되지 않고 뒷 스텝이 진행되므로
	 * 앞 스텝이 실패했을 때 뒷 스텝이 진행되어 발생하는 문제를 예측할 수 없음
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if(!ExitStatus.COMPLETED.equals(stepExecution.getExitStatus())) {
			stepExecution.setExitStatus(ExitStatus.COMPLETED);
		}
		if(!BatchStatus.COMPLETED.equals(stepExecution.getStatus())) {
			stepExecution.setStatus(BatchStatus.COMPLETED);
		}
		return ExitStatus.COMPLETED;
	}

}
