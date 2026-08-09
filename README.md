# 🏥 Patient Management System

A microservice-based Patient Management System built with **Spring Boot** and **Java 21**. The project demonstrates modern backend development using REST APIs, gRPC, Apache Kafka, Spring Cloud Gateway, Docker, and GitHub Actions.

---

## Technology Stack

| Category             | Technologies                               |
| -------------------- | ------------------------------------------ |
| **Backend**          | Java 21, Spring Boot, Spring Data JPA      |
| **API Gateway**      | Spring Cloud Gateway                       |
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

```bash id="l9nqiw"
git clone https://github.com/anishshinde01/patient-management-system.git
cd patient-management-system
docker compose up --build
```

This starts the complete application stack, including the API Gateway, microservices, PostgreSQL, Apache Kafka, and Kafka UI.

---

## Access the application

* **API Gateway:** `http://localhost:4004`
* **Patient API:** `http://localhost:4004/api/patients`
* **Patient OpenAPI Docs:** `http://localhost:4004/api-docs/patients`
* **Kafka UI:** `http://localhost:8080`

All external API requests are routed through the API Gateway, while the backend services communicate internally through the Docker network.

---

## Stop the application

```bash id="zngkfy"
docker compose down
```

To also remove persistent Docker volumes:

```bash id="2mxfjf"
docker compose down -v
```
