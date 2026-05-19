package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.model.DemandeAbonnementHistory;
import crm.chifco.com.templateclasse.AllClientHistory;

public interface DemandeAbonnementHistoryRepository
    extends JpaRepository<DemandeAbonnementHistory, Long> {
  List<DemandeAbonnementHistory> findDemandeAbonnementHistoryByCinOrderByCreatedDateAsc(String cin);

  @Query(
      value = "select us.first_name ,us.code_user ,abh.created_by, abh.created_date , abh.description  from demandesabonnementhistoriques abh join Users us on abh.created_by = us.userid   where  abh.cin =  :cin",
      nativeQuery = true)
  List<AllClientHistory> findDemandeAbonnementhistoryByCin(String cin);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE clsH SET clsH.cin=:newCin FROM  demandesabonnementhistoriques clsH WHERE clsH.cin = :oldCin",
      nativeQuery = true)
  void updateClientHistoryByCin(String oldCin, String newCin);

}
