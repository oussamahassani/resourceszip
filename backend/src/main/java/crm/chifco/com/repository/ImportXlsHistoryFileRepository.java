package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.ImportXlsHistoryFile;

public interface ImportXlsHistoryFileRepository extends JpaRepository<ImportXlsHistoryFile, Long> {

  ImportXlsHistoryFile findByxlsHistoriqueFile(Long id);
}
