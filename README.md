# Another Adventure ⛐𓂃 ོ𓂃ᨒ↟𖠰

Simple project to test Maven + Kotlin + Spring Boot against a database.

### Start the project

A `docker-compose.yaml` file is provided to easier the developments. Another `PostgreSQL` instance can be used; in this
case the `application.yaml` may have to be updated.

```shell
docker compose up
```

This project is also configured with the `spring-boot-docker-compose` Spring dependency. The `docker-compose.yaml` can
be automatically launched using `spring.docker.compose.enabled` property.

#### Start the server

The server can be run using `maven`:

```shell
./mvnw spring-boot:run
```

### Run the tests

The tests can be run using `maven`:

```shell
./mvnw clean test
```

### Docker image build

A `local.docker-compose.yaml` file is provided to easier the test using Docker images. For the moment, the Docker image
are not pushed and need to be locally built.

#### Build the image

```shell
docker build -t another-adventure:local .
```

#### Start the docker compose

```shell
docker compose -f local.docker-compose.yaml up
```
