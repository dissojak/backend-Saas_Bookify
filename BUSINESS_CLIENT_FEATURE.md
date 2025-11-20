# BusinessClient Feature - Implementation Guide

## Overview
This feature allows businesses to manage their own local clients (BusinessClient) separately from global platform users. A BusinessClient belongs exclusively to one business and can be used for bookings alongside regular User clients.

## Components Created

### 1. Entity Layer

#### BusinessClient Entity
- **Location**: `models/entities/BusinessClient.java`
- **Fields**:
  - `id` (Long, auto-generated)
  - `name` (String, required)
  - `phone` (String, required, unique per business)
  - `email` (String, optional)
  - `notes` (String, optional, max 2000 chars)
  - `business` (ManyToOne, required reference to Business)
  - `createdAt`, `updatedAt` (timestamps)
- **Constraints**: Unique constraint on (business_id, phone)

#### ServiceBooking Entity Updates
- **Location**: `models/entities/ServiceBooking.java`
- **Changes**:
  - Added `businessClient` field (ManyToOne to BusinessClient)
  - Made `client` field optional (was required)
  - Added validation method `validateClientPresence()` to ensure either `client` or `businessClient` is set (but not both)

### 2. Repository Layer

#### BusinessClientRepository
- **Location**: `repositories/BusinessClientRepository.java`
- **Methods**:
  - `findByBusinessId(Long businessId)` - Get all clients for a business
  - `findByIdAndBusinessId(Long id, Long businessId)` - Get specific client with business check
  - `existsByBusinessIdAndPhone(Long businessId, String phone)` - Check phone uniqueness
  - `deleteByBusinessId(Long businessId)` - Delete all clients for a business

#### ServiceBookingRepository Updates
- **Location**: `repositories/ServiceBookingRepository.java`
- **Added Methods**:
  - Support for querying bookings by BusinessClient
  - Support for date-based queries

### 3. Service Layer

#### BusinessClientService Interface
- **Location**: `services/BusinessClientService.java`
- **Methods**: Full CRUD operations with authentication checks

#### BusinessClientServiceImpl
- **Location**: `services/impl/BusinessClientServiceImpl.java`
- **Features**:
  - **Access Control**: Validates that authenticated user belongs to the business (owner or staff)
  - **Phone Validation**: Ensures phone numbers are unique per business
  - **Full CRUD**: Create, Read, Update, Delete operations
  - **DTO Mapping**: Converts entities to response DTOs

#### BookingServiceImpl
- **Location**: `services/impl/BookingServiceImpl.java`
- **New Features**:
  - `createServiceBookingFromRequest()` - Creates bookings with BusinessClient support
  - Validates BusinessClient belongs to same business as the service
  - `getBookingsByBusinessClient()` - Query bookings for a BusinessClient
  - Client type validation before saving

### 4. Controller Layer

#### BusinessClientController
- **Location**: `controllers/BusinessClientController.java`
- **Base Path**: `/v1/business/{businessId}/clients`
- **Security**: `@PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'STAFF')")`

**Endpoints**:
```
POST   /v1/business/{businessId}/clients              - Create client
GET    /v1/business/{businessId}/clients              - List all clients
GET    /v1/business/{businessId}/clients/{clientId}   - Get client details
PUT    /v1/business/{businessId}/clients/{clientId}   - Update client
DELETE /v1/business/{businessId}/clients/{clientId}   - Delete client
```

### 5. DTOs

#### Request DTOs
- **BusinessClientCreateRequest**: Name, phone (required), email, notes (optional)
- **BusinessClientUpdateRequest**: All fields optional for partial updates
- **ServiceBookingCreateRequest**: Supports both `clientId` and `businessClientId`

#### Response DTOs
- **BusinessClientResponse**: Full client info with business details
- **ServiceBookingResponse**: Includes `clientType` field ("USER" or "BUSINESS_CLIENT")

## Security & Access Control

### Access Rules
1. **Business Owner**: Full access to their business's clients
2. **Staff Members**: Full access to clients of the business they work for
3. **Other Users**: No access

### Validation
- User's business ID is validated against the requested business ID
- For staff: Checks if `staff.getBusiness().getId()` matches requested business
- For owners: Checks if `user.getBusiness().getId()` matches requested business

## Booking Logic Updates

### Creating Bookings
When creating a booking, you can now provide either:
- `clientId` (references a global User)
- `businessClientId` (references a BusinessClient)

**Validation Rules**:
1. Exactly one of `clientId` or `businessClientId` must be provided
2. If `businessClientId` is used, the BusinessClient must belong to the same business as the service
3. Time slot availability is checked before booking creation

### Example Request
```json
{
  "serviceId": 1,
  "businessClientId": 5,
  "staffId": 3,
  "date": "2025-11-25",
  "startTime": "10:00",
  "endTime": "11:00",
  "notes": "First appointment",
  "price": 50.00,
  "status": "CONFIRMED"
}
```

## Database Schema

### business_clients Table
```sql
CREATE TABLE business_clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    notes VARCHAR(2000),
    business_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (business_id) REFERENCES businesses(id),
    UNIQUE KEY uq_business_client_phone (business_id, phone)
);
```

### service_bookings Table Update
```sql
ALTER TABLE service_bookings
    MODIFY COLUMN client_id BIGINT NULL,
    ADD COLUMN business_client_id BIGINT NULL,
    ADD FOREIGN KEY (business_client_id) REFERENCES business_clients(id);
```

## Usage Examples

### 1. Create a Business Client
```bash
POST /v1/business/1/clients
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "John Doe",
  "phone": "+1234567890",
  "email": "john@example.com",
  "notes": "VIP client, prefers morning appointments"
}
```

### 2. List Business Clients
```bash
GET /v1/business/1/clients
Authorization: Bearer {jwt_token}
```

### 3. Update a Client
```bash
PUT /v1/business/1/clients/5
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "phone": "+0987654321",
  "notes": "Updated contact information"
}
```

### 4. Create Booking with BusinessClient
```bash
POST /v1/bookings
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "serviceId": 10,
  "businessClientId": 5,
  "date": "2025-11-30",
  "startTime": "14:00",
  "endTime": "15:00",
  "price": 75.00
}
```

## Testing Checklist

- [ ] Business owner can create clients for their business
- [ ] Staff can create clients for their business
- [ ] Users cannot access clients from other businesses
- [ ] Phone uniqueness is enforced per business
- [ ] Bookings can be created with BusinessClient
- [ ] BusinessClient validation prevents cross-business bookings
- [ ] Partial updates work correctly
- [ ] Delete operations work and respect access control
- [ ] List endpoints return correct data with business info

## Benefits

1. **Flexibility**: Businesses can manage walk-in or phone clients without requiring platform registration
2. **Privacy**: Business client data is isolated per business
3. **Simplicity**: No need to create full User accounts for all clients
4. **Backward Compatible**: Existing User-based bookings continue to work
5. **Data Integrity**: Validation ensures BusinessClients can only book with their own business

## Next Steps

1. Add pagination to the list endpoints
2. Add search/filter capabilities (by name, phone, email)
3. Add export functionality for client lists
4. Add client booking history endpoint
5. Add analytics for business client management

