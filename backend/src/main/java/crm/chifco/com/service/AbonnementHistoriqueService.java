package crm.chifco.com.service;

import java.util.List;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.DemandeAbonnementHistory;
import crm.chifco.com.model.OperationAbonnement;
import crm.chifco.com.model.User;

public interface AbonnementHistoriqueService {
  Page<DemandeAbonnementHistory> findPaginatedAbonnementHistorique(int pageNo, int pageSize);

  List<DemandeAbonnementHistory> findDemandeAbonnementHistory();

  List<DemandeAbonnementHistory> findDemandeAbonnementHistoryByCin(String cin);

  void insertNewHistory(DemandeAbonnement Abonnement, User createdBy);

  void insertNewEditedHistory(User user, DemandeAbonnement demandeAbonnementtoedit);

  void saveNewHistorique(User createdBy, Long demandeId, String comment);

  void updateCinHistory(String oldCin, String newCin);

  void saveHistoryToDataBase(User createdBy, DemandeAbonnement newAbonnement, String description);

  void insertNewHistoryOperationAbon(OperationAbonnement operationAbonnement, User createdBy);

}
