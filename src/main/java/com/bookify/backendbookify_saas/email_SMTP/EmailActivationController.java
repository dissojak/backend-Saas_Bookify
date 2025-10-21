package com.bookify.backendbookify_saas.email;

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
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour l'inscription et la connexion")
public class EmailActivationController {

    private final AuthService authService;

    /**
     * Activation d'un compte utilisateur
     */
    @GetMapping("/activate")
    @Operation(summary = "Active un compte utilisateur", description = "Active le compte avec le token reçu par email")
    public ResponseEntity<Map<String, String>> activateAccount(@RequestParam("token") String token) {
        String message = authService.activateAccount(token);
        return ResponseEntity.ok(Map.of("message", message));
    }
}
