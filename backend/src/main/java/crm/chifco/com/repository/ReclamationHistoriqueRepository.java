package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.ReclamationHistorique;
import crm.chifco.com.templateclasse.AllClientHistory;

public interface ReclamationHistoriqueRepository
    extends JpaRepository<ReclamationHistorique, Long> {
  @Query(
      value = "select us.first_name ,us.code_user ,rlh.created_by, rlh.created_date , rlh.description  from Reclamationhistoriques rlh Left join Users us on rlh.created_by = us.userid   where  rlh.ref_reclamation =  :reference order by created_date desc",
      nativeQuery = true)
  List<AllClientHistory> findReclamationhistoryByReference(String reference);

  @Query(
      value = "select us.first_name ,us.code_user ,rlh.created_by, rlh.created_date , rlh.description  from Reclamationhistoriques rlh Left join Users us on rlh.created_by = us.userid   where  rlh.ref_reclamation in :referencesReclamtion order by created_date desc",
      nativeQuery = true)
  List<AllClientHistory> findReclamationhistoryByReferences(List<String> referencesReclamtion);

}
