package crm.chifco.com.controller;

import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.Typepaiement;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.TypePaiementService;

@Controller
@RequestMapping(value = "typepaiement/*")
public class TypepaiementController {
  private final Logger logger = LogManager.getLogger(this.getClass());
  @Autowired
  UserRepository userRepository;
  @Autowired
  TypePaiementService typePaiementService;

  @GetMapping(value = "alltypepaiements/{pageNo}")
  @PreAuthorize("hasAuthority('READ_TYPEPAIEMENT')")
  public String typepaiements(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");
      // liste des clients
      if (pageNo == null) {
        pageNo = 1;
      }
      int pageSize = 20;
      Page<Typepaiement> pages = typePaiementService.getalltypepaiementpaginated(pageNo, pageSize);
      model.addAttribute("currentPage", pageNo);
      model.addAttribute("totalPages", pages.getTotalPages());
      model.addAttribute("totalElements", pages.getTotalElements());
      model.addAttribute("typepaiement", pages.getContent());


    }
    return "typepaiement/alltypepaiements";
  }

  @GetMapping("addtypepaiement")
  @PreAuthorize("hasAuthority('ADD_PAYMENT_TYPE')")
  public String addtypepaiement(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    return "typepaiement/addtypepaiement";
  }

  @RequestMapping(method = RequestMethod.POST, value = "addtypepaiement")
  @PreAuthorize("hasAuthority('ADD_PAYMENT_TYPE')")
  public String addclient(@RequestParam("referencetypepaiement") String referencetypepaiement,
      @RequestParam("nomTypePaiement") String nomTypePaiement,
      @RequestParam("nombreMoisTypePaiement") Integer nombreMoisTypePaiement, Model model) {
    Typepaiement testtypeexiste = typePaiementService.gettypepaiementbyref(referencetypepaiement);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      if (testtypeexiste != null) {
        model.addAttribute("existedCode", "existedCode");
        return "typepaiement/addtypepaiement";
      } else {
        Typepaiement typepaiement =
            new Typepaiement(referencetypepaiement, nomTypePaiement, nombreMoisTypePaiement);
        typePaiementService.savetypepaiement(typepaiement);
      }
    }
    return "redirect:/typepaiement/alltypepaiements/" + 1;
  }

  @PreAuthorize("hasAuthority('WRITE_TYPEPAIEMENT')")
  @RequestMapping(method = RequestMethod.GET, value = "edittypepaiement/{typepaiementid}")
  public String updateClient(@PathVariable("typepaiementid") Long typepaiementid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    Optional<Typepaiement> typeexiste = typePaiementService.gettypepaiement(typepaiementid);
    model.addAttribute("typepaiement", typeexiste.get());
    return "typepaiement/edittypepaiement";
  }

  @PreAuthorize("hasAuthority('WRITE_TYPEPAIEMENT')")
  @RequestMapping(method = RequestMethod.POST, value = "edittypepaiement/{typepaiementid}")
  public String updateClient(@PathVariable("typepaiementid") Long typepaiementid,
      @RequestParam("referencetypepaiement") String referencetypepaiement,
      @RequestParam("nomTypePaiement") String nomTypePaiement,
      @RequestParam("nombreMoisTypePaiement") Integer nombreMoisTypePaiement, Abonnement abonnement,
      Model model) {
    Typepaiement testtypeexiste = typePaiementService.gettypepaiementbyref(referencetypepaiement);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      if (testtypeexiste != null
          && (!Objects.equals(typepaiementid, testtypeexiste.getTypePaiementId()))) {
        model.addAttribute("typepaiement", testtypeexiste);
        model.addAttribute("existedCode", "existedCode");
        return "typepaiement/edittypepaiement";
      } else {
        Optional<Typepaiement> typeexiste = typePaiementService.gettypepaiement(typepaiementid);
        typeexiste.get().setReferenceTypePaiement(referencetypepaiement);
        typeexiste.get().setNomTypePaiement(nomTypePaiement);
        typeexiste.get().setNombreMoisTypePaiement(nombreMoisTypePaiement);
        typePaiementService.savetypepaiement(typeexiste.get());
      }
    }
    return "redirect:/typepaiement/alltypepaiements/" + 1;
  }
}
