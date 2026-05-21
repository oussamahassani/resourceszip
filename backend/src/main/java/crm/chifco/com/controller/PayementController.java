package crm.chifco.com.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.lowagie.text.DocumentException;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.Payement;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.RecuNumeroSequence;
import crm.chifco.com.model.User;
import crm.chifco.com.radius.service.RadcheckService;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.CodePostaleRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.PayementRepository;
import crm.chifco.com.repository.VilleRepository;
import crm.chifco.com.service.AbonnementService;
import crm.chifco.com.service.BordereauService;
import crm.chifco.com.service.CommissionService;
import crm.chifco.com.service.DemandeAbonnementService;
import crm.chifco.com.service.EncaissementService;
import crm.chifco.com.service.ExisteFactureOld;
import crm.chifco.com.service.FactureService;
import crm.chifco.com.service.PayementsService;
import crm.chifco.com.service.RecuNumeroSequenceService;
import crm.chifco.com.service.StatutService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.templateclasse.EncaissementNonPayee;
import crm.chifco.com.templateclasse.ListeBordereau;
import crm.chifco.com.templateclasse.RevendeurRecap;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.PrefixDocument;
import crm.chifco.com.utils.UserTypeConstant;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping(value = "payement/*")
public class PayementController {
  private final Logger logger = LogManager.getLogger(this.getClass());
  @Autowired
  UserService userservice;

  @Autowired
  PayementsService paymentservice;

  @Autowired
  EncaissementService encaissementService;

  @Autowired
  BordereauService bordereauService;

  @Autowired
  FactureService factureservice;

  @Autowired
  StatutService statutService;

  @Autowired
  AbonnementService abonnementService;

  @Autowired
  RecuNumeroSequenceService recuNumeroSequenceService;

  @Autowired
  PayementRepository payementRepository;

  @Autowired
  AvoirRepository avoirRepository;

  @Value("${pathRecu}")
  private String pathRecu;

  @Value("${serverNameCRM}")
  private String serverNameCRM;

  @Autowired
  RadcheckService radcheckService;

  @Autowired
  private DemandeAbonnementService demandeAbonnementService;

  @Autowired
  private AbonnementRepository abonnementRepository;

  @Autowired
  GouvernoratRepository gouvernoratRepository;

  @Autowired
  VilleRepository villeRepository;

  @Autowired
  private CodePostaleRepository codePostaleRepository;

  @Autowired
  private CommissionService commissionService;

  @Autowired
  ExisteFactureOld factureOldExiste;

  // ************************ api pour payment facture controler */

  @PreAuthorize("hasAnyAuthority('INVOICE_PAYMENT')")

  @RequestMapping(path = "/paymentOneFactures", method = RequestMethod.POST)
  public String PaymentFactures(Long onefacture, String methodepayment, String Bankname,
      String NumeroCarte, String Numcheque, HttpServletResponse response,
      RedirectAttributes redirectAttrs, HttpServletRequest request)
      throws IOException, DocumentException {
    List<String> facturelist = new ArrayList<String>();
    List<String> avoirList = new ArrayList<String>();
    facturelist.add(onefacture.toString());
    Facture myfirstfacture = factureservice.getFacture(onefacture);
    if (myfirstfacture != null) {
      List<String> avoirListeId =
          avoirRepository.getallavoirNonPayeeByClient(myfirstfacture.getAbonnement().getClientid());

      if (avoirListeId.size() > 0) {
        avoirList = avoirListeId;
      }
    }
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userservice.findUsersByEmail(currentUser);

      try {
        paymentservice.createNewPaymentMultiple(facturelist, avoirList, user, methodepayment,
            Bankname, NumeroCarte, Numcheque, null, false, request);
      } catch (ServletException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    Facture facture = factureservice.getFacture(onefacture);
    Abonnement abonnementFactures = facture.getAbonnement();
    DemandeAbonnement demandeAbonnementFactures = demandeAbonnementService
        .findDemandeAbonnementByReferencechifco(abonnementFactures.getReferenceClient());

    redirectAttrs.addFlashAttribute("message", "facture payee avec sucee");

    return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/"
        + demandeAbonnementFactures.getDemandeId();
  }

  @RequestMapping(path = "/imprimer_recu_payment_A4", method = RequestMethod.POST)
  public void downloadPDFRecuFactureA4(String factureliste, String type,
      HttpServletResponse response) throws Exception {

    try {
      // set file Recu Payment

      if (type != null) {
        if (type.equals("avoir")) {
          Long idFact = payementRepository.getFacutreIdByAvoirId(Long.parseLong(factureliste));
          if (idFact != null)
            factureliste = idFact.toString();
        }
      }

      Payement oldPayement = paymentservice.returnPayementFromList(factureliste);
      if (oldPayement == null) {
        oldPayement = paymentservice.returnPayementFromoneAvoir(factureliste);
      }
      if (oldPayement.getRecuNumeroSequence() == null) {
    	  Date d = new Date();
    	    int annee = d.getYear() + 1900;
    	    String codeRecu =   annee + "-" + oldPayement.getUser().getCodeUser() + "--" + d.getTime();
       
        RecuNumeroSequence recuNumeroSequence = new RecuNumeroSequence();
        recuNumeroSequence.setCodePayement(codeRecu);
        recuNumeroSequence.setMontantTotal(oldPayement.getMontant());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        User user = userservice.findUsersByEmail(currentUser);
        recuNumeroSequence.setUser(user);
        RecuNumeroSequence recuNumeroSequenceNew =
            recuNumeroSequenceService.save(recuNumeroSequence);
        payementRepository.updateCodeRecu(oldPayement.getPayementid(),
            recuNumeroSequenceNew.getRecuNumeroSequenceId());
        oldPayement.setRecuNumeroSequence(recuNumeroSequenceNew);
      }

      File filePyement = new File(pathRecu + CrmUtils.getYear() + "/" + CrmUtils.getMonth() + "/"
          + PrefixDocument.NOMEFILE_RECU_PAYMENT
          + oldPayement.getRecuNumeroSequence().getCodePayement() + "-"
          + oldPayement.getRecuNumeroSequence().getRecuNumeroSequenceId() + ".pdf");
      if (!filePyement.exists()) {

        filePyement = paymentservice.createPDFRecuPaymentA4(factureliste, oldPayement);
      }

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

  @PreAuthorize("hasAnyAuthority('INVOICE_PAYMENT')")
  @RequestMapping(method = RequestMethod.POST, value = "PaymentFactures")
  @ResponseBody
  public String PaymentFactures(String factureliste, String methodepayment, String Bankname,
      String avoirliste, String NumeroCarte, String Numcheque, Model model,
      HttpServletResponse response, RedirectAttributes redirectAttrs, HttpServletRequest request)
      throws JRException, IOException, DocumentException {


    List<String> facturelist = new ArrayList<String>(Arrays.asList(factureliste.split(",")));

    List<String> avoirList = new ArrayList<>();


    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userservice.findUsersByEmail(currentUser);
      Long abonnementId = factureservice.findAbonnementByListFacture(facturelist);
      Boolean IsResilationExiste = false;

      try {
        if (facturelist.size() == 1) {
          Facture chekIfResilationFacture =
              factureservice.getFactureById(Long.parseLong(facturelist.get(0)));
          if (chekIfResilationFacture != null
              && chekIfResilationFacture.getIsFactureResilation() == true)
            IsResilationExiste = true;
        }
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (!avoirliste.isEmpty() && !IsResilationExiste) {

        avoirList = new ArrayList<>(Arrays.asList(avoirliste.split(",")));
        Boolean isExistePyament = payementRepository.findIfAnyPayementExisteWithAvoir(avoirList);
        if (isExistePyament)
          return null;
      }
      List<Facture> factureOlderthanSelected = new ArrayList<>();
      try {

        factureOlderthanSelected = factureOldExiste.getFilteredFactures(abonnementId, facturelist);

      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      if (!factureOlderthanSelected.isEmpty() && IsResilationExiste == false) {
        for (Facture f : factureOlderthanSelected) {
          Boolean recherche = false;
          for (String id : facturelist) {
            if (f.getFactureId().toString().equals(id)) {
              recherche = true;
              break;
            }
          }
          if (recherche == false) {
            if (f.getIsFirstFacture())
              return "erreurFirstFacture";
            else {
              return "erreur";
            }
          }
        }
      }
      try {
        Boolean isExistePyament =
            payementRepository.findIfAnyPayementExisteWithFacture(facturelist);
        if (isExistePyament)
          return null;

        List<Payement> listePayement =
            paymentservice.createNewPaymentMultiple(facturelist, avoirList, user, methodepayment,
                Bankname, NumeroCarte, Numcheque, null, false, request);

        // paymentservice.creationRecuPayement(listePayement, user);

        // verification si il ya une first facture non payé
        /*
         * if (resultat == null) { Facture facture =
         * factureservice.getFacture(Long.parseLong(facturelist.get(0))); Facture firstFacture =
         * factureservice.findFirstByAbonnement_clientid(facture.getAbonnement().getClientid()); if
         * (firstFacture.getEtat_facture() == false && firstFacture.getVisibility() == false) {
         * return ""; } else { return "erreur"; }
         * 
         * }
         */

      } catch (ServletException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());

    }
    return factureliste;

  }

  @RequestMapping(method = RequestMethod.GET, value = "viewlistepayement")
  public String viewlistepayement(Model model) {
    List<String> StringsRole = new ArrayList<String>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userservice.findUsersByEmail(currentUser);
    StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
    model.addAttribute("userphoto", user.getPhoto());
    model.addAttribute("userrole", user.getRole().getRoleName());
    model.addAttribute("useremail", user.getEmail());
    model.addAttribute("CREATE_SLIP", StringsRole.contains("CREATE_SLIP"));

    return "payement/Listepayement";
  }

  @RequestMapping(method = RequestMethod.GET, value = "listepaymentrevendeur")
  @ResponseBody
  public HashMap<String, Object> getAlAbonnement(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("columns[0][search][value]") String filterrecherche,
      @RequestParam("order[0][column]") int ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir) {
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    int currentpage = start / length;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userservice.findUsersByEmail(currentUser);
    Page<EncaissementNonPayee> responseData = null;
    String sort = "";
    Double prixmin = 0.0;
    Double prixmax = 0.0;
    String ref_facture = null;
    String datedebut = null;
    String datefin = null;
    String ref_avoir = null;
    switch (ordercolumnaram) {

      case 1:
        sort = "encaissement_id";
        break;

      case 3:
      case 4:
        sort = "client";
        break;
      case 5:
        sort = "created_date";
        break;
      case 6:
        sort = "montant_facture";
        break;

      case 7:
        sort = "date";
        break;
      case 8:
        sort = "type_de_payment";
        break;
      case 9:
        sort = "date";
        break;
    }

    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("prixmin"), "") && obj.getString("prixmin") != null) {
        prixmin = obj.getDouble("prixmin");
      }
      if (!Objects.equals(obj.getString("prixmax"), "") && obj.getString("prixmax") != null) {
        prixmax = obj.getDouble("prixmax");

      }
      if (!Objects.equals(obj.getString("datedebut"), "") && obj.getString("datedebut") != null) {
        datedebut = obj.getString("datedebut").trim() + " 00:00:00.000";

      }
      if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
        datefin = obj.getString("datefin").trim() + " 23:59:59.999";

      }
      if (!Objects.equals(obj.getString("ref_facture"), "")
          && obj.getString("ref_facture") != null) {
        ref_facture = obj.getString("ref_facture").trim();

      }
      if (!Objects.equals(obj.getString("ref_avoir"), "") && obj.getString("ref_avoir") != null) {
        ref_avoir = obj.getString("ref_avoir").trim();

      }

    }
    // ROLE_ADMINISTRATEUR
    if (StringsRole.contains("READ_PAYED_INVOICE_ALL")) {
      responseData = encaissementService.findPaginatedadmin(currentpage + 1, length, prixmin,
          prixmax, datedebut, datefin, ref_facture, ref_avoir, sort, orderdir);
    }
    // ROLE_REVENDEUR || ROLE_DISTRIBUTEUR

    else if (StringsRole.contains("CREATE_SLIP")) {
      responseData = encaissementService.findPaginatedRevendeur(currentpage + 1, length,
          user.getUserid(), prixmin, prixmax, datedebut, datefin, sort, orderdir);
    }

    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }

    else {
      myGreetings.put("data", null);
      myGreetings.put("recordsTotal", 0);
      myGreetings.put("recordsFiltered", 0);
    }

    myGreetings.put("draw", draw);
    myGreetings.put("start", start);

    return myGreetings;
  }

  @PreAuthorize("hasAuthority('READ_RETAIL_SUMMARY_LIST_ALL')"
      + "|| hasAuthority('READ_RETAIL_SUMMARY_LIST_AREA')")
  @RequestMapping(method = RequestMethod.GET, value = "viewlisterecaperevendeur")
  public String viewListeRecapeRevendeur(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userservice.findUsersByEmail(currentUser);
    model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
    model.addAttribute("userphoto", user.getPhoto());
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    model.addAttribute("READ_RETAIL_SUMMARY_DETAILS",
        StringsRole.contains("READ_RETAIL_SUMMARY_DETAILS"));
    model.addAttribute("UNLOCK_RETAIL_SUMMARY", StringsRole.contains("UNLOCK_RETAIL_SUMMARY"));
    List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("gouvernorats", listGouvernorats);
    List<User> listeDistributeur = userservice.findUsersByTypeUser(UserTypeConstant.DISTRIBUTEUR);
    model.addAttribute("listeDistributeur", listeDistributeur);
    userservice.returnInfoUserConnected(model);
    return "payement/recaprevendeur";
  }

  @PreAuthorize("hasAuthority('READ_RETAIL_SUMMARY_LIST_ALL')"
      + "|| hasAuthority('READ_RETAIL_SUMMARY_LIST_AREA')")
  @RequestMapping(method = RequestMethod.GET, value = "recaperevendeur")
  @ResponseBody
  public HashMap<String, Object> recaperevendeur(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    int currentpage = start / length;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userservice.findUsersByEmail(currentUser);
    Page<RevendeurRecap> responseData = null;

    Long gouvernorat = null;
    Long villes = null;
    String Nom = null;
    String Prenom = null;
    String refUser = null;
    Long distributeur = null;
    Boolean status = null;
    String datedebut = null;
    String datefin = null;

    if (filterrecherche != null && filterrecherche != "") {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("gouvernorat"), "")
          && obj.getString("gouvernorat") != null) {
        gouvernorat = obj.getLong("gouvernorat");
      }
      if (!Objects.equals(obj.getString("villes"), "") && obj.getString("villes") != null) {
        villes = obj.getLong("villes");
      }
      if (!Objects.equals(obj.getString("Nom"), "") && obj.getString("Nom") != null) {
        Nom = obj.getString("Nom").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("Prenom"), "") && obj.getString("Prenom") != null) {
        Prenom = obj.getString("Prenom").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("refUser"), "") && obj.getString("refUser") != null) {
        refUser = obj.getString("refUser").trim();
      }
      if (!Objects.equals(obj.getString("distributeur"), "")
          && obj.getString("distributeur") != null) {
        distributeur = obj.getLong("distributeur");
      }
      if (!Objects.equals(obj.getString("status"), "") && obj.getString("status") != null) {
        status = obj.getBoolean("status");
      }
      if (!Objects.equals(obj.getString("datedebut"), "") && obj.getString("datedebut") != null) {
        datedebut = obj.getString("datedebut").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
        datefin = obj.getString("datefin").trim().toLowerCase();
      }
    }
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

    if (StringsRole.contains("READ_RETAIL_SUMMARY_LIST_ALL")) {

      responseData = encaissementService.findRecapeRevendeur(currentpage + 1, length, gouvernorat,
          villes, Nom, Prenom, refUser, status, datedebut, datefin, distributeur);
    }

    if (StringsRole.contains("READ_RETAIL_SUMMARY_LIST_AREA")) {

      responseData = encaissementService.findRecapeRevendeurByDistributeur(currentpage + 1, length,
          user.getUserid(), gouvernorat, villes, Nom, Prenom, refUser, status);
    }

    if (responseData.getContent() != null)
      myGreetings.put("data", responseData.getContent());

    ;
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    return myGreetings;
  }

  // top revendeur
  @RequestMapping(method = RequestMethod.GET, value = "toprevendeur")
  @ResponseBody
  public HashMap<String, Object> toprevendeurChifreAfaireAndDemandREAliser(
      @RequestParam("draw") int draw, @RequestParam("start") int start,
      @RequestParam("length") int length, @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return encaissementService.findTopRevendeurSearch(draw, start, length, search, ordercolumnaram,
        orderdir, filterrecherche);
  }

  @PreAuthorize("hasAuthority('READ_RETAIL_SUMMARY_DETAILS')")
  @RequestMapping(method = RequestMethod.GET, value = "viewDetailsRecap/{userid}")
  public String viewDetailsRecap(@PathVariable("userid") Long Userid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    User user = userservice.findUsersByIduser(Userid);
    User usercreteur = userservice.findUsersByIduser(user.getCreatedByUserId());
    model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
    model.addAttribute("userphoto", user.getPhoto());
    model.addAttribute("userrole", user.getRole().getRoleName());
    model.addAttribute("useremail", user.getEmail());
    userservice.returnInfoUserConnected(model);
    List<Encaissement> encaismentlistpayed =
        encaissementService.findEncaissementPayedbyRevendeur(user);

    if (user.getCodePostale() != null) {
      Optional<PostalCode> postalCode = codePostaleRepository.findById(user.getCodePostale());
      model.addAttribute("postalCode",
          postalCode.get().getCode() + "-" + postalCode.get().getName());
    }
    userservice.returnInfoUserConnected(model);
    model.addAttribute("Information", user);

    model.addAttribute("infromationcreateur", usercreteur);
    model.addAttribute("Payedencaisment", encaismentlistpayed);

    model.addAttribute("userid", Userid);

    // commission
    List<Commission> lCommissions = commissionService.finAllByUserID(Userid);
    model.addAttribute("lCommissions", lCommissions);

    return "payement/viewOneDetailsRecap";
  }

  @PreAuthorize("hasAuthority('READ_RETAIL_SUMMARY_DETAILS')")
  @RequestMapping(method = RequestMethod.GET, value = "viewDetailsRecapFactureNversee/{userid}")
  @ResponseBody
  public HashMap<String, Object> viewDetailsRecapFactureNversee(@PathVariable("userid") Long Userid,
      @RequestParam("draw") int draw, @RequestParam("start") int start,
      @RequestParam("length") int length) {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    Page<Encaissement> responseData = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    int currentpage = start / length;
    User user = userservice.findUsersByIduser(Userid);
    // User usercreteur = userservice.findUsersByIduser(user.getCreatedbyuserid());
    responseData =
        encaissementService.findListEncaissementNotPyedByRevendeur(currentpage + 1, length, user);

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());

    return myGreetings;
  }

  @PreAuthorize("hasAuthority('READ_RETAIL_SUMMARY_DETAILS')")
  @RequestMapping(method = RequestMethod.GET, value = "viewDetailsRecapFactureversee/{userid}")
  @ResponseBody
  public HashMap<String, Object> viewDetailsRecapFactureversee(@PathVariable("userid") Long Userid,
      @RequestParam("draw") int draw, @RequestParam("start") int start,
      @RequestParam("length") int length) {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    Page<Encaissement> responseData = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    int currentpage = start / length;
    User user = userservice.findUsersByIduser(Userid);

    responseData =
        encaissementService.findListEncaissementPyedByRevendeur(currentpage + 1, length, user);

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());

    return myGreetings;
  }

  @PreAuthorize("hasAuthority('READ_RETAIL_SUMMARY_DETAILS')")
  @RequestMapping(method = RequestMethod.GET, value = "viewDetailsRecapBordereau/{userid}")
  @ResponseBody
  public HashMap<String, Object> viewDetailsRecapBordereau(@PathVariable("userid") Long Userid,
      @RequestParam("draw") int draw, @RequestParam("start") int start,
      @RequestParam("length") int length) {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    Page<ListeBordereau> responseData = null;
    String statut = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    int currentpage = start / length;
    User user = userservice.findUsersByIduser(Userid);

    responseData = bordereauService.findPaginatedbordereauxRevendeur(currentpage + 1, length, user,
        statut, null, null);

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());

    return myGreetings;
  }

  @PreAuthorize("hasAuthority('READ_RETAIL_SUMMARY_DETAILS')")
  @RequestMapping(method = RequestMethod.GET, value = "viewDetailsRecapClient/{userid}")
  @ResponseBody
  public HashMap<String, Object> viewDetailsRecapClient(@PathVariable("userid") Long Userid,
      @RequestParam("draw") int draw, @RequestParam("start") int start,
      @RequestParam("length") int length) {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    Page<Abonnement> responseData = null;
    String statut = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    int currentpage = start / length;
    User user = userservice.findUsersByIduser(Userid);
    // if (StringsRole.contains("READ_CLIENT_REVENDEUR")) {
    responseData = abonnementService.findPaginatedbyRevendeurWithoutSort(currentpage + 1, length,
        user.getRole().getRoleId(), user.getUserid());
    // }
    // distributeur
    // else if (StringsRole.contains("READ_CLIENT_DISTRIBUTEUR")) {
    // responseData = abonnementService.findPaginatedbydistributeur(currentpage + 1,
    // length, user.getUserid());
    // }

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());

    return myGreetings;
  }

  @RequestMapping(method = RequestMethod.GET, value = "calculeencaissement")
  @ResponseBody
  public HashMap<String, Object> calculeEncaismenet() {
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userservice.findUsersByEmail(currentUser);
    Double totaleciasmentNonpayee = encaissementService.countByUserAndIschifcopayed(user);
    Double totalAvoirNonPayee = encaissementService.countAvoirByUser(user);
    if (totaleciasmentNonpayee == null)
      totaleciasmentNonpayee = 0.0;
    myGreetings.put("plafon", user.getPlafonRevendeur());
    myGreetings.put("total", totaleciasmentNonpayee - totalAvoirNonPayee);
    myGreetings.put("serverNameCRM", serverNameCRM);

    return myGreetings;
  }

  @PreAuthorize("hasAuthority('READ_RETAIL_SUMMARY_DETAILS')")
  @RequestMapping(method = RequestMethod.GET, value = "viewDetailsRecapDemande/{userid}")
  @ResponseBody
  public HashMap<String, Object> viewDetailsRecapDemande(@PathVariable("userid") Long Userid,
      @RequestParam("draw") int draw, @RequestParam("start") int start,
      @RequestParam("length") int length) {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    Page<DemandeAbonnement> responseData = null;
    String statut = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    int currentpage = start / length;
    User user = userservice.findUsersByIduser(Userid);
    {
      responseData = demandeAbonnementService.findPaginatedByRevendeurWithSort(currentpage + 1,
          length, user.getRole().getRoleId(), user.getUserid(), "createdDate", "desc");

      myGreetings.put("data", responseData.getContent());
      myGreetings.put("draw", draw);
      myGreetings.put("start", start);
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());

      return myGreetings;
    }
  }

  @GetMapping("/extractenmasse")
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(value = "ExportRechercheNom", required = false) String Nom,
      @RequestParam(value = "ExportRecherchePrenom", required = false) String Prenom,
      @RequestParam(value = "ExportRechercheRefUser", required = false) String RefUser,
      @RequestParam(value = "ExportRechercheStatus", required = false) Boolean Status,
      @RequestParam(value = "ExportRechercheGouvernorats", required = false) Long Gouvernorats,
      @RequestParam(value = "ExportRechercheVilles", required = false) Long Villes,
      @RequestParam(value = "ExportRechercheDistributeur", required = false) Long Distributeur,
      @RequestParam(value = "ExportRechercheDatedebut", required = false) String Datedebut,
      @RequestParam(value = "ExportRechercheDatefin", required = false) String Datefin) {
    return encaissementService.exportToExcel(request, response, Nom, Prenom, RefUser, Status,
        Gouvernorats, Villes, Distributeur, Datedebut, Datefin);
  }

  @GetMapping("/factureDownloadEnMasse")
  public ModelAndView exportToExcelFacture(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(value = "ExportRecherchePrixMin", required = false) Double PrixMin,
      @RequestParam(value = "ExportRecherchePrixmax", required = false) Double Prixmax,
      @RequestParam(value = "ExportRechercheDatedebut", required = false) String Datedebut,
      @RequestParam(value = "ExportRechercheCodeDatefin", required = false) String Datefin,
      @RequestParam(value = "ExportRechercheRef_facture", required = false) String Ref_facture,
      @RequestParam(value = "ExportRechercheRef_avoir", required = false) String Ref_avoir

  ) {
    return encaissementService.exportToExcelFactureEncaisse(request, PrixMin, Prixmax, Datedebut,
        Datefin, Ref_facture, Ref_avoir, response);
  }

  @ResponseBody
  @PostMapping(value = "/allFactureId")
  public Map<String, Object> getAllFactureId(@RequestBody String filterrecherche, Model model) {

    String role = null;
    List<String> StringsRole = new ArrayList<String>();
    User user = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userservice.findUsersByEmail(currentUser);
    }

    return encaissementService.getAllFactureId(user.getUserid(), filterrecherche);
  }

}
