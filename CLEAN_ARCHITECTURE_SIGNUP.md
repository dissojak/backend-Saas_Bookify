# Clean Architecture - Signup Feature

## 📐 Implemented Strict Structure

The architecture strictly follows the Clean Architecture pattern for the signup flow:

```
Controller → DTO → Service Interface → Service Implementation → Repository → Entity
```

---

## 🏗️ Architecture Components

### 1. Controller Layer (Presentation)
**File:** `AuthController.java`

**Responsibility:**
- Receive HTTP requests
- Validate input with `@Valid`
- Call the service via its interface
- Return HTTP responses with appropriate status codes

**Strict rules:**
- ❌ NEVER access the Repository directly
- ❌ NEVER contain business logic
- ✅ Only call the `AuthService` interface

```java
@PostMapping("/signup")
public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
    AuthResponse response = authService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

---

### 2. DTO Layer (Data Transfer Objects)
**Files:** `SignupRequest.java`, `AuthResponse.java`

**Responsibility:**
- Carry data between layers
- Validate incoming data using Jakarta Validation annotations

**SignupRequest (Input DTO):**
```java
@Data
@Builder
public class SignupRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;
    
    @NotBlank @Email
    private String email;
    
    @NotBlank @Size(min = 6)
    private String password;
    
    @Size(min = 8, max = 12)
    private String phoneNumber;
    
    private String avatarUrl;

    // Optional field: if null, default to CLIENT in the service
    private RoleEnum role;
}
```

**AuthResponse (Output DTO):**
```java
@Data
@Builder
public class AuthResponse {
    private String token;        // null at signup until account activation
    private String refreshToken; // null at signup
    private Long userId;
    private String name;
    private String email;
    private RoleEnum role;
    private String message;
}
```

---

### 3. Service Interface (Contract)
**File:** `AuthService.java`

**Responsibility:**
- Define the service contract
- Provide an abstraction for the implementation

```java
public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    String activateAccount(String token);
}
```

**Benefits:**
- ✅ Easier unit testing (mocking)
- ✅ Allows multiple implementations
- ✅ Decouples layers

---

### 4. Service Implementation (Business Logic)
**File:** `AuthServiceImpl.java`

**Responsibility:**
- Implement business logic
- Orchestrate repository calls
- Encode passwords
- Handle email activation (7-day token)
- Map Entity → DTO

**Signup flow (role-aware):**

```java
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse signup(SignupRequest request) {
        // 1) Unique email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("A user with this email already exists");
        }

        // 2) Determine role (default CLIENT)
        RoleEnum role = request.getRole() == null ? RoleEnum.CLIENT : request.getRole();

        // 3) Instantiate the correct subtype
        User user = switch (role) {
            case ADMIN -> new Admin();
            case BUSINESS_OWNER -> new BusinessOwner();
            case STAFF -> new Staff();
            case CLIENT -> new Client();
        };

        // 4) Common fields
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        // 5) Client/staff specific fields
        if (user instanceof Client) {
            user.setPhoneNumber(request.getPhoneNumber());
            user.setAvatarUrl(request.getAvatarUrl());
        }

        // 6) Initial status
        user.setStatus(role == RoleEnum.ADMIN ? UserStatusEnum.VERIFIED : UserStatusEnum.PENDING);

        // 7) Persist
        User saved = userRepository.save(user);

        // 8) If PENDING → create activation token (7 days) and send email
        if (saved.getStatus() != UserStatusEnum.VERIFIED) {
            ActivationToken token = ActivationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(saved)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
            activationTokenRepository.save(token);
            mailService.sendActivationEmail(saved.getEmail(), saved.getName(), token.getToken());
        }

        // 9) Response: no JWT at signup
        return AuthResponse.builder()
            .token(null)
            .refreshToken(null)
            .userId(saved.getId())
            .name(saved.getName())
            .email(saved.getEmail())
            .role(saved.getRole())
            .message(saved.getStatus() == UserStatusEnum.VERIFIED
                ? "Admin signup successful. Account is already verified."
                : "Signup successful. Please check your email to activate your account.")
            .build();
    }
}
```

**Strict rules:**
- ✅ Use Repository only for data access
- ✅ Never return entities directly — always use DTOs
- ✅ Manage statuses and email activation within the service

---

### 5. Repository Layer (Data Access)
**File:** `UserRepository.java`

**Responsibility:**
- Database access
- JPA queries

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

**Strict rules:**
- ❌ NEVER called directly by the Controller
- ✅ Only used by the Service Implementation

---

### 6. Entity Layer (Domain)
**Files:** `User` (base), subtypes `Client`, `BusinessOwner`, `Staff`, `Admin`

**Responsibility:**
- Represent database records
- JPA inheritance (JOINED strategy)

```java
@Entity
@Table(name = "clients")
public class Client extends User {
    // ... client-specific relations
}
```

---

## 🔄 Complete Signup Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. HTTP CLIENT REQUEST                                           │
│    POST /api/v1/auth/signup                                      │
│    Body: { name, email, password, phoneNumber?, avatarUrl?,      │
│            role?=CLIENT }                                        │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 2. CONTROLLER (AuthController)                                  │
│    - Receives SignupRequest DTO                                  │
│    - Validates with @Valid                                        │
│    - Calls authService.signup(request)                           │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 3. SERVICE IMPLEMENTATION (AuthServiceImpl)                      │
│    ✓ Checks unique email                                         │
│    ✓ Determines role and subtype                                 │
│    ✓ Encodes password                                             │
│    ✓ Initial status: ADMIN=VERIFIED, others=PENDING              │
│    ✓ If PENDING → generates token (7 days) + sends email         │
│    ✓ Builds AuthResponse (no JWT at signup)                      │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 4. EMAIL ACTIVATION                                              │
│    GET /api/v1/auth/activate?token=<uuid>                        │
│    → Status set to VERIFIED                                      │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 5. LOGIN                                                         │
│    POST /api/v1/auth/login → JWT + refreshToken                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## ✅ Strict Rules Enforced

- Controller → only calls `AuthService`
- DTOs for input/output
- Business logic and data access encapsulated in the service

---

## 🎯 Architecture Benefits

- Separation of responsibilities (SRP)
- Testability (mockable service via interface)
- Maintainability and scalability
- Security (email activation, encoded passwords)

---

## 📝 Unit Test Example (excerpt)

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Test
    void signup_ShouldCreatePendingClient_WhenEmailNotExists() {
        SignupRequest request = SignupRequest.builder()
            .name("John Doe")
            .email("john@example.com")
            .password("password123")
            .build();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        AuthResponse response = authService.signup(request);

        assertNotNull(response);
        assertNull(response.getToken()); // no JWT at signup
        verify(userRepository).save(any(User.class));
        verify(mailService).sendActivationEmail(anyString(), anyString(), anyString());
    }
}
```

---

## 🚀 Key Points to Remember

✅ Signup supports an optional `role` field (defaults to `CLIENT`)  
✅ No JWT at signup; email activation required (7-day token)  
✅ `ADMIN` users are created `VERIFIED` and do not receive an activation email  
✅ Login unchanged: returns JWTs when account is `VERIFIED`
