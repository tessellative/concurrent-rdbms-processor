# Concurrent RDBMS Processing Demo.


## Concurrency

The concurrent execution is internally implemented. Meaning, that application internal threads are calling the database resource.

Scaling this further multiple instance of this application can be started simultaneously.

## Setup

### Docker compose to set up the postgresql DB  
docker-compose -f ./docker/conc_rdbms_compose.yml up -d

### Build and Execute
mvn clean package

java -jar target/concurrent-rdbms-processor-0.0.1-SNAPSHOT.jar

### Stats Rest Endpoint
http://localhost:8080/cities/stats

### Default Config
See the application.yml


## Sample Database Content:
http://pgfoundry.org/projects/dbsamples/


### Resetting the statuses
UPDATE city set processing_status = 'NOT_STARTED';


### Graceful Shutdown
The application needs to be shut down gracefully, in order to avoid corrupted state in the database.
The executors need to finish with the currently executing tasks.

In order to shut down gracefully and wait for the async executors to finish send a POST HTTP request for the http://localhost:8080/actuator/shutdown endpoint