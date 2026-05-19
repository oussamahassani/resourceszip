package crm.chifco.com.service;

import java.io.File;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.ApiDTO.getLoginAndPassswordModem;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.User;
import crm.chifco.com.templateclasse.AcsInfo;
import net.sf.jasperreports.engine.JRException;

public interface AbonnementService {

  Page<Abonnement> findPaginatedwithfilter(int pageNo, int pageSize, String firstName,
      String lastName, String cin, String codeClient, Boolean status, Long tel, Long villes,
      Long produit, Long categories, Long gouvernorat, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, String loginModem,
      String ancienloginModem, Long AffecterTo, Long Creepar, String statutChifcoListfiltre,
      Boolean listeClientFilter, Long contactNumber, String sortvar, String sorttype,
      Boolean isOnlyActiveUser, Date dateAffectionModem, Date datefinAffectionModem , String typeAbonnment);



  Page<Abonnement> findPaginatedByDistributeur(int pageNo, int pageSize, Long createdbyuserid,
      String sortvar, String sorttype, String firstName, String lastName, String cin,
      String codeClient, Boolean status, Long tel, Long villes, Long produit, Long categories,
      Long gouvernorat, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, String loginModem, Long AffecterTo, Long Creepar,
      String statutChifcoListfiltre , String typeAbonnment);

  HashMap<String, Object> getAllClient(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, Boolean isOnlyActiveUser);

  Page<Abonnement> findPaginatedbyRevendeurWithoutSort(int pageNo, int pageSize, Long roleid,
      Long userid);

  Abonnement saveNewAbonnement(DemandeAbonnement demandeAbonnement);

  Abonnement findAbonnementByCin(String cin);

  List<AcsInfo> findListClientToAcs(Instant dateCreation, String statutUser);

  public Page<Abonnement> findPaginatedByRevendeurWithFilter(int pageNo, int pageSize, Long roleid,
      Long userid, String sortvar, String sorttype, String firstName, String lastName, String cin,
      String codeClient, Boolean status, Long tel, Long villes, Long pack, Long categories,
      Long gouvernorat, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, String loginModem , String typeAbonnment);

  Abonnement findAbonnementByReferenceClient(String referenceChifco);
  Abonnement findAbonnementByReferenceClientAndUpdate(String referenceChifco, String montantComision);

  Abonnement findUserByFixeNumberOrCin(String recherche, Long telephone);

  Abonnement findUserByFixeNumber(Long telephone);

  Boolean affectRevendeur(Long clientid, String codeRevendeur, String emailRevendeur,
      String identificationFiscale);

  Boolean affectOneRevendeur(String string, Long parseLong);

  ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      Long gouvernorat, Long villes, String cin, String nom, String prenom, String codeClient,
      Boolean status, Long tel, Long category, Long produit, String dateDebut, String dateFin,
      String dateDebutModification, String dateFinModification, String loginModem, Long affecterTo,
      Long creepar, String statutChifcoListfiltre, String listeClientFilter,
      String exportRecherchedatedebutAffectionModem,String typeAbonnment , String ExportRecherchedatefinAffectionModem);

  getLoginAndPassswordModem getLoginAndPassswordModem(String numSerie);

  Boolean saveResiliation(User user, Long clientid);

  String changerModem(Long clientId, String numSerieModem, User user);
  
  String changerSimModem(Long clientId, String numSerieModem, User user);

  String verificationTelFixEdit(MultiValueMap<String, String> formData);

  String verificationCinEdit(MultiValueMap<String, String> formData);

  List<String> getMotifStatutTT(String statutId);

  void changeToRecouvrement(Long clientid, RedirectAttributes redirectAttrs);



  void updateEnvoiSms(Long clientid, RedirectAttributes redirectAttrs);

  void sendSmsToclient(User user, Long clientid, String sms);

  String insertRadusIfNotExiste();



  Long findIdClientByIdDemandeAbonnment(Long idDemandeAbonnment);

  ModelAndView extractEnMasseClientNonConnecter(HttpServletRequest request,
      HttpServletResponse response, Long gouvernorat, Long villes, String cin, String nom,
      String prenom, String codeClient, Long tel, Long category, Long produit, String dateDebut,
      String dateFin, String dateDebutModification, String dateFinModification, String loginModem,
      Long affecterTo, Long creepar, String exportRecherchedatedebutAffectionModem);

  HashMap<String, Object> getallClientNonConnecter(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche);


  void changeToActive(Long clientid, RedirectAttributes redirectAttrs);

  void changeToSuspendu(Long clientid, RedirectAttributes redirectAttrs);


  void addToRadus(Long clientid, RedirectAttributes redirectAttrs);


  void ReprendreCycleAbonnement(Long clientid, RedirectAttributes redirectAttrs);



  void changrRadusAbonnement(Long clientid, RedirectAttributes redirectAttrs);



  void changrProchainDateFacturationAbonnement(Long clientid, String dateNouvelle,
      RedirectAttributes redirectAttrs);

  Map<String, Object> getAbonnementSummaryForMonth(Date startOfMonth, Date endOfMonth, Long revId);

  HashMap<String, Object> getallClientNonConnecterMiseService(int draw, int start, int length,
      String search, int ordercolumnaram, String orderdir, String filterrecherche);


  List<Abonnement> findAbonnementsByReferenceClient( List<String> referenceChifco);




  File createPDFRecuResilationA4(Long idClient, String numSerieModem) throws Exception, JRException;

}
