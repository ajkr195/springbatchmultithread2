# Spring Batch with Spring Cloud DataFlow Procedure

You might want to follow instructions here: 

https://dataflow.spring.io/docs/installation/

https://dataflow.spring.io/docs/installation/local/manual/

Batch only mode - https://dataflow.spring.io/docs/recipes/batch/batch-only-mode/

Download these jars- 

https://repo.spring.io/release/org/springframework/cloud/spring-cloud-skipper-server/2.6.0/spring-cloud-skipper-server-2.6.0.jar

https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-server/2.7.0/spring-cloud-dataflow-server-2.7.0.jar

https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-shell/2.7.0/spring-cloud-dataflow-shell-2.7.0.jar


Run the jars in this sequence:

1. java -jar spring-cloud-skipper-server-2.6.0.jar

2. java -jar spring-cloud-dataflow-server-2.7.0.jar

or 

Use your own database (for example, Oracle/MySQL)

java -jar spring-cloud-dataflow-server-2.7.0.jar --spring.datasource.url=jdbc:mysql://linuxpc:3306/batchdbmultithread --spring.datasource.username=root --spring.datasource.password=root --spring.datasource.driver-class-name=org.mariadb.jdbc.Driver --spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL55Dialect


Browse to http://localhost:9393/dashboard

Use the UI to create Application and Task (refer Screenshots section) or follow below instructions to do it command line way:

Run the third jar (Optional, if you want to use SCDF Shell):

3. java -jar spring-cloud-dataflow-shell-2.7.0.jar


You will get -- 

Dataflow Shell Prompt > .........Here you can register your app .........


For example,

app register --name <nameofyourapp> --type source --uri maven://<packagename>:yourlocal-source:jar:0.0.1-SNAPSHOT


Remote Maven:

app register --name batchdbmultithread --type task --uri maven://com.spring.batch:springbatch:jar:batchdbmultithread-0.0.1-SNAPSHOT


Local Maven:

app register --name batchdbmultithread --type task --uri file:///parth/to/your/batchdbmultithread-0.0.1-SNAPSHOT.jar


task create batchdbmultithread --definition batchdbmultithread


# Screenshots

<h4> Register App and Creating Task Using SCDF UI </h4>

![springbootrocks](https://github.com/ajkr195/springbatchmultithread2/blob/master/screenshots/registerapp.jpg)

![springbootrocks](https://github.com/ajkr195/springbatchmultithread2/blob/master/screenshots/appregd.jpg)

![springbootrocks](https://github.com/ajkr195/springbatchmultithread2/blob/master/screenshots/createtask.jpg)

![springbootrocks](https://github.com/ajkr195/springbatchmultithread2/blob/master/screenshots/taskcreated.jpg)

![springbootrocks](https://github.com/ajkr195/springbatchmultithread2/blob/master/screenshots/taskcreated2.jpg)

![springbootrocks](https://github.com/ajkr195/springbatchmultithread2/blob/master/screenshots/launchtask.jpg)



Batch only mode config (optional)

export SPRING_CLOUD_DATAFLOW_FEATURES_STREAMS_ENABLED=false<br>
export SPRING_CLOUD_DATAFLOW_FEATURES_SCHEDULES_ENABLED=false<br>
export SPRING_CLOUD_DATAFLOW_FEATURES_TASKS_ENABLED=true<br>
export spring_datasource_url=jdbc:mariadb://localhost:3306/task<br>
export spring_datasource_username=root<br>
export spring_datasource_password=password<br>
export spring_datasource_driverClassName=org.mariadb.jdbc.Driver<br>
export spring_datasource_initialization_mode=always<br>
