package crm.chifco.com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.DTOclass.PackDto;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.CategorieProduitInternet;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.Offre;
import crm.chifco.com.model.OperationAbonnement;
import crm.chifco.com.model.Pack;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.Profession;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Ville;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.DemandeAbonnementHistoryRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.OffreRepository;
import crm.chifco.com.repository.OperationAbonnementRepository;
import crm.chifco.com.repository.PackRepository;
import crm.chifco.com.repository.ProfessionRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.ClientHistoryService;
import crm.chifco.com.service.FactureService;
import crm.chifco.com.service.ImportExcel;
import crm.chifco.com.service.OperationAbonnementService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.templateclasse.AllClientHistory;
import crm.chifco.com.utils.TypeAbonnment;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping(value = "operationAbonnement/*")
public class OperationAbonnementController {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PackRepository packRepository;
  @Autowired
  private OffreRepository offreRepository;

  @Autowired
  private GouvernoratRepository gouvernoratRepository;
  @Autowired
  private OperationAbonnementService operationAbonnementService;

  @Autowired
  private AbonnementRepository abonnementRepository;

  @Autowired
  private FactureService factureService;
  @Autowired
  private UserService userService;

  @Autowired
  private ClientHistoryService ClientHistoryService;

  @Autowired
  private CategorieProduitInternetRepository categorieProduitInternetRepository;

  @Autowired
  DemandeAbonnementHistoryRepository demandeAbonnementHistoryRepository;

  @Autowired
  private ProfessionRepository professionRepository;

  @Autowired
  private ImportExcel importExcel;

  @Autowired
  private OperationAbonnementRepository operationAbonnementRepository;

  /*** duplicate the demand ***/
  @PostMapping("/duplicatedDemande")
  public String duplicatedDemande(@RequestParam("operationId") Long operationId, Model model,
      RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    List<Abonnement> clients = new ArrayList<Abonnement>();
    User user = userRepository.findUsersByEmail(currentUser);
    OperationAbonnement op = operationAbonnementService.getDemandeMigration(operationId);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
      clients =
          abonnementRepository.findAbonnementsNotInMigrationAdminwithRef(op.getReferenceChifco());

    } else if (StringsRole.contains("READ_SUBSCRIPTION_LIST_OWNER")) {
      clients = abonnementRepository.findAbonnementsNotInMigrationRevendeur(op.getReferenceChifco(),
          user.getUserid());
    } else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
      clients = abonnementRepository
          .findAbonnementsNotInMigrationDistributeur(op.getReferenceChifco(), user.getUserid());
    }
    if (clients != null && !clients.isEmpty()) {

      boolean isDuplicateSuccess = operationAbonnementService.duplicateDemande(op, user);
      if (isDuplicateSuccess) {
        redirectAttrs.addFlashAttribute("message", "Demande a été dupliquée avec succès.");
        if (op.getTypeDemande().equals("T")) {
          return "redirect:/operationAbonnement/alldemandestransfert";
        } else if (op.getTypeDemande().equals("M")) {
          return "redirect:/operationAbonnement/alldemandesmigration";
        } else {
          return "redirect:/operationAbonnement/alldemandeschangementdebit";
        }
      } else {
        redirectAttrs.addFlashAttribute("message", "Échec de la duplication de la demande.");
        return "redirect:/operationAbonnement/changerstatut/" + operationId;
      }
    } else {
      redirectAttrs.addFlashAttribute("message",
          "Échec de la duplication une autre demande est en cours");
      return "redirect:/operationAbonnement/changerstatut/" + operationId;
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "alldemandesmigration")
  public String test(Model model, HttpServletRequest request) {
    userService.returnInfoUserConnected(model);
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    model.addAttribute("categories", categorieProduitInternets);
    List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
    List<Profession> listprofessions = professionRepository.findAll();
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      String[] listesvides = {};
      model.addAttribute("listedes_ids", listesvides);
    } else {
      Object listedesIds = request.getSession().getAttribute("listedes_ids");
      model.addAttribute("listedes_ids", listedesIds);
    }
    model.addAttribute("villes", listGouvernorats);
    model.addAttribute("professions", listprofessions);
    return "operationAbonnement/alldemandesmigration";
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallmigrations")
  @ResponseBody
  public HashMap<String, Object> getAlAbonnement(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    String typeDemande = "M";
    HashMap<String, Object> DemandesMigration = operationAbonnementService.findByTypeDemande(draw,
        start, length, search, ordercolumnaram, orderdir, filterrecherche, typeDemande);
    return DemandesMigration;
  }

  @GetMapping(value = "rechercheficheabonnementpourmigrationwithref")
  @ResponseBody
  public List<Abonnement> rechercheficheabonnementwithRef(Model model, HttpServletRequest request,
      @RequestParam String reference) {
    userService.returnInfoUserConnected(model);
    List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("listgouvernorats", listgouvernorats);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    List<Abonnement> clients = new ArrayList<Abonnement>();
    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
      clients = abonnementRepository.findAbonnementsNotInMigrationAdminwithRef(reference);

    } else if (StringsRole.contains("READ_SUBSCRIPTION_LIST_OWNER")) {
      clients =
          abonnementRepository.findAbonnementsNotInMigrationRevendeur(reference, user.getUserid());
    } else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
      clients = abonnementRepository.findAbonnementsNotInMigrationDistributeur(reference,
          user.getUserid());
    }
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    model.addAttribute("categories", categorieProduitInternets);
    model.addAttribute("clients", clients);
    request.getSession().setAttribute("listedes_ids", "");
    return clients;
  }

  @GetMapping(value = "rechercheficheabonnementpourmigration")
  public String rechercheficheabonnement(Model model, HttpServletRequest request) {
    userService.returnInfoUserConnected(model);
    Boolean isadmin = false;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("gouvernorats", listgouvernorats);
    List<Abonnement> clients = new ArrayList<Abonnement>();
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    model.addAttribute("categories", categorieProduitInternets);
    model.addAttribute("clients", clients);
    request.getSession().setAttribute("listedes_ids", "");
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    isadmin = StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL");
    model.addAttribute("isadmin", isadmin);
    return "operationAbonnement/addMigration";
  }

  @RequestMapping(method = RequestMethod.GET, value = "getabonnementtomigrationInfo/{clientid}")
  @ResponseBody
  public HashMap<String, Object> viewclientInfo(@PathVariable("clientid") Long clientid) {
    HashMap<String, Object> result = new HashMap<String, Object>();
    List<Facture> factures = factureService.getFacturesByClient(clientid);
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
    List<AllClientHistory> ClientHistory =
        ClientHistoryService.findClientHistoryByCin(abonnement.getCin());
    List<PackDto> packs = packRepository.findPacksWithDifferentCategoryInSameOffre(
        abonnement.getPack().getCategoriePack().getCategorieProduitInternetNom(),
        abonnement.getPack().getOffre().getOffreId());
    Modem modem = abonnement.getModem();
    List<Offre> offres = new ArrayList<Offre>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    Boolean isadmin = StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL");
    if (isadmin) {
      offres = offreRepository.findAllOffreByIsActive(true);

    } else {
      offres = offreRepository.findAllOffreExisteInPackByVisibility();

    }
    packs = packs.stream()
    	    .filter(el -> !el.getCategorie_produit_internet_code().equals(TypeAbonnment.Box))
    	    .collect(Collectors.toList());
    result.put("modem", modem);
    result.put("packs", packs);
    result.put("offres", offres);
    result.put("ClientHistory", ClientHistory);
    result.put("abonnement", abonnement);
    result.put("factures", factures);
    return result;
  }


  @RequestMapping(method = RequestMethod.GET, value = "changerstatut/{demandeId}")
  public String changerstatut(@PathVariable("demandeId") Long demandeId, Model model) {

    return operationAbonnementService.changerstatut(demandeId, model);
  }

  @RequestMapping(method = RequestMethod.POST, value = "migration")
  public String saveClient(@RequestParam("clientid") Long clientid,
      @RequestParam("packId") Long packid) {
    operationAbonnementService.saveNewMigration(packid, clientid);
    return "redirect:/operationAbonnement/alldemandesmigration";
  }

  @RequestMapping(method = RequestMethod.POST, value = "updatestatut")
  public String updateStatut(@RequestParam("operationId") Long operationId,
      @RequestParam(value = "motifRefus", required = false) String motifRefus,
      @RequestParam(value = "modemId", required = false) String modemId,
      @RequestParam(value = "telefixe", required = false) Long telefixe,
      RedirectAttributes redirectAttrs) {
    operationAbonnementService.updateStatutMigration(operationId, motifRefus, modemId, telefixe,
        redirectAttrs);
    return "redirect:/operationAbonnement/changerstatut/" + operationId;

  }

  @RequestMapping(method = RequestMethod.POST, value = "confirmeAbonnement")
  public String confirmeAbonnement(@RequestParam("confirmation") String confirmation,
      @RequestParam("idabon") Long idabon,
      @RequestParam(value = "modemId", required = false) Long modemId,
      @RequestParam(value = "motifRefus", required = false) String motifRefus,
      RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      operationAbonnementService.confirmeAbonnement(confirmation, idabon, user, modemId, motifRefus,
          redirectAttrs);
    }
    return "redirect:/operationAbonnement/changerstatut/" + idabon;
  }

  @RequestMapping(method = RequestMethod.POST, value = "referencett")
  public String sendNewReferencett(@RequestParam("referencett") String referencett,
      @RequestParam("idabon") Long idabon, RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      operationAbonnementService.sendNewrefTT(referencett, idabon, user, redirectAttrs);
    }
    return "redirect:/operationAbonnement/changerstatut/" + idabon;
  }

  @ResponseBody
  @PostMapping(value = "/addid")
  public JsonResponseBody addfiled(@RequestBody Long id, HttpServletRequest request) {
    return operationAbonnementService.addFiled(id, request);
  }

  @ResponseBody
  @PostMapping(value = "/removeid")
  public JsonResponseBody removefiled(@RequestBody Long id, HttpServletRequest request) {
    return operationAbonnementService.removeFiled(id, request);
  }

  @ResponseBody
  @PostMapping(value = "/addall")
  public List<Long> addAllIdToExport(@RequestBody String filterrecherche,
      @RequestParam("typeDemande") String typeDemande, HttpServletRequest request) {
    return operationAbonnementService.addAllIdToExport(filterrecherche, typeDemande, request);
  }

  @ResponseBody
  @PostMapping(value = "/removeall")
  public JsonResponseBody removeAllFromListExport(@RequestParam("typeDemande") String typeDemande,
      HttpServletRequest request) {
    return operationAbonnementService.removeAllFromListExport(typeDemande, request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "uploadeditmigrationenmasse")
  public String uploadeditmigrationenmasse(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttrs, HttpServletResponse response)
      throws JRException, IOException {
    importExcel.uploadEditOperationEnMasse(file, redirectAttrs, response);
    return "redirect:/operationAbonnement/alldemandesmigration";
  }

  @PostMapping("/extractenmasse")
  public void exportToExcel(@RequestParam("typeDemande") String typeDemande,
      HttpServletRequest request, HttpServletResponse response) {
    operationAbonnementService.exportToExcel(typeDemande, request, response);
  }

  @RequestMapping(method = RequestMethod.POST, value = "uploadedittranfertenmasse")
  public String uploadedittranfertenmasse(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttrs, HttpServletResponse response)
      throws JRException, IOException {
    importExcel.uploadEditOperationEnMasse(file, redirectAttrs, response);
    return "redirect:/operationAbonnement/alldemandestransfert";
  }

  @RequestMapping(method = RequestMethod.POST, value = "uploadeditchangedebitenmasse")
  public String uploadeditchangedebitenmasse(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttrs, HttpServletResponse response)
      throws JRException, IOException {
    importExcel.uploadEditOperationEnMasse(file, redirectAttrs, response);
    return "redirect:/operationAbonnement/alldemandeschangementdebit";
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallstatusabonnement")
  @ResponseBody
  public List<Statut> getallstatusabonnement() {
    return operationAbonnementService.getallStatusAbonnement();
  }

  @RequestMapping(method = RequestMethod.POST, value = "editdemandemigration")
  public String handleEditMigrationFormSubmit(@RequestParam("operationId") Long operationId,
      @RequestParam("packId") Long packId) {
    return operationAbonnementService.getUpdateDemandeAbonnement(packId, operationId);
  }

  @RequestMapping(method = RequestMethod.GET, value = "modifdemandemigration/{operationId}")
  public String modifdemandemigration(@PathVariable("operationId") Long operationId, Model model) {
    userService.returnInfoUserConnected(model);
    OperationAbonnement demandeMigration =
        operationAbonnementService.getDemandeMigration(operationId);
    Abonnement abonnement =
        abonnementRepository.findAbonnementByReferenceClient(demandeMigration.getReferenceChifco());
    List<Pack> packs = packRepository
        .findPacksWithCategoryInSameOffreEager(demandeMigration.getPack().getOffre().getOffreId());
    Pack packActuel = demandeMigration.getPack();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    Boolean isadmin = StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL");
    List<Offre> offres = new ArrayList<Offre>();
    if (isadmin) {
      offres = offreRepository.findAllOffreByIsActive(true);
    } else {
      offres = offreRepository.findAllOffreExisteInPackByVisibility();

    }
    model.addAttribute("offres", offres);
    model.addAttribute("abonnement", abonnement);
    model.addAttribute("packs", packs);
    model.addAttribute("packActuel", packActuel);
    model.addAttribute("demande", demandeMigration);
    model.addAttribute("operationId", operationId);
    return "operationAbonnement/modifmigration";

  }

  // ************************demande changement débit*************************
  @RequestMapping(method = RequestMethod.GET, value = "modifdemandechdebit/{operationId}")
  public String modifdemandechdebit(@PathVariable("operationId") Long operationId, Model model) {
    userService.returnInfoUserConnected(model);
    OperationAbonnement demandeMigration =
        operationAbonnementService.getDemandeMigration(operationId);
    Abonnement abonnement =
        abonnementRepository.findAbonnementByReferenceClient(demandeMigration.getReferenceChifco());
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<Pack> packs = new ArrayList<Pack>();
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    Boolean isadmin = StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL");
    if (isadmin) {
      packs = packRepository.findPacksWithSameCategory(
          abonnement.getPack().getCategoriePack().getCategorieProduitInternetNom());

    } else {

      packs = packRepository.findPacksWithSameCategoryNotPrivate(
          abonnement.getPack().getCategoriePack().getCategorieProduitInternetNom(),
          abonnement.getPack().getOffre().getTitle());
    }
    Pack packActuel = demandeMigration.getPack();
    List<Offre> offres = offreRepository.findAllOffreByIsActive(true);
    model.addAttribute("offres", offres);
    model.addAttribute("abonnement", abonnement);
    model.addAttribute("packs", packs);
    model.addAttribute("packActuel", packActuel);
    model.addAttribute("demande", demandeMigration);
    model.addAttribute("operationId", operationId);
    return "operationAbonnement/modifchangementdebit";

  }

  @RequestMapping(method = RequestMethod.POST, value = "editdemandechdebit/{operationId}")
  public String editDemandechangementdebit(@PathVariable("operationId") Long operationId,
      @RequestParam("packId") Long packId) {
    operationAbonnementService.editNewDemandeChangementdébit(packId, operationId);
    return "redirect:/operationAbonnement/alldemandeschangementdebit";
  }

  @RequestMapping(method = RequestMethod.GET, value = "alldemandeschangementdebit")
  public String alldemandeschangementdebit(Model model, HttpServletRequest request) {
    userService.returnInfoUserConnected(model);

    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    model.addAttribute("categories", categorieProduitInternets);
    List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
    List<Profession> listprofessions = professionRepository.findAll();
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      String[] listesvides = {};
      model.addAttribute("listedes_ids", listesvides);
    } else {
      Object listedesIds = request.getSession().getAttribute("listedes_ids");
      model.addAttribute("listedes_ids", listedesIds);
    }
    model.addAttribute("villes", listGouvernorats);
    model.addAttribute("professions", listprofessions);
    return "operationAbonnement/alldemandeschangementdebit";
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
        Boolean isAffectedUser = operationAbonnementService.affectRevendeur(demandeId,
            codeRevendeur, emailRevendeur, identificationFiscale);
        if (isAffectedUser) {
          ClientHistoryService.saveNewHistorique(user, demandeId,
              "Affectation de Revendeur à demande operation");
          redirectAttrs.addFlashAttribute("message", "demande Affectée");
        } else {
          redirectAttrs.addFlashAttribute("message", "demande non affectée");
        }
      }
    } catch (Exception e) {
    }
    return "redirect:/operationAbonnement/changerstatut/" + demandeId;
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
            operationAbonnementService.affectOneRevendeur(affectationClient.get("userselected"),
                Long.parseLong(affectationClient.get("demandeId")));
        if (isAffectedUser) {
          User userToAffected =
              userRepository.getById(Long.parseLong(affectationClient.get("userselected")));
          ClientHistoryService.saveNewHistorique(user,
              Long.parseLong(affectationClient.get("demandeId")),
              "La demande  a été réaffectée avec succès au revendeur  "
                  + userToAffected.getFirstName() + " " + userToAffected.getLastName() + '('
                  + userToAffected.getCodeUser() + ")");
        }

      }
    } catch (Exception e) {
      e.getMessage();

    }
    return isAffectedUser;

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
    }
    return userListe;
  }

  @RequestMapping(method = RequestMethod.GET, value = "getalldemandeschangementdebit")
  @ResponseBody
  public HashMap<String, Object> getalldemandeschangementdebit(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    String typeDemande = "CH";
    HashMap<String, Object> DemandesMigration = operationAbonnementService.findByTypeDemande(draw,
        start, length, search, ordercolumnaram, orderdir, filterrecherche, typeDemande);
    return DemandesMigration;
  }

  @GetMapping(value = "rechercheficheabonnementpourchdebit")
  public String rechercheficheabonnementpourchdebit(Model model, HttpServletRequest request) {
    userService.returnInfoUserConnected(model);
    Boolean isadmin = false;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("gouvernorats", listgouvernorats);
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    model.addAttribute("categories", categorieProduitInternets);
    request.getSession().setAttribute("listedes_ids", "");
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    isadmin = StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL");
    model.addAttribute("isadmin", isadmin);
    return "operationAbonnement/addChangementDebit";
  }

  @RequestMapping(method = RequestMethod.GET,
      value = "getabonnementtoChangementDebitInfo/{clientid}")
  @ResponseBody
  public HashMap<String, Object> viewchangementDébitInfoClient(
      @PathVariable("clientid") Long clientid) {
    HashMap<String, Object> result = new HashMap<String, Object>();
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientid);
    List<AllClientHistory> ClientHistory =
        ClientHistoryService.findClientHistoryByCin(abonnement.getCin());
    List<Pack> packs = new ArrayList<Pack>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    Boolean isadmin = StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL");
    if (isadmin) {
      packs = packRepository.findPacksWithSameCategory(
          abonnement.getPack().getCategoriePack().getCategorieProduitInternetNom());

    } else {
      packs = packRepository.findPacksWithSameCategoryNotPrivate(
          abonnement.getPack().getCategoriePack().getCategorieProduitInternetNom(),
          abonnement.getPack().getOffre().getTitle());

    }

    Modem modem = abonnement.getModem();
    result.put("modem", modem);
    result.put("packs", packs);
    result.put("ClientHistory", ClientHistory);
    result.put("abonnement", abonnement);
    return result;
  }

  @RequestMapping(method = RequestMethod.POST, value = "changementdebit")
  public String saveNewDemandechangementdebit(
      @RequestParam(value = "clientid", required = false) Long clientid,
      @RequestParam(value = "packId", required = false) Long packid,
      RedirectAttributes redirectAttrs) {
    Boolean result = operationAbonnementService.saveNewDemandeChangementdebit(packid, clientid);
    if (result == true) {
      redirectAttrs.addFlashAttribute("message", "success saveNewDemandeChangementdébit");
    } else {
      redirectAttrs.addFlashAttribute("message", "erreur saveNewDemandeChangementdébit");
    }
    return "redirect:/operationAbonnement/alldemandeschangementdebit";
  }

  // *******************Demande de transfert*************************
  @RequestMapping(method = RequestMethod.GET, value = "alldemandestransfert")
  public String alldemandesTransfert(Model model, HttpServletRequest request) {
    userService.returnInfoUserConnected(model);
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    model.addAttribute("categories", categorieProduitInternets);
    List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
    List<Profession> listprofessions = professionRepository.findAll();
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      String[] listesvides = {};
      model.addAttribute("listedes_ids", listesvides);
    } else {
      Object listedesIds = request.getSession().getAttribute("listedes_ids");
      model.addAttribute("listedes_ids", listedesIds);
    }
    model.addAttribute("villes", listGouvernorats);
    model.addAttribute("professions", listprofessions);
    return "operationAbonnement/alldemandetransfert";
  }

  @GetMapping(value = "rechercheficheabonnementpourtransfertwithref")
  @ResponseBody
  public List<Abonnement> rechercheficheabonnementwithRefTransfert(Model model,
      HttpServletRequest request, @RequestParam String reference) {
    userService.returnInfoUserConnected(model);
    List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("gouvernorats", listgouvernorats);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    List<Abonnement> clients = new ArrayList<Abonnement>();
    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
      clients = abonnementRepository.findAbonnementsNotInMigrationAdminwithRef(reference);

    } else if (StringsRole.contains("READ_SUBSCRIPTION_LIST_OWNER")) {
      clients =
          abonnementRepository.findAbonnementsNotInMigrationRevendeur(reference, user.getUserid());
    } else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
      clients = abonnementRepository.findAbonnementsNotInMigrationDistributeur(reference,
          user.getUserid());
    }
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    model.addAttribute("categories", categorieProduitInternets);
    model.addAttribute("clients", clients);
    request.getSession().setAttribute("listedes_ids", "");
    return clients;
  }

  @RequestMapping(method = RequestMethod.GET, value = "getalldemandestransfert")
  @ResponseBody
  public HashMap<String, Object> getalldemandestransfert(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    String typeDemande = "T";
    HashMap<String, Object> DemandesMigration = operationAbonnementService.findByTypeDemande(draw,
        start, length, search, ordercolumnaram, orderdir, filterrecherche, typeDemande);
    return DemandesMigration;
  }

  @GetMapping(value = "rechercheficheabonnementtransfert")
  public String rechercheficheabonnementtransfert(Model model, HttpServletRequest request) {
    userService.returnInfoUserConnected(model);
    List<Gouvernorat> listgouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("villes", listgouvernorats);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    Boolean isadmin = false;
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    isadmin = StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL");
    model.addAttribute("isadmin", isadmin);
    return "operationAbonnement/addDemandeTransfert";
  }

  @RequestMapping(method = RequestMethod.POST, value = "addDemandeTransfert")
  public String AjoutDemandeDetransfert(@RequestParam("clientId") Long clientId,
      @RequestParam("adresse") String adresse,
      @RequestParam("gouvernorats") Gouvernorat gouvernorat, @RequestParam("villes") Ville ville,
      @RequestParam("codepostale") PostalCode codepostale,
      @RequestParam("positionxy") String positionxy, @RequestParam("residence") Boolean residence,
      Model model, RedirectAttributes redirectAttrs) {
    return operationAbonnementService.AddDemandTransfert(clientId, adresse, gouvernorat, ville,
        codepostale, positionxy, residence, model);
  }

  @RequestMapping(method = RequestMethod.POST, value = "updateDemandeTransfert/{id}")
  public String updateDemandeTransfert(@PathVariable("id") Long id,
      @RequestParam("clientId") String clientIdStr, @RequestParam("adresse") String adresse,
      @RequestParam("gouvernorats") Gouvernorat gouvernorat, @RequestParam("villes") Ville ville,
      @RequestParam("codepostale") PostalCode codepostale,
      @RequestParam("positionxy") String positionxy, @RequestParam("residence") Boolean residence,
      Model model, RedirectAttributes redirectAttrs) {
    long clientId = Long.parseLong(clientIdStr);
    return operationAbonnementService.updateDemandTransfert(id, clientId, adresse, gouvernorat,
        ville, codepostale, positionxy, residence, model);
  }

  @RequestMapping(method = RequestMethod.GET, value = "modifdemandetransfert/{operationId}")
  public String modifDemandTransfert(@PathVariable("operationId") Long operationId, Model model) {
    userService.returnInfoUserConnected(model);
    OperationAbonnement demandeMigration =
        operationAbonnementService.getDemandeMigration(operationId);
    Abonnement abonnement =
        abonnementRepository.findAbonnementByReferenceClient(demandeMigration.getReferenceChifco());
    List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("gouvernorats", listGouvernorats);
    model.addAttribute("abonnement", abonnement);
    model.addAttribute("demande", demandeMigration);
    model.addAttribute("villeOld", demandeMigration.getVille().getVilleName());
    model.addAttribute("codePostalOld", demandeMigration.getCodePostale().getName());
    model.addAttribute("operationId", operationId);
    return "operationAbonnement/modiftransfert";
  }

  @RequestMapping(method = RequestMethod.GET, value = "getabonnementtoTransfertInfo/{operationId}")
  @ResponseBody
  public HashMap<String, Object> getabonnementtoTransfertInfo(
      @PathVariable("operationId") Long operationId) {

    OperationAbonnement op = operationAbonnementService.getDemandeMigration(operationId);
    HashMap<String, Object> result = new HashMap<String, Object>();
    Abonnement abonnement = abonnementRepository.findAbonnementByClientid(op.getAbonnementId());
    List<AllClientHistory> ClientHistory =
        ClientHistoryService.findClientHistoryByCin(abonnement.getCin());
    List<PackDto> packs = packRepository.findPacksWithSameCategoryInSameOffre(
        abonnement.getPack().getCategoriePack().getCategorieProduitInternetNom(),
        abonnement.getPack().getOffre().getOffreId());
    Modem modem = abonnement.getModem();
    result.put("modem", modem);
    result.put("packs", packs);
    result.put("ClientHistory", ClientHistory);
    result.put("abonnement", abonnement);
    return result;
  }

  @GetMapping("/Transfert")
  @ResponseBody
  public List<Map<String, Object>> getOperationsForTransfert(@RequestParam int year,
      @RequestParam String type) {
    return getOperationsData(year, "T");
  }

  @RequestMapping(method = RequestMethod.GET, value = ("/migration"))
  @ResponseBody
  public List<Map<String, Object>> getOperationsForMigration(@RequestParam int year,
      @RequestParam String type) {
    return getOperationsData(year, type);
  }

  @GetMapping("/ChangementDB")
  @ResponseBody
  public List<Map<String, Object>> getOperationsForChangementDB(@RequestParam int year,
      @RequestParam String type) {
    return getOperationsData(year, type);
  }

  private List<Map<String, Object>> getOperationsData(int year, String typeOperation) {
    List<Map<String, Object>> results =
        operationAbonnementRepository.countOperationsAndSucceededByMonth(year, typeOperation);
    List<Map<String, Object>> data = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      Map<String, Object> monthData = new HashMap<>();
      monthData.put("month", i);
      monthData.put("totalOperations", null);
      monthData.put("succeededOperations", null);
      data.add(monthData);
    }
    for (Map<String, Object> result : results) {
      int month = (int) result.get("month");
      data.set(month - 1, result); // Set data at index (month-1)
    }

    return data;
  }
}
