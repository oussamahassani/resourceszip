package crm.chifco.com.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.ModemAccess;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.ModemAccessRepository;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.ModemHistoryService;
import crm.chifco.com.service.ModemService;
import crm.chifco.com.templateclasse.AdminModem;
import crm.chifco.com.templateclasse.ModemDistributeur;
import crm.chifco.com.templateclasse.ModemEtatStockDist;
import crm.chifco.com.templateclasse.ModemEtatStockRev;
import crm.chifco.com.templateclasse.ModemRevendeur;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.TypeAbonnment;

@Service
@Transactional
public class ModemServiceImpl implements ModemService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  @Autowired
  private ModemRepository modemRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ModemHistoryService modemHistoryService;

  @Autowired
  private CategorieProduitInternetRepository categorieProduitInternetRepository;

  @Autowired
  private AbonnementRepository abonnementRepository;

  @Autowired
  private ModemAccessRepository modemAccessRepository;

  public ModemServiceImpl(ModemRepository modemRepo) {
    this.modemRepository = modemRepo;
  }

  public Map<String, Object> filtre(String filterrecherche) {

    Map<String, Object> filters = new HashMap<>();

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);

      String numSerie = obj.optString("numSerie", null);
      if (numSerie != null && !numSerie.isEmpty()) {
        filters.put("numSerie", numSerie.trim());
      }

      String affetedUser = obj.optString("affetedUser", null);
      if (affetedUser != null && !affetedUser.isEmpty()) {
        Optional<User> user = userRepository.findByCodeUser(affetedUser.trim());
        if (user.isPresent()) {
          filters.put("affetedUser", user.get().getUserid());
        } else {
          filters.put("affetedUser", 0L);
        }
      }

      String type = obj.optString("type", null);
      if (type != null && !type.isEmpty()) {
        filters.put("type", type.trim());
      }

      String numSerieDebut = obj.optString("numSerieDebut", null);
      if (numSerieDebut != null && !numSerieDebut.isEmpty()) {
        filters.put("numSerieDebut", numSerieDebut.trim());
      }

      String numSerieFin = obj.optString("numSerieFin", null);
      if (numSerieFin != null && !numSerieFin.isEmpty()) {
        filters.put("numSerieFin", numSerieFin.trim());
      }

      String statutStr = obj.optString("statut", null);
      if (statutStr != null && !statutStr.isEmpty()) {
        Boolean statut = statutStr.trim().equals("0") ? false : true;
        filters.put("statut", statut);
      }

      String login = obj.optString("login", null);
      if (login != null && !login.isEmpty()) {
        filters.put("login", login);
      }

      String dateCreation = obj.optString("dateCreation", null);
      if (dateCreation != null && !dateCreation.isEmpty()) {
        filters.put("dateCreation", dateCreation);
      }

      String etatStr = obj.optString("etat", null);
      if (etatStr != null && !etatStr.isEmpty()) {
        Boolean etat = etatStr.trim().equals("true") ? true : false;
        filters.put("etat", etat);
      }
    }

    return filters;
  }

  @Override
  public Page<AdminModem> findPaginatedModemAdmin(int pageNo, int pageSize,
      String filterrecherche) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

    Map<String, Object> filtersMap = filtre(filterrecherche);

    String numSerie =
        filtersMap.containsKey("numSerie") ? filtersMap.get("numSerie").toString() : null;
    Long affetedUser =
        filtersMap.containsKey("affetedUser") ? (Long) filtersMap.get("affetedUser") : null;
    String type = filtersMap.containsKey("type") ? filtersMap.get("type").toString() : null;
    String numSerieDebut =
        filtersMap.containsKey("numSerieDebut") ? filtersMap.get("numSerieDebut").toString() : null;
    String numSerieFin =
        filtersMap.containsKey("numSerieFin") ? filtersMap.get("numSerieFin").toString() : null;
    Boolean statut = filtersMap.containsKey("statut") ? (Boolean) filtersMap.get("statut") : null;
    Boolean etat = filtersMap.containsKey("etat") ? (Boolean) filtersMap.get("etat") : null;
    String login = filtersMap.containsKey("login") ? filtersMap.get("login").toString() : null;
    String dateCreationDebut = filtersMap.containsKey("dateCreation")
        ? filtersMap.get("dateCreation").toString() + " 00:00:00.000"
        : null;
    String dateCreationFin = filtersMap.containsKey("dateCreation")
        ? filtersMap.get("dateCreation").toString() + " 23:59:59.999"
        : null;

    return modemRepository.paginatelistmodemtoAdmin(pageable, numSerie, affetedUser, type,
        numSerieDebut, numSerieFin, login, statut, dateCreationDebut, dateCreationFin, etat);

  }

  @Override
  public Page<ModemRevendeur> modemPosFindPaginated(Long idpos, int pageNo, int pageSize,
      String filterrecherche) {
    PageRequest paging = PageRequest.of(pageNo - 1, pageSize);

    Map<String, Object> filtersMap = filtre(filterrecherche);

    String numSerie =
        filtersMap.containsKey("numSerie") ? filtersMap.get("numSerie").toString() : null;
    String type = filtersMap.containsKey("type") ? filtersMap.get("type").toString() : null;
    String numSerieDebut =
        filtersMap.containsKey("numSerieDebut") ? filtersMap.get("numSerieDebut").toString() : null;
    String numSerieFin =
        filtersMap.containsKey("numSerieFin") ? filtersMap.get("numSerieFin").toString() : null;
    Boolean statut = filtersMap.containsKey("statut") ? (Boolean) filtersMap.get("statut") : null;
    Boolean etat = filtersMap.containsKey("etat") ? (Boolean) filtersMap.get("etat") : null;

    return modemRepository.paginatelistmodemtoPos(idpos, paging, numSerie, type, statut,
        numSerieDebut, numSerieFin, etat);

  }

  @Override
  public Page<ModemDistributeur> modemDistFindPaginated(Long iddist, int pageNo, int pageSize,
      String filterrecherche) {
    PageRequest paging = PageRequest.of(pageNo - 1, pageSize);

    Map<String, Object> filtersMap = filtre(filterrecherche);

    String numSerie =
        filtersMap.containsKey("numSerie") ? filtersMap.get("numSerie").toString() : null;
    Long affetedUser =
        filtersMap.containsKey("affetedUser") ? (Long) filtersMap.get("affetedUser") : null;
    String type = filtersMap.containsKey("type") ? filtersMap.get("type").toString() : null;
    String numSerieDebut =
        filtersMap.containsKey("numSerieDebut") ? filtersMap.get("numSerieDebut").toString() : null;
    String numSerieFin =
        filtersMap.containsKey("numSerieFin") ? filtersMap.get("numSerieFin").toString() : null;
    Boolean statut = filtersMap.containsKey("statut") ? (Boolean) filtersMap.get("statut") : null;
    Boolean etat = filtersMap.containsKey("etat") ? (Boolean) filtersMap.get("etat") : null;

    return modemRepository.paginatelistmodemtoDist(iddist, paging, numSerie, type, affetedUser,
        numSerieDebut, numSerieFin, statut, etat);

  }

  @Override
  public Page<ModemRevendeur> modemRevFindPaginated(Long idrev, int pageNo, int pageSize,
      String filterrecherche) {
    PageRequest paging = PageRequest.of(pageNo - 1, pageSize);

    Map<String, Object> filtersMap = filtre(filterrecherche);

    String numSerie =
        filtersMap.containsKey("numSerie") ? filtersMap.get("numSerie").toString() : null;
    String type = filtersMap.containsKey("type") ? filtersMap.get("type").toString() : null;
    String numSerieDebut =
        filtersMap.containsKey("numSerieDebut") ? filtersMap.get("numSerieDebut").toString() : null;
    String numSerieFin =
        filtersMap.containsKey("numSerieFin") ? filtersMap.get("numSerieFin").toString() : null;
    Boolean statut = filtersMap.containsKey("statut") ? (Boolean) filtersMap.get("statut") : null;
    Boolean etat = filtersMap.containsKey("etat") ? (Boolean) filtersMap.get("etat") : null;


    return modemRepository.paginatelistmodemtoRevn(idrev, paging, numSerie, type, statut,
        numSerieDebut, numSerieFin, etat);

  }

  // ******************************************** reuperer un prdouit par son id

  @Override
  public Modem getmodemById(Long id) throws Exception {
    Optional<Modem> modem = modemRepository.findById(id); // essayer de recuperer un optional objet
                                                          // (modem) en
    // appelelnt la methode findById qui retourne un objet de
    // type optional

    if (modem.isPresent()) { // si le modem existe.
      return modem.get(); // retourner le modem
    } else {
      throw new Exception("No Product record exist for given id"); // si non retourne une exception
    }
  }

  // *************************************************** mettre à jour ou ajouter modem

  @Override
  public Boolean createOrUpdatemodem(Modem modem, User user) // une methode determine le type de la
  // fonctionnalité à traiter
  // (creation ou modification) en verifiant l'existance de l'id du
  // modem dans la BDD
  {
    if (modem.getModemId() == null) // si l'id du modem inexistant (null)
    {
      // List<Modem> findexisteSerie =
      // modemRepository.findListModemByNumSerieOrEmail(modem.getNumSerie(), modem.getEmail());
      // if (findexisteSerie.size() == 0) {
      modemHistoryService.save("Creation", user, modem);
      modem = modemRepository.save(modem);
      return true;
      // } else // donc on va enregistrer directement le nouveau modem
      // return false;
    } else {
      Optional<Modem> prod = modemRepository.findById(modem.getModemId()); // si l'id est existe :
                                                                           // recuperer le
      // modem pour qu'on peut faire set de
      // ses attributs (modification)

      if (prod.isPresent()) {
        Modem newmodem = new Modem();
        newmodem.setModemId(modem.getModemId());
        newmodem.setModelModem(modem.getModelModem());
        newmodem.setNumSerie(modem.getNumSerie());
        newmodem.setMarque(modem.getMarque());
        newmodem.setEmail(modem.getEmail());
        newmodem.setPassword(modem.getPassword());
        newmodem.setAdresseMac(prod.get().getAdresseMac());
        newmodem.setStatus(prod.get().getStatus());
        newmodem.setAffecteClient(prod.get().getAffecteClient());
        newmodem.setAffectePointdeVente(prod.get().getAffectePointdeVente());
        newmodem.setAffecteDistributeur(prod.get().getAffecteDistributeur());
        newmodem.setAffecteRevendeur(prod.get().getAffecteRevendeur());
        newmodem.setStatutReservation(prod.get().getStatutReservation());
        newmodem.setCreatedDate(prod.get().getCreatedDate());

        Boolean verif = true;
        if (!modem.getEmail().equals(prod.get().getEmail())
            || (!modem.getEmail().equals(prod.get().getEmail()))) {
          modemRepository.findListModemByNumSerieOrEmail(modem.getNumSerie(), modem.getEmail());
        }

        if (!verif) {
          return false;
        }
        modemHistoryService.editHistory(prod.get(), newmodem, user);
        newmodem = modemRepository.save(newmodem);

        return true;
      }

      else {
        Modem findexisteSerie =
            modemRepository.findByNumSerieOrEmail(modem.getNumSerie(), modem.getEmail());
        if (findexisteSerie == null) {
          modem = modemRepository.save(modem);
          modemHistoryService.save("Creation", user, modem);
          return true;
        }

        else {
          return false;
        }

      }
    }
  }

  // ***********************************************methode qui retourne le nombre total des modems
  // dans la BDD

  // *********************** cette methode consiste à recuperer la liste des reference d'une liste
  // des modems

  @Override
  public List<String> listNumSerie(List<Modem> modems) {
    // on a besoin de cette liste pour creer une fiche de stock lors d'une
    // affectation
    List<String> numSerie = new ArrayList<String>();

    for (Modem modem : modems) {
      numSerie.add(modem.getNumSerie());
    }
    return numSerie;
  }

  @Override
  public List<Long> getAllModemIds(String filterrecherche, List<String> StringsRole,
      User userConnected) {
    // TODO Auto-generated method stub
    Map<String, Object> filtersMap = filtre(filterrecherche);

    String numSerie =
        filtersMap.containsKey("numSerie") ? filtersMap.get("numSerie").toString() : null;
    Long affetedUser =
        filtersMap.containsKey("affetedUser") ? (Long) filtersMap.get("affetedUser") : null;
    String type = filtersMap.containsKey("type") ? filtersMap.get("type").toString() : null;
    String numSerieDebut =
        filtersMap.containsKey("numSerieDebut") ? filtersMap.get("numSerieDebut").toString() : null;
    String numSerieFin =
        filtersMap.containsKey("numSerieFin") ? filtersMap.get("numSerieFin").toString() : null;
    Boolean statut = filtersMap.containsKey("statut") ? (Boolean) filtersMap.get("statut") : null;
    Boolean etat = filtersMap.containsKey("etat") ? (Boolean) filtersMap.get("etat") : null;
    String login = filtersMap.containsKey("login") ? filtersMap.get("login").toString() : null;
    Date dateCreationDebut = filtersMap.containsKey("dateCreation")
        ? CrmUtils.convertStringToDate(filtersMap.get("dateCreation").toString())
        : null;
    Date dateCreationFin = filtersMap.containsKey("dateCreation")
        ? CrmUtils.convertStringToLocalDateTime(filtersMap.get("dateCreation").toString())
        : null;

    Boolean isAdmin = StringsRole.contains("READ_MODEM");
    Boolean isDistributeur = StringsRole.contains("READ_MODEM_LIST_AREA");
    Boolean isRevendeur = StringsRole.contains("READ_MODEM_OWNER");
    Boolean isPos = StringsRole.contains("READ_MODEM_POS");

    Boolean isExport = StringsRole.contains("EXPORT_MODEM");
    Boolean isAffected = StringsRole.contains("LIST_MODEM_AFFECTED_ADMIN")
        || StringsRole.contains("LIST_MODEM_AFFECTED_OTHER");

    if (isAdmin && (isExport || isAffected)) {
      return modemRepository.getAllModemIdsAdmin(numSerie, type, affetedUser, numSerieDebut,
          numSerieFin, statut, login, dateCreationDebut, dateCreationFin, etat);
    } else if (!isAdmin && (isAffected || isExport)) {

      if (isDistributeur) {
        return modemRepository.getAllModemIdsDist(userConnected.getUserid(), numSerie, type,
            affetedUser, numSerieDebut, numSerieFin, statut);
      } else if (isRevendeur) {
        return modemRepository.getAllModemIdsRev(userConnected.getUserid(), numSerie, type,
            numSerieDebut, numSerieFin, statut);
      } else if (isPos) {
        return modemRepository.getAllModemIdsPos(userConnected.getUserid(), numSerie, type,
            numSerieDebut, numSerieFin, statut);
      }

    }
    return null;
  }

  @Override
  public Page<ModemEtatStockDist> etatStockDist(int pageNo, int pageSize, String filterrecherche) {
    // TODO Auto-generated method stub
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

    String distributeur = null;
    Integer max = null;
    Integer min = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("distributeur"), "")
          && obj.getString("distributeur") != null) {
        distributeur = obj.getString("distributeur").trim();
      }
      if (!Objects.equals(obj.getString("max"), "") && obj.getString("max") != null) {
        max = obj.getInt("max");
      }
      if (!Objects.equals(obj.getString("min"), "") && obj.getString("min") != null) {
        min = obj.getInt("min");
      }
    }

    return modemRepository.etatStockDist(pageable, distributeur, max, min);
  }

  @Override
  public Page<ModemEtatStockRev> etatStockRev(int pageNo, int pageSize, List<String> StringsRole,
      Long idConnected, String filterrecherche) {
    // TODO Auto-generated method stub
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

    String revendeur = null;
    Integer max = null;
    Integer min = null;
    Long gouvernorat = null;
    Long distributeur = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("revendeur"), "") && obj.getString("revendeur") != null) {
        revendeur = obj.getString("revendeur").trim();
      }
      if (!Objects.equals(obj.getString("max"), "") && obj.getString("max") != null) {
        max = obj.getInt("max");
      }
      if (!Objects.equals(obj.getString("min"), "") && obj.getString("min") != null) {
        min = obj.getInt("min");
      }
      if (!Objects.equals(obj.getString("gouvernorat"), "")
          && obj.getString("gouvernorat") != null) {
        gouvernorat = obj.getLong("gouvernorat");
      }
      if (!Objects.equals(obj.getString("distributeur"), "")
          && obj.getString("distributeur") != null) {
        distributeur =
            userRepository.findByCodeUser(obj.getString("distributeur")).get().getUserid();
      }
    }

    Long usreId = null;

    if (StringsRole.contains("READ_MODEM_LIST_AREA")) {
      usreId = idConnected;
    }

    return modemRepository.etatStockRev(pageable, usreId, revendeur, min, max, gouvernorat,
        distributeur);
  }

  @Override
  public List<String> getAllCodeUserStock(String filterrecherche, String type, User userConnected) {
    // TODO Auto-generated method stub
    Map<String, Object> filtersMap = filtre(filterrecherche);
    List<String> StringsRole =
        userConnected.getRole().getStringsRole(userConnected.getRole().getPrivileges());

    String userCode = null;
    Integer max = null;
    Integer min = null;
    Long gouvernorat = null;
    Long distributeur = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("userCode"), "") && obj.getString("userCode") != null) {
        userCode = obj.getString("userCode").trim();
      }
      if (!Objects.equals(obj.getString("max"), "") && obj.getString("max") != null) {
        max = obj.getInt("max");
      }
      if (!Objects.equals(obj.getString("min"), "") && obj.getString("min") != null) {
        min = obj.getInt("min");
      }

      if (type.equals("rev")) {
        if (!Objects.equals(obj.getString("gouvernorat"), "")
            && obj.getString("gouvernorat") != null) {
          gouvernorat = obj.getLong("gouvernorat");
        }
        if (!Objects.equals(obj.getString("distributeur"), "")
            && obj.getString("distributeur") != null) {
          distributeur =
              userRepository.findByCodeUser(obj.getString("distributeur")).get().getUserid();
        }

      }
    }
    if (type.equals("dist")) {
      return modemRepository.getListCodesUserDist(userCode, max, min);
    } else if (type.equals("rev")) {
      if (StringsRole.contains("READ_MODEM")) {
        return modemRepository.getListCodesUserRev(userCode, max, min, gouvernorat, null,
            distributeur);
      } else if (StringsRole.contains("READ_MODEM_LIST_AREA")) {
        return modemRepository.getListCodesUserRev(userCode, max, min, gouvernorat,
            userConnected.getUserid(), null);
      }
    }
    return null;

  }

  @Override
  public Map<String, List<Modem>> getDetailsStockDist(Long idUser) {
    // TODO Auto-generated method stub
    List<Modem> modems = modemRepository.getDetailsStockDist(idUser);

    List<Modem> modemsADSL = new ArrayList<>();
    List<Modem> modemsVDSL = new ArrayList<>();
    List<Modem> modemsGPON = new ArrayList<>();

    List<Modem> modemsXDSL = new ArrayList<>();

    for (Modem modem : modems) {
      if (modem.getModelModem().equals("ADSL")) {
        modemsADSL.add(modem);
      } else if (modem.getModelModem().equals("VDSL")) {
        modemsVDSL.add(modem);
      } else if (modem.getModelModem().equals("GPON")) {
        modemsGPON.add(modem);
      } else if (modem.getModelModem().equals("XDSL")) {
        modemsXDSL.add(modem);
      }
    }

    Map<String, List<Modem>> res = new HashMap<>();
    res.put("modemsADSL", modemsADSL);
    res.put("modemsVDSL", modemsVDSL);
    res.put("modemsGPON", modemsGPON);
    res.put("modemsXDSL", modemsXDSL);

    return res;
  }


  @Override
  public Map<String, List<Modem>> getDetailsStockRev(Long idUser) {
    // TODO Auto-generated method stub
    List<Modem> modems = modemRepository.getDetailsStockRev(idUser);

    List<Modem> modemsADSL = new ArrayList<>();
    List<Modem> modemsVDSL = new ArrayList<>();
    List<Modem> modemsGPON = new ArrayList<>();
    List<Modem> modemsXDSL = new ArrayList<>();

    for (Modem modem : modems) {
      if (modem.getModelModem().equals("ADSL")) {
        modemsADSL.add(modem);
      } else if (modem.getModelModem().equals("VDSL")) {
        modemsVDSL.add(modem);
      } else if (modem.getModelModem().equals("GPON")) {
        modemsGPON.add(modem);
      } else if (modem.getModelModem().equals("XDSL")) {
        modemsXDSL.add(modem);
      }
    }

    Map<String, List<Modem>> res = new HashMap<>();
    res.put("modemsADSL", modemsADSL);
    res.put("modemsVDSL", modemsVDSL);
    res.put("modemsGPON", modemsGPON);
    res.put("modemsXDSL", modemsXDSL);

    return res;
  }

  @Override
  public crm.chifco.com.ApiDTO.getLoginAndPassswordModem getLoginAndPassswordModem(
      String numSerie) {
    // TODO Auto-generated method stub
    return modemRepository.getLoginAndPassswordModemByNumSerie(numSerie);
  }

  @Override
  public List<Modem> getListeModemsDisponiblesByUser(String codeProduit, User user) {
    // TODO Auto-generated method stub

    List<String> roles = user.getRole().getStringsRole(user.getRole().getPrivileges());
    List<Modem> modems = new ArrayList<Modem>();

    if (roles.contains("READ_MODEM")) {
      modems = modemRepository.findModemsNotAffectedAdmin(codeProduit);

    } else if (roles.contains("READ_MODEM_LIST_AREA")) {
      modems = modemRepository.findModemNotAffectedDistributeur(user.getUserid(), codeProduit);

    } else if (roles.contains("READ_MODEM_POS")) {
      modems = modemRepository.findModemNotAffectedPos(user.getUserid(), codeProduit);

    } else if (roles.contains("READ_MODEM_OWNER")) {
      modems = modemRepository.findModemNotAffectedRev(user.getUserid(), codeProduit);
    }

    return modems;
  }

  @Override
  public String changerStatus(Long modemId, User user, String newEmail, String newPassword,
      Boolean keepCredentials, String commentaire) {
    // TODO Auto-generated method stub
    LOGGER.info(
        "Début de la méthode changerStatus avec les paramètres : modemId={}, user={}, newEmail={}, newPassword={}, garder la méme email={}, commentaire={}",
        modemId, user.getUserid(), newEmail, newPassword, keepCredentials, commentaire);


    if (modemId == null) {
      LOGGER.error("modemId est null. La méthode changerStatus ne peut pas continuer.");
      return null;
    }
    Modem modem = modemRepository.getById(modemId);

    if (modem.getStatus()) {
      // keepCredentials : garder la méme emai et mot de passe si keepCredentials = true
      if (keepCredentials == true) {
        List<Modem> verifModem = modemRepository.findAllByEmail(modem.getEmail());
        if (verifModem.size() > 1) {
          LOGGER.error("Il existe plus d'un modem avec le même email. Impossible de continuer.");
          return "MODEM_EMAIL_EXISTS";
        } else {
          LOGGER.info("Sauvegarde de l'historique du modem avec conservation des identifiants.");
          String action =
              "Ce modem est passé à l'état actif en conservant les mêmes identifiants et mot de passe";
          modemHistoryService.save(action, user, modem);
        }
      } else {

        LOGGER.info(
            "garder la mémee email est false. Vérification de l'existence de modems avec le nouvel email.");
        List<Modem> verifModem = modemRepository.findAllByEmail(newEmail);

        if (verifModem.size() > 0) {
          LOGGER.error("Il existe déjà un modem avec le nouvel email.");
          return "MODEM_EMAIL_EXISTS";
        } else {
          LOGGER.info("Sauvegarde de l'historique du modem avec changement d'identifiants.");
          String action = "Le modem a été activé avec un changement de login de " + modem.getEmail()
              + " à " + newEmail + ", ainsi qu'un changement de mot de passe de "
              + modem.getPassword() + " à " + newPassword;
          modemHistoryService.save(action, user, modem);

          modem.setEmail(newEmail);
          modem.setPassword(newPassword);
        }
      }
    } else {
      LOGGER.info("Le modem est inactif. Sauvegarde de l'historique avec commentaire.");
      String action = "Le modem a été désactivé en raison de : " + commentaire;
      modemHistoryService.save(action, user, modem);
    }

    modem.setStatus(modem.getStatus() ? false : true);
    modemRepository.save(modem);


    LOGGER.info("Fin de la méthode changerStatus.");
    return "STATUS_CHANGE_SUCCESSFUL";
  }


  @Override
  public List<Modem> getAllModemsAvailableByType(String type) {
    // TODO Auto-generated method stub
    List<Modem> listemodem = modemRepository.findAllModemsDisponible(type);
    if (listemodem.size() > 0) {
      return listemodem;
    } else if(!type.equals(TypeAbonnment.SIM) || !type.equals(TypeAbonnment.Box)) {
      listemodem = modemRepository.findAllModemsDisponible("XDSL");
      return listemodem;
    }
    else
    return listemodem ;	
  }

  @Override
  public String controleParental(String telephoneFix, boolean activer) {
    LOGGER.info("Début de la méthode controleParental avec telephoneFix: {} et activer: {}", telephoneFix, activer);

    // Validation du paramètre telephoneFix
    if (telephoneFix == null || telephoneFix.length() != 8 || !telephoneFix.matches("\\d{8}")) {
      LOGGER.error("Le numéro de téléphone fixe doit contenir exactement 8 chiffres");
      return "INVALID_TELEPHONE_FIX";
    }

    Long telFixe = Long.parseLong(telephoneFix);

    // Recherche de l'abonnement par téléphone fixe
    Abonnement abonnement = abonnementRepository.findTopByTelFixeOrderByCreatedDateDesc(telFixe);
    if (abonnement == null) {
      LOGGER.error("Aucun abonnement trouvé pour le téléphone fixe: {}", telFixe);
      return "ABONNEMENT_NOT_FOUND";
    }

    // Vérification si l'abonnement a un modem affecté
    if (abonnement.getModem() == null) {
      LOGGER.error("L'abonnement n'a pas de modem affecté");
      return "NO_MODEM_ASSIGNED";
    }

    Modem modem = abonnement.getModem();

    if(modem.getModelModem().equals("ADSL") || modem.getModelModem().equals("XDSL")||modem.getModelModem().equals("VDSL") ) {
    	

    if (activer) {
      // Activation du contrôle parental
      if (modem.getLoginControleParental() != null && modem.getControleParentaleActiver()  ) {
        LOGGER.info("Contrôle parental déjà activé pour le modem: {}", modem.getModemId());
        return "CONTROLE_PARENTAL_DEJA_ACTIVE";
      }

      // Détermination du type de modem (adl ou vdsl)
      String modelModem = modem.getModelModem();
      String typeModem = modelModem != null && abonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().contains("ADSL") ? "ADSL" : "VDSL";

      // Recherche d'un ModemAccess disponible
    

      // Attribution des credentials
      modem.setLoginControleParental(modem.getEmail().replaceAll("\\.[^.]+$", ".org"));
      modem.setControleParentaleActiver(true);
   

      modemRepository.save(modem);
      abonnement.setLoginModem(modem.getEmail().replaceAll("\\.[^.]+$", ".org"));
      abonnementRepository.save(abonnement);
      LOGGER.info("Contrôle parental activé avec succès pour le modem: {}", modem.getModemId());
      return "CONTROLE_PARENTAL_ACTIVE";
    } else {
      // Désactivation du contrôle parental
      if (modem.getControleParentaleActiver() != null &&  !modem.getControleParentaleActiver()) {
        LOGGER.info("Contrôle parental déjà désactivé pour le modem: {}", modem.getModemId());
        return "CONTROLE_PARENTAL_DEJA_DESACTIVE";
      }

      // Remettre le ModemAccess à disposition si nécessaire
      if (modem.getControleParentaleActiver()) {
    	   modem.setControleParentaleActiver(false);

    	      modemRepository.save(modem);
      }

      // Réinitialiser les champs
   

      LOGGER.info("Contrôle parental désactivé avec succès pour le modem: {}", modem.getModemId());
      return "CONTROLE_PARENTAL_DESACTIVE";
    }
    }
    else {
    	  return "CONTROLE_PARENTAL_TYPE_MODEM_MUST_BE_ADSL_OR_VDSL";
    }
  }

}
