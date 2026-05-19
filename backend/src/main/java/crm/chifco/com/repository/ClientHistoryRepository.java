package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.model.ClientHistory;
import crm.chifco.com.templateclasse.AllClientHistory;

public interface ClientHistoryRepository extends JpaRepository<ClientHistory, Long> {

  public ClientHistory findEntriesByClienHistoriquId(Long clienHistoriquId);

  @Query(
      value = "select us.first_name ,us.code_user ,clh.created_by, clh.created_date , clh.description  from clientshistoriques clh Left join Users us on clh.created_by = us.userid   where  clh.cin =  :cin",
      nativeQuery = true)
  List<AllClientHistory> findClientHistoryByCin(String cin);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE  clsH SET clsH.cin=:newcin FROM  clientshistoriques clsH WHERE clsH.cin = :oldCin",
      nativeQuery = true)
  void updateClientHistoryByCin(String newcin, String oldCin);


}
