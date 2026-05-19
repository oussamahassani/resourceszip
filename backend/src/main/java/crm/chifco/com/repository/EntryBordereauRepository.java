package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.EntryBordereau;

public interface EntryBordereauRepository extends JpaRepository<EntryBordereau, Long> {

  @Query(
      value = "select * from entry_bordereau entrybd join bordereau bd   on  entrybd.bordereau_id = bd.bordereau_id where (bd.status ='versement confirmé' or bd.status ='versement en instance' or bd.status='En attente de justificatif' ) and entrybd.encaissement_id in :encaissementId   ",
      nativeQuery = true)

  List<EntryBordereau> findByEncaissement_Facture_encaissementId(List<String> encaissementId);

}
