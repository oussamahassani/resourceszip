package crm.chifco.com.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.Privilege;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AbonnementService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.UserTypeConstant;

@Service("userService")
public class UserServiceImpl implements UserService {
  private static final Logger logger = LogManager.getLogger(CrmUtils.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EncaissementRepository encaissementRepository;
  @Autowired
  private AbonnementService abonnementService;
  @Autowired
  private ModemRepository modemRepository;

  @Autowired
  private EntityManager entityManager;

  @Value("${pathphoto}")
  private String pathphoto;

  @Override
  @Transactional(readOnly = true)
  public User findUsersByEmail(String Email) {
    User user;
    user = userRepository.findUsersByEmail(Email);
    return user;
  }

  public User findUsersByIduser(Long Id) {
    User user = null;
    user = userRepository.findUsersByUserid(Id);
    return user;
  }

  @Override
  public void updateImage(MultipartFile imageFile, String email, String oldimg) throws Exception {

    String folder = pathphoto + email + "/";
    File fileToDelete = new File(pathphoto + email + "/" + oldimg);
    File uploadDir = new File(folder);
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }
    FileSystemUtils.deleteRecursively(fileToDelete);
    byte[] bytes = imageFile.getBytes();
    Path path = Paths.get(folder + CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
    Files.write(path, bytes);
  }

  @Override
  public Page<User> findPaginatedUserByType(int pageNo, int pageSize, String typeUser) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.userRepository.findUserByTypeUser(pageable, typeUser);
  }

  public Model returnInfoUserConnected(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      model.addAttribute("user", user);
      model.addAttribute("authorities", user.getRole().getPrivileges().stream()
              .map(Privilege::getPrivilegeName)
              .collect(Collectors.toList()));

    }
    return model;
  }

  public List<User> findUsersByTypeUser(String typeuser) {
    return this.userRepository.findUsersByTypeUser(typeuser);
  }

  @Override
  public String generateCodeUser(String nameville, String typeUser) {
    // TODO Auto-generated method stub
    String type = "P";
    if (typeUser.equals("REVENDEUR"))
      type = "R";
    long countUser = userRepository.countByGouvernera(nameville) + 1;
    String newcount = String.format("%1$03d", countUser);
    return type + newcount + "-" + nameville;
  }

  @Override
  public Page<User> findPaginatedUserByTypeandCreatedByUserId(Integer pageNo, int pageSize,
      String typeuser, Long userid, String firstName, String lastName, String refUser,
      Long gouvernorat, Long villes, Date dateCreationDebut, Date dateCreationFin,
      Boolean activation, String role) {
    // TODO Auto-generated method stub

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.userRepository.findPaginatedUserByTypeUserAndCreatedByUserId(pageable, typeuser,
        userid, firstName, lastName, refUser, gouvernorat, villes, dateCreationDebut,
        dateCreationFin, activation, role);
  }

  @Override
  public User findTop1UsersByTypeuser(String typeuser) {
    // TODO Auto-generated method stub
    return userRepository.findTop1UsersByTypeuser(typeuser);
  }

  @Override
  public Page<User> findPaginatedUserByTypeAndFirstNameAndLastName(int pageNo, int pageSize,
      String typeUser, String firstName, String lastName, String refUser, Long gouvernorat,
      Long villes, Date datedebut, Date datefin, Long distributeur, Boolean status, String role,
      String classification) {
    // TODO Auto-generated method stub
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

    return this.userRepository.findUserByTypeUserAndFirstNameAndLastName(pageable, typeUser,
        firstName, lastName, refUser, gouvernorat, villes, datedebut, datefin, distributeur, status,
        role, classification);

  }

  @Override
  public Page<User> findPaginatedWithFilter(Integer pageNo, int pageSize, String nom, String prenom,
      String refUser, Long gouvernorat, Long villes, Date dateCreationDebut, Date dateCreationFin,
      Long role) {
    // TODO Auto-generated method stub
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.userRepository.findUsersbynotemailandFilter("SYSTEM", pageable, nom, prenom,
        refUser, gouvernorat, villes, dateCreationDebut, dateCreationFin, role);
  }

  @Override
  public List<User> affectRevendeurgetListeRevendeur(String Recherche, User user) {
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    if (StringsRole.contains("AFFECT_ABONNEMENT_TO_REVENDEUR")) {
      return userRepository.findUserToBeAffectedALL(Recherche);
    } else
      // TODO Auto-generated method stub
      return userRepository.revendeurFindUserToBeAffected(Recherche, user.getUserid(),
          UserTypeConstant.REVENDEUR);
  }

  @Override
  public Boolean affectUser(String userId, Long userIdRow) {
    // TODO Auto-generated method stub
    User userToBeAffected = userRepository.getById(Long.parseLong(userId));
    User userisAffected = userRepository.getById(userIdRow);

    if (userToBeAffected != null && userisAffected != null) {
      userisAffected.setAffectedTo(userToBeAffected.getUserid());
      userRepository.save(userisAffected);
      return true;
    }
    return false;
  }

  @Override
  public List<User> findUsersByTypeUserNotIn(List<String> typeuser) {
    // TODO Auto-generated method stub
    return userRepository.findUsersByTypeUserNotIn(typeuser);
  }

  @Override
  public List<User> findUsersByTypeUserAndRecherche(String distributeur, String recherche) {
    // TODO Auto-generated method stub
    return userRepository.findUsersByTypeUserAndRecherche(distributeur, recherche);
  }

  @Override
  public List<User> findUsersByTypeUserAndAffectedToAndRecherche(String typeuser, Long userid,
      String nom, String prenom, String refUser, Long gouvernorat, Long villes, String datedebut,
      String datefin, Long distributeur) {
    // TODO Auto-generated method stub
    return userRepository.findUsersByTypeUserAndAffectedTo(typeuser, userid);
  }

  @Override
  public List<User> findAllUserByRecherche(String typeuser, String nom, String prenom,
      String refUser, Long gouvernorat, Long villes, String datedebut, String datefin,
      Long distributeur, Boolean activation, Long role, String classification) {
    Date DateDebut = null;
    Date DateFin = null;
    if (datedebut != null && !datedebut.isEmpty()) {
      try {
        datedebut = datedebut + " 00:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateDebut = dateFormat.parse(datedebut);
      } catch (ParseException e) {
        logger.warn("findAllUserByRecherche : " + e.getMessage());
      }
    }
    if (datefin != null && !datefin.isEmpty()) {
      try {
        datefin = datefin + " 23:59:59";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFin = dateFormat.parse(datefin);
      } catch (ParseException e) {
        logger.warn("findAllUserByRecherche : " + e.getMessage());
      }
    }

    if (nom != null && nom.isEmpty()) {
      nom = null;
    }
    if (prenom != null && prenom.isEmpty()) {
      prenom = null;
    }
    if (classification != null && classification.isEmpty()) {
      classification = null;
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

    // TODO Auto-generated method stub
    return userRepository.findAllUserByRecherche(typeuser, nom, prenom, refUser, gouvernorat,
        villes, DateDebut, DateFin, distributeur, activation, role, classification);
  }

  @Override
  public User findTop1UsersByEmail(String email) {
    // TODO Auto-generated method stub
    return userRepository.findTop1UsersByEmail(email);
  }

  @Override
  public Map<String, Object> getRevendeurStats(Long chefsecteurId) {
    Map<String, Object> revendeurActifInactif =
        userRepository.getRevendeurActifInactif(chefsecteurId);
    Map<String, Object> revendeursRetrograde =
        userRepository.getRevendeursRetrograde(chefsecteurId);
    Map<String, Object> topRevByChiffreAffaire =
        encaissementRepository.findTopRevByChiffreAffairParChefSecteur(null, null, chefsecteurId);
    Integer activeRevendeurCount = revendeurActifInactif != null
        ? (Integer) revendeurActifInactif.getOrDefault("activeRevendeurCount", 0)
        : 0;
    Integer inactiveRevendeurCount = revendeurActifInactif != null
        ? (Integer) revendeurActifInactif.getOrDefault("inactiveRevendeurCount", 0)
        : 0;
    Integer revendeursRetrograder = revendeursRetrograde != null
        ? (Integer) revendeursRetrograde.getOrDefault("RevendeursRetrograder", 0)
        : 0;
    String firstname = topRevByChiffreAffaire != null
        ? (String) topRevByChiffreAffaire.getOrDefault("firstname", "")
        : "";
    String lastname = topRevByChiffreAffaire != null
        ? (String) topRevByChiffreAffaire.getOrDefault("lastname", "")
        : "";
    String codeUser = topRevByChiffreAffaire != null
        ? (String) topRevByChiffreAffaire.getOrDefault("code_user", "")
        : "";
    float plafonRevendeur = topRevByChiffreAffaire != null
        ? ((Number) topRevByChiffreAffaire.getOrDefault("plafon_revendeur", 0)).floatValue()
        : 0.0f;
    Boolean enabled = topRevByChiffreAffaire != null
        ? (Boolean) topRevByChiffreAffaire.getOrDefault("enabled", false)
        : false;
    float chiffreAffaire = topRevByChiffreAffaire != null
        ? ((Number) topRevByChiffreAffaire.getOrDefault("chiffre_affaire", 0)).floatValue()
        : 0.0f;
    Map<String, Object> combinedResults = new HashMap<>();
    combinedResults.put("topRevendeurFirstname", firstname);
    combinedResults.put("topRevendeurLastname", lastname);
    combinedResults.put("topRevendeurCodeUser", codeUser);
    combinedResults.put("topRevendeurChiffreAffaire", chiffreAffaire);
    combinedResults.put("activeRevendeurs", activeRevendeurCount);
    combinedResults.put("inactiveRevendeurs", inactiveRevendeurCount);
    combinedResults.put("revendeursRetrograders", revendeursRetrograder);
    // combinedResults.put("topRevendeurPlafonRevendeur", plafonRevendeur);
    // combinedResults.put("topRevendeurEnabled", enabled);


    return combinedResults;
  }

  @Override
  public Map<String, Object> executeDynamicQuery(Boolean isActive, Boolean isNotActive,
      Long chefSecteurId, Boolean retrograde, Long revId) {
    String sqlQuery = buildDynamicQuery(isActive, isNotActive, chefSecteurId, retrograde, revId);
    Query query = entityManager.createNativeQuery(sqlQuery);
    query.setParameter("ChefsecteurId", chefSecteurId);
    query.setParameter("revId", revId);

    List<Object[]> resultList = query.getResultList();
    Map<String, Object> resultMap = new HashMap<>();
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    Date startOfMonth = calendar.getTime();
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    Date endOfMonth = calendar.getTime();
    // nbre d'abonnement ajourdhui
    Map<String, Object> abonnementSummary =
        abonnementService.getAbonnementSummaryForMonth(startOfMonth, endOfMonth, revId);
    Integer activeCountToday = (Integer) abonnementSummary.get("activeCountToday");
    Integer activeCountThisMonth = (Integer) abonnementSummary.get("activeCountThisMonth");
    // nbre de modem en stock
    List<Modem> modems = modemRepository.findAllModemNotAffectedRev(revId);
    List<Modem> modemsnotactiv = modemRepository.findAllModemNotAffectedRevNotActiv(revId);
    Integer modemCount = (modems != null) ? modems.size() : 0;
    Integer modemCountNotactiv = (modemsnotactiv != null) ? modemsnotactiv.size() : 0;
    if (!resultList.isEmpty()) {
      Object[] result = resultList.get(0);
      resultMap.put("montant", formatToThreeDecimalPlaces(result[0]));
      resultMap.put("chiffredaffaire", formatToThreeDecimalPlaces(result[1]));
      resultMap.put("Montantpayéversé", formatToThreeDecimalPlaces(result[2]));
      resultMap.put("montantpayénonversé", formatToThreeDecimalPlaces(result[3]));
      resultMap.put("firstname", result[4]);
      resultMap.put("lastname", result[5]);
      resultMap.put("adresse", result[6]);
      resultMap.put("telephone", castToInt(result[7]));
      resultMap.put("userid", castToInt(result[8]));
      resultMap.put("code_user", result[9]);
      resultMap.put("plafon_revendeur", formatToThreeDecimalPlaces(result[10]));
      resultMap.put("atteint", formatToThreeDecimalPlaces(result[11]));
      resultMap.put("abonnementToDay", activeCountToday);
      resultMap.put("abonnementThisMonth", activeCountThisMonth);
      resultMap.put("modemDisponible", modemCount);
      resultMap.put("modemDisponibleNotActiv", modemCountNotactiv);
    }

    return resultMap;
  }


  private BigDecimal formatToThreeDecimalPlaces(Object value) {
    if (value instanceof Number) {
      BigDecimal decimalValue = new BigDecimal(((Number) value).toString());
      return decimalValue.setScale(3, RoundingMode.HALF_UP);
    }
    return BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
  }

  private Integer castToInt(Object value) {
    if (value instanceof Number) {
      return ((Number) value).intValue();
    } else if (value instanceof String) {
      try {
        return Integer.parseInt((String) value);
      } catch (NumberFormatException e) {
        return 0;
      }
    }
    return 0;
  }

  public String buildDynamicQuery(Boolean isActive, Boolean isNotActive, Long ChefsecteurId,
      Boolean retrograde, Long revId) {
    StringBuilder query = new StringBuilder(
        "SELECT ISNULL(SUM(CASE WHEN f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END), 0) AS montant, "
            + "ISNULL(SUM(CASE WHEN f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END), 0) - "
            + "SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) AS chiffredaffaire, "
            + "SUM(CASE WHEN f.is_chifco_payed = 1 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END) - "
            + "SUM(CASE WHEN f.is_chifco_payed = 1 AND f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) AS Montantpayéversé, "
            + "SUM(CASE WHEN f.is_chifco_payed = 0 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END) - "
            + "SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) + "
            + "SUM(CASE WHEN f.is_chifco_payed = 1 AND f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) AS montantpayénonversé, "
            + "u.first_name AS firstname, u.last_name AS lastname, u.adresse AS adresse, u.telephone, u.userid AS userid, "
            + "u.code_user AS code_user, u.plafon_revendeur AS plafon_revendeur, "
            + "(CASE WHEN u.plafon_revendeur > 0 THEN "
            + "((SUM(CASE WHEN f.is_chifco_payed = 0 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END) "
            + "- SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) "
            + "+ SUM(CASE WHEN f.is_chifco_payed = 1 AND f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END)) * 100.0 / u.plafon_revendeur) "
            + "ELSE 0 END) AS atteint " + "FROM users u "
            + "LEFT JOIN Encaissement f ON u.userid = f.userid "
            + "GROUP BY u.userid, u.code_user, u.first_name, u.last_name, u.adresse, u.telephone, u.plafon_revendeur, "
            + "is_locked, u.role_id, u.typeuser, u.affected_to, u.gouvernorat_id, u.ville_id, u.enabled "
            + "HAVING u.typeuser = 'REVENDEUR' AND u.affected_to = :ChefsecteurId AND u.userid = :revId");

    if (isActive != null) {
      if (isActive) {
        query.append(
            " AND (ISNULL(SUM(CASE WHEN f.facture_id  is not null  THEN f.montant_facture else 0 END), 0) - "
                + "SUM(CASE WHEN f.avoir_client_id is not null THEN f.montant_facture else 0 END))  > 0");
      }
    }
    if (isNotActive != null) {
      if (isNotActive) {
        query.append(
            "  AND (ISNULL(SUM(CASE WHEN f.facture_id  is not null  THEN f.montant_facture else 0 END), 0) -"
                + "SUM(CASE WHEN f.avoir_client_id is not null THEN f.montant_facture else 0 END)) <= 0");
      }
    }
    if (retrograde != null && retrograde) {
      query.append(" AND u.role_id = 2522 AND u.enabled=1 AND is_locked=1 ");
    }
    return query.toString();
  }

  public String buildDynamicQuery2(Boolean isActive, Boolean isNotActive, Long ChefsecteurId,
      Boolean retrograde) {
    StringBuilder query = new StringBuilder(
        "SELECT  u.first_name as firstname, u.last_name as lastname, u.userid as userid,u.code_user as codeuser "
            + " FROM users u LEFT JOIN Encaissement f ON u.userid = f.userid "
            + " GROUP BY u.userid, u.first_name, u.last_name,u.code_user,is_locked, "
            + " u.role_id, u.typeuser, u.affected_to, u.ville_id, u.enabled "
            + " HAVING u.typeuser = 'REVENDEUR' AND u.affected_to = :ChefsecteurId ");
    if (isActive != null) {
      if (isActive) {
        query.append(
            " AND (ISNULL(SUM(CASE WHEN f.facture_id  is not null  THEN f.montant_facture else 0 END), 0) - "
                + "SUM(CASE WHEN f.avoir_client_id is not null THEN f.montant_facture else 0 END))  > 0");
      }
    }
    if (isNotActive != null) {
      if (isNotActive) {
        query.append(
            "  AND (ISNULL(SUM(CASE WHEN f.facture_id  is not null  THEN f.montant_facture else 0 END), 0) -"
                + "SUM(CASE WHEN f.avoir_client_id is not null THEN f.montant_facture else 0 END)) <= 0");
      }
    }
    if (retrograde != null && retrograde) {
      query.append(" AND u.role_id = 2522 AND u.enabled=1 ");
    }
    return query.toString();
  }

  @Override
  public List<Map<String, Object>> executeDynamicQuery2(Boolean isActive, Boolean isNotActive,
      Long chefSecteurId, Boolean retrograde) {
    String sqlQuery = buildDynamicQuery2(isActive, isNotActive, chefSecteurId, retrograde);
    Query query = entityManager.createNativeQuery(sqlQuery);
    query.setParameter("ChefsecteurId", chefSecteurId);
    List<Object[]> resultList = query.getResultList();
    return resultList.stream().map(result -> {
      Map<String, Object> resultMap = new HashMap<>();
      resultMap.put("firstname", result[0]);
      resultMap.put("lastname", result[1]);
      resultMap.put("userid", castToInt(result[2]));
      resultMap.put("codeuser", result[3]);
      return resultMap;
    }).collect(Collectors.toList());
  }

}
