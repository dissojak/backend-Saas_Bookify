# Architecture Clean - Signup Feature

## 📐 Structure Stricte Implémentée

L'architecture suit strictement le pattern Clean Architecture pour le flux d'inscription :

```
Controller → DTO → Service Interface → Service Implementation → Repository → Entity
```

---

## 🏗️ Composants de l'Architecture

### 1. **Controller Layer** (Présentation)
**Fichier:** `AuthController.java`

**Responsabilité:** 
- Recevoir les requêtes HTTP
- Valider les données avec `@Valid`
- Appeler le service via l'interface
- Retourner les réponses HTTP avec codes de statut appropriés

**Règles strictes:**
- ❌ **JAMAIS** accéder au Repository directement
- ❌ **JAMAIS** contenir de logique métier
- ✅ Seulement appeler l'interface `AuthService`

```java
@PostMapping("/signup")
public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
    AuthResponse response = authService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

---

### 2. **DTO Layer** (Data Transfer Objects)
**Fichiers:** `SignupRequest.java`, `AuthResponse.java`

**Responsabilité:**
- Transporter les données entre les couches
- Valider les données d'entrée avec annotations Jakarta Validation

**SignupRequest (Input DTO):**
```java
@Data
@Builder
public class SignupRequest {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String name;
    
    @NotBlank @Email
    private String email;
    
    @NotBlank @Size(min = 6)
    private String password;
    
    @Size(min = 8, max = 12)
    private String phoneNumber;
    
    private String avatarUrl;

    // Champ optionnel: si null, sera considéré comme CLIENT côté service
    private RoleEnum role;
}
```

**AuthResponse (Output DTO):**
```java
@Data
@Builder
public class AuthResponse {
    private String token;        // null à l'inscription tant que le compte n'est pas activé
    private String refreshToken; // null à l'inscription
    private Long userId;
    private String name;
    private String email;
    private RoleEnum role;
    private String message;
}
```

---

### 3. **Service Interface** (Contrat)
**Fichier:** `AuthService.java`

**Responsabilité:**
- Définir le contrat du service
- Abstraction pour l'implémentation

```java
public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    String activateAccount(String token);
}
```

**Avantages:**
- ✅ Facilite les tests unitaires (mocking)
- ✅ Permet plusieurs implémentations
- ✅ Découplage des couches

---

### 4. **Service Implementation** (Logique Métier)
**Fichier:** `AuthServiceImpl.java`

**Responsabilité:**
- Implémenter toute la logique métier
- Orchestrer les appels au Repository
- Encoder les mots de passe
- Gérer l'activation par email (token 7 jours)
- Transformer Entity → DTO

**Flow d'inscription détaillé (rôle-aware):**

```java
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse signup(SignupRequest request) {
        // 1) Email unique
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }

        // 2) Déterminer le rôle (par défaut CLIENT)
        RoleEnum role = request.getRole() == null ? RoleEnum.CLIENT : request.getRole();

        // 3) Instancier le bon sous-type
        User user = switch (role) {
            case ADMIN -> new Admin();
            case BUSINESS_OWNER -> new BusinessOwner();
            case STAFF -> new Staff();
            case CLIENT -> new Client();
        };

        // 4) Champs communs
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        // 5) Champs client/staff
        if (user instanceof Client) {
            user.setPhoneNumber(request.getPhoneNumber());
            user.setAvatarUrl(request.getAvatarUrl());
        }

        // 6) Statut initial
        user.setStatus(role == RoleEnum.ADMIN ? UserStatusEnum.VERIFIED : UserStatusEnum.PENDING);

        // 7) Sauvegarde
        User saved = userRepository.save(user);

        // 8) Si PENDING → créer token d'activation (7 jours) et envoyer email
        if (saved.getStatus() != UserStatusEnum.VERIFIED) {
            ActivationToken token = ActivationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(saved)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
            activationTokenRepository.save(token);
            mailService.sendActivationEmail(saved.getEmail(), saved.getName(), token.getToken());
        }

        // 9) Réponse: pas de JWT au signup
        return AuthResponse.builder()
            .token(null)
            .refreshToken(null)
            .userId(saved.getId())
            .name(saved.getName())
            .email(saved.getEmail())
            .role(saved.getRole())
            .message(saved.getStatus() == UserStatusEnum.VERIFIED
                ? "Inscription administrateur réussie. Le compte est déjà vérifié."
                : "Inscription réussie. Veuillez vérifier votre email pour activer votre compte.")
            .build();
    }
}
```

**Règles strictes:**
- ✅ Utilise uniquement le Repository pour accéder aux données
- ✅ Ne retourne jamais d'entités, seulement des DTOs
- ✅ Gère les statuts et l'activation par email

---

### 5. **Repository Layer** (Accès aux Données)
**Fichier:** `UserRepository.java`

**Responsabilité:**
- Accès à la base de données
- Requêtes JPA

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

**Règles strictes:**
- ❌ **JAMAIS** appelé directement par le Controller
- ✅ Seulement utilisé par le Service Implementation

---

### 6. **Entity Layer** (Domaine)
**Fichiers:** `User` (base), sous-classes `Client`, `BusinessOwner`, `Staff`, `Admin`

**Responsabilité:**
- Représentation des données en base
- Héritage JPA (JOINED)

```java
@Entity
@Table(name = "clients")
public class Client extends User {
    // ... relations spécifiques client
}
```

---

## 🔄 Flow Complet du Signup

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. CLIENT HTTP REQUEST                                          │
│    POST /api/v1/auth/signup                                     │
│    Body: { name, email, password, phoneNumber?, avatarUrl?,     │
│            role?=CLIENT }                                       │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 2. CONTROLLER (AuthController)                                  │
│    - Reçoit SignupRequest DTO                                   │
│    - Valide avec @Valid                                         │
│    - Appelle authService.signup(request)                        │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 3. SERVICE IMPLEMENTATION (AuthServiceImpl)                     │
│    ✓ Vérifie email unique                                       │
│    ✓ Détermine le rôle et le sous-type                          │
│    ✓ Encode le mot de passe                                     │
│    ✓ Statut initial: ADMIN=VERIFIED, autres=PENDING             │
│    ✓ Si PENDING → génère token (7 jours) + envoie email         │
│    ✓ Construit AuthResponse (sans JWT au signup)                │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 4. ACTIVATION PAR EMAIL                                         │
│    GET /api/v1/auth/activate?token=<uuid>                       │
│    → Passage à VERIFIED                                         │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ 5. LOGIN                                                        │
│    POST /api/v1/auth/login → JWT + refreshToken                │
└─────────────────────────────────────────────────────────────────┘
```

---

## ✅ Règles Strictes Respectées

- Controller → appelle uniquement `AuthService`
- DTOs pour les entrées/sorties
- Logique métier et accès données encapsulés dans le service

---

## 🎯 Avantages de cette Architecture

- Séparation des responsabilités (SRP)
- Testabilité (mock du service via interface)
- Maintenabilité et évolutivité
- Sécurité (activation par email, mots de passe encodés)

---

## 📝 Exemple de Test Unitaire (extrait)

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
        assertNull(response.getToken()); // pas de JWT au signup
        verify(userRepository).save(any(User.class));
        verify(mailService).sendActivationEmail(anyString(), anyString(), anyString());
    }
}
```

---

## 🚀 Points Clés à Retenir

✅ Signup supporte un champ `role` optionnel (par défaut `CLIENT`)  
✅ Pas de JWT au signup; activation par email (token 7 jours)  
✅ `ADMIN` créé directement `VERIFIED`, pas d'email d'activation  
✅ Login inchangé: retourne les JWT si compte `VERIFIED`
