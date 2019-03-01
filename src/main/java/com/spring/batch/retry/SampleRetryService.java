package com.spring.batch.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class SampleRetryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SampleRetryService.class);
	private static int COUNTER = 0;

	@Retryable(value = { NumberFormatException.class,
			NullPointerException.class }, maxAttempts = 5, backoff = @Backoff(delay = 1000, maxDelay = 2000)) 
	public String retryWhenException() throws NumberFormatException, NullPointerException {
		COUNTER++;
		System.err.println("COUNTER = " + COUNTER);
		LOGGER.info("COUNTER = " + COUNTER);
		if (COUNTER == 1)
			throw new NumberFormatException();
		else if (COUNTER == 2)
			throw new NullPointerException();
		else
			throw new RuntimeException();
	}

	@Recover
	public String recover(Throwable t) {
		System.err.println("Recovering from the catastrophy....!!");
		LOGGER.info("Recovering from the catastrophy....");
		System.err.println("Recovered....!!");
		return "Error Class :: " + t.getClass().getName();
	}
}