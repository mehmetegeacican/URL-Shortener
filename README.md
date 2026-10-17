# URL Shortener & Full-Stack Platform

This project is a modern, scalable, and containerized full-stack URL shortening and management application. It combines a robust Spring Boot backend service, an Expo & React Native (Web-supported) frontend interface, and an integrated monitoring stack (Prometheus & Grafana).

---

## 🚀 Tech Stack & Architecture

### Backend (`/server`)
* **Framework:** Spring Boot (Java 17) [![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot) [![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
* **Database:** PostgreSQL (16-alpine) [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
* **Caching / Storage:** Redis (7-alpine) [![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
* **Observability:** Prometheus & Grafana [![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)](https://prometheus.io/) [![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)](https://grafana.com/)
* **Containerization:** Docker & Docker Compose [![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

### Frontend / Client (`/client/mobile`)
* **Framework:** Expo & React Native (Web support with Docker build) [![Expo](https://img.shields.io/badge/Expo-000020?style=for-the-badge&logo=expo&logoColor=white)](https://expo.dev/) [![React Native](https://img.shields.io/badge/React_Native-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://reactnative.dev/)
* **Language:** TypeScript [![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
* **Styling:** NativeWind / Tailwind CSS
---

## 🔄 CI/CD & Automation (GitHub Actions)

The project utilizes two distinct GitHub Actions pipelines to maintain code quality and ensure reliable deployments:

1. **Backend CI/CD (`backend.yml`):**
   * Spins up ephemeral PostgreSQL and Redis service containers via Docker.
   * Runs Maven build and integration test suites (`./mvnw -B verify`).
   * Verifies Docker image compilation.
   * Automatically triggers deployment webhooks upon pushes to the `master` branch.

2. **Client CI/CD (`client.yml`):**
   * Sets up the Node.js environment and installs dependencies (`npm ci`).
   * Executes TypeScript type checking (`npx tsc --noEmit`).
   * Verifies production web exports (`npx expo export --platform web`).

---

## 🛠️ Local Development & Running Options

To run the project locally on your machine, you can choose to run components individually or start the entire system at once using Docker Compose.

### 1. Run All (Full Stack via Docker Compose)
If you want to spin up the database, Redis, Prometheus, Grafana, Spring Boot backend, and the Expo frontend all together with a single command from the root directory (where your `docker-compose.yml` is located):
```
docker-compose up --build
```
- **Frontend:** [http://localhost:3000](http://localhost:3000)
- **Backend API:** [http://localhost:8080](http://localhost:8080)
- **Grafana Dashboard:** [http://localhost:3001](http://localhost:3001) (Default credentials: `admin` / `admin`)
- **Prometheus:** [http://localhost:9090](http://localhost:9090)
- **Swagger API Docs:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

# 2. Run the Backend (Manually without Docker Compose)
If you prefer running the backend services separately for development/debugging:

## 2.1. Start Infrastructure (PostgreSQL & Redis):
You can spin up just the database and Redis using docker or run them locally.

## 2.2. Run Spring Boot:
```
cd server
./mvnw spring-boot:run
```
# Run the Frontend (Manually without Docker)
To run the Expo frontend locally in development mode:
```
cd client/mobile
npm install
npx expo start
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

