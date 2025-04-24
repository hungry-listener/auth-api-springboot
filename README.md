# 🔐 auth-api-springboot

A secure, lightweight authentication API built with Spring Boot — designed to be used as a plug-and-play backend or a starting point for building secure applications.

This project streamlines the complex security setup process and saves developers countless hours of configuration and research by providing a robust,  authentication system out of the box.

---

## ✨ Features

- ✅ **User registration with validation**  
- 🔐 **Login and email verification**  
- 🛡️ **JWT-based authentication (RSA256)**  
- 🧾 **Audit logging for user activity**  
- 🧑‍💼 **Role-based access control (RBAC)**  
- 👤 **Admin-level user management**  
- 🔐 **Brute force attack prevention using Redis**  
- 🔁 **Password reset via token**  
- ✅ **Token validation and refresh support**

---

## 🧰 Tech Stack

- **Language & Framework:** Java, Spring Boot  
- **Security:** Spring Security, JWT (RSA256)  
- **Data Layer:** MySQL, JPA, Hibernate  
- **Build Tool:** Maven  
- **Caching & Rate Limiting:** Redis

---

## 🎯 Target Audience

This project is ideal for:

- 🚀 Startups needing a secure backend  
- 👨‍💻 Developers building secure applications  
- 🎓 Students learning Spring Boot authentication  
- 🌍 Open source contributors

---

## 📜 License

Released under the **MIT License**, making it free to use, modify, and contribute to.

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 17+
- Maven
- MySQL
- Redis (running locally or in the cloud)

---

### 📥 Clone the Repository

```bash
git clone https://github.com/hungry-listener/auth-api-springboot.git
cd auth-api-springboot
```

---

### ⚙️ Setup Database

Update your MySQL credentials in `application.properties`:

```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Run the app and let JPA generate the required tables automatically.

📌 Note: Make sure you have created a database with the name authentication_micro or any other name of your choice and have granted access to the user name.

---

### 🌐 Add GeoLite2 Database

Due to licensing restrictions, the GeoLite2 database is **not included**.

To enable geolocation features:

1. Register at [MaxMind](https://www.maxmind.com)
2. Download `GeoLite2-City.mmdb`
3. Place it in:

```plaintext
src/main/resources/geolite/GeoLite2-City.mmdb
```

---

## 🔐 Set up JWT

This application uses RSA-based JWT authentication. Sample RSA key pairs are included for development, but it is **highly recommended** to generate and use your own secure keys in production.

### 🔧 Generate your own RSA key pair using OpenSSL:
```bash
# Generate private key 
openssl genrsa -out private.key 2048

# Extract public key
openssl rsa -in private.key -pubout -out public.key
```

### 🛠️ Update the following configuration properties in `application.properties`:

```properties
app.jwt.issuer=auth-service
app.jwt.access-token-expiration-minutes=60
app.jwt.public-key-path=classpath:keys/sample-public.key
app.jwt.private-key-path=classpath:keys/sample-private.key
```

---

## 🧠 Set up Redis

This application uses **Redis** to protect against brute-force attacks by locking out users based on their email and IP address after multiple failed login attempts.

### 📦 Install Redis

Make sure Redis is installed and running on your system. You can download it from [https://redis.io](https://redis.io) or install via package manager:

```bash
# On Ubuntu
sudo apt install redis-server

# On macOS with Homebrew
brew install redis
```

### 🔒 Enable Redis Lockout & Update Configuration
Update the following properties in your application.properties or application.yml file:

```properties
# Login security
app.security.login.max-failed-attempts=5
app.security.login.lock-duration-minutes=240
app.security.login.enable-redis-lockout=true
app.security.login.account-approval-enabled=true

# Redis configuration
spring.data.redis.host=localhost       # Default host
spring.data.redis.port=6379            # Default port
spring.data.redis.password=your_password  # Optional: only if Redis auth is enabled
```

📌 Note: Make sure your Redis server is running and accepts the configured credentials. If you use a password for Redis, ensure it's securely stored and never hard-coded in production environments.

---

## 📧 Set up Email Server

The application uses an email server to send verification emails, password reset links, and other notifications. For development and testing, you can use **[MailHog](https://github.com/mailhog/MailHog)** — a lightweight local email testing tool.

### 🛠️ Install MailHog

Install MailHog using the following command:

```bash
# On macOS using Homebrew
brew install mailhog

# Or run using Docker
docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog
```

Access the MailHog web UI at http://localhost:8025

### 📬 Email Configuration

Update the following properties in your application.properties:

```properties
# Email server properties
spring.mail.host=localhost
spring.mail.port=1025
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
```

📌 Note: These settings are suitable for local development using MailHog. For production, use a real SMTP provider (e.g., SendGrid, Amazon SES, Mailgun) and enable authentication and TLS.

---

## 🛠️ API Documentation

- Use **Postman** or **Swagger** (if enabled) to test the endpoints.
- You can import the Postman collection from the `/postman` directory or You can view the live Postman documentation [here](https://documenter.getpostman.com/view/44246711/2sB2ixjE3F).

---

## 🙌 Contributing

Pull requests are welcome!  
For major changes, please open an issue first to discuss what you would like to change.  
Don't forget to ⭐ the repo if you find it useful!

## 🚧 Work in Progress

This project includes a few placeholder or partially implemented classes (e.g., related to OAuth authentication) that are not yet wired into the application flow.

These are part of upcoming features and are being actively developed. Stay tuned for future updates!

Feel free to explore the code and contribute if you're interested!

