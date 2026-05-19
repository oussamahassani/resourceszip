package crm.chifco.com.controller;

import java.util.List;
import java.util.Optional;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.CategorieProduitInternet;
import crm.chifco.com.model.Produit;
import crm.chifco.com.model.Tarification;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.ProduitRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.CategorieProduitInternetService;
import crm.chifco.com.service.ProduitService;
import crm.chifco.com.service.TarificationServices;
import crm.chifco.com.utils.CrmUtils;

@Controller
@RequestMapping(value = "pi/*")
public class ProduitInternetController {

  private final Logger logger = LogManager.getLogger(this.getClass());
  @Autowired
  UserRepository userRepository;
  @Autowired
  CategorieProduitInternetService categorieProduitInternetService;
  @Autowired
  CategorieProduitInternetRepository categorieProduitInternetRepository;
  @Autowired
  ProduitRepository produitRepository;
  @Autowired
  ProduitService produitService;

  @Autowired
  TarificationServices tarificationServices;

  @PreAuthorize("hasAuthority('READ_CATEGORY')")
  @GetMapping(value = "allcategoriesproduitsinternet/{pageNo}")
  public String categoriesproduitsinternet(@PathVariable(value = "pageNo") Integer pageNo,
      Model model, HttpServletRequest request) {
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
    Page<CategorieProduitInternet> pages =
        categorieProduitInternetService.findPaginated(pageNo, pageSize);
    model.addAttribute("categoriesproduitsinternet", pages.getContent());

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
    return "pi/allcategoriesproduitsinternet";
  }

  @RequestMapping(method = RequestMethod.GET, value = "createcategorieproduitinternet")
  public String createcategorieproduitinternet(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    return "pi/addcategorieproduitinternet";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createcategorieproduitinternet")
  public String createcategorieproduitinternet(CategorieProduitInternet categorieProduitInternet,
      Model model) {

    CategorieProduitInternet categorieProduitInternettocheck = categorieProduitInternetRepository
        .findCategorieProduitInternetByCategorieProduitInternetCode(
            categorieProduitInternet.getCategorieProduitInternetCode());
    if (categorieProduitInternettocheck == null) {
      categorieProduitInternettocheck =
          new CategorieProduitInternet(categorieProduitInternet.getCategorieProduitInternetNom(),
              categorieProduitInternet.getCategorieProduitInternetCode());
      categorieProduitInternetRepository.save(categorieProduitInternettocheck);
      return "redirect:/pi/allcategoriesproduitsinternet/1";
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
      model.addAttribute("existedCode", "codeexiste");
      return "pi/addcategorieproduitinternet";
    }
  }

  @PreAuthorize("hasAuthority('WRITE_CATEGORY')")
  @RequestMapping(method = RequestMethod.GET,
      value = "editcategorieproduitinternet/{categorieproduitinternetid}")
  public String updatecategorieproduitinternet(
      @PathVariable("categorieproduitinternetid") Long categorieproduitinternetid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    CategorieProduitInternet categorieProduitInternet = categorieProduitInternetRepository
        .findCategorieProduitInternetByCategorieProduitInternetId(categorieproduitinternetid);
    model.addAttribute("categorieproduitinternet", categorieProduitInternet);
    return "pi/editcategorieproduitinternet";
  }

  @RequestMapping(method = RequestMethod.POST,
      value = "editcategorieproduitinternet/{categorieproduitinternetid}")
  public String updateCategorieProduitInternet(
      @PathVariable("categorieproduitinternetid") Long categorieproduitinternetid,
      CategorieProduitInternet categorieProduitInternet, Model mv) {
    CategorieProduitInternet categorieProduitInternettocheck = categorieProduitInternetRepository
        .findCategorieProduitInternetByCategorieProduitInternetCode(
            categorieProduitInternet.getCategorieProduitInternetCode());
    if (categorieProduitInternettocheck != null && !categorieProduitInternettocheck
        .getCategorieProduitInternetId().equals(categorieproduitinternetid)) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        mv.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        mv.addAttribute("userphoto", user.getPhoto());
        mv.addAttribute("userrole", user.getRole().getRoleName());
        mv.addAttribute("useremail", user.getEmail());
      }
      CategorieProduitInternet categorieProduitInternet1 = categorieProduitInternetRepository
          .findCategorieProduitInternetByCategorieProduitInternetId(categorieproduitinternetid);
      mv.addAttribute("categorieproduitinternet", categorieProduitInternet1);
      mv.addAttribute("existedCode", "existedCode");
      return "pi/editcategorieproduitinternet";
    } else {
      CategorieProduitInternet categorieProduitInternettoedit = categorieProduitInternetRepository
          .findCategorieProduitInternetByCategorieProduitInternetId(categorieproduitinternetid);
      categorieProduitInternettoedit.setCategorieProduitInternetCode(
          categorieProduitInternet.getCategorieProduitInternetCode());
      categorieProduitInternettoedit.setCategorieProduitInternetNom(
          categorieProduitInternet.getCategorieProduitInternetNom());
      categorieProduitInternetRepository.save(categorieProduitInternettoedit);
      return "redirect:/pi/allcategoriesproduitsinternet/1";
    }
  }

  @PreAuthorize("hasAuthority('READ_PRODUCT')")
  @GetMapping(value = "allproduits/{pageNo}")
  public String produits(@PathVariable(value = "pageNo") Integer pageNo, Model model,
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
    Page<Produit> pages = produitService.findPaginated(pageNo, pageSize);
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
    model.addAttribute("produits", pages.getContent());
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

    return "pi/allproduits";
  }


  @PreAuthorize("hasAuthority('WRITE_PRODUCT')")
  @RequestMapping(method = RequestMethod.GET, value = "createproduit")
  public String createproduit(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    model.addAttribute("categories", categorieProduitInternets);
    return "pi/addproduit";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createproduit")
  public String createproduit(@RequestParam("Nom") String Nom, @RequestParam("Code") String Code,
      @RequestParam(value = "isExtra", required = false) Boolean isExtra,
      @RequestParam(value = "isRacordement", required = false) Boolean isRacordement,
      @RequestParam(value = "isDefault", required = false) Boolean isDefault,
      @RequestParam(value = "withIpFix", required = false) Boolean withiffixe,
      @RequestParam("produitPrix") Double prixUnitaire, @RequestParam("remise") Double remise,
      @RequestParam("pourcentTva") Double pourcentTva,



      Model model) {

    Produit produittocheck = produitRepository.findProduitByProduitCode(Code);
    if (produittocheck == null) {
      Produit produit = new Produit();
      produit.setProduitCode(Code);
      produit.setProduitNom(Nom);

      Double produitPrixTTC = ((prixUnitaire * (pourcentTva * 0.01)) + prixUnitaire);
      produitPrixTTC = CrmUtils.formatDoubleInput(produitPrixTTC);


      if (withiffixe != null) {
        produit.setWithIpFix(withiffixe);
      }
      if (isDefault != null) {
        produit.setIsDefault(isDefault);
      } else {
        produit.setIsDefault(false);
      }
      if (isRacordement != null) {
        produit.setIsRacordement(isRacordement);
      }

      if (isExtra != null) {
        produit.setIsExtra(isExtra);
      }
      produitRepository.save(produit);


      tarificationServices.saveNewTarification(null, produit.getProduitId(), prixUnitaire,
          pourcentTva.longValue(), "montant", remise, produitPrixTTC, null);
      return "redirect:/pi/allproduits/1";
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
      model.addAttribute("existedCode", "codeexiste");
      return "pi/addproduit";
    }
  }

  @PreAuthorize("hasAuthority('WRITE_PRODUCT')")
  @RequestMapping(method = RequestMethod.GET, value = "editproduit/{produitid}")
  public String updateproduit(@PathVariable("produitid") Long produitid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    Produit produit = produitRepository.findProduitByProduitId(produitid);
    Tarification tarification =
        tarificationServices.getTarificationByProduitId(produit.getProduitId());
    model.addAttribute("produit", produit);
    model.addAttribute("tarification", tarification);
    return "pi/editproduit";
  }

  @RequestMapping(method = RequestMethod.POST, value = "editproduit/{produitid}")
  public String updateproduit(@PathVariable("produitid") Long produitid, Produit produit,
      Tarification tarification, Model mv) {
    Produit produittocheck = produitRepository.findProduitByProduitCode(produit.getProduitCode());
    if (produittocheck != null && !produittocheck.getProduitId().equals(produitid)) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        mv.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        mv.addAttribute("userphoto", user.getPhoto());
        mv.addAttribute("userrole", user.getRole().getRoleName());
        mv.addAttribute("useremail", user.getEmail());
      }
      Produit produittoedit = produitRepository.findProduitByProduitId(produitid);
      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      mv.addAttribute("produit", produittoedit);
      mv.addAttribute("categories", categorieProduitInternets);
      mv.addAttribute("existedCode", "existedCode");
      return "pi/editproduit";
    } else {
      Produit produittoedit = produitRepository.findProduitByProduitId(produitid);
      produittoedit.setProduitNom(produit.getProduitNom());
      produittoedit.setProduitCode(produit.getProduitCode());
      produittoedit.setWithIpFix(produit.getWithIpFix());
      produittoedit.setIsExtra(produit.getIsExtra());
      produittoedit.setIsRacordement(produit.getIsRacordement());
      produittoedit.setIsDefault(produit.getIsDefault());
      produitRepository.save(produittoedit);
      Double produitPrixTTC = ((tarification.getPrixUnitaire() * (tarification.getTaxe() * 0.01))
          + tarification.getPrixUnitaire());

      produitPrixTTC = CrmUtils.formatDoubleInput(produitPrixTTC);


      tarificationServices.updateTarification(tarification.getTarificationId(),
          tarification.getPrixUnitaire(), tarification.getTaxe(), "montant",
          tarification.getRemise(), produitPrixTTC, null);
      logger.info("produit editer" + produit.getProduitCode());
      return "redirect:/pi/allproduits/1";
    }
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "calculettc")
  public String createproduit(@RequestParam("pourcentTva") Double pourcentTva,
      @RequestParam("produitprixHt") Double produitprixHt) {

    Double produitPrixTTC = ((produitprixHt * (pourcentTva / 100)) + produitprixHt);
    produitPrixTTC = CrmUtils.formatDoubleInput(produitPrixTTC);
    // String fromatedPrixTTc = produitPrixTTC.toString().format("%.3f", produitPrixTTC);
    return produitPrixTTC.toString();
  }

  @RequestMapping(method = RequestMethod.GET, value = "editactivation/{produitid}")
  public String updateActivation(@PathVariable("produitid") Long produitid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Produit produit = produitRepository.findProduitByProduitId(produitid);
    Tarification tarification =
        tarificationServices.getTarificationByProduitId(produit.getProduitId());
    model.addAttribute("produit", produit);
    model.addAttribute("tarification", tarification);
    return "pi/editproduit";
  }

  @PreAuthorize("hasAuthority('WRITE_PRODUCT')")
  @RequestMapping(method = RequestMethod.GET, value = "/produit/activation/{produitid}")
  public String activationProduit(@PathVariable("produitid") Long produitId,
      RedirectAttributes redirectAttributes) {

    Optional<Produit> produit = produitRepository.findById(produitId);
    if (produit.isPresent()) {
      produitService.activationProduit(produitId, produit.get().getIsActive());
      redirectAttributes.addFlashAttribute("successMessage",
          "Le changement a été effectué avec succès.");
      return "redirect:/pi/allproduits/1";
    } else {
      return "redirect:/pi/allproduits/1";
    }
  }

}
