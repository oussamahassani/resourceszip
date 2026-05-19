package crm.chifco.com.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import crm.chifco.com.model.Zone;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

  Optional<Zone> findByCode(String code);

  Optional<Zone> findByNom(String nom);

  Page<Zone> findByActiveTrue(Pageable pageable);

  Page<Zone> findAll(Pageable pageable);

  Page<Zone> findByNomContainingIgnoreCaseOrCodeContainingIgnoreCase(String nom, String code,
      Pageable pageable);

  @Query("SELECT z FROM Zone z WHERE z.active = true")
  List<Zone> findAllActiveZones();

  boolean existsByCode(String code);

  boolean existsByNom(String nom);

  @Query("SELECT DISTINCT u.email FROM Zone z JOIN z.gouvernorats g JOIN z.utilisateurs u WHERE g.gouvernoratName = :gouvernoratName AND u.email IS NOT NULL AND u.email != ''")
  List<String> findUniqueEmailsByGouvernoratName(@Param("gouvernoratName") String gouvernoratName);

  @Query("SELECT DISTINCT z FROM Zone z JOIN z.utilisateurs u WHERE u.userid = :userId AND z.active = true")
  List<Zone> findZonesByTechnicianId(@Param("userId") Long userId);
}
