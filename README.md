# Patient Management System

A microservice-based Patient Management System built with **Spring Boot** and **Java 21**. The project demonstrates modern backend development using REST APIs, gRPC, Apache Kafka, Spring Cloud Gateway, JWT authentication, Docker, GitHub Actions, and integration testing with REST Assured.

---

## Technology Stack

| Category             | Technologies                               |
| -------------------- |--------------------------------------------|
| **Backend**          | Java 21, Spring Boot, Spring Data JPA      |
| **API Gateway**      | Spring Cloud Gateway                       |
| **Security**         | Spring Security, JWT, BCrypt               |
| **Communication**    | REST, gRPC, Apache Kafka, Protocol Buffers |
| **Database**         | PostgreSQL, H2 (Testing)                   |
| **Testing**          | JUnit, REST Assured (Integration Testing)  |
| **Documentation**    | Swagger / OpenAPI 3                        |
| **Build Tool**       | Maven                                      |
| **Containerization** | Docker, Docker Compose                     |
| **CI/CD**            | GitHub Actions                             |

---

## Quick Start

### Prerequisites

* Docker Desktop
* Git

### Configure environment variables

Create a `.env` file in the project root and provide a Base64-encoded secret used for signing JWTs:

```env
JWT_SECRET=your_base64_encoded_secret
```

The `.env` file is excluded from Git so secrets are not committed to the repository.

### Run the application

```bash
git clone https://github.com/anishshinde01/patient-management-system.git
cd patient-management-system
docker compose up --build -d
```

This starts the complete application stack, including the API Gateway, authentication service, microservices, PostgreSQL databases, Apache Kafka, and Kafka UI.

---

## Access the application

* **API Gateway:** `http://localhost:4004`
* **Authentication API:** `http://localhost:4004/auth/login`
* **Patient API:** `http://localhost:4004/api/patients`
* **Patient OpenAPI Docs:** `http://localhost:4004/api-docs/patients`
* **Authentication OpenAPI Docs:** `http://localhost:4004/api-docs/auth`
* **Kafka UI:** `http://localhost:8080`

All external API requests are routed through the API Gateway, while the backend services communicate internally through the Docker network.

Patient API requests are protected by a gateway JWT validation filter. The gateway forwards the supplied Bearer token to the authentication service for validation before forwarding an authorized request to the patient service.

---

## Stop the application

```bash
docker compose down
```

To also remove persistent Docker volumes:

```bash
docker compose down -v
```
