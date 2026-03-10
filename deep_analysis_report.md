# 🔍 Análise Profunda do Sistema de Microserviços


### 10. Gateway sem Autenticação Própria
**Arquivo:** [Gateway application.properties](file:///c:/Users/Ralf/Desktop/Programa%C3%A7%C3%A3o/meu-ecommerce-microservicos/Gateway/gateway/src/main/resources/application.properties)

O Gateway repassa tudo sem verificar tokens. Cada microserviço valida separadamente. Isso funciona, mas idealmente o Gateway seria o ponto central de validação JWT, simplificando os serviços internos.

---


### 15. Sem Testes Unitários
Nenhum serviço possui testes JUnit. Isso torna refatorações arriscadas e impede CI/CD confiável.
