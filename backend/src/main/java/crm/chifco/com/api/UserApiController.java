package crm.chifco.com.api;

import crm.chifco.com.dto.ApiResponse;
import crm.chifco.com.dto.UserResponse;
import crm.chifco.com.model.User;
import crm.chifco.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") Long id) {
        User user = userService.findUsersByIduser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserResponse response = toUserResponse(user, null);
        return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur récupéré", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Non authentifié", null));
        }

        String email = authentication.getName();
        User user = userService.findUsersByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
        }

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur connecté", toUserResponse(user, authorities)));
    }

    private UserResponse toUserResponse(User user, List<String> authorities) {
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
        return new UserResponse(user.getUserid(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getTelephone(), user.getTypeUser(), roleName, authorities);
    }
}
