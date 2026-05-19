package crm.chifco.com.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.DemandeAbonnementHistory;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.MigrationFacture;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.ModemAccess;
import crm.chifco.com.model.OperationAbonnement;
import crm.chifco.com.model.Pack;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.Tarification;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Ville;
import crm.chifco.com.radius.model.Radcheck;
import crm.chifco.com.radius.model.Radusergroup;
import crm.chifco.com.radius.repository.RadacctRepository;
import crm.chifco.com.radius.repository.RadcheckRepository;
import crm.chifco.com.radius.repository.RadusergroupRepository;
import crm.chifco.com.radius.service.RadcheckService;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.DemandeAbonnementHistoryRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.MigrationFactureRepository;
import crm.chifco.com.repository.ModemAccessRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.OperationAbonnementRepository;
import crm.chifco.com.repository.PackRepository;
import crm.chifco.com.repository.SmstemplateRepository;
import crm.chifco.com.repository.StatutRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AbonnementHistoriqueService;
import crm.chifco.com.service.AbonnementService;
import crm.chifco.com.service.ClientHistoryService;
import crm.chifco.com.service.DemandeAbonnementService;
import crm.chifco.com.service.ExportOperationAbonnementService;
import crm.chifco.com.service.ImportXlsHistoryFileService;
import crm.chifco.com.service.ModemHistoryService;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.OperationAbonnementService;
import crm.chifco.com.service.StatutService;
import crm.chifco.com.service.TarificationServices;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.DBEtatTT;
import crm.chifco.com.utils.NomStatutChifco;
import crm.chifco.com.utils.typeCalcluleMigrationFacture;

@Service("OperationAbonnementService")
public class OperationAbonnementServiceImpl implements OperationAbonnementService {

  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  private OperationAbonnementRepository operationAbonnementRepository;
  @Autowired
  private DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  private AbonnementRepository abonnementRepository;
  @Autowired
  ClientHistoryService abonnementHistoriqueService;


  @Value("${pathDemandesAbonnement}")
  private String pathDemandesMigration;

  @Autowired
  AbonnementService AbonnementService;

  @Autowired
  UserService UserService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PackRepository packRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private StatutRepository statutRepository;

  @Value("${pathuploadxlsx}")
  private String pathUploadXlsx;

  @Value("${pathDemandesAbonnement}")
  private String pathDemandesAbonnement;

  @Value("${access.mail.modem.XDSL.nety}")
  private String  EmailmodemXDSL;

  @Value("${prixhtRacordment}")
  private String prixhtRacordment;

  @Autowired
  private AbonnementHistoriqueService AbonnementHistoriqueservice;
  @Autowired
  private DemandeAbonnementService demandeAbonnementService;

  @Autowired
  ImportXlsHistoryFileService ImportXlsHistoryFileService;


  @Autowired
  DemandeAbonnementHistoryRepository demandeAbonnementHistoryRepository;

  @Autowired
  SmstemplateRepository templatesmsRepository;

  @Autowired
  Notification notificationservice;

//  @Autowired
 // RadcheckService radcheckService;

  @Autowired
  RadcheckRepository radcheckRepository;

  @Autowired
  StatutRepository StatutRepository;

  @Autowired
  StatutService statutService;
  @Autowired
  ModemRepository modemRepository;
  @Autowired
  ModemHistoryService modemHistoryService;

  @Autowired
  RadusergroupRepository radusergroupRepository;

  @Autowired
  RadacctRepository radacctRepository;

  @Autowired
  MigrationFactureRepository migrationFactureRepository;

  @Autowired
  FactureRepository factureRepository;

  @Autowired
  TarificationServices tarificationServices;
  @Autowired
  ExportOperationAbonnementService exporttoexcel;

  private static final Logger logger = LogManager.getLogger(RadcheckService.class);

  @Autowired
  ModemAccessRepository modemAccessRepository;

  @Override
  public HashMap<String, Object> findByTypeDemande(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, String typeDemande) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    int currentpage = start / length;
    String refChif = null;
    String refTT = null;
    String cin = null;
    String prenom = null;
    String nom = null;
    Long tel = null;
    Long villes = null;
    Long gouvernorat = null;
    Long professions = null;
    Long categories = null;
    Long produit = null;
    Long statutListfiltre = null;
    String statutTTListfiltre = null;
    String datedebut = null;
    String datefin = null;
    String dateDebutModification = null;
    String dateFinModification = null;
    Long AffecterTo = null;
    Long CreePar = null;
    int page = start / length;
    String sort = "";

    switch (ordercolumnaram) {

      case 1:
        sort = "reference_chifco";
        break;
      case 2:
        sort = "etattt";
        break;

      case 4:
        sort = "cin";
        break;
      case 5:
        sort = "first_name";
        break;
      case 6:
        sort = "tel_fixe";
        break;
      case 7:
        sort = "created_date";
        break;
      default:
        sort = "created_date";
    }
    if (search != null && search != "") {
      refChif = search;
    }
    Boolean CheckFilterIfExiste = false;
    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      CheckFilterIfExiste = this.checkFilterValue(obj);

      if (filterrecherche != null && !filterrecherche.equals("") && CheckFilterIfExiste) {

        if (!Objects.equals(obj.getString("refChif"), "") && obj.getString("refChif") != null) {
          refChif = obj.getString("refChif").trim().toLowerCase();
        }
        if (!Objects.equals(obj.getString("refTT"), "") && obj.getString("refTT") != null) {
          refTT = obj.getString("refTT").trim().toLowerCase();
        }
        if (!Objects.equals(obj.getString("cin"), "") && obj.getString("cin") != null) {
          cin = obj.getString("cin").trim().toLowerCase();
        }
        if (!Objects.equals(obj.getString("prenom"), "") && obj.getString("prenom") != null) {
          prenom = obj.getString("prenom").trim();
        }
        if (!Objects.equals(obj.getString("nom"), "") && obj.getString("nom") != null) {
          nom = obj.getString("nom").trim().toLowerCase();
        }
        if (!Objects.equals(obj.getString("tel"), "") && obj.getString("tel") != null) {
          tel = obj.getLong("tel");
        }
        if (!Objects.equals(obj.getString("villes"), "") && obj.getString("villes") != null) {
          villes = obj.getLong("villes");
        }
        if (!Objects.equals(obj.getString("gouvernorat"), "")
            && obj.getString("gouvernorat") != null) {
          gouvernorat = obj.getLong("gouvernorat");
        }
        if (!Objects.equals(obj.getString("professions"), "")
            && obj.getString("professions") != null) {
          professions = obj.getLong("professions");
        }
        if (!Objects.equals(obj.getString("Categories"), "")
            && obj.getString("Categories") != null) {
          categories = Long.parseLong(obj.getString("Categories").trim());
        }
        if (!Objects.equals(obj.getString("produit"), "") && obj.getString("produit") != null) {
          produit = obj.getLong("produit");
        }

        if (!Objects.equals(obj.get("CreePar"), "") && obj.getString("CreePar") != null) {
          CreePar = obj.getLong("CreePar");
        }
        if (!Objects.equals(obj.getString("statutListfiltre"), "")
            && obj.getString("statutListfiltre") != null) {
          statutListfiltre = obj.getLong("statutListfiltre");
        }
        if (!Objects.equals(obj.getString("datedebut"), "") && obj.getString("datedebut") != null) {
          datedebut = obj.getString("datedebut") + "T00:00:00.000";
        }
        if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
          datefin = obj.getString("datefin") + "T23:59:59.999";
        }
        if (!Objects.equals(obj.getString("statutTTListfiltre"), "")
            && obj.getString("statutTTListfiltre") != null) {
          statutTTListfiltre = obj.getString("statutTTListfiltre");
        }
      }
    }

    Page<OperationAbonnement> responseData = null;
    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
      responseData = this.findPaginatedWithSearchParamsNotemptyWithSort(page + 1, length,
          statutTTListfiltre, refChif, refTT, cin, prenom, nom, tel, villes, gouvernorat,
          professions, categories, produit, statutListfiltre, datedebut, datefin,
          dateDebutModification, dateFinModification, sort, orderdir, typeDemande);
      // Revendeur
    } else if (StringsRole.contains("READ_SUBSCRIPTION_LIST_OWNER")) {
      responseData = this.findPaginatedByRevendeurWithSort(user.getUserid(), page + 1, length,
          statutTTListfiltre, refChif, refTT, cin, prenom, nom, tel, villes, gouvernorat,
          professions, categories, produit, statutListfiltre, datedebut, datefin,
          dateDebutModification, dateFinModification, sort, orderdir, typeDemande);
    }
    // ROLE_DISTRIBUTEUR
    else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {

      responseData = this.findPaginatedByDistributeurWithSort(user.getUserid(), currentpage + 1,
          length, user.getUserid(), refChif, refTT, cin, prenom, nom, tel, villes, gouvernorat,
          professions, categories, produit, statutListfiltre, statutTTListfiltre, datedebut,
          datefin, dateDebutModification, dateFinModification, CreePar, AffecterTo, sort, orderdir,
          typeDemande);
    }

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);

    return myGreetings;

  }

  public Boolean checkFilterValue(JSONObject obj) {
    boolean statutListfiltre =
        (obj.has("statutListfiltre") && obj.getString("statutListfiltre").trim() != "");
    boolean statutTTListfiltre =
        (obj.has("statutTTListfiltre") && obj.getString("statutTTListfiltre").trim() != "");
    boolean gouvernorat = (obj.has("gouvernorat") && obj.getString("gouvernorat").trim() != "");
    boolean professions = (obj.has("professions") && obj.getString("professions").trim() != "");
    boolean categories = (obj.has("categories") && obj.getString("categories").trim() != "");
    boolean produit = (obj.has("produit") && obj.getString("produit").trim() != "");
    boolean villes = (obj.has("villes") && obj.getString("villes").trim() != "");
    boolean refChif = (obj.has("refChif") && obj.getString("refChif").trim() != "");
    boolean refTT = (obj.has("refTT") && obj.getString("refTT").trim() != "");
    boolean cin = (obj.has("cin") && obj.getString("cin").trim() != "");
    boolean prenom = (obj.has("prenom") && obj.getString("prenom").trim() != "");
    boolean nom = (obj.has("nom") && obj.getString("nom").trim() != "");
    boolean tel = (obj.has("tel") && obj.getString("tel").trim() != "");
    boolean datedebut = (obj.has("datedebut") && obj.getString("datedebut").trim() != "");
    boolean datefin = (obj.has("datefin") && obj.getString("datefin").trim() != "");
    boolean dateDebutModification =
        (obj.has("dateDebutModification") && obj.getString("dateDebutModification").trim() != "");
    boolean dateFinModification =
        (obj.has("dateFinModification") && obj.getString("dateFinModification").trim() != "");

    if (statutListfiltre || gouvernorat || professions || categories || produit || villes || refChif
        || refTT || cin || prenom || nom || tel || datedebut || datefin || statutTTListfiltre
        || dateDebutModification || dateFinModification) {
      return true;
    }
    return false;

  }

  public Page<OperationAbonnement> findPaginatedWithSearchParamsNotemptyWithSort(int pageNo,
      int pageSize, String statutTTListfiltre, String refchif, String refTT, String cin,
      String prenom, String nom, Long tel, Long villeid, Long gouvernoratid, Long professionid,
      Long categorieid, Long produitid, Long statutListfiltre, String datedebut, String datefin,
      String dateDebutModification, String dateFinModification, String sortvar, String sorttype,
      String typeDemande) {

    Sort sort = Sort.by("modified_date");
    if (sortvar == null || sortvar == "") {
      sortvar = "modified_date";
    }
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.operationAbonnementRepository.findDemandesMigrationsByPramsnotempty(
        statutTTListfiltre, refchif, refTT, cin, prenom, nom, tel, villeid, gouvernoratid,
        professionid, categorieid, produitid, statutListfiltre, datedebut, datefin,
        dateDebutModification, dateFinModification, pageable, typeDemande);
  }

  public Page<OperationAbonnement> findPaginatedByRevendeurWithSort(Long userid, int pageNo,
      int pageSize, String statutTTListfiltre, String refchif, String refTT, String cin,
      String prenom, String nom, Long tel, Long villeid, Long gouvernoratid, Long professionid,
      Long categorieid, Long produitid, Long statutListfiltre, String datedebut, String datefin,
      String dateDebutModification, String dateFinModification, String sortvar, String sorttype,
      String typeDemande) {
    Sort sort = Sort.by("createdDate").descending();
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.operationAbonnementRepository.findDemandeMigrationByRev(userid, statutTTListfiltre,
        refchif, refTT, cin, prenom, nom, tel, villeid, gouvernoratid, professionid, categorieid,
        produitid, statutListfiltre, datedebut, datefin, dateDebutModification, dateFinModification,
        pageable, typeDemande);
  }


  public Page<OperationAbonnement> findPaginatedByDistributeurWithSort(Long userid, int pageNo,
      int pageSize, Long createdbyuserid, String refChif, String refTT, String cin, String prenom,
      String nom, Long tel, Long villes, Long gouvernorat, Long professions, Long categories,
      Long produit, Long statutListfiltre, String statutTTListfiltre, String datedebut,
      String datefin, String dateDebutModification, String dateFinModification, Long creePar,
      Long AffecterTo, String sortvar, String sorttype, String typeDemande) {
    Sort sort = Sort.by("createdDate").descending();
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Date dateDebutModifications = null;
    Date dateFinModifications = null;
    Date dateDebutCreation = null;
    Date dateFinCreation = null;

    if (dateDebutModification != null) {
      dateDebutModifications = CrmUtils.convertedFilterRechercheDate(dateDebutModification);
    }

    if (dateFinModification != null) {
      dateFinModifications = CrmUtils.convertedFilterRechercheDate(dateFinModification);
    }

    if (datedebut != null) {
      dateDebutCreation = CrmUtils.convertedFilterRechercheDate(datedebut);
    }

    if (datefin != null) {
      dateFinCreation = CrmUtils.convertedFilterRechercheDate(datefin);
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.operationAbonnementRepository.findDemandMigrationByDistributeur(userid, refChif,
        refTT, cin, prenom, nom, tel, villes, gouvernorat, professions, categories, produit,
        statutListfiltre, statutTTListfiltre, dateDebutModifications, dateFinModifications,
        dateDebutCreation, dateFinCreation, creePar, pageable, typeDemande);
  }

  @Override
  public void save(OperationAbonnement demandeMigration) {
    try {
      LOGGER.info("Start the save of a new demande : " + demandeMigration.getReferenceChifco());
      this.operationAbonnementRepository.save(demandeMigration);
      LOGGER.info("end the process of saving with success ");
    } catch (Exception e) {
      LOGGER.error(
          "Le prcess d'ajout d'une nouvelle operation demande a échoué(operationAbonnementServiceImp.save) : "
              + e.getMessage());
    }
  }

  @Override
  public JsonResponseBody addFiled(Long id, HttpServletRequest request) {
    LOGGER.info("id: " + id);
    Object checkliste = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checkliste);
    List<Long> listesdesIds = new ArrayList<>();
    if (checkliste == null || checkliste.equals("")) {
      if (id == null || id.equals("")) {
        request.getSession().setAttribute("listedes_ids", listesdesIds);
        LOGGER.info("listedes_ids if: " + listesdesIds);
      } else {
        listesdesIds.add(id);
        request.getSession().setAttribute("listedes_ids", listesdesIds);
      }

    } else {
      listesdesIds = (List<Long>) request.getSession().getAttribute("listedes_ids");
      LOGGER.info("listedes_ids else: " + listesdesIds.contains(id));
      if (listesdesIds.contains(id) == false) {
        listesdesIds.add(id);
      }

      request.getSession().setAttribute("listedes_ids", listesdesIds);
    }
    JsonResponseBody jrb = new JsonResponseBody();
    jrb.setCode(String.valueOf(200));
    jrb.setMsg("Ajout de l'id à liste avec succes");
    jrb.setResult(listesdesIds);
    return jrb;
  }

  @Override
  public JsonResponseBody removeFiled(Long id, HttpServletRequest request) {
    LOGGER.info("removefiled from session attribut id: " + id);
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      LOGGER.info("liste des ids est vide ");
      List<Long> listesdesIds = new ArrayList<>();
      JsonResponseBody jrb = new JsonResponseBody();
      jrb.setCode(String.valueOf(200));
      jrb.setMsg("liste des ids est vide");
      jrb.setResult(listesdesIds);
      return jrb;
    } else {
      List<Long> listedesIds = (List<Long>) request.getSession().getAttribute("listedes_ids");
      int pos = listedesIds.indexOf(id);
      listedesIds.remove(pos);
      LOGGER.info("nouvelle liste session attribut des id: " + listedesIds);
      request.getSession().setAttribute("listedes_ids", listedesIds);

      JsonResponseBody jrb1 = new JsonResponseBody();
      jrb1.setCode(String.valueOf(200));
      jrb1.setMsg("Suppression de l'id de liste avec succes");
      jrb1.setResult(listedesIds);
      return jrb1;
    }
  }

  public String changerstatut(Long demandemigrationid, Model model) {

    try {
      OperationAbonnement DemandeMigration =
          operationAbonnementRepository.findById(demandemigrationid).get();
      if (DemandeMigration.getAssignedTo() != null) {
        model.addAttribute("AssignedToUser", DemandeMigration.getAssignedTo());
      }
      Long idconnected = null;
      Boolean isadmin = false;
      Boolean isduplicate = false;
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        model.addAttribute("userphoto", user.getPhoto());
        model.addAttribute("userrole", user.getRole().getRoleName());
        model.addAttribute("userwithstock", user.getWithStock());
        model.addAttribute("useremail", user.getEmail());
        idconnected = user.getUserid();
        List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
        isadmin = StringsRole.contains("SEARCH_SUBSCRIPTION_REQUEST_ALL");
      }
      List<Modem> Modems = new ArrayList<Modem>();
      Abonnement Abonnement = abonnementRepository.findAbonnementByCin(DemandeMigration.getCin());
      String codeProduit =
          DemandeMigration.getPack().getCategoriePack().getCategorieProduitInternetCode();
      if (Abonnement.getModem().getModelModem().equals("XDSL")) {
        Modems.add(Abonnement.getModem());

      } else {
        Modems = demandeAbonnementService.getModems(codeProduit, false);

      }

      Integer res = operationAbonnementRepository.checkOperationAbonnementAndAbonnementStatus(
          DemandeMigration.getReferenceChifco(), DemandeMigration.getTypeDemande());
      // List<Abonnement> ab = abonnementRepository.findAbonnementsNotInMigrationAdminwithRef2(
      // DemandeMigration.getReferenceChifco(), DemandeMigration.getTypeDemande());
      if (res != 0) {
        isduplicate = true;
      }

      List<Statut> liststatus = new ArrayList<>();
      if (DemandeMigration != null) {
        if (DemandeMigration.getStatut().getNomStatut().equals(NomStatutChifco.SIGNED_DOC)) {
          Statut statutAnnuler = statutRepository.findStatutByNomStatut(NomStatutChifco.CANCELED);
          Statut statutevoieTT = statutRepository.findStatutByNomStatut(NomStatutChifco.WAIT_TT);
          liststatus.add(statutevoieTT);
          liststatus.add(statutAnnuler);

        } else if (DemandeMigration.getStatut().getNomStatut().equals(NomStatutChifco.DRAFT)) {
          Statut statutAnnuler = statutRepository.findStatutByNomStatut(NomStatutChifco.CANCELED);
          liststatus.add(statutAnnuler);

        } else if (DemandeMigration.getStatut().getNomStatut().equals(NomStatutChifco.WAIT_TT)) {
          Statut statutAnnuler = statutRepository.findStatutByNomStatut(NomStatutChifco.CANCELED);
          Statut statutvaliderTT =
              statutRepository.findStatutByNomStatut(NomStatutChifco.INSTALLED);
          Statut statutRefuseTT = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);
          liststatus.add(statutAnnuler);
          liststatus.add(statutvaliderTT);
          liststatus.add(statutRefuseTT);

        } else if (DemandeMigration.getStatut().getNomStatut().equals(NomStatutChifco.INSTALLED)) {
          Statut Modemaffetcter = statutRepository.findStatutByNomStatut(NomStatutChifco.ASSIGNED);
          liststatus.add(Modemaffetcter);

        } else if (DemandeMigration.getStatut().getNomStatut().equals(NomStatutChifco.ASSIGNED)) {
          Statut Modemvalider = statutRepository.findStatutByNomStatut(NomStatutChifco.VALID);
          liststatus.add(Modemvalider);

        } else if (DemandeMigration.getStatut().getNomStatut().equals(NomStatutChifco.ACTIVE)) {
          Statut nonpayer = statutRepository.findStatutByNomStatut(NomStatutChifco.UNPAID);
          liststatus.add(nonpayer);

        } else if (DemandeMigration.getStatut().getNomStatut().equals(NomStatutChifco.VALID)) {
          Statut activer = statutRepository.findStatutByNomStatut(NomStatutChifco.ACTIVE);
          liststatus.add(activer);
          Abonnement AbonnementValider = AbonnementService
              .findAbonnementByReferenceClient(DemandeMigration.getReferenceChifco());
        }
        List<DemandeAbonnementHistory> DemandeAbonnementHistoryList = AbonnementHistoriqueservice
            .findDemandeAbonnementHistoryByCin(DemandeMigration.getCin());

        model.addAttribute("historylist", DemandeAbonnementHistoryList);
        model.addAttribute("statut", liststatus);
        model.addAttribute("demande", DemandeMigration);
        model.addAttribute("abonnement", Abonnement);
        model.addAttribute("modems", Modems);
        model.addAttribute("idconnected", idconnected);
        model.addAttribute("isadmin", isadmin);
        model.addAttribute("isduplicate", isduplicate);
        model.addAttribute("DBEtatTT", DBEtatTT.dbEtatTT);
        if (DemandeMigration.getTypeDemande().equals("M")) {
          LOGGER.info(
              "getAbonnementTomigration (operationAbonnementServiceImpl.changerStatut): demande: "
                  + DemandeMigration);
          LOGGER.info("getAbonnementTomigration : liststatus: " + liststatus);
        }
        if (DemandeMigration.getTypeDemande().equals("T")) {
          LOGGER.info(
              "getAbonnementTotransfert (operationAbonnementServiceImpl.changerStatut): demande: "
                  + DemandeMigration);
          LOGGER.info("getAbonnementTotransfert: liststatus: " + liststatus);
        }
        if (DemandeMigration.getTypeDemande().equals("CH")) {
          LOGGER.info(
              "getAbonnementTochangement débit(operationAbonnementServiceImpl.changerStatut) : demande: "
                  + DemandeMigration);
          LOGGER.info("getAbonnementTomigration : liststatus: " + liststatus);
        }
      } else {
        model.addAttribute("message", "demande migration non existant");
      }

      if (DemandeMigration.getTypeDemande().equals("M")) {
        return "operationAbonnement/viewmigration";
      } else if (DemandeMigration.getTypeDemande().equals("T")) {
        return "operationAbonnement/viewtransfert";
      } else {
        return "operationAbonnement/viewchangementdebit";
      }
    } catch (Exception e) {

      LOGGER.error("error de operationAbonnementserviceImpl.changerstatut:" + e.getMessage());
      return "operationAbonnement/viewmigration";
    }
  }

  @Override
  public Boolean confirmeAbonnement(String confirmation, Long idabon, User user, Long modemId,
      String motifRefus, RedirectAttributes redirectAttrs) {
    String etatTT = null;
    Boolean result = false;
    String typeDemande = "";
    OperationAbonnement finddemande = operationAbonnementRepository.findById(idabon).get();
    Statut etatChifco = finddemande.getStatut();
    Statut statusannuler = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);
    switch (confirmation) {
      case "activationService": {
        etatTT = DBEtatTT.ActivationService;
        etatChifco = statutRepository.findStatutByNomStatut(NomStatutChifco.INSTALLED);
        break;
      }
      case "reservermodem": {
        if (modemId != null)
          etatTT = DBEtatTT.Affectation_Modem;

        else {
          etatTT = null;
          redirectAttrs.addFlashAttribute("message", "Modem n'existe pas");
        }
        break;
      }
      case "Rejetée": {
        etatTT = DBEtatTT.Rejected;
        etatChifco = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);
        break;
      }
      case "raccordement": {
        etatTT = DBEtatTT.Raccordement;

        break;
      }
      case "InstanceNonFibré": {
        etatTT = DBEtatTT.Instance;
        // etatChifco = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);

        break;
      }
      case "execution": {
        etatTT = DBEtatTT.Execution;

        break;
      }
      case "anuulé": {
        etatTT = DBEtatTT.Refused;
        etatChifco = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);

        break;
      }
      case "annuler": {
        etatTT = DBEtatTT.Refused;
        finddemande.setStatut(statusannuler);
        etatChifco = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);
        break;
      }
      case "attenteConstruction": {
        etatTT = DBEtatTT.Attente_Construction;

        break;
      }
      case "instancecomercial": {
        etatTT = DBEtatTT.Instance_Commercial;
        if (motifRefus != null) {
          finddemande.setMotifRefus(motifRefus);
        }

        break;
      }
      case DBEtatTT.Clôturée: {
        etatTT = DBEtatTT.Clôturée;
        break;
      }
      case "migration": {
        etatTT = DBEtatTT.Migration;
        motifRefus = null;
        break;
      }
      case "Instance": {
        etatTT = DBEtatTT.Instance;
        break;
      }
    }
    if (etatTT != null) {
      if (etatTT.equals(DBEtatTT.Execution)
          && finddemande.getEtatTT().equals(DBEtatTT.Enregister)) {
        ArrayList<String> arrayTelephoneConstructionLigne = new ArrayList<>();

        arrayTelephoneConstructionLigne.add(finddemande.getTelMobile().toString());

        sendSmsConstructionLigne(arrayTelephoneConstructionLigne, finddemande.getReferenceChifco());
      }
      if (etatTT.equals(DBEtatTT.Affectation_Modem)
          && finddemande.getEtatTT().equals(DBEtatTT.Raccordement) && modemId != null) {

        Modem modem = modemRepository.findModemBymodemId(modemId);
        finddemande.setModem(modem);
      }
      finddemande.setEtatTT(etatTT);
      finddemande.setStatut(etatChifco);
      if (finddemande.getTypeDemande().equals("M")) {
        typeDemande = "Migration";
      } else if (finddemande.getTypeDemande().equals("T")) {
        typeDemande = "Tranfert";
      } else {
        typeDemande = "Changement débit";
      }
      Abonnement abonnement =
          abonnementRepository.findAbonnementByClientid(finddemande.getAbonnementId());
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(abonnement.getAdresse());
      demandeAbonnementHistory.setCin(abonnement.getCin());
      if (etatTT.equals("Instance") && motifRefus != null
          || etatTT.equals("Instance Commercial") && motifRefus != null) {
        demandeAbonnementHistory.setDescription("Le statut de demande de " + typeDemande
            + " passe à  «" + etatTT + "» , le motif est «" + motifRefus + "».");
        result = true;
      } else {
        demandeAbonnementHistory.setDescription(
            "Le statut de demande de " + typeDemande + " passe à  «" + etatTT + "»");
        result = true;
      }
      demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
      demandeAbonnementHistory.setLastName(abonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(user);
      if (typeDemande.equals("Migration") && etatTT.equals("Annulée") && finddemande.getPack()
          .getCategoriePack().getCategorieProduitInternetCode().equals("VDSL")) {
        result = true;
        finddemande.setStatut(statusannuler);
      }
      if (motifRefus != null && etatTT.equals("Instance")
          || motifRefus != null && etatTT.equals("Instance Commercial")) {
        finddemande.setMotifRefus(motifRefus);
        result = true;
      } else {
        finddemande.setMotifRefus(null);
        result = true;
      }
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      operationAbonnementRepository.save(finddemande);
      if (typeDemande.equals("Tranfert") && finddemande.getEtatTT().equals(DBEtatTT.Clôturée)
          || typeDemande.equals("Tranfert")
              && finddemande.getEtatTT().equals(DBEtatTT.Mise_en_service)) {
        result = true;
        Statut status = statutRepository.findStatutByNomStatut(NomStatutChifco.VALID);
        finddemande.setStatut(status);
        operationAbonnementRepository.save(finddemande);
        Abonnement abon =
            abonnementRepository.findAbonnementByReferenceClient(finddemande.getReferenceChifco());
        abon.setStatut(status);
        LocalDate currentDate = LocalDate.now();
        Date date = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        abon.setDateProchainFacturation(date);
        abonnementRepository.save(abon);

      }
      if (typeDemande.equals("Changement débit")
          && finddemande.getEtatTT().equals(DBEtatTT.Clôturée)
          || typeDemande.equals("Changement débit")
              && finddemande.getEtatTT().equals(DBEtatTT.Mise_en_service)) {
        result = true;
        Statut status = statutRepository.findStatutByNomStatut(NomStatutChifco.VALID);
        finddemande.setStatut(status);
        operationAbonnementRepository.save(finddemande);
        this.makeChangementDébit(finddemande, user);
      }
    }
    return result;
  }

  private void sendSmsConstructionLigne(ArrayList<String> arrayTelephoneConstructionLigne,
      String referenceChifco) {
    // TODO Auto-generated method stub

  }

  @Override
  public void sendNewrefTT(String referencett, Long idabon, User user,
      RedirectAttributes redirectAttrs) {
    String typeDemande = "";
    OperationAbonnement finddemande = operationAbonnementRepository.findById(idabon).get();
    if (finddemande.getTypeDemande().equals("M")) {
      typeDemande = "Migration";
    } else if (finddemande.getTypeDemande().equals("T")) {
      typeDemande = "Tranfert";
    } else {
      typeDemande = "Changement débit";
    }
    if (referencett != null && !referencett.equals("")) {
      List<DemandeAbonnement> checkReferenceExiste =
          demandeAbonnementRepository.findDemandeAbonnementByreferenceTT(referencett);
      if (checkReferenceExiste.size() == 0) {
        finddemande.setEtatTT(DBEtatTT.Enregister);
        finddemande.setReferenceTT(referencett);
        finddemande.setStatut(statutRepository.findStatutByNomStatut(NomStatutChifco.WAIT_TT));
        Abonnement abonnement =
            abonnementRepository.findAbonnementByClientid(finddemande.getAbonnementId());
        DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
        if (typeDemande.equals("Migration")) {
          demandeAbonnementHistory.setAdresse(abonnement.getAdresse());
        } else {
          demandeAbonnementHistory.setAdresse(finddemande.getAdresse());
        }
        demandeAbonnementHistory.setCin(abonnement.getCin());
        demandeAbonnementHistory.setDescription(
            "Le statut de demande de" + typeDemande + " passe à  «" + DBEtatTT.Enregister
                + "» et status chifco à «demande envoyé à TT». et nouvelle référence tt :"
                + referencett);
        demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
        demandeAbonnementHistory.setLastName(abonnement.getLastName());
        demandeAbonnementHistory.setCreatedBy(user);
        demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
        finddemande.setAbonnementId(abonnement.getClientid());
        operationAbonnementRepository.save(finddemande);
        redirectAttrs.addFlashAttribute("message", "sendtt");
      } else {
        redirectAttrs.addFlashAttribute("message", "sendttReferenceExiste");
      }

    }

  }

  public void makeChangementDébit(OperationAbonnement demandechangementDébit, User connected) {
    try {
      LOGGER.info("<--------------------Start of changement débit en abonnement process -------->");
      LOGGER.info("input:demandeReferenceChifco =" + demandechangementDébit.getReferenceChifco()
          + ", user connected ID:" + connected.getUserid());
      Abonnement oldAbonnement = AbonnementService
          .findAbonnementByReferenceClient(demandechangementDébit.getReferenceChifco());
      Facture lastFacture = factureRepository
          .findTopByAbonnement_clientidOrderByFactureIdDesc(oldAbonnement.getClientid());
      Tarification tarificationOldpack =
          tarificationServices.getTarificationBypackId(oldAbonnement.getPack().getPackId());
      Double produitprixHtOldPack = tarificationOldpack.getPrixUnitaire();
      Tarification tarificationNewpack = tarificationServices
          .getTarificationBypackId(demandechangementDébit.getPack().getPackId());
      Double produitprixHtNewPack = tarificationNewpack.getPrixUnitaire();
      Long DateMonthPack =
          CrmUtils.DateDifference(lastFacture.getDateDeDebut(), lastFacture.getDateDeFin());

      LocalDate currentDate = LocalDate.now();
      Date date = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
      Long DayConsomeeOldPack = CrmUtils.DateDifference(lastFacture.getDateDeDebut(), date);

      Long DayConsomeeNewPack = CrmUtils.DateDifference(date, lastFacture.getDateDeFin());
      Double oldPackDayPrice = CrmUtils.CalculePrixPackConsomeParJours(produitprixHtOldPack,
          DayConsomeeOldPack, DateMonthPack);
      Double newPackDayPrice = CrmUtils.CalculePrixPackConsomeParJours(produitprixHtNewPack,
          DayConsomeeNewPack, DateMonthPack);



      Double PrixTotalMigration = (newPackDayPrice + oldPackDayPrice) - produitprixHtOldPack;
      MigrationFacture newFactureMigrationCalcule = new MigrationFacture();
      newFactureMigrationCalcule.setMontantHt(
          PrixTotalMigration * oldAbonnement.getTypePaiement().getNombreMoisTypePaiement());
      newFactureMigrationCalcule.setClientid(oldAbonnement.getClientid());
      newFactureMigrationCalcule
          .setMontantTva((PrixTotalMigration * tarificationNewpack.getTaxe()) * 0.01);
      newFactureMigrationCalcule.setTypeCalcule(typeCalcluleMigrationFacture.CHANGEMENTDEBIT);
      newFactureMigrationCalcule
          .setNameMigration("Complément changement de " + oldAbonnement.getPack().getDebitPack()
              + "  à " + demandechangementDébit.getPack().getDebitPack() + " débit");
      newFactureMigrationCalcule.setPercentTva(7L);
      migrationFactureRepository.save(newFactureMigrationCalcule);


      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(oldAbonnement.getAdresse());
      demandeAbonnementHistory.setCin(oldAbonnement.getCin());
      demandeAbonnementHistory
          .setDescription("Le changement de débit de demande a été effectuée avec succées de  «"
              + oldAbonnement.getPack().getDebitPack() + "»  à un nouveau débit «"
              + demandechangementDébit.getPack().getDebitPack() + "».");

      demandeAbonnementHistory.setFirstName(oldAbonnement.getFirstName());
      demandeAbonnementHistory.setLastName(oldAbonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(connected);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      oldAbonnement.setPack(demandechangementDébit.getPack());
      oldAbonnement.setIsChangementDebit(true);
      abonnementRepository.save(oldAbonnement);

      LOGGER.info("<--------------------End of changement débit process -------->");
    } catch (Exception e) {
      LOGGER.error(
          "OperationAbonnementServiceImpl.makechangement debit exception for demande changement debit:"
              + demandechangementDébit.getReferenceChifco());
    }
  }

  public void makeTransfert(OperationAbonnement demandeTransfert, User connected) {
    try {
      LOGGER.info("<--------------------Start of transfert process -------->");
      LOGGER.info("input:demandeReferenceChifco =" + demandeTransfert.getReferenceChifco()
          + ", user connected ID:" + connected.getUserid());
      Abonnement oldAbonnement =
          AbonnementService.findAbonnementByReferenceClient(demandeTransfert.getReferenceChifco());
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(demandeTransfert.getAdresse());
      demandeAbonnementHistory.setCin(oldAbonnement.getCin());
      demandeAbonnementHistory
          .setDescription("Le transfert de demande a été effectuée avec succées d'adresse  «"
              + oldAbonnement.getAdresse() + "»  à une nouvelle adresse «"
              + demandeTransfert.getAdresse() + "», et le numéro fixe de «"
              + oldAbonnement.getTelFixe() + "» à «" + demandeTransfert.getTelFixe() + "».");
      demandeAbonnementHistory.setFirstName(oldAbonnement.getFirstName());
      demandeAbonnementHistory.setLastName(oldAbonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(connected);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      LocalDate currentDate = LocalDate.now();
      if (demandeTransfert.getHasRaccordement()) {
        MigrationFacture newFactureMigrationCalcule = new MigrationFacture();
        newFactureMigrationCalcule.setMontantHt(Double.parseDouble(prixhtRacordment));
        newFactureMigrationCalcule.setClientid(oldAbonnement.getClientid());
        newFactureMigrationCalcule
            .setMontantTva((Double.parseDouble(prixhtRacordment) * 19) * 0.01);
        newFactureMigrationCalcule.setTypeCalcule(typeCalcluleMigrationFacture.MIGRATION);
        newFactureMigrationCalcule.setPercentTva(19L);
        newFactureMigrationCalcule.setNameMigration("Frais de transfert Tunisie Telecom");
        migrationFactureRepository.save(newFactureMigrationCalcule);
      }
      Date date = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
      oldAbonnement.setDateProchainFacturation(date);
      oldAbonnement.setAdresse(demandeTransfert.getAdresse());
      oldAbonnement.setFirstName(demandeTransfert.getFirstName());
      oldAbonnement.setLastName(demandeTransfert.getLastName());
      oldAbonnement.setGouvernorat(demandeTransfert.getGouvernorat());
      oldAbonnement.setHasRaccordement(demandeTransfert.getHasRaccordement());
      oldAbonnement.setProprietaire(demandeTransfert.getProprietaire());
      oldAbonnement.setTelFixe(demandeTransfert.getTelFixe());
      oldAbonnement.setVille(demandeTransfert.getVille());
      oldAbonnement.setIsMigration(true);

      abonnementRepository.save(oldAbonnement);
      LOGGER.info("<--------------------End of transfert process -------->");
    } catch (Exception e) {
      LOGGER.error("OperationAbonnementServiceImpl.makeTransfert exception for demandeTransfert:"
          + demandeTransfert.getReferenceChifco());
    }
  }

  @Override
  public void makeMigration(OperationAbonnement demandeMigration, User connected) {
    try {
      LOGGER.info("<--------------------Start of migration -------->");
      LOGGER.info("input:demandeReferenceChifco =" + demandeMigration.getReferenceChifco()
          + ", user connected ID:" + connected.getUserid());
      Abonnement oldAbonnement =
          AbonnementService.findAbonnementByReferenceClient(demandeMigration.getReferenceChifco());
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      Boolean resultRadius = this.changerModemRadius(oldAbonnement.getClientid(),
          demandeMigration.getModem().getNumSerie(), connected);
      if (resultRadius) {
        demandeAbonnementHistory.setAdresse(oldAbonnement.getAdresse());
        demandeAbonnementHistory.setCin(oldAbonnement.getCin());
        demandeAbonnementHistory
            .setDescription("Le migration a été effectuée avec succées de pack  «"
                + oldAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode()
                + "»  à un nouveau pack «"
                + demandeMigration.getPack().getCategoriePack().getCategorieProduitInternetCode()
                + "»" + ",l'email à changé de « " + oldAbonnement.getLoginModem() + "» à «"
                + demandeMigration.getModem().getEmail() + "» et le mot de passe de «"
                + oldAbonnement.getPassword() + "» à «" + demandeMigration.getModem().getPassword()
                + "».");
        String msg5 = "Ce modem est attribué à ce client " + oldAbonnement.getReferenceClient()
            + " au lieu du modem " + oldAbonnement.getModem().getNumSerie();
        modemHistoryService.save(msg5, connected, demandeMigration.getModem());
        demandeAbonnementHistory.setFirstName(oldAbonnement.getFirstName());
        demandeAbonnementHistory.setLastName(oldAbonnement.getLastName());
        demandeAbonnementHistory.setCreatedBy(connected);
        demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);


        Facture lastFacture = factureRepository
            .findTopByAbonnement_clientidOrderByFactureIdDesc(oldAbonnement.getClientid());
        Tarification tarificationOldpack =
            tarificationServices.getTarificationBypackId(oldAbonnement.getPack().getPackId());
        Double produitprixHtOldPack = tarificationOldpack.getPrixUnitaire();
        Tarification tarificationNewpack =
            tarificationServices.getTarificationBypackId(demandeMigration.getPack().getPackId());
        Double produitprixHtNewPack = tarificationNewpack.getPrixUnitaire();
        Long DateMonthPack =
            CrmUtils.DateDifference(lastFacture.getDateDeDebut(), lastFacture.getDateDeFin());

        LocalDate localDate = LocalDate.now();

        // Convert LocalDate to LocalDateTime
        LocalDateTime localDateTime = localDate.atStartOfDay();

        // Convert LocalDateTime to Date
        Date dateNow = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Long DayConsomeeOldPack = CrmUtils.DateDifference(lastFacture.getDateDeDebut(), dateNow);
        LOGGER.info("DayConsomeeOldPack" + lastFacture.getDateDeDebut() + "new Date()" + dateNow);

        Long DayConsomeeNewPack = CrmUtils.DateDifference(dateNow, lastFacture.getDateDeFin());
        Double oldPackDayPrice = CrmUtils.CalculePrixPackConsomeParJours(produitprixHtOldPack,
            DayConsomeeOldPack, DateMonthPack);
        Double newPackDayPrice = CrmUtils.CalculePrixPackConsomeParJours(produitprixHtNewPack,
            DayConsomeeNewPack, DateMonthPack);
        LOGGER.info(
            " oldPackDayPrice " + produitprixHtOldPack + " dayconsommeenewDay " + DayConsomeeOldPack
                + "monthday " + DateMonthPack + " total Calcule old " + oldPackDayPrice);
        LOGGER.info(
            "newPackDayPrice " + produitprixHtNewPack + " dayconsommeenewDay " + DayConsomeeNewPack
                + " monthday " + DateMonthPack + " Total Calcule new " + newPackDayPrice);

        Double PrixTotalMigration = (newPackDayPrice + oldPackDayPrice) - produitprixHtOldPack;
        LOGGER.info("PrixTotalMigration" + PrixTotalMigration);

        MigrationFacture newFactureMigrationCalcule = new MigrationFacture();
        newFactureMigrationCalcule.setMontantHt(PrixTotalMigration
            * lastFacture.getAbonnement().getTypePaiement().getNombreMoisTypePaiement());
        newFactureMigrationCalcule.setClientid(oldAbonnement.getClientid());
        newFactureMigrationCalcule
            .setMontantTva((PrixTotalMigration * tarificationNewpack.getTaxe()) * 0.01);
        newFactureMigrationCalcule.setTypeCalcule(typeCalcluleMigrationFacture.MIGRATION);
        newFactureMigrationCalcule.setNameMigration(
            "Complément migration   de débit" + oldAbonnement.getPack().getDebitPack() + "  à "
                + demandeMigration.getPack().getDebitPack() + " débit");
        newFactureMigrationCalcule.setPercentTva(7L);
        migrationFactureRepository.save(newFactureMigrationCalcule);

        // oldAbonnement.setAncienLogin(oldAbonnement.getLoginModem());
        if (oldAbonnement.getLoginModem() != null) {
          String newAncienLogin = oldAbonnement.getLoginModem();
          String currentAncienLogin = oldAbonnement.getAncienLogin();

          if (currentAncienLogin != null && !currentAncienLogin.isEmpty()) {
            oldAbonnement.setAncienLogin(currentAncienLogin + "," + newAncienLogin);
          } else {
            oldAbonnement.setAncienLogin(newAncienLogin);
          }
        }
        oldAbonnement.setModem(demandeMigration.getModem());
        oldAbonnement.setLoginModem(demandeMigration.getModem().getEmail());
        oldAbonnement.setPassword(demandeMigration.getModem().getPassword());
        oldAbonnement.setPack(demandeMigration.getPack());
        oldAbonnement.setStatut(demandeMigration.getStatut());
        oldAbonnement.setIsMigration(true);
        abonnementRepository.save(oldAbonnement);
      } else {
        LOGGER.info("<--------------------we have a problem in insert radius -------->");
      }
      LOGGER.info("<--------------------End of migration process -------->");
    } catch (

    Exception e) {
      LOGGER.error("une exception lors de migration vers abonnement:" + e.getMessage());
    }

  }

  private Boolean changerModemRadius(Long clientId, String numSerieModem, User user) {
    try {

      logger.info("Debut changement modem. clientId :" + clientId + ", numSerieModem : "
          + numSerieModem + " , user : ", user.getUserid());
      Modem newModem = modemRepository.findAllByNumSerie(numSerieModem);

      Abonnement ab = abonnementRepository.getById(clientId);
      if (newModem == null) {
        logger.error("Le modem avec le numéro de série {} n'a pas été trouvé.", numSerieModem);
        logger.info("Fin de la methode changerModem.");
        return false;
      } else {
        Modem oldModem = ab.getModem();
        if (!newModem.getNumSerie().equals(ab.getModem().getNumSerie())
            && (!newModem.getModelModem().equals("XDSL")
                || !ab.getModem().getModelModem().equals("XDSL"))) {

          oldModem.setAffecteClient(null);
          oldModem.setStatus(true);
          String msg1 = "Le modem a été remplacé par le modem " + newModem.getNumSerie();
          String msg2 = "Suite à un changement de modem, ce modem passe à l'état Inactif";
          modemHistoryService.save(msg1, user, oldModem);
          modemHistoryService.save(msg2, user, oldModem);
          modemRepository.save(oldModem);
          logger.info(msg1);
        }
        Radusergroup radusgroup = radusergroupRepository.findAllByUsername(ab.getLoginModem());
        if (radusgroup != null) {
          radusgroup.setUsername(newModem.getEmail());
          if (newModem.getModelModem().equals("VDSL") || newModem.getModelModem().equals("XDSL")) {
            radusgroup.setGroupname("VDSLPPP");
          } else {
            radusgroup.setGroupname("FASTPPP");
          }
          radusergroupRepository.save(radusgroup);
          logger.info("Modification dans la table radusgroup avec nouveau username: "
              + newModem.getEmail());
        } else {
          logger.error("Modification dans la table radusgroup avec nouveau username non aboutie: "
              + newModem.getEmail());
        }
        Radcheck radcheck1 =
            radcheckRepository.findUsernameAndAttribute(ab.getLoginModem(), "Cleartext-Password");
        Radcheck radcheck2 =
            radcheckRepository.findUsernameAndAttribute(ab.getLoginModem(), "Expiration");
        if (radcheck1 != null) {
          radcheck1.setUsername(newModem.getEmail());
          radcheck1.setValue(newModem.getPassword());
          radcheckRepository.save(radcheck1);
          logger.info("Modification dans la table radcheck Cleartext-Password avec username:"
              + newModem.getEmail());
        } else {
          logger.error(
              "Modification dans la table radcheck Cleartext-Password non aboutie avec nouveau username: "
                  + newModem.getEmail());
        }
        if (radcheck2 != null) {
          radcheck2.setUsername(newModem.getEmail());
          radcheckRepository.save(radcheck2);
          logger.info("Modification dans la table radcheck d'expiration");

        } else {
          logger.error(
              "Modification dans la table radcheck d'expiration non aboutie avec nouveau username: "
                  + newModem.getEmail());
        }
        return true;
      }
    } catch (Exception e) {
      logger.error("une erreur se produite lors de l'insertion dans radius");
      return false;
    }
  }

  @Override
  public String getUpdateDemandeAbonnement(Long packId, Long operationId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      LOGGER.info(
          "<--------------------Start modification of migration Id:" + operationId + " -------->");
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      OperationAbonnement DemandeMigration =
          operationAbonnementRepository.findById(operationId).get();
      Pack pack = packRepository.findById(packId).get();
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(DemandeMigration.getAdresse());
      demandeAbonnementHistory.setCin(DemandeMigration.getCin());
      demandeAbonnementHistory.setDescription("La demande de migration a été changé de pack « "
          + DemandeMigration.getPack().getTitle() + "("
          + DemandeMigration.getPack().getCategoriePack().getCategorieProduitInternetCode()
          + ") » à « " + pack.getTitle() + "("
          + pack.getCategoriePack().getCategorieProduitInternetCode() + ") »");
      demandeAbonnementHistory.setFirstName(DemandeMigration.getFirstName());
      demandeAbonnementHistory.setLastName(DemandeMigration.getLastName());
      demandeAbonnementHistory.setCreatedBy(user);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      DemandeMigration.setPack(pack);
      DemandeMigration.setCategorieProduitInternet(pack.getCategoriePack());
      DemandeMigration.setEditedBy(user.getUserid());
      operationAbonnementRepository.save(DemandeMigration);

      LOGGER.info("<--------------------end of modification de migration -------->");
    }
    return "redirect:/operationAbonnement/alldemandesmigration";
  }

  @Override
  public void saveNewMigration(Long packid, Long clientid) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User userconected = userRepository.findUsersByEmail(currentUser);
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
    Pack pack = packRepository.getById(packid);
    Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.SIGNED_DOC);
    OperationAbonnement DemandeMigration = new OperationAbonnement();
    DemandeMigration.setLastName(abonnement.getFirstName());
    DemandeMigration.setFirstName(abonnement.getLastName());
    DemandeMigration.setEmail(abonnement.getEmail());
    DemandeMigration.setCin(abonnement.getCin());
    DemandeMigration.setVille(abonnement.getVille());
    DemandeMigration.setGouvernorat(abonnement.getGouvernorat());
    DemandeMigration.setAdresse(abonnement.getAdresse());
    DemandeMigration.setTelMobile(abonnement.getTelMobile());
    DemandeMigration.setTelFixe(abonnement.getTelFixe());
    DemandeMigration.setPack(pack);
    DemandeMigration.setEtatTT("");
    DemandeMigration.setTypeDemande("M");
    DemandeMigration.setStatut(statut);
    DemandeMigration.setTypePaiement(abonnement.getTypePaiement());
    DemandeMigration.setPhotoCin1(abonnement.getPhotoCin1());
    DemandeMigration.setPhotoCin2(abonnement.getPhotoCin2());
    DemandeMigration.setUser(userconected);
    DemandeMigration.setCategorieProduitInternet(pack.getCategoriePack());
    DemandeMigration.setAssignedTo(abonnement.getAssignedTo());
    DemandeMigration.setProfession(abonnement.getProfession());
    DemandeMigration.setReferenceChifco(abonnement.getReferenceClient());
    DemandeMigration.setCodePostale(abonnement.getCodePostale());
    DemandeMigration.setAbonnementId(clientid);
    DemandeMigration.setProprietaire(abonnement.getProprietaire());
    DemandeMigration.setFax(abonnement.getFax());
    DemandeMigration
        .setAncien_value(abonnement.getPack().getCategoriePack().getCategorieProduitInternetCode());
    DemandeMigration.setHasRaccordement(false);
    DemandeMigration.setDateNaissance(abonnement.getDateNaissance());
    DemandeMigration.setSituationFamiliale(abonnement.getSituationFamiliale());
    DemandeMigration.setTelMobile2(abonnement.getTelMobile2());
    DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
    demandeAbonnementHistory.setAdresse(abonnement.getAdresse());
    demandeAbonnementHistory.setCin(abonnement.getCin());
    demandeAbonnementHistory.setDescription("Création de la demande de migration");
    demandeAbonnementHistory.setFirstName(userconected.getFirstName());
    demandeAbonnementHistory.setLastName(userconected.getLastName());
    demandeAbonnementHistory.setCreatedBy(userconected);
    demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
    abonnementRepository.save(abonnement);
    this.save(DemandeMigration);
  }

  @Override
  public Boolean updateStatutMigration(Long operationAbonId, String motifRefus, String modemId,
      Long telefixe, RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    String typeDemande = "";
    Boolean result = false;
    User user = userRepository.findUsersByEmail(currentUser);
    OperationAbonnement DemandeMigration =
        operationAbonnementRepository.findById(operationAbonId).get();
    if (DemandeMigration.getTypeDemande().equals("M")) {
      typeDemande = "Migration";
    } else if (DemandeMigration.getTypeDemande().equals("T")) {
      typeDemande = "Transfert";
    } else {
      typeDemande = "Changement débit";
    }
    Abonnement abonnement =
        abonnementRepository.findAbonnementByReferenceClient(DemandeMigration.getReferenceChifco());
    if ((DemandeMigration.getEtatTT().equals(DBEtatTT.ActivationService)
        || DemandeMigration.getEtatTT().equals(DBEtatTT.Mise_en_service))
        && (DemandeMigration.getStatut()
            .equals(statutRepository.findStatutByNomStatut(NomStatutChifco.VALID)))
        && typeDemande.equals("Migration")) {
      result = true;
      DemandeMigration.setEtatTT(DBEtatTT.Clôturée);
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(abonnement.getAdresse());
      demandeAbonnementHistory.setCin(abonnement.getCin());
      demandeAbonnementHistory
          .setDescription("Le statut de demande de " + typeDemande + " passe à « Clôturée »");
      demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
      demandeAbonnementHistory.setLastName(abonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(user);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      this.save(DemandeMigration);
      redirectAttrs.addFlashAttribute("message", "Clôturée");
    }
    if (typeDemande.equals("Migration")
        && (DemandeMigration.getPack().getCategoriePack().getCategorieProduitInternetCode())
            .equals("GPON")
        && telefixe != null
        && ((DemandeMigration.getEtatTT().equals(DBEtatTT.Instance)
            && (DemandeMigration.getStatut())
                .equals(statutRepository.findStatutByNomStatut(NomStatutChifco.WAIT_TT)))
            || (DemandeMigration.getEtatTT().equals(DBEtatTT.Enregister)
                && DemandeMigration.getStatut()
                    .equals(statutRepository.findStatutByNomStatut(NomStatutChifco.WAIT_TT))))) {
      result = true;
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(DemandeMigration.getAdresse());
      demandeAbonnementHistory.setCin(abonnement.getCin());
      demandeAbonnementHistory
          .setDescription("Le statut de demande de " + typeDemande + " passe de «"
              + DemandeMigration.getEtatTT() + "» à « Raccordement» et num fixe :" + telefixe);
      demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
      demandeAbonnementHistory.setLastName(abonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(user);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      DemandeMigration.setEtatTT(DBEtatTT.Raccordement);
      DemandeMigration.setTelFixe(telefixe);
      DemandeMigration.setMotifRefus(null);
      operationAbonnementRepository.save(DemandeMigration);

    }
    if (typeDemande.equals("Migration") && (modemId != null && !modemId.isEmpty())
        && (DemandeMigration.getPack().getCategoriePack().getCategorieProduitInternetCode())
            .equals("GPON")
        && DemandeMigration.getEtatTT().equals(DBEtatTT.ActivationService)
        && (DemandeMigration.getStatut())
            .equals(statutRepository.findStatutByNomStatut(NomStatutChifco.INSTALLED))) {
      result = true;
      Boolean verifRev = true;
      Boolean verifPos = true;
      Boolean verifchefSec = true;
      if (user.getRole().getRoleName().equals("ROLE_REVENDEUR")) {
        Long ModemIdLong = Long.parseLong(modemId);
        Optional<Modem> modem2 =
            modemRepository.findActiveModemByIdAndRevendeur(ModemIdLong, user.getUserid());
        if (!modem2.isPresent()) {
          redirectAttrs.addFlashAttribute("message", "Modem n'existe pas dans votre stock");
          verifRev = false;
        }
      }
      if (user.getRole().getRoleName().equals("ROLE_POS")) {
        Long ModemIdLong = Long.parseLong(modemId);
        Optional<Modem> modem2 =
            modemRepository.findActiveModemByIdAndPOS(ModemIdLong, user.getUserid());
        if (!modem2.isPresent()) {
          redirectAttrs.addFlashAttribute("message", "Modem n'existe pas dans votre stock");
          verifPos = false;
        }
      }
      if (user.getRole().getRoleName().equals("ROLE_CHEF_SECTEUR")) {
        Long ModemIdLong = Long.parseLong(modemId);
        Optional<Modem> modem2 =
            modemRepository.findActiveModemByIdAndChefSecteur(ModemIdLong, user.getUserid());
        if (!modem2.isPresent()) {
          redirectAttrs.addFlashAttribute("message", "Modem n'existe pas dans votre stock");
          verifchefSec = false;
        }
      }
      if (verifRev && verifPos && verifchefSec) {
        Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.VALID);
        if (modemId != null && !modemId.isEmpty()) {
          result = true;
          Long longValue = Long.parseLong(modemId);
          Modem modem = modemRepository.findModemBymodemId(longValue);
          DemandeMigration.setModem(modem);
          modem.setAffecteClient(DemandeMigration.getAbonnementId());
          DemandeMigration.setStatut(statut);
          DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
          demandeAbonnementHistory.setAdresse(DemandeMigration.getAdresse());
          demandeAbonnementHistory.setCin(abonnement.getCin());
          demandeAbonnementHistory.setDescription("Le statut de demande de " + typeDemande
              + " passe de «Activation de service» à « Validé»");
          demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
          demandeAbonnementHistory.setLastName(abonnement.getLastName());
          demandeAbonnementHistory.setCreatedBy(user);
          demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
          this.save(DemandeMigration);
          Abonnement abo = abonnementRepository
              .findAbonnementByReferenceClient(DemandeMigration.getReferenceChifco());
          abo.setTelFixe(DemandeMigration.getTelFixe());
          abonnementRepository.save(abo);
          this.makeMigration(DemandeMigration, user);
          redirectAttrs.addFlashAttribute("message", "Modem est affecté");

        } else {
          redirectAttrs.addFlashAttribute("message", "Modem n'existe pas");
        }
      }
    }
    if ((DemandeMigration.getEtatTT().equals("Mise en service TT"))
        && (DemandeMigration.getStatut().getNomStatut().equals("INSTALLED"))) {

      Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.VALID);
      if (typeDemande.equals("Migration")) {
        Boolean verif = true;
        if (modemId != null && !modemId.isEmpty()) {
          if (user.getRole().getRoleName().equals("ROLE_REVENDEUR")) {
            Long ModemIdLong = Long.parseLong(modemId);
            Optional<Modem> modem2 =
                modemRepository.findActiveModemByIdAndRevendeur(ModemIdLong, user.getUserid());
            if (!modem2.isPresent()) {
              verif = false;
              redirectAttrs.addFlashAttribute("message", "Modem n'existe pas dans votre stock");

            }
          }
          if (verif) {
            Long longValue = Long.parseLong(modemId);
            Modem modem = modemRepository.findModemBymodemId(longValue);
            if (modem.getModelModem().equals("XDSL")) {
            	   long modemAccessCount =
                           modemAccessRepository.countByStatusAndModelModem(true,
                        		   DemandeMigration.getPack().getCategoriePack().getCategorieProduitInternetCode());
                   if(modemAccessCount<20)
                   {
                 	  notificationservice.sendSimpleMail(EmailmodemXDSL,"Merci de nous envoyer la liste des accès des modems de ce type: " + DemandeMigration.getPack().getCategoriePack().getCategorieProduitInternetCode(),"Acces modem XDSL:Operation client") ;
                   }
              ModemAccess modemAccess = modemAccessRepository
                  .findFirstModemAccessByStatusAndModelModem(true, DemandeMigration.getPack()
                      .getCategoriePack().getCategorieProduitInternetCode());

              if (modemAccess != null) {
                modem.setEmail(modemAccess.getEmail());
                modem.setPassword(modemAccess.getPassword());
                modemRepository.save(modem);
                modemAccess.setStatus(false);
                modemAccess.setIdModem(modem.getModemId());
                modemAccessRepository.save(modemAccess);

              } else {
                redirectAttrs.addFlashAttribute("message", "Modem n'existe pas dans votre stock");

                return false;
              }
            }
            if (abonnement.getModem() != null) {
              Modem AncienModem =
                  modemRepository.findModemBymodemId(abonnement.getModem().getModemId());
              AncienModem.setAffecteClient(null);
              modemRepository.save(AncienModem);
              String msg =
                  "Le modem «" + AncienModem.getNumSerie() + "» n'est plus affécté au client «"
                      + abonnement.getReferenceClient() + "» à cause d'une migration.";
              modemHistoryService.save(msg, user, AncienModem);
            }
            DemandeMigration.setModem(modem);
            modem.setAffecteClient(DemandeMigration.getAbonnementId());
            modemRepository.save(modem);

            DemandeMigration.setStatut(statut);
            DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
            demandeAbonnementHistory.setAdresse(DemandeMigration.getAdresse());
            demandeAbonnementHistory.setCin(abonnement.getCin());
            demandeAbonnementHistory.setDescription("Le statut de demande de " + typeDemande
                + " passe de «Mise en service» à « validé»");
            demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
            demandeAbonnementHistory.setLastName(abonnement.getLastName());
            demandeAbonnementHistory.setCreatedBy(user);
            demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
            result = true;
            if (DemandeMigration.getTypeDemande().equals("M")) {
              this.save(DemandeMigration);
              this.makeMigration(DemandeMigration, user);
              redirectAttrs.addFlashAttribute("message", "Modem est affecté!");

            }
          }
        } else {
          redirectAttrs.addFlashAttribute("message", "Modem n'existe pas");
        }
      } else {
        result = true;
        DemandeMigration.setEtatTT(DBEtatTT.Clôturée);
        DemandeMigration.setStatut(statut);
        DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
        demandeAbonnementHistory.setAdresse(DemandeMigration.getAdresse());
        demandeAbonnementHistory.setCin(abonnement.getCin());
        demandeAbonnementHistory.setDescription("Le statut de demande de " + typeDemande
            + " passe de «Activation de service» à « Clôturée»");
        demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
        demandeAbonnementHistory.setLastName(abonnement.getLastName());
        demandeAbonnementHistory.setCreatedBy(user);
        demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
        operationAbonnementRepository.save(DemandeMigration);
      }

    }

    if ((DemandeMigration.getEtatTT().equals(DBEtatTT.Attente_Construction))
        && (DemandeMigration.getStatut().getNomStatut().equals(NomStatutChifco.WAIT_TT)
            && DemandeMigration.getTypeDemande().equals("T"))) {
      result = true;
      Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.INSTALLED);
      DemandeMigration.setStatut(statut);
      DemandeMigration.setEtatTT(DBEtatTT.Mise_en_service);
      if (telefixe != null) {
        DemandeMigration.setTelFixe(telefixe);
      }
      this.save(DemandeMigration);
      this.makeTransfert(DemandeMigration, user);
      redirectAttrs.addFlashAttribute("message", "INSTALLED");
    }
    if ((DemandeMigration.getEtatTT().equals("Construction Ligne"))
        && (DemandeMigration.getStatut().getNomStatut().equals("WAIT_TT")) && motifRefus != null) {
      result = true;
      Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);
      DemandeMigration.setStatut(statut);
      DemandeMigration.setEtatTT("Refusé par TT");
      DemandeMigration.setMotifRefus(motifRefus);
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(abonnement.getAdresse());
      demandeAbonnementHistory.setCin(abonnement.getCin());
      demandeAbonnementHistory.setDescription(
          "Le statut de " + typeDemande + " passe de « Envoyé à TT » à « Réfusé par TT »");
      demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
      demandeAbonnementHistory.setLastName(abonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(user);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      this.save(DemandeMigration);
      redirectAttrs.addFlashAttribute("message", "statutanuller");
    }
    if ((DemandeMigration.getEtatTT().equals("Migration"))
        && (DemandeMigration.getStatut().getNomStatut().equals(NomStatutChifco.WAIT_TT))) {
      result = true;
      Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.INSTALLED);
      DemandeMigration.setStatut(statut);
      DemandeMigration.setEtatTT(DBEtatTT.Mise_en_service);
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(DemandeMigration.getAdresse());
      demandeAbonnementHistory.setCin(abonnement.getCin());
      demandeAbonnementHistory.setDescription(
          "Le statut de demande de " + typeDemande + " passe de «migration» à « Mise en Service»");
      demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
      demandeAbonnementHistory.setLastName(abonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(user);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      this.save(DemandeMigration);
    }

    if ((DemandeMigration.getEtatTT().equals("Instance"))
        && (DemandeMigration.getEtatTT().equals("Migration"))
        && (DemandeMigration.getStatut().getNomStatut().equals("WAIT_TT"))) {
      Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);
      result = true;
      DemandeMigration.setStatut(statut);
      DemandeMigration.setEtatTT("Annulée");
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(abonnement.getAdresse());
      demandeAbonnementHistory.setCin(abonnement.getCin());
      demandeAbonnementHistory.setDescription("Le statut de demande de " + typeDemande
          + " passe de « Envoyé à TT » à « Réfusé par TT »");
      demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
      demandeAbonnementHistory.setLastName(abonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(user);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      this.save(DemandeMigration);
      redirectAttrs.addFlashAttribute("message", "statutanuller");
    }
    if ((DemandeMigration.getEtatTT().equals("Instance"))
        && (DemandeMigration.getStatut().getNomStatut().equals("REFUSED"))) {
      result = true;
      Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);
      DemandeMigration.setStatut(statut);
      DemandeMigration.setEtatTT("Annulée");
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(abonnement.getAdresse());
      demandeAbonnementHistory.setCin(abonnement.getCin());
      demandeAbonnementHistory.setDescription(
          "Le statut de demande de " + typeDemande + " passe de « Instance» à « Annulé »");
      demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
      demandeAbonnementHistory.setLastName(abonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(user);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      this.save(DemandeMigration);
      redirectAttrs.addFlashAttribute("message", "statutanuller");
    }
    if ((DemandeMigration.getEtatTT().equals("Instance Commercial"))
        && (DemandeMigration.getStatut().getNomStatut().equals("REFUSED")
            && DemandeMigration.getTypeDemande().equals("T"))) {
      result = true;
      Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);
      DemandeMigration.setStatut(statut);
      DemandeMigration.setEtatTT("Annulée");
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(abonnement.getAdresse());
      demandeAbonnementHistory.setCin(abonnement.getCin());
      demandeAbonnementHistory.setDescription(
          "Le statut de demande de transfert passe de « Envoyé à TT » à « Réfusé par TT »");
      demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
      demandeAbonnementHistory.setLastName(abonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(user);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      this.save(DemandeMigration);
      redirectAttrs.addFlashAttribute("message", "statutanuller");
    }
    return result;
  }

  @Override
  public List<Statut> getallStatusAbonnement() {
    List<String> statuses = Arrays.asList("SIGNED_DOC", "WAIT_TT", "INSTALLED", "ASSIGNED", "VALID",
        "ACTIVE", "REFUSED");
    return statutRepository.findByNomStatutIn(statuses);

  }

  @Override
  public OperationAbonnement getDemandeMigration(Long operationId) {
    try {
      OperationAbonnement operationAb = operationAbonnementRepository.findById(operationId).get();
      return operationAb;
    } catch (Exception e) {

      LOGGER.error("can not find the demand of migration for reaseon: " + e.getMessage());
      return null;
    }
  }

  // ---------********changement débit*****---------------------
  @Override

  public Boolean saveNewDemandeChangementdebit(Long packid, Long clientid) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String currentUser = authentication.getName();
      User userconected = userRepository.findUsersByEmail(currentUser);
      Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
      Pack pack = packRepository.getById(packid);
      Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.SIGNED_DOC);
      OperationAbonnement CH = new OperationAbonnement();
      CH.setLastName(abonnement.getFirstName());
      CH.setFirstName(abonnement.getLastName());
      CH.setEmail(abonnement.getEmail());
      CH.setCin(abonnement.getCin());
      CH.setVille(abonnement.getVille());
      CH.setGouvernorat(abonnement.getGouvernorat());
      CH.setAdresse(abonnement.getAdresse());
      CH.setTelMobile(abonnement.getTelMobile());
      CH.setTelFixe(abonnement.getTelFixe());
      CH.setPack(pack);
      CH.setEtatTT("");
      CH.setTypeDemande("CH");
      CH.setStatut(statut);
      CH.setTypePaiement(abonnement.getTypePaiement());
      CH.setPhotoCin1(abonnement.getPhotoCin1());
      CH.setPhotoCin2(abonnement.getPhotoCin2());
      CH.setUser(userconected);
      CH.setCategorieProduitInternet(pack.getCategoriePack());
      CH.setAssignedTo(abonnement.getAssignedTo());
      CH.setProfession(abonnement.getProfession());
      CH.setReferenceChifco(abonnement.getReferenceClient());
      CH.setCodePostale(abonnement.getCodePostale());
      CH.setAbonnementId(clientid);
      CH.setAncien_value(abonnement.getPack().getDebitPack());
      CH.setProprietaire(abonnement.getProprietaire());
      CH.setIdDecisionDemande(null);
      CH.setFax(abonnement.getFax());
      CH.setHasRaccordement(false);
      CH.setDateNaissance(abonnement.getDateNaissance());
      CH.setSituationFamiliale(abonnement.getSituationFamiliale());
      CH.setTelMobile2(abonnement.getTelMobile2());
      DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
      demandeAbonnementHistory.setAdresse(abonnement.getAdresse());
      demandeAbonnementHistory.setCin(abonnement.getCin());
      demandeAbonnementHistory.setDescription("Création de la demande de changement débit");
      demandeAbonnementHistory.setFirstName(abonnement.getFirstName());
      demandeAbonnementHistory.setLastName(abonnement.getLastName());
      demandeAbonnementHistory.setCreatedBy(userconected);
      demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
      abonnementRepository.save(abonnement);
      this.save(CH);
      return true;
    } catch (Exception e) {
      LOGGER.error("cration new demande changement debit Exception" + e.getMessage());
      return false;
    }


  }

  @Override
  public void editNewDemandeChangementdébit(Long packId, Long operationId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User userconected = userRepository.findUsersByEmail(currentUser);
    OperationAbonnement oldoperation = operationAbonnementRepository.findById(operationId).get();
    Pack pack = packRepository.getById(packId);
    oldoperation.setPack(pack);
    oldoperation.setEtatTT("");
    oldoperation.setTypeDemande("CH");
    oldoperation.setEditedBy(userconected.getUserid());
    DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
    demandeAbonnementHistory.setAdresse(oldoperation.getAdresse());
    demandeAbonnementHistory.setCin(oldoperation.getCin());
    demandeAbonnementHistory.setDescription("Modification de la demande de changement débit");
    demandeAbonnementHistory.setFirstName(oldoperation.getFirstName());
    demandeAbonnementHistory.setLastName(oldoperation.getLastName());
    demandeAbonnementHistory.setCreatedBy(userconected);
    demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
    operationAbonnementRepository.save(oldoperation);
  }

  // ------******Demande de transfert*********----------
  @Override
  public String AddDemandTransfert(Long clientId, String adresse, Gouvernorat gouvernorat,
      Ville ville, PostalCode codepostale, String positionxy, Boolean residence, Model model) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.SIGNED_DOC);
        Abonnement abon = abonnementRepository.findAbonnementByClientid(clientId);
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        userService.returnInfoUserConnected(model);
        OperationAbonnement demandetransfert = new OperationAbonnement();
        demandetransfert.setAbonnementId(clientId);
        demandetransfert.setAdresse(adresse);
        demandetransfert.setCodePostale(codepostale);
        demandetransfert.setFirstName(abon.getFirstName());
        demandetransfert.setLastName(abon.getLastName());
        demandetransfert.setGouvernorat(gouvernorat);
        demandetransfert.setVille(ville);
        demandetransfert.setPositionxy(positionxy);
        demandetransfert.setProprietaire(residence);
        demandetransfert.setHasRaccordement(true);
        demandetransfert.setUser(user);
        demandetransfert.setAncien_value(abon.getAdresse() + " - "
            + abon.getGouvernorat().getGouvernoratName() + " - " + abon.getVille().getVilleName());
        demandetransfert.setTypeDemande("T");
        demandetransfert.setEtatTT("");
        demandetransfert.setAssignedTo(abon.getAssignedTo());
        demandetransfert.setEmail(abon.getEmail());
        demandetransfert.setCin(abon.getCin());
        demandetransfert.setTelMobile(abon.getTelMobile());
        demandetransfert.setTelFixe(abon.getTelFixe());
        demandetransfert.setPack(abon.getPack());
        demandetransfert.setStatut(statut);
        demandetransfert.setTypePaiement(abon.getTypePaiement());
        demandetransfert.setPhotoCin1(abon.getPhotoCin1());
        demandetransfert.setPhotoCin2(abon.getPhotoCin2());
        demandetransfert.setCategorieProduitInternet(abon.getPack().getCategoriePack());
        demandetransfert.setProfession(abon.getProfession());
        demandetransfert.setReferenceChifco(abon.getReferenceClient());
        demandetransfert.setIdDecisionDemande(null);
        demandetransfert.setFax(abon.getFax());
        demandetransfert.setDateProchainFacturation(abon.getDateProchainFacturation());
        demandetransfert.setSituationFamiliale(abon.getSituationFamiliale());
        demandetransfert.setTelMobile2(abon.getTelMobile2());
        DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
        if (adresse != null) {
          demandeAbonnementHistory.setAdresse(adresse);
        } else {
          demandeAbonnementHistory.setAdresse(abon.getAdresse());
        }
        demandeAbonnementHistory.setCin(abon.getCin());
        demandeAbonnementHistory.setDescription("Création de la demande de transfert");
        demandeAbonnementHistory.setLastName(abon.getLastName());
        demandeAbonnementHistory.setFirstName(abon.getFirstName());
        demandeAbonnementHistory.setCreatedBy(user);
        demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
        operationAbonnementRepository.save(demandetransfert);
        abon.setDateProchainFacturation(null);
        abonnementRepository.save(abon);
      }
      return "redirect:/operationAbonnement/alldemandestransfert";
    } catch (Exception e) {
      LOGGER.error(
          "l'ajout de demande de transfert(operationServiceImpl.AddDemandTransfert)a échoué a cause de :"
              + e.getMessage());
      return "error/403";
    }
  }

  @Override
  public String updateDemandTransfert(Long id, Long clientId, String adresse,
      Gouvernorat gouvernorat, Ville ville, PostalCode codepostale, String positionxy,
      Boolean residence, Model model) {
    try {
      OperationAbonnement op = operationAbonnementRepository.findById(id).get();
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      userService.returnInfoUserConnected(model);
      if (!op.equals("") && op != null) {
        op.setAbonnementId(clientId);
        op.setAdresse(adresse);
        op.setAdresse(adresse);
        op.setGouvernorat(gouvernorat);
        op.setVille(ville);
        op.setCodePostale(codepostale);
        op.setPositionxy(positionxy);
        op.setProprietaire(residence);
        op.setHasRaccordement(true);
        DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
        demandeAbonnementHistory.setCin(op.getCin());
        demandeAbonnementHistory.setDescription("Modification de la demande de transfert");
        demandeAbonnementHistory.setLastName(op.getLastName());
        demandeAbonnementHistory.setFirstName(op.getFirstName());
        demandeAbonnementHistory.setCreatedBy(user);
        demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);
        operationAbonnementRepository.save(op);
      }
      return "redirect:/operationAbonnement/alldemandestransfert";
    } catch (Exception e) {
      LOGGER.error(
          "l'ajout de demande de transfert(operationServiceImpl.AddDemandTransfert)a échoué a cause de :"
              + e.getMessage());
      return "error/403";
    }
  }

  @Override
  public boolean duplicateDemande(OperationAbonnement op, User user) {
    try {
      OperationAbonnement newOperationAb = new OperationAbonnement();
      newOperationAb.setAbonnementId(op.getAbonnementId());
      newOperationAb.setAdresse(op.getAdresse());
      newOperationAb.setAssignedTo(op.getAssignedTo());
      newOperationAb.setCategorieProduitInternet(op.getCategorieProduitInternet());
      newOperationAb.setCin(op.getCin());
      newOperationAb.setCodePostale(op.getCodePostale());
      newOperationAb.setContratPdf(op.getContratPdf());
      newOperationAb.setDateNaissance(op.getDateNaissance());
      newOperationAb.setDateProchainFacturation(null);
      newOperationAb.setEditedBy(null);
      newOperationAb.setEmail(op.getEmail());
      newOperationAb.setFax(op.getFax());
      newOperationAb.setFirstName(op.getFirstName());
      newOperationAb.setGouvernorat(op.getGouvernorat());
      newOperationAb.setHasRaccordement(op.getHasRaccordement());
      newOperationAb.setIsActive(op.getIsActive());
      newOperationAb.setIsFraisRaccordementTT(op.getIsFraisRaccordementTT());
      newOperationAb.setLastName(op.getLastName());
      newOperationAb.setModem(op.getModem());
      newOperationAb.setNbFaisApayeReccardement(op.getNbFaisApayeReccardement());
      newOperationAb.setPack(op.getPack());
      newOperationAb.setPhotoCin1(op.getPhotoCin1());
      newOperationAb.setPhotoCin2(op.getPhotoCin2());
      newOperationAb.setPositionxy(op.getPositionxy());
      newOperationAb.setProfession(op.getProfession());
      newOperationAb.setProprietaire(op.getProprietaire());
      newOperationAb.setRefClientSite(op.getRefClientSite());
      newOperationAb.setReferenceChifco(op.getReferenceChifco());
      newOperationAb.setReferenceTT("");
      newOperationAb.setSituationFamiliale(op.getSituationFamiliale());
      newOperationAb.setTelFixe(op.getTelFixe());
      newOperationAb.setTelMobile(op.getTelMobile());
      newOperationAb.setTelMobile2(op.getTelMobile2());
      newOperationAb.setTrancheRaccordement(op.getTrancheRaccordement());
      newOperationAb.setTrancheRaccordementSelected(op.getTrancheRaccordementSelected());
      newOperationAb.setTypeDemande(op.getTypeDemande());
      newOperationAb.setTypePaiement(op.getTypePaiement());
      newOperationAb.setUser(user);
      newOperationAb.setAncien_value(op.getAncien_value());
      newOperationAb.setVille(op.getVille());
      newOperationAb.setWithIpFix(op.getWithIpFix());
      Statut statut = statutRepository.findStatutByNomStatut(NomStatutChifco.SIGNED_DOC);
      newOperationAb.setStatut(statut);
      newOperationAb.setEtatTT("");
      operationAbonnementRepository.save(newOperationAb);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public Boolean affectRevendeur(Long clientid, String codeRevendeur, String emailRevendeur,
      String identificationFiscale) {
    String CodeRevendeur = null;
    String EmailRevendeur = null;
    String IdentificationFiscale = null;
    if (!codeRevendeur.isEmpty()) {
      CodeRevendeur = codeRevendeur;
    }
    if (!emailRevendeur.isEmpty()) {
      EmailRevendeur = emailRevendeur;
    }
    if (!identificationFiscale.isEmpty()) {
      IdentificationFiscale = identificationFiscale;
    }

    List<User> ListeAbonement = userRepository.findUserByEmailOrEmailOrIdentification(CodeRevendeur,
        EmailRevendeur, IdentificationFiscale);
    if (ListeAbonement.size() > 1 || ListeAbonement.size() < 1) {
      return false;
    } else {
      OperationAbonnement myAbonnement = operationAbonnementRepository.findById(clientid).get();
      myAbonnement.setAssignedTo(ListeAbonement.get(0));
      operationAbonnementRepository.save(myAbonnement);
      return true;
    }

  }

  @Override
  public Boolean affectOneRevendeur(String idRevendeurToAffected, Long demandeId) {
    User userToAffected = userRepository.getById(Long.parseLong(idRevendeurToAffected));
    OperationAbonnement myAbonnement = operationAbonnementRepository.findById(demandeId).get();
    if (myAbonnement != null && userToAffected != null) {
      myAbonnement.setAssignedTo(userToAffected);
      operationAbonnementRepository.save(myAbonnement);
      return true;
    } else
      return false;
  }

  @Override
  public List<Long> addAllIdToExport(String filterrecherche, String typeDemande,
      HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

    String refChif = null;
    String refTT = null;
    String cin = null;
    String prenom = null;
    String nom = null;
    Long tel = null;
    Long villes = null;
    Long gouvernorat = null;
    Long professions = null;
    Long categories = null;
    Long produit = null;
    Long statutListfiltre = null;
    String statutTTListfiltre = null;
    Date datedebut = null;
    Date datefin = null;
    Long CreePar = null;


    JSONObject obj = new JSONObject(filterrecherche);
    if (!Objects.equals(obj.getString("prenom"), "") && obj.getString("prenom") != null) {
      prenom = obj.getString("prenom").trim();
    }
    if (!Objects.equals(obj.getString("nom"), "") && obj.getString("nom") != null) {
      nom = obj.getString("nom").trim().toLowerCase();
    }
    if (!Objects.equals(obj.getString("statutListfiltre"), "")
        && obj.getString("statutListfiltre") != null) {
      statutListfiltre = obj.getLong("statutListfiltre");
    }
    if (!Objects.equals(obj.getString("statutTTListfiltre"), "")
        && obj.getString("statutTTListfiltre") != null) {
      statutTTListfiltre = obj.getString("statutTTListfiltre");
    }
    if (!Objects.equals(obj.getString("villes"), "") && obj.getString("villes") != null) {
      villes = obj.getLong("villes");
    }
    if (!Objects.equals(obj.getString("gouvernorat"), "") && obj.getString("gouvernorat") != null) {
      gouvernorat = obj.getLong("gouvernorat");
    }
    if (!Objects.equals(obj.getString("cin"), "") && obj.getString("cin") != null) {
      cin = obj.getString("cin").trim().toLowerCase();
    }
    if (!Objects.equals(obj.getString("tel"), "") && obj.getString("tel") != null) {
      tel = obj.getLong("tel");
    }
    if (!Objects.equals(obj.getString("refChif"), "") && obj.getString("refChif") != null) {
      refChif = obj.getString("refChif").trim().toLowerCase();
    }
    if (!Objects.equals(obj.getString("refTT"), "") && obj.getString("refTT") != null) {
      refTT = obj.getString("refTT").trim().toLowerCase();
    }
    if (!Objects.equals(obj.getString("professions"), "") && obj.getString("professions") != null) {
      professions = obj.getLong("professions");
    }
    if (!Objects.equals(obj.getString("categories"), "") && obj.getString("categories") != null) {
      categories = obj.getLong("categories");
    }
    if (!Objects.equals(obj.getString("produit"), "") && obj.getString("produit") != null) {
      produit = obj.getLong("produit");
    }
    if (!Objects.equals(obj.getString("datedebut"), "") && obj.getString("datedebut") != null) {
      datedebut = CrmUtils.convertStringToDate(obj.getString("datedebut"));
    }
    if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
      datefin = CrmUtils.convertStringToLocalDateTime(obj.getString("datefin"));
    }

    if (!Objects.equals(obj.getString("CreePar"), "") && obj.getString("CreePar") != null) {
      CreePar = obj.getLong("CreePar");

    }


    List<Long> listesdesIdsfromrequest = new ArrayList<>();

    // ROLE_ADMINISTRATEUR || ROLE_POS
    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {

      List<String> arrayOfStrings = new ArrayList<>();


      listesdesIdsfromrequest = operationAbonnementRepository.findAllToExportAdmin(prenom, nom,
          statutListfiltre, statutTTListfiltre, villes, gouvernorat, cin, tel, refChif, refTT,
          professions, datedebut, datefin, categories, produit, CreePar, typeDemande);

      // ajouter 0 à la fin d'une liste pour checked button checkbox 'select-all' dans front lorsque
      // 0 existe
      // listesdesIds.add((long) 0);

    } // ROLE_REVENDEUR
    else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_RETAIL")) {
      listesdesIdsfromrequest = operationAbonnementRepository.findAllToExportRevendeur(prenom, nom,
          statutListfiltre, statutTTListfiltre, villes, gouvernorat, cin, tel, refChif, refTT,
          professions, datedebut, datefin, user.getRole().getRoleId(), user.getUserid(), categories,
          produit, typeDemande);
      // ajouter 0 à la fin d'une liste pour checked button checkbox 'select-all' dans front lorsque
      // 0 existe
      // listesdesIds.add((long) 0);

    } // ROLE_DISTRIBUTEUR
    else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
      listesdesIdsfromrequest = operationAbonnementRepository.findAllToExportDistributeur(prenom,
          nom, statutListfiltre, statutTTListfiltre, villes, gouvernorat, cin, tel, refChif, refTT,
          professions, datedebut, datefin, user.getUserid(), user.getUserid(), categories, produit,
          typeDemande);
      // ajouter 0 à la fin d'une liste pour checked button checkbox 'select-all' dans front lorsque
      // 0 existe
      // listesdesIds.add((long) 0);
    }
    List<Long> listesdesIds = new ArrayList<>();
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      listesdesIds.addAll(listesdesIdsfromrequest);
    } else {
      listesdesIds = (List<Long>) request.getSession().getAttribute("listedes_ids");
      LOGGER.info("listedes_ids selected  from request: " + listesdesIdsfromrequest);
      for (Long id : listesdesIdsfromrequest) {

        if (listesdesIds.contains(id) == false) {
          listesdesIds.add(id);
        }
      }
    }
    request.getSession().setAttribute("listedes_ids", listesdesIds);
    LOGGER.info("listedes_ids else: " + listesdesIds);
    return listesdesIds;
  }

  @Override
  public void exportToExcel(String typeDemande, HttpServletRequest request,
      HttpServletResponse response) {
    try {
      if (request.getSession().getAttribute("listedes_ids") != null) {
        if (!request.getSession().getAttribute("listedes_ids").equals("")) {
          List<Long> listedes_ids = (List<Long>) request.getSession().getAttribute("listedes_ids");
          /*
           * if (listedes_ids.get(listedes_ids.size() - 1) == 0) {
           * listedes_ids.remove(listedes_ids.size() - 1); }
           */
          List<List<Long>> batches = new ArrayList<>();
          int batchSize = 1500;
          // Diviser la liste en sous-listes de taille batchSize
          for (int i = 0; i < listedes_ids.size(); i += batchSize) {
            int end = Math.min(i + batchSize, listedes_ids.size());
            List<Long> batch = listedes_ids.subList(i, end);
            batches.add(batch);
          }

          exporttoexcel.exportDemande(batches, typeDemande, response);
          request.getSession().setAttribute("listedes_ids", null);
        }
      }


    } catch (Exception e) {
      LOGGER.error(
          "DemandeAbonnementServiceimpl.exportToExcel demande exportToExcel " + e.getMessage());
    }
  }

  @Override
  public JsonResponseBody removeAllFromListExport(String typeDemande, HttpServletRequest request) {
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      LOGGER.info("liste des ids est vide ");
      List<Long> listesdesIds = new ArrayList<>();
      JsonResponseBody jrb = new JsonResponseBody();
      jrb.setCode(String.valueOf(200));
      jrb.setMsg("liste des ids est vide");
      jrb.setResult(listesdesIds);
      return jrb;
    } else {
      List<Long> listedesIds = (List<Long>) request.getSession().getAttribute("listedes_ids");
      listedesIds.clear();
      LOGGER.info("nouvelle liste session attribut des id: " + listedesIds);
      request.getSession().setAttribute("listedes_ids", listedesIds);
      JsonResponseBody jrb1 = new JsonResponseBody();
      jrb1.setCode(String.valueOf(200));
      jrb1.setMsg("Vider liste avec succes");
      jrb1.setResult(listedesIds);
      return jrb1;
    }
  }

}
