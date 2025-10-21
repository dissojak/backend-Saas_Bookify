package com.bookify.backendbookify_saas.email_SMTP;

import com.bookify.backendbookify_saas.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user signup and login")
public class EmailActivationController {

    private final AuthService authService;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("${frontend.prod.url:https://myproductionfrontend.com}")
    private String prodFrontendUrl;

    @GetMapping("/activate")
    @Operation(summary = "Activate a user account", description = "Activates the account using the token received by email")
    public Object activateAccount(@RequestParam("token") String token) {
        String message = authService.activateAccount(token);
        boolean success = message.toLowerCase().contains("success");

        if ("prod".equals(activeProfile)) {
            // Production → always redirect to production frontend
            String path = success ? "/activation-success" : "/activation-failed";
            return new RedirectView(prodFrontendUrl + path);
        } else {
            // Development → check if localhost:3000 is running
            String devUrl = "http://localhost:3000";
            if (isServerRunning(devUrl)) {
                String path = success ? "/activation-success" : "/activation-failed";
                return new RedirectView(devUrl + path);
            } else {
                // If dev frontend not running → return simple HTML
                return "<html><body style='font-family:sans-serif;text-align:center;padding:50px;'>"
                        + "<h1>" + (success ? "Account Activated Successfully ✅" : "Activation Failed ❌") + "</h1>"
                        + "<p>" + message + "</p>"
                        + "</body></html>";
            }
        }
    }

    private boolean isServerRunning(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000); // 2 seconds
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode < 400);
        } catch (IOException e) {
            return false;
        }
    }
}
