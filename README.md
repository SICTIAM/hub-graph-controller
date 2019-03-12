# Graph Controller for HDD project (SICTIAM)

Inside a SBT shell 

###Build the source code
```bash
sbt:hub-graph-controller>;clean;reload;update;compile 
```

###Package the fat JAR required by sbt-docker plugin 
```bash
sbt:hub-graph-controller>assembly 
```

###Build the docker image 
```bash
sbt:hub-graph-controller>docker 
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

docker run sictiam/hub-graph-controller:0.1.0-SNAPSHOT 
```