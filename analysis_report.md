# 🔍 Análise Completa — E-commerce Microserviços

Análise de todos os 5 microserviços (User, Product, Order, Payments, Inventory) com recomendações priorizadas por impacto.

---

## 🔴 Problemas Críticos (Segurança & Arquitetura)

### 3. Credenciais hardcoded em todos os arquivos

Todos os [application.properties](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/user/src/main/resources/application.properties) e [docker-compose.yml](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/docker-compose.yml) contêm:
- `password=root` (PostgreSQL)
- `username=guest / password=guest` (RabbitMQ)

**O que falta:** Usar variáveis do [application.properties](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/user/src/main/resources/application.properties) via `${VARIAVEL:valor_padrao}` ou Spring Profiles (`application-dev.properties` e `application-prod.properties`).

---


### 5. Nenhum código de mensageria RabbitMQ implementado

Apesar de `spring-boot-starter-amqp` estar no [pom.xml](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/user/pom.xml) de todos os serviços, **não existe**:
- Nenhuma classe `@RabbitListener`
- Nenhum `RabbitTemplate.convertAndSend()`
- Nenhuma configuração de Exchanges, Queues ou Bindings

**O README diz que os serviços se comunicam via RabbitMQ, mas isso não existe no código.** A comunicação entre serviços é inexistente.

---

## 🟠 Problemas Importantes (Qualidade de Código)

### 6. Entidade JPA exposta diretamente nos Controllers

Todos os controllers (exceto User no login) recebem a entidade JPA diretamente no `@RequestBody`:

```java
// Todos fazem isso:
public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product) { ... }
```

**Problema:** O cliente pode enviar `id` no JSON e sobrescrever registros. Deveria usar DTOs de **request** separados (ex: `CreateProductRequest`).

---

### 7. Sem validação de dados de entrada

Nenhum campo tem `@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Min`, etc. Exemplos:
- Criar usuário com `username = null` e `password = ""` → funciona
- Criar produto com `price = -999` → funciona
- Criar pedido com `total_amount = null` → funciona

**O que falta:** Adicionar Bean Validation (`spring-boot-starter-validation`) e `@Valid` nos controllers.

---

### 8. Sem tratamento global de exceções

Todos os services lançam `RuntimeException` genérica:

```java
.orElseThrow(() -> new RuntimeException("Order not found"));
```

O cliente recebe `500 Internal Server Error` com stack trace. **O que falta:**
- Exceções customizadas (ex: `ResourceNotFoundException`)
- `@RestControllerAdvice` global retornando HTTP 404, 400, 409, etc.
- Respostas padronizadas de erro com `timestamp`, `status`, `message`

---

### 9. Inconsistência de nomenclatura nos campos

| Serviço | Estilo | Exemplos |
|---------|--------|----------|
| **Order** | snake_case ❌ | `customer_id`, `order_date`, `total_amount` |
| **Payments** | snake_case ❌ | `order_id`, `transaction_id`, `payment_date` |
| **Product** | camelCase ✅ | `categoryId`, `imageUrl` |
| **User** | camelCase ✅ | `username`, [password](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/user/src/main/java/com/betolara1/user/config/SecurityConfig.java#17-21) |
| **Inventory** | camelCase ✅ | `quantity`, `reserved` |

**Java usa camelCase por padrão.** Os campos snake_case (Order e Payments) geram getters estranhos como `getCustomer_id()` e violam convençoes Java. Use `@Column(name = "customer_id")` para mapear nomes de colunas diferentes.

---

### 10. Mistura de `@Autowired` (field injection) e `@RequiredArgsConstructor` (constructor injection)

| Serviço | Controller | Service |
|---------|-----------|---------|
| **User** | `@Autowired` ❌ | `@RequiredArgsConstructor` ✅ |
| **Product** | `@Autowired` ❌ | `@Autowired` ❌ |
| **Order** | `@Autowired` ❌ | `@Autowired` ❌ |
| **Payments** | `@Autowired` ❌ | `@Autowired` ❌ |
| **Inventory** | `@Autowired` ❌ | `@Autowired` ❌ |

**Padronizar tudo com constructor injection** (`@RequiredArgsConstructor` + campos `final`). Isso facilita testes e é a recomendação oficial do Spring.

---

### 11. Bug no PaymentController — `@RequestBody` no GET

```java
// PaymentController.java — linha 38
@GetMapping("/id/{id}")
public ResponseEntity<PaymentDTO> getPaymentById(@RequestBody Long id) { // ❌ deveria ser @PathVariable
```

O `id` deveria vir do `@PathVariable`, não do `@RequestBody`. Requisições GET não têm body.

---

### 12. Tipo errado para valores monetários

```java
// Order.java
private Long total_amount;    // ❌ Long para dinheiro

// Payment.java  
private int amount;           // ❌ int para dinheiro
```

**Valores monetários devem usar `BigDecimal`** para evitar perda de precisão. `Long` e `int` não suportam centavos.

---

### 13. Tipo `Time` usado para data de pagamento

```java
// Payment.java
private Time payment_date;    // ❌ java.sql.Time registra apenas hora
```

Deveria ser `LocalDateTime` ou `Instant` para registrar data + hora.

---

## 🟡 Melhorias de Robustez (Infraestrutura)

### 14. Sem API Gateway

Cada serviço expõe sua própria porta (8080-8084). **Um API Gateway (Spring Cloud Gateway):**
- Centraliza a entrada em uma única porta
- Faz roteamento inteligente (`/api/users/**` → User Service)
- Centraliza autenticação JWT
- Permite rate limiting, CORS, logging
date
---

### 15. Sem Circuit Breaker (Resilience4j)

Se um serviço cair, os outros continuam chamando e acumulam timeout. **O que falta:**
- Circuit Breaker para interromper chamadas a serviços indisponíveis
- Retry com backoff exponencial
- Fallback methods

---

### 16. Sem Health Checks / Actuator

Nenhum serviço tem `spring-boot-starter-actuator`. Isso impede:
- Monitoramento de saúde (`/actuator/health`)
- Métricas de performance (`/actuator/metrics`)
- Informações do ambiente (`/actuator/info`)

> [!NOTE]
> O Dockerfile do User tem um `HEALTHCHECK curl`, mas sem Actuator, o endpoint `/` provavelmente retorna 401 (tudo está bloqueado pelo Security).

---

### 17. Sem Swagger/OpenAPI

Nenhum serviço tem documentação de API. **Adicionar `springdoc-openapi-starter-webmvc-ui`** para gerar documentação interativa automaticamente em `/swagger-ui.html`.

---

### 18. Sem testes reais

Todos os 5 serviços têm apenas o teste padrão gerado pelo Spring Initializr:
```java
@Test
void contextLoads() { }
```

**O que falta:** Testes unitários para Services, testes de integração para Controllers (`@WebMvcTest`), testes para os consumers/producers RabbitMQ.

---

### 19. `ddl-auto=update` inadequado para produção

```properties
spring.jpa.hibernate.ddl-auto=update
```

Isso permite que o Hibernate altere o schema do banco em produção. **Use `validate` em produção** e gerencie migrações com **Flyway** ou **Liquibase**.

---

### 20. Sem paginação nos endpoints de listagem

```java
// Todos fazem:
return productRepository.findAll();  // retorna TODOS os registros
```

Com milhares de registros, isso causa problemas de memória e performance. **Use `Pageable`** do Spring Data.

---

## 🟢 Melhorias Desejáveis (Polish)

| # | Melhoria | Descrição |
|---|----------|-----------|
| 21 | **Service Discovery** | Eureka ou Consul para os serviços se encontrarem dinamicamente |
| 22 | **Config Server** | Spring Cloud Config para centralizar configurações |
| 23 | **Logging centralizado** | ELK Stack (Elasticsearch + Logstash + Kibana) ou Loki |
| 24 | **Tracing distribuído** | Micrometer + Zipkin/Jaeger para rastrear requests entre serviços |
| 25 | **GitHub Actions** | CI/CD com build, testes e lint |
| 26 | **Docker multi-stage otimizado** | Cada Dockerfile instala Maven inteiro (~400MB). Usar Maven wrapper no builder estágio |
| 27 | **Nomes de tabelas = nomes de banco** | `@Table(name = "product_db")` — o nome da tabela não deveria ser o nome do database. Usar `@Table(name = "products")` |
| 28 | **DTO inconsistente no Payments** | [PaymentDTO](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/Payments/payments/src/main/java/com/betolara1/Payments/DTO/PaymentDTO.java#9-43) usa factory method estático enquanto todos os outros usam construtor. Padronizar |
| 29 | **Pacotes com case inconsistente** | `com.betolara1.Product`, `com.betolara1.Payments` (maiúsculo) vs `com.betolara1.order`, `com.betolara1.inventory` (minúsculo). Java convention: tudo lowercase |
| 30 | **CORS** | Nenhum serviço configura CORS para permitir acesso de frontends |

---

## 📊 Priorização Sugerida

Ordem recomendada de implementação para ter o maior impacto com menor esforço:

```
Fase 1 — Consertar o que está quebrado
  ├─ 1. RabbitMQ compartilhado (uma instância)
  ├─ 2. Implementar mensageria real (producers/consumers)
  ├─ 3. Corrigir bug do @RequestBody no PaymentController
  ├─ 4. Retornar UserDTO no register (não User)
  └─ 5. Corrigir tipos (BigDecimal, LocalDateTime)

Fase 2 — Segurança
  ├─ 6. Implementar JWT (geração + validação)
  ├─ 7. Externalizar credenciais (profiles)
  └─ 8. Liberar /auth/login no SecurityConfig

Fase 3 — Qualidade de código
  ├─ 9. Bean Validation (@Valid)
  ├─ 10. GlobalExceptionHandler (@RestControllerAdvice)
  ├─ 11. Padronizar camelCase
  ├─ 12. Constructor injection em tudo
  └─ 13. DTOs de request separados

Fase 4 — Infraestrutura
  ├─ 14. Spring Boot Actuator
  ├─ 15. Swagger/OpenAPI
  ├─ 16. Paginação
  ├─ 17. Flyway para migrações
  └─ 18. Testes (unitários + integração)

Fase 5 — Arquitetura avançada
  ├─ 19. API Gateway
  ├─ 20. Circuit Breaker
  ├─ 21. Tracing distribuído
  └─ 22. GitHub Actions CI/CD
```


MELHORAR AS CONSULTAS DE TODOS AS ROTAS