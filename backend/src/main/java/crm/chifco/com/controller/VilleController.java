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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.Ville;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.VilleRepository;
import crm.chifco.com.service.GouverneratsService;
import crm.chifco.com.service.VillesService;

@RestController
@RequestMapping("ville")
public class VilleController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  GouvernoratRepository gouvernoratsRepository;
  @Autowired
  GouverneratsService gouverneratsService;
  @Autowired
  VilleRepository villeRepository;
  @Autowired
  VillesService villeService;

  @GetMapping("/gouvernorats")
  public ResponseEntity<List<Gouvernorat>> getAllGouvernorats() {
    return ResponseEntity.ok(gouvernoratsRepository.findAll());
  }

  @GetMapping("/villes")
  public ResponseEntity<List<Ville>> getVilles(
      @RequestParam(required = false) Long gouvernoratId) {
    if (gouvernoratId != null) {
      return ResponseEntity.ok(
          villeRepository.findGouvernoratsByGouvernerat_GouvernoratId(gouvernoratId));
    }
    return ResponseEntity.ok(villeRepository.findAll());
  }

  @GetMapping("/gouvernorats/{id}")
  public ResponseEntity<Gouvernorat> getGouvernorat(@PathVariable Long id) {
    Gouvernorat g = gouvernoratsRepository.findGouverneratByGouvernoratId(id);
    return g != null ? ResponseEntity.ok(g) : ResponseEntity.notFound().build();
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @PostMapping("/gouvernorats")
  public ResponseEntity<?> createGouvernorat(@RequestBody Gouvernorat gouvernorat) {
    Gouvernorat existing =
        gouvernoratsRepository.findByGouvernoratName(gouvernorat.getGouvernoratName());
    if (existing != null) {
      return ResponseEntity.badRequest().body("Gouvernorat déjà existant");
    }
    return ResponseEntity.ok(gouvernoratsRepository.save(gouvernorat));
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @PutMapping("/gouvernorats/{id}")
  public ResponseEntity<?> updateGouvernorat(@PathVariable Long id,
      @RequestBody Gouvernorat body) {
    Gouvernorat existing = gouvernoratsRepository.findGouverneratByGouvernoratId(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    existing.setGouvernoratName(body.getGouvernoratName());
    return ResponseEntity.ok(gouvernoratsRepository.save(existing));
  }

  @GetMapping("/villes/{id}")
  public ResponseEntity<Ville> getVille(@PathVariable Long id) {
    Ville v = villeRepository.findVilleByVilleId(id);
    return v != null ? ResponseEntity.ok(v) : ResponseEntity.notFound().build();
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @PostMapping("/villes")
  public ResponseEntity<Ville> createVille(@RequestBody Ville ville) {
    return ResponseEntity.ok(villeRepository.save(ville));
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @PutMapping("/villes/{id}")
  public ResponseEntity<?> updateVille(@PathVariable Long id, @RequestBody Ville body) {
    Ville existing = villeRepository.findVilleByVilleId(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    existing.setVilleName(body.getVilleName());
    existing.setAbreviation(body.getAbreviation());
    return ResponseEntity.ok(villeRepository.save(existing));
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @DeleteMapping("/gouvernorats/{id}")
  public ResponseEntity<Void> deleteGouvernorat(@PathVariable Long id) {
    gouvernoratsRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @DeleteMapping("/villes/{id}")
  public ResponseEntity<Void> deleteVille(@PathVariable Long id) {
    villeRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }
}
