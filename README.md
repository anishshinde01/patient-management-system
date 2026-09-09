# Patient Management System

A microservice-based Patient Management System built with **Spring Boot** and **Java 21**. The project demonstrates modern backend development using REST APIs, gRPC, Apache Kafka, Spring Cloud Gateway, JWT authentication, PostgreSQL, Docker, automated integration testing with REST Assured, and CI/CD with GitHub Actions.

It also includes **Infrastructure as Code with AWS CDK and CloudFormation**, using LocalStack to deploy and test AWS services such as ECS/Fargate, RDS, MSK, Application Load Balancing, CloudWatch, and Secrets Manager locally.

---
## Service Communication

The services collaborate through a combination of synchronous and event-driven communication:

* Client requests enter through the **API Gateway**, which handles routing and validates JWTs through the **Authentication Service**.
* The **Patient Service** manages patient data using its own PostgreSQL database.
* When a patient is created, the Patient Service communicates synchronously with the **Billing Service over gRPC** to create the corresponding billing account.
* The Patient Service also publishes patient events to **Apache Kafka**, which are consumed asynchronously by the **Analytics Service**.
* **Protocol Buffers** define the contracts used for gRPC communication and Kafka event messages.

---
## Technology Stack


| Category             | Technologies                                                     |
| -------------------- | ---------------------------------------------------------------- |
| **Backend**          | Java 21, Spring Boot, Spring Data JPA                            |
| **API Gateway**      | Spring Cloud Gateway                                             |
| **Security**         | Spring Security, JWT, BCrypt, AWS Secrets Manager                |
| **Communication**    | REST, gRPC, Apache Kafka, Protocol Buffers                       |
| **Database**         | PostgreSQL, H2 (Testing)                                         |
| **Testing**          | JUnit, REST Assured (Integration Testing)                        |
| **Documentation**    | Swagger / OpenAPI 3                                              |
| **Build Tool**       | Maven                                                            |
| **Containerization** | Docker, Docker Compose                                           |
| **Cloud / IaC**      | AWS CDK, CloudFormation, LocalStack                              |
| **AWS Services**     | VPC, ECS/Fargate, RDS, MSK, ALB, CloudWatch, Cloud Map, Route 53 |
| **CI/CD**            | GitHub Actions, GitHub Container Registry                        |

---

## Quick Start

### Prerequisites

* Docker Desktop
* Git

### 1. Clone the repository

```bash
git clone https://github.com/anishshinde01/patient-management-system.git
cd patient-management-system
```

### 2. Configure environment variables

Run the setup script from the project root:

```bash
./scripts/setup-env.sh
```

This creates a local `.env` file with a generated Base64-encoded secret used for signing JWTs.

Alternatively, you can create the `.env` file manually in the project root:

```env
JWT_SECRET=your_base64_encoded_secret
```

The `.env` file is excluded from Git so secrets are not committed to the repository.

### 3. Run the application

```bash
docker compose up --build -d
```

This starts the complete application stack, including the API Gateway, authentication service, microservices, PostgreSQL databases, Apache Kafka, and Kafka UI.

---
## Access the Application

### Main Interfaces

| Interface   | URL                                     |
| ----------- | --------------------------------------- |
| API Gateway | `http://localhost:4004`                 |
| Swagger UI  | `http://localhost:4004/swagger-ui.html` |
| Kafka UI    | `http://localhost:8080`                 |

### API Endpoints

| API             | URL                                  |
| --------------- | ------------------------------------ |
| Authentication  | `http://localhost:4004/auth/login`   |
| Patient Service | `http://localhost:4004/api/patients` |

### OpenAPI Specifications

| Service                | URL                                       |
| ---------------------- | ----------------------------------------- |
| Patient Service        | `http://localhost:4004/api-docs/patients` |
| Authentication Service | `http://localhost:4004/api-docs/auth`     |

Swagger UI provides interactive documentation for both the **Patient Service** and **Authentication Service** through a single gateway endpoint.

All external API requests are routed through the API Gateway. Protected patient requests require a JWT Bearer token, which the gateway validates through the Authentication Service before forwarding the request.

---

## AWS-Style Deployment with LocalStack

The `infrastructure` module defines the application's AWS infrastructure using **AWS CDK for Java**. CDK synthesizes the infrastructure into a CloudFormation template, which is deployed to **LocalStack** for local AWS-compatible development and testing.

The infrastructure includes:

* VPC networking
* ECS/Fargate services for the application containers
* RDS PostgreSQL databases
* MSK for Kafka
* Application Load Balancer
* CloudWatch logging
* Cloud Map service discovery
* Route 53 health checks
* Secrets Manager for JWT secret management

### Prerequisites

In addition to the standard prerequisites:

* Java 21
* Maven
* AWS CLI
* LocalStack

Build the service Docker images:

```bash
docker compose build
```

Then start LocalStack either through the **LocalStack Desktop application** or with:

```bash
lstk start
```

Once LocalStack is running, run the `LocalStack` class in the `infrastructure` module to synthesize the AWS CDK stack into a CloudFormation template.

Deploy the generated infrastructure with:

```bash
cd infrastructure
./localstack-deploy.sh
```

The deployment script prints the generated load balancer address. The application can then be accessed through:

```text
http://<load-balancer-address>:4004
```

and Swagger UI through:

```text
http://<load-balancer-address>:4004/swagger-ui.html
```

For the LocalStack deployment, the JWT signing secret is generated and stored in **AWS Secrets Manager**.

---

## Testing and CI/CD

GitHub Actions automatically builds and tests the services, validates the **AWS CDK infrastructure** by synthesizing the CloudFormation template, builds the Docker images, starts the complete application stack, and runs **REST Assured integration tests** against the running system.

On the main branch, service images are published to the **GitHub Container Registry**.

---

## Stop the application

```bash
docker compose down
```

To also remove persistent Docker volumes:

```bash
docker compose down -v
```
