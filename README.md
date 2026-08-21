# EcoRides - Electric Car Rental System (Backend)

A backend system for managing electric car rentals, built with **Spring Boot** and **JWT authentication**.

## Features

- User authentication with JWT (login & signup)
- Role-based access control (Admin, User)
- CRUD operations for cars and bookings
- Booking and cancellation logic
- Search, sorting, and pagination
- Docker deployment (in progress)

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Spring Security (JWT)
- MySQL
- Docker

## Setup Instructions

1. Clone the repo

   git clone https://github.com/<your-username>/greenrides-car-rental-backend.git

2. Import into your IDE (IntelliJ / Eclipse / VS Code).

3. Update application.properties with your DB config.

4. Run:

mvn spring-boot:run

5. API available at: http://localhost:8080

Future Improvements--

Complete Docker deployment

Add frontend integration

Improve booking logic (pricing, duration, cancellation fees)

CI/CD pipeline
 




