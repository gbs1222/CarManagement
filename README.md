# Car Management System

This is a Spring Boot-based GraphQL application for managing car information. It provides functionalities such as adding, updating, deleting, and querying cars, as well as subscribing to car updates in real-time.

---

## Technologies Used

- Java
- Spring Boot
- GraphQL
- PostgreSQL
- Docker

---

## How to Run

### 1. Set Up the Database

The application uses PostgreSQL. You can set it up using Docker by running the following command:
```
docker-compose up -d
```
This will start:
- A PostgreSQL database at `localhost:5432`.
- pgAdmin at `http://localhost:80`.

### 2. Build and Run the Application

#### Build the Application

Run the following command to build the application:
```
./gradlew build
```
#### Run the Application

Run the following command to start the application:
```
./gradlew bootRun
```
The application will start at `http://localhost:8080`.

---
