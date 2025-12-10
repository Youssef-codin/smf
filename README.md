# Secure Monitoring & Fortification (SMF)

This project is a Spring Boot application that provides secure monitoring and fortification services.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK):** Version 21
- **Apache Maven:** A recent version
- **PostgreSQL:** A running instance of PostgreSQL

## Getting Started

To get the project up and running on your local machine, follow these steps:

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/your-username/smf.git
    cd smf
    ```

2.  **Configure the database:**

    - Create a new PostgreSQL database named `smf`.
    - Update the `src/main/resources/application.properties` file with your PostgreSQL username and password:

      ```properties
      spring.datasource.username=your-username
      spring.datasource.password=your-password
      ```

3.  **Run the application:**

    You can run the application using the Maven wrapper included in the project:

    ```bash
    mvn spring-boot:run
    ```

    The application will start on the default port (usually 8080).

## Project Structure

The core logic of the application is organized into the following packages under `src/main/java/com/smf`:

-   `controller`: Contains REST controllers that handle incoming HTTP requests and return responses.
-   `dto`: (Data Transfer Objects) Holds classes used for transferring data between layers, including request and response bodies.
-   `exception`: Defines custom exception classes and global exception handling.
-   `model`: Contains JPA entities that represent the application's domain model and are mapped to database tables.
-   `repo`: (Repository) Provides interfaces for data access operations, extending Spring Data JPA repositories.
-   `security`: Houses security-related configurations, JWT utilities, and custom user details services for authentication and authorization.
-   `service`: Contains business logic and orchestrates operations between controllers and repositories.

## Database Migrations

This project uses [Flyway](https://flywaydb.org/) for managing database schema migrations. Migration scripts are located in `src/main/resources/db/migration/` and are automatically applied when the application starts. Ensure your database is properly configured in `application.properties` for Flyway to run successfully.

## API Documentation

The API documentation is generated using SpringDoc and is available at the following endpoint once the application is running:

- **OpenAPI UI:** [/api-docs](http://localhost:8080/api-docs)

## Development Standards

1.  All API responses **must use the `ApiResponse` class**.
2.  All controllers **must be mapped using the `/api/v1` prefix**.
3.  All exceptions **must be handled by the `GlobalExceptionHandler`**.
4.  All work **must be done in a new branch** (never commit directly to `main`).
5.  Commit messages must be **clear and descriptive**.
    - Example: `feature: add device registration`
6.  After finishing a feature, **contact Youssef for code review** before merging into `main`.
