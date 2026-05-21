package crm.chifco.com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import crm.chifco.com.model.FicheStock;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.FicheRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.FicheStockService;
import crm.chifco.com.service.ModemService;
import crm.chifco.com.templateclasse.ModemAffectationFiches;

@Controller
public class FicheController {

  @Autowired
  private FicheStockService ficheservice;
  @Autowired
  private ModemService modemservice;

  @Autowired
  UserRepository userRepository;
  @Autowired
  FicheRepository ficheRepository;

  // ************************************* api pour afficher la liste des fiches
  // dans la BDD

  @RequestMapping(value = "/fiches")
  public String fiches(Model model) {
    Long idconnected = null;
    List<String> StringsRole = new ArrayList<String>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      idconnected = user.getUserid();
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    if (StringsRole.contains("READ_MODEM")) {
      model.addAttribute("listDistributeur", userRepository.findUsersByTypeUser("DISTRIBUTEUR"));
      model.addAttribute("listAffectA", userRepository.listUsersDistRevPos());
    } else if ((StringsRole.contains("READ_MODEM_LIST_AREA")
        || (StringsRole.contains("READ_MODEM_POS") || (StringsRole.contains("READ_MODEM_OWNER"))
            && (StringsRole.contains("LIST_MODEM_AFFECTED_ADMIN")
                || StringsRole.contains("LIST_MODEM_AFFECTED_OTHER"))))) {
      model.addAttribute("listAffectA", userRepository.findUsersByAffectedTo(idconnected));
    }
    return "fiche/fiches";
  }

  @RequestMapping(value = "/allfiches")
  @ResponseBody
  public HashMap<String, Object> allFiches(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("order[0][column]") String ordercolumnaram,
      @RequestParam("order[0][dir]") String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    Long idconnected = null;

    List<String> StringsRole = new ArrayList<String>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = null;
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      user = userRepository.findUsersByEmail(currentUser);
      idconnected = user.getUserid();
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    }

    int currentpage = start / length;
    Pageable pageable =
        PageRequest.of(currentpage, length, Sort.by(Sort.Direction.DESC, "id_fiche"));

    // filtre
    String datedebut = null;
    String datefin = null;
    Long distId = null;
    Long affectA = null;
    Long myAffect = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("dateDebut"), "") && obj.getString("dateDebut") != null) {
        datedebut = obj.getString("dateDebut") + "T00:00:00.000";
      }
      if (!Objects.equals(obj.getString("dateFin"), "") && obj.getString("dateFin") != null) {
        datefin = obj.getString("dateFin") + "T23:59:59.999";
      }
      if (!Objects.equals(obj.getString("distId"), "") && obj.getString("distId") != null) {
        distId = obj.getLong("distId");
      }
      if (!Objects.equals(obj.getString("affectA"), "") && obj.getString("affectA") != null) {
        affectA = obj.getLong("affectA");
      }
      if (!Objects.equals(obj.getString("myAffect"), "") && obj.getString("myAffect") != null) {
        myAffect = obj.getString("myAffect").trim().equals("myAffect") ? user.getUserid() : 0L;
      }
    }

    Page<ModemAffectationFiches> fiches = null;

    if (StringsRole.contains("READ_MODEM")) {
      fiches =
          ficheRepository.findAllFiches(pageable, datedebut, datefin, distId, affectA, myAffect);
    } else if ((StringsRole.contains("READ_MODEM_LIST_AREA")
        || (StringsRole.contains("READ_MODEM_POS") || (StringsRole.contains("READ_MODEM_OWNER"))
            && (StringsRole.contains("LIST_MODEM_AFFECTED_ADMIN")
                || StringsRole.contains("LIST_MODEM_AFFECTED_OTHER"))))) {
      fiches =
          ficheservice.getFiches(currentpage + 1, length, idconnected, datedebut, datefin, affectA);
    }

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", fiches.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", fiches.getTotalElements());
    myGreetings.put("recordsFiltered", fiches.getTotalElements());

    return myGreetings;
  }

  // ***************************************** api pour visulaiser une fiche de
  // stock
  @RequestMapping(path = {"/fiche/{ref}"})
  public String getfiche(Model model, @PathVariable("ref") String ref) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    FicheStock fiche = ficheservice.getFiche(ref); // recuperer la fiche par sa reference qui est
                                                   // unique
    List<Modem> modems = ficheservice.listmodemfiche(ref); // recuperer la liste des modems en
                                                           // utilisant leurs
                                                           // references
    // methode de la classe FicheService , package : service
    User u = ficheservice.usersfiche(fiche.getAffecteID()); // recuperer user de la fiche
    User u2 = userRepository.findByUserId(fiche.getAffectedBYuser());
    model.addAttribute("fiche", fiche);
    model.addAttribute("modems", modems);
    model.addAttribute("users", u);
    model.addAttribute("usersBy", u2);

    model.addAttribute("quantite", modems.size()); // quantite des modems affectés dans la fiche
    return "fiche/OpenFiche";
  }

  // *************************************

}
