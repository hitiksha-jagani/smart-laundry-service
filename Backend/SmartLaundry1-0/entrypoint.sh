#!/bin/bash

# Start Redis in the background
service redis-server start

# Wait for Redis to be ready (optional but recommended)
sleep 3

# Start the Spring Boot application
exec java -jar /app.jar
