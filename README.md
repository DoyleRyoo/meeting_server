# damlok_backend

### 기술 스택
- Java Spring Boot JPA
- Docker
- Fast Api
- PostgreSQL
- pgvector
- storage

### 📁 구조
__*도커로 기본 세팅 필요*__

```text
backend
├── .mvn/
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── damrok
│   │   │           └── server
│   │   │               ├── ServerApplication.java
│   │   │               │
│   │   │               ├── common
│   │   │               │   ├── config
│   │   │               │   ├── exception
│   │   │               │   ├── response
│   │   │               │   └── util
│   │   │               │
│   │   │               ├── domain
│   │   │               │   ├── user
│   │   │               │   │   ├── controller
│   │   │               │   │   ├── service
│   │   │               │   │   ├── repository
│   │   │               │   │   ├── entity
│   │   │               │   │   └── dto
│   │   │               │   │
│   │   │               │   ├── meeting
│   │   │               │   └── workspace
│   │   │               │
│   │   │               └── infrastructure
│   │   │                   ├── notion
│   │   │                   └── ai
│   │   │
│   │   └── resources
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   │
│   └── test
│       └── java
│           └── com
│               └── damlok
│                   └── server
│
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── Dockerfile
```
