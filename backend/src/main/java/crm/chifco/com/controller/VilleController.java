package crm.chifco.com.controller;

import java.util.List;
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
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Ville;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.repository.VilleRepository;
import crm.chifco.com.service.GouverneratsService;
import crm.chifco.com.service.VillesService;

@Controller
@RequestMapping(value = "ville/*")
public class VilleController {
  @Autowired
  GouvernoratRepository gouvernoratsRepository;
  @Autowired
  GouverneratsService gouverneratsService;
  @Autowired
  VilleRepository villeRepository;
  @Autowired
  VillesService villeService;
  @Autowired
  UserRepository userRepository;

  private final Logger logger = LogManager.getLogger(this.getClass());

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @GetMapping(value = "allgouvernorats/{pageNo}")
  public String allGouvernorats(@PathVariable(value = "pageNo") Integer pageNo, Model model,
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

    }
    if (pageNo == null) {
      pageNo = 1;
    }
    int pageSize = 20;
    Page<Gouvernorat> pages = gouverneratsService.findPaginated(pageNo, pageSize);
    model.addAttribute("villes", pages.getContent());
    int[] body;
    if (pages.getTotalPages() > 7) {
      int totalPages = pages.getTotalPages();
      int pageNumber = pages.getNumber() + 1;
      int[] head = (pageNumber > 4) ? new int[] {1, -1} : new int[] {1, 2, 3};
      int[] bodyBefore = (pageNumber > 4 && pageNumber < totalPages - 1)
          ? new int[] {pageNumber - 2, pageNumber - 1}
          : new int[] {};
      int[] bodyCenter =
          (pageNumber > 3 && pageNumber < totalPages - 2) ? new int[] {pageNumber} : new int[] {};
      int[] bodyAfter = (pageNumber > 2 && pageNumber < totalPages - 3)
          ? new int[] {pageNumber + 1, pageNumber + 2}
          : new int[] {};
      int[] tail = (pageNumber < totalPages - 3) ? new int[] {-1, totalPages}
          : new int[] {totalPages - 2, totalPages - 1, totalPages};
      body = Utils.merge(head, bodyBefore, bodyCenter, bodyAfter, tail);

    } else {
      body = new int[pages.getTotalPages()];
      for (int i = 0; i < pages.getTotalPages(); i++) {
        body[i] = 1 + i;
      }
    }
    model.addAttribute("body", body);
    model.addAttribute("page", pages);
    model.addAttribute("thisnumber", pages.getNumber() + 1);
    if ((pages.getNumber() + 2) <= pages.getTotalPages()) {
      model.addAttribute("next", pages.getNumber() + 2);
    } else {
      model.addAttribute("next", pages.getNumber() + 1);
    }
    if (pages.getNumber() <= 0) {
      model.addAttribute("previous", 1);
    } else {
      model.addAttribute("previous", pages.getNumber());
    }
    return "ville/allgouvernorat";
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @RequestMapping(method = RequestMethod.GET, value = "editgouvernorat/{villeid}")
  public String updateGouvernorat(@PathVariable("villeid") Long villeid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    Gouvernorat ville = gouvernoratsRepository.findGouverneratByGouvernoratId(villeid);
    model.addAttribute("ville", ville);
    return "ville/editgouvernorat";
  }

  @RequestMapping(method = RequestMethod.POST, value = "editgouvernorat/{villeid}")
  public String updateVille(@PathVariable("villeid") Long villeid, Gouvernorat ville, Model mv) {
    Gouvernorat villetoedit =
        gouvernoratsRepository.findByGouvernoratName(ville.getGouvernoratName());
    if (villetoedit != null && !villetoedit.getGouvernoratId().equals(villeid)) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        mv.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        mv.addAttribute("userphoto", user.getPhoto());
        mv.addAttribute("userrole", user.getRole().getRoleName());
        mv.addAttribute("useremail", user.getEmail());
      }
      Gouvernorat villetoreedit = gouvernoratsRepository.findGouverneratByGouvernoratId(villeid);
      mv.addAttribute("ville", villetoreedit);
      mv.addAttribute("existedCode", villetoreedit.getGouvernoratName());
      return "ville/editville";
    } else {
      Gouvernorat gouvernorattoredit =
          gouvernoratsRepository.findGouverneratByGouvernoratId(villeid);
      gouvernorattoredit.setGouvernoratName(ville.getGouvernoratName());
      gouvernoratsRepository.save(gouvernorattoredit);
      return "redirect:/ville/allvilles/1";
    }
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @RequestMapping(method = RequestMethod.GET, value = "creategouvernerat")
  public String createVille(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    return "ville/addgouvernerat";
  }

  @RequestMapping(method = RequestMethod.POST, value = "creategouvernerat")
  public String createVille(Gouvernorat villecreate, Model model) {

    Gouvernorat ville =
        gouvernoratsRepository.findByGouvernoratName(villecreate.getGouvernoratName());
    if (ville == null) {
      ville = new Gouvernorat(villecreate.getGouvernoratName());
      gouvernoratsRepository.save(ville);
      return "redirect:/ville/allgouvernorats/1";
    } else {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        model.addAttribute("userphoto", user.getPhoto());
        model.addAttribute("userrole", user.getRole().getRoleName());
        model.addAttribute("useremail", user.getEmail());
      }

      model.addAttribute("existedCode", villecreate.getGouvernoratName());
      return "ville/addgouvernerat";
    }
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @GetMapping(value = "allvilles/{pageNo}")
  public String Gouvernorats(@PathVariable(value = "pageNo") Integer pageNo, Model model,
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
    }
    if (pageNo == null) {
      pageNo = 1;
    }
    int pageSize = 20;
    Page<Ville> pages = villeService.findPaginated(pageNo, pageSize);
    model.addAttribute("gouvernorats", pages.getContent());
    int[] body;
    if (pages.getTotalPages() > 7) {
      int totalPages = pages.getTotalPages();
      int pageNumber = pages.getNumber() + 1;
      int[] head = (pageNumber > 4) ? new int[] {1, -1} : new int[] {1, 2, 3};
      int[] bodyBefore = (pageNumber > 4 && pageNumber < totalPages - 1)
          ? new int[] {pageNumber - 2, pageNumber - 1}
          : new int[] {};
      int[] bodyCenter =
          (pageNumber > 3 && pageNumber < totalPages - 2) ? new int[] {pageNumber} : new int[] {};
      int[] bodyAfter = (pageNumber > 2 && pageNumber < totalPages - 3)
          ? new int[] {pageNumber + 1, pageNumber + 2}
          : new int[] {};
      int[] tail = (pageNumber < totalPages - 3) ? new int[] {-1, totalPages}
          : new int[] {totalPages - 2, totalPages - 1, totalPages};
      body = Utils.merge(head, bodyBefore, bodyCenter, bodyAfter, tail);

    } else {
      body = new int[pages.getTotalPages()];
      for (int i = 0; i < pages.getTotalPages(); i++) {
        body[i] = 1 + i;
      }
    }
    model.addAttribute("body", body);
    model.addAttribute("page", pages);
    model.addAttribute("thisnumber", pages.getNumber() + 1);
    if ((pages.getNumber() + 2) <= pages.getTotalPages()) {
      model.addAttribute("next", pages.getNumber() + 2);
    } else {
      model.addAttribute("next", pages.getNumber() + 1);
    }
    if (pages.getNumber() <= 0) {
      model.addAttribute("previous", 1);
    } else {
      model.addAttribute("previous", pages.getNumber());
    }
    return "ville/allvillees";
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @RequestMapping(method = RequestMethod.GET, value = "createvilles")
  public String createGouvernorat(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    List<Gouvernorat> gouvernorats = gouvernoratsRepository.findAll();
    model.addAttribute("gouvernorats", gouvernorats);
    return "ville/createvilles";
  }


  @RequestMapping(method = RequestMethod.POST, value = "createvilles")
  public String createGouvernorat(Ville villes, @RequestParam("villes") Gouvernorat gouvernerats) {


    Ville villesCreated = new Ville();
    villesCreated.setVilleName(villes.getVilleName());
    villesCreated.setGouvernerat(gouvernerats);
    villeRepository.save(villesCreated);
    logger.info("gouvernorat: " + villes.getVilleName());
    return "redirect:/ville/allvilles/1";

  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @RequestMapping(method = RequestMethod.GET, value = "editvilles/{gouvernoratid}")
  public String updateVilles(@PathVariable("gouvernoratid") Long gouvernoratid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    Ville ville = villeRepository.findVilleByVilleId(gouvernoratid);
    model.addAttribute("ville", ville);
    List<Gouvernorat> gouvernerat = gouvernoratsRepository.findAll();
    model.addAttribute("gouvernerat", gouvernerat);
    return "ville/editvilles";
  }

  @RequestMapping(method = RequestMethod.POST, value = "editville/{gouvernoratid}")
  public String updateGouvernorat(@PathVariable("gouvernoratid") Long gouvernoratid, Ville villes,
      @RequestParam("gouvernerat") Gouvernorat gouvernerat) {

    Ville Villetoedit = villeRepository.findVilleByVilleId(gouvernoratid);
    Villetoedit.setVilleName(villes.getVilleName());
    Villetoedit.setGouvernerat(gouvernerat);
    villeRepository.save(Villetoedit);
    return "redirect:/ville/allvilles/1";

  }

}
