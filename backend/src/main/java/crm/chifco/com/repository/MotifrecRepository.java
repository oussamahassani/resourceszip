package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.Motifrec;

public interface MotifrecRepository extends JpaRepository<Motifrec, Long> {
  // List<Motifrec> findByServicetypeId(Long serviceTypeId);
  @Query("select mot from Motifrec mot where nomMotif = :nommotif ")
  Motifrec findByNomMotif(String nommotif);

  @Query("select mot from Motifrec mot where mot.servicetype.servicetypeId = :serviceTypeId And mot.category=:category ")
  List<Motifrec> findMotifsByServiceType(Long serviceTypeId, String category);

  @Query("select mot from Motifrec mot where nomMotif = :nomMotif And  mot.servicetype.servicetypeId = :servicetypeId And mot.category=:category ")
  Motifrec findByNomMotifAndServiceTypeAndCategory(String nomMotif, Long servicetypeId,
      String category);
}
