# Actuator Demo

This is a simple Spring Boot application that demonstrates the use of Spring Boot Actuator.

## Tested With
- Java 25
- Maven 3.9.10

## Tools Used

- HTTPie for making HTTP requests. (for default highlighted commands)
  `brew install httpie`

## References
- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/reference/actuator/index.html)

## Commands

Check the available actuator endpoints:
```bash
http :8080/actuator
```

Check the health status:
```bash
http :8080/actuator/health
```

Shutdown the application gracefully:
```bash
http -X POST :8080/actuator/shutdown
```

Access with username and password (e.g.: admin / 123456):
```bash
http -a admin:123456 :8081/actuator/env
```

Start the redis
```bash
make compose/up
```

k8s probe
```bash
http :8081/actuator/health/liveness
http :8081/actuator/health/readiness
http :8080/livezy
http :8080/readyz
```

info endpoint
```bash
http :8081/actuator/info
```

## QA

## Can't see access denied when adding security?

Can try `mvn clean package` then retry to access the `/actuator` endpoint.
