package crm.chifco.com.service;

import java.util.List;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.OperationAbonnement;
import crm.chifco.com.model.User;
import crm.chifco.com.templateclasse.AllClientHistory;

public interface ClientHistoryService {


  List<AllClientHistory> findClientHistoryByCin(String cin);

  void insertNewHistoryclient(DemandeAbonnement Abonnement, String description, User createdBy);

  void insertNewEditedAbonnementHistory(User createdBy, Abonnement newAbonnement,
      Boolean modificationContratPdf);

  void saveNewHistorique(User user, Long demandeId, String comment);

  void updateCinHistory(String newCin, String oldCin);

  void insertNewHistoryclient1(OperationAbonnement demandeMigration, String description,
      User createdBy);

}
