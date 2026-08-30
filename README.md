# API implementation microservices

The project is split into independently runnable Spring Boot applications:

| Application | Port | Direct base URL |
|---|---:|---|
| `api-gateway` | 8080 | http://localhost:8080 |
| `user-service` | 8081 | http://localhost:8081 |
| `product-service` | 8082 | http://localhost:8082 |
| `order-service` | 8083 | http://localhost:8083 |
| `cart-service` | 8084 | http://localhost:8084 |

The `api-gateway` module forwards `/api/users`, `/api/products`, `/api/orders`, and `/api/cart` to the corresponding service. You can also call each service directly on its own port.

The root `pom.xml` is a convenience aggregator for the five applications. Each application is also a standalone Spring Boot project with its own `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn` directory, and project documentation.

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

## Run a service standalone

Open a terminal in the service directory and run its own Maven wrapper:

```bash
cd user-service
./mvnw spring-boot:run
```

Use the same pattern for `api-gateway`, `product-service`, `order-service`, or `cart-service`. Run each service in a separate terminal when you want the whole system running.

You can also run them from the repository root through the aggregator:

```bash
./mvnw -pl api-gateway spring-boot:run
./mvnw -pl user-service spring-boot:run
./mvnw -pl product-service spring-boot:run
./mvnw -pl order-service spring-boot:run
./mvnw -pl cart-service spring-boot:run
```

To compile and test all modules:

```bash
./mvnw clean test
```

The user service uses MongoDB, the product service uses MySQL, and the order and cart services use PostgreSQL. The services communicate locally through the REST URLs defined in their `application.properties` files.
