package crm.chifco.com.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import crm.chifco.com.ApiDTO.EncaissementDto;
import crm.chifco.com.DTOclass.CommissionDemDash;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AvanceCommissionAcquisition;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.DemandeCommission;
import crm.chifco.com.model.DetailsCommissionDemande;
import crm.chifco.com.model.DetailsCommissionFacture;

import crm.chifco.com.model.DetailsCommissionFactureMiseEnService;

import crm.chifco.com.model.DetailsRetardCommissionPremiereFacture;

import crm.chifco.com.model.DetailsCommissionPremiereFacture;
import crm.chifco.com.model.DetailsRetardCommissionPremiereFacture;
import crm.chifco.com.model.OffreCommission;
import crm.chifco.com.model.OffreCommissionPromo;
import crm.chifco.com.model.User;
import crm.chifco.com.model.jasper.FactureCommisionDataSet;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AvanceCommissionAcquisitionRepository;
import crm.chifco.com.repository.CommissionRepository;
import crm.chifco.com.repository.DemandeCommissionRepository;
import crm.chifco.com.repository.DetailsCommissionDemandeRepository;
import crm.chifco.com.repository.DetailsCommissionFactureMiseEnServiceRepository;
import crm.chifco.com.repository.DetailsCommissionFactureRepository;
import crm.chifco.com.repository.DetailsCommissionPremiereFactureRepository;
import crm.chifco.com.repository.DetailsRetardCommissionPremiereFactureRepository;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.OffreCommissionPromoRepository;
import crm.chifco.com.repository.OffreCommissionRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AbonnementService;
import crm.chifco.com.service.CommissionService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.PrefixDocument;
import crm.chifco.com.utils.StatutAvanceBordereau;
import crm.chifco.com.utils.UserTypeConstant;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import crm.chifco.com.utils.FactureDetailCommision;
@Controller
@RequestMapping(value = "commission/*")
public class CommissionController {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  UserRepository userRepository;

  @Autowired
  CommissionService commissionService;

  @Autowired
  CommissionRepository commissionRepository;

  @Autowired
  DetailsCommissionDemandeRepository detailsCommissionDemandeRepository;

  @Autowired
  DetailsCommissionPremiereFactureRepository detailsCommissionPremiereFactureRepository;

  @Autowired
  DetailsCommissionFactureRepository detailsCommissionFactureRepository;

  @Autowired
  DemandeCommissionRepository demandeCommissionRepository;

  @Autowired
  OffreCommissionRepository offreCommissionRepository;

  @Autowired
  OffreCommissionPromoRepository offreCommissionPromoRepository;

  @Autowired
  UserService userService;

  @Autowired
  EncaissementRepository encaissementRepository;

  @Autowired
  AvanceCommissionAcquisitionRepository avanceCommissionAcquisitionRepository;
  
  @Autowired
  DetailsRetardCommissionPremiereFactureRepository detailsRetardCommissionPremiereFactureRepository;

  @Autowired
  DetailsCommissionFactureMiseEnServiceRepository detailsCommissionFactureMiseEnServiceRepository;

  @Autowired
  AbonnementService abonnementService;

  @Autowired
  AbonnementRepository abonnementRepository;


  @Value("${pathFacture}")
  private String pathFacture;

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER_FREELANCER')"
	      + "|| hasAuthority('COMMISSION_FINNANCIER_ALL')")
	  @RequestMapping(method = RequestMethod.GET, value = "suivifreelance")
	  public String suivifreelance(Model model, RedirectAttributes redirectAttrs) {

	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (!(authentication instanceof AnonymousAuthenticationToken)) {
	      String currentUser = authentication.getName();

	      Optional<User> user = Optional.of(userRepository.findUsersByEmail(currentUser));
	      List<String> StringsRole =
	          user.get().getRole().getStringsRole(user.get().getRole().getPrivileges());

	      List<User> users = new ArrayList<>();
	      if (StringsRole.contains("COMMISSION_FINNANCIER_ALL")) {
	        users = userRepository.findUsersByTypeUserNotIn(
	            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
	      } else if (StringsRole.contains("COMMISSION_FINNANCIER_AREA")) {
	        users = userRepository.findUsersByAffectedTo(user.get().getUserid());
	      }

	      model.addAttribute("client", users);

	      model.addAttribute("userfullname",
	          user.get().getLastName() + " " + user.get().getFirstName());
	      model.addAttribute("userphoto", user.get().getPhoto());
	      model.addAttribute("userrole", user.get().getRole().getRoleName());
	      model.addAttribute("useremail", user.get().getEmail());
	    }


	    return "commission/suivifreelance";
	  }
  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_FINNANCIER_AREA')"
      + "|| hasAuthority('COMMISSION_FINNANCIER_ALL')")
  @RequestMapping(method = RequestMethod.GET, value = "suivi_page")
  public String showSuiviPage(Model model, RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();

      Optional<User> user = Optional.of(userRepository.findUsersByEmail(currentUser));
      List<String> StringsRole =
          user.get().getRole().getStringsRole(user.get().getRole().getPrivileges());

      List<User> users = new ArrayList<>();
      if (StringsRole.contains("COMMISSION_FINNANCIER_ALL")) {
        users = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("COMMISSION_FINNANCIER_AREA")) {
        users = userRepository.findUsersByAffectedTo(user.get().getUserid());
      }

      model.addAttribute("client", users);

      model.addAttribute("userfullname",
          user.get().getLastName() + " " + user.get().getFirstName());
      model.addAttribute("userphoto", user.get().getPhoto());
      model.addAttribute("userrole", user.get().getRole().getRoleName());
      model.addAttribute("useremail", user.get().getEmail());
    }


    return "commission/suivi";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_FINNANCIER_AREA')"
      + "|| hasAuthority('COMMISSION_FINNANCIER_ALL')")
  @RequestMapping(method = RequestMethod.POST, value = "getCommission",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public HashMap<String, Object> getCommission(
      @RequestParam(value = "date", required = false) String date,
      @RequestParam(value = "revendeur", required = false) String revendeur) {

    HashMap<String, Object> result = new HashMap<>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      Optional<User> user = Optional.of(userRepository.findUsersByEmail(currentUser));
      List<String> StringsRole =
          user.get().getRole().getStringsRole(user.get().getRole().getPrivileges());

      List<String> erreur = new ArrayList<>();

      // validation date obligatoire
      if (date.isEmpty()) {
        erreur.add("DATE_REQUIRED");
      }

      // validation revendeur obligatoire avec privilège : COMMISSION_FINNANCIER_ALL
      if (StringsRole.contains("COMMISSION_FINNANCIER_ALL")) {
        if (revendeur.isEmpty()) {
          erreur.add("REV_REQUIRED");
        }
      }

      if (!erreur.isEmpty()) {
        result.put("erreur", erreur);
        return result;
      }

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
      YearMonth yearMonth = YearMonth.parse(date.trim(), formatter);
      Integer numMois = null;
      Integer annee = null;
      annee = yearMonth.getYear();
      numMois = yearMonth.getMonth().getValue();
      Boolean isAdmin = false;
      if (StringsRole.contains("COMMISSION_FINNANCIER_ALL")
          || StringsRole.contains("COMMISSION_FINNANCIER_AREA")) {
        user = userRepository.findByCodeUser(revendeur);
        if (user.isPresent()) {
          result = commissionService.CalculeCommisionFront(false,user.get().getUserid(), date);
          isAdmin = true;

          // afficher la liste first facture non payé
          LocalDate startOfMonth = yearMonth.atDay(1);
          LocalDate endOfMonth = yearMonth.atEndOfMonth();

          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

          List<EncaissementDto> listEncaissementFirstFactureNonVerse =
              encaissementRepository.findAllFirstFactureNonVerse(user.get().getUserid(),
                  sdf.format(CrmUtils.convertStringToDate(startOfMonth.toString())),
                  sdf.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString())));
          result.put("listFirstFactureNonVerse", listEncaissementFirstFactureNonVerse);

        } else {
          erreur.add("REV_NOT_FOUND");
          result.put("erreur", erreur);
          return result;
        }

      } else {
        result = commissionService.CalculeCommisionFront(false,user.get().getUserid(), date);
      }



      Boolean NonExisteCommision = commissionService.findExiteCommision(user.get(), annee, numMois);
      List<OffreCommissionPromo> existeOffrePromo =
          offreCommissionPromoRepository.findCommisionPromoInMonth(numMois, annee);
      Boolean verif15Jours = null;
      Boolean verifCommissionEnAttente = null;
      Boolean verifFirstFactureNonVerse = null;
      Boolean verifFactureNonVerse = null;
      if (NonExisteCommision) {

        // La validation doit être effectuée 15 jours après le début de chaque mois."
        YearMonth moisSuivant = yearMonth.plusMonths(1);
        LocalDate premierJourDuMoisSuivant = moisSuivant.atDay(1);
        LocalDate currentDate = LocalDate.now();
        LocalDate datePlus10Days = premierJourDuMoisSuivant.plus(10, ChronoUnit.DAYS);
        verif15Jours = (currentDate.isAfter(datePlus10Days) || currentDate.isEqual(datePlus10Days));

        // Il ne doit y avoir aucune demande en attente pour valider la commission.
        HashMap<String, Object> commissionDemandes =
            (HashMap<String, Object>) result.get("commissionDemandes");
        verifCommissionEnAttente =
            Integer.parseInt(commissionDemandes.get("demandeEnAttente").toString()) > 0;

        // Il ne doit y avoir aucune First Facture non versé pour valider la commission.
        HashMap<String, Object> commissionActivation =
            (HashMap<String, Object>) result.get("commissionActivation");
        /*
         * verifFirstFactureNonVerse =
         * Integer.parseInt(commissionActivation.get("nbFirsFactureNonVerse").toString()) > 0;
         */
        // Il ne doit y avoir aucune facture non versé pour valider la commission.
        HashMap<String, Object> commissionPaiements =
            (HashMap<String, Object>) result.get("commissionPaiements");
        verifFactureNonVerse =
            Integer.parseInt(commissionPaiements.get("nombreFactureNonVerse").toString()) > 0;

      }

      result.put("monthName", numMois);
      result.put("year", annee);
      result.put("validation", NonExisteCommision);
      result.put("verif15Jours", verif15Jours);
      result.put("verifCommissionEnAttente", verifCommissionEnAttente);
      // result.put("verifFirstFactureNonVerse", verifFirstFactureNonVerse);
      result.put("verifFactureNonVerse", verifFactureNonVerse);
      result.put("isAdmin", isAdmin);
      if (existeOffrePromo.size() > 0) {
        Boolean isSavedPromo = commissionService.findExiteCommisionPromo(user.get(),
            existeOffrePromo.get(0).getDateDebut(), existeOffrePromo.get(0).getDateFin());
        if (isSavedPromo) {
          result.put("isexsitePromo", true);
        } else {
          result.put("isexsitePromo", false);
        }

      } else
        result.put("isexsitePromo", false);

    }
    return result;

  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER' , 'COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.GET, value = "mes_commission_page")
  public String mesCommission(Model model, RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    return "commission/mesCommission";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.GET, value = "mesCommission")
  @ResponseBody
  public Map<String, Object> getCommissions(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") int ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    // Exemple de données de commission pour les mois de Janvier et Février

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = new User();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);

    }

    return commissionService.getAllByRevendeurId(draw, start, length, search, ordercolumnaram,
        orderdir, filterrecherche, user.getUserid());

  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN','COMMISSION_AREA')")
  @RequestMapping(method = RequestMethod.GET, value = "all_commission_page")
  public String allCommission(Model model, RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      Boolean isCancelCommission = StringsRole.contains("CANCEL_COMMISSION");
      Boolean isCommissionAdmin = StringsRole.contains("COMMISSION_ADMIN");
      Boolean isCommissionArea = StringsRole.contains("COMMISSION_AREA");

      model.addAttribute("isCancelCommission", isCancelCommission);

      if (isCommissionAdmin) {
        model.addAttribute("listeUser", userRepository.findUsersByTypeUser("REVENDEUR"));
      } else if (isCommissionArea) {
        model.addAttribute("listeUser", userRepository.findUsersByAffectedTo(user.getUserid()));
      }
    }

    return "commission/allCommission";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN','COMMISSION_AREA')")
  @RequestMapping(method = RequestMethod.GET, value = "allCommission")
  @ResponseBody
  public Map<String, Object> getAllCommissions(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") int ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

      Boolean isCommissionAdmin = StringsRole.contains("COMMISSION_ADMIN");
      Boolean isCommissionArea = StringsRole.contains("COMMISSION_AREA");
      Long idUserConnected = user.getUserid();

      return commissionService.getAll(draw, start, length, search, ordercolumnaram, orderdir,
          filterrecherche, isCommissionAdmin, isCommissionArea, idUserConnected);
    }
    return null;
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN','COMMISSION_OWNER','COMMISSION_AREA')")
  @RequestMapping(method = RequestMethod.GET, value = "details_commission_page/{id}")
  public String detailsCommission(@PathVariable Long id, Model model,
      RedirectAttributes redirectAttrs) {

    Optional<Commission> commission = commissionService.getDetailsCommission(id);
    if (!commission.isPresent()) {
      model.addAttribute("commission", null);
      return "commission/detailsCommission";
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

      if (StringsRole.contains("COMMISSION_OWNER")) {
        if (!commission.get().getRevendeur().getUserid().equals(user.getUserid())) {
          return "redirect:/access-denied";
        }
      } else if (StringsRole.contains("COMMISSION_AREA")) {
        if (!commission.get().getRevendeur().getAffectedTo().equals(user.getUserid())) {
          return "redirect:/access-denied";
        }
      }
    }

    model.addAttribute("commission", commission.get());

    List<DetailsCommissionDemande> lDetailsCommissionDemandes =
        detailsCommissionDemandeRepository.findAllByCommissionId(id);
    model.addAttribute("lDetailsCommissionDemandes", lDetailsCommissionDemandes);

    List<DetailsCommissionPremiereFacture> lDetailsCommissionPremiereFactures =
        detailsCommissionPremiereFactureRepository.findAllByCommissionId(id);
    model.addAttribute("lDetailsCommissionPremiereFactures", lDetailsCommissionPremiereFactures);

    List<DetailsCommissionFacture> lDetailsCommissionFactures =
        detailsCommissionFactureRepository.findAllByCommissionId(id);
    model.addAttribute("lDetailsCommissionFactures", lDetailsCommissionFactures);

    List<DemandeCommission> lDemandeCommissions =
        demandeCommissionRepository.findAllByCommissionId(id);
    model.addAttribute("lDemandeCommissions", lDemandeCommissions);

    return "commission/detailsCommission";

  }
  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN','COMMISSION_OWNER_FREELANCER','COMMISSION_AREA')")
  @RequestMapping(method = RequestMethod.GET, value = "details_commission_page_freelancer/{id}")
  public String details_commission_page_freelancer(@PathVariable Long id, Model model,
      RedirectAttributes redirectAttrs) {

    Optional<Commission> commission = commissionService.getDetailsCommission(id);
    if (!commission.isPresent()) {
      model.addAttribute("commission", null);
      return "commission/detailsCommissionfreelance";
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

      if (StringsRole.contains("COMMISSION_OWNER")) {
        if (!commission.get().getRevendeur().getUserid().equals(user.getUserid())) {
          return "redirect:/access-denied";
        }
      } else if (StringsRole.contains("COMMISSION_AREA")) {
        if (!commission.get().getRevendeur().getAffectedTo().equals(user.getUserid())) {
          return "redirect:/access-denied";
        }
      }
    }

    model.addAttribute("commission", commission.get());

    List<DetailsCommissionDemande> lDetailsCommissionDemandes =
        detailsCommissionDemandeRepository.findAllByCommissionId(id);
    model.addAttribute("lDetailsCommissionDemandes", lDetailsCommissionDemandes);

    List<DetailsCommissionPremiereFacture> lDetailsCommissionPremiereFactures =
        detailsCommissionPremiereFactureRepository.findAllByCommissionId(id);
    model.addAttribute("lDetailsCommissionPremiereFactures", lDetailsCommissionPremiereFactures);

    List<DetailsCommissionFacture> lDetailsCommissionFactures =
        detailsCommissionFactureRepository.findAllByCommissionId(id);
    model.addAttribute("lDetailsCommissionFactures", lDetailsCommissionFactures);
    
    List<DetailsCommissionFactureMiseEnService> DetailsCommissionFactureMiseEnService =
    		detailsCommissionFactureMiseEnServiceRepository.findAllByCommissionId(id);
        model.addAttribute("DetailsCommissionFactureMiseEnService", DetailsCommissionFactureMiseEnService);

       
            
    List<DemandeCommission> lDemandeCommissions =
        demandeCommissionRepository.findAllByCommissionId(id);
    model.addAttribute("lDemandeCommissions", lDemandeCommissions);

    return "commission/detailsCommissionfreelance";

  }
  
  
  
/*  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER')")
  @RequestMapping(method = RequestMethod.POST, value = "saveCommisionMiseEnService",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Boolean saveCommisionMiseEnService(@RequestBody HashMap<String, Object> obj) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
    	
    	return true ;
    }
    return false ;
    }
  */
  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.POST, value = "saveCommision",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Boolean saveCommision(@RequestBody HashMap<String, Object> obj) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      Boolean NonExisteCommision =
          commissionService.findExiteCommision(user, Integer.parseInt(obj.get("year").toString()),
              Integer.parseInt(obj.get("monthName").toString()));
      if (!NonExisteCommision) {
        return false;
      }
      List<AvanceCommissionAcquisition> listCommisionValiderNonPayee =
          avanceCommissionAcquisitionRepository.findByStatutAndRevendeur_userid(
              StatutAvanceBordereau.CEREATE_AVANCE, user.getUserid());

      listCommisionValiderNonPayee.forEach(el -> {

        el.setStatut(StatutAvanceBordereau.AVANCE_TREAT_VALIDATION_COMMISION);
        avanceCommissionAcquisitionRepository.save(el);

      });
      List<AvanceCommissionAcquisition> listCommisionValidereEnCours =
          avanceCommissionAcquisitionRepository.findByStatutAndRevendeur_userid(
              StatutAvanceBordereau.AVANCE_INSTENCE, user.getUserid());

      listCommisionValidereEnCours.forEach(el -> {

        el.setStatut(StatutAvanceBordereau.AVANCE_TREAT_VALIDATION_COMMISION);
        avanceCommissionAcquisitionRepository.save(el);

      });
      Commission newCommision = new Commission();
      JSONObject myobj = new JSONObject(obj);
      ObjectMapper objectMapper = new ObjectMapper();

      // String comm = myobj.getString("monthName");
      HashMap<String, Object> commissionDemandes =
          (HashMap<String, Object>) obj.get("commissionDemandes");

      HashMap<String, Object> commissionActivation =
          (HashMap<String, Object>) obj.get("commissionActivation");

      HashMap<String, Object> commissionPaiements =
          (HashMap<String, Object>) obj.get("commissionPaiements");
      
      HashMap<String, Object> commissionMiseEnservicePaiements =
              (HashMap<String, Object>) obj.get("commissionMiseEnservicePaiements");
      if(commissionMiseEnservicePaiements != null && !commissionMiseEnservicePaiements.isEmpty()) {
     	 
          List<Object> detailsCommissionMiseService =
                  (List<Object>) commissionMiseEnservicePaiements.get("detailsCommissionFacture");
              if ( detailsCommissionMiseService.size() > 0) {
                List<FactureDetailCommision> list1 =
                		detailsCommissionMiseService.stream().map(item -> {
                			FactureDetailCommision dto =
                          objectMapper.convertValue(item, FactureDetailCommision.class);
                      return dto;
                    }).collect(Collectors.toList());
                List<FactureDetailCommision> list2 = new ArrayList<>();
                List<Object> detailsCommissionActivationList =
                        (List<Object>) commissionActivation.get("detailsCommissionFirstFactures");
                    if (detailsCommissionActivationList.size() > 0) {
                      list2 =
                          detailsCommissionActivationList.stream().map(item -> {
                        	  FactureDetailCommision dto =
                                objectMapper.convertValue(item, FactureDetailCommision.class);
                       
                            return dto;
                          }).collect(Collectors.toList());
                    }
      List<FactureDetailCommision> result = new ArrayList<>();

      for (FactureDetailCommision f1 : list1) {
          if (!list2.contains(f1)) {
              result.add(f1);
              Double comisionfact = (0.7 * (f1.montantCommisison/0.3));
              abonnementService.findAbonnementByReferenceClientAndUpdate(f1.referenceClient , comisionfact.toString());
          }
      }

      // Affichage
      System.out.println("Éléments dans list1 mais pas dans list2 :");
      for (FactureDetailCommision f : result) {
          System.out.println(" - " + f.referenceFacture);
      }
              }
      }
   
      newCommision.setAnnee(Integer.parseInt(obj.get("year").toString()));
      // newCommision.setCommentaire(null);
      newCommision.setMois(Integer.parseInt(obj.get("monthName").toString()));
      newCommision.setMontantCommissionDemandes(
          Double.parseDouble(commissionDemandes.get("total").toString()));
      newCommision.setMontantCommissionPaiements(
          Double.parseDouble(commissionPaiements.get("totalCommissionsPaiement").toString()));
      newCommision.setMontantCommissionPremiereFacture(
          Double.parseDouble(commissionActivation.get("totalCommissions").toString()));
      newCommision.setMontantAvancePremiereFacture(
          Double.parseDouble(commissionActivation.get("totalCommissionsAvancePayee").toString()));
      newCommision.setMontantTotalPremiereFacture(
          Double.parseDouble(commissionActivation.get("totalCommissionsSansAvance").toString()));

      if(commissionActivation.containsKey("totalCommissionsActivationNewFreelance")) {
    	  
    	  newCommision.setTotalCommissionsActivationNewFreelance( Double.parseDouble(commissionActivation.get("totalCommissionsActivationNewFreelance").toString()));
      }
      newCommision.setNbrDemandesAcceptees(
          Integer.parseInt(commissionDemandes.get("demandesAccepte").toString()));



      newCommision.setNbrDemandesActivees(
          Integer.parseInt(commissionActivation.get("nbCommissionActivation").toString()));
      newCommision.setMontantRetardPayemnt( Double.parseDouble(commissionActivation.get("totalRetardPayement").toString()));
      
      newCommision.setNbrDemandesRejetees(
          Integer.parseInt(commissionDemandes.get("demandesRejete").toString()));
      newCommision.setNbrDemandesEnAttente(
          Integer.parseInt(commissionDemandes.get("demandeEnAttente").toString()));
      newCommision.setNbrDemandesNonRealisee(
          Integer.parseInt(commissionDemandes.get("nbFirsFactureNonPayed").toString()));
      newCommision.setNbrFacturesPayees(
          Integer.parseInt(commissionPaiements.get("nbFacturePayee").toString()));
      newCommision.setNbrFacturesNonVerseePayement(
          Integer.parseInt(commissionPaiements.get("nombreFactureNonVerse").toString()));
      newCommision.setNbrTotalDemandes(
          Integer.parseInt(commissionDemandes.get("totalDemandes").toString()));
      
      if(commissionDemandes.containsKey("primeMs")) {
          newCommision.setPrimeCommision(Double.parseDouble(commissionDemandes.get("primeMs").toString()));


      }
      newCommision.setRevendeur(user);
      newCommision.setStatut("NOT_PAID");
      if(commissionMiseEnservicePaiements != null && !commissionMiseEnservicePaiements.isEmpty()) {
    	  newCommision.setNbrFactureMiseService( Integer.parseInt(commissionMiseEnservicePaiements.get("nbrFactureMiseService").toString()));
          newCommision.setMontantCommissionPremiereFactureMiseService(Double.parseDouble(commissionMiseEnservicePaiements.get("montantCommissionPremiereFactureMiseService").toString()));
      } else {
    	  newCommision.setNbrFactureMiseService(0); 
    	  newCommision.setMontantCommissionPremiereFactureMiseService(0.0);
      }
      
   /*   if(commissionActivation.get("totalRetardPayement") != null
    		  && commissionActivation.get("totalechancePayement") != null  
    		  ) {
          newCommision.setMontantRetardPayemnt(Double.parseDouble(commissionActivation.get("totalechancePayement").toString()) - Double.parseDouble(commissionActivation.get("totalRetardPayement").toString()));
      }
      else {
    	  newCommision.setMontantRetardPayemnt(0.0);  
      }*/
          newCommision.setTotalHt(newCommision.getMontantCommissionDemandes()
          + newCommision.getMontantTotalPremiereFacture()
          + newCommision.getMontantCommissionPremiereFactureMiseService()
          + newCommision.getMontantCommissionPaiements()
          + newCommision.getPrimeCommision()
         // + newCommision.getMontantRetardPayemnt()
        		  );
          if(user.getIsExonoree()) {
              newCommision.setTva(0);
              newCommision.setMontantTva(newCommision.getTotalHt() * (newCommision.getTva() / 100.0));
           
              newCommision.setMontantTva(0.0);
          }else {
        	  
          newCommision.setTva(19);
          newCommision.setMontantTva(newCommision.getTotalHt()*.19);
          }

      newCommision.setTotalTtc(newCommision.getTotalHt() + newCommision.getMontantTva());
      // - (newCommision.getMontantCommissionPaiements()
      // + (newCommision.getMontantCommissionPaiements() * 0.19)
      // + newCommision.getRetunuSource()));
      newCommision.setRetunuSource(newCommision.getTotalTtc() * 0.10);
      if(commissionDemandes.get("isFreelance") != null ) {
    	  newCommision.setIsFreelance(true);
      }
      Commission commission = commissionRepository.save(newCommision);

      // details commission
      if(commissionMiseEnservicePaiements != null && !commissionMiseEnservicePaiements.isEmpty()) {
    	 
          List<Object> detailsCommissionMiseService =
                  (List<Object>) commissionMiseEnservicePaiements.get("detailsCommissionFacture");
              if (commission.getMontantCommissionPremiereFactureMiseService() != 0
                  && detailsCommissionMiseService.size() > 0) {
                List<DetailsCommissionFactureMiseEnService> streamDetailsCommissionFacture =
                		detailsCommissionMiseService.stream().map(item -> {
                    	DetailsCommissionFactureMiseEnService dto =
                          objectMapper.convertValue(item, DetailsCommissionFactureMiseEnService.class);
                      dto.setCommissionId(commission.getId());
                      return dto;
                    }).collect(Collectors.toList());

                detailsCommissionFactureMiseEnServiceRepository.saveAll(streamDetailsCommissionFacture);
     }
     }
  
      if(commissionActivation.get("listeRetardPayement") != null && commissionActivation.get("listeechancePayement") != null  ) {

    /*
      List<Object> listeRetardPayement =
              (List<Object>) commissionPaiements.get("listeRetardPayement");
          if (commission.getMontantRetardPayemnt() != 0
              && listeRetardPayement.size() > 0) {
            List<DetailsRetardCommissionPremiereFacture> streamDetailsCommissionFacture =
            		listeRetardPayement.stream().map(item -> {
            			DetailsRetardCommissionPremiereFacture dto =
                      objectMapper.convertValue(item, DetailsRetardCommissionPremiereFacture.class);
                  dto.setCommissionId(commission.getId());
                  dto.setMontantCommisison(-3.0);
                  return dto;
                }).collect(Collectors.toList());
            */
            List<Object> listeechancePayement =
                    (List<Object>) commissionPaiements.get("listeechancePayement");
        	/*   if (commission.getMontantRetardPayemnt() != 0
                    && listeechancePayement.size() > 0) {
                  List<DetailsRetardCommissionPremiereFacture> streamDetailsCommissionFactureEchnce =
                  		listeRetardPayement.stream().map(item -> {
                  			DetailsRetardCommissionPremiereFacture dto =
                            objectMapper.convertValue(item, DetailsRetardCommissionPremiereFacture.class);
                        dto.setCommissionId(commission.getId());
                        dto.setMontantCommisison(3.0);
                        return dto;
                      }).collect(Collectors.toList());
                
            detailsRetardCommissionPremiereFactureRepository.saveAll(streamDetailsCommissionFactureEchnce);
          } 
          
      } */
      }
      List<Object> detailsioCommissnDemandeList =
          (List<Object>) commissionDemandes.get("detailsCommissionDemande");
      if (commission.getMontantCommissionDemandes() != 0
          && detailsioCommissnDemandeList.size() > 0) {
        List<DetailsCommissionDemande> streamDetailsCommissionDemande =
            detailsioCommissnDemandeList.stream().map(item -> {
              DetailsCommissionDemande dto =
                  objectMapper.convertValue(item, DetailsCommissionDemande.class);
              dto.setCommissionId(commission.getId());
              return dto;
            }).collect(Collectors.toList());
        detailsCommissionDemandeRepository.saveAll(streamDetailsCommissionDemande);
      }

      List<Object> detailsCommissionActivationList =
          (List<Object>) commissionActivation.get("detailsCommissionFirstFactures");
      if (commission.getMontantCommissionPremiereFacture() != 0
          && detailsCommissionActivationList.size() > 0) {
        List<DetailsCommissionPremiereFacture> streamDetailsCommissionActivation =
            detailsCommissionActivationList.stream().map(item -> {
              DetailsCommissionPremiereFacture dto =
                  objectMapper.convertValue(item, DetailsCommissionPremiereFacture.class);
              dto.setCommissionId(commission.getId());
              return dto;
            }).collect(Collectors.toList());
      
        detailsCommissionPremiereFactureRepository.saveAll(streamDetailsCommissionActivation);
        
        // 1. Filtrer la liste
        List<DetailsCommissionPremiereFacture> clientsFiltres = streamDetailsCommissionActivation.stream()
            .filter(el -> el.getFromClient() != null && el.getFromClient()) // filtre fromClient == true
            .collect(Collectors.toList());
        
        String[]   references = clientsFiltres.stream()
                .map(DetailsCommissionPremiereFacture::getReferenceClient)
                .toArray(String[]::new);
        List<Abonnement>  getallclientsFiltres =   abonnementService.findAbonnementsByReferenceClient(new ArrayList<>(Arrays.asList(references)));
        		getallclientsFiltres.forEach(el -> {
            el.setComissionActivationIsPayed(false);
        });
        abonnementRepository.saveAll(getallclientsFiltres); // dépend de ton framework (ex: Spring Data JPA)

      }

      List<Object> detailsCommissionFactureList =
          (List<Object>) commissionPaiements.get("detailsCommissionFacture");
      if (commission.getMontantCommissionPaiements() != 0
          && detailsCommissionFactureList.size() > 0) {
        List<DetailsCommissionFacture> streamDetailsCommissionFacture =
            detailsCommissionFactureList.stream().map(item -> {
              DetailsCommissionFacture dto =
                  objectMapper.convertValue(item, DetailsCommissionFacture.class);
              dto.setCommissionId(commission.getId());
              return dto;
            }).collect(Collectors.toList());
        detailsCommissionFactureRepository.saveAll(streamDetailsCommissionFacture);
      }
      /*
      List<Object> listeRetardPayement =
              (List<Object>) commissionPaiements.get("listeRetardPayement");
          if (commission.getMontantRetardPayemnt() != 0
              && listeRetardPayement.size() > 0) {
            List<DetailsRetardCommissionPremiereFacture> streamDetailsCommissionFacture =
            		listeRetardPayement.stream().map(item -> {
            			DetailsRetardCommissionPremiereFacture dto =
                      objectMapper.convertValue(item, DetailsRetardCommissionPremiereFacture.class);
                  dto.setCommissionId(commission.getId());
                  return dto;
                }).collect(Collectors.toList());
            detailsRetardCommissionPremiereFactureRepository.saveAll(streamDetailsCommissionFacture);
          }
          */
      return true;
    }
    return false;
  }

  @GetMapping("/extracteExcel")
  public ModelAndView exportToExcel(
      @RequestParam(name = "date", required = false, defaultValue = "") String date,
      @RequestParam(name = "statut", required = false, defaultValue = "") String statut,
      @RequestParam(name = "codeRevendeur", required = false,
          defaultValue = "") String codeRevendeur,
      @RequestParam(name = "startCreatedDate", required = false,
          defaultValue = "") String startCreatedDate,
      @RequestParam(name = "endCreatedDate", required = false,
          defaultValue = "") String endCreatedDate,
      @RequestParam(name = "reference", required = false, defaultValue = "") String reference,
      @RequestParam(name = "typeC", required = false,
      defaultValue = "") String typeC,
      
      
      HttpServletRequest request, HttpServletResponse response) {

    Integer annee = null;
    Integer numMois = null;
    Boolean typeCommision = null ;

    if (!"".equals(date)) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
      YearMonth yearMonth = YearMonth.parse(date, formatter);

      annee = yearMonth.getYear();
      numMois = yearMonth.getMonth().getValue();
    }

    if ("".equals(statut)) {
      statut = null;
    }

    if ("".equals(codeRevendeur)) {
      codeRevendeur = null;
    }

    Date startDate = null;
    Date endDate = null;
    if (!"".equals(startCreatedDate)) {
        startDate = CrmUtils.convertStringToDate(startCreatedDate);
      }
    if (!"".equals(typeC)) {
    	typeCommision = typeC.equals("true");
    }

    if (!"".equals(endCreatedDate)) {
      endDate = CrmUtils.convertStringToLocalDateTime(endCreatedDate);
    }

    if ("".equals(reference)) {
      reference = null;
    }

    return commissionService.exportListCommissionToExcel(annee, numMois, statut, codeRevendeur,
        startDate, endDate, reference,typeCommision, request, response);
  }

  @PreAuthorize("hasAnyAuthority('CANCEL_COMMISSION')")
  @PostMapping("/annuler-commission")
  public String annulerCommission(@RequestParam Long id, Model model,
      RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);

    String result = commissionService.annulerCommission(id, user);
    redirectAttrs.addFlashAttribute("result", result);

    return "redirect:all_commission_page";
  }

  @PreAuthorize("hasAnyAuthority('CANCEL_COMMISSION')")
  @PostMapping("/change-commission-to-awaiting")
  public String changeCommissionToAwaiting(
      @RequestParam(value = "source", defaultValue = "commission") String source,
      @RequestParam(value = "commissionId", required = false) Long commissionId,
      @RequestParam(value = "groupId", required = false) Long groupId,
      @RequestParam(value = "demandeid", required = false) Long demandeid,
      
      Model model, RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    String result = null ; 
    if(groupId != null)
    {
    	result = commissionService.changeCommissionGroupToAwaiting(groupId, user);

    }
    else {
    	result = commissionService.changeCommissionToAwaiting(commissionId, user);
    }
    redirectAttrs.addFlashAttribute("result", result);

    // Redirect based on source
    if ("demande".equals(source) && commissionId != null) {
      return "redirect:/demandeCommission/details_demande_commission_page/" + demandeid;
    } else if ("group".equals(source) && groupId != null) {
      return "redirect:/demandeCommission/details_grouped_demande_commission_page/" + groupId;
    } else {
      return "redirect:/commission/details_commission_page/" + commissionId;
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "listeCommision")
  public String listeCommision(Model model) {
    userService.returnInfoUserConnected(model);
    return "commission/listeCommision";
  }

  @RequestMapping(method = RequestMethod.GET, value = "offrecommissions")
  public String listOffreCommissions(Model model) {
    userService.returnInfoUserConnected(model);
    return "commission/offreCommissions";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OFFER_MANAGEMENT')")
  @GetMapping("/allOffreCommissions")
  @ResponseBody
  public Map<String, Object> getAllOffreCommissions(@RequestParam int start,
      @RequestParam int length) {
    Map<String, Object> response = new HashMap<>();

    Page<OffreCommission> page =
        offreCommissionRepository.findAll(PageRequest.of(start / length, length));

    response.put("data", page.getContent());
    response.put("recordsTotal", page.getTotalElements());
    response.put("recordsFiltered", page.getTotalElements());

    return response;
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OFFER_MANAGEMENT')")
  @PostMapping("/offre/ajouter")
  @ResponseBody
  public String ajouterOffreCommission(@RequestParam(value = "date") String date,
      @RequestParam(value = "type") String type, @RequestParam(value = "montant") Double montant,
      @RequestParam(value = "debit") Integer debit, @RequestParam(value = "palier") String palier) {

    Integer palierMin = 0;
    Integer palierMax = 0;
    switch (palier) {
      case "0-49":
        palierMin = 0;
        palierMax = 49;
        break;
      case "50-99":
        palierMin = 50;
        palierMax = 99;
        break;
      case "100":
        palierMin = 100;
        palierMax = null;
        break;
      default:
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);

    String resultat = commissionService.ajouterOffreCommission(date, type, montant, debit,
        palierMin, palierMax, user);

    return resultat;
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OFFER_MANAGEMENT')")
  @PostMapping("/changer-etat-offre")
  public String changerEtatOffre(@RequestParam("idOffre") Long idOffre, Model model,
      RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);

    String resultat = commissionService.changementEtatOffre(idOffre, user);

    redirectAttrs.addFlashAttribute("resultat", resultat);

    return "redirect:offrecommissions";
  }


  // @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER')")
  @PostMapping("/telechergerFacturePdf/{id}")
  public void telechergerFacturePdf(HttpServletResponse response, @PathVariable Long id,
      @RequestParam("numfact") String numfact, @RequestParam("matfiscale") String matfiscale,
     @RequestParam("cbancaire") String cbancaire) {
    File file1 = null;
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      Optional<Commission> resultat = commissionService.getDetailsCommission(id);
      Map<String, Object> parametes = new HashMap<>();
      Collection<FactureCommisionDataSet> factureCommisionDataSetArrayList = new ArrayList<>();
      FactureCommisionDataSet factureDataSet = new FactureCommisionDataSet();
      if (resultat.get() != null) {
        List<Commission> factureList = new ArrayList<>();
        factureList.add(resultat.get());
        Collection<Commission> commisionToGenerateFacture =
            Collections.synchronizedList(factureList);
        factureDataSet.setCommission(commisionToGenerateFacture);
        factureCommisionDataSetArrayList.add(factureDataSet);

        parametes.put("isset_tva", true);
        parametes.put("matfiscale", matfiscale);
        parametes.put("numfact", numfact);
        parametes.put("cbancaire", cbancaire);

        JRBeanCollectionDataSource dataSource =
            new JRBeanCollectionDataSource(factureCommisionDataSetArrayList);
        File file  = ResourceUtils.getFile("classpath:reports/factureCommisionA4WithTva.jrxml"); 
        if (user.getIsExonoree()) {
          file = ResourceUtils.getFile("classpath:reports/factureCommisionA4WithOutTva.jrxml");
        }
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametes, dataSource);
        File pathFolder = new File(pathFacture);
        if (!pathFolder.exists()) {
          pathFolder.mkdirs();
          pathFolder.setWritable(true);

        }

        String nomfile = pathFacture + "/" + PrefixDocument.NOMEFILE_FACTURE
            + resultat.get().getRefCommission() + ".pdf";

        JasperExportManager.exportReportToPdfFile(jasperPrint, nomfile);
        file1 = ResourceUtils.getFile(nomfile);
        response.setContentType(
            "application/x-pdf ; charset=" + Charset.forName("utf-8").displayName());
        response.setHeader("Content-disposition", "inline; filename=" + file1.getName());
        // get your file as InputStream
        InputStream targetStream = new FileInputStream(file1);
        // copy it to response's OutputStream
        org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
        response.flushBuffer();

        // close input stream file
        targetStream.close();
      }
    } catch (JRException e) {
      // TODO Auto-generated catch block
      LOGGER.warn("error creation commision facture: " + e);

    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @PostMapping("/telechergerFacturePdfMultiple")
  public void telechergerFacturePdfMultiple(HttpServletResponse response,
      @RequestParam("commissionIds") List<Long> commissionIds,
      @RequestParam("numfact") String numfact,
      @RequestParam("matfiscale") String matfiscale,
      @RequestParam("cbancaire") String cbancaire) {
    File file1 = null;
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      List<Commission> commissions = commissionRepository.findAllById(commissionIds);
      if (commissions == null || commissions.isEmpty()) {
        return;
      }

      Map<String, Object> parametes = new HashMap<>();
      Collection<FactureCommisionDataSet> factureCommisionDataSetArrayList = new ArrayList<>();
      FactureCommisionDataSet factureDataSet = new FactureCommisionDataSet();
      Collection<Commission> commisionToGenerateFacture = Collections.synchronizedList(commissions);
      User revendeur = commissions.get(0).getRevendeur();
      factureDataSet.setCommission(commisionToGenerateFacture);
      factureDataSet.setRevendeur(Collections.singletonList(revendeur));
      factureCommisionDataSetArrayList.add(factureDataSet);

      double totalHt = commissions.stream()
          .mapToDouble(c -> c.getTotalHt() != null ? c.getTotalHt() : 0.0)
          .sum();
      double totalTva = commissions.stream()
          .mapToDouble(c -> c.getMontantTva() != null ? c.getMontantTva() : 0.0)
          .sum();
      double montantAvancePremiereFacture = commissions.stream()
          .mapToDouble(c -> c.getMontantAvancePremiereFacture() != null ? c.getMontantAvancePremiereFacture() : 0.0)
          .sum();
      double totalTtc = commissions.stream()
          .mapToDouble(c -> c.getTotalTtc() != null ? c.getTotalTtc() : 0.0)
          .sum();
      double totalRetenu = commissions.stream()
          .mapToDouble(c -> c.getRetunuSource() != null ? c.getRetunuSource() : 0.0)
          .sum();

      parametes.put("isset_tva", true);
      parametes.put("matfiscale", matfiscale);
      parametes.put("numfact", numfact);
      parametes.put("cbancaire", cbancaire);
      parametes.put("createdDate", null);
      parametes.put("totalHt", totalHt);
      parametes.put("totalTTC", totalTtc);
      parametes.put("totalTva", totalTva);
      parametes.put("totalRetenu", totalRetenu);
      parametes.put("montantAvancePremiereFacture", montantAvancePremiereFacture);

      JRBeanCollectionDataSource dataSource =
          new JRBeanCollectionDataSource(factureCommisionDataSetArrayList);
      File file = ResourceUtils.getFile("classpath:reports/factureCommisionMultiplez.jrxml");
      JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametes, dataSource);
      File pathFolder = new File(pathFacture);
      if (!pathFolder.exists()) {
        pathFolder.mkdirs();
        pathFolder.setWritable(true);
      }

      String nomfile = pathFacture + "/" + PrefixDocument.NOMEFILE_FACTURE + "Multiple" + ".pdf";

      JasperExportManager.exportReportToPdfFile(jasperPrint, nomfile);
      file1 = ResourceUtils.getFile(nomfile);
      response.setContentType(
          "application/x-pdf ; charset=" + Charset.forName("utf-8").displayName());
      response.setHeader("Content-disposition", "inline; filename=" + file1.getName());
      InputStream targetStream = new FileInputStream(file1);
      org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
      response.flushBuffer();
      targetStream.close();
    } catch (JRException e) {
      LOGGER.warn("error creation commision facture multiple: " + e);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // return "redirect:/commission/details_commission_page/" + id;

  @RequestMapping(method = RequestMethod.GET, value = "/AllCommissionDashboard")
  @ResponseBody
 public HashMap<String, Object> AllCommissionDashboard(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) Long ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    int currentpage = start / length;
    Page<CommissionDemDash> responseData = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    responseData =
        commissionService.AllCommissionDashboard(currentpage + 1, length, filterrecherche);
    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    return myGreetings;

  }
  
  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER_FREELANCER','COMMISSION_FINNANCIER_AREA')"
	      + "|| hasAuthority('COMMISSION_FINNANCIER_ALL')")
	  @RequestMapping(method = RequestMethod.POST, value = "getCommissionFreelancer",
	      produces = MediaType.APPLICATION_JSON_VALUE)
	  @ResponseBody
	  public HashMap<String, Object> getCommissionFreelancer(
	      @RequestParam(value = "date", required = false) String date,
	      @RequestParam(value = "revendeur", required = false) String revendeur) {

	    HashMap<String, Object> result = new HashMap<>();
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (!(authentication instanceof AnonymousAuthenticationToken)) {
	      String currentUser = authentication.getName();
	      Optional<User> user = Optional.of(userRepository.findUsersByEmail(currentUser));
	      List<String> StringsRole =
	          user.get().getRole().getStringsRole(user.get().getRole().getPrivileges());

	      List<String> erreur = new ArrayList<>();

	      // validation date obligatoire
	      if (date.isEmpty()) {
	        erreur.add("DATE_REQUIRED");
	      }

	      // validation revendeur obligatoire avec privilège : COMMISSION_FINNANCIER_ALL
	      if (StringsRole.contains("COMMISSION_FINNANCIER_ALL")) {
	        if (revendeur.isEmpty()) {
	          erreur.add("REV_REQUIRED");
	        }
	      }

	      if (!erreur.isEmpty()) {
	        result.put("erreur", erreur);
	        return result;
	      }

	      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
	      YearMonth yearMonth = YearMonth.parse(date.trim(), formatter);
	      Integer numMois = null;
	      Integer annee = null;
	      annee = yearMonth.getYear();
	      numMois = yearMonth.getMonth().getValue();
	      Boolean isAdmin = false;
	      if (StringsRole.contains("COMMISSION_FINNANCIER_ALL")
	          || StringsRole.contains("COMMISSION_FINNANCIER_AREA")) {
	        user = userRepository.findByCodeUser(revendeur);
	        if (user.isPresent()) {
	          result = commissionService.CalculeCommisionFrontFreelancer(true,user.get().getUserid(), date);
	          isAdmin = true;

	          // afficher la liste first facture non payé
	          LocalDate startOfMonth = yearMonth.atDay(1);
	          LocalDate endOfMonth = yearMonth.atEndOfMonth();

	          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	          List<EncaissementDto> listEncaissementFirstFactureNonVerse =
	              encaissementRepository.findAllFirstFactureNonVerse(user.get().getUserid(),
	                  sdf.format(CrmUtils.convertStringToDate(startOfMonth.toString())),
	                  sdf.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString())));
	          result.put("listFirstFactureNonVerse", listEncaissementFirstFactureNonVerse);

	        } else {
	          erreur.add("REV_NOT_FOUND");
	          result.put("erreur", erreur);
	          return result;
	        }

	      } else {
	        result = commissionService.CalculeCommisionFrontFreelancer(true,user.get().getUserid(), date);
	      }



	      Boolean NonExisteCommision = commissionService.findExiteCommision(user.get(), annee, numMois);
	      List<OffreCommissionPromo> existeOffrePromo =
	          offreCommissionPromoRepository.findCommisionPromoInMonth(numMois, annee);
	      Boolean verif15Jours = null;
	      Boolean verifCommissionEnAttente = null;
	      Boolean verifFirstFactureNonVerse = null;
	      Boolean verifFactureNonVerse = null;
	      if (NonExisteCommision) {

	        // La validation doit être effectuée 15 jours après le début de chaque mois."
	        YearMonth moisSuivant = yearMonth.plusMonths(1);
	        LocalDate premierJourDuMoisSuivant = moisSuivant.atDay(1);
	        LocalDate currentDate = LocalDate.now();
	        LocalDate datePlus10Days = premierJourDuMoisSuivant.plus(10, ChronoUnit.DAYS);
	        verif15Jours = (currentDate.isAfter(datePlus10Days) || currentDate.isEqual(datePlus10Days));

	        // Il ne doit y avoir aucune demande en attente pour valider la commission.
	        HashMap<String, Object> commissionDemandes =
	            (HashMap<String, Object>) result.get("commissionDemandes");
	        verifCommissionEnAttente =
	            Integer.parseInt(commissionDemandes.get("demandeEnAttente").toString()) > 0;

	        // Il ne doit y avoir aucune First Facture non versé pour valider la commission.
	        HashMap<String, Object> commissionActivation =
	            (HashMap<String, Object>) result.get("commissionActivation");
	        /*
	         * verifFirstFactureNonVerse =
	         * Integer.parseInt(commissionActivation.get("nbFirsFactureNonVerse").toString()) > 0;
	         */
	        // Il ne doit y avoir aucune facture non versé pour valider la commission.
	        HashMap<String, Object> commissionPaiements =
	            (HashMap<String, Object>) result.get("commissionPaiements");
	        verifFactureNonVerse =
	            Integer.parseInt(commissionPaiements.get("nombreFactureNonVerse").toString()) > 0;

	      }

	      result.put("monthName", numMois);
	      result.put("year", annee);
	      result.put("validation", NonExisteCommision);
	      result.put("verif15Jours", verif15Jours);
	      result.put("verifCommissionEnAttente", verifCommissionEnAttente);
	      // result.put("verifFirstFactureNonVerse", verifFirstFactureNonVerse);
	      result.put("verifFactureNonVerse", verifFactureNonVerse);
	      result.put("isAdmin", isAdmin);
	      if (existeOffrePromo.size() > 0) {
	        Boolean isSavedPromo = commissionService.findExiteCommisionPromo(user.get(),
	            existeOffrePromo.get(0).getDateDebut(), existeOffrePromo.get(0).getDateFin());
	        if (isSavedPromo) {
	          result.put("isexsitePromo", true);
	        } else {
	          result.put("isexsitePromo", false);
	        }

	      } else
	        result.put("isexsitePromo", false);

	    }
	    return result;

	  }

  @RequestMapping(method = RequestMethod.GET, value = "/generateFactureCtureCommisionMultiple")
  @ResponseBody
  public void generateFactureCtureCommisionMultiple(HttpServletResponse response,
	      @RequestParam(name ="dateFact" , required = false) String dateFact, @RequestParam(name ="dateFireFox", required = false) String dateFireFox,
	     @RequestParam(name ="codeRevendeurFact", required = false) String codeRevendeurFact ,  @RequestParam(name ="startCreatedDateFact", required = false) String startCreatedDateFact,
	     @RequestParam(name ="endCreatedDateFact", required = false) String endCreatedDateFact ,   @RequestParam(name ="referenceFact", required = false) String referenceFact  ) throws JRException, FileNotFoundException {
	  File file1 = null;
	//    try {
	      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	      String currentUser = authentication.getName();
	      User user = userRepository.findUsersByEmail(currentUser);
	      Integer dateFacts = null;
	      Integer numMois  = null;
	      String codeRevendeurFacts = null;
	      Date startCreatedDateFacts = null;
	      Date endCreatedDateFacts = null ;
	      String referenceFacts = null ;
	      if (dateFact != null && !dateFact.isEmpty()) {
	    	  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
	          YearMonth yearMonth = YearMonth.parse(dateFact.trim(), formatter);

	          dateFacts = yearMonth.getYear();
	          numMois = yearMonth.getMonth().getValue();
          }

          if (dateFireFox != null && !dateFireFox.isEmpty()) {
        	  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
	          YearMonth yearMonth = YearMonth.parse(dateFireFox.trim(), formatter);

	          dateFacts = yearMonth.getYear();
	          numMois = yearMonth.getMonth().getValue();
          }

          if (codeRevendeurFact != null && !codeRevendeurFact.isEmpty()) {
            codeRevendeurFacts = codeRevendeurFact;
          }
        

          if (startCreatedDateFact != null && !startCreatedDateFact.isEmpty()) {
        	  startCreatedDateFacts = CrmUtils.convertStringToDate(startCreatedDateFact);
          }

          if (endCreatedDateFact != null && !endCreatedDateFact.isEmpty()) {
           endCreatedDateFacts = CrmUtils.convertStringToLocalDateTime(endCreatedDateFact);
          }

          if (referenceFact != null && !referenceFact.isEmpty()) {
             referenceFacts  =referenceFact;
          }
          List<DemandeCommission> resultat = commissionService.finAllByFilterForFactureMultiple(dateFacts ,numMois ,codeRevendeurFacts ,
	    		  startCreatedDateFacts,endCreatedDateFacts,referenceFacts  );
          
          
          
	      Map<String, Object> parametes = new HashMap<>();
	      Collection<FactureCommisionDataSet> factureCommisionDataSetArrayList = new ArrayList<>();
	      FactureCommisionDataSet factureDataSet = new FactureCommisionDataSet();
	      if (resultat != null  ) {
	    	  List<Commission> commissions = resultat.stream()
	    			    .map(demande -> {
	    			      
	    			        // Map other fields as needed
	    			        return demande.getCommission();
	    			    })
	    			    .collect(Collectors.toList());
	    	    Commission firstCommission = commissions.get(0);

	        Collection<Commission> commisionToGenerateFacture =
	            Collections.synchronizedList(commissions);
	        User revendeur = firstCommission.getRevendeur();
	        factureDataSet.setCommission(commisionToGenerateFacture);
	        factureDataSet.setRevendeur(Collections.singletonList(revendeur));

	        factureCommisionDataSetArrayList.add(factureDataSet);
	        double totalHt = commissions.stream()
	        	    .mapToDouble(c -> c.getTotalHt() != null ? c.getTotalHt() : 0.0)
	        	    .sum();
	        double totalTva = commissions.stream()
	        	    .mapToDouble(c -> c.getMontantTva() != null ? c.getMontantTva() : 0.0)
	        	    .sum();
	        
	        	double montantAvancePremiereFacture = commissions.stream()
	        	    .mapToDouble(c -> c.getMontantAvancePremiereFacture() != null ? c.getMontantAvancePremiereFacture() : 0.0)
	        	    .sum();

	        	double totalTtc = commissions.stream()
	        	    .mapToDouble(c -> c.getTotalTtc() != null ? c.getTotalTtc() : 0.0)
	        	    .sum();
	        	double totalRetenu = commissions.stream()
		        	    .mapToDouble(c -> c.getRetunuSource() != null ? c.getRetunuSource() : 0.0)
		        	    .sum();
	        parametes.put("isset_tva", true);
	       parametes.put("matfiscale", "sz");
	        parametes.put("numfact", "");
	        parametes.put("cbancaire", "sz");
	        parametes.put("createdDate",null);
	        parametes.put("totalHt",totalHt);
	        parametes.put("totalTTC",totalTtc);
	        parametes.put("totalTva",totalTva);
	        parametes.put("totalRetenu",totalRetenu);
	        parametes.put("montantAvancePremiereFacture",montantAvancePremiereFacture);
	        JRBeanCollectionDataSource dataSource =
	            new JRBeanCollectionDataSource(factureCommisionDataSetArrayList);
	        File file = ResourceUtils.getFile("classpath:reports/factureCommisionMultiplez.jrxml");
	      
	        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
	        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametes, dataSource);
	        File pathFolder = new File(pathFacture);
	        if (!pathFolder.exists()) {
	          pathFolder.mkdirs();
	          pathFolder.setWritable(true);

	        }

	        String nomfile = pathFacture + "/" + PrefixDocument.NOMEFILE_FACTURE
	            + "" + ".pdf";

	        JasperExportManager.exportReportToPdfFile(jasperPrint, nomfile);
	        file1 = ResourceUtils.getFile(nomfile);
	        response.setContentType(
	            "application/x-pdf ; charset=" + Charset.forName("utf-8").displayName());
	        response.setHeader("Content-disposition", "inline; filename=" + file1.getName());
	        // get your file as InputStream
	        InputStream targetStream = new FileInputStream(file1);
	        
	        // copy it to response's OutputStream
	        try {
				org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        // close input stream file
	        try {
				targetStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	  /*  } catch (JRException e) {
	      // TODO Auto-generated catch block
	      LOGGER.warn("error creation commision facture: " + e);

	    } catch (FileNotFoundException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
*/
	    //  return "";
  }
  

}
