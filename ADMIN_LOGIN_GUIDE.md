# Admin Login Access Guide

## URLs for Your Application

Based on your `application.properties` configuration:
- **Server Port**: `8088`
- **Context Path**: `/api`

### Access URLs

1. **Admin Login Page**:
   ```
   http://localhost:8088/api/LoginAdmin.html
   ```

2. **Dashboard Page** (after login):
   ```
   http://localhost:8088/api/Dashboard.html
   ```

## How It Works

### 1. Static Resources
- Your HTML files are located in `src/main/resources/static/`
- Spring Boot automatically serves them at the root context
- With `context-path=/api`, all URLs are prefixed with `/api`

### 2. Authentication Flow

**Login Process**:
1. User accesses `http://localhost:8088/api/LoginAdmin.html`
2. Enters email and password
3. JavaScript sends POST to `/api/v1/auth/login`
4. On success, JWT token is stored in `localStorage`
5. User is redirected to `http://localhost:8088/api/Dashboard.html`

**Logout Process**:
1. User clicks "Logout" button on Dashboard
2. JavaScript sends POST to `/api/v1/auth/logout` with JWT token
3. Token is removed from `localStorage`
4. User is redirected back to `http://localhost:8088/api/LoginAdmin.html`

### 3. Security Configuration

The following endpoints are **publicly accessible** (no authentication required):
- `/LoginAdmin.html` - Admin login page
- `/Dashboard.html` - Dashboard page (now public)
- `/v1/auth/login` - Login API endpoint
- `/v1/auth/signup` - Signup API endpoint
- Static resources (CSS, JS, images, etc.)

### 4. Base Path Detection

Both `LoginAdmin.html` and `Dashboard.html` automatically detect the context path:
- They parse the URL to extract the base path (`/api`)
- All API calls are prefixed with the detected base path
- This makes the application work both with and without a context path

## Files Modified

1. **SecurityConfig.java**
   - Added `Dashboard.html` to permitted public endpoints
   
2. **Dashboard.html**
   - Added base path detection logic (same as LoginAdmin.html)
   - Fixed logout API URL to use detected base path
   - Fixed redirect to use `LoginAdmin.html` instead of `/login`

## Testing

1. Start your Spring Boot application
2. Open browser and navigate to: `http://localhost:8088/api/LoginAdmin.html`
3. Login with valid credentials
4. You should be redirected to the Dashboard
5. Click "Logout" to return to the login page

## Troubleshooting

If you get a 403 error:
- Make sure the application has been recompiled after the SecurityConfig changes
- Check that the server is running on port 8088
- Verify the context path is `/api` in your `application.properties`
- Clear browser cache and cookies

## API Endpoints

- **Login**: `POST /api/v1/auth/login`
- **Logout**: `POST /api/v1/auth/logout`
- **Signup**: `POST /api/v1/auth/signup`
- **Refresh Token**: `POST /api/v1/auth/refresh`

All protected endpoints require the `Authorization: Bearer <token>` header.

