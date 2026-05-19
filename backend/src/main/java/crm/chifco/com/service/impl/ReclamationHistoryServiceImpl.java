package crm.chifco.com.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.model.ReclamationHistorique;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ReclamationHistoriqueRepository;
import crm.chifco.com.repository.ReclamationRepository;
import crm.chifco.com.service.ReclamationHistoryService;
import crm.chifco.com.templateclasse.AllClientHistory;

@Service("ReclamationHistoryService")
public class ReclamationHistoryServiceImpl implements ReclamationHistoryService {

  @Autowired
  ReclamationHistoriqueRepository reclamationHistoriqueRepository;
  @Autowired
  ReclamationRepository reclamationRepository;

  @Override
  public void insertNewHistoryclaims(Reclamation reclamation, String description, User createdBy) {
    ReclamationHistorique ReclaHistory = new ReclamationHistorique();
    ReclaHistory.setRef_reclamation(reclamation.getRef_reclamation());
    ReclaHistory.setDescription(description);
    if (reclamation.getCategory().equals("Client")) {
      ReclaHistory.setFirstName(reclamation.getClient().getFirstName());
      ReclaHistory.setLastName(reclamation.getClient().getLastName());
    } else {
      ReclaHistory.setFirstName(reclamation.getUser().getFirstName());
      ReclaHistory.setLastName(reclamation.getUser().getLastName());
    }
    ReclaHistory.setCreatedBy(createdBy);
    reclamationHistoriqueRepository.save(ReclaHistory);

  }

  @Override
  public void saveNewHistorique(User user, Long reclamationid, String comment) {
    Reclamation reclamation = reclamationRepository.findById(reclamationid).get();
    this.saveHistoryCommentToDataBase(user, reclamation, comment);
  }


  private void saveHistoryCommentToDataBase(User createdBy, Reclamation reclamation,
      String description) {
    ReclamationHistorique ReclaHistory = new ReclamationHistorique();
    ReclaHistory.setRef_reclamation(reclamation.getRef_reclamation());
    if (reclamation.getCategory().equals("Client")) {
      ReclaHistory.setFirstName(reclamation.getClient().getFirstName());
      ReclaHistory.setLastName(reclamation.getClient().getLastName());
    } else {
      ReclaHistory.setFirstName(reclamation.getUser().getFirstName());
      ReclaHistory.setLastName(reclamation.getUser().getLastName());
    }
    ReclaHistory.setCreatedBy(createdBy);
    ReclaHistory.setDescription(description);
    reclamationHistoriqueRepository.save(ReclaHistory);

  }

  @Override
  public List<AllClientHistory> reclamationshis(String reference) {
    List<AllClientHistory> newList =
        this.reclamationHistoriqueRepository.findReclamationhistoryByReference(reference);

    return newList;
  }

  @Override
  public List<AllClientHistory> getHistroriqueByreclamtionReferences(
      List<String> referencesReclamtion) {
    // TODO Auto-generated method stub
    return this.reclamationHistoriqueRepository
        .findReclamationhistoryByReferences(referencesReclamtion);
  }

}
