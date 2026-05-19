package crm.chifco.com.crmMobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.DemandeModem;
import crm.chifco.com.model.FicheStock;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.Motifrec;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.model.Servicetype;
import crm.chifco.com.model.Statusrec;
import crm.chifco.com.model.TypeVisite;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Visite;
import crm.chifco.com.radius.model.Radacct;
import crm.chifco.com.radius.model.Radcheck;
import crm.chifco.com.radius.service.RadcheckService;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.DemandeModemRepository;
import crm.chifco.com.repository.FicheRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.ReclamationRepository;
import crm.chifco.com.repository.StatusrecRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AbonnementHistoriqueService;
import crm.chifco.com.service.AffectService;
import crm.chifco.com.service.BordereauService;
import crm.chifco.com.service.ClientHistoryService;
import crm.chifco.com.service.DemandeAbonnementService;
import crm.chifco.com.service.FactureService;
import crm.chifco.com.service.FicheStockService;
import crm.chifco.com.service.MotifrecService;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.ReclamationHistoryService;
import crm.chifco.com.service.ReclamationService;
import crm.chifco.com.service.ServicetypeService;
import crm.chifco.com.service.StatusrecService;
import crm.chifco.com.service.TypeVisiteService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.service.VisiteService;
import crm.chifco.com.templateclasse.ModemAffectationFiches;
import crm.chifco.com.templateclasse.ModemDistributeur;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.HtmlTemplateEmail;
import crm.chifco.com.utils.NomStatutReclamation;
import crm.chifco.com.utils.RedchekConstant;
import crm.chifco.com.utils.UserTypeConstant;

@RestController
@RequestMapping("/mobileapp")
public class MobileAppController {

  @Autowired
  UserService userservice;
  @Autowired
  UserRepository userRepository;

  @Autowired
  DemandeAbonnementService demandeAbonnementService;
  @Autowired
  VisiteService visiteService;
  @Autowired
  TypeVisiteService typeVisiteService;

  @Autowired
  AbonnementRepository abonnementRepository;
  @Autowired
  DemandeAbonnementRepository demandeAbonnementRepository;
  @Autowired
  ClientHistoryService clientHistoryService;
  @Autowired
  BordereauService bordereauService;
  @Autowired
  private AbonnementHistoriqueService abonnementHistoriqueService;
  @Autowired
  private LogoutService logoutService;
  @Autowired
  private FactureService factureService;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private RadcheckService radcheckService;
  @Autowired
  private PasswordEncoder passwordEncoder;
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
  private StatusrecRepository statusrecRepository;
  @Autowired
  private ReclamationHistoryService reclamationHistoryService;
  @Autowired
  private ReclamationService reclamationService2;
  @Autowired
  private ModemRepository modemRepository;
  @Autowired
  private FicheRepository ficheRepository;
  @Autowired
  private FicheStockService ficheservice;
  @Autowired
  private AffectService affectservice;

  @Autowired
  private DemandeModemRepository DemandeModemRepository;


  @Value("${pathReclamation}")
  private String pathReclamation;
  private final Logger logger = LogManager.getLogger(this.getClass());

  @GetMapping("/chefsecteur-stats")
  public Map<String, Object> getRevendeurStats(@RequestParam(required = false) String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String phoneNumber = (String) authentication.getPrincipal();
        if (type == null) {
          return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires",
              null);
        }
        if (type.equals("DISTRIBUTEUR")) {
          User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
          if (user == null) {
            return createResponse(false, HttpStatus.NOT_FOUND,
                "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
          }

          return createResponse(true, HttpStatus.OK, "ok",
              userservice.getRevendeurStats(user.getUserid()));
        } else {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
              null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST,
            "MobileAppController.sendAffectationClient Error: in authentication", null);
      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.chefsecteur-stats Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @GetMapping("/revendeur-stats")
  public Map<String, Object> getDynamicQueryResult(@RequestParam(required = false) Long revId,
      @RequestParam(required = false) String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String phoneNumber = (String) authentication.getPrincipal();
        if (type == null) {
          return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires",
              null);
        }
        if (revId == null) {
          return createResponse(false, HttpStatus.NOT_FOUND, "Le champs revId est obligatoires",
              null);
        }
        if (type.equals("DISTRIBUTEUR")) {
          User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
          if (user == null) {
            return createResponse(false, HttpStatus.NOT_FOUND,
                "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
          }
          Map<String, Object> result =
              userservice.executeDynamicQuery(null, null, user.getUserid(), null, revId);
          return createResponse(true, HttpStatus.OK, "ok ", result);

        } else {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
              null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST,
            "MobileAppController.sendAffectationClient Error: in authentication", null);
      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.revendeur-stats Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @GetMapping("/List-revendeurs")
  public Map<String, Object> getDynamicQueryResult2(
      @RequestParam(required = false) Boolean isActive,
      @RequestParam(required = false) Boolean isNotActive,
      @RequestParam(required = false) Boolean retrograde,
      @RequestParam(required = false) String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String phoneNumber = (String) authentication.getPrincipal();
        if (type == null) {
          return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires",
              null);
        }
        if (type.equals("DISTRIBUTEUR")) {
          User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
          if (user == null) {
            return createResponse(false, HttpStatus.NOT_FOUND,
                "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
          }

          List<Map<String, Object>> result =
              userservice.executeDynamicQuery2(isActive, isNotActive, user.getUserid(), retrograde);
          return createResponse(true, HttpStatus.OK, "ok", result);
        } else {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
              null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST,
            "MobileAppController.sendAffectationClient Error: in authentication", null);
      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.List-revendeurs Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @GetMapping("/countDemandeAbonnement-by-month")
  public Map<String, Object> getDemandeAbonnementCountByMonth(@RequestParam("year") int year,
      @RequestParam(value = "revId", required = false) Long revId,
      @RequestParam(required = false) String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String phoneNumber = (String) authentication.getPrincipal();
        if (type == null) {
          return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires",
              null);
        }
        if (type.equals("DISTRIBUTEUR")) {
          User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
          if (user == null) {
            return createResponse(false, HttpStatus.NOT_FOUND,
                "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
          }

          List<Map<String, Object>> result = demandeAbonnementService
              .getFullYearDemandeAbonnementCounts(year, revId, user.getUserid());
          return createResponse(true, HttpStatus.OK, "ok", result);
        } else {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
              null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST,
            "MobileAppController.sendAffectationClient Error: in authentication", null);
      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.countDemandeAbonnement-by-month Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);

    }
  }

  @PostMapping("/refresh-token")
  public Map<String, Object> refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    return jwtService.refreshToken(request, response);
  }

  @PostMapping("/search-demande")
  public Map<String, Object> searchAbonnement(@RequestParam("value") String value,
      @RequestParam("searchBy") String searchBy, @RequestParam(required = false) String type) {
    String telfixe = null;
    String cin = null;
    String referenceNety = null;

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Erreur d'authentification", null);
      }

      String phoneNumber = (String) authentication.getPrincipal();
      if (type == null) {
        return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoire", null);
      }

      if (!type.equals("DISTRIBUTEUR")) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur.",
            null);
      }

      User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
      if (user == null) {
        return createResponse(false, HttpStatus.NOT_FOUND,
            "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
      }

      Map<String, Object> data = new HashMap<>();
      Map<String, Object> myDemandeAbonnement = null;

      switch (searchBy) {
        case "cin":
          cin = value;
          break;
        case "telfixe":
          telfixe = value;
          break;
        case "referenceNety":
          referenceNety = value;
          break;
        default:
          throw new IllegalArgumentException("Paramètre searchBy invalide");
      }

      myDemandeAbonnement =
          demandeAbonnementRepository.getDemandeAbonnementByCinByTelFixeByReferenceNety(
              user.getUserid(), cin, telfixe, referenceNety);

      if (myDemandeAbonnement == null || myDemandeAbonnement.isEmpty()) {
        return createResponse(false, HttpStatus.NOT_FOUND, "Aucune demande d'abonnement trouvée",
            null);
      }
      String etatConnection = "indisponible";
      Radcheck infoRadus = null;

      Abonnement ab = abonnementRepository
          .findAbonnementByReferenceClient(myDemandeAbonnement.get("reference_chifco").toString());
      List<Radacct> sessionConnected = null;

      if (ab != null) {

        data.put("clientid", ab.getClientid());
        if (ab.getLoginModem() != null) {
          try {
            infoRadus = radcheckService.getRadchecksByUsernameAndAttribute(ab.getLoginModem(),
                RedchekConstant.Expiration);
          } catch (Exception e) {
            logger.error("Erreur lors de la récupération des informations de Radus : ", e);
          }
          try {
            List<Radacct> chekIfconnected =
                radcheckService.getRadacctConnection(ab.getLoginModem());
            sessionConnected =
                radcheckService.findSessionByUsername(ab.getCreatedDate(), ab.getLoginModem());
            if (chekIfconnected != null && !chekIfconnected.isEmpty()) {
              etatConnection = "connecté";
            } else {
              etatConnection = "non connecté";
            }

          } catch (Exception e) {
            logger.error("infoRadus date d'expiration : " + e);

          }
        }
      } else {
        data.put("clientid", null);
      }
      data.put("etatConnection", etatConnection);
      data.put("demandeAbonnement", myDemandeAbonnement);
      data.put("infoRadus", infoRadus);
      return createResponse(true, HttpStatus.OK, "Demande abonnement trouvée", data);

    } catch (Exception e) {
      logger.error("Erreur dans search-demande: ", e);
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }



  @RequestMapping(method = RequestMethod.GET, value = "affectationClient")
  public Map<String, Object> affectationClients(
      @RequestBody HashMap<String, String> affectationClient) {
    try {
      List<User> userListe = null;
      List<UserDtoApp> userDtoList = new ArrayList<>();
      String type = affectationClient.get("type");
      if (type == null) {
        return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires", null);
      }
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String phoneNumber = (String) authentication.getPrincipal();
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        userListe = userservice
            .affectRevendeurgetListeRevendeur(affectationClient.get("recherche").trim(), user);
        userDtoList = userListe.stream().map(UserDtoApp::fromEntity).collect(Collectors.toList());
        return createResponse(true, HttpStatus.OK, "Liste des revendeurs ", userDtoList);
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST,
            "MobileAppController.affectationClient Error: in authentication", null);
      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.affectationClient Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @RequestMapping(method = RequestMethod.POST, value = "sendAffectationClient")
  public Map<String, Object> submitAffectationClients(@RequestParam("demandeId") Long demandeId,
      @RequestParam(value = "codeRevendeur", required = false) String codeRevendeur,
      @RequestParam(value = "emailRevendeur", required = false) String emailRevendeur,
      @RequestParam(value = "identificationFiscale", required = false) String identificationFiscale,
      @RequestParam(required = false) String type) {
    Boolean isAffectedUser = false;

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String phoneNumber = (String) authentication.getPrincipal();
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
        if (type == null) {
          return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires",
              null);
        }
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        isAffectedUser = demandeAbonnementService.affectRevendeur(demandeId, codeRevendeur,
            emailRevendeur, identificationFiscale);
        if (isAffectedUser) {
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
          List<User> revendeur = userRepository.findUserByEmailOrEmailOrIdentification(
              CodeRevendeur, EmailRevendeur, IdentificationFiscale);
          abonnementHistoriqueService.saveNewHistorique(user, demandeId,
              "Affectation au revendeur :" + revendeur.get(0).getFirstName() + " "
                  + revendeur.get(0).getLastName() + " (" + revendeur.get(0).getCodeUser() + " ).");
          return createResponse(true, HttpStatus.OK,
              "La demande d'abonnement a été affecté avec succée ", isAffectedUser);
        } else {
          return createResponse(false, HttpStatus.BAD_REQUEST,
              "Demande d'abonnement n'a pas pu être affecté", null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST,
            "MobileAppController.sendAffectationClient Error: in authentication", null);
      }
    } catch (Exception e) {
      logger.error(" MobileAppController.sendAffectationClient Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "listBordereaux")
  public Map<String, Object> ListDesBordereaux(@RequestParam(required = false) Boolean isEnInstance,
      @RequestParam(required = false) Boolean isConfirmed,
      @RequestParam(required = false) Boolean isAnomalie,
      @RequestParam(required = false) Boolean isJustificatif,

      @RequestParam(required = false) String type, @RequestParam(required = false) Long revId,
      @RequestParam(defaultValue = "1", required = false) int page) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String phoneNumber = (String) authentication.getPrincipal();
        if (type == null) {
          return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires",
              null);
        }
        if (type.equals("DISTRIBUTEUR")) {
          User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
          if (user == null) {
            return createResponse(false, HttpStatus.NOT_FOUND,
                "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
          }
          if (page <= 0) {
            page = 1;
          }
          PageRequest pageRequest = PageRequest.of(page - 1, 10);

          Page<Map<String, Object>> result =
              bordereauService.bordereauListBychefsecteur(isEnInstance, isConfirmed,
                  user.getUserid(), isAnomalie, isJustificatif, revId, pageRequest);
          return createResponse(true, HttpStatus.OK, "ok", result);
        } else {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
              null);
        }

      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST,
            "MobileAppController.listbordereaux Error: in authentication", null);
      }
    } catch (Exception e) {
      logger.error(" MobileAppController.listbordereaux Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "demandeRealiser")
  public Map<String, Object> ListDemandeRealiser(@RequestParam("year") int year,
      @RequestParam(value = "revId", required = false) Long revId,
      @RequestParam(required = false) String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String phoneNumber = (String) authentication.getPrincipal();
        if (type == null) {
          return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires",
              null);
        }
        if (type.equals("DISTRIBUTEUR")) {
          User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
          if (user == null) {
            return createResponse(false, HttpStatus.NOT_FOUND,
                "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
          }
          List<Map<String, Object>> result = demandeAbonnementService
              .getFullYearDemandeAbonnementRealiserCounts(year, revId, user.getUserid());
          return createResponse(true, HttpStatus.OK, "ok", result);
        } else {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
              null);
        }

      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST,
            "MobileAppController.listbordereaux Error: in authentication", null);
      }
    } catch (Exception e) {
      logger.error(" MobileAppController.ListDemandeRealiser Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

  }

  @PostMapping("/addvisite")
  public Map<String, Object> addVisite(@RequestParam Long typevisiteId, @RequestBody Visite visite,
      @RequestParam String type, Long revendeurId) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String phoneNumber = (String) authentication.getPrincipal();
      if (type == null) {
        return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires", null);
      }
      if (type.equals("DISTRIBUTEUR")) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        TypeVisite typevisite = typeVisiteService.findById(typevisiteId);
        Visite newVisite = visiteService.addVisite(visite, user, revendeurId, typevisite);

        // VisiteDto visiteDto = VisiteDto.fromEntity(newVisite);

        return createResponse(true, HttpStatus.OK, "Nouvelle visite ajoutée avec succès", null);
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
            null);
      }
    } catch (Exception e) {
      logger.error(" MobileAppController.addVisite Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @GetMapping("/listFactures")
  public Map<String, Object> getFactures(@RequestParam String type, @RequestParam Long clientid,
      @RequestParam(defaultValue = "1", required = false) int page) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String phoneNumber = (String) authentication.getPrincipal();
      if (type == null) {
        return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires", null);
      }
      if (type.equals("DISTRIBUTEUR")) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        if (page <= 0) {
          page = 1;
        }
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Optional<Abonnement> ab = abonnementRepository.findById(clientid);
        if (ab.isPresent()) {
          Page<Map<String, Object>> factures =
              factureService.getAllFacturesByClientForMobile(ab.get().getClientid(), pageRequest);
          return createResponse(true, HttpStatus.OK, "succès", factures);
        } else {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Aucun abonnement trouvé", null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
            null);
      }
    } catch (Exception e) {
      logger.error("Error in getFactures:", e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @GetMapping("/listVisteBychefSecteur")
  public Map<String, Object> getVisites(@RequestParam(required = false) Long typevisiteid,
      @RequestParam(required = false) Long editedBy,
      @RequestParam(required = false) Long revendeurId,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate, @RequestParam String type,
      @RequestParam(defaultValue = "1", required = false) int page) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String phoneNumber = (String) authentication.getPrincipal();
      if (type == null) {
        return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires", null);
      }
      if (type.equals("DISTRIBUTEUR")) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        if (page <= 0) {
          page = 1;
        }
        // TypeVisite typeVisite = typeVisiteService.findById(typevisiteid);
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<Visite> visites = visiteService.findVisitesBychefsecteur(user.getUserid(),
            typevisiteid, editedBy, revendeurId, startDate, endDate, pageRequest);
        Page<VisiteDto> visiteDtos = visites.map(VisiteDto::fromEntity);
        return createResponse(true, HttpStatus.OK, "succès", visiteDtos);
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
            null);
      }
    } catch (Exception e) {
      logger.error("Error in getVisites:", e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @PostMapping("/editVisite/{visiteId}")
  public Map<String, Object> editVisite(@RequestBody Visite visite, @RequestParam String type,
      @RequestParam Long typevisiteid, Long revendeurId, @PathVariable Long visiteId) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String phoneNumber = (String) authentication.getPrincipal();
      if (type == null) {
        return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires", null);
      }
      if (type.equals("DISTRIBUTEUR")) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        // TypeVisite TypeVisiteOld=typeVisiteService.findById(typevisiteid);
        Visite oldVisite =
            visiteService.editVisite(visite, user, revendeurId, visiteId, typevisiteid);
        if (oldVisite != null) {
          VisiteDto visiteDto = VisiteDto.fromEntity(oldVisite);

          return createResponse(true, HttpStatus.OK, "La visite a été modifié avec succès",
              visiteDto);
        } else {
          return createResponse(true, HttpStatus.NOT_FOUND,
              "Aucune visite a été trouvée avec cet Id: " + visiteId, null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
            null);
      }
    } catch (Exception e) {
      logger.error(" MobileAppController.editVisite Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @PostMapping("/editVisitestatus/{visiteId}")
  public Map<String, Object> editVisitestatus(@RequestParam String status,
      @RequestParam String type, @PathVariable Long visiteId) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String phoneNumber = (String) authentication.getPrincipal();
      if (type == null) {
        return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires", null);
      }
      if (type.equals("DISTRIBUTEUR")) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        Visite oldVisite = visiteService.modifyStatus(status, user, visiteId);
        if (oldVisite != null) {
          VisiteDto visiteDto = VisiteDto.fromEntity(oldVisite);

          return createResponse(true, HttpStatus.OK,
              "La status de la visite a été modifié avec succès", visiteDto);
        } else {
          return createResponse(true, HttpStatus.NOT_FOUND,
              "Aucune visite a été trouvée avec cet Id: " + visiteId, null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
            null);
      }
    } catch (Exception e) {
      logger.error(" MobileAppController.editVisite Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  private Map<String, Object> createResponse(boolean status, HttpStatus code, String message,
      Object data) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", status);
    response.put("code", code.value());
    response.put("message", message);
    response.put("data", data);
    return response;
  }

  @PostMapping("/logout")
  public Map<String, Object> logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    try {
      final String authHeader = request.getHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return createResponse(false, HttpStatus.BAD_REQUEST,
            "Vous n'êtes pas autorisé, jwt token est manquant", null);
      } else {
        logoutService.logout(request, response, authentication);
        return createResponse(true, HttpStatus.OK, "Vous avez déconnecté.", null);
      }
    } catch (Exception e) {
      logger.error(" MobileAppController.logout Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

  }

  @GetMapping("/profile")
  public Map<String, Object> getProfile(Authentication authentication,
      @RequestParam(value = "type", required = false) String type, HttpServletRequest request) {
    try {
      if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String phoneNumber = (String) authentication.getPrincipal();
      if (type == null) {
        return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires", null);
      }
      if (type.equals("DISTRIBUTEUR")) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        } else {
          UserProfileDto userDto = UserProfileDto.fromEntity(user);
          if (userDto.getImageFile() != null) {
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String domain =
                serverPort == 80 || serverPort == 443 ? String.format("%s://%s", scheme, serverName)
                    : String.format("%s://%s:%d", scheme, serverName, serverPort);

            // System.err.println(domain);
            userDto.setImageFile(
                domain + "/photos/" + userDto.getEmail() + "/" + userDto.getImageFile());
          }
          return createResponse(true, HttpStatus.OK, null, userDto);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
            null);
      }
    } catch (Exception e) {
      logger.error(" MobileAppController.getProfile Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  @PostMapping("/profile")
  public Map<String, Object> updateUserProfile(@RequestParam("lastname") String lastname,
      @RequestParam("firstname") String firstname, @RequestParam("email") String email,
      @RequestParam("telephone") String telephone, @RequestParam("password") String password,
      @RequestParam("confirm_Password") String confirmPassword,
      @RequestParam("activitePrincipale") String activitePrincipale,
      @RequestParam("coordonneesBancaires") String coordonneesBancaires,
      @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
      @RequestParam String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String phoneNumber = (String) authentication.getPrincipal();
        User oldUser = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);

        if (oldUser == null) {
          return createResponse(false, HttpStatus.NOT_FOUND, "l'utilisateur n'existe pas ", null);
        }
        if (password.equals(confirmPassword) && email.equals(oldUser.getEmail())) {

          oldUser.setActivitePrincipale(activitePrincipale);
          if (!password.isEmpty()) {
            oldUser.setPassword(passwordEncoder.encode(password));
          }
          if (lastname != null && !lastname.isEmpty()) {
            oldUser.setLastName(lastname);
          }
          if (firstname != null && !firstname.isEmpty()) {
            oldUser.setFirstName(firstname);
          }
          if (telephone != null && !telephone.isEmpty()) {
            oldUser.setTelephone(telephone);
          }
          oldUser.setCoordonneesBancaires(coordonneesBancaires);
          if (imageFile != null && !imageFile.isEmpty()) {
            try {
              userservice.updateImage(imageFile, oldUser.getEmail(), oldUser.getPhoto());
              oldUser.setPhoto(CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
            } catch (Exception e) {
              return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
                  "Impossible de modifié l'image ", null);
            }
          }
          userRepository.save(oldUser);
          return createResponse(true, HttpStatus.OK, "Profile a été modifié ", null);
        } else {
          return createResponse(false, HttpStatus.BAD_REQUEST,
              "Confirmation de password est incorrecte ou email n'est pas autorisé ", null);
        }
      }
      return createResponse(false, HttpStatus.UNAUTHORIZED, "Vous n'êtes pas autorisé ", null);
    } catch (Exception e) {
      logger.error(" MobileAppController.getProfile Error:" + e.getMessage());
      return createResponse(false, HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }
  }

  // @PreAuthorize("hasAuthority('CREATE_RECLAMATION')")
  @PostMapping("/addReclamation")
  public Map<String, Object> saveReclamation(@ModelAttribute Reclamation reclamation,
      @RequestParam("servicetypeId") Long servicetypeId,
      @RequestParam(value = "motifId", required = false) Long motifId,
      @RequestParam(value = "clientId", required = false) Long clientId,
      @RequestParam(value = "justificatif", required = false) MultipartFile[] justificatifFiles,
      @RequestParam String type, Authentication authentication) {

    try {
      Authentication authentication2 = SecurityContextHolder.getContext().getAuthentication();
      if (authentication2 instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String phoneNumber = (String) authentication.getPrincipal();
      if (type == null) {
        return createResponse(false, HttpStatus.NOT_FOUND, "Le champs type est obligatoires", null);
      }
      if (type.equals("DISTRIBUTEUR")) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        Statusrec status = statusrecService.getStatusrecByDesignation(NomStatutReclamation.OPENED);
        Servicetype serviceType = servicetypeService.findbyServicetypeId(servicetypeId);

        if (status == null || serviceType == null) {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Invalid status or service type",
              null);
        }

        Motifrec motif = null;
        Long telephone = null;

        if (motifId != null) {
          motif = motifrecService.findById(motifId);
          reclamation.setMotif(motif);
        }

        String currentUser = authentication.getName();
        User userConnected = userRepository.findUsersByUserid(Long.parseLong(currentUser));

        if (clientId != null) {
          User user2 = userRepository.findById(clientId).get();
          reclamation.setUser(user2);
          telephone = user2 != null ? Long.parseLong(user2.getTelephone()) : null;
          // }
        } else {
          reclamation.setUser(userConnected);
          telephone = Long.parseLong(userConnected.getTelephone());
        }
        if (justificatifFiles != null && justificatifFiles.length > 0) {
          List<String> justificatifFileNames = new ArrayList<>();
          for (MultipartFile justificatifFile : justificatifFiles) {
            if (justificatifFile != null && !justificatifFile.isEmpty()) {
              String fileName = "reclamation_justification_" + System.currentTimeMillis() + "_"
                  + CrmUtils.noSpecialCharacters(justificatifFile.getOriginalFilename());
              CrmUtils.saveImage(justificatifFile, "", pathReclamation, fileName);
              justificatifFileNames.add(fileName);
            }
          }
          reclamation.setJustificatifs(justificatifFileNames);
        }
        reclamation.setSource("CHEFSECTEUR_APP");
        reclamation.setStatus(status);
        reclamation.setServiceType(serviceType);
        reclamation.setCreatedby(userConnected);
        reclamation.setEditedby(userConnected);
        reclamationService.saveReclamation(reclamation);
        reclamationHistoryService.insertNewHistoryclaims(reclamation,
            "Création d'une réclamation sous référence:" + reclamation.getRef_reclamation(),
            userConnected);
        if (telephone != null) {
          ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
          Map<String, Object> Message = new HashMap<String, Object>();

          String sms =
              "Bonjour cher(e) client(e), Nety vous informe que votre réclamation a été enregistrée \r\n"
                  + "sous la référence " + reclamation.getRef_reclamation() + ".";
          Message.put("number", telephone);
          Message.put("message", sms);
          smsToSend.add(Message);
          logger.info("sms creation de réclamation sent" + smsToSend.size());
          if (smsToSend.size() > 0) {
            Boolean resultaSms = notificationservice.sendsmsnotification(smsToSend);
            logger.info("sms creation reclamation sent " + smsToSend + "resultat " + resultaSms);
          }
        }


        return createResponse(true, HttpStatus.OK, "Réclamation a été ajouté avec succée", null);
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur ",
            null);
      }
    } catch (Exception e) {
      logger.error("Error saving reclamation", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while saving the reclamation", null);
    }
  }

  @PostMapping("/getListReclamations")
  public Map<String, Object> listReclamations(@RequestParam String type,
      @RequestParam(value = "ref_reclamation", required = false) String refReclamation,
      @RequestParam(value = "statutId", required = false) Long statutId,
      @RequestParam(value = "creepar", required = false) Long creePar,
      @RequestParam(value = "datedebut", required = false) Date dateDebut,
      @RequestParam(value = "datefin", required = false) Date dateFin,
      @RequestParam(value = "category", required = false) String category,
      @RequestParam(value = "TelFixe", required = false) Long telFixe,
      @RequestParam(value = "servicetypeId", required = false) Long serviceTypeId,
      @RequestParam(defaultValue = "1", required = false) int page,
      @RequestParam(value = "sorttype", required = false) String sorttype,
      @RequestParam(value = "sortvar", required = false) String sortvar) {
    try {
      // Authentication validation
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String phoneNumber = (String) authentication.getPrincipal();
      if (type == null || type.isEmpty()) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Le champ type est obligatoire", null);
      }
      if (UserTypeConstant.DISTRIBUTEUR.equals(type)) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        if (page <= 0) {
          page = 1;
        }
        Sort sort = Sort.by("modifiedDate").descending();
        if (sorttype != null && sorttype.equals("desc")) {
          sort = Sort.by(sortvar).descending();
        } else if (sorttype != null && !sorttype.equals("desc")) {
          sort = Sort.by(sortvar).ascending();
        }
        PageRequest pageRequest = PageRequest.of(page - 1, 10, sort);
        String statut = null;
        if (statutId != null) {
          Statusrec status = statusrecService.findById(statutId);
          if (status != null) {
            statut = status.getDesignation();
          }
        }

        String serviceType = null;
        if (serviceTypeId != null) {
          Servicetype serviceTypeName = servicetypeService.findbyServicetypeId(serviceTypeId);
          if (serviceTypeName != null) {
            serviceType = serviceTypeName.getCategorytype();
          }
        }

        Page<Reclamation> reclamations = reclamationRepository.findlistReclamationBychefsecteur(
            user.getUserid(), null, statutId, category, pageRequest);
        Page<ReclamationDto> reclamationDtos = reclamations.map(ReclamationDto::fromEntity);

        return createResponse(true, HttpStatus.OK, "Succès", reclamationDtos);
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur",
            null);
      }
    } catch (Exception e) {
      logger.error("Error in ListReclamations:", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
          "Une erreur est survenue lors de la récupération des réclamations", null);
    }
  }

  @GetMapping("/revendeurs")
  public Map<String, Object> getClientsByCategory(@RequestParam String category) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User currentUserObj = userRepository.findUsersByUserid(Long.parseLong(currentUser));

      List<Map<String, Object>> userList = new ArrayList<>();
      List<String> stringsRole =
          currentUserObj.getRole().getStringsRole(currentUserObj.getRole().getPrivileges());

      if (category.equals("Revendeur")) {

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
          return map;
        }).collect(Collectors.toList());
      }
      return createResponse(true, HttpStatus.OK, null, userList);

    }
    return createResponse(false, HttpStatus.BAD_REQUEST, "Aucune valeur", null);
  }


  @GetMapping("/listmodems")
  public Map<String, Object> getListModems(@RequestParam Boolean affectation,
      @RequestParam String type, @RequestParam Boolean etatModem,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "3") Integer sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User currentUserObj = userRepository.findUsersByUserid(Long.parseLong(currentUser));

      List<Map<String, Object>> userList = new ArrayList<>();
      List<String> stringsRole =
          currentUserObj.getRole().getStringsRole(currentUserObj.getRole().getPrivileges());

      if (stringsRole.contains("READ_MODEM_LIST_AREA")) {
        Boolean statut = affectation ? (Boolean) affectation : null;
        Boolean etat = etatModem ? (Boolean) etatModem : null;
        int currentpage = page / size;
        Sort.Direction direction =
            sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "num_serie");

        Pageable pageable = PageRequest.of(currentpage, size, sort);
        return createResponse(true, HttpStatus.OK, null, modemRepository.paginatelistmodemtoDist(
            currentUserObj.getUserid(), pageable, null, type, null, null, null, statut, etat));
      }
    }
    return createResponse(false, HttpStatus.BAD_REQUEST, "Aucune valeur", null);
  }

  @GetMapping("/Listaffectations")
  public Map<String, Object> getListAffectations(@RequestParam String datedebut,
      @RequestParam String dateFin, @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User currentUserObj = userRepository.findUsersByUserid(Long.parseLong(currentUser));

      List<Map<String, Object>> userList = new ArrayList<>();
      List<String> stringsRole =
          currentUserObj.getRole().getStringsRole(currentUserObj.getRole().getPrivileges());

      if (stringsRole.contains("READ_MODEM_LIST_AREA")) {
        int currentpage = page / size;
        Pageable pageable =
            PageRequest.of(currentpage, size, Sort.by(Sort.Direction.DESC, "id_fiche"));
        return createResponse(true, HttpStatus.OK, null, ficheRepository.getFicheByUserID(pageable,
            currentUserObj.getUserid(), datedebut, dateFin, null));
      }
    }
    return createResponse(false, HttpStatus.BAD_REQUEST, "Aucune valeur", null);
  }

  // modem api for commercial
  @GetMapping("/detailsStockRev")
  public Map<String, Object> getDetailsStockRev(@RequestParam("idUser") Long idUser,
      @RequestParam String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String userID = (String) authentication.getPrincipal();
      if (type == null || type.isEmpty()) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Le champ type est obligatoire", null);
      }
      if (UserTypeConstant.DISTRIBUTEUR.equals(type)) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(userID), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        List<Modem> modems = modemRepository.getDetailsStockRev(idUser);
        List<Modem> modemsADSL = new ArrayList<>();
        List<Modem> modemsVDSL = new ArrayList<>();
        List<Modem> modemsGPON = new ArrayList<>();
        List<Modem> modemsXDSL = new ArrayList<>();
        for (Modem modem : modems) {
          if (modem.getModelModem().equals("ADSL")) {
            modemsADSL.add(modem);
          } else if (modem.getModelModem().equals("VDSL")) {
            modemsVDSL.add(modem);
          } else if (modem.getModelModem().equals("GPON")) {
            modemsGPON.add(modem);
          } else if (modem.getModelModem().equals("XDSL")) {
            modemsXDSL.add(modem);
          }
        }
        Map<String, Object> res = new HashMap<>();
        res.put("modemsADSL", modemsADSL);
        res.put("modemsVDSL", modemsVDSL);
        res.put("modemsGPON", modemsGPON);
        res.put("modemsXDSL", modemsXDSL);

        // ✅ Add counts for each type
        Map<String, Integer> counts = new HashMap<>();
        counts.put("ADSL", modemsADSL.size());
        counts.put("VDSL", modemsVDSL.size());
        counts.put("GPON", modemsGPON.size());
        counts.put("XDSL", modemsXDSL.size());
        counts.put("TOTAL", modems.size());

        // ✅ Add counts map to response
        res.put("counts", counts);
        return createResponse(true, HttpStatus.OK, null, res);
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur",
            null);
      }
    } catch (Exception e) {
      logger.error("Error in detailsStockRev:", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
          "Une erreur est survenue lors de la récupération de detailsStockRev", null);
    }
  }


  @PostMapping("sendMailDemande")
  public Map<String, Object> sendMailDemande(@RequestBody List<DemandeModemRequest> demandes,
      @RequestParam String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }

      String userID = (String) authentication.getPrincipal();

      if (type == null || type.isEmpty()) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Le champ type est obligatoire", null);
      }

      if (!UserTypeConstant.DISTRIBUTEUR.equals(type)) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur",
            null);
      }

      User user = userRepository.findTop1UsersByUserid(Long.parseLong(userID), type);
      if (user == null) {
        return createResponse(false, HttpStatus.NOT_FOUND,
            "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
      }

      List<DemandeModem> demandeList = new ArrayList<>();
      String fullName = user.getFirstName() + " " + user.getLastName();

      for (DemandeModemRequest req : demandes) {
        DemandeModem demande = new DemandeModem();
        demande.setUser(user);
        demande.setQuantiter(req.getQuantite().toString());
        demande.setTypeModem(req.getTypemodem());
        demandeList.add(demande);
      }
      DemandeModemRepository.saveAll(demandeList);
      logger.info("Saved all demandes in DB");

      // Send email once per admin with all demande details
      List<User> adminUsers = userservice.findUsersByTypeUser(UserTypeConstant.ADMINISTRATEUR);
      StringBuilder modemDetails = new StringBuilder();

      for (DemandeModemRequest req : demandes) {
        modemDetails.append("- ").append(req.getQuantite()).append(" modems de type ")
            .append(req.getTypemodem()).append("<br>");
      }

      String emailTemplate = HtmlTemplateEmail
          .HtmlEmailDemandeModemsDistToAdminGroup(modemDetails.toString(), fullName);
      for (User admin : adminUsers) {
        notificationservice.sendSimpleMailHtml(admin.getEmail(), emailTemplate, "Demande de Stock");
      }
      /*
       * notificationservice.sendSimpleMailHtml("abidoussama2015@gmail.com", emailTemplate,
       * "Demande de Stock");
       */
      return createResponse(true, HttpStatus.OK, "Demandes des modems ont été envoyées", null);

    } catch (Exception e) {
      logger.error("Error in sendMailDemande:", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
          "Une erreur est survenue lors de la sendMailDemande", null);
    }
  }

  @GetMapping("/listModemDist")
  public Map<String, Object> listModemDist(
      @RequestParam(value = "numSerie", required = false) String numSerie,
      @RequestParam(value = "model", required = false) String model,
      @RequestParam(value = "affectedUser", required = false) Long affetedUser,
      @RequestParam(value = "numSerieDebut", required = false) String numSerieDebut,
      @RequestParam(value = "numSerieFin", required = false) String numSerieFin,
      @RequestParam(value = "statut", required = false) Boolean statut,
      @RequestParam(value = "etat", required = false) Boolean etat, @RequestParam String type,
      @RequestParam(defaultValue = "1") int page) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String userID = (String) authentication.getPrincipal();
      if (type == null || type.isEmpty()) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Le champ type est obligatoire", null);
      }
      if (UserTypeConstant.DISTRIBUTEUR.equals(type)) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(userID), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        if (page <= 0) {
          page = 1;
        }
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<ModemDistributeur> modems = modemRepository.listmodemtoDist(user.getUserid(), numSerie,
            model, affetedUser, numSerieDebut, numSerieFin, statut, etat, pageRequest);

        return createResponse(true, HttpStatus.OK, null, modems);
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur",
            null);
      }
    } catch (Exception e) {
      logger.error("Error in detailsStockRev:", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
          "Une erreur est survenue lors de la récupération de detailsStockRev", null);
    }
  }

  @GetMapping("/listDemandModemDist")
  public Map<String, Object> listDemandModem(@RequestParam(value = "id", required = false) Long id,
      @RequestParam(value = "userId", required = false) Long userId,
      @RequestParam(value = "dateDebut", required = false) Date dateDebut,
      @RequestParam(value = "datefin", required = false) Date dateFin, @RequestParam String type,
      @RequestParam(value = "typefilter", required = false) String typefilter,
      @RequestParam(value = "idDemandeModem", required = false) Long idDemandeModem) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String userID = (String) authentication.getPrincipal();
      if (type == null || type.isEmpty()) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Le champ type est obligatoire", null);
      }
      if (UserTypeConstant.DISTRIBUTEUR.equals(type)) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(userID), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        List<DemandeModem> modems = DemandeModemRepository.findAllAreaDist(id, userId, dateDebut,
            dateFin, typefilter, user.getUserid(), idDemandeModem);
        List<DemandeModemDto> modemDtos =
            modems.stream().map(DemandeModemDto::fromEntity).collect(Collectors.toList());
        return createResponse(true, HttpStatus.OK, null, modemDtos);
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur",
            null);
      }
    } catch (Exception e) {
      logger.error("Error in detailsStockRev:", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
          "Une erreur est survenue lors de la récupération de listDemandModemDist", null);
    }
  }

  @GetMapping("/getFicheAffectationModem")
  public Map<String, Object> getFicheAffectationModem(
      @RequestParam(value = "affectA", required = false) Long affectA,
      @RequestParam(value = "datedebut", required = false) String datedebut,
      @RequestParam(value = "datefin", required = false) String datefin, @RequestParam String type,
      @RequestParam(defaultValue = "1") int page) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String userID = (String) authentication.getPrincipal();
      if (type == null || type.isEmpty()) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Le champ type est obligatoire", null);
      }
      if (UserTypeConstant.DISTRIBUTEUR.equals(type)) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(userID), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        List<String> StringsRole = new ArrayList<String>();
        StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
        if (StringsRole.contains("READ_MODEM_LIST_AREA")) {
          if (page <= 0) {
            page = 1;
          }
          PageRequest pageRequest = PageRequest.of(page - 1, 10);
          Page<ModemAffectationFiches> fiches = ficheRepository
              .getFicheByUserIDComm(user.getUserid(), datedebut, datefin, affectA, pageRequest);

          return createResponse(true, HttpStatus.OK, null, fiches);
        } else {
          return createResponse(false, HttpStatus.BAD_REQUEST,
              "Vous n'avez pas le privilège pour faire cette action", null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur",
            null);
      }
    } catch (Exception e) {
      logger.error("Error in detailsStockRev:", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
          "Une erreur est survenue lors de la récupération de getFicheAffectationModem:"
              + e.getMessage(),
          null);
    }
  }

  @GetMapping("/getDetailFicheAffectation")
  public Map<String, Object> getDetailFicheAffectation(
      @RequestParam(value = "ref", required = true) String ref, @RequestParam String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Authentication failed", null);
      }
      String userID = (String) authentication.getPrincipal();
      if (type == null || type.isEmpty()) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Le champ type est obligatoire", null);
      }
      if (UserTypeConstant.DISTRIBUTEUR.equals(type)) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(userID), type);
        if (user == null) {
          return createResponse(false, HttpStatus.NOT_FOUND,
              "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
        }
        FicheStock fiche = ficheservice.getFiche(ref);
        List<Modem> modems = ficheservice.listmodemfiche(ref);
        User affectedToUser = ficheservice.usersfiche(fiche.getAffecteID());
        User affectedByUser = userRepository.findByUserId(fiche.getAffectedBYuser());
        Map<String, Object> res = new HashMap<>();
        res.put("affectedTo", UserDtoApp.fromEntity(affectedToUser));
        res.put("fiche", fiche);
        res.put("modems", modems);
        res.put("affectedBy", UserDtoApp.fromEntity(affectedByUser));

        return createResponse(true, HttpStatus.OK, null, res);
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur",
            null);
      }
    } catch (Exception e) {
      logger.error("Error in detailsStockRev:", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
          "Une erreur est survenue lors de la récupération de getDetailFicheAffectation", null);
    }
  }

  @PostMapping("/verificationAffectModem")
  public Map<String, Object> AffectationModem(@RequestParam("modemIdsField") String modemIds,
      @RequestParam("selectuser") String selectedCodeUser, @RequestParam String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Vous n'êtes pas authentifié", null);
      }
      String userID = (String) authentication.getPrincipal();
      if (type == null || type.isEmpty()) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Le champ type est obligatoire", null);
      }
      if (UserTypeConstant.DISTRIBUTEUR.equals(type)) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(userID), type);
        List<String> roles = user.getRole().getStringsRole(user.getRole().getPrivileges());
        if (selectedCodeUser == null || selectedCodeUser.isEmpty()) {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Le code utilisateur est requis.",
              null);
        }

        if (modemIds == null || modemIds.isEmpty()) {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Veuillez sélectionner des modems.",
              null);
        }

        List<Long> idList =
            Arrays.stream(modemIds.split(",")).map(Long::parseLong).collect(Collectors.toList());

        ModemVerificationResult result = affectservice.verificationAffectModemRest(idList,
            selectedCodeUser, roles, user.getUserid());

        switch (result.getStatus()) {
          case "USER_NOT_FOUND":
            return createResponse(false, HttpStatus.NOT_FOUND, "Utilisateur introuvable.", null);
          case "SUCCESS":
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("modemValid", result.getModemValid());
            responseData.put("modemNotValid", result.getModemNotValid());

            return createResponse(true, HttpStatus.OK, "Vérification réussie", responseData);
          default:
            return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, "Erreur inconnue.",
                null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur",
            null);
      }
    } catch (Exception e) {
      logger.error("Erreur lors de la vérification des modems", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne", null);
    }
  }

  @PostMapping("/confirmAffectModem")
  public Map<String, Object> confirmAffectModem(@RequestParam("modemIdsField") String modemIds,
      @RequestParam("selectuser") String selectedCodeUser, @RequestParam String type) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication instanceof AnonymousAuthenticationToken) {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "Vous n'êtes pas authentifié", null);
      }
      String userID = (String) authentication.getPrincipal();
      if (type == null || type.isEmpty()) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Le champ type est obligatoire", null);
      }
      if (UserTypeConstant.DISTRIBUTEUR.equals(type)) {
        User user = userRepository.findTop1UsersByUserid(Long.parseLong(userID), type);
        List<String> roles = user.getRole().getStringsRole(user.getRole().getPrivileges());
        if (selectedCodeUser == null || selectedCodeUser.isEmpty()) {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Le code utilisateur est requis.",
              null);
        }
        if (modemIds == null || modemIds.isEmpty()) {
          return createResponse(false, HttpStatus.BAD_REQUEST, "Veuillez sélectionner des modems.",
              null);
        }
        List<Long> idList =
            Arrays.stream(modemIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<Modem> modemList =
            affectservice.getModemsForVerification(roles, user.getUserid(), idList);
        if (modemList.isEmpty()) {
          return createResponse(false, HttpStatus.BAD_REQUEST,
              "Svp,entrez une liste valide des modems qui existent et ne sont pas affecté a aucun revendeur ou client",
              null);
        }
        String result = affectservice.confirmAffectModemRest(modemList, user, selectedCodeUser);
        switch (result) {
          case "USER_NOT_FOUND":
            return createResponse(false, HttpStatus.NOT_FOUND, "Utilisateur introuvable.", null);
          case "SUCCESS":
            return createResponse(true, HttpStatus.OK, "Afféctation réussie", null);
          default:
            return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, "Erreur inconnue.",
                null);
        }
      } else {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Vous n'êtes pas un chef secteur",
            null);
      }
    } catch (Exception e) {
      logger.error("Erreur lors de la vérification des modems", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne", null);
    }
  }


}
