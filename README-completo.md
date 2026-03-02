# 🛒 E-commerce Microserviços

Um projeto de e-commerce desenvolvido com **Spring Boot 4.0.1/4.0.2** e **Java 21**, utilizando arquitetura de **microserviços** para aprender e praticar várias funcionalidades avançadas do Spring Boot.

> ✅ **Status:** Projeto funcional - Cinco microserviços operacionais

## 📋 Sobre o Projeto

Este projeto implementa um sistema de e-commerce com múltiplos microserviços independentes que se comunicam através de **mensageria (RabbitMQ/AMQP)**. O objetivo é explorar e consolidar conhecimentos em:

- ✅ Spring Boot (Web, Data JPA, AMQP)
- ✅ Arquitetura de Microserviços
- ✅ Docker & Docker Compose
- ✅ Mensageria (RabbitMQ)
- ✅ Bancos de Dados Relacionais
- ✅ API REST
- ✅ Containerização com Docker

## 🏗️ Arquitetura

O projeto está organizado em cinco microserviços principais:

```
meu-ecommerce-microservicos/
├── user/           → Serviço de Autenticação e Usuários
├── product/        → Serviço de Produtos
├── inventory/      → Serviço de Inventário
├── order/          → Serviço de Pedidos
├── payments/       → Serviço de Pagamentos
└── docker-compose.overview.yml
```

### 📦 Microserviços

#### **User (Autenticação e Usuários)**
- **Porta:** 8080
- **Descrição:** Autenticação, registro de usuários e dados cadastrais
- **Java Version:** 21
- **Dependências principais:** Spring Data JPA, Spring AMQP, Spring Security
- **Status:** ✅ Concluído
- **Banco de Dados:** PostgreSQL (user_db)

#### **Product (Produtos)**
- **Porta:** 8083
- **Descrição:** CRUD de produtos, categorias e controle de preços
- **Java Version:** 21
- **Dependências principais:** Spring Data JPA, Spring AMQP, Spring MVC
- **Status:** ✅ Concluído
- **Banco de Dados:** PostgreSQL (product_db)

#### **Inventory (Inventário)**
- **Porta:** 8081
- **Descrição:** Verifica disponibilidade, reserva e controle de estoque de produtos
- **Java Version:** 21
- **Dependências principais:** Spring Data JPA, Spring AMQP, Spring MVC
- **Status:** ✅ Concluído
- **Banco de Dados:** PostgreSQL (inventory_db)

#### **Order (Pedidos)**
- **Porta:** 8082
- **Descrição:** Recebe pedidos, gerencia status da compra e histórico de transações
- **Java Version:** 21
- **Dependências principais:** Spring Data JPA, Spring AMQP, Spring MVC
- **Status:** ✅ Concluído
- **Banco de Dados:** PostgreSQL (order_db)

#### **Payments (Pagamentos)**
- **Porta:** 8084
- **Descrição:** Processamento de transações financeiras e gestão de pagamentos
- **Java Version:** 21
- **Dependências principais:** Spring Data JPA, Spring AMQP, Spring MVC
- **Status:** ✅ Concluído
- **Banco de Dados:** PostgreSQL (payments_db)

## 🔧 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.0.1 / 4.0.2**
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - Spring Boot Starter AMQP
  - Spring Boot Starter Security (User Service)
  - Spring Boot DevTools
  - Spring Boot Docker Compose
- **Banco de Dados:** PostgreSQL (5 databases separados)
- **Docker & Docker Compose**
- **RabbitMQ** (Mensageria AMQP)
- **Maven** (Build & Dependency Management)
- **Spring Security** (Autenticação e Autorização)

## 🚀 Como Executar

### Pré-requisitos

- Java 21+
- Docker & Docker Compose
- Maven 3.6+ (ou usar `mvnw`)

### 1. Clonar o Repositório

```bash
git clone https://github.com/betolara1/meu-ecommerce-microservicos.git
cd meu-ecommerce-microservicos
```

### 2. Iniciar os Serviços com Docker Compose

A arquitetura usa `docker-compose.overview.yml` que agrupa todos os serviços:

```bash
docker-compose -f docker-compose.overview.yml up -d
```

Isso iniciará:
- ✅ PostgreSQL (5 databases separados)
- ✅ RabbitMQ (message broker)
- ✅ User Service (8080)
- ✅ Inventory Service (8081)
- ✅ Order Service (8082)
- ✅ Product Service (8083)
- ✅ Payments Service (8084)

**Status:** Docker Compose com 5 microserviços operacionais 100% 🐳

### 3. Executar Localmente (Desenvolvimento)

Para cada microserviço:

```bash
cd <microservico>
./mvnw spring-boot:run
```

Ou com Maven instalado:

```bash
mvn spring-boot:run
```

## 📡 Comunicação Entre Serviços

Os microserviços se comunicam através de **filas RabbitMQ (AMQP)**:

```
User Service (8080) → [Autenticação]
Product Service (8083) → [Catálogo de Produtos]
Inventory Service (8081) ← [Verificar Estoque]
Order Service (8082) → [Criar Pedidos] → Inventory Service
                      → [Confirmar Pedido] → Payments Service
Payments Service (8084) → [Processar Transação] → Order Service
                       → [Confirmação de Pagamento] → RabbitMQ
```

## 🔄 Fluxo de Funcionamento

### 1. Registro e Autenticação do Usuário
```
Cliente → POST /auth/register → User Service (8080)
        ↓
        User Service valida, criptografa senha e armazena no PostgreSQL
        ↓
        Retorna dados do usuário (sem senha)
```

### 2. Consulta de Produtos
```
Cliente → GET /products → Product Service (8083)
        ↓
        Product Service retorna lista de produtos disponíveis
```

### 3. Criação de Pedido
```
Cliente → POST /order → Order Service (8082)
        ↓
        Order Service cria pedido e envia mensagem para:
        ├─ Inventory Service (8081) - Verificar e reservar estoque
        └─ Payments Service (8084) - Aguardar confirmação
        ↓
        Se aprovado, pedido é confirmado
```

### 4. Processamento de Pagamento
```
Order Service → Mensagem RabbitMQ → Payments Service (8084)
                                    ↓
                                    Processa transação
                                    ↓
                                    Retorna confirmação via RabbitMQ
                                    ↓
Order Service recebe confirmação → Atualiza status do pedido
```

## 🗂️ Estrutura de Projeto

Cada microserviço segue a seguinte estrutura:

```
microservico/
├── src/
│   ├── main/
│   │   ├── java/com/betolara1/<service>/
│   │   │   ├── <Service>Application.java
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── dto/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/betolara1/<service>/
├── pom.xml
├── mvnw
└── compose.yaml
```

## 🔗 Endpoints Disponíveis

### User Service (8080) - Autenticação e Usuários

- `POST /auth/register` - Registrar novo usuário
- `POST /auth/login` - Fazer login
- `GET /users/{id}` - Buscar perfil de usuário (protegido)
- `PUT /users/{id}` - Atualizar dados do usuário (protegido)
- `DELETE /users/{id}` - Excluir usuário (protegido)

### Product Service (8083)

- `GET /products` - Listar todos os produtos
- `POST /products` - Criar novo produto
- `GET /products/id/{id}` - Buscar produto específico
- `PUT /products/id/{id}` - Atualizar produto
- `DELETE /products/id/{id}` - Deletar produto

### Inventory Service (8081)

- `GET /inventory` - Listar todo o inventário
- `POST /inventory` - Criar novo item de inventário
- `GET /inventory/id/{id}` - Buscar item específico
- `GET /inventory/sku/{sku}` - Buscar item por SKU
- `PUT /inventory/{id}` - Atualizar quantidade em estoque
- `DELETE /inventory/{id}` - Remover item do inventário

### Order Service (8082)

- `GET /order` - Listar todos os pedidos
- `POST /order` - Criar novo pedido
- `GET /order/{id}` - Buscar pedido específico
- `PUT /order/{id}` - Atualizar status do pedido
- `DELETE /order/{id}` - Cancelar pedido

### Payments Service (8084)

- `GET /payments` - Listar todas as transações
- `POST /payments` - Processar novo pagamento
- `GET /payments/{id}` - Buscar transação específica
- `PUT /payments/{id}` - Atualizar status do pagamento
- `DELETE /payments/{id}` - Cancelar pagamento

## 📝 Configuração

Cada microserviço possui seu arquivo `application.properties` com as seguintes configurações padrão:

```properties
# User Service
spring.application.name=user
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/user_db
spring.datasource.username=postgres
spring.datasource.password=root
```

```properties
# Product Service
spring.application.name=Product
server.port=8083
spring.datasource.url=jdbc:postgresql://localhost:5432/product_db
spring.datasource.username=postgres
spring.datasource.password=root
```

```properties
# Inventory Service
spring.application.name=inventory
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
spring.datasource.username=postgres
spring.datasource.password=root
```

```properties
# Order Service
spring.application.name=order
server.port=8082
spring.datasource.url=jdbc:postgresql://localhost:5432/order_db
spring.datasource.username=postgres
spring.datasource.password=root
```

```properties
# Payments Service
spring.application.name=Payments
server.port=8084
spring.datasource.url=jdbc:postgresql://localhost:5432/payments_db
spring.datasource.username=postgres
spring.datasource.password=root
```

### Configuração RabbitMQ (comum a todos)

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=update
```

## 🧪 Testes

Para executar os testes de um microserviço:

```bash
cd <microservico>
./mvnw test
```

Ou com Maven:

```bash
mvn test
```

## 🧪 Testando a API

### Usando cURL

#### 1. Registrar novo usuário
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"usuario@email.com","password":"senha123"}'
```

#### 2. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"usuario@email.com","password":"senha123"}'
```

#### 3. Listar produtos
```bash
curl -X GET http://localhost:8083/products
```

#### 4. Criar novo produto
```bash
curl -X POST http://localhost:8083/products \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Produto Test",
    "price":99.99,
    "category":"Eletrônicos",
    "description":"Descrição do produto"
  }'
```

#### 5. Criar pedido
```bash
curl -X POST http://localhost:8082/order \
  -H "Content-Type: application/json" \
  -d '{
    "userId":1,
    "productId":1,
    "quantity":2,
    "totalPrice":199.98
  }'
```

### Usando Postman ou Insomnia

Você pode importar os endpoints no Postman/Insomnia para testes mais completos. Crie uma coleção com as requisições acima e teste os fluxos completos.

### Verificando RabbitMQ

Acesse a interface web do RabbitMQ:
```
http://localhost:15672
# Padrão: guest / guest
```

## 📚 Aprendizados

Este projeto foi criado para consolidar conhecimentos em:

- [x] Spring Boot Web (REST APIs)
- [x] Spring Data JPA (Persistência de dados)
- [x] Spring AMQP (Mensageria)
- [x] Spring Security (Autenticação)
- [x] Docker & Containerização
- [x] Arquitetura de Microserviços
- [x] Padrões de Design (DTO, Repository, Service)
- [x] Comunicação entre microserviços
- [x] PostgreSQL
- [ ] Testes Unitários e de Integração

## 🔄 Próximos Passos

- [x] Completar microserviço Product
- [x] Completar microserviço User com Spring Security
- [x] Completar microserviço Payments
- [x] Configurar Docker Compose (100% funcional com 5 serviços)
- [ ] Implementar logging centralizado (ELK Stack)
- [ ] Adicionar autenticação JWT com tokens
- [ ] Implementar circuit breaker (Resilience4j)
- [ ] Service discovery (Eureka/Consul)
- [ ] API Gateway
- [ ] Melhorar testes (Unit, Integration, E2E)
- [ ] Documentação Swagger/OpenAPI
- [ ] Health checks e métricas (Actuator)

## ⚠️ Troubleshooting

### MAPEAMENTO DE PORTAS CORRETO

| Serviço | Porta | Função |
|---------|-------|--------|
| **User Service** | `8080` | Autenticação |
| **Inventory Service** | `8081` | Estoque |
| **Order Service** | `8082` | Pedidos |
| **Product Service** | `8083` | Produtos |
| **Payments Service** | `8084` | Pagamentos |
| **RabbitMQ** | `5672` | Mensageria |
| **RabbitMQ Management** | `15672` | Web UI |

### Problema: Portas já em uso
```bash
# Encontrar processo usando porta (exemplo: 8080)
netstat -ano | findstr :8080

# Encerrar processo (substituir PID)
taskkill /PID <PID> /F
```

### Problema: Banco de dados não conecta
- Verificar se PostgreSQL está rodando nos containers
- Verificar credenciais em application.properties
- Verificar se o banco de dados existe

### Problema: RabbitMQ não conecta
- Verificar se RabbitMQ está rodando em `localhost:5672`
- Verificar credenciais padrão: `guest/guest`
- Acessar interface web em `http://localhost:15672`

### Limpar e reiniciar tudo
```bash
# Parar todos os containers
docker-compose -f docker-compose.overview.yml down

# Remover volumes (limpar dados)
docker-compose -f docker-compose.overview.yml down -v

# Limpar sistema 
docker system prune -f

# Criar a Network de conexão local entre os microserviços
docker network create ecommerce-network

# Reiniciar
docker-compose -f docker-compose.overview.yml up -d
docker-compose -f docker-compose.overview.yml up -d --build

# Reiniciar um microserviço específico
docker-compose up -d --build user-service
```

### 📊 Scripts de Diagnóstico

**Windows PowerShell:**
```powershell
.\diagnostico.ps1
```

Este script verifica:
- ✅ Status de todos os containers
- ✅ Conectividade em cada porta
- ✅ Endpoints HTTP respondendo
- ✅ Teste de registro de usuário
- ✅ Diagnóstico completo do sistema

### 📄 Documentos de Referência

- **DIAGNOSTICO_CONEXAO.md** - Análise completa dos problemas encontrados
- **SWAGGER_GUIDE.md** - Guia completo sobre Swagger/OpenAPI

## 📊 Informações do Projeto

- **Arquitetura:** Microserviços com comunicação assíncrona via RabbitMQ
- **Padrões utilizados:** Repository, Service, DTO, REST
- **Segurança:** Spring Security com criptografia de senhas
- **Escalabilidade:** Preparado para crescimento com múltiplos bancos de dados
- **Containerização:** 100% dockerizado com compose

## 📖 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring AMQP](https://spring.io/projects/spring-amqp)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [RabbitMQ Tutorial](https://www.rabbitmq.com/getstarted.html)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Microservices Patterns](https://microservices.io/patterns/)

## 👨‍💻 Autor

**Ralf Betolara**

## 📄 Licença

Projeto de aprendizado pessoal.

---

**Última atualização:** Janeiro de 2026  
**Status:** 5 microserviços operacionais ✅ | Docker Compose 100% funcional 🐳 | Problemas de conexão RESOLVIDOS 🔧 | Pronto para produção 🚀
