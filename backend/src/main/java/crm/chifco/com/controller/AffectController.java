package crm.chifco.com.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AffectService;

@Controller
public class AffectController {

  private static final Logger logger = LogManager.getLogger(AffectController.class);

  @Autowired
  private AffectService affectservice;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  CategorieProduitInternetRepository categorieProduitInternetRepository;

  @PreAuthorize("hasAnyAuthority('LIST_MODEM_AFFECTED_ADMIN','LIST_MODEM_AFFECTED_OTHER')")
  @RequestMapping(value = "/verificationAffectModem")
  public String sendInformation2(@RequestParam("modemIdsField") String modemIds,
      @RequestParam("selectuser") String selectedCodeUser, Model model,
      RedirectAttributes redirectAttrs, HttpServletRequest request) {

    User user = null;
    List<String> roles = new ArrayList<>();
    String url = null;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      roles = user.getRole().getStringsRole(user.getRole().getPrivileges());
    }

    url = roles.contains("READ_MODEM") ? "redirect:/modemsadmin" : "redirect:/modems";

    if (selectedCodeUser.equals("")) {
      redirectAttrs.addFlashAttribute("USER_REQUIRED",
          "Désolé, ce champ nécessite un code utilisateur pour être rempli. Veuillez fournir un code utilisateur valide pour continuer.");
      return url;
    }
    if (modemIds.equals("")) {
      redirectAttrs.addFlashAttribute("MODEM_IDS_REQUIRED",
          "Veuillez sélectionner la liste des modems à affecter avant de procéder à l'affectation.");
      return url;
    }

    List<Long> idList =
        Arrays.stream(modemIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
    String verif = affectservice.verificationAffectModem(idList, selectedCodeUser, model, request,
        roles, user.getUserid());


    if (verif.equals("USER_NOT_FOUND")) {
      redirectAttrs.addFlashAttribute("USER_NOT_FOUND", "L'utilisateur n'a pas été trouvé.");
      return url;
    }

    return "modem/affected_modem_result";

  }

  @PreAuthorize("hasAnyAuthority('LIST_MODEM_AFFECTED_ADMIN','LIST_MODEM_AFFECTED_OTHER')")
  @RequestMapping(value = "/confirmAffectModem")
  public String confirAffectedModem(Model model, RedirectAttributes redirectAttrs,
      HttpServletRequest request) {
    User user = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }
    affectservice.confirmAffectModem(model, redirectAttrs, request, user.getUserid());

    return "redirect:/fiches";
  }

  @PreAuthorize("hasAnyAuthority('UNASSIGN_MODEM')")
  @PostMapping(value = "/desaffecterModem")
  public String desaffecterModem(
      @RequestParam(value = "revendeurId", required = false) Long revendeurId,
      @RequestParam(value = "distId", required = false) Long distId,
      @RequestParam(value = "posId", required = false) Long posId,
      @RequestParam(value = "modemId", required = false) Long modemId, Model model) {

    User user = null;
    List<String> roles = new ArrayList<String>();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      roles = user.getRole().getStringsRole(user.getRole().getPrivileges());
    }

    affectservice.desaffecterMdeom(revendeurId, distId, posId, modemId, user);

    if (roles.contains("READ_MODEM")) {
      return "redirect:/modemsadmin";
    } else {
      return "redirect:/modems";
    }

  }

}
