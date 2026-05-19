/*
 * created by hatem ghozzi on 18 10 2022
 */

package crm.chifco.com.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.controller.AffectController;
import crm.chifco.com.crmMobile.ModemVerificationResult;
import crm.chifco.com.model.FicheStock;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AffectService;
import crm.chifco.com.service.FicheStockService;
import crm.chifco.com.service.ModemHistoryService;
import crm.chifco.com.service.ModemService;
import crm.chifco.com.utils.UserTypeConstant;

@Service("AffectService")
public class AffectServiceimpl implements AffectService {

  private static final Logger logger = LogManager.getLogger(AffectController.class);
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private FicheStockService ficheStockService;

  @Autowired
  private ModemService modemservice;
  @Autowired
  private ModemRepository modemRepository;

  @Autowired
  private ModemHistoryService modemHistoryService;

  // *********************************************** Valider Affectation
  @Override
  public Boolean affecter(List<Modem> modems, User user, String type_name, Long idUserConnected) {
    // pour assurer l'affectation, on va modifier le champs affectpointdevente, ou
    // affectrevendeur ou affectclient, ou affectdistributeur (selon le role)
    // ces champs doivent prendre iduser comme valeur pour indiquer que ce modem est
    // affecté au l'utilisateur qui a iduser.

    // modem historique
    String action = "Affecter le modem au " + user.getFirstName() + " " + user.getLastName()
        + (user.getNomCommercial() != null ? " / " + user.getNomCommercial() : "") + " / "
        + user.getCodeUser();

    User userConnnected = userRepository.getById(idUserConnected);

    switch (type_name) {
      case UserTypeConstant.POS:

        for (Modem p : modems) {
          p.setAffectePointdeVente(user.getUserid());
          modemRepository.save(p);
          modemHistoryService.save(action, userConnnected, p);
        } // affecté au pos
        return true;
      case UserTypeConstant.DISTRIBUTEUR:
        for (Modem p : modems) {
          p.setAffecteDistributeur(user.getUserid());
          modemRepository.save(p);
          modemHistoryService.save(action, userConnnected, p);
        } // affecté au distributeur
        return true;
      case UserTypeConstant.REVENDEUR:
        for (Modem p : modems) {
          p.setAffecteRevendeur(user.getUserid());
          modemRepository.save(p);
          modemHistoryService.save(action, userConnnected, p);
        } // affecté au revendeur
        return true;

      default:
        return false;
    }

  }

  @Override
  public void confirmerAffectation(Model model, String roleName, String usermail, String modele,
      Integer quantite) {
    Long idconnected = null;
    String roleNameUser = null;
    User user = null;

  }

  private FicheStock getFicheStock(List<String> listrefrence, Integer quantite, Long idconnected,
      Long iduser, Random rand) {
    // recuperés
    FicheStock fiches = new FicheStock(); // creation une nouvelle fiche de stock
    fiches.setNumSerieModem(listrefrence);
    fiches.setAffecteID(iduser);
    fiches.setAffectequantite(quantite);
    fiches.setRef_fiche("Fiche_" + rand.nextInt()); // set la reference d'une fiche en utilisant
                                                    // random pour assurer
    // l'unicité de ce champs
    fiches.setAffectedBYuser(idconnected);
    return fiches;
  }

  @Override
  public String verificationAffectModem(List<Long> modemIds, String codeUser, Model model,
      HttpServletRequest request, List<String> roles, Long idUserConnected) {

    Optional<User> user = userRepository.findByCodeUser(codeUser);

    if (user.isPresent() && roles.contains("LIST_MODEM_AFFECTED_OTHER")) {
      if (!user.get().getAffectedTo().equals(idUserConnected)) {
        return "USER_NOT_FOUND";
      }
    }

    if (!user.isPresent()) {
      return "USER_NOT_FOUND";

    }

    List<Modem> modemList = getModemsForVerification(roles, idUserConnected, modemIds);

    final List<Modem> modemValid;
    List<Modem> modemNotValid = new ArrayList<>();

    if (modemList.size() == modemIds.size()) {
      modemValid = modemList;
    } else {
      modemValid = modemList.stream().filter(modem -> {
        if (modemIds.contains(modem.getModemId())) {
          modemIds.remove(modem.getModemId());
          return true;
        }
        return false;
      }).collect(Collectors.toList());

      modemNotValid = modemRepository.findAllByModemIdIn(modemIds);
    }

    model.addAttribute("modemValid", modemValid);
    model.addAttribute("nbModemValid", modemValid.size());
    request.getSession().setAttribute("codeUserAffectedModem", codeUser);
    request.getSession().setAttribute("modemIds", modemValid);
    model.addAttribute("modemNotValid", modemNotValid);
    model.addAttribute("nbModemNotValid", modemNotValid.size());

    return "success";

  }

  @Override
  public String confirmAffectModem(Model model, RedirectAttributes redirectAttrs,
      HttpServletRequest request, Long idUserConnected) {
    // TODO Auto-generated method stub

    Optional<User> user = userRepository
        .findByCodeUser(request.getSession().getAttribute("codeUserAffectedModem").toString());
    List<Modem> modems = (List<Modem>) request.getSession().getAttribute("modemIds");

    affecter(modems, user.get(), user.get().getTypeUser(), idUserConnected);
    logger.info(
        "Affecter les modem [" + modems.stream().map(Modem::getModemId).collect(Collectors.toList())
            + "] à user avec id " + user.get().getUserid());

    Random rand = new Random();
    List<String> listrefrence = modemservice.listNumSerie(modems);
    FicheStock fiches =
        getFicheStock(listrefrence, modems.size(), idUserConnected, user.get().getUserid(), rand);
    ficheStockService.saveStock(fiches);

    model.addAttribute("message", "Les modems sont bien affectés au " + user.get().getFirstName()
        + " " + user.get().getLastName() + " avec le code : " + user.get().getCodeUser());

    return "success";
  }

  @Override
  public List<Modem> getModemsForVerification(List<String> roles, Long idUserConnected,
      List<Long> modemIds) {
    // TODO Auto-generated method stub
    List<Modem> modems = new ArrayList<Modem>();
    if (roles.contains("READ_MODEM")) {
      modems = modemRepository.findNonAffectedModemsAdmin(modemIds);

    } else if (roles.contains("READ_MODEM_LIST_AREA")) {
      modems = modemRepository.findNonAffectedModemsByDist(idUserConnected, modemIds);

    } else if (roles.contains("READ_MODEM_POS")) {
      modems = modemRepository.findNonAffectedModemsByPos(idUserConnected, modemIds);

    } else if (roles.contains("READ_MODEM_OWNER")) {
      modems = modemRepository.findNonAffectedModemsByRev(idUserConnected, modemIds);
    }

    return modems;
  }

  @Override
  public String desaffecterMdeom(Long revendeurId, Long distId, Long posId, Long modemId,
      User user) {
    // TODO Auto-generated method stub
    Modem modem = modemRepository.findById(modemId).get();
    if (modem.getAffecteClient() != null) {
      return null;
    }

    if (posId != null) {

      User pos = userRepository.getById(posId);
      String action = "Désaffecter le modem depuis le point de vente " + pos.getLastName() + " / "
          + pos.getCodeUser();
      modemHistoryService.save(action, user, modem);
      modem.setAffectePointdeVente(null);

    } else if (revendeurId != null) {

      User rev = userRepository.getById(revendeurId);
      String action =
          "Désaffecter le modem depuis le revendeur " + rev.getFirstName() + " " + rev.getLastName()
              + " / " + (rev.getNomCommercial() != null ? rev.getNomCommercial() : "") + " / ";
      modemHistoryService.save(action, user, modem);
      modem.setAffecteRevendeur(null);

    } else if (distId != null) {

      User dist = userRepository.getById(distId);
      String action = "Désaffecter le modem depuis le distributeur " + dist.getFirstName() + " "
          + dist.getLastName();
      modemHistoryService.save(action, user, modem);
      modem.setAffecteDistributeur(null);

    }

    modemRepository.save(modem);

    return "success";
  }

  @Override
  public ModemVerificationResult verificationAffectModemRest(List<Long> modemIds, String codeUser,
      List<String> roles, Long idUserConnected) {

    Optional<User> user = userRepository.findByCodeUser(codeUser);

    if (!user.isPresent() || (roles.contains("LIST_MODEM_AFFECTED_OTHER")
        && !user.get().getAffectedTo().equals(idUserConnected))) {
      return new ModemVerificationResult("USER_NOT_FOUND", Collections.emptyList(),
          Collections.emptyList());
    }

    List<Modem> modemList = getModemsForVerification(roles, idUserConnected, modemIds);

    List<Modem> modemValid;
    List<Modem> modemNotValid = new ArrayList<>();

    if (modemList.size() == modemIds.size()) {
      modemValid = modemList;
    } else {
      modemValid = modemList.stream().filter(modem -> {
        if (modemIds.contains(modem.getModemId())) {
          modemIds.remove(modem.getModemId());
          return true;
        }
        return false;
      }).collect(Collectors.toList());

      modemNotValid = modemRepository.findAllByModemIdIn(modemIds);
    }
    return new ModemVerificationResult("SUCCESS", modemValid, modemNotValid);
  }

  @Override
  public String confirmAffectModemRest(List<Modem> modemList, User userConnected,
      String selectedCodeUser) {
    Optional<User> user = userRepository.findByCodeUser(selectedCodeUser);
    affecter(modemList, user.get(), user.get().getTypeUser(), userConnected.getUserid());
    logger.info("Affecter les modem ["
        + modemList.stream().map(Modem::getModemId).collect(Collectors.toList())
        + "] à user avec id " + user.get().getUserid());

    Random rand = new Random();
    List<String> listrefrence = modemservice.listNumSerie(modemList);
    FicheStock fiches = getFicheStock(listrefrence, modemList.size(), userConnected.getUserid(),
        user.get().getUserid(), rand);
    ficheStockService.saveStock(fiches);
    logger.info("message", "Les modems sont bien affectés au " + user.get().getFirstName() + " "
        + user.get().getLastName() + " avec le code : " + user.get().getCodeUser());
    return "SUCCESS";
  }

}
