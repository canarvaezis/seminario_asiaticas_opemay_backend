# Etapa 1: Construcción
FROM gradle:8.8-jdk21 AS builder
WORKDIR /app

# Copiamos primero los archivos necesarios para cachear dependencias
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle

# Descarga dependencias (cache útil para builds futuros)
RUN ./gradlew dependencies --no-daemon || true

# Copiamos el código fuente
COPY src ./src

# Compilamos y generamos el .jar (Spring Boot)
RUN ./gradlew clean bootJar --no-daemon

# Etapa 2: Imagen final (ligera)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiamos solo el .jar desde el builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Variables de entorno configurables
ENV SPRING_PROFILES_ACTIVE=dev
ENV JAVA_OPTS=""

# Exponemos el puerto
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
