# Health

Using microprofile, you can use health information to check the status of your running application. 

It is often used by monitoring software to alert someone when a production system goes down. 

## REST endpoint

Head to http://127.0.0.1:13990/health to get the health check status.

## Custom health check

In order to add a custom health check create a new class annotated with @Health and implementing HealthCheck.

```java
@Health
@ApplicationScoped
public class MyHealthCheck implements HealthCheck {

    public HealthCheckResponse call() {
        return HealthCheckResponse
                .builder()
                .name("ServiceName")
                .withData("Some Key", "Some Value")
                .up()
                .build();
    }
}

```