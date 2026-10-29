# OrderTracker 📦

OrderTracker is a robust, enterprise-grade e-commerce order management backend. It allows users to create and track orders while utilizing an event-driven architecture (via webhooks) to automatically update order statuses based on external events (e.g., Stripe payments or shipping updates).

## Features

- **Stateless Authentication:** JWT-based security with Role-Based Access Control (RBAC).
- **Event-Driven Webhooks:** Public endpoints to ingest real-time external events.
- **Asynchronous Processing:** Background threads handle JSON parsing, database updates, and email sending to ensure sub-10ms webhook response times.
- **Resiliency:** Automated retry mechanisms (`@Retryable`) for transient SMTP/email failures.
- **Real-Time Notifications:** WebSocket integration (STOMP) for pushing live status updates to frontend clients.
- **Audit Logging:** Comprehensive database logging of all incoming webhook payloads.
- **Admin Tools:**  CSV exports for order history.

## Tech Stack

- **Java 21 / Spring Boot 3.x**
- **Spring Security 6** (JWT Authentication)
- **Spring Data JPA** (Hibernate)
- **PostgreSQL** (Containerized via Docker)
- **Spring Mail & Spring Retry**
- **WebSockets** (STOMP over SockJS)
- **OpenAPI 3 / Swagger** (Documentation)

## Getting Started

### 1. Prerequisites
- Java 17+
- Gradle
- Docker & Docker Compose
- A free [Mailtrap](https://mailtrap.io/) (or Gmail App Password) for local email testing.

### 2. Infrastructure Setup
Spin up the PostgreSQL database using Docker Compose:
```bash
docker-compose up -d
```

### 3. Application Configuration (Security Best Practices)
To prevent leaking credentials to GitHub, we use environment variables. 
Create or update your `application.properties` to map to environment variables:

```properties
app.jwt.secret=${JWT_SECRET}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
stripe.webhook.secret=${STRIPE_WEBHOOK_SECRET}
```

When running the application, provide these variables via your IDE (Run Configurations -> Environment Variables) or via your terminal:
```bash
export JWT_SECRET="your_generated_jwt_secret"
export MAIL_USERNAME="your_email@gmail.com"
export MAIL_PASSWORD="your_app_password"
export STRIPE_WEBHOOK_SECRET="whsec_..."
```

### 4. Run the Application
Start the Spring Boot application using Gradle:
```bash
./gradlew bootRun
```

The server will start on `http://localhost:8080`.

## API Documentation & Testing

The interactive Swagger UI is available at:  
👉 **`http://localhost:8080/swagger-ui.html`**

### Testing Workflow

1. **Register & Log In:**
   - Use `POST /api/auth/register` to create an account.
   - Use `POST /api/auth/login` to retrieve your JWT token.
   - Click the **Authorize** padlock in Swagger and paste your token to unlock secured endpoints.

2. **Create an Order:**
   - Use `POST /api/orders` to create an order.
   - Note the generated `externalTransactionId` in the response.

3. **Simulate a Webhook Event:**
   - Use the public `POST /api/webhooks/payment` endpoint.
   - Inject the `externalTransactionId` into the JSON payload:
     ```json
     {
       "id": "evt_9999",
       "type": "payment_intent.succeeded",
       "data": { "object": { "metadata": { "externalTransactionId": "YOUR_TXN_ID" } } }
     }
     ```
   - Check your email inbox to verify the asynchronous notification was sent!

## Real-Time WebSocket Client

To test the live WebSocket updates:
1. Open `http://localhost:8080/index.html` in your browser.
2. Enter an existing Order ID and connect.
3. Fire a webhook update via Swagger or curl.
4. Watch the UI update instantly without a page refresh.

## Webhook Integration (Local Testing via Stripe CLI)

If you wish to test with live Stripe events rather than manual POST requests, you can use the Stripe CLI to forward events directly to your local server.

1. Install the [Stripe CLI](https://github.com/stripe/stripe-cli#installation).
2. Authenticate your account: `stripe login`
3. Forward events to your application:
   ```bash
   stripe listen --forward-to localhost:8080/api/webhooks/payment
   ```
