<div align="center">

# LifeSure Insurance Management System

Comprehensive life insurance management built on Spring Boot 3.5, featuring secure user/branch administration, performance dashboards, and customer operations.

</div>

## Features

- Branch management: CRUD, status filters, search, and performance KPIs
- Performance dashboard: target vs achieved, variance, achievement progress
- User authentication and role-based access (Spring Security)
- Server-side validation (Jakarta Validation) + client hints
- Thymeleaf UI with Bootstrap 5 and icons
- File upload support and static resource handling
- JPA/Hibernate with MariaDB (H2 for dev/test)

## Tech Stack

- Java 17, Spring Boot 3.5.x
- Spring Web, Spring Data JPA, Spring Security, Validation
- Thymeleaf, Bootstrap 5, Bootstrap Icons
- MariaDB (prod), H2 (runtime/dev)

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+ (wrapper included: `./mvnw`)
- MariaDB running locally (or change DB settings)

### Configuration

Edit `src/main/resources/application.properties` as needed:

- `server.port=8080`
- `spring.datasource.url=jdbc:mariadb://localhost:3306/SecureLife`
- `spring.datasource.username=workbench`
- `spring.datasource.password=***` (set your own)
- `spring.jpa.hibernate.ddl-auto=update`
- Static uploads: `spring.web.resources.static-locations=classpath:/static/,file:./uploads/`

### Run locally

Using Maven Wrapper (recommended):

```bash
./mvnw spring-boot:run
```

Or run the packaged jar:

```bash
./mvnw -DskipTests package
java -jar target/*.jar
```

Then open:

- Branch list: http://localhost:8080/branches
- Branch dashboard: http://localhost:8080/branches/dashboard

### Tests

```bash
./mvnw test
```

## Project Structure

```
src/
    main/
        java/com/example/lifesureinsuarncemanagementsystem/...
        resources/
            templates/
                branches/ (list, form, dashboard)
                feedback/ ...
            static/
                css/ (style.css, branch-management.css)
                images/
    test/
```

## Key Screens

- Branches: list, search, status filter, CRUD
- Branch form: client-side constraints mirror backend annotations
    - bCode pattern: `BR###` (e.g., BR001)
    - Phone: exactly 10 digits
    - Name/Address lengths enforced
- Dashboard: blue/green hero with KPI cards; table for per-branch performance and updates

## Security

- Spring Security with CSRF enabled (forms include CSRF tokens)
- Role-based navigation and action visibility

## Database

Default is MariaDB. To try quickly with H2 (in-memory), you can add the following to `application.properties` while developing:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

## File Uploads

- Configured limits: 10MB per file, 10MB per request
- Files can be served from `./uploads` alongside classpath static content

## Build

```bash
./mvnw -q -DskipTests package
```

## Troubleshooting

- Port already in use: change `server.port`
- DB connection errors: verify URL, credentials, and DB is up
- Static resources not updating: clear browser cache or disable caching in dev tools

## License

This project is provided as-is for educational and internal use. Add your organization’s license terms here if needed.

## Acknowledgements

- Spring Boot team, Thymeleaf, Bootstrap, MariaDB
