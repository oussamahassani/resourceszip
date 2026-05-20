package crm.chifco.com.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.DemandeCommission;
import crm.chifco.com.model.DemandeCommissionGroup;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.DemandeCommissionRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.DemandeCommissionService;
import crm.chifco.com.service.UserService;

@Controller
@RequestMapping(value = "demandeCommission/*")
public class DemandeCommissionController {

  @Autowired
  UserRepository userRepository;

  @Autowired
  DemandeCommissionRepository demandeCommissionRepository;

  @Autowired
  DemandeCommissionService demandeCommissionService;

  @Autowired
  UserService userService;

  @Value("${pathCommission}")
  private String pathCommission;

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN')")
  @RequestMapping(method = RequestMethod.GET, value = "all_demande_commission_page")
  public String allCommission(Model model, RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    model.addAttribute("listeUser", userRepository.findUsersByTypeUser("REVENDEUR"));
    return "commission/allDemandeCommission";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN')")
  @RequestMapping(method = RequestMethod.GET, value = "allDemandeCommission")
  @ResponseBody
  public Map<String, Object> getAllCommissions(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") int ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    return demandeCommissionService.getAll(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche);
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @PostMapping("/demande")
  public ResponseEntity<String> validation(@RequestParam("commissionId") Long commissionId,
      @RequestParam(value = "image", required = false) MultipartFile image,
      @RequestParam("commentaire") String commentaire) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = new User();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);

    }

    String response =
        demandeCommissionService.demandeCommissionParRev(commissionId, image, commentaire, user);
    if (response.equals("SUCCESS")) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @PostMapping("/demandeMultiple")
  public ResponseEntity<String> demandeMultiple(
      @RequestParam("commissionIds") List<Long> commissionIds,
      @RequestParam(value = "image", required = false) MultipartFile image,
      @RequestParam("commentaire") String commentaire) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = new User();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }

    String response =
        demandeCommissionService.demandeCommissionMultiple(commissionIds, image, commentaire, user);
    if (response.equals("SUCCESS")) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN')")
  @PostMapping("/validerMultiple")
  public ResponseEntity<String> validerMultiple(
      @RequestParam("commissionIds") List<Long> commissionIds,
      @RequestParam(value = "motif", required = false) String motif) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = new User();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }

    String response = demandeCommissionService.validerCommissionMultiple(commissionIds, motif, user);
    if (response.equals("SUCCESS")) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN','COMMISSION_OWNER' , 'COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.GET, value = "details_demande_commission_page/{id}")
  public String detailsCommission(@PathVariable Long id, Model model,
      RedirectAttributes redirectAttrs) {

    userService.returnInfoUserConnected(model);

    Optional<DemandeCommission> demandeCommission = demandeCommissionRepository.findById(id);

    if (!demandeCommission.isPresent()) {
      model.addAttribute("demandeCommission", null);
      return "commission/detailsDemandeCommission";
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

      if (StringsRole.contains("COMMISSION_OWNER")) {
        if (!demandeCommission.get().getDemandeBy().getUserid().equals(user.getUserid())) {
          return "redirect:/access-denied";
        }
      } /*
         * else if (StringsRole.contains("COMMISSION_AREA")) { if
         * (!demandeCommission.get().getDemandeBy().getAffectedTo().equals(user.getUserid())) {
         * return "redirect:/access-denied"; } }
         */
    }

    model.addAttribute("demandeCommission", demandeCommission.get());

    return "commission/detailsDemandeCommission";

  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN')")
  @PostMapping("/validationCommission")
  @ResponseBody
  public ResponseEntity<String> submitValidation(@RequestParam String decision,
      @RequestParam(value = "image", required = false) MultipartFile image,
      @RequestParam String commentaire, @RequestParam String raison, @RequestParam Long id) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = new User();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);

    }

    String response = demandeCommissionService.validationCommission(decision, image, commentaire,
        raison, id, user);

    if (response.equals("SUCCESS")) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.GET, value = "all_my_demande_commission_page")
  public String allMyCommission(Model model, RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    return "commission/allMyDemandeCommission";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.GET, value = "allMyDemandeCommission")
  @ResponseBody
  public Map<String, Object> getAllMyCommissions(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") int ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = new User();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);

    }
    return demandeCommissionService.getAllByRev(draw, start, length, search, ordercolumnaram,
        orderdir, filterrecherche, user);
  }

  @GetMapping("/extracteExcel")
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response) {

    return demandeCommissionService.exportListDemandeCommissionToExcel(request, response);
  }

  // Grouped demand endpoints
  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @PostMapping("/create-group")
  @ResponseBody
  public ResponseEntity<String> createDemandeGroup(
      @RequestParam("demandeIds[]") List<Long> demandeIds,
      @RequestParam(value = "commentaire", required = false) String commentaire) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = new User();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }

    String response = demandeCommissionService.createDemandeGroup(demandeIds, commentaire, user);
    if (response.equals("SUCCESS")) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN')")
  @PostMapping("/validate-group")
 
  public String validateDemandeGroup(
      @RequestParam Long groupId,
      @RequestParam String decision,
      @RequestParam(value = "motif", required = false) String motif,
      @RequestParam(value = "commentaire", required = false) String commentaire,
      @RequestParam(value = "decisionFile", required = false) MultipartFile decisionFile,
      RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = new User();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }

    String response = demandeCommissionService.validateDemandeGroup(groupId, decision, motif, commentaire, decisionFile, user);
    if (response.equals("SUCCESS")) {
        redirectAttrs.addFlashAttribute("message", "validateDemandeGroupSuccess");
    } else {
        redirectAttrs.addFlashAttribute("message", "validateDemandeFalse");
    }
    return "redirect:/demandeCommission/details_grouped_demande_commission_page/" + groupId ;
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN')")
  @RequestMapping(method = RequestMethod.GET, value = "all_grouped_demande_commission_page")
  public String allGroupedDemandeCommission(Model model, RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    return "commission/allGroupedDemandeCommission";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN','COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.GET, value = "allGroupedDemandeCommission")
  @ResponseBody
  public Map<String, Object> getAllGroupedDemandes(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") int ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = null;
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }

    return demandeCommissionService.getAllGroupedDemandes(draw, start, length, search,
        ordercolumnaram, orderdir, filterrecherche, user);
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_ADMIN','COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.GET, value = "details_grouped_demande_commission_page/{id}")
  public String detailsGroupedDemandeCommission(@PathVariable Long id, Model model,
      RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);

    DemandeCommissionGroup groupedDemande = demandeCommissionService.getGroupedDemandeById(id);
    // Calcul des totaux
    Double totalHt = 0.0;
    Double montantTva = 0.0;
    Double totalTtc = 0.0;
    Integer tva = 0;
    if (groupedDemande != null) {
      model.addAttribute("groupedDemande", null);
      if (groupedDemande.getDemandeCommissions() != null) {
          for (DemandeCommission dc : groupedDemande.getDemandeCommissions()) {
              if (dc.getCommission() != null) {
                  totalHt = totalHt + (dc.getCommission().getTotalHt());
                  montantTva = montantTva + (dc.getCommission().getMontantTva());
                  totalTtc = totalTtc + (dc.getCommission().getTotalTtc());
                  if (tva == null  || tva.equals(0) ) {
                      tva = dc.getCommission().getTva();
                  }
              }
          }
      }
      model.addAttribute("totalHt", totalHt);
      model.addAttribute("montantTva", montantTva);
      model.addAttribute("totalTtc", totalTtc);
      model.addAttribute("tva", tva);
    }

    model.addAttribute("groupedDemande", groupedDemande);

    return "commission/detailsGroupedDemandeCommission";
  }

  // My Grouped Demandes (for COMMISSION_OWNER and COMMISSION_OWNER_FREELANCER)
  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.GET, value = "all_my_grouped_demande_commission_page")
  public String allMyGroupedDemandeCommission(Model model, RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    return "commission/allMyFemndeDeCommisionGrouped";
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.GET, value = "myGroupedDemandeCommission")
  @ResponseBody
  public Map<String, Object> getMyGroupedDemandes(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") int ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = null;
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }

    return demandeCommissionService.getAllGroupedDemandes(draw, start, length, search,
        ordercolumnaram, orderdir, filterrecherche, user);
  }

 // @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @PostMapping("/upload-invoice")
  @ResponseBody
  public ResponseEntity<String> uploadInvoiceForGroup(
      @RequestParam Long groupId,
      @RequestParam("invoiceFile") MultipartFile invoiceFile) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = new User();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }

    String response = demandeCommissionService.uploadInvoiceForGroup(groupId, invoiceFile, user);
    if (response.equals("SUCCESS")) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PreAuthorize("hasAnyAuthority('COMMISSION_OWNER','COMMISSION_OWNER_FREELANCER')")
  @RequestMapping(method = RequestMethod.GET, value = "/download-facture-multiple")
  @ResponseBody
  public void downloadFactureMultiple(
      @RequestParam Long groupId,
      HttpServletResponse response) {
    demandeCommissionService.generateAndDownloadFactureMultiple(groupId, response);
  }
}
