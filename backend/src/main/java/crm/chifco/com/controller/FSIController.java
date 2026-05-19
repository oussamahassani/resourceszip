package crm.chifco.com.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import crm.chifco.com.ApiDTO.FactureDTO;
import crm.chifco.com.ApiDTO.ListeFactureAndAvoirDTO;
import crm.chifco.com.ApiDTO.ListeFacturePayeDTO;
import crm.chifco.com.ApiDTO.MazamaFilter;
import crm.chifco.com.ApiDTO.ParinageDTO;
import crm.chifco.com.ApiDTO.PaymentDTO;
import crm.chifco.com.ApiDTO.PaymentDTOApi;
import crm.chifco.com.ApiDTO.ProduitWithTarifsDTO;
import crm.chifco.com.DTOclass.ReclamationDto;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AntivirusKey;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.ClassificationDemande;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.EntryDemandeAbonnement;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.Motifrec;
import crm.chifco.com.model.Pack;
import crm.chifco.com.model.Payement;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.Produit;
import crm.chifco.com.model.Profession;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.model.Servicetype;
import crm.chifco.com.model.Statusrec;
import crm.chifco.com.model.Tarification;
import crm.chifco.com.model.Typepaiement;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Ville;
import crm.chifco.com.radius.model.Radacct;
import crm.chifco.com.radius.repository.RadacctRepository;
import crm.chifco.com.radius.service.RadcheckService;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AntivirusKeyRepository;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.ClassificationDemandeRepository;
import crm.chifco.com.repository.CodePostaleRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.EntryDemandeAbonnementRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.ProduitRepository;
import crm.chifco.com.repository.ReclamationRepository;
import crm.chifco.com.repository.TarificationRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.repository.VilleRepository;
import crm.chifco.com.service.AbonnementHistoriqueService;
import crm.chifco.com.service.AbonnementService;
import crm.chifco.com.service.AvoirService;
import crm.chifco.com.service.CategorieProduitInternetService;
import crm.chifco.com.service.DemandeAbonnementService;
import crm.chifco.com.service.FactureService;
import crm.chifco.com.service.GouverneratsService;
import crm.chifco.com.service.ModemService;
import crm.chifco.com.service.MotifrecService;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.PackService;
import crm.chifco.com.service.ParinageService;
import crm.chifco.com.service.PayementsService;
import crm.chifco.com.service.ProduitService;
import crm.chifco.com.service.ProfessionService;
import crm.chifco.com.service.ReclamationHistoryService;
import crm.chifco.com.service.ReclamationService;
import crm.chifco.com.service.ServicetypeService;
import crm.chifco.com.service.StatusrecService;
import crm.chifco.com.service.StatutService;
import crm.chifco.com.service.TarificationServices;
import crm.chifco.com.service.TypePaiementService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.service.Utilsjwt;
import crm.chifco.com.service.VillesService;
import crm.chifco.com.templateclasse.ClientLogin;
import crm.chifco.com.templateclasse.ContratApiDTO;
import crm.chifco.com.templateclasse.InvoiceAvoir;
import crm.chifco.com.templateclasse.ListeFactureAndAvoirNonPayeDTO;
import crm.chifco.com.templateclasse.ListeFactureNonPayeDTO;
import crm.chifco.com.templateclasse.RunPayLogin;
import crm.chifco.com.templateclasse.getAbonnementApi;
import crm.chifco.com.utils.ClassificationCode;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.DBEtatTT;
import crm.chifco.com.utils.HtmlTemplateEmail;
import crm.chifco.com.utils.NomStatutChifco;
import crm.chifco.com.utils.NomStatutReclamation;
import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping(value = "api/*")

public class FSIController {
  private static final Logger logger = LogManager.getLogger(ACSController.class);

  @Value("${pathDemandesAbonnement}")
  private String pathDemandesAbonnement;

  @Value("${pathFacture}")
  private String pathFacture;

  @Value("${pathAvoir}")
  private String pathAvoir;
  @Value("${xls.mail.responsable}")
  private String xlsMailResponsable;


  @Autowired
  UserService userservice;

  @Autowired
  GouverneratsService gouverneratsService;

  @Autowired
  GouvernoratRepository gouvernoratRepository;

  @Autowired
  VillesService villesService;

  @Autowired
  ProfessionService professionService;

  @Autowired
  DemandeAbonnementService demandeAbonnementService;

  @Autowired
  EntryDemandeAbonnementRepository entryDemandeAbonnementRepository;

  @Autowired
  AbonnementService abonnementService;

  @Autowired
  TypePaiementService typePaiementService;

  @Autowired
  StatutService statutService;

  @Autowired
  ProduitService produitService;

  @Autowired
  ProduitRepository produitRepository;

  @Autowired
  CodePostaleRepository codePostaleRepository;

  @Autowired
  CategorieProduitInternetService categorieProduitInternetService;

  @Autowired
  DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  TarificationRepository tarificationRepository;


  @Autowired
  TarificationServices tarificationServices;

  @Autowired
  PackService packService;

  @Autowired
  FactureService factureService;

  @Autowired
  FactureRepository factureRepository;

  @Autowired
  VilleRepository villeRepository;

  @Autowired
  private AbonnementHistoriqueService abonnementHistoriqueservice;

  @Autowired
  private AbonnementRepository abonnementRepository;

  @Autowired
  PayementsService payementsService;

  @Autowired
  AvoirService avoirService;

  @Autowired
  AvoirRepository avoirRepository;

  @Autowired

  AntivirusKeyRepository antivirusKeyRepository;

  @Autowired
  ClassificationDemandeRepository classificationDemandeRepository;
  @Autowired
  private RadcheckService radcheckService;
  @Autowired
  UserRepository userRepository;
  @Autowired
  ReclamationRepository reclamationRepository;
  // private final InvoiceAvoirService invoiceAvoirService;

  /*
   * @Autowired public FSIController(InvoiceAvoirService invoiceAvoirService) {
   * this.invoiceAvoirService = invoiceAvoirService; }
   */

  @Autowired
  Utilsjwt utilsJwt;
  @Autowired
  public RadacctRepository radacctRepository;


  @Autowired
  private MotifrecService motifrecService;

  @Autowired
  private StatusrecService statusrecService;
  @Autowired
  private ReclamationService reclamationService;

  @Autowired
  Notification notificationservice;
  @Autowired
  private ReclamationHistoryService reclamationHistoryService;

  @Value("${pathReclamation}")
  private String pathReclamation;
  @Autowired
  private ServicetypeService servicetypeService;



  @Value("${grantTypeMazam}")
  private String grantTypeMazam;

  @Value("${clientSecretMazam}")
  private String clientSecretMazam;


  @Value("${grantTypeRunPay}")
  private String grantTypeRunPay;

  @Value("${clientSecretRunPay}")
  private String clientSecretRunPay;

  @Value("${grantTypeClient}")
  private String grantTypeClient;

  @Value("${clientSecretkey}")
  private String clientSecretkey;

  @Value("${grantTypeEnda}")
  private String grantTypeEnda;

  @Value("${clientSecretEnda}")
  private String clientSecretEnda;

  @Autowired
  private ParinageService parinageService;

  @Autowired
  private ModemService modemService;


  @RequestMapping(method = RequestMethod.GET, value = "getallgouvernerat")
  public ResponseEntity<List<Gouvernorat>> getAllGouvernerat() {
    ResponseEntity<List<Gouvernorat>> response = null;
    try {
      List<Gouvernorat> Listgouvernerat = gouverneratsService.findAllGouvernorat();
      response = new ResponseEntity<List<Gouvernorat>>(Listgouvernerat, HttpStatus.OK);
    } catch (Exception e) {

      logger.error(" FSIController.getallgouvernerat System Error:", e.getMessage());
      List<Gouvernorat> Listgouvernerat = new ArrayList<>();
      response =
          new ResponseEntity<List<Gouvernorat>>(Listgouvernerat, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallProduit")
  public ResponseEntity<List<ProduitWithTarifsDTO>> getallProduit() {
    ResponseEntity<List<ProduitWithTarifsDTO>> response = null;
    try {
      List<Produit> Listproduit = produitService.findAllProduitIsExtratAndActive(true, true);
      List<Long> listIds =
          Listproduit.stream().map(Produit::getProduitId).collect(Collectors.toList());
      List<Tarification> findAllProduitPrix = tarificationServices.findAllProduitPrix(listIds);
      Map<Object, List<Tarification>> tarificationMap =
          findAllProduitPrix.stream().collect(Collectors.groupingBy(t -> t.getProduitId()));

      List<ProduitWithTarifsDTO> produitsAvecTarifs = Listproduit.stream()
          .map(p -> new ProduitWithTarifsDTO(p,
              tarificationMap.getOrDefault(p.getProduitId(), new ArrayList<>())))
          .collect(Collectors.toList());
      response = new ResponseEntity<List<ProduitWithTarifsDTO>>(produitsAvecTarifs, HttpStatus.OK);
    } catch (Exception e) {

      logger.error(" FSIController.getallgouvernerat System Error:", e.getMessage());
      List<ProduitWithTarifsDTO> Listgouvernerat = new ArrayList<>();
      response = new ResponseEntity<List<ProduitWithTarifsDTO>>(Listgouvernerat,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallville")
  public ResponseEntity<List<Ville>> getAllVille(@RequestParam Long gouveneratid) {
    ResponseEntity<List<Ville>> response = null;
    try {
      List<Ville> ListVille = villesService.findAllByIdGrouvernerat(gouveneratid);
      response = new ResponseEntity<List<Ville>>(ListVille, HttpStatus.OK);
    } catch (Exception e) {

      logger.error("FSIController.getallville System Error:", e.getMessage());
      List<Ville> ListVille = new ArrayList<>();
      response = new ResponseEntity<List<Ville>>(ListVille, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallcodepostale")
  public ResponseEntity<List<PostalCode>> getAllCodePostale(@RequestParam Long villeid) {
    ResponseEntity<List<PostalCode>> response = null;
    try {
      List<PostalCode> listCodePostale =
          codePostaleRepository.findPostalCodeByVille_VilleId(villeid);
      response = new ResponseEntity<List<PostalCode>>(listCodePostale, HttpStatus.OK);
    } catch (Exception e) {

      logger.error("FSIController.getAllCodePstale System Error:", e.getMessage());
      List<PostalCode> listCodePostale = new ArrayList<>();
      response =
          new ResponseEntity<List<PostalCode>>(listCodePostale, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.GET, value = "getlisteprofession")
  public ResponseEntity<List<Profession>> getListeProfession() {
    ResponseEntity<List<Profession>> response = null;
    try {
      List<Profession> ListVille = professionService.findlistProfession();
      response = new ResponseEntity<List<Profession>>(ListVille, HttpStatus.OK);
    } catch (Exception e) {

      logger.error(" FSIController.getlisteprofession System Error:", e.getMessage());
      List<Profession> ListProfession = new ArrayList<>();
      response =
          new ResponseEntity<List<Profession>>(ListProfession, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.POST, value = "verificationexistancecin",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)

  public ResponseEntity<HashMap<String, String>> verificationExistanceCin(
      @RequestBody MultiValueMap<String, String> formData) {

    ResponseEntity<HashMap<String, String>> response = null;
    HashMap<String, String> errors = new HashMap<String, String>();
    response = new ResponseEntity<HashMap<String, String>>(errors, HttpStatus.BAD_REQUEST);

    List<DemandeAbonnement> demandeAbonnement = demandeAbonnementService
        .findDemandeAbonnementsByCinAndStatusAvaibled(formData.getFirst("cin"));

    if (demandeAbonnement.size() == 0) {
      errors.put("msg", "cin n'existe pas");
      errors.put("code", "200");
      errors.put("verif", "true");
      response = new ResponseEntity<HashMap<String, String>>(errors, HttpStatus.OK);
    } else {
      errors.put("msg", "cin existe");
      errors.put("code", "520");
      errors.put("verif", "false");
      response = new ResponseEntity<HashMap<String, String>>(errors, HttpStatus.OK);
    }

    return response;

  }

  @RequestMapping(method = RequestMethod.POST, value = "adddemandeabonnement",
      consumes = MediaType.ALL_VALUE)
  public ResponseEntity<HashMap<String, Object>> addDemandeAbonnement(
      @RequestParam MultiValueMap<String, String> abonnement, @RequestParam MultipartFile cinRecto,
      @RequestParam MultipartFile cinVerso) {

    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> error = new HashMap<String, Object>();
    HashMap<String, Object> Validation = new HashMap<String, Object>();
    DemandeAbonnement abn = new DemandeAbonnement();
    response = new ResponseEntity<HashMap<String, Object>>(Validation, HttpStatus.OK);
    try {

      logger.info("Abonnement Object from web site: " + abonnement.getFirst("prenom")
          + abonnement.getFirst("nom"));
      if (abonnement.getFirst("prenom") == null || abonnement.getFirst("prenom").equals("")) {
        error.put("prenom", "Veuillez renseigner le prénom");
      }

      if (abonnement.getFirst("nom") == null || abonnement.getFirst("nom").equals("")) {
        error.put("nom", "Veuillez renseigner le nom");
      }

      if (abonnement.getFirst("cin") == null || abonnement.getFirst("cin").equals("")) {
        error.put("cin", "Veuillez renseigner le numero de la carte d'identité nationale");
      }

      if (abonnement.getFirst("ville") == null || abonnement.getFirst("ville").equals("")) {
        error.put("ville", "Veuillez sélectionner votre ville");
      } else {
        Ville ville = villeRepository.findByAbreviation(abonnement.getFirst("ville"));
        if (ville == null) {
          error.put("ville", "reference ville invalide");
        } else {

          abn.setVille(ville);

        }
      }

      if (abonnement.getFirst("gouvernorat") == null
          || abonnement.getFirst("gouvernorat").equals("")) {
        error.put("gouvernorat", "Veuillez sélectionner votre gouvernorat");
      } else {
        Gouvernorat gov =
            gouvernoratRepository.findByAbreviation(abonnement.getFirst("gouvernorat"));
        if (gov == null) {
          error.put("gouvernorat", "reference gouvernorat invalide");
        } else {

          abn.setGouvernorat(gov);

        }
      }

      if (abonnement.getFirst("adresse") == null || abonnement.getFirst("adresse").equals(""))

      {
        error.put("adresse", "Veuillez renseigner votre adresse");
      }

      if (abonnement.getFirst("codePostale") == null
          || abonnement.getFirst("codePostale").equals("")) {
        error.put("codePostale", "Veuillez renseigner le code postale");
      } else {
        PostalCode postalCode =
            codePostaleRepository.findByAbreviation(abonnement.getFirst("codePostale"));
        if (postalCode == null) {
          error.put("codePostale", "reference code postale invalide");
        } else {
          abn.setCodePostale(postalCode);
        }
      }

      if (abonnement.getFirst("tel") == null || abonnement.getFirst("tel").equals("")) {
        error.put("tel", "Veuillez nous indiquer votre numéro de téléphone");
      }

      if (abonnement.getFirst("paiement") == null || abonnement.getFirst("paiement").equals("")) {

        error.put("paiement", "Veuillez sélectionner type de paiement");
      } else {
        Typepaiement typepaiement =
            typePaiementService.gettypepaiementbyref(abonnement.getFirst("paiement"));
        if (typepaiement == null) {
          error.put("paiement", "Veuillez sélectionner type de paiement");

        }
      }
      if (abonnement.getFirst("residence") == null || abonnement.getFirst("residence").equals("")) {
        error.put("residence", "erreur residence");
      }

      if (cinRecto.isEmpty()) {
        error.put("cinRecto", "Cin recto est obligatoire");
      }

      if (cinVerso.isEmpty()) {
        error.put("cinVerso", "Cin verso est obligatoire");
      }

      if (abonnement.getFirst("positionxy") == null
          || abonnement.getFirst("positionxy").equals("")) {
        error.put("positionxy", "positionxy est obligatoire");
      }

      List<DemandeAbonnement> demandeAbonnement = demandeAbonnementService
          .findDemandeAbonnementsByCinAndStatusAvaibled(abonnement.getFirst("cin"));
      if (demandeAbonnement.size() != 0) {
        error.put("cin", "cin déja utilisé");
      }

      if (abonnement.getFirst("produit") == null && abonnement.getFirst("produit").equals("")) {
        error.put("produit", "Pack est obligatoire");
      } else {
        Pack pack = packService.findPackByCodepack(abonnement.getFirst("produit"));
        if (pack == null) {
          error.put("produit", "reference produit invalide");
        } else {
          abn.setPack(pack);
          abn.setCategorieProduitInternet(pack.getCategoriePack());
        }
      }

      if (error.isEmpty()) {

        Typepaiement typepaiement =
            typePaiementService.gettypepaiementbyref(abonnement.getFirst("paiement"));

        boolean residence = true;
        if (abonnement.getFirst("residence").equals("false")) {
          residence = false;
        }

        abn.setFirstName(abonnement.getFirst("prenom"));
        abn.setLastName(abonnement.getFirst("nom"));
        abn.setCin(abonnement.getFirst("cin"));
        abn.setEmail(abonnement.getFirst("email"));
        abn.setTelMobile(Long.parseLong(abonnement.getFirst("tel")));

        abn.setAdresse(abonnement.getFirst("adresse"));

        abn.setTypePaiement(typepaiement);
        // abn.setProduit(produit);

        abn.setStatut(statutService.findStatutByNomstatut(NomStatutChifco.SIGNED_DOC));
        abn.setProprietaire(residence);

        abn.setPositionxy(abonnement.getFirst("positionxy"));
        if (abonnement.getFirst("telfixe") != null && !abonnement.getFirst("telfixe").equals("")
            && abonnement.getFirst("telfixe").startsWith("7")) {
          abn.setTelFixe(Long.parseLong(abonnement.getFirst("telfixe")));
          abn.setHasRaccordement(false);
        } else {
          abn.setTelFixe(null);
          if (abonnement.getFirst("racordement") != null)
            abn.setNbFaisApayeReccardement(Integer.parseInt(abonnement.getFirst("racordement")));
          else
            abn.setNbFaisApayeReccardement(1);
        }

        abn.setDateNaissance(CrmUtils.convertStringToDate("1990-01-01"));
        User user = null;
        if (abonnement.getFirst("website") != null && !abonnement.getFirst("website").isEmpty()) {
          // user
          user = userservice.findTop1UsersByEmail(abonnement.getFirst("website"));
          if (user != null)
            logger.info("user affecter est : " + user.getUserid());
          else {
            user = userservice.findTop1UsersByEmail("demandeAbonnementWebSite@nety.tn");
          }
        } else {
          user = userservice.findTop1UsersByEmail("demandeAbonnementWebSite@nety.tn");
        }

        abn.setUser(user);
        abn.setAssignedTo(user);
        ClassificationDemande classificationEnAttent = classificationDemandeRepository
            .findClassificationDemandeByCodeClassification(ClassificationCode.DEnAttente);
        abn.setDecisionDemande(classificationEnAttent);
        String caractaireIndication = "";
        if (!cinRecto.isEmpty()) {
          try {
            caractaireIndication = "recto";
            CrmUtils.saveImage(cinRecto, abonnement.getFirst("cin"), pathDemandesAbonnement,
                caractaireIndication);
            abn.setPhotoCin1(caractaireIndication
                + CrmUtils.noSpecialCharacters(cinRecto.getOriginalFilename()));
          } catch (Exception e) {
            logger.error("DemandeAbonnementServiceimpl.adddemandeabonnementwithsteps Exception: "
                + e.getMessage());

          }
        }
        if (!cinVerso.isEmpty()) {
          try {
            caractaireIndication = "verso";
            CrmUtils.saveImage(cinVerso, abonnement.getFirst("cin"), pathDemandesAbonnement,
                caractaireIndication);
            abn.setPhotoCin2(caractaireIndication
                + CrmUtils.noSpecialCharacters(cinVerso.getOriginalFilename()));
          } catch (Exception e) {
            logger.error("DemandeAbonnementServiceimpl.adddemandeabonnementwithsteps Exception: "
                + e.getMessage());

          }
        }
        if (abonnement.getFirst("origin") != null && !abonnement.getFirst("origin").isEmpty()) {
          abn.setOrigin(abonnement.getFirst("origin"));
        }
        if (abonnement.getFirst("multipleSubproduct") != null
            && !abonnement.getFirst("multipleSubproduct").isEmpty()) {
          List<EntryDemandeAbonnement> entryDemandeAbonnement =
              new ArrayList<EntryDemandeAbonnement>();
          Object result = abonnement.getFirst("multipleSubproduct");

          if (result instanceof List) {
            List<Long> list = (List<Long>) result;

            for (int i = 0; i < list.size(); i++) {
              Produit findProduit = produitRepository.getById(list.get(i));
              EntryDemandeAbonnement newEntryDemandeAbonnement = new EntryDemandeAbonnement();
              newEntryDemandeAbonnement.setProduit(findProduit);
              entryDemandeAbonnementRepository.save(newEntryDemandeAbonnement);
              entryDemandeAbonnement.add(newEntryDemandeAbonnement);

            }

          } ;
          if (entryDemandeAbonnement.size() > 0) {
            abn.setEntriesDemandeAbonnement(entryDemandeAbonnement);
          }
        }

        demandeAbonnementRepository.save(abn);
        abonnementHistoriqueservice.insertNewHistory(abn, user);

        logger.info("Abonnement par API  Add success : " + abn.getDemandeId());

        Validation.put("succes", true);
        Validation.put("error", error);
      } else {
        logger.info("Abonnement liste error : " + error);
        Validation.put("succes", false);
        Validation.put("error", error);
      }

      return response;
    } catch (Exception e) {

      logger.error("DemandeAbonnementServiceimpl.apiAddDemandeAbonnement: " + e.getMessage());
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.GET, value = "listeFactureNonPayee")
  public ResponseEntity<HashMap<String, Object>> listeFactureNonPayee(
      @RequestParam("identifiant") String recherche,
      @RequestParam("type_identif") String typeIdentif) {
    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataObject = new HashMap<String, Object>();
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();
    List<ListeFactureNonPayeDTO> ListFactureNonPayee = new ArrayList<ListeFactureNonPayeDTO>();
    List<ListeFactureAndAvoirNonPayeDTO> ListFactureNonPayeeByInterface =
        new ArrayList<ListeFactureAndAvoirNonPayeDTO>();
    try {
      Long telephone = null;
      if (CrmUtils.Isnumber(recherche)) {
        telephone = Long.parseLong(recherche);

      }
      logger.info("listeFactureNonPayee identifiant : " + recherche);
      logger.info("listeFactureNonPayee type_identif : " + typeIdentif);
      Abonnement Abonnement = null;

      if (typeIdentif.equals("2")) {
        Facture facture = factureService.findFactureNonPayeeByReference(recherche);
        if (facture != null) {
          Abonnement = facture.getAbonnement();
          ListFactureNonPayeeByInterface =
              factureRepository.findListeFactureNonPayeeByCinForApi(Abonnement.getCin());

          logger.info("Recuperation Id facture avec la recherche  de reference facture : "
              + facture.getFactureId());
        }

      } else if (typeIdentif.equals("1")) {
        Abonnement = abonnementService.findAbonnementByCin(recherche);
        // ListFactureNonPayee = factureService.findListeFactureNonPayeeByCin(recherche);
        ListFactureNonPayeeByInterface =
            factureRepository.findListeFactureNonPayeeByCinForApi(recherche);

      } else if (typeIdentif.equals("3")) {
        // ListFactureNonPayee = factureService.findListeFactureNonPayeeByFixeNumber(telephone);
        ListFactureNonPayeeByInterface =
            factureRepository.findListeFactureNonPayeeByFixeNumberForApi(telephone);
        Abonnement = abonnementService.findUserByFixeNumber(telephone);
      }

      if (typeIdentif.equals("1") || typeIdentif.equals("3") || typeIdentif.equals("2")) {
        ListFactureNonPayeeByInterface.forEach(el -> {
          ListeFactureNonPayeDTO newFactureNonPayee = new ListeFactureNonPayeDTO();
          newFactureNonPayee.setDateDeDebut(el.getDateDeDebut());
          newFactureNonPayee.setDateDeFin(el.getDateDeFin());
          newFactureNonPayee.setEcheance(el.getEcheance());
          newFactureNonPayee.setRef_facture(el.getRef_facture());
          newFactureNonPayee.setTotal_ttc(el.getMontant_payer());
          newFactureNonPayee.setTypeFacture(el.getTypeFacture());
          newFactureNonPayee.setIsFactureResilation(el.getIsFactureResilation());
          ListFactureNonPayee.add(newFactureNonPayee);
        });
      }
      HashMap<String, Object> clientInformation = new HashMap<String, Object>();
      if (Abonnement != null) {
        logger.info("Preparation Object Abonnement");
        clientInformation.put("name", Abonnement.getFirstName() + " " + Abonnement.getLastName());
        clientInformation.put("ref_abonnement", Abonnement.getReferenceClient());
        clientInformation.put("num_fixe", Abonnement.getTelFixe());
      }

      for (ListeFactureNonPayeDTO factureNonPayee : ListFactureNonPayee) {
        Facture findMyFacturefacture = factureRepository
            .findFactureNonPayeeByReferenceforApi(factureNonPayee.getRef_facture());
        File factureFile = null;
        if (findMyFacturefacture != null) {
          try {
            factureFile = factureService.createPDFFactureA4(findMyFacturefacture);
            logger.info(
                "creation de ficher pdf pour la facture" + findMyFacturefacture.getRef_facture());
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        } else {
          AvoirClient avoirClient =
              avoirRepository.getByRefAvoirClient(factureNonPayee.getRef_facture());
          factureFile = avoirService.createPDFAvoirA4(avoirClient);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(factureFile)) {
          int len;
          while ((len = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
          }
        } catch (IOException e) {
          e.printStackTrace();
          logger.info("Excpetion de lire le ficher facture" + e.getMessage());
        }
        byte[] pdfBytes = bos.toByteArray();
        factureNonPayee.setPdf_facture(pdfBytes);
      }
      if (Abonnement != null && ListFactureNonPayee.size() > 0) {
        Facture factureFirstVisible =
            factureRepository.findFirstByAbonnement_clientid(Abonnement.getClientid());
        if (factureFirstVisible != null && factureFirstVisible.getIsFirstFacture()
            && factureFirstVisible.getVisibility()) {

          dataRespenseObject.put("showfacture", true);
        } else {

          dataRespenseObject.put("showfacture", false);

        }
        dataObject.put("client", clientInformation);
        dataObject.put("factures", ListFactureNonPayee);
        dataRespenseObject.put("success", true);
        dataRespenseObject.put("message", "");
        dataRespenseObject.put("code", 200);
        dataRespenseObject.put("data", dataObject);
      } else {
        if (Abonnement == null) {
          switch (typeIdentif) {
            case "1": {
              dataRespenseObject.put("code", "NC");
              dataRespenseObject.put("message", "num cin not found ");
            }
              break;
            case "3": {
              dataRespenseObject.put("code", "NL");
              dataRespenseObject.put("message", "num ligne not found");

            }
              break;
            case "2": {
              dataRespenseObject.put("code", "NF");
              dataRespenseObject.put("message", "num facture not found ");
            }
              break;
            default: {
              dataRespenseObject.put("code", "TypeInvalide");
              dataRespenseObject.put("message", "type_identif invalide ");
            }

          }
        } else {
          dataRespenseObject.put("code", "NOF");
          dataRespenseObject.put("message", "AUNCUNE FACTURE A PAYER ");
        }

        dataRespenseObject.put("success", false);
        dataRespenseObject.put("data", null);
        dataRespenseObject.put("showfacture", false);
      }
      logger.info("code liste FactureNonPayee API  : " + dataRespenseObject.get("code"));
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
    } catch (Exception e) {
      // List<ListeFactureNonPayeDTO> ListFactureNonPayee = new ArrayList<>();

      logger.info("liste FactureNonPayee API Exption : " + e);
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("message", e.getMessage());
      dataRespenseObject.put("code", "exception");
      dataRespenseObject.put("data", null);
      dataRespenseObject.put("showfacture", false);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.POST, value = "payeementFacture")
  public ResponseEntity<HashMap<String, Object>> payeementFacture(HttpServletRequest request,
      @RequestBody PaymentDTO PayementFactures) {
    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();
    try {
      List<String> factureids = PayementFactures.getFactures();
      List<String> avrArray = new ArrayList<>();
      List<String> factureArray = new ArrayList<>();

      for (String element : factureids) {
        if (element.startsWith("AVR-") || element.startsWith("AR-")) {
          avrArray.add(element);
        } else {
          factureArray.add(element);
        }
      }
      User user = userservice.findTop1UsersByTypeuser("SYSTEM");
      String methodepayment = PayementFactures.getMethodePayment();
      if (methodepayment == null || methodepayment.isEmpty()) {
        methodepayment = "carte bancaire";
      }

      if (PayementFactures.getCreatedBy() == null && PayementFactures.getTransactionId() != null) {
        methodepayment = methodepayment + " IZI";
      } else if (PayementFactures.getCreatedBy() != null
          && PayementFactures.getCreatedBy().equals("1")) {
        methodepayment = methodepayment + " web site nety";
      } else if (PayementFactures.getCreatedBy() != null
          && PayementFactures.getCreatedBy().equals("2")) {
        methodepayment = methodepayment + " application mobile";
      }
      else {
          methodepayment = methodepayment + "carte bancaire";
 
      }
      String Bankname = PayementFactures.getBankname();
      String TransactionId = PayementFactures.getTransactionId();
      logger.info("payeementFacture TranscationId : " + TransactionId);
      logger.info("payeementFacture Liste des Reference factures a payer : " + factureArray.size());
      List<Facture> existeFacture =
          factureService.findListeFactureNonPayeeByRefFacture(factureArray);
      if (existeFacture != null && existeFacture.size() == factureArray.size()) {
        List<String> idList =
            existeFacture.stream().map(Facture -> String.valueOf(Facture.getFactureId())) // extract
                // id
                // from
                // each Model object
                .collect(Collectors.toList()); //
        List<String> avoirList = new ArrayList<String>();
        if (avrArray.size() > 0) {
          List<AvoirClient> existeAvoir =
              avoirRepository.findAllAvoirbyListeReferenceAndNotPayed(avrArray);
          if (existeAvoir.size() == avrArray.size())
            avoirList = existeAvoir.stream().map(Avoir -> String.valueOf(Avoir.getAvoirId()))
                .collect(Collectors.toList());
        }
        logger.info("parms  API : " + idList  + " " + avoirList + " "+user + " "+methodepayment + " " + Bankname + " "+TransactionId );

        List<Payement> payementFacture = payementsService.createNewPaymentMultiple(idList,
            avoirList, user, methodepayment, Bankname, null, null, TransactionId, true, request);
        if (payementFacture != null) {
          dataRespenseObject.put("success", true);
          dataRespenseObject.put("message", "payement avec success");

        } else {
          dataRespenseObject.put("success", false);
          dataRespenseObject.put("message", "erreur avec Payement");
        }
      } else {
        dataRespenseObject.put("success", false);
        dataRespenseObject.put("message", "facture Ids non existe ou non conforme");
      }
      dataRespenseObject.put("code", 200);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.info("payeementFacture Mesage API : " + dataRespenseObject.get("message"));
    } catch (Exception e) {
      logger.info("Error payeementFacture API Exption: " + e.getMessage());
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("message", e.getMessage());
      dataRespenseObject.put("code", 200);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @PostMapping(value = "/getAbonneeCRM", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HashMap<String, Object>> getAbonnement(
      @RequestParam(required = false) String identifiant,
      @RequestParam(required = false) String num_fixe) {

    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();

    if (identifiant == null || num_fixe == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "PARAMS_MISSING");
      dataRespenseObject.put("message",
          "Les paramètres identifiant (CIN/carte sejour) et num_fixe sont manquants.");
      dataRespenseObject.put("data", null);
      logger.error("API getAbonneeCRM (identifiant = " + identifiant + " / num_fixe = " + num_fixe
          + ") identifiant et num_fixe sont manquants.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      return response;
    }

    Long numFixe = null;
    if (num_fixe != null) {
      try {
        numFixe = Long.parseLong(num_fixe);
      } catch (NumberFormatException e) {
        dataRespenseObject.put("success", false);
        dataRespenseObject.put("code", "NUM_FIXE_NOT_VALID");
        dataRespenseObject.put("message",
            "La valeur fournie pour numero fixe n'est pas un nombre valide.");
        dataRespenseObject.put("data", null);
        response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
        logger
            .error("API getAbonneeCRM ( num_fixe : " + num_fixe + ") n'est pas un nombre valide.");
        return response;
      }
    }

    Optional<getAbonnementApi> abonnement =
        abonnementRepository.findByIdentifiantAndNumFixe(numFixe, identifiant);
    if (!abonnement.isPresent()) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "CLIENT_NOT_FOUND");
      dataRespenseObject.put("message", "Aucun abonnement trouvé.");
      dataRespenseObject.put("data", null);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error("API getAbonneeCRM : identifiant = " + identifiant + " / num fixe = " + num_fixe
          + " (abonnement : " + abonnement + ") : Aucun abonnement trouvé.");
      return response;
    }

    dataRespenseObject.put("success", true);
    dataRespenseObject.put("message", null);
    dataRespenseObject.put("data", abonnement);
    response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
    logger.info("API getAbonneeCRM avec success : identifiant : " + identifiant + " num_fixe : "
        + num_fixe + " abonnement : " + abonnement);
    return response;
  }

  @PostMapping(value = "/saveWebsiteAccount", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HashMap<String, Object>> saveWebsiteAccount(
      @RequestParam(required = false) String ref_clientsite,
      @RequestParam(required = false) String ref_abonnement) {

    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();

    if (ref_clientsite == null || ref_abonnement == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "PARAMS_MISSING");
      dataRespenseObject.put("message",
          "Les paramètres ref_clientsite ou ref_abonnement sont manquants.");
      dataRespenseObject.put("data", null);
      logger.error("API saveWebsiteAccount : ref_clientsite = " + ref_clientsite
          + " ref_abonnement = " + ref_abonnement + " sont manquants");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      return response;
    }

    String referenceClient = null;
    String refClientSite = null;
    Object[] abn = abonnementRepository.findtest(ref_abonnement);
    if (abn.length > 0) {
      Object[] row = (Object[]) abn[0];
      referenceClient = (String) row[0];
      refClientSite = (String) row[1];
    }
    if (referenceClient == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "CLIENT_NOT_FOUND");
      dataRespenseObject.put("message", "Aucun abonnement trouvé.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error("API saveWebsiteAccount : reference abonnement + " + ref_abonnement
          + " / ref_clientsite = " + ref_clientsite + " n'existe pas");
      return response;
    }

    if (refClientSite != null) {
      dataRespenseObject.put("code", "REF_ALREADY_EXIST");
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("message", "Ce client a déjà une référence de site associée.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error("API saveWebsiteAccount : reference client site = " + ref_clientsite
          + " a déjà une référence de site associée.");
      return response;
    }

    Boolean verif = abonnementRepository.existsByReference(ref_clientsite);
    if (verif == false) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "REF_ALREADY_USED");
      dataRespenseObject.put("message", "La référence client site est déjà utilisée.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error(
          "API saveWebsiteAccount : reference site = " + ref_clientsite + " est déjà utilisée..");
      return response;
    } else {
      abonnementRepository.updateRefCLientSite(ref_abonnement, ref_clientsite);
      dataRespenseObject.put("success", true);
      dataRespenseObject.put("message", "Success");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.info("API saveWebsiteAccount : reference site = " + ref_clientsite
          + " reference abonnement = " + ref_abonnement + " est ajouté avec succès");
      return response;
    }
  }

  @PostMapping(value = "/getAllFacture", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HashMap<String, Object>> getAllFacture(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "3") Integer sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder, @RequestParam String search,
      @RequestParam(required = false) String ref_abonnement) throws ParseException, SQLException {

    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();
    String recherche = "";
    if (ref_abonnement == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "REF_REQUIRED");
      dataRespenseObject.put("message", "la référence d'abonnement est obligatoire.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error("API getAllFacture : ref_abonnement = " + ref_abonnement + " est obligatoire.");
      return response;
    }

    Long abonnementId = abonnementRepository.getClientIdByReference(ref_abonnement);
    if (abonnementId == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "CLIENT_NOT_FOUND");
      dataRespenseObject.put("message", "Aucun abonnement trouvé.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error(
          "API getAllFacture : ref_abonnement = " + ref_abonnement + " Aucun abonnement trouvé");
      return response;
    }
    int currentpage = page / size;
    Sort.Direction direction = sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

    String sorting;
    switch (sortBy) {
      case 1:
        sorting = "ref_facture";
        break;
      case 3:
        sorting = "date_de_debut"; // "dateDeDebut";
        break;
      case 4:
        sorting = "date_de_fin"; // "dateDeFin";
        break;
      case 5:
        sorting = "montant_payer";
        break;
      case 6:
        sorting = "date_echeance";
        break;
      case 7:
        sorting = "etat_facture";
        break;
      default:
        sorting = "date_de_fin"; // "dateDeFin";
    }
    Sort sort = Sort.by(direction, sorting);

    Pageable pageable = PageRequest.of(currentpage, size, sort);

    // Page<FactureDTO> pageListeFacture =
    // factureRepository.findByAbonnementId(pageable, abonnementId, search);

    // Page<InvoiceAvoirDTO> pageListeFacture =
    // invoiceAvoirService.getInvoiceAvoirData(abonnementId,
    // sorting, sortOrder, currentpage + 1, size);
    if (search != null && !search.isEmpty()) {
      recherche = search;
    }
    Facture factureFirstVisible = factureRepository.findFirstByAbonnement_clientid(abonnementId);

    Page<InvoiceAvoir> pageListeFacture =
        factureRepository.findfactureAvoirByAbonnementId(pageable, abonnementId, recherche);
    List<FactureDTO> invoicesAvoirDTOList = new ArrayList<>();

    for (InvoiceAvoir invoiceAvoir : pageListeFacture) {
      FactureDTO factureDTO = new FactureDTO();
      if (invoiceAvoir.getFacture_id() == null) {
        factureDTO.setDate_echeance(null);;
        factureDTO.setDateDeDebut(invoiceAvoir.getDate_de_debut());
        factureDTO.setDateDeFin(null);
        factureDTO.setTypeFacture("avoir");
      } else {
        factureDTO.setDate_echeance(invoiceAvoir.getDate_echeance());;
        factureDTO.setDateDeDebut(invoiceAvoir.getDate_de_debut());
        factureDTO.setDateDeFin(invoiceAvoir.getDate_de_fin());
        factureDTO.setTypeFacture("facture");
      }
      factureDTO.setEtat_facture(invoiceAvoir.getEtat_facture());
      factureDTO.setMontant_payer(invoiceAvoir.getMontant_payer());
      factureDTO.setRef_facture(invoiceAvoir.getRef_facture());

      invoicesAvoirDTOList.add(factureDTO);
    }
    Page<FactureDTO> pageableInvoicesAvoirDTO = new PageImpl<>(invoicesAvoirDTOList,
        pageListeFacture.getPageable(), pageListeFacture.getTotalElements());
    if (factureFirstVisible != null && factureFirstVisible.getIsFirstFacture()
        && factureFirstVisible.getVisibility()) {
      dataRespenseObject.put("success", true);
      dataRespenseObject.put("message", null);
      dataRespenseObject.put("data", pageableInvoicesAvoirDTO);
      dataRespenseObject.put("showFacture", true);
      logger.info("API getAllFacture avec success : ref_abonnement : " + ref_abonnement
          + " ( data: " + pageListeFacture + " ) ");
    } else {
      dataRespenseObject.put("success", true);
      dataRespenseObject.put("message", null);
      dataRespenseObject.put("data", pageableInvoicesAvoirDTO);
      dataRespenseObject.put("showFacture", false);
    }
    response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);

    return response;
  }

  @PostMapping("/facture")
  public ResponseEntity<HashMap<String, Object>> getFacturePdf(
      @RequestParam(required = false) String refFacture,
      @RequestParam(required = false) String typeFacture) throws JRException, Exception {

    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();

    if (refFacture == null && typeFacture != null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "PARAMS_MISSING");
      dataRespenseObject.put("message", "Le paramètre refFacture  ou typeFacture est manquant.");
      dataRespenseObject.put("data", null);
      logger.error("API /facture : Le paramètre refFacture : " + refFacture + " est manquant.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      return response;
    }
    Facture facture = null;
    AvoirClient avoir = null;
    if (typeFacture.equals("facture")) {
      logger.info("reference facture" + refFacture);
      facture = factureRepository.findByRefFacture(refFacture);
    }
    if (typeFacture.equals("avoir")) {
      logger.info("reference avoir" + refFacture);
      avoir = avoirRepository.findAvoirClientByRefAvoirClient(refFacture);
    }
    if (facture == null && avoir == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "FACTURE_NOT_FOUND");
      dataRespenseObject.put("message", "Aucun facture trouvé.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error("API /facture : refFacture = " + refFacture + " Aucun facture trouvé");
      return response;
    }
    Resource resource = null;

    if (facture != null) {
      Date date = facture.getCreatedDate();

      int year = CrmUtils.getYearFromDate(date);
      int month = CrmUtils.getMonthFromDate(date);
      DecimalFormat decimalFormat = new DecimalFormat("00");
      String formattedMonth = decimalFormat.format(month);

      resource = new FileSystemResource(pathFacture + year + "/" + formattedMonth + "/FACTURE_"
          + facture.getRef_facture() + ".pdf");
    } else if (avoir != null) {
      resource = new FileSystemResource(pathAvoir + CrmUtils.getYear() + "/" + CrmUtils.getMonth()
          + "/" + avoir.getRefAvoirClient() + ".pdf");
    }
    logger.info("facture api file : resource existe" + resource.exists());
    if (resource.exists()) {
      byte[] pdfBase64 = convertPDFToBase64(resource);

      dataRespenseObject.put("success", true);
      dataRespenseObject.put("data", pdfBase64);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.info("API /facture avec success : refFacture = " + refFacture + " fichier : "
          + resource.getFile());
      return response;
    } else {
      if (typeFacture.equals("facture") && facture != null) {
        Date date = facture.getCreatedDate();
        logger.info("facture api file : generation de facture avec reference" + refFacture);
        int year = CrmUtils.getYearFromDate(date);
        int month = CrmUtils.getMonthFromDate(date);
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String formattedMonth = decimalFormat.format(month);
        factureService.createPDFFactureA4(facture);
        resource = new FileSystemResource(pathFacture + year + "/" + formattedMonth + "/FACTURE_"
            + facture.getRef_facture() + ".pdf");
      } else {
        avoirService.createPDFAvoirA4(avoir);
        resource = new FileSystemResource(pathAvoir + CrmUtils.getYear() + "/" + CrmUtils.getMonth()
            + "/" + avoir.getRefAvoirClient() + ".pdf");
      }
      byte[] pdfBase64 = convertPDFToBase64(resource);
      dataRespenseObject.put("success", true);
      dataRespenseObject.put("data", pdfBase64);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.info("API /facture , création fichier avec success : refFacture = " + refFacture
          + " fichier : " + resource.getFile());
      return response;
    }
  }

  public byte[] convertPDFToBase64(Resource resource) throws IOException {

    // récupère un flux d'entrée (InputStream) à partir de l'objet Resource. L'InputStream permet de
    // lire les données du fichier.
    InputStream inputStream = resource.getInputStream();

    // crée un flux de sortie (ByteArrayOutputStream). Il s'agit d'un tampon dans lequel les données
    // lues à partir de l'InputStream seront accumulées.
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // un tableau de bytes est créé pour stocker les données lues à partir de l'InputStream.
    byte[] buffer = new byte[1024];

    // utilisée pour stocker le nombre de bytes lus à chaque itération de la boucle.
    int bytesRead;

    // Cette boucle lit les données du fichier PDF par morceaux et les stocke dans le buffer.
    // La méthode read(buffer) de l'InputStream renvoie le nombre de bytes réellement lus à chaque
    // itération.
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }

    byte[] pdfData = outputStream.toByteArray();
    return pdfData;
  }

  @PostMapping(value = "/getInfoContratAbonnee", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HashMap<String, Object>> saveWebsiteAccount(
      @RequestParam(required = false) String ref_abonnement) {

    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();

    List<ContratApiDTO> listAllAbonnementByRef =
        abonnementRepository.findAllByReference(ref_abonnement);

    if (listAllAbonnementByRef.size() > 0) {
      dataRespenseObject.put("success", true);
      dataRespenseObject.put("code", "SUCCESS");
      dataRespenseObject.put("message", null);
      dataRespenseObject.put("data", listAllAbonnementByRef);
      logger.info("API /getInfoContratAbonnee avec success : ref_abonnement = " + ref_abonnement);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
    } else {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "CONTRAT_NOT_FOUND");
      dataRespenseObject.put("message", "Aucun contrat trouvé.");
      dataRespenseObject.put("data", listAllAbonnementByRef);
      logger.error("API /getInfoContratAbonnee : ref_abonnement = " + ref_abonnement
          + " Aucun contrat trouvé.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
    }

    return response;
  }

  @PostMapping("/getContratFile")
  public ResponseEntity<HashMap<String, Object>> getContratFile(
      @RequestParam(required = false) String reference) throws JRException, Exception {

    DemandeAbonnement dAbn =
        demandeAbonnementRepository.findDemandeAbonnementByReferenceChifco(reference);
    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();
    if (dAbn == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "CLIENT_NOT_FOUND");
      dataRespenseObject.put("message", "Aucun client trouvé.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error(
          "API /getContratFile : reference abonnement : " + reference + "  Aucun client trouvé.");
      return response;
    }
    String nomContratFile = dAbn.getContratPdf();

    if (nomContratFile != null) {
      File directory = new File(pathDemandesAbonnement + dAbn.getCin() + "/");
      File[] files = directory.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.contains(nomContratFile);
        }
      });

      /*
       * File directory = new File(pathDemandesAbonnement + dAbn.getCin() + "/"); File[] files =
       * directory.listFiles(new FilenameFilter() {
       * 
       * @Override public boolean accept(File dir, String name) { return
       * name.contains(nomContratFile); } });
       */

      if (files != null && files.length > 0) {
        File firstFile = files[0]; // Récupérer le premier fichier correspondant
        Resource resource = new FileSystemResource(firstFile);
        byte[] pdfBase64 = convertPDFToBase64(resource);
        dataRespenseObject.put("success", true);
        dataRespenseObject.put("data", pdfBase64);
        response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
        logger.info("API /getContratFile avec success : reference abonnement : " + reference
            + " ( contrat : " + resource.getFile() + " ).");
        return response;
      } else {
        dataRespenseObject.put("success", false);
        dataRespenseObject.put("code", "CONTRAT_NOT_FOUND");
        dataRespenseObject.put("message", "Aucun contrat trouvé.");
        response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
        logger.error("API /getContratFile : reference abonnement : " + reference
            + " Aucun fichier contrat trouvé.");
      }
    } else {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "CONTRAT_NOT_FOUND");
      dataRespenseObject.put("message", "Demande n'a pas encore un contart.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);

    }
    return response;

  }


  @PostMapping(value = "/updateAccount", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HashMap<String, Object>> updateAccount(
      @RequestParam(required = false) String ref_abonnement,
      @RequestParam(required = false) String numTel, @RequestParam(required = false) String email) {

    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();

    if (numTel == null || email == null || ref_abonnement == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "PARAMS_MISSING");
      dataRespenseObject.put("message",
          "Les paramètres ref_abonnement , numTel et email sont manquants.");
      dataRespenseObject.put("data", null);
      logger.error("API updateAccount : ref_abonnement = " + ref_abonnement + " numTel = " + numTel
          + " email " + email + " sont manquants");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      return response;
    }

    Long tel = null;
    if (numTel != null) {
      try {
        tel = Long.parseLong(numTel);
      } catch (NumberFormatException e) {
        dataRespenseObject.put("success", false);
        dataRespenseObject.put("code", "NUM_FIXE_NOT_VALID");
        dataRespenseObject.put("message",
            "La valeur fournie pour numero tel n'est pas un nombre valide.");
        dataRespenseObject.put("data", null);
        response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
        logger.error("API updateAccount ( num_fixe : " + numTel + ") n'est pas un nombre valide.");
        return response;
      }
    }

    Long id = abonnementRepository.getClientIdByReference(ref_abonnement);
    if (id == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "CLIENT_NOT_FOUND");
      dataRespenseObject.put("message", "Aucun abonnement trouvé.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger
          .error("API updateAccount : reference abonnement + " + ref_abonnement + " n'existe pas");
      return response;
    }

    abonnementRepository.updateClient(ref_abonnement, tel, email);
    dataRespenseObject.put("success", true);
    dataRespenseObject.put("message", "Success");
    response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
    logger.info("API updateAccount : rref_abonnement = " + ref_abonnement + " numTel = " + numTel
        + " email = " + numTel + " est modifier avec succès");
    return response;
  }

  // Api pour demande une clé antiVirus
  @PostMapping(value = "/getAntiVirusKey", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HashMap<String, Object>> getAntiVirusKey(
      @RequestParam(required = false) String ref_abonnement,
      @RequestParam(required = false) String type) {

    logger.info("getAntiVirusKey API endpoint called.");

    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();

    // Vérifier si le paramètre 'ref_abonnement' est manquant
    if (ref_abonnement == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "PARAMS_MISSING");
      dataRespenseObject.put("message", "Le paramètre ref_abonnement est manque.");
      dataRespenseObject.put("data", null);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error("Le paramètre ref_abonnement est manquant.");
      return response;
    }

    // Vérifier si l'abonnement correspondant au client avec la référence 'ref_abonnement' n'a pas
    // été trouvé
    Abonnement ab = abonnementRepository.findAbonnementByReferenceClient(ref_abonnement);
    if (ab == null) {
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("code", "CLIENT_NOT_FOUND");
      dataRespenseObject.put("message", "Aucun abonnement trouvé.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error("Aucun abonnement trouvé pour la référence " + ref_abonnement + ".");
      return response;
    }

    AntivirusKey antivirusKey = new AntivirusKey();

    antivirusKey = antivirusKeyRepository.getKeyByClient(ab.getClientid(), type);
    if (antivirusKey != null) {
      dataRespenseObject.put("success", true);
      dataRespenseObject.put("code", "SUCCESS");
      dataRespenseObject.put("message", antivirusKey.getLicenseKey());
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      return response;
    }

    // Récupérer la première clé antivirus disponible
    antivirusKey = antivirusKeyRepository.getFirstAntivirusKey(type);
    if (antivirusKey == null) {
      dataRespenseObject.put("success", true);
      dataRespenseObject.put("code", "NO_KEYS_AVAILABLE");
      dataRespenseObject.put("message", "Aucune clé disponible.");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      return response;
    }

    antivirusKey.setDateAffectation(new Date());
    antivirusKey.setAbonnement(ab);

    User user = userservice.findTop1UsersByEmail("demandeAbonnementWebSite@nety.tn");
    antivirusKey.setAffectedBy(user);

    // Vérification si l'utilisateur de siteWeb n'a pas été trouvé (user est null)
    if (user == null) {
      dataRespenseObject.put("success", true);
      dataRespenseObject.put("code", "USER_NOT_FOUND");
      dataRespenseObject.put("message", "l'utilisateru de webSite non trouvé .");
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
      logger.error("Utilisateur avec email demandeAbonnementWebSite@nety.tn non trouvé.");
      return response;
    }
    antivirusKeyRepository.save(antivirusKey);

    dataRespenseObject.put("success", true);
    dataRespenseObject.put("code", "SUCCESS");
    dataRespenseObject.put("message", antivirusKey.getLicenseKey());
    response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
    logger.info("Clé antivirus " + antivirusKey.getLicenseKey()
        + " affectée avec succès pour l'abonnement " + ref_abonnement + ".");
    return response;
  }


  @RequestMapping(method = RequestMethod.POST, value = "auth")
  public ResponseEntity<HashMap<String, Object>> authenticateUser(
      @RequestBody RunPayLogin loginDto) {
    HashMap<String, Object> returnapi = new HashMap<String, Object>();
    try {
      if ((loginDto.getClientSecret().equals(clientSecretRunPay)
          && loginDto.getGrant_type().equals(grantTypeRunPay))
          || (loginDto.getClientSecret().equals(clientSecretEnda)
              && loginDto.getGrant_type().equals(grantTypeEnda))
          || (loginDto.getClientSecret().equals(clientSecretMazam)
              && loginDto.getGrant_type().equals(grantTypeMazam))) {

        String jwt = utilsJwt.generateJwtToken(loginDto.getGrant_type());

        returnapi.put("msg", "Connexion réussie de l’utilisateur!.");
        returnapi.put("jwt", jwt);

      } else {
        returnapi.put("msg", "Échec de la connexion utilisateur !!.");
      }
      return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);

    } catch (BadCredentialsException e) {
      logger.error("signin Acs Api : " + e);
      returnapi.put("msg", "Échec de la connexion utilisateur !!.");
      return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);

    }

  }

  @RequestMapping(method = RequestMethod.GET, value = "getway_liste_facture_non_payee")
  public ResponseEntity<HashMap<String, Object>> listeFactureNonPayeeGetway(
      @RequestParam("identifiant") String recherche,
      @RequestParam("type_identif") String typeIdentif, HttpServletRequest request) {
    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataObject = new HashMap<String, Object>();
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();
    List<ListeFactureNonPayeDTO> ListFactureNonPayee = new ArrayList<ListeFactureNonPayeDTO>();
    List<ListeFactureAndAvoirNonPayeDTO> ListFactureNonPayeeByInterface =
        new ArrayList<ListeFactureAndAvoirNonPayeDTO>();
    try {
      String jwt = utilsJwt.parseJwt(request);
      if (jwt != null && utilsJwt.validateJwtToken(jwt)) {
        Long telephone = null;
        if (CrmUtils.Isnumber(recherche)) {
          telephone = Long.parseLong(recherche);

        }
        logger.info("listeFactureNonPayee identifiant : " + recherche);
        logger.info("listeFactureNonPayee type_identif : " + typeIdentif);
        Abonnement Abonnement = null;

        if (typeIdentif.equals("2")) {
          Facture facture = factureService.findFactureNonPayeeByReference(recherche);
          if (facture != null) {
            Abonnement = facture.getAbonnement();
            ListFactureNonPayeeByInterface =
                factureRepository.findListeFactureNonPayeeByCinForApi(Abonnement.getCin());

            logger.info("Recuperation Id facture avec la recherche  de reference facture : "
                + facture.getFactureId());
          }

        } else if (typeIdentif.equals("1")) {
          Abonnement = abonnementService.findAbonnementByCin(recherche);
          // ListFactureNonPayee = factureService.findListeFactureNonPayeeByCin(recherche);
          ListFactureNonPayeeByInterface =
              factureRepository.findListeFactureNonPayeeByCinForApi(recherche);

        } else if (typeIdentif.equals("3")) {
          // ListFactureNonPayee = factureService.findListeFactureNonPayeeByFixeNumber(telephone);
          ListFactureNonPayeeByInterface =
              factureRepository.findListeFactureNonPayeeByFixeNumberForApi(telephone);
          Abonnement = abonnementService.findUserByFixeNumber(telephone);
        }

        if (typeIdentif.equals("1") || typeIdentif.equals("3") || typeIdentif.equals("2")) {
          ListFactureNonPayeeByInterface.forEach(el -> {
            ListeFactureNonPayeDTO newFactureNonPayee = new ListeFactureNonPayeDTO();
            newFactureNonPayee.setDateDeDebut(el.getDateDeDebut());
            newFactureNonPayee.setDateDeFin(el.getDateDeFin());
            newFactureNonPayee.setEcheance(el.getEcheance());
            newFactureNonPayee.setRef_facture(el.getRef_facture());
            newFactureNonPayee.setTotal_ttc(el.getMontant_payer());
            newFactureNonPayee.setTypeFacture(el.getTypeFacture());
            if (el.getTypeFacture().equals("avoire")) {
              newFactureNonPayee.setIsrequired(true);
            }
            ListFactureNonPayee.add(newFactureNonPayee);
          });
        }
        HashMap<String, Object> clientInformation = new HashMap<String, Object>();
        if (Abonnement != null) {
          logger.info("Preparation Object Abonnement");
          clientInformation.put("id", Abonnement.getClientid());
          clientInformation.put("name", Abonnement.getFirstName() + " " + Abonnement.getLastName());
          clientInformation.put("ref_abonnement", Abonnement.getReferenceClient());
          clientInformation.put("num_fixe", Abonnement.getTelFixe());
          clientInformation.put("num_mobile", Abonnement.getTelMobile());
        }

        for (ListeFactureNonPayeDTO factureNonPayee : ListFactureNonPayee) {

        }
        if (Abonnement != null && ListFactureNonPayee.size() > 0) {
          Facture factureFirstVisible =
              factureRepository.findFirstByAbonnement_clientid(Abonnement.getClientid());
          if (factureFirstVisible != null && factureFirstVisible.getIsFirstFacture()
              && factureFirstVisible.getVisibility()) {

            dataRespenseObject.put("showfacture", true);
          } else {

            dataRespenseObject.put("showfacture", false);

          }
          dataObject.put("client", clientInformation);
          dataObject.put("factures", ListFactureNonPayee);
          dataRespenseObject.put("success", true);
          dataRespenseObject.put("message", "");
          dataRespenseObject.put("code", 200);
          dataRespenseObject.put("data", dataObject);
        } else {
          if (Abonnement == null) {
            switch (typeIdentif) {
              case "1": {
                dataRespenseObject.put("code", "NC");
                dataRespenseObject.put("message", "num cin not found ");
              }
                break;
              case "3": {
                dataRespenseObject.put("code", "NL");
                dataRespenseObject.put("message", "num ligne not found");

              }
                break;
              case "2": {
                dataRespenseObject.put("code", "NF");
                dataRespenseObject.put("message", "num facture not found ");
              }
                break;
              default: {
                dataRespenseObject.put("code", "TypeInvalide");
                dataRespenseObject.put("message", "type_identif invalide ");
              }

            }
          } else {
            dataRespenseObject.put("code", "NOF");
            dataRespenseObject.put("message", "AUNCUNE FACTURE A PAYER ");
          }

          dataRespenseObject.put("success", false);
          dataRespenseObject.put("data", null);
          dataRespenseObject.put("showfacture", false);
        }

      } else {
        dataRespenseObject.put("success", false);
        dataRespenseObject.put("data", null);
        dataRespenseObject.put("message", "token non valide ou manquant");

      }

      logger.info("code liste FactureNonPayee API  : " + dataRespenseObject.get("code"));
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
    } catch (Exception e) {
      // List<ListeFactureNonPayeDTO> ListFactureNonPayee = new ArrayList<>();

      logger.info("liste FactureNonPayee API Exption : " + e);
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("message", e.getMessage());
      dataRespenseObject.put("code", "exception");
      dataRespenseObject.put("data", null);
      dataRespenseObject.put("showfacture", false);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.POST, value = "getway_payeement_facture")
  public ResponseEntity<HashMap<String, Object>> payeementFacturGetway(HttpServletRequest request,
      @RequestBody PaymentDTO PayementFactures) {
    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();
    try {
      String jwt = utilsJwt.parseJwt(request);
      if (jwt != null && utilsJwt.validateJwtToken(jwt)) {
        List<String> factureids = PayementFactures.getFactures();
        List<String> avrArray = new ArrayList<>();
        List<String> factureArray = new ArrayList<>();

        for (String element : factureids) {
          if (element.startsWith("AVR-")) {
            avrArray.add(element);
          } else {
            factureArray.add(element);
          }
        }
        User user = userservice.findTop1UsersByTypeuser("SYSTEM");
        String methodepayment = "Run Pay"; // PayementFactures.getMethodePayment();
        /*
         * if (methodepayment != null) { methodepayment = "carte bancaire"; }
         */
        String Bankname = PayementFactures.getBankname();
        String TransactionId = PayementFactures.getTransactionId();
        logger.info("payeementFacture TranscationId : " + TransactionId);
        logger
            .info("payeementFacture Liste des Reference factures a payer : " + factureArray.size());
        List<Facture> existeFacture =
            factureService.findListeFactureNonPayeeByRefFacture(factureArray);
        if (existeFacture != null && existeFacture.size() == factureArray.size()) {
          List<String> idList =
              existeFacture.stream().map(Facture -> String.valueOf(Facture.getFactureId())) // extract
                  // id
                  // from
                  // each Model object
                  .collect(Collectors.toList()); //
          List<String> avoirList = new ArrayList<String>();
          if (avrArray.size() > 0) {
            List<AvoirClient> existeAvoir =
                avoirRepository.findAllAvoirbyListeReferenceAndNotPayed(avrArray);
            if (existeAvoir.size() == avrArray.size())
              avoirList = existeAvoir.stream().map(Avoir -> String.valueOf(Avoir.getAvoirId()))
                  .collect(Collectors.toList());
          }

          List<Payement> payementFacture = payementsService.createNewPaymentMultiple(idList,
              avoirList, user, methodepayment, Bankname, null, null, TransactionId, true, request);
          if (payementFacture != null) {
            dataRespenseObject.put("success", true);
            dataRespenseObject.put("message", "payement avec success");
            dataRespenseObject.put("TransactionId", TransactionId);


          } else {
            dataRespenseObject.put("success", false);
            dataRespenseObject.put("message", "erreur avec Payement");
          }
        } else {
          dataRespenseObject.put("success", false);
          dataRespenseObject.put("message", "facture Ids non existe ou non conforme");
        }
        dataRespenseObject.put("code", 200);
        response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
        logger.info("payeementFacture Mesage API : " + dataRespenseObject.get("message"));
      } else {
        dataRespenseObject.put("message", "token non valide ou manquant");
        dataRespenseObject.put("success", false);

      }
    } catch (Exception e) {
      logger.info("Error payeementFacture API Exption: " + e.getMessage());
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("message", e.getMessage());
      dataRespenseObject.put("code", 200);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.POST, value = "enda_payeement_facture")
  public ResponseEntity<HashMap<String, Object>> payeementFacturGetwayEnda(
      HttpServletRequest request, @RequestBody PaymentDTO PayementFactures) {
    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();
    try {
      String jwt = utilsJwt.parseJwt(request);
      if (jwt != null && utilsJwt.validateJwtToken(jwt)) {
        List<String> factureids = PayementFactures.getFactures();
        List<String> avrArray = new ArrayList<>();
        List<String> factureArray = new ArrayList<>();

        for (String element : factureids) {
          if (element.startsWith("AVR-")) {
            avrArray.add(element);
          } else {
            factureArray.add(element);
          }
        }
        User user = userservice.findTop1UsersByTypeuser("SYSTEM");
        String methodepayment = "Enda"; // PayementFactures.getMethodePayment();
        /*
         * if (methodepayment != null) { methodepayment = "carte bancaire"; }
         */
        String Bankname = PayementFactures.getBankname();
        String TransactionId = PayementFactures.getTransactionId();
        logger.info("payeementFacture TranscationId : " + TransactionId);
        logger
            .info("payeementFacture Liste des Reference factures a payer : " + factureArray.size());
        List<Facture> existeFacture =
            factureService.findListeFactureNonPayeeByRefFacture(factureArray);
        if (existeFacture != null && existeFacture.size() == factureArray.size()) {
          List<String> idList =
              existeFacture.stream().map(Facture -> String.valueOf(Facture.getFactureId())) // extract
                  // id
                  // from
                  // each Model object
                  .collect(Collectors.toList()); //
          List<String> avoirList = new ArrayList<String>();
          if (avrArray.size() > 0) {
            List<AvoirClient> existeAvoir =
                avoirRepository.findAllAvoirbyListeReferenceAndNotPayed(avrArray);
            if (existeAvoir.size() == avrArray.size())
              avoirList = existeAvoir.stream().map(Avoir -> String.valueOf(Avoir.getAvoirId()))
                  .collect(Collectors.toList());
          }

          List<Payement> payementFacture = payementsService.createNewPaymentMultiple(idList,
              avoirList, user, methodepayment, Bankname, null, null, TransactionId, true, request);
          if (payementFacture != null) {
            dataRespenseObject.put("success", true);
            dataRespenseObject.put("message", "payement avec success");
            dataRespenseObject.put("TransactionId", TransactionId);


          } else {
            dataRespenseObject.put("success", false);
            dataRespenseObject.put("message", "erreur avec Payement");
          }
        } else {
          dataRespenseObject.put("success", false);
          dataRespenseObject.put("message", "facture Ids non existe ou non conforme");
        }
        dataRespenseObject.put("code", 200);
        response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
        logger.info("payeementFacture Mesage API : " + dataRespenseObject.get("message"));
      } else {
        dataRespenseObject.put("message", "token non valide ou manquant");
        dataRespenseObject.put("success", false);

      }
    } catch (Exception e) {
      logger.info("Error payeementFacture API Exption: " + e.getMessage());
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("message", e.getMessage());
      dataRespenseObject.put("code", 200);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.POST, value = "addDemandeParinage",
      consumes = MediaType.ALL_VALUE)
  public ResponseEntity<HashMap<String, Object>> addDemandeParinage(String parrain,
      String cinParrain, String fullname, String cin, String tel,
      @RequestParam MultipartFile cinRecto, @RequestParam MultipartFile cinVerso, String email,
      String website) {
    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();

    try {
      /*
       * Boolean saveParinage(String cinParrain, String cinParinee, String nomParrain, String
       * prenomParrain, String nomParinee, String prenomParinee, String telFixe)
       */
      logger.info(cinParrain, cin, parrain, fullname, tel, cinRecto, cinVerso, email, website);
      Boolean prinageService = parinageService.saveParinage(cinParrain, cin, parrain, fullname, tel,
          cinRecto, cinVerso, email, website);
      if (prinageService) {
        String SendEmailToBO = HtmlTemplateEmail.HtmlEmailParrainage(parrain, fullname, cin, tel);

        notificationservice.sendSimpleMailHtml(xlsMailResponsable, SendEmailToBO,
            "Demande de Parrainage");
        dataRespenseObject.put("success", true);
        dataRespenseObject.put("message", "done");
        dataRespenseObject.put("code", 200);
      } else {
        dataRespenseObject.put("success", false);
        dataRespenseObject.put("message", "erreur");
        dataRespenseObject.put("code", 400);
      }
      response =
          new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.CREATED);
    } catch (Exception e) {
      logger.info("Error addDemandeParinage Exption: " + e.getMessage());
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("message", e.getMessage());
      dataRespenseObject.put("code", 400);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
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

  @PostMapping(value = "/addComplaint")
  public Map<String, Object> saveReclamation(@RequestParam("reference_nety") String reference_nety,
      @RequestParam("servicetype_id") Long servicetypeId,
      @RequestParam(value = "motif_id", required = false) Long motifId,
      @RequestParam(value = "autre", required = false) String autre,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "justificatif", required = false) MultipartFile[] justificatifFiles,
      String website, @RequestParam(value = "source", required = false) String source) {

    try {
      Reclamation reclamation = new Reclamation();
      Statusrec status = statusrecService.getStatusrecByDesignation(NomStatutReclamation.OPENED);
      Servicetype serviceType = servicetypeService.findbyServicetypeId(servicetypeId);

      if (status == null || serviceType == null) {
        return createResponse(false, HttpStatus.BAD_REQUEST, "Invalid status or service type",
            null);
      }
      Abonnement abo = abonnementService.findAbonnementByReferenceClient(reference_nety);
      if (abo != null) {
        Reclamation reclamationold =
            reclamationRepository.findlastreclamationByclientIdServiceAndCategory("Technique",
                "Client", abo.getClientid());
        if (reclamationold != null
            && !reclamationold.getStatus().getNomStatut().equals(DBEtatTT.Clôturée)
            && reclamationold.getCategory().equals("Client")
            && reclamationold.getServiceType().getCategorytype().equals("Technique")
            && serviceType.getCategorytype().equals("Technique")) {
          return createResponse(false, HttpStatus.OK,
              "Vous avez une autre réclamation téchnique en cours", null);
        }
        User user = null;
        if (website != null) {
          // user
          user = userservice.findTop1UsersByEmail(website);
          if (user != null)
            logger.info("Réclamation de l'utilisateur: " + user.getUserid());
          else {
            user = userservice.findTop1UsersByEmail("demandeAbonnementWebSite@nety.tn");
          }
        } else {
          user = userservice.findTop1UsersByEmail("demandeAbonnementWebSite@nety.tn");
        }

        Motifrec motif = null;
        Long telephone = null;

        if (motifId != null) {
          motif = motifrecService.findById(motifId);
          reclamation.setMotif(motif);
        }
        if (abo != null) {
          reclamation.setClient(abo);
          telephone = abo.getTelMobile();
        }
        if (source != null) {
          reclamation.setSource(source);
        }
        reclamation.setCreatedby(user);
        reclamation.setEditedby(user);
        reclamation.setCategory("Client");
        if (justificatifFiles != null && justificatifFiles.length > 0) {
          List<String> justificatifFileNames = new ArrayList<>();
          for (MultipartFile justificatifFile : justificatifFiles) {
            if (justificatifFile != null && !justificatifFile.isEmpty()) {
              String fileName = "reclamation_justification_" + System.currentTimeMillis() + "_"
                  + CrmUtils.noSpecialCharacters(justificatifFile.getOriginalFilename());
              CrmUtils.saveImageReclamation(justificatifFile, "", pathReclamation, fileName);
              justificatifFileNames.add(fileName);
            }
          }
          reclamation.setJustificatifs(justificatifFileNames);
        }
        reclamation.setStatus(status);
        reclamation.setAutre(autre);
        reclamation.setDescription(description);
        reclamation.setServiceType(serviceType);
        reclamationService.saveReclamation(reclamation);
        reclamationHistoryService.insertNewHistoryclaims(reclamation,
            "Création d'une réclamation sous référence:" + reclamation.getRef_reclamation(), user);
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
        if (reclamation.getCategory().equals("Client")) {
          String EmailReclamation = HtmlTemplateEmail.HtmlEmailReclamation(
              reclamation.getClient().getFirstName() + " " + reclamation.getClient().getLastName(),
              reclamation.getServiceType().getCategorytype(), reclamation.getMotif().getNomMotif(),
              reclamation.getRef_reclamation());

          notificationservice.sendSimpleMailHtml(xlsMailResponsable, EmailReclamation,
              "demande de rclamation");
        }
        return createResponse(true, HttpStatus.OK, "Réclamation a été ajouté avec succée", null);
      } else {
        return createResponse(false, HttpStatus.OK, "Vous n'êtes pas un client Nety", null);
      }

    } catch (Exception e) {
      logger.error("Error saving reclamation", e);
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while saving the reclamation", null);
    }
  }

  @GetMapping("/listReclamation")
  public Map<String, Object> listReclamation(@RequestParam(required = false) Long client_id,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String ref_reclamation,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) Long serviceTypeId,
      @RequestParam(required = false) Long statusId,
      @RequestParam(required = false) String identifiant,
      @RequestParam(required = false) Long telfixe,
      @RequestParam(required = false) String referencenety,
      @RequestParam(required = false) String source,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
      @RequestParam(defaultValue = "createdDate") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection,
      @RequestParam(required = false) String status) {

    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<Reclamation> reclamations = reclamationService.findAllReclamationAPI(client_id,
          ref_reclamation, category, serviceTypeId, statusId, identifiant, telfixe, referencenety,
          source, startDate, endDate, status, pageable);

      if (reclamations == null) {
        reclamations = Page.empty(pageable);
      }
      Page<ReclamationDto> dtoPage = reclamations.map(ReclamationDto::fromEntity);
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Liste des réclamations (catégorie = Client)");
      response.put("data", dtoPage.getContent());
      response.put("totalElements", dtoPage.getTotalElements());
      response.put("totalPages", dtoPage.getTotalPages());
      response.put("currentPage", dtoPage.getNumber());
      response.put("pageSize", dtoPage.getSize());
      response.put("empty", dtoPage.isEmpty());

      return response;

    } catch (Exception e) {
      logger.error("Error in listReclamation", e);
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("success", false);
      errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      errorResponse.put("message", "Erreur lors de la récupération des réclamations");
      return errorResponse;
    }
  }

  @GetMapping("/isClientNety")
  public Map<String, Object> checkIfClientNety(@RequestParam(required = false) String identifiant,
      @RequestParam(required = false) Long telfixe,
      @RequestParam(required = false) String referencenety) {

    try {
      Map<String, Object> response = new HashMap<>();
      Abonnement abonnement = null;
      String searchType = "";
      if (identifiant != null && !identifiant.trim().isEmpty()) {
        abonnement = abonnementService.findAbonnementByCin(identifiant.trim());
        searchType = "CIN";
      } else if (telfixe != null) {
        abonnement = abonnementService.findUserByFixeNumber(telfixe);
        searchType = "Téléphone";
      } else if (referencenety != null && !referencenety.trim().isEmpty()) {
        abonnement = abonnementService.findAbonnementByReferenceClient(referencenety.trim());
        searchType = "Référence";
      }

      if (abonnement != null) {
        Map<String, Object> clientData = new HashMap<>();
        clientData.put("reference_nety", abonnement.getReferenceClient());
        clientData.put("cin", abonnement.getCin());
        clientData.put("nom", abonnement.getFirstName());
        clientData.put("prenom", abonnement.getLastName());
        clientData.put("telephone", abonnement.getTelFixe());
        clientData.put("email", abonnement.getEmail());
        clientData.put("adresse", abonnement.getAdresse());
        clientData.put("client_id", abonnement.getClientid());
        response.put("success", true);
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Client Nety trouvé");
        response.put("client", clientData);
        response.put("search_type", searchType);
      } else {
        response.put("success", false);
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", "Vous n'êtes pas un client Nety");
        response.put("client", null);
      }

      return response;

    } catch (Exception e) {
      logger.error("Erreur dans checkIfClientNety", e);
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("success", false);
      errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      errorResponse.put("message", "Erreur lors de la vérification du client");
      return errorResponse;
    }
  }

  @GetMapping("/detailReclamation/{id}")
  public Map<String, Object> getReclamationDetail(@PathVariable Long id) {
    try {
      Reclamation reclamation = reclamationService.getReclamationById(id);

      if (reclamation == null) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("message", "Réclamation non trouvée avec l'ID: " + id);
        errorResponse.put("data", null);
        return errorResponse;
      }
      ReclamationDto reclamationDto = ReclamationDto.fromEntity(reclamation);
      Map<String, Object> additionalInfo = new HashMap<>();
      if (reclamation.getClient() != null) {
        Map<String, Object> clientInfo = new HashMap<>();
        clientInfo.put("clientId", reclamation.getClient().getClientid());
        clientInfo.put("reference", reclamation.getClient().getReferenceClient());
        clientInfo.put("firstName", reclamation.getClient().getFirstName());
        clientInfo.put("lastName", reclamation.getClient().getLastName());
        clientInfo.put("email", reclamation.getClient().getEmail());
        additionalInfo.put("client", clientInfo);
      }
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Détails de la réclamation récupérés avec succès");
      response.put("data", reclamationDto);
      response.put("additionalInfo", additionalInfo);

      return response;

    } catch (Exception e) {
      logger.error("Error fetching reclamation detail for ID: " + id, e);

      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("success", false);
      errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      errorResponse.put("message", "Erreur lors de la récupération des détails de la réclamation");
      errorResponse.put("data", null);
      return errorResponse;
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "informationClient")
  public ResponseEntity<HashMap<String, Object>> informationClient(
      @RequestParam(name = "recherche", required = false) String recherche,
      @RequestParam(name = "type_identif", required = false) String typeIdentif,
      HttpServletRequest request) {

    HashMap<String, Object> dataResponse = new HashMap<>();
    HashMap<String, Object> dataObject = new HashMap<>();
    List<ListeFacturePayeDTO> unpaidInvoices = new ArrayList<>();
    List<ListeFacturePayeDTO> paidInvoices = new ArrayList<>();
    List<ListeFactureAndAvoirDTO> unpaidByInterface = new ArrayList<>();
    List<ListeFactureAndAvoirDTO> paidByInterface = new ArrayList<>();
    Abonnement abonnement = null;
    DemandeAbonnement demandeAbonnement = null;
    List<Radacct> sessionConnected = null;
    HashMap<String, Object> demandeAb = new HashMap<>();
    String etatConnection = "indisponible";
    try {
      String jwt = utilsJwt.parseJwt(request);
      if (jwt != null && utilsJwt.validateJwtToken(jwt)) {
        if (recherche == null) {
          dataResponse.put("code", "400");
          dataResponse.put("message", "recherche est obligatoire");
          dataResponse.put("success", false);
          dataResponse.put("data", null);
          return ResponseEntity.ok(dataResponse);
        }
        if (typeIdentif == null) {
          dataResponse.put("code", "400");
          dataResponse.put("message", "type_identif est obligatoire");
          dataResponse.put("success", false);
          dataResponse.put("data", null);
          return ResponseEntity.ok(dataResponse);
        }

        Long phoneNumber = CrmUtils.Isnumber(recherche) ? Long.parseLong(recherche) : null;
        logger.info("Recherche identifiant: " + recherche + ", type: " + typeIdentif);

        switch (typeIdentif) {
          case "2":
            Facture unpaidFacture = factureService.findFactureNonPayeeByReference(recherche);
            if (unpaidFacture != null) {
              abonnement = unpaidFacture.getAbonnement();
              unpaidByInterface =
                  factureRepository.findListeFactureByCinForApi(abonnement.getCin(), false);
              logger.info("Unpaid Facture found: " + unpaidFacture.getFactureId());
            }

            Facture paidFacture = factureService.findFacturePayeeByReference(recherche);
            if (paidFacture != null) {
              abonnement = paidFacture.getAbonnement();
              paidByInterface =
                  factureRepository.findListeFactureByCinForApi(abonnement.getCin(), true);
              logger.info("Paid Facture found: " + paidFacture.getFactureId());
            }
            break;

          case "1":
            demandeAbonnement =
                demandeAbonnementRepository.findDemandeAbonnementByCinOrByTelFix(recherche, null);
            abonnement = abonnementService.findAbonnementByCin(recherche);
            unpaidByInterface = factureRepository.findListeFactureByCinForApi(recherche, false);
            paidByInterface = factureRepository.findListeFactureByCinForApi(recherche, true);
            break;

          case "3":
            demandeAbonnement =
                demandeAbonnementRepository.findDemandeAbonnementByCinOrByTelFix(null, phoneNumber);
            unpaidByInterface =
                factureRepository.findListeFactureByFixeNumberForApi(phoneNumber, false);
            paidByInterface =
                factureRepository.findListeFactureByFixeNumberForApi(phoneNumber, true);
            abonnement = abonnementService.findUserByFixeNumber(phoneNumber);
            break;

          default:
            dataResponse.put("code", "400");
            dataResponse.put("message", "type_identif invalide");
            dataResponse.put("success", false);
            dataResponse.put("data", null);
            return ResponseEntity.ok(dataResponse);
        }

        // Convert unpaid interface list
        for (ListeFactureAndAvoirDTO el : unpaidByInterface) {
          ListeFacturePayeDTO dto = new ListeFacturePayeDTO();
          dto.setDateDeDebut(el.getDateDeDebut());
          dto.setDateDeFin(el.getDateDeFin());
          dto.setEcheance(el.getEcheance());
          dto.setRef_facture(el.getRef_facture());
          dto.setTotal_ttc(el.getMontant_payer());
          dto.setTypeFacture(el.getTypeFacture());
          dto.setIsFactureResilation(el.getIsFactureResilation());
          dto.setDatePaiement(el.getDateDePayement());
          unpaidInvoices.add(dto);
        }

        // Convert paid interface list
        for (ListeFactureAndAvoirDTO el : paidByInterface) {
          ListeFacturePayeDTO dto = new ListeFacturePayeDTO();
          dto.setDateDeDebut(el.getDateDeDebut());
          dto.setDateDeFin(el.getDateDeFin());
          dto.setEcheance(el.getEcheance());
          dto.setRef_facture(el.getRef_facture());
          dto.setTotal_ttc(el.getMontant_payer());
          dto.setTypeFacture(el.getTypeFacture());
          dto.setIsFactureResilation(el.getIsFactureResilation());
          dto.setDatePaiement(el.getDateDePayement());
          paidInvoices.add(dto);
        }
        if (demandeAbonnement != null) {
          demandeAb.put("nom", demandeAbonnement.getLastName());
          demandeAb.put("prenom", demandeAbonnement.getFirstName());
          demandeAb.put("ref_abonnement", demandeAbonnement.getReferenceChifco());
          demandeAb.put("num_fixe", demandeAbonnement.getTelFixe());
          demandeAb.put("identifiant", demandeAbonnement.getCin());
          demandeAb.put("Mobile", demandeAbonnement.getTelMobile());
          demandeAb.put("gouvernorat", demandeAbonnement.getGouvernorat().getGouvernoratName());
          demandeAb.put("ville", demandeAbonnement.getVille().getVilleName());
          demandeAb.put("adresse", demandeAbonnement.getAdresse());
          demandeAb.put("codePostal", demandeAbonnement.getCodePostale().getName());
          // demandeAb.put("profession", demandeAbonnement.getProfession().getName());
          demandeAb.put("fax", demandeAbonnement.getFax());
          demandeAb.put("situation_familiale", demandeAbonnement.getSituationFamiliale());
          demandeAb.put("date_naissance", demandeAbonnement.getDateNaissance());
          demandeAb.put("Débit", demandeAbonnement.getPack().getDebitPack());
          demandeAb.put("etatConnection", etatConnection);
          String cleanStatus =
              demandeAbonnement.getStatut().getDesignation().replaceAll("[\\r\\n]+", " ").trim();
          demandeAb.put("statut", cleanStatus);
          demandeAb.put("Catégorie",
              demandeAbonnement.getCategorieProduitInternet().getCategorieProduitInternetCode());
          demandeAb.put("pack", demandeAbonnement.getPack().getTitle());
          demandeAb.put("offre", demandeAbonnement.getPack().getOffre().getTitle());
          demandeAb.put("type_paiement", demandeAbonnement.getTypePaiement().getNomTypePaiement());
          demandeAb.put("reference_tt", demandeAbonnement.getReferenceTT());
          demandeAb.put("statut_tt", demandeAbonnement.getEtatTT());
          demandeAb.put("reclamation", null);
        }
        if (abonnement != null) {

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
          HashMap<String, Object> clientInfo = new HashMap<>();
          clientInfo.put("nom", abonnement.getLastName());
          clientInfo.put("prenom", abonnement.getFirstName());
          clientInfo.put("ref_abonnement", abonnement.getReferenceClient());
          clientInfo.put("num_fixe", abonnement.getTelFixe());
          clientInfo.put("identifiant", abonnement.getCin());
          clientInfo.put("Mobile", abonnement.getTelMobile());
          clientInfo.put("etatConnection", etatConnection);
          clientInfo.put("gouvernorat", abonnement.getGouvernorat().getGouvernoratName());
          clientInfo.put("ville", abonnement.getVille().getVilleName());
          clientInfo.put("adresse", abonnement.getAdresse());
          clientInfo.put("codePostal", abonnement.getCodePostale().getName());

          clientInfo.put("fax", abonnement.getFax());
          clientInfo.put("situation_familiale", abonnement.getSituationFamiliale());
          clientInfo.put("date_naissance", abonnement.getDateNaissance());
          clientInfo.put("type_paiement", abonnement.getTypePaiement().getNomTypePaiement());
          String cleanStatusAbo =
              abonnement.getStatut().getDesignation().replaceAll("[\\r\\n]+", " ").trim();
          clientInfo.put("statut", cleanStatusAbo);
          clientInfo.put("pack", abonnement.getPack().getTitle());
          clientInfo.put("offre", abonnement.getPack().getOffre().getTitle());
          clientInfo.put("Catégorie",
              abonnement.getPack().getCategoriePack().getCategorieProduitInternetCode());
          clientInfo.put("Débit", abonnement.getPack().getDebitPack());
          Reclamation reclamationInProgress =
              reclamationRepository.findlastreclamationByclientId(abonnement.getClientid());
          clientInfo.put("reclamation", reclamationInProgress);
          dataObject.put("details", clientInfo);
          dataObject.put("facturesNonPayee", unpaidInvoices);
          dataObject.put("facturesPayee", paidInvoices);
          dataResponse.put("success", true);
          dataResponse.put("message", "");
          dataResponse.put("code", 200);
          dataResponse.put("data", dataObject);

        } else {
          if (abonnement == null && demandeAbonnement == null) {

            switch (typeIdentif) {
              case "1":
                dataResponse.put("code", "404");
                dataResponse.put("message", "num cin not found");
                break;
              case "2":
                dataResponse.put("code", "404");
                dataResponse.put("message", "num facture not found");
                break;
              case "3":
                dataResponse.put("code", "404");
                dataResponse.put("message", "num ligne not found");
                break;
            }
          } else {
            if (demandeAbonnement != null) {
              dataObject.put("details", demandeAb);
              dataResponse.put("success", true);
              dataResponse.put("message", "");
              dataResponse.put("code", 200);
              dataResponse.put("data", dataObject);
              return ResponseEntity.ok(dataResponse);
            }
          }
          dataResponse.put("success", false);
          dataResponse.put("data", null);
        }
      } else {
        dataResponse.put("code", "401");
        dataResponse.put("success", false);
        dataResponse.put("data", null);
        dataResponse.put("message", "token non valide ou manquant");


      }
    } catch (Exception e) {
      logger.error("Erreur dans informationClient: ", e);
      dataResponse.put("success", false);
      dataResponse.put("code", "ERROR");
      dataResponse.put("message", "Erreur interne");
      dataResponse.put("data", null);
    }

    return ResponseEntity.ok(dataResponse);
  }

  @RequestMapping(method = RequestMethod.POST, value = "authentication")
  public ResponseEntity<HashMap<String, Object>> authenticateUserDetails(
      @RequestBody ClientLogin loginDto) {
    HashMap<String, Object> returnapi = new HashMap<String, Object>();
    try {
      if ((loginDto.getClientSecret().equals(clientSecretkey)
          && loginDto.getGrant_type().equals(grantTypeClient))) {

        String jwt = utilsJwt.generateJwtToken(loginDto.getGrant_type());

        returnapi.put("msg", "Connexion réussie!.");
        returnapi.put("jwt", jwt);
        returnapi.put("code", "200");
        returnapi.put("success", true);

      } else {
        returnapi.put("msg", "Échec de la connexion  !!.");
        returnapi.put("code", "401");
        returnapi.put("success", false);
      }
      return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);

    } catch (BadCredentialsException e) {
      logger.error("signin Acs Api : " + e);
      returnapi.put("msg", "Échec de la connexion !!.");
      returnapi.put("code", "401");
      returnapi.put("success", false);
      return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);

    }

  }


  @RequestMapping(method = RequestMethod.GET, value = "verifyIsChefsecteur")
  public ResponseEntity<HashMap<String, Object>> verifyIsChefsecteur(
      @RequestParam String telcommercial) {

    HashMap<String, Object> response = new HashMap<>();
    HashMap<String, Object> dataObject = new HashMap<>();
    try {
      // User user = userRepository.findTop1UsersByTelephone(telcommercial, "DISTRIBUTEUR");
      User user = userRepository.findTop1UsersByTelephoneNETYACTION(telcommercial);
      boolean exists = user != null;

      response.put("code", "200");
      response.put("success", true);
      dataObject.put("exists", exists);
      dataObject.put("email", exists ? user.getEmail() : null);
      response.put("data", dataObject);
      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (Exception e) {
      response.put("code", "400");
      response.put("success", false);
      dataObject.put("exists", false);
      response.put("data", dataObject);
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
  }


  @RequestMapping(method = RequestMethod.POST, value = "mazama_payeement_facture")
  public ResponseEntity<HashMap<String, Object>> payeementFacturGetwaymazam(
      HttpServletRequest request, @RequestBody PaymentDTO PayementFactures) {
    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();
    try {
      String jwt = utilsJwt.parseJwt(request);
      if (jwt != null && utilsJwt.validateJwtToken(jwt)) {
        if (PayementFactures.getTransactionId() != null
            && !PayementFactures.getTransactionId().isEmpty()) {
          List<String> factureids = PayementFactures.getFactures();
          List<String> avrArray = new ArrayList<>();
          List<String> factureArray = new ArrayList<>();

          for (String element : factureids) {
            if (element.startsWith("AVR-")) {
              avrArray.add(element);
            } else {
              factureArray.add(element);
            }
          }
          User user = userservice.findTop1UsersByTypeuser("SYSTEM");
          String methodepayment = "mazama"; // PayementFactures.getMethodePayment();
          /*
           * if (methodepayment != null) { methodepayment = "carte bancaire"; }
           */
          String Bankname = PayementFactures.getBankname();
          String TransactionId = PayementFactures.getTransactionId();
          logger.info("payeementFacture TranscationId : " + TransactionId);
          logger.info(
              "payeementFacture Liste des Reference factures a payer : " + factureArray.size());
          List<Facture> existeFacture =
              factureService.findListeFactureNonPayeeByRefFacture(factureArray);
          if (existeFacture != null && existeFacture.size() == factureArray.size()) {
            List<String> idList =
                existeFacture.stream().map(Facture -> String.valueOf(Facture.getFactureId())) // extract
                    // id
                    // from
                    // each Model object
                    .collect(Collectors.toList()); //
            List<String> avoirList = new ArrayList<String>();
            if (avrArray.size() > 0) {
              List<AvoirClient> existeAvoir =
                  avoirRepository.findAllAvoirbyListeReferenceAndNotPayed(avrArray);
              if (existeAvoir.size() == avrArray.size())
                avoirList = existeAvoir.stream().map(Avoir -> String.valueOf(Avoir.getAvoirId()))
                    .collect(Collectors.toList());
            }

            List<Payement> payementFacture =
                payementsService.createNewPaymentMultiple(idList, avoirList, user, methodepayment,
                    Bankname, null, null, TransactionId, true, request);
            String result = payementFacture.stream().map(Payement::getCodePayement)
                .collect(Collectors.joining(" | "));;// extrait
                                                     // uniquement le
            logger.info("code de payement mazam : " + result);
            // mode de
            // paiement

            if (payementFacture != null) {
              dataRespenseObject.put("success", true);
              dataRespenseObject.put("message", "payement avec success");
              dataRespenseObject.put("TransactionId", TransactionId);
              dataRespenseObject.put("Reference", "Re-" + result);

            } else {
              dataRespenseObject.put("success", false);
              dataRespenseObject.put("message", "erreur avec Payement");
            }
          } else {
            dataRespenseObject.put("success", false);
            dataRespenseObject.put("message", "facture Ids non existe ou non conforme");
          }
          dataRespenseObject.put("code", 200);
          response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.OK);
          logger.info("payeementFacture Mesage API : " + dataRespenseObject.get("message"));
        } else {
          dataRespenseObject.put("message", "tansaction Id non valide ou manquant");
          dataRespenseObject.put("success", false);
        }
      } else {
        dataRespenseObject.put("message", "token non valide ou manquant");
        dataRespenseObject.put("success", false);

      }
    } catch (Exception e) {
      logger.info("Error payeementFacture API Exption: " + e.getMessage());
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("message", e.getMessage());
      dataRespenseObject.put("code", 200);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @RequestMapping(method = RequestMethod.POST, value = "mazama_payeement_facture_historique")
  public ResponseEntity<HashMap<String, Object>> HistoriquepayeementFacturGetwaymazam(
      HttpServletRequest request, @RequestBody MazamaFilter PayementFactures) {
    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataRespenseObject = new HashMap<String, Object>();
    try {
      String jwt = utilsJwt.parseJwt(request);
      if (jwt != null && utilsJwt.validateJwtToken(jwt)) {
        List<PaymentDTOApi> payementFacture =
            payementsService.findPaymentBydevice(PayementFactures.getDateDebut(),
                PayementFactures.getDateFin(), PayementFactures.getTransactionId(), "mazama");

        if (payementFacture != null && !payementFacture.isEmpty()) {
          dataRespenseObject.put("success", true);

          dataRespenseObject.put("data", payementFacture);


        } else {
          dataRespenseObject.put("success", false);
          dataRespenseObject.put("message", "aucun paiement n'est trouvé ");
        }
      } else {
        dataRespenseObject.put("success", false);
        dataRespenseObject.put("message", "autorisation expirer ou manquer ");
      }
      response =
          new ResponseEntity<HashMap<String, Object>>(dataRespenseObject, HttpStatus.ACCEPTED);
    } catch (Exception e) {
      logger.info("Error payeementFacture API Exption: " + e.getMessage());
      dataRespenseObject.put("success", false);
      dataRespenseObject.put("message", e.getMessage());
      dataRespenseObject.put("code", 200);
      response = new ResponseEntity<HashMap<String, Object>>(dataRespenseObject,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return response;
  }

  @PostMapping(value = "/parinage-app", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> parinageApp(@RequestBody Map<String, Object> formData) {
    // formData sera un LinkedHashMap
    System.out.println(formData);
    Map<String, Object> response = new HashMap<>();

    System.out.println("REQUEST = " + formData);

    List<ParinageDTO> listParinage = parinageService.findListwithfilterForApplication(formData);

    if (listParinage == null || listParinage.isEmpty()) {
      response.put("code", "404");
      response.put("data", null);
    } else {

      response.put("code", "200");
      response.put("data", listParinage);
    }

    return ResponseEntity.ok(response);
  }

  @PostMapping(value = "/controleParental", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HashMap<String, Object>> controleParental(
      @RequestParam String telephoneFix,
      @RequestParam boolean activer) {

    ResponseEntity<HashMap<String, Object>> response = null;
    HashMap<String, Object> dataResponseObject = new HashMap<String, Object>();

    try {
      String result = modemService.controleParental(telephoneFix, activer);
      Abonnement abonnement = abonnementService.findUserByFixeNumber(Long.getLong(telephoneFix));
      switch (result) {
        case "INVALID_TELEPHONE_FIX":
          dataResponseObject.put("success", false);
          dataResponseObject.put("code", "INVALID_TELEPHONE_FIX");
          dataResponseObject.put("message", "Le numéro de téléphone fixe doit contenir exactement 8 chiffres.");
          dataResponseObject.put("modem", null);
          response = new ResponseEntity<HashMap<String, Object>>(dataResponseObject, HttpStatus.BAD_REQUEST);
          break;

        case "ABONNEMENT_NOT_FOUND":
          dataResponseObject.put("success", false);
          dataResponseObject.put("code", "ABONNEMENT_NOT_FOUND");
          dataResponseObject.put("message", "Aucun abonnement trouvé pour ce numéro de téléphone fixe.");
          dataResponseObject.put("modem", null);
          response = new ResponseEntity<HashMap<String, Object>>(dataResponseObject, HttpStatus.NOT_FOUND);
          break;

        case "NO_MODEM_ASSIGNED":
          dataResponseObject.put("success", false);
          dataResponseObject.put("code", "NO_MODEM_ASSIGNED");
          dataResponseObject.put("message", "L'abonnement n'a pas de modem affecté.");
          dataResponseObject.put("modem", null);
          response = new ResponseEntity<HashMap<String, Object>>(dataResponseObject, HttpStatus.BAD_REQUEST);
          break;

        case "NO_MODEM_ACCESS_AVAILABLE":
          dataResponseObject.put("success", false);
          dataResponseObject.put("code", "NO_MODEM_ACCESS_AVAILABLE");
          dataResponseObject.put("message", "Aucun accès modem disponible pour ce type de modem.");
          dataResponseObject.put("modem", null);
          response = new ResponseEntity<HashMap<String, Object>>(dataResponseObject, HttpStatus.BAD_REQUEST);
          break;

        case "CONTROLE_PARENTAL_DEJA_ACTIVE":
          dataResponseObject.put("success", false);
          dataResponseObject.put("code", "CONTROLE_PARENTAL_DEJA_ACTIVE");
          dataResponseObject.put("message", "Le contrôle parental est déjà activé pour ce modem.");
          dataResponseObject.put("modem", null);
          response = new ResponseEntity<HashMap<String, Object>>(dataResponseObject, HttpStatus.BAD_REQUEST);
          break;

        case "CONTROLE_PARENTAL_DEJA_DESACTIVE":
          dataResponseObject.put("success", false);
          dataResponseObject.put("code", "CONTROLE_PARENTAL_DEJA_DESACTIVE");
          dataResponseObject.put("message", "Le contrôle parental est déjà désactivé pour ce modem.");
          dataResponseObject.put("modem", null);
          response = new ResponseEntity<HashMap<String, Object>>(dataResponseObject, HttpStatus.BAD_REQUEST);
          break;

        case "CONTROLE_PARENTAL_ACTIVE":
          dataResponseObject.put("success", true);
          dataResponseObject.put("code", "CONTROLE_PARENTAL_ACTIVE");
          dataResponseObject.put("message", "Contrôle parental activé avec succès.");
          dataResponseObject.put("modem", abonnement.getModem().getNumSerie());
          response = new ResponseEntity<HashMap<String, Object>>(dataResponseObject, HttpStatus.OK);
          break;

        case "CONTROLE_PARENTAL_DESACTIVE":
          dataResponseObject.put("success", true);
          dataResponseObject.put("code", "CONTROLE_PARENTAL_DESACTIVE");
          dataResponseObject.put("message", "Contrôle parental désactivé avec succès.");
          dataResponseObject.put("modem", abonnement.getModem().getNumSerie());
          response = new ResponseEntity<HashMap<String, Object>>(dataResponseObject, HttpStatus.OK);
          break;

        default:
          dataResponseObject.put("success", false);
          dataResponseObject.put("code", "UNKNOWN_ERROR");
          dataResponseObject.put("message", "Une erreur inconnue s'est produite.");
          dataResponseObject.put("modem", null);
          response = new ResponseEntity<HashMap<String, Object>>(dataResponseObject, HttpStatus.INTERNAL_SERVER_ERROR);
          break;
      }

      logger.info("API controleParental (telephoneFix = {}, activer = {}) - Result: {}", telephoneFix, activer, result);

    } catch (Exception e) {
      logger.error("Erreur dans l'API controleParental: ", e);
      dataResponseObject.put("success", false);
      dataResponseObject.put("code", "INTERNAL_ERROR");
      dataResponseObject.put("message", "Une erreur interne s'est produite.");
      dataResponseObject.put("data", null);
      response = new ResponseEntity<HashMap<String, Object>>(dataResponseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return response;
  }

}
