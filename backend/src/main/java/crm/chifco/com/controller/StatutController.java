package crm.chifco.com.controller;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.StatutRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.StatutService;

@Controller
@RequestMapping(value = "statut/*")
public class StatutController {
  @Autowired
  UserRepository userRepository;
  @Autowired
  StatutService statutService;
  @Autowired
  StatutRepository statutRepository;

  private final Logger logger = LogManager.getLogger(this.getClass());

  @RequestMapping(method = RequestMethod.GET, value = "getallstatus")
  @ResponseBody
  public HashMap<String, Object> AllStatus(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length) {

    int currentpage = start / length;
    Page<Statut> responseData = statutService.findPaginated(currentpage, length);

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getContent().size());
    myGreetings.put("recordsFiltered", responseData.getContent().size());
    return myGreetings;

  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @GetMapping(value = "allstatus/{pageNo}/{pageSize}")
  public String status(@PathVariable(value = "pageNo") Integer pageNo,
      @PathVariable(value = "pageSize") Integer pageSize, Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE_STATUS");
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");

      if (pageNo == null) {
        pageNo = 1;
      }
      int evalPageSize;
      int[] PAGE_SIZES = {20, 50, 100};
      if (pageSize == null) {
        evalPageSize = 20;
      } else {
        evalPageSize = pageSize;
      }

      Page<Statut> pages = statutService.findPaginated(pageNo, pageSize);

      int[] body;
      assert pages != null;
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
      model.addAttribute("selectedPageSize", evalPageSize);
      model.addAttribute("pageSizes", PAGE_SIZES);
      model.addAttribute("currentPage", pageNo);
      model.addAttribute("totalPages", pages.getTotalPages());
      model.addAttribute("totalElements", pages.getTotalElements());
      model.addAttribute("status", pages.getContent());
      model.addAttribute("body", body);
      model.addAttribute("page", pages);
      model.addAttribute("hasediterole", hasediterole);
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
    }

    return "statut/allstatus";
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @RequestMapping(method = RequestMethod.GET, value = "createstatut")
  public String createStatut(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    return "statut/addstatut";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createstatut")
  public String createStatut(Statut statut, Model model) {
    Statut statut1 = statutRepository.findStatutByNomStatut(statut.getNomStatut());
    if (statut1 == null) {
      statut1 = new Statut();
      statut1.setNomStatut(statut.getNomStatut());
      statut1.setDesignation(statut.getDesignation());
      statut1.setCouleur(statut.getCouleur());
      statutRepository.save(statut1);
      return "redirect:/statut/allstatus/1/20";
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

      model.addAttribute("existedCode", statut.getNomStatut());
      return "statut/addstatut";
    }
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @RequestMapping(method = RequestMethod.GET, value = "editstatut/{statutid}")
  public String updatestatut(@PathVariable("statutid") Long statutid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    Statut statut = statutRepository.findStatutByStatutId(statutid);
    model.addAttribute("statut", statut);
    return "statut/editstatut";
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @RequestMapping(method = RequestMethod.POST, value = "editstatut/{statutid}")
  public String updatestatut(@PathVariable("statutid") Long statutid, Statut statut, Model mv) {
    Statut statuttoedit = statutRepository.findStatutByNomStatut(statut.getNomStatut());
    if (statuttoedit != null && !statuttoedit.getStatutId().equals(statutid)) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        mv.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        mv.addAttribute("userphoto", user.getPhoto());
        mv.addAttribute("userrole", user.getRole().getRoleName());
        mv.addAttribute("useremail", user.getEmail());
      }
      Statut statuttoreedit = statutRepository.findStatutByStatutId(statutid);
      mv.addAttribute("statut", statuttoreedit);
      mv.addAttribute("existedCode", statuttoreedit.getNomStatut());
      return "statut/editstatut";
    } else {
      Statut statuttoreedit = statutRepository.findStatutByStatutId(statutid);
      statuttoreedit.setDesignation(statut.getDesignation());
      statuttoreedit.setCouleur(statut.getCouleur());
      statutRepository.save(statuttoreedit);
      return "redirect:/statut/allstatus/1/20";
    }
  }
}
