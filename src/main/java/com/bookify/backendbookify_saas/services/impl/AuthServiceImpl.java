package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.email_SMTP.MailService;
import com.bookify.backendbookify_saas.exceptions.InvalidTokenException;
import com.bookify.backendbookify_saas.exceptions.UserAlreadyExistsException;
import com.bookify.backendbookify_saas.models.dtos.AuthResponse;
import com.bookify.backendbookify_saas.models.dtos.LoginRequest;
import com.bookify.backendbookify_saas.models.dtos.RefreshTokenResponse;
import com.bookify.backendbookify_saas.models.dtos.SignupRequest;
import com.bookify.backendbookify_saas.models.dtos.UserProfileResponse;
import com.bookify.backendbookify_saas.models.entities.*;
import com.bookify.backendbookify_saas.models.enums.RoleEnum;
import com.bookify.backendbookify_saas.models.enums.UserStatusEnum;
import com.bookify.backendbookify_saas.repositories.ActivationTokenRepository;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.UserRepository;
import com.bookify.backendbookify_saas.security.JwtService;
import com.bookify.backendbookify_saas.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation du service d'authentification
 * Architecture: Controller → DTO → Service Interface → Service Implementation → Repository → Entity
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final BusinessRepository businessRepository;

    /**
     * Inscription d'un nouveau client/utilisateur avec rôle optionnel
     */
    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // 1. Vérifier l'unicité de l'email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("A user with that email already exists");
        }

        // 2. Déterminer le rôle à utiliser (par défaut CLIENT)
        RoleEnum role = request.getRole() == null ? RoleEnum.CLIENT : request.getRole();

        // 3. Créer un utilisateur
        User user = new User();

        // 4. Définir les champs communs
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        // 5. Définir avatar/phone pour tous les utilisateurs
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAvatarUrl(request.getAvatarUrl());

        // 6. Définir le statut initial: ADMIN → VERIFIED, autres → PENDING
        if (role == RoleEnum.ADMIN) {
            user.setStatus(UserStatusEnum.VERIFIED);
        } else {
            user.setStatus(UserStatusEnum.PENDING);
        }

        // 7. Sauvegarder
        User savedUser = userRepository.save(user);

        // 8. Si non VERIFIED, créer un token d'activation et envoyer l'email
        if (savedUser.getStatus() != UserStatusEnum.VERIFIED) {
            String activationTokenValue = UUID.randomUUID().toString();
            ActivationToken activationToken = ActivationToken.builder()
                    .token(activationTokenValue)
                    .user(savedUser)
                    .expiryDate(LocalDateTime.now().plusDays(7))
                    .build();
            activationTokenRepository.save(activationToken);

            mailService.sendActivationEmail(
                    savedUser.getEmail(),
                    savedUser.getName(),
                    activationTokenValue
            );
        }

        // 9. Build response (generate access token only if account is VERIFIED)
        // Refresh token will be set in httpOnly cookie by the controller
        String token = null;
        if (savedUser.getStatus() == UserStatusEnum.VERIFIED) {
            String subject = String.valueOf(savedUser.getId());
            token = jwtService.generateTokenForSubject(subject);
        }

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .status(savedUser.getStatus())
                .avatar(savedUser.getAvatarUrl())
                .message(savedUser.getStatus() == UserStatusEnum.VERIFIED
                        ? "Administrator signup successful. The account is already verified."
                        : "Signup successful. Please check your email to activate your account.")
                .build();

        logger.info("Signup response being returned: status={} avatarUrl={} response={}", savedUser.getStatus(), savedUser.getAvatarUrl(), response);
        return response;
    }

    /**
     * Activate a user account
     */
    @Override
    @Transactional
    public String activateAccount(String token) {
        // 1. Retrieve the activation token
        ActivationToken activationToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid activation token or already used"));

        // 2. Check if the token has expired
        if (activationToken.isExpired()) {
            activationTokenRepository.delete(activationToken);
            throw new InvalidTokenException("Activation token has expired");
        }

        // 3. Retrieve the user
        User user = activationToken.getUser();

        // 4. Check if the account is already activated
        if (user.getStatus() == UserStatusEnum.VERIFIED) {
            activationTokenRepository.delete(activationToken);
            return "Account is already activated.";
        }

        // 5. Activate the account
        user.setStatus(UserStatusEnum.VERIFIED);
        userRepository.save(user);

        // 6. Delete the activation token
        activationTokenRepository.delete(activationToken);

        // 7. Send activation confirmation email
        mailService.sendActivationConfirmationEmail(user.getEmail(), user.getName());

        // 8. Return success message in English
        return "Your account has been activated successfully. You can now log in.";
    }

    /**
     * Connexion d'un utilisateur
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Authentifier l'utilisateur
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Récupérer l'utilisateur depuis le Repository
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 3. Vérifier si le compte est activé
        if (user.getStatus() == UserStatusEnum.PENDING) {
            throw new IllegalArgumentException("Please activate your account using the activation email we've sent to you");
        }

        if (user.getStatus() == UserStatusEnum.SUSPENDED) {
            throw new IllegalArgumentException("Your account has been suspended. Please contact support.");
        }

        // 4. Generate access token only (refresh token will be set in httpOnly cookie by controller)
        String token = jwtService.generateTokenForSubject(String.valueOf(user.getId()));

        // Prepare builder with common fields
        AuthResponse.AuthResponseBuilder builder = AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .avatar(user.getAvatarUrl());

        // 5. If owner — check business existence and include it in response
        if (user.getRole() == RoleEnum.BUSINESS_OWNER) {
            boolean hasBusiness = false;
            Long businessId = null;
            String businessName = null;

            Optional<Business> maybeBusiness = businessRepository.findByOwnerId(user.getId());
            if (maybeBusiness.isPresent()) {
                Business b = maybeBusiness.get();
                hasBusiness = true;
                businessId = b.getId();
                businessName = b.getName();
            }

            // attach owner-specific fields (will be serialized only for owners)
            builder.hasBusiness(hasBusiness)
                    .businessId(businessId)
                    .businessName(businessName);
        }

        // message and build
        builder.message("Login successful");
        AuthResponse response = builder.build();
        logger.info("Login response being returned: status={} avatar={} response={}", user.getStatus(), user.getAvatarUrl(), response);
        return response;
    }

    /**
     * Refresh JWT access token using a refresh token
     */
    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        try {
            // Extract userId from refresh token
            String userId = jwtService.extractUsername(refreshToken);

            // Validate the refresh token
            if (!jwtService.isTokenValidForSubject(refreshToken, userId)) {
                throw new InvalidTokenException("Invalid or expired refresh token");
            }

            // Generate new access token only (new refresh token will be set in cookie by controller)
            String newAccessToken = jwtService.generateTokenForSubject(userId);

            return RefreshTokenResponse.builder()
                    .token(newAccessToken)
                    .message("Token refreshed successfully")
                    .build();
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
    }

    /**
     * Logout user (invalidate session)
     * Note: With JWT, actual invalidation would require a token blacklist/redis
     * For now, we just return success and client should delete tokens
     */
    @Override
    public String logout(String userId, String refreshToken) {
        // Validate user exists
        userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // In production, you would:
        // 1. Add refresh token to blacklist (Redis)
        // 2. Clear any session data
        // Example: if (refreshToken != null) { redisTemplate.opsForValue().set("blacklist:" + refreshToken, "true", refreshTokenExpiration, TimeUnit.MILLISECONDS); }

        logger.info("User {} logged out", userId);
        return "Logged out successfully";
    }

    /**
     * Generate a refresh token for a specific user
     */
    @Override
    public String generateRefreshTokenForUser(Long userId) {
        return jwtService.generateRefreshTokenForSubject(String.valueOf(userId));
    }

    /**
     * Get current authenticated user profile
     */
    @Override
    public UserProfileResponse getCurrentUser(String userId) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .avatarUrl(user.getAvatarUrl());

        // If business owner, include business info
        if (user.getRole() == RoleEnum.BUSINESS_OWNER) {
            Optional<Business> maybeBusiness = businessRepository.findByOwnerId(user.getId());
            if (maybeBusiness.isPresent()) {
                Business b = maybeBusiness.get();
                builder.hasBusiness(true)
                        .businessId(b.getId())
                        .businessName(b.getName());
            } else {
                builder.hasBusiness(false);
            }
        }

        return builder.build();
    }
}
