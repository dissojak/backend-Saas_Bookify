# API d'Authentification - Guide d'Utilisation

## Endpoints disponibles

### 1. Inscription d'un nouvel utilisateur (Signup)

**Endpoint:** `POST /api/v1/auth/signup`

**Description:** Crée un nouveau compte utilisateur (selon le rôle) et envoie un email d'activation si nécessaire.

**Corps de la requête (JSON):**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "motdepasse123",
  "phoneNumber": "+33612345678",
  "avatarUrl": "https://example.com/avatar.jpg",
  "role": "CLIENT"
}
```

**Champs obligatoires:**
- `name`: Nom complet (min 2, max 100 caractères)
- `email`: Email valide
- `password`: Mot de passe (min 6 caractères)

**Champs optionnels:**
- `phoneNumber`: Numéro de téléphone (8 à 12 caractères)
- `avatarUrl`: URL de la photo de profil
- `role`: Rôle de l'utilisateur (`CLIENT`, `BUSINESS_OWNER`, `STAFF`, `ADMIN`). Si omis, la valeur par défaut est `CLIENT`.

**Réponse en cas de succès (201 Created):**
```json
{
  "token": null,
  "refreshToken": null,
  "userId": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "role": "CLIENT",
  "message": "Inscription réussie. Veuillez vérifier votre email pour activer votre compte."
}
```

**Notes:**
- Les tokens JWT sont `null` à l'inscription. La connexion n'est possible qu'après activation du compte.
- Les comptes `ADMIN` sont créés avec le statut `VERIFIED` (aucun email d'activation requis).

**Réponse en cas d'erreur (400 Bad Request):**
```json
{
  "message": "Un utilisateur avec cet email existe déjà"
}
```

---

### 2. Activation du compte

**Endpoint:** `GET /api/v1/auth/activate?token=<uuid>`

**Description:** Active un compte utilisateur avec le token reçu par email.

**Paramètres:**
- `token`: Token d'activation UUID (reçu par email, valide 7 jours)

**Exemple:**
```
GET /api/v1/auth/activate?token=a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Réponse en cas de succès (200 OK):**
```json
{
  "message": "Votre compte a été activé avec succès. Vous pouvez maintenant vous connecter."
}
```

**Réponses en cas d'erreur (400 Bad Request):**
```json
{
  "message": "Token d'activation invalide"
}
```
ou
```json
{
  "message": "Le token d'activation a expiré"
}
```

**Email d'activation:**
Après l'inscription, l'utilisateur reçoit un email contenant un lien d'activation :
```
http://localhost:8088/api/v1/auth/activate?token=<uuid>
```
Ce lien est valide pendant **7 jours**.

---

### 3. Connexion (Login)

**Endpoint:** `POST /api/v1/auth/login`

**Description:** Authentifie un utilisateur dont le compte est activé.

**Corps de la requête (JSON):**
```json
{
  "email": "john.doe@example.com",
  "password": "motdepasse123"
}
```

**Champs obligatoires:**
- `email`: Email valide
- `password`: Mot de passe

**Réponse en cas de succès (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "role": "CLIENT",
  "message": "Connexion réussie"
}
```

**Réponses en cas d'erreur:**

**401 Unauthorized (identifiants incorrects):**
```json
{
  "message": "Email ou mot de passe incorrect"
}
```

**400 Bad Request (compte non activé):**
```json
{
  "message": "Veuillez activer votre compte via l'email d'activation envoyé"
}
```

**400 Bad Request (compte suspendu):**
```json
{
  "message": "Votre compte a été suspendu. Veuillez contacter le support."
}
```

---

## Flow complet d'inscription et activation

```
1. Utilisateur s'inscrit
   POST /api/v1/auth/signup
   ↓
2. Système crée le compte avec status PENDING (ou VERIFIED si ADMIN)
   ↓
3. Système génère un token UUID unique (expire dans 7 jours) si le compte est PENDING
   ↓
4. Système envoie un email d'activation (si nécessaire)
   ↓
5. Utilisateur clique sur le lien dans l'email
   GET /api/v1/auth/activate?token=<uuid>
   ↓
6. Système vérifie et active le compte (status VERIFIED)
   ↓
7. Utilisateur peut maintenant se connecter
   POST /api/v1/auth/login
   ↓
8. Système retourne les tokens JWT
```

---

## Utilisation des tokens JWT

Après une connexion réussie, vous recevez deux tokens:

1. **token**: Token d'accès principal (valide 24 heures)
2. **refreshToken**: Token de rafraîchissement (valide 7 jours)

### Comment utiliser le token

Pour accéder aux endpoints protégés, ajoutez le token dans l'en-tête HTTP:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Exemples avec cURL

### Inscription
```bash
curl -X POST http://localhost:8088/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "motdepasse123",
    "phoneNumber": "+33612345678",
    "role": "CLIENT"
  }'
```

### Activation (simuler un clic sur le lien email)
```bash
curl -X GET "http://localhost:8088/api/v1/auth/activate?token=a1b2c3d4-e5f6-7890-abcd-ef1234567890"
```

### Connexion
```bash
curl -X POST http://localhost:8088/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "motdepasse123"
  }'
```

---

## Statuts des comptes utilisateurs

| Statut | Description |
|--------|-------------|
| `PENDING` | Compte créé mais non activé (en attente de validation email) |
| `VERIFIED` | Compte activé, l'utilisateur peut se connecter |
| `SUSPENDED` | Compte suspendu par un administrateur |

---

## Codes de statut HTTP

- **200 OK**: Activation réussie / Connexion réussie
- **201 Created**: Inscription réussie (email d'activation envoyé si nécessaire)
- **400 Bad Request**: Données invalides, email déjà utilisé, compte non activé, ou token expiré
- **401 Unauthorized**: Identifiants incorrects
- **500 Internal Server Error**: Erreur serveur

---

## Configuration Email

Pour activer l'envoi d'emails, configurez ces propriétés dans `application.properties`:

```properties
# URL de base pour les liens d'activation
application.base-url=http://localhost:8088/api

# Configuration SMTP (exemple Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-app
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Note:** Pour Gmail, utilisez un "Mot de passe d'application" plutôt que votre mot de passe habituel.

---

## Sécurité

### Tokens d'activation
- Générés avec UUID aléatoire
- Uniques et non prédictibles
- Expiration automatique après 7 jours
- Supprimés après utilisation ou expiration

### Mots de passe
- Encodés avec BCrypt avant sauvegarde
- Jamais stockés en clair dans la base de données
- Minimum 6 caractères requis

### Validation des données
- Validation côté serveur avec Jakarta Validation
- Protection contre les injections SQL (JPA)
- Vérification de l'unicité des emails

---

## Documentation Swagger

Une fois l'application démarrée, accédez à la documentation interactive Swagger UI:

**URL:** http://localhost:8088/api/swagger-ui.html

Vous pourrez y tester les endpoints directement depuis votre navigateur.

---

## FAQ

**Q: Que se passe-t-il si je ne reçois pas l'email d'activation ?**
R: Vérifiez votre dossier spam. Si le problème persiste, contactez le support.

**Q: Le lien d'activation a expiré, que faire ?**
R: Actuellement, il faut créer un nouveau compte. Une fonctionnalité de renvoi d'email sera ajoutée prochainement.

**Q: Puis-je me connecter sans activer mon compte ?**
R: Non, l'activation via email est obligatoire pour des raisons de sécurité (sauf cas `ADMIN`).

**Q: Combien de temps les tokens JWT sont-ils valides ?**
R: Le token d'accès est valide 24 heures, le refresh token 7 jours.
