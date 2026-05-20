package crm.chifco.com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.repository.VilleRepository;
import crm.chifco.com.service.RoleService;
import crm.chifco.com.service.UserHistoryService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.ClassificationRevendeur;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.UserTypeConstant;

@Controller
@RequestMapping(value = "RevendeurUser/*")
public class RevendeurUserController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleService roleService;

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  GouvernoratRepository gouvernoratRepository;

  @Autowired
  VilleRepository villeRepository;

  @Autowired
  private CodePostaleRepository codePostaleRepository;

  @Autowired
  private UserHistoryService userHistoryService;

  @PersistenceContext
  private EntityManager entityManager;

  @RequestMapping(method = RequestMethod.POST, value = "/users/mon-profile/{userid}")
  public String modifyprofile(@PathVariable("userid") Long userid,
      @RequestParam("email") String email, @RequestParam("telephone") String telephone,
      @RequestParam("Password") String Password,
      @RequestParam("Confirm_Password") String Confirm_Password,
      @RequestParam("ActivitePrincipale") String ActivitePrincipale,
      @RequestParam("coordonneesBancaires") String coordonneesBancaires,
      @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      if (Password.equals(Confirm_Password) && email.equals(currentUser)) {
        User olduser = userRepository.findByUserId(userid);
        olduser.setActivitePrincipale(ActivitePrincipale);
        if (Password.length() > 0) {
          olduser.setPassword(passwordEncoder.encode(Password));
        }
        olduser.setTelephone(telephone);
        olduser.setCoordonneesBancaires(coordonneesBancaires);

        if (!imageFile.isEmpty()) {

          try {
            userService.updateImage(imageFile, olduser.getEmail(), olduser.getPhoto());
            olduser.setPhoto(CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
          } catch (Exception e) {
            logger.error("UserController.updateUser Exception: " + e.getMessage());
          }

        }

        redirectAttrs.addFlashAttribute("message", "update_success");
        userRepository.save(olduser);

      } else {
        redirectAttrs.addFlashAttribute("password_error", "La confirmation n'est pas identique");
      }

    }

    return "redirect:/RevendeurUser/users/mon-profile";
  }

  @GetMapping(value = "/users/mon-profile")
  public String modify(Model model) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("user", user);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    return "others/modify";
  }

  // @GetMapping(value = "/revendeurliste/{pageNo}")
  @RequestMapping(value = "/revendeurliste/{pageNo}",
      method = {RequestMethod.POST, RequestMethod.GET})
  public String revendeurliste(@PathVariable(value = "pageNo") Integer pageNo,
      @RequestParam(value = "gouvernorat", required = false) Long gouvernorat,
      @RequestParam(value = "villes", required = false) Long villes,
      @RequestParam(value = "Nom", required = false) String Nom,
      @RequestParam(value = "Prenom", required = false) String Prenom,
      @RequestParam(value = "refUser", required = false) String refUser,
      @RequestParam(value = "datedebut", required = false) String datedebut,
      @RequestParam(value = "datefin", required = false) String datefin,
      @RequestParam(value = "distributeur", required = false) Long distributeur,
      @RequestParam(value = "status", required = false) String status,
      @RequestParam(value = "classification", required = false) String classification,

      @RequestParam(value = "role", required = false) String role,

      Model model, HttpServletRequest request) {
    Page<User> pages = null;
    Date dateCreationDebut = null;
    Date dateCreationFin = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<User> listeDistributeur =
          userRepository.findUsersByTypeUser(UserTypeConstant.DISTRIBUTEUR);

      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      model.addAttribute("listeDistributeur", listeDistributeur);
      request.getSession().setAttribute("listedes_ids", "");

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
      if (classification != null && classification.isEmpty()) {
        classification = null;
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
      if (distributeur != null && Long.valueOf(distributeur) == null) {
        distributeur = null;
      }
      Boolean activation = null;
      if (status != null) {
        if (status.equals("true")) {
          activation = true;
        } else if (status.equals("false")) {
          activation = false;
        }
      }
      if (role != null && role.isEmpty()) {
        role = null;
      }
      int pageSize = 20;
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      if (StringsRole.contains("READ_RETAIL_LIST")) {
        pages = userService.findPaginatedUserByTypeAndFirstNameAndLastName(pageNo, pageSize,
            "REVENDEUR", Nom, Prenom, refUser, gouvernorat, villes, dateCreationDebut,
            dateCreationFin, distributeur, activation, role, classification);
      }
      if (StringsRole.contains("READ_RETAIL_LIST__AREA")) {
        pages = userService.findPaginatedUserByTypeandCreatedByUserId(pageNo, pageSize, "REVENDEUR",
            user.getUserid(), Nom, Prenom, refUser, gouvernorat, villes, dateCreationDebut,
            dateCreationFin, activation, role);
      }
      if (pages != null)
        model.addAttribute("users", pages.getContent());
      int[] body;
      if (pages != null && pages.getTotalPages() > 7) {
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

      model.addAttribute("numberRev", pages.getTotalElements());

      List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
      model.addAttribute("gouvernorats", listGouvernorats);
      model.addAttribute("body", body);
      model.addAttribute("page", pages);
      model.addAttribute("refUser", refUser);
      model.addAttribute("Prenom", Prenom);
      model.addAttribute("Nom", Nom);
      model.addAttribute("datedebut", datedebut);
      model.addAttribute("datefin", datefin);
      model.addAttribute("distributeur", distributeur);
      model.addAttribute("selectedGouvernorat", gouvernorat);
      model.addAttribute("selectedvilles", villes);
      model.addAttribute("selectedStatus", status);
      model.addAttribute("selectedRole", role);
      model.addAttribute("selectedClassification", classification);
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
    return "admin/users/allrevendeur";
  }

  @PreAuthorize("hasAuthority('UPDATE_RETAIL')")
  @RequestMapping(method = RequestMethod.GET, value = "users/editrevendeur/{userid}")
  public String editrevendeurview(@PathVariable("userid") Long userid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    List<Gouvernorat> gouvernerats = gouvernoratRepository.findAll();
    User useredit = userRepository.findUsersByUserid(userid);

    model.addAttribute("user", useredit);
    model.addAttribute("gouvernerats", gouvernerats);

    if (useredit.getGouvernorat() != null) {

      List<Ville> villes = villeRepository.findGouvernoratsByGouvernerat_GouvernoratId(
          useredit.getGouvernorat().getGouvernoratId());
      if (useredit.getVille() != null) {
        List<PostalCode> codePostaleList =
            codePostaleRepository.findPostalCodeByVille_VilleId(useredit.getVille().getVilleId());
        model.addAttribute("codePostaleList", codePostaleList);
      }
      model.addAttribute("villes", villes);
      if (useredit.getGouvernorat() != null) {
        model.addAttribute("gouverneratid", useredit.getGouvernorat().getGouvernoratId());
      }

    } else {
      model.addAttribute("gouverneratid", null);
      model.addAttribute("villes", null);
      model.addAttribute("codePostaleList", null);
    }

    logger.info("user to edit :" + useredit.getCodeUser());
    return "admin/users/edit-revendeur";
  }

  @RequestMapping(method = RequestMethod.POST, value = "users/editrevendeur/{userid}")
  public String editrevendeur(@PathVariable("userid") Long userid, User user1, Model mv,
      @RequestParam("Password") String Password,
      @RequestParam("Confirm_Password") String Confirm_Password,
      @RequestParam("imageFile") MultipartFile imageFile,
      @RequestParam("isExonoree") Boolean isExonoree,

      
      @RequestParam(value = "imageFileRNE", required = false) MultipartFile imageFileRNE,
      @RequestParam(value = "imageFileFiscale", required = false) MultipartFile pdfFileFiscale,
      @RequestParam(value = "Contratpdf", required = false) MultipartFile Contratpdf,
      RedirectAttributes redirectAttrs) {

    User verifUser = userRepository.findUsersByEmail(user1.getEmail());

    User userConnected = new User();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      userConnected = userRepository.findUsersByEmail(currentUser);
    }
    if (verifUser != null && !verifUser.getUserid().equals(userid)) {

      mv.addAttribute("userfullname",
          userConnected.getLastName() + " " + userConnected.getFirstName());
      mv.addAttribute("userphoto", userConnected.getPhoto());
      mv.addAttribute("userrole", userConnected.getRole().getRoleName());
      mv.addAttribute("useremail", userConnected.getEmail());

      mv.addAttribute("existedCode", user1.getEmail());
      User useredit = userRepository.findUsersByUserid(userid);
      mv.addAttribute("user", useredit);
      return "admin/users/edit-revendeur";
    } else {
      if (Password.equals(Confirm_Password)) {



        User newUser = new User();
        newUser = userRepository.findUsersByUserid(userid);

        User oldUser = new User();
        try {
          BeanUtils.copyProperties(oldUser, newUser);
          entityManager.detach(oldUser);
        } catch (IllegalAccessException e1) {
          e1.printStackTrace();
        } catch (InvocationTargetException e1) {
          e1.printStackTrace();
        }



        try {
          BeanUtils.copyProperties(oldUser, newUser);
          entityManager.detach(oldUser);
        } catch (IllegalAccessException e1) {
          e1.printStackTrace();
        } catch (InvocationTargetException e1) {
          e1.printStackTrace();
        }

        newUser.setFirstName(user1.getFirstName());
        newUser.setLastName(user1.getLastName());
        newUser.setEmail(user1.getEmail());
        newUser.setPlafonRevendeur(user1.getPlafonRevendeur());
        newUser.setAdresse(user1.getAdresse());
        newUser.setCodePostale(user1.getCodePostale());
        newUser.setPcActivationCommision(user1.getPcActivationCommision());
        newUser.setPcRefusCommision(user1.getPcRefusCommision());
        newUser.setIsExonoree(isExonoree);
        
        newUser.setWithStock(user1.getWithStock());
        newUser.setRegimeFiscal(user1.getRegimeFiscal());
        newUser.setCoordonneesBancaires(user1.getCoordonneesBancaires());
        newUser.setActivitePrincipale(user1.getActivitePrincipale());

        if (user1.getGouvernorat() != newUser.getGouvernorat()) {
          String codeUser = newUser.getCodeUser();
          if (!codeUser.endsWith("-B")) {
            newUser.setCodeUser(codeUser + "-B");
          }
        }
        if (user1.getNomCommercial() != null) {
          newUser.setNomCommercial(user1.getNomCommercial());
        }
        if (user1.getInterlocuteur() != null) {
          newUser.setInterlocuteur(user1.getInterlocuteur());
        }
        newUser.setVille(user1.getVille());
        newUser.setGouvernorat(user1.getGouvernorat());
        newUser.setClassUser(user1.getClassUser());
        newUser.setTelephone(user1.getTelephone());
        newUser.setFormeJuridique(user1.getFormeJuridique());
        newUser.setIdentificationFiscale(user1.getIdentificationFiscale());
        if (Password.length() > 0) {
          newUser.setPassword(passwordEncoder.encode(Password));
        }

        if (!imageFile.isEmpty()) {
          try {
            userService.updateImage(imageFile, user1.getEmail(), newUser.getPhoto());
            newUser.setPhoto(CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
          } catch (Exception e) {

            logger.error("RevendeurUserController.editrevendeur Exception: " + e.getMessage());

          }
        }
        if (imageFileRNE != null && !imageFileRNE.isEmpty()) {
          try {
            userService.updateImage(imageFileRNE, user1.getEmail(), newUser.getRNE());
            newUser.setRNE(CrmUtils.noSpecialCharacters(imageFileRNE.getOriginalFilename()));
          } catch (Exception e) {

            logger.error(
                " imageFileRNE RevendeurUserController.editrevendeur Exception: " + e.getMessage());

          }
        }
        if (pdfFileFiscale != null && !pdfFileFiscale.isEmpty()) {
          try {

            logger.info("RevendeurUserController edit name  pdfFileFiscale "
                + CrmUtils.noSpecialCharacters(pdfFileFiscale.getOriginalFilename()));
            userService.updateImage(pdfFileFiscale, user1.getEmail(), newUser.getCarteFiscale());
            newUser.setCarteFiscale(
                CrmUtils.noSpecialCharacters(pdfFileFiscale.getOriginalFilename()));
          } catch (Exception e) {

            logger.error("pdfFileFiscale RevendeurUserController.editrevendeur Exception: "
                + e.getMessage());

          }
        }

        if (Contratpdf != null && !Contratpdf.isEmpty()) {
          try {

            logger.info("editrevendeur name  Contratpdf"
                + CrmUtils.noSpecialCharacters(Contratpdf.getOriginalFilename()));
            userService.updateImage(Contratpdf, user1.getEmail(), newUser.getContrat());
            newUser.setContrat(CrmUtils.noSpecialCharacters(Contratpdf.getOriginalFilename()));
          } catch (Exception e) {

            logger.error("RevendeurUserController.editrevendeur Exception: " + e.getMessage());

          }
        }


        userRepository.save(newUser);
        userHistoryService.checkAndSaveHistory(oldUser, newUser, userConnected);
        logger.info("revendeuredited success");

        redirectAttrs.addFlashAttribute("message", "revendeuredited");
      } else {
        mv.addAttribute("userfullname",
            userConnected.getLastName() + " " + userConnected.getFirstName());
        mv.addAttribute("userphoto", userConnected.getPhoto());
        mv.addAttribute("userrole", userConnected.getRole().getRoleName());
        mv.addAttribute("useremail", userConnected.getEmail());

        mv.addAttribute("passwordnotmatch", Confirm_Password);
        User useredit = userRepository.findUsersByUserid(userid);
        List<Gouvernorat> gouvernerat = gouvernoratRepository.findAll();
        mv.addAttribute("villes", gouvernerat);
        mv.addAttribute("user", useredit);

        if (useredit.getGouvernorat() != null) {
          mv.addAttribute("villdeid", useredit.getGouvernorat().getGouvernoratId());
        } else {
          mv.addAttribute("villdeid", null);
        }
        if (useredit.getGouvernorat() != null) {
          mv.addAttribute("gvid", useredit.getGouvernorat());
        } else {
          mv.addAttribute("gvid", "");
        }
        return "admin/users/edit-revendeur";
      }
    }
    return "redirect:/RevendeurUser/revendeurliste/" + 1;
  }

  @PreAuthorize("hasAuthority('DEACTIVATE_RETAIL')")
  @RequestMapping(value = "/revendeurEnableDisable/{userid}", method = RequestMethod.POST)
  public String revendeurEnableDisable(@PathVariable Long userid,
      RedirectAttributes redirectAttrs) {

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

    if (!useredit.isEnabled()) {
      redirectAttrs.addFlashAttribute("message", "isnoteactive");
    } else {
      redirectAttrs.addFlashAttribute("message", "isactive");
    }
    return "redirect:/RevendeurUser/revendeurliste/" + 1;
  }

  
  @RequestMapping(value = "/edit_classification_revendeur/{userid}", method = RequestMethod.POST)
  public String editClassificationRevendeur(@PathVariable Long userid,   @RequestParam(value = "dateSuspendu", required = false) String dateSuspendu,
		  @RequestParam("newClassification") String newClassification , 
      RedirectAttributes redirectAttrs) {

    User userConnected = new User();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      userConnected = userRepository.findUsersByEmail(currentUser);
    }

    if (!ClassificationRevendeur.isValid(newClassification)) {
    	redirectAttrs.addFlashAttribute("message", "Statut-classification-invalide.");
        return "redirect:/RevendeurUser/list";
    }
    User useredit = userRepository.findUsersByUserid(userid);
    useredit.setClassification(newClassification);
    if((newClassification.equals(ClassificationRevendeur.Senrecouvrement)
    		|| newClassification.equals(ClassificationRevendeur.suspendu))
    		&& dateSuspendu != null ) {
    	Date datesuspendu  = CrmUtils.convertStringToDate(dateSuspendu);
    	useredit.setDesactivationDate(datesuspendu);
    	useredit.setDateUpdateclassification(datesuspendu);
    }
    else {
    	useredit.setDesactivationDate(null);
    }
    userRepository.save(useredit);

    // historique utilistaeur
    String action =  "La classification de l'utilisateur a été changée en : "
         + newClassification;
    if(!action.trim().isEmpty()) {
        userHistoryService.addHistoryEntry(userid, action, userConnected);

    }

  
      redirectAttrs.addFlashAttribute("message", "statut"+ClassificationRevendeur.precontentieux);
   
    return "redirect:/RevendeurUser/revendeurliste/" + 1;
  }
  @RequestMapping(value = "/revendeurEnableDisableFromRecap/{userid}", method = RequestMethod.POST)
  public String revendeurEnableDisableFromRecap(@PathVariable Long userid,
      RedirectAttributes redirectAttrs) {
    User useredit = userRepository.findUsersByUserid(userid);
    useredit.setEnabled(!useredit.isEnabled());
    userRepository.save(useredit);
    User userConnected = new User();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      userConnected = userRepository.findUsersByEmail(currentUser);
    }
    // historique utilistaeur
    String action = useredit.isEnabled() ? "Le statut de l'utilisateur a été activé."
        : "Le statut de l'utilisateur a été désactivé.";
    userHistoryService.addHistoryEntry(userid, action, userConnected);
    if (!useredit.isEnabled()) {
      redirectAttrs.addFlashAttribute("message", "isnoteactive");
    } else {
      redirectAttrs.addFlashAttribute("message", "isactive");
    }
    return "redirect:/payement/viewlisterecaperevendeur";
  }

  @PreAuthorize("hasAuthority('UNLOCK_RETAIL')")
  @RequestMapping(value = "/revendeurisLocked/{userid}", method = RequestMethod.POST)
  public String revendeurisLocked(@PathVariable Long userid, RedirectAttributes redirectAttrs) {

    User userConnected = new User();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      userConnected = userRepository.findUsersByEmail(currentUser);
    }

    String action = null;

    User useredit = userRepository.findUsersByUserid(userid);
    // useredit.setLocked(!useredit.isLocked());
    Role roleuser = useredit.getRole();
    if (useredit.isLocked() == true) {
      String namerole = roleuser.getRoleName().substring(0, roleuser.getRoleName().length() - 10);

      Role newrole = roleService.findRoleByRoleName(namerole);
      if (newrole != null) {
        useredit.setRole(newrole);
        useredit.setLocked(!useredit.isLocked());
        if(useredit.getClassification() == null  || (useredit.getClassification() != null &&
          		!useredit.getClassification().equals(ClassificationRevendeur.precontentieux)	) )
          {
        	  useredit.setClassification(ClassificationRevendeur.Sactiver);
        	  useredit.setDateUpdateclassification(new Date());
          	useredit.setDesactivationDate(null);

        }
      
        redirectAttrs.addFlashAttribute("message", "isunLocked");
        if (useredit.isLocked() == true && useredit.getDesactivatedByCron() == true) {
          useredit.setDesactivatedByCron(false);
        }
        // historique user
        action = "Le statut de l'utilisateur a été Verrouillé";

      } else {
        redirectAttrs.addFlashAttribute("message", "roleundefind");
      }
    } else {

      Role newrole = roleService.findRoleByRoleName(roleuser.getRoleName() + "_DESACTIVE");

      if (newrole != null) {
        useredit.setRole(newrole);
        useredit.setLocked(!useredit.isLocked());
        if(useredit.getClassification() == null  || (useredit.getClassification() != null &&
          		!useredit.getClassification().equals(ClassificationRevendeur.precontentieux)	) )
          {
        	useredit.setClassification(ClassificationRevendeur.suspendu);
        	useredit.setDesactivationDate(new Date());
        	useredit.setDateUpdateclassification(new Date());
          }
    

        redirectAttrs.addFlashAttribute("message", "isLocked");

        // historique user
        action = "Le statut de l'utilisateur a été Déverrouillé";

      } else {
        redirectAttrs.addFlashAttribute("message", "roleundefind");
      }
    }

    userHistoryService.addHistoryEntry(userid, action, userConnected);

    userRepository.save(useredit);
    return "redirect:/RevendeurUser/revendeurliste/" + 1;
  }

  @PreAuthorize("hasAuthority('READ_RETAIL_LIST')")
  @RequestMapping(method = RequestMethod.GET, value = "users/detailrevendeur/{userid}")
  public String detailrevendeur(@PathVariable("userid") Long userid, Model model) {
    List<String> StringsRole = new ArrayList<String>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    }

    if (StringsRole.contains("VIEW_USER_HISTORY")) {
      List<UserHistory> lUserHistories = userHistoryService.getHistoryByUser(userid);
      model.addAttribute("lUserHistories", lUserHistories);
    }

    User userDetail = userRepository.findUsersByUserid(userid);

    User creted = userRepository.findUsersByUserid(userDetail.getCreatedByUserId());

    if (userDetail.getCodePostale() != null) {
      Optional<PostalCode> postalCode = codePostaleRepository.findById(userDetail.getCodePostale());
      model.addAttribute("postalCode",
          postalCode.get().getCode() + "-" + postalCode.get().getName());
    }
    User AssignedToUser = null;
    if (userDetail.getAffectedTo() != null) {
      AssignedToUser = userRepository.getById(userDetail.getAffectedTo());
      model.addAttribute("AssignedToUser", AssignedToUser);
    }
    model.addAttribute("user", userDetail);
    model.addAttribute("creted", creted);

    return "admin/users/detailrevendeur";
  }

  @PreAuthorize("hasAuthority('AFFECTED_ALL_USER')")
  @RequestMapping(method = RequestMethod.POST, value = "affectationUser",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public List<User> affectationUser(@RequestBody HashMap<String, String> affectationClient,
      RedirectAttributes redirectAttrs) {
    List<User> userListe = null;
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        userListe = userService.findUsersByTypeUserAndRecherche(UserTypeConstant.DISTRIBUTEUR,
            affectationClient.get("recherche").trim());

      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.affectationClientt Error:" + e);

    }
    return userListe;

  }

  @RequestMapping(method = RequestMethod.POST, value = "sendAffectationClient",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Boolean submitAffectationClients(@RequestBody HashMap<String, String> affectationClient,
      RedirectAttributes redirectAttrs) {
    Boolean isAffectedUser = false;
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        isAffectedUser = userService.affectUser(affectationClient.get("userselected"),
            Long.parseLong(affectationClient.get("userId")));
        if (isAffectedUser) {
          User userToBeAffected =
              userRepository.getById(Long.parseLong(affectationClient.get("userselected")));
          String action = "Réaffectation d'un revendeur à" + userToBeAffected.getCodeUser();
          userHistoryService.addHistoryEntry(Long.parseLong(affectationClient.get("userId")),
              action, user);
        }

        /*
         * if (isAffectedUser) { abonnementHistoriqueService.saveNewHistorique(user,
         * Long.parseLong(affectationClient.get("demandeId")), "Affectation Revendeur"); }
         */
        // historique utilistaeur

      }
    } catch (Exception e) {
      logger.error(" demandeabonnement.affectationClientt Error:" + e);

    }
    return isAffectedUser;

  }

  @RequestMapping(method = RequestMethod.GET, value = "Exporter")
  public void Exporter(@RequestParam(value = "gouvernorat", required = false) Long gouvernorat,
      @RequestParam(value = "villes", required = false) Long villes,
      @RequestParam(value = "Nom", required = false) String Nom,
      @RequestParam(value = "Prenom", required = false) String Prenom,
      @RequestParam(value = "refUser", required = false) String refUser,
      @RequestParam(value = "datedebut", required = false) String datedebut,
      @RequestParam(value = "datefin", required = false) String datefin,
      @RequestParam(value = "role", required = false) String role,
      @RequestParam(value = "distributeur", required = false) Long distributeur,
      @RequestParam(value = "status", required = false) String status,
      @RequestParam(value = "classification", required = false) String classification,


      HttpServletResponse response, HttpServletRequest request) throws IOException {

    System.out.println(gouvernorat + " " + villes + " " + Nom + " " + Prenom + " " + refUser + " "
        + datedebut + " " + datefin + " " + distributeur + " " + status);

    Boolean activation = null;
    if (status != null) {
      if (status.equals("true")) {
        activation = true;
      } else if (status.equals("false")) {
        activation = false;
      }
    }
    List<User> listuser = new ArrayList<User>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Long roleRecherch = null;
      if (role != null && !role.isEmpty()) {
        Role getrole = roleService.findRoleByRoleName(role);
        roleRecherch = getrole.getRoleId();
      }
      if (StringsRole.contains("READ_RETAIL_LIST")) {
        listuser =
            userService.findAllUserByRecherche("REVENDEUR", Nom, Prenom, refUser, gouvernorat,
                villes, datedebut, datefin, distributeur, activation, roleRecherch, classification);
      }
      if (StringsRole.contains("READ_RETAIL_LIST__AREA")) {
        listuser =
            userService.findAllUserByRecherche("REVENDEUR", Nom, Prenom, refUser, gouvernorat,
                villes, datedebut, datefin, user.getUserid(), activation, roleRecherch, null);
      }

      if (listuser.size() > 0) {
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headervalue = "attachment; filename=revendeur_info.csv";
        response.setHeader(headerKey, headervalue);
        PrintWriter writer = response.getWriter();
        String title = "Liste revendeur";
        int totalWidth = 200;
        int titleWidth = title.length();
        int padding = (totalWidth - titleWidth) / 2;

        String centeredTitle = String.format("%" + padding + "s%s%" + padding + "s", "", title, "");
        writer.println(centeredTitle);
        writer.println(

            "Nom ; Prénom;Créé le ; Type; CIN ; Email; Code utilisateur; Interlocuteur;Nom commercial; Gouvernorat ;Ville ;Assigné à ;Code assigné;Créé par;Code du créateur;  Plafond autorisé ; Coordonnées bancaires;Classification; Date classification  ;Role ; Status;Tva");

        String interlocuteur = "";
        String nomComercial = "";
        String nomUserCreePar = "";
        String lastNameUserCreePar = "";
        String codeUserCreePar = "";
        String plafonRev = "";
        String roleUser = "";
        String statusString = "Activé" ;
        String  statusTVAString="Soumise à la TVA";
        for (User obj : listuser) {
          if (obj.getInterlocuteur() != null)
            interlocuteur = obj.getInterlocuteur();
          User userAssinger = userService.findUsersByIduser(obj.getAffectedTo());

          User userCreePar = userService.findUsersByIduser(obj.getCreatedByUserId());
          if (userCreePar != null) {
            nomUserCreePar = userCreePar.getFirstName();
            lastNameUserCreePar = userCreePar.getLastName();
            codeUserCreePar = userCreePar.getCodeUser();
          }
          if (obj.getRole() != null) {
            roleUser = obj.getRole().getRoleName();
          }
          if (obj.getNomCommercial() != null)
            nomComercial = obj.getNomCommercial();
          if (obj.getPlafonRevendeur() != null)
            plafonRev = obj.getPlafonRevendeur().toString();
          // Convert the object fields to an array of strings
          if (obj.isEnabled() == false) {
        	  statusString = "Desactivé";  
          }
          else {
        	  statusString = "Activé";  

          }
        	
          if (obj.getIsExonoree()== false) {
        	  statusTVAString = "oumise à la TVA";  
          }
          else {
        	  statusTVAString = "Exonérée de TVA";  

          }
          String csvLine = obj.getFirstName() + ";" + obj.getLastName() + ";" + obj.getCreatedDate()
              + ";" + obj.getTypeUser() + ";" + obj.getCin() + ";" + obj.getEmail() + ";"
              + obj.getCodeUser() + ";" + interlocuteur + ";" + nomComercial + ";"
              + obj.getGouvernorat().getGouvernoratName() + ";" + obj.getVille().getVilleName()
              + ";" + userAssinger.getFirstName() + " " + userAssinger.getLastName() + "("
              + userAssinger.getCodeUser() + ")" + ";" + userAssinger.getCodeUser() + ";"
              + nomUserCreePar + " " + lastNameUserCreePar + ";" + codeUserCreePar + ";" + plafonRev
              + ";" + obj.getCoordonneesBancaires() + ";" + obj.getClassification()+ ";"+obj.getDateUpdateclassification() +";" + roleUser +
              ";"  + statusString +  ";" + statusTVAString;

          writer.println(csvLine);
        }
        writer.flush();
        writer.close();
      } else {
        try {
          request.getRequestDispatcher("/RevendeurUser/revendeurliste/1").forward(request,
              response);
        } catch (ServletException | IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          logger.error(" revendeur .Exporter xls Error:" + e);
        }
      }
    }
  }
}
