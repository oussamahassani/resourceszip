package crm.chifco.com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Zone;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.ZoneService;

@Controller
@RequestMapping("zone")
public class ZoneController {

  @Autowired
  private ZoneService zoneService;

  @Autowired
  private GouvernoratRepository gouvernoratRepository;

  @Autowired
  private UserRepository userRepository;

  private final Logger logger = LogManager.getLogger(this.getClass());

  private void addUserToModel(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth instanceof AnonymousAuthenticationToken)) {
      User user = userRepository.findUsersByEmail(auth.getName());
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @GetMapping("allzones/{pageNo}")
  public String getAllZones(@PathVariable Integer pageNo,
      @RequestParam(value = "search", required = false) String search, Model model,
      HttpServletRequest request) {

    addUserToModel(model);
    request.getSession().setAttribute("listedes_ids", "");

    if (pageNo == null || pageNo <= 0)
      pageNo = 1;

    int pageSize = 20;
    Page<Zone> pages;

    if (search != null && !search.isEmpty()) {
      pages = zoneService.findPaginatedWithSearch(pageNo, pageSize, search);
      model.addAttribute("searchTerm", search);
    } else {
      pages = zoneService.findPaginated(pageNo, pageSize);
    }

    model.addAttribute("zones", pages.getContent());
    model.addAttribute("page", pages);
    model.addAttribute("thisnumber", pages.getNumber() + 1);
    model.addAttribute("body", calculatePagination(pages));
    model.addAttribute("next", calculateNextPage(pages));
    model.addAttribute("previous", calculatePreviousPage(pages));
    request.getSession().removeAttribute("selectedGouvernorats");
    request.getSession().removeAttribute("selectedUtilisateurs");

    return "zone/allzones";
  }

  @GetMapping("createzone")
  public String createZone(Model model, HttpServletRequest request) {
    addUserToModel(model);

    model.addAttribute("zone", new Zone());
    model.addAttribute("gouvernorats", gouvernoratRepository.findAll());
    List<User> listChefsecteurs = userRepository.findEnabledUsersByRole("ROLE_TECHNICIEN");
    model.addAttribute("utilisateurs", listChefsecteurs);

    return "zone/createzone";
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @GetMapping("editzone/{id}")
  public String editZone(@PathVariable("id") Long id, Model model, HttpServletRequest request) {

    addUserToModel(model);

    Zone zone = zoneService.getZoneById(id);

    if (zone == null) {
      return "redirect:/zone/allzones/1";
    }

    request.getSession().removeAttribute("selectedGouvernorats");
    request.getSession().removeAttribute("selectedUtilisateurs");
    List<Gouvernorat> selectedGouvernorats = zoneService.getGouvernoratsByZoneId(id);
    List<User> selectedUtilisateurs = zoneService.getUtilisateursByZoneId(id);
    List<Long> selectedGouvernoratIds = selectedGouvernorats.stream()
        .map(Gouvernorat::getGouvernoratId).collect(Collectors.toList());
    List<Long> selectedUtilisateurIds =
        selectedUtilisateurs.stream().map(User::getUserid).collect(Collectors.toList());
    model.addAttribute("zone", zone);
    model.addAttribute("gouvernorats", gouvernoratRepository.findAll());
    List<User> listTechniciens = userRepository.findEnabledUsersByRole("ROLE_TECHNICIEN");
    model.addAttribute("utilisateurs", listTechniciens);
    // model.addAttribute("utilisateurs", userRepository.findAll());
    model.addAttribute("selectedGouvernorats", selectedGouvernoratIds);
    model.addAttribute("selectedUtilisateurs", selectedUtilisateurIds);

    return "zone/editzone";
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @GetMapping("/deletezone/{zoneId}")
  public String deleteZone(@PathVariable("zoneId") Long zoneId,
      RedirectAttributes redirectAttributes) {
    Zone zone = zoneService.getZoneById(zoneId);

    if (zone == null) {
      redirectAttributes.addFlashAttribute("error", "Zone introuvable !");
      return "redirect:/zone/allzones/1";
    }

    try {
      zoneService.clearGouvernoratsFromZone(zoneId);
      zoneService.clearUtilisateursFromZone(zoneId);

      zoneService.deleteZone(zoneId);

      redirectAttributes.addFlashAttribute("success", "Zone supprimée avec succès !");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de la zone !");
      e.printStackTrace();
    }

    return "redirect:/zone/allzones/1";
  }

  @PreAuthorize("hasAuthority('UPDATE_CITY')")
  @PostMapping("editzone/{id}")
  public String updateZone(@PathVariable("id") Long id, @ModelAttribute Zone zone,
      @RequestParam(value = "selectedGouvernorats", required = false) List<Long> gouvIds,
      @RequestParam(value = "selectedUtilisateurs", required = false) List<Long> userIds,
      RedirectAttributes redirectAttributes) {

    Zone existingZone = zoneService.getZoneById(id);
    if (existingZone == null) {
      redirectAttributes.addFlashAttribute("error", "Zone introuvable !");
      return "redirect:/zone/allzones/1";
    }

    if (!zoneService.isZoneCodeUnique(zone.getCode(), id)) {
      redirectAttributes.addFlashAttribute("error", "Code déjà utilisé !");
      return "redirect:/zone/editzone/" + id;
    }

    if (!zoneService.isZoneNomUnique(zone.getNom(), id)) {
      redirectAttributes.addFlashAttribute("error", "Nom déjà utilisé !");
      return "redirect:/zone/editzone/" + id;
    }

    existingZone.setCode(zone.getCode());
    existingZone.setNom(zone.getNom());
    existingZone.setDescription(zone.getDescription());
    existingZone.setActive(zone.isActive());

    Zone saved = zoneService.saveZone(existingZone);

    zoneService.clearGouvernoratsFromZone(saved.getZoneId());
    if (gouvIds != null) {
      zoneService.addGouvernoratsToZone(saved.getZoneId(), gouvIds);
    }

    zoneService.clearUtilisateursFromZone(saved.getZoneId());
    if (userIds != null) {
      zoneService.addUtilisateursToZone(saved.getZoneId(), userIds);
    }

    redirectAttributes.addFlashAttribute("success", "Zone mise à jour avec succès !");
    return "redirect:/zone/allzones/1";
  }

  @PostMapping("createzone")
  public String saveZone(@ModelAttribute Zone zone,
      @RequestParam(value = "selectedGouvernorats", required = false) List<Long> gouvIds,
      @RequestParam(value = "selectedUtilisateurs", required = false) List<Long> userIds,
      RedirectAttributes redirectAttributes) {

    if (!zoneService.isZoneCodeUnique(zone.getCode(), null)) {
      redirectAttributes.addFlashAttribute("error", "Code déjà utilisé !");
      return "redirect:/zone/createzone";
    }

    if (!zoneService.isZoneNomUnique(zone.getNom(), null)) {
      redirectAttributes.addFlashAttribute("error", "Nom déjà utilisé !");
      return "redirect:/zone/createzone";
    }

    Zone saved = zoneService.saveZone(zone);

    if (gouvIds != null) {
      zoneService.addGouvernoratsToZone(saved.getZoneId(), gouvIds);
    }

    if (userIds != null) {
      zoneService.addUtilisateursToZone(saved.getZoneId(), userIds);
    }

    redirectAttributes.addFlashAttribute("success", "Zone créée !");
    return "redirect:/zone/allzones/1";
  }

  @GetMapping("/viewzone/{id}")
  public String viewZone(@PathVariable("id") Long id, Model model) {
    Zone zone = zoneService.getZoneById(id);
    if (zone == null) {
      return "redirect:/zone/allzones/1";
    }

    List<Gouvernorat> gouvernorats = zoneService.getGouvernoratsByZoneId(id);
    List<User> utilisateurs = zoneService.getUtilisateursByZoneId(id);
    model.addAttribute("zone", zone);
    model.addAttribute("gouvernorats", gouvernorats);
    model.addAttribute("utilisateurs", utilisateurs);

    return "zone/viewzone";
  }

  @PostMapping("addGouvernoratToSession")
  @ResponseBody
  public Map<String, Object> addGouvernorat(@RequestParam Long gouvernoratId,
      HttpServletRequest request) {

    List<Long> list = getSessionList(request, "selectedGouvernorats");

    if (!list.contains(gouvernoratId)) {
      list.add(gouvernoratId);
    }

    return buildResponse(list.size());
  }

  @PostMapping("removeGouvernoratFromSession")
  @ResponseBody
  public Map<String, Object> removeGouvernorat(@RequestParam Long gouvernoratId,
      HttpServletRequest request) {

    List<Long> list = getSessionList(request, "selectedGouvernorats");
    list.remove(gouvernoratId);

    return buildResponse(list.size());
  }

  @PostMapping("addUtilisateurToSession")
  @ResponseBody
  public Map<String, Object> addUtilisateur(@RequestParam Long userId, HttpServletRequest request) {

    List<Long> list = getSessionList(request, "selectedUtilisateurs");

    if (!list.contains(userId)) {
      list.add(userId);
    }

    return buildResponse(list.size());
  }

  @PostMapping("removeUtilisateurFromSession")
  @ResponseBody
  public Map<String, Object> removeUtilisateur(@RequestParam Long userId,
      HttpServletRequest request) {

    List<Long> list = getSessionList(request, "selectedUtilisateurs");
    list.remove(userId);

    return buildResponse(list.size());
  }

  @GetMapping("getSelectedGouvernorats")
  @ResponseBody
  public List<Long> getSelectedGouvernorats(HttpServletRequest request) {
    return getSessionList(request, "selectedGouvernorats");
  }

  @GetMapping("getSelectedUtilisateurs")
  @ResponseBody
  public List<Long> getSelectedUtilisateurs(HttpServletRequest request) {
    return getSessionList(request, "selectedUtilisateurs");
  }

  @PostMapping("clearGouvernoratsSession")
  @ResponseBody
  public Map<String, Object> clearGouv(HttpServletRequest request) {
    request.getSession().removeAttribute("selectedGouvernorats");
    return simpleSuccess();
  }

  @PostMapping("clearUtilisateursSession")
  @ResponseBody
  public Map<String, Object> clearUsers(HttpServletRequest request) {
    request.getSession().removeAttribute("selectedUtilisateurs");
    return simpleSuccess();
  }


  private List<Long> getSessionList(HttpServletRequest request, String key) {
    List<Long> list = (List<Long>) request.getSession().getAttribute(key);
    if (list == null) {
      list = new ArrayList<>();
      request.getSession().setAttribute(key, list);
    }
    return list;
  }

  private Map<String, Object> buildResponse(int count) {
    Map<String, Object> map = new HashMap<>();
    map.put("success", true);
    map.put("count", count);
    return map;
  }

  private Map<String, Object> simpleSuccess() {
    Map<String, Object> map = new HashMap<>();
    map.put("success", true);
    return map;
  }


  private int[] calculatePagination(Page<?> pages) {
    int total = pages.getTotalPages();
    int[] body = new int[total];
    for (int i = 0; i < total; i++) {
      body[i] = i + 1;
    }
    return body;
  }

  private int calculateNextPage(Page<?> pages) {
    return (pages.getNumber() + 2 <= pages.getTotalPages()) ? pages.getNumber() + 2
        : pages.getNumber() + 1;
  }

  private int calculatePreviousPage(Page<?> pages) {
    return (pages.getNumber() <= 0) ? 1 : pages.getNumber();
  }
}
