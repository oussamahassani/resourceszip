package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.ImportXlsHistoryReclamation;

public interface ImportXlsHistoryReclamationRepository
    extends JpaRepository<ImportXlsHistoryReclamation, Long> {

  public List<ImportXlsHistoryReclamation> getImportXlsHistoryByIdfile(Long id);
}

