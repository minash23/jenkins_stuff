# Use the correct JDK base image for building Java applications
FROM eclipse-temurin:21-jdk-jammy

# Copy the JAR file built by Maven into the container
COPY target/XPS.jar /app/XPS.jar

# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "XPS.jar"]

# Expose the port your application will run on
EXPOSE 8000

