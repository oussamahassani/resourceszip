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
import crm.chifco.com.model.Profession;
import crm.chifco.com.repository.ProfessionRepository;

@RestController
@RequestMapping("profession")
public class ProfessionController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  ProfessionRepository professionRepository;

  @GetMapping("/allprofessions")
  public ResponseEntity<List<Profession>> getAllProfessions() {
    return ResponseEntity.ok(professionRepository.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Profession> getById(@PathVariable Long id) {
    Profession p = professionRepository.findProfessionByProfessionId(id);
    return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
  }

  @PreAuthorize("hasAuthority('ADD_PROFESSION')")
  @PostMapping
  public ResponseEntity<?> create(@RequestBody Profession profession) {
    Profession existing = professionRepository.findProfessionByName(profession.getName());
    if (existing != null) {
      return ResponseEntity.badRequest().body("Profession déjà existante");
    }
    return ResponseEntity.ok(professionRepository.save(profession));
  }

  @PreAuthorize("hasAuthority('ADD_PROFESSION')")
  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Profession body) {
    Profession existing = professionRepository.findProfessionByProfessionId(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    existing.setName(body.getName());
    existing.setIsActive(body.getIsActive());
    return ResponseEntity.ok(professionRepository.save(existing));
  }

  @PreAuthorize("hasAuthority('ADD_PROFESSION')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    professionRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }
}
