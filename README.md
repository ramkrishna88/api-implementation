# API implementation microservices

The project is split into independently runnable Spring Boot applications:

| Application | Port | Direct base URL |
|---|---:|---|
| `gateway` | 8080 | http://localhost:8080 |
| `user-service` | 8081 | http://localhost:8081 |
| `product-service` | 8082 | http://localhost:8082 |
| `order-service` | 8083 | http://localhost:8083 |
| `cart-service` | 8084 | http://localhost:8084 |
| `configserver` | 8888 | http://localhost:8888 |
| `eureka` | 8761 | http://localhost:8761 |

The `gateway` module forwards `/api/users`, `/api/products`, `/api/orders`, and `/api/cart` to the corresponding service. You can also call each service directly on its own port.

The root `pom.xml` is a convenience aggregator for the five applications. Each application is also a standalone Spring Boot project with its own `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn` directory, and project documentation.

## Database and local configuration

All persistence services use an embedded, file-backed H2 database. No database container is required. By default, the database files are stored under `./data` and are separated by service:

- `./data/userdb`
- `./data/productdb`
- `./data/orderdb`
- `./data/cartdb`

For a temporary in-memory database, override the service-specific URL, for example:

```bash
PRODUCT_DB_URL=jdbc:h2:mem:productdb ./mvnw -pl product-service spring-boot:run
```

The H2 console is enabled for local runs at `http://localhost:<port>/h2-console`. Use JDBC URL `jdbc:h2:file:./data/<database>`, username `sa`, and an empty password.

Config Server reads the checked-out `config-mgmt` files locally by default. For production configuration, run Config Server and Eureka first, then start services with `SPRING_PROFILES_ACTIVE=prod`. The production Config Server profile uses the Git repository configured by `CONFIG_GIT_URI`, `CONFIG_GIT_USERNAME`, and `CONFIG_GIT_PASSWORD`; services import it through `CONFIG_SERVER_URL` (default `http://localhost:8888`) and register with `EUREKA_DEFAULT_ZONE` (default `http://localhost:8761/eureka/`).

## Run a service standalone

Open a terminal in the service directory and run its own Maven wrapper:

```bash
cd user-service
./mvnw spring-boot:run
```

Use the same pattern for `gateway`, `product-service`, `order-service`, or `cart-service`. Run each service in a separate terminal when you want the whole system running.

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

The user, product, order, and cart services all use H2. The services communicate locally through the REST URLs defined in their `application.properties` files. `api-gateway` is retained as the older MVC forwarding implementation; `gateway` is the Spring Cloud Gateway implementation.
