package crm.chifco.com.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.ImportXlsHistoryReclamation;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.repository.ImportXlsHistoryReclamationRepository;
import crm.chifco.com.service.ImportXlsHistoryReclamationService;


@Service("ImportXlsHistoryReclamationService")
public class ImportXlsHistoryServiceImplReclamation implements ImportXlsHistoryReclamationService {
  @Autowired
  ImportXlsHistoryReclamationRepository ImportXlsHistoryReclamationRepository;

  public Page<ImportXlsHistoryReclamation> getallImportXlsHistory(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

    return this.ImportXlsHistoryReclamationRepository.findAll(pageable);
  };

  public List<ImportXlsHistoryReclamation> getImportXlsHistoryById(Long id)

  {

    return this.ImportXlsHistoryReclamationRepository.getImportXlsHistoryByIdfile(id);
  }

  public void insertNewImportXlsHistory(String statut, String referenceXlsvalue, String description,
      Reclamation reclamation, Long idfile) {
    ImportXlsHistoryReclamation ImportXlsHistoryReclamation = new ImportXlsHistoryReclamation();
    if (reclamation != null) {
      ImportXlsHistoryReclamation.setReferencett(reclamation.getReferencett());
      if (reclamation.getClient() != null) {
        ImportXlsHistoryReclamation
            .setReferenceChifco(reclamation.getClient().getReferenceClient());
      } else {
        ImportXlsHistoryReclamation.setReferenceChifco("");
      }
    } else {
      ImportXlsHistoryReclamation.setReferencett(referenceXlsvalue);
    }

    ImportXlsHistoryReclamation.setStatus(statut);
    ImportXlsHistoryReclamation.setDescription(description);
    ImportXlsHistoryReclamation.setIdfile(idfile);

    ImportXlsHistoryReclamationRepository.save(ImportXlsHistoryReclamation);
  }
}
