# Spring Boot TLS/SSL Certificate Tracker

This project is a Spring Boot application designed to track TLS/SSL certificates, manage user authentication, and provide scheduled email notifications for certificate expirations. The application follows best practices in security and maintainability.

## Features

- **User Registration and Authentication**: Allows users to register and log in securely.
- **Password Management**: Includes functionality for password reset with a one-time password.
- **Token-Based Authentication**: Utilizes JWTs for access and refresh tokens.
- **Email Verification**: Sends verification tokens for user account confirmation.
- **Scheduled Notifications**: Automatically sends email notifications for certificate expiration.
- **RESTful API**: Provides endpoints for certificate management and user interactions.

## Technologies Used

- **Java**
- **Spring Boot**
- **Spring Security**
- **Hibernate**
- **JPA**
- **PostgreSQL**
- **Maven**
- **JWT (JSON Web Tokens)**
- **Spring Mail**
- **Spring Scheduling**

## API Endpoints

### User Authentication

- **Register User**
    - `POST /api/auth/register`
- **Login User**
    - `POST /api/auth/signin`
- **Sign Out User**
    - `POST /api/auth/signout`
- **Refresh Token**
    - `POST /api/auth/refreshtoken`
- **Password Reset Request**
    - `POST /api/auth/password-reset-request`
- **Reset Password**
    - `POST /api/auth/password-reset`
- **Email Verification**
    - `GET /api/auth/verifyEmail`
- **Resend Verification Token**
    - `GET /api/auth/resend-verification-token`
- **Validate Password Reset Code**
    - `GET /api/auth/validate-password-code`

### Certificate Management

- **Get Certificate Info**
    - `POST /api/certificates/info`
- **Add Certificate**
    - `POST /api/certificates/add`
- **Delete Certificate by ID**
    - `DELETE /api/certificates/delete/{certificateId}`
- **Delete User Certificate by ID**
    - `DELETE /api/certificates/delete/user/{certificateId}`
- **Get All Certificates**
    - `GET /api/certificates/all`
- **Get All User Certificates**
    - `GET /api/certificates/user/all`
- **Get Certificate by ID**
    - `GET /api/certificates/get/{certificateId}`

### User Management

- **Change Password**
    - `PATCH /api/users/change-password`

## Application Structure

The application consists of the following main components:

### 1. User Management
Handles user authentication, registration, and password management.

#### Key Methods:
- `registerUser`: Registers a new user and sends a verification email.
- `resetPassword`: Initiates the password reset process using a one-time password.
- `loadUserByUsername`: Loads user details for authentication.

### 2. Token Management
Handles JWT and refresh token management.

#### Key Methods:
- `createAccessToken`: Generates a new access token for authenticated users.
- `createRefreshToken`: Generates a new refresh token for user sessions.
- `validateToken`: Validates the expiration of a refresh token.

### 3. Email Notifications
Manages scheduled email notifications for certificate expirations.

#### Key Methods:
- `sendEmail`: Scheduled task that runs daily to check for certificate expirations and send notifications.

### 4. Security Configuration
Handles security aspects of the application, including CORS configuration, JWT authentication, and method security.

#### Key Methods:
- `filterChain`: Configures security filter chains, including CORS and role-based access control.
- `authenticationJwtTokenFilter`: Custom filter to validate JWT tokens from requests.

## Configuration

Make sure to configure your `application.properties` with the necessary parameters:

```properties
# Database Configuration
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Spring Mail Configuration
spring.mail.host=${MAIL_HOST}
spring.mail.port=${MAIL_PORT}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JWT Configuration
application.security.jwt.secretKey=${JWT_SECRET_KEY}
application.security.jwt.cookieName=${JWT_COOKIE_NAME}
application.security.jwt.refresh.cookieName=${JWT_REFRESH_COOKIE_NAME}
application.security.jwt.expiration=${JWT_EXPIRATION}
application.security.jwt.refresh.expiration=${JWT_REFRESH_EXPIRATION}
application.jwt.verification.expiration=${JWT_VERIFICATION_EXPIRATION}
application.code.length=${CODE_LENGTH}
application.email.notification.days=${EMAIL_NOTIFICATION_DAYS}
application.cors.domain=${CORS_DOMAIN}

# Logging Configuration
logging.level.com.devops.certtracker.controller=DEBUG
