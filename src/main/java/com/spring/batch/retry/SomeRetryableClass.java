package com.spring.batch.retry;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class SomeRetryableClass {
	private int retrynpecounter = 0;
	private int retrynfecounter = 0;

	@Retryable(value = { NumberFormatException.class,
			NullPointerException.class }, maxAttempts = 5, backoff = @Backoff(delay = 1000, maxDelay = 3000)) // )
																												// (2000))
	public String myWeirdMethodWhichCanDitchMeAnytime1(String str) throws NullPointerException {
		retrynpecounter++;
		System.err.println("Trying to recover from Catastrophy...Retry attempt number - " + retrynpecounter);
		if (str == null) {
			throw new NullPointerException();
		} else {
			throw new RuntimeException();
		}

	}

	@Recover
	public String recover(NullPointerException npex) {
		System.err.println("\n##########################################################");
		System.err.println("In Recover method - To recover from Null Pointer Exception");
		System.err.println("##########################################################\n");
		return npex.getClass().getName();

	}

	@Retryable(value = { NumberFormatException.class,
			NullPointerException.class }, maxAttempts = 5, backoff = @Backoff(delay = 1000, maxDelay = 3000)) // )
																												// (2000))
	public String myWeirdMethodWhichCanDitchMeAnytime2() throws NumberFormatException {
		retrynfecounter++;
		System.err.println("Trying to recover from Catastrophy...Retry attempt number - " + retrynfecounter);
		Integer.parseInt("");
		return "success";

	}

	@Recover
	public String recover(NumberFormatException nfex) {
		System.err.println("\n###########################################################");
		System.err.println("In Recover method - To recover from Number format Exception");
		System.err.println("###########################################################\n");
		return nfex.getClass().getName();

	}

}
