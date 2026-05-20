package crm.chifco.com.controller;

import java.util.List;
import java.util.Optional;
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
import crm.chifco.com.model.Typepaiement;
import crm.chifco.com.service.TypePaiementService;

@RestController
@RequestMapping("typepaiement")
public class TypepaiementController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  TypePaiementService typePaiementService;

  @GetMapping("/alltypepaiements")
  public ResponseEntity<List<Typepaiement>> getAllTypesPaiement() {
    return ResponseEntity.ok(typePaiementService.getalltypepaiements());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Typepaiement> getById(@PathVariable Long id) {
    Optional<Typepaiement> tp = typePaiementService.gettypepaiement(id);
    return tp.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAuthority('ADD_PAYMENT_TYPE')")
  @PostMapping
  public ResponseEntity<?> create(@RequestBody Typepaiement typepaiement) {
    Typepaiement existing =
        typePaiementService.gettypepaiementbyref(typepaiement.getReferenceTypePaiement());
    if (existing != null) {
      return ResponseEntity.badRequest().body("Référence déjà existante");
    }
    typePaiementService.savetypepaiement(typepaiement);
    return ResponseEntity.ok(typepaiement);
  }

  @PreAuthorize("hasAuthority('WRITE_TYPEPAIEMENT')")
  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Typepaiement body) {
    Optional<Typepaiement> existing = typePaiementService.gettypepaiement(id);
    if (!existing.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    Typepaiement conflict =
        typePaiementService.gettypepaiementbyref(body.getReferenceTypePaiement());
    if (conflict != null && !conflict.getTypePaiementId().equals(id)) {
      return ResponseEntity.badRequest().body("Référence déjà utilisée");
    }
    Typepaiement tp = existing.get();
    tp.setReferenceTypePaiement(body.getReferenceTypePaiement());
    tp.setNomTypePaiement(body.getNomTypePaiement());
    tp.setNombreMoisTypePaiement(body.getNombreMoisTypePaiement());
    typePaiementService.savetypepaiement(tp);
    return ResponseEntity.ok(tp);
  }

  @PreAuthorize("hasAuthority('WRITE_TYPEPAIEMENT')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Optional<Typepaiement> existing = typePaiementService.gettypepaiement(id);
    if (!existing.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok().build();
  }
}
