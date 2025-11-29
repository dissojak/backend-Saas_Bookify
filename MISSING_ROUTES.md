Auth:

/v1/auth/refresh : refresh JWT access token using a refresh token
/v1/auth/logout : invalidate user session / refresh token (log out)
/v1/auth/me : return current authenticated user's profile
/v1/auth/forgot-password : start password reset (send email)
/v1/auth/reset-password : complete password reset with token
/v1/auth/confirm-email-resend : resend activation/confirmation email
Health:

/v1/health : general app health / readiness check
/v1/health/db : database health / readiness probe
/v1/health/metrics : application metrics / telemetry
Business (owner/public):

/v1/owner/businesses (GET) : list businesses owned by authenticated owner
/v1/owner/businesses/{businessId} (GET) : get owner-specific business full details
/v1/owner/businesses/{businessId} (DELETE) : delete a business (owner)
/v1/owner/businesses/{businessId}/images : upload/manage business images
/v1/owner/businesses/{businessId}/status : change business status (open/closed/verified)
/v1/businesses : list / search public businesses
/v1/businesses/{businessId} : get public business details
/v1/businesses/{businessId}/services (GET) : list services offered by business
/v1/businesses/{businessId}/services/{serviceId} (GET) : get service details
/v1/businesses/{businessId}/services (POST) : create a service (owner/staff)
/v1/businesses/{businessId}/services/{serviceId} (PUT) : update a service (owner/staff)
/v1/businesses/{businessId}/services/{serviceId} (DELETE) : delete a service (owner/staff)
/v1/businesses/{businessId}/staff : list staff members for a business
/v1/businesses/{businessId}/staff/{staffId} : add/update/remove staff member
/v1/businesses/{businessId}/availability : get service/slot availability / calendar
Business client extras:

/v1/business/{businessId}/clients/search : search clients by name/email
/v1/business/{businessId}/clients/{clientId}/notes : create/list client notes
/v1/business/{businessId}/clients/{clientId}/history : client booking/history
Bookings:

/v1/bookings (POST) : create a new booking/reservation (client)
/v1/bookings/{bookingId} (GET) : retrieve booking by ID (client/owner/staff)
/v1/bookings/{bookingId} (PUT) : update a booking (client/owner/staff)
/v1/bookings/{bookingId}/status (PUT) : update booking status (owner/staff)
/v1/bookings/client (GET) : retrieve bookings for authenticated client
/v1/bookings/business/{businessId} (GET) : retrieve bookings for a business
/v1/bookings/{bookingId}/cancel (POST/PUT) : cancel a booking
Reviews:

/v1/reviews (POST) : create a review (client)
/v1/reviews/{reviewId} (GET) : get a review by ID (public)
/v1/reviews/business/{businessId} (GET) : list reviews for a business (public)
/v1/reviews/{reviewId} (PUT) : update a review (author)
/v1/reviews/{reviewId}/respond (POST) : owner/staff reply to a review
/v1/reviews/{reviewId} (DELETE) : delete review (author/owner/admin)
Categories:

/v1/categories (GET) : list categories (public)
/v1/categories (POST) : create category (admin)
/v1/categories/{categoryId} (GET) : get category by id (public)
/v1/categories/{categoryId} (PUT) : update category (admin)
/v1/categories/{categoryId} (DELETE) : delete category (admin)
Users / Admin:

/v1/users (GET) : list all users (admin)
/v1/users/{userId} (GET) : get user by id (admin)
/v1/users/{userId} (PUT) : update user (admin)
/v1/users/{userId} (DELETE) : delete user (admin)
/v1/users/role/{role} (GET) : list users filtered by role (admin)
/v1/users/{userId}/avatar : upload/get user avatar
/v1/admin/login : admin authentication (if separate)
/v1/admin/dashboard : admin stats / reports
/v1/admin/businesses : admin manage/approve businesses
/v1/admin/reports : generate/download admin reports
Payments:

/v1/payments/create-intent : create payment intent / start checkout
/v1/payments/webhook : payment provider webhook (stripe/paypal)
/v1/payments/{paymentId} : payment status / receipt
Files / uploads:

/v1/uploads (POST) : upload files/images
/v1/uploads/{id} (GET) : fetch uploaded asset
/v1/uploads/{id} (DELETE) : remove uploaded asset
Search / notifications / misc:

/v1/search?q=... : global search (businesses/services/staff)
/v1/notifications (GET) : list user notifications
/v1/notifications/mark-read (POST) : mark notifications as read
AI / OAuth:

/v1/ai/evaluate-business (POST) : run AI evaluation for a business (submit data)
/v1/ai/reports/{reportId} (GET) : get AI evaluation result/report
/v1/oauth/{provider}/callback : OAuth provider callback (google/facebook)
/v1/oauth/{provider}/link : link external account to user


/v1/business/{businessId}/clients/search : search clients by name/email ( a normal client or a buissness client )

/v1/business/{businessId}/clients/{clientId}/history : client booking/history 

