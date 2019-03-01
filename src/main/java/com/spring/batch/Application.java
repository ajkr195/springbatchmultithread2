package com.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

import com.spring.batch.retry.SampleRetryService;
 
@EnableTask
@EnableBatchProcessing
@SpringBootApplication
//@EnableScheduling
@EnableRetry
@ComponentScan({ "com.spring.batch.*" })
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		SampleRetryService bean = context.getBean(SampleRetryService.class);
		bean.retryWhenException();
		
	}

}

//SomeRetryableClass bean = context.getBean(SomeRetryableClass.class);
//try {
//	bean.myWeirdMethodWhichCanDitchMeAnytime1("SomeString");
//	System.out.println("Recovered");
//} catch (Exception e) {
//	System.out.println("Not recovered: " + e);
//}
//try {
//	bean.myWeirdMethodWhichCanDitchMeAnytime1(null);
//} catch (Exception e) {
//	System.out.println("Not recovered: " + e);
//}
//
//Foo mybean = context.getBean(Foo.class);
//try {
//	mybean.out("test");
//	System.out.println("Recovered");
//} catch (Exception e) {
//	System.out.println("Not recovered: " + e);
//}
//try {
//	mybean.out("bar");
//} catch (Exception e) {
//	System.out.println("Not recovered: " + e);
//}
//}
//
//}

