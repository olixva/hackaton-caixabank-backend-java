# Utiliza una imagen Alpine de Maven como base para compilar el proyecto
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establece el directorio de trabajo en /app
WORKDIR /app

# Copia el archivo pom.xml para instalar las dependencias
COPY pom.xml .

# Instala las dependencias del proyecto
RUN mvn dependency:go-offline

# Copia el resto de los archivos del proyecto
COPY . .

# Compila el proyecto
RUN mvn package

# Imagen para ejecutar el archivo JAR compilado en la etapa anterior
# Se utiliza una imagen alpine para reducir el tamaño de la imagen final
# Se establece el directorio de trabajo en /app
# Version de Java 21
FROM eclipse-temurin:21-jre-alpine

# Establece el directorio de trabajo en /app
WORKDIR /app


# Copia el archivo JAR compilado desde la etapa de compilación anterior
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto en el que se ejecuta la aplicación
EXPOSE 3000

# Comando para ejecutar la aplicación cuando se inicie el contenedor
CMD ["java", "-jar", "app.jar"]
