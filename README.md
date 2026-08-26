# API implementation microservices

The project is split into independently runnable Spring Boot applications:

| Application | Port | Direct base URL |
|---|---:|---|
| `api-implementation` gateway | 8080 | http://localhost:8080 |
| `user-service` | 8081 | http://localhost:8081 |
| `product-service` | 8082 | http://localhost:8082 |
| `order-service` | 8083 | http://localhost:8083 |
| `cart-service` | 8084 | http://localhost:8084 |

The root application forwards `/api/users`, `/api/products`, `/api/orders`, and `/api/cart` to the corresponding service. You can also call each service directly on its own port.

The root `pom.xml` is a Maven aggregator for all four services. Open the repository root in IntelliJ and accept the Maven import prompt; the services will be available in one Maven and Services view.

## Run PostgreSQL and pgAdmin with Docker

This Compose setup starts PostgreSQL and pgAdmin. The Spring Boot applications continue to run independently from IntelliJ or Maven.

```bash
docker compose up -d --build
docker compose ps
```

Stop the containers with:

```bash
docker compose down
```

PostgreSQL is exposed on port `5432`; pgAdmin is available at http://localhost:5050. The initialization script creates `user_db`, `product_db`, `order_db`, and `cart_db` on a new PostgreSQL volume.

In pgAdmin, register a server with host `postgres`, port `5432`, username `venkata`, and password `venkata`. When connecting from Spring Boot applications running in IntelliJ, use host `localhost` as configured in each service.

## Run services separately from IntelliJ or Maven

```bash
./mvnw -f api-implementation/pom.xml spring-boot:run
./mvnw -f user-service/pom.xml spring-boot:run
./mvnw -f product-service/pom.xml spring-boot:run
./mvnw -f order-service/pom.xml spring-boot:run
./mvnw -f cart-service/pom.xml spring-boot:run
```

To compile and test all projects from the unified root project:

```bash
./mvnw clean test
```

Each service owns a separate PostgreSQL database. The services communicate locally through the REST URLs defined in their `application.properties` files.
