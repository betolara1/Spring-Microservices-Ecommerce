# Guia de Uso do Spring Boot Actuator

O Actuator fornece endpoints prontos para monitorar e gerenciar sua aplicação. No seu microserviço `user`, ele já está configurado e pronto para uso.

## Como acessar os endpoints

Por padrão, todos os endpoints do Actuator ficam sob o prefixo `/actuator`. Considerando que sua aplicação rode na porta `8080` (padrão do Spring Boot), você pode acessar:

1.  **Listagem de Endpoints**: [http://localhost:8080/actuator](http://localhost:8080/actuator)
    *   Mostra todos os endpoints disponíveis e expostos.
2.  **Saúde da Aplicação (Health)**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
    *   Verifica se a aplicação e suas dependências (Banco de Dados, RabbitMQ, etc.) estão online.
3.  **Informações da Aplicação (Info)**: [http://localhost:8080/actuator/info](http://localhost:8080/actuator/info)
    *   Exibe informações personalizadas que você definir no [application.properties](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/user/src/main/resources/application.properties).
4.  **Métricas Prometheus**: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)
    *   Exporta métricas formatadas para serem lidas pelo Prometheus.

## Configurações Atuais

Seu arquivo [application.properties](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/user/src/main/resources/application.properties) já possui as seguintes configurações:

```properties
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.health.probes.enabled=true
```

Isso significa que esses três endpoints estão expostos via Web.

## Segurança

Notei que no seu [SecurityConfig.java](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/user/src/main/java/com/betolara1/user/config/SecurityConfig.java), você já liberou o acesso público aos endpoints do actuator:

```java
.requestMatchers("/actuator/**").permitAll()
```

Dessa forma, você não precisará de autenticação JWT para consultar a saúde da aplicação.

## Dicas de Uso

### 1. Ver Detalhes da Saúde
Para ver detalhes sobre o banco de dados e o RabbitMQ no endpoint `/health`, adicione isso ao seu [application.properties](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/User/user/src/main/resources/application.properties):
```properties
management.endpoint.health.show-details=always
```

### 2. Adicionar Informações Customizadas
Você pode expor a versão e descrição da sua API no endpoint `/info`:
```properties
info.app.name=${spring.application.name}
info.app.description=Microserviço de Gerenciamento de Usuários
info.app.version=1.0.0
```

### 3. Expor mais Endpoints
Se quiser ver todas as métricas detalhadas (memória, CPU, threads), você pode expor o endpoint `metrics`:
```properties
management.endpoints.web.exposure.include=health,info,prometheus,metrics
```
E acessar em: [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)
