package crm.chifco.com.service;

import java.util.List;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.model.User;
import crm.chifco.com.templateclasse.AllClientHistory;

public interface ReclamationHistoryService {

  void insertNewHistoryclaims(Reclamation reclamation, String description, User createdBy);

  void saveNewHistorique(User user, Long reclamationid, String comment);

  List<AllClientHistory> reclamationshis(String reference);

  List<AllClientHistory> getHistroriqueByreclamtionReferences(List<String> referencesReclamtion);



}
