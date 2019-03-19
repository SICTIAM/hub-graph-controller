# Graph Controller for HDD project (SICTIAM)
 
###Build the source code
```bash
sbt clean reload update compile 
```

###Package the fat JAR required by sbt-docker plugin 
```bash
sbt assembly 
```

###Build the docker image 
```bash
sbt docker 
```

###Run image 
```bash
export RABBITMQ_USER="admin"
export RABBITMQ_PWD="Pa55w0rd"
export RABBITMQ_HOST="localhost"
export RABBITMQ_PORT=5672
export RABBITMQ_DURABLE_MESSAGES=true
export RABBITMQ_TIMEOUT=10000
export RABBITMQ_EXCHANGE_NAME="hdd_exchange"

export RDFSTORE_USER="admin"
export RDFSTORE_PWD="Pa55w0rd"
export RDFSTORE_ROOT_URI="http://localhost:32768/repositories"
export RDFSTORE_REPOSITORY_NAME="hdd"
export RDFSTORE_READ_ENDPOINT=""
export RDFSTORE_WRITE_ENDPOINT="statements"

docker run sictiam/hub-graph-controller:0.1.1-SNAPSHOT 
```