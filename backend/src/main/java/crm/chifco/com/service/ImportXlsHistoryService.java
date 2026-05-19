package crm.chifco.com.service;

import java.util.List;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.ImportXlsHistory;
import crm.chifco.com.model.OperationAbonnement;

public interface ImportXlsHistoryService {


  Page<ImportXlsHistory> getallImportXlsHistory(int pageNo, int pageSize);

  void insertNewImportXlsHistory(String status, String statusdemande, String Description,
      DemandeAbonnement Abonnement, Long idfile);

  String insertNewRowImportXlsHistoryStatutSendTT(String fichXlsEtat,
      String AbonnementEtatCurentValue);

  String insertNewRowImportXlsHistoryStatutSendTT2(String fichXlsEtat,
      String OperationEtatCurentValue);

  List<ImportXlsHistory> getImportXlsHistoryById(Long id);

  void insertNewImportXlsHistory2(String status, String designation, String description,
      OperationAbonnement demandeMigration, Long xlsHistoriqueFile);
}
