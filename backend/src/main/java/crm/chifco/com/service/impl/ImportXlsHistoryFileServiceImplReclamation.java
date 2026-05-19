package crm.chifco.com.service.impl;

import java.util.Date;
import java.util.Objects;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.ImportXlsHistoryFileReclamation;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ImportXlsHistoryFileReclamationRepository;
import crm.chifco.com.service.ImportXlsHistoryFileServiceReclamation;
import crm.chifco.com.utils.CrmUtils;

@Service("ImportXlsHistoryFileServiceReclamation")
public class ImportXlsHistoryFileServiceImplReclamation
    implements ImportXlsHistoryFileServiceReclamation {

  @Autowired
  ImportXlsHistoryFileReclamationRepository ImportXlsHistoryFileRepository;


  public Page<ImportXlsHistoryFileReclamation> getallImportXlsHistory(String filterrecherche,
      int pageNo, int pageSize) {
    Date datedebut = null;
    Date datefin = null;
    Long creePar = null;
    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);


      if (obj.keySet().contains("datedebut") && !Objects.equals(obj.getString("datedebut"), "")
          && obj.getString("datedebut") != null) {
        datedebut = CrmUtils.convertStringToDate(obj.getString("datedebut"));
      }
      if (obj.keySet().contains("datefin") && !Objects.equals(obj.getString("datefin"), "")
          && obj.getString("datefin") != null) {
        datefin = CrmUtils.convertStringToLocalDateTime(obj.getString("datefin"));
      }
      if (obj.keySet().contains("creepar") && !Objects.equals(obj.get("creepar"), "")
          && obj.getString("creepar") != null) {
        creePar = obj.getLong("creepar");
      }
    }
    Pageable pageable =
        PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

    return this.ImportXlsHistoryFileRepository.findAllReclamationHistory(creePar, datedebut,
        datefin, pageable);
  };

  public ImportXlsHistoryFileReclamation getImportXlsHistoryById(Long id) {
    return this.ImportXlsHistoryFileRepository.findByxlsHistoriqueFile(id);
  }

  public void insertNewFileImportXlsHistory(String errorRow, String successRow,
      String originFileName, String getlFilename, String HistoryFileXlsSize,
      ImportXlsHistoryFileReclamation HistoryFileXls, User user) {
    HistoryFileXls.setErrorRow(errorRow);
    HistoryFileXls.setSuccessRow(successRow);
    HistoryFileXls.setOrigineNameFile(originFileName);
    HistoryFileXls.setNameFile(getlFilename);
    HistoryFileXls.setTotalRow(HistoryFileXlsSize);
    HistoryFileXls.setUser(user);
    ImportXlsHistoryFileRepository.save(HistoryFileXls);

  }


}
