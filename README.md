## Running load tests

1. Data bootstrap:
    ```bash
    ./gradlew bootstrap --args="postgres"
    ```
2. Run application (see different options below)
3. Run JMeter
    ```bash
    tools/apache-jmeter-5.5/bin/jmeter -n -t tools/http2_load_test.jmx
    ```

### Web + Postgres + jOOQ
```bash
./gradlew :spring-web:bootRun --args="--spring.profiles.active=postgres,jooq"
```
#### Results (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
JMeter:
```text
summary +  30414 in 00:00:07 = 4105,6/s Avg:     2 Min:     1 Max:   677 Err:     0 (0,00%) Active: 10 Started: 10 Finished: 0
summary +  69586 in 00:00:13 = 5543,4/s Avg:     1 Min:     1 Max:     5 Err:     0 (0,00%) Active: 0 Started: 10 Finished: 10
summary = 100000 in 00:00:20 = 5009,8/s Avg:     1 Min:     1 Max:   677 Err:     0 (0,00%)
```

Logs:
```text
2022-10-31 23:14:15.555  INFO 10264 --- [           main] c.g.g.app.SpringWebLoomApplicationKt     : Starting SpringWebLoomApplicationKt using Java 19 on agavlyukovskiy-pc with PID 10264 (C:\Users\agavl\IdeaProjects\benchmark-spring-aplication\spring-web\build\classes\kotlin\main started by agavl in C:\Users\agavl\IdeaProjects\benchmark-spring-aplication\spring-web)
2022-10-31 23:14:15.558  INFO 10264 --- [           main] c.g.g.app.SpringWebLoomApplicationKt     : The following 2 profiles are active: "postgres", "jooq"
2022-10-31 23:14:16.527  INFO 10264 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2022-10-31 23:14:16.535  INFO 10264 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2022-10-31 23:14:16.535  INFO 10264 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.68]
2022-10-31 23:14:16.625  INFO 10264 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2022-10-31 23:14:16.626  INFO 10264 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1025 ms
2022-10-31 23:14:16.822  INFO 10264 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2022-10-31 23:14:16.953  INFO 10264 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2022-10-31 23:14:17.724  INFO 10264 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
2022-10-31 23:14:17.787  INFO 10264 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2022-10-31 23:14:17.800  INFO 10264 --- [           main] c.g.g.app.SpringWebLoomApplicationKt     : Started SpringWebLoomApplicationKt in 2.665 seconds (JVM running for 2.965)
2022-10-31 23:14:22.996  INFO 10264 --- [io-8080-exec-13] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2022-10-31 23:14:22.996  INFO 10264 --- [io-8080-exec-13] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2022-10-31 23:14:22.996  INFO 10264 --- [io-8080-exec-13] o.s.web.servlet.DispatcherServlet        : Completed initialization in 0 ms
2022-10-31 23:14:27.813  INFO 10264 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 3688req/s (total: 18444)
2022-10-31 23:14:32.819  INFO 10264 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 5499req/s (total: 45941)
2022-10-31 23:14:37.832  INFO 10264 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 5500req/s (total: 73443)
2022-10-31 23:14:42.843  INFO 10264 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 5311req/s (total: 100000)
2022-10-31 23:14:47.850  INFO 10264 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 0req/s (total: 100000)
```
### Web + Postgres + jOOQ + Loom
```bash
./gradlew :spring-web:bootRun --args="--spring.profiles.active=postgres,jooq,loom"
```
#### Results (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
JMeter:
```text
summary +  24643 in 00:00:06 = 3846,3/s Avg:     2 Min:     1 Max:   684 Err:     0 (0,00%) Active: 10 Started: 10 Finished: 0
summary +  75357 in 00:00:14 = 5541,4/s Avg:     1 Min:     1 Max:    13 Err:     0 (0,00%) Active: 0 Started: 10 Finished: 10
summary = 100000 in 00:00:20 = 4998,3/s Avg:     1 Min:     1 Max:   684 Err:     0 (0,00%)
```

Logs:
```text
2022-10-31 23:12:48.294  INFO 2564 --- [           main] c.g.g.app.SpringWebLoomApplicationKt     : Starting SpringWebLoomApplicationKt using Java 19 on agavlyukovskiy-pc with PID 2564 (C:\Users\agavl\IdeaProjects\benchmark-spring-aplication\spring-web\build\classes\kotlin\main started by agavl in C:\Users\agavl\IdeaProjects\benchmark-spring-aplication\spring-web)
2022-10-31 23:12:48.296  INFO 2564 --- [           main] c.g.g.app.SpringWebLoomApplicationKt     : The following 3 profiles are active: "postgres", "jooq", "loom"
2022-10-31 23:12:49.312  INFO 2564 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2022-10-31 23:12:49.321  INFO 2564 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2022-10-31 23:12:49.321  INFO 2564 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.68]
2022-10-31 23:12:49.406  INFO 2564 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2022-10-31 23:12:49.407  INFO 2564 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1066 ms
2022-10-31 23:12:49.575  INFO 2564 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2022-10-31 23:12:49.714  INFO 2564 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2022-10-31 23:12:50.428  INFO 2564 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
2022-10-31 23:12:50.489  INFO 2564 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2022-10-31 23:12:50.502  INFO 2564 --- [           main] c.g.g.app.SpringWebLoomApplicationKt     : Started SpringWebLoomApplicationKt in 2.584 seconds (JVM running for 2.881)
2022-10-31 23:12:54.012  INFO 2564 --- [               ] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2022-10-31 23:12:54.012  INFO 2564 --- [               ] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2022-10-31 23:12:54.013  INFO 2564 --- [               ] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
2022-10-31 23:12:55.524  INFO 2564 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 615req/s (total: 3077)
2022-10-31 23:13:00.528  INFO 2564 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 4892req/s (total: 27541)
2022-10-31 23:13:05.532  INFO 2564 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 5509req/s (total: 55090)
2022-10-31 23:13:10.537  INFO 2564 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 5590req/s (total: 83041)
2022-10-31 23:13:15.538  INFO 2564 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 3391req/s (total: 100000)
```
### Webflux + Postgres + jOOQ
```bash
./gradlew :spring-webflux:bootRun --args="--spring.profiles.active=postgres,jooq"
```
#### Results (Windows, 12 vCPU, i5-10400F 2.9 GHz, 32GB)
JMeter:
```text
summary +  51698 in 00:00:16 = 3288,7/s Avg:     2 Min:     1 Max:  1080 Err:     0 (0,00%) Active: 10 Started: 10 Finished: 0
summary +  48302 in 00:00:12 = 4079,6/s Avg:     2 Min:     1 Max:     9 Err:     0 (0,00%) Active: 0 Started: 10 Finished: 10
summary = 100000 in 00:00:28 = 3628,4/s Avg:     2 Min:     1 Max:  1080 Err:     0 (0,00%)
```

Logs:
```text
2022-10-31 23:15:37.635  INFO 15316 --- [           main] c.g.g.app.SpringWebfluxApplicationKt     : Starting SpringWebfluxApplicationKt using Java 19 on agavlyukovskiy-pc with PID 15316 (C:\Users\agavl\IdeaProjects\benchmark-spring-aplication\spring-webflux\build\classes\kotlin\main started by agavl in C:\Users\agavl\IdeaProjects\benchmark-spring-aplication\spring-webflux)
2022-10-31 23:15:37.637  INFO 15316 --- [           main] c.g.g.app.SpringWebfluxApplicationKt     : The following 2 profiles are active: "postgres", "jooq"
2022-10-31 23:15:39.089  INFO 15316 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
2022-10-31 23:15:39.537  INFO 15316 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
2022-10-31 23:15:39.550  INFO 15316 --- [           main] c.g.g.app.SpringWebfluxApplicationKt     : Started SpringWebfluxApplicationKt in 2.262 seconds (JVM running for 2.572)
2022-10-31 23:15:49.566  INFO 15316 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 2078req/s (total: 10394)
2022-10-31 23:15:54.567  INFO 15316 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 3788req/s (total: 29336)
2022-10-31 23:15:59.577  INFO 15316 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 4125req/s (total: 49962)
2022-10-31 23:16:04.592  INFO 15316 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 4051req/s (total: 70217)
2022-10-31 23:16:09.598  INFO 15316 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 4136req/s (total: 90898)
2022-10-31 23:16:14.608  INFO 15316 --- [   scheduling-1] com.github.gavlyukovskiy.app.Controller  : Throughput[5s]: 1820req/s (total: 100000)
```
