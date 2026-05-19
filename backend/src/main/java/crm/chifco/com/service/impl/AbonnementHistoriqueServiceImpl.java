package crm.chifco.com.service.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.DemandeAbonnementHistory;
import crm.chifco.com.model.OperationAbonnement;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.DemandeAbonnementHistoryRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.service.AbonnementHistoriqueService;
import crm.chifco.com.templateclasse.DemandeAbonnementInterface;
import crm.chifco.com.utils.DBEtatTT;
import crm.chifco.com.utils.NomStatutChifco;

@Service("AbonnementHistoriqueService")
public class AbonnementHistoriqueServiceImpl implements AbonnementHistoriqueService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  @Autowired
  private DemandeAbonnementRepository demandeAbonnementRepository;
  @Autowired
  DemandeAbonnementHistoryRepository demandeAbonnementHistoryRepository;

  public Page<DemandeAbonnementHistory> findPaginatedAbonnementHistorique(int pageNo,
      int pageSize) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.demandeAbonnementHistoryRepository.findAll(pageable);
  }

  public List<DemandeAbonnementHistory> findDemandeAbonnementHistory() {
    return this.demandeAbonnementHistoryRepository.findAll();
  }

  public List<DemandeAbonnementHistory> findDemandeAbonnementHistoryByCin(String cin) {
    return this.demandeAbonnementHistoryRepository
        .findDemandeAbonnementHistoryByCinOrderByCreatedDateAsc(cin);
  }

  public void insertNewHistory(DemandeAbonnement Abonnement, User createdBy) {
    String description = "";
    switch (Abonnement.getStatut().getNomStatut()) {
      case NomStatutChifco.DRAFT:
        description = "Création de la demande d’abonnement";
        break;
      case NomStatutChifco.SIGNED_DOC:
        description =
            "Le statut de demande passe de « Demande d’abonnement non signée » à « Demande d’abonnement signée »";
        break;
      case NomStatutChifco.CLIENT_INJOIGNABLE:
        description += "Le statut  de la demande d'abonnement  est passé à « Client injoignable».";
        break;
      case NomStatutChifco.WAIT_TT:
        switch (Abonnement.getEtatTT()) {
          case DBEtatTT.Enregister:
            description += "La demande d'abonnement est Enregistrée  sous la référence:"
                + Abonnement.getReferenceTT() + "";
            break;
          case DBEtatTT.Etude:
            description += "La demande d’abonnement est envoyée à l’Etude sous  la référence"
                + Abonnement.getReferenceTT();
            break;

          case DBEtatTT.ConfirmationOK:
            description +=
                "Le statut TT de la demande d'abonnement  est passé à « Confirmation Client OK ».";
            break;
          case DBEtatTT.ConfirmationAnnuler:
            description +=
                "Le statut TT de la demande d'abonnement   est passé à  « Confirmation Client Annulée ».";
            break;
          case DBEtatTT.ConstructionLigne:
            description +=
                "Le statut TT de la demande d'abonnement  est passé à « Construction Ligne ».";
            break;
          case DBEtatTT.Refused:
            description += "Le statut TT de la demande d'abonnement  est passé à « Annulée ».:"
                + Abonnement.getMotifRefus();
            break;
          case DBEtatTT.Cancled:
            description +=
                "La demande d'abonnement est annulée à la suite d'une demande de client.";
            break;
          case DBEtatTT.Instance:
            description += "Statut TT" + DBEtatTT.Instance + Abonnement.getMotifRefus();;
            break;
          case DBEtatTT.Clôturée:
            description += "Le statut TT de la demande d'abonnement  est passé à « Clôturée».";
            break;


        }
        break;
      case NomStatutChifco.INSTALLED:
        description =
            "Le statut de demande passe de « Envoyé à TT » à « Demande validée et installée par TT »";
        break;
      case NomStatutChifco.SAISIE_INFAISABLE:
        description += "La saisie de  demande d’abonnement est infaisable";

        break;
      case NomStatutChifco.ASSIGNED:
        description =
            "Affectation d’un modem  sous reference" + Abonnement.getModem().getNumSerie();
        break;
      case NomStatutChifco.VALID:
        description = "La demande d’abonnement est validée";
        break;
      case NomStatutChifco.ACTIVE:
        description = "La demande d’abonnement est Activé";
        break;
      case NomStatutChifco.REFUSED:
        description = "La demande d’abonnement est refusée par TT ";
        break;
      case NomStatutChifco.CANCELED:
        description = "La demande d’abonnement est annulée";
        break;
    }

    saveHistoryToDataBase(createdBy, Abonnement, description);

  }

  public void insertNewEditedHistory(User createdBy, DemandeAbonnement newAbonnement) {
    DemandeAbonnementInterface dbDemande =
        demandeAbonnementRepository.findDemandeAbonnementsByid(newAbonnement.getDemandeId());
    String description = "";
    LOGGER.info("ddd" + newAbonnement.getFirstName() + dbDemande.getFirst_name());
    if (dbDemande.getFirst_name() != null
        && !dbDemande.getFirst_name().equals(newAbonnement.getFirstName())) {
      description += "Une modification de Nom a été effectuée";

    }
    if (!dbDemande.getLast_name().equals(newAbonnement.getLastName())) {
      description += " Une modification de Prénom a été effectuée";
    }
    if (!dbDemande.getAdresse().equals(newAbonnement.getAdresse())) {
      description += " Une modification d'adresse a été effectuée";
    }
    if (dbDemande.getTel_fixe() != null
        && !dbDemande.getTel_fixe().equals(newAbonnement.getTelFixe())) {
      description += " Une modification de  Telfixe a été effectuée de " + dbDemande.getTel_fixe()
          + " à " + newAbonnement.getTelFixe();
    } else if (dbDemande.getTel_fixe() == null && newAbonnement.getTelFixe() != null) {
      description += "  Tel fixe a été Ajouté" + newAbonnement.getTelFixe();

    }
    if (dbDemande.getCin() != null && !dbDemande.getCin().equals(newAbonnement.getCin())) {
      description += " Une modification de  cin a été effectuée de " + dbDemande.getCin() + "à "
          + newAbonnement.getCin();
    }
    if (dbDemande.getTel_mobile() != null
        && !dbDemande.getTel_mobile().equals(newAbonnement.getTelMobile())) {
      description += " Une modification de  Telmobile a été effectuée";
    }

    if (dbDemande.getReferencett() != null
        && !dbDemande.getReferencett().equals(newAbonnement.getReferenceTT())) {
      description += " Une modification de  Référence TT a été effectuée";
    }
    if (dbDemande.getContrat_pdf() != null
        && !dbDemande.getContrat_pdf().equals(newAbonnement.getContratPdf())) {
      description += " Une modification du contrat a été effectuée ";
    }

    saveHistoryToDataBase(createdBy, newAbonnement, description);

  }

  @Override
  public void saveNewHistorique(User createdBy, Long demandeId, String comment) {
    // TODO Auto-generated method stub
    DemandeAbonnement dbDemande =
        demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);

    saveHistoryToDataBase(createdBy, dbDemande, comment);

  }

  @Override
  public void saveHistoryToDataBase(User createdBy, DemandeAbonnement newAbonnement,
      String description) {
    DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
    demandeAbonnementHistory.setAdresse(newAbonnement.getAdresse());
    demandeAbonnementHistory.setCin(newAbonnement.getCin());
    demandeAbonnementHistory.setContratpdf(newAbonnement.getContratPdf());
    demandeAbonnementHistory.setDescription(description);
    demandeAbonnementHistory.setFirstName(newAbonnement.getFirstName());
    demandeAbonnementHistory.setLastName(newAbonnement.getLastName());
    demandeAbonnementHistory.setCreatedBy(createdBy);
    if (!description.equals(""))
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
  }

  @Override
  public void updateCinHistory(String oldCin, String newCin) {
    demandeAbonnementHistoryRepository.updateClientHistoryByCin(oldCin, newCin);

  }

  @Override
  public void insertNewHistoryOperationAbon(OperationAbonnement operationAbonnement,
      User createdBy) {
    String description = "";
    String typeDemande = "";
    String type = operationAbonnement.getTypeDemande();
    if (type.equals("M")) {
      typeDemande = "migration";
    } else if (type.equals("T")) {
      typeDemande = "transfert";
    } else {
      typeDemande = "changement débit";
    }
    switch (operationAbonnement.getStatut().getNomStatut()) {
      case NomStatutChifco.SIGNED_DOC:
        description = "Création de la demande de " + typeDemande;
        break;
      case NomStatutChifco.WAIT_TT:
        switch (operationAbonnement.getEtatTT()) {
          case DBEtatTT.Enregister:
            description += "La demande de " + typeDemande + " est Enregistrée  sous la référence:"
                + operationAbonnement.getReferenceTT() + "";
            break;
          case DBEtatTT.Etude:
            description +=
                "La demande de " + typeDemande + " est envoyée à l’Etude sous  la référence"
                    + operationAbonnement.getReferenceTT();
            break;

          case DBEtatTT.ConfirmationOK:
            description += "Le statut TT de la demande de " + typeDemande
                + "  est passé à « Confirmation Client OK ».";
            break;
          case DBEtatTT.ConfirmationAnnuler:
            description += "Le statut TT de la demande de " + typeDemande
                + "  est passé à  « Confirmation Client Annulée ».";
            break;
          case DBEtatTT.ConstructionLigne:
            description += "Le statut TT de la demande de " + typeDemande
                + "  est passé à « Construction Ligne ».";
            break;
          case DBEtatTT.Refused:
            description += "Le statut TT de la demande de " + typeDemande
                + "  est passé à « Annulée ».:" + operationAbonnement.getMotifRefus();
            break;
          case DBEtatTT.Cancled:
            description +=
                "La demande d'abonnement est annulée à la suite d'une demande de client.";
            break;
          case DBEtatTT.Instance:
            description += "Statut TT" + DBEtatTT.Instance + operationAbonnement.getMotifRefus();;
            break;
          case DBEtatTT.Clôturée:
            description +=
                "Le statut TT de la demande de " + typeDemande + " est passé à « Clôturée».";
            break;
        }
        break;
      case NomStatutChifco.INSTALLED:
        description = "Le statut de demande de " + typeDemande
            + " passe de « Envoyé à TT » à « Demande validée et installée par TT »";
        break;
      case NomStatutChifco.ASSIGNED:
        description = "Affectation d’un modem  sous numéro série "
            + operationAbonnement.getModem().getNumSerie();
        break;
      case NomStatutChifco.VALID:
        description = "La demande de " + typeDemande + " est validée sous référence "
            + operationAbonnement.getReferenceChifco();
        break;
      case NomStatutChifco.ACTIVE:
        description = "La demande de " + typeDemande + "  est Activé sous référence "
            + operationAbonnement.getReferenceChifco();
        break;
      case NomStatutChifco.REFUSED:
        description = "La demande de " + typeDemande + "  est refusée par TT ";
        break;
      case NomStatutChifco.CANCELED:
        description = "La demande de " + typeDemande + "  est annulée";
        break;

    }
    DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
    demandeAbonnementHistory.setAdresse(operationAbonnement.getAdresse());
    demandeAbonnementHistory.setCin(operationAbonnement.getCin());
    demandeAbonnementHistory.setContratpdf(operationAbonnement.getContratPdf());
    demandeAbonnementHistory.setDescription(description);
    demandeAbonnementHistory.setFirstName(operationAbonnement.getFirstName());
    demandeAbonnementHistory.setLastName(operationAbonnement.getLastName());
    demandeAbonnementHistory.setCreatedBy(createdBy);
    demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
  }


}
