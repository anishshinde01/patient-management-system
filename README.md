# 🏥 Patient Management System
A microservice-based **REST API** built with **Spring Boot** (Java 21) for managing patient records.
The system supports containerized deployment using **Docker**, continuous integration with **GitHub Actions**, and interactive API testing via Swagger UI.

---

## 🛠 Technology Stack

| Category | Technologies |
|-----------|--------------|
| **Backend** | Java 21, Spring Boot, Spring Data JPA |
| **Database** | PostgreSQL (Primary), H2 (Testing) |
| **Validation** | Spring Validation |
| **Code Simplification** | Lombok |
| **API Documentation** | Swagger / OpenAPI 3 |
| **Build Tool** | Maven |
| **Containerization** | Docker & Docker Compose |
| **CI/CD** | GitHub Actions |


---
<img width="1437" height="689" alt="Bildschirmfoto 2025-10-10 um 14 43 43" src="https://github.com/user-attachments/assets/7013b312-23f0-4702-bcf4-2149b3bdfc4a" />

## 🚀 Quick Start with Docker Compose

Run the entire application stack with a single command!

### Prerequisites
- Docker Desktop installed and running
- Git (to clone the repository)

### Running the Application

1. **Clone the repository:**
```bash
git clone https://github.com/anishshinde01/patient-management-system.git
cd patient-management-system
```

2. **Start the application stack:**
```bash
docker-compose up --build
```

This will:
- ✅ Build the Spring Boot application
- ✅ Start a PostgreSQL database
- ✅ Connect the application to the database
- ✅ Make the API available at http://localhost:4000

## 🌐 Accessing the Application

### Swagger UI (Recommended)
- Visit: [http://localhost:4000/swagger-ui.html](http://localhost:4000/swagger-ui.html)
- Interactive testing interface with request/response examples.

## 🛑 Stopping the Application

```bash
# Stop services
docker-compose down
```
or
```bash
# Stop services and remove database volume
docker-compose down -v
```
