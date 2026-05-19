package crm.chifco.com.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.EntryAbonnement;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.MigrationFacture;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.ModemAccess;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.Tarification;
import crm.chifco.com.model.User;
import crm.chifco.com.model.jasper.PaymentDataSet;
import crm.chifco.com.radius.model.Radacct;
import crm.chifco.com.radius.model.Radcheck;
import crm.chifco.com.radius.model.Radusergroup;
import crm.chifco.com.radius.repository.RadacctRepository;
import crm.chifco.com.radius.repository.RadcheckRepository;
import crm.chifco.com.radius.repository.RadusergroupRepository;
import crm.chifco.com.radius.service.RadcheckService;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.EntryAbonnementRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.MigrationFactureRepository;
import crm.chifco.com.repository.ModemAccessRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.PackRepository;
import crm.chifco.com.repository.StatutRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AbonnementExcelExport;
import crm.chifco.com.service.AbonnementNonConnecterExcelExport;
import crm.chifco.com.service.AbonnementService;
import crm.chifco.com.service.ClientHistoryService;
import crm.chifco.com.service.ModemHistoryService;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.TarificationServices;
import crm.chifco.com.templateclasse.AcsInfo;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.NomStatutChifco;
import crm.chifco.com.utils.RedchekConstant;
import crm.chifco.com.utils.TypeAbonnment;
import crm.chifco.com.utils.typeCalcluleMigrationFacture;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service("abonnementService")
public class AbonnementServiceImpl implements AbonnementService {
  private final Logger logger = LogManager.getLogger(this.getClass());
  @Autowired
  AbonnementRepository abonnementRepository;
  @Autowired
  UserRepository userRepository;

  @Autowired
  private EntryAbonnementRepository entryAbonnementRepository;

  @Autowired
  DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  PackRepository packRepository;

  @Autowired
  StatutRepository statutRepository;

  @Autowired
  ClientHistoryService abonnementHistoriqueService;

  @Autowired
  ModemRepository modemRepository;

  @Autowired
  ModemHistoryService modemHistoryService;

  @Autowired
  RadcheckService radcheckService;

  @Autowired
  RadcheckRepository radcheckRepository;

  @Autowired
  RadusergroupRepository radusergroupRepository;

  @Autowired
  RadacctRepository radacctRepository;

  @Autowired
  Notification notificationservice;

  @Autowired
  FactureRepository factureRepository;

  @Autowired
  TarificationServices tarificationServices;

  @Autowired
  MigrationFactureRepository migrationFactureRepository;

  @Autowired
  ModemAccessRepository modemAccessRepository;

  @Value("${echanceOffrePromo}")
  String echanceOffrePromo;

  @Value("${access.mail.modem.XDSL.nety}")
  private String  EmailmodemXDSL;
  

  @Value("${pathRecuResilation}")
  private String pathRecuResilation;

  @Value("${avanceFacture5g}")
  private int avanceFacture5G;
  
  @Override
  public Page<Abonnement> findPaginatedwithfilter(int pageNo, int pageSize, String firstName,
      String lastName, String cin, String codeClient, Boolean status, Long Tel, Long villes,
      Long produit, Long categories, Long gouvernorat, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, String loginModem,
      String ancienloginModem, Long affecterTo, Long creePar, String statutChifcoListfiltre,
      Boolean listeClientFilter, Long contactNumber, String sortvar, String sorttype,
      Boolean isOnlyActiveUser, Date dateAffectionModem, Date datefinAffectionModem , String typeAbonnment) {


    Sort sort = Sort.by("createdDate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }


    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

    if (isOnlyActiveUser)
      return this.abonnementRepository.findAllClientActive(pageable, firstName, lastName, cin,
          codeClient, status, Tel, villes, gouvernorat, produit, categories, datedebut, datefin,
          dateDebutModification, dateFinModification, loginModem, ancienloginModem, affecterTo,
          creePar);
    else {
      // System.out.println(dateAffectionModem.getClass());
      System.out.println(datefinAffectionModem);
      // System.out.println(datefinAffectionModem.getClass());

      return this.abonnementRepository.findAllClient(pageable, firstName, lastName, cin, codeClient,
          status, Tel, villes, gouvernorat, produit, categories, datedebut, datefin,
          dateDebutModification, dateFinModification, loginModem, ancienloginModem, affecterTo,
          creePar, statutChifcoListfiltre, listeClientFilter, contactNumber, dateAffectionModem,
          datefinAffectionModem,typeAbonnment);
      // dateAffectionModem
    }



  }

  @Override
  public Page<Abonnement> findPaginatedByRevendeurWithFilter(int pageNo, int pageSize, Long roleid,
      Long userid, String sortvar, String sorttype, String firstName, String lastName, String cin,
      String codeClient, Boolean status, Long tel, Long villes, Long gouvernorat, Long produit,
      Long categories, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, String loginModem , String typeAbonnment) {

    Sort sort = Sort.by("modifieddate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.abonnementRepository.findClientsbyuserandfilter(pageable, userid, firstName,
        lastName, cin, codeClient, status, tel, villes, produit, categories, gouvernorat, datedebut,
        datefin, dateDebutModification, dateFinModification, loginModem,typeAbonnment);

  }

  @Override
  public Page<Abonnement> findPaginatedbyRevendeurWithoutSort(int pageNo, int pageSize, Long roleid,
      Long userid) {

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.abonnementRepository.findClientsByAssignedTo_Role_RoleIdAndAssignedTo_Userid(roleid,
        userid, pageable);
  }

  @Override
  public Page<Abonnement> findPaginatedByDistributeur(int pageNo, int pageSize,
      Long createdbyuserid, String sortvar, String sorttype, String firstName, String lastName,
      String cin, String codeClient, Boolean status, Long tel, Long villes, Long gouvernorat,
      Long pack, Long categories, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, String loginModem, Long affecterTo, Long creepar,
      String statutChifcoListfiltre ,String typeAbonnment) {
    Sort sort = Sort.by("modifieddate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.abonnementRepository.findClientsByCreatedBy_AffectedTo(pageable, createdbyuserid,
        firstName, lastName, cin, codeClient, status, tel, villes, pack, categories, gouvernorat,
        loginModem, affecterTo, creepar, datedebut, datefin, dateDebutModification,
        dateFinModification, statutChifcoListfiltre , typeAbonnment);

  }

  @Override
  public HashMap<String, Object> getAllClient(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, Boolean isOnlyActiveUser) {
    String firstName = null;
    String lastName = null;
    String cin = null;
    String codeClient = null;
    Boolean status = null;
    Long tel = null;
    Long villes = null;
    Long categories = null;
    Long produit = null;
    Long gouvernorat = null;
    Date datedebut = null;
    Date datefin = null;
    Date dateDebutModification = null;
    Date dateFinModification = null;
    String loginModem = null;
    String ancienloginModem = null;
    Long AffecterTo = null;
    Long creePar = null;
    String statutChifcoListfiltre = null;
    Boolean listeClientFilter = null;
    Long contactNumber = null;
    Date dateAffectionModem = null;
    Date datefinAffectionModem = null;
    String typeAbonnment = null;

    String currentUser = getCurrentUser();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    String sort = "";

    Page<Abonnement> responseData = null;
    int currentpage = start / length;
    HashMap<String, Object> myGreetings = new HashMap<>();

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (obj.keySet().contains("firstName") && !Objects.equals(obj.getString("firstName"), "")
          && obj.getString("firstName") != null) {
        firstName = obj.getString("firstName").trim();
      }
      if (obj.keySet().contains("loginModem") && !Objects.equals(obj.getString("loginModem"), "")
          && obj.getString("loginModem") != null) {
        loginModem = obj.getString("loginModem").trim();
      }
      if (obj.keySet().contains("ancienloginModem")
          && !Objects.equals(obj.getString("ancienloginModem"), "")
          && obj.getString("ancienloginModem") != null) {
        ancienloginModem = obj.getString("ancienloginModem").trim();
      }
      if (obj.keySet().contains("lastName") && !Objects.equals(obj.getString("lastName"), "")
          && obj.getString("lastName") != null) {
        lastName = obj.getString("lastName").trim();
      }
      if (obj.keySet().contains("cin") && obj.get("cin") != null
          && !Objects.equals(obj.getString("cin"), "") && obj.getString("cin") != null) {
        cin = obj.getString("cin").trim();
      }
      if (obj.keySet().contains("CodeClient") && obj.get("CodeClient") != null
          && !Objects.equals(obj.getString("CodeClient"), "")
          && obj.getString("CodeClient") != null) {
        codeClient = obj.getString("CodeClient").trim();
      }
      if (obj.keySet().contains("Status") && !Objects.equals(obj.getString("Status"), "")
          && obj.getString("Status") != null) {
        if (obj.getString("Status").trim().equals("0")) {
          status = false;
        } else {
          status = true;
        }
      }
      if (obj.keySet().contains("Tel") && !Objects.equals(obj.getString("Tel"), "")
          && obj.getString("Tel") != null) {
        tel = Long.parseLong(obj.getString("Tel").trim());
      }
      if (obj.keySet().contains("villes") && !Objects.equals(obj.getString("villes"), "")
          && !Objects.equals(obj.getString("villes"), "")) {
        villes = Long.parseLong(obj.getString("villes").trim());
      }
      if (obj.keySet().contains("gouvernorats")
          && !Objects.equals(obj.getString("gouvernorats"), "")
          && obj.getString("gouvernorats") != null) {
        gouvernorat = Long.parseLong(obj.getString("gouvernorats").trim());
      }
      if (obj.keySet().contains("Categories") && !Objects.equals(obj.getString("Categories"), "")
          && obj.getString("Categories") != null) {
        categories = Long.parseLong(obj.getString("Categories").trim());
      }
      if (obj.keySet().contains("Produit") && !Objects.equals(obj.getString("Produit"), "")
          && obj.getString("Produit") != null) {
        produit = Long.parseLong(obj.getString("Produit").trim());
      }
      if (obj.keySet().contains("datedebut") && !Objects.equals(obj.getString("datedebut"), "")
          && obj.getString("datedebut") != null) {
        datedebut = CrmUtils.convertStringToDate(obj.getString("datedebut"));
      }
      if (obj.keySet().contains("datefin") && !Objects.equals(obj.getString("datefin"), "")
          && obj.getString("datefin") != null) {
        datefin = CrmUtils.convertStringToLocalDateTime(obj.getString("datefin"));
      }

      if (obj.keySet().contains("dateDebutModification")
          && !Objects.equals(obj.getString("dateDebutModification"), "")
          && obj.getString("dateDebutModification") != null) {
        dateDebutModification =
            CrmUtils.convertStringToDate(obj.getString("dateDebutModification"));
      }
      if (obj.keySet().contains("dateFinModification")
          && !Objects.equals(obj.getString("dateFinModification"), "")
          && obj.getString("dateFinModification") != null) {
        dateFinModification =
            CrmUtils.convertStringToLocalDateTime(obj.getString("dateFinModification"));
      }
      if (obj.keySet().contains("AffecterTo") && !Objects.equals(obj.get("AffecterTo"), "")
          && obj.getString("AffecterTo") != null) {
        AffecterTo = obj.getLong("AffecterTo");
      }
      if (obj.keySet().contains("Creepar") && !Objects.equals(obj.get("Creepar"), "")
          && obj.getString("Creepar") != null) {
        creePar = obj.getLong("Creepar");
      }

      if (obj.keySet().contains("statutChifcoListfiltre")
          && !Objects.equals(obj.getString("statutChifcoListfiltre"), "")
          && obj.getString("statutChifcoListfiltre") != null) {
        statutChifcoListfiltre = obj.getString("statutChifcoListfiltre");
      }
      if (!Objects.equals(obj.getString("statutChifcoListfiltre"), "")
          && obj.getString("statutChifcoListfiltre") != null) {
        statutChifcoListfiltre = obj.getString("statutChifcoListfiltre");
      }

      if (obj.keySet().contains("contactNumber")
          && !Objects.equals(obj.getString("contactNumber"), "")
          && obj.getString("contactNumber") != null) {
        contactNumber = obj.getLong("contactNumber");
      }
      if (obj.keySet().contains("datedebutAffectionModem")
          && !Objects.equals(obj.getString("datedebutAffectionModem"), "")
          && obj.getString("datedebutAffectionModem") != null) {
        dateAffectionModem = CrmUtils.convertStringToDate(obj.getString("datedebutAffectionModem"));
        /*
         * Calendar calendar = Calendar.getInstance(); calendar.setTime(dateAffectionModem); //
         * Assigner la date à l'objet Calendar
         * 
         * // Ajouter 5 mois à la date calendar.add(Calendar.MONTH, 5);
         * 
         * // Obtenir la nouvelle date après l'ajout de 5 mois datefinAffectionModem =
         * calendar.getTime();
         */
      }
      if (obj.keySet().contains("datefinAffectionModem")
          && !Objects.equals(obj.getString("datefinAffectionModem"), "")
          && obj.getString("datedebutAffectionModem") != null) {
        // datefinAffectionModem =
        datefinAffectionModem =
            CrmUtils.convertStringToLocalDateTime(obj.getString("datefinAffectionModem").trim());
      }
      if (obj.keySet().contains("typeAbonnment")
              && !Objects.equals(obj.getString("typeAbonnment"), "")
              && obj.getString("typeAbonnment") != null) {
            // datefinAffectionModem =
    	  typeAbonnment =obj.getString("typeAbonnment").trim();
          }
      
      if (obj.keySet().contains("listeClientFilter")
          && !Objects.equals(obj.getString("listeClientFilter"), "")
          && obj.getString("listeClientFilter") != null) {
        if (obj.getString("listeClientFilter").equals("non")) {
          listeClientFilter = false;
        } else if (obj.getString("listeClientFilter").equals("oui")) {
          listeClientFilter = true;
        }
      }
    }

    // admin, pos, finance
    if (StringsRole.contains("READ_SUBSCRIPTION_LIST")) {
      switch (ordercolumnaram) {

        case 1:
          sort = "firstName";
          break;
        case 2:
          sort = "adresse";

          break;
        case 3:
          sort = "ville";
          break;
        case 4:
          sort = "telFixe";
          break;
        case 5:
          sort = "pack";
          break;
        case 6:
          sort = "statut";
          break;
        case 7:
          sort = "user";
          break;
        case 8:
          sort = "createdDate";
          break;
        default:
          sort = "createdDate";

      }

      responseData = this.findPaginatedwithfilter(currentpage + 1, length, firstName, lastName, cin,
          codeClient, status, tel, villes, produit, categories, gouvernorat, datedebut, datefin,
          dateDebutModification, dateFinModification, loginModem, ancienloginModem, AffecterTo,
          creePar, statutChifcoListfiltre, listeClientFilter, contactNumber, sort, orderdir,
          isOnlyActiveUser, dateAffectionModem, datefinAffectionModem ,typeAbonnment);



    }
    // revendeur
    else if (StringsRole.contains("READ_SUBSCRIPTION_LIST_OWNER")) {
      switch (ordercolumnaram) {

        case 1:
          sort = "firstName"; // "first_name";
          break;
        case 2:
          sort = "adresse";

          break;
        case 3:
          sort = "ville";
          break;
        case 4:
          sort = "telFixe";
          break;
        case 5:
          sort = "statut";
          break;
        case 6:
          sort = "statut";
        case 7:
          sort = "user";
          break;
        case 8:
          sort = "createdDate";
          break;

      }
      responseData = this.findPaginatedByRevendeurWithFilter(currentpage + 1, length,

          user.getRole().getRoleId(), user.getUserid(), sort, orderdir, firstName, lastName, cin,
          codeClient, status, tel, villes, gouvernorat, produit, categories, datedebut, datefin,
          dateDebutModification, dateFinModification, loginModem , typeAbonnment);

    }
    // distributeur
    else if (StringsRole.contains("READ_SUBSCRIPTION_LIST_AREA")) {
      switch (ordercolumnaram) {

        case 1:
          sort = "firstName"; // "first_name";
          break;
        case 2:
          sort = "adresse";

          break;
        case 3:
          sort = "ville"; // villeid
          break;
        case 4:
          sort = "telFixe";
          break;
        case 5:
          sort = "statut"; // "statutid";
          break;
        case 6:
          sort = "statut";
        case 7:
          sort = "user";
          break;
        case 8:
          sort = "createdDate";
          break;

      }
      responseData = this.findPaginatedByDistributeur(currentpage + 1, length, user.getUserid(),
          sort, orderdir, firstName, lastName, cin, codeClient, status, tel, villes, gouvernorat,
          produit, categories, datedebut, datefin, dateDebutModification, dateFinModification,
          loginModem, AffecterTo, creePar, statutChifcoListfiltre , typeAbonnment);

    }

    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;
  }

  private String getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    return currentUser;
  }

  public Abonnement saveNewAbonnement(DemandeAbonnement demandeAbonnement) {
    Abonnement validatAabonnement = new Abonnement();

    validatAabonnement.setLastName(demandeAbonnement.getLastName());
    validatAabonnement.setFirstName(demandeAbonnement.getFirstName());
    if (demandeAbonnement.getEmail() != null)
      validatAabonnement.setEmail(demandeAbonnement.getEmail());
    validatAabonnement.setCin(demandeAbonnement.getCin());
    validatAabonnement.setVille(demandeAbonnement.getVille());
    validatAabonnement.setGouvernorat(demandeAbonnement.getGouvernorat());
    validatAabonnement.setAdresse(demandeAbonnement.getAdresse());
    validatAabonnement.setCodePostale(demandeAbonnement.getCodePostale());
    validatAabonnement.setTelMobile(demandeAbonnement.getTelMobile());
    validatAabonnement.setTelMobile2(demandeAbonnement.getTelMobile2());
    validatAabonnement.setDateNaissance(demandeAbonnement.getDateNaissance());
    if (demandeAbonnement.getProfession() != null)
      validatAabonnement.setProfession(demandeAbonnement.getProfession());
    if (demandeAbonnement.getTelFixe() != null)
      validatAabonnement.setTelFixe(demandeAbonnement.getTelFixe());
    if (demandeAbonnement.getFax() != null)
      validatAabonnement.setFax(demandeAbonnement.getFax());
    validatAabonnement.setEnabled(true);
    validatAabonnement.setReferenceClient(demandeAbonnement.getReferenceChifco());
    validatAabonnement.setStatut(demandeAbonnement.getStatut());
    validatAabonnement.setUser(demandeAbonnement.getUser());
    validatAabonnement.setAssignedTo(demandeAbonnement.getAssignedTo());
    validatAabonnement.setDemandeAbonnement(demandeAbonnement.getDemandeId());
    validatAabonnement.setPhotoCin1(demandeAbonnement.getPhotoCin1());
    validatAabonnement.setPhotoCin2(demandeAbonnement.getPhotoCin2());
    logger.info(" la demande de : " + validatAabonnement.getCin()); //
    validatAabonnement.setTypePaiement(demandeAbonnement.getTypePaiement());
    validatAabonnement.setStatut(demandeAbonnement.getStatut());

    Instant datenow = Instant.now();
    // int typdedepaymentmonth = demandeAbonnement.getTypePaiement().getNombreMoisTypePaiement();

    LocalDate dateActuelle = LocalDate.now();

    // Date dateProchainFacture = CrmUtils.calculeDateFin(typdedepaymentmonth, null);
    if (demandeAbonnement.getPack().getPayLater() != null
        && demandeAbonnement.getPack().getPayLater()
        && dateActuelle.isBefore(CrmUtils.dateLimitePromo())) {
      validatAabonnement.setTranchcreditFacture(10L);

      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

      Date dateProchainFacture;

      // Date actuelle
      long joursRestants = ChronoUnit.DAYS.between(dateActuelle, CrmUtils.dateLimitePromo());
      if (demandeAbonnement.getPack().getIdPackBase() != null) {
        Tarification trafication = tarificationServices
            .getTarificationBypackId(demandeAbonnement.getPack().getIdPackBase());
        Double reliquatParTranch = ((trafication.getPrixUnitaire() / 30) * joursRestants) / 10;
        validatAabonnement.setCreditFacture(reliquatParTranch);
      }

      try {

        dateProchainFacture = formatter.parse(echanceOffrePromo);
        validatAabonnement.setDateProchainFacturation(dateProchainFacture);

      } catch (ParseException e) {
        // TODO Auto-generated catch block

        logger.info("creation abonnement erreur date prochain facture promo : " + e); //
      }

    }
  
  else if(demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {
    	 Date nouvauDateFin =
    	            CrmUtils.calculeDateFin(avanceFacture5G, new Date());
        validatAabonnement.setDateProchainFacturation(nouvauDateFin);

    }else {
      validatAabonnement.setDateProchainFacturation(null);

    }
    validatAabonnement.setHasRaccordement(demandeAbonnement.getHasRaccordement());
    validatAabonnement
        .setTrancheRaccordementSelected(demandeAbonnement.getNbFaisApayeReccardement());
    validatAabonnement.setTrancheRaccordement(demandeAbonnement.getNbFaisApayeReccardement());
    validatAabonnement.setPack(demandeAbonnement.getPack());

    if (demandeAbonnement.getPack() != null
        && demandeAbonnement.getPack().getOffre().getIsPromo()) {
      Long totalPromotion = demandeAbonnement.getPack().getOffre().getPeriodeValidPromo() * 30;
      Instant DateFinPromotion = datenow.plus(totalPromotion, ChronoUnit.DAYS);
      if (demandeAbonnement.getPack().getPayLater() == null
          || !demandeAbonnement.getPack().getPayLater()) {
        validatAabonnement.setDateFinPromotion(Date.from(DateFinPromotion));
      } else {
        validatAabonnement.setDateFinPromotion(
            Date.from(CrmUtils.dateLimitePromo().atStartOfDay(ZoneId.systemDefault()).toInstant()));

      }
    }
    validatAabonnement.setHasBankCard(demandeAbonnement.getHasBankCard());
    validatAabonnement.setHouseHolder(demandeAbonnement.getHouseHolder());
    validatAabonnement.setProprietaire(demandeAbonnement.getProprietaire());
    validatAabonnement.setSituationFamiliale(demandeAbonnement.getSituationFamiliale());
    validatAabonnement.setWithIpFix(demandeAbonnement.getWithIpFix());
    validatAabonnement.setDateDeMiseEnService(demandeAbonnement.getDateDeMiseEnService());
    validatAabonnement.setTypeAbonnment(demandeAbonnement.getTypeAbonnment());
    
    abonnementRepository.save(validatAabonnement);
    logger.info("creation 1er client sur la demande de : " + validatAabonnement.getClientid());

    demandeAbonnement.getEntriesDemandeAbonnement().forEach(demandeAbonnemententry -> {
      EntryAbonnement entriesAbonnement = new EntryAbonnement();

      entriesAbonnement.setProduit(demandeAbonnemententry.getProduit());
      entriesAbonnement.setAbonnement(validatAabonnement);
      entryAbonnementRepository.save(entriesAbonnement);

    });
    return validatAabonnement;
  }

  public Abonnement findAbonnementByCin(String cin) {
    Abonnement abonnement = null;
    abonnement = abonnementRepository.findAbonnementByCin(cin);
    return abonnement;
  }

  @Override
  public List<AcsInfo> findListClientToAcs(Instant dateCreation, String statutUser) {
    // TODO Auto-generated method stub
    return this.abonnementRepository.findListClientByDateCreation(dateCreation, statutUser);

  }

  @Override
  public Abonnement findAbonnementByReferenceClient(String referenceChifco) {
    // TODO Auto-generated method stub
    return this.abonnementRepository.findAbonnementByReferenceClient(referenceChifco);
  }

  @Override
  public Abonnement findUserByFixeNumberOrCin(String recherche, Long telephone) {
    // TODO Auto-generated method stub
    return this.abonnementRepository.findUserByFixeNumberOrCin(recherche, telephone);
  }

  @Override
  public Abonnement findUserByFixeNumber(Long telephone) {
    // TODO Auto-generated method stub
    return this.abonnementRepository.findUserByFixeNumber(telephone);
  }

  @Override
  public Boolean affectRevendeur(Long clientid, String codeRevendeur, String emailRevendeur,
      String identificationFiscale) {
    // TODO Auto-generated method stub

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
      Abonnement myAbonnement = abonnementRepository.findAbonnementByClientid(clientid);
      myAbonnement.setAssignedTo(ListeAbonement.get(0));
      abonnementRepository.save(myAbonnement);

      DemandeAbonnement myDemandeAbonnement = demandeAbonnementRepository
          .findDemandeAbonnementByDemandeId(myAbonnement.getDemandeAbonnement());
      myDemandeAbonnement.setAssignedTo(ListeAbonement.get(0));
      demandeAbonnementRepository.save(myDemandeAbonnement);
      return true;
    }

  }

  @Override
  public Boolean affectOneRevendeur(String idRevendeurToAffected, Long demandeId) {
    // TODO Auto-generated method stub
    User userToAffected = userRepository.getById(Long.parseLong(idRevendeurToAffected));
    Abonnement myAbonnement = abonnementRepository.findAbonnementByClientid(demandeId);
    if (myAbonnement != null && userToAffected != null) {
      myAbonnement.setAssignedTo(userToAffected);
      abonnementRepository.save(myAbonnement);

      DemandeAbonnement myDemandeAbonnement = demandeAbonnementRepository
          .findDemandeAbonnementByDemandeId(myAbonnement.getDemandeAbonnement());
      if (myDemandeAbonnement != null) {
        myDemandeAbonnement.setAssignedTo(userToAffected);
        demandeAbonnementRepository.save(myDemandeAbonnement);
      }
      return true;
    } else
      return false;
  }

  @Override
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      Long gouvernorat, Long villes, String cin, String nom, String prenom, String codeClient,
      Boolean status, Long tel, Long category, Long produit, String dateDebut, String dateFin,
      String dateDebutModification, String dateFinModification, String loginModem, Long affecterTo,
      Long creePar, String statutChifcoListfiltre, String listeClientFilter,
      String exportRecherchedatedebutAffectionModem,String typeAbonnment, String ExportRecherchedatefinAffectionModem) {
    // TODO Auto-generated method stub
    ModelAndView mav = new ModelAndView();
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String firstName = null;
      String lastName = null;
      String modemLogin = null;
      String cinClient = null;
      String referenceClient = null;
      String statutChifco = null;
      Date datedebut = null;
      Date dateDeFin = null;
      Date dateDeFinDeModification = null;
      Date datedebutDeModification = null;
      Date dateAffectionModem = null;
      Date recherchedatefinAffectionModem = null;
      Boolean listeClientActiveFilter = null;
      String typeAbonnments = null ;
      if (nom != null && !nom.isEmpty()) {
        firstName = nom;
      }
      if (!prenom.isEmpty()) {
        lastName = prenom;
      }
      if (!loginModem.isEmpty()) {
        modemLogin = loginModem;
      }
      if (!cin.isEmpty()) {
        cinClient = cin;
      }
      if (!codeClient.isEmpty()) {
        referenceClient = codeClient;
      }

      if (!statutChifcoListfiltre.isEmpty()) {
        statutChifco = statutChifcoListfiltre;
      }
      if(!typeAbonnment.isEmpty()) {
    	  typeAbonnments= typeAbonnment;
      }
      if (!listeClientFilter.isEmpty()) {
        if (listeClientFilter.equals("oui")) {
          listeClientActiveFilter = false;
        } else if (listeClientFilter.equals("non")) {
          listeClientActiveFilter = true;
        }
      }
      if (dateDebut != null && !dateDebut.isEmpty()) {
        try {
          dateDebut = dateDebut + " 00:00:00";

          datedebut = dateFormat.parse(dateDebut);
        } catch (ParseException e) {
          logger.warn("findAllUserByRecherche : " + e.getMessage());
        }
      }

      if (dateFin != null && !dateFin.isEmpty()) {
        try {
          dateFin = dateFin + "  23:59:59";

          dateDeFin = dateFormat.parse(dateFin);
        } catch (ParseException e) {
          logger.warn("findAllUserByRecherche : " + e.getMessage());
        }
      }

      if (dateDebutModification != null && !dateDebutModification.isEmpty()) {
        try {
          dateDebutModification = dateDebutModification + " 00:00:00";

          datedebutDeModification = dateFormat.parse(dateDebutModification);
        } catch (ParseException e) {
          logger.warn("findAllUserByRecherche : " + e.getMessage());
        }
      }

      if (dateFinModification != null && !dateFinModification.isEmpty()) {
        try {
          dateFinModification = dateFinModification + "  23:59:59";

          dateDeFinDeModification = dateFormat.parse(dateFinModification);
        } catch (ParseException e) {
          logger.warn("findAllUserByRecherche : " + e.getMessage());
        }
      }
      if (exportRecherchedatedebutAffectionModem != null
          && !exportRecherchedatedebutAffectionModem.isEmpty()) {



        dateAffectionModem = CrmUtils.convertStringToDate(exportRecherchedatedebutAffectionModem);

      }
      if (ExportRecherchedatefinAffectionModem != null
          && !ExportRecherchedatefinAffectionModem.isEmpty()) {


        // exportRecherchedatedebutAffectionModem =
        // exportRecherchedatedebutAffectionModem + " 23:59:59";
        recherchedatefinAffectionModem =
            CrmUtils.convertStringToDate(ExportRecherchedatefinAffectionModem);

      }

      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

        List<Abonnement> myList = new ArrayList<>();

        if (StringsRole.contains("READ_SUBSCRIPTION_LIST_OWNER")) {
          myList = abonnementRepository.exportClientsbyuserandfilter(user.getUserid(), firstName,
              lastName, cinClient, referenceClient, status, tel, villes, produit, category,
              gouvernorat, datedebut, dateDeFin, datedebutDeModification, dateDeFinDeModification,
              firstName , typeAbonnments);
        }
        if (StringsRole.contains("READ_SUBSCRIPTION_LIST")) {
          myList = abonnementRepository.exportAllClient(firstName, lastName, cinClient,
              referenceClient, status, tel, villes, gouvernorat, produit, category, datedebut,
              dateDeFin, datedebutDeModification, dateDeFinDeModification, modemLogin, affecterTo,
              creePar, statutChifco, listeClientActiveFilter, dateAffectionModem,
              recherchedatefinAffectionModem , typeAbonnments);
        }
        if (StringsRole.contains("READ_SUBSCRIPTION_LIST_AREA")) {
          myList = abonnementRepository.exportClientsByaAssignedTo_AffectedTo(user.getUserid(),
              firstName, lastName, cinClient, referenceClient, status, tel, villes, produit,
              category, gouvernorat, modemLogin, affecterTo, creePar, datedebut, dateDeFin,
              datedebutDeModification, dateDeFinDeModification,typeAbonnments);
        }

        if (myList.size() > 0) {

          mav.setView(new AbonnementExcelExport());
          mav.addObject("list", myList);
          mav.addObject("user", user);
          return mav;
        } else {
          request.getRequestDispatcher("/client/allclients/1").forward(request, response);
          return null;
        }
      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.exportation client Error:" + e);

    }
    return mav;
  }

  @Override
  public crm.chifco.com.ApiDTO.getLoginAndPassswordModem getLoginAndPassswordModem(
      String numSerie) {
    // TODO Auto-generated method stub
    return abonnementRepository.getLoginAndPassswordModemByNumSerie(numSerie);
  }

  @Override
  public Boolean saveResiliation(User user, Long clientid) {
    // TODO Auto-generated method stub
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
    Statut statutResilier = statutRepository.findStatutByNomStatut(NomStatutChifco.RESILIATION);
    if (statutResilier != null && abonnement != null) {

      if (abonnement.getModem() != null) {
        abonnement.getModem().setAffecteClient(null);
        abonnement.getModem().setStatus(true);
        modemRepository.save(abonnement.getModem());
        modemHistoryService.save("Retrait du modem du client suite à la résiliation du service.",
            user, abonnement.getModem());
        if (abonnement.getLoginModem() != null) {
          List<Radcheck> radusDataModem =
              radcheckService.getRadchecksByUsernamee(abonnement.getLoginModem());

          for (Radcheck readCheck : radusDataModem) {
            // Perform your update logic here
            readCheck.setUsername("resilier" + readCheck.getUsername());
            if (readCheck.getAttribute().equals(RedchekConstant.CleartextPassword)) {
              readCheck.setValue("resilier" + readCheck.getValue());
            }
            radcheckRepository.save(readCheck);
          }
        }
      }
      abonnement.setIsActive(false);
      abonnement.setEnabled(false);
      abonnement.setDateResiliation(new Date());
      abonnement.setResiliePar(user.getUserid());
      abonnement.setDateProchainFacturation(null);
      abonnement.setStatut(statutResilier);
      abonnement.setCalculeIsFirstSession(true);
      abonnement.setAncienLogin(abonnement.getAncienLogin() + ',' + abonnement.getLoginModem());
      abonnement.setLoginModem(null);
      abonnement.setModem(null);
      abonnement.setPassword(null);
      abonnementRepository.save(abonnement);
      DemandeAbonnement demandeAbonnement = demandeAbonnementRepository
          .findDemandeAbonnementByDemandeId(abonnement.getDemandeAbonnement());
      demandeAbonnement.setStatut(statutResilier);
      demandeAbonnementRepository.save(demandeAbonnement);
      abonnementHistoriqueService.saveNewHistorique(user, abonnement.getClientid(),
          "Resiliation Abonnement reference " + abonnement.getPack().getTitle());



      return true;
    } else
      return false;
  }
  @Override
  public String changerSimModem(Long clientId, String numSerieModem, User user) {
    // TODO Auto-generated method stub
    logger.info("Debut changement modem. clientId :" + clientId + ", numSerieModem : "
        + numSerieModem + " , user : ", user.getUserid());

    Modem newModem = modemRepository.findAllByNumSerie(numSerieModem);
    if (newModem == null) {
      logger.error("Le modem avec le numéro de série {} n'a pas été trouvé.", numSerieModem);
      logger.info("Fin de la methode changerModem.");
      return "MODEM_NOT_FOUND";
    }

    Abonnement ab = abonnementRepository.getById(clientId);
    if (ab.getTelFixe() == null) {
      logger.error("Le client ne peut pas changer de modem, car il n'a pas de modem actuellement.");
      logger.info("Fin de la methode changerModem.");
      return "CLIENT_CANNOT_CHANGE_MODEM";
    }

    if ((!newModem.getModelModem().equals("Sim.5G") && 
        ! ab.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box))
        || newModem.getAffecteClient() != null || newModem.getStatus() != false) {
      logger.error("modem incorrect.");
      logger.info("Fin de la méthode changerModem.");
      return "MODEM_INCORRECT";
    }

    
    List<Modem> oldModems  = modemRepository.findAllByEmail(ab.getTelFixe().toString());
    Modem oldModem = oldModems.get(0);
    oldModem.setAffecteClient(null);
    oldModem.setStatus(true);

    String msg1 = "Le Sim a été remplacé par le Sim " + newModem.getNumSerie();
    String msg2 = "Suite à un changement de Sim, ce Sim passe à l'état Inactif";
    modemHistoryService.save(msg1, user, oldModem);
    modemHistoryService.save(msg2, user, oldModem);
    modemRepository.save(oldModem);
    logger.info(msg1);


      String msg5 = "Ce Sim est attribué à ce client " + ab.getReferenceClient()
          + " au lieu du Sim " + oldModem.getNumSerie();
      modemHistoryService.save(msg5, user, newModem);
      logger.info(msg5);
    

    newModem.setAffecteClient(clientId);
    modemRepository.save(newModem);
    logger.info(
        "affecter Sim " + newModem.getNumSerie() + " pour le client " + ab.getReferenceClient());

    ab.setTelFixe(Long.valueOf(newModem.getEmail()));
    abonnementRepository.save(ab);
    String msg = "Le client a effectué un changement de SIM de l'ancien Sim "
        + oldModem.getNumSerie() + " vers le nouveau Sim " + newModem.getNumSerie();
    abonnementHistoriqueService.saveNewHistorique(user, clientId, msg);

    logger.info("Fin de la methode changerModem.");
    return "success";
  }


  @Override
  public String changerModem(Long clientId, String numSerieModem, User user) {
    // TODO Auto-generated method stub
    logger.info("Debut changement modem. clientId :" + clientId + ", numSerieModem : "
        + numSerieModem + " , user : ", user.getUserid());

    Modem newModem = modemRepository.findAllByNumSerie(numSerieModem);
    if (newModem == null) {
      logger.error("Le modem avec le numéro de série {} n'a pas été trouvé.", numSerieModem);
      logger.info("Fin de la methode changerModem.");
      return "MODEM_NOT_FOUND";
    }

    Abonnement ab = abonnementRepository.getById(clientId);
    if (ab.getModem() == null) {
      logger.error("Le client ne peut pas changer de modem, car il n'a pas de modem actuellement.");
      logger.info("Fin de la methode changerModem.");
      return "CLIENT_CANNOT_CHANGE_MODEM";
    }

    if ((!newModem.getModelModem().equals("XDSL") && !newModem.getModelModem()
        .equals(ab.getPack().getCategoriePack().getCategorieProduitInternetCode()))
        || newModem.getAffecteClient() != null || newModem.getStatus() != false) {
      logger.error("modem incorrect.");
      logger.info("Fin de la méthode changerModem.");
      return "MODEM_INCORRECT";
    }

    Modem oldModem = ab.getModem();
    oldModem.setAffecteClient(null);
    oldModem.setStatus(true);

    String msg1 = "Le modem a été remplacé par le modem " + newModem.getNumSerie();
    String msg2 = "Suite à un changement de modem, ce modem passe à l'état Inactif";
    modemHistoryService.save(msg1, user, oldModem);
    modemHistoryService.save(msg2, user, oldModem);
    modemRepository.save(oldModem);
    logger.info(msg1);

    Radacct historiqueConnexion = radacctRepository.findTop1ByUserName(ab.getLoginModem());
    if (historiqueConnexion != null) {

      String msg3 = "Ce modem est attribué à ce client " + ab.getReferenceClient()
          + " au lieu du modem " + oldModem.getNumSerie();
      String msg4 = "Suite au changement de modem, l'adresse e-mail passe de " + newModem.getEmail()
          + " à " + ab.getLoginModem() + ", et le mot de passe est modifié de "
          + newModem.getPassword() + " à " + ab.getPassword();
      modemHistoryService.save(msg3, user, newModem);
      modemHistoryService.save(msg4, user, newModem);

      newModem.setEmail(ab.getLoginModem());
      newModem.setPassword(ab.getPassword());
      modemRepository.save(newModem);

      logger.info(
          "Transfert des informations (login et mdp) de connexion du modem précédent au nouveau modem.");
    } else {

      if (ab.getLoginModem() != null && ab.getPassword() != null) {
    	 if( newModem.getModelModem().equals("XDSL")) {
    	   long modemAccessCount =
                   modemAccessRepository.countByStatusAndModelModem(true,
                		   ab.getPack().getCategoriePack().getCategorieProduitInternetCode());
           if(modemAccessCount<20)
           {
         	  notificationservice.sendSimpleMail(EmailmodemXDSL,"Merci de nous envoyer la liste des accès des modems de ce type: " + ab.getPack().getCategoriePack().getCategorieProduitInternetCode(),"Acces modem XDSL:changer Modem") ;
           }
    	  }
    	 if(newModem.getEmail() == null) {
        ModemAccess modemAccess = modemAccessRepository.findFirstModemAccessByStatusAndModelModem(
            true, ab.getPack().getCategoriePack().getCategorieProduitInternetCode());
        newModem.setEmail(modemAccess.getEmail());
        newModem.setPassword(modemAccess.getPassword());
        modemRepository.save(newModem);
        modemAccess.setStatus(false);
        modemAccess.setIdModem(newModem.getModemId());
        modemAccessRepository.save(modemAccess);
        }
        Radcheck radcheckRow1 = radcheckService.getRadchecksByUsernameAndAttribute(
            oldModem.getEmail(), RedchekConstant.CleartextPassword);

        if (radcheckRow1 != null) {
          logger.info("Modification de radcheckRow1 - Username : {}, Value : {}",
              newModem.getEmail(), newModem.getPassword());

          radcheckRow1.setUsername(newModem.getEmail());
          radcheckRow1.setValue(newModem.getPassword());
          radcheckRepository.save(radcheckRow1);

          logger.info("Modification de radcheckRow2 - Username : {}", newModem.getEmail());
          Radcheck radcheckRow2 = radcheckService
              .getRadchecksByUsernameAndAttribute(oldModem.getEmail(), RedchekConstant.Expiration);
          radcheckRow2.setUsername(newModem.getEmail());
          radcheckRepository.save(radcheckRow2);


          Radusergroup radusergroup = radcheckService.getRaduserGroup(ab.getLoginModem());
          if (radusergroup != null) {
            logger.info("Modification de radusergroup - Username : {}", newModem.getEmail());
            radusergroup.setGroupname((radcheckService.generateRadusGroupGroupName(
                ab.getPack().getCategoriePack().getCategorieProduitInternetCode())));
            radusergroup.setUsername(newModem.getEmail());
            radusergroupRepository.save(radusergroup);

          }
          ab.setLoginModem(newModem.getEmail());
          ab.setPassword(newModem.getPassword());
          logger.info("Modification des informations d'abonnement - LoginModem : {}, Password : {}",
              newModem.getEmail(), newModem.getPassword());
        }
      }

      String msg5 = "Ce modem est attribué à ce client " + ab.getReferenceClient()
          + " au lieu du modem " + oldModem.getNumSerie();
      modemHistoryService.save(msg5, user, newModem);
      logger.info(msg5);
    }

    newModem.setAffecteClient(clientId);
    modemRepository.save(newModem);
    logger.info(
        "affecter modem " + newModem.getNumSerie() + " pour le client " + ab.getReferenceClient());

    ab.setModem(newModem);
    abonnementRepository.save(ab);
    String msg = "Le client a effectué un changement de modem de l'ancien modem "
        + oldModem.getNumSerie() + " vers le nouveau modem " + newModem.getNumSerie();
    abonnementHistoriqueService.saveNewHistorique(user, clientId, msg);

    logger.info("Fin de la methode changerModem.");
    return "success";
  }

  @Override
  public String verificationTelFixEdit(MultiValueMap<String, String> formData) {
    if (formData.getFirst("telFixe") != null && !formData.getFirst("telFixe").isEmpty()
        && formData.getFirst("idAbonnement") != null
        && !formData.getFirst("idAbonnement").isEmpty()) {
      String telFixe = formData.getFirst("telFixe");
      Abonnement abonnement = abonnementRepository
          .findAbonnementByClientid(Long.parseLong(formData.getFirst("idAbonnement")));
      if (abonnement.getTelFixe() != null
          && abonnement.getTelFixe().equals(Long.parseLong(telFixe)))
        return "true";
      else {
        DemandeAbonnement demandeAbonnement = demandeAbonnementRepository
            .findDemandeAbonnementByTelfixeAndStatusAvaibled(Long.parseLong(telFixe));

        if (demandeAbonnement == null)
          return "true";
        else
          return "false";
      }

    } else
      return "true";

  }

  @Override
  public String verificationCinEdit(MultiValueMap<String, String> formData) {
    // TODO Auto-generated method stub
    if (formData.getFirst("cin") != null && !formData.getFirst("cin").isEmpty()
        && formData.getFirst("idAbonnement") != null
        && !formData.getFirst("idAbonnement").isEmpty()) {
      String cin = formData.getFirst("cin");
      Abonnement abonnement = abonnementRepository
          .findAbonnementByClientid(Long.parseLong(formData.getFirst("idAbonnement")));
      if (abonnement.getCin() != null && abonnement.getCin().equals(cin))
        return "true";
      else {
        DemandeAbonnement demandeAbonnement =
            demandeAbonnementRepository.findDemandeAbonnementByCinAndStatusAvaibled(cin);
        if (demandeAbonnement == null)
          return "true";
        else
          return "false";
      }

    } else
      return "true";
  }

  @Override
  public List<String> getMotifStatutTT(String statutTT) {
    // TODO Auto-generated method stub
    return demandeAbonnementRepository.findMotifInstanceByStatutTT(statutTT);
  }

  @Override
  public void changeToRecouvrement(Long clientid, RedirectAttributes redirectAttrs) {
    // TODO Auto-generated method stub
    Statut statutRecouvrement =
        statutRepository.findStatutByNomStatut(NomStatutChifco.RECOUVREMENT);

    Abonnement abonnmentById = abonnementRepository.findAbonnementByClientid(clientid);
    if (abonnmentById != null) {
      abonnmentById.setStatut(statutRecouvrement);
      // abonnmentById.setDateProchainFacturation(null);
      abonnementRepository.save(abonnmentById);

      DemandeAbonnement demandeAbonnement = demandeAbonnementRepository
          .findDemandeAbonnementByDemandeId(abonnmentById.getDemandeAbonnement());
      demandeAbonnement.setStatut(statutRecouvrement);
      demandeAbonnementRepository.save(demandeAbonnement);
      redirectAttrs.addFlashAttribute("message", "recouvrement avec success");
    } else {
      redirectAttrs.addFlashAttribute("message", "erreur recouvrement");
    }
  }


  @Override
  public void updateEnvoiSms(Long clientid, RedirectAttributes redirectAttrs) {
    // TODO Auto-generated method stub
    try {
      Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
      if (abonnement.getIsSmsClientSend() == true) {
        abonnement.setIsSmsClientSend(false);
      }

      else {
        abonnement.setIsSmsClientSend(true);
      }
      abonnementRepository.save(abonnement);
      redirectAttrs.addFlashAttribute("message", "updateEnvoiSmsSuccess");
    } catch (Exception e) {
      logger.error(" demandeabonnement.exportation client Error:" + e);
      redirectAttrs.addFlashAttribute("message", "updateEnvoiSmsErreur");
    }
  }

  @Override
  public void sendSmsToclient(User user, Long clientid, String sms) {
    // TODO Auto-generated method stub
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
    Map<String, Object> Message = new HashMap<String, Object>();
    ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
    if (abonnement != null) {
      Message.put("number", abonnement.getTelMobile());
      Message.put("message", sms);
      smsToSend.add(Message);
      Boolean resultaSms = notificationservice.sendsmsnotification(smsToSend);
      if (resultaSms) {
        logger.info("sms is send to" + abonnement.getTelMobile() + "from fiche client");
        String msg = "\"Le SMS client a été envoyé par" + user.getCodeUser();
        abonnementHistoriqueService.saveNewHistorique(user, clientid, msg);
      }
    }
  }


  @Override
  public void changeToActive(Long clientid, RedirectAttributes redirectAttrs) {
    // TODO Auto-generated method stub
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
    if (abonnement.getStatut().getNomStatut().equals(NomStatutChifco.RECOUVREMENT)) {
      Statut statutActive = statutRepository.findStatutByNomStatut(NomStatutChifco.ACTIVE);
      if (abonnement.getDateProchainFacturation() == null) {
        Facture lastFacture =
            factureRepository.findTopByAbonnement_clientidOrderByFactureIdDesc(clientid);
        abonnement.setDateProchainFacturation(lastFacture.getDateDeFin());
      }


      abonnement.setStatut(statutActive);
      abonnementRepository.save(abonnement);
    }
  }

  @Override
  public void changeToSuspendu(Long clientid, RedirectAttributes redirectAttrs) {
    // TODO Auto-generated method stub
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      Abonnement abonnmentById = abonnementRepository.findAbonnementByClientid(clientid);
      if (abonnmentById != null) {
        abonnmentById.setDateProchainFacturation(null);
        abonnementRepository.save(abonnmentById);

        abonnementHistoriqueService.saveNewHistorique(user, abonnmentById.getClientid(),
            "Une suspension du cycle de facturation a été effectuée.");
      }
    }
  }

  public String insertRadusIfNotExiste() {
    try {
      // TODO Auto-generated method stub
      List<Abonnement> abonnmentsNotAffecter = abonnementRepository
          .findAllByStatut_nomStatutAndLoginModemIsNull(NomStatutChifco.ASSIGNED);
      for (Abonnement abonnement : abonnmentsNotAffecter) {

        if (abonnement.getLoginModem() == null && !abonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {

          Radcheck radcheckRow = radcheckService.getRadchecksByUsernameAndAttribute(
              abonnement.getModem().getEmail(), RedchekConstant.CleartextPassword);
          if (radcheckRow == null) {
            Radcheck infoRadus = radcheckService.getRadchecksByUsernameAndAttribute(
                abonnement.getLoginModem(), RedchekConstant.Expiration);
            if (radcheckRow == null && infoRadus == null) {
              radcheckService.addNewRow(abonnement.getModem().getEmail(),
                  RedchekConstant.CleartextPassword, abonnement.getModem().getPassword());
              radcheckService.addNewRow(abonnement.getModem().getEmail(),
                  RedchekConstant.Expiration,
                  CrmUtils.RadusDateDexpiration(abonnement.getDateProchainFacturation()));
              radcheckService.AddNewradusergroup(abonnement.getModem().getEmail(),
                  abonnement.getPack().getCategoriePack().getCategorieProduitInternetCode());
              logger
                  .info("create new row  in raduse data base with email " + abonnement.getModem());
            } else {
              logger.info("raduse info avec cette user name " + abonnement.getModem().getEmail());
            }

          }
        }
      }
      return "don";
    } catch (Exception err) {
      return err.getMessage();

    }
  }


  @Override
  public Long findIdClientByIdDemandeAbonnment(Long idDemandeAbonnment) {
    // TODO Auto-generated method stub
    return abonnementRepository.findIdClientByIdDemandeAbonnment(idDemandeAbonnment);
  }

  @Override
  public ModelAndView extractEnMasseClientNonConnecter(HttpServletRequest request,
      HttpServletResponse response, Long gouvernorat, Long villes, String cin, String nom,
      String prenom, String codeClient, Long tel, Long category, Long produit, String dateDebut,
      String dateFin, String dateDebutModification, String dateFinModification, String loginModem,
      Long affecterTo, Long creepar, String exportRecherchedatedebutAffectionModem) {
    ModelAndView mav = new ModelAndView();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String firstName = null;
    String lastName = null;
    String modemLogin = null;
    String cinClient = null;
    String referenceClient = null;
    String statutChifco = null;
    Date datedebut = null;
    Date dateDeFin = null;
    Date dateDeFinDeModification = null;
    Date datedebutDeModification = null;
    Date dateAffectionModem = null;

    Boolean listeClientActiveFilter = null;
    if (nom != null && !nom.isEmpty()) {
      firstName = nom;
    }
    if (!prenom.isEmpty()) {
      lastName = prenom;
    }
    if (!loginModem.isEmpty()) {
      modemLogin = loginModem;
    }
    if (!cin.isEmpty()) {
      cinClient = cin;
    }
    if (!codeClient.isEmpty()) {
      referenceClient = codeClient;
    }

    if (dateDebut != null && !dateDebut.isEmpty()) {
      try {
        dateDebut = dateDebut + " 00:00:00";

        datedebut = dateFormat.parse(dateDebut);
      } catch (ParseException e) {
        logger.warn("findAllUserByRecherche : " + e.getMessage());
      }
    }

    if (dateFin != null && !dateFin.isEmpty()) {
      try {
        dateFin = dateFin + "  23:59:59";

        dateDeFin = dateFormat.parse(dateFin);
      } catch (ParseException e) {
        logger.warn("findAllUserByRecherche : " + e.getMessage());
      }
    }

    if (dateDebutModification != null && !dateDebutModification.isEmpty()) {
      try {
        dateDebutModification = dateDebutModification + " 00:00:00";

        datedebutDeModification = dateFormat.parse(dateDebutModification);
      } catch (ParseException e) {
        logger.warn("findAllUserByRecherche : " + e.getMessage());
      }
    }

    if (dateFinModification != null && !dateFinModification.isEmpty()) {
      try {
        dateFinModification = dateFinModification + "  23:59:59";

        dateDeFinDeModification = dateFormat.parse(dateFinModification);
      } catch (ParseException e) {
        logger.warn("findAllUserByRecherche : " + e.getMessage());
      }
    }

    if (exportRecherchedatedebutAffectionModem != null
        && !exportRecherchedatedebutAffectionModem.isEmpty()) {
      try {
        exportRecherchedatedebutAffectionModem =
            exportRecherchedatedebutAffectionModem + "  23:59:59";

        dateAffectionModem = dateFormat.parse(exportRecherchedatedebutAffectionModem);
      } catch (ParseException e) {
        logger.warn("findAllUserByRecherche : " + e.getMessage());
      }
    }
    List<Abonnement> myList = new ArrayList<>();
    myList = abonnementRepository.exportAllClientNonConnecter(firstName, lastName, cinClient,
        referenceClient, tel, villes, gouvernorat, produit, category, datedebut, dateDeFin,
        datedebutDeModification, dateDeFinDeModification, modemLogin, affecterTo);


    if (myList.size() > 0) {

      mav.setView(new AbonnementNonConnecterExcelExport());
      mav.addObject("list", myList);
      return mav;
    } else {
      try {
        request.getRequestDispatcher("/client/allclients/1").forward(request, response);
      } catch (ServletException e) {
        // TODO Auto-generated catch block

        logger.error("msg1 : xls client non connecter Error:" + e);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        logger.error("msg2 : xls client non connecter Error:" + e);
      }
      return mav;
    }

  }

  @Override
  public HashMap<String, Object> getallClientNonConnecter(int draw, int start, int length,
      String search, int ordercolumnaram, String orderdir, String filterrecherche) {
    String firstName = null;
    String lastName = null;
    String cin = null;
    String codeClient = null;
    Long tel = null;
    Long villes = null;
    Long categories = null;
    Long produit = null;
    Long gouvernorat = null;
    Date datedebut = null;
    Date datefin = null;
    Date dateDebutModification = null;
    Date dateFinModification = null;
    String loginModem = null;
    String sortvar = "";

    Page<Abonnement> responseData = null;
    int currentpage = start / length;
    HashMap<String, Object> myGreetings = new HashMap<>();

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("firstName"), "") && obj.getString("firstName") != null) {
        firstName = obj.getString("firstName").trim();
      }
      if (!Objects.equals(obj.getString("loginModem"), "") && obj.getString("loginModem") != null) {
        loginModem = obj.getString("loginModem").trim();
      }
      if (!Objects.equals(obj.getString("lastName"), "") && obj.getString("lastName") != null) {
        lastName = obj.getString("lastName").trim();
      }
      if (!Objects.equals(obj.getString("cin"), "") && obj.getString("cin") != null) {
        cin = obj.getString("cin").trim();
      }
      if (!Objects.equals(obj.getString("CodeClient"), "") && obj.getString("CodeClient") != null) {
        codeClient = obj.getString("CodeClient").trim();
      }

      if (!Objects.equals(obj.getString("Tel"), "") && obj.getString("Tel") != null) {
        tel = Long.parseLong(obj.getString("Tel").trim());
      }
      if (!Objects.equals(obj.getString("villes"), "")
          && !Objects.equals(obj.getString("villes"), "")) {
        villes = Long.parseLong(obj.getString("villes").trim());
      }
      if (!Objects.equals(obj.getString("gouvernorats"), "")
          && obj.getString("gouvernorats") != null) {
        gouvernorat = Long.parseLong(obj.getString("gouvernorats").trim());
      }
      if (!Objects.equals(obj.getString("Categories"), "") && obj.getString("Categories") != null) {
        categories = Long.parseLong(obj.getString("Categories").trim());
      }
      if (!Objects.equals(obj.getString("Produit"), "") && obj.getString("Produit") != null) {
        produit = Long.parseLong(obj.getString("Produit").trim());
      }
      if (!Objects.equals(obj.getString("datedebut"), "") && obj.getString("datedebut") != null) {
        datedebut = CrmUtils.convertStringToDate(obj.getString("datedebut"));
      }
      if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
        datefin = CrmUtils.convertStringToLocalDateTime(obj.getString("datefin"));
      }

      if (!Objects.equals(obj.getString("dateDebutModification"), "")
          && obj.getString("dateDebutModification") != null) {
        dateDebutModification =
            CrmUtils.convertStringToDate(obj.getString("dateDebutModification"));
      }
      if (!Objects.equals(obj.getString("dateFinModification"), "")
          && obj.getString("dateFinModification") != null) {
        dateFinModification =
            CrmUtils.convertStringToLocalDateTime(obj.getString("dateFinModification"));
      }



    }

    switch (ordercolumnaram) {

      case 1:
        sortvar = "firstName";
        break;
      case 2:
        sortvar = "adresse";

        break;
      case 3:
        sortvar = "ville";
        break;
      case 4:
        sortvar = "telFixe";
        break;
      case 5:
        sortvar = "pack";
        break;
      case 6:
        sortvar = "statut";
        break;
      case 7:
        sortvar = "user";
        break;
      case 8:
        sortvar = "createdDate";
        break;
      default:
        sortvar = "createdDate";

    }
    Sort sort = Sort.by("createdDate");
    if (orderdir.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!orderdir.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(currentpage, length, sort);
    responseData = this.abonnementRepository.findAllClientNonConnecter(pageable, firstName,
        lastName, cin, codeClient, tel, villes, gouvernorat, produit, categories, datedebut,
        datefin, dateDebutModification, dateFinModification, loginModem);



    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;

  }

  @Override
  public void addToRadus(Long clientid, RedirectAttributes redirectAttrs) {
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
    Facture ChekIfIsFirstFactureExist = factureRepository.ChekIfIsFirstFactureExist(clientid);
    if (ChekIfIsFirstFactureExist != null && abonnement.getModem() != null && !abonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {

      if (abonnement.getLoginModem() == null) {
        abonnement.setLoginModem(abonnement.getModem().getEmail());
        abonnement.setPassword(abonnement.getModem().getPassword());
        abonnementRepository.save(abonnement);


      }
      Radcheck radcheck = radcheckService.getRadchecksByUsernameAndAttribute(
          abonnement.getLoginModem(), RedchekConstant.Expiration);
      if (radcheck == null) {
        radcheckService.addNewRow(abonnement.getLoginModem(), RedchekConstant.CleartextPassword,
            abonnement.getPassword());
        radcheckService.addNewRow(abonnement.getLoginModem(), RedchekConstant.Expiration,
            CrmUtils.RadusDateDexpiration(ChekIfIsFirstFactureExist.getDateDeFin()));
        radcheckService.AddNewradusergroup(abonnement.getLoginModem(),
            abonnement.getPack().getCategoriePack().getCategorieProduitInternetCode());
      }
      logger.info("create new row  in raduse data base with email " + abonnement.getLoginModem());
    }

  }

  @Override
  public void ReprendreCycleAbonnement(Long clientid, RedirectAttributes redirectAttrs) {
    // TODO Auto-generated method stub
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      Abonnement abonnmentById = abonnementRepository.findAbonnementByClientid(clientid);
      if (abonnmentById != null) {
        Facture lastFacture =
            factureRepository.findTopByAbonnement_clientidOrderByFactureIdDesc(clientid);
        abonnmentById.setDateProchainFacturation(lastFacture.getDateDeFin());
        abonnementRepository.save(abonnmentById);

        abonnementHistoriqueService.saveNewHistorique(user, abonnmentById.getClientid(),
            "Reprendre du cycle de facturation a été effectuée.");
      }
    }
  }

  @Override

  public void changrRadusAbonnement(Long clientid, RedirectAttributes redirectAttrs) {
    // TODO Auto-generated method stub
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
      if (abonnement.getLoginModem() != null) {
        Radcheck radcheck = radcheckService.getRadchecksByUsernameAndAttribute(
            abonnement.getLoginModem(), RedchekConstant.Expiration);
        if (radcheck != null) {
          Date newExpirationRadus =
              CrmUtils.addDaysToGivenDate(CrmUtils.stringToDate(radcheck.getValue()), 5L);
          radcheckService.updateDateExpiration(
              CrmUtils.ChangeRadusDateDexpiration(newExpirationRadus), abonnement.getLoginModem());
          abonnementHistoriqueService.saveNewHistorique(user, clientid,
              "Un changement du date d'expiration de connection a été effectuée ");
        }
      }
    }
  }

  @Override
  public void changrProchainDateFacturationAbonnement(Long clientid, String dateNouvelle,
      RedirectAttributes redirectAttrs) {
    // TODO Auto-generated method stub
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      Facture lastFacture =
          factureRepository.findTopByAbonnement_clientidOrderByFactureIdDesc(clientid);
      if (lastFacture != null && lastFacture.getEtat_facture() == true) {
        Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
        if (abonnement.getDateProchainFacturation() != null) {
          Date nouvelleDate = CrmUtils.convertStringToDate(dateNouvelle);
          abonnementHistoriqueService.saveNewHistorique(user, clientid,
              "Un changement du cycle de facturation a été effectuée de "
                  + CrmUtils.formatedDate(abonnement.getDateProchainFacturation()) + " a "
                  + CrmUtils.formatedDate(nouvelleDate));
          Long DayConsomee =
              CrmUtils.DateDifference(abonnement.getDateProchainFacturation(), nouvelleDate);

          Tarification tarificationpack =
              tarificationServices.getTarificationBypackId(abonnement.getPack().getPackId());
          Double differencePrice = CrmUtils
              .CalculePrixPackConsomeParJours(tarificationpack.getPrixUnitaire(), DayConsomee, 30L);
          MigrationFacture newFactureMigrationCalcule = new MigrationFacture();
          newFactureMigrationCalcule.setMontantHt(differencePrice);
          newFactureMigrationCalcule.setClientid(abonnement.getClientid());
          newFactureMigrationCalcule.setMontantTva((differencePrice * 7) * 0.01);
          newFactureMigrationCalcule.setTypeCalcule(typeCalcluleMigrationFacture.MIGRATION);
          newFactureMigrationCalcule
              .setNameMigration("Complément  changement du cycle de facturation a été effectuée de "
                  + CrmUtils.formatedDate(abonnement.getDateProchainFacturation()) + " a "
                  + CrmUtils.formatedDate(nouvelleDate));
          newFactureMigrationCalcule.setPercentTva(7L);
          migrationFactureRepository.save(newFactureMigrationCalcule);
          abonnement.setDateProchainFacturation(nouvelleDate);
          abonnementRepository.save(abonnement);
          redirectAttrs.addFlashAttribute("message", "cycleFactureChangee");
        } else {
          redirectAttrs.addFlashAttribute("message", "cycleFactureSuspendu");
        }

      } else {
        redirectAttrs.addFlashAttribute("message", "facture non payee");
      }

    }

  }

  // service nbre d'abonnement mobile app
  @Override
  public Map<String, Object> getAbonnementSummaryForMonth(Date startOfMonth, Date endOfMonth,
      Long revId) {
    Object[] result =
        (Object[]) abonnementRepository.getAbonnementSummaryMobApp(startOfMonth, endOfMonth, revId);
    Map<String, Object> response = new HashMap<>();
    response.put("activeCountToday", result[0]);
    response.put("activeCountThisMonth", result[1]);
    return response;
  }

  public HashMap<String, Object> getallClientNonConnecterMiseService(int draw, int start,
      int length, String search, int ordercolumnaram, String orderdir, String filterrecherche) {
    // TODO Auto-generated method stub
    
      String firstName = null;
      String lastName = null;
      String cin = null;
      String codeClient = null;
      Long tel = null;
      Long villes = null;
      Long categories = null;
      Long produit = null;
      Long gouvernorat = null;
      Date datedebut = null;
      Date datefin = null;
      Date dateDebutModification = null;
      Date dateFinModification = null;
      String loginModem = null;
      String sortvar = "";

      Page<Abonnement> responseData = null;
      int currentpage = start / length;
      HashMap<String, Object> myGreetings = new HashMap<>();

      if (filterrecherche != null && !filterrecherche.equals("")) {
        JSONObject obj = new JSONObject(filterrecherche);
        if (!Objects.equals(obj.getString("firstName"), "") && obj.getString("firstName") != null) {
          firstName = obj.getString("firstName").trim();
        }
        if (!Objects.equals(obj.getString("loginModem"), "")
            && obj.getString("loginModem") != null) {
          loginModem = obj.getString("loginModem").trim();
        }
        if (!Objects.equals(obj.getString("lastName"), "") && obj.getString("lastName") != null) {
          lastName = obj.getString("lastName").trim();
        }
        if (!Objects.equals(obj.getString("cin"), "") && obj.getString("cin") != null) {
          cin = obj.getString("cin").trim();
        }
        if (!Objects.equals(obj.getString("CodeClient"), "")
            && obj.getString("CodeClient") != null) {
          codeClient = obj.getString("CodeClient").trim();
        }

        if (!Objects.equals(obj.getString("Tel"), "") && obj.getString("Tel") != null) {
          tel = Long.parseLong(obj.getString("Tel").trim());
        }
        if (!Objects.equals(obj.getString("villes"), "")
            && !Objects.equals(obj.getString("villes"), "")) {
          villes = Long.parseLong(obj.getString("villes").trim());
        }
        if (!Objects.equals(obj.getString("gouvernorats"), "")
            && obj.getString("gouvernorats") != null) {
          gouvernorat = Long.parseLong(obj.getString("gouvernorats").trim());
        }
        if (!Objects.equals(obj.getString("Categories"), "")
            && obj.getString("Categories") != null) {
          categories = Long.parseLong(obj.getString("Categories").trim());
        }
        if (!Objects.equals(obj.getString("Produit"), "") && obj.getString("Produit") != null) {
          produit = Long.parseLong(obj.getString("Produit").trim());
        }
        if (!Objects.equals(obj.getString("datedebut"), "") && obj.getString("datedebut") != null) {
          datedebut = CrmUtils.convertStringToDate(obj.getString("datedebut"));
        }
        if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
          datefin = CrmUtils.convertStringToLocalDateTime(obj.getString("datefin"));
        }

        if (!Objects.equals(obj.getString("dateDebutModification"), "")
            && obj.getString("dateDebutModification") != null) {
          dateDebutModification =
              CrmUtils.convertStringToDate(obj.getString("dateDebutModification"));
        }
        if (!Objects.equals(obj.getString("dateFinModification"), "")
            && obj.getString("dateFinModification") != null) {
          dateFinModification =
              CrmUtils.convertStringToLocalDateTime(obj.getString("dateFinModification"));
        }



      }

      switch (ordercolumnaram) {

        case 1:
          sortvar = "firstName";
          break;
        case 2:
          sortvar = "adresse";

          break;
        case 3:
          sortvar = "ville";
          break;
        case 4:
          sortvar = "telFixe";
          break;
        case 5:
          sortvar = "pack";
          break;
        case 6:
          sortvar = "statut";
          break;
        case 7:
          sortvar = "user";
          break;
        case 8:
          sortvar = "createdDate";
          break;
        default:
          sortvar = "createdDate";

      }
      Sort sort = Sort.by("createdDate");
      if (orderdir.equals("desc")) {
        sort = Sort.by(sortvar).descending();
      } else if (!orderdir.equals("desc")) {
        sort = Sort.by(sortvar).ascending();
      }
      Pageable pageable = PageRequest.of(currentpage, length, sort);
      responseData =


          this.abonnementRepository.getallClientNonConnecterMiseService(pageable, firstName,
              lastName, cin, codeClient, tel, villes, gouvernorat, produit, categories, datedebut,
              datefin, dateDebutModification, dateFinModification, loginModem);



      if (responseData != null) {
        myGreetings.put("data", responseData.getContent());
        myGreetings.put("recordsTotal", responseData.getTotalElements());
        myGreetings.put("recordsFiltered", responseData.getTotalElements());
      }
      myGreetings.put("draw", draw);
      myGreetings.put("start", start);
      return myGreetings;
    

  }


@Override
public Abonnement findAbonnementByReferenceClientAndUpdate(String referenceChifco , String montantComision) {
	Abonnement abonnement = findAbonnementByReferenceClient(referenceChifco);
	abonnement.setComisionActivationFreelancer(Double.parseDouble(montantComision));
	abonnement.setComissionActivationIsPayed(false);
	abonnementRepository.save(abonnement);
	return abonnement;
}

@Override
public List<Abonnement> findAbonnementsByReferenceClient(List<String> referenceChifco) {
	// TODO Auto-generated method stub
	return abonnementRepository.findAbonnementsByReferenceClientIn(referenceChifco);
}


  @Override
  public File createPDFRecuResilationA4(Long idClient, String numSerieModem)
      throws Exception, JRException {
    File file1 = null;

    Map<String, Object> parametes = new HashMap<>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      // String currentUser = authentication.getName();
      // User user = userRepository.findUsersByEmail(currentUser);

      parametes.put("image1", new ClassPathResource("reports/netyrecu.png").getInputStream());
      parametes.put("numSerieModem", numSerieModem);


      // Double montant = factureRepository.findSumByListFactureIdS(factureids);
      List<Facture> ListFacture =
       factureRepository.getFacturesByAbonnement_clientidAndIsFactureResilation(idClient, true);
   

      if (ListFacture != null && !ListFacture.isEmpty()) {
      
    	  parametes.put("DATE_RESILIATION", ListFacture.get(0).getCreatedDate());

      }
      else {
    	  parametes.put("DATE_RESILIATION", new Date()); 
      }
      Abonnement myAbonnement = abonnementRepository.findAbonnementByClientid(idClient);

      // if (ListFacture.size() > 0) {

      Collection<User> UserList = new ArrayList<>();
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      UserList.add(user);

      Collection<PaymentDataSet> PaymentDataSetArrayList = new ArrayList<>();
      PaymentDataSet paymentDataSet = new PaymentDataSet();
      paymentDataSet.setFactures(ListFacture);
      paymentDataSet.setUsers(UserList);
      Collection<Abonnement> abonnement = new ArrayList<>();

      abonnement.add(myAbonnement);

      paymentDataSet.setAbonnements(abonnement);
      PaymentDataSetArrayList.add(paymentDataSet);
      ///

      ///

      File file = ResourceUtils.getFile("classpath:reports/ficherecuresiationtA4.jrxml");
      JRBeanCollectionDataSource dataSource =
          new JRBeanCollectionDataSource(PaymentDataSetArrayList);
      JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametes, dataSource);

      File foldertocreate =
          new File(pathRecuResilation + CrmUtils.getYear() + "/" + CrmUtils.getMonth());
      if (!foldertocreate.exists()) {
        foldertocreate.mkdirs();
        foldertocreate.setWritable(true);
      }

      String fileName = pathRecuResilation + CrmUtils.getYear() + "/" + CrmUtils.getMonth() + "/"
          + "RECU" + idClient + ".pdf";
      JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);

      file1 = ResourceUtils.getFile(fileName);

      // }
      // }

    }
    return file1;
  }

}
