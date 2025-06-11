Smart Laundry - Laundry Service Management System

SmartLaundry is a full-stack laundry service platform designed to connect customers, service providers, and delivery agents. It supports profile management, Password/OTP-based authentication, order scheduling, delivery tracking, and admin control through a robust dashboard system.

## Tech Stack

## Backend

- Spring Boot – Java-based backend framework
- PostgreSQL – Relational database
- Redis – Caching and temporary storage 
- JWT (JSON Web Token) – Stateless authentication
- Custom ID Generation – For unique identifiers
- OpenCage API - To fetch latitude and longitude

## Frontend

- React.js – Web frontend
- React Native – Mobile frontend

## Features

- Registration with phone/email and Password/OTP
- Stateless JWT authentication
- Role-based login with redirection (Admin, Customer, Service Provider, Delivery Agent)
- Profile completion and approval workflow using Redis
- Order booking & status tracking
- Delivery agent auto-assignment and fallback mechanism
- Admin dashboard with filtering, sorting, and search
- Weekly availability scheduling for delivery agents
- Caching with Redis to reduce DB hits
-  View & Filter Nearby Service Providers
-  Multi-Step Booking Flow (using Redis)
-  Optional Schedule Plans

## Installation & Setup

### 1. Clone the Repository

git clone https://github.com/hitiksha-jagani/smart-laundry-service
cd smart-laundry-service

### 2. Backend Setup

cd Backend
mvn clean install
mvn spring-boot:run

### 3. Redis Setup
on Linux
sudo apt update
sudo apt install redis-server
sudo service redis-server start
redis-cli ping


### 3. Environement Variable

Create Env_Var.env files in your machine anywhere and give path of that file in 
Backend/SmartLaundry1-0/src/main/java/com/SmartLaundry/Application.java(for linux) file.
ex. : EnvUtils.loadEnv("/your-path/Env_Var.env");

Add below data in Env_Var.env file
DB_URL=jdbc:postgresql://localhost:5432/your_db_name
DB_USERNAME=your_username
DB_PASSWORD=your_password
SPRING_MAIL_FROM=your_mail
SPRING_MAIL_PASSWORD=your_password
OPEN_CAGE_API_KEY=your_api_key
REDIS_HOST=Redis server hostname
REDIS_PORT=Redis port (default 6379)
twilio.account.sid=YOUR_TWILIO_SID
twilio.auth.token=YOUR_TWILIO_AUTH_TOKEN
twilio.phone.number=YOUR_TWILIO_PHONE_NUMBER

