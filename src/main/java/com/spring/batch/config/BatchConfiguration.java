package com.spring.batch.config;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
//import org.springframework.batch.item.ItemWriter; 
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.spring.batch.itemprocessor.SalesItemProcessor;
import com.spring.batch.listener.CustomItemReaderListener;
import com.spring.batch.listener.CustomItemWriterListener;
import com.spring.batch.listener.CustomStepListener;
import com.spring.batch.listener.InterceptingJobExecution;
import com.spring.batch.listener.JobCompletionNotificationListener;
import com.spring.batch.model.Sales;
import com.spring.batch.tasklets.TaskOne;
import com.spring.batch.tasklets.TaskThree;
import com.spring.batch.tasklets.TaskTwo;

@Configuration
@EnableBatchProcessing
@EnableRetry
public class BatchConfiguration {

	private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);
	@Value("${mycustom.batch.partition.size.int}")
	private int mycustombatchpartitionsize;
	@Value("${mycustom.batch.concurrency.size.int}")
	private int mycustombatchconcurrencysize;
	@Value("${mycustom.batch.throttle.limit.int}")
	private int mycustombatchthrottlelimit;
	@Value("${mycustom.batch.grid.size.int}")
	private int mycustombatchgridsize;
	@Value("${mycustom.batch.chunk.size.int}")
	private int mycustombatchchunksize;
	@Value("${mycustom.batch.maxpool.size.int}")
	private int mycustombatchmaxpoolsize;
	@Value("${mycustom.batch.corepool.size.int}")
	private int mycustombatchcorepoolsize;
	@Value("${mycustom.batch.queuecapacity.size.int}")
	private int mycustombatchqueuecapacitysize;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	InterceptingJobExecution interceptingJob;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JdbcBatchItemWriter<Sales> writer;

	@Autowired
	private FlatFileItemReader<Sales> salesItemReader;

	@Autowired
	private CustomStepListener stepListner;

	@Autowired
	private CustomItemReaderListener itemReaderListner;

	@Autowired
	private CustomItemWriterListener itemWriterListner;

	@Bean("partitioner")
	@StepScope
	public Partitioner partitioner() {
		log.info("In Partitioner");
		MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = null;
		try {
			resources = resolver.getResources("*.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		partitioner.setResources(resources);
		partitioner.partition(mycustombatchpartitionsize);
		return partitioner;
	}

	@Bean
	public ThreadPoolTaskExecutor threadpooltaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(mycustombatchmaxpoolsize);
		taskExecutor.setCorePoolSize(mycustombatchcorepoolsize);
		taskExecutor.setQueueCapacity(mycustombatchqueuecapacitysize);
		taskExecutor.setThreadNamePrefix("CSV2DB-");
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

	@Bean
	public TaskExecutor asynctaskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor("CSV2DB-");
		asyncTaskExecutor.setConcurrencyLimit(mycustombatchconcurrencysize); // asyncTaskExecutor.setThreadNamePrefix("CSVtoDB");
		return asyncTaskExecutor;
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
		return jobRegistryBeanPostProcessor;
	}

//	@Bean
//    public Job job(@Qualifier("step1") Step step1, @Qualifier("step2") Step step2) {
//        return jobBuilderFactory.get("myJob").start(step1).next(step2).build();
//    }

	@Bean
	@StepScope
	@Qualifier("salesItemReader")
	@DependsOn("partitioner")
	public FlatFileItemReader<Sales> salesItemReader(@Value("#{stepExecutionContext['fileName']}") String filename)
			throws MalformedURLException {
		log.info("In Flat-File-Item-Reader, currently reading   >>     " + filename);
		return new FlatFileItemReaderBuilder<Sales>().name("salesItemReader")// .linesToSkip(1)
				.delimited()
				.names(new String[] { "region", "country", "itemtype", "saleschannel", "orderpriority", "orderdate",
						"orderid", "shipdate", "unitssold", "unitprice", "unitcost", "totalrevenue", "totalcost",
						"totalprofit" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Sales>() {
					{
						setTargetType(Sales.class);
					}
				}).resource(new UrlResource(filename)).build();
	}

	@Bean
	public SalesItemProcessor processor() {
		log.info("BATCH CONFIG 1 - Processing CSV values.........");
		return new SalesItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Sales> writer(DataSource dataSource) {
		log.info("BATCH CONFIG 1 - Loading CSV values to Database.........");
		return new JdbcBatchItemWriterBuilder<Sales>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO salesreport (region, country, itemtype, saleschannel, orderpriority,orderdate, orderid, shipdate, "
						+ "unitssold, unitprice, unitcost, totalrevenue, totalcost, totalprofit) "
						+ "VALUES (:region, :country, :itemtype, :saleschannel, :orderpriority, :orderdate, :orderid"
						+ ", :shipdate, :unitssold, :unitprice, :unitcost, :totalrevenue, :totalcost, :totalprofit)")
				.dataSource(dataSource).build();
	}

	@Bean
	@Qualifier("masterStep")
	public Step masterStep() {
		return stepBuilderFactory.get("masterStep").partitioner("step1", partitioner()).step(step1())
				.taskExecutor(asynctaskExecutor()).taskExecutor(threadpooltaskExecutor())
				.gridSize(mycustombatchgridsize).build();
	}

	@Bean
	public Job importSalesJob(JobCompletionNotificationListener jobCompletionListener, Step step1) {
		return jobBuilderFactory.get("importSalesJob").incrementer(new RunIdIncrementer())
				.listener(jobCompletionListener).flow(masterStep()).end().build();
	}

	@SuppressWarnings("unused")
	@Bean
	public Step step1() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		// attribute.setPropagationBehavior(Propagation.REQUIRED.value());
		// attribute.setIsolationLevel(Isolation.DEFAULT.value());
		// attribute.setTimeout(30);
		return stepBuilderFactory.get("step1").<Sales, Sales>chunk(mycustombatchchunksize).reader(salesItemReader)
				.processor(processor()).writer(writer).taskExecutor(threadpooltaskExecutor())
				.taskExecutor(asynctaskExecutor()).throttleLimit(mycustombatchthrottlelimit)
				// .skipLimit(10) //default is set to 0 // .startLimit(1)
				// .stream(fileItemWriter1())// .stream(fileItemWriter2())
				// .transactionAttribute(attribute) // .readerIsTransactionalQueue()
				// .faultTolerant() // .noRollback(ValidationException.class)
				// .skip(Exception.class)// .noSkip(FileNotFoundException.class)
				// .retryLimit(3)// .retry(DeadlockLoserDataAccessException.class)
				// .skip(FlatFileParseException.class) // .writer(compositeItemWriter())
				// .allowStartIfComplete(true)
				.listener(stepListner).listener(itemReaderListner).listener(itemWriterListner).build();
	}

	@Bean
	public JobExecutionListener jobExecutionListener(ThreadPoolTaskExecutor executor) {
		return new JobExecutionListener() {
//			private ThreadPoolTaskExecutor taskExecutor = executor;
			@Override
			public void beforeJob(JobExecution jobExecution) {
				log.info(
						"This message is \"before\" the start of job. You might want to do something here - Before the job.");
				System.err.println(
						"This message is \"before\" the start of job. You might want to do something here - Before the job.");
			}

			@Override
			public void afterJob(JobExecution jobExecution) {
				log.info("This message is \"after\" the job. You might want to do something here - After the job.");

				System.err.println(
						"This message is \"after\" the job. You might want to do something here - After the job.");
				executor.shutdown();
			}
		};
	}

	@Bean
	public Job importSecondJob(Step step2) {
		return jobBuilderFactory.get("importSecondJob").incrementer(new RunIdIncrementer())
				// .preventRestart() // By default all jobs are re-startable. Use this if want
				// to restrict it.
				.flow(step2).end().listener(interceptingJob)
				// .validator(parametersValidator())
				.build();
	}

	@Bean
	public Job importThirdJob(Step step3) {
		return jobBuilderFactory.get("importThirdJob").incrementer(new RunIdIncrementer())
				// .preventRestart()
				.flow(step3).end().listener(interceptingJob).build();
	}

	@Bean
	public Job importFourthJob(Step step4) {
		return jobBuilderFactory.get("importFourthJob").incrementer(new RunIdIncrementer())
				// .preventRestart()
				.flow(step4).end().listener(jobExecutionListener(threadpooltaskExecutor())).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").tasklet(new TaskOne()).listener(stepListner).build();
	}

	@Bean
	public Step step3(PlatformTransactionManager transactionManager) {
		// Autowired in. No need to use this explicitely.
		return stepBuilderFactory.get("step3").tasklet(new TaskTwo()).transactionManager(transactionManager)
				.listener(stepListner).build();
	}

	@Bean
	public Step step4() {
		return stepBuilderFactory.get("step4").tasklet(new TaskThree()).listener(stepListner).build();
	}

}
