package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.ModemHistoryImportFile;

public interface ModemHistoryImportFileRepository
    extends JpaRepository<ModemHistoryImportFile, Long> {

}
