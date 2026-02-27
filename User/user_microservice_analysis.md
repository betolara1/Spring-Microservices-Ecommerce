

---

## 🛑 O que falta / O que pode melhorar




### 6. Tratamento de Erros Genérico
O sistema utiliza `RuntimeException` e não possui um tratador global.
- **Problema**: Em caso de erro (ex: usuário não encontrado), o cliente recebe um Erro 500 com o stack trace do Java, o que é inseguro e pouco amigável.
- **Solução**: Implementar um `@RestControllerAdvice` para capturar exceções e retornar códigos HTTP adequados (404, 400, 401).



### 7. Dependências Ausentes
Faltam ferramentas essenciais para a saúde do microserviço no [pom.xml](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/user/pom.xml):
- **Actuator**: Necessário para monitoramento e health checks (essencial para que o Docker/Kubernetes saiba se o serviço está vivo).

---

## 🛠️ Recomendações Priorizadas

| Prioridade | Ação | Descrição |
| :--- | :--- | :--- |
| 🟡 **Média** | **Global Exception Handler** | Parar de retornar 500 para erros que são do cliente (ex: 404). |
| 🔵 **Baixa** | **Otimizar Dockerfile** | Usar Maven Wrapper ([./mvnw](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/user/mvnw)) ou separar o build para ser mais rápido. |

---
