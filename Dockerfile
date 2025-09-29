# Etapa 1: Construcción
FROM gradle:8.8-jdk21 AS builder
WORKDIR /app

# Copiamos solo los archivos de Gradle primero (para cachear dependencias)
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle

RUN ./gradlew dependencies --no-daemon || return 0

# Copiamos el resto del proyecto
COPY . .

# Compilamos la app (esto generará un .jar en build/libs)
RUN ./gradlew clean bootJar --no-daemon

# Etapa 2: Imagen final (más ligera)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el JAR desde el builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Variables de entorno (puedes sobreescribirlas al correr el contenedor)
ENV SPRING_PROFILES_ACTIVE=dev
ENV JAVA_OPTS=""

# Exponemos el puerto de la app
EXPOSE 8080

# Comando para arrancar la app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
