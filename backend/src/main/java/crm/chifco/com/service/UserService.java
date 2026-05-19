package crm.chifco.com.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import crm.chifco.com.model.User;

public interface UserService {
  User findUsersByEmail(String Email);

  User findUsersByIduser(Long Iduser);

  List<User> findUsersByTypeUser(String typeuser);

  Page<User> findPaginatedUserByType(int pageNo, int pageSize, String typeUser);

  Page<User> findPaginatedUserByTypeAndFirstNameAndLastName(int pageNo, int pageSize,
      String typeUser, String firstName, String lastName, String RefUser, Long gouvernorat,
      Long villes, Date datedebut, Date datefin, Long distributeur, Boolean status, String role,
      String Classification);

  public void updateImage(MultipartFile imageFile, String email, String oldimg) throws Exception;

  public Model returnInfoUserConnected(Model model);

  public String generateCodeUser(String nameville, String typeUser);

  Page<User> findPaginatedUserByTypeandCreatedByUserId(Integer pageNo, int pageSize, String string,
      Long userid, String firstName, String lastName, String refUser, Long gouvernorat, Long villes,
      Date dateCreationDebut, Date dateCreationFin, Boolean activation, String role);

  User findTop1UsersByTypeuser(String typeuser);

  Page<User> findPaginatedWithFilter(Integer pageNo, int pageSize, String nom, String prenom,
      String refUser, Long gouvernorat, Long villes, Date dateCreationDebut, Date dateCreationFin,
      Long role);

  List<User> affectRevendeurgetListeRevendeur(String Recherche, User user);

  Boolean affectUser(String string, Long parseLong);

  List<User> findUsersByTypeUserNotIn(List<String> typeuser);

  List<User> findUsersByTypeUserAndRecherche(String distributeur, String string);

  List<User> findUsersByTypeUserAndAffectedToAndRecherche(String string, Long userid, String nom,
      String prenom, String refUser, Long gouvernorat, Long villes, String datedebut,
      String datefin, Long distributeur);

  List<User> findAllUserByRecherche(String typeuser, String nom, String prenom, String refUser,
      Long gouvernorat, Long villes, String datedebut, String datefin, Long distributeur,
      Boolean activation, Long role, String classification);

  User findTop1UsersByEmail(String string);

  Map<String, Object> getRevendeurStats(Long chefsecteurId);

  Map<String, Object> executeDynamicQuery(Boolean isActive, Boolean isNotActive, Long chefSecteurId,
      Boolean retrograde, Long revId);

  List<Map<String, Object>> executeDynamicQuery2(Boolean isActive, Boolean isNotActive,
      Long chefSecteurId, Boolean retrograde);

}
