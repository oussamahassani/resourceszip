package crm.chifco.com.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.MultiValueMap;
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

import aj.org.objectweb.asm.Type;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.CategorieProduitInternet;
import crm.chifco.com.model.Commande;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.EntryCommande;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.Profession;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.model.Smstemplate;
import crm.chifco.com.model.Typepaiement;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Ville;
import crm.chifco.com.radius.model.Radacct;
import crm.chifco.com.radius.model.Radcheck;
import crm.chifco.com.radius.service.RadcheckService;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.CodePostaleRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.RoleRepository;
import crm.chifco.com.repository.SmstemplateRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.repository.VilleRepository;
import crm.chifco.com.service.AbonnementHistoriqueService;
import crm.chifco.com.service.AbonnementService;
import crm.chifco.com.service.AvoirService;
import crm.chifco.com.service.ClientHistoryService;
import crm.chifco.com.service.FactureService;
import crm.chifco.com.service.ModemService;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.ProfessionService;
import crm.chifco.com.service.ReclamationHistoryService;
import crm.chifco.com.service.ReclamationService;
import crm.chifco.com.service.TypePaiementService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.templateclasse.AllClientHistory;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.PrefixDocument;
import crm.chifco.com.utils.RedchekConstant;
import crm.chifco.com.utils.TypeAbonnment;
import crm.chifco.com.utils.UserTypeConstant;

@Controller
@RequestMapping(value = "client/*")
public class AbonnementController {
  private final Logger logger = LogManager.getLogger(this.getClass());
  @Autowired
  UserRepository userRepository;
  @Autowired
  AbonnementRepository abonnementRepository;
  @Autowired
  AbonnementService abonnementService;
  @Autowired
  GouvernoratRepository gouvernoratRepository;
  @Autowired
  VilleRepository villeRepository;
  @Autowired
  RoleRepository roleRepository;

  @Autowired
  private FactureService factureService;

  @Autowired
  ClientHistoryService clientHistoryService;

  @Autowired
  Notification notificationservice;

  @Autowired
  SmstemplateRepository templatesmsRepository;

  @Autowired
  private AbonnementHistoriqueService abonnementHistoriqueService;

  @Autowired
  UserService UserService;

  @Autowired
  private TypePaiementService typePaiementService;

  @Autowired
  DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  CodePostaleRepository codePostaleRepository;

  @Autowired
  private CategorieProduitInternetRepository categorieProduitInternetRepository;

  @Autowired
  private RadcheckService radcheckService;

  @Autowired
  private AvoirService avoirService;

  @Autowired
  AvoirRepository avoirRepository;

  @Autowired
  private ProfessionService professionService;

  @Autowired
  private ModemService modemService;

  @Value("${pathDemandesAbonnement}")
  private String pathDemandesAbonnement;

  @Value("${facture.resilation.prix.depasse.6}")
  private String factureResilationPrixDepass6;

  @Value("${facture.resilation.prix.non.depasse.6}")
  private String facturResilationPrixNondepasse6;

  @Value("${facture.resilation.date.fin}")

  private String factureResilationDateFin;
  @Value("${facture.resilation.modem}")
  private String factureResilationPrixModem;


  @Autowired
  private ReclamationService reclamationService;
  @Autowired
  private ReclamationHistoryService reclamationHistoryService;

  @Value("${pathRecuResilation}")
  private String pathRecuResilation;


  @RequestMapping(method = RequestMethod.GET, value = "getallclient")
  @ResponseBody
  public HashMap<String, Object> getallclient(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return abonnementService.getAllClient(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche, false);
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallclientactive")
  @ResponseBody
  public HashMap<String, Object> getallclientactive(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return abonnementService.getAllClient(draw, start, length, search, ordercolumnaram, orderdir,

        filterrecherche, true);
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallclientEnRecouvrement")
  @ResponseBody
  public HashMap<String, Object> getallclientEnRecouvrement(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    String result =
        CrmUtils.addKeyValuePair(filterrecherche, "statutChifcoListfiltre", "RECOUVREMENT");

    return abonnementService.getAllClient(draw, start, length, search, ordercolumnaram, orderdir,
        result, false);
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallclientResilier")
  @ResponseBody
  public HashMap<String, Object> getallclientResilier(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    String result =
        CrmUtils.addKeyValuePair(filterrecherche, "statutChifcoListfiltre", "RESILIATION");

    return abonnementService.getAllClient(draw, start, length, search, ordercolumnaram, orderdir,
        result, false);
  }

  @GetMapping(value = "allclients/{pageNo}")
  public String clients(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE_SUBSCRIPTION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("DEACTIVATE_SUBSCRIPTION",
          StringsRole.contains("DEACTIVATE_SUBSCRIPTION"));

      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
      model.addAttribute("gouvernorats", listgouvernorats);

      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("categories", categorieProduitInternets);
      request.getSession().setAttribute("listedes_ids", "");
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      model.addAttribute("AffectedTo", User);
    }

    return "client/allclients";
  }



  @GetMapping(value = "allclientactive/{pageNo}")
  public String allclientactive(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE_SUBSCRIPTION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("DEACTIVATE_SUBSCRIPTION",
          StringsRole.contains("DEACTIVATE_SUBSCRIPTION"));

      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
      model.addAttribute("gouvernorats", listgouvernorats);

      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("categories", categorieProduitInternets);
      request.getSession().setAttribute("listedes_ids", "");
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      model.addAttribute("AffectedTo", User);
    }

    return "client/allclientsActive";
  }



  @GetMapping(value = "allclientsEnRecouvrement/{pageNo}")
  public String allclientsEnRecouvrement(@PathVariable(value = "pageNo") Integer pageNo,
      Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE_SUBSCRIPTION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("DEACTIVATE_SUBSCRIPTION",
          StringsRole.contains("DEACTIVATE_SUBSCRIPTION"));

      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
      model.addAttribute("gouvernorats", listgouvernorats);

      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("categories", categorieProduitInternets);
      request.getSession().setAttribute("listedes_ids", "");
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      model.addAttribute("AffectedTo", User);
    }


    return "client/allclientsEnRecouvrement";
  }


  @GetMapping(value = "allclientsResilier/{pageNo}")
  public String allclientsResilier(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE_SUBSCRIPTION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("DEACTIVATE_SUBSCRIPTION",
          StringsRole.contains("DEACTIVATE_SUBSCRIPTION"));

      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
      model.addAttribute("gouvernorats", listgouvernorats);

      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("categories", categorieProduitInternets);
      request.getSession().setAttribute("listedes_ids", "");
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      model.addAttribute("AffectedTo", User);
    }

    return "client/allclientsResilier";



  }

  @GetMapping(value = "allClientNonConnecterView/{pageNo}")
  public String allClientNonConnecterView(@PathVariable(value = "pageNo") Integer pageNo,
      Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE_SUBSCRIPTION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("DEACTIVATE_SUBSCRIPTION",
          StringsRole.contains("DEACTIVATE_SUBSCRIPTION"));

      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
      model.addAttribute("gouvernorats", listgouvernorats);

      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("categories", categorieProduitInternets);
      request.getSession().setAttribute("listedes_ids", "");
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      model.addAttribute("AffectedTo", User);
    }

    return "client/allClientsNonConnecter";
  }

  @GetMapping(value = "allClientsNonConnecterMiseEnService/{pageNo}")
  public String allClientsNonConnecterMiseEnService(@PathVariable(value = "pageNo") Integer pageNo,
      Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE_SUBSCRIPTION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("DEACTIVATE_SUBSCRIPTION",
          StringsRole.contains("DEACTIVATE_SUBSCRIPTION"));

      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
      model.addAttribute("gouvernorats", listgouvernorats);

      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("categories", categorieProduitInternets);
      request.getSession().setAttribute("listedes_ids", "");
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      model.addAttribute("AffectedTo", User);
    }

    return "client/allClientsNonConnecterMiseEnService";
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallClientNonConnecter")
  @ResponseBody
  public HashMap<String, Object> getallClientNonConnecter(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return abonnementService.getallClientNonConnecter(draw, start, length, search, ordercolumnaram,
        orderdir, filterrecherche);


  }

  @RequestMapping(method = RequestMethod.GET, value = "getallClientNonConnecterMiseService")
  @ResponseBody
  public HashMap<String, Object> getallClientNonConnecterMiseService(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return abonnementService.getallClientNonConnecterMiseService(draw, start, length, search,
        ordercolumnaram, orderdir, filterrecherche);


  }


  @RequestMapping(method = RequestMethod.POST, value = "add-client")
  public String addclient(@RequestParam("Nom") String nom, @RequestParam("Prenom") String prenom,
      @RequestParam("Email") String email, @RequestParam("CIN") String cin,
      @RequestParam("villes") Gouvernorat ville, @RequestParam("gouvernorat") Long gouvernoratid,
      @RequestParam("adresse") String adresse, @RequestParam("codepostale") PostalCode codepostale,
      @RequestParam("telMobile") Long telMobile,
      @RequestParam(value = "telFixe", required = false) Long telFixe,
      @RequestParam("activation") Boolean activation, Model model) {
    Abonnement testclient = abonnementRepository.findClientByEmail(email);
    Abonnement testclient2 = abonnementRepository.findAbonnementByCin(cin);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      if (testclient != null) {
        List<Gouvernorat> villes = gouvernoratRepository.findAll();
        model.addAttribute("villes", villes);
        model.addAttribute("existedCode", email);
        return "client/addclient";
      } else if (testclient2 != null) {
        List<Gouvernorat> villes = gouvernoratRepository.findAll();
        model.addAttribute("villes", villes);
        model.addAttribute("existedCode2", cin);
        return "client/addclient";
      } else {
        saveClient(nom, prenom, email, cin, ville, gouvernoratid, adresse, codepostale, telMobile,
            telFixe, activation, user);
      }
    }
    return "redirect:/client/allclients/" + 1;
  }

  private void saveClient(String nom, String prenom, String email, String CIN,
      Gouvernorat gouvernorat, Long villeid, String adresse, PostalCode codepostale, Long telMobile,
      Long telFixe, Boolean activation, User user) {
    Ville ville = villeRepository.findVilleByVilleId(villeid);
    Abonnement abonnement = new Abonnement();
    abonnement.setLastName(prenom);
    abonnement.setFirstName(nom);
    abonnement.setEmail(email);
    abonnement.setCin(CIN);
    abonnement.setVille(ville);
    abonnement.setGouvernorat(gouvernorat);
    abonnement.setAdresse(adresse);
    abonnement.setCodePostale(codepostale);
    abonnement.setTelMobile(telMobile);
    if (telFixe != null)
      abonnement.setTelFixe(telFixe);
    abonnement.setEnabled(activation);
    // abonnement.setRole(roleRepository.findRoleByRoleName("ROLE_CLIENT"));
    abonnement.setUser(user);
    abonnementRepository.save(abonnement);
  }

  @RequestMapping(method = RequestMethod.GET, value = "getgouvernorats/{villeid}")
  @ResponseBody
  public List<Ville> getGouvernoratsByVille(@PathVariable("villeid") Long villeid) {
    return villeRepository.findGouvernoratsByGouvernerat_GouvernoratId(villeid);
  }

  @RequestMapping(value = "/enabledisable/{clientid}", method = RequestMethod.POST)
  public String enabledisable(@PathVariable Long clientid) {
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
    abonnement.setEnabled(!abonnement.isEnabled());
    abonnementRepository.save(abonnement);
    return "redirect:/client/allclients/" + 1;
  }

  @RequestMapping(method = RequestMethod.GET, value = "editclient/{clientid}")
  public String updateClient(@PathVariable("clientid") Long clientid, Model model) {
    UserService.returnInfoUserConnected(model);
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
    List<Gouvernorat> gouvernorats = gouvernoratRepository.findAll();
    List<Ville> villes = villeRepository.findGouvernoratsByGouvernerat_GouvernoratId(
        abonnement.getGouvernorat().getGouvernoratId());
    List<PostalCode> codePostaleList =
        codePostaleRepository.findPostalCodeByVille_VilleId(abonnement.getVille().getVilleId());
    List<Typepaiement> typepaiements = typePaiementService.getalltypepaiements();
    model.addAttribute("client", abonnement);
    model.addAttribute("villes", villes);
    model.addAttribute("codePostaleList", codePostaleList);
    model.addAttribute("typepaiements", typepaiements);
    model.addAttribute("gouvernorats", gouvernorats);
    model.addAttribute("datedenaissancess", abonnement.getDateNaissance());
    List<Profession> professions = professionService.findlistProfession();
    model.addAttribute("professions", professions);

    Boolean proprietaire = demandeAbonnementRepository.findProprietaireByClientId(clientid);
    model.addAttribute("isProprietaire", proprietaire);

    String contratPdf = demandeAbonnementRepository
        .findContratPdfByReferenceChifco(abonnement.getReferenceClient());
    model.addAttribute("contartPDF", contratPdf);
    return "client/editclient";
  }

  @RequestMapping(method = RequestMethod.POST, value = "editclient/{clientid}")
  public String updateClient(@PathVariable("clientid") Long clientid,
      @RequestParam("villes") Ville ville, @RequestParam("gouvernorats") Gouvernorat gouvernorats,
      @RequestParam(value = "houseHolder", required = false) Boolean houseHolder,
      @RequestParam(value = "hasBankCard", required = false) Boolean hasBankCard,
      @RequestParam("codepostale") PostalCode postalCode,
      @RequestParam("imageFile") MultipartFile imageFile,
      @RequestParam("imageFile2") MultipartFile imageFile2,
      @RequestParam(value = "pdfcontrat", required = false) MultipartFile pdfcontrat,
      @RequestParam("typepaiements") Typepaiement typepaiement,
      @RequestParam("professions") Long professionsId, @RequestParam("residence") Boolean residence,
      @RequestParam("situationFamiliale") String situationFamiliale,
      @RequestParam("datedenaissancess") String datedenaissancess, Abonnement abonnement,
      Model model, RedirectAttributes redirectAttrs) {
    logger.info("edit abonnement Id :" + clientid);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      Abonnement clienttoedit = abonnementRepository.findAbonnementByClientid(clientid);
      DemandeAbonnement demandeAbonnement = demandeAbonnementRepository
          .findDemandeAbonnementByReferenceChifco(clienttoedit.getReferenceClient());

      Profession profession =
          (professionsId != null) ? professionService.findById(professionsId) : null;


      if (!clienttoedit.getCin().equals(abonnement.getCin())) {
        logger.info("edit cin  abonnement :" + abonnement.getCin());

        try {
          Path oldFolderPath = Paths.get(pathDemandesAbonnement + clienttoedit.getCin());
          Path newFolderPath = Paths.get(pathDemandesAbonnement + abonnement.getCin());
          clientHistoryService.updateCinHistory(abonnement.getCin(), clienttoedit.getCin());
          if (demandeAbonnement != null) {
            if (!demandeAbonnement.getCin().equals(abonnement.getCin())) {
              abonnementHistoriqueService.updateCinHistory(demandeAbonnement.getCin(),
                  abonnement.getCin());
            }
          }
          Files.move(oldFolderPath, newFolderPath);
          logger.info("Folder renamed successfully.");

        } catch (IOException e) {
          logger.info("Failed to rename folder: " + e.getMessage());
        }
      }
      clienttoedit.setLastName(abonnement.getLastName());
      clienttoedit.setFirstName(abonnement.getFirstName());
      clienttoedit.setTelMobile2(abonnement.getTelMobile2());
      if (abonnement.getEmail() != null) {
        clienttoedit.setEmail(abonnement.getEmail());
      }

      if (typepaiement != null) {
        clienttoedit.setTypePaiement(typepaiement);

      }
      if (!clienttoedit.getTelFixe().equals(abonnement.getTelFixe())) {
        clienttoedit.setTelFixe(abonnement.getTelFixe());
      }

      if (!clienttoedit.getCin().equals(abonnement.getCin())) {
        clienttoedit.setCin(abonnement.getCin());
      }
      clienttoedit.setVille(ville);
      clienttoedit.setGouvernorat(gouvernorats);
      clienttoedit.setCodePostale(postalCode);
      clienttoedit.setAdresse(abonnement.getAdresse());
      clienttoedit.setTelMobile(abonnement.getTelMobile());
      clienttoedit.setProfession(profession);
      clienttoedit.setSituationFamiliale(situationFamiliale);
      clienttoedit.setHouseHolder(houseHolder);
      clienttoedit.setHasBankCard(hasBankCard);
      // clienttoedit.setTelfixe(client.getTelfixe());

      if (!datedenaissancess.equals("")) {
        clienttoedit.setDateNaissance(CrmUtils.convertStringToDate(datedenaissancess));
      }
      String caractaireIndication = "";
      if (!imageFile.isEmpty()) {
        caractaireIndication = "recto";
        try {
          updateImage(imageFile, clienttoedit.getCin(), clienttoedit.getCin(),
              clienttoedit.getPhotoCin1(), caractaireIndication);
          clienttoedit.setPhotoCin1(
              caractaireIndication + CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
        } catch (Exception e) {

          logger.error(
              " DemandeAbonnementServiceimpl.Updatedemandeabonnement -->Edit Abonnement Uplod Image Recto Exception: "
                  + e.getMessage());

        }
      }
      if (!imageFile2.isEmpty()) {
        try {
          caractaireIndication = "verso";
          updateImage(imageFile2, clienttoedit.getCin(), clienttoedit.getCin(),
              clienttoedit.getPhotoCin2(), caractaireIndication);
          clienttoedit.setPhotoCin2(caractaireIndication
              + CrmUtils.noSpecialCharacters(imageFile2.getOriginalFilename()));
        } catch (Exception e) {

          logger.error(
              "DemandeAbonnementServiceimpl.Updatedemandeabonnement -->Edit Abonnement Uplod Image Verso Exception: "
                  + e.getMessage());
        }
      }
      if (pdfcontrat != null && !pdfcontrat.isEmpty()) {
        try {
          caractaireIndication = "contart";
          updateImage(pdfcontrat, demandeAbonnement.getCin(), demandeAbonnement.getCin(),
              demandeAbonnement.getContratPdf(), caractaireIndication);
          demandeAbonnement.setContratPdf(caractaireIndication
              + CrmUtils.noSpecialCharacters(pdfcontrat.getOriginalFilename()));
        } catch (Exception e) {

          logger.error(
              "DemandeAbonnementServiceimpl.Updatedemandeabonnement -->Edit Contrat Uplod Exception: "
                  + e.getMessage());
        }
      }

      if (demandeAbonnement != null) {
        String oldCinDemande = demandeAbonnement.getCin();
        if (houseHolder != null) {

          demandeAbonnement.setHouseHolder(houseHolder);
        }
        if (hasBankCard != null) {
          demandeAbonnement.setHasBankCard(hasBankCard);
        }
        demandeAbonnement.setFirstName(abonnement.getFirstName());
        demandeAbonnement.setLastName(abonnement.getLastName());
        demandeAbonnement.setEmail(abonnement.getEmail());
        demandeAbonnement.setGouvernorat(gouvernorats);
        demandeAbonnement.setVille(ville);
        demandeAbonnement.setAdresse(abonnement.getAdresse());
        demandeAbonnement.setCodePostale(postalCode);
        demandeAbonnement.setTelMobile(abonnement.getTelMobile());
        demandeAbonnement.setTypePaiement(typepaiement);

        demandeAbonnement.setProprietaire(residence);
        demandeAbonnement.setProfession(profession);
        demandeAbonnement.setSituationFamiliale(situationFamiliale);
        demandeAbonnement.setTelMobile2(abonnement.getTelMobile2());
        demandeAbonnement.setHouseHolder(houseHolder);
        demandeAbonnement.setHasBankCard(hasBankCard);
        demandeAbonnement.setDateDecisionDemande(clienttoedit.getDateNaissance());
        demandeAbonnement.setPhotoCin1(clienttoedit.getPhotoCin1());
        demandeAbonnement.setPhotoCin2(clienttoedit.getPhotoCin2());
        if (!demandeAbonnement.getCin().equals(abonnement.getCin())) {
          demandeAbonnement.setCin(abonnement.getCin());
        }
        if (!demandeAbonnement.getTelFixe().equals(abonnement.getTelFixe())) {
          demandeAbonnement.setTelFixe(abonnement.getTelFixe());
        }
        boolean modificationContratPdf = false;
        if (pdfcontrat != null && !pdfcontrat.isEmpty()) {
          modificationContratPdf = (demandeAbonnement.getContratPdf() != CrmUtils
              .noSpecialCharacters(pdfcontrat.getOriginalFilename()));

        }
        clientHistoryService.insertNewEditedAbonnementHistory(user, clienttoedit,
            modificationContratPdf);
        demandeAbonnementRepository.save(demandeAbonnement);
        List<DemandeAbonnement> verififExiteOtherDemandeXithCin =
            demandeAbonnementRepository.findDemandeAbonnementByCin(oldCinDemande);
        if (verififExiteOtherDemandeXithCin.size() > 0) {
          verififExiteOtherDemandeXithCin.forEach(el -> {
            el.setCin(demandeAbonnement.getCin());
            demandeAbonnementRepository.save(el);
          });
        }

      }
      abonnementRepository.save(clienttoedit);


      redirectAttrs.addFlashAttribute("message", "clientupdated");

    }
    return "redirect:/client/allclients/" + 1;

  }

  @RequestMapping(method = RequestMethod.GET, value = "viewclient/{clientid}")
  public String viewclient(@PathVariable("clientid") Long clientid, Model model) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      model.addAttribute("user", user);


      List<Facture> factures = factureService.getAllFacturesByClient(clientid);
      List<Radacct> sessionConnected = null;
      List<AvoirClient> avoirClient = avoirService.getAllAvoirByClient(clientid);
      List<Object> combinedList = new ArrayList<>();
      combinedList.addAll(factures);
      combinedList.addAll(avoirClient);

      Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
      List<AllClientHistory> clientHistory =
          clientHistoryService.findClientHistoryByCin(abonnement.getCin());
      if (abonnement.getAssignedTo() != null) {
        model.addAttribute("AssignedToUser", abonnement.getAssignedTo());
      }

      Double montantFactureNonPayee = CrmUtils
          .formatDoubleInput(factureService.getSumFactureNonPayee(abonnement.getClientid()));
      Double montantAvoir =
          avoirRepository.getSumallAvoirNonPayeeByClient(abonnement.getClientid());
      montantFactureNonPayee = montantFactureNonPayee - montantAvoir;
      String etatConnection = "indisponible";
      if (abonnement.getLoginModem() != null) {
        try {
          Radcheck infoRadus = radcheckService.getRadchecksByUsernameAndAttribute(
              abonnement.getLoginModem(), RedchekConstant.Expiration);
          model.addAttribute("infoRadus", infoRadus);
        } catch (Exception e) {
          logger.error("infoRadus date d'expiration : " + e);
          model.addAttribute("infoRaduserror", "erreur");
        }
        try {
          List<Radacct> chekIfconnected =
              radcheckService.getRadacctConnection(abonnement.getLoginModem());
          sessionConnected = radcheckService.findSessionByUsername(abonnement.getCreatedDate(),
              abonnement.getLoginModem());
          if (chekIfconnected != null && !chekIfconnected.isEmpty()) {
            etatConnection = "connecté";
          } else {
            etatConnection = "non connecté";
          }

        } catch (Exception e) {
          logger.error("infoRadus date d'expiration : " + e);

        }
      }

      if (StringsRole.contains("CHANGE_MODEM")) {
        List<Modem> listModem = modemService.getAllModemsAvailableByType(
            abonnement.getPack().getCategoriePack().getCategorieProduitInternetCode());
        model.addAttribute("listModem", listModem);
        
        List<Modem> listSimModem = modemService.getAllModemsAvailableByType(
        		TypeAbonnment.SIM);
            model.addAttribute("listSimModem", listSimModem);
      }
      
      List<Reclamation> reclamation =
          reclamationService.getReclamatiobByIDclient(abonnement.getClientid());
      model.addAttribute("ClientHistory", clientHistory);
      model.addAttribute("client", abonnement);
      model.addAttribute("sessionConnected", sessionConnected);
      model.addAttribute("etatConnection", etatConnection);
      model.addAttribute("factures", combinedList);
      model.addAttribute("reclamationHistory", reclamation);

      model.addAttribute("montantFactureNonPayee",
          CrmUtils.formatDoubleInputToString(montantFactureNonPayee));
      List<User> User = userRepository.findUsersByTypeUserNotIn(
          Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      model.addAttribute("AffectedTo", User);
    }
    return "client/viewclient";
  }

  @RequestMapping(method = RequestMethod.GET, value = "codepostale/{villeid}")
  @ResponseBody
  public List<PostalCode> getcodepostale(@PathVariable("villeid") Long villeid) {

    List<PostalCode> PostalCode = codePostaleRepository.findPostalCodeByVille_VilleId(villeid);

    return PostalCode;
  }

  public void updateImage(MultipartFile imageFile, String oldcin, String newcin, String oldimg,
      String caractaireIndication) throws Exception {

    String newfolder = pathDemandesAbonnement + newcin + "/";
    logger.info("cin abonnement update image  " + oldcin);

    File fileToDelete = new File(pathDemandesAbonnement + oldcin + "/" + oldimg);
    logger.info("update Image path to delete: " + fileToDelete);
    File uploadDir = new File(newfolder);
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }
    FileSystemUtils.deleteRecursively(fileToDelete);
    byte[] bytes = imageFile.getBytes();
    Path path = Paths.get(newfolder + caractaireIndication
        + CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));

    Files.write(path, bytes);
  }

  @RequestMapping(method = RequestMethod.POST, value = "addComment")
  public String addComment(@RequestParam("clientid") Long clientid,
      @RequestParam("cometaire") String Comment, RedirectAttributes redirectAttrs) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        clientHistoryService.saveNewHistorique(user, clientid, Comment);
        redirectAttrs.addFlashAttribute("message", "comentaireAjouter");
      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.addComment Error:" + e);
      redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    }
    return "redirect:/client/viewclient/" + clientid;

  }

  @RequestMapping(method = RequestMethod.POST, value = "sendSms")
  public String sendSms(@RequestParam("clientid") Long clientid, @RequestParam("sms") String sms,
      RedirectAttributes redirectAttrs) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        abonnementService.sendSmsToclient(user, clientid, sms);
        redirectAttrs.addFlashAttribute("message", "smsend");
      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.smsend Error:" + e);
      redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    }
    return "redirect:/client/viewclient/" + clientid;

  }


  @RequestMapping(method = RequestMethod.POST, value = "affectationClient")
  public String affectationClient(@RequestParam("clientid") Long clientid,
      @RequestParam(value = "codeRevendeur", required = false) String codeRevendeur,
      @RequestParam(value = "emailRevendeur", required = false) String emailRevendeur,
      @RequestParam(value = "identificationFiscale", required = false) String identificationFiscale,
      RedirectAttributes redirectAttrs) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        Boolean isAffectedUser = abonnementService.affectRevendeur(clientid, codeRevendeur,
            emailRevendeur, identificationFiscale);
        if (isAffectedUser) {
          clientHistoryService.saveNewHistorique(user, clientid, "Affectation Revendeur");
          redirectAttrs.addFlashAttribute("message", "abonnementAffected");
        } else {
          redirectAttrs.addFlashAttribute("message", "abonnementNonAffected");
        }

      }
    } catch (Exception e) {
      logger.error("demandeabonnement.affectationClientt Error:" + e);

    }
    return "redirect:/client/viewclient/" + clientid;

  }

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
        userListe = UserService
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
        isAffectedUser = abonnementService.affectOneRevendeur(affectationClient.get("userselected"),
            Long.parseLong(affectationClient.get("demandeId")));
        if (isAffectedUser) {
          User userToAffected =
              userRepository.getById(Long.parseLong(affectationClient.get("userselected")));
          clientHistoryService.saveNewHistorique(user,
              Long.parseLong(affectationClient.get("demandeId")),
              "L'abonnement  a été réaffectée avec succès au revendeur  "
                  + userToAffected.getFirstName() + " " + userToAffected.getLastName() + '('
                  + userToAffected.getCodeUser() + ")");
        }

      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.affectationClientt Error:" + e);

    }
    return isAffectedUser;

  }

  @GetMapping("/extractenmasse")
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(value = "ExportRecherchegouvernorats", required = false) Long gouvernorat,
      @RequestParam(value = "ExportRecherchevilles", required = false) Long villes,
      @RequestParam(value = "ExportRechercheCIN", required = false) String Cin,
      @RequestParam(value = "ExportRechercheNom", required = false) String Nom,
      @RequestParam(value = "ExportRecherchePrenom", required = false) String Prenom,
      @RequestParam(value = "ExportRechercheCodeClient", required = false) String codeClient,
      @RequestParam(value = "ExportRechercheStatus", required = false) Boolean status,
      @RequestParam(value = "ExportRechercheTel", required = false) Long Tel,
      @RequestParam(value = "ExportRechercheCategories", required = false) Long category,
      @RequestParam(value = "ExportRechercheProduit", required = false) Long produit,
      @RequestParam(value = "ExportRecherchedatedebut", required = false) String dateDebut,
      @RequestParam(value = "ExportRecherchedatefin", required = false) String dateFin,
      @RequestParam(value = "ExportRecherchedateDebutModification",
          required = false) String dateDebutModification,
      @RequestParam(value = "ExportRecherchedateFinModification",
          required = false) String dateFinModification,
      @RequestParam(value = "ExportRechercheloginModem", required = false) String loginModem,
      @RequestParam(value = "ExportRechercheAffecterTo", required = false) Long AffecterTo,
      @RequestParam(value = "ExportRechercheCreepar", required = false) Long Creepar,
      @RequestParam(value = "ExportRechercheStatutChifcoListfiltre",
          required = false) String statutChifcoListfiltre,
      @RequestParam(value = "ExportRechercheListeClientFilter",
          required = false) String listeClientFilter,
      @RequestParam(value = "ExportRecherchedatedebutAffectionModem",
          required = false) String ExportRecherchedatedebutAffectionModem,
      @RequestParam(value = "ExportRecherchetypeAbonnment",
      required = false) String ExportRecherchetypeAbonnment,
      @RequestParam(value = "ExportRecherchedatefinAffectionModem",
          required = false) String ExportRecherchedatefinAffectionModem) {
    return abonnementService.exportToExcel(request, response, gouvernorat, villes, Cin, Nom, Prenom,
        codeClient, status, Tel, category, produit, dateDebut, dateFin, dateDebutModification,
        dateFinModification, loginModem, AffecterTo, Creepar, statutChifcoListfiltre,
        listeClientFilter, ExportRecherchedatedebutAffectionModem,ExportRecherchetypeAbonnment,
        ExportRecherchedatefinAffectionModem);
  }


  @GetMapping("/getallClientNonConnecterExtractEnMasse")
  public ModelAndView getallClientNonConnecterExtractEnMasse(HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(value = "ExportRecherchegouvernorats", required = false) Long gouvernorat,
      @RequestParam(value = "ExportRecherchevilles", required = false) Long villes,
      @RequestParam(value = "ExportRechercheCIN", required = false) String Cin,
      @RequestParam(value = "ExportRechercheNom", required = false) String Nom,
      @RequestParam(value = "ExportRecherchePrenom", required = false) String Prenom,
      @RequestParam(value = "ExportRechercheCodeClient", required = false) String codeClient,
      @RequestParam(value = "ExportRechercheTel", required = false) Long Tel,
      @RequestParam(value = "ExportRechercheCategories", required = false) Long category,
      @RequestParam(value = "ExportRechercheProduit", required = false) Long produit,
      @RequestParam(value = "ExportRecherchedatedebut", required = false) String dateDebut,
      @RequestParam(value = "ExportRecherchedatefin", required = false) String dateFin,
      @RequestParam(value = "ExportRecherchedateDebutModification",
          required = false) String dateDebutModification,
      @RequestParam(value = "ExportRecherchedateFinModification",
          required = false) String dateFinModification,
      @RequestParam(value = "ExportRechercheloginModem", required = false) String loginModem,
      @RequestParam(value = "ExportRechercheAffecterTo", required = false) Long AffecterTo,
      @RequestParam(value = "ExportRechercheCreepar", required = false) Long Creepar,
      @RequestParam(value = "ExportRecherchedatedebutAffectionModem",
          required = false) String ExportRecherchedatedebutAffectionModem,

      @RequestParam(value = "ExportRecherchedatefinAffectionModem",
          required = false) String ExportRecherchedatefinAffectionModem

  ) {
    return abonnementService.extractEnMasseClientNonConnecter(request, response, gouvernorat,
        villes, Cin, Nom, Prenom, codeClient, Tel, category, produit, dateDebut, dateFin,
        dateDebutModification, dateFinModification, loginModem, AffecterTo, Creepar,
        ExportRecherchedatedebutAffectionModem);
  }

  @PreAuthorize("hasAuthority('RESILIATION_ABONNEMENT')")
  @RequestMapping(method = RequestMethod.POST, value = "resiliationAbonnement")
  public String resiliationAbonnement(@RequestParam("clientid") Long clientid,
      @RequestParam("recuperationModdem") String recuperationModdem,
      RedirectAttributes redirectAttrs) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        Boolean resultFacture = generateFactureResilation(clientid, user.getUserid(),
            recuperationModdem, redirectAttrs);
        if (resultFacture) {
          abonnementService.saveResiliation(user, clientid);

          redirectAttrs.addFlashAttribute("message", "abonnementResilieé");
        }
      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.addComment Error:" + e);
      redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    }
    return "redirect:/client/viewclient/" + clientid;

  }


  @PreAuthorize("hasAnyAuthority('CHANGE_MODEM')")
  @PostMapping("/changerModem")
  public String changerModem(@RequestParam("modem") String numSerieModem,
      @RequestParam("clientId") Long clientId, RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      String resultat = abonnementService.changerModem(clientId, numSerieModem, user);

      redirectAttrs.addFlashAttribute("message", resultat);
    }

    return "redirect:/client/viewclient/" + clientId;
  }
  @PreAuthorize("hasAnyAuthority('CHANGE_MODEM')")
  @PostMapping("/changerSimModem")
  public String changerSimModem(@RequestParam("modem") String numSerieModem,
      @RequestParam("clientId") Long clientId, RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      String resultat = abonnementService.changerSimModem(clientId, numSerieModem, user);

      redirectAttrs.addFlashAttribute("message", resultat);
    }

    return "redirect:/client/viewclient/" + clientId;
  }

  @ResponseBody
  @RequestMapping(value = "/verificationtelfixEdite", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String verificationTelFixEdit(@RequestBody MultiValueMap<String, String> formData) {
    return abonnementService.verificationTelFixEdit(formData);

  }

  @ResponseBody
  @RequestMapping(value = "/verificationcinEdit", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String verificationcinEdit(@RequestBody MultiValueMap<String, String> formData) {
    return abonnementService.verificationCinEdit(formData);

  }

  @ResponseBody
  @RequestMapping(value = "/getMotif/{statutTT}", method = RequestMethod.GET)
  public List<String> getMotifStatutTT(@PathVariable("statutTT") String statutTT) {
    return abonnementService.getMotifStatutTT(statutTT);

  }

  @ResponseBody
  @RequestMapping(value = "/getAllMotif", method = RequestMethod.GET)
  public List<String> getAllMotif() {
    return demandeAbonnementRepository.findMotifInstance();

  }


  @GetMapping("/AbonnementStats")
  @ResponseBody
  public List<Map<String, Object>> getAbonnementStats(@RequestParam String date,
      @RequestParam String type, @RequestParam(required = false) Long userId,
      @RequestParam(required = false) Long idRevOrPos) {

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
    }

    List<Object[]> results;
    if ("total".equalsIgnoreCase(type)) {
      results =
          abonnementRepository.getAbonnementSummaryTotal(startOfSelectedDate, endOfSelectedDate);
    } else if ("byChefSecteur".equalsIgnoreCase(type)) {
      results = abonnementRepository.getAbonnementSummaryByChefSecteur(startOfSelectedDate,
          endOfSelectedDate, userId, null);
    } else {
      throw new IllegalArgumentException("Invalid type selected: " + type);
    }
    List<Map<String, Object>> abonnements = new ArrayList<>();
    for (Object[] result : results) {
      Map<String, Object> map = new HashMap<>();
      if ("total".equalsIgnoreCase(type)) {
        map.put("resiliationCount", result[0]);
        map.put("assignedCount", result[1]);
        map.put("proformatCount", result[2]);
        map.put("miseenserviceCount", result[3]);
      } else if ("byChefSecteur".equalsIgnoreCase(type)) {
        map.put("chefSecteurId", result[0]);
        map.put("chefSecteurName", result[1]);
        map.put("resiliationCount", result[2]);
        map.put("assignedCount", result[3]);
        map.put("proformatCount", result[4]);
        map.put("miseenserviceCount", result[5]);

      }

      abonnements.add(map);
    }

    return abonnements;
  }

  @RequestMapping(method = RequestMethod.POST, value = "resiliationAbonnementFacture")
  public String resiliationAbonnementFacture(@RequestParam("clientid") Long clientid,
      @RequestParam(value = "affectedTo", required = false) Long affectedTo,
      @RequestParam("recuperationModdem") String recuperationModdem,
      RedirectAttributes redirectAttrs) {

    try {
      generateFactureResilation(clientid, affectedTo, recuperationModdem, redirectAttrs);

    } catch (Exception e) {
      logger.error(" demandeabonnement.resiliationAbonnementFacture Error:" + e);
      redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    }

    return "redirect:/client/viewclient/" + clientid;
  }

  @RequestMapping(method = RequestMethod.POST, value = "updateEnvoiSms")
  public String updateEnvoiSms(@RequestParam("clientid") Long clientid,

      RedirectAttributes redirectAttrs) {

    try {
      abonnementService.updateEnvoiSms(clientid, redirectAttrs);

    } catch (Exception e) {
      logger.error(" demandeabonnement.resiliationAbonnementFacture Error:" + e);
      redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    }

    return "redirect:/client/viewclient/" + clientid;
  }

  private Boolean generateFactureResilation(Long clientid, Long affectedTo,
      String recuperationModdem, RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      Instant datenow = Instant.now();
      DateTimeFormatter formatter =
          DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
      Instant datefin = datenow.plus(Long.parseLong(factureResilationDateFin), ChronoUnit.DAYS);
      String echancedate = formatter.format(datefin);
      // commande.setProduit(produits);
      Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);

      List<Facture> factureResilation =
          factureService.getFacturesByClientAndIsFactureResilation(clientid, true);
      List<EntryCommande> EntryCommandes = new ArrayList<>();

      if (factureResilation.size() == 0) {
        Double PrixResiliation = Double.parseDouble(factureResilationPrixDepass6);
        if (abonnement.getCreatedDate() != null) {
          LocalDate currentDate = datenow.atZone(ZoneId.systemDefault()).toLocalDate();
          Instant instantcretedAbonnmentDate = abonnement.getCreatedDate().toInstant();

          // Convert Instant to LocalDate using the system's default time zone
          LocalDate cretedAbonnmentDate =
              instantcretedAbonnmentDate.atZone(ZoneId.systemDefault()).toLocalDate();


          // Calculate the difference in months
          long monthsDifference = ChronoUnit.MONTHS.between(cretedAbonnmentDate, currentDate);
          if (monthsDifference < 6) {
            PrixResiliation = Double.parseDouble(facturResilationPrixNondepasse6);
          }
          if (abonnement.getDateFinContrat() != null) {
            DateTimeFormatter mydatefinContratformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Parse the string into a LocalDate object
            LocalDate localDateFinContrat =
                LocalDate.parse(abonnement.getDateFinContrat(), mydatefinContratformatter);
            long monthsDifferenceDateFin = currentDate.compareTo(localDateFinContrat);
            if (monthsDifferenceDateFin >= 0 && monthsDifference >= 12) {
              PrixResiliation = 0.0;
            }

          }
        }
        if (PrixResiliation > 0.0) {
          Double totalPrixtva = (PrixResiliation * (19 * 0.01)) * 1;
          EntryCommandes
              .add(factureService.saveEntriescommande(CrmUtils.formatDoubleInput(PrixResiliation),
                  CrmUtils.formatDoubleInput(PrixResiliation + totalPrixtva), 1, null, null,
                  "Résiliation abonnement" + abonnement.getPack().getTitle(),
                  CrmUtils.formatDoubleInput(totalPrixtva), 19L));

          if (recuperationModdem.equals("false")) {
            Double PrixResiliationModem = Double.parseDouble(factureResilationPrixModem);
            Double totalPrixtvaModem = (PrixResiliationModem * (19 * 0.01)) * 1;
            EntryCommandes.add(
                factureService.saveEntriescommande(CrmUtils.formatDoubleInput(PrixResiliationModem),
                    CrmUtils.formatDoubleInput(PrixResiliationModem + totalPrixtvaModem), 1, null,
                    null, "Routeur Nety", CrmUtils.formatDoubleInput(totalPrixtvaModem), 19L));
          }
          if (affectedTo != null) {
            user = userRepository.findByUserId(affectedTo);
          }
          Commande commande = factureService.setCommande(abonnement, datefin.toString(),
              echancedate, user, EntryCommandes);
          Facture premiereFacture =
              factureService.generateFacture(commande, user, false, null, true);

          factureService.setEntryTvaFacture(premiereFacture);
          redirectAttrs.addFlashAttribute("message", "generationFactureResilationSuccess");

          ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
          Map<String, Object> Message = new HashMap<String, Object>();
          Smstemplate findtemplatesms =
              templatesmsRepository.findSmstemplateByname("SmsResiliation");
          String Template = findtemplatesms.getTemplate();

          Template = Template.replace("{facture}",
              premiereFacture.getAbonnement().getTelFixe().toString());
          Message.put("number", premiereFacture.getAbonnement().getTelMobile());
          Message.put("message", Template);
          smsToSend.add(Message);
          Boolean resultaSms = notificationservice.sendsmsnotification(smsToSend);
          logger.info("sms resilation send " + premiereFacture.getAbonnement().getTelMobile()
              + " statut " + resultaSms);
        }
        return true;
      } else {
        redirectAttrs.addFlashAttribute("message", "factureResilationGenere");
        return false;
      }
    }
    return false;
  }

  @RequestMapping(method = RequestMethod.POST, value = "recouvrementAbonnement")
  public String recouvrementAbonnement(@RequestParam("clientid") Long clientid,
      @RequestParam(value = "affectedTo", required = false) Long affectedTo,
      RedirectAttributes redirectAttrs) {

    // try {
    abonnementService.changeToRecouvrement(clientid, redirectAttrs);

    // } catch (Exception e) {
    // logger.error(" demandeabonnement.resiliationAbonnementFacture Error:" + e);
    // redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    // }

    return "redirect:/client/viewclient/" + clientid;
  }

  @RequestMapping(method = RequestMethod.POST, value = "reactivationAbonnement")
  public String reactivationAbonnement(@RequestParam("clientid") Long clientid,
      @RequestParam(value = "affectedTo", required = false) Long affectedTo,
      RedirectAttributes redirectAttrs) {

    // try {
    abonnementService.changeToActive(clientid, redirectAttrs);


    return "redirect:/client/viewclient/" + clientid;
  }

  @RequestMapping(method = RequestMethod.POST, value = "SuspenduAbonnement")
  public String SuspenduAbonnement(@RequestParam("clientid") Long clientid,
      @RequestParam(value = "affectedTo", required = false) Long affectedTo,
      RedirectAttributes redirectAttrs) {

    // try {
    abonnementService.changeToSuspendu(clientid, redirectAttrs);


    return "redirect:/client/viewclient/" + clientid;
  }

  @RequestMapping(method = RequestMethod.POST, value = "ReprendreCycleAbonnement")
  public String ReprendreCycleAbonnement(@RequestParam("clientid") Long clientid,
      @RequestParam(value = "affectedTo", required = false) Long affectedTo,
      RedirectAttributes redirectAttrs) {

    // try {
    abonnementService.ReprendreCycleAbonnement(clientid, redirectAttrs);


    return "redirect:/client/viewclient/" + clientid;
  }


  @RequestMapping(method = RequestMethod.POST, value = "addToRadus")
  public String addToRadus(@RequestParam("clientid") Long clientid,

      RedirectAttributes redirectAttrs) {

    // try {
    abonnementService.addToRadus(clientid, redirectAttrs);


    return "redirect:/client/viewclient/" + clientid;
  }

  @RequestMapping(method = RequestMethod.POST, value = "changrRadusAbonnement")
  public String changrRadusAbonnement(@RequestParam("clientid") Long clientid,

      RedirectAttributes redirectAttrs) {

    // try {
    abonnementService.changrRadusAbonnement(clientid, redirectAttrs);


    return "redirect:/client/viewclient/" + clientid;
  }

  @RequestMapping(method = RequestMethod.POST, value = "changrProchainDateFacturationAbonnement")

  public String changrProchainDateFacturationAbonnement(@RequestParam("clientid") Long clientid,
      @RequestParam("dateNouvelle") String dateNouvelle, RedirectAttributes redirectAttrs) {
    abonnementService.changrProchainDateFacturationAbonnement(clientid, dateNouvelle,
        redirectAttrs);

    return "redirect:/client/viewclient/" + clientid;
  }

  @GetMapping("/client-data")
  @ResponseBody
  public List<Map<String, Object>> getClientData(@RequestParam String filterOption2,
      @RequestParam(required = false) Long distributeur) {
    List<Map<String, Object>> resultClient = null;
    if ("total2".equalsIgnoreCase(filterOption2)) {
      resultClient = abonnementRepository.getClientCountsTotal();
    } else if ("byChefSecteur2".equalsIgnoreCase(filterOption2)) {
      resultClient = abonnementRepository.getClientCounts(distributeur);
    } else {
      throw new IllegalArgumentException("Invalid type selected: " + filterOption2);
    }
    return resultClient;
  }


  @RequestMapping(path = "/imprimer_recu_resilation_A4", method = RequestMethod.POST)
  public void downloadPDFRecuResilioationA4(Long clientId, String numSerieModem,
      HttpServletResponse response) throws Exception {

    try {
      // set file Recu Payment



      File filePyement = new File(pathRecuResilation + CrmUtils.getYear() + "/"
          + CrmUtils.getMonth() + "/" + PrefixDocument.NOMEFILE_RECU_PAYMENT + clientId + ".pdf");
      if (filePyement.exists()) {
        filePyement.delete();
      }
      filePyement = abonnementService.createPDFRecuResilationA4(clientId, numSerieModem);


      response
          .setContentType("application/x-pdf ; charset=" + Charset.forName("utf-8").displayName());
      response.setHeader("Content-disposition", "inline; filename=" + filePyement.getName());

      // get your file as InputStream
      InputStream targetStream = new FileInputStream(filePyement);
      // copy it to response's OutputStream

      org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
      response.flushBuffer();
      // close input stream file
      targetStream.close();
      // delete file
      // CrmUtils.deleteFile(File1);
    } catch (IOException ex) {

      logger.error("PayementController.downloadPDFRecuFactureA4 Exception: " + ex.getMessage());
      throw new RuntimeException("IOError writing file to output stream");
    }

  }
}
