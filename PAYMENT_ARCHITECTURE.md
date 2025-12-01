d the rity config an # Architecture de Paiement - Principes SOLID

## Vue d'ensemble

L'architecture de paiement a été refactorisée pour suivre les principes SOLID, permettant une extension facile avec de nouveaux fournisseurs de paiement (Stripe, PayPal, etc.) sans modifier le code existant.

## Architecture en Couches

### 1. **Abstraction du Gateway de Paiement** (Interface Segregation & Dependency Inversion)

```
PaymentGateway (interface)
    ├── generatePayment()
    ├── verifyPayment()
    └── getGatewayName()
```

**Implémentations:**
- `FlouciPaymentGateway` - Implémentation pour Flouci
- (Futur) `StripePaymentGateway`, `PayPalPaymentGateway`, etc.

### 2. **Services**

#### `PaymentService`
Gère la logique métier des paiements. Dépend de l'abstraction `PaymentGateway`, pas d'une implémentation spécifique.

#### `SubscriptionPricingService`
Gère les prix et durées des plans d'abonnement. **Le montant n'est JAMAIS envoyé par le frontend.**

### 3. **Flux de Paiement**

#### A. Initiation du Paiement

1. **Frontend** envoie uniquement :
   ```json
   {
     "businessId": 123,
     "plan": "PRO",
     "successLink": "http://localhost:3000/subscription/success",
     "failLink": "http://localhost:3000/subscription/fail"
   }
   ```

2. **Backend** :
   - Calcule le prix selon le plan (sécurité !)
   - Crée une `Subscription` en statut `PENDING`
   - Génère le lien de paiement via le gateway
   - Retourne le lien de checkout

3. **Frontend** redirige l'utilisateur vers le lien de paiement

#### B. Vérification du Paiement

1. Après paiement, Flouci redirige vers : 
   ```
   http://localhost:3000/subscription/success?payment_id=XXX&subscriptionId=YYY
   ```

2. **Frontend** appelle :
   ```
   GET /v1/subscriptions/payments/verify?payment_id=XXX&subscriptionId=YYY
   ```

3. **Backend** :
   - Vérifie le paiement avec Flouci
   - Crée un **log de paiement** dans la table `payments` (transaction ref, montant, statut)
   - Si succès : active la subscription (statut `ACTIVE`, définit `startDate` et `endDate`)
   - Si échec : marque la subscription comme `FAILED`
   - Lie le paiement à la subscription via `subscription_id`

## Endpoints

### Nouveaux (Recommandés)

```
POST /v1/subscriptions/payments/initiate
GET  /v1/subscriptions/payments/verify?payment_id=XXX&subscriptionId=YYY
POST /v1/subscriptions/payments/webhook
```

### Legacy (Dépréciés)

```
POST /v1/payments/flouci/generate
POST /v1/payments/flouci/verify
POST /v1/payments/flouci/webhook
```

## Intégration Flouci

Basé sur la documentation officielle : https://docs.flouci.com

### Générer un Paiement

**Request:**
```json
{
  "app_token": "xxx",
  "app_secret": "xxx",
  "amount": 10000,
  "accept_card": "true",
  "session_timeout_secs": 1200,
  "success_link": "http://localhost:3000/subscription/success",
  "fail_link": "http://localhost:3000/subscription/fail",
  "developer_tracking_id": "SUB-123"
}
```

**Response:**
```json
{
  "result": {
    "success": true,
    "payment_id": "abc123",
    "link": "https://pay.flouci.com/checkout/xxx"
  }
}
```

### Vérifier un Paiement

**Request:**
```
GET https://developers.flouci.com/api/verify_payment/{payment_id}
Headers:
  apppublic: xxx
  appsecret: xxx
```

**Response:**
```json
{
  "result": {
    "status": "SUCCESS",
    "amount": 10000
  }
}
```

## Table des Paiements (Logs)

La table `payments` sert **uniquement de log** pour les paiements de subscriptions :

```sql
CREATE TABLE payments (
    id BIGINT PRIMARY KEY,
    amount DECIMAL(10,2),
    status VARCHAR(50),
    method VARCHAR(50),
    transaction_ref VARCHAR(255),
    created_at TIMESTAMP,
    subscription_id BIGINT  -- Lien vers la subscription payée
);
```

**Caractéristiques:**
- Créé **APRÈS** vérification du paiement
- Contient le statut final (SUCCESS, FAILED, PENDING)
- Lié à la subscription via `subscription_id`
- N'est PAS consulté pour savoir si une subscription est active (on regarde `subscription.status`)
- **Les bookings ne sont pas liés aux paiements** (pas de champ `booking_id`)

## Ajouter un Nouveau Gateway (ex: Stripe)

Grâce aux principes SOLID, c'est très simple :

1. **Créer l'implémentation:**
```java
@Service
public class StripePaymentGateway implements PaymentGateway {
    
    @Override
    public PaymentGatewayResponse generatePayment(long amount, String successLink, String failLink, String trackingId) {
        // Appeler l'API Stripe
    }
    
    @Override
    public PaymentVerificationResult verifyPayment(String paymentId) {
        // Vérifier avec Stripe
    }
    
    @Override
    public String getGatewayName() {
        return "STRIPE";
    }
}
```

2. **Créer le bean:**
```java
@Bean("stripePaymentGateway")
public PaymentGateway stripePaymentGateway() {
    return new StripePaymentGateway(stripeConfig);
}
```

3. **Changer l'injection:**
```java
public PaymentServiceImpl(
    @Qualifier("stripePaymentGateway") PaymentGateway paymentGateway,
    // ...
) {
```

**Aucune modification du code métier nécessaire !** ✅

## Configuration (.env)

```env
FLOUCI_BASE_URL=https://developers.flouci.com
FLOUCI_APP_TOKEN=your_app_token
FLOUCI_APP_SECRET=your_app_secret
FLOUCI_APP_PUBLIC=your_app_public
FLOUCI_DEVELOPER_TRACKING_ID=your_tracking_id
```

## Prix des Plans

Définis dans `SubscriptionPricingServiceImpl` :

| Plan       | Prix (TND) | Durée (jours) |
|------------|------------|---------------|
| FREE       | 0          | 0             |
| BASIC      | 10         | 30            |
| PRO        | 25         | 90            |
| PREMIUM    | 50         | 365           |
| ENTERPRISE | 100        | 3650          |

**Pour modifier les prix**, éditez simplement `SubscriptionPricingServiceImpl.java`.

## Sécurité

✅ Le montant est calculé côté backend
✅ Le subscription_id est généré par le backend
✅ La vérification du paiement passe par l'API Flouci
✅ Les logs de paiement sont créés après vérification

## Exemple Frontend (React)

```javascript
// 1. Initier le paiement
const handleSubscribe = async (plan) => {
  const response = await fetch('/v1/subscriptions/payments/initiate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      businessId: currentBusiness.id,
      plan: plan,
      successLink: 'http://localhost:3000/subscription/success',
      failLink: 'http://localhost:3000/subscription/fail'
    })
  });
  
  const data = await response.json();
  if (data.success) {
    window.location.href = data.checkoutUrl;
  }
};

// 2. Vérifier le paiement (page success)
const SuccessPage = () => {
  const [searchParams] = useSearchParams();
  
  useEffect(() => {
    const verify = async () => {
      const paymentId = searchParams.get('payment_id');
      const subscriptionId = searchParams.get('subscriptionId');
      
      const response = await fetch(
        `/v1/subscriptions/payments/verify?payment_id=${paymentId}&subscriptionId=${subscriptionId}`
      );
      
      const data = await response.json();
      if (data.success) {
        toast.success('Subscription activated!');
      }
    };
    verify();
  }, []);
};
```

## Tests

Pour tester en local avec Flouci :
1. Utilisez les credentials de test fournis par Flouci
2. Les liens de succès/échec doivent être accessibles publiquement (utilisez ngrok si nécessaire)
3. Vérifiez les logs dans la console pour voir les requêtes/réponses Flouci
# Architecture de Paiement - Principes SOLID

## Vue d'ensemble

L'architecture de paiement a été refactorisée pour suivre les principes SOLID, permettant une extension facile avec de nouveaux fournisseurs de paiement (Stripe, PayPal, etc.) sans modifier le code existant.

**Note importante:** Le système de paiement gère **UNIQUEMENT les subscriptions**. Les bookings ne nécessitent pas de paiement.

## Architecture en Couches

### 1. **Abstraction du Gateway de Paiement** (Interface Segregation & Dependency Inversion)

```
PaymentGateway (interface)
    ├── generatePayment()
    ├── verifyPayment()
    └── getGatewayName()
```

**Implémentations:**
- `FlouciPaymentGateway` - Implémentation pour Flouci
- (Futur) `StripePaymentGateway`, `PayPalPaymentGateway`, etc.

### 2. **Services**

#### `PaymentService`
Gère la logique métier des paiements. Dépend de l'abstraction `PaymentGateway`, pas d'une implémentation spécifique.

#### `SubscriptionPricingService`
Gère les prix et durées des plans d'abonnement. **Le montant n'est JAMAIS envoyé par le frontend.**

### 3. **Flux de Paiement**

#### A. Initiation du Paiement

1. **Frontend** envoie uniquement :
   ```json
   {
     "businessId": 123,
     "plan": "PRO",
     "successLink": "http://localhost:3000/subscription/success",
     "failLink": "http://localhost:3000/subscription/fail"
   }
   ```

2. **Backend** :
   - Calcule le prix selon le plan (sécurité !)
   - Crée une `Subscription` en statut `PENDING`
   - Génère le lien de paiement via le gateway
   - Retourne le lien de checkout

3. **Frontend** redirige l'utilisateur vers le lien de paiement

#### B. Vérification du Paiement

1. Après paiement, Flouci redirige vers : 
   ```
   http://localhost:3000/subscription/success?payment_id=XXX&subscriptionId=YYY
   ```

2. **Frontend** appelle :
   ```
   GET /v1/subscriptions/payments/verify?payment_id=XXX&subscriptionId=YYY
   ```

3. **Backend** :
   - Vérifie le paiement avec Flouci
   - Crée un **log de paiement** dans la table `payments` (transaction ref, montant, statut)
   - Si succès : active la subscription (statut `ACTIVE`, définit `startDate` et `endDate`)
   - Si échec : marque la subscription comme `FAILED`
   - Lie le paiement à la subscription via `subscription_id`

## Endpoints

### Nouveaux (Recommandés)

```
POST /v1/subscriptions/payments/initiate
GET  /v1/subscriptions/payments/verify?payment_id=XXX&subscriptionId=YYY
POST /v1/subscriptions/payments/webhook
```

### Legacy (Dépréciés)

```
POST /v1/payments/flouci/generate
POST /v1/payments/flouci/verify
POST /v1/payments/flouci/webhook
```

## Intégration Flouci

Basé sur la documentation officielle : https://docs.flouci.com

### Générer un Paiement

**Request:**
```json
{
  "app_token": "xxx",
  "app_secret": "xxx",
  "amount": 10000,
  "accept_card": "true",
  "session_timeout_secs": 1200,
  "success_link": "http://localhost:3000/subscription/success",
  "fail_link": "http://localhost:3000/subscription/fail",
  "developer_tracking_id": "SUB-123"
}
```

**Response:**
```json
{
  "result": {
    "success": true,
    "payment_id": "abc123",
    "link": "https://pay.flouci.com/checkout/xxx"
  }
}
```

### Vérifier un Paiement

**Request:**
```
GET https://developers.flouci.com/api/verify_payment/{payment_id}
Headers:
  apppublic: xxx
  appsecret: xxx
```

**Response:**
```json
{
  "result": {
    "status": "SUCCESS",
    "amount": 10000
  }
}
```

## Table des Paiements (Logs)

La table `payments` sert **uniquement de log** :

```sql
CREATE TABLE payments (
    id BIGINT PRIMARY KEY,
    amount DECIMAL(10,2),
    status VARCHAR(50),
    method VARCHAR(50),
    transaction_ref VARCHAR(255),
    created_at TIMESTAMP,
    subscription_id BIGINT,  -- Lien vers la subscription payée
    booking_id BIGINT        -- (Futur) Lien vers le booking payé
);
```

**Caractéristiques:**
- Créé **APRÈS** vérification du paiement
- Contient le statut final (SUCCESS, FAILED, PENDING)
- Lié à la subscription via `subscription_id`
- N'est PAS consulté pour savoir si une subscription est active (on regarde `subscription.status`)

## Ajouter un Nouveau Gateway (ex: Stripe)

Grâce aux principes SOLID, c'est très simple :

1. **Créer l'implémentation:**
```java
@Service
public class StripePaymentGateway implements PaymentGateway {
    
    @Override
    public PaymentGatewayResponse generatePayment(long amount, String successLink, String failLink, String trackingId) {
        // Appeler l'API Stripe
    }
    
    @Override
    public PaymentVerificationResult verifyPayment(String paymentId) {
        // Vérifier avec Stripe
    }
    
    @Override
    public String getGatewayName() {
        return "STRIPE";
    }
}
```

2. **Créer le bean:**
```java
@Bean("stripePaymentGateway")
public PaymentGateway stripePaymentGateway() {
    return new StripePaymentGateway(stripeConfig);
}
```

3. **Changer l'injection:**
```java
public PaymentServiceImpl(
    @Qualifier("stripePaymentGateway") PaymentGateway paymentGateway,
    // ...
) {
```

**Aucune modification du code métier nécessaire !** ✅

## Configuration (.env)

```env
FLOUCI_BASE_URL=https://developers.flouci.com
FLOUCI_APP_TOKEN=your_app_token
FLOUCI_APP_SECRET=your_app_secret
FLOUCI_APP_PUBLIC=your_app_public
FLOUCI_DEVELOPER_TRACKING_ID=your_tracking_id
```

## Prix des Plans

Définis dans `SubscriptionPricingServiceImpl` :

| Plan       | Prix (TND) | Durée (jours) |
|------------|------------|---------------|
| FREE       | 0          | 0             |
| BASIC      | 10         | 30            |
| PRO        | 25         | 90            |
| PREMIUM    | 50         | 365           |
| ENTERPRISE | 100        | 3650          |

**Pour modifier les prix**, éditez simplement `SubscriptionPricingServiceImpl.java`.

## Sécurité

✅ Le montant est calculé côté backend
✅ Le subscription_id est généré par le backend
✅ La vérification du paiement passe par l'API Flouci
✅ Les logs de paiement sont créés après vérification

## Exemple Frontend (React)

```javascript
// 1. Initier le paiement
const handleSubscribe = async (plan) => {
  const response = await fetch('/v1/subscriptions/payments/initiate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      businessId: currentBusiness.id,
      plan: plan,
      successLink: 'http://localhost:3000/subscription/success',
      failLink: 'http://localhost:3000/subscription/fail'
    })
  });
  
  const data = await response.json();
  if (data.success) {
    window.location.href = data.checkoutUrl;
  }
};

// 2. Vérifier le paiement (page success)
const SuccessPage = () => {
  const [searchParams] = useSearchParams();
  
  useEffect(() => {
    const verify = async () => {
      const paymentId = searchParams.get('payment_id');
      const subscriptionId = searchParams.get('subscriptionId');
      
      const response = await fetch(
        `/v1/subscriptions/payments/verify?payment_id=${paymentId}&subscriptionId=${subscriptionId}`
      );
      
      const data = await response.json();
      if (data.success) {
        toast.success('Subscription activated!');
      }
    };
    verify();
  }, []);
};
```

## Tests

Pour tester en local avec Flouci :
1. Utilisez les credentials de test fournis par Flouci
2. Les liens de succès/échec doivent être accessibles publiquement (utilisez ngrok si nécessaire)
3. Vérifiez les logs dans la console pour voir les requêtes/réponses Flouci

