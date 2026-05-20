package crm.chifco.com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import crm.chifco.com.model.Zone;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.repository.ZoneRepository;
import crm.chifco.com.service.ZoneService;

@RestController
@RequestMapping("zone")
public class ZoneController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  ZoneRepository zoneRepository;

  @Autowired
  ZoneService zoneService;

  @Autowired
  GouvernoratRepository gouvernoratRepository;

  @Autowired
  UserRepository userRepository;

  @GetMapping("/allzones")
  public ResponseEntity<List<Zone>> getAllZones() {
    return ResponseEntity.ok(zoneRepository.findAll());
  }

  @GetMapping
  public ResponseEntity<List<Zone>> getActiveZones() {
    return ResponseEntity.ok(zoneRepository.findAllActiveZones());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Zone> getById(@PathVariable Long id) {
    return zoneRepository.findById(id).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAuthority('ADD_ZONE')")
  @PostMapping
  public ResponseEntity<Zone> create(@RequestBody Zone zone) {
    return ResponseEntity.ok(zoneRepository.save(zone));
  }

  @PreAuthorize("hasAuthority('ADD_ZONE')")
  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Zone body) {
    return zoneRepository.findById(id).map(existing -> {
      body.setZoneId(id);
      return ResponseEntity.ok(zoneRepository.save(body));
    }).orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAuthority('ADD_ZONE')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    zoneRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/gouvernorats")
  public ResponseEntity<List<Gouvernorat>> getAllGouvernorats() {
    return ResponseEntity.ok(gouvernoratRepository.findAll());
  }

  @GetMapping("/byGouvernorat/{gouvernoratId}")
  public ResponseEntity<List<Zone>> getByGouvernorat(@PathVariable Long gouvernoratId) {
    List<Zone> zones = zoneRepository.findAllActiveZones();
    List<Zone> filtered = new java.util.ArrayList<>();
    for (Zone z : zones) {
      List<Gouvernorat> govs = z.getGouvernorats();
      if (govs != null) {
        for (Gouvernorat g : govs) {
          if (gouvernoratId.equals(g.getGouvernoratId())) {
            filtered.add(z);
            break;
          }
        }
      }
    }
    return ResponseEntity.ok(filtered);
  }

  @GetMapping("/uniqueEmails")
  public ResponseEntity<List<String>> getUniqueEmailsByGouvernorat(
      @RequestParam String gouvernoratName) {
    return ResponseEntity.ok(zoneRepository.findUniqueEmailsByGouvernoratName(gouvernoratName));
  }

  @GetMapping("/byTechnician/{userId}")
  public ResponseEntity<List<Zone>> getByTechnician(@PathVariable Long userId) {
    return ResponseEntity.ok(zoneRepository.findZonesByTechnicianId(userId));
  }

  @GetMapping("/stats")
  public ResponseEntity<Map<String, Object>> getStats() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("total", zoneRepository.count());
    stats.put("active", zoneRepository.findAllActiveZones().size());
    return ResponseEntity.ok(stats);
  }
}
