package crm.chifco.com.controller;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import crm.chifco.com.model.Engagement;
import crm.chifco.com.model.Motifrec;
import crm.chifco.com.model.Servicetype;
import crm.chifco.com.model.Statusrec;
import crm.chifco.com.model.TypeVisite;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.StatusrecRepository;
import crm.chifco.com.repository.TypeVisiteRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.EngagementService;
import crm.chifco.com.service.MotifrecService;
import crm.chifco.com.service.ServicetypeService;
import crm.chifco.com.service.StatusrecService;
import crm.chifco.com.service.TypeVisiteService;

@Controller
@RequestMapping("/parameters")
public class ParameterController {
  @Autowired
  private ServicetypeService servicetypeService;
  @Autowired
  private TypeVisiteRepository typeVisiteRepository;

  @Autowired
  private MotifrecService motifrecService;

  @Autowired
  private StatusrecService statusrecService;
  @Autowired
  private StatusrecRepository statusrecRepository;
  @Autowired
  private TypeVisiteService typeVisiteService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EngagementService engagementService;

  @RequestMapping(method = RequestMethod.GET, value = "getallstatus")
  @ResponseBody
  public HashMap<String, Object> AllStatus(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length) {

    int currentpage = start / length;
    Page<Statusrec> responseData = statusrecService.findPaginated(currentpage, length);

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements()); // ✅ total count
    myGreetings.put("recordsFiltered", responseData.getTotalElements());;
    return myGreetings;

  }

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

      Page<Statusrec> pages = statusrecService.findPaginated(pageNo, pageSize);

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

    return "statut/allstatusreclamation";
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
    return "statut/addstatutrec";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createstatut")
  public String createStatut(Statusrec statut, Model model) {
    Statusrec statut1 = statusrecService.getStatusrecByName(statut.getNomStatut());
    if (statut1 == null) {
      statut1 = new Statusrec();
      statut1.setNomStatut(statut.getNomStatut());
      statut1.setNomStatutar(statut.getNomStatutar());
      statut1.setNomStatuten(statut.getNomStatuten());
      statut1.setDesignation(statut.getDesignation());
      statut1.setCouleur(statut.getCouleur());
      statusrecRepository.save(statut1);
      return "redirect:/parameters/allstatus/1/20";
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
      return "statut/allstatusreclamation";
    }
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @RequestMapping(method = RequestMethod.GET, value = "editstatutrec/{statutid}")
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

    Statusrec statut = statusrecRepository.getById(statutid);
    model.addAttribute("statut", statut);
    return "statut/editstatutrec";
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @RequestMapping(method = RequestMethod.POST, value = "editstatutrec/{statutid}")
  public String updatestatut(@PathVariable("statutid") Long statutid, Statusrec statut, Model mv) {
    Statusrec statuttoedit = statusrecRepository.findByNomStatut(statut.getNomStatut());
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
      Statusrec statuttoreedit = statusrecRepository.getById(statutid);
      mv.addAttribute("statut", statuttoreedit);
      mv.addAttribute("existedCode", statuttoreedit.getNomStatut());
      return "statut/editstatutrec";
    } else {
      Statusrec statuttoreedit = statusrecRepository.getById(statutid);
      statuttoreedit.setNomStatut(statut.getNomStatut());
      statuttoreedit.setNomStatutar(statut.getNomStatutar());
      statuttoreedit.setNomStatuten(statut.getNomStatuten());
      statuttoreedit.setDesignation(statut.getDesignation());
      statuttoreedit.setCouleur(statut.getCouleur());
      statusrecRepository.save(statuttoreedit);
      return "redirect:/parameters/allstatus/1/20";
    }
  }

  // status type visite
  @RequestMapping(method = RequestMethod.GET, value = "getalltypeVisite")
  @ResponseBody
  public HashMap<String, Object> getalltypeVisite(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length) {

    int currentpage = start / length;
    Page<TypeVisite> responseData = typeVisiteService.findPaginated(currentpage, length);

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getContent().size());
    myGreetings.put("recordsFiltered", responseData.getContent().size());
    return myGreetings;

  }

  @GetMapping(value = "Alltypevisite/{pageNo}/{pageSize}")
  public String Alltypevisite(@PathVariable(value = "pageNo") Integer pageNo,
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

      Page<TypeVisite> pages = typeVisiteService.findPaginated(pageNo, pageSize);

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

    return "typevisite/alltypevisites";
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @RequestMapping(method = RequestMethod.GET, value = "createtypevisite")
  public String createtypevisite(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    return "typevisite/addtypevisite";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createtypevisite")
  public String createtypevisite(TypeVisite statut, Model model) {
    TypeVisite statut1 = typeVisiteService.getStatusrecByName(statut.getNomType());
    if (statut1 == null) {
      statut1 = new TypeVisite();
      statut1.setNomType(statut.getNomType());
      statut1.setDesignation(statut.getDesignation());
      typeVisiteRepository.save(statut1);
      return "redirect:/parameters/Alltypevisite/1/20";
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

      model.addAttribute("existedCode", statut.getNomType());
      return "typevisite/addtypevisite";
    }
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @RequestMapping(method = RequestMethod.GET, value = "edittypevisite/{typeid}")
  public String edittypevisite(@PathVariable("typeid") Long edittypevisite, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    TypeVisite typevisite = typeVisiteRepository.getById(edittypevisite);
    model.addAttribute("typevisite", typevisite);
    return "typevisite/edittypevisite";
  }

  @PreAuthorize("hasAuthority('UPDATE_STATUS')")
  @RequestMapping(method = RequestMethod.POST, value = "editTypeVisite/{typeid}")
  public String editTypeVisite(@PathVariable("typeid") Long typeid, TypeVisite statut, Model mv) {
    TypeVisite statuttoedit = typeVisiteRepository.findByNomType(statut.getNomType());
    if (statuttoedit != null && !statuttoedit.getTypevisiteid().equals(typeid)) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        mv.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        mv.addAttribute("userphoto", user.getPhoto());
        mv.addAttribute("userrole", user.getRole().getRoleName());
        mv.addAttribute("useremail", user.getEmail());
      }
      TypeVisite statuttoreedit = typeVisiteRepository.getById(typeid);
      mv.addAttribute("statut", statuttoreedit);
      mv.addAttribute("existedCode", statuttoreedit.getNomType());
      return "typevisite/edittypevisite";
    } else {
      TypeVisite statuttoreedit = typeVisiteRepository.getById(typeid);
      statuttoreedit.setDesignation(statut.getDesignation());
      typeVisiteRepository.save(statuttoreedit);
      return "redirect:/parameters/Alltypevisite/1/20";
    }
  }

  // controller to add servicetype
  @RequestMapping(method = RequestMethod.GET, value = "getallservicetypes")
  @ResponseBody
  public HashMap<String, Object> AllServiceTypes(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length) {
    int currentPage = start / length;
    Page<Servicetype> responseData = servicetypeService.findPaginated(currentPage, length);

    HashMap<String, Object> result = new HashMap<>();
    result.put("data", responseData.getContent());
    result.put("draw", draw);
    result.put("start", start);
    result.put("recordsTotal", responseData.getContent().size());
    result.put("recordsFiltered", responseData.getContent().size());
    return result;
  }

  @GetMapping(value = "allservicetypes/{pageNo}/{pageSize}")
  public String servicetypes(@PathVariable(value = "pageNo") Integer pageNo,
      @PathVariable(value = "pageSize") Integer pageSize, Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE_STATUS");
      model.addAttribute("hasediterole", hasediterole);
      // Setting user details
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());


      // Pagination
      Page<Servicetype> pages = servicetypeService.findPaginated(pageNo, pageSize);
      model.addAttribute("servicetypes", pages.getContent());
      model.addAttribute("totalPages", pages.getTotalPages());
    }
    return "servicetype/allservicetypes";
  }

  @PreAuthorize("hasAuthority('UPDATE_SERVICETYPE')")
  @RequestMapping(method = RequestMethod.GET, value = "createservicetype")
  public String createServiceType(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("servicetype", new Servicetype()); // Important: Initialize the object

    }
    return "servicetype/addservicetype";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createservicetype")
  public String createServiceType(Servicetype servicetype, Model model) {
    Servicetype existing =
        servicetypeService.getServicetypeByCategory(servicetype.getCategorytype());
    if (existing == null) {
      servicetypeService.save(servicetype);
      return "redirect:/parameters/allservicetypes/1/20";
    } else {
      model.addAttribute("existedName", servicetype.getCategorytype());
      return "servicetype/addservicetype";
    }
  }

  @PreAuthorize("hasAuthority('UPDATE_SERVICETYPE')")
  @RequestMapping(method = RequestMethod.GET, value = "editservicetype/{id}")
  public String editServiceType(@PathVariable("id") Long id, Model model) {
    Servicetype servicetype = servicetypeService.findbyServicetypeId(id);
    if (servicetype == null) {
      return "redirect:/reclamations/allservicetypes/1/20"; // Redirect if not found
    }
    model.addAttribute("servicetype", servicetype);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
    }
    return "servicetype/editservicetype"; // View for editing
  }

  @RequestMapping(method = RequestMethod.POST, value = "editservicetype/{id}")
  public String updateServiceType(@PathVariable("id") Long id,
      @ModelAttribute Servicetype updatedServicetype, Model model) {
    Servicetype existing = servicetypeService.findbyServicetypeId(id);
    if (existing == null) {
      return "redirect:/parameters/allservicetypes/1/20"; // Redirect if not found
    }
    existing.setCategorytypear(updatedServicetype.getCategorytypear());
    existing.setCategorytypeen(updatedServicetype.getCategorytypeen());
    existing.setCategorytype(updatedServicetype.getCategorytype());
    existing.setDesignation(updatedServicetype.getDesignation());
    existing.setIsPrivate(updatedServicetype.getIsPrivate());
    // Update fields as necessary
    servicetypeService.save(existing);
    return "redirect:/parameters/allservicetypes/1/20";
  }

  // add motif as parameters
  // Motifrec Controllers
  @RequestMapping(method = RequestMethod.GET, value = "getallmotifs")
  @ResponseBody
  public HashMap<String, Object> AllMotifs(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length) {
    int currentPage = start / length;
    Page<Motifrec> responseData = motifrecService.findPaginated(currentPage, length);

    HashMap<String, Object> result = new HashMap<>();
    result.put("data", responseData.getContent());
    result.put("draw", draw);
    result.put("recordsTotal", responseData.getTotalElements()); // ✅ total count
    result.put("recordsFiltered", responseData.getTotalElements()); // ✅ filtered count
    return result;
  }


  @GetMapping(value = "allmotifs/{pageNo}/{pageSize}")
  public String motifs(@PathVariable(value = "pageNo") Integer pageNo,
      @PathVariable(value = "pageSize") Integer pageSize, Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE_STATUS");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());

      Page<Motifrec> pages = motifrecService.findPaginated(pageNo, pageSize);
      model.addAttribute("motifs", pages.getContent());
      model.addAttribute("totalPages", pages.getTotalPages());
    }
    return "motif/allmotifs";
  }

  @PreAuthorize("hasAuthority('UPDATE_MOTIF')")
  @RequestMapping(method = RequestMethod.GET, value = "createmotif")
  public String createMotif(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      List<Servicetype> serviceTypes = servicetypeService.getAllServicetypes();
      model.addAttribute("serviceTypes", serviceTypes);
    }
    return "motif/addmotif";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createmotif")
  public String createMotif(Motifrec motifrec, Model model, @RequestParam Long servicetypeId) {
    Motifrec existing = motifrecService.getMotifByNameByserviceByCategory(motifrec.getNomMotif(),
        servicetypeId, motifrec.getCategory());
    if (existing == null) {
      Servicetype service = servicetypeService.findbyServicetypeId(servicetypeId);
      motifrec.setCategory(motifrec.getCategory());
      motifrec.setServicetype(service);
      motifrecService.save(motifrec);
      return "redirect:/parameters/allmotifs/1/20";
    } else {
      model.addAttribute("existedName", motifrec.getNomMotif());
      return "motif/addmotif";
    }
  }

  @PreAuthorize("hasAuthority('UPDATE_MOTIF')")
  @RequestMapping(method = RequestMethod.GET, value = "editmotif/{id}")
  public String editMotif(@PathVariable("id") Long id, Model model) {
    Motifrec motifrec = motifrecService.findById(id);
    if (motifrec == null) {
      return "redirect:/parameters/allmotifs/1/20"; // Redirect if not found
    }
    model.addAttribute("motif", motifrec);

    List<Servicetype> servicetypes = servicetypeService.getAllServicetypes();


    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
    }
    model.addAttribute("servicetype", servicetypes);
    return "motif/editmotif"; // View for editing
  }

  @RequestMapping(method = RequestMethod.POST, value = "editmotif/{id}")
  public String updateMotif(@PathVariable("id") Long id, @ModelAttribute Motifrec updatedMotifrec,
      @RequestParam Long servicetypeId, Model model) {
    Motifrec existing = motifrecService.findById(id);
    if (existing == null) {
      return "redirect:/parameters/allmotifs/1/20";
    }

    Servicetype servicetype = servicetypeService.findbyServicetypeId(servicetypeId);
    if (servicetype != null) {
      existing.setServicetype(servicetype);
    }
    existing.setNomMotif(updatedMotifrec.getNomMotif());
    existing.setNomMotifar(updatedMotifrec.getNomMotifar());
    existing.setNomMotifen(updatedMotifrec.getNomMotifen());
    existing.setDesignation(updatedMotifrec.getDesignation());
    existing.setCategory(updatedMotifrec.getCategory());
    motifrecService.save(existing);
    return "redirect:/parameters/allmotifs/1/20";
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallengagements")
  @ResponseBody
  public HashMap<String, Object> getallengagements(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length) {
    int currentPage = start / length;
    Page<Engagement> pages = engagementService.findPaginated(currentPage, length);

    HashMap<String, Object> result = new HashMap<>();
    result.put("data", pages.getContent());
    result.put("draw", draw);
    result.put("start", start);
    result.put("recordsTotal", pages.getContent().size());
    result.put("recordsFiltered", pages.getContent().size());
    return result;
  }

  @GetMapping(value = "allengagements/{pageNo}/{pageSize}")
  public String allengagements(@PathVariable(value = "pageNo") Integer pageNo,
      @PathVariable(value = "pageSize") Integer pageSize, Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasediterole = StringsRole.contains("UPDATE_STATUS");
      model.addAttribute("hasediterole", hasediterole);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      Page<Engagement> pages = engagementService.findPaginated(pageNo, pageSize);
      model.addAttribute("engagements", pages.getContent());
      model.addAttribute("totalPages", pages.getTotalPages());
    }
    return "engagement/allengagements";
  }

  @RequestMapping(method = RequestMethod.GET, value = "createEngagement")
  public String createEngagement(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
    }
    return "engagement/addengagement";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createEngagement")
  public String createEngagementservice(Engagement engagement, Model model) {
    Engagement existing = engagementService.searchByNom(engagement.getNomEngagement());
    if (existing == null) {
      engagementService.createEngagement(engagement);
      return "redirect:/parameters/allengagements/1/20";
    } else {
      model.addAttribute("existedName", engagement.getNomEngagement());
      return "engagement/addengagement";
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "editengagement/{id}")
  public String editengagement(@PathVariable("id") Long id, Model model) {
    Engagement engagement = engagementService.getEngagementById(id).get();
    if (engagement == null) {
      return "redirect:/engagement/allengagements/1/20"; // Redirect if not found
    }
    model.addAttribute("engagement", engagement);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
    }
    return "engagement/editengagement";
  }

  @RequestMapping(method = RequestMethod.POST, value = "editengagement/{id}")
  public String updateEngagement(@PathVariable("id") Long id, @ModelAttribute Engagement engagement,
      Model model) {

    Engagement oldEngagement = engagementService.getEngagementById(id).get();
    if (oldEngagement == null) {
      return "redirect:/parameters/allengagements/1/20";
    }
    Engagement existingByName = engagementService.searchByNom(engagement.getNomEngagement());
    if (existingByName != null
        && !existingByName.getEngagementId().equals(oldEngagement.getEngagementId())) {
      model.addAttribute("existedName", engagement.getNomEngagement());
      model.addAttribute("engagement", oldEngagement);
      return "engagement/editengagement";
    }
    oldEngagement.setNomEngagement(engagement.getNomEngagement());
    oldEngagement.setNombre(engagement.getNombre());
    engagementService.createEngagement(oldEngagement);

    return "redirect:/parameters/allengagements/1/20";
  }


}
