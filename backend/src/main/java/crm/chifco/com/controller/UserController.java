 package crm.chifco.com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.Role;
import crm.chifco.com.model.User;
import crm.chifco.com.model.UserHistory;
import crm.chifco.com.model.Ville;
import crm.chifco.com.repository.CodePostaleRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.RoleRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.repository.VilleRepository;
import crm.chifco.com.service.DashboardService;
import crm.chifco.com.service.UserHistoryService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.UserTypeConstant;

@Controller
@RequestMapping(value = "/admin")
public class UserController {
  @Autowired
  UserRepository userRepository;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  GouvernoratRepository gouvernoratRepository;

  @Autowired
  CodePostaleRepository codePostaleRepository;

  @Autowired
  VilleRepository villeRepository;

  @Autowired
  UserHistoryService userHistoryService;

  @PersistenceContext
  private EntityManager entityManager;


  @Autowired
  DashboardService dashboardService;


  @Value("${pathphoto}")
  private String pathphoto;

  private final Logger logger = LogManager.getLogger(this.getClass());

  @GetMapping(value = "dashboard")
  public ModelMap mmDashboard(Model model, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");
      dashboardService.returnDashbordStatsAdmin(model);
      List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
      model.addAttribute("gouvernorats", listGouvernorats);
      List<User> listeDistributeur = userService.findUsersByTypeUser(UserTypeConstant.DISTRIBUTEUR);
      model.addAttribute("listeDistributeur", listeDistributeur);
    }
    return new ModelMap();
  }

  @RequestMapping(value = "users/allusers/{pageNo}",
      method = {RequestMethod.POST, RequestMethod.GET})
  public String users(@PathVariable(value = "pageNo") Integer pageNo,
      @RequestParam(value = "gouvernorat", required = false) Long gouvernorat,
      @RequestParam(value = "villes", required = false) Long villes,
      @RequestParam(value = "Nom", required = false) String Nom,
      @RequestParam(value = "Prenom", required = false) String Prenom,
      @RequestParam(value = "refUser", required = false) String refUser,
      @RequestParam(value = "datedebut", required = false) String datedebut,
      @RequestParam(value = "datefin", required = false) String datefin,
      @RequestParam(value = "role", required = false) Long role,
      
      Model model,
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

    Date dateCreationDebut = null;
    Date dateCreationFin = null;

    if (pageNo == null) {
      pageNo = 1;
    }
    if (datedebut != null && !datedebut.isEmpty()) {
      dateCreationDebut = CrmUtils.convertStringToDate(datedebut);
    }
    if (datefin != null && !datefin.isEmpty()) {
      dateCreationFin = CrmUtils.convertStringToLocalDateTime(datefin);
    }

    if (Nom != null && Nom.isEmpty()) {
      Nom = null;
    }
    if (Prenom != null && Prenom.isEmpty()) {
      Prenom = null;
    }
    if (refUser != null && refUser.isEmpty()) {
      refUser = null;
    }
    if (gouvernorat != null && Long.valueOf(gouvernorat) == null) {
      gouvernorat = null;
    }
    if (villes != null && Long.valueOf(villes) == null) {
      villes = null;
    }
    if (role != null && Long.valueOf(role) == null) {
      role = null;
    }
    int pageSize = 20;
    Page<User> pages = userService.findPaginatedWithFilter(pageNo, pageSize, Nom, Prenom, refUser,
        gouvernorat, villes, dateCreationDebut, dateCreationFin, role);
    model.addAttribute("users", pages.getContent());
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

    model.addAttribute("numberUser", pages.getTotalElements());

    List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("gouvernorats", listGouvernorats);
    model.addAttribute("body", body);
    model.addAttribute("page", pages);
    model.addAttribute("refUser", refUser);
    model.addAttribute("Prenom", Prenom);
    model.addAttribute("Nom", Nom);

    model.addAttribute("selectedvilles", villes);

    model.addAttribute("selectedGouvernorat", gouvernorat);
    model.addAttribute("datedebut", datedebut);
    model.addAttribute("datefin", datefin);
    model.addAttribute("role", role);
    model.addAttribute("listeRole", roleRepository.findAll());
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
    return "admin/users/allusers";
  }

  @PreAuthorize("hasAuthority('ADD_USER')")
  @GetMapping("users/add-user")
  public String adduser(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    List<Role> roles = roleRepository.findAll();
    Role rolerevendeur = roleRepository.findRoleByRoleName("ROLE_REVENDEUR");
    List<Gouvernorat> gouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("gouvernorats", gouvernorats);
    model.addAttribute("roles", roles);
    model.addAttribute("rolerevendeur", rolerevendeur);
    return "admin/users/add-user";
  }

  @RequestMapping(method = RequestMethod.POST, value = "users/add-user")
  public String adduser(@RequestParam("Nom") String Nom, @RequestParam("Prenom") String Prenom,
      @RequestParam("Email") String Email, @RequestParam("roles") Role role,
      @RequestParam("Password") String Password,
      @RequestParam("Confirm_Password") String Confirm_Password,
      @RequestParam("activation") Boolean activation, Model model,
      @RequestParam("imageFile") MultipartFile imageFile, @RequestParam("typerole") String typerole,
      @RequestParam(value = "pcRefusCommision", required = false) Long pcRefusCommision,
      @RequestParam(value = "pcfacturerecurent", required = false) Long pcfacturerecurent,
      @RequestParam(value = "plafonrevendeur", required = false) Long plafonrevendeur,
      @RequestParam(value = "coordonneesBancaires", required = false) String coordonneesBancaires,
      @RequestParam("CIN") String CIN,
      @RequestParam(value = "ActivitePrincipale", required = false) String ActivitePrincipale,
      @RequestParam(value = "withstock", required = false) Boolean withstock,
      @RequestParam(value = "classuser", required = false) String classuser,
      @RequestParam(value = "Regimefiscal", required = false) String Regimefiscal,
      @RequestParam(value = "Formejuridique", required = false) String Formejuridique,
      @RequestParam(value = "identificationfiscale", required = false) String identificationfiscale,
      @RequestParam(value = "telephone", required = false) String telephone,
      @RequestParam(value = "adresse", required = false) String adresse,
      @RequestParam("ville") Long ville, @RequestParam("gouvernorat") Gouvernorat gouvernorat,
      @RequestParam("codepostale") Long codepostale,
      @RequestParam(value = "imageFileRNE", required = false) MultipartFile imageFileRNE,
      @RequestParam(value = "imageFileFiscale", required = false) MultipartFile imageFileFiscale,
      @RequestParam(value = "Contratpdf", required = false) MultipartFile Contratpdf,
      @RequestParam(value = "nomCommercial", required = false) String nomCommercial,
      @RequestParam(value = "interlocuteur", required = false) String interlocuteur,
      @RequestParam(value = "isExonoree", required = false) Boolean isExonoree,
      @RequestParam(value = "codePGHUser", required = false) String codePGHUser

      
        
		  ) {

    User testuser = userRepository.findUsersByEmail(Email);
    Long useridtosave = null;
    List<String> StringsRole = null;
    User usercurrent = new User();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      usercurrent = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname",
          usercurrent.getLastName() + " " + usercurrent.getFirstName());
      model.addAttribute("userphoto", usercurrent.getPhoto());
      model.addAttribute("userrole", usercurrent.getRole().getRoleName());
      model.addAttribute("useremail", usercurrent.getEmail());
      useridtosave = usercurrent.getUserid();
      StringsRole = usercurrent.getRole().getStringsRole(usercurrent.getRole().getPrivileges());
    }
    if (testuser != null) {

      List<Role> roles = roleRepository.findAll();
      List<Gouvernorat> gouvernorats = gouvernoratRepository.findAll();
      List<Ville> villes = villeRepository
          .findGouvernoratsByGouvernerat_GouvernoratId(gouvernorat.getGouvernoratId());
      List<PostalCode> listCodePostale = codePostaleRepository.findPostalCodeByVille_VilleId(ville);

      model.addAttribute("gouvernorats", gouvernorats);
      model.addAttribute("villes", villes);
      model.addAttribute("listCodePostale", listCodePostale);
      model.addAttribute("codePostaleId", codepostale);
      model.addAttribute("roles", roles);
      model.addAttribute("existedCode", Email);

      model.addAttribute("nom", Nom);
      model.addAttribute("prenom", Prenom);
      model.addAttribute("email", Email);
      model.addAttribute("cin", CIN);
      model.addAttribute("role_id", role.getRoleId());
      model.addAttribute("type", typerole);
      model.addAttribute("ville_id", ville);
      model.addAttribute("gouvernorat_id", gouvernorat.getGouvernoratId());
      model.addAttribute("adresse", adresse);
      model.addAttribute("activation", activation);
      model.addAttribute("plafonrevendeur", plafonrevendeur);
      model.addAttribute("coordonneesBancaires", coordonneesBancaires);
      model.addAttribute("ActivitePrincipale", ActivitePrincipale);
      model.addAttribute("Regimefiscal", Regimefiscal);
      model.addAttribute("identificationfiscale", identificationfiscale);
      model.addAttribute("telephone", telephone);
      model.addAttribute("adresse", adresse);
      model.addAttribute("classuser", classuser);
      model.addAttribute("interlocuteur", interlocuteur);
      model.addAttribute("nomCommercial", nomCommercial);
      model.addAttribute("isExonoree", isExonoree);

      model.addAttribute("codePGHUser", codePGHUser);

      
      
      
      return "admin/users/add-user";
    } else {

      if (Password.equals(Confirm_Password)) {
        User user = new User();
        user.setFirstName(Nom);
        user.setLastName(Prenom);
        user.setEmail(Email);
        user.setPassword(passwordEncoder.encode(Password));
        user.setRole(role);
        user.setTypeUser(typerole);
        user.setEnabled(activation);
        user.setIsExonoree(isExonoree);
        user.setCodePGHUser(codePGHUser);
        
        user.setCin(CIN);
        if (gouvernorat != null) {
          user.setGouvernorat(gouvernorat);
        }
        if (codepostale != null) {

          user.setCodePostale(codepostale);
        }

        if (ville != null) {
          Ville villeobj = villeRepository.findVilleByVilleId(ville);
          user.setVille(villeobj);
        }

        if (plafonrevendeur != null) {
          user.setPlafonRevendeur(plafonrevendeur);
        }
        if (pcfacturerecurent != null) {
          user.setPcActivationCommision(pcfacturerecurent);
        }
        if (pcRefusCommision != null) {
          user.setPcRefusCommision(pcRefusCommision);
        }

        if (ActivitePrincipale != null) {
          user.setActivitePrincipale(ActivitePrincipale);
        }
        if (coordonneesBancaires != null) {
          user.setCoordonneesBancaires(coordonneesBancaires);
        }
        if (withstock != null) {
          user.setWithStock(withstock);
        }

        if (telephone != null) {
          user.setTelephone(telephone);
        }

        if (classuser != null) {
          user.setClassUser(classuser);
        }

        if (Regimefiscal != null) {
          user.setRegimeFiscal(Regimefiscal);
        }

        if (Formejuridique != null) {
          user.setFormeJuridique(Formejuridique);
        }

        if (identificationfiscale != null) {
          user.setIdentificationFiscale(identificationfiscale);
        }

        if (adresse != null) {
          user.setAdresse(adresse);
        }
        if (nomCommercial != null) {
          user.setNomCommercial(nomCommercial);
        }
        if (interlocuteur != null) {
          user.setInterlocuteur(interlocuteur);
        }
        if (imageFileRNE != null && !imageFileRNE.isEmpty()) {
          try {
            userService.updateImage(imageFileRNE, user.getEmail(), user.getRNE());
            user.setRNE(CrmUtils.noSpecialCharacters(imageFileRNE.getOriginalFilename()));
          } catch (Exception e) {

            logger.error("FileRNE UserController.adduser Exception: " + e.getMessage());

          }
        }

        if (imageFileFiscale != null && !imageFileFiscale.isEmpty()) {
          try {
            logger.info("name  pdfFileFiscale"
                + CrmUtils.noSpecialCharacters(imageFileFiscale.getOriginalFilename()));
            userService.updateImage(imageFileFiscale, user.getEmail(), user.getCarteFiscale());
            user.setCarteFiscale(
                CrmUtils.noSpecialCharacters(imageFileFiscale.getOriginalFilename()));
          } catch (Exception e) {

            logger.error("imageFileFiscale UserController.adduser Exception: " + e.getMessage());

          }
        }
        logger.info(
            "user to Contratpdf :" + Contratpdf + "user to pdfFileFiscale" + imageFileFiscale);

        if (Contratpdf != null && !Contratpdf.isEmpty()) {
          try {
            logger.info("name  Contratpdf"
                + CrmUtils.noSpecialCharacters(Contratpdf.getOriginalFilename()));
            userService.updateImage(Contratpdf, user.getEmail(), user.getContrat());
            user.setContrat(CrmUtils.noSpecialCharacters(Contratpdf.getOriginalFilename()));
          } catch (Exception e) {

            logger.error("contratPdf UserController.adduser Exception: " + e.getMessage());

          }
        }

        user.setCreatedByUserId(useridtosave);
        user.setAffectedTo(useridtosave);
        if (!imageFile.isEmpty()) {
          try {
            CrmUtils.saveImage(imageFile, Email, pathphoto, "");
            user.setPhoto(CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
          } catch (Exception e) {

            logger.error("UserController.adduser Exception: " + e.getMessage());

          }

        }

        String codeuser =
            userService.generateCodeUser(gouvernorat.getAbreviation(), user.getTypeUser());
        user.setCodeUser(codeuser);

        userRepository.save(user);

        userHistoryService.addHistoryEntry(user.getUserid(), "Création du compte", usercurrent);
      } else {
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        List<Gouvernorat> gouvernerat = gouvernoratRepository.findAll();
        model.addAttribute("villes", gouvernerat);
        model.addAttribute("passwordnotmatch", Confirm_Password);
        return "admin/users/add-user";
      }

    }
    if (StringsRole != null && StringsRole.contains("READ_USER_LIST"))
      return "redirect:/admin/users/allusers/" + 1;
    else
      return "redirect:/RevendeurUser/revendeurliste/1";
  }

  @RequestMapping(method = RequestMethod.GET, value = "users/edituser/{userid}")
  public String updateUser(@PathVariable("userid") Long userid, Model model) {
    userService.returnInfoUserConnected(model);
    User useredit = userRepository.findUsersByUserid(userid);
    List<Role> roles = roleRepository.findAll();
    model.addAttribute("roles", roles);
    model.addAttribute("user", useredit);
    List<Gouvernorat> gouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("gouvernorats", gouvernorats);
    List<Ville> villes = null;
    if (useredit.getGouvernorat() != null) {
      villes = villeRepository.findGouvernoratsByGouvernerat_GouvernoratId(
          useredit.getGouvernorat().getGouvernoratId());
    }
    model.addAttribute("villes", villes);

    List<PostalCode> listCodePostale = null;
    if (useredit.getGouvernorat() != null && useredit.getVille() != null) {
      listCodePostale =
          codePostaleRepository.findPostalCodeByVille_VilleId(useredit.getVille().getVilleId());
    }
    model.addAttribute("codePostaleList", listCodePostale);

    logger.info("user to edit :" + useredit.getCodeUser());
    return "admin/users/edit-user";
  }

  @RequestMapping(method = RequestMethod.POST, value = "users/edituser/{userid}")
  public String updateUser(@PathVariable("userid") Long userid, User user1, Model mv,
      @RequestParam("roles") Role role, @RequestParam("Password") String Password,
      @RequestParam("Confirm_Password") String Confirm_Password,
      @RequestParam("activation") Boolean activation,
      
      //@RequestParam("isExonoree" ) Boolean isExonoree,

      @RequestParam("typeuser") String typeuser,
      @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttrs) {

    User userConnected = new User();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      userConnected = userRepository.findUsersByEmail(currentUser);
      mv.addAttribute("userfullname",
          userConnected.getLastName() + " " + userConnected.getFirstName());
      mv.addAttribute("userphoto", userConnected.getPhoto());
      mv.addAttribute("userrole", userConnected.getRole().getRoleName());
      mv.addAttribute("useremail", userConnected.getEmail());
    }

    User verifUser = userRepository.findUsersByEmail(user1.getEmail());
    if (verifUser != null && !verifUser.getUserid().equals(userid)) {
      List<Role> roles = roleRepository.findAll();
      mv.addAttribute("roles", roles);
      mv.addAttribute("existedCode", user1.getEmail());
      User useredit = userRepository.findUsersByUserid(userid);
      mv.addAttribute("user", useredit);
      return "admin/users/edit-user";
    } else {

      if (Password.equals(Confirm_Password)) {
        User newUser = userRepository.findUsersByUserid(userid);
        User oldUser = new User();
        try {
          BeanUtils.copyProperties(oldUser, newUser);
          entityManager.detach(oldUser);
        } catch (IllegalAccessException e1) {
          e1.printStackTrace();
        } catch (InvocationTargetException e1) {
          e1.printStackTrace();
        }

        if (user1.getGouvernorat() != newUser.getGouvernorat()) {
          String codeUser = newUser.getCodeUser();
          if (!codeUser.endsWith("-B")) {
            newUser.setCodeUser(codeUser + "-B");
          }
        }

        newUser.setFirstName(user1.getFirstName());
        newUser.setLastName(user1.getLastName());
        newUser.setEmail(user1.getEmail());
        newUser.setCin(user1.getCin());
        newUser.setGouvernorat(user1.getGouvernorat());
        newUser.setVille(user1.getVille());
        newUser.setCodePostale(user1.getCodePostale());
        newUser.setAdresse(user1.getAdresse());
        newUser.setCodePGHUser(user1.getCodePGHUser());
        
        
        if (typeuser != null) {
          newUser.setTypeUser(typeuser);
        }

        if (user1.getPcActivationCommision() != null) {
          newUser.setPcActivationCommision(user1.getPcActivationCommision());
        }
        if (user1.getNomCommercial() != null) {
          newUser.setNomCommercial(user1.getNomCommercial());
        }
        if (user1.getInterlocuteur() != null) {
          newUser.setInterlocuteur(user1.getInterlocuteur());
        }

        if (user1.getPcRefusCommision() != null) {
          newUser.setPcRefusCommision(user1.getPcRefusCommision());
        }
        if (user1.getCoordonneesBancaires() != null) {
          newUser.setCoordonneesBancaires(user1.getCoordonneesBancaires());
        }
        if (user1.getRegimeFiscal() != null) {
          newUser.setRegimeFiscal(user1.getRegimeFiscal());
        }
        newUser.setPlafonRevendeur(user1.getPlafonRevendeur());
        newUser.setActivitePrincipale(user1.getActivitePrincipale());
        if (Password.length() > 0) {
          newUser.setPassword(passwordEncoder.encode(Password));
        }
        newUser.setRole(role);
        newUser.setEnabled(activation);
    //    newUser.setIsExonoree(isExonoree);
        if (!imageFile.isEmpty()) {
          try {
            userService.updateImage(imageFile, user1.getEmail(), newUser.getPhoto());
            newUser.setPhoto(CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
          } catch (Exception e) {

            logger.error("UserController.updateUser Exception: " + e.getMessage());

          }
        }
        userRepository.save(newUser);
        userHistoryService.checkAndSaveHistory(oldUser, newUser, userConnected);
        redirectAttrs.addFlashAttribute("message", "userdited");

      } else {
        List<Role> roles = roleRepository.findAll();
        mv.addAttribute("roles", roles);
        mv.addAttribute("passwordnotmatch", Confirm_Password);
        User useredit = userRepository.findUsersByUserid(userid);
        mv.addAttribute("user", useredit);
        return "admin/users/edit-user";
      }
    }
    return "redirect:/admin/users/allusers/" + 1;
  }

  @RequestMapping(value = "/enabledisable/{userid}", method = RequestMethod.POST)
  public String enabledisable(@PathVariable Long userid) {

    User userConnected = new User();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      userConnected = userRepository.findUsersByEmail(currentUser);
    }

    User useredit = userRepository.findUsersByUserid(userid);
    useredit.setEnabled(!useredit.isEnabled());

    userRepository.save(useredit);

    // historique utilistaeur
    String action = useredit.isEnabled() ? "Le statut de l'utilisateur a été activé."
        : "Le statut de l'utilisateur a été désactivé.";
    userHistoryService.addHistoryEntry(userid, action, userConnected);


    return "redirect:/admin/users/allusers/" + 1;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/usersSystem/getusers")
  public String updateUser(Model model) {
    userService.returnInfoUserConnected(model);
    return "admin/users/usersSystem";
  }

  @GetMapping("/getAllUserSystem")
  @ResponseBody
  public HashMap<String, Object> getAllUserSystem(
      @RequestParam(value = "draw", required = false) Integer draw,
      @RequestParam(value = "start", required = false) Integer start,
      @RequestParam(value = "length", required = false) Integer length,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size,
      @RequestParam(value = "nom", required = false) String nom,
      @RequestParam(value = "prenom", required = false) String prenom,
      @RequestParam(value = "codeUser", required = false) String codeUser) {

    int currentPage;
    int pageSize;
    boolean isDatatablesRequest = draw != null && start != null && length != null;

    if (isDatatablesRequest) {
      currentPage = start / length;
      pageSize = length;
    } else {
      currentPage = (page != null && page >= 0) ? page : 0;
      pageSize = (size != null && size > 0) ? size : 20;
    }

    HashMap<String, Object> myGreetings = new HashMap<>();
    Page<User> responseData = userService.findPaginatedUserByType(currentPage + 1, pageSize, "SYSTEM");

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw != null ? draw : 1);
    myGreetings.put("start", isDatatablesRequest ? start : currentPage * pageSize);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    return myGreetings;
  }

  @RequestMapping(method = RequestMethod.GET, value = "users/Exporter")
  public void Exporter(@RequestParam(value = "gouvernorat", required = false) Long gouvernorat,
      @RequestParam(value = "villes", required = false) Long villes,
      @RequestParam(value = "Nom", required = false) String Nom,
      @RequestParam(value = "Prenom", required = false) String Prenom,
      @RequestParam(value = "refUser", required = false) String refUser,
      @RequestParam(value = "datedebut", required = false) String datedebut,
      @RequestParam(value = "datefin", required = false) String datefin,
      @RequestParam(value = "distributeur", required = false) Long distributeur,
      @RequestParam(value = "role", required = false) Long role,

      HttpServletResponse response, HttpServletRequest request) throws IOException, ParseException {
    response.setContentType("text/csv");
    String headerKey = "Content-Disposition";
    String headervalue = "attachment; filename=user_info.csv";

    response.setHeader(headerKey, headervalue);
    List<User> listuser = new ArrayList<User>();
    listuser = userService.findAllUserByRecherche(null, Nom, Prenom, refUser, gouvernorat, villes,
        datedebut, datefin, distributeur, null, role, null);
    if (listuser.size() > 0) {
      PrintWriter writer = response.getWriter();
      String title = "Liste utilisateur";
      int totalWidth = 200;
      int titleWidth = title.length();
      int padding = (totalWidth - titleWidth) / 2;

      String centeredTitle = String.format("%" + padding + "s%s%" + padding + "s", "", title, "");
      writer.println(centeredTitle);
      writer.println(
          "Nom ; Prénom; Date de création ; Type; CIN ; Email; Code utilisateur; Interlocuteur;Nom commercial;Gouvernorat ;Ville ;Assignée à ;Code de l'assignée; Créé par ; Code du créateur ;"

          + "role ; Status");
      String interlocuteur = "";
      String nomComercial = "";
      String nomAssignedTo = "";
      String lastNameAssignedTo = "";
      String codeUserAssignedTo = "";

      String nomUserCreePar = "";
      String lastNameUserCreePar = "";
      String codeUserCreePar = "";
      String GouvernoratName = "";
      String VilleName = "";
      String status = "Activé" ;
      String roleString = "";
      for (User obj : listuser) {
        if (obj.getInterlocuteur() != null)
          interlocuteur = obj.getInterlocuteur();
        User userAssinger = userService.findUsersByIduser(obj.getAffectedTo());
        if (userAssinger != null) {
          nomAssignedTo = userAssinger.getFirstName();
          lastNameAssignedTo = userAssinger.getLastName();
          codeUserAssignedTo = userAssinger.getCodeUser();
        }

        User userCreePar = userService.findUsersByIduser(obj.getCreatedByUserId());
        if (userCreePar != null) {
          nomUserCreePar = userCreePar.getFirstName();
          lastNameUserCreePar = userCreePar.getLastName();
          codeUserCreePar = userCreePar.getCodeUser();
        }
        if (obj.getGouvernorat() != null) {
          GouvernoratName = obj.getGouvernorat().getGouvernoratName();
        }
        if (obj.getVille() != null) {
          VilleName = obj.getVille().getVilleName();
        }

        if (obj.getNomCommercial() != null)
          nomComercial = obj.getNomCommercial();
        
        if (obj.getRole() != null)
        	roleString = obj.getRole().getRoleName();

        if (obj.isEnabled() == false)
        {
        	status = "Desactivé";
        }
        else {
        	status = "Activé";  

        }
  // if(obj.get)
	//   status = 
        // Convert the object fields to an array of strings
        String csvLine = obj.getFirstName() + ";" + obj.getLastName() + ";" + obj.getCreatedDate()
            + ";" + obj.getTypeUser() + ";" + obj.getCin() + ";" + obj.getEmail() + ";"
            + obj.getCodeUser() + ";" + interlocuteur + ";" + nomComercial + ";" + GouvernoratName
            + ";" + VilleName + ";" + nomAssignedTo + " " + lastNameAssignedTo + "("
            + codeUserAssignedTo + ")" + ";" + codeUserAssignedTo + ";" + nomUserCreePar + " "
            + lastNameUserCreePar + ";" + codeUserCreePar  +";"+ roleString +";" +status ;
        writer.println(csvLine);
      }
      writer.flush();
      writer.close();
    } else {
      try {
        request.getRequestDispatcher("/users/allusers/1").forward(request, response);
      } catch (ServletException | IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        logger.error(" revendeur .Exporter xls Error:" + e);
      }
    }
  }

  @PreAuthorize("hasAnyAuthority('VIEW_USER_HISTORY')")
  @GetMapping("user/historique/{userId}")
  public String historique(@PathVariable Long userId, Model model) {
    userService.returnInfoUserConnected(model);
    User detailHistoryUser = userService.findUsersByIduser(userId);
    List<UserHistory> historyUser = userHistoryService.getHistoryByUser(userId);
    model.addAttribute("historyUser", historyUser);
    model.addAttribute("detailHistoryUser", detailHistoryUser);
    return "admin/users/historiqueUser";
  }

}
