

# Run Batch
* java -Dspring.batch.job.names=helloWorldJob -jar target/spring-batch-remote-0.0.1-SNAPSHOT.jar
* java -jar app.jar --spring.batch.job.names=helloWorldJob
* java -jar app.jar --spring.batch.job.names=helloWorldJob,goodbyeJob
* java -jar target\spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=helloWorldJob
* java -jar target/spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=helloWorldJob --spring.batch.job.enabled=true

* java -jar target/spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=dummyJob --spring.batch.job.enabled=true
* java -jar target/spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=kafkajobreader --spring.batch.job.enabled=true --spring.kafka.producer.enabled=true