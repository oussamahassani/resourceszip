package crm.chifco.com.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.Motifrec;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.model.Servicetype;
import crm.chifco.com.model.Statusrec;
import crm.chifco.com.model.User;
import crm.chifco.com.radius.model.Radacct;
import crm.chifco.com.radius.model.Radcheck;
import crm.chifco.com.radius.service.RadcheckService;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.ReclamationRepository;
import crm.chifco.com.repository.StatusrecRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.ExportReclamationService;
import crm.chifco.com.service.ImportReclamationService;
import crm.chifco.com.service.MotifrecService;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.ReclamationHistoryService;
import crm.chifco.com.service.ReclamationService;
import crm.chifco.com.service.ServicetypeService;
import crm.chifco.com.service.StatusrecService;
import crm.chifco.com.templateclasse.AllClientHistory;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.DBEtatTT;
import crm.chifco.com.utils.HtmlTemplateEmail;
import crm.chifco.com.utils.NomStatutReclamation;
import crm.chifco.com.utils.RedchekConstant;
import crm.chifco.com.utils.UserTypeConstant;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping("/reclamations")
public class ReclamationController {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  Notification notificationservice;
  @Autowired
  private ServicetypeService servicetypeService;
  @Autowired
  private ReclamationRepository reclamationRepository;

  @Autowired
  private MotifrecService motifrecService;

  @Autowired
  private StatusrecService statusrecService;
  @Autowired
  private ReclamationService reclamationService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private StatusrecRepository statusrecRepository;
  @Autowired
  private ReclamationHistoryService reclamationHistoryService;
  @Autowired
  private AbonnementRepository abonnementRepository;
  @Value("${pathReclamation}")
  private String pathReclamation;
  @Autowired
  private ImportReclamationService importExcel;
  @Autowired
  private ExportReclamationService exportReclamationService;
  @Autowired
  private RadcheckService radcheckService;
  @Value("${xls.mail.responsable}")
  private String xlsMailResponsable;
  @Value("${xls.mail.sav}")
  private String xlsMailSAV;
  @Autowired
  private GouvernoratRepository gouvernoratRepository;



  @PreAuthorize("hasAuthority('CREATE_RECLAMATION')")
  @RequestMapping(method = RequestMethod.GET, value = "/addreclamation")
  public String addReclamationForm(Model model) {
    List<Statusrec> statusList = statusrecService.getAllStatusrec();
    List<Servicetype> servicetypeList = servicetypeService.getAllServicetypes();
    List<Motifrec> motifList = motifrecService.getAllMotifrec();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean isRevendeur = user.getTypeUser().equals(UserTypeConstant.REVENDEUR);
      Boolean isDistributeur = user.getTypeUser().equals(UserTypeConstant.DISTRIBUTEUR);
      Boolean isAdmin = StringsRole.contains("READ-RECLAMATION-ALL");
      model.addAttribute("isRevendeur", isRevendeur);
      model.addAttribute("isDistributeur", isDistributeur);
      model.addAttribute("isAdmin", isAdmin);
      model.addAttribute("currentId", user.getUserid());
    }

    model.addAttribute("statusList", statusList);
    model.addAttribute("servicetypeList", servicetypeList);
    model.addAttribute("motifList", motifList);
    model.addAttribute("reclamation", new Reclamation());
    return "reclamation/addreclamation";
  }

  @PreAuthorize("hasAuthority('CREATE_RECLAMATION')")
  @RequestMapping(method = RequestMethod.POST, value = "/addreclamation")
  public String saveReclamation(@ModelAttribute("reclamation") Reclamation reclamation,
      @RequestParam("servicetypeId") Long servicetypeId,
      @RequestParam(value = "motifId", required = false) Long motifId,
      @RequestParam(value = "clientId", required = false) Long clientId,
      @RequestParam(value = "justificatif", required = false) MultipartFile[] justificatifFiles,
      Authentication authentication, RedirectAttributes redirectAttributes) {
    Statusrec status = statusrecService.getStatusrecByDesignation(NomStatutReclamation.OPENED);
    Servicetype serviceType = servicetypeService.findbyServicetypeId(servicetypeId);
    if (servicetypeId == null || motifId == null || reclamation.getCategory() == null
        || clientId == null) {
      redirectAttributes.addFlashAttribute("message", "Des parametres sont manquants");
      return "redirect:/reclamations/addreclamation";
    }

    Motifrec motif = null;
    Long telephone = null;
    if (motifId != null) {
      motif = motifrecService.findById(motifId);
      reclamation.setMotif(motif);
    }
    String currentUser = authentication.getName();
    User userconnected = userRepository.findUsersByEmail(currentUser);
    if (clientId != null) {
      if (reclamation.getCategory().equals("Client")) {
        Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientId);
        reclamation.setClient(abonnement);
        telephone = abonnement.getTelMobile();
        Long ref_client = abonnement.getClientid();
        Reclamation reclamationold = reclamationRepository
            .findlastreclamationByclientIdServiceAndCategory("Technique", "Client", ref_client);
        if (reclamationold != null
            && !reclamationold.getStatus().getNomStatut().equals(DBEtatTT.Clôturée)
            && reclamationold.getCategory().equals("Client")
            && reclamationold.getServiceType().getCategorytype().equals("Technique")
            && serviceType.getCategorytype().equals("Technique")) {
          redirectAttributes.addFlashAttribute("message",
              "Vous avez une autre réclamation encours.");
          return "redirect:/reclamations/addreclamation";

        }
      } else {
        User user = userRepository.findById(clientId).get();
        reclamation.setUser(user);
        telephone = Long.parseLong(user.getTelephone());
      }
    } else {
      reclamation.setUser(userconnected);
      telephone = Long.parseLong(userconnected.getTelephone());
    }

    if (status != null && serviceType != null) {
      reclamation.setStatus(status);
      reclamation.setServiceType(serviceType);


      if (justificatifFiles != null && justificatifFiles.length > 0) {
        List<String> justificatifFileNames = new ArrayList<>();
        for (MultipartFile justificatifFile : justificatifFiles) {
          if (justificatifFile != null && !justificatifFile.isEmpty()) {
            try {
              String caractaireDestingtion = "reclamation_justification_";
              String fileName = caractaireDestingtion + System.currentTimeMillis() + "_"
                  + CrmUtils.noSpecialCharacters(justificatifFile.getOriginalFilename());
              CrmUtils.saveImageReclamation(justificatifFile, "", pathReclamation, fileName);
              justificatifFileNames.add(fileName);
            } catch (Exception e) {
              LOGGER.error("File upload exception: " + e.getMessage());
              redirectAttributes.addFlashAttribute("message",
                  "Failed to upload one or more justificatif files.");
              return "redirect:/reclamations/addreclamation";
            }
          }
        }
        reclamation.setJustificatifs(justificatifFileNames);
      }
      reclamation.setCreatedby(userconnected);
      reclamation.setEditedby(userconnected);
      reclamationService.saveReclamation(reclamation);
      String EmailReclamationTemplate = null;
      if (reclamation.getCategory().equals("Client")) {
        EmailReclamationTemplate = HtmlTemplateEmail.HtmlEmailReclamation(
            reclamation.getClient().getFirstName() + " " + reclamation.getClient().getLastName(),
            reclamation.getServiceType().getCategorytype(), reclamation.getMotif().getNomMotif(),
            reclamation.getRef_reclamation());
      } else {
        EmailReclamationTemplate = HtmlTemplateEmail.HtmlEmailReclamation(
            reclamation.getUser().getFirstName() + " " + reclamation.getUser().getLastName(),
            reclamation.getServiceType().getCategorytype(), reclamation.getMotif().getNomMotif(),
            reclamation.getRef_reclamation());
      }
      notificationservice.sendSimpleMailHtml(xlsMailSAV, EmailReclamationTemplate,
          "Demande de réclamation");
      // add reclamation to history
      reclamationHistoryService.insertNewHistoryclaims(reclamation,
          "Création d'une réclamation sous référence:" + reclamation.getRef_reclamation(),
          userconnected);
      String statutCheck = reclamation.getStatus().getNomStatut();
      if (statutCheck != null && reclamation.getCategory().equals("Client")
          && (statutCheck.equalsIgnoreCase(NomStatutReclamation.Clôturée)
              || statutCheck.equalsIgnoreCase(NomStatutReclamation.OPENED)
              || statutCheck.equalsIgnoreCase(NomStatutReclamation.RELANCEE)
              || statutCheck.equalsIgnoreCase(NomStatutReclamation.IN_PROGRESS))) {
        notificationservice.sendsmsToClient(reclamation,
            reclamation.getClient().getTelMobile().toString());
      }
      redirectAttributes.addFlashAttribute("message", "Une réclamation a été ajouté avec succées");
      switch (reclamation.getCategory()) {
        case "Client":
          if (reclamation.getServiceType().getCategorytype().equals("Technique")) {
            return "redirect:/reclamations/allclientreclamationTech/1";
          } else if (reclamation.getServiceType().getCategorytype().equals("TechniqueFSI")) {
            return "redirect:/reclamations/allclientreclamationTechniqueFSI/1";
          } else {
            return "redirect:/reclamations/allclientreclamationCommercial/1";
          }

        case "Revendeur":
          if (reclamation.getServiceType().getCategorytype().equals("Technique")) {
            return "redirect:/reclamations/allRevTechReclamations/1";
          } else {
            return "redirect:/reclamations/allRevComReclamations/1";
          }

        case "chefsecteur":
          if (reclamation.getServiceType().getCategorytype().equals("Technique")) {
            return "redirect:/reclamations/allDistTechReclamations/1";
          } else {
            return "redirect:/reclamations/allDistComReclamations/1";
          }

        default:
          return "redirect:/reclamations/addreclamation";
      }
    } else {
      redirectAttributes.addFlashAttribute("message",
          "Failed to upload one or more justificatif files.");
      return "reclamation/addreclamation";
    }
  }

  @GetMapping(value = "allclientreclamationTech/{pageNo}")
  public String allclientreclamationTech(@PathVariable(value = "pageNo") Integer pageNo,
      Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE-RECLAMATION");
      List<User> sav = userRepository.findUsersByPrivilegeName("ASSIGN_REC_TO_SAV");
      model.addAttribute("sav", sav);
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids_reclamations", new ArrayList<Long>());
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ-RECLAMATION-ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ-RECLAMATION-AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      Object listedes_ids_reclamations =
          request.getSession().getAttribute("listedes_ids_reclamations");
      model.addAttribute("listedes_ids_reclamations", listedes_ids_reclamations);
      model.addAttribute("AffectedTo", User);
      Servicetype serviceTechnique = servicetypeService.getServicetypeByCategory("Technique");
      List<Motifrec> recMotif =
          motifrecService.findMotifsByServiceType(serviceTechnique.getServicetypeId(), "Client");
      List<String> centrals = reclamationRepository.findAllDistinctCentral();
      List<Gouvernorat> gouvernorats = gouvernoratRepository.findAll();
      List<Statusrec> status = statusrecService.getAllStatusrec();
      model.addAttribute("gouvernorats", gouvernorats);
      model.addAttribute("centrals", centrals);
      model.addAttribute("status", status);
      model.addAttribute("motifRec", recMotif);
      List<String> sources = reclamationRepository.findDistinctSource();
      model.addAttribute("sources", sources);
    }

    return "reclamation/client-technique";
  }

  @GetMapping("centrals/by-gouvernorat")
  @ResponseBody
  public List<String> getCentralsByGouvernorat(@RequestParam String gouvernorat) {
    return reclamationRepository.findDistinctCentralByGouvernorat(gouvernorat);
  }

  @GetMapping("centrals/all")
  @ResponseBody
  public List<String> getAllCentrals() {
    return reclamationRepository.findAllDistinctCentral();
  }

  @RequestMapping(method = RequestMethod.GET, value = "getAllclientreclamationData")
  @ResponseBody
  public HashMap<String, Object> getAllclientreclamationData(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "category", required = false) String category) {
    return reclamationService.findAlltechReclamations(draw, start, length, search, ordercolumnaram,
        orderdir, filterrecherche, category, type);
  }

  @GetMapping(value = "allclientreclamationCommercial/{pageNo}")
  public String clients(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE-RECLAMATION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids_reclamations", new ArrayList<Long>());

      Object listedes_ids_reclamations =
          request.getSession().getAttribute("listedes_ids_reclamations");
      model.addAttribute("listedes_ids_reclamations", listedes_ids_reclamations);
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ-RECLAMATION-ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ-RECLAMATION-AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      Servicetype serviceTechnique = servicetypeService.getServicetypeByCategory("Commercial");
      List<Motifrec> recMotif =
          motifrecService.findMotifsByServiceType(serviceTechnique.getServicetypeId(), "Client");
      List<Statusrec> status = statusrecService.getAllStatusrec();
      model.addAttribute("status", status);
      model.addAttribute("motifRec", recMotif);
      model.addAttribute("AffectedTo", User);
      List<User> sav = userRepository.findUsersByPrivilegeName("ASSIGN_REC_TO_COM");
      model.addAttribute("sav", sav);
      List<String> sources = reclamationRepository.findDistinctSource();
      model.addAttribute("sources", sources);
    }

    return "reclamation/client-commercial";
  }

  @GetMapping(value = "allclientreclamationTechniqueFSI/{pageNo}")
  public String allclientreclamationTechniqueFSI(@PathVariable(value = "pageNo") Integer pageNo,
      Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE-RECLAMATION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids_reclamations", new ArrayList<Long>());

      Object listedes_ids_reclamations =
          request.getSession().getAttribute("listedes_ids_reclamations");
      model.addAttribute("listedes_ids_reclamations", listedes_ids_reclamations);
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ-RECLAMATION-ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ-RECLAMATION-AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      Servicetype serviceTechnique = servicetypeService.getServicetypeByCategory("TechniqueFSI");
      List<Motifrec> recMotif =
          motifrecService.findMotifsByServiceType(serviceTechnique.getServicetypeId(), "Client");
      List<Statusrec> status = statusrecService.getAllStatusrec();
      model.addAttribute("status", status);
      model.addAttribute("motifRec", recMotif);
      model.addAttribute("AffectedTo", User);
      List<User> sav = userRepository.findUsersByPrivilegeName("ASSIGN_REC_TO_COM");
      model.addAttribute("sav", sav);
      List<String> sources = reclamationRepository.findDistinctSource();
      model.addAttribute("sources", sources);
    }

    return "reclamation/client-techniqueFSI";
  }

  @GetMapping(value = "mesReclamations/{pageNo}")
  public String mesReclamations(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE-RECLAMATION_AGENT");
      List<User> sav = userRepository.findUsersByPrivilegeName("ASSIGN_REC_TO_SAV");
      model.addAttribute("sav", sav);
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids_reclamations", new ArrayList<Long>());
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ-RECLAMATION-ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ-RECLAMATION-AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      Object listedes_ids_reclamations =
          request.getSession().getAttribute("listedes_ids_reclamations");
      model.addAttribute("listedes_ids_reclamations", listedes_ids_reclamations);
      model.addAttribute("AffectedTo", User);
      List<Motifrec> recMotif = motifrecService.getAllMotifrec();
      List<String> centrals = reclamationRepository.findAllDistinctCentral();
      List<Gouvernorat> gouvernorats = gouvernoratRepository.findAll();
      List<Statusrec> status = statusrecService.getAllStatusrec();
      model.addAttribute("gouvernorats", gouvernorats);
      model.addAttribute("centrals", centrals);
      model.addAttribute("status", status);
      model.addAttribute("motifRec", recMotif);
      List<String> sources = reclamationRepository.findDistinctSource();
      model.addAttribute("sources", sources);
    }


    return "reclamation/mes_reclamations";
  }

  @RequestMapping(method = RequestMethod.GET, value = "getAllclientreclamationDataForAgent")
  @ResponseBody
  public HashMap<String, Object> getAllclientreclamationDataForAgent(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "category", required = false) String category) {

    return reclamationService.findAlltechReclamationsAgent(draw, start, length, search,
        ordercolumnaram, orderdir, filterrecherche, category, type);
  }

  @GetMapping(value = "allRevTechReclamations/{pageNo}")
  public String allRevTechReclamations(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE-RECLAMATION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ-RECLAMATION-ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ-RECLAMATION-AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      Servicetype serviceTechnique = servicetypeService.getServicetypeByCategory("Technique");
      List<Motifrec> recMotif =
          motifrecService.findMotifsByServiceType(serviceTechnique.getServicetypeId(), "Revendeur");
      List<Statusrec> status = statusrecService.getAllStatusrec();
      model.addAttribute("status", status);
      model.addAttribute("motifRec", recMotif);
      model.addAttribute("AffectedTo", User);
    }

    return "reclamation/revendeur-technique";
  }

  @GetMapping(value = "allRevComReclamations/{pageNo}")
  public String allRevComReclamations(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE-RECLAMATION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ-RECLAMATION-ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ-RECLAMATION-AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      model.addAttribute("AffectedTo", User);
    }
    Servicetype serviceTechnique = servicetypeService.getServicetypeByCategory("Commercial");
    List<Motifrec> recMotif =
        motifrecService.findMotifsByServiceType(serviceTechnique.getServicetypeId(), "Revendeur");
    List<Statusrec> status = statusrecService.getAllStatusrec();
    model.addAttribute("status", status);
    model.addAttribute("motifRec", recMotif);
    return "reclamation/revendeur-commercial";
  }



  @GetMapping(value = "allDistTechReclamations/{pageNo}")
  public String allDistTechReclamations(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE-RECLAMATION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ-RECLAMATION-ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ-RECLAMATION-AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      model.addAttribute("AffectedTo", User);
      Servicetype serviceTechnique = servicetypeService.getServicetypeByCategory("Technique");
      List<Motifrec> recMotif = motifrecService
          .findMotifsByServiceType(serviceTechnique.getServicetypeId(), "chefsecteur");
      List<Statusrec> status = statusrecService.getAllStatusrec();
      model.addAttribute("status", status);
      model.addAttribute("motifRec", recMotif);
    }

    return "reclamation/distributeur-technique";
  }



  @GetMapping(value = "allDistComReclamations/{pageNo}")
  public String allDistComReclamations(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE-RECLAMATION");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ-RECLAMATION-ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ-RECLAMATION-AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      Servicetype serviceTechnique = servicetypeService.getServicetypeByCategory("Commercial");
      List<Motifrec> recMotif = motifrecService
          .findMotifsByServiceType(serviceTechnique.getServicetypeId(), "chefsecteur");
      List<Statusrec> status = statusrecService.getAllStatusrec();
      model.addAttribute("status", status);
      model.addAttribute("motifRec", recMotif);
      model.addAttribute("AffectedTo", User);
    }

    return "reclamation/distributeur-commercial";
  }

  @GetMapping("/motifs/{serviceTypeId}/{category}")
  @ResponseBody
  public List<Motifrec> getMotifsByServiceType(@PathVariable Long serviceTypeId,
      @PathVariable String category) {
    List<Motifrec> lismotifs = motifrecService.findMotifsByServiceType(serviceTypeId, category);
    return lismotifs;
  }

  @GetMapping("/clients/{category}")
  @ResponseBody
  public List<Map<String, Object>> getClientsByCategory(@PathVariable String category) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User currentUserObj = userRepository.findUsersByEmail(currentUser);

      List<Map<String, Object>> userList = new ArrayList<>();
      List<String> stringsRole =
          currentUserObj.getRole().getStringsRole(currentUserObj.getRole().getPrivileges());

      if (category.equals("Client")) {
        return abonnementRepository.getAllClientForReclamation();
      } else if (category.equals("Revendeur")) {

        List<User> users = new ArrayList<>();
        // coté administrateur
        if (stringsRole.contains("READ-RECLAMATION-ALL")) {
          users = userRepository.findUsersByTypeUser("REVENDEUR");
          // coté chef secteur
        } else if (stringsRole.contains("READ-RECLAMATION-AREA")) {
          users = userRepository.findUsersByAffectedTo(currentUserObj.getUserid());
          // coté revendeur
        } else {
          users.add(currentUserObj);
        }

        userList = users.stream().map(u -> {
          Map<String, Object> map = new HashMap<>();
          map.put("firstName", u.getFirstName());
          map.put("lastName", u.getLastName());
          map.put("clientId", u.getUserid());
          map.put("codeUser", u.getCodeUser());
          return map;
        }).collect(Collectors.toList());
      } else if (category.equals("chefsecteur")) {
        // coté administrateur
        List<User> users = new ArrayList<>();

        if (stringsRole.contains("READ-RECLAMATION-ALL")) {
          users = userRepository.findUsersByTypeUser("DISTRIBUTEUR");
          // coté chef secteur
        } else {
          users.add(currentUserObj);
        }
        userList = users.stream().map(u -> {
          Map<String, Object> map = new HashMap<>();
          map.put("firstName", u.getFirstName());
          map.put("lastName", u.getLastName());
          map.put("clientId", u.getUserid());
          map.put("codeUser", u.getCodeUser());

          return map;
        }).collect(Collectors.toList());
      }

      return userList;
    }
    return null;
  }

  @GetMapping("/getReclamation/{id}")
  public String viewReclamation(@PathVariable Long id, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE-RECLAMATION");
      Boolean canSeeMycomplaints = StringsRole.contains("CREATE_INTERVENTION_AGENT");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("canSeeMycomplaints", canSeeMycomplaints);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      Reclamation reclamation = reclamationRepository.findByIdWithJustificatifs(id).get();
      Abonnement abonnement = null;
      User userDetail = null;
      if (reclamation.getCategory().equals("Client")) {
        abonnement = abonnementRepository
            .findAbonnementByReferenceClient(reclamation.getClient().getReferenceClient());
      } else {
        userDetail = userRepository.findUsersByUserid(reclamation.getUser().getUserid());
      }
      List<AllClientHistory> reclamationHistory =
          reclamationHistoryService.reclamationshis(reclamation.getRef_reclamation());
      List<Statusrec> statusList = statusrecService.getAllStatusrec();
      model.addAttribute("statusList", statusList);
      model.addAttribute("reclamationHistory", reclamationHistory);
      model.addAttribute("user", userDetail);
      model.addAttribute("abonnement", abonnement);
      model.addAttribute("reclamation", reclamation);
    }
    return "reclamation/view";

  }

  @RequestMapping(method = RequestMethod.POST, value = "addComment")
  public String addComment(@RequestParam("reclamationid") Long reclamationid,
      @RequestParam("cometaire") String Comment, RedirectAttributes redirectAttrs) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        reclamationHistoryService.saveNewHistorique(user, reclamationid, Comment);
        redirectAttrs.addFlashAttribute("message", "comentaireAjouter");
      }

    } catch (Exception e) {
      LOGGER.error(" reclamationController.addComment Error:", e.getMessage());
      redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    }

    return "redirect:/reclamations/getReclamation/" + reclamationid;

  }

  @PreAuthorize("hasAuthority('CREATE_INTERVENTION')")
  @RequestMapping(method = RequestMethod.POST, value = "addIntervention")
  public String addIntervention(@RequestParam("reclamationid") Long reclamationid,
      @RequestParam("status") String status, @RequestParam("description") String description,
      RedirectAttributes redirectAttrs) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        Reclamation oldReclamation = reclamationRepository.findById(reclamationid).get();
        Statusrec statut = statusrecRepository.findByNomStatut(status);

        reclamationHistoryService.saveNewHistorique(user, reclamationid,
            description + ", avec changement de statut de "
                + oldReclamation.getStatus().getNomStatut() + " à " + statut.getNomStatut());
        oldReclamation.setStatus(statut);
        reclamationService.saveReclamation(oldReclamation);
        redirectAttrs.addFlashAttribute("message", "interventionAjouter");
        // send sms to client

        String statutCheck = oldReclamation.getStatus().getNomStatut();

        if (statutCheck != null && oldReclamation.getCategory().equals("Client")
            && (statutCheck.equalsIgnoreCase(NomStatutReclamation.Clôturée)
                || statutCheck.equalsIgnoreCase(NomStatutReclamation.OPENED)
                || statutCheck.equalsIgnoreCase(NomStatutReclamation.RELANCEE)
                || statutCheck.equalsIgnoreCase(NomStatutReclamation.SAVED)
                || statutCheck.equalsIgnoreCase(NomStatutReclamation.IN_PROGRESS))) {
          notificationservice.sendsmsToClient(oldReclamation,
              oldReclamation.getClient().getTelMobile().toString());
        }
      }
    } catch (Exception e) {
      LOGGER.error(" reclamationController.addIntervention Error:", e.getMessage());
      redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    }

    return "redirect:/reclamations/getReclamation/" + reclamationid;

  }

  @PreAuthorize("hasAuthority('CREATE_INTERVENTION_AGENT')")
  @RequestMapping(method = RequestMethod.POST, value = "addInterventionTech")
  public String addInterventionTech(@RequestParam("reclamationid") Long reclamationid,
      @RequestParam("status") String status, @RequestParam("description") String description,
      RedirectAttributes redirectAttrs) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        Reclamation oldReclamation = reclamationRepository.findById(reclamationid).get();
        reclamationHistoryService.saveNewHistorique(user, reclamationid, description
            + ", avec changement de statut de " + oldReclamation.getStatuttech() + " à " + status);
        oldReclamation.setStatuttech(status);
        reclamationService.saveReclamation(oldReclamation);
        redirectAttrs.addFlashAttribute("message", "interventionAjouter");
      }
    } catch (Exception e) {
      LOGGER.error(" reclamationController.addIntervention Error:", e.getMessage());
      redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    }

    return "redirect:/reclamations/getReclamation/" + reclamationid;

  }

  @PreAuthorize("hasAuthority('CREATE_REFERENCE_TT')")
  @RequestMapping(method = RequestMethod.POST, value = "addreferencett")
  public String addreferencett(@RequestParam("reclamationid") Long reclamationid,
      @RequestParam("state") String state, @RequestParam("referencett") String referencett,
      @RequestParam(value = "date_reclamationtt", required = false) String date_reclamationtt,
      @RequestParam(value = "date_etattt", required = false) String date_etattt,
      @RequestParam(value = "date_verificationfsi", required = false) String date_verificationfsi,
      @RequestParam(value = "etattt", required = false) String etattt,
      RedirectAttributes redirectAttrs) {

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {

        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        Reclamation oldReclamation = reclamationRepository.findById(reclamationid).orElse(null);

        if (oldReclamation == null) {
          redirectAttrs.addFlashAttribute("message", "reclamationNotFound");
          return "redirect:/reclamations/allclientreclamationTech/1/20";
        }

        Reclamation existing = reclamationRepository.findByReferencett(referencett);
        if (existing != null && !existing.getReclamationid().equals(reclamationid)) {
          redirectAttrs.addFlashAttribute("message", "referencettExistDeja");
          return "redirect:/reclamations/getReclamation/" + reclamationid;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (date_reclamationtt != null && !date_reclamationtt.isEmpty()) {
          oldReclamation.setDate_reclamationtt(formatter.parse(date_reclamationtt + " 00:05"));

        }
        if (date_etattt != null && !date_etattt.isEmpty()) {
          oldReclamation.setDate_etattt(formatter.parse(date_etattt + " 00:05"));
        }
        if (date_verificationfsi != null && !date_verificationfsi.isEmpty()) {
          oldReclamation.setDate_verificationfsi(formatter.parse(date_verificationfsi + " 00:05"));
        }
        if (etattt != null && !etattt.isEmpty()) {
          oldReclamation.setEtattt(etattt);
          Statusrec statut = statusrecService.getStatusrecByName(etattt);
          oldReclamation.setStatus(statut);
        }
        String description;
        if ("new".equalsIgnoreCase(state)) {
          description = "Insertion d'une nouvelle référence TT : " + referencett + " avec statut "
              + etattt + " avec date etat TT " + date_etattt + " ,date de réclamation TT "
              + date_reclamationtt + " et date de vérification FSI" + date_verificationfsi;
          redirectAttrs.addFlashAttribute("message", "referencettAjouter");
        } else {
          description = "Changement de référence TT de : " + oldReclamation.getReferencett()
              + " à une nouvelle : " + referencett + " et changement de statut à " + etattt
              + " avec date etat TT " + date_etattt + " ,date de réclamation TT "
              + date_reclamationtt + " et date de vérification FSI" + date_verificationfsi;;
          redirectAttrs.addFlashAttribute("message", "referencettModifier");
        }

        oldReclamation.setReferencett(referencett);
        reclamationService.saveReclamation(oldReclamation);

        reclamationHistoryService.saveNewHistorique(user, reclamationid, description);

        String statutCheck = oldReclamation.getStatus().getNomStatut();

        if (statutCheck != null && oldReclamation.getCategory().equals("Client")
            && (statutCheck.equalsIgnoreCase(NomStatutReclamation.Clôturée)
                || statutCheck.equalsIgnoreCase(NomStatutReclamation.OPENED)
                || statutCheck.equalsIgnoreCase(NomStatutReclamation.SAVED)
                || statutCheck.equalsIgnoreCase(NomStatutReclamation.RELANCEE)
                || statutCheck.equalsIgnoreCase(NomStatutReclamation.IN_PROGRESS))) {

          notificationservice.sendsmsToClient(oldReclamation,
              oldReclamation.getClient().getTelMobile().toString());
        }

      }

    } catch (Exception e) {
      LOGGER.error("ReclamationController.addreferencett Error: ", e);
      redirectAttrs.addFlashAttribute("message", "paramaterManquante");
    }

    return "redirect:/reclamations/getReclamation/" + reclamationid;
  }


  @GetMapping("/editreclamation/{reclamationid}")
  public String showEditReclamationForm(@PathVariable Long reclamationid, Model model) {

    Reclamation reclamation = reclamationService.getReclamationById(reclamationid);
    model.addAttribute("reclamation", reclamation);
    List<Statusrec> statusList = statusrecService.getAllStatusrec();
    List<Servicetype> servicetypeList = servicetypeService.getAllServicetypes();
    List<Motifrec> motifList = motifrecService.getAllMotifrec();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean isRevendeur = user.getTypeUser().equals(UserTypeConstant.REVENDEUR);
      Boolean isDistributeur = user.getTypeUser().equals(UserTypeConstant.DISTRIBUTEUR);
      Boolean isAdmin = StringsRole.contains("READ-RECLAMATION-ALL");
      model.addAttribute("isRevendeur", isRevendeur);
      model.addAttribute("isDistributeur", isDistributeur);
      model.addAttribute("isAdmin", isAdmin);
    }

    model.addAttribute("statusList", statusList);
    model.addAttribute("servicetypeList", servicetypeList);
    model.addAttribute("motifList", motifList);
    return "reclamation/edit";
  }

  @PreAuthorize("hasAuthority('UPDATE-RECLAMATION')")
  @RequestMapping(method = RequestMethod.POST, value = "/editreclamation/{reclamationid}")
  public String editreclamationPost(@PathVariable Long reclamationid,
      @ModelAttribute("reclamation") Reclamation reclamation,
      @RequestParam("servicetypeId") Long servicetypeId,
      @RequestParam(value = "motifId", required = false) Long motifId,
      @RequestParam(value = "clientId", required = false) Long clientId,
      @RequestParam(value = "justificatif", required = false) MultipartFile[] justificatifFiles,
      Authentication authentication, RedirectAttributes redirectAttributes) {
    Reclamation oldreclamation = reclamationRepository.findById(reclamationid).get();
    StringBuilder historyDesc = new StringBuilder();
    historyDesc.append("Modification de la réclamation ")
        .append(oldreclamation.getRef_reclamation()).append(" : ");

    Servicetype serviceType = servicetypeService.findbyServicetypeId(servicetypeId);
    if (serviceType != null && !serviceType.getServicetypeId()
        .equals(oldreclamation.getServiceType().getServicetypeId())) {

      historyDesc.append("ServiceType [").append(oldreclamation.getServiceType().getCategorytype())
          .append(" à ").append(serviceType.getCategorytype()).append("], ");
    }

    Motifrec oldMotif = oldreclamation.getMotif();

    Motifrec motif = null;
    if (motifId != null) {
      motif = motifrecService.findById(motifId);
    }

    if (motif != null && (oldMotif == null || !motif.getMotifId().equals(oldMotif.getMotifId()))) {

      historyDesc.append("Motif [").append(oldMotif != null ? oldMotif.getNomMotif() : "Aucun")
          .append(" à ").append(motif.getNomMotif()).append("], ");
    }

    // AFTER comparison
    oldreclamation.setMotif(motif);
    String currentUser = authentication.getName();
    User userconnected = userRepository.findUsersByEmail(currentUser);
    if (clientId != null) {
      if (reclamation.getCategory().equals("Client")) {
        if (oldreclamation.getClient() == null
            || !oldreclamation.getClient().getClientid().equals(clientId)) {

          historyDesc.append("Client changé, ");
        }
        Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientId);
        oldreclamation.setClient(abonnement);
        oldreclamation.setUser(null);
      } else {
        if (oldreclamation.getUser() == null
            || !oldreclamation.getUser().getUserid().equals(clientId)) {

          historyDesc.append("Utilisateur changé, ");
        }
        User user = userRepository.findById(clientId).orElse(null);
        oldreclamation.setUser(user);
        oldreclamation.setClient(null);
      }
    } else {
      oldreclamation.setUser(userconnected);
      oldreclamation.setClient(null);
    }
    if (serviceType != null) {
      if (!serviceType.getCategorytype().equals(oldreclamation.getServiceType().getCategorytype())
          && serviceType.getCategorytype().equals("Technique")
          && reclamation.getCategory().equals("Client")
          && !oldreclamation.getStatus().getNomStatut().equals(DBEtatTT.Clôturée)) {
        Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientId);
        Reclamation VerifyReclamationTechexist =
            reclamationRepository.findlastreclamationByclientIdServiceAndCategory("Technique",
                "Client", abonnement.getClientid());

        if (VerifyReclamationTechexist != null
            && !VerifyReclamationTechexist.getStatus().getNomStatut().equals(DBEtatTT.Clôturée)
            && VerifyReclamationTechexist.getCategory().equals("Client")
            && VerifyReclamationTechexist.getServiceType().getCategorytype().equals("Technique")
            && serviceType.getCategorytype().equals("Technique")) {
          redirectAttributes.addFlashAttribute("message",
              "Vous avez une autre réclamation technique encours");
          return "redirect:/reclamations/editreclamation/" + oldreclamation.getReclamationid();
        }

      }
      if (!Objects.equals(oldreclamation.getCategory(), reclamation.getCategory())) {
        historyDesc.append("Catégorie [").append(oldreclamation.getCategory()).append(" à ")
            .append(reclamation.getCategory()).append("], ");
      }
      if (!Objects.equals(oldreclamation.getDescription(), reclamation.getDescription())) {
        historyDesc.append("Description modifiée, ");
      }
      oldreclamation.setServiceType(serviceType);
      oldreclamation.setMotif(motif);
      oldreclamation.setCategory(reclamation.getCategory());
      oldreclamation.setAutre(reclamation.getAutre());
      boolean hasJustificatifs = justificatifFiles != null && justificatifFiles.length > 0
          && Arrays.stream(justificatifFiles).anyMatch(file -> file != null && !file.isEmpty());
      if (hasJustificatifs) {
        historyDesc.append("Ajout de justificatifs, ");
        List<String> justificatifFileNames = new ArrayList<>();
        for (MultipartFile justificatifFile : justificatifFiles) {
          if (justificatifFile != null && !justificatifFile.isEmpty()) {
            try {
              String caractaireDestingtion = "reclamation_justification_";
              String fileName = caractaireDestingtion + System.currentTimeMillis() + "_"
                  + CrmUtils.noSpecialCharacters(justificatifFile.getOriginalFilename());
              CrmUtils.saveImageReclamation(justificatifFile, "", pathReclamation, fileName);
              justificatifFileNames.add(fileName);
            } catch (Exception e) {
              LOGGER.error("File upload exception: " + e.getMessage());
              redirectAttributes.addFlashAttribute("message",
                  "Failed to upload one or more justificatif files.");
              return "redirect:/reclamations/editreclamation/" + oldreclamation.getReclamationid();
            }
            oldreclamation.setJustificatifs(justificatifFileNames);
          }
        }
      }
      oldreclamation.setDescription(reclamation.getDescription());
      oldreclamation.setEditedby(userconnected);
      reclamationService.saveReclamation(oldreclamation);
      redirectAttributes.addFlashAttribute("message", "Reclamationmodif");
      String finalDescription = historyDesc.toString();
      if (finalDescription.endsWith(", ")) {
        finalDescription = finalDescription.substring(0, finalDescription.length() - 2);
      }
      reclamationHistoryService.saveNewHistorique(userconnected, reclamationid, finalDescription);
      switch (oldreclamation.getCategory()) {
        case "Client":
          if (oldreclamation.getServiceType().getCategorytype().equals("Technique")) {
            return "redirect:/reclamations/allclientreclamationTech/1";
          } else if (oldreclamation.getServiceType().getCategorytype().equals("TechniqueFSI")) {
            return "redirect:/reclamations/allclientreclamationTechniqueFSI/1";
          } else {
            return "redirect:/reclamations/allclientreclamationCommercial/1";
          }

        case "Revendeur":
          if (oldreclamation.getServiceType().getCategorytype().equals("Technique")) {
            return "redirect:/reclamations/allRevTechReclamations/1";
          } else {
            return "redirect:/reclamations/allRevComReclamations/1";
          }

        case "chefsecteur":
          if (oldreclamation.getServiceType().getCategorytype().equals("Technique")) {
            return "redirect:/reclamations/allDistTechReclamations/1";
          } else {
            return "redirect:/reclamations/allDistComReclamations/1";
          }

        default:
          return "redirect:/reclamations/editreclamation/" + oldreclamation.getReclamationid();
      }

      /* return "redirect:/reclamations/editreclamation/" + oldreclamation.getReclamationid(); */
    } else {
      redirectAttributes.addFlashAttribute("message",
          "Failed to upload one or more justificatif files.");
      return "reclamation/editreclamation/" + oldreclamation.getReclamationid();
    }
  }


  @GetMapping("/{id}/delete")
  public String deleteReclamation(@PathVariable Long id) {
    reclamationService.deleteReclamation(id);
    return "redirect:/reclamations";
  }

  @ResponseBody
  @PostMapping(value = "/addid")
  public JsonResponseBody addfiled(@RequestBody Long id, HttpServletRequest request) {
    return reclamationService.addFiled(id, request);
  }

  @ResponseBody
  @PostMapping(value = "/removeid")
  public JsonResponseBody removefiled(@RequestBody Long id, HttpServletRequest request) {
    return reclamationService.removeFiled(id, request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "assignerReclamationMultiple")
  public String assignerDemandeMultiple(@RequestParam("reclamationid") String listeReclamationId,
      @RequestParam("agentId") Long agentId, @RequestParam("type") String type,
      @RequestParam("categorie") String categorie, HttpServletRequest request,
      RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String result = null;

    if (authentication instanceof AnonymousAuthenticationToken) {
      return "redirect:/login";
    }

    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    User agent = userRepository.findByUserId(agentId);
    List<String> listeReclamationsId =
        new ArrayList<String>(Arrays.asList(listeReclamationId.split(",")));

    switch (categorie) {
      case "Client":
        if ("Technique".equals(type)) {
          result = "redirect:/reclamations/allclientreclamationTech/1";
        } else if ("Commercial".equals(type)) {
          result = "redirect:/reclamations/allclientreclamationCommercial/1";
        } else {
          result = "redirect:/reclamations/allclientreclamationTechniqueFSI/1";
        }
        break;

      case "Revendeur":
        result = type.equals("Technique") ? "redirect:/reclamations/allRevTechReclamations/1"
            : "redirect:/reclamations/allRevComReclamations/1";
        break;

      case "chefsecteur":
        result = type.equals("Technique") ? "redirect:/reclamations/allDistTechReclamations/1"
            : "redirect:/reclamations/allDistComReclamations/1";
        break;

    }
    if (listeReclamationsId.size() >= 501) {
      redirectAttrs.addFlashAttribute("message", "assignementMaxSizeEreur");
      request.getSession().removeAttribute("listedes_ids_reclamations");
      return result;
    }

    List<Reclamation> reclamations =
        reclamationService.findReclamationByListeReclamationId(listeReclamationsId);

    if (agent != null && reclamations != null && !reclamations.isEmpty()) {

      reclamations.forEach(reclamation -> {
        String message = (reclamation.getTreatedBy() == null)
            ? "La réclamation a été assignée à l'agent : " + agent.getFirstName() + " "
                + agent.getLastName()
            : "L'assignation de la réclamation a été changée à : " + agent.getFirstName() + " "
                + agent.getLastName();

        reclamationHistoryService.saveNewHistorique(user, reclamation.getReclamationid(), message);

        reclamation.setTreatedBy(agent);
        reclamationRepository.save(reclamation);
      });

      request.getSession().removeAttribute("listedes_ids_reclamations");
      redirectAttrs.addFlashAttribute("message", "assignementAvecSuccess");
    }

    return result;
  }


  @ResponseBody
  @PostMapping(value = "/addall/{type}/{category}")
  public List<Long> addAllIdToExport(@RequestBody String filterrecherche,
      HttpServletRequest request, @PathVariable String type, @PathVariable String category) {
    return reclamationService.addAllIdReclamation(filterrecherche, request, type, category);
  }

  @ResponseBody
  @PostMapping(value = "/removeall")
  public JsonResponseBody removeAllFromListExport(HttpServletRequest request) {
    return reclamationService.removeAllFromListReclamation(request);
  }

  @RequestMapping(method = RequestMethod.POST, value = "uploadreclamationttenmasse")
  public String uploadeditabonnementenmasse(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttrs, HttpServletResponse response)
      throws JRException, IOException {
    importExcel.uploadReclamationsTTenmasse(file, redirectAttrs, response);
    return "redirect:/reclamations/allclientreclamationTech/" + 1;
  }

  // add reclamation search by client
  @GetMapping("/search-client")
  @ResponseBody
  public Map<String, Object> searchClient(@RequestParam String type, @RequestParam String value) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Map<String, Object> response = new HashMap<>();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      Abonnement abonnement = null;

      if ("cin".equals(type)) {
        abonnement = abonnementRepository.findAbonnementByCin(value);
      } else {
        abonnement =
            abonnementRepository.findTopByTelFixeOrderByCreatedDateDesc(Long.valueOf(value));
      }

      if (abonnement != null) {
        // check if client already has claim in progress
        Long ref_client = abonnement.getClientid();
        Reclamation reclamation = reclamationRepository.findlastreclamationByclientId(ref_client);
        String etatConnection = "indisponible";
        List<Radacct> sessionConnected = null;
        if (abonnement.getLoginModem() != null) {
          try {
            Radcheck infoRadus = radcheckService.getRadchecksByUsernameAndAttribute(
                abonnement.getLoginModem(), RedchekConstant.Expiration);
            response.put("infoRadus", infoRadus);
          } catch (Exception e) {

            response.put("infoRadus", "non connecté");
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
            response.put("etatConnection", etatConnection);

          } catch (Exception e) {
            LOGGER.error("infoRadus date d'expiration : " + e);

          }
        } else {
          response.put("etatConnection", etatConnection);
        }
        Reclamation reclamationold = reclamationRepository
            .findlastreclamationByclientIdServiceAndCategory("Technique", "Client", ref_client);
        if (reclamationold != null
            && !reclamationold.getStatus().getNomStatut().equals(DBEtatTT.Clôturée)
            && reclamationold.getCategory().equals("Client")
            && reclamationold.getServiceType().getCategorytype().equals("Technique")) {
          response.put("success", false);
          response.put("message", "Vous avez une autre réclamation technique encours.");
          response.put("reclamationid", reclamationold.getReclamationid());
          response.put("reclamationRef", reclamationold.getRef_reclamation());
          response.put("clientId", abonnement.getClientid());
          response.put("firstName", abonnement.getFirstName());
          response.put("lastName", abonnement.getLastName());
          response.put("cin", abonnement.getCin());
          response.put("telFixe", abonnement.getTelFixe());
          response.put("statut",
              abonnement.getStatut() != null ? abonnement.getStatut().getDesignation()
                  : "Non payé");

        } else {

          response.put("success", true);
          response.put("clientId", abonnement.getClientid());
          response.put("firstName", abonnement.getFirstName());
          response.put("lastName", abonnement.getLastName());
          response.put("cin", abonnement.getCin());
          response.put("telFixe", abonnement.getTelFixe());
          response.put("statut",
              abonnement.getStatut() != null ? abonnement.getStatut().getDesignation()
                  : "Non payé");

        }
      } else {
        response.put("success", false);
        response.put("message", "Aucun client trouvé");
      }
    }

    return response;
  }

  @PostMapping("/export")
  public void exportReclamations(@RequestParam("reclamationids") String reclamationIds,
      HttpServletResponse response) throws IOException {

    if (reclamationIds == null || reclamationIds.isEmpty()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "Aucune réclamation sélectionnée pour l'export");
      return;
    }

    List<Long> ids =
        Arrays.stream(reclamationIds.split(",")).map(Long::parseLong).collect(Collectors.toList());

    if (ids.size() > 1000) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "Vous avez dépassé le nombre maximum de lignes exportables (1000).");
      return;
    }

    exportReclamationService.exportReclamations(ids, response);
  }

  @PreAuthorize("hasAuthority('UPDATE-RECLAMATION_AGENT')")
  @GetMapping("/editreclamationAgent/{reclamationid}")
  public String showEditReclamationFormAgent(@PathVariable Long reclamationid, Model model) {

    Reclamation reclamation = reclamationService.getReclamationById(reclamationid);
    model.addAttribute("reclamation", reclamation);
    List<Statusrec> statusList = statusrecService.getAllStatusrec();
    List<Servicetype> servicetypeList = servicetypeService.getAllServicetypes();
    List<Motifrec> motifList = motifrecService.getAllMotifrec();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean isAdmin = StringsRole.contains("UPDATE-RECLAMATION_AGENT");
      model.addAttribute("isAdmin", isAdmin);
    }

    model.addAttribute("statusList", statusList);
    model.addAttribute("servicetypeList", servicetypeList);
    model.addAttribute("motifList", motifList);
    return "reclamation/editAgent";
  }

  @PreAuthorize("hasAuthority('UPDATE-RECLAMATION_AGENT')")
  @RequestMapping(method = RequestMethod.POST, value = "/editreclamationAgent/{reclamationid}")
  public String UPDATERECLAMATIONAGENT(@PathVariable Long reclamationid,
      @ModelAttribute("reclamation") Reclamation reclamation,
      @RequestParam("servicetypeId") Long servicetypeId,
      @RequestParam(value = "motifId", required = false) Long motifId,
      @RequestParam(value = "clientId", required = false) Long clientId,
      @RequestParam(value = "justificatif", required = false) MultipartFile[] justificatifFiles,
      Authentication authentication, RedirectAttributes redirectAttributes) {
    Reclamation oldreclamation = reclamationRepository.findById(reclamationid).get();
    StringBuilder historyDesc = new StringBuilder();
    historyDesc.append("Modification de la réclamation ")
        .append(oldreclamation.getRef_reclamation()).append(" : ");

    Servicetype serviceType = servicetypeService.findbyServicetypeId(servicetypeId);
    if (serviceType != null && !serviceType.getServicetypeId()
        .equals(oldreclamation.getServiceType().getServicetypeId())) {

      historyDesc.append("ServiceType [").append(oldreclamation.getServiceType().getCategorytype())
          .append(" à ").append(serviceType.getCategorytype()).append("], ");
    }

    Motifrec oldMotif = oldreclamation.getMotif();

    Motifrec motif = null;
    if (motifId != null) {
      motif = motifrecService.findById(motifId);
    }

    if (motif != null && (oldMotif == null || !motif.getMotifId().equals(oldMotif.getMotifId()))) {

      historyDesc.append("Motif [").append(oldMotif != null ? oldMotif.getNomMotif() : "Aucun")
          .append(" à ").append(motif.getNomMotif()).append("], ");
    }

    oldreclamation.setMotif(motif);
    String currentUser = authentication.getName();
    User userconnected = userRepository.findUsersByEmail(currentUser);
    if (clientId != null) {
      if (reclamation.getCategory().equals("Client")) {
        if (oldreclamation.getClient() == null
            || !oldreclamation.getClient().getClientid().equals(clientId)) {

          historyDesc.append("Client changé, ");
        }
        Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientId);
        oldreclamation.setClient(abonnement);
        oldreclamation.setUser(null);
      } else {
        if (oldreclamation.getUser() == null
            || !oldreclamation.getUser().getUserid().equals(clientId)) {

          historyDesc.append("Utilisateur changé, ");
        }
        User user = userRepository.findById(clientId).orElse(null);
        oldreclamation.setUser(user);
        oldreclamation.setClient(null);
      }
    } else {
      oldreclamation.setUser(userconnected);
      oldreclamation.setClient(null);
    }
    if (serviceType != null) {
      if (!serviceType.getCategorytype().equals(oldreclamation.getServiceType().getCategorytype())
          && serviceType.getCategorytype().equals("Technique")
          && reclamation.getCategory().equals("Client")
          && !oldreclamation.getStatus().getNomStatut().equals(DBEtatTT.Clôturée)) {
        Abonnement abonnement = abonnementRepository.findAbonnementByClientid(clientId);
        Reclamation VerifyReclamationTechexist =
            reclamationRepository.findlastreclamationByclientIdServiceAndCategory("Technique",
                "Client", abonnement.getClientid());

        if (VerifyReclamationTechexist != null
            && !VerifyReclamationTechexist.getStatus().getNomStatut().equals(DBEtatTT.Clôturée)
            && VerifyReclamationTechexist.getCategory().equals("Client")
            && VerifyReclamationTechexist.getServiceType().getCategorytype().equals("Technique")
            && serviceType.getCategorytype().equals("Technique")) {
          redirectAttributes.addFlashAttribute("message",
              "Vous avez une autre réclamation technique encours");
          return "redirect:/reclamations/editreclamationAgent/" + oldreclamation.getReclamationid();
        }

      }
      if (!Objects.equals(oldreclamation.getCategory(), reclamation.getCategory())) {
        historyDesc.append("Catégorie [").append(oldreclamation.getCategory()).append(" à ")
            .append(reclamation.getCategory()).append("], ");
      }
      if (!Objects.equals(oldreclamation.getDescription(), reclamation.getDescription())) {
        historyDesc.append("Description modifiée, ");
      }
      oldreclamation.setServiceType(serviceType);
      oldreclamation.setMotif(motif);
      oldreclamation.setCategory(reclamation.getCategory());
      oldreclamation.setAutre(reclamation.getAutre());
      boolean hasJustificatifs = justificatifFiles != null && justificatifFiles.length > 0
          && Arrays.stream(justificatifFiles).anyMatch(file -> file != null && !file.isEmpty());
      if (hasJustificatifs) {
        historyDesc.append("Ajout de justificatifs, ");
        List<String> justificatifFileNames = new ArrayList<>();
        for (MultipartFile justificatifFile : justificatifFiles) {
          if (justificatifFile != null && !justificatifFile.isEmpty()) {
            try {
              String caractaireDestingtion = "reclamation_justification_";
              String fileName = caractaireDestingtion + System.currentTimeMillis() + "_"
                  + CrmUtils.noSpecialCharacters(justificatifFile.getOriginalFilename());
              CrmUtils.saveImageReclamation(justificatifFile, "", pathReclamation, fileName);
              justificatifFileNames.add(fileName);
            } catch (Exception e) {
              LOGGER.error("File upload exception: " + e.getMessage());
              redirectAttributes.addFlashAttribute("message",
                  "Failed to upload one or more justificatif files.");
              return "redirect:/reclamations/editreclamationAgent/"
                  + oldreclamation.getReclamationid();
            }
            oldreclamation.setJustificatifs(justificatifFileNames);
          }
        }
      }
      oldreclamation.setDescription(reclamation.getDescription());
      oldreclamation.setEditedby(userconnected);
      reclamationService.saveReclamation(oldreclamation);
      redirectAttributes.addFlashAttribute("message", "Reclamationmodif");
      String finalDescription = historyDesc.toString();
      if (finalDescription.endsWith(", ")) {
        finalDescription = finalDescription.substring(0, finalDescription.length() - 2);
      }
      reclamationHistoryService.saveNewHistorique(userconnected, reclamationid, finalDescription);
      switch (oldreclamation.getCategory()) {
        case "Client":

          return "redirect:/reclamations/mesReclamations/1";
        default:
          return "redirect:/reclamations/mesReclamations/1";
      }

    } else {
      redirectAttributes.addFlashAttribute("message",
          "Failed to upload one or more justificatif files.");
      return "reclamation/editreclamationAgent/" + oldreclamation.getReclamationid();
    }
  }

}


