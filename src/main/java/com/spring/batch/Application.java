package com.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.spring.batch.retry.SomeRetryableClass;
 
@EnableTask
@EnableBatchProcessing
@SpringBootApplication
//@EnableScheduling
@ComponentScan({ "com.spring.batch.*" })
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		SomeRetryableClass bean = context.getBean(SomeRetryableClass.class);
		try {
			System.err.println("\nCatastrophic condition...NullPointerException\n");
			bean.myWeirdMethodWhichCanDitchMeAnytime1(null);
			System.err.println("\n   >>>   Recovered from NullPointerException   <<<   \n");
		} catch (Exception e) {
			System.err.println("\n   !!!   Not recovered: " + e + "   !!!   \n");
		}
		try {
			System.err.println("\nCatastrophic condition...NumberFormatException\n");
			bean.myWeirdMethodWhichCanDitchMeAnytime2();
			System.err.println("\n   >>>   Recovered from NumberFormatException   <<<   \n");
		} catch (Exception e) {
			System.err.println("\n   !!!   Not recovered: " + e + "   !!!   \n");
		}
	}

}
