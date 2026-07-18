# Task Manager API

API RESTful para gerenciamento de tarefas construída com Spring Boot.

## 🚀 Features

- ✅ Criar, ler, atualizar e deletar tarefas (CRUD completo)
- ✅ Persistência em MySQL (TiBD Cloud)
- ✅ API RESTful com endpoints padronizados
- ✅ Validações de entrada
- ✅ Deployed no Render

## 📋 Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **MySQL**
- **Lombok**
- **Maven**

## 🔗 Live Demo

[https://task-manager-api-ella.onrender.com]

## 📦 Como Rodar Localmente

### Pré-requisitos
- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### Setup

1. Clone o repositório
```bash
git clone https://github.com/seu-user/task-api.git
cd task-api
```

2. Configure banco de dados (MySQL)
```bash
mysql -u root -p
CREATE DATABASE task_manager;
```

3. Configure variáveis de ambiente
```bash
# Editar src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/task_manager
spring.datasource.username=root
spring.datasource.password=SEU_PASSWORD
```

4. Instale dependências e rode
```bash
mvn clean install
mvn spring-boot:run
```

5. Acesse

```http://localhost:8080/api/tasks```

## 🔌 API Endpoints

### Criar Tarefa
```http
POST /api/tasks
Content-Type: application/json

{
  "title": "Fazer compras",
  "description": "Leite, pão, ovos",
  "completed": false
}

Response: 201 Created
```

### Listar Todas as Tarefas
```http
GET /api/tasks

Response: 200 OK
[
  {
    "id": 1,
    "title": "Fazer compras",
    "description": "Leite, pão, ovos",
    "completed": false,
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  }
]
```

### Obter Tarefa por ID
```http
GET /api/tasks/1

Response: 200 OK
{
  "id": 1,
  "title": "Fazer compras",
  ...
}
```

### Atualizar Tarefa
```http
PUT /api/tasks/1
Content-Type: application/json

{
  "title": "Fazer compras atualizado",
  "completed": true
}

Response: 200 OK
```

### Deletar Tarefa
```http
DELETE /api/tasks/1

Response: 204 No Content
```

## 📁 Estrutura do Projeto

src/main/java/com/taskmanager/
├── controller/
│   └── TaskController.java
├── service/
│   └── TaskService.java
├── repository/
│   └── TaskRepository.java
├── entity/
│   └── Task.java
└── TaskApiApplication.java

## 🧪 Testing

Importe `postman-collection.json` (em anexo) no Postman para testar endpoints facilmente.

## 📝 Aprendizados

- Spring Boot e Spring Data JPA
- REST API design
- JPA/Hibernate
- MySQL
- Deployment no Heroku

## 🤝 Author

[Carlos Mendoza]
- GitHub: [@carlosjmendozadev](https://github.com/carlosjmendozadev)
- LinkedIn: [Carlos Mendoza](https://www.linkedin.com/in/carlosjmendoza91/)

## 📄 License

MIT
