package crm.chifco.com.controller;

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
import crm.chifco.com.model.Profession;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ProfessionRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.ProfessionService;

@Controller
@RequestMapping(value = "profession/*")
public class ProfessionController {
  @Autowired
  UserRepository userRepository;
  @Autowired
  ProfessionRepository professionRepository;
  @Autowired
  ProfessionService professionService;

  private final Logger logger = LogManager.getLogger(this.getClass());

  @PreAuthorize("hasAuthority('READ_PROFESSION')")
  @GetMapping(value = "allprofessions/{pageNo}")
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
    Page<Profession> pages = professionService.findPaginated(pageNo, pageSize);
    model.addAttribute("professions", pages.getContent());

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

    return "profession/allprofessions";
  }

  @PreAuthorize("hasAuthority('WRITE_PROFESSION')")
  @RequestMapping(method = RequestMethod.GET, value = "createprofession")
  public String createprofession(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    return "profession/addprofession";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createprofession")
  public String createprofession(Profession profession, Model model) {

    Profession professiontocreate = professionRepository.findProfessionByName(profession.getName());
    if (professiontocreate == null) {
      professiontocreate = new Profession(profession.getName());
      professionRepository.save(professiontocreate);
      return "redirect:/profession/allprofessions/1";
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

      model.addAttribute("existedCode", profession.getName());
      return "profession/addprofession";
    }
  }

  @PreAuthorize("hasAuthority('WRITE_PROFESSION')")
  @RequestMapping(method = RequestMethod.GET, value = "editprofession/{professionid}")
  public String updateprofession(@PathVariable("professionid") Long professionid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    Profession profession = professionRepository.findProfessionByProfessionId(professionid);
    model.addAttribute("profession", profession);
    return "profession/editprofession";
  }

  @RequestMapping(method = RequestMethod.POST, value = "editprofession/{professionid}")
  public String updateprofession(@PathVariable("professionid") Long professionid,
      Profession profession, Model mv) {
    Profession professiontoedit = professionRepository.findProfessionByName(profession.getName());
    if (professiontoedit != null && !professiontoedit.getProfessionId().equals(professionid)) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        mv.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        mv.addAttribute("userphoto", user.getPhoto());
        mv.addAttribute("userrole", user.getRole().getRoleName());
        mv.addAttribute("useremail", user.getEmail());
      }
      Profession professiontoreedit =
          professionRepository.findProfessionByProfessionId(professionid);
      mv.addAttribute("profession", professiontoreedit);
      mv.addAttribute("existedCode", "exist");
      return "profession/editprofession";
    } else {
      Profession professiontoreedit =
          professionRepository.findProfessionByProfessionId(professionid);
      professiontoreedit.setName(profession.getName());
      professionRepository.save(professiontoreedit);
      return "redirect:/profession/allprofessions/1";
    }
  }
}
