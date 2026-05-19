package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.ImportXlsHistory;

public interface ImportXlsHistoryRepository extends JpaRepository<ImportXlsHistory, Long> {

  public List<ImportXlsHistory> getImportXlsHistoryByIdfile(Long id);
}
