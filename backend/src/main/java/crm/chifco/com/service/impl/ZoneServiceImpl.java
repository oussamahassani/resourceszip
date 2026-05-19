package crm.chifco.com.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Zone;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.repository.ZoneRepository;
import crm.chifco.com.service.ZoneService;
import crm.chifco.com.utils.GouvernoratMapping;

@Service
@Transactional
public class ZoneServiceImpl implements ZoneService {

  @Autowired
  private ZoneRepository zoneRepository;

  @Autowired
  private GouvernoratRepository gouvernoratRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public Zone saveZone(Zone zone) {
    return zoneRepository.save(zone);
  }

  @Override
  public Zone updateZone(Zone zone) {
    return zoneRepository.save(zone);
  }

  @Override
  public void deleteZone(Long zoneId) {
    Zone zone = getZoneById(zoneId);
    // zone.setActive(false);
    zoneRepository.delete(zone);
  }

  @Override
  public Zone getZoneById(Long zoneId) {
    return zoneRepository.findById(zoneId)
        .orElseThrow(() -> new RuntimeException("Zone not found with id: " + zoneId));
  }

  @Override
  public Zone getZoneByCode(String code) {
    return zoneRepository.findByCode(code).orElse(null);
  }

  @Override
  public Zone getZoneByNom(String nom) {
    return zoneRepository.findByNom(nom).orElse(null);
  }

  @Override
  public Page<Zone> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("createdDate").descending());
    return zoneRepository.findAll(pageable);
  }

  @Override
  public Page<Zone> findPaginatedWithSearch(int pageNo, int pageSize, String searchTerm) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("createdDate").descending());
    if (searchTerm != null && !searchTerm.isEmpty()) {
      return zoneRepository.findByNomContainingIgnoreCaseOrCodeContainingIgnoreCase(searchTerm,
          searchTerm, pageable);
    }
    return zoneRepository.findByActiveTrue(pageable);
  }

  @Override
  public List<Zone> getAllActiveZones() {
    return zoneRepository.findAllActiveZones();
  }

  @Override
  @Transactional
  public Zone addGouvernoratsToZone(Long zoneId, List<Long> gouvernoratIds) {
    Zone zone = getZoneById(zoneId);

    for (Long id : gouvernoratIds) {
      Gouvernorat gouvernorat = gouvernoratRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Gouvernorat not found with id: " + id));
      if (!zone.getGouvernorats().contains(gouvernorat)) {
        zone.getGouvernorats().add(gouvernorat);
      }
    }

    return zoneRepository.save(zone);
  }

  @Override
  @Transactional
  public Zone updateZoneGouvernorats(Long zoneId, List<Long> gouvernoratIds) {
    Zone zone = getZoneById(zoneId);
    zone.getGouvernorats().clear();
    if (gouvernoratIds != null && !gouvernoratIds.isEmpty()) {
      for (Long id : gouvernoratIds) {
        Gouvernorat gouvernorat = gouvernoratRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Gouvernorat not found with id: " + id));
        zone.getGouvernorats().add(gouvernorat);
      }
    }
    return zoneRepository.save(zone);
  }

  @Override
  public List<Gouvernorat> getGouvernoratsByZoneId(Long zoneId) {
    Zone zone = getZoneById(zoneId);
    return zone.getGouvernorats();
  }

  @Override
  @Transactional
  public Zone addUtilisateursToZone(Long zoneId, List<Long> userIds) {
    Zone zone = getZoneById(zoneId);

    for (Long id : userIds) {
      User user = userRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
      if (!zone.getUtilisateurs().contains(user)) {
        zone.getUtilisateurs().add(user);
      }
    }

    return zoneRepository.save(zone);
  }

  @Override
  @Transactional
  public Zone updateZoneUtilisateurs(Long zoneId, List<Long> userIds) {
    Zone zone = getZoneById(zoneId);

    zone.getUtilisateurs().clear();

    if (userIds != null && !userIds.isEmpty()) {
      for (Long id : userIds) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        zone.getUtilisateurs().add(user);
      }
    }

    return zoneRepository.save(zone);
  }

  @Override
  public List<User> getUtilisateursByZoneId(Long zoneId) {
    Zone zone = getZoneById(zoneId);
    return zone.getUtilisateurs();
  }

  @Override
  public boolean isZoneCodeUnique(String code, Long excludeZoneId) {
    Zone existing = getZoneByCode(code);
    if (existing == null)
      return true;
    if (excludeZoneId != null && existing.getZoneId().equals(excludeZoneId))
      return true;
    return false;
  }

  @Override
  public boolean isZoneNomUnique(String nom, Long excludeZoneId) {
    Zone existing = getZoneByNom(nom);
    if (existing == null)
      return true;
    if (excludeZoneId != null && existing.getZoneId().equals(excludeZoneId))
      return true;
    return false;
  }

  @Override
  @Transactional
  public void clearGouvernoratsFromZone(Long zoneId) {
    Zone zone = getZoneById(zoneId);
    zone.getGouvernorats().clear();
    zoneRepository.save(zone);
  }

  @Override
  @Transactional
  public void clearUtilisateursFromZone(Long zoneId) {
    Zone zone = getZoneById(zoneId);
    zone.getUtilisateurs().clear();
    zoneRepository.save(zone);
  }

  @Override
  public List<String> getUniqueEmailsByGouvernoratName(String gouvernoratName) {
    String dbGouvernoratName = GouvernoratMapping.getDatabaseGouvernorat(gouvernoratName);

    if (dbGouvernoratName == null) {
      System.out.println("Gouvernorat not found in mapping: " + gouvernoratName);
      return Collections.emptyList();
    }

    Gouvernorat gouvernorat = gouvernoratRepository.findByGouvernoratName(dbGouvernoratName);
    if (gouvernorat == null) {
      System.out.println("Gouvernorat not found in database: " + dbGouvernoratName);
      return Collections.emptyList();
    }

    return zoneRepository.findUniqueEmailsByGouvernoratName(dbGouvernoratName);
  }

  @Override
  public List<Zone> getZonesByTechnicianId(Long technicianId) {
    return zoneRepository.findZonesByTechnicianId(technicianId);
  }

  @Override
  public List<String> getGouvernoratsByTechnicianId(Long technicianId) {
    List<Zone> technicianZones = getZonesByTechnicianId(technicianId);
    Set<String> gouvernorats = new HashSet<>();

    for (Zone zone : technicianZones) {
      for (Gouvernorat gouv : zone.getGouvernorats()) {
        gouvernorats.add(gouv.getGouvernoratName());
      }
    }

    return new ArrayList<>(gouvernorats);
  }

  @Override
  public Map<User, List<String>> getAllTechniciansWithGouvernorats(List<User> technicians) {
    Map<User, List<String>> result = new HashMap<>();

    for (User technician : technicians) {
      List<String> gouvernorats = getGouvernoratsByTechnicianId(technician.getUserid());
      if (!gouvernorats.isEmpty()) {
        result.put(technician, gouvernorats);
      } else {
        System.out.println("Technician " + technician.getEmail() + " has no gouvernorats assigned");
      }
    }

    System.out.println("Found " + result.size() + " technicians with gouvernorats");
    return result;
  }
}
