package crm.chifco.com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.CategorieProduitInternet;
import crm.chifco.com.model.DemandeModem;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.DemandeModemRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.DataFromCsv;
import crm.chifco.com.service.ExportExcelModem;
import crm.chifco.com.service.ModemHistoryService;
import crm.chifco.com.service.ModemService;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.UserService;
import crm.chifco.com.templateclasse.AdminModem;
import crm.chifco.com.templateclasse.ExportModem;
import crm.chifco.com.templateclasse.ModemDistributeur;
import crm.chifco.com.templateclasse.ModemEtatStockDist;
import crm.chifco.com.templateclasse.ModemEtatStockRev;
import crm.chifco.com.templateclasse.ModemRevendeur;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.HtmlTemplateEmail;
import crm.chifco.com.utils.UserTypeConstant;

@Controller
public class ModemController {

  @Autowired
  private ModemRepository modemRepository;
  @Autowired
  private DataFromCsv datafromcsv;

  @Autowired
  ModemService modemservice;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserService UserService;

  @Autowired
  Notification notificationservice;

  @Autowired
  DemandeModemRepository DemandeModemRepository;

  @Autowired
  CategorieProduitInternetRepository categorieProduitInternetRepository;

  @Autowired
  ModemHistoryService modemHistoryService;

  @Autowired
  AbonnementRepository abonnementRepository;

  @Autowired
  GouvernoratRepository gouvernoratRepository;

  private final Logger logger = LogManager.getLogger(this.getClass());

  // ************************************************************* api pour
  // recuperer la liste des modems

  /** modame */
  @PreAuthorize("hasAnyAuthority('READ_MODEM_POS','READ_MODEM_LIST_AREA','READ_MODEM_OWNER')")
  @RequestMapping(method = RequestMethod.GET, value = "allmodemExceptAdmin")
  @ResponseBody
  public HashMap<String, Object> getAllDemandeAbonnement(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") String ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    String role = null;
    Long idconnected = null;
    List<String> StringsRole = new ArrayList<String>();
    int currentpage = start / length;
    Page<ModemDistributeur> responseDataDis = null;
    Page<ModemRevendeur> responseDataRevAndPos = null;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      role = user.getRole().getRoleName();
      idconnected = user.getUserid();

    }
    logger.info("all liste modem : user conneted role : " + role + " id user " + idconnected);
    // int page = start / length;

    if (StringsRole.contains("READ_MODEM_POS")) {

      responseDataRevAndPos =
          modemservice.modemPosFindPaginated(idconnected, currentpage + 1, length, filterrecherche);
    }

    else if (StringsRole.contains("READ_MODEM_LIST_AREA")) {
      responseDataDis = modemservice.modemDistFindPaginated(idconnected, currentpage + 1, length,
          filterrecherche);
    }

    else if (StringsRole.contains("READ_MODEM_OWNER")) {
      responseDataRevAndPos =
          modemservice.modemRevFindPaginated(idconnected, currentpage + 1, length, filterrecherche);
    }

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    if (responseDataDis != null) {
      myGreetings.put("data", responseDataDis.getContent());
      myGreetings.put("recordsTotal", responseDataDis.getTotalElements());
      myGreetings.put("recordsFiltered", responseDataDis.getTotalElements());

    } else if (responseDataRevAndPos != null) {
      myGreetings.put("data", responseDataRevAndPos.getContent());
      myGreetings.put("recordsTotal", responseDataRevAndPos.getTotalElements());
      myGreetings.put("recordsFiltered", responseDataRevAndPos.getTotalElements());
    } else {
      myGreetings.put("data", null);
      myGreetings.put("recordsTotal", 0);
      myGreetings.put("recordsFiltered", 0);
    }

    myGreetings.put("draw", draw);
    myGreetings.put("start", start);

    return myGreetings;
    /* } */

  }

  /** modame */

  @PreAuthorize("hasAuthority('READ_MODEM')")
  @RequestMapping(method = RequestMethod.GET, value = "allmodemAdmin")
  @ResponseBody
  public HashMap<String, Object> getallmodemAdmin(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") String ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    int currentpage = start / length;
    Page<AdminModem> responseData = null;

    responseData = modemservice.findPaginatedModemAdmin(currentpage + 1, length, filterrecherche);

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    myGreetings.put("num", responseData.getNumberOfElements());

    return myGreetings;
  }

  @PreAuthorize("hasAuthority('READ_MODEM')")
  @RequestMapping(value = "/modemsadmin")
  public String modemsAdmin(Model model) {

    List<String> StringsRole = new ArrayList<String>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      model.addAttribute("UPDATE_MODEM", StringsRole.contains("UPDATE_MODEM"));
      model.addAttribute("LIST_MODEM_AFFECTED_ADMIN",
          StringsRole.contains("LIST_MODEM_AFFECTED_ADMIN"));
      model.addAttribute("EXPORT_MODEM", StringsRole.contains("EXPORT_MODEM"));
      model.addAttribute("READ_MODEM_DETAILS", StringsRole.contains("READ_MODEM_DETAILS"));
      model.addAttribute("READ_MODEM_HISTORY", StringsRole.contains("READ_MODEM_HISTORY"));
      model.addAttribute("DEACTIVATE_MODEM", StringsRole.contains("DEACTIVATE_MODEM"));
      model.addAttribute("UNASSIGN_MODEM", StringsRole.contains("UNASSIGN_MODEM"));

    }

    List<CategorieProduitInternet> type = categorieProduitInternetRepository.findAll();
    model.addAttribute("type", type);
    List<User> listusers = userRepository.listUsersDistRevPos();
    model.addAttribute("listusers", listusers);

    return "modem/adminmodem";
  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM_POS','READ_MODEM_LIST_AREA','READ_MODEM_OWNER')")
  @RequestMapping(value = "/modems")
  public String modems(Model model) {

    Long idconnected = null;
    List<String> StringsRole = new ArrayList<String>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      idconnected = user.getUserid();
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      model.addAttribute("UPDATE_MODEM", StringsRole.contains("UPDATE_MODEM"));
      model.addAttribute("LIST_MODEM_AFFECTED_ADMIN",
          StringsRole.contains("LIST_MODEM_AFFECTED_ADMIN"));
      model.addAttribute("LIST_MODEM_AFFECTED_OTHER",
          StringsRole.contains("LIST_MODEM_AFFECTED_OTHER"));
      model.addAttribute("EXPORT_MODEM", StringsRole.contains("EXPORT_MODEM"));
      model.addAttribute("READ_MODEM_DETAILS", StringsRole.contains("READ_MODEM_DETAILS"));
      model.addAttribute("READ_MODEM_HISTORY", StringsRole.contains("READ_MODEM_HISTORY"));
      model.addAttribute("DEACTIVATE_MODEM", StringsRole.contains("DEACTIVATE_MODEM"));
      model.addAttribute("UNASSIGN_MODEM", StringsRole.contains("UNASSIGN_MODEM"));


    }

    List<CategorieProduitInternet> type = categorieProduitInternetRepository.findAll();
    model.addAttribute("type", type);
    List<User> listusers = new ArrayList<>();

    if (StringsRole.contains("LIST_MODEM_AFFECTED_ADMIN")) {
      listusers = userRepository.listUsersDistRevPos();
    } else if (StringsRole.contains("LIST_MODEM_AFFECTED_OTHER")) {
      listusers = userRepository.findUsersByAffectedTo(idconnected);
    }

    model.addAttribute("listusers", listusers);
    return "modem/modems";
  }
  // ****************************************************************** (1)api
  // pour creer ou modifier un modem

  @PreAuthorize("hasAnyAuthority('CREATE_MODEM','UPDATE_MODEM')")
  @RequestMapping(path = {"/ajoutermodem", "/edit/{id}"})
  public String editmodemById(Model model, @PathVariable("id") Optional<Long> id) throws Exception {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    // recuperer les informations necessaires vers le formlaire pour (creer ou
    // mettre à jour un modem) : Consulter le fichier ImodemRepository dans le
    // package Repo
    List<CategorieProduitInternet> type = categorieProduitInternetRepository.findAll(); // liste
                                                                                        // des
                                                                                        // models
                                                                                        // des
    // modems qui sont
    // enregistrés dans la
    // BDD
    // List<Long> debit = modemRepository.listdebit(); // liste des debits des
    // modems qui sont enregistrés dans la BDD
    List<String> marque = modemRepository.listmarque(); // liste des marques des modems qui sont
                                                        // enregistrés dans la
    // BDD

    // envoyer les informations recuperées pour presenter le formulaire HTML
    model.addAttribute("type", type);
    model.addAttribute("debit", "");
    model.addAttribute("marque", marque);

    if (id.isPresent()) { // si l'id du modem existe

      Modem modem = modemservice.getmodemById(id.get()); // recuperer les informatons du ce modem et
                                                         // les afficher
      // dans le formulaire de la modification

      model.addAttribute("modem", modem);

    } else { // si non creer une nouvelle instance modem et l'envoyer au formulaire de la
      // creation
      Modem modem = new Modem();

      model.addAttribute("modem", modem);
    }

    return "modem/enregistrermodem";
  }

  // **************************************************** (2) api pour creer ou
  // modifier un modem
  @PreAuthorize("hasAnyAuthority('CREATE_MODEM','UPDATE_MODEM')")
  @RequestMapping(path = "/createmodem", method = RequestMethod.POST)
  public String createOrUpdatemodem(Modem modem, RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = null;
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }
    Boolean addedOrUpdatedModem = modemservice.createOrUpdatemodem(modem, user); // une methode qui
    // determine le type de la
    // fonction à traiter
    // (create
    // or update)
    if (!addedOrUpdatedModem) {
      redirectAttrs.addFlashAttribute("message", "modemexiste");
      return "redirect:/ajoutermodem";
    } else {
      redirectAttrs.addFlashAttribute("message", "modemecree");
      return "redirect:/modemsadmin";
    }
  }

  // ********************************************************* api pour exporter
  // la liste des modems
  @PreAuthorize("hasAnyAuthority('EXPORT_MODEM')")
  @RequestMapping(value = "/exporterData")
  public ModelAndView exportToExcel(HttpServletResponse response,
      @RequestParam("modemIds") String modemIds, RedirectAttributes redirectAttrs)
      throws IOException {
    User user = null;
    List<String> roles = new ArrayList<>();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      roles = user.getRole().getStringsRole(user.getRole().getPrivileges());
    }
    List<ExportModem> myListmodem = new ArrayList<>();
    List<Long> idList =
        Arrays.stream(modemIds.split(",")).map(Long::parseLong).collect(Collectors.toList());

    List<List<Long>> batches = new ArrayList<>();
    int batchSize = 1500;
    // Diviser la liste en sous-listes de taille batchSize
    for (int i = 0; i < idList.size(); i += batchSize) {
      int end = Math.min(i + batchSize, idList.size());
      List<Long> batch = idList.subList(i, end);
      batches.add(batch);
    }

    // Parcourir les sous-listes et exécuter la requête pour chaque sous-liste
    for (List<Long> batch : batches) {

      List<ExportModem> listmodem = modemRepository.findAllByModemIdInToExport(batch);

      myListmodem.addAll(listmodem);
    }

    // recuperer
    // tout
    // les modems

    Boolean isAdmin = roles.contains("READ_MODEM");
    Boolean isDistributeur = roles.contains("READ_MODEM_LIST_AREA");
    Boolean isRevendeur = roles.contains("READ_MODEM_OWNER");
    Boolean isPos = roles.contains("READ_MODEM_POS");
    ModelAndView mav = new ModelAndView();
    mav.setView(new ExportExcelModem(isAdmin, isDistributeur, isRevendeur, isPos));

    // un
    // objet
    // ExportExcel
    // avec
    // la
    // liste
    // des modems ( la
    // classe : ExportExcel , package :service)
    mav.addObject("list", myListmodem);// // terminer l'exportation en appelant la methode export(
                                       // la
    // classe :
    // ExportExcel
    // , package :service)
    return mav; // afficher un message si l'exportation terminée avec succés
  }

  // ***********************************************************api pour confirmer
  // l'importation une liste des modems

  @PreAuthorize("hasAnyAuthority('CREATE_MODEM')")
  @RequestMapping(value = "/confirm_import")
  public String importe2File(@RequestParam("file") MultipartFile file, Model model,
      RedirectAttributes redirAttrs)

      throws Exception {
    Map<String, List<Modem>> result = new HashMap<>();
    User user = null;
    List<String> StringsRole = new ArrayList<String>();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    try {
      result = datafromcsv.importerModemfromExcel(file, user); // appel la methode qui sert à
      // importer une liste modems
      // (fichier csv)
      // dans la base des données //( classe :DataFromCsv , package : service)

      if (result.containsKey("EXCEL_ROW_LIMIT_ERROR") || result.containsKey("EXCEL_TABLE_ERROR")) {

        if (result.containsKey("EXCEL_ROW_LIMIT_ERROR")) {
          redirAttrs.addFlashAttribute("EXCEL_ROW_LIMIT_ERROR",
              "Le nombre de lignes dans votre fichier Excel dépasse la limite autorisée. Veuillez vous assurer que le fichier ne dépasse pas 700 lignes.");
        }

        if (result.containsKey("EXCEL_TABLE_ERROR")) {
          redirAttrs.addFlashAttribute("EXCEL_TABLE_ERROR",
              "Impossible d'importer ce fichier : Le contenu de ce fichier n'est pas valide.");
        }

        if (StringsRole.contains("READ_MODEM")) {
          return "redirect:/modemsadmin";
        } else if (!StringsRole.contains("READ_MODEM")) {
          return "redirect:/modems";
        }
      }

      model.addAttribute("messagesucces",
          "Vous avez Bien importer le fichier :  " + file.getOriginalFilename());
    } catch (Exception e) {

      logger.error("ModemController.importe2File Exception: " + e.getMessage());
      redirAttrs.addFlashAttribute("EXCEL_IMPORT_ERROR",
          " Un probléme d'importation le fichier :  " + file.getOriginalFilename());
      return "redirect:modemsadmin";
    }

    // List<modem> modems = modemservice.getmodems();
    List<Modem> modems = modemRepository.getListmodem(); // recuperer la nouvelle liste des modems
    if (modems == null) {
      modems = new ArrayList<Modem>();
    }
    model.addAttribute("modems", modems);

    model.addAttribute("modemValid", result.get("modemValide"));
    model.addAttribute("modemNonValid", result.get("modemNonValide"));
    model.addAttribute("modemNotValidInExcel", result.get("modemNotValidInExcel"));

    if (result.get("modemValide") != null && result.get("modemNonValide") != null) {
      model.addAttribute("nombreModemValide", result.get("modemValide").size());
      model.addAttribute("nombreModemNonValide", result.get("modemNonValide").size());
    }

    return "modem/resultatImportationExcel";
    // return "redirect:/modems";
  }

  @PreAuthorize("hasAnyAuthority('ASK_MODEM')")
  @RequestMapping(value = "/demandeModem")
  public String demandeModem(Model model, Long ref) throws Exception {

    UserService.returnInfoUserConnected(model);
    model.addAttribute("listCat", categorieProduitInternetRepository.findAll());
    return "modem/demandemodem";
  }

  @PreAuthorize("hasAnyAuthority('ASK_MODEM')")
  @RequestMapping(method = RequestMethod.POST, value = "sendMailDemande")
  public String sendMailDemande(@RequestParam("quantite") Long quantite,
      @RequestParam("username") String username, @RequestParam("radiotype") String typemodem,
      RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);

    List<User> userlist = UserService.findUsersByTypeUser(UserTypeConstant.ADMINISTRATEUR);
    // String resulta = "";

    if (user.getTypeUser().equals(UserTypeConstant.DISTRIBUTEUR)
        || user.getTypeUser().equals(UserTypeConstant.POS)) {
      String templateDistToAdmin = HtmlTemplateEmail.HtmlEmailDemandeModemsDistToAdmin(quantite,
          typemodem, user.getFirstName() + " " + user.getLastName());
      if (user.getTypeUser().equals(UserTypeConstant.POS)) {

        templateDistToAdmin = HtmlTemplateEmail.HtmlEmailDemandeModemsPOSToAdmin(quantite,
            typemodem, user.getFirstName() + " " + user.getLastName());
      }
      if (userlist.size() > 0) {
        for (int i = 0; i < userlist.size(); i++) {
          User userinfo = userlist.get(i);
          notificationservice.sendSimpleMailHtml(userinfo.getEmail(), templateDistToAdmin,
              "Demande de Stock");

        }
      }
    }

    else if (user.getTypeUser().equals(UserTypeConstant.REVENDEUR)) {

      User affectedToUser = userRepository.getById(user.getAffectedTo());

      String templateRevToAdmin = HtmlTemplateEmail.HtmlEmailDemandeModemsRevToAdmin(quantite,
          typemodem, user.getFirstName() + " " + user.getLastName());
      if (userlist.size() > 0) {
        for (int i = 0; i < userlist.size(); i++) {
          User userinfo = userlist.get(i);
          notificationservice.sendSimpleMailHtml(userinfo.getEmail(), templateRevToAdmin,
              "Demande de Stock");

        }
        String templateRevToDist = HtmlTemplateEmail.HtmlEmailDemandeModemsRevToDist(quantite,
            typemodem, user.getFirstName() + " " + user.getLastName());
        notificationservice.sendSimpleMailHtml(affectedToUser.getEmail(), templateRevToDist,
            "Demande de Stock");

      }
    }
    DemandeModem newdemande = new DemandeModem();
    newdemande.setUser(user);
    newdemande.setQuantiter(quantite.toString());
    newdemande.setTypeModem(typemodem);
    DemandeModemRepository.save(newdemande);
    redirectAttrs.addFlashAttribute("message", "SUCCESS_REGISTER");
    return "redirect:listeModemDemande";

  }

  @PreAuthorize("hasAnyAuthority('VOIR_LISTE_DEMANDE_MODEM','VOIR_LISTE_DEMANDE_MODEM_AREA','VOIR_LISTE_DEMANDE_MODEM_OWNER')")
  @RequestMapping(value = "/listeModemDemande")
  public String listeModemDemande(Model model, Long ref) throws Exception {

    UserService.returnInfoUserConnected(model);

    List<User> listUser = new ArrayList<>();

    User user = (User) model.getAttribute("user");

    List<String> priv = user.getRole().getStringsRole(user.getRole().getPrivileges());

    if (priv.contains("VOIR_LISTE_DEMANDE_MODEM")) {
      listUser = userRepository.listUsersDistRevPos();
    } else if (priv.contains("VOIR_LISTE_DEMANDE_MODEM_AREA")) {
      listUser = userRepository.findUsersByAffectedTo(user.getUserid());
    }
    model.addAttribute("listusers", listUser);
    return "modem/listeModemDemande";
  }

  @PreAuthorize("hasAnyAuthority('VOIR_LISTE_DEMANDE_MODEM','VOIR_LISTE_DEMANDE_MODEM_AREA','VOIR_LISTE_DEMANDE_MODEM_OWNER')")
  @RequestMapping(method = RequestMethod.GET, value = "getAllListeModemDemande")
  @ResponseBody
  public HashMap<String, Object> getAllListeModemDemande(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") String ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    // String role = null;
    // Long idconnected = null;
    List<String> StringsRole = new ArrayList<String>();
    // int currentpage = start / length;
    Page<DemandeModem> responseData = null;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = null;
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      // role = user.getRole().getRoleName();
      // idconnected = user.getUserid();

    }

    // filtre
    String type = null;
    Long userId = null;
    Date dateDebut = null;
    Date dateFin = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("type"), "") && obj.getString("type") != null) {
        type = obj.getString("type").trim();
      }
      if (!Objects.equals(obj.getString("user"), "") && obj.getString("user") != null) {
        userId = obj.getLong("user");
      }
      if (!Objects.equals(obj.getString("dateDebut"), "") && obj.getString("dateDebut") != null) {
        dateDebut = CrmUtils.convertStringToDate(obj.getString("dateDebut"));
      }
      if (!Objects.equals(obj.getString("dateFin"), "") && obj.getString("dateFin") != null) {
        dateFin = CrmUtils.convertStringToLocalDateTime(obj.getString("dateFin"));
      }
    }

    int page = start / length;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    PageRequest paging =
        PageRequest.of(page, length, Sort.by(Sort.Direction.DESC, "idDemandeModem"));
    if (StringsRole.contains("VOIR_LISTE_DEMANDE_MODEM")) {
      responseData = DemandeModemRepository.findAll(paging, type, userId, dateDebut, dateFin);
    } else if (StringsRole.contains("VOIR_LISTE_DEMANDE_MODEM_AREA")) {
      responseData = DemandeModemRepository.findAllArea(paging, user.getUserid(), userId, dateDebut,
          dateFin, type, user.getUserid());
    } else if (StringsRole.contains("VOIR_LISTE_DEMANDE_MODEM_OWNER")) {
      responseData =
          DemandeModemRepository.findAllOwner(paging, user.getUserid(), dateDebut, dateFin);
    }
    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());

    return myGreetings;
    // }

  }

  @PreAuthorize("hasAnyAuthority('LIST_MODEM_AFFECTED_ADMIN','LIST_MODEM_AFFECTED_OTHER','EXPORT_MODEM')")
  @ResponseBody
  @PostMapping(value = "/modem/allmodemid")
  public List<Long> getAllModemIds(@RequestBody String filterrecherche, Model model) {

    // String role = null;
    List<String> StringsRole = new ArrayList<String>();
    User user = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    }

    return modemservice.getAllModemIds(filterrecherche, StringsRole, user);
  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM_DETAILS','READ_MODEM_HISTORY')")
  @GetMapping("/modem/detail/{id}")
  public String getModemDetailPage(@PathVariable Long id, Model model) {
    Modem modem = modemRepository.findById(id).orElse(null);
    if (modem == null) {
      // Si aucun modem n'a été trouvé pour l'ID donné, afficher une erreur
      return "error";
    }
    // Ajouter le modem au modèle pour qu'il soit disponible dans la vue Thymeleaf
    model.addAttribute("modem", modem);

    // modem affecté a cette utilisateur
    User user = null;

    // modem affecté a cette reference client
    String refClient = null;

    if (modem.getAffecteDistributeur() != null && modem.getAffecteRevendeur() == null
        && modem.getAffecteClient() == null) {
      user = userRepository.findById(modem.getAffecteDistributeur()).get();
    } else if (modem.getAffecteRevendeur() != null && modem.getAffecteClient() == null) {
      user = userRepository.findById(modem.getAffecteRevendeur()).get();
    } else if (modem.getAffectePointdeVente() != null && modem.getAffecteClient() == null) {
      user = userRepository.findById(modem.getAffectePointdeVente()).get();
    }
    if (modem.getAffecteClient() != null) {
      refClient = abonnementRepository.findClientReferenceById(modem.getAffecteClient());
    }

    model.addAttribute("user", user);
    model.addAttribute("refClient", refClient);

    // Ajouter le historuqe modem au modèle
    model.addAttribute("modemHisotry", modemHistoryService.listModemHistoryByModemId(id));

    // Renvoyer la vue Thymeleaf pour afficher les détails du modem
    return "modem/detail";
  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM')")
  @RequestMapping(value = "/recapDist")
  public String getPageRecapDistAdmin(Model model) {

    // List<String> StringsRole = new ArrayList<String>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      // StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    List<User> listUsers = userRepository.findUsersByTypeUser("DISTRIBUTEUR");
    model.addAttribute("listUsers", listUsers);

    return "modem/recapDist";
  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM')")
  @RequestMapping(method = RequestMethod.GET, value = "recapdist")
  @ResponseBody
  public HashMap<String, Object> getRecapDistAdmin(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") String ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    int currentpage = start / length;
    Page<ModemEtatStockDist> responseData = null;

    responseData = modemservice.etatStockDist(currentpage + 1, length, filterrecherche);

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    myGreetings.put("num", responseData.getNumberOfElements());

    return myGreetings;
  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM','READ_MODEM_LIST_AREA')")
  @RequestMapping(value = "/recapRev")
  public String getPageRecapRevAdmin(Model model) {

    List<String> StringsRole = new ArrayList<String>();
    User user = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    List<User> listUsers = null;
    if (StringsRole.contains("READ_MODEM")) {
      listUsers = userRepository.findUsersByTypeUser("REVENDEUR");
    } else if (StringsRole.contains("READ_MODEM_LIST_AREA")) {
      listUsers = userRepository.findUsersByAffectedTo(user.getUserid());
    }

    model.addAttribute("listDistributeur", userRepository.findUsersByTypeUser("DISTRIBUTEUR"));
    model.addAttribute("listUsers", listUsers);
    model.addAttribute("listGouvernorats", gouvernoratRepository.findAll());

    return "modem/recapRev";
  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM','READ_MODEM_LIST_AREA')")
  @RequestMapping(method = RequestMethod.GET, value = "recaprev")
  @ResponseBody
  public HashMap<String, Object> getRecapRevAdmin(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") String ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    List<String> StringsRole = new ArrayList<String>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = null;
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    }

    int currentpage = start / length;
    Page<ModemEtatStockRev> responseData = null;

    responseData = modemservice.etatStockRev(currentpage + 1, length, StringsRole, user.getUserid(),
        filterrecherche);

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    myGreetings.put("num", responseData.getNumberOfElements());

    return myGreetings;
  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM','READ_MODEM_LIST_AREA')")
  @ResponseBody
  @PostMapping(value = "/modem/codeuser/{type}")
  public List<String> getAllCodeUserStock(@RequestBody String filterrecherche, Model model,
      @PathVariable String type) {

    // String role = null;
    List<String> StringsRole = new ArrayList<String>();
    User user = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    }

    return modemservice.getAllCodeUserStock(filterrecherche, type, user);
  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM','READ_MODEM_LIST_AREA')")
  @PostMapping("/stockDist")
  public String getStockDist(Model model, @RequestParam("userId") Long userId) {

    Map<String, List<Modem>> res = modemservice.getDetailsStockDist(userId);
    model.addAttribute("modemsADSL", res.get("modemsADSL"));
    model.addAttribute("modemsVDSL", res.get("modemsVDSL"));
    model.addAttribute("modemsGPON", res.get("modemsGPON"));
    model.addAttribute("modemsXDSL", res.get("modemsXDSL"));

    // Pour le bouton de retour : pour retourner vers la page de stock distributeur
    model.addAttribute("retourVers", "dist");

    return "modem/stockDetails";
  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM','READ_MODEM_LIST_AREA')")
  @PostMapping("/stockRev")
  public String getStockRev(Model model, @RequestParam("userId") Long userId) {

    Map<String, List<Modem>> res = modemservice.getDetailsStockRev(userId);
    model.addAttribute("modemsADSL", res.get("modemsADSL"));
    model.addAttribute("modemsVDSL", res.get("modemsVDSL"));
    model.addAttribute("modemsGPON", res.get("modemsGPON"));
    model.addAttribute("modemsXDSL", res.get("modemsXDSL"));

    // Pour le bouton de retour : pour retourner vers la page de stock distributeur
    model.addAttribute("retourVers", "rev");

    return "modem/stockDetails";
  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM','READ_MODEM_LIST_AREA')")
  @PostMapping("/exporterEtat")
  public void exportDataToExcel(HttpServletResponse response,
      @RequestParam("userCodes") String userCodes, @RequestParam("typeUser") String typeUser)
      throws IOException {

    List<String> listUserCodes = Arrays.asList(userCodes.split(","));

    // Créer un classeur Excel
    Workbook workbook = new XSSFWorkbook();

    // Créer une feuille de calcul
    Sheet sheet = workbook.createSheet("Données");

    Row rowTitle = sheet.createRow(0);

    // Créer les cellules individuelles
    Cell cell1 = rowTitle.createCell(0);
    // Cell cell2 = rowTitle.createCell(1);
    // Cell cell3 = rowTitle.createCell(2);
    // Cell cell4 = rowTitle.createCell(3);
    // Cell cell5 = rowTitle.createCell(4);

    // Fusionner les cellules
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

    // Définir le contenu de la cellule fusionnée
    cell1.setCellValue(
        typeUser.equals("dist") ? "État du stock par distributeur" : "État du stock par revendeur");

    // Facultatif : personnaliser le style de la cellule fusionnée
    CellStyle cellStyle = workbook.createCellStyle();
    Font tileFont = workbook.createFont();
    tileFont.setBold(true);
    // Définir la taille de la police
    tileFont.setFontHeightInPoints((short) 14);
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    cellStyle.setFont(tileFont);
    cell1.setCellStyle(cellStyle);

    // Créer un style de cellule pour les en-têtes
    CellStyle headerStyle = workbook.createCellStyle();
    Font headerFont = workbook.createFont();
    headerFont.setBold(true);
    headerStyle.setFont(headerFont);

    // Créer une ligne pour les en-têtes
    Row headerRow = sheet.createRow(1);
    // Ajouter les en-têtes des colonnes
    headerRow.createCell(0).setCellValue("Code");
    headerRow.createCell(1)
        .setCellValue(typeUser.equals("dist") ? "Nom et Prénom" : "Nom et Prénom / Nom commercial");
    headerRow.createCell(2).setCellValue("Nombre total de modems");
    headerRow.createCell(3).setCellValue("Nombre de modems affectés");
    headerRow.createCell(4).setCellValue("Nombre de modems disponibles");
    // Appliquer le style de cellule aux en-têtes
    for (Cell headerCell : headerRow) {
      headerCell.setCellStyle(headerStyle);
    }

    // Remplir les données
    int rowNum = 2;

    if (typeUser.equals("dist")) {
      List<ModemEtatStockDist> dataList = modemRepository.exportEtatStockDist(listUserCodes);
      for (ModemEtatStockDist data : dataList) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(data.getCodeUser());
        row.createCell(1).setCellValue(data.getFirstName() + " " + data.getLastName());
        row.createCell(2).setCellValue(data.getNb_modems_distributeur());
        row.createCell(3).setCellValue(data.getNb_modems_affectes());
        row.createCell(4).setCellValue(data.getNb_modems_disponible());
      }
    } else {
      List<ModemEtatStockRev> dataList = modemRepository.exportEtatStockRev(listUserCodes);
      for (ModemEtatStockRev data : dataList) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(data.getCodeUser());
        row.createCell(1).setCellValue(data.getFirstName() + " " + data.getLastName()
            + (data.getNomCommercial() != null ? " / " + data.getNomCommercial() : ""));
        row.createCell(2).setCellValue(data.getNb_modems_affectes());
        row.createCell(3).setCellValue(data.getNb_modems_client());
        row.createCell(4).setCellValue(data.getNb_modems_disponible());

      }
    }

    // Ajuster la largeur des colonnes automatiquement
    for (int i = 0; i < 5; i++) {
      sheet.autoSizeColumn(i);
    }

    // Configurer les informations de la réponse HTTP
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=etatStock.xlsx");

    // Obtenir le flux de sortie de la réponse
    ServletOutputStream outputStream = response.getOutputStream();

    // Écrire le contenu du classeur Excel dans le flux de sortie
    workbook.write(outputStream);
    workbook.close();

    // Vider et fermer le flux de sortie
    outputStream.flush();
    outputStream.close();
  }

  @PreAuthorize("hasAnyAuthority('DEACTIVATE_MODEM')")
  @PostMapping("/modem/changerStatus")
  public String changerStatus(@RequestParam("modemIdStatus") Long modemId,
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "newPassword", required = false) String password,
      @RequestParam(value = "keepCredentials", required = false) boolean keepCredentials,
      @RequestParam(value = "commentaire", required = false) String commentaire,
      RedirectAttributes redirectAttrs) {

    String role = null;
    List<String> StringsRole = new ArrayList<String>();
    User user = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      String resultat =
          modemservice.changerStatus(modemId, user, email, password, keepCredentials, commentaire);

      redirectAttrs.addFlashAttribute("message", resultat);

      if (StringsRole.contains("READ_MODEM")) {
        return "redirect:/modemsadmin";
      } else {
        return "redirect:/modems";
      }

    }

    return null;


  }


}
