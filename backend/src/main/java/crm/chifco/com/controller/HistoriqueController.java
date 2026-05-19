package crm.chifco.com.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import crm.chifco.com.model.Historique;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.HistoriqueRepository;
import crm.chifco.com.repository.UserRepository;

@Controller
public class HistoriqueController {

  @Autowired
  private FactureRepository fRepo;

  @Autowired
  private DemandeAbonnementRepository abonRepo;
  @Autowired
  private UserRepository uRepo;

  @Autowired
  private HistoriqueRepository historiqueRepository;

  @RequestMapping(value = "/historique")
  public String historiques(Model model) {

    Long idconnected = null;
    String role = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = uRepo.findUsersByEmail(currentUser);
      role = user.getRole().getRoleName();
      idconnected = user.getUserid();
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    List<Historique> historique = new ArrayList<>();
    if (role.equals("ROLE_ADMINISTRATEUR") || role.equals("ROLE_G.STOCK")) {
      historique = historiqueRepository.findAll();
      model.addAttribute("listehistorique", historique);
    } else {
      historique = historiqueRepository.historiqueByIdConnected(idconnected);
      model.addAttribute("listehistorique", historique);
    }

    return "historique/historique";
  }

  @RequestMapping(value = "/historique_facture")
  public String HistoriqueFilter(Model model) {

    Long idconnected = null;
    String role = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = uRepo.findUsersByEmail(currentUser);
      role = user.getRole().getRoleName();
      idconnected = user.getUserid();
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    List<User> user = new ArrayList<>();
    if (role.equals("ROLE_ADMINISTRATEUR") || role.equals("ROLE_G.STOCK")) {
      user = uRepo.findAll();
      model.addAttribute("user", user);
    } else {
      user = uRepo.findUsersByAffectedTo(idconnected);
      model.addAttribute("user", user);
    }

    return "historique/filtrer_historique_user";
  }

  @RequestMapping(value = "/List_Historique")
  public String AffectedQuantiteByUser(Model model,

      @RequestParam(value = "userid") String usermail) throws Exception {
    Long idconnected = null;
    String role = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = uRepo.findUsersByEmail(currentUser);
      idconnected = user.getUserid();
      role = user.getRole().getRoleName();
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    List<User> user;

    if (role.equals("ROLE_ADMINISTRATEUR") || role.equals("ROLE_G.STOCK")) {
      user = uRepo.findAll();
      model.addAttribute("user", user);
    } else {
      user = uRepo.findUsersByAffectedTo(idconnected);
      model.addAttribute("user", user);
    }
    Long iduser = uRepo.findUsersByEmail(usermail).getUserid();
    User users = uRepo.getById(iduser);
    List<Historique> listHistoriqueUser = historiqueRepository.historiqueByUser(iduser);

    // envoyer la liste ver sla page html pour les afficher
    model.addAttribute("Message", "L'historique de factures générées pour " + users.getEmail());
    model.addAttribute("listHistorique", listHistoriqueUser);

    return "historique/historique_user";

  }

}
