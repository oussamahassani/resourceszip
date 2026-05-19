package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  User findUsersByEmail(String email);

  User findUsersByUserid(Long id);

  // recuperer user par son id
  @Query("select u from User u where u.userid = :x")
  public User findByUserId(@Param("x") Long userid);

  // recuperer liste des users par son role id
  @Query("select u from User u where u.role.roleId = :x and u.createdByUserId = :y")
  public List<User> findByUserIdRole(@Param("x") Long id, @Param("y") Long idconnected);

  @Query("select u from User u where  u.typeUser  = :typeUser  and "
      + "(u.firstName = :firstName or :firstName is null) and "
      + "(u.lastName = :lastName or :lastName is null) "
      + "and (u.codeUser = :refUser or :refUser is null ) and"
      + " (u.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) and "
      + "(u.ville.villeId = :villes or :villes is null ) and"
      + "(u.affectedTo = :distributeur or :distributeur is null )"
      + "and (u.enabled = :status or :status is null )"
      + "and (u.classification = :classification or :classification is null )"
      + "and (u.role.roleName = :roleName or :roleName is null )"
      + "and (u.createdDate >= :datedebut or :datedebut is null ) and (u.createdDate <= :datefin or :datefin is null )"

  )
  Page<User> findUserByTypeUserAndFirstNameAndLastName(Pageable pageable, String typeUser,
      String firstName, String lastName, String refUser, Long gouvernorat, Long villes,
      Date datedebut, Date datefin, Long distributeur, Boolean status, String roleName,
      String classification);

  Page<User> findUserByTypeUser(Pageable pageable, String role);

  List<User> findUsersByTypeUser(String string);

  List<User> findUsersByTypeUserAndAffectedTo(String user, Long affectedTo);

  @Query("select u from User u where  NOT  u.typeUser  = :typeuser "
      + "and (u.firstName = :firstName or :firstName is null) "
      + "and (u.lastName = :lastName or :lastName is null) "
      + "and (u.codeUser = :refUser or :refUser is null ) "
      + "and (u.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and (u.ville.villeId = :villes or :villes is null )"
      + "and (u.role.roleId = :role or :role is null )"
      + "and (u.createdDate >= :datedebut or :datedebut is null ) and (u.createdDate <= :datefin or :datefin is null )")
  Page<User> findUsersbynotemailandFilter(@Param("typeuser") String typeuser, Pageable pageable,
      String firstName, String lastName, String refUser, Long gouvernorat, Long villes,
      Date datedebut, Date datefin, Long role);

  @Query(
      value = "select COALESCE(MAX(CAST(SUBSTRING(code_user, 2, 3) as int)),1) from users u  join gouvernorats gov on gov.gouvernorat_id = u.gouvernorat_id   where SUBSTRING(u.code_user, 6, 3) = :nameville",
      nativeQuery = true)
  long countByGouvernera(String nameville);

  @Query("select u from User u where  u.typeUser  = :typeuser and (u.firstName = :firstName or :firstName is null) and (u.lastName = :lastName or :lastName is null) "
      + "and (u.codeUser = :refUser or :refUser is null ) "
      + "and (u.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and (u.enabled = :activation or :activation is null )"
      + "and (u.role.roleName = :role or :role is null )"
      + "and (u.ville.villeId = :villes or :villes is null ) and (u.createdDate >= :datedebut or :datedebut is null ) and (u.createdDate <= :datefin or :datefin is null ) and u.affectedTo = :userid")
  Page<User> findPaginatedUserByTypeUserAndCreatedByUserId(Pageable pageable, String typeuser,
      Long userid, String firstName, String lastName, String refUser, Long gouvernorat, Long villes,
      Date datedebut, Date datefin, Boolean activation, String role);

  @Query(value = "select TOP 1 * from users u where u.typeuser = :type", nativeQuery = true)
  User findTop1UsersByTypeuser(String type);

  Optional<User> findByCodeUser(String codeUser);

  @Query("SELECT U FROM User U  where  ( U.codeUser = :codeRevendeur or :codeRevendeur is null ) and ( U.identificationFiscale = :identificationFiscale or :identificationFiscale is null ) and ( U.email = :emailRevendeur or :emailRevendeur is null)")
  List<User> findUserByEmailOrEmailOrIdentification(String codeRevendeur, String emailRevendeur,
      String identificationFiscale);

  @Query("SELECT U FROM User U  where  (( U.nomCommercial = :recherche or :recherche is null ) Or ( U.codeUser = :recherche or :recherche is null ) Or ( U.identificationFiscale = :recherche or :recherche is null ) Or ( U.email = :recherche or :recherche is null) Or ( U.cin = :recherche or :recherche is null ) Or ( U.interlocuteur = :recherche or :recherche is null )) and U.affectedTo =:createdBy and U.typeUser = :typeUser")
  List<User> revendeurFindUserToBeAffected(String recherche, Long createdBy, String typeUser);

  @Query("SELECT U FROM User U  where  ( U.nomCommercial = :recherche or :recherche is null ) Or ( U.codeUser = :recherche or :recherche is null ) Or ( U.identificationFiscale = :recherche or :recherche is null ) Or ( U.email = :recherche or :recherche is null) Or ( U.cin = :recherche or :recherche is null ) Or ( U.interlocuteur = :recherche or :recherche is null )")
  List<User> findUserToBeAffectedALL(String recherche);

  List<User> findUsersByTypeUserNotIn(List<String> typeUser);

  List<User> findUsersByAffectedTo(Long userid);

  @Query("SELECT U FROM User U  where  (( U.nomCommercial = :recherche or :recherche is null ) Or ( U.codeUser = :recherche or :recherche is null ) Or ( U.identificationFiscale = :recherche or :recherche is null ) Or ( U.email = :recherche or :recherche is null) Or ( U.cin = :recherche or :recherche is null ) Or ( U.interlocuteur = :recherche or :recherche is null )) and U.typeUser = :typeUser")
  List<User> findUsersByTypeUserAndRecherche(String typeUser, String recherche);

  @Query("SELECT U FROM User U  where  ( U.typeUser = :typeuser or :typeuser is null ) and ( U.firstName = :nom or :nom is null ) and ( U.lastName = :prenom or :prenom is null)"
      + "and ( U.codeUser = :refUser or :refUser is null) and ( U.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) and ( U.ville.villeId = :villes or :villes is null ) "
      + "and ( U.createdDate >= :datedebut or :datedebut is null) and ( U.createdDate <= :datefin or :datefin is null ) and ( U.affectedTo = :distributeur or :distributeur is null ) "
      + "and ( U.enabled = :activation or :activation is null ) and"
      + " ( U.role.roleId = :role or :role is null ) "
      + " and (U.classification = :classification or :classification is null) ")

  List<User> findAllUserByRecherche(String typeuser, String nom, String prenom, String refUser,
      Long gouvernorat, Long villes, Date datedebut, Date datefin, Long distributeur,
      Boolean activation, Long role, String classification);

  @Query(value = "SELECT u FROM User u WHERE u.typeUser IN ('REVENDEUR', 'DISTRIBUTEUR', 'POS')")
  List<User> listUsersDistRevPos();

  User findTop1UsersByEmail(String email);


  Optional<User> findByCodeUserAndAffectedTo(String revendeur, Long userid);


  @Query(
      value = "select * from users u where u.role_id=(select r.role_id from roles r where role_name='ROLE_FINANCE'",
      nativeQuery = true)
  List<User> findAllUserWithFinanceRole();

  @Query(value = "select * from users u where u.typeuser = 'G.STOCK' ", nativeQuery = true)
  List<User> findAllUserWithGStockRole();

  @Query("SELECT u FROM User u WHERE u.role.roleName = :rolename")
  List<User> findUsersByRole(@Param("rolename") String rolename);

  @Query("SELECT u FROM User u WHERE u.role.roleName = :rolename and u.enabled =true ")
  List<User> findEnabledUsersByRole(@Param("rolename") String rolename);

  @Query("SELECT u FROM User u WHERE u.role.roleName IN ('ROLE_SAV', 'ROLE_BO')")
  List<User> findUsersByRoleSavAndBo();

  @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = :rolename AND u.enabled = true AND u.typeUser='REVENDEUR'")
  Long countUsersByRole(@Param("rolename") String rolename);

  @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true AND u.typeUser='REVENDEUR'")
  Long countUsersByIsEnabled();

  @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = :rolename AND u.affectedTo=:userId")
  Long countUsersByRoleForDist(@Param("rolename") String rolename, @Param("userId") Long userId);

  @Query(
      value = "WITH RevendeurStats AS ( SELECT u.userid, u.first_name AS firstname,u.last_name AS lastname, "
          + " u.adresse AS adresse, u.code_user AS code_user, u.enabled,u.is_locked, u.role_id, "
          + " u.plafon_revendeur AS plafon_revendeur, "
          + " ISNULL(SUM(CASE WHEN f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END), 0) "
          + " - SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) AS chiffre_daffaire "
          + " FROM users u  LEFT JOIN Encaissement f ON u.userid = f.userid "
          + " LEFT JOIN factures fact ON fact.facture_id = f.facture_id WHERE u.typeuser = 'REVENDEUR' AND u.affected_to = :chefsecteurId and u.enabled=1 "
          + " AND (u.role_id = (SELECT role_id FROM roles WHERE role_name = 'ROLE_REVENDEUR') "
          + " OR u.role_id = (SELECT role_id FROM roles WHERE role_name = 'ROLE_REVENDEUR_DESACTIVE')) GROUP BY "
          + " u.userid, u.first_name, u.last_name, u.adresse, u.code_user, u.plafon_revendeur, u.enabled, u.is_locked, u.role_id ) SELECT COUNT(DISTINCT CASE "
          + " WHEN chiffre_daffaire IS NOT NULL AND chiffre_daffaire != 0  THEN userid ELSE NULL  END) AS activeRevendeurCount, "
          + " COUNT(DISTINCT CASE WHEN chiffre_daffaire IS NULL OR chiffre_daffaire = 0 "
          + " THEN userid ELSE NULL END) AS inactiveRevendeurCount FROM RevendeurStats",
      nativeQuery = true)
  Map<String, Object> getRevendeurActifInactif(Long chefsecteurId);

  @Query(
      value = "select count(*) AS RevendeursRetrograder from users u  where u.enabled=1 and "
          + " role_id=2522 AND u.affected_to = :chefsecteurId and u.typeuser='REVENDEUR' ",
      nativeQuery = true)
  Map<String, Object> getRevendeursRetrograde(Long chefsecteurId);

  @Query(
      value = "select TOP 1 * from users u where u.telephone = :telephone AND u.typeuser=:type order by created_date desc ",
      nativeQuery = true)
  User findTop1UsersByTelephone(String telephone, String type);

  @Query(
      value = "select TOP 1 * from users u where u.userid = :userid AND u.typeuser=:type order by created_date desc ",
      nativeQuery = true)
  User findTop1UsersByUserid(Long userid, String type);


  @Query("SELECT u FROM User u JOIN u.role r JOIN r.privileges p WHERE p.privilegeName = :privilegeName")
  List<User> findUsersByPrivilegeName(@Param("privilegeName") String privilegeName);


  List<User> findUsersByClassification(@Param("classfication") String classfication);

  @Query(
      value = "select TOP 1 * from users u where u.telephone = :telephone  order by created_date desc ",
      nativeQuery = true)
  User findTop1UsersByTelephoneNETYACTION(String telephone);

}
