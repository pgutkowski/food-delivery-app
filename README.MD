# FOOD DELIVERY APP

This project is small showcase for recruitment process & analysis of upcoming features of Spring Boot v2.2.0. One of objectives was to show how to use & test coroutines on the backend. Extended support for coroutines in webflux is great opportunity for it.

To see full order process, go to `com.github.pgutkowski.fda.FoodDeliveryAppApplicationTests.testOrderFlow`

## Tech Stack

### Spring Boot 2.2.0.M4
Bleeding edge version of spring boot with support for suspended functions in webflux. There are still some growing pains & problems with AOP

### Exposed (https://github.com/JetBrains/Exposed)
Jetbrains curated fluent SQL DSL. Great lightweight ORM, but for small demo project a bit too much of work to setup simple CRUD.

### SuspendedResult (https://github.com/kittinunf/Result)
Nice functional programming tool. Unfortunately, it lead to marking a lots of functions as suspended, where otherwise it was not needed.

Surprisingly, it lacks `bind` function.

### Coroutines
Coroutines are making async code look like old-school synchronized threads. IMO code is much more readable than Reactor's Mono & Flux, but still is lacking support.

## Simplifications

To reduce the scope of the project, a lots of aspects of web service have been simplified:

### CRUD operations
Resources have only those operations available, which are required to go through order process

### Validation
Because of problems with AOP & suspended functions, Bean Validation is unstable. It forced me to implement almost manual validation. It easy pretty straightforward & easy to read, but could get tedious in long run.

### Authentication/Authorization
I've lef out authentication & authorization matters out of this project entirely. I would probably use spring security, but there are still problems with AOP & suspended functions, eq. https://github.com/spring-projects/spring-framework/issues/22986

### External configuration
Right now this service needs no configuration, but for real production usage it should fetch configuration from dedicated store

### Production database driver
No JDBC driver is specified for production runtime, only H2 for tests

### Client notifications
To limit scope client notification aspect has been omitted almost entirely. To avoid frequent polling of http clients for status of order, depending on frontend clients, some notification should be implemented.

### Database thread pool configuration
Until https://github.com/r2dbc has better polish & is production ready, asynchronous applications with SQL databases are forced to use Executors to avoid blocking asynchronous threads/coroutines. Right now this example wraps Exposed Database object with super simple CachedThreadPoolExecutor. Executor settings should be benchmarked & tuned for optimal performance.

### business case simplifications
Customer has only one address, coded as simple string. The same goes for order content. In real world application, these values should be represented by much more complex data structures.

## Building
To run tests run

```bash
./gradlew clean test
```

to build executable jar run:

```bash
./gradlew clean bootJar
```