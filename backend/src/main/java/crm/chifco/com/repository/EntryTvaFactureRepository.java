package crm.chifco.com.repository;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import crm.chifco.com.DTOclass.EntryTvaFactureDataDTO;
import crm.chifco.com.model.EntryTvaFacture;

@Repository
public interface EntryTvaFactureRepository extends JpaRepository<EntryTvaFacture, Long> {



  List<EntryTvaFacture> findEntryTvaFacturesByFactureFactureId(Long factureId);



  @Query("SELECT NEW crm.chifco.com.DTOclass.EntryTvaFactureDataDTO(" + "entry.entryTvaFactureId, "
      + "entry.tauxTva, " + "entry.base, " + "entry.montant) "
      + "FROM EntryTvaFacture entry WHERE (:factureId IS NULL OR entry.facture.factureId = :factureId)")
  List<EntryTvaFactureDataDTO> findEntrysTvaFacturesByFactureId(Long factureId);

}
