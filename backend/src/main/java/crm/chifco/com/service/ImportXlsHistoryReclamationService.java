package crm.chifco.com.service;

import java.util.List;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.ImportXlsHistoryReclamation;
import crm.chifco.com.model.Reclamation;

public interface ImportXlsHistoryReclamationService {
  Page<ImportXlsHistoryReclamation> getallImportXlsHistory(int pageNo, int pageSize);

  void insertNewImportXlsHistory(String status, String statusdemande, String Description,
      Reclamation reclamation, Long idfile);

  List<ImportXlsHistoryReclamation> getImportXlsHistoryById(Long id);



}
