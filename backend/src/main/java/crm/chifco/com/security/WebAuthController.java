package crm.chifco.com.security;

import crm.chifco.com.dto.ApiResponse;
import crm.chifco.com.dto.AuthenticationRequest;
import crm.chifco.com.dto.AuthenticationResponse;
import crm.chifco.com.model.User;
import crm.chifco.com.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class WebAuthController {

    private static final Logger logger = LogManager.getLogger(WebAuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebJwtService webJwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login( @RequestBody AuthenticationRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username.trim(), password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = webJwtService.generateToken(username.trim());
            String refreshToken = webJwtService.generateRefreshToken(username.trim());

            User user = userService.findUsersByEmail(username.trim());
            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            Map<String, Object> userData = buildUserData(user, authorities);
            AuthenticationResponse response = new AuthenticationResponse(token, refreshToken, userData);

            return ResponseEntity.ok(new ApiResponse<>(true, "Authentification réussie", response));

        } catch (DisabledException e) {
            return ResponseEntity.status(403).body(new ApiResponse<>(false, "Compte désactivé", null));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Email ou mot de passe incorrect", null));
        } catch (Exception e) {
            logger.error("Login error for {}: {}", username, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Erreur interne du serveur", null));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "Token de rafraîchissement manquant", null));
        }

        String refreshToken = authHeader.substring(7);
        if (!webJwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, "Token de rafraîchissement invalide ou expiré", null));
        }

        try {
            String email = webJwtService.extractEmail(refreshToken);
            String newToken = webJwtService.generateToken(email);
            String newRefreshToken = webJwtService.generateRefreshToken(email);

            AuthenticationResponse response = new AuthenticationResponse(newToken, newRefreshToken, null);
            return ResponseEntity.ok(new ApiResponse<>(true, "Token rafraîchi avec succès", response));
        } catch (Exception e) {
            logger.error("Refresh token error: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Erreur lors du rafraîchissement du token", null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Non authentifié", null));
        }

        try {
            String email = authentication.getName();
            User user = userService.findUsersByEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }

            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur récupéré", buildUserData(user, authorities)));
        } catch (Exception e) {
            logger.error("Get current user error: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Erreur serveur", null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new ApiResponse<>(true, "Déconnexion réussie", null));
    }

    private Map<String, Object> buildUserData(User user, List<String> authorities) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getUserid());
        userData.put("email", user.getEmail());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("photo", user.getPhoto());
        userData.put("telephone", user.getTelephone());
        userData.put("typeUser", user.getTypeUser());
        userData.put("authorities", authorities);
        if (user.getRole() != null) {
            userData.put("role", user.getRole().getRoleName());
        }
        return userData;
    }
}
