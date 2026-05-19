package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.ModemAccess;

public interface ModemAccessRepository extends JpaRepository<ModemAccess, Long> {

  ModemAccess findFirstModemAccessByStatusAndModelModem(Boolean Status, String modelModem);

  ModemAccess findFirstModemAccessByStatus(Boolean Status);

  ModemAccess findModemAccessByEmailAndModelModem(String statut, String modelModem);

  ModemAccess findOneModemAccessByEmail(String email);
 Long  countByStatusAndModelModem(Boolean Status, String modelModem);

  @Query("SELECT m FROM ModemAccess m WHERE email IN :listLogin ")

  List<ModemAccess> findListModemAccessByEmail(List<String> listLogin);
}
