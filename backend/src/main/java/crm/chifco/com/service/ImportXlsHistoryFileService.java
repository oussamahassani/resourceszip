package crm.chifco.com.service;

import org.springframework.data.domain.Page;
import crm.chifco.com.model.ImportXlsHistoryFile;
import crm.chifco.com.model.User;

public interface ImportXlsHistoryFileService {

  Page<ImportXlsHistoryFile> getallImportXlsHistory(int pageNo, int pageSize);

  ImportXlsHistoryFile getImportXlsHistoryById(Long id);

  void insertNewFileImportXlsHistory(String errorRow, String successRow, String getOriginalFilename,
      String getFilename, String HistoryFileXlsSize, ImportXlsHistoryFile HistoryFileXls,
      User user);
}
