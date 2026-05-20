package crm.chifco.com.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.Historique;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.HistoriqueRepository;
import crm.chifco.com.repository.UserRepository;

@RestController
@RequestMapping("/historique")
public class HistoriqueController {

  @Autowired
  private UserRepository uRepo;

  @Autowired
  private HistoriqueRepository historiqueRepository;

  @GetMapping
  public ResponseEntity<List<Historique>> getHistorique() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.ok(new ArrayList<>());
    }
    String currentUser = authentication.getName();
    User user = uRepo.findUsersByEmail(currentUser);
    String role = user.getRole().getRoleName();
    Long idconnected = user.getUserid();

    List<Historique> historique;
    if (role.equals("ROLE_ADMINISTRATEUR") || role.equals("ROLE_G.STOCK")) {
      historique = historiqueRepository.findAll();
    } else {
      historique = historiqueRepository.historiqueByIdConnected(idconnected);
    }
    return ResponseEntity.ok(historique);
  }
}
