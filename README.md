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

datasource / hikari connection pool

The app serves business endpoints on **8080** and actuator on **8081**. Everything
below is a plain GET, so it all works by pasting the URL into a browser.

```bash
## the H2-backed endpoint, plus one that holds a pooled connection for a while
http :8080/widgets
http ":8080/widgets/hold?seconds=25"

## the db health indicator (also part of the readiness group)
http :8081/actuator/health/db
```

Pool **configuration** — `/actuator/env/{property}` is far easier to read in a
browser than the full `/actuator/env` dump, and it reports the origin down to the
`application.yaml` line:
```bash
http :8081/actuator/env/spring.datasource.hikari.maximum-pool-size
http :8081/actuator/env/spring.datasource.hikari.minimum-idle
http :8081/actuator/env/spring.datasource.hikari.connection-timeout
http :8081/actuator/env/spring.datasource.url

## everything hikari-related in one shot, defaults included
http :8081/actuator/configprops | jq '.. | select(.properties?.jdbcUrl?) | .properties'
```

Pool **usage** — gauges are live, counters are cumulative:
```bash
http :8081/actuator/metrics/hikaricp.connections.active   ## in use right now
http :8081/actuator/metrics/hikaricp.connections.idle     ## parked, ready to hand out
http :8081/actuator/metrics/hikaricp.connections.pending  ## threads queued for a connection
http :8081/actuator/metrics/hikaricp.connections.max
http :8081/actuator/metrics/hikaricp.connections.timeout  ## COUNT of borrow attempts that gave up
http :8081/actuator/metrics/hikaricp.connections.usage    ## how long borrowers hold a connection
http :8081/actuator/metrics/hikaricp.connections.acquire  ## how long borrowers wait to get one
```

To make the pool interesting, saturate it. Nine 25s holds against a 5-connection
pool with a 20s `connection-timeout`:
```bash
seq 1 9 | xargs -P 9 -I{} curl -s -o /dev/null -w '%{http_code}\n' "http://localhost:8080/widgets/hold?seconds=25"
```
While that runs you have ~20 seconds to refresh the metrics URLs in a browser:
`active` 5, `pending` 4, `idle` 0. Once the queue gives up, `timeout` reaches
COUNT 4 and four requests come back 500. `connection-timeout` is set to 20s
precisely so that window is long enough to click through — drop it to `3000` if
you'd rather see the failure fast than watch the queue.

## QA

## Can't see access denied when adding security?

Can try `mvn clean package` then retry to access the `/actuator` endpoint.
