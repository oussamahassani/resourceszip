package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.UserHistory;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

  List<UserHistory> findAllByUserEditId(Long userEditId);

}
