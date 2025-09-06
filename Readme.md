
```markdown
# 🛒 eCommerce Microservices Platform

![Java](https://img.shields.io/badge/Java-24-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-purple)
![Podman](https://img.shields.io/badge/Podman-4.x-orange)

A **modern, production-ready eCommerce platform** built with a **microservices architecture** using Spring Boot, PostgreSQL, and containerized with Podman.

---

## 🚀 Features

- 🧑‍💻 **User Service** – Authentication & Profile Management
- 🛍️ **Product Service** – Catalog & Inventory Management
- 📦 **Order Service** – Order Processing & History
- 🐳 **Containerized Services** – Each service + database runs in isolated Podman containers
- ⚡ **Modern Tech Stack** – Java 24, Spring Boot 3.x, JPA, Lombok

---

## 🖥️ Tech Stack

| Component        | Technology               |
|------------------|--------------------------|
| **Language**     | Java 24                  |
| **Framework**    | Spring Boot 3.x          |
| **Database**     | PostgreSQL (per service) |
| **ORM**          | Spring Data JPA          |
| **Container**    | Podman                   |
| **Build**        | Maven                    |
| **Utilities**    | Lombok                   |

---

## 📦 Prerequisites

- **Java 24** JDK  
- **Maven** 3.9+  
- **Podman** 4.0+  
- **podman-compose**  
- **PostgreSQL client** (optional, for manual DB access)

---

## ⚡ Quick Start

1. **Clone the repository**

git clone https://github.com/your-repo/ecommerce-microservices.git
cd ecommerce-microservices

2. **Build all services**

make build-all

3. **Run all services**

make run-all

4. **Verify health**

curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Product Service
curl http://localhost:8083/actuator/health  # Order Service

---

## 🌐 Service Endpoints

| Service          | Base URL              | Health Check Endpoint |
|------------------|-----------------------|-----------------------|
| User Service     | http://localhost:8081 | `/actuator/health`    |
| Product Service  | http://localhost:8082 | `/actuator/health`    |
| Order Service    | http://localhost:8083 | `/actuator/health`    |

---

## 🛠️ Makefile Commands

| Command              | Description                              |
|----------------------|------------------------------------------|
| `make build-all`     | Build all services                       |
| `make run-all`       | Start all containers                     |
| `make stop`          | Stop running containers                  |
| `make clean`         | Remove containers, images, and volumes   |
| `make logs`          | View container logs                      |
| `make run-db-only`   | Start only database containers           |

---

## 🗄️ Database Access

Each service has its own PostgreSQL database:

| Service          | Database   | Port  |
|------------------|------------|-------|
| User Service     | userdb     | 5433  |
| Product Service  | productdb  | 5434  |
| Order Service    | orderdb    | 5435  |

Example connection command:

psql -h localhost -p 5433 -U postgres -d userdb
Default credentials: **`postgres / postgres`**

---

## 💻 Local Development

Run services outside containers while databases run in Podman:


Start only databases
make run-db-only
Run a service locally
cd user-service && mvn spring-boot:run

---

## 🚢 Deployment

1. **Set Docker Hub credentials** (if pushing images):

export DOCKER_USERNAME=yourusername
export DOCKER_PASSWORD=yourpassword

2. **Build & push images**:

make build-and-push

---

## 📌 Roadmap (Optional)

- ✅ Core microservices setup
- 📡 API Gateway & Service Discovery
- 🛡️ JWT Authentication
- 📊 Centralized Logging & Monitoring (Prometheus + Grafana)
- 🧪 Integration & Load Testing

 
Do you want me to also include API Documentation section with Swagger UI links so developers can test endpoints easily? That would make the README even more useful for onboarding.
⁂

