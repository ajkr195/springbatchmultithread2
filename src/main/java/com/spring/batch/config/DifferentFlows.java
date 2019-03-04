package com.spring.batch.config;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

public class DifferentFlows {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	// Sequential Flow - Step A > Step B > Step C
	@Bean
	public Job job1() {
		return this.jobBuilderFactory.get("job").start(stepA()).next(stepB()).next(stepC()).build();
	}

	// Success on Step A > Step B ... if failed then go to Step C
	@Bean
	public Job job2() {
		return this.jobBuilderFactory.get("job").start(stepA()).on("*").to(stepB()).from(stepA()).on("FAILED")
				.to(stepC()).end().build();
	}

	@Bean
	public Job job3() {
		return this.jobBuilderFactory.get("job").start(step1()).on("FAILED").end().from(step1())
				.on("COMPLETED WITH SKIPS")
				// .to(errorPrint1())
				// .from(step1()).on("*")
				.to(step2()).end().build();
	}

	public class SkipCheckingListener extends StepExecutionListenerSupport {
		public ExitStatus afterStep(StepExecution stepExecution) {
			String exitCode = stepExecution.getExitStatus().getExitCode();
			if (!exitCode.equals(ExitStatus.FAILED.getExitCode()) && stepExecution.getSkipCount() > 0) {
				return new ExitStatus("COMPLETED WITH SKIPS");
			} else {
				return null;
			}
		}
	}

	// if step2 fails, then the Job stops with a BatchStatus of COMPLETED and an
	// ExitStatus of COMPLETED and step3 does not run. Otherwise, execution moves to
	// step3. Note that if step2 fails, the Job is not restartable (because the
	// status is COMPLETED).
	@Bean
	public Job job4() {
		return this.jobBuilderFactory.get("job").start(step1()).next(step2()).on("FAILED").end().from(step2()).on("*")
				.to(step3()).end().build();
	}

	// Configuring a step to fail at a given point instructs a Job to stop with a
	// BatchStatus of FAILED. Unlike end, the failure of a Job does not prevent the
	// Job from being restarted.

	// In the following scenario, if step2 fails, then the Job stops with a
	// BatchStatus of FAILED and an ExitStatus of EARLY TERMINATION and step3 does
	// not execute. Otherwise, execution moves to step3. Additionally, if step2
	// fails and the Job is restarted, then execution begins again on step2.

	@Bean
	public Job job5() {
		return this.jobBuilderFactory.get("job").start(step1()).next(step2()).on("FAILED").fail().from(step2()).on("*")
				.to(step3()).end().build();
	}

//	Stopping a Job at a Given Step
//	Configuring a job to stop at a particular step instructs a Job to stop with a BatchStatus of STOPPED. Stopping a Job can provide a temporary break in processing, so that the operator can take some action before restarting the Job.
//	When using java configuration, the stopAndRestart method requires a 'restart' attribute that specifies the step where execution should pick up when the "Job is restarted".
//	In the following scenario, if step1 finishes with COMPLETE, then the job stops. Once it is restarted, execution begins on step2.

	@Bean
	public Job job6() {
		return this.jobBuilderFactory.get("job").start(step1()).on("COMPLETED").stopAndRestart(step2()).end().build();
	}

//	Programmatic Flow
//	Decisions In some situations, more information than	the ExitStatus may be	required to decide which step to
//	execute next.In this case, 	a JobExecutionDecider	can be 	used to assist in	the decision, as shown in the following example:

	public class MyDecider implements JobExecutionDecider {
		@SuppressWarnings("unused")
		public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
			String status;
			String A = null;
			String B = null;
			if (A == B) { // somecondition()
				status = "FAILED";
			} else {
				status = "COMPLETED";
			}
			return new FlowExecutionStatus(status);
		}
	}

//	In the following example, a bean implementing the JobExecutionDecider is passed directly to the next call when using Java configuration.
	@Bean
	public Job job7() {
		return this.jobBuilderFactory.get("job").start(step1())
				// .next(decider())
				.on("FAILED").to(step2())
				// .from(decider())
				.on("COMPLETED").to(step3()).end().build();
	}

//	Split Flows
//	Every scenario described so far has involved a Job that executes its steps one at a time in a linear fashion. In addition to this typical style, Spring Batch also allows for a job to be configured with parallel flows.
//	Java based configuration lets you configure splits through the provided builders. As the following example shows, the 'split' element contains one or more 'flow' elements, where entire separate flows can be defined. A 'split' element may also contain any of the previously discussed transition elements, such as the 'next' attribute or the 'next', 'end' or 'fail' elements.

	@Bean
	public Job job8() {
		Flow flow1 = new FlowBuilder<SimpleFlow>("flow1").start(step1()).next(step2()).build();
		Flow flow2 = new FlowBuilder<SimpleFlow>("flow2").start(step3()).build();

		return this.jobBuilderFactory.get("job").start(flow1).split(new SimpleAsyncTaskExecutor()).add(flow2)
				.next(step4()).end().build();
	}

//	Externalizing Flow Definitions and Dependencies Between Jobs
//	Part of the flow in a job can be externalized as a separate bean definition and then re-used. There are two ways to do so. The first is to simply declare the flow as a reference to one defined elsewhere, as shown in the following example:
	@Bean
	public Job job9() {
		return this.jobBuilderFactory.get("job").start(flow1()).next(step3()).end().build();
	}

	@Bean
	public Flow flow1() {
		return new FlowBuilder<SimpleFlow>("flow1").start(step1()).next(step2()).build();
	}

//	The effect of defining an external flow as shown in the preceding example is to insert the steps from the external flow into the job as if they had been declared inline. In this way, many jobs can refer to the same template flow and compose such templates into different logical flows. This is also a good way to separate the integration testing of the individual flows.
//	The other form of an externalized flow is to use a JobStep. A JobStep is similar to a FlowStep but actually creates and launches a separate job execution for the steps in the flow specified.
//	The following Java snippet shows an example of a JobStep:

	@Bean
	public Job jobStepJob() {
		return this.jobBuilderFactory.get("jobStepJob").start(jobStepJobStep1(null)).build();
	}

	@Bean
	public Step jobStepJobStep1(JobLauncher jobLauncher) {
		return this.stepBuilderFactory.get("jobStepJobStep1").job(job()).launcher(jobLauncher)
				.parametersExtractor(jobParametersExtractor()).build();
	}

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("job").start(step1()).build();
	}

	@Bean
	public DefaultJobParametersExtractor jobParametersExtractor() {
		DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();

		extractor.setKeys(new String[] { "input.file" });

		return extractor;
	}

	// Dummy Steps

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").<String, String>chunk(10)// .tasklet(new TaskOne())
				// .listener(stepListner)
				.build();
	}

	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3").<String, String>chunk(10)// .tasklet(new TaskTwo())
				// .listener(stepListner)
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step4").<String, String>chunk(10)// .tasklet(new TaskThree())
				// .listener(stepListner)
				.build();
	}

	@Bean
	public Step step4() {
		return stepBuilderFactory.get("step4").<String, String>chunk(10)// .tasklet(new TaskThree())
				// .listener(stepListner)
				.build();
	}

	@Bean
	public Step stepA() {
		return stepBuilderFactory.get("step2").<String, String>chunk(10)// .tasklet(new TaskOne())
				// .listener(stepListner)
				.build();
	}

	@Bean
	public Step stepB() {
		return stepBuilderFactory.get("step3").<String, String>chunk(10)// .tasklet(new TaskTwo())
				// .listener(stepListner)
				.build();
	}

	@Bean
	public Step stepC() {
		return stepBuilderFactory.get("step4").<String, String>chunk(10)// .tasklet(new TaskThree())
				// .listener(stepListner)
				.build();
	}

}
