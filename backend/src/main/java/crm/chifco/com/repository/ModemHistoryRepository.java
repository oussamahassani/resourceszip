package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.ModemHistory;

public interface ModemHistoryRepository extends JpaRepository<ModemHistory, Long> {

  @Query(value = "select * from modem_history where modem_id = :modemId", nativeQuery = true)
  List<ModemHistory> findByModemId(Long modemId);
}
