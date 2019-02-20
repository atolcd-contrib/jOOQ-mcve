# MCVE for issue jOOQ#8342

I couldn't get the `jooq-codegen-maven` to work,
so I threw in some Gradle, and committed the generated code.

I left the Maven build, without `jooq-codegen-maven`,
such that
```
mvn clean test
```
should Just Work™. If you're OK with Gradle, then
```
./gradlew test
```
works too.

Make sure to launch a Postgres database first:

```
docker-compose up -d
```

You can rerun the jOOQ codegen with
```
./gradlew jooq
```
(this will automatically run the Flyway migration first).
