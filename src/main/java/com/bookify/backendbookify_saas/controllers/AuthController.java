package com.bookify.backendbookify_saas.controllers.client;

import com.bookify.backendbookify_saas.models.dtos.AuthResponse;
import com.bookify.backendbookify_saas.models.dtos.LoginRequest;
import com.bookify.backendbookify_saas.models.dtos.SignupRequest;
import com.bookify.backendbookify_saas.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur pour gérer l'authentification (inscription et connexion)
 * Architecture Clean: Reçoit les requêtes, valide les données, appelle le service
 */
@RestController
@RequestMapping("/v1/client/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour l'inscription et la connexion")
public class AuthController {

    private final AuthService authService;

    /**
     * Inscription d'un nouveau client
     * Les exceptions sont gérées par le GlobalExceptionHandler
     */
    @PostMapping("/signup")
    @Operation(summary = "Inscription d'un nouveau client", description = "Crée un nouveau compte client")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Connexion d'un utilisateur
     * Les exceptions sont gérées par le GlobalExceptionHandler
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion d'un utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
