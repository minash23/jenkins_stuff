FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the JAR file into the image
COPY target/xperience-server.jar /app/

# Expose the port your application runs on
EXPOSE 8000

# Command to run the application
ENTRYPOINT ["java", "-jar", "xperience-server.jar"]
