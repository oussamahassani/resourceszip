package crm.chifco.com.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;
import crm.chifco.com.ApiDTO.EncaissementDto;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.DetailsCommissionDemande;
import crm.chifco.com.model.OffreCommissionPromo;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.CommissionRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.DemandeCommissionRepository;
import crm.chifco.com.repository.DetailsCommissionDemandeRepository;
import crm.chifco.com.repository.DetailsCommissionFactureRepository;
import crm.chifco.com.repository.DetailsCommissionPremiereFactureRepository;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.CommissionPromoService;
import crm.chifco.com.service.CommissionService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.UserTypeConstant;

@Controller
@RequestMapping(value = "commissionpromotionnel/*")
public class CommissionPromotionnelController {

  @Autowired
  UserRepository userRepository;

  @Autowired
  CommissionService commissionService;

  @Autowired
  DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  FactureRepository factureRepository;

  @Autowired
  CommissionPromoService commissionPromoService;

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
  UserService userService;

  @Autowired
  EncaissementRepository encaissementRepository;

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
      List<OffreCommissionPromo> offreActive = commissionPromoService.getALLactiveCommision(true);
      model.addAttribute("offreActive", offreActive);
      model.addAttribute("client", users);

      model.addAttribute("userfullname",
          user.get().getLastName() + " " + user.get().getFirstName());
      model.addAttribute("userphoto", user.get().getPhoto());
      model.addAttribute("userrole", user.get().getRole().getRoleName());
      model.addAttribute("useremail", user.get().getEmail());
    }


    return "commissionpromotionnel/suivi";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_FINNANCIER_AREA')"
      + "|| hasAuthority('COMMISSION_FINNANCIER_ALL')")
  @RequestMapping(method = RequestMethod.POST, value = "getCommission",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public HashMap<String, Object> getCommission(
      @RequestParam(value = "idPromo", required = false) Long idPromo,
      @RequestParam(value = "revendeur", required = false) String revendeur) {

    HashMap<String, Object> result = new HashMap<>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      Optional<User> userConnected = Optional.of(userRepository.findUsersByEmail(currentUser));
      List<String> StringsRole = userConnected.get().getRole()
          .getStringsRole(userConnected.get().getRole().getPrivileges());

      List<String> erreur = new ArrayList<>();

      // validation date obligatoire
      if (idPromo == null) {
        erreur.add("DATE_REQUIRED");
      }
      OffreCommissionPromo offrePromo =
          commissionPromoService.getOffreCommissionPromoByIdAndIsActive(idPromo);
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


      Boolean isAdmin = false;
      Optional<User> user = null;
      if (StringsRole.contains("COMMISSION_FINNANCIER_ALL")
          || StringsRole.contains("COMMISSION_FINNANCIER_AREA")) {

        if (StringsRole.contains("COMMISSION_FINNANCIER_ALL")) {

          user = userRepository.findByCodeUser(revendeur);
        } else {
          user = userRepository.findByCodeUserAndAffectedTo(revendeur,
              userConnected.get().getUserid());
        }
        if (user.isPresent()) {
          result = commissionPromoService.CalculeCommisionFront(user.get().getUserid(), idPromo);
          isAdmin = true;

          //
          userConnected = user;
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
          if (offrePromo != null) {
            List<EncaissementDto> listEncaissementFirstFactureNonVerse =
                encaissementRepository.findAllFirstFactureNonVerse(user.get().getUserid(),
                    offrePromo.getDateDebut().toString(), offrePromo.getDateFin().toString());
            result.put("listFirstFactureNonVerse", listEncaissementFirstFactureNonVerse);
          }
        } else {
          erreur.add("REV_NOT_FOUND");
          result.put("erreur", erreur);
          return result;
        }

      } else {
        result =
            commissionPromoService.CalculeCommisionFront(userConnected.get().getUserid(), idPromo);
      }



      Boolean NonExisteCommision = commissionRepository.findExiteCommisionPromo(userConnected.get(),
          offrePromo.getDateDebut(), offrePromo.getDateFin());

      Boolean verif15Jours = null;
      Boolean verifCommissionEnAttente = null;

      if (NonExisteCommision) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(offrePromo.getDateFin());
        calendar.add(Calendar.MONTH, 1);
        // La validation doit être effectuée 15 jours après le début de chaque mois."

        LocalDate currentDate = LocalDate.now();
        LocalDate datePlus1Month =
            calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        verif15Jours = (currentDate.isAfter(datePlus1Month) || currentDate.isEqual(datePlus1Month));

        // Il ne doit y avoir aucune demande en attente pour valider la commission.
        HashMap<String, Object> commissionDemandes =
            (HashMap<String, Object>) result.get("commissionDemandes");
        verifCommissionEnAttente =
            Integer.parseInt(commissionDemandes.get("demandeEnAttente").toString()) > 0;



      }
      if (offrePromo != null) {
        result.put("dateDebut", offrePromo.getDateDebut());
        result.put("dateFin", offrePromo.getDateFin());
      }
      result.put("validation", NonExisteCommision);
      result.put("verif15Jours", verif15Jours);
      result.put("verifCommissionEnAttente", verifCommissionEnAttente);

      result.put("isAdmin", isAdmin);

    }
    return result;

  }



  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN','COMMISSION_AREA')")
  @RequestMapping(method = RequestMethod.GET, value = "allOffreCommissions")
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



      return commissionPromoService.getAll(draw, start, length, search, ordercolumnaram, orderdir,
          filterrecherche);
    }
    return null;
  }



  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER')")
  @RequestMapping(method = RequestMethod.POST, value = "saveCommision",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Boolean saveCommision(@RequestBody HashMap<String, Object> obj) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);


      Commission newCommision = new Commission();

      String dateTimeString = "T00:00:00";
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

      // String comm = myobj.getString("monthName");
      HashMap<String, Object> commissionDemandes =
          (HashMap<String, Object>) obj.get("commissionDemandes");
      LocalDateTime dateTimeFinPromo =
          LocalDateTime.parse(obj.get("dateFin").toString() + dateTimeString, formatter);
      LocalDateTime dateTimeDebutPromo =
          LocalDateTime.parse(obj.get("dateDebut").toString() + dateTimeString, formatter);
      // Extract month and year
      Month month = dateTimeFinPromo.getMonth();
      int year = dateTimeFinPromo.getYear();
      Boolean NonExisteCommision = commissionService.findExiteCommisionPromo(user,
          Date.from(dateTimeDebutPromo.atZone(ZoneId.systemDefault()).toInstant()),
          Date.from(dateTimeFinPromo.atZone(ZoneId.systemDefault()).toInstant()));
      if (!NonExisteCommision) {
        return false;
      }


      newCommision.setAnnee(year);
      // newCommision.setCommentaire(null);
      newCommision.setMois(month.getValue());
      newCommision.setMontantCommissionDemandes(
          Double.parseDouble(commissionDemandes.get("total").toString()));
      newCommision.setMontantCommissionPaiements(Double.parseDouble("0"));
      newCommision.setMontantCommissionPremiereFacture(Double.parseDouble("0"));
      newCommision.setNbrDemandesAcceptees(
          Integer.parseInt(commissionDemandes.get("demandesAccepte").toString()));



      newCommision.setNbrDemandesActivees(Integer.parseInt("0"));
      newCommision.setNbrDemandesRejetees(
          Integer.parseInt(commissionDemandes.get("demandesRejete").toString()));
      newCommision.setNbrDemandesEnAttente(
          Integer.parseInt(commissionDemandes.get("demandeEnAttente").toString()));
      newCommision.setNbrDemandesNonRealisee(
          Integer.parseInt(commissionDemandes.get("nbFirsFactureNonPayed").toString()));
      newCommision.setNbrFacturesPayees(Integer.parseInt("0"));
      newCommision.setNbrFacturesNonVerseePayement(Integer.parseInt("0"));
      newCommision.setNbrTotalDemandes(
          Integer.parseInt(commissionDemandes.get("totalDemandes").toString()));
      newCommision.setRevendeur(user);
      newCommision.setStatut("NOT_PAID");

      newCommision.setTotalHt(newCommision.getMontantCommissionDemandes()
          + newCommision.getMontantCommissionPremiereFacture()
          + newCommision.getMontantCommissionPaiements());
      newCommision.setTva(19);
      newCommision.setMontantTva(newCommision.getTotalHt() * (newCommision.getTva() / 100.0));
      newCommision.setTotalTtc(newCommision.getTotalHt() + newCommision.getMontantTva());
      newCommision.setIsPromo(true);
      newCommision.setPeriodPromoDebut(
          Date.from(dateTimeDebutPromo.atZone(ZoneId.systemDefault()).toInstant()));
      newCommision.setPeriodPromoFin(
          Date.from(dateTimeFinPromo.atZone(ZoneId.systemDefault()).toInstant()));
      Commission commission = commissionRepository.save(newCommision);

      // details commission
      ObjectMapper objectMapper = new ObjectMapper();

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
        List<String> stringListOfReferenceDemande = streamDetailsCommissionDemande.stream()
            .map(DetailsCommissionDemande::getReferenceClient) // Replace with the actual method to
                                                               // get the string
            .collect(Collectors.toList());
        detailsCommissionDemandeRepository.saveAll(streamDetailsCommissionDemande);

        demandeAbonnementRepository.updateIsCommisionSaved(stringListOfReferenceDemande, true);
      }
      /*
       * List<Object> detailsCommissionActivationList = (List<Object>)
       * commissionActivation.get("detailsCommissionFirstFactures"); if
       * (commission.getMontantCommissionPremiereFacture() != 0 &&
       * detailsCommissionActivationList.size() > 0) { List<DetailsCommissionPremiereFacture>
       * streamDetailsCommissionActivation = detailsCommissionActivationList.stream().map(item -> {
       * DetailsCommissionPremiereFacture dto = objectMapper.convertValue(item,
       * DetailsCommissionPremiereFacture.class); dto.setCommissionId(commission.getId()); return
       * dto; }).collect(Collectors.toList()); List<String> stringListOfReferenceFirstFacture =
       * streamDetailsCommissionActivation.stream()
       * .map(DetailsCommissionPremiereFacture::getReferenceFacture) .collect(Collectors.toList());
       * detailsCommissionPremiereFactureRepository.saveAll(streamDetailsCommissionActivation);
       * factureRepository.updateIsCommisionSaved(stringListOfReferenceFirstFacture); }
       * 
       * List<Object> detailsCommissionFactureList = (List<Object>)
       * commissionPaiements.get("detailsCommissionFacture"); if
       * (commission.getMontantCommissionPaiements() != 0 && detailsCommissionFactureList.size() >
       * 0) { List<DetailsCommissionFacture> streamDetailsCommissionFacture =
       * detailsCommissionFactureList.stream().map(item -> { DetailsCommissionFacture dto =
       * objectMapper.convertValue(item, DetailsCommissionFacture.class);
       * dto.setCommissionId(commission.getId()); return dto; }).collect(Collectors.toList());
       * List<String> stringListOfReferenceFacture = streamDetailsCommissionFacture.stream()
       * .map(DetailsCommissionFacture::getReferenceFacture).collect(Collectors.toList());
       * detailsCommissionFactureRepository.saveAll(streamDetailsCommissionFacture);
       * factureRepository.updateIsCommisionSaved(stringListOfReferenceFacture); }
       */
      return true;
    }
    return false;
  }



  @PreAuthorize("hasAnyAuthority('COMMISSION_OFFER_MANAGEMENT')")
  @PostMapping("/offre/ajouter")
  public String ajouterOffreCommission(@RequestParam(value = "namepromo") String namepromo,
      @RequestParam(value = "datedebut") String datedebut,
      @RequestParam(value = "datefin") String datefin,
      @RequestParam(value = "montantdemande1") Double montantdemande1,
      @RequestParam(value = "montantactivation1", required = false) Double montantactivation1,
      @RequestParam(value = "montantpayement1", required = false) Double montantpayement1,
      @RequestParam(value = "montantdemande2") Double montantdemande2,
      @RequestParam(value = "montantactivation2", required = false) Double montantactivation2,
      @RequestParam(value = "montantpayement2", required = false) Double montantpayement2,
      @RequestParam(value = "montantdemande3") Double montantdemande3,
      @RequestParam(value = "montantactivation3", required = false) Double montantactivation3,
      @RequestParam(value = "montantpayement3", required = false) Double montantpayement3,
      @RequestParam(value = "montantdemande4") Double montantdemande4,
      @RequestParam(value = "montantactivation4", required = false) Double montantactivation4,
      @RequestParam(value = "montantpayement4", required = false) Double montantpayement4,
      @RequestParam(value = "montantdemande5") Double montantdemande5,
      @RequestParam(value = "montantactivation5", required = false) Double montantactivation5,
      @RequestParam(value = "montantpayement5", required = false) Double montantpayement5) {



    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);

    String resultat = commissionPromoService.ajouterOffreCommission(namepromo, datedebut, datefin,
        montantdemande1, montantactivation1, montantpayement1, montantdemande2, montantactivation2,
        montantpayement2, montantdemande3, montantactivation3, montantpayement3, montantdemande4,
        montantactivation4, montantpayement4, montantdemande5, montantactivation5, montantpayement5,
        user);

    return "redirect:/commissionpromotionnel/offrecommissions";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OFFER_MANAGEMENT')")
  @PostMapping("/changer-etat-offre")
  public String changerEtatOffre(@RequestParam("idOffre") Long idOffre, Model model,
      RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);

    String resultat = commissionPromoService.changementEtatOffre(idOffre, user);

    redirectAttrs.addFlashAttribute("resultat", resultat);

    return "redirect:offrecommissions";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OFFER_MANAGEMENT')")
  @RequestMapping(method = RequestMethod.GET, value = "offrecommissions")
  public String listOffreCommissions(Model model) {
    userService.returnInfoUserConnected(model);
    return "commissionpromotionnel/offreCommissions";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OFFER_MANAGEMENT')")
  @RequestMapping(method = RequestMethod.GET, value = "detailsCommissionPromo/{idPromo}")
  public String detailCommissionsPromo(Model model, @PathVariable("idPromo") Long idPromo) {
    userService.returnInfoUserConnected(model);
    OffreCommissionPromo detailOffre = commissionPromoService.getOffreCommissionPromoById(idPromo);
    model.addAttribute("detailOffre", detailOffre);
    return "commissionpromotionnel/detailsCommission";
  }
}
