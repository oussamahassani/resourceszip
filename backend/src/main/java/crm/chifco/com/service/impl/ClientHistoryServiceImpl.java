package crm.chifco.com.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.ClientHistory;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.OperationAbonnement;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.ClientHistoryRepository;
import crm.chifco.com.repository.DemandeAbonnementHistoryRepository;
import crm.chifco.com.service.ClientHistoryService;
import crm.chifco.com.templateclasse.AbonnementInterfaceHistory;
import crm.chifco.com.templateclasse.AllClientHistory;

@Service("clienthistoryService")
public class ClientHistoryServiceImpl implements ClientHistoryService {

  @Autowired
  ClientHistoryRepository ClientHistoryRepository;

  @Autowired
  DemandeAbonnementHistoryRepository DemandeAbonnementHistoryRepository;

  @Autowired
  private AbonnementRepository abonnementRepository;

  public List<AllClientHistory> findClientHistoryByCin(String cin) {
    List<AllClientHistory> clientHistory = this.ClientHistoryRepository.findClientHistoryByCin(cin);
    List<AllClientHistory> abonnementHistory =
        this.DemandeAbonnementHistoryRepository.findDemandeAbonnementhistoryByCin(cin);
    List<AllClientHistory> newList = new ArrayList<AllClientHistory>();
    newList = ListUtils.union(newList, abonnementHistory);
    newList = ListUtils.union(newList, clientHistory);
    newList.sort((el1, el2) -> el1.getCreated_date().compareTo(el2.getCreated_date()));
    return newList;
  }

  public void insertNewHistoryclient(DemandeAbonnement Abonnement, String description,
      User createdBy) {

    ClientHistory ClientHistory = new ClientHistory();
    ClientHistory.setCin(Abonnement.getCin());
    ClientHistory.setDescription(description);
    ClientHistory.setFirstName(Abonnement.getFirstName());
    ClientHistory.setLastName(Abonnement.getLastName());
    ClientHistory.setCreatedBy(createdBy);
    ClientHistoryRepository.save(ClientHistory);
  }

  public void insertNewEditedAbonnementHistory(User createdBy, Abonnement newAbonnement,
      Boolean modificationContratPdf) {
    AbonnementInterfaceHistory abonnement =
        abonnementRepository.findByCodeClient(newAbonnement.getCodeClient());

    String modification = "";
    String description = "";

    if (abonnement.getFirstName() != null
        && !abonnement.getFirstName().equals(newAbonnement.getFirstName())) {
      modification += "(Nom) ";
    }
    if (!abonnement.getLastName().equals(newAbonnement.getLastName())) {
      modification += "(Prénom) ";
    }
    if (!abonnement.getTelMobile().equals(newAbonnement.getTelMobile())) {
      modification += "(Telmobile) ";
    }

    if (!abonnement.getTelFixe().equals(newAbonnement.getTelFixe())) {
      modification +=
          "(TelFixe de " + abonnement.getTelFixe() + " a " + newAbonnement.getTelFixe() + ")";
    }
    if (!abonnement.getCin().equals(newAbonnement.getCin())) {
      modification += "(cin de " + abonnement.getCin() + " a " + newAbonnement.getCin() + ") ";
    }
    if (!abonnement.getTypePaiement().getReferenceTypePaiement()
        .equals(newAbonnement.getTypePaiement().getReferenceTypePaiement())) {
      modification += "(Type de paiement) ";
    }
    if (abonnement.getPhotoCin1() != null && newAbonnement.getPhotoCin1() != null
        && !abonnement.getPhotoCin1().equals(newAbonnement.getPhotoCin1())) {
      modification += "(Photo cin recto) ";
    } else if (abonnement.getPhotoCin1() != null && newAbonnement.getPhotoCin1() == null) {
      modification += "(Photo cin recto) ";
    }
    if (abonnement.getPhotoCin2() != null && newAbonnement.getPhotoCin2() != null
        && !abonnement.getPhotoCin2().equals(newAbonnement.getPhotoCin2())) {
      modification += "(Photo cin verso) ";
    } else if (abonnement.getPhotoCin2() != null && newAbonnement.getPhotoCin2() == null) {
      modification += "(Photo cin verso) ";
    }
    if (modificationContratPdf == true) {
      modification += "(Contrat) ";
    }
    if (!modification.equals("")) {
      description = "Modification " + modification + " a été effectuée avec succès.";
      saveHistoryClientToDataBase(createdBy, newAbonnement, abonnement, description);
    }


  }

  private void saveHistoryClientToDataBase(User createdBy, Abonnement newAbonnement,
      AbonnementInterfaceHistory abonnement, String description) {
    ClientHistory ClientHistory = new ClientHistory();
    ClientHistory.setCin(newAbonnement.getCin());
    ClientHistory.setFirstName(abonnement.getFirstName());
    ClientHistory.setLastName(abonnement.getLastName());
    ClientHistory.setTypePaiement(abonnement.getTypePaiement().getReferenceTypePaiement());
    ClientHistory.setPhotoCin1(abonnement.getPhotoCin1());
    ClientHistory.setPhotoCin2(abonnement.getPhotoCin2());
    ClientHistory.setCreatedBy(createdBy);
    ClientHistory.setDescription(description);
    ClientHistoryRepository.save(ClientHistory);

  }

  @Override
  public void saveNewHistorique(User user, Long demandeId, String comment) {
    // TODO Auto-generated method stub
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(demandeId);
    saveHistoryCommentToDataBase(user, abonnement, comment);

  }

  private void saveHistoryCommentToDataBase(User createdBy, Abonnement abonnement,
      String description) {
    ClientHistory ClientHistory = new ClientHistory();
    ClientHistory.setCin(abonnement.getCin());
    ClientHistory.setFirstName(abonnement.getFirstName());
    ClientHistory.setLastName(abonnement.getLastName());
    ClientHistory.setTypePaiement(abonnement.getTypePaiement().getReferenceTypePaiement());
    ClientHistory.setPhotoCin1(abonnement.getPhotoCin1());
    ClientHistory.setPhotoCin2(abonnement.getPhotoCin2());
    ClientHistory.setCreatedBy(createdBy);
    ClientHistory.setDescription(description);
    ClientHistoryRepository.save(ClientHistory);

  }

  @Override
  public void updateCinHistory(String newCin, String oldCin) {
    ClientHistoryRepository.updateClientHistoryByCin(newCin, oldCin);

  }

  @Override
  public void insertNewHistoryclient1(OperationAbonnement demandeMigration, String description,
      User createdBy) {
    ClientHistory ClientHistory = new ClientHistory();
    ClientHistory.setCin(demandeMigration.getCin());
    ClientHistory.setDescription(description);
    ClientHistory.setFirstName(demandeMigration.getFirstName());
    ClientHistory.setLastName(demandeMigration.getLastName());
    ClientHistory.setCreatedBy(createdBy);
    ClientHistoryRepository.save(ClientHistory);

  }

}
