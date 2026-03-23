# 💊 Took Your Medicine?

A personal medicine reminder application that helps you track whether you've taken your daily medications. The app sends automatic reminders via message queues and caches data for fast access.

---

## 🎯 Objective

Never forget to take your medicine again! This application allows you to register your medications with a scheduled time, and automatically reminds you every hour until you confirm you've taken them.

---

## ✨ Features

- Register medications with name, quantity per day, and scheduled time
- Automatic reminders via message queue (every hour until confirmed)
- Mark medication as taken to stop reminders
- Fast data retrieval via Redis caching
- User management
- Custom exception handling with meaningful error responses
- Unit tests with JUnit 5 and Mockito

---

## 🛠️ Technologies

| Technology                  | Purpose                              |
| --------------------------- | ------------------------------------ |
| **Java 17**                 | Main programming language            |
| **Spring Boot 4**           | Application framework                |
| **PostgreSQL**              | Relational database                  |
| **RabbitMQ**                | Message queue for medicine reminders |
| **Redis**                   | Caching layer for fast data access   |
| **Docker / Docker Compose** | Container orchestration              |
| **Jenkins**                 | CI/CD pipeline                       |
| **Lombok**                  | Boilerplate code reduction           |
| **Jackson**                 | JSON serialization/deserialization   |
| **JUnit 5 + Mockito**       | Unit testing                         |

---

## 🏗️ Architecture

```
POST /medicines
      ↓
 MedicineService (creates medicine with take=false)
      ↓
 MedicineProducer → medicine.exchange → medicine.waiting (TTL: 1h)
                                               ↓ (after TTL expires)
                                         medicine.dlx → medicine.queue
                                               ↓
                                         MedicineConsumer
                                               ↓
                                    take=false → sends reminder + republishes
                                    take=true  → discards message ✅
```

---

## 📁 Project Structure

```
src/
├── main/
│   └── java/
│       └── com/matheus/tookYourMedicine/
│           ├── config/
│           │   ├── CacheConfig.java          # Redis cache TTL and settings
│           │   ├── RabbitMQConfig.java       # RabbitMQ queues, exchanges and bindings
│           │   └── RedisConfig.java          # Redis connection configuration
│           ├── consumer/
│           │   └── MedicineConsumer.java     # Reads from queue and triggers reminders
│           ├── controller/
│           │   ├── MedicineController.java
│           │   └── UserController.java
│           ├── dto/
│           │   ├── MedicineCreateDTO.java
│           │   ├── MedicineDTO.java
│           │   └── UserDTO.java
│           ├── entity/
│           │   ├── MedicineEntity.java
│           │   └── UserEntity.java
│           ├── exception/
│           │   ├── ErrorResponse.java        # Standard error response format
│           │   ├── GlobalExceptionHandler.java # Centralized exception handling
│           │   └── NotFoundException.java    # Custom 404 exception
│           ├── message/
│           │   └── MedicineMessage.java      # RabbitMQ message payload
│           ├── producer/
│           │   └── MedicineProducer.java     # Publishes messages to RabbitMQ
│           └── services/
│               ├── MedicineService.java
│               └── UserService.java
└── test/
    └── java/
        └── com/matheus/tookYourMedicine/
            └── services/
                └── MedicineServiceTest.java  # Unit tests with JUnit 5 + Mockito
```

---

## 🚀 Running the Application

### Prerequisites

- Docker and Docker Compose installed
- Java 17+
- Maven

### Environment Setup

Copy `.env.example` to `.env` and fill in your values:

```bash
cp .env.example .env
```

```env
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=
RABBITMQ_USER=
RABBITMQ_PASS=
```

### Steps

**1. Build the project:**

```bash
./mvnw clean package -DskipTests
```

**2. Start all services:**

```bash
docker compose up -d
```

**3. Run the application:**

```bash
./mvnw spring-boot:run
```

**4. Access the services:**

| Service     | URL                    |
| ----------- | ---------------------- |
| Application | http://localhost:8080  |
| RabbitMQ UI | http://localhost:15672 |
| Jenkins     | http://localhost:8090  |
| Redis       | localhost:6379         |
| PostgreSQL  | localhost:5432         |

---

## 📡 API Endpoints

### Users

| Method   | Endpoint             | Description       |
| -------- | -------------------- | ----------------- |
| `POST`   | `/users/create`      | Create a new user |
| `GET`    | `/users/all`         | List all users    |
| `GET`    | `/users/{id}`        | Find a user by ID |
| `DELETE` | `/users/delete/{id}` | Delete a user     |

### Medicines

| Method   | Endpoint                   | Description                                       |
| -------- | -------------------------- | ------------------------------------------------- |
| `POST`   | `/medicines/create`        | Register a new medicine (triggers reminder queue) |
| `GET`    | `/medicines/{id}`          | Find medicine by ID                               |
| `GET`    | `/medicines/name/{name}`   | Find medicine by name                             |
| `GET`    | `/medicines/user/{userId}` | Find medicines by user                            |
| `PUT`    | `/medicines/{id}/taken`    | Mark medicine as taken                            |
| `DELETE` | `/medicines/delete/{id}`   | Delete a medicine by ID                           |

---

## ⚠️ Exception Handling

Custom exceptions return structured error responses instead of generic 500 errors:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Medicine not found with id: 8093da31-2a4b-4ccb-a571-9297864d6223",
  "timestamp": "2026-03-11T11:30:00"
}
```

| Exception           | HTTP Status | When                       |
| ------------------- | ----------- | -------------------------- |
| `NotFoundException` | 404         | Medicine or user not found |
| `Exception`         | 500         | Unexpected internal errors |

---

## 🧪 Running Tests

```bash
./mvnw test
```

Unit tests cover the following scenarios:

| Test                                                | Description                                            |
| --------------------------------------------------- | ------------------------------------------------------ |
| `shouldCreateMedicineWithTakeFalse`                 | Medicine is created with take=false                    |
| `shouldMarkMedicineAsTaken`                         | Medicine take is updated to true                       |
| `shouldThrowNotFoundWhenMedicineNotFound`           | NotFoundException thrown for invalid ID                |
| `shouldThrowNotFoundWhenMarkingNonExistentMedicine` | NotFoundException thrown when marking unknown medicine |

---

## 📨 RabbitMQ Queue Flow

| Queue               | Role                                                     |
| ------------------- | -------------------------------------------------------- |
| `medicine.exchange` | Entry point — receives published messages                |
| `medicine.waiting`  | Waiting room — holds message for 1 hour (TTL)            |
| `medicine.dlx`      | Dead Letter Exchange — redirects expired messages        |
| `medicine.queue`    | Final destination — consumer reads and triggers reminder |

---

## ⚙️ CI/CD with Jenkins

The project includes a `Jenkinsfile` with the following pipeline stages:

1. **Checkout** — pulls latest code from GitHub
2. **Build** — compiles and packages the application
3. **Test** — runs unit tests
4. **Docker Build** — builds the Docker image
5. **Deploy** — restarts containers with the new image

---

## 🐳 Docker Services

```yaml
services:
  app        # Spring Boot application (port 8080)
  rabbitmq   # Message broker (ports 5672, 15672)
  redis      # Cache layer (port 6379)
  postgres   # Relational database (port 5432)
  jenkins    # CI/CD server (port 8090)
```
