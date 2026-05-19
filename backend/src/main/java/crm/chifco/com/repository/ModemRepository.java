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
import org.springframework.stereotype.Repository;
import crm.chifco.com.ApiDTO.getLoginAndPassswordModem;
import crm.chifco.com.model.Modem;
import crm.chifco.com.templateclasse.AdminModem;
import crm.chifco.com.templateclasse.ExportModem;
import crm.chifco.com.templateclasse.ModemDistributeur;
import crm.chifco.com.templateclasse.ModemEtatStockDist;
import crm.chifco.com.templateclasse.ModemEtatStockRev;
import crm.chifco.com.templateclasse.ModemRevendeur;

@Repository
public interface ModemRepository extends JpaRepository<Modem, Long> {
  Modem findModemBymodemId(Long modemId);

  Modem findByNumSerieOrEmail(String numSerie, String email);

  List<Modem> findListModemByNumSerieOrEmail(String numSerie, String email);

  @Query(value = "select m FROM  Modem m WHERE " + "m.numSerie = :numSerie "
      + "AND affectePointdeVente IS NULL " + "AND affecteDistributeur IS NULL "
      + "AND affecteRevendeur IS NULL "
      + "AND affecteClient IS NULL AND (modelModem = :codeProduit or :codeProduit is null ) AND status = false")
  Optional<Modem> findModemByNumSerieOptionalAdmin(String numSerie, String codeProduit);

  @Query(value = "select m FROM  Modem m WHERE " + "m.numSerie = :numSerie "
      + "AND affectePointdeVente IS NULL " + "AND affecteDistributeur = :idConnected "
      + "AND affecteRevendeur IS NULL "
      + "AND affecteClient IS NULL AND ( modelModem = :codeProduit or :codeProduit is null) AND status = false")
  Optional<Modem> findModemByNumSerieOptionalDist(String numSerie, Long idConnected,
      String codeProduit);

  @Query(value = "select m FROM  Modem m WHERE "
      + "m.numSerie = :numSerie AND affecteRevendeur = :idConnected "
      + " AND ( modelModem = :codeProduit or :codeProduit is null ) AND affecteClient IS NULL AND status = false")
  Optional<Modem> findModemByNumSerieOptionalRev(String numSerie, Long idConnected,
      String codeProduit);

  @Query(value = "select m FROM  Modem m WHERE " + "m.numSerie = :numSerie "
      + "AND affectePointdeVente = :idConnected " + "AND affecteDistributeur IS NULL "
      + "AND affecteRevendeur IS NULL "
      + "AND affecteClient IS NULL AND modelModem = :codeProduit AND status = false")
  Optional<Modem> findModemByNumSerieOptionalPos(String numSerie, Long idConnected,
      String codeProduit);

  @Query(value = "select * from modem " + "where affecte_client IS NULL "
      + "and affecte_distributeur IS NULL " + "and affecte_pointde_vente IS NULL "
      + "and affecte_revendeur IS NULL and (model_modem = :codeProduit or model_modem = 'XDSL') "
      + "and status = 0", nativeQuery = true)
  List<Modem> findModemsNotAffectedAdmin(String codeProduit);

  @Query(
      value = "select * from modem " + "where affecte_client IS NULL "
          + "and affecte_distributeur IS NULL " + "and affecte_pointde_vente IS NULL "
          + "and affecte_revendeur IS NULL and (model_modem = :codeProduit ) " + "and status = 0",
      nativeQuery = true)
  List<Modem> findModemsNotAffectedAdminBYcategory(String codeProduit);

  @Query(value = "select * from modem " + "where affecte_client IS NULL "
      + "and affecte_distributeur IS NULL and affecte_pointde_vente = :idPos and ( model_modem = :codeProduit or model_modem = 'XDSL') "
      + "and status = 0", nativeQuery = true)

  List<Modem> findModemNotAffectedPos(Long idPos, String codeProduit);

  @Query(value = "select * from modem " + "where affecte_client IS NULL "
      + "and affecte_distributeur IS NULL and affecte_pointde_vente = :idPos and ( model_modem = :codeProduit ) "
      + "and status = 0", nativeQuery = true)
  List<Modem> findModemNotAffectedPosBYcategory(Long idPos, String codeProduit);

  @Query(value = "select * from modem " + "where affecte_client IS NULL "
      + "and affecte_pointde_vente IS NULL "
      + "and affecte_revendeur = :IDrevendeur and ( model_modem = :codeProduit or model_modem = 'XDSL') "
      + "and status = 0", nativeQuery = true)
  List<Modem> findModemNotAffectedRev(Long IDrevendeur, String codeProduit);

  @Query(value = "select * from modem " + "where affecte_client IS NULL "
      + "and affecte_pointde_vente IS NULL "
      + "and affecte_revendeur = :IDrevendeur and ( model_modem = :codeProduit ) "
      + "and status = 0", nativeQuery = true)
  List<Modem> findModemNotAffectedRevBYcategory(Long IDrevendeur, String codeProduit);

  @Query(value = "select * from modem "
      + "where affecte_client IS NULL and affecte_revendeur IS NULL"
      + " and affecte_distributeur = :idDistributeur  and  ( model_modem = :codeProduit  or model_modem = 'XDSL')"
      + "and status = 0", nativeQuery = true)
  List<Modem> findModemNotAffectedDistributeur(Long idDistributeur, String codeProduit);

  @Query(
      value = "select * from modem " + "where affecte_client IS NULL and affecte_revendeur IS NULL"
          + " and affecte_distributeur = :idDistributeur  and  ( model_modem = :codeProduit )"
          + "and status = 0",
      nativeQuery = true)
  List<Modem> findModemNotAffectedDistributeurBYcategory(Long idDistributeur, String codeProduit);

  // recuperer la liste des Modems existes dans la BDD dont leus chmaps de
  // suppression sont false
  @Query("SELECT p FROM Modem p where p.status = false")
  public List<Modem> getListmodem();

  // recuperer la liste des marques dasn la BDD en ignornant les duplications
  @Query("SELECT DISTINCT p.marque FROM Modem p")
  public List<String> listmarque();

  // recuperer la liste des Modems affecte au revendeur selon un model specifié
  @Query("SELECT p FROM Modem p where p.modelModem like :x and p.affectePointdeVente is null and p.affecteRevendeur =:z and p.affecteClient is null and p.status = false and p.statutReservation is  null")
  public List<Modem> listmodemRevendeur(@Param("x") String model, @Param("z") Long idConnected);

  @Query(value = "select abn.first_name as nomClient," + "abn.reference_client as referenceClient,"
      + "affecte_revendeur as affecterevendeur," + "marque as marque ,"
      + "model_modem as modelModem," + "num_serie as numSerie , status as status "
      + ",modem_id as modem_id,revendeur.code_user as code_userRv ,"
      + " revendeur.first_name as first_nameRv , revendeur.nom_commercial as nomCommercial "
      + "from modem " + " left join Users revendeur on revendeur.userid =modem.affecte_revendeur "
      + " left join abonnement abn on abn.clientid =modem.affecte_client"
      + " where ((affecte_revendeur in (select userid from users where affected_to = :idConnected) OR (affecte_distributeur = :idConnected ))"
      + "and (num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
      + "and (affecte_revendeur = :affetedUser or :affetedUser is null)"
      + "and (model_modem = :type or :type is null)"
      + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
      + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
      + "and (status = :etat or :etat is null) "
      + " and (:statut IS NULL OR ((affecte_client IS NOT NULL or affecte_pointde_vente IS NOT NULL or affecte_revendeur IS NOT NULL)AND affecte_distributeur = :idConnected  AND :statut = 1) "
      + "OR (affecte_client IS NULL and affecte_distributeur = :idConnected and affecte_pointde_vente IS NULL and affecte_revendeur IS NULL AND :statut = 0)"
      + ")" + ")",
      countQuery = "select count(*) from modem "
          + " left join Users revendeur on revendeur.userid =modem.affecte_revendeur "
          + " left join abonnement abn on abn.clientid =modem.affecte_client"
          + " where ((affecte_revendeur in (select userid from users where affected_to = :idConnected) OR (affecte_distributeur = :idConnected ))"
          + "and (num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
          + "and (affecte_revendeur = :affetedUser or :affetedUser is null)"
          + "and (model_modem = :type or :type is null)"
          + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
          + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
          + "and (status = :etat or :etat is null) "
          + " and (:statut IS NULL OR ((affecte_client IS NOT NULL or affecte_pointde_vente IS NOT NULL or affecte_revendeur IS NOT NULL)AND affecte_distributeur = :idConnected  AND :statut = 1) "
          + "OR (affecte_client IS NULL and affecte_distributeur = :idConnected and affecte_pointde_vente IS NULL and affecte_revendeur IS NULL AND :statut = 0)"
          + ")" + ")",
      nativeQuery = true)
  public Page<ModemDistributeur> paginatelistmodemtoDist(Long idConnected, Pageable pageable,
      String numSerie, String type, Long affetedUser, String numSerieDebut, String numSerieFin,
      Boolean statut, Boolean etat);

  @Query(
      value = "select model_modem as modelModem , num_serie as numSerie , marque as Marque , abn.reference_client as referenceClient"
          + ", abn.first_name as nomClient , modem_id as modem_id, status as status "
          + " from modem m left join abonnement abn on abn.clientid = m.affecte_client where affecte_pointde_vente = :idConnected "
          + "and (num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
          + "and (model_modem = :type or :type is null)"
          + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
          + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
          + "and (status = :etat or :etat is null) "
          + " and (:statut IS NULL OR ((affecte_client IS NOT NULL or affecte_revendeur IS NOT NULL) AND affecte_pointde_vente = :idConnected  AND :statut = 1) "
          + "OR (affecte_client IS NULL and affecte_distributeur IS NULL and affecte_revendeur IS NULL and affecte_pointde_vente = :idConnected AND :statut = 0)"
          + ")",
      countQuery = "select count(*)"
          + " from modem m left join abonnement abn on abn.clientid = m.affecte_client where affecte_pointde_vente = :idConnected "
          + "and (num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
          + "and (model_modem = :type or :type is null)"
          + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
          + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
          + "and (status = :etat or :etat is null) "
          + " and (:statut IS NULL OR ((affecte_client IS NOT NULL or affecte_revendeur IS NOT NULL) AND affecte_pointde_vente = :idConnected  AND :statut = 1) "
          + "OR (affecte_client IS NULL and affecte_distributeur IS NULL and affecte_revendeur IS NULL and affecte_pointde_vente = :idConnected AND :statut = 0)"
          + ")",
      nativeQuery = true)
  public Page<ModemRevendeur> paginatelistmodemtoPos(Long idConnected, Pageable pageable,
      String numSerie, String type, Boolean statut, String numSerieDebut, String numSerieFin,
      Boolean etat);

  @Query(
      value = "select model_modem as modelModem , num_serie as numSerie , marque as Marque , abn.reference_client as referenceClient"
          + ", abn.first_name as nomClient , modem_id as modem_id, status as status "
          + " from modem m left join abonnement abn on abn.clientid = m.affecte_client where affecte_revendeur = :idConnected "
          + "and (num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
          + "and (model_modem = :type or :type is null)"
          + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
          + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
          + "and (status = :etat or :etat is null) "
          + " and (:statut IS NULL OR (affecte_client IS NOT NULL   AND :statut = 1) "
          + "OR (affecte_client IS NULL  AND :statut = 0)" + ")",
      countQuery = "select count(*)"
          + " from modem m left join abonnement abn on abn.clientid = m.affecte_client where affecte_revendeur = :idConnected "
          + "and (num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
          + "and (model_modem = :type or :type is null)"
          + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
          + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
          + "and (status = :etat or :etat is null) "
          + " and (:statut IS NULL OR (affecte_client IS NOT NULL   AND :statut = 1) "
          + "OR (affecte_client IS NULL  AND :statut = 0)" + ")",
      nativeQuery = true)
  public Page<ModemRevendeur> paginatelistmodemtoRevn(Long idConnected, Pageable pageable,
      String numSerie, String type, Boolean statut, String numSerieDebut, String numSerieFin,
      Boolean etat);

  @Query(value = "select abn.first_name as nomClient," + "abn.reference_client as referenceClient,"
      + "status as status, affecte_distributeur as affectedistributeur,CONVERT(varchar,modem.created_date, 105) as created_date ,CONVERT(varchar,modem.modified_date, 105) as modified_date,"
      + "affecte_pointde_vente as affectepointdevente," + "affecte_revendeur as affecterevendeur,"
      + "marque as marque ," + "model_modem as modelModem," + "num_serie as numSerie "
      + ",modem_id as modem_id,revendeur.code_user as code_userRv , "
      + " revendeur.first_name as first_nameRv , revendeur.nom_commercial as nomCommercial , "
      + " distributeur.first_name as first_nameDistributeur," + "pos.code_user as codePos ,"
      + "distributeur.code_user as code_userDistributeur" + " from modem "
      + " left join Users revendeur on revendeur.userid =modem.affecte_revendeur "
      + " left join Users distributeur on distributeur.userid =modem.affecte_distributeur "
      + " left join Users pos on pos.userid =modem.affecte_pointde_vente "
      + " left join abonnement abn on abn.clientid =modem.affecte_client"
      + " where ((num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
      + " and (affecte_distributeur = :affetedUser OR affecte_pointde_vente = :affetedUser OR affecte_Revendeur = :affetedUser OR affecte_client = :affetedUser or :affetedUser is null)"
      + "and (model_modem = :type or :type is null)"
      + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
      + "and ( modem.created_date  >= CAST(:dateCreation AS datetime2)  or :dateCreation is null ) "
      + "and ( modem.created_date  <= CAST(:dateCreationFin AS datetime2)  or :dateCreationFin is null ) "
      + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
      + "and (modem.email = :login or :login is null) "
      + "and (modem.status = :etat or :etat is null) "
      + " and (:statut IS NULL OR ((affecte_client IS NOT NULL or affecte_distributeur IS NOT NULL or affecte_pointde_vente IS NOT NULL or affecte_revendeur IS NOT NULL) AND :statut = 1) "
      + "OR (affecte_client IS NULL and affecte_distributeur IS NULL and affecte_pointde_vente IS NULL and affecte_revendeur IS NULL AND :statut = 0)"
      + ")" + ")",
      countQuery = "select count(*) from modem "
          + " left join Users revendeur on revendeur.userid =modem.affecte_revendeur "
          + " left join Users distributeur on distributeur.userid =modem.affecte_distributeur "
          + " left join abonnement abn on abn.clientid =modem.affecte_client"
          + " where ((num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
          + " and (affecte_distributeur = :affetedUser OR affecte_pointde_vente = :affetedUser OR affecte_Revendeur = :affetedUser OR affecte_client = :affetedUser or :affetedUser is null)"
          + "and (model_modem = :type or :type is null)"
          + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
          + "and ( modem.created_date  >= CAST(:dateCreation AS datetime2)  or :dateCreation is null ) "
          + "and ( modem.created_date  <= CAST(:dateCreationFin AS datetime2)  or :dateCreationFin is null ) "
          + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
          + "and (modem.email = :login or :login is null) "
          + "and (modem.status = :etat or :etat is null) "
          + " and (:statut IS NULL OR ((affecte_client IS NOT NULL or affecte_distributeur IS NOT NULL or affecte_pointde_vente IS NOT NULL or affecte_revendeur IS NOT NULL) AND :statut = 1) "
          + "OR (affecte_client IS NULL and affecte_distributeur IS NULL and affecte_pointde_vente IS NULL and affecte_revendeur IS NULL AND :statut = 0)"
          + ")" + ")",
      nativeQuery = true)
  Page<AdminModem> paginatelistmodemtoAdmin(@Param("pageable") Pageable pageable, String numSerie,
      Long affetedUser, String type, String numSerieDebut, String numSerieFin, String login,
      Boolean statut, String dateCreation, String dateCreationFin, Boolean etat);

  @Query("SELECT m FROM Modem m WHERE m.affectePointdeVente IS NULL AND m.affecteDistributeur IS NULL AND m.affecteRevendeur IS NULL AND m.affecteClient IS NULL AND modemId IN :modemIds AND m.status = false")
  List<Modem> findNonAffectedModemsAdmin(List<Long> modemIds);

  @Query("SELECT m FROM Modem m WHERE m.affectePointdeVente IS NULL AND m.affecteDistributeur = :idDist AND m.affecteRevendeur IS NULL AND m.affecteClient IS NULL AND modemId IN :modemIds AND m.status = false")
  List<Modem> findNonAffectedModemsByDist(Long idDist, List<Long> modemIds);

  @Query("SELECT m FROM Modem m WHERE m.affectePointdeVente IS NULL AND m.affecteRevendeur = :idRev AND m.affecteClient IS NULL AND modemId IN :modemIds AND m.status = false")
  List<Modem> findNonAffectedModemsByRev(Long idRev, List<Long> modemIds);

  @Query("SELECT m FROM Modem m WHERE m.affectePointdeVente = :idPos AND m.affecteDistributeur IS NULL AND m.affecteRevendeur IS NULL AND m.affecteClient IS NULL AND modemId IN :modemIds AND m.status = false")
  List<Modem> findNonAffectedModemsByPos(Long idPos, List<Long> modemIds);

  List<Modem> findAllByModemIdIn(List<Long> modemIds);

  @Query(
      value = "select status, num_serie , model_modem , marque , m.email , m.password , CONVERT(varchar,m.created_date, 105) as dateCreation , CONVERT(varchar,m.modified_date, 105) as dateModification , "
          + "CASE WHEN m.affecte_distributeur IS NULL THEN '' ELSE CONCAT(afDist.first_name, ' ', afDist.last_name,' ( ',afDist.code_user,' )') END AS affecte_distributeur ,"
          + "CASE WHEN m.affecte_revendeur IS NULL THEN '' ELSE CONCAT(afRev.first_name, ' ', afRev.last_name,' ( ',afRev.code_user,' )') END AS affecte_revendeur ,"
          + "CASE WHEN m.affecte_pointde_vente IS NULL THEN '' ELSE afPos.code_user END AS affecte_Pos ,"
          + "CASE WHEN m.affecte_client IS NULL THEN '' ELSE CONCAT(afClient.first_name, ' ', afClient.last_name,' ( ',afClient.reference_client,' )') END AS affecte_client "
          + "from modem m left join users afDist on m.affecte_distributeur=afDist.userid "
          + "left join users afRev on m.affecte_revendeur=afRev.userid "
          + "left join users afPos on m.affecte_pointde_vente=afPos.userid "
          + "left join abonnement afClient on m.affecte_client=afClient.clientid where m.modem_id IN :modemIds",
      nativeQuery = true)
  List<ExportModem> findAllByModemIdInToExport(List<Long> modemIds);

  @Query("SELECT m.modemId FROM Modem m WHERE "
      + "(m.numSerie LIKE CONCAT('%'+ :numSerie+ '%') OR :numSerie IS NULL) "
      + "AND (m.modelModem = :type OR :type IS NULL) "
      + "AND (:numSerieDebut IS NULL OR m.numSerie >= :numSerieDebut) "
      + "AND (:numSerieFin IS NULL  OR m.numSerie <= :numSerieFin) "
      + "AND (email = :login OR :login IS NULL)"
      + "AND (createdDate  >= :dateCreationDebut or :dateCreationDebut is null ) "
      + "AND (createdDate  <= :dateCreationFin or :dateCreationFin is null ) "
      + "AND (status = :etat or :etat IS NULL) "
      + "AND (m.affecteDistributeur = :affetedUser OR m.affectePointdeVente = :affetedUser OR m.affecteRevendeur = :affetedUser OR m.affecteClient = :affetedUser OR :affetedUser IS NULL) "
      + "AND (:statut IS NULL OR ((m.affecteClient IS NOT NULL OR m.affecteDistributeur IS NOT NULL OR m.affectePointdeVente IS NOT NULL OR m.affecteRevendeur IS NOT NULL) AND :statut = true) "
      + "OR (m.affecteClient IS NULL AND m.affecteDistributeur IS NULL AND m.affectePointdeVente IS NULL AND m.affecteRevendeur IS NULL AND :statut = false))")
  public List<Long> getAllModemIdsAdmin(String numSerie, String type, Long affetedUser,
      String numSerieDebut, String numSerieFin, Boolean statut, String login,
      Date dateCreationDebut, Date dateCreationFin, Boolean etat);

  @Query("SELECT m.modemId FROM Modem m WHERE "
      + "(m.numSerie LIKE CONCAT('%'+ :numSerie+ '%') OR :numSerie IS NULL)"
      + "AND (m.modelModem = :type OR :type IS NULL) "
      + "AND (:numSerieDebut IS NULL OR m.numSerie >= :numSerieDebut) "
      + "AND (m.affecteRevendeur IN (SELECT u.userid FROM User u WHERE u.affectedTo = :idConnected) OR m.affecteDistributeur = :idConnected)"
      + "AND (:numSerieFin IS NULL  OR m.numSerie <= :numSerieFin) "
      + "AND (m.affecteDistributeur = :affetedUser OR m.affectePointdeVente = :affetedUser OR m.affecteRevendeur = :affetedUser OR m.affecteClient = :affetedUser OR :affetedUser IS NULL) "
      + "AND (:statut IS NULL OR ((m.affecteClient IS NOT NULL OR m.affectePointdeVente IS NOT NULL OR m.affecteRevendeur IS NOT NULL)  AND :statut = true) "
      + "OR (m.affecteClient IS NULL AND m.affecteDistributeur = :idConnected AND m.affectePointdeVente IS NULL AND m.affecteRevendeur IS NULL AND  :statut = false))")
  public List<Long> getAllModemIdsDist(Long idConnected, String numSerie, String type,
      Long affetedUser, String numSerieDebut, String numSerieFin, Boolean statut);

  @Query("SELECT m.modemId FROM Modem m WHERE "
      + "(m.numSerie LIKE CONCAT('%'+ :numSerie+'%') OR :numSerie IS NULL) "
      + "AND (m.modelModem = :type OR :type IS NULL) " + "AND (affecteRevendeur = :idConnected)"
      + "AND (:numSerieDebut IS NULL OR m.numSerie >= :numSerieDebut) "
      + "AND (:numSerieFin IS NULL  OR m.numSerie <= :numSerieFin) "
      + "AND (:statut IS NULL OR (m.affecteClient IS NOT NULL AND :statut = true) OR (m.affecteClient IS NULL AND :statut = false))")
  public List<Long> getAllModemIdsRev(Long idConnected, String numSerie, String type,
      String numSerieDebut, String numSerieFin, Boolean statut);

  @Query("SELECT m.modemId FROM Modem m WHERE "
      + "(m.numSerie LIKE CONCAT('%'+ :numSerie+ '%') OR :numSerie IS NULL) "
      + "AND (m.modelModem = :type OR :type IS NULL) " + "AND (affectePointdeVente = :idConnected)"
      + "AND (:numSerieDebut IS NULL OR m.numSerie >= :numSerieDebut) "
      + "AND (:numSerieFin IS NULL  OR m.numSerie <= :numSerieFin) "
      + "AND (:statut IS NULL OR (m.affecteClient IS NOT NULL AND :statut = true) OR (m.affecteClient IS NULL AND :statut = false))")
  public List<Long> getAllModemIdsPos(Long idConnected, String numSerie, String type,
      String numSerieDebut, String numSerieFin, Boolean statut);

  @Query("SELECT m FROM Modem m WHERE numSerie IN :listNumSerie OR email IN :login")
  List<Modem> findByListNumSerieAndLogin(List<String> listNumSerie, List<String> login);



  @Query(
      value = "SELECT DISTINCT u.code_user AS codeUser,  u.userid AS userId , u.first_name AS firstName, u.last_name AS lastName,"
          + "  (SELECT COUNT(*) FROM modem WHERE affecte_distributeur = u.userid) AS nb_modems_distributeur,"
          + "  (SELECT COUNT(*) FROM modem WHERE affecte_distributeur = u.userid and (affecte_revendeur IS NOT NULL OR affecte_client IS NOT NULL)) AS nb_modems_affectes,"
          + " (SELECT count(*) FROM modem where affecte_distributeur = u.userid AND affecte_revendeur IS NULL AND affecte_client IS NULL) AS nb_modems_disponible"
          + " FROM users u LEFT JOIN modem m ON u.userid = m.affecte_distributeur WHERE "
          + "u.typeuser = 'DISTRIBUTEUR' "
          + "AND (u.code_user = :distributeur OR :distributeur IS NULL) "
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_distributeur = u.userid and affecte_revendeur IS NULL AND affecte_client IS NULL) >= :min OR :min IS NULL) "
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_distributeur = u.userid and affecte_revendeur IS NULL AND affecte_client IS NULL) <= :max OR :max IS NULL)",
      countQuery = "SELECT count(DISTINCT code_user) "
          + " FROM users u LEFT JOIN modem m ON u.userid = m.affecte_distributeur WHERE "
          + "u.typeuser = 'DISTRIBUTEUR' "
          + "AND (u.code_user = :distributeur OR :distributeur IS NULL)"
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_distributeur = u.userid and affecte_revendeur IS NULL AND affecte_client IS NULL) >= :min OR :min IS NULL) "
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_distributeur = u.userid and affecte_revendeur IS NULL AND affecte_client IS NULL) <= :max OR :max IS NULL)",
      nativeQuery = true)
  Page<ModemEtatStockDist> etatStockDist(Pageable pageable, String distributeur, Integer max,
      Integer min);

  @Query(
      value = "SELECT DISTINCT u.code_user AS codeUser, u.userid AS userId , u.first_name AS firstName, u.last_name AS lastName, u.nom_commercial AS nomCommercial,"
          + "  (SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid) AS nb_modems_affectes,"
          + "  (SELECT COUNT(*) FROM modem WHERE  affecte_revendeur = u.userid AND affecte_client IS NOT NULL) AS nb_modems_client,"
          + " (SELECT count(*) FROM modem where affecte_revendeur = u.userid AND affecte_client IS NULL) AS nb_modems_disponible"
          + " FROM users u LEFT JOIN modem m ON u.userid = m.affecte_revendeur WHERE u.typeuser = 'REVENDEUR' "
          + "AND (u.userid IN (SELECT u.userid FROM users u WHERE u.affected_to = :idConnected) OR :idConnected IS NULL) "
          + "AND (u.code_user = :revendeur OR :revendeur IS NULL)"
          + "AND (u.affected_to = :distributeur OR :distributeur IS NULL)"
          + "AND (u.gouvernorat_id = :gouvernorat OR :gouvernorat IS NULL)"
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid AND affecte_client IS NULL) >= :min OR :min IS NULL)"
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid AND affecte_client IS NULL) <= :max OR :max IS NULL)",
      countQuery = "SELECT count(*) FROM users u LEFT JOIN modem m ON u.userid = m.affecte_distributeur WHERE u.typeuser = 'REVENDEUR'"
          + " AND (u.userid IN (SELECT u.userid FROM users u WHERE u.affected_to = :idConnected) OR :idConnected IS NULL)"
          + "AND (u.code_user = :revendeur OR :revendeur IS NULL)"
          + "AND (u.affected_to = :distributeur OR :distributeur IS NULL)"
          + "AND (u.gouvernorat_id = :gouvernorat OR :gouvernorat IS NULL)"
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid AND affecte_client IS NULL) >= :min OR :min IS NULL)"
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid AND affecte_client IS NULL) <= :max OR :max IS NULL)",
      nativeQuery = true)
  Page<ModemEtatStockRev> etatStockRev(Pageable pageable, Long idConnected, String revendeur,
      Integer min, Integer max, Long gouvernorat, Long distributeur);

  @Query(
      value = "SELECT DISTINCT u.code_user AS codeUser, u.first_name AS firstName, u.last_name AS lastName,"
          + "  (SELECT COUNT(*) FROM modem WHERE affecte_distributeur = u.userid) AS nb_modems_distributeur,"
          + " (SELECT count(*) FROM modem where affecte_distributeur = u.userid AND affecte_revendeur IS NULL AND affecte_client IS NULL) AS nb_modems_disponible,"
          + "  (SELECT COUNT(*) FROM modem WHERE affecte_distributeur = u.userid and (affecte_revendeur IS NOT NULL OR affecte_client IS NOT NULL)) AS nb_modems_affectes"
          + " FROM users u LEFT JOIN modem m ON u.userid = m.affecte_distributeur WHERE "
          + "u.typeuser = 'DISTRIBUTEUR' AND (u.code_user IN :distributeurs)",
      nativeQuery = true)
  List<ModemEtatStockDist> exportEtatStockDist(List<String> distributeurs);

  @Query(
      value = "SELECT DISTINCT u.code_user AS codeUser, u.first_name AS firstName, u.last_name AS lastName , u.nom_commercial AS nomCommercial,"
          + "  (SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid) AS nb_modems_affectes,"
          + "  (SELECT COUNT(*) FROM modem WHERE  affecte_revendeur = u.userid AND affecte_client IS NOT NULL) AS nb_modems_client,"
          + " (SELECT count(*) FROM modem where affecte_revendeur = u.userid AND affecte_client IS NULL) AS nb_modems_disponible "
          + "FROM users u LEFT JOIN modem m ON u.userid = m.affecte_revendeur WHERE u.typeuser = 'REVENDEUR' "
          + "AND (u.code_user IN :revendeurs)",
      nativeQuery = true)
  List<ModemEtatStockRev> exportEtatStockRev(List<String> revendeurs);

  @Query(
      value = "SELECT DISTINCT u.code_user FROM users u LEFT JOIN modem m ON u.userid = m.affecte_distributeur WHERE "
          + "u.typeuser = 'DISTRIBUTEUR' "
          + "AND (u.code_user = :distributeur OR :distributeur IS NULL) "
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_distributeur = u.userid and affecte_revendeur IS NULL AND affecte_client IS NULL) >= :min OR :min IS NULL) "
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_distributeur = u.userid and affecte_revendeur IS NULL AND affecte_client IS NULL) <= :max OR :max IS NULL)",
      nativeQuery = true)
  List<String> getListCodesUserDist(String distributeur, Integer max, Integer min);

  @Query(value = "SELECT DISTINCT u.code_user "
      + "FROM users u LEFT JOIN modem m ON u.userid = m.affecte_revendeur WHERE u.typeuser = 'REVENDEUR' "
      + "AND (u.userid IN (SELECT u.userid FROM users u WHERE u.affected_to = :idConnected) OR :idConnected IS NULL) "
      + "AND (u.code_user = :revendeur OR :revendeur IS NULL)"
      + "AND (u.affected_to = :distributeur OR :distributeur IS NULL)"
      + "AND (u.gouvernorat_id = :gouvernorat OR :gouvernorat IS NULL)"
      + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid AND affecte_client IS NULL) >= :min OR :min IS NULL)"
      + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid AND affecte_client IS NULL) <= :max OR :max IS NULL)",
      nativeQuery = true)
  List<String> getListCodesUserRev(String revendeur, Integer max, Integer min, Long gouvernorat,
      Long idConnected, Long distributeur);

  @Query(
      value = "select * from modem where" + "  affecte_revendeur IS NULL and affecte_client is null"
          + "  and affecte_distributeur = :id",
      nativeQuery = true)
  List<Modem> getDetailsStockDist(Long id);

  @Query(value = "select * from modem where affecte_client is null and affecte_revendeur = :id",
      nativeQuery = true)
  List<Modem> getDetailsStockRev(Long id);

  @Query(value = "select m from Modem m where numSerie IN :listNumSerie")
  List<Modem> findByListNumSerie(List<String> listNumSerie);

  @Query("SELECT  new crm.chifco.com.ApiDTO.getLoginAndPassswordModem(a.password,a.email,a.loginControleParental,a.modelModem , a.controleParentaleActiver ) from Modem a where a.numSerie = :numSerie")
  getLoginAndPassswordModem getLoginAndPassswordModemByNumSerie(String numSerie);

  @Query(
      value = "select * from modem where affecte_client IS NULL and model_modem = :codeProduit and status = 0",
      nativeQuery = true)
  List<Modem> findAllModemsDisponible(String codeProduit);

  Modem findAllByNumSerie(String numSerie);

  List<Modem> findAllByEmail(String email);


  @Query("SELECT m FROM Modem m WHERE m.modemId = :modemId AND m.status = false AND m.affecteClient IS NULL AND m.affecteRevendeur = :userconnected")
  Optional<Modem> findActiveModemByIdAndRevendeur(@Param("modemId") Long modemId,
      @Param("userconnected") Long userconnected);

  @Query("SELECT m FROM Modem m WHERE m.modemId = :modemId AND m.status = false AND m.affecteClient IS NULL AND m.affectePointdeVente = :userconnected AND "
      + " m.affecteRevendeur IS NULL AND m.affecteDistributeur IS NULL ")
  Optional<Modem> findActiveModemByIdAndPOS(@Param("modemId") Long modemId,
      @Param("userconnected") Long userconnected);

  @Query("SELECT m FROM Modem m WHERE m.modemId = :modemId AND m.status = false AND m.affecteClient IS NULL AND m.affecteDistributeur = :userconnected AND "
      + " m.affecteRevendeur IS NULL AND m.affectePointdeVente IS NULL")
  Optional<Modem> findActiveModemByIdAndChefSecteur(@Param("modemId") Long modemId,
      @Param("userconnected") Long userconnected);

  /***** Admin all modems */////
  @Query(value = "select * from modem " + "where affecte_client IS NULL "
      + "and affecte_distributeur IS NULL " + "and affecte_pointde_vente IS NULL "
      + "and affecte_revendeur IS NULL  " + "and status = 0", nativeQuery = true)
  List<Modem> findAllModemsNotAffectedAdmin();

  /********* Revendeur all modems ***************/
  @Query(value = "select * from modem " + "where affecte_client IS NULL "
      + "and affecte_pointde_vente IS NULL " + "and affecte_revendeur = :IDrevendeur  "
      + "and status = 0", nativeQuery = true)
  List<Modem> findAllModemNotAffectedRev(Long IDrevendeur);

  @Query(value = "select * from modem " + "where affecte_client IS NULL "
      + "and affecte_pointde_vente IS NULL " + "and affecte_revendeur = :IDrevendeur  "
      + "and status = 1", nativeQuery = true)
  List<Modem> findAllModemNotAffectedRevNotActiv(Long IDrevendeur);

  /*********** Distributeur all modems ****************/
  @Query(
      value = "select * from modem " + "where affecte_client IS NULL and affecte_revendeur IS NULL"
          + " and affecte_distributeur = :idDistributeur   " + "and status = 0",
      nativeQuery = true)
  List<Modem> findAllModemNotAffectedDistributeur(Long idDistributeur);

  @Query(value = "select * from modem " + "where affecte_client IS NULL "
      + "and affecte_distributeur IS NULL and affecte_pointde_vente = :idPos  " + "and status = 0",
      nativeQuery = true)
  List<Modem> findALLModemNotAffectedPos(Long idPos);

  @Query(
      value = "SELECT model_modem AS label, COUNT(*) AS count " + "FROM modem "
          + "WHERE status = 0 AND affecte_client IS NOT NULL " + "GROUP BY model_modem",
      nativeQuery = true)
  List<Map<String, Object>> getModelModemCounts();

  @Query(value = "SELECT "
      + "COUNT(CASE WHEN m.affecte_client IS NOT NULL THEN 1 END) AS countAffecteClient, "
      + "COUNT(CASE WHEN m.affecte_distributeur IS NOT NULL AND m.affecte_client IS NULL AND m.affecte_revendeur IS NULL AND m.affecte_pointde_vente IS NULL THEN 1 END) AS countAffecteDistributeur, "
      + "COUNT(CASE WHEN m.affecte_pointde_vente IS NOT NULL AND m.affecte_client IS NULL AND m.affecte_revendeur IS NULL THEN 1 END) AS countAffectePointdeVente, "
      + "COUNT(CASE WHEN m.affecte_revendeur IS NOT NULL AND m.affecte_client IS NULL THEN 1 END) AS countAffecteRevendeur, "
      + "COUNT(CASE WHEN m.affecte_revendeur IS NULL AND m.affecte_pointde_vente IS NULL AND m.affecte_distributeur IS NULL AND m.affecte_client IS NULL AND m.created_date IS NOT NULL THEN 1 END) AS countModemNonAffecte, "
      + "COUNT(CASE WHEN m.created_date IS NOT NULL AND m.status = 0 THEN 1 END) AS countModemActif, "
      + "COUNT(CASE WHEN m.created_date IS NOT NULL AND m.status = 1 THEN 1 END) AS countModemNonActif "
      + "FROM Modem m", nativeQuery = true)
  List<Map<String, Object>> getCountsModemForSummary();

  @Query("SELECT m.categorieProduitInternetCode AS label, COUNT(m)  AS count "
      + "FROM Abonnement a " + "JOIN a.pack.categoriePack m " + "JOIN a.statut s "
      + "WHERE s.nomStatut IN ('UNPAID', 'ACTIVE', 'VALID', 'ASSIGNED', 'RECOUVREMENT') "
      + "AND a.modemAffectedDate IS NOT NULL  " + "GROUP BY m.categorieProduitInternetCode")
  List<Map<String, Object>> countModemsByModel();


  @Query(value = "SELECT model_modem AS label, COUNT(*) AS count " + "FROM modem "
      + "WHERE status = 0 AND affecte_client IS NULL AND affecte_revendeur IS NULL AND affecte_distributeur=:userId AND affecte_pointde_vente IS NULL "
      + "GROUP BY model_modem", nativeQuery = true)
  List<Map<String, Object>> getModelModemCountsForDist(Long userId);

  @Query(value = "SELECT model_modem AS label, COUNT(*) AS count " + "FROM modem "
      + "WHERE status = 0 AND affecte_client IS NULL AND affecte_revendeur=:userId  "
      + "GROUP BY model_modem", nativeQuery = true)
  List<Map<String, Object>> getModelModemCountsForRev(Long userId);

  @Query(value = "SELECT model_modem AS label, COUNT(*) AS count " + "FROM modem "
      + "WHERE status = 0 AND affecte_client IS NULL AND affecte_pointde_vente = :userId  "
      + "GROUP BY model_modem", nativeQuery = true)
  List<Map<String, Object>> getModelModemCountsForPOS(Long userId);

  // for nety commercial for statut affecter /non affecter for etat actif or not
  @Query(value = "select abn.first_name as nomClient," + "abn.reference_client as referenceClient,"
      + "affecte_revendeur as affecterevendeur," + "marque as marque ,"
      + "model_modem as modelModem," + "num_serie as numSerie , status as status "
      + ",modem_id as modem_id,revendeur.code_user as code_userRv ,"
      + " revendeur.first_name as first_nameRv , revendeur.nom_commercial as nomCommercial "
      + "from modem " + " left join Users revendeur on revendeur.userid =modem.affecte_revendeur "
      + " left join abonnement abn on abn.clientid =modem.affecte_client"
      + " where ((affecte_revendeur in (select userid from users where affected_to = :idConnected) OR (affecte_distributeur = :idConnected ))"
      + "and (num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
      + "and (affecte_revendeur = :affetedUser or :affetedUser is null)"
      + "and (model_modem = :type or :type is null)"
      + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
      + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
      + "and (status = :etat or :etat is null) "
      + " and (:statut IS NULL OR ((affecte_client IS NOT NULL or affecte_pointde_vente IS NOT NULL or affecte_revendeur IS NOT NULL)AND affecte_distributeur = :idConnected  AND :statut = 1) "
      + "OR (affecte_client IS NULL and affecte_distributeur = :idConnected and affecte_pointde_vente IS NULL and affecte_revendeur IS NULL AND :statut = 0)"
      + ")" + ")",
      countQuery = "select count(*) from modem "
          + " left join Users revendeur on revendeur.userid =modem.affecte_revendeur "
          + " left join abonnement abn on abn.clientid =modem.affecte_client"
          + " where ((affecte_revendeur in (select userid from users where affected_to = :idConnected) OR (affecte_distributeur = :idConnected ))"
          + "and (num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
          + "and (affecte_revendeur = :affetedUser or :affetedUser is null)"
          + "and (model_modem = :type or :type is null)"
          + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
          + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
          + "and (status = :etat or :etat is null) "
          + " and (:statut IS NULL OR ((affecte_client IS NOT NULL or affecte_pointde_vente IS NOT NULL or affecte_revendeur IS NOT NULL)AND affecte_distributeur = :idConnected  AND :statut = 1) "
          + "OR (affecte_client IS NULL and affecte_distributeur = :idConnected and affecte_pointde_vente IS NULL and affecte_revendeur IS NULL AND :statut = 0)"
          + ")" + ")",
      nativeQuery = true)
  public Page<ModemDistributeur> listmodemtoDist(Long idConnected, String numSerie, String type,
      Long affetedUser, String numSerieDebut, String numSerieFin, Boolean statut, Boolean etat,
      Pageable page);

  @Query(
      value = "select model_modem as modelModem , num_serie as numSerie , marque as Marque , abn.reference_client as referenceClient"
          + ", abn.first_name as nomClient , modem_id as modem_id, status as status "
          + " from modem m left join abonnement abn on abn.clientid = m.affecte_client where affecte_revendeur = :idConnected "
          + "and (num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
          + "and (model_modem = :type or :type is null)"
          + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
          + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
          + "and (status = :etat or :etat is null) "
          + " and (:statut IS NULL OR (affecte_client IS NOT NULL   AND :statut = 1) "
          + "OR (affecte_client IS NULL  AND :statut = 0)" + ")",
      countQuery = "select count(*)"
          + " from modem m left join abonnement abn on abn.clientid = m.affecte_client where affecte_revendeur = :idConnected "
          + "and (num_serie like concat('%', :numSerie, '%') or :numSerie is null)"
          + "and (model_modem = :type or :type is null)"
          + "and (num_serie >= :numSerieDebut or :numSerieDebut is null )"
          + "and (num_serie <= :numSerieFin or :numSerieFin is null)"
          + "and (status = :etat or :etat is null) "
          + " and (:statut IS NULL OR (affecte_client IS NOT NULL   AND :statut = 1) "
          + "OR (affecte_client IS NULL  AND :statut = 0)" + ")",
      nativeQuery = true)
  public List<ModemRevendeur> listmodemtoRevn(Long idConnected, String numSerie, String type,
      Boolean statut, String numSerieDebut, String numSerieFin, Boolean etat);

  @Query(
      value = "SELECT DISTINCT u.code_user AS codeUser, u.userid AS userId , u.first_name AS firstName, u.last_name AS lastName, u.nom_commercial AS nomCommercial,"
          + "  (SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid) AS nb_modems_affectes,"
          + "  (SELECT COUNT(*) FROM modem WHERE  affecte_revendeur = u.userid AND affecte_client IS NOT NULL) AS nb_modems_client,"
          + " (SELECT count(*) FROM modem where affecte_revendeur = u.userid AND affecte_client IS NULL) AS nb_modems_disponible"
          + " FROM users u LEFT JOIN modem m ON u.userid = m.affecte_revendeur WHERE u.typeuser = 'REVENDEUR' "
          + "AND (u.userid IN (SELECT u.userid FROM users u WHERE u.affected_to = :idConnected) OR :idConnected IS NULL) "
          + "AND (u.code_user = :revendeur OR :revendeur IS NULL)"
          + "AND (u.affected_to = :distributeur OR :distributeur IS NULL)"
          + "AND (u.gouvernorat_id = :gouvernorat OR :gouvernorat IS NULL)"
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid AND affecte_client IS NULL) >= :min OR :min IS NULL)"
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid AND affecte_client IS NULL) <= :max OR :max IS NULL)",
      countQuery = "SELECT count(*) FROM users u LEFT JOIN modem m ON u.userid = m.affecte_distributeur WHERE u.typeuser = 'REVENDEUR'"
          + " AND (u.userid IN (SELECT u.userid FROM users u WHERE u.affected_to = :idConnected) OR :idConnected IS NULL)"
          + "AND (u.code_user = :revendeur OR :revendeur IS NULL)"
          + "AND (u.affected_to = :distributeur OR :distributeur IS NULL)"
          + "AND (u.gouvernorat_id = :gouvernorat OR :gouvernorat IS NULL)"
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid AND affecte_client IS NULL) >= :min OR :min IS NULL)"
          + "AND ((SELECT COUNT(*) FROM modem WHERE affecte_revendeur = u.userid AND affecte_client IS NULL) <= :max OR :max IS NULL)",
      nativeQuery = true)
  List<ModemEtatStockRev> etatStockRevComm(Long idConnected, String revendeur, Integer min,
      Integer max, Long gouvernorat, Long distributeur);

  @Query("SELECT DISTINCT m.modelModem FROM Modem m WHERE m.modelModem IS NOT NULL")
  List<String> findDistinctModeles();



}
