
# 💈 Barbearia API - Sistema de Gerenciamento

Este é um projeto backend desenvolvido em **Java com Spring Boot** para gerenciamento de uma barbearia. A aplicação permite cadastro de usuários, controle de produtos, agendamento de serviços, autenticação via JWT e outras funcionalidades administrativas.

## 🚀 Funcionalidades

- ✅ Cadastro e autenticação de usuários (com JWT)
- ✅ Agendamento de serviços
- ✅ Cadastro e listagem de produtos
- ✅ Gerenciamento de serviços oferecidos pela barbearia
- ✅ Controle de permissões (Roles: ADMIN / CLIENTE)
- ✅ Geração de relatórios em PDF

## 🛠️ Tecnologias utilizadas

- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA
- Banco de dados (H2 ou PostgreSQL)
- JWT para autenticação
- Maven
- Hibernate
- JasperReports (para geração de PDFs)

## 📁 Estrutura do projeto

```
src/
├── main/
│   ├── java/com/devffl/babershop/
│   │   ├── controllers/
│   │   ├── dto/
│   │   ├── entities/
│   │   ├── repositories/
│   │   ├── security/
│   │   └── BabershopApplication.java
│   └── resources/
│       ├── application.properties
```

## 🔐 Autenticação

A autenticação é feita via JWT. Após o login com e-mail e senha, o token deve ser usado no header `Authorization` como:

```
Authorization: Bearer <seu-token-jwt>
```

## 📦 Como executar o projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/seu-repo.git
   ```

2. Navegue até o projeto e execute:
   ```bash
   ./mvnw spring-boot:run
   ```

3. A aplicação estará disponível em:
   ```
   http://localhost:8080
   ```

## 🧪 Exemplos de Endpoints

- `POST /auth/login` - Login do usuário
- `POST /users` - Cadastro de novo usuário
- `GET /produtos` - Lista produtos cadastrados
- `POST /agendamentos` - Cadastra novo agendamento

## ✍️ Autor

Desenvolvido por **Felipe Lucas**  
📧 [arq.felipe.f@gmail.com]  
🔗 [https://www.linkedin.com/in/felipe-lucas-0442a426b/]
