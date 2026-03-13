# 🌐 Guia Central de Acesso e Monitoramento

Este documento centraliza todas as informações de acesso, rotas e monitoramento para os microserviços do E-commerce.

## 🚪 API Gateway (Ponto de Entrada Único)
O Gateway é a porta de entrada para todas as requisições externas. No ambiente Docker, ele expõe a porta `8080`.

| Microserviço | Prefixo da Rota | URL Exemplo | Swagger UI Direto |
| :--- | :--- | :--- | :--- |
| **User (Auth)** | `/auth/**` | [http://localhost:8080/auth/login](http://localhost:8080/auth/login) | [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html) |
| **User (Perfil)** | `/users/**` | [http://localhost:8080/users](http://localhost:8080/users) | [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html) |
| **Product** | `/products/**` | [http://localhost:8080/products](http://localhost:8080/products) | [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html) |
| **Order** | `/orders/**` | [http://localhost:8080/orders](http://localhost:8080/orders) | [http://localhost:8083/swagger-ui/index.html](http://localhost:8083/swagger-ui/index.html) |
| **Inventory** | `/inventory/**` | [http://localhost:8080/inventory](http://localhost:8080/inventory) | [http://localhost:8084/swagger-ui/index.html](http://localhost:8084/swagger-ui/index.html) |
| **Payments** | `/payments/**` | [http://localhost:8080/payments](http://localhost:8080/payments) | [http://localhost:8085/swagger-ui/index.html](http://localhost:8085/swagger-ui/index.html) |

---

## 📋 Rotas Detalhadas por Microserviço

### 🔐 Auth (`/auth`)
| Método | Rota | Descrição | Acesso | Headers |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Registrar novo usuário | Público | — |
| `POST` | `/auth/login` | Login (retorna JWT) | Público | — |

---

### 👤 User (`/users`)
| Método | Rota | Descrição | Acesso | Headers |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/users` | Listar todos (ADMIN) ou próprio perfil (USER) | ADMIN / USER | `X-User-Role`, `X-User-Id` |
| `PUT` | `/users/{id}` | Atualizar próprio perfil | Próprio usuário | `X-User-Id` |
| `DELETE` | `/users/{id}` | Deletar usuário | ADMIN | `X-User-Role` |

---

### 📦 Product (`/products`)
| Método | Rota | Descrição | Acesso | Headers |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/products` | Listar todos os produtos | Público | — |
| `GET` | `/products/{id}` | Buscar produto por ID | Público | — |
| `GET` | `/products/name={name}` | Buscar produto por nome | Público | — |
| `GET` | `/products/sku={sku}` | Buscar produto por SKU | Público | — |
| `GET` | `/products/category={categoryId}` | Buscar produtos por categoria | Público | — |
| `GET` | `/products/active={active}` | Buscar produtos ativos/inativos | Público | — |
| `POST` | `/products` | Criar produto | ADMIN | `X-User-Role` |
| `PUT` | `/products/{id}` | Atualizar produto | ADMIN | `X-User-Role` |
| `DELETE` | `/products/{id}` | Deletar produto | ADMIN | `X-User-Role` |

---

### 🛒 Order (`/orders`)
| Método | Rota | Descrição | Acesso | Headers |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/orders` | Listar todos (ADMIN) ou próprios pedidos (USER) | ADMIN / USER | `X-User-Role`, `X-User-Id` |
| `GET` | `/orders/customerId={customerId}` | Buscar pedidos por cliente | ADMIN | `X-User-Role` |
| `GET` | `/orders/status={status}` | Buscar por status (ADMIN: todos, USER: próprios) | ADMIN / USER | `X-User-Role`, `X-User-Id` |
| `GET` | `/orders/orderDate={orderDate}` | Buscar por data (formato: `yyyy-MM-dd`) | ADMIN | `X-User-Role`, `X-User-Id` |
| `GET` | `/orders/{id}` | Buscar pedido por ID (ADMIN: qualquer, USER: próprio) | ADMIN / USER | `X-User-Role`, `X-User-Id` |
| `POST` | `/orders` | Criar pedido (customerId é preenchido automaticamente) | Autenticado | `X-User-Id` |
| `PUT` | `/orders/{id}` | Atualizar pedido | ADMIN | `X-User-Role` |
| `DELETE` | `/orders/{id}` | Deletar pedido | ADMIN | `X-User-Role` |

---

### 📊 Inventory (`/inventory`)
| Método | Rota | Descrição | Acesso | Headers |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/inventory` | Listar todo o estoque | ADMIN | `X-User-Role` |
| `GET` | `/inventory/status={status}` | Buscar estoque por status | ADMIN | `X-User-Role` |
| `GET` | `/inventory/{id}` | Buscar estoque por ID | ADMIN | `X-User-Role` |
| `GET` | `/inventory/sku={sku}` | Buscar estoque por SKU | ADMIN | `X-User-Role` |
| `POST` | `/inventory` | Criar estoque | ADMIN | `X-User-Role` |
| `PUT` | `/inventory/{id}` | Atualizar estoque | ADMIN | `X-User-Role` |
| `DELETE` | `/inventory/{id}` | Deletar estoque | ADMIN | `X-User-Role` |

---

### 💳 Payments (`/payments`)
| Método | Rota | Descrição | Acesso | Headers |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/payments` | Listar todos (ADMIN) ou próprios pagamentos (USER) | ADMIN / USER | `X-User-Role`, `X-User-Id` |
| `GET` | `/payments/status={status}` | Buscar pagamentos por status | ADMIN | `X-User-Role` |
| `GET` | `/payments/paymentMethod={paymentMethod}` | Buscar por método de pagamento | ADMIN | `X-User-Role` |
| `GET` | `/payments/{id}` | Buscar pagamento por ID | ADMIN | `X-User-Role` |
| `GET` | `/payments/orderId={orderId}` | Buscar pagamento por ID do pedido | ADMIN | `X-User-Role` |
| `GET` | `/payments/transactionId={transactionId}` | Buscar pagamento por transação | ADMIN | `X-User-Role` |
| `POST` | `/payments` | Criar pagamento | ADMIN | `X-User-Role` |
| `PUT` | `/payments/{id}` | Atualizar pagamento | ADMIN | `X-User-Role` |
| `DELETE` | `/payments/{id}` | Deletar pagamento | ADMIN | `X-User-Role` |

---

## 🐇 RabbitMQ (Mensageria)
Interface de gerenciamento de filas e exchanges do RabbitMQ.

- **Link**: [http://localhost:15672](http://localhost:15672)
- **Login**: `guest`
- **Senha**: `guest`
- **Uso**: Visualize mensagens trafegando entre microserviços (ex: `payment.created`, `inventory.reserved`).

---

## 🔍 Eureka (Service Discovery)
Painel de controle que mostra quais microserviços estão online e registrados.

- **Link**: [http://localhost:8761](http://localhost:8761)
- **Uso**: Verifique se todas as instâncias (Order, User, etc.) estão com status `UP`.

---

## 📊 Spring Boot Actuator (Saúde e Métricas)
Todos os microserviços possuem o Actuator configurado para monitoramento. Você pode acessar via Gateway ou diretamente na porta do serviço.

| Funcionalidade | Endpoint | Descrição |
| :--- | :--- | :--- |
| **Lista Geral** | `/actuator` | Lista todos os links de monitoramento disponíveis. |
| **Saúde (Health)** | `/actuator/health` | Verifica se o serviço e o Banco de Dados estão OK. |
| **Métricas** | `/actuator/metrics` | Exibe estatísticas de CPU, Memória e Threads. |
| **Prometheus** | `/actuator/prometheus` | Métricas formatadas para o painel Grafana/Prometheus. |

### Configuração de Exposição
Para liberar novos endpoints (como `beans` ou `env`), altere no `application.properties`:
```properties
management.endpoints.web.exposure.include=health,info,prometheus,metrics
```

---

## 🔐 Autenticação e Login
Para acessar rotas protegidas (ex: `/orders`), você deve seguir este fluxo:

1. **Login**: Faça um POST para [http://localhost:8080/auth/login](http://localhost:8080/auth/login) com:
   ```json
   { "username": "seu_email@v.com", "password": "sua_password" }
   ```
2. **Token**: Copie o `token` JWT retornado.
3. **Uso**: Adicione o header `Authorization: Bearer <seu_token>` nas suas próximas requisições.

> [!TIP]
> Use o **Postman** ou **Insomnia** para automatizar o envio do token Bearer.

---
*Atualizado por Antigravity em Março de 2026*
