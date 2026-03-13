# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
WORKDIR /build

# 1. Copia o pom.xml primeiro
COPY eureka/pom.xml ./

# 2. Baixa as dependências (camada pesada que será cacheada)
# Isso só rodará de novo se o seu pom.xml mudar
RUN mvn dependency:go-offline -B -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false -Dmaven.wagon.http.retryHandler.count=5 -Dhttps.protocols=TLSv1.2,TLSv1.3

# 3. Agora sim copia o código fonte e gera o JAR
COPY eureka/src ./src
RUN mvn clean package -DskipTests -B -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false -Dmaven.wagon.http.retryHandler.count=5 -Dhttps.protocols=TLSv1.2,TLSv1.3


# Estágio 2: Runtime (Imagem final bem leve)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*


# Copia apenas o JAR do estágio anterior
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
