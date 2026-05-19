package crm.chifco.com.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Zone;

public interface ZoneService {

  Zone saveZone(Zone zone);

  Zone updateZone(Zone zone);

  void deleteZone(Long zoneId);

  Zone getZoneById(Long zoneId);

  Zone getZoneByCode(String code);


  Zone getZoneByNom(String nom);

  Page<Zone> findPaginated(int pageNo, int pageSize);

  Page<Zone> findPaginatedWithSearch(int pageNo, int pageSize, String searchTerm);

  List<Zone> getAllActiveZones();

  Zone addGouvernoratsToZone(Long zoneId, List<Long> gouvernoratIds);

  Zone updateZoneGouvernorats(Long zoneId, List<Long> gouvernoratIds);

  List<Gouvernorat> getGouvernoratsByZoneId(Long zoneId);

  Zone addUtilisateursToZone(Long zoneId, List<Long> userIds);

  Zone updateZoneUtilisateurs(Long zoneId, List<Long> userIds);

  List<User> getUtilisateursByZoneId(Long zoneId);

  boolean isZoneCodeUnique(String code, Long excludeZoneId);

  boolean isZoneNomUnique(String nom, Long excludeZoneId);

  void clearUtilisateursFromZone(Long zoneId);

  void clearGouvernoratsFromZone(Long zoneId);

  List<String> getUniqueEmailsByGouvernoratName(String gouvernoratName);

  List<String> getGouvernoratsByTechnicianId(Long technicianId);

  List<Zone> getZonesByTechnicianId(Long technicianId);

  Map<User, List<String>> getAllTechniciansWithGouvernorats(List<User> technicians);
}
