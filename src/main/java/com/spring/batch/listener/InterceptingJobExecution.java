package com.spring.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class InterceptingJobExecution implements JobExecutionListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.err.println("Performing another operation - Before Job!");
		log.info("Intercepting Job Excution - Before Job!");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
//		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
//			// job success
//		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
//			// job failure
//		}
		System.err.println("Performing another operation - After Job!");
		log.info("Intercepting Job Excution - After Job!");
	}

}