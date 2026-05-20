package crm.chifco.com.controller.rest;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.DTOclass.DemandeAbbonmentDataDTO;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.DemandeAbonnementService;

@RestController
@RequestMapping("demandeabonnement")
public class DemandeAbonnementRestController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  DemandeAbonnementService demandeAbonnementService;

  @Autowired
  DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  UserRepository userRepository;

  private User getConnectedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      return userRepository.findUsersByEmail(authentication.getName());
    }
    return null;
  }

  @GetMapping
  public ResponseEntity<Page<DemandeAbbonmentDataDTO>> getAll(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String refChif,
      @RequestParam(required = false) String refTT,
      @RequestParam(required = false) String cin,
      @RequestParam(required = false) String prenom,
      @RequestParam(required = false) String nom,
      @RequestParam(required = false) Long tel,
      @RequestParam(required = false) Long villes,
      @RequestParam(required = false) Long gouvernorat,
      @RequestParam(required = false) Long professions,
      @RequestParam(required = false) Long categories,
      @RequestParam(required = false) Long produit,
      @RequestParam(required = false) Long statutListfiltre,
      @RequestParam(required = false) String statutTTListfiltre,
      @RequestParam(required = false) String datedebut,
      @RequestParam(required = false) String datefin,
      @RequestParam(required = false) String dateDebutModification,
      @RequestParam(required = false) String dateFinModification,
      @RequestParam(required = false) Long creePar,
      @RequestParam(required = false) Long affecterTo,
      @RequestParam(required = false) String datedebutMiseService,
      @RequestParam(required = false) String datefinMiseService,
      @RequestParam(required = false) String typeDabonnement,
      @RequestParam(defaultValue = "demandeId") String sort,
      @RequestParam(defaultValue = "desc") String sorttype) {
    try {
      User user = getConnectedUser();
      Long userId = user != null ? user.getUserid() : null;
      Long roleId = user != null && user.getRole() != null ? user.getRole().getRoleId() : null;

      Page<DemandeAbbonmentDataDTO> result =
          demandeAbonnementService.findPaginatedByDistributeurWithSort(
              page, size, userId, null,
              refChif, refTT, cin, prenom, nom, tel, villes, gouvernorat,
              professions, categories, produit, statutListfiltre, statutTTListfiltre,
              datedebut, datefin, dateDebutModification, dateFinModification,
              creePar, affecterTo, datedebutMiseService, datefinMiseService,
              typeDabonnement, sort, sorttype);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      logger.error("Error fetching demandes", e);
      return ResponseEntity.ok(Page.empty());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<DemandeAbonnement> getById(@PathVariable Long id) {
    DemandeAbonnement demande = demandeAbonnementService.getDemandeAbonnementBydemandeId(id);
    return demande != null ? ResponseEntity.ok(demande) : ResponseEntity.notFound().build();
  }

  @GetMapping("/non-signees")
  public ResponseEntity<Page<DemandeAbbonmentDataDTO>> getNonSignees(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "demandeId") String sort,
      @RequestParam(defaultValue = "desc") String sorttype) {
    try {
      User user = getConnectedUser();
      Long userId = user != null ? user.getUserid() : null;
      Long roleId = user != null && user.getRole() != null ? user.getRole().getRoleId() : null;
      Page<DemandeAbbonmentDataDTO> result =
          demandeAbonnementService.findPaginatedByDistributeurWithSort(
              page, size, userId, null,
              null, null, null, null, null, null, null, null,
              null, null, null, null, "EN_ATTENTE_SIGNATURE",
              null, null, null, null,
              null, null, null, null,
              null, sort, sorttype);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      logger.error("Error fetching non-signees", e);
      return ResponseEntity.ok(Page.empty());
    }
  }

  @GetMapping("/en-cours")
  public ResponseEntity<Page<DemandeAbbonmentDataDTO>> getEnCours(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    try {
      User user = getConnectedUser();
      Long userId = user != null ? user.getUserid() : null;
      Page<DemandeAbbonmentDataDTO> result =
          demandeAbonnementService.findPaginatedByDistributeurWithSort(
              page, size, userId, null,
              null, null, null, null, null, null, null, null,
              null, null, null, null, "EN_COURS",
              null, null, null, null,
              null, null, null, null,
              null, "demandeId", "desc");
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      logger.error("Error fetching en-cours", e);
      return ResponseEntity.ok(Page.empty());
    }
  }

  @GetMapping("/traites")
  public ResponseEntity<Page<DemandeAbbonmentDataDTO>> getTraites(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    try {
      User user = getConnectedUser();
      Long userId = user != null ? user.getUserid() : null;
      Page<DemandeAbbonmentDataDTO> result =
          demandeAbonnementService.findPaginatedByDistributeurWithSort(
              page, size, userId, null,
              null, null, null, null, null, null, null, null,
              null, null, null, null, "TRAITE",
              null, null, null, null,
              null, null, null, null,
              null, "demandeId", "desc");
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      logger.error("Error fetching traites", e);
      return ResponseEntity.ok(Page.empty());
    }
  }

  @GetMapping("/valides")
  public ResponseEntity<Page<DemandeAbbonmentDataDTO>> getValides(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    try {
      User user = getConnectedUser();
      Long userId = user != null ? user.getUserid() : null;
      Page<DemandeAbbonmentDataDTO> result =
          demandeAbonnementService.findPaginatedByDistributeurWithSort(
              page, size, userId, null,
              null, null, null, null, null, null, null, null,
              null, null, null, null, "VALID",
              null, null, null, null,
              null, null, null, null,
              null, "demandeId", "desc");
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      logger.error("Error fetching valides", e);
      return ResponseEntity.ok(Page.empty());
    }
  }

  @GetMapping("/recherche")
  public ResponseEntity<Page<DemandeAbbonmentDataDTO>> recherche(
      @RequestParam(required = false) String cin,
      @RequestParam(required = false) String nom,
      @RequestParam(required = false) String prenom,
      @RequestParam(required = false) String refChif,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    try {
      User user = getConnectedUser();
      Long userId = user != null ? user.getUserid() : null;
      Long roleId = user != null && user.getRole() != null ? user.getRole().getRoleId() : null;
      Page<DemandeAbbonmentDataDTO> result =
          demandeAbonnementService.findPaginatedByDistributeurWithSort(
              page, size, userId, null,
              refChif, null, cin, prenom, nom, null, null, null,
              null, null, null, null, null,
              null, null, null, null,
              null, null, null, null,
              null, "demandeId", "desc");
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      logger.error("Error searching demandes", e);
      return ResponseEntity.ok(Page.empty());
    }
  }

  @GetMapping("/verify-cin/{cin}")
  public ResponseEntity<List<DemandeAbonnement>> verifyCin(@PathVariable String cin) {
    List<DemandeAbonnement> result =
        demandeAbonnementService.findDemandeAbonnementsByCinAndStatusAvaibled(cin);
    return ResponseEntity.ok(result != null ? result : java.util.Collections.emptyList());
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody DemandeAbonnement demande) {
    try {
      DemandeAbonnement saved = demandeAbonnementRepository.save(demande);
      return ResponseEntity.ok(saved);
    } catch (Exception e) {
      logger.error("Error creating demande", e);
      return ResponseEntity.badRequest().body("Erreur lors de la création de la demande");
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DemandeAbonnement body) {
    DemandeAbonnement existing = demandeAbonnementService.getDemandeAbonnementBydemandeId(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    try {
      body.setDemandeId(id);
      DemandeAbonnement saved = demandeAbonnementRepository.save(body);
      return ResponseEntity.ok(saved);
    } catch (Exception e) {
      logger.error("Error updating demande", e);
      return ResponseEntity.badRequest().body("Erreur lors de la mise à jour de la demande");
    }
  }
}
