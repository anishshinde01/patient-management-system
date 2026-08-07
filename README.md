# 🏥 Patient Management System

A microservice-based Patient Management System built with **Spring Boot** and **Java 21**. The project demonstrates modern backend development using REST APIs, gRPC, Apache Kafka, Docker, and GitHub Actions.

---

## Technology Stack

| Category             | Technologies                               |
| -------------------- | ------------------------------------------ |
| **Backend**          | Java 21, Spring Boot, Spring Data JPA      |
| **Communication**    | REST, gRPC, Apache Kafka, Protocol Buffers |
| **Database**         | PostgreSQL, H2 (Testing)                   |
| **Documentation**    | Swagger / OpenAPI 3                        |
| **Build Tool**       | Maven                                      |
| **Containerization** | Docker, Docker Compose                     |
| **CI/CD**            | GitHub Actions                             |

---

## Quick Start

### Prerequisites

* Docker Desktop
* Git

### Run the application

```bash
git clone https://github.com/anishshinde01/patient-management-system.git
cd patient-management-system
docker compose up --build
```

This starts the complete application stack, including the microservices, PostgreSQL, Apache Kafka, and Kafka UI.

---

## Access the application

* **API Gateway / REST API:** http://localhost:4000
* **Swagger UI:** http://localhost:4000/swagger-ui.html
* **Kafka UI:** http://localhost:8080

---

## Stop the application

```bash
docker compose down
```

To also remove persistent Docker volumes:

```bash
docker compose down -v
```
