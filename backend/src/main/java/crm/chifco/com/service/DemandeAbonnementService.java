package crm.chifco.com.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.DTOclass.DemandeAbbonmentDataDTO;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.Offre;
import crm.chifco.com.model.Pack;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.Profession;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.Typepaiement;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Ville;
import net.sf.jasperreports.engine.JRException;

public interface DemandeAbonnementService {

  DemandeAbonnement getDemandeAbonnementBydemandeId(Long demandeId);

  List<DemandeAbonnement> findDemandeAbonnementsByCinAndStatusAvaibled(String Cin);

  DemandeAbonnement findDemandeAbonnementByReferencechifco(String refchifco);

  Page<DemandeAbonnement> findPaginatedByRevendeurWithSort(int pageNo, int pageSize, Long roleid,
      Long userid, String sort, String sorttype);

  Page<DemandeAbbonmentDataDTO> findPaginatedByDistributeurWithSort(int pageNo, int pageSize,
      Long userid, Long createdbyuserid, String refChif, String refTT, String cin, String prenom,
      String nom, Long tel, Long villes, Long gouvernorat, Long professions, Long categories,
      Long produit, Long statutListfiltre, String statutTTListfiltre, String datedebut,
      String datefin, String dateDebutModification, String dateFinModification, Long creePar,
      Long AffecterTo, String datedebutMiseService, String datefinMiseService,String typeDabonnement ,String sort,
      String sorttype);

  Page<DemandeAbbonmentDataDTO> findPaginatedByRevendeurWithSearchParamsNotEmptyWithSort(int pageNo,
      int pageSize, Long roleid, Long userid, String refChif, String refTT, String cin,
      String prenom, String nom, Long tel, Long villeid, Long gouvernoratid, Long professionid,
      Long categorieid, Long produitid, Long statutid, String statutTTListfiltre, String datedebut,
      String datefin, String dateDebutModification, String dateFinModification, String sortvar,
      String sorttype);

  /*
   * Page<DemandeAbonnement> findPaginatedByDistributeurWithSearchParamsNotEmpty(int pageNo, int
   * pageSize, Long createdbyuserid, Long userid, String refChif, String refTT, String cin, String
   * prenom, String nom, String tel, Long villeid, Long gouvernoratid, Long professionid, Long
   * categorieid, Long produitid, Long statutid, String statutTTListfiltre, String
   * dateDebutModification, String dateFinModification);
   */
  /*
   * Page<DemandeAbonnement> findPaginatedByDistributeurWithSearchParamsNotEmptyWithSort(int pageNo,
   * int pageSize, Long createdbyuserid, Long userid, String refChif, String refTT, String cin,
   * String prenom, String nom, String tel, Long villeid, Long gouvernoratid, Long professionid,
   * Long categorieid, Long produitid, Long statutid, String statutTTListfiltre, String
   * dateDebutModification, String dateFinModification, Long AffecterTo, Long CreePar, String
   * sortvar, String sorttype);
   */
  Page<DemandeAbonnement> findPaginatedWithSearchParamsNotemptyWithSort(int pageNo, int pageSize,
      String refChif, String refTT, String cin, String prenom, String nom, String tel, Long villeid,
      Long gouvernoratid, Long professionid, Long categorieid, Long produitid, Long statutid,
      String statutTTListfiltre, String datedebut, String datefin, String dateDebutModification,
      String dateFinModification, Long AffecterTo, Long CreePar, String sortvar, String sorttype);

  Boolean checkFilterValue(JSONObject obj);

  String verificationTelFix(MultiValueMap<String, String> formData);

  String verificationTelFixEdit(MultiValueMap<String, String> formData);

  String verificationCin(MultiValueMap<String, String> formData);

  List<Statut> getallStatusAbonnement();

  List<Modem> getModems(String codeProduit, Boolean getAllType);

  List<Long> addAllIdToExport(String filterrecherche, HttpServletRequest request);

  JsonResponseBody removeAllFromListExport(HttpServletRequest request);

  JsonResponseBody addFiled(Long id, HttpServletRequest request);

  HashMap<String, Object> getAlAbonnement(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche);

  HashMap<String, Object> getfiltredStatusAbonnemnt(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, Long status);

  String getdemandeAbonnementToImprimer(Long demandeId, Model model);

  String getDemandeAbonnement(Long demandeId, Model model);

  JsonResponseBody removeFiled(Long id, HttpServletRequest request);

  void alldemandesNonSigneer(Model model, HttpServletRequest request);

  String allDemandesValider(Model model, HttpServletRequest request);

  String allDemandesAbonnement(Integer pageNo, Model model, String keyword, Long villes,
      Long gouvernorat, Long professions, Long categories, Long produit, Long statutListfiltre,
      String datedebut, String datefin, Integer pageSize, HttpServletRequest request);

  List<DemandeAbonnement> rechercherFicherClient(String reftt, String refchifco, String numfixe,
      String numcin, RedirectAttributes redirectAttrs);

  String getAddDemandeAbonnement(Model model);

  String getVerifClient(Model model);

  String getUpdateDemandeAbonnement(Long demandeId, Model model);

  String updateDemandeAbonnement(Long demandeId, Gouvernorat ville, PostalCode postalCode,
      Profession profession, Ville gouvernorat, Offre offres, MultipartFile imageFile,
      MultipartFile imageFile2, String datedenaissancess, Typepaiement typepaiement,
      DemandeAbonnement demandeAbonnement, MultipartFile pdfcontrat, String situationFamiliale,
      Model model, RedirectAttributes redirectAttrs, Pack produit, Boolean houseHolder,
      Boolean hasBankCard, Boolean residence);

  String getUrlDemandeAbonnementWithSteps(Model model);

  Statut getStatusAbonnement(Long demandeId);

  String verifClient(String cin, Model model);

  String uploadAddReferenceAbonnementEnMasse(MultipartFile file, RedirectAttributes redirectAttrs);

  ModelAndView exportToExcel(HttpServletRequest request);

  String uploadFile(MultipartFile file, Long demandeId, String cin, MultipartFile imageFile,
      MultipartFile imageFile2, RedirectAttributes redirectAttrs, HttpServletResponse response);

  void downloadContrat(Long demandeId, HttpServletResponse response)
      throws JRException, IOException;

  void downloadReport(Long demandeId, HttpServletResponse response);

  String addDemandeAbonnementWithSteps(int raccordementtranche, String nom, String prenom,
      String email, String cin, Gouvernorat gouvernorat, Long gouvernoratid, String adresse,
      PostalCode codepostale, Long telMobile, Long telMobile2, Long telFixe, Long fax,
      String positionxy, Profession profession, Typepaiement typepaiement,
      Long categorieProduitInternet, Long produitid, String datedenaissance, Boolean residence,
      MultipartFile imageFile, MultipartFile imageFile2, Long[] multipleSubproduct,
      String situationFamiliale, Boolean houseHolder, Boolean hasBankCard, String origin,
      Model model, RedirectAttributes redirectAttrs);

  String editAbonnement(Statut statut, String modemfromlist, Long demandeAbonnementid,
      String fromlocation, MultipartFile file, Model model, RedirectAttributes redirectAttrs,
      Long telefixe, String referencett, String motifRefus , String telemobile5g);

  ArrayList<Map<String, Object>> sendSmsIfNonStock(String model, Long iduserCreted,
      ArrayList numeroTelephone, String referenceChifco, Boolean isMultiple);

  void sendSmsConstructionLigne(ArrayList numeroTelephone, String referenceChifco);

  String confirmeAbonnement(String confirmation, Long idabon, User user);

  DemandeAbonnement saveStatutDemande(DemandeAbonnement demandeAbonnementFactures);

  String generateLoginRadus(Abonnement getclient);

  String smsVerification(Long demandeId, User user);

  DemandeAbonnement duplicatedDemande(Long demandeId, String cin, User user);

  Map<String, Object> saveDuplicatedDemande(MultipartFile cinRecto, MultipartFile cinVerso,
      String datedenaissancess, DemandeAbonnement demandeAbonnement, User user,
      Typepaiement typePaiement, Gouvernorat gouvernorat, Profession profession, Ville ville,
      PostalCode postalCode, Boolean residence, Boolean houseHolder, Boolean hasBankCard,
      Long telFixe, int raccordementtranche, Long packes, Long[] multipleSubproduct);

  Boolean affectRevendeur(Long clientid, String codeRevendeur, String emailRevendeur,
      String identificationFiscale);

  Boolean affectOneRevendeur(String idRevendeurToAffected, Long idDemande);

  String verificationCinFixEdit(MultiValueMap<String, String> formData);

  String confirmeAbonnementInjoignable(String confirmation, Long idabon, User user);

  List<Map<String, Object>> getFullYearDemandeAbonnementCounts(int year, Long revId,
      Long chefSecteurId);


  List<Map<String, Object>> getFullYearDemandeAbonnementRealiserCounts(int year, Long revId,
      Long chefSecteurId);

  HashMap<String, Object> getfiltredstatusabonnemnSuividesDemandesTransferees(int draw, int start,
      int length, String search, int ordercolumnaram, String orderdir, String filterrecherche,
      Long status);


}
