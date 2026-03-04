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

---

## 🛠️ Technologies

| Technology | Purpose |
|---|---|
| **Java 21** | Main programming language |
| **Spring Boot 4** | Application framework |
| **RabbitMQ** | Message queue for medicine reminders |
| **Redis** | Caching layer for fast data access |
| **Docker / Docker Compose** | Container orchestration |
| **Jenkins** | CI/CD pipeline |
| **Lombok** | Boilerplate code reduction |
| **Jackson** | JSON serialization/deserialization |

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
├── config/
│   ├── RabbitMQConfig.java       # RabbitMQ queues, exchanges and bindings
│   └── RedisConfig.java          # Redis cache configuration
├── consumer/
│   └── MedicineConsumer.java     # Reads from queue and triggers reminders
├── controller/
│   ├── MedicineController.java
│   └── UserController.java
├── dto/
│   ├── MedicineCreateDTO.java
│   ├── MedicineDTO.java
│   └── UserDTO.java
├── entity/
│   ├── MedicineEntity.java
│   └── UserEntity.java
├── message/
│   └── MedicineMessage.java      # RabbitMQ message payload
├── producer/
│   └── MedicineProducer.java     # Publishes messages to RabbitMQ
└── services/
    ├── MedicineService.java
    └── UserService.java
```

---

## 🚀 Running the Application

### Prerequisites
- Docker and Docker Compose installed
- Java 21+
- Maven

### Steps

**1. Build the project:**
```bash
./mvnw clean package -DskipTests
```

**2. Start all services:**
```bash
docker compose up -d
```

**3. Access the services:**

| Service | URL |
|---|---|
| Application | http://localhost:8080 |
| RabbitMQ UI | http://localhost:15672 |
| Jenkins | http://localhost:8090 |
| Redis | localhost:6379 |

> RabbitMQ default credentials: `guest / guest`

---

## 📡 API Endpoints

### Users
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/users` | Create a new user |
| `GET` | `/users` | List all users |
| `DELETE` | `/users/{id}` | Delete a user |

### Medicines
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/medicines` | Register a new medicine (triggers reminder queue) |
| `GET` | `/medicines/{id}` | Find medicine by ID |
| `GET` | `/medicines/name/{name}` | Find medicine by name |
| `GET` | `/medicines/user/{userId}` | Find medicines by user |
| `PUT` | `/medicines/{id}/taken` | Mark medicine as taken |
| `DELETE` | `/medicines/{id}` | Delete a medicine |

---

## 📨 RabbitMQ Queue Flow

| Queue | Role |
|---|---|
| `medicine.exchange` | Entry point — receives published messages |
| `medicine.waiting` | Waiting room — holds message for 1 hour (TTL) |
| `medicine.dlx` | Dead Letter Exchange — redirects expired messages |
| `medicine.queue` | Final destination — consumer reads and triggers reminder |

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
  app       # Spring Boot application (port 8080)
  rabbitmq  # Message broker (ports 5672, 15672)
  redis     # Cache layer (port 6379)
  jenkins   # CI/CD server (port 8090)
```
