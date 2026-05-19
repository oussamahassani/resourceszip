package crm.chifco.com.service;

import org.springframework.data.domain.Page;
import crm.chifco.com.model.ImportXlsHistoryFileReclamation;
import crm.chifco.com.model.User;

public interface ImportXlsHistoryFileServiceReclamation {
  Page<ImportXlsHistoryFileReclamation> getallImportXlsHistory(String filterrecherche, int pageNo,
      int pageSize);

  ImportXlsHistoryFileReclamation getImportXlsHistoryById(Long id);

  void insertNewFileImportXlsHistory(String errorRow, String successRow, String getOriginalFilename,
      String getFilename, String HistoryFileXlsSize, ImportXlsHistoryFileReclamation HistoryFileXls,
      User user);
}
