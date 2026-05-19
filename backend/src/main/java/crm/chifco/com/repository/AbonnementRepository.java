package crm.chifco.com.repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.ApiDTO.getLoginAndPassswordModem;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.User;
import crm.chifco.com.templateclasse.AbonnementInterfaceHistory;
import crm.chifco.com.templateclasse.AcsInfo;
import crm.chifco.com.templateclasse.ContratApiDTO;
import crm.chifco.com.templateclasse.getAbonnementApi;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
  Abonnement findClientByUserIsAndFirstNameOrLastNameOrCinOrEmail(User user, String fn, String ln,
      String cin, String email);

  Abonnement findAbonnementByCin(String cin);

  AbonnementInterfaceHistory findByCodeClient(String codeClient);

  Abonnement findAbonnementByClientid(Long clientid);

  Abonnement findAbonnementByDemandeAbonnement(Long demandeAbonnementId);

  Abonnement findClientByEmail(String email);

  @Query(
      value = "select TOP 1 * from abonnement where login_modem = :loginModem  ORDER BY login_modem  DESC ",
      nativeQuery = true)
  Abonnement findClientByLoginModem(String loginModem);

  List<Abonnement> findAbonnementByisFraisRaccordementTTOrIsFraisRaccordementTTIsNullAndHasRaccordementAndLoginModemNotNull(
      Boolean isFraisRaccordment, Boolean hasRaccoredment);

  Page<Abonnement> findClientsByAssignedTo_Role_RoleIdAndAssignedTo_Userid(Long roleid, Long userid,
      Pageable pageable);

  @Query(value = "select cls from Abonnement cls "
      + "where ( ( cls.firstName = :firstName or :firstName is null ) "
      + "and ( cls.lastName = :lastName or :lastName is null ) "
      + "and ( cls.referenceClient = :referenceClient or :referenceClient is null ) "
      + "and ( cls.enabled = :status or :status is null ) "
      + "and ( cls.telFixe = :tel or :tel is null ) "
      + "and ( cls.ville.villeId = :villes or :villes is null ) "
      + "and ( cls.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.pack.packId  = :produit or :produit is null ) "
      + "and ( cls.user.userid  = :Creepar or :Creepar is null ) "
      + "and ( cls.assignedTo.userid  = :AffecterTo or :AffecterTo is null ) "
      + "and ( cls.pack.categoriePack.categorieProduitInternetId = :categories or :categories is null )"
      + "and (cls.loginModem = :loginModem or :loginModem is null)"
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and (cls.statut.nomStatut = :statutChifcoListfiltre or :statutChifcoListfiltre is null) "
      + "and (cls.typeAbonnment = :typeAbonnment or :typeAbonnment is null) "

      + "and ( cls.cin= :cin or :cin is null ) and ( cls.user.affectedTo  =:createdbyuserid  Or cls.user.userid =:createdbyuserid))")

  Page<Abonnement> findClientsByCreatedBy_AffectedTo(Pageable pageable, Long createdbyuserid,
      String firstName, String lastName, String cin, String referenceClient, Boolean status,
      Long tel, Long villes, Long produit, Long categories, Long gouvernorat, String loginModem,
      Long AffecterTo, Long Creepar, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, String statutChifcoListfiltre ,String typeAbonnment);

  // @Query(
  // value = "select abn from Abonnement abn where ( abn.dateProchainFacturation <= :instantdatenow
  // )")
  List<Abonnement> findByDateProchainFacturationLessThanEqualAndIsActive(Date instantdatenow,
      Boolean isAbonnementActive);

  Abonnement findAbonnementByReferenceClient(String referenceChifco);

  @Query(value = "select cls from Abonnement cls "
      + "where ( ( cls.firstName = :firstName or :firstName is null ) "
      + "and ( cls.lastName = :lastName or :lastName is null ) "
      + "and ( cls.referenceClient = :referenceClient or :referenceClient is null ) "
      + "and ( cls.enabled = :status or :status is null ) "
      + "and ( cls.telFixe = :tel or :tel is null ) "
      + "and ( cls.ville.villeId = :villes or :villes is null ) "
      + "and ( cls.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.pack.packId = :pack or :pack is null ) "
      + "and ( cls.user.userid = :Creepar or :Creepar is null ) "
      + "and ( cls.assignedTo.userid = :AffecterTo or :AffecterTo is null ) "
      + "and ( cls.pack.categoriePack.categorieProduitInternetId = :categories or :categories is null )"
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and (cls.loginModem = :loginModem or :loginModem is null) "
      + " and ( :ancienloginModem is null or (:ancienloginModem IS NOT NULL AND cls.ancienLogin LIKE %:ancienloginModem%)   ) "
      + "and (cls.statut.nomStatut = :statutChifcoListfiltre or :statutChifcoListfiltre is null) "
      + "and ( cls.cin= :cin or :cin is null ) "
      + "and ( cls.isClient  = :isClient or :isClient is null ) "
      + "and ( cls.modemAffectedDate  >= :dateAffectionModem or :dateAffectionModem is null ) "
      + "and ( cls.modemAffectedDate  <= :dateFinAffection or :dateFinAffection is null )"
      + "and ( cls.typeAbonnment  = :typeAbonnment or :typeAbonnment is null )"

      + "and ( cls.telMobile  = :contactNumber or cls.telMobile2 = :contactNumber or :contactNumber is null ))")
  Page<Abonnement> findAllClient(Pageable pageable, String firstName, String lastName, String cin,
      String referenceClient, Boolean status, Long tel, Long villes, Long gouvernorat, Long pack,
      Long categories, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, String loginModem, String ancienloginModem, Long AffecterTo,
      Long Creepar, String statutChifcoListfiltre, Boolean isClient, Long contactNumber,
      Date dateAffectionModem, Date dateFinAffection , String typeAbonnment);

  @Query(
      value = "select mo.num_serie as numSerieModem ,mo.adresse_mac as adresse_mac ,  cls.login_modem as loginModem ,cls.password as password from abonnement cls  join status us on cls.statutid = us.statut_id join modem mo on cls.clientid = mo.affecte_client "
          + "where ( ( cls.created_date >=:instantdatenow) "

          + "and ( us.nom_statut = :nomstatut or :nomstatut is null )  )",
      nativeQuery = true)
  List<AcsInfo> findListClientByDateCreation(Instant instantdatenow, String nomstatut);

  @Query(value = "select count(clientid) from Abonnement")
  int countClient();

  @Query(value = "select cls from Abonnement cls "
      + "where ( ( cls.firstName = :firstName or :firstName is null ) "
      + "and ( cls.lastName = :lastName or :lastName is null ) "
      + "and ( cls.referenceClient = :referenceClient or :referenceClient is null ) "
      + "and ( cls.enabled = :status or :status is null ) "
      + "and ( cls.telFixe = :tel or :tel is null ) "
      + "and ( cls.ville.villeId = :villes or :villes is null ) "
      + "and ( cls.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.pack.packId  = :produit or :produit is null ) "
      + "and ( cls.pack.categoriePack.categorieProduitInternetId = :categories or :categories is null )"
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and (cls.loginModem = :loginModem or :loginModem is null)"
      + "and (cls.typeAbonnment = :typeAbonnment or :typeAbonnment is null)"

      + "and ( cls.cin= :cin or :cin is null ) and cls.assignedTo.userid =:userid )")

  Page<Abonnement> findClientsbyuserandfilter(Pageable pageable, Long userid, String firstName,
      String lastName, String cin, String referenceClient, Boolean status, Long tel, Long villes,
      Long produit, Long categories, Long gouvernorat, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, String loginModem , String typeAbonnment);

  @Query(
      value = "select  DISTINCT   ISNULL(  ( select    ISNULL(  c.reference_client , ' ')  from abonnement c  where  c.cin = :cin),'')",
      nativeQuery = true)
  String getReferenceAbonnementByCin(@Param("cin") String cin);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE abonnement  SET abonnement.tranche_raccordement = :trancheRaccordement WHERE abonnement.clientid = :clientid",
      nativeQuery = true)
  void updateTranshRaccordement(@Param("trancheRaccordement") int trancheRaccordement,
      @Param("clientid") Long clientid);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE abonnement  SET abonnement.date_prochain_facturation= :dateProchainFacture WHERE abonnement.clientid = :clientid",
      nativeQuery = true)
  void updateDateProchainFacture(@Param("dateProchainFacture") Date dateProchainFacture,
      @Param("clientid") Long clientid);

  @Query("SELECT f FROM Abonnement f where  f.telFixe = :telephone or  f.cin = :recherche")
  Abonnement findUserByFixeNumberOrCin(String recherche, Long telephone);

  @Query("SELECT f FROM Abonnement f where  f.telFixe = :telephone")
  Abonnement findUserByFixeNumber(Long telephone);

  @Query("SELECT a.referenceClient FROM Abonnement a WHERE a.clientid = :clientId")
  String findClientReferenceById(@Param("clientId") Long clientId);

  @Query(value = "select cls from Abonnement cls "
      + "where ( ( cls.firstName = :firstName or :firstName is null ) "
      + "and ( cls.lastName = :lastName or :lastName is null ) "
      + "and ( cls.referenceClient = :referenceClient or :referenceClient is null ) "
      + "and ( cls.enabled = :status or :status is null ) "
      + "and ( cls.telFixe = :tel or :tel is null ) "
      + "and ( cls.ville.villeId = :villes or :villes is null ) "
      + "and ( cls.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.pack.packId  = :produit or :produit is null ) "
      + "and ( cls.pack.categoriePack.categorieProduitInternetId = :categories or :categories is null )"
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and (cls.loginModem = :loginModem or :loginModem is null)"
      + "and (cls.typeAbonnment = :typeAbonnment or :typeAbonnment is null)"

      + "and ( cls.cin= :cin or :cin is null ) and cls.assignedTo.userid =:userid )")

  List<Abonnement> exportClientsbyuserandfilter(Long userid, String firstName, String lastName,
      String cin, String referenceClient, Boolean status, Long tel, Long villes, Long produit,
      Long categories, Long gouvernorat, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, String loginModem , String typeAbonnment);

  @Query(value = "select cls from Abonnement cls "
      + "where ( ( cls.firstName = :firstName or :firstName is null ) "
      + "and ( cls.lastName = :lastName or :lastName is null ) "
      + "and ( cls.referenceClient = :referenceClient or :referenceClient is null ) "
      + "and ( cls.enabled = :status or :status is null ) "
      + "and ( cls.telFixe = :tel or :tel is null ) "
      + "and ( cls.ville.villeId = :villes or :villes is null ) "
      + "and ( cls.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.pack.packId = :pack or :pack is null ) "
      + "and ( cls.user.userid = :Creepar or :Creepar is null ) "
      + "and ( cls.assignedTo.userid = :AffecterTo or :AffecterTo is null ) "
      + "and ( cls.pack.categoriePack.categorieProduitInternetId = :categories or :categories is null )"
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and (cls.loginModem = :loginModem or :loginModem is null) "
      + "and (cls.statut.nomStatut = :statutChifco or :statutChifco is null) "
      + "and ( cls.cin= :cin or :cin is null )"
      + "and ( cls.modemAffectedDate >= :dateAffectionModem or :dateAffectionModem is null ) "
      + "and ( cls.modemAffectedDate <= :recherchedatefinAffectionModem or :recherchedatefinAffectionModem is null ) "
      + "and ( cls.typeAbonnment = :typeAbonnment or :typeAbonnment is null )"

      + " and ( cls.isClient  = :listeClientActiveFilter or :listeClientActiveFilter is null )  )")
  List<Abonnement> exportAllClient(String firstName, String lastName, String cin,
      String referenceClient, Boolean status, Long tel, Long villes, Long gouvernorat, Long pack,
      Long categories, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, String loginModem, Long AffecterTo, Long Creepar,
      String statutChifco, Boolean listeClientActiveFilter, Date dateAffectionModem,
      Date recherchedatefinAffectionModem, String typeAbonnment);

  @Query(value = "select cls from Abonnement cls "
      + "where ( ( cls.firstName = :firstName or :firstName is null ) "
      + "and ( cls.lastName = :lastName or :lastName is null ) "
      + "and ( cls.referenceClient = :referenceClient or :referenceClient is null ) "
      + "and ( cls.enabled = :status or :status is null ) "
      + "and ( cls.telFixe = :tel or :tel is null ) "
      + "and ( cls.ville.villeId = :villes or :villes is null ) "
      + "and ( cls.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.pack.packId  = :produit or :produit is null ) "
      + "and ( cls.user.userid  = :Creepar or :Creepar is null ) "
      + "and ( cls.assignedTo.userid  = :AffecterTo or :AffecterTo is null ) "
      + "and ( cls.pack.categoriePack.categorieProduitInternetId = :categories or :categories is null )"
      + "and (cls.loginModem = :loginModem or :loginModem is null)"
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.typeAbonnment =  :typeAbonnment or :typeAbonnment is null ) "

      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.cin= :cin or :cin is null ) and (cls.user.affectedTo  =:createdbyuserid  Or cls.assignedTo.userid =:createdbyuserid))")

  List<Abonnement> exportClientsByaAssignedTo_AffectedTo(Long createdbyuserid, String firstName,
      String lastName, String cin, String referenceClient, Boolean status, Long tel, Long villes,
      Long produit, Long categories, Long gouvernorat, String loginModem, Long AffecterTo,
      Long Creepar, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification , String typeAbonnment);

  @Query(value = "SELECT reference_client as ref_aonnement, IIF([date_resiliation] IS NULL,CAST(1 AS BIT), CAST(0 AS BIT)) AS isClient, cin as identifiant, last_name as nom, "
      + "first_name as prenom, a.email as email, CONCAT(adresse, ' , ', p.name, ' - ', p.code, ' ', "
      + "gouvernorat_name) AS adresse, tel_mobile as num_mobile, tel_fixe as num_fixe, "
      + "CONCAT(m.marque ,' - ', m.num_serie ) as equipement  ,ps.title as nameOffre, "
      + "factureCount = (SELECT COUNT(factures.clientid) FROM factures  WHERE factures.clientid = a.clientid and factures.etat_facture = 'false'),"
      + "isfirstfacture = (SELECT factures.etat_facture FROM factures  WHERE factures.clientid = a.clientid and factures.is_first_facture = 'true') "
      + "FROM abonnement a " + "JOIN gouvernorats gov ON a.gouvernorat_id = gov.gouvernorat_id "
      + "JOIN postal_code p ON p.postal_code_id = a.code_postale "
      + "LEFT JOIN modem m ON m.modem_id = a.modemid "
      + "LEFT JOIN pack ps on ps.pack_id = a.pack_id "
      + "WHERE a.cin = :identifiant AND a.tel_fixe = :numFixe", nativeQuery = true)
  Optional<getAbonnementApi> findByIdentifiantAndNumFixe(Long numFixe, String identifiant);

  @Modifying
  @Transactional
  @Query("UPDATE Abonnement SET refClientSite = :ref_clientsite WHERE referenceClient = :ref_abonnement")
  void updateRefCLientSite(String ref_abonnement, String ref_clientsite);

  @Query("SELECT count(clientid) FROM Abonnement WHERE refClientSite = :ref_clientsite")
  Integer findByRefClientSite(String ref_clientsite);

  @Query("SELECT CASE WHEN COUNT(clientid) > 0 THEN false ELSE true END FROM Abonnement a WHERE a.refClientSite = :ref_clientSite")
  boolean existsByReference(String ref_clientSite);

  @Query("SELECT new crm.chifco.com.templateclasse.ContratApiDTO(a.referenceClient, a.createdDate,a.statut.nomStatut,CASE WHEN dm.contratPdf  IS Not NULL THEN true ELSE false END AS haveContrat , a.pack.title  ) "
      + "FROM Abonnement a " + " JOIN DemandeAbonnement dm on dm.demandeId = a.demandeAbonnement "
      + "WHERE a.referenceClient = :ref_clientsite")

  List<ContratApiDTO> findAllByReference(@Param("ref_clientsite") String ref_clientsite);

  @Query("SELECT abn.referenceClient as referenceClient,abn.refClientSite as refClientSite from Abonnement abn where abn.referenceClient = :ref_abonnement")
  Object[] findtest(String ref_abonnement);

  @Query("SELECT abn.clientid FROM Abonnement abn WHERE referenceClient = :reference")
  Long getClientIdByReference(String reference);

  @Modifying
  @Transactional
  @Query("UPDATE Abonnement SET telMobile = :numTel, email = :email WHERE referenceClient = :ref_abonnement")
  void updateClient(String ref_abonnement, Long numTel, String email);

  @Query("SELECT  new crm.chifco.com.ApiDTO.getLoginAndPassswordModem(a.modem.password,a.modem.email , a.modem.loginControleParental,a.modem.modelModem, a.modem.controleParentaleActiver ) from Abonnement a where a.modem.numSerie = :numSerie")
  getLoginAndPassswordModem getLoginAndPassswordModemByNumSerie(String numSerie);

  @Query(value = "SELECT *" + "FROM abonnement " + "WHERE " + " (" + " ("
      + "            YEAR(date_calcule_frais_servies) <> YEAR(:curreentMonth) "
      + "            OR MONTH(date_calcule_frais_servies) <> MONTH(:curreentMonth)" + " ) "
      + "        OR date_calcule_frais_servies IS NULL " + " )" + "    AND login_modem IS NOT NULL"
      + "", nativeQuery = true)
  List<Abonnement> findAbonnementByLoginModemNotNullAndDateCalculeFraisServiesNotIn(
      String curreentMonth);


  List<Abonnement> findAbonnementByisFraisRaccordementTTOrIsFraisRaccordementTTIsNullAndHasRaccordement(
      boolean isFraisRaccordementTT, boolean HasRaccordement);

  List<Abonnement> findAbonnementByCalculeIsFirstSession(boolean b);

  @Query("SELECT abn FROM Abonnement abn WHERE abn.loginModem is not null and (abn.calculeServiceResiliation is null or abn.calculeServiceResiliation = false) and abn.calculeIsFirstSession = true ")
  List<Abonnement> findAllByLoginModemNotNullAndNotCalculeResilationTTAndFirtConectionIsTrue();



  // search list abonnement pour migration


  @Query(
      value = "SELECT a.* FROM Abonnement a LEFT JOIN User u on a.userid=u.userid WHERE a.reference_client = :referenceClient "
          + " AND a.statutid IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE', 'VALID', 'UNPAID')) "
          + "  AND NOT EXISTS (SELECT TOP 1 * FROM OperationAbonnement d "
          + "WHERE d.reference_chifco = :referenceClient"
          + "   AND (d.statut_id NOT IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE', 'REFUSED', 'CANCELED', 'VALID')) "
          + "   AND d.etatTT NOT IN ('Confirmation Client Annuler', 'Annulée'))"
          + " AND (a.userid= :userId OR :userId IS NULL) " + "   ORDER BY d.operationid DESC)",
      nativeQuery = true)
  List<Abonnement> findAbonnementsNotInMigrationAdminwithRefRev(
      @Param("referenceClient") String referenceClient, Long userId);

  @Query(
      value = "SELECT a.* FROM Abonnement a LEFT JOIN User u on a.userid=u.userid WHERE a.reference_client = :referenceClient "
          + "  AND a.statutid IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE', 'VALID', 'UNPAID')) "
          + "  AND NOT EXISTS (SELECT TOP 1 * FROM OperationAbonnement d "
          + " WHERE d.reference_chifco = :referenceClient "
          + " AND (d.statut_id NOT IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE', 'REFUSED', 'CANCELED', 'VALID')) "
          + " AND d.etatTT NOT IN ('Confirmation Client Annuler', 'Annulée')) AND (u.affected_to=:userId OR :userId IS NULL )"
          + "  ORDER BY d.operationid DESC) ",
      nativeQuery = true)
  List<Abonnement> findAbonnementsNotInMigrationAdminwithRefDist(
      @Param("referenceClient") String referenceClient, Long userId);



  @Query(value = "select cls from Abonnement cls "
      + "where ( ( cls.firstName = :firstName or :firstName is null ) "
      + "and ( cls.lastName = :lastName or :lastName is null ) "
      + "and ( cls.referenceClient = :referenceClient or :referenceClient is null ) "
      + "and ( cls.enabled = :status or :status is null ) "
      + "and ( cls.telFixe = :tel or :tel is null ) "
      + "and ( cls.ville.villeId = :villes or :villes is null ) "
      + "and ( cls.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.pack.packId = :pack or :pack is null ) "
      + "and ( cls.user.userid = :Creepar or :Creepar is null ) "
      + "and ( cls.assignedTo.userid = :AffecterTo or :AffecterTo is null ) "
      + "and ( cls.pack.categoriePack.categorieProduitInternetId = :categories or :categories is null )"
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and (cls.loginModem = :loginModem or :loginModem is null) "
      + "and (cls.ancienLogin = :ancienloginModem or :ancienloginModem is null) "
      + "and (cls.statut.nomStatut = 'UNPAID' or cls.statut.nomStatut = 'ACTIVE' or cls.statut.nomStatut = 'VALID')"
      + "and ( cls.cin= :cin or :cin is null ) )")
  Page<Abonnement> findAllClientActive(Pageable pageable, String firstName, String lastName,
      String cin, String referenceClient, Boolean status, Long tel, Long villes, Long gouvernorat,
      Long pack, Long categories, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, String loginModem, String ancienloginModem, Long AffecterTo,
      Long Creepar);


  List<Abonnement> findAllByStatut_nomStatutAndLoginModemIsNull(String nomStatut);



  @Query(value = "select cls from Abonnement cls "
      + "where ( ( cls.firstName = :firstName or :firstName is null ) "
      + "and ( cls.lastName = :lastName or :lastName is null ) "
      + "and ( cls.cin= :cinClient or :cinClient is null ) "
      + "and ( cls.referenceClient = :referenceClient or :referenceClient is null ) "
      + "and ( cls.telFixe = :tel or :tel is null ) "
      + "and ( cls.ville.villeId = :villes or :villes is null ) "
      + "and ( cls.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.pack.packId = :produit or :produit is null ) "
      + "and ( cls.assignedTo.userid = :affecterTo or :affecterTo is null ) "
      + "and ( cls.pack.categoriePack.categorieProduitInternetId = :category or :category is null )"
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :dateDeFin or :dateDeFin is null ) "
      + "and ( cls.modifiedDate  >= :datedebutDeModification or :datedebutDeModification is null ) "
      + "and ( cls.modifiedDate  <= :dateDeFinDeModification or :dateDeFinDeModification is null ) "
      + "and (cls.loginModem = :modemLogin or :modemLogin is null) and cls.firstConnectionDate is null )")

  List<Abonnement> exportAllClientNonConnecter(String firstName, String lastName, String cinClient,
      String referenceClient, Long tel, Long villes, Long gouvernorat, Long produit, Long category,
      Date datedebut, Date dateDeFin, Date datedebutDeModification, Date dateDeFinDeModification,
      String modemLogin, Long affecterTo);

  @Query(value = "select cls from Abonnement cls "
      + "where ( ( cls.firstName = :firstName or :firstName is null ) "
      + "and ( cls.lastName = :lastName or :lastName is null ) "
      + "and ( cls.cin= :cinClient or :cinClient is null ) "
      + "and ( cls.referenceClient = :referenceClient or :referenceClient is null ) "
      + "and ( cls.telFixe = :tel or :tel is null ) "
      + "and ( cls.ville.villeId = :villes or :villes is null ) "
      + "and ( cls.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.pack.packId = :produit or :produit is null ) "
      + "and ( cls.pack.categoriePack.categorieProduitInternetId = :category or :category is null )"
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :dateDeFin or :dateDeFin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and (cls.loginModem = :modemLogin or :modemLogin is null) and cls.firstConnectionDate is null )"
      + " and cls.statut.nomStatut != 'RESILIATION' and cls.statut.nomStatut != 'POROFORMA'")
  Page<Abonnement> findAllClientNonConnecter(Pageable pageable, String firstName, String lastName,
      String cinClient, String referenceClient, Long tel, Long villes, Long gouvernorat,
      Long produit, Long category, Date datedebut, Date dateDeFin, Date dateDebutModification,
      Date dateFinModification, String modemLogin);

  @Query(value = "select cls from Abonnement cls " + "where ( "
      + " ( cls.createdDate  <= :datedebut or :datedebut is null ) and cls.firstConnectionDate is null )"
      + " and ( cls.statut.nomStatut = 'INSTALLED' or  cls.statut.nomStatut = 'POROFORMA') order by cls.createdDate desc")
  List<Abonnement> findAllClientNonConnecterMiseEnService(Date datedebut);

  @Query("SELECT a FROM Abonnement a "
      + "WHERE (a.referenceClient NOT IN (SELECT d.referenceChifco FROM OperationAbonnement d WHERE d.statut.nomStatut NOT IN('ACTIVE', 'REFUSED', 'CANCELED','VALID') OR d.etatTT='Confirmation Client Annuler') "
      + "AND (a.statut.nomStatut IN ('ACTIVE', 'VALID', 'UNPAID')))")
  List<Abonnement> findAbonnementsNotInMigrationAdmin();



  // search list abonnement pour migration
  @Query(value = "SELECT a.* FROM Abonnement a WHERE a.reference_client = :referenceClient "
      + " AND a.statutid IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE','RECOUVREMENT', 'VALID', 'UNPAID')) "

      + "  AND NOT EXISTS (SELECT TOP 1 * " + "FROM OperationAbonnement d "
      + "WHERE d.reference_chifco = :referenceClient"
      + "  AND (d.statut_id NOT IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE', 'REFUSED', 'CANCELED', 'VALID')) "
      + "       AND d.etatTT NOT IN ('Confirmation Client Annuler', 'Annulée'))"
      + "ORDER BY d.operationid DESC)", nativeQuery = true)
  List<Abonnement> findAbonnementsNotInMigrationAdminwithRef(
      @Param("referenceClient") String referenceClient);

  @Query(value = "SELECT a.* FROM Abonnement a WHERE a.reference_client = :referenceClient "
      + " AND a.statutid IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE','RECOUVREMENT', 'VALID', 'UNPAID')) "
      + "  AND  EXISTS (SELECT TOP 1 * " + "FROM OperationAbonnement d "
      + "WHERE d.reference_chifco = :referenceClient"
      + "  AND (d.statut_id  IN (SELECT statut_id FROM status WHERE nom_statut IN ('REFUSED', 'CANCELED')  ) "
      + "       AND d.etatTT IN ('Annulée') AND d.type_demande =:typeDemande )"
      + "ORDER BY d.operationid DESC)", nativeQuery = true)
  List<Abonnement> findAbonnementsNotInMigrationAdminwithRef2(
      @Param("referenceClient") String referenceClient, String typeDemande);

  @Query("SELECT abn.clientid FROM Abonnement abn WHERE  abn.demandeAbonnement = :idDemandeAbonnment ")

  Long findIdClientByIdDemandeAbonnment(long idDemandeAbonnment);

  @Query(
      value = "SELECT a.* FROM Abonnement a WHERE a.reference_client = :referenceClient  AND a.assigned_to=:userId "
          + " AND a.statutid IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE','RECOUVREMENT', 'VALID', 'UNPAID')) "
          + "  AND NOT EXISTS (SELECT TOP 1 * " + "FROM OperationAbonnement d "
          + "WHERE d.reference_chifco = :referenceClient"
          + "  AND (d.statut_id NOT IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE', 'REFUSED', 'CANCELED', 'VALID')) "
          + "       AND d.etatTT NOT IN ('Confirmation Client Annuler', 'Annulée')) "
          + "ORDER BY d.operationid DESC)",
      nativeQuery = true)
  List<Abonnement> findAbonnementsNotInMigrationRevendeur(
      @Param("referenceClient") String referenceClient, Long userId);

  @Query(
      value = "SELECT a.* FROM Abonnement a  LEFT JOIN users u on a.assigned_to =u.userid WHERE a.reference_client = :referenceClient AND u.affected_to=:userId "
          + " AND a.statutid IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE','RECOUVREMENT', 'VALID', 'UNPAID')) "
          + "  AND NOT EXISTS (SELECT TOP 1 * " + "FROM OperationAbonnement d "
          + "WHERE d.reference_chifco = :referenceClient"
          + "  AND (d.statut_id NOT IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE', 'REFUSED', 'CANCELED', 'VALID')) "
          + "       AND d.etatTT NOT IN ('Confirmation Client Annuler', 'Annulée'))  "
          + "ORDER BY d.operationid DESC)",
      nativeQuery = true)
  List<Abonnement> findAbonnementsNotInMigrationDistributeur(
      @Param("referenceClient") String referenceClient, Long userId);



  @Query("SELECT "
      + "COUNT(CASE WHEN a.isActive = true AND a.dateResiliation IS NULL THEN 1 END) as activeCount, "
      + "COUNT(CASE WHEN CONVERT(DATE, a.dateResiliation) = CONVERT(DATE, GETDATE()) AND a.isActive = false AND a.dateResiliation IS NOT NULL THEN 1 END) as resiliedCount, "
      + "COUNT(CASE WHEN CONVERT(DATE, a.dateResiliation) = CONVERT(DATE, :yesterday) AND a.isActive = false AND a.dateResiliation IS NOT NULL THEN 1 END) as resiliedCountLastMonth, "
      + "COUNT(a) as totalAbonnements, "
      + "COUNT(CASE WHEN CONVERT(DATE, a.createdDate) = CONVERT(DATE, GETDATE()) AND a.isActive = true AND a.dateResiliation IS NULL THEN 1 END) as nouvelleAbonnementsThisMonth, "
      + "COUNT(CASE WHEN CONVERT(DATE, a.createdDate) = CONVERT(DATE, :yesterday) AND a.isActive = true AND a.dateResiliation IS NULL THEN 1 END) as nouvelleAbonnementsLastMonth "
      + "FROM Abonnement a")
  Object getAbonnementSummary(@Param("yesterday") String yesterday);


  @Query("SELECT "
      + "COUNT(CASE WHEN a.dateResiliation >= :startOfMonth AND a.dateResiliation <= :endOfMonth AND a.isActive = false AND a.dateResiliation IS NOT NULL THEN 1 END) as resiliedCount, "
      + "COUNT(CASE WHEN a.dateResiliation >= :startOfLastMonth AND a.dateResiliation <= :endOfLastMonth AND a.isActive = false AND a.dateResiliation IS NOT NULL THEN 1 END) as resiliedCountLastMonth, "
      + "COUNT(CASE WHEN a.createdDate >= :startOfMonth AND a.createdDate <= :endOfMonth AND a.isActive = true AND a.dateResiliation IS NULL THEN 1 END) as nouvelleAbonnementsThisMonth, "
      + "COUNT(CASE WHEN a.createdDate >= :startOfLastMonth AND a.createdDate <= :endOfLastMonth AND a.isActive = true AND a.dateResiliation IS NULL THEN 1 END) as nouvelleAbonnementsLastMonth "
      + "FROM Abonnement a where (a.assignedTo.affectedTo=:userId OR :userId IS NULL) AND (a.assignedTo.userid=:idRevOrPos OR :idRevOrPos IS NULL)")
  Object getAbonnementSummaryForOthers(@Param("startOfMonth") Date startOfMonth,
      @Param("endOfMonth") Date endOfMonth, @Param("startOfLastMonth") Date startOfLastMonth,
      @Param("endOfLastMonth") Date endOfLastMonth, @Param("userId") Long userId,
      @Param("idRevOrPos") Long idRevOrPos);

  @Query(value = "SELECT p.title AS packName, " + "o.title AS offre, "
      + "CONCAT(p.title, '( ', o.title, ' )') AS concatPack, "
      + "COUNT(a.clientid) AS abonnementNumber " + "FROM pack p "
      + "INNER JOIN offre o ON p.offre_id = o.offre_id "
      + "INNER JOIN abonnement a ON p.pack_id = a.pack_id  where a.is_active=1 AND (a.assigned_to = :revOrPosId OR :revOrPosId IS NULL) "
      + " AND (a.assigned_to IN(SELECT userid from users u where u.affected_to =  :distId OR :distId IS NULL)) "
      + "GROUP BY p.title, o.title", nativeQuery = true)
  List<Map<String, Object>> getAbonnementStatsByPack(@Param("revOrPosId") Long revOrPosId,
      @Param("distId") Long distId);

  @Query(value = "SELECT COUNT(a.clientid) AS abonnementCount,t.nom_type_paiement as typepaiment "
      + "FROM abonnement a "
      + "LEFT JOIN typepaiement t on a.typepaiement_id=t.type_paiement_id where a.is_active=1 AND (a.assigned_to = :revOrPosId OR :revOrPosId IS NULL) "
      + "AND (a.assigned_to IN(SELECT userid from users u where u.affected_to =  :distId OR :distId IS NULL)) "
      + "GROUP BY t.nom_type_paiement", nativeQuery = true)
  List<Map<String, Object>> getTotalAbonnementStatsByTypePaiement(
      @Param("revOrPosId") Long revOrPosId, @Param("distId") Long distId);

  @Query("SELECT cs.codeUser AS chefSecteurId, "
      + "CONCAT(cs.firstName, ' ', cs.lastName) AS chefSecteurName, "
      + "COUNT(DISTINCT CASE WHEN  ((da.dateResiliation BETWEEN :startOfDate AND :endOfDate)OR (:startOfDate IS NULL AND :endOfDate IS NULL)) AND da.dateResiliation IS NOT NULL THEN da.clientid ELSE NULL END) as resiliationCount, "
      + "COUNT(DISTINCT CASE WHEN  da.modemAffectedDate IS NOT NULL AND ((da.modemAffectedDate BETWEEN :startOfDate AND :endOfDate)OR (:startOfDate IS NULL AND :endOfDate IS NULL)) THEN da.clientid ELSE NULL END) as assignedCount, "
      + "COUNT(DISTINCT CASE WHEN  f.dateDePayement IS NOT NULL AND((f.dateDePayement BETWEEN :startOfDate AND :endOfDate)OR (:startOfDate IS NULL AND :endOfDate IS NULL)) AND f.isProformat=1 THEN da.clientid ELSE NULL END) as proformatCount, "
      + "COUNT(DISTINCT CASE WHEN  da.dateDeMiseEnService IS NOT NULL AND ((da.dateDeMiseEnService BETWEEN :startOfDate AND :endOfDate)OR (:startOfDate IS NULL AND :endOfDate IS NULL)) THEN da.clientid ELSE NULL END) as miseenserviceCount "
      + "FROM Abonnement da Left join DemandeAbonnement d on d.demandeId=da.demandeAbonnement Left join Facture f on f.abonnement.clientid=da.clientid "
      + " LEFT JOIN User u ON da.user.userid = u.userid "
      + "LEFT JOIN User cs ON u.affectedTo = cs.userid "
      + "WHERE (u.affectedTo = :userId OR :userId IS NULL) "
      + "AND (u.userid = :idRevOrPos OR :idRevOrPos IS NULL) " + "AND cs.typeUser='DISTRIBUTEUR' "
      + "GROUP BY cs.codeUser, cs.firstName, cs.lastName")
  List<Object[]> getAbonnementSummaryByChefSecteur(@Param("startOfDate") Date startOfDate,
      @Param("endOfDate") Date endOfDate, @Param("userId") Long userId,
      @Param("idRevOrPos") Long idRevOrPos);

  @Query("SELECT "
      + "COUNT(DISTINCT CASE WHEN da.dateResiliation IS NOT NULL AND (da.dateResiliation BETWEEN :startOfDate AND :endOfDate OR :startOfDate IS NULL AND :endOfDate IS NULL) THEN da.clientid ELSE NULL END) as resiliationCount, "
      + "COUNT(DISTINCT CASE WHEN da.modemAffectedDate IS NOT NULL AND (da.modemAffectedDate BETWEEN :startOfDate AND :endOfDate OR :startOfDate IS NULL AND :endOfDate IS NULL) THEN da.clientid ELSE NULL END) as assignedCount, "
      + "COUNT(DISTINCT CASE WHEN f.dateDePayement IS NOT NULL AND (f.dateDePayement BETWEEN :startOfDate AND :endOfDate OR :startOfDate IS NULL AND :endOfDate IS NULL) AND f.isProformat = 1 THEN da.clientid ELSE NULL END) as proformatCount, "
      + "COUNT(DISTINCT CASE WHEN da.dateDeMiseEnService IS NOT NULL AND (da.dateDeMiseEnService BETWEEN :startOfDate AND :endOfDate OR :startOfDate IS NULL AND :endOfDate IS NULL) THEN da.clientid ELSE NULL END) as miseenserviceCount "
      + "FROM Abonnement da "
      + "LEFT JOIN DemandeAbonnement d ON d.demandeId = da.demandeAbonnement "
      + "LEFT JOIN Facture f ON f.abonnement.clientid = da.clientid where d.decisionDemande.classificationId != 1")
  List<Object[]> getAbonnementSummaryTotal(@Param("startOfDate") Date startOfDate,
      @Param("endOfDate") Date endOfDate);

  @Query("SELECT g.gouvernoratName, COUNT(a) FROM Abonnement a " + "JOIN a.gouvernorat g "
      + "WHERE ((:startOfDate IS NULL OR a.createdDate >= :startOfDate) AND (:endOfDate IS NULL OR a.createdDate <= :endOfDate)) "
      + "AND (a.statut.nomStatut IN( 'ACTIVE', 'VALID','UNPAID','RECOUVREMENT','ASSIGNED','INSTALLED')) "
      + "AND  a.dateResiliation IS NULL AND (a.user.affectedTo=:distributeur OR :distributeur IS NULL)"
      + " AND (a.gouvernorat.gouvernoratId=:gouvernoratId OR :gouvernoratId IS NULL) "
      + "GROUP BY g.gouvernoratName")
  List<Object[]> countActiveAbonnementsByGouvernorat(@Param("startOfDate") Date startOfDate,
      @Param("endOfDate") Date endOfDate, Long distributeur, Long gouvernoratId);

  @Query(value = "SELECT "
      + "SUM(CASE WHEN s.nom_statut IN ('UNPAID', 'ACTIVE', 'VALID','ASSIGNED','INSTALLED','RECOUVREMENT') THEN 1 ELSE 0 END) AS countActiveClient, "
      + "SUM(CASE WHEN s.nom_statut = 'RECOUVREMENT' THEN 1 ELSE 0 END) AS countRecouvrement, "
      + "SUM(CASE WHEN s.nom_statut = 'RESILIATION' THEN 1 ELSE 0 END) AS countResilier, "
      + "SUM(CASE WHEN cls.clientid IS NOT NULL THEN 1 ELSE 0 END) AS countAllClient, "
      + "cs.code_user, cs.first_name, cs.last_name, totalRevendeur.totalRevendeurCount "
      + "FROM users u " + "LEFT JOIN abonnement cls ON cls.userid = u.userid "
      + "LEFT JOIN status s ON s.statut_id = cls.statutid "
      + "LEFT JOIN users cs ON u.affected_to = cs.userid " + "JOIN ("
      + "    SELECT u1.affected_to AS chefSecteurId, COUNT(DISTINCT u1.userid) AS totalRevendeurCount "
      + "    FROM users u1 " + "    WHERE u1.typeuser IN ('REVENDEUR','POS') AND u1.enabled = 1 "
      + " GROUP BY u1.affected_to"
      + ") AS totalRevendeur ON totalRevendeur.chefSecteurId = cs.userid "
      + "WHERE u.typeuser IN('REVENDEUR','POS') AND cs.typeuser='DISTRIBUTEUR' AND (cs.userid=:distributeur or :distributeur IS NULL) "
      + "GROUP BY cs.code_user, cs.first_name, cs.last_name, totalRevendeur.totalRevendeurCount",
      nativeQuery = true)
  List<Map<String, Object>> getClientCounts(Long distributeur);

  @Query(value = "SELECT "
      + "SUM(CASE WHEN s.nom_statut IN ('UNPAID', 'ACTIVE', 'VALID','ASSIGNED','INSTALLED','RECOUVREMENT') THEN 1 ELSE 0 END) AS countActiveClient, "
      + "SUM(CASE WHEN s.nom_statut = 'RECOUVREMENT' THEN 1 ELSE 0 END) AS countRecouvrement, "
      + "SUM(CASE WHEN s.nom_statut = 'RESILIATION' THEN 1 ELSE 0 END) AS countResilier, "
      + "SUM(CASE WHEN cls.clientid IS NOT NULL THEN 1 ELSE 0 END) AS countAllClient "
      + "FROM abonnement cls LEFT JOIN status s ON s.statut_id = cls.statutid ", nativeQuery = true)
  List<Map<String, Object>> getClientCountsTotal();

  // nbre d'abonnement today and this month
  @Query(value = "SELECT "
      + "COUNT(CASE WHEN a.is_active = 1 AND a.date_resiliation IS NULL AND  CAST(a.created_date AS DATE) = CAST(GETDATE() AS DATE) THEN 1 END) as activeCountToday, "
      + "COUNT(CASE WHEN a.is_active = 1 AND a.date_resiliation IS NULL AND a.created_date >= :startOfMonth AND a.created_date <= :endOfMonth THEN 1 END) as activeCountThisMonth "
      + "FROM Abonnement a where a.userid=:revId ", nativeQuery = true)
  Object getAbonnementSummaryMobApp(@Param("startOfMonth") Date startOfMonth,
      @Param("endOfMonth") Date endOfMonth, Long revId);


  @Query(value = "select cls from Abonnement cls "
      + "where ( ( cls.firstName = :firstName or :firstName is null ) "
      + "and ( cls.lastName = :lastName or :lastName is null ) "
      + "and ( cls.cin= :cin or :cin is null ) "
      + "and ( cls.referenceClient = :codeClient or :codeClient is null ) "
      + "and ( cls.telFixe = :tel or :tel is null ) "
      + "and ( cls.ville.villeId = :villes or :villes is null ) "
      + "and ( cls.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.pack.packId = :produit or :produit is null ) "
      + "and ( cls.pack.categoriePack.categorieProduitInternetId = :categories or :categories is null )"
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and (cls.loginModem = :loginModem or :loginModem is null) and cls.firstConnectionDate is null "
      + " and cls.statut.nomStatut = 'POROFORMA' ) ")
  Page<Abonnement> getallClientNonConnecterMiseService(Pageable pageable, String firstName,
      String lastName, String cin, String codeClient, Long tel, Long villes, Long gouvernorat,
      Long produit, Long categories, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, String loginModem);

  @Query(value = "select cls from Abonnement cls where cls.loginModem in :loginModem")
  List<Abonnement> findClientsbyloginModem(List<String> loginModem);


  @Modifying
  @Transactional

  @Query("UPDATE Abonnement SET telMobile = :numTel, email = :email WHERE clientid in :abonnementResiler")
  void findClientsansetItToResiler(List<Long> abonnementResiler);


  @Query("UPDATE Abonnement SET statut = :status, dateResiliation = :dateResilation , resiliePar = :user WHERE clientid in :abonnementResiler")
  void findClientsansetItToResiler(List<Long> abonnementResiler, User user, Statut status,
      Date dateResilation);

  @Query("SELECT new map(abo.firstName as firstName, abo.lastName as lastName, abo.clientid as clientId,abo.referenceClient as codeUser) FROM Abonnement abo")
  List<Map<String, Object>> getAllClientForReclamation();

  List<Abonnement> findAbonnementByComissionActivationIsPayed(Boolean isPayed);

  // List<Abonnement> findAbonnementByComissionActivationIsPayedAndStatut_statutIdIn(Boolean isPayed
  // , List<Long> statutId);
  List<Abonnement> findAbonnementsByReferenceClientIn(List<String> referenceChifco);

  @Query(value = "SELECT * FROM abonnement a "
      + "JOIN demandesabonnement d ON a.demande_abonnement = d.demandeid "
      + "WHERE a.comission_activation_is_payed = :isPayed "
      + "AND d.statut_id IN (:statutIds) AND d.user_id = :userId", nativeQuery = true)
  List<Abonnement> findAbonnementsByIsPayedAndDemandeAbonnementStatutIn(
      @Param("isPayed") Boolean isPayed, @Param("statutIds") List<Long> statutIds,
      @Param("userId") Long userId);

  Abonnement findTopByTelFixeOrderByCreatedDateDesc(Long telephone);

  @Query(value = "SELECT * FROM abonnement a "
	      + "JOIN factures fb  ON fb.clientid = a.clientid "
	      + "WHERE fb.etat_facture = 0 "
	      + "AND fb.date_echeance = :currentDate AND a.type_abonnment = :box", nativeQuery = true)
List<Abonnement> findClientsbyFactureNonPayeeAndTypeAbonnement(LocalDate currentDate, String box);

}
