package crm.chifco.com.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.ImportXlsHistory;
import crm.chifco.com.model.OperationAbonnement;
import crm.chifco.com.repository.ImportXlsHistoryRepository;
import crm.chifco.com.service.ImportXlsHistoryService;
import crm.chifco.com.utils.DBEtatTT;
import crm.chifco.com.utils.StatutTTConstants;

@Service("ImportXlsHistoryService")
public class ImportXlsHistoryServiceImpl implements ImportXlsHistoryService {

  @Autowired
  ImportXlsHistoryRepository ImportXlsHistoryRepository;

  public Page<ImportXlsHistory> getallImportXlsHistory(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

    return this.ImportXlsHistoryRepository.findAll(pageable);
  };

  public List<ImportXlsHistory> getImportXlsHistoryById(Long id)

  {

    return this.ImportXlsHistoryRepository.getImportXlsHistoryByIdfile(id);
  }

  public void insertNewImportXlsHistory(String statut, String referenceXlsvalue, String description,
      DemandeAbonnement Abonnement, Long idfile) {
    ImportXlsHistory importXlsHistory = new ImportXlsHistory();
    if (Abonnement != null) {
      importXlsHistory.setReferencett(Abonnement.getReferenceTT());
      importXlsHistory.setReferenceChifco(Abonnement.getReferenceChifco());
    } else {
      importXlsHistory.setReferencett(referenceXlsvalue);
    }

    importXlsHistory.setStatus(statut);
    importXlsHistory.setDescription(description);
    importXlsHistory.setIdfile(idfile);

    ImportXlsHistoryRepository.save(importXlsHistory);
  }

  public String insertNewRowImportXlsHistoryStatutSendTT(String fichXlsEtat,
      String AbonnementEtatCurentValue) {
    String description = null;
    if (Arrays.stream(StatutTTConstants.StatutTTCloturee).anyMatch(fichXlsEtat::equals)
        && (AbonnementEtatCurentValue.equals(DBEtatTT.Etude)
            || AbonnementEtatCurentValue.equals(DBEtatTT.Enregister)
            || AbonnementEtatCurentValue.equals(DBEtatTT.ConstructionLigne)
            || AbonnementEtatCurentValue.equals(DBEtatTT.ConstructionLigne)
            || AbonnementEtatCurentValue.equals(DBEtatTT.Instance))) {

      description = " Il faut passer par la phase de  Mise en service ";
    } else {
      boolean isEtude =
          Arrays.stream(StatutTTConstants.statutttetude).anyMatch(fichXlsEtat::equals);
      boolean isMiseEnService =
          Arrays.stream(StatutTTConstants.StatutTTMiseenservice).anyMatch(fichXlsEtat::equals);
      boolean isWaitingConfirmationClient =
          Arrays.stream(StatutTTConstants.StatutTTConfirmationClient).anyMatch(fichXlsEtat::equals);
      if ((isEtude || isMiseEnService || isWaitingConfirmationClient || isConstruction(fichXlsEtat)
          || isConfirmationClientOk(fichXlsEtat) || isConfirmationAnnuler(fichXlsEtat)
          || isEnregisterTT(fichXlsEtat) || isInstanceTT(fichXlsEtat)
          || isReservationTT(fichXlsEtat) || isAnnulerTT(fichXlsEtat) || isTTcancled(fichXlsEtat))
          && (AbonnementEtatCurentValue.equals(DBEtatTT.Clôturée))) {
        description = "La demande d'abonnement est actuellement Clôturée";
      } else if ((isEtude || isMiseEnService || isWaitingConfirmationClient
          || isConstruction(fichXlsEtat) || isConfirmationClientOk(fichXlsEtat)
          || isConfirmationAnnuler(fichXlsEtat) || isEnregisterTT(fichXlsEtat)
          || isInstanceTT(fichXlsEtat) || isReservationTT(fichXlsEtat) || isAnnulerTT(fichXlsEtat)
          || isTTcancled(fichXlsEtat))
          && (AbonnementEtatCurentValue.equals(DBEtatTT.Mise_en_service))) {
        description = "La demande  d'abonnement est actuellement en Mise en service";
      } else if ((isEtude || isEnregisterTT(fichXlsEtat))
          && (AbonnementEtatCurentValue.equals(DBEtatTT.ConfirmationClient))) {
        description = "La demande d'abonnement est actuellement en Confirmation client";
      } else if ((isEtude || isEnregisterTT(fichXlsEtat))
          && (AbonnementEtatCurentValue.equals(DBEtatTT.ConfirmationAnnuler))) {
        description = "La demande d'abonnement est actuellement en Confirmation client";
      } else if ((isEtude || isEnregisterTT(fichXlsEtat) || isConstruction(fichXlsEtat))
          && (AbonnementEtatCurentValue.equals(DBEtatTT.ConstructionLigne))) {
        description = "La demande  d'abonnement est actuellement en Construction Ligne";
      } else if (fichXlsEtat.equals(DBEtatTT.Enregister)
          && AbonnementEtatCurentValue.equals(DBEtatTT.Etude)) {
        description = "La demande est actuellement en Etude";

      } else if ((fichXlsEtat.equals(DBEtatTT.Enregister) || fichXlsEtat.equals(DBEtatTT.Etude))
          && AbonnementEtatCurentValue.equals(DBEtatTT.Instance)) {
        description = "La demande  d'abonnement  est actuellement en Instance";

      } else if ((isEtude || isMiseEnService || isWaitingConfirmationClient
          || isConstruction(fichXlsEtat) || isConfirmationClientOk(fichXlsEtat)
          || isConfirmationAnnuler(fichXlsEtat) || isEnregisterTT(fichXlsEtat)
          || isInstanceTT(fichXlsEtat) || isReservationTT(fichXlsEtat) || isAnnulerTT(fichXlsEtat)
          || isTTcancled(fichXlsEtat))
          && (AbonnementEtatCurentValue.equals(DBEtatTT.Cancled)
              || AbonnementEtatCurentValue.equals(DBEtatTT.Refused))) {
        description = "La demande d'abonnement est Annulée";
      } else if ((isEnregisterTT(fichXlsEtat)
          && AbonnementEtatCurentValue.equals(DBEtatTT.Enregister))) {
        description = "La demande d'abonnement est actuellement Enregistrée ";
      } else if ((isMiseEnService && AbonnementEtatCurentValue.equals(DBEtatTT.Mise_en_service))) {
        description = "La demande d'abonnement est actuellement en mise en service ";
      }

    }
    return description;
  }

  public String insertNewRowImportXlsHistoryStatutSendTT2(String fichXlsEtat,
      String OpérationEtatCurentValue) {
    String description = null;
    boolean isMiseEnService =
        Arrays.stream(StatutTTConstants.StatutTTMiseenservice).anyMatch(fichXlsEtat::equals);
    if ((isEnregisterTT(fichXlsEtat) && OpérationEtatCurentValue.equals(DBEtatTT.Enregister))) {
      description = "La demande est actuellement Enregistrée ";
    } else if ((isMiseEnService) && (OpérationEtatCurentValue.equals(DBEtatTT.Mise_en_service))) {
      description = "La demande  est actuellement en Mise en service";
    } else if ((fichXlsEtat.equals(DBEtatTT.Instance)
        && OpérationEtatCurentValue.equals(DBEtatTT.Instance))) {
      description = "La demande est actuellement en instance";
    } else if ((isMiseEnService && OpérationEtatCurentValue.equals(DBEtatTT.Mise_en_service))) {
      description = "La demande d'abonnement est actuellement en mise en service ";
    } else if ((fichXlsEtat.equals(DBEtatTT.Instance_Commercial)
        && OpérationEtatCurentValue.equals(DBEtatTT.Instance_Commercial))) {
      description = "La demande est actuellement en Instance_Commercial";
    } else if ((fichXlsEtat.equals(DBEtatTT.Raccordement)
        && OpérationEtatCurentValue.equals(DBEtatTT.Raccordement))) {
      description = "La demande est actuellement en raccordement";
    } else if ((fichXlsEtat.equals(DBEtatTT.Attente_Construction)
        && OpérationEtatCurentValue.equals(DBEtatTT.Attente_Construction))) {
      description = "La demande est actuellement en Attente Construction";
    } else if ((fichXlsEtat.equals(DBEtatTT.Execution)
        && OpérationEtatCurentValue.equals(DBEtatTT.Execution))) {
      description = "La demande est actuellement en execution";
    } else if ((fichXlsEtat.equals(DBEtatTT.Migration)
        && OpérationEtatCurentValue.equals(DBEtatTT.Migration))) {
      description = "La demande est actuellement en migration";
    } else if ((fichXlsEtat.equals(DBEtatTT.Resilation)
        && OpérationEtatCurentValue.equals(DBEtatTT.Resilation))) {
      description = "La demande est actuellement en résiliation";
    }

    return description;
  }

  private boolean isTTcancled(String fichXlsEtat) {
    return Arrays.stream(StatutTTConstants.StatutTTcancled).anyMatch(fichXlsEtat::equals);
  }

  private boolean isAnnulerTT(String fichXlsEtat) {
    return Arrays.stream(StatutTTConstants.StatutTTAnnulee).anyMatch(fichXlsEtat::equals);
  }

  private boolean isReservationTT(String fichXlsEtat) {
    return Arrays.stream(StatutTTConstants.StatutTTReservation).anyMatch(fichXlsEtat::equals);
  }

  private boolean isInstanceTT(String fichXlsEtat) {
    return Arrays.stream(StatutTTConstants.StatutTTInstance).anyMatch(fichXlsEtat::equals);
  }

  private boolean isEnregisterTT(String fichXlsEtat) {
    return Arrays.stream(StatutTTConstants.statuttEnregister).anyMatch(fichXlsEtat::equals);
  }

  private boolean isConfirmationAnnuler(String fichXlsEtat) {
    return Arrays.stream(StatutTTConstants.StatutTTConfirmationClientAnnulerNety)
        .anyMatch(fichXlsEtat::equals);
  }

  private boolean isConfirmationClientOk(String fichXlsEtat) {
    return Arrays.stream(StatutTTConstants.StatutTTConfirmationClientOKNety)
        .anyMatch(fichXlsEtat::equals);
  }

  private boolean isConstruction(String fichXlsEtat) {
    return Arrays.stream(StatutTTConstants.StatutTTConstruction).anyMatch(fichXlsEtat::equals);
  }

  @Override
  public void insertNewImportXlsHistory2(String statut, String referenceXlsvalue,
      String description, OperationAbonnement demandeMigration, Long idfile) {

    ImportXlsHistory importXlsHistory = new ImportXlsHistory();
    if (demandeMigration != null) {
      importXlsHistory.setReferencett(demandeMigration.getReferenceTT());
      importXlsHistory.setReferenceChifco(demandeMigration.getReferenceChifco());
    } else {
      importXlsHistory.setReferencett(referenceXlsvalue);
    }
    importXlsHistory.setStatus(statut);
    importXlsHistory.setDescription(description);
    importXlsHistory.setIdfile(idfile);

    ImportXlsHistoryRepository.save(importXlsHistory);

  }
}
