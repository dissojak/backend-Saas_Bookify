# Authentication API - Usage Guide

## Available Endpoints

### 1. Register a new user (Signup)

**Endpoint:** `POST /api/v1/auth/signup`

**Description:** Creates a new user account (according to role) and sends an activation email when required.

**Request body (JSON):**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "phoneNumber": "+33612345678",
  "avatarUrl": "https://example.com/avatar.jpg",
  "role": "CLIENT"
}
```

**Required fields:**
- `name`: Full name (min 2, max 100 characters)
- `email`: Valid email
- `password`: Password (min 6 characters)

**Optional fields:**
- `phoneNumber`: Phone number (8 to 12 characters)
- `avatarUrl`: Profile picture URL
- `role`: User role (`CLIENT`, `BUSINESS_OWNER`, `STAFF`, `ADMIN`). If omitted, the default is `CLIENT`.

**Success response (201 Created):**
```json
{
  "token": null,
  "refreshToken": null,
  "userId": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "role": "CLIENT",
  "message": "Signup successful. Please check your email to activate your account."
}
```

**Notes:**
- JWT tokens are `null` at signup. Login is only possible after the account has been activated.
- `ADMIN` accounts are created with status `VERIFIED` (no activation email required).

**Error response (400 Bad Request):**
```json
{
  "message": "A user with this email already exists"
}
```

---

### 2. Account activation

**Endpoint:** `GET /api/v1/auth/activate?token=<uuid>`

**Description:** Activates a user account using the token received by email.

**Parameters:**
- `token`: Activation token (UUID) received by email (valid for 7 days)

**Example:**
```
GET /api/v1/auth/activate?token=a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Success response (200 OK):**
```json
{
  "message": "Your account has been successfully activated. You can now log in."
}
```

**Error responses (400 Bad Request):**
```json
{
  "message": "Invalid activation token"
}
```
or
```json
{
  "message": "The activation token has expired"
}
```

**Activation email:**
After signup, the user receives an email containing an activation link:
```
http://localhost:8088/api/v1/auth/activate?token=<uuid>
```
This link is valid for **7 days**.

---

### 3. Login

**Endpoint:** `POST /api/v1/auth/login`

**Description:** Authenticates a user whose account is activated.

**Request body (JSON):**
```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Required fields:**
- `email`: Valid email
- `password`: Password

**Success response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "role": "CLIENT",
  "message": "Login successful"
}
```

**Error responses:**

**401 Unauthorized (invalid credentials):**
```json
{
  "message": "Email or password incorrect"
}
```

**400 Bad Request (account not activated):**
```json
{
  "message": "Please activate your account using the activation email sent to you"
}
```

**400 Bad Request (account suspended):**
```json
{
  "message": "Your account has been suspended. Please contact support."
}
```

---

## Full signup & activation flow

```
1. User signs up
   POST /api/v1/auth/signup
   ↓
2. System creates account with status PENDING (or VERIFIED for ADMIN)
   ↓
3. System generates a unique UUID activation token (expires in 7 days) if account is PENDING
   ↓
4. System sends activation email (when required)
   ↓
5. User clicks the link in the email
   GET /api/v1/auth/activate?token=<uuid>
   ↓
6. System verifies and activates the account (status set to VERIFIED)
   ↓
7. User can now log in
   POST /api/v1/auth/login
   ↓
8. System returns JWT tokens
```

---

## Using JWT tokens

After a successful login you receive two tokens:

1. **token**: Access token (valid 24 hours)
2. **refreshToken**: Refresh token (valid 7 days)

### How to use the token

To access protected endpoints, add the token in the HTTP header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## cURL examples

### Signup
```bash
curl -X POST http://localhost:8088/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "phoneNumber": "+33612345678",
    "role": "CLIENT"
  }'
```

### Activation (simulate clicking the activation link)
```bash
curl -X GET "http://localhost:8088/api/v1/auth/activate?token=a1b2c3d4-e5f6-7890-abcd-ef1234567890"
```

### Login
```bash
curl -X POST http://localhost:8088/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

---

## User account statuses

| Status | Description |
|--------|-------------|
| `PENDING` | Account created but not activated (awaiting email verification) |
| `VERIFIED` | Account activated, user can log in |
| `SUSPENDED` | Account suspended by an administrator |

---

## HTTP status codes

- **200 OK**: Activation successful / Login successful
- **201 Created**: Signup successful (activation email sent when required)
- **400 Bad Request**: Invalid data, email already used, account not activated, or token expired
- **401 Unauthorized**: Invalid credentials
- **500 Internal Server Error**: Server error

---

## Email configuration

To enable sending emails, configure these properties in `application.properties`:

```properties
# Base URL for activation links
application.base-url=http://localhost:8088/api

# SMTP configuration (Gmail example)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Note:** For Gmail, use an "App Password" instead of your regular account password.

---

## Security

### Activation tokens
- Generated as random UUIDs
- Unique and non-predictable
- Automatically expire after 7 days
- Removed after use or expiration

### Passwords
- Encoded with BCrypt before saving
- Never stored in plain text
- Minimum 6 characters required

### Data validation
- Server-side validation using Jakarta Validation
- Protection against SQL injection (JPA)
- Email uniqueness checks

---

## Swagger documentation

Once the application is running, open the interactive Swagger UI:

**URL:** http://localhost:8088/api/swagger-ui.html

You can test endpoints directly from the browser there.

---

## FAQ

**Q: What if I don't receive the activation email?**
A: Check your spam folder. If the issue persists, contact support.

**Q: The activation link expired—what should I do?**
A: Currently you need to create a new account. A resend activation feature will be added soon.

**Q: Can I log in without activating my account?**
A: No — email activation is required for security (except for `ADMIN` accounts).

**Q: How long are JWT tokens valid?**
A: Access tokens are valid for 24 hours; refresh tokens are valid for 7 days.
