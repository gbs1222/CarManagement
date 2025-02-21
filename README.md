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
The application will start at `http://localhost:8080/graphiql`.

---

## Queries, Mutations, and Subscriptions

### 1. Add a Car
**Mutation:**
```graphql
mutation addCarOperation($input: AddCarInput!) {
  addCar(input: $input) {
    id
    brand
    model
    color
    yearOfProduction
    price
  }
}
```
**Example Variables:**
```json
{
  "input": {
    "brand": "Toyota",
    "model": "Camry",
    "color": "Red",
    "yearOfProduction": 2020,
    "price": 2000
  }
}
```

### 2. Update Car Details
**Mutation:**
```graphql
mutation updateCarOperation($input: UpdateCarInput!) {
  updateCarDetails(input: $input) {
    id
    brand
    model
    color
    yearOfProduction
    price
  }
}
```
**Example Variables:**
```json
{
  "input": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "color": "Blue",
    "price": 2200
  }
}
```

### 3. Delete a Car
**Mutation:**
```graphql
mutation deleteCarOperation($id: ID!) {
  deleteCar(id: $id)
}
```
**Example Variables:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000"
}
```

### 4. Get Cars with Filtering and Pagination
**Query:**
```graphql
query carsQuery($filter: FilterInput, $pageInput: PageInput) {
  cars(filter: $filter, pageInput: $pageInput) {
    content {
      id
      brand
      model
      color
      yearOfProduction
      price
    }
    totalPages
    totalElements
    size
    number
  }
}
```
**Example Variables:**
```json
{
  "filter": {
    "brand": "Toyota"
  },
  "pageInput": {
    "page": 0,
    "size": 10
  }
}
```

### 5. Get a Car by ID
**Query:**
```graphql
query carByIdQuery($id: ID!) {
  carById(id: $id) {
    id
    brand
    model
    color
    yearOfProduction
    price
  }
}
```
**Example Variables:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000"
}
```

### 6. Subscribe to Car Updates
**Subscription:**
```graphql
subscription carSubscription($filter: FilterInput, $pageInput: PageInput) {
  carSubscription(filter: $filter, pageInput: $pageInput) {
    content {
      id
      brand
      model
      color
      yearOfProduction
      price
    }
    totalPages
    totalElements
    size
    number
  }
}
```
**Example Variables:**
```json
{
  "filter": {
    "brand": "Toyota"
  },
  "pageInput": {
    "page": 0,
    "size": 10
  }
}
```



---


