package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.ModemHistoryImport;

public interface ModemHistoryImportRepository
    extends JpaRepository<ModemHistoryImport, Long> {

}
