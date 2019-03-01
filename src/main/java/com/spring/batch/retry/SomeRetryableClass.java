package com.spring.batch.retry;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class SomeRetryableClass {

	@SuppressWarnings("null")
	@Retryable(value = { NumberFormatException.class,
			NullPointerException.class }, maxAttempts = 5, backoff = @Backoff(delay = 100, maxDelay = 500)) // ) (2000))
	public String myWeirdMethodWhichCanDitchMeAnytime1(String str) {
		System.err.println("This is expected behavior..Now you will see a NullPointer Exception......");
		str = null;
		str.length();
		return str;

	} 

	@Recover
	public void recover(NullPointerException npex) {
		System.out.println("Recover method - Null Pointer Exception");

	}

//	public String myWeirdMethodWhichCanDitchMeAnytime2() {
//		System.err.println("This is expected behavior..Now you will see a NumberFormatException Exception......"); 
//		Integer.parseInt("");
//		return "success";
//
//	}
//
//	@Recover
//	public void recover(NumberFormatException nfex) {
//		System.out.println("Recover method - Number format Exception");
//
//	}
	
	
	

}
