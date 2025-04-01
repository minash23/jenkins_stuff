# Use Eclipse Temurin as the base image
FROM eclipse-temurin:17-jre-slim

# Copy the JAR file into the Docker image
COPY target/XPS.jar /XPS.jar

# Expose the port your application runs on
EXPOSE 8000

# Command to run the application
ENTRYPOINT ["java", "-jar", "/XPS.jar"]

