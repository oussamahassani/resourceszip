package crm.chifco.com.controller.rest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.Historique;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.HistoriqueRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.UserTypeConstant;

@RestController
@RequestMapping("/admin")
public class UserRestController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserService userService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  HistoriqueRepository historiqueRepository;

  @GetMapping("/users")
  public ResponseEntity<Page<User>> getUsers(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String nom,
      @RequestParam(required = false) String prenom,
      @RequestParam(required = false) String refUser,
      @RequestParam(required = false) Long gouvernorat,
      @RequestParam(required = false) Long villes,
      @RequestParam(required = false) Long role) {
    try {
      Page<User> users = userService.findPaginatedWithFilter(
          page, size, nom, prenom, refUser, gouvernorat, villes, null, null, role);
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      logger.error("Error fetching users", e);
      return ResponseEntity.ok(Page.empty());
    }
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAuthority('ADD_USER')")
  @PostMapping("/users")
  public ResponseEntity<?> createUser(@RequestBody User user) {
    try {
      if (userRepository.findUsersByEmail(user.getEmail()) != null) {
        return ResponseEntity.badRequest().body("Email déjà utilisé");
      }
      if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
      }
      user.setEnabled(true);
      User saved = userRepository.save(user);
      return ResponseEntity.ok(saved);
    } catch (Exception e) {
      logger.error("Error creating user", e);
      return ResponseEntity.badRequest().body("Erreur lors de la création de l'utilisateur");
    }
  }

  @PreAuthorize("hasAuthority('EDIT_USER')")
  @PutMapping("/users/{id}")
  public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User body) {
    Optional<User> existing = userRepository.findById(id);
    if (!existing.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    try {
      User user = existing.get();
      if (body.getFirstName() != null) user.setFirstName(body.getFirstName());
      if (body.getLastName() != null) user.setLastName(body.getLastName());
      if (body.getEmail() != null) user.setEmail(body.getEmail());
      if (body.getRole() != null) user.setRole(body.getRole());
      if (body.getTypeUser() != null) user.setTypeUser(body.getTypeUser());
      if (body.getPassword() != null && !body.getPassword().isEmpty()) {
        user.setPassword(passwordEncoder.encode(body.getPassword()));
      }
      User saved = userRepository.save(user);
      return ResponseEntity.ok(saved);
    } catch (Exception e) {
      logger.error("Error updating user", e);
      return ResponseEntity.badRequest().body("Erreur lors de la mise à jour de l'utilisateur");
    }
  }

  @PreAuthorize("hasAuthority('DELETE_USER')")
  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    if (!userRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    userRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/revendeurs")
  public ResponseEntity<List<User>> getRevendeurs() {
    try {
      return ResponseEntity.ok(userRepository.findUsersByTypeUser(UserTypeConstant.REVENDEUR));
    } catch (Exception e) {
      logger.error("Error fetching revendeurs", e);
      return ResponseEntity.ok(Collections.emptyList());
    }
  }

  @GetMapping("/revendeurs/{id}")
  public ResponseEntity<User> getRevendeurById(@PathVariable Long id) {
    return userRepository.findById(id).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/users/{id}/historique")
  public ResponseEntity<List<Historique>> getUserHistorique(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(historiqueRepository.historiqueByIdConnected(id));
    } catch (Exception e) {
      logger.error("Error fetching user historique", e);
      return ResponseEntity.ok(Collections.emptyList());
    }
  }
}
