package com.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

import com.spring.batch.retry.SomeRetryableClass;
 
@EnableTask
@EnableBatchProcessing
@SpringBootApplication
//@EnableScheduling
@EnableRetry
@ComponentScan({ "com.spring.batch.*" })
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		SomeRetryableClass bean = context.getBean(SomeRetryableClass.class);
		try {
			System.err.println("Catastrophic condition...NullPointerException");
			bean.myWeirdMethodWhichCanDitchMeAnytime1(null);
			System.err.println("Recovered");
		} catch (Exception e) {
			System.err.println("Not recovered: " + e);
		}
		try {
			System.err.println("Catastrophic condition...NumberFormatException");
			bean.myWeirdMethodWhichCanDitchMeAnytime2();
			System.err.println("Recovered");
		} catch (Exception e) {
			System.err.println("Not recovered: " + e);
		}
	}

}
