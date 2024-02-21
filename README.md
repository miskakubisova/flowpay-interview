# Flowpay Interview Project

This project is designed for the Flowpay interview process, showcasing a Java/Spring Boot application that manages companies and their representatives. It
leverages Swagger for API documentation, Liquibase for database migrations, Maven for dependency management, and Docker for containerization.

## Getting Started

Follow these instructions to get the project up and running on your local machine for development and testing purposes.

### Prerequisites

- JDK 17 or newer
- Maven
- Docker
- PostgreSQL (if running outside of Docker)

### Setting Up for Development

1. **Clone the Repository**

    ```bash
    git clone https://github.com/miskakubisova/flowpay-interview.git
    ```

2. **Navigate to Project Directory**
    ```bash
   cd flowpay-interview 
   ```
3. **Install Dependencies**
   ```bash
   mvn install
   ```
4. **Initialize Database**
   - With Docker: The project includes a docker-compose.yml file that sets up PostgreSQL. Run it using:
      ```bash
      docker-compose up -d
      ```
   - Without Docker: Ensure PostgreSQL is installed and create a database named flowpay_interview.

5. **Apply Database Migrations**
   Liquibase is integrated into the Spring Boot application, and migrations will automatically run on application start.

6. **Start the Application**
    ```bash
    mvn spring-boot:run
    ```

### Accessing the Swagger UI

Once the application is running, you can access the Swagger UI to test the API endpoints at:
```bash
http://localhost:8080/swagger-ui.html
```

### Running Tests

Run automated tests with Maven:
```bash
mvn test
```

### Built With
- Spring Boot - The web framework used 
- Maven - Dependency Management 
- Liquibase - Database Migration Tool
- Swagger - API Documentation
- Docker - Containerization Platform

### Author
Miska Kubisova - miskakubisova
