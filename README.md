## Κύριες τεχνολογίες του παραδείγματος

* Java 8
* Gradle build tool

### Back-end

* MySQL Driver
* [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/8.0/en/)
* [Spring JDBC](https://spring.io/guides/gs/relational-data-access/)
* Spring Web

### CLI client

* Java 8

### Βάση Δεδομένων

* MySQL 

## Γρήγορες οδηγίες

* Για την εκτέλεση του back-end:

```bash
cd back-end
./gradlew bootrun
```

Το REST API base URL είναι το `http://localhost:8765/energy/api`.

* Για την εκτέλεση του cli app:

```bash
cd cli-client/src
javac ./snippet/MultipartUtility.java
javac ./snippet/FullResponseBuilder.java
javac ./snippet/CliApplication.java
java snippet.CliApplication
```
