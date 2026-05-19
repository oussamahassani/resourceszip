package crm.chifco.com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.model.TypeVisite;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Visite;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.TypeVisiteService;
import crm.chifco.com.service.VisiteService;
import crm.chifco.com.utils.UserTypeConstant;

@Controller
@RequestMapping(value = "visite/*")
public class VisiteController {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  UserRepository userRepository;
  @Autowired
  VisiteService visiteService;
  @Autowired
  TypeVisiteService typeVisiteService;



  @RequestMapping(method = RequestMethod.GET, value = "getallvisites")
  @ResponseBody
  public HashMap<String, Object> getallclient(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return visiteService.getAllvisits(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche);
  }

  @GetMapping(value = "allvisites/{pageNo}")
  public String clients(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      List<User> revendeurs = new ArrayList<User>();
      List<User> chefsecteurs = userRepository.findUsersByTypeUser(UserTypeConstant.DISTRIBUTEUR);
      List<TypeVisite> typevisites = typeVisiteService.getAllStatusrec();
      if (StringsRole.contains("READ_VISIT_LIST")) {
        revendeurs = userRepository.findUsersByTypeUser(UserTypeConstant.REVENDEUR);
      } else if (StringsRole.contains("READ_VISIT_LIST_OWNER")) {
        revendeurs = userRepository.findUsersByTypeUserAndAffectedTo(UserTypeConstant.REVENDEUR,
            user.getUserid());
      }
      model.addAttribute("READ_VISIT_LIST", StringsRole.contains("READ_VISIT_LIST"));
      model.addAttribute("chefsecteurs", chefsecteurs);
      model.addAttribute("revendeurs", revendeurs);
      model.addAttribute("typevisites", typevisites);
    }
    return "visite/allvisites";
  }

  @RequestMapping(method = RequestMethod.GET, value = "viewvisite/{id}")
  public String viewvisite(@PathVariable("id") Long id, Model model) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      model.addAttribute("user", user);
      Visite visite = visiteService.findVisiteById(id);
      model.addAttribute("visite", visite);

    }
    return "visite/viewvisite";
  }

  @GetMapping("extractenmasse")
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(value = "ExportRechercheReference", required = false) String Reference,
      @RequestParam(value = "ExportRechercheTypeVisit", required = false) String typeVisite,
      @RequestParam(value = "ExportRechercheStatus", required = false) String status,
      @RequestParam(value = "ExportRechercheCreepar", required = false) Long creepar,
      @RequestParam(value = "ExportRechercheRevendeur", required = false) Long Revendeur,
      @RequestParam(value = "ExportRecherchedatedebut", required = false) String datedebut,
      @RequestParam(value = "ExportRecherchedatefin", required = false) String datefin) {
    return visiteService.exportToExcel(request, response, Reference, typeVisite, status, creepar,
        Revendeur, datedebut, datefin);
  }


}
