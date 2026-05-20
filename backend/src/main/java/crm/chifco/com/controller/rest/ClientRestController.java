package crm.chifco.com.controller.rest;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.service.AbonnementService;

@RestController
@RequestMapping("client")
public class ClientRestController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  AbonnementService abonnementService;

  @Autowired
  AbonnementRepository abonnementRepository;

  @GetMapping("/allclients")
  public ResponseEntity<Page<Abonnement>> getAllClients(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName,
      @RequestParam(required = false) String cin,
      @RequestParam(required = false) String codeClient,
      @RequestParam(required = false) Long gouvernorat,
      @RequestParam(required = false) Long villes,
      @RequestParam(required = false) String sortvar,
      @RequestParam(required = false) String sorttype) {
    Page<Abonnement> result = abonnementService.findPaginatedwithfilter(
        page, size, firstName, lastName, cin, codeClient,
        null, null, villes, null, null, gouvernorat,
        null, null, null, null, null, null,
        null, null, null, null, null,
        sortvar != null ? sortvar : "clientid",
        sorttype != null ? sorttype : "desc",
        null, null, null, null);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Abonnement> getById(@PathVariable Long id) {
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(id);
    return abonnement != null ? ResponseEntity.ok(abonnement) : ResponseEntity.notFound().build();
  }

  @GetMapping("/active")
  public ResponseEntity<Page<Abonnement>> getActive(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    Page<Abonnement> result = abonnementService.findPaginatedwithfilter(
        page, size, null, null, null, null,
        null, null, null, null, null, null,
        null, null, null, null, null, null,
        null, null, "ACTIVE", null, null,
        "clientid", "desc", true, null, null, null);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/resilie")
  public ResponseEntity<Page<Abonnement>> getResilie(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    Page<Abonnement> result = abonnementService.findPaginatedwithfilter(
        page, size, null, null, null, null,
        null, null, null, null, null, null,
        null, null, null, null, null, null,
        null, null, "RESILIER", null, null,
        "clientid", "desc", null, null, null, null);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/non-connecte")
  public ResponseEntity<Page<Abonnement>> getNonConnecte(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    Page<Abonnement> result = abonnementService.findPaginatedwithfilter(
        page, size, null, null, null, null,
        null, null, null, null, null, null,
        null, null, null, null, null, null,
        null, null, null, null, null,
        "clientid", "desc", null, null, null, null);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/recouvrement")
  public ResponseEntity<Page<Abonnement>> getRecouvrement(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    Page<Abonnement> result = abonnementService.findPaginatedwithfilter(
        page, size, null, null, null, null,
        null, null, null, null, null, null,
        null, null, null, null, null, null,
        null, null, "UNPAID", null, null,
        "clientid", "desc", null, null, null, null);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/byCin/{cin}")
  public ResponseEntity<Abonnement> getByCin(@PathVariable String cin) {
    Abonnement abonnement = abonnementService.findAbonnementByCin(cin);
    return abonnement != null ? ResponseEntity.ok(abonnement) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody Abonnement abonnement) {
    try {
      Abonnement saved = abonnementRepository.save(abonnement);
      return ResponseEntity.ok(saved);
    } catch (Exception e) {
      logger.error("Error creating client", e);
      return ResponseEntity.badRequest().body("Erreur lors de la création du client");
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Abonnement body) {
    Abonnement existing = abonnementRepository.findAbonnementByClientid(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    try {
      body.setClientid(id);
      Abonnement saved = abonnementRepository.save(body);
      return ResponseEntity.ok(saved);
    } catch (Exception e) {
      logger.error("Error updating client", e);
      return ResponseEntity.badRequest().body("Erreur lors de la mise à jour du client");
    }
  }

  @GetMapping("/export")
  public ResponseEntity<List<Abonnement>> export(
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName,
      @RequestParam(required = false) String cin,
      @RequestParam(required = false) String codeClient,
      @RequestParam(required = false) Long gouvernorat,
      @RequestParam(required = false) Long villes) {
    Page<Abonnement> page = abonnementService.findPaginatedwithfilter(
        1, 10000, firstName, lastName, cin, codeClient,
        null, null, villes, null, null, gouvernorat,
        null, null, null, null, null, null,
        null, null, null, null, null,
        "clientid", "asc", null, null, null, null);
    return ResponseEntity.ok(page.getContent());
  }
}
