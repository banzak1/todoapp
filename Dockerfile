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

# Baixa o certificado CA do Aiven Kafka e adiciona ao truststore do JVM
# Necessário porque o Aiven Free Tier usa CA próprio não incluso no JDK padrão
RUN apk add --no-cache openssl && \
    openssl s_client -connect kafka-228e503d-todoapp-kafka.j.aivencloud.com:16350 \
      -showcerts </dev/null 2>/dev/null | \
      sed -n '/-----BEGIN CERTIFICATE-----/,/-----END CERTIFICATE-----/p' | \
      awk 'BEGIN { f="/tmp/cert-all.pem" } /-----BEGIN CERTIFICATE-----/{f="/tmp/cert-" NR ".pem"} {print > f}' && \
    for f in /tmp/cert-*.pem; do \
      keytool -importcert -noprompt -trustcacerts \
        -alias "aiven-$(basename $f .pem)" -file "$f" \
        -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit; \
    done && \
    rm -f /tmp/cert-*.pem /tmp/cert-all.pem

USER spring:spring

# Copia o JAR empacotado do primeiro estágio
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]