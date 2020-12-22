# Spring Batch with Spring Cloud DataFlow Procedure

You might want to follow instructions here: 

https://dataflow.spring.io/docs/installation/

https://dataflow.spring.io/docs/installation/local/manual/

Download these jars- 


https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-server/2.7.0/spring-cloud-dataflow-server-2.7.0.jar

https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-shell/2.7.0/spring-cloud-dataflow-shell-2.7.0.jar


Run the first jar 


java -jar spring-cloud-dataflow-server-2.7.0.jar

or 

Use your own database (for example, Oracle/MySQL)

java -jar spring-cloud-dataflow-server-2.7.0.jar --spring.datasource.url=jdbc:mysql://linuxpc:3306/batchdbmultithread --spring.datasource.username=root --spring.datasource.password=root --spring.datasource.driver-class-name=org.mariadb.jdbc.Driver --spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL55Dialect


Browse to http://localhost:9393/dashboard

Use the UI to create Application and Task or follow below instructions to do it command line way:

Run the second jar:

java -jar spring-cloud-dataflow-shell-2.7.0.jar


You will get -- 

Dataflow Shell Prompt > .........Here you can register your app .........


For example,

app register --name <nameofyourapp> --type source --uri maven://<packagename>:yourlocal-source:jar:0.0.1-SNAPSHOT


Remote Maven:

app register --name batchdbmultithread --type task --uri maven://com.spring.batch:springbatch:jar:batchdbmultithread-0.0.1-SNAPSHOT


Local Maven:

app register --name batchdbmultithread --type task --uri file:///parth/to/your/batchdbmultithread-0.0.1-SNAPSHOT.jar


task create batchdbmultithread --definition batchdbmultithread