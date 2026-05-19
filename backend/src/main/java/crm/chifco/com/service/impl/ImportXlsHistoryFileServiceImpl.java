package crm.chifco.com.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.ImportXlsHistoryFile;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ImportXlsHistoryFileRepository;
import crm.chifco.com.service.ImportXlsHistoryFileService;

@Service("ImportXlsHistoryFileService")
public class ImportXlsHistoryFileServiceImpl implements ImportXlsHistoryFileService {

  @Autowired
  ImportXlsHistoryFileRepository ImportXlsHistoryFileRepository;


  public Page<ImportXlsHistoryFile> getallImportXlsHistory(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

    return this.ImportXlsHistoryFileRepository.findAll(pageable);
  };

  public ImportXlsHistoryFile getImportXlsHistoryById(Long id) {
    return this.ImportXlsHistoryFileRepository.findByxlsHistoriqueFile(id);
  }

  public void insertNewFileImportXlsHistory(String errorRow, String successRow,
      String originFileName, String getlFilename, String HistoryFileXlsSize,
      ImportXlsHistoryFile HistoryFileXls, User user) {
    HistoryFileXls.setErrorRow(errorRow);
    HistoryFileXls.setSuccessRow(successRow);
    HistoryFileXls.setOrigineNameFile(originFileName);
    HistoryFileXls.setNameFile(getlFilename);
    HistoryFileXls.setTotalRow(HistoryFileXlsSize);
    HistoryFileXls.setUser(user);
    ImportXlsHistoryFileRepository.save(HistoryFileXls);

  }
}
