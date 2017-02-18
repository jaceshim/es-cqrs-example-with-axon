# Spring Boot with Axon Demo Application

## Run RabbitMQ

Run rabbitMQ in a docker container:

    docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 5671:5671 -p 5672:5672 rabbitmq:3-management
    
now you can access the web GUI at

* Web URL: http://<dockerhost>:8080/#/queues
* user: `guest` 
* pass: `guest`

## Configure the app to use docker rabbitMQ

Add the following to both of your `application.properties` so they can reach RabbitMQ:

    spring.rabbitmq.addresses=<dockerhost>:5672


## Testing

Run both applications (e.g. in IntelliJ)

Use `httpie` client (`brew install httpie`):

### Post some Animals

    http --json POST http://localhost:8080 name=Lion description=Lion\ Desc
    http --json POST http://localhost:8080  name=Tiger description=Tiger\ Desc
    
### now read the query-side
    
    http http://localhost:8081

