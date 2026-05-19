package crm.chifco.com.service;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.OperationAbonnement;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Ville;

public interface OperationAbonnementService {
  HashMap<String, Object> findByTypeDemande(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, String typeDemande);

  void save(OperationAbonnement operationAbonnement);

  JsonResponseBody addFiled(Long id, HttpServletRequest request);

  JsonResponseBody removeFiled(Long id, HttpServletRequest request);

  String changerstatut(Long demandemigrationid, Model model);

  void sendNewrefTT(String referencett, Long idabon, User user, RedirectAttributes redirectAttrs);

  void makeMigration(OperationAbonnement operationAbonnement, User userconnected);


  Boolean confirmeAbonnement(String confirmation, Long idabon, User user, Long modemId,
      String motifRefus, RedirectAttributes redirectAttrs);


  String getUpdateDemandeAbonnement(Long packId, Long operationId);

  public void saveNewMigration(Long packid, Long clientid);

  Boolean updateStatutMigration(Long operationAbonId, String motifRefus, String modemId,
      Long telefixe, RedirectAttributes redirectAttrs);

  List<Statut> getallStatusAbonnement();

  OperationAbonnement getDemandeMigration(Long operationId);

  public Boolean saveNewDemandeChangementdebit(Long packid, Long clientid);

  String AddDemandTransfert(Long clientId, String adresse, Gouvernorat gouvernorat, Ville ville,
      PostalCode codepostale, String positionxy, Boolean residence, Model model);


  String updateDemandTransfert(Long id, Long clientId, String adresse, Gouvernorat gouvernorat,
      Ville ville, PostalCode codepostale, String positionxy, Boolean residence, Model model);

  boolean duplicateDemande(OperationAbonnement op, User user);

  void editNewDemandeChangementdébit(Long packId, Long operationId);

  Boolean affectRevendeur(Long clientid, String codeRevendeur, String emailRevendeur,
      String identificationFiscale);

  Boolean affectOneRevendeur(String idRevendeurToAffected, Long demandeId);

  List<Long> addAllIdToExport(String filterrecherche, String typeDemande,
      HttpServletRequest request);

  JsonResponseBody removeAllFromListExport(String typedemande, HttpServletRequest request);

  void exportToExcel(String typeDemande, HttpServletRequest request, HttpServletResponse response);


}
