package crm.chifco.com.controller;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.Statut;
import crm.chifco.com.repository.StatutRepository;
import crm.chifco.com.service.StatutService;

@RestController
@RequestMapping("statut")
public class StatutController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  StatutService statutService;

  @Autowired
  StatutRepository statutRepository;

  @GetMapping("/allstatus")
  public ResponseEntity<List<Statut>> getAllStatuts() {
    return ResponseEntity.ok(statutRepository.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Statut> getById(@PathVariable Long id) {
    Statut s = statutRepository.findStatutByStatutId(id);
    return s != null ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @PostMapping
  public ResponseEntity<?> create(@RequestBody Statut statut) {
    Statut existing = statutRepository.findStatutByNomStatut(statut.getNomStatut());
    if (existing != null) {
      return ResponseEntity.badRequest().body("Statut déjà existant: " + statut.getNomStatut());
    }
    return ResponseEntity.ok(statutRepository.save(statut));
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Statut body) {
    Statut existing = statutRepository.findStatutByStatutId(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    Statut nameConflict = statutRepository.findStatutByNomStatut(body.getNomStatut());
    if (nameConflict != null && !nameConflict.getStatutId().equals(id)) {
      return ResponseEntity.badRequest().body("Nom statut déjà utilisé");
    }
    existing.setNomStatut(body.getNomStatut());
    existing.setDesignation(body.getDesignation());
    existing.setCouleur(body.getCouleur());
    return ResponseEntity.ok(statutRepository.save(existing));
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    statutRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }
}
