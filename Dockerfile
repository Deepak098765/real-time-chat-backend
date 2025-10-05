# Use a stable JDK base image
FROM openjdk:21-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the Spring Boot jar into the container
COPY target/chat-app-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (default Spring Boot port)
EXPOSE 8080

# Set environment variable for MongoDB URI
# This will be overridden by Docker run or cloud platform environment variables
ENV MONGO_URI=${MONGO_URI}
ENV FRONTEND_BASE_URL=${FRONTEND_BASE_URL}
# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
