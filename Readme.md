

# Run Batch
* java -Dspring.batch.job.names=helloWorldJob -jar target/spring-batch-remote-0.0.1-SNAPSHOT.jar
* java -jar app.jar --spring.batch.job.names=helloWorldJob
* java -jar app.jar --spring.batch.job.names=helloWorldJob,goodbyeJob
* java -jar target\spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=helloWorldJob
* java -jar target/spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=helloWorldJob --spring.batch.job.enabled=true

* java -jar target/spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=dummyJob --spring.batch.job.enabled=true
* java -jar target/spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=kafkajobreader --spring.batch.job.enabled=true --spring.kafka.producer.enabled=true



# Master worker chunk 
* tolong gunakan link ini sebagai rujukan https://github.com/Diags/HNBATCH/tree/main
* blognya pake yang ini https://dev.to/diags/spring-batch-remote-chunk-with-spring-integration-with-kafka-broker-2njj
* one master 

java -jar target\spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=masterchunk  --spring.batch.job.enabled=true --spring.profiles.active=master
* three worker 

java -jar target\spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=workerchunk --spring.kafka.consumer.group-id=batch-worker-10  --spring.batch.job.enabled=true --spring.profiles.active=worker

java -jar target\spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=workerchunk --spring.kafka.consumer.group-id=batch-worker-11 --spring.batch.job.enabled=true --spring.profiles.active=worker

java -jar target\spring-batch-remote-0.0.1-SNAPSHOT.jar --spring.batch.job.names=workerchunk  --spring.kafka.consumer.group-id=batch-worker-12 --spring.batch.job.enabled=true --spring.profiles.active=worker


