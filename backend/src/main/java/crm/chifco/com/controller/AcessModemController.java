package crm.chifco.com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.ModemAccess;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ModemAccessRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AcessModemService;

@Controller
@RequestMapping(value = "accessModem/*")
public class AcessModemController {
  @Autowired
  UserRepository userRepository;
  @Autowired
  AcessModemService acessModemService;
  @Autowired
  ModemAccessRepository modemRepository;

  private final Logger logger = LogManager.getLogger(this.getClass());

  @RequestMapping(method = RequestMethod.GET, value = "getall")
  @ResponseBody
  public HashMap<String, Object> AllStatus(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length) {

    int currentpage = start / length;
    Page<ModemAccess> responseData = acessModemService.findPaginated(currentpage, length);

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    return myGreetings;

  }

  @GetMapping(value = "getallAccess/{pageNo}/{pageSize}")
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
    }
    return "acessModem/allAccess";
  }

  @RequestMapping(method = RequestMethod.GET, value = "createaccess")
  public String createaccess(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    return "acessModem/addAccess";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createNew")
  public String createNew(ModemAccess statut, Model model) {
    ModemAccess statut1 = modemRepository.findOneModemAccessByEmail(statut.getEmail());
    if (statut1 == null) {
      statut1 = new ModemAccess();
      statut1.setEmail(statut.getEmail());
      statut1.setPassword(statut.getPassword());
      modemRepository.save(statut1);
      return "redirect:/accessModem/getallAccess/1/20";
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

      model.addAttribute("existedCode", statut.getEmail());
      return "acessModem/addAccess";
    }
  }


  @RequestMapping(value = "/confirm_import")
  public String importe2File(@RequestParam("file") MultipartFile file, Model model,
      RedirectAttributes redirAttrs)

      throws Exception {
    Map<String, List<ModemAccess>> result = new HashMap<>();
    User user = null;
    List<String> StringsRole = new ArrayList<String>();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    try {
      result = acessModemService.importerModemfromExcel(file, user); // appel la methode qui sert à
      // importer une liste modems
      // (fichier csv)
      // dans la base des données //( classe :DataFromCsv , package : service)

      if (result.containsKey("EXCEL_ROW_LIMIT_ERROR") || result.containsKey("EXCEL_TABLE_ERROR")) {

        if (result.containsKey("EXCEL_ROW_LIMIT_ERROR")) {
          redirAttrs.addFlashAttribute("EXCEL_ROW_LIMIT_ERROR",
              "Le nombre de lignes dans votre fichier Excel dépasse la limite autorisée. Veuillez vous assurer que le fichier ne dépasse pas 700 lignes.");
        }

        if (result.containsKey("EXCEL_TABLE_ERROR")) {
          redirAttrs.addFlashAttribute("EXCEL_TABLE_ERROR",
              "Impossible d'importer ce fichier : Le contenu de ce fichier n'est pas valide.");
        }

        return "redirect:/accessModem/getallAccess/1/20";
      }

      model.addAttribute("messagesucces",
          "Vous avez Bien importer le fichier :  " + file.getOriginalFilename());
    } catch (Exception e) {

      logger.error("ModemController.importe2File Exception: " + e.getMessage());
      redirAttrs.addFlashAttribute("EXCEL_IMPORT_ERROR",
          " Un probléme d'importation le fichier :  " + file.getOriginalFilename());
      return "redirect:/accessModem/getallAccess/1/20";
    }



    if (result.get("modemValide") != null && result.get("modemNonValide") != null) {
      model.addAttribute("nombreModemValide", result.get("modemValide").size());
      model.addAttribute("nombreModemNonValide", result.get("modemNonValide").size());
    }

    return "redirect:/accessModem/getallAccess/1/20";
  }



}
