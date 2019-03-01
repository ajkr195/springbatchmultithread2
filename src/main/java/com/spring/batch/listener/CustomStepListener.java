package com.spring.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;
@Component
public class CustomStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.err.println("StepExecutionListener - beforeStep");
//		JobParameters parameters = stepExecution.getJobExecution().getJobParameters();
//		parameters.getParameters();
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.err.println("StepExecutionListener - afterStep");
		return null;
	}

}