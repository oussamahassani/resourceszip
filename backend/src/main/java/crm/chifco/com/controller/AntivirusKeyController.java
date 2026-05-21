package crm.chifco.com.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.AntivirusKey;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AntivirusKeyRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AntivirusKeyService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.CrmUtils;

@Controller
@RequestMapping(value = "antiVirus/*")
public class AntivirusKeyController {

  @Autowired
  private AntivirusKeyService antivirusKeyService;

  @Autowired
  private AntivirusKeyRepository antivirusKeyRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @PreAuthorize("hasAnyAuthority('ANTIVIRUS_KEY_MANAGEMENT')")
  @RequestMapping(method = RequestMethod.GET, value = "antivirus_keys_page")
  public String showAntivirusKeysPage(Model model, RedirectAttributes redirectAttrs) {

    userService.returnInfoUserConnected(model);
    List<String> types = antivirusKeyService.getAllAntivirusTypes();
    model.addAttribute("types", types);
    model.addAttribute("listeAbonnement", antivirusKeyRepository.findAllAbonnementInfo());

    return "antiVirus/antivirus_key";
  }

  @PreAuthorize("hasAnyAuthority('ANTIVIRUS_KEY_MANAGEMENT')")
  @RequestMapping(method = RequestMethod.GET, value = "getAllAntivirusKey")
  @ResponseBody
  public HashMap<String, Object> getallAntivirusKey(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return antivirusKeyService.getAll(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche);
  }

  @PreAuthorize("hasAnyAuthority('ANTIVIRUS_KEY_MANAGEMENT')")
  @PostMapping(value = "/importKeys")
  public String importKeys(@RequestParam("file") MultipartFile file, Model model,
      RedirectAttributes redirAttrs) throws Exception {
    User user = new User();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }

    Map<String, List<AntivirusKey>> result = new HashMap<>();

    try {
      result = antivirusKeyService.importerKey(file, user);

      if (result.containsKey("EXCEL_TABLE_ERROR")) {
        redirAttrs.addFlashAttribute("message", "EXCEL_TABLE_ERROR");
        return "redirect:antivirus_keys_page";
      }

      model.addAttribute("messagesucces",
          "Vous avez Bien importer le fichier :  " + file.getOriginalFilename());
    } catch (Exception e) {

      return "redirect:antivirus_keys_page";
    }

    model.addAttribute("keysValid", result.get("antiVirusKeysValide"));
    model.addAttribute("keysNonValid", result.get("antiVirusKeysNonValide"));
    model.addAttribute("keysNotValidInExcel", result.get("antiVirusKeysNotValidInExcel"));

    if (result.get("antiVirusKeysValide") != null && result.get("antiVirusKeysNonValide") != null) {
      model.addAttribute("nombreKeysValide", result.get("antiVirusKeysValide").size());
      model.addAttribute("nombreKeysNonValide", result.get("antiVirusKeysNonValide").size());
      model.addAttribute("nombrekeysNotValidInExcel",
          result.get("antiVirusKeysNotValidInExcel").size());

    }

    return "antiVirus/resultatImportationExcel";

  }

  @PreAuthorize("hasAnyAuthority('ANTIVIRUS_KEY_MANAGEMENT')")
  @RequestMapping(method = RequestMethod.POST, value = "changeEtat")
  @ResponseBody
  public Boolean changeEtat(@RequestParam Long id) {
    return antivirusKeyService.changeEtat(id);
  }

  @RequestMapping(method = RequestMethod.POST, value = "affecter")
  public String affecter(@RequestParam Long keyId, @RequestParam String referenceClient,
      Model model, RedirectAttributes redirAttrs) {

    User user = new User();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
    }
    String type = antivirusKeyRepository.findById(keyId).get().getType();
    HashMap<String, String> result = new HashMap<>();
    result = antivirusKeyService.affecter(keyId, referenceClient, user, type);

    // Liste des clés pour les messages
    List<String> messagesKeys = Arrays.asList("KEY_ALREADY_USED", "CLIENT_NOT_FOUND",
        "KEY_NOT_FOUND", "KEY_DISABLE", "KEY_ASSIGN_SUCCESS", "CLIENT_ALREADY_HAS_KEY");

    // Parcours des clés pour ajouter les attributs "message" correspondants
    for (String key : messagesKeys) {
      if (result.containsKey(key)) {
        redirAttrs.addFlashAttribute("message", key);
        break;
      }
    }


    return "redirect:antivirus_keys_page";
  }

  @GetMapping("/extractenmasse")
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(value = "key", required = false) String key,
      @RequestParam(value = "referenceClient", required = false) String referenceClient,
      @RequestParam(value = "etat", required = false, defaultValue = "") Boolean etat,
      @RequestParam(value = "statut", required = false, defaultValue = "") Boolean statut,
      @RequestParam(value = "startAffectedDate", required = false) String startAffectedDate,
      @RequestParam(value = "endAffectedDate", required = false) String endAffectedDate,
      @RequestParam(value = "startCreatedDate", required = false) String startCreatedDate,
      @RequestParam(value = "endCreatedDate", required = false) String endCreatedDate,
      @RequestParam(value = "type", required = false) String type) {

    if (key.equals("")) {
      key = null;
    }
    if (referenceClient.equals("")) {
      referenceClient = null;
    }
    if (type.equals("") || type == null) {
      type = null;
    }
    Date startAffectedDateConverted =
        (startAffectedDate.isEmpty()) ? null : CrmUtils.convertStringToDate(startAffectedDate);
    Date endAffectedDateConverted =
        (endAffectedDate.isEmpty()) ? null : CrmUtils.convertStringToLocalDateTime(endAffectedDate);
    Date startCreatedDateConverted =
        (startCreatedDate.isEmpty()) ? null : CrmUtils.convertStringToDate(startCreatedDate);
    Date endCreatedDateConverted =
        (endCreatedDate.isEmpty()) ? null : CrmUtils.convertStringToLocalDateTime(endCreatedDate);


    return antivirusKeyService.exportListClé(request, response, key, referenceClient, etat, statut,
        startAffectedDateConverted, endAffectedDateConverted, startCreatedDateConverted,
        endCreatedDateConverted, type);

  }
}
