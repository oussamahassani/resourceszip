package crm.chifco.com.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.CategorieProduitInternet;
import crm.chifco.com.model.ClassificationDemande;
import crm.chifco.com.model.Commande;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.EntryCommande;
import crm.chifco.com.model.EntryDemandeAbonnement;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.Offre;
import crm.chifco.com.model.OtpSms;
import crm.chifco.com.model.Pack;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.Profession;
import crm.chifco.com.model.Typepaiement;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Ville;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.ClassificationDemandeRepository;
import crm.chifco.com.repository.CodePostaleRepository;
import crm.chifco.com.repository.CommandeRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.ProfessionRepository;
import crm.chifco.com.repository.TarificationRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AbonnementHistoriqueService;
import crm.chifco.com.service.AbonnementService;
import crm.chifco.com.service.ClientHistoryService;
import crm.chifco.com.service.DemandeAbonnementService;
import crm.chifco.com.service.FactureService;
import crm.chifco.com.service.ImportExcel;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.OffreService;
import crm.chifco.com.service.OtpService;
import crm.chifco.com.service.PackService;
import crm.chifco.com.service.ReportService;
import crm.chifco.com.service.TypePaiementService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.service.VillesService;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.TypeAbonnment;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping(value = "demandeabonnement/*")
public class DemandeAbonnementController {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  private UserRepository userRepository;
  @Autowired
  UserService userService;

  @Autowired
  private DemandeAbonnementService demandeAbonnementService;

  @Autowired
  private CategorieProduitInternetRepository categorieProduitInternetRepository;

  @Autowired
  private GouvernoratRepository gouvernoratRepository;

  @Autowired
  private ReportService reportService;

  @Autowired
  private ProfessionRepository professionRepository;

  @Autowired
  private ImportExcel importExcel;

  @Autowired
  private Notification notification;

  @Autowired
  private TypePaiementService typePaiementService;

  @Autowired
  private VillesService villesService;

  @Autowired
  private OtpService otpService;

  @Autowired
  private CodePostaleRepository codePostaleRepository;

  @Autowired
  private DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  OffreService offreService;

  @Autowired
  PackService packService;

  @Autowired
  TarificationRepository tarificationRepository;

  @Autowired
  private AbonnementHistoriqueService abonnementHistoriqueService;

  @Autowired
  private ClassificationDemandeRepository classificationDemandeRepository;
  @Autowired
  private AbonnementRepository abonnementRepository;


  @Autowired
  private FactureService factureService;

  @Autowired
  CommandeRepository commandeRepository;


  @Autowired
  private ClientHistoryService clientHistoryService;

  @Autowired
  AbonnementService abonnementService;

  @ResponseBody
  @PostMapping(value = "/addid")
  public JsonResponseBody addfiled(@RequestBody Long id, HttpServletRequest request) {
    return demandeAbonnementService.addFiled(id, request);
  }

  /** allabonement */

  @RequestMapping(method = RequestMethod.GET, value = "getallabonement")
  @ResponseBody
  public HashMap<String, Object> getAlAbonnement(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    return demandeAbonnementService.getAlAbonnement(draw, start, length, search, ordercolumnaram,
        orderdir, filterrecherche);

  }


  @RequestMapping(method = RequestMethod.GET, value = "/NombreDemande")

  @ResponseBody
  public List<Map<String, Object>> chiffredaffaire(@RequestParam String date,
      @RequestParam String type, @RequestParam(required = false) Long distributeur) {
    List<Map<String, Object>> commande = new ArrayList<Map<String, Object>>();
    Date startOfSelectedDate = null;
    Date endOfSelectedDate = null;
    if (date != null && !date.isEmpty()) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
      YearMonth yearMonth = YearMonth.parse(date, formatter);
      LocalDateTime startOfSelectedDateTime = yearMonth.atDay(1).atStartOfDay();
      LocalDateTime endOfSelectedDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
      startOfSelectedDate =
          Date.from(startOfSelectedDateTime.atZone(ZoneId.systemDefault()).toInstant());
      endOfSelectedDate =
          Date.from(endOfSelectedDateTime.atZone(ZoneId.systemDefault()).toInstant());
      commande = demandeAbonnementRepository.getDemandeAbonnementCountByChefSecteurAndDateRange(
          startOfSelectedDate, endOfSelectedDate, distributeur);
    }

    if ("total10".equalsIgnoreCase(type)) {
      commande = demandeAbonnementRepository
          .getDemandeAbonnementCountAndDateRange(startOfSelectedDate, endOfSelectedDate);

    } else if ("byChefSecteur10".equalsIgnoreCase(type)) {
      commande = demandeAbonnementRepository.getDemandeAbonnementCountByChefSecteurAndDateRange(
          startOfSelectedDate, endOfSelectedDate, distributeur);
    } else {
      throw new IllegalArgumentException("Invalid type selected: " + type);
    }
    return commande;
  }

  @GetMapping("/NombreRejectCommercial")
  @ResponseBody
  public List<Map<String, Object>> getRejectCommercialCount(@RequestParam String date,
      @RequestParam String type, @RequestParam(required = false) Long distributeur) {

    Date startOfSelectedDate = null;
    Date endOfSelectedDate = null;
    List<Map<String, Object>> results = null;
    if (date != null && !date.isEmpty()) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
      YearMonth yearMonth = YearMonth.parse(date, formatter);
      LocalDateTime startOfSelectedDateTime = yearMonth.atDay(1).atStartOfDay();
      LocalDateTime endOfSelectedDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);

      startOfSelectedDate =
          Date.from(startOfSelectedDateTime.atZone(ZoneId.systemDefault()).toInstant());
      endOfSelectedDate =
          Date.from(endOfSelectedDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    if ("total3".equalsIgnoreCase(type)) {
      results = demandeAbonnementRepository
          .getRejectCommerCountTotalAndDateRange(startOfSelectedDate, endOfSelectedDate);
    } else if ("byChefSecteur3".equalsIgnoreCase(type)) {
      results = demandeAbonnementRepository.getRejectCommerCountByChefSecteurAndDateRange(
          startOfSelectedDate, endOfSelectedDate, distributeur);
    } else {
      throw new IllegalArgumentException("Invalid type selected: " + type);
    }
    return results;
  }


  @GetMapping("/countByGouvernorat")
  @ResponseBody
  public List<Map<String, Object>> countDemandeByGouvernorat(@RequestParam String date,
      @RequestParam(required = false) Long distributeur,
      @RequestParam(required = false) Long gouvernoratId) {
    List<Object[]> results;
    if (!date.equals("")) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
      YearMonth yearMonth = YearMonth.parse(date, formatter);
      LocalDateTime startOfSelectedDateTime = yearMonth.atDay(1).atStartOfDay();
      LocalDateTime endOfSelectedDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
      Date startOfSelectedDate =
          Date.from(startOfSelectedDateTime.atZone(ZoneId.systemDefault()).toInstant());
      Date endOfSelectedDate =
          Date.from(endOfSelectedDateTime.atZone(ZoneId.systemDefault()).toInstant());
      results = abonnementRepository.countActiveAbonnementsByGouvernorat(startOfSelectedDate,
          endOfSelectedDate, distributeur, gouvernoratId);
    } else {
      results = abonnementRepository.countActiveAbonnementsByGouvernorat(null, null, distributeur,
          gouvernoratId);
    }
    List<Map<String, Object>> response = new ArrayList<>();

    for (Object[] result : results) {
      Map<String, Object> item = new HashMap<>();
      item.put("gouvernoratName", result[0]);
      item.put("demandeCount", result[1]);
      response.add(item);
    }

    return response;
  }

  @RequestMapping(method = RequestMethod.GET, value = "getfiltredstatusabonnemnt")
  @ResponseBody
  public HashMap<String, Object> getfiltredstatusabonnemnt(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") int ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche, Long status) {

    return demandeAbonnementService.getfiltredStatusAbonnemnt(draw, start, length, search,
        ordercolumnaram, orderdir, filterrecherche, status);

  }

  @RequestMapping(method = RequestMethod.GET,
      value = "getfiltredstatusabonnemnSuividesDemandesTransferees")
  @ResponseBody
  public HashMap<String, Object> getfiltredstatusabonnemnSuividesDemandesTransferees(
      @RequestParam("draw") int draw, @RequestParam("start") int start,
      @RequestParam("length") int length, @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") int ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche, Long status) {

    return demandeAbonnementService.getfiltredstatusabonnemnSuividesDemandesTransferees(draw, start,
        length, search, ordercolumnaram, orderdir, filterrecherche, status);

  }

  @ResponseBody
  @PostMapping(value = "/removeid")
  public JsonResponseBody removefiled(@RequestBody Long id, HttpServletRequest request) {
    return demandeAbonnementService.removeFiled(id, request);
  }

  @ResponseBody
  @PostMapping(value = "/addall")
  public List<Long> addAllIdToExport(@RequestBody String filterrecherche,
      HttpServletRequest request) {
    return demandeAbonnementService.addAllIdToExport(filterrecherche, request);
  }

  @ResponseBody
  @PostMapping(value = "/removeall")
  public JsonResponseBody removeAllFromListExport(HttpServletRequest request) {
    return demandeAbonnementService.removeAllFromListExport(request);
  }

  @GetMapping(value = "alldemandesnonsigneer")
  public String alldemandesnonsigneer(Model model, HttpServletRequest request) {
    demandeAbonnementService.alldemandesNonSigneer(model, request);
    return "demandeabonnement/alldemandesabonnementNonSignee";

  }

  @GetMapping(value = "alldemandesencours")
  public String alldemandesencours(Model model, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userrole", StringsRole.contains("WRITE_ABONNEMENT_STATUS"));
      model.addAttribute("useremail", user.getEmail());
      Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
      logger.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
      if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
        String[] listesvides = {};
        model.addAttribute("listedes_ids", listesvides);
      } else {
        Object listedesIds = request.getSession().getAttribute("listedes_ids");
        model.addAttribute("listedes_ids", listedesIds);
      }
      List<Gouvernorat> listvilles = gouvernoratRepository.findAll();
      List<Profession> listprofessions = professionRepository.findByisActive(true);
      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("professions", listprofessions);
      model.addAttribute("villes", listvilles);
      model.addAttribute("categories", categorieProduitInternets);
    }
    return "demandeabonnement/alldemandesabonnementEnCour";

  }

  @GetMapping(value = "alldemandestraiter")
  public String alldemandestraiter(Model model, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userrole", StringsRole.contains("WRITE_ABONNEMENT_STATUS"));
      Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
      logger.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
      if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
        String[] listesvides = {};
        model.addAttribute("listedes_ids", listesvides);
      } else {
        Object listedesIds = request.getSession().getAttribute("listedes_ids");
        model.addAttribute("listedes_ids", listedesIds);
      }
      List<Gouvernorat> listvilles = gouvernoratRepository.findAll();
      List<Profession> listprofessions = professionRepository.findByisActive(true);
      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("professions", listprofessions);
      model.addAttribute("villes", listvilles);
      model.addAttribute("categories", categorieProduitInternets);
    }
    return "demandeabonnement/alldemandesabonnementTraiter";

  }

  @GetMapping(value = "alldemandestransferees")
  public String alldemandestransferees(Model model, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userrole", StringsRole.contains("WRITE_ABONNEMENT_STATUS"));
      Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
      logger.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
      if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
        String[] listesvides = {};
        model.addAttribute("listedes_ids", listesvides);
      } else {
        Object listedesIds = request.getSession().getAttribute("listedes_ids");
        model.addAttribute("listedes_ids", listedesIds);
      }
      List<Gouvernorat> listvilles = gouvernoratRepository.findAll();
      List<Profession> listprofessions = professionRepository.findByisActive(true);
      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("professions", listprofessions);
      model.addAttribute("villes", listvilles);
      model.addAttribute("categories", categorieProduitInternets);
    }
    return "demandeabonnement/Suividesdemandestransferees";

  }

  @GetMapping(value = "alldemandesvalider")
  public String alldemandesvalider(Model model, HttpServletRequest request) {

    return demandeAbonnementService.allDemandesValider(model, request);

  }

  @PreAuthorize("hasAuthority('SEARCH_SUBSCRIPTION_REQUEST_ALL')"
      + "|| hasAuthority('SEARCH_SUBSCRIPTION_REQUEST_RETAIL')")
  @GetMapping(value = "recherchefichedemande")
  public String recherchefichedemande(Model model, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
    model.addAttribute("userphoto", user.getPhoto());
    model.addAttribute("userrole", user.getRole().getRoleName());
    model.addAttribute("useremail", user.getEmail());
    return "demandeabonnement/Rechercheficheclient";
  }

  @RequestMapping(value = "rechercheabonnementresultat")
  public String rechercherficherclient(@RequestParam(value = "reftt") String reftt,
      @RequestParam(value = "refchifco") String refchifco,
      @RequestParam(value = "numfixe") String numfixe,
      @RequestParam(value = "numcin") String numcin, RedirectAttributes redirectAttrs,
      Model model) {
    List<DemandeAbonnement> listeDemandeAbonnement = demandeAbonnementService
        .rechercherFicherClient(reftt, refchifco, numfixe, numcin, redirectAttrs);
    model.addAttribute("abonnement", listeDemandeAbonnement);
    if (listeDemandeAbonnement.size() == 0) {
      redirectAttrs.addFlashAttribute("message", "demade non existe");
      return "redirect:/demandeabonnement/recherchefichedemande?usernotfound=true";
    }
    return "demandeabonnement/rechercheabonnementresultat";

  }

  @GetMapping(value = "alldemandesabonnement/{pageNo}/{pageSize}")
  public String clients(@PathVariable(value = "pageNo") Integer pageNo, Model model, String keyword,
      Long villes, Long gouvernorat, Long professions, Long categories, Long produit,
      Long statutListfiltre, @Param("datedebut") String datedebut, @Param("datefin") String datefin,
      @PathVariable(value = "pageSize") Integer pageSize, HttpServletRequest request) {

    return demandeAbonnementService.allDemandesAbonnement(pageNo, model, keyword, villes,
        gouvernorat, professions, categories, produit, statutListfiltre, datedebut, datefin,
        pageSize, request);

  }

  @GetMapping("add-demandeabonnement")
  public String adddemandeabonnement(Model model) {
    return demandeAbonnementService.getAddDemandeAbonnement(model);
  }

  @GetMapping("adddemandeabonnementwithsteps")
  public String getadddemandeabonnementwithsteps(Model model) {

    return demandeAbonnementService.getUrlDemandeAbonnementWithSteps(model);
  }

  @RequestMapping(method = RequestMethod.POST, value = "adddemandeabonnementwithsteps")
  public String adddemandeabonnementwithsteps(
      @RequestParam("raccordementtranche") int raccordementtranche, @RequestParam("Nom") String Nom,
      @RequestParam("Prenom") String Prenom,
      @RequestParam(value = "Email", required = false) String Email,
      @RequestParam("cin") String cin, @RequestParam("villes") Gouvernorat ville,
      @RequestParam("gouvernorat") Long gouvernoratid, @RequestParam("adresse") String adresse,
      @RequestParam("codepostale") PostalCode codepostale,
      @RequestParam("telMobile") Long telMobile,
      @RequestParam(value = "telMobile2", required = false) Long telMobile2,
      @RequestParam(value = "telFixe", required = false) Long telFixe,
      @RequestParam(value = "fax", required = false) Long fax,
      @RequestParam(value = "positionxy", required = false) String positionxy,
      @RequestParam(value = "professions", required = false) Profession profession,

      @RequestParam("typepaiements") Typepaiement typepaiement,
      @RequestParam("customRadio") Long categorieProduitInternet,
      @RequestParam("customRadioproduit") Long produitid,
      @RequestParam("residence") Boolean residence,
      @RequestParam(value = "houseHolder", required = false) Boolean houseHolder,
      @RequestParam(value = "hasBankCard", required = false) Boolean hasBankCard,
      @RequestParam(value = "datedenaissance", required = false) String datedenaissance,
      @RequestParam("imageFile") MultipartFile imageFile,
      @RequestParam("imageFile2") MultipartFile imageFile2,
      @RequestParam(value = "multipleSubproduct[]", required = false) Long[] multipleSubproduct,
      @RequestParam(value = "situationFamiliale", required = false) String situationFamiliale,
      @RequestParam("origin") String origin, Model model, RedirectAttributes redirectAttrs) {

    return demandeAbonnementService.addDemandeAbonnementWithSteps(raccordementtranche, Nom, Prenom,
        Email, cin, ville, gouvernoratid, adresse, codepostale, telMobile, telMobile2, telFixe, fax,
        positionxy, profession, typepaiement, categorieProduitInternet, produitid, datedenaissance,
        residence, imageFile, imageFile2, multipleSubproduct, situationFamiliale, houseHolder,
        hasBankCard, origin, model, redirectAttrs);
  }

  @PostMapping("/report")
  public void generateReport(DemandeAbonnement demandeAbonnement, HttpServletResponse response)
      throws Exception {
    // page 1 détails de Demande d'Abonnement de client

    // set file facure
    File file1 = reportService.generateReport(demandeAbonnement);

    // page 2 détails de contrat de Demande d'Abonnement
    DemandeAbonnement demandeAbonnements =
            demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeAbonnement.getDemandeId());
    File demandeAbonnementFile = ResourceUtils.getFile("classpath:reports/demandeDabonnement.pdf");
    
    if(demandeAbonnements.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box))
    {
        demandeAbonnementFile = ResourceUtils.getFile("classpath:reports/abonnment5G.pdf");

    }

    
    response.setContentType("application/x-pdf ; charset=" + StandardCharsets.UTF_8.displayName());
    response.setHeader("Content-disposition",
        "inline; filename=" + demandeAbonnementFile.getName());
    OutputStream outputStream = response.getOutputStream();

    // Merger two file and copy it to response's OutputStream
    PDFMergerUtility ut = new PDFMergerUtility();
    ut.addSource(file1);
    ut.addSource(demandeAbonnementFile);
    PDDocumentInformation info = new PDDocumentInformation();
    info.setTitle(file1.getName());
    ut.setDestinationDocumentInformation(info);
    ut.setDestinationStream(outputStream);
    ut.mergeDocuments();
    // close input stream file
    outputStream.close();
    // delete file
    CrmUtils.deleteFile(file1);

  }

  @PostMapping("/contratNonsigne")
  public void generateContratNonsigne(Long demandeAbonnementid, HttpServletResponse response)
      throws JRException, IOException {

    // page 1 détails de Demande d'Abonnement de client
    File file1 = reportService.generateContratNonsigne(demandeAbonnementid);
    // page 2 détails de contrat de Demande d'Abonnement
    File file2 = ResourceUtils.getFile("classpath:reports/contratDabonnement.pdf");
    DemandeAbonnement demandeAbonnement =
            demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeAbonnementid);
    if(demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box))
    {
    	file2 = ResourceUtils.getFile("classpath:reports/abonnment5G.pdf");

    }
    response.setContentType("application/x-pdf");
    response.setHeader("Content-disposition", "inline; filename=" + file1.getName());
    OutputStream outputStream = response.getOutputStream();

    // Merger two file and copy it to response's OutputStream
    PDFMergerUtility ut = new PDFMergerUtility();
    ut.addSource(file1);
    ut.addSource(file2);
    PDDocumentInformation info = new PDDocumentInformation();
    info.setTitle(file1.getName());
    ut.setDestinationDocumentInformation(info);
    ut.setDestinationStream(outputStream);
    ut.mergeDocuments();
    // close input stream file
    outputStream.close();
    // delete file
    CrmUtils.deleteFile(file1);

  }

  @PostMapping("/downloadreport")
  public void downloadReport(Long demandeId, HttpServletResponse response)
      throws JRException, IOException {
    demandeAbonnementService.downloadReport(demandeId, response);
  }

  @PostMapping("/downloadcontrat")
  public void downloadcontrat(Long demandeId, HttpServletResponse response)
      throws JRException, IOException {
    demandeAbonnementService.downloadContrat(demandeId, response);
  }

  @PostMapping("/upload/pdf")
  public String uploadFile(@RequestParam(value = "file", required = false) MultipartFile file,
      Long demandeId, String cin,
      @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
      @RequestParam(value = "imageFile2", required = false) MultipartFile imageFile2,
      RedirectAttributes redirectAttrs, HttpServletResponse response)
      throws JRException, IOException {
    return demandeAbonnementService.uploadFile(file, demandeId, cin, imageFile, imageFile2,
        redirectAttrs, response);
  }

  @PostMapping("/extractenmasse")
  public ModelAndView exportToExcel(HttpServletRequest request) {
    return demandeAbonnementService.exportToExcel(request);
  }

  @RequestMapping(method = RequestMethod.GET, value = "editabonnement/{demandeId}")
  public String updatedemandeabonnement(@PathVariable("demandeId") Long demandeId, Model model) {
    return demandeAbonnementService.getUpdateDemandeAbonnement(demandeId, model);
  }

  @RequestMapping(method = RequestMethod.POST, value = "editabonnement/{demandeId}")
  public String updatedemandeabonnement(@PathVariable("demandeId") Long demandeId,
      @RequestParam("gouvernorats") Gouvernorat gouvernorat,
      @RequestParam(value = "professions", required = false) Profession profession,
      @RequestParam(value = "houseHolder", required = false) Boolean houseHolder,
      @RequestParam(value = "hasBankCard", required = false) Boolean hasBankCard,
      @RequestParam("villes") Ville ville, @RequestParam("codepostale") PostalCode postalCode,
      @RequestParam("offres") Offre offres, @RequestParam("produits") Pack pack,
      @RequestParam("imageFile") MultipartFile imageFile,
      @RequestParam("imageFile2") MultipartFile imageFile2,
      @RequestParam("datedenaissancess") String datedenaissancess,
      @RequestParam("typepaiements") Typepaiement typepaiement, DemandeAbonnement demandeAbonnement,
      @RequestParam(value = "pdfcontrat", required = false) MultipartFile pdfcontrat,
      @RequestParam("situationFamiliale") String situationFamiliale,
      @RequestParam("residence") Boolean residence, Model model, RedirectAttributes redirectAttrs) {
    return demandeAbonnementService.updateDemandeAbonnement(demandeId, gouvernorat, postalCode,
        profession, ville, offres, imageFile, imageFile2, datedenaissancess, typepaiement,
        demandeAbonnement, pdfcontrat, situationFamiliale, model, redirectAttrs, pack, houseHolder,
        hasBankCard, residence);
  }

  @RequestMapping(method = RequestMethod.GET, value = "getdemandeabonnement/{demandeId}")
  public String getdemandeabonnement(@PathVariable("demandeId") Long demandeId, Model model) {
    return demandeAbonnementService.getDemandeAbonnement(demandeId, model);

  }

  @RequestMapping(method = RequestMethod.GET, value = "getdemandeabonnementtoimprimer/{demandeId}")
  public String getdemandeabonnementtoimprimer(@PathVariable("demandeId") Long demandeId,
      Model model) {
    return demandeAbonnementService.getdemandeAbonnementToImprimer(demandeId, model);
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallstatusabonnement")
  @ResponseBody
  public List<crm.chifco.com.model.Statut> getallstatusabonnement() {
    return demandeAbonnementService.getallStatusAbonnement();

  }

  @RequestMapping(method = RequestMethod.GET, value = "changerstatutabonnement/{demandeId}")
  @ResponseBody
  public crm.chifco.com.model.Statut getstatusabonnement(
      @PathVariable("demandeId") Long demandeId) {
    return demandeAbonnementService.getStatusAbonnement(demandeId);
  }

  @RequestMapping(method = RequestMethod.GET, value = "getmodems/{codeCatProduit}")
  @ResponseBody
  public List<Modem> getmodems(@PathVariable("codeCatProduit") String codeProduit,
      @RequestParam(value = "getAllType", required = false) Boolean getAllType) {
    return demandeAbonnementService.getModems(codeProduit, getAllType);
  }

  @RequestMapping(method = RequestMethod.POST, value = "uploadaddreferenceabonnementenmasse")
  public String uploadaddreferenceabonnementenmasse(
      @RequestParam("filecustomFile") MultipartFile file, RedirectAttributes redirectAttrs) {
    return demandeAbonnementService.uploadAddReferenceAbonnementEnMasse(file, redirectAttrs);
  }

  @RequestMapping(method = RequestMethod.POST, value = "uploadeditabonnementenmasse")
  public String uploadeditabonnementenmasse(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttrs, HttpServletResponse response)
      throws JRException, IOException {
    importExcel.uploadEditAbonnementEnMasse(file, redirectAttrs, response);
    return "redirect:/demandeabonnement/alldemandesabonnement/" + 1 + "/" + 20;
  }

  @RequestMapping(method = RequestMethod.POST, value = "editabonnement")
  public String GeteditAbonnement(@RequestParam("statutList") crm.chifco.com.model.Statut statut,
      @RequestParam(value = "modemList", required = false) String modemfromlist,
      @RequestParam("demandeAbonnementid") Long demandeAbonnementid,
      @RequestParam("fromlocation") String fromlocation, @RequestParam("file") MultipartFile file,
      Model model, RedirectAttributes redirectAttrs,
      @RequestParam(value = "telefixe", required = false) Long telefixe,
      @RequestParam(value = "telemobile5g", required = false) String mobile5G,
      @RequestParam(value = "referencett", required = false) String referencett,
      @RequestParam(value = "motifRefus", required = false) String motifRefus) {

    String resultchangmentstatut =
        demandeAbonnementService.editAbonnement(statut, modemfromlist, demandeAbonnementid,
            fromlocation, file, model, redirectAttrs, telefixe, referencett, motifRefus ,mobile5G);
    if (resultchangmentstatut.equals("MODEM_NOT_FOUNT")) {
      redirectAttrs.addFlashAttribute("MODEM_NOT_FOUNT", "Aucun modem n'a été trouvé !");
    }
    model.addAttribute("message", "changmentStaut");
    if (fromlocation.equals("fichedemande"))
      return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + demandeAbonnementid
          + "?changmentstatut=" + resultchangmentstatut;
    else if (fromlocation.equals("tableuxencours"))
      return "redirect:/demandeabonnement/alldemandesencours/" + "?changmentstatut="
          + resultchangmentstatut;
    else if (fromlocation.equals("tableuxtraiter"))
      return "redirect:/demandeabonnement/alldemandestraiter/" + "?changmentstatut="
          + resultchangmentstatut;
    else
      return "redirect:/demandeabonnement/alldemandesabonnement/" + 1 + "/" + 20
          + "?changmentstatut=" + resultchangmentstatut;

  }

  @GetMapping("verifcin")
  public String verifclient(Model model) {
    return demandeAbonnementService.getVerifClient(model);

  }

  @RequestMapping(method = RequestMethod.POST, value = "verifcin")
  public String verifclient(@RequestParam("cin") String cin, Model model) {
    return demandeAbonnementService.verifClient(cin, model);
  }

  @ResponseBody
  @RequestMapping(value = "/verificationcin", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String Verificationcin(@RequestBody MultiValueMap<String, String> formData) {
    return demandeAbonnementService.verificationCin(formData);
  }

  @ResponseBody
  @RequestMapping(value = "/verificationtelfix", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String Verificationtelfix(@RequestBody MultiValueMap<String, String> formData) {
    return demandeAbonnementService.verificationTelFix(formData);

  }

  @ResponseBody
  @RequestMapping(value = "/verificationtelfixEdite", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String verificationTelFixEdit(@RequestBody MultiValueMap<String, String> formData) {
    return demandeAbonnementService.verificationTelFixEdit(formData);

  }

  @RequestMapping(method = RequestMethod.POST, value = "confirmeAbonnement")
  public String confirmeAbonnement(@RequestParam("confirmation") String confirmation,
      @RequestParam("idabon") Long idabon) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      demandeAbonnementService.confirmeAbonnement(confirmation, idabon, user);
    }
    return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + idabon;
  }

  @RequestMapping(method = RequestMethod.POST, value = "confirmeAbonnementInjoignable")
  public String confirmeAbonnementInjoignable(@RequestParam("confirmation") String confirmation,
      @RequestParam("idabon") Long idabon) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      demandeAbonnementService.confirmeAbonnementInjoignable(confirmation, idabon, user);
    }
    return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + idabon;
  }

  @RequestMapping(method = RequestMethod.POST, value = "sendOtpSms",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Boolean sendOtpSms(@RequestBody HashMap<String, String> bodyOtpSms) {
    if (!bodyOtpSms.containsKey("demandeId")) {
      logger.info("sendOtpSms: not existe");
      return false;
    }
    DemandeAbonnement demandeAbonnement = demandeAbonnementService
        .getDemandeAbonnementBydemandeId(Long.parseLong(bodyOtpSms.get("demandeId")));
    String repenseNotification = notification.sendotp(demandeAbonnement.getTelMobile().toString(),

        "Votre code de sécurité est ");


    logger.info("send otp message repense Notification: " + repenseNotification);
    if (!repenseNotification.isEmpty()) {
      JSONObject jsonResponse = new JSONObject(repenseNotification.toString());
      otpService.saveNewOtpMessage(jsonResponse.getString("PhoneNumber"),
          jsonResponse.getString("otpCode"), jsonResponse.getNumber("expiryDate"));
      demandeAbonnement.setIsSmsSend(true);
      demandeAbonnementRepository.save(demandeAbonnement);

      return true;
    }

    return false;
  }

  @RequestMapping(method = RequestMethod.POST, value = "sendOtpVificationSms",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Boolean sendOtpVificationSms(@RequestBody HashMap<String, String> bodyOtpSms) {
    if (!bodyOtpSms.containsKey("number") || !bodyOtpSms.containsKey("optCode")) {

      return false;
    }
    OtpSms findLastOtpSms = otpService.findLastOtpSms(bodyOtpSms.get("number"));
    logger.info("findLastOtpSms: " + findLastOtpSms);
    if (findLastOtpSms.getCodeOtp().equals(bodyOtpSms.get("optCode"))) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        String resultSaved = demandeAbonnementService
            .smsVerification(Long.parseLong(bodyOtpSms.get("demandeId")), user);
        logger.info("demande Id : " + bodyOtpSms.get("demandeId") + "resulta verification : "
            + resultSaved);
      }

      return true;
    }
    return false;

  }

  @PreAuthorize("hasAuthority('DUPLICATED_SUBSCRIPTION_REQUEST')")
  @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "duplicatedDemande")
  public String duplicatedDemande(@RequestParam("demandeId") Long demandeId,
      @RequestParam("cin") String cin, Model model, RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    DemandeAbonnement dublicatedDemande = null;
    User user = null;
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      dublicatedDemande = demandeAbonnementService.duplicatedDemande(demandeId, cin, user);
    }

    if (dublicatedDemande == null) {
      redirectAttrs.addFlashAttribute("message", "demade deja dupliquer");
      return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + demandeId;
    }

    else {

      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      List<Offre> offrePackInternets = null;
      if (StringsRole.contains("ADD_SUBSCRIPTION_REQUEST_ALL")) {
        offrePackInternets = offreService.findAllOffreExisteInPack();
      }

      else {
        offrePackInternets = offreService.findAllOffreExisteInPackByVisibility();
      }
      List<Gouvernorat> gouvernorats = gouvernoratRepository.findAll();
      List<Profession> professions = professionRepository.findByisActive(true);
      List<Typepaiement> typepaiements = typePaiementService.getalltypepaiements();
      List<Ville> villes = villesService
          .findAllByIdGrouvernerat(dublicatedDemande.getGouvernorat().getGouvernoratId());
      List<Pack> packs =
          packService.getPackSByOffre_offreId(dublicatedDemande.getPack().getOffre().getOffreId());
      List<PostalCode> codePostaleList = codePostaleRepository
          .findPostalCodeByVille_VilleId(dublicatedDemande.getVille().getVilleId());
      List<Object[]> produitsAndTarifications = new ArrayList<>();
      if (dublicatedDemande.getEntriesDemandeAbonnement().size() > 0)

        for (EntryDemandeAbonnement entryDemandeAbonnement : dublicatedDemande
            .getEntriesDemandeAbonnement()) {
          produitsAndTarifications
              .add(new Object[] {entryDemandeAbonnement.getProduit(), tarificationRepository
                  .findByProduitId(entryDemandeAbonnement.getProduit().getProduitId())});

        }
      model.addAttribute("produitsAndTarifications", produitsAndTarifications);
      model.addAttribute("villes", villes);
      model.addAttribute("gouvernorats", gouvernorats);
      model.addAttribute("professions", professions);
      model.addAttribute("demande", dublicatedDemande);
      model.addAttribute("typepaiements", typepaiements);
      model.addAttribute("codePostaleList", codePostaleList);
      model.addAttribute("offres", offrePackInternets);
      model.addAttribute("packs", packs);
      return "demandeabonnement/duplicationDemande";
    }
  }

  @RequestMapping(method = RequestMethod.POST, value = "duplicatedDemandesave")
  public String saveDuplicationDemandeAbonnement(@RequestParam("demandeId") Long demandeId,
      DemandeAbonnement demandeAbonnement, @RequestParam("imageFile") MultipartFile cinRecto,
      @RequestParam("imageFile2") MultipartFile cinVerso,
      @RequestParam("datedenaissance") String datedenaissancess,
      @RequestParam("typepaiements") Typepaiement typepaiement,
      @RequestParam("gouvernorats") Gouvernorat gouvernorat,
      @RequestParam(value = "professions", required = false) Profession profession,
      @RequestParam("villes") Ville ville, @RequestParam("codepostale") PostalCode postalCode,
      @RequestParam("residence") Boolean residence,
      @RequestParam(value = "houseHolder", required = false) Boolean houseHolder,
      @RequestParam(value = "hasBankCard", required = false) Boolean hasBankCard,
      @RequestParam(value = "telFixe", required = false) Long telFixe,
      @RequestParam("raccordementtranche") int raccordementtranche,
      @RequestParam(value = "multipleSubproduct[]", required = false) Long[] multipleSubproduct,
      @RequestParam("offres") Long offres, @RequestParam("packes") Long packes,
      RedirectAttributes redirectAttrs, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    DemandeAbonnement dublicatedDemande = null;
    Map<String, Object> response = new HashMap<>();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      // String cin = demandeAbonnementRepository.getCinById(demandeId);

      response = demandeAbonnementService.saveDuplicatedDemande(cinRecto, cinVerso,
          datedenaissancess, demandeAbonnement, user, typepaiement, gouvernorat, profession, ville,
          postalCode, residence, houseHolder, hasBankCard, telFixe, raccordementtranche, packes,
          multipleSubproduct);
      if (response.containsKey("erreurCin1")) {
        redirectAttrs.addFlashAttribute("message", "erreurCin1");
        return "redirect:duplicatedDemande?demandeId=" + demandeId;

      }
      if (response.containsKey("erreurCin2")) {
        redirectAttrs.addFlashAttribute("message", "erreurCin2");
        return "redirect:duplicatedDemande?demandeId=" + demandeId;
      }
      redirectAttrs.addFlashAttribute("message", "demade dupliquer avec succee");

    }
    dublicatedDemande = (DemandeAbonnement) response.get("success");
    return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/"
        + dublicatedDemande.getDemandeId();
  }

  @RequestMapping(method = RequestMethod.GET, value = "duplicationsDemande")
  public String duplicatedDemande(Model model) {
    userService.returnInfoUserConnected(model);
    return "demandeabonnement/duplicationDemande";

  }

  @RequestMapping(method = RequestMethod.POST, value = "addComment")
  public String addComment(@RequestParam("demandeId") Long demandeId,
      @RequestParam("cometaire") String Comment, RedirectAttributes redirectAttrs) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        abonnementHistoriqueService.saveNewHistorique(user, demandeId, Comment);
        redirectAttrs.addFlashAttribute("message", "comentaireAjouter");
      }

    } catch (Exception e) {
      logger.error(" demandeabonnement.addComment Error:", e.getMessage());
      redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    }

    return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + demandeId;

  }

  @RequestMapping(method = RequestMethod.POST, value = "classificationDemande")
  public String classificationDemande(@RequestParam("idabon") Long demandeId,
      String statusClasssification) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      ClassificationDemande classification = classificationDemandeRepository
          .findClassificationDemandeByCodeClassification(statusClasssification);
      DemandeAbonnement demandeabn =
          demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
      if (classification != null && demandeabn != null) {
        String message = (demandeabn.getDecisionDemande() == null)
            ? "La classification a été changée à '" + classification.getValue() + "'"
            : "La classification a été changée de '" + demandeabn.getDecisionDemande().getValue()
                + "' à '" + classification.getValue() + "'";

        abonnementHistoriqueService.saveNewHistorique(user, demandeId, message);

        demandeabn.setDateDecisionDemande(new Date());
        demandeabn.setDecisionDemande(classification);
        demandeAbonnementRepository.save(demandeabn);

      }
    }
    return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + demandeId;
  }

  @RequestMapping(method = RequestMethod.POST, value = "updateClassement",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Boolean updateClassement(@RequestBody HashMap<String, String> updateClassement) {
    try {
      ClassificationDemande classification = classificationDemandeRepository
          .findClassificationDemandeByCodeClassification(updateClassement.get("classementCode"));
      DemandeAbonnement demandeabn = demandeAbonnementRepository
          .findDemandeAbonnementByDemandeId(Long.parseLong(updateClassement.get("demandeId")));
      if (classification != null && demandeabn != null) {
        demandeabn.setDateDecisionDemande(new Date());

        // historique de classification
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        String message =
            "La classification a été changée de '" + demandeabn.getDecisionDemande().getValue()
                + "' à '" + classification.getValue() + "'";
        abonnementHistoriqueService.saveNewHistorique(user, demandeabn.getDemandeId(), message);

        demandeabn.setDecisionDemande(classification);
        demandeAbonnementRepository.save(demandeabn);
        return true;
      }
      return false;
    } catch (Exception e) {
      logger.error(" demandeabonnement.updateClassement Error:" + e);
      return false;
    }
  }

  @RequestMapping(method = RequestMethod.POST, value = "affectationClient")
  public String affectationClient(@RequestParam("demandeId") Long demandeId,
      @RequestParam(value = "codeRevendeur", required = false) String codeRevendeur,
      @RequestParam(value = "emailRevendeur", required = false) String emailRevendeur,
      @RequestParam(value = "identificationFiscale", required = false) String identificationFiscale,
      RedirectAttributes redirectAttrs) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        Boolean isAffectedUser = demandeAbonnementService.affectRevendeur(demandeId, codeRevendeur,
            emailRevendeur, identificationFiscale);
        if (isAffectedUser) {
          abonnementHistoriqueService.saveNewHistorique(user, demandeId, "Affectation Revendeur");
          redirectAttrs.addFlashAttribute("message", "abonnementAffected");
        } else {
          redirectAttrs.addFlashAttribute("message", "abonnementNonAffected");
        }

      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.affectationClientt Error:" + e);

    }
    return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + demandeId;

  }

  @PreAuthorize("hasAuthority('AFFECT_ABONNEMENT_TO_REVENDEUR_BY_DISTRIBITEUR')"
      + "|| hasAuthority('AFFECT_ABONNEMENT_TO_REVENDEUR')")
  @RequestMapping(method = RequestMethod.POST, value = "affectationClient",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public List<User> affectationClients(@RequestBody HashMap<String, String> affectationClient,
      RedirectAttributes redirectAttrs) {
    List<User> userListe = null;
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        userListe = userService
            .affectRevendeurgetListeRevendeur(affectationClient.get("recherche").trim(), user);

      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.affectationClientt Error:" + e);

    }
    return userListe;

  }

  @RequestMapping(method = RequestMethod.POST, value = "sendAffectationClient",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Boolean submitAffectationClients(@RequestBody HashMap<String, String> affectationClient,
      RedirectAttributes redirectAttrs) {
    Boolean isAffectedUser = false;
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        isAffectedUser =
            demandeAbonnementService.affectOneRevendeur(affectationClient.get("userselected"),
                Long.parseLong(affectationClient.get("demandeId")));
        if (isAffectedUser) {
          User userToAffected =
              userRepository.getById(Long.parseLong(affectationClient.get("userselected")));
          abonnementHistoriqueService.saveNewHistorique(user,
              Long.parseLong(affectationClient.get("demandeId")),
              "L'abonnement  a été réaffectée avec succès au revendeur "
                  + userToAffected.getFirstName() + " " + userToAffected.getLastName() + '('
                  + userToAffected.getCodeUser() + ")");
        }

      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.affectationClientt Error:" + e);

    }
    return isAffectedUser;

  }

  @ResponseBody
  @RequestMapping(value = "/verificationcinEdit", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String verificationcinEdit(@RequestBody MultiValueMap<String, String> formData) {
    return demandeAbonnementService.verificationCinFixEdit(formData);

  }

  @RequestMapping(method = RequestMethod.POST, value = "classificationDemandeMultiple")
  public String classificationDemandeMultiple(
      @RequestParam("idabonforClassification") String listeDemandeId, String statusClasssification,
      HttpServletRequest request, RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      ClassificationDemande classification = classificationDemandeRepository
          .findClassificationDemandeByCodeClassification(statusClasssification);

      List<String> listeDemandeAbonnementId =
          new ArrayList<String>(Arrays.asList(listeDemandeId.split(",")));

      if (listeDemandeAbonnementId.size() >= 501) {
        redirectAttrs.addFlashAttribute("message", "classificationMaxSizeEreur");
        request.getSession().setAttribute("listedes_ids", "");
        return "redirect:/demandeabonnement/alldemandesabonnement/" + 1 + "/" + 20;
      }
      List<DemandeAbonnement> demandeabn = demandeAbonnementRepository
          .findDemandeAbonnementByListeDemandeId(listeDemandeAbonnementId);
      if (classification != null && demandeabn != null) {

        demandeabn.forEach(el -> {
          String message = (el.getDecisionDemande() == null)
              ? "La classification a été changée à '" + classification.getValue() + "'"
              : "La classification a été changée de '" + el.getDecisionDemande().getValue()
                  + "' à '" + classification.getValue() + "'";

          abonnementHistoriqueService.saveHistoryToDataBase(user, el, message);

          el.setDateDecisionDemande(new Date());
          el.setDecisionDemande(classification);
          demandeAbonnementRepository.save(el);

        });

        request.getSession().setAttribute("listedes_ids", "");
        redirectAttrs.addFlashAttribute("message", "classificationAvecSuccess");
      }
    }
    return "redirect:/demandeabonnement/alldemandesabonnement/" + 1 + "/" + 20;
  }

  @RequestMapping(method = RequestMethod.POST, value = "assignerDemandeMultiple")
  public String assignerDemandeMultiple(@RequestParam("idabon") String listeDemandeId, Long agentId,
      HttpServletRequest request, RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      User Agent = userRepository.findByUserId(agentId);

      List<String> listeDemandeAbonnementId =
          new ArrayList<String>(Arrays.asList(listeDemandeId.split(",")));

      if (listeDemandeAbonnementId.size() >= 501) {
        redirectAttrs.addFlashAttribute("message", "assignementMaxSizeEreur");
        request.getSession().setAttribute("listedes_ids", "");
        return "redirect:/demandeabonnement/alldemandesabonnement/" + 1 + "/" + 20;
      }
      List<DemandeAbonnement> demandeabn = demandeAbonnementRepository
          .findDemandeAbonnementByListeDemandeId(listeDemandeAbonnementId);
      if (Agent != null && demandeabn != null) {

        demandeabn.forEach(el -> {
          String message = (el.getTreatedBy() == null)
              ? "La demande a été signée à l'agent: '" + Agent.getFirstName() + ' '
                  + Agent.getLastName() + "'"
              : "L'assignement de la demande a été changée à '" + Agent.getFirstName() + ' '
                  + Agent.getLastName();

          abonnementHistoriqueService.saveHistoryToDataBase(user, el, message);

          el.setTreatedBy(Agent);
          demandeAbonnementRepository.save(el);

        });

        request.getSession().setAttribute("listedes_ids", "");
        redirectAttrs.addFlashAttribute("message", "assignementAvecSuccess");
      }
    }
    return "redirect:/demandeabonnement/alldemandesabonnement/" + 1 + "/" + 20;
  }

  @RequestMapping(method = RequestMethod.POST, value = "creeClient")
  public String creeClientIfNotExiste(@RequestParam("idabon") Long idDemandeId,
      String statusClasssification, HttpServletRequest request, RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      Abonnement abonnement = abonnementRepository.findAbonnementByDemandeAbonnement(idDemandeId);
      DemandeAbonnement demandeAbonnement =
          demandeAbonnementService.getDemandeAbonnementBydemandeId(idDemandeId);
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      if (abonnement == null) {
        abonnement = abonnementService.saveNewAbonnement(demandeAbonnement);
        clientHistoryService.insertNewHistoryclient(demandeAbonnement, "Abonnement creé", user);

      }



      Commande findCommandeby = commandeRepository.findFirstByabonnement(abonnement);

      if (findCommandeby == null) {

        int typdedepaymentmonth = demandeAbonnement.getTypePaiement().getNombreMoisTypePaiement();

        Date nouvauDateFin = CrmUtils.calculeDateFin(typdedepaymentmonth, null);

        List<EntryCommande> EntryCommande =
            factureService.setEntriesCommande(abonnement, abonnement.getPack());

        findCommandeby = factureService.setCommande(abonnement, nouvauDateFin.toString(), null,
            user, EntryCommande);

        Facture premiereFacture =
            factureService.generateFacture(findCommandeby, user, true, null, null);

        factureService.setEntryTvaFacture(premiereFacture);

      }
    }
    return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + idDemandeId;

  }


}
