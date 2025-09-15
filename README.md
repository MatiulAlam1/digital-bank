# Digital Bank – Microservices Architecture

This document outlines the architecture, technical stack, and deployment strategy for the **Digital Bank** project. The platform is designed using a microservice-based approach, with React on the frontend, Java Spring Boot backend services, Apache Kafka for asynchronous communication, PostgreSQL for data persistence, Redis for caching, and containerized with Docker and Kubernetes. Kustomize is used for environment-specific deployment.

---

## 1. Overview

The Digital Bank platform consists of independent, loosely coupled microservices, promoting scalability, flexibility, and independent service deployment. Each service focuses on a specific business capability, such as customer accounts, transactions, or authentication.  

The front end is a React SPA, communicating with backend microservices through an API Gateway. Inter-service communication is primarily event-driven, using Apache Kafka.

---

## 2. Architecture Diagram

**Key Components:**

- **Frontend (React):** User interface of the Digital Bank.
- **API Gateway:** Single entry point for client requests, routing to appropriate microservices.
- **Microservices (Java Spring Boot):** Independent services handling business domains (Account, Transaction, User, etc.).
- **Message Broker (Apache Kafka):** Facilitates asynchronous communication and event streaming.
- **Database (PostgreSQL):** Each service has its own database for loose coupling.
- **In-Memory Cache (Redis):** Caches frequently accessed data to reduce latency.
- **Containerization (Docker):** Packages microservices into containers.
- **Orchestration (Kubernetes + Kustomize):** Manages deployment, scaling, and networking.
- **CI/CD Pipeline:** Automates build, test, and deployment processes.

---

## 3. Services

### 3.1. User Service

- **Description:** Handles user registration, login, profile management, and authentication.
- **Tech Stack:** Java Spring Boot, PostgreSQL
- **API Endpoints:**  
  - `POST /api/users/register`  
  - `POST /api/users/login`  
  - `GET /api/users/{userId}`  

---

### 3.2. Account Service

- **Description:** Manages customer accounts, balance inquiries, and statements.
- **Tech Stack:** Java Spring Boot, PostgreSQL, Redis (caching)
- **API Endpoints:**  
  - `POST /api/accounts`  
  - `GET /api/accounts/{accountId}`  
  - `GET /api/accounts/user/{userId}`  
- **Kafka Topics:**  
  - `account-created`: Triggered on account creation.

---

### 3.3. Transaction Service

- **Description:** Handles deposits, withdrawals, and transfers between accounts.
- **Tech Stack:** Java Spring Boot, PostgreSQL
- **API Endpoints:**  
  - `POST /api/transactions/deposit`  
  - `POST /api/transactions/withdraw`  
  - `POST /api/transactions/transfer`  
- **Kafka Topics:**  
  - `transaction-completed`: Triggered after successful transactions.

---

### 3.4. Notification Service

- **Description:** Sends notifications (email/SMS) based on system events.
- **Tech Stack:** Java Spring Boot
- **Kafka Subscriptions:**  
  - `account-created`  
  - `transaction-completed`

---

### 3.5. Loan Service

- **Description:** Manages loan applications, approvals, and repayments.
- **Tech Stack:** Java Spring Boot, PostgreSQL
- **API Endpoints:**  
  - `POST /api/loans/apply`  
  - `GET /api/loans/{loanId}`  
  - `GET /api/loans/user/{userId}`  
- **Kafka Topics:**  
  - `loan-approved`  
  - `loan-rejected`

---

### 3.6. Card Service

- **Description:** Handles debit/credit card issuance, management, and transactions.
- **Tech Stack:** Java Spring Boot, PostgreSQL
- **API Endpoints:**  
  - `POST /api/cards/issue`  
  - `GET /api/cards/{cardId}`  
  - `GET /api/cards/user/{userId}`  
- **Kafka Topics:**  
  - `card-issued`  
  - `card-blocked`

---

### 3.7. Audit Service

- **Description:** Logs critical operations and provides auditing for compliance.
- **Tech Stack:** Java Spring Boot, PostgreSQL
- **API Endpoints:**  
  - `GET /api/audit/logs`  
  - `GET /api/audit/logs/{userId}`  
- **Kafka Subscriptions:**  
  - Subscribes to `transaction-completed`, `account-created`, `loan-approved`, `card-issued`

---

## 4. Technology Stack

### Backend

- **Framework:** Java Spring Boot  
- **Language:** Java  
- **Build Tool:** Maven or Gradle  

### Frontend

- **Library:** React  
- **State Management:** Redux (or similar)  
- **Package Manager:** npm or yarn  

### Data Storage

- **Database:** PostgreSQL (one per microservice for loose coupling)  
- **Cache:** Redis for frequently accessed data  

### Messaging

- **Message Broker:** Apache Kafka for asynchronous, event-driven communication  

### Containerization & Orchestration

- **Containerization:** Docker  
- **Orchestration:** Kubernetes  
- **Configuration Management:** Kustomize for environment-specific resource customization  

---

## 5. CI/CD Pipeline

A robust CI/CD pipeline ensures agility and seamless deployment of microservices.  

- **Continuous Integration (CI):**
  - Triggered on every push to main branch
  - Steps: code checkout → unit tests → integration tests → code analysis → Docker image build
- **Continuous Deployment (CD):**
  - Triggered after CI success
  - Docker images pushed to registry
  - Kustomize applies configurations to Kubernetes environments
  - Supports blue-green or canary deployments

**Pipeline Tools:** Jenkins, GitLab CI, GitHub Actions

---

## 6. Local Development Setup

Requirements:

- Java (as specified in `pom.xml` or `build.gradle`)  
- Node.js & npm/yarn  
- Docker & Docker Compose  
- `kubectl` & local Kubernetes cluster (Minikube, Kind)  

`docker-compose.yml` in the repo root spins up PostgreSQL, Kafka, and Redis.

---

## 7. Configuration Management

- Application configs in `application.yml`/`application.properties`  
- Kubernetes configs externalized via ConfigMaps and Secrets  
- Environment-specific overlays managed using Kustomize  

---

## 8. API Documentation

- Generated via Swagger/OpenAPI  
- Accessible at `/swagger-ui.html` on each microservice

