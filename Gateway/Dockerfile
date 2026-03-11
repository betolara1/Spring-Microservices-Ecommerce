# Estágio 1: Build
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /build

# 1. Copia o Maven Wrapper e o pom.xml primeiro
COPY gateway/.mvn/ .mvn
COPY gateway/mvnw gateway/pom.xml ./

# 2. Baixa as dependências (camada pesada que será cacheada)
# Isso só rodará de novo se o seu pom.xml mudar
RUN ./mvnw dependency:go-offline -Dhttps.protocols=TLSv1.2,TLSv1.3

# 3. Agora sim copia o código fonte e gera o JAR
COPY gateway/src ./src
RUN ./mvnw clean package -DskipTests -Dhttps.protocols=TLSv1.2,TLSv1.3


# Estágio 2: Runtime (Imagem final bem leve)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia apenas o JAR do estágio anterior
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
