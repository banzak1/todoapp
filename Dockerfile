# Estágio 1: Build da aplicação utilizando JDK completo
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copia os arquivos do Maven Wrapper e o pom.xml para cache de dependências
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Baixa as dependências offline para otimizar builds futuros
RUN ./mvnw dependency:go-offline -B

# Copia as fontes e gera o build do JAR (pulando testes para acelerar o deploy local)
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Estágio 2: Ambiente de Runtime leve utilizando apenas JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Criação de um usuário não-root por motivos de segurança (Boas práticas de DevOps)
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Copia o JAR empacotado do primeiro estágio
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]