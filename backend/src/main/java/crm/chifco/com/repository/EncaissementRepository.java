package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.ApiDTO.EncaissementDto;
import crm.chifco.com.DTOclass.RecapFactureLimitDTO;
import crm.chifco.com.DTOclass.TopRevendeur;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.User;
import crm.chifco.com.templateclasse.EncaissementNonPayee;
import crm.chifco.com.templateclasse.RevendeurRecap;

public interface EncaissementRepository extends JpaRepository<Encaissement, Long> {

  @Query(
      value = "SELECT DISTINCT en.client,en.encaissement_id as encaissement_id, CASE WHEN fact.ref_facture IS NULL THEN 'Avoir' ELSE 'Facture' END AS type, av.ref_avoir_client as ref_avoir_client, fact.ref_facture as ref_facture,"
          + "  cls.first_name as first_name, cls.last_name as last_name, cls.cin as cin,en.montant_facture as montant_facture, en.type_de_payment as type_de_payment, en.date as date, CASE WHEN fact.ref_facture IS NULL THEN av.created_date ELSE fact.created_date END AS created_date,"
          + "  fact.date_echeance as date_echeance FROM encaissement en "
          + " LEFT JOIN abonnement cls ON cls.clientid = en.client "
          + " LEFT JOIN factures fact ON fact.facture_id = en.facture_id "
          + " LEFT JOIN avoir_client av ON av.avoir_id = en.avoir_client_id"
          + " WHERE en.userid = :userid AND en.has_bordereau = 0 AND "
          + " (en.facture_id is not null or av.can_revendeur_viewed = 1) AND "
          + "(en.montant_facture >= :prixmin OR :prixmin IS NULL) AND "
          + "(en.montant_facture <= :prixmax OR :prixmax IS NULL) AND "
          + "(en.created_date >= CAST(:datedebut AS datetime2) OR :datedebut IS NULL) AND "
          + "(en.created_date <= CAST(:datefin AS datetime2) OR :datefin IS NULL)",
      countQuery = "SELECT COUNT(*) FROM encaissement en "
          + " LEFT JOIN abonnement cls ON cls.clientid = en.client "
          + " LEFT JOIN factures fact ON fact.facture_id = en.facture_id "
          + " LEFT JOIN avoir_client av ON av.avoir_id = en.avoir_client_id"
          + " WHERE en.userid = :userid AND en.has_bordereau = 0 AND "
          + " (en.facture_id is not null or av.can_revendeur_viewed = 1) AND "
          + "(en.montant_facture >= :prixmin OR :prixmin IS NULL) AND "
          + "(en.montant_facture <= :prixmax OR :prixmax IS NULL) AND "
          + "(en.created_date >= CAST(:datedebut AS datetime2) OR :datedebut IS NULL) AND "
          + "(en.created_date <= CAST(:datefin AS datetime2) OR :datefin IS NULL)",
      nativeQuery = true)
  Page<EncaissementNonPayee> findbyuserAndHasbordereauAndfilter(Pageable pageable, Long userid,
      @Param("prixmin") Double prixmin, @Param("prixmax") Double prixmax,
      @Param("datedebut") String datedebut, @Param("datefin") String datefin);

  Encaissement findByencaissementId(Long parseLong);

  @Query("SELECT en FROM Encaissement en WHERE encaissementId IN :ids")
  List<Encaissement> findByencaissementByListIds(List<Long> ids);

  @Query("SELECT SUM(en.montantFacture) FROM Encaissement en WHERE en.user.userid = :idUser and en.isChifcoPayed = false and en.avoirClient.avoirId is  null")
  Double montantNonVErseFactureeRevendeurtById(Long idUser);
  
  @Query("SELECT SUM(en.montantFacture) FROM Encaissement en WHERE en.user.userid = :idUser and en.isChifcoPayed = false and en.avoirClient.avoirId  is not null")
  Double montantNonVErseAvoirRevendeurtById(Long idUser);
  
  List<Encaissement> findByidbordaureau_bordereauId(Long idbordreau);

  @Query(
      value = "SELECT COUNT(f.date) as sumfacture,SUM( CASE WHEN f.is_chifco_payed = 0  and f.facture_id  is not null THEN f.montant_facture else 0 END ) as montantnonpayer, SUM( CASE WHEN f.is_chifco_payed = 1 and f.facture_id  is not null THEN  f.montant_facture else 0 END  ) as montantpayer ,"
          + "SUM( CASE WHEN f.is_chifco_payed = 1 and f.avoir_client_id  is not null  THEN f.montant_facture else 0 END   ) as avoirConsomme,SUM( CASE WHEN f.avoir_client_id  is not null  THEN f.montant_facture else 0 END   ) as totalAvoir, ISNULL(SUM(CASE WHEN f.facture_id  is not null  THEN f.montant_facture else 0 END),0) as montant , u.first_name as firstname  , u.last_name as lastname ,u.adresse as adresse ,u.userid as  userid,u.code_user as code_user,u.plafon_revendeur as plafon_revendeur , u.enabled as enabled "
          + "From  users u   Left JOIN  Encaissement f   On u.userid = f.userid   GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.adresse ,u.plafon_revendeur , u.role_id,u.role_id,u.typeuser , u.gouvernorat_id , u.ville_id , u.enabled  , u.affected_to HAVING  u.typeuser = 'REVENDEUR' and (u.gouvernorat_id = :gouvernorat or :gouvernorat is null ) and (u.ville_id = :villes or :villes is null) and ( LOWER(u.first_name) = :nomUser or :nomUser is null ) and ( LOWER(u.last_name) = :prenomUser or :prenomUser is null ) and ( u.code_user =:refUser or :refUser is null ) and ( u.affected_to =:distributeur or :distributeur is null ) and ( u.enabled =:statusEnabled or :statusEnabled is null )  ",
      countQuery = "SELECT COUNT(u.userid)  From  users u Left JOIN  Encaissement f   On u.userid = f.userid   GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.adresse ,u.plafon_revendeur , u.role_id,u.role_id,u.typeuser , u.gouvernorat_id , u.ville_id ,u.enabled, u.affected_to   HAVING  u.typeuser = 'REVENDEUR' and (u.gouvernorat_id = :gouvernorat or :gouvernorat is null ) and (u.ville_id = :villes or :villes is null) and (LOWER( u.first_name ) = :nomUser or :nomUser is null ) and ( LOWER(u.last_name) = :prenomUser or :prenomUser is null ) and ( u.code_user =:refUser or :refUser is null ) and ( u.affected_to =:distributeur or :distributeur is null ) and ( u.enabled =:statusEnabled or :statusEnabled is null )",
      nativeQuery = true)
  Page<RevendeurRecap> findByusernotpayedgrouby(Pageable pageable, Long gouvernorat, Long villes,
      String nomUser, String prenomUser, String refUser, Long distributeur, Boolean statusEnabled);

  @Query(value = "SELECT "
  		+ "  COUNT(f.date) as sumfacture, "
  		+ "  ISNULL(SUM(CASE WHEN f.is_chifco_payed = 0 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END),0) as montantnonpayer, "
  		+ "  ISNULL(SUM(CASE WHEN f.is_chifco_payed = 1 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END),0) as montantpayer, "
  		+ "  ISNULL(SUM(CASE WHEN f.is_chifco_payed = 1 AND f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END),0) as avoirConsomme, "
  		+ "  ISNULL(SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END),0) as totalAvoir, "
  		+ "  ISNULL(SUM(CASE WHEN f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END),0) as montant, "
		+" MAX(u.first_name) as first_name, "
+ " MAX(u.last_name) as last_name, "
+ " MAX(u.adresse) as adresse, "
+ " MAX(u.userid) as userid, "
+ " MAX(u.code_user) as code_user, "
+ " MAX(u.plafon_revendeur) as plafon_revendeur "
  		+ "FROM Encaissement f "
  		+ "INNER JOIN users u ON u.userid = f.userid "
  		+ "WHERE u.userid = :Userid ", nativeQuery = true)
  RevendeurRecap findByusernotpayedgrouby(Long Userid);

  @Query(value = "select  TOP 10  * from encaissement enc "
      + "where enc.userid = :user   ORDER BY enc.created_date desc ", nativeQuery = true)
  List<Encaissement> findlastencaissementbyRevendeur(User user);

  List<Encaissement> findEncaissementByUserAndIsChifcoPayed(User user, boolean b);

  @Query(
      value = "SELECT ISNULL(SUM(montant_facture),0) FROM Encaissement f  where f.userid = :userid  and f.is_chifco_payed  = :b and f.facture_id is not null",
      nativeQuery = true)
  Double sumFactureByUserAndIschifcopayed(Long userid, boolean b);

  @Query(
      value = "SELECT ISNULL(SUM(montant_facture),0) FROM Encaissement f  where f.userid = :userid  and f.is_chifco_payed  = 0 and f.avoir_client_id is not null",
      nativeQuery = true)
  Double countAvoirByUser(Long userid);

  Page<Encaissement> findEncaissementByUserAndIsChifcoPayed(Pageable pageable, User user,
      boolean b);

  @Query(value = "SELECT COUNT(f.date) as sumfacture,"
      + "SUM( CASE WHEN f.is_chifco_payed = 0  and f.facture_id  is not null  THEN f.montant_facture else 0 END ) as montantnonpayer,"
      + " SUM( CASE WHEN f.is_chifco_payed = 1 and f.facture_id  is not null  THEN  f.montant_facture else 0 END  ) as montantpayer ,"
      + "SUM( CASE WHEN f.is_chifco_payed = 1 and f.avoir_client_id  is not null  THEN f.montant_facture else 0 END   ) as avoirConsomme,"
      + "SUM( CASE WHEN f.avoir_client_id  is not null  THEN f.montant_facture else 0 END  ) as totalAvoir ,"
      + " ISNULL(SUM(CASE WHEN f.facture_id  is not null  THEN f.montant_facture else 0 END),0) as montant ,"
      + " u.first_name as firstname  , u.last_name as lastname ,u.adresse as adresse ,u.userid  as userid, u.code_user as code_user,u.plafon_revendeur as plafon_revendeur "
      + "From users u   Left JOIN Encaissement f  On u.userid = f.userid   GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.adresse ,u.plafon_revendeur , u.role_id,u.role_id,u.typeuser,u.affected_to,u.gouvernorat_id,u.ville_id,u.enabled  "
      + "HAVING  u.typeuser = 'REVENDEUR' and u.affected_to = :userid and (u.gouvernorat_id = :gouvernorat or :gouvernorat is null ) and (u.ville_id = :villes or :villes is null) and ( LOWER(u.first_name) = :nomUser or :nomUser is null ) and ( LOWER(u.last_name) = :prenomUser or :prenomUser is null ) and ( u.code_user =:refUser or :refUser is null ) and ( u.enabled =:statusEnabled or :statusEnabled is null )",
      countQuery = "SELECT COUNT(u.userid)  From  users u Left JOIN  Encaissement f   On u.userid = f.userid   GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.adresse ,u.plafon_revendeur , u.role_id,u.role_id,u.typeuser ,u.affected_to , u.gouvernorat_id,u.ville_id,u.enabled   HAVING  u.typeuser = 'REVENDEUR' and u.affected_to= :userid and (u.gouvernorat_id = :gouvernorat or :gouvernorat is null ) and (u.ville_id = :villes or :villes is null) and ( LOWER(u.first_name) = :nomUser or :nomUser is null ) and ( LOWER(u.last_name) = :prenomUser or :prenomUser is null ) and ( u.code_user =:refUser or :refUser is null )  and ( u.enabled =:statusEnabled or :statusEnabled is null )",
      nativeQuery = true)

  Page<RevendeurRecap> findByusernotpayedgroubyDistributeur(Pageable pageable, Long userid,
      Long gouvernorat, Long villes, String nomUser, String prenomUser, String refUser,
      Boolean statusEnabled);

  // "select
  // fact.ref_facture,cls.first_name,cls.last_name,cls.cin,dmdab.type_de_payment,dmdab.date,fact.created_date,montant_payer,date_echeance
  // ,us.first_name as first_nameRevendeur , us.last_name as last_nameRevendeur , us.code_user as
  // codeRevendeur from encaissement dmdab join abonnement cls on cls.clientid = dmdab.client join
  // factures fact on fact.facture_id = dmdab.facture_id join users us "
  // "on dmdab.userid = us.userid",

  @Query(
      value = "SELECT DISTINCT en.client,en.encaissement_id as encaissement_id, CASE WHEN fact.ref_facture IS NULL THEN 'Avoir' ELSE 'Facture' END AS type, av.ref_avoir_client as ref_avoir_client, fact.ref_facture as ref_facture,"
          + "  cls.first_name as first_name, cls.last_name as last_name, cls.cin as cin,en.montant_facture as montant_facture, en.type_de_payment as type_de_payment, en.date as date, CASE WHEN fact.ref_facture IS NULL THEN av.created_date ELSE fact.created_date END AS created_date,"
          + "  fact.date_echeance as date_echeance FROM encaissement en "
          + " LEFT JOIN abonnement cls ON cls.clientid = en.client "
          + " LEFT JOIN factures fact ON fact.facture_id = en.facture_id "
          + " LEFT JOIN avoir_client av ON av.avoir_id = en.avoir_client_id "
          + "where(( en.montant_facture >= :prixmin or :prixmin is null ) and "
          + "(en.montant_facture <= :prixmax or :prixmax is null)  and "
          + "(en.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null  )   and "
          + "(en.created_date <=  CAST(:datefin AS datetime2) or :datefin is null )  "
          + " and (fact.ref_facture = :ref_facture or :ref_facture is null) and (av.ref_avoir_client = :ref_avoir or :ref_avoir is null)     )",

      countQuery = "select count(en.type_de_payment) from encaissement en join abonnement  cls  on cls.clientid = en.client   LEFT JOIN factures  fact  on fact.facture_id = en.facture_id join users us "
          + "on en.userid = us.userid"
          + " LEFT JOIN avoir_client av ON av.avoir_id = en.avoir_client_id  where(( en.montant_facture >= :prixmin or :prixmin is null ) and "
          + "(en.montant_facture <= :prixmax or :prixmax is null)  and "
          + "(en.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null  )   and "
          + "(en.created_date <=  CAST(:datefin AS datetime2) or :datefin is null )"
          + " and (fact.ref_facture = :ref_facture or :ref_facture is null ) and (av.ref_avoir_client = :ref_avoir or :ref_avoir is null)   )",
      nativeQuery = true)
  Page<EncaissementNonPayee> findAllAdmin(Pageable pageable, @Param("prixmin") Double prixmin,
      @Param("prixmax") Double prixmax, @Param("datedebut") String datedebut,
      @Param("datefin") String datefin, @Param("ref_facture") String ref_facture,
      @Param("ref_avoir") String ref_avoir);

  @Query(
      value = "SELECT COUNT(f.date) as sumfacture,SUM( CASE WHEN f.is_chifco_payed = 0 and f.facture_id  is not null THEN f.montant_facture else 0 END ) as montantnonpayer, count( CASE WHEN f.is_chifco_payed = 'true' and f.facture_id  is not null THEN 1 END) as nbrFacturepayer ,count( CASE WHEN f.is_chifco_payed = 'false' THEN 1 END) as nbrFactureNonpayer  ,  SUM( CASE WHEN f.is_chifco_payed = 1 and f.facture_id  is not null  THEN  f.montant_facture else 0 END  ) as montantpayer , ISNULL(SUM(CASE WHEN f.facture_id  is not null  THEN f.montant_facture else 0 END),0) as montant ,SUM( CASE WHEN f.is_chifco_payed = 1 and f.avoir_client_id  is not null  THEN f.montant_facture else 0 END   ) as avoirConsomme,"
          + "SUM( CASE WHEN f.avoir_client_id  is not null  THEN f.montant_facture else 0 END  ) as totalAvoir,  u.first_name as firstname  , u.last_name as lastname ,u.adresse as adresse ,u.userid as  userid,u.code_user as code_user,u.plafon_revendeur as plafon_revendeur , vls.ville_name ,gvs.gouvernorat_name, u.enabled as enabled,assignedUser.first_name as  assignedUserFirstName , assignedUser.last_name as  assignedUserLastName ,assignedUser.code_user as codeUserAssignee , count( CASE WHEN f.idbordaureau is not null THEN 1 END) as nbrBordereau   "
          + "From  users u   Left JOIN  Encaissement f   On u.userid = f.userid Left JOIN  villes vls   On vls.ville_id = u.ville_id   Left JOIN  gouvernorats gvs   On gvs.gouvernorat_id = u.gouvernorat_id  Left JOIN users assignedUser on u.affected_to = assignedUser.userid GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.adresse ,u.plafon_revendeur , u.role_id,u.role_id,u.typeuser , u.gouvernorat_id , u.ville_id ,vls.ville_name ,gvs.gouvernorat_name, u.enabled  , u.affected_to , assignedUser.first_name, assignedUser.last_name , assignedUser.code_user HAVING  u.typeuser = 'REVENDEUR' and (u.gouvernorat_id = :gouvernorat or :gouvernorat is null ) and (u.ville_id = :villes or :villes is null) and ( LOWER(u.first_name) = :nomUser or :nomUser is null ) and ( LOWER(u.last_name) = :prenomUser or :prenomUser is null ) and ( u.code_user =:refUser or :refUser is null ) and ( u.affected_to =:distributeur or :distributeur is null ) and ( u.enabled =:statusEnabled or :statusEnabled is null )  ",
      countQuery = "SELECT COUNT(u.userid)  From  users u Left JOIN  Encaissement f   On u.userid = f.userid   GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.adresse ,u.plafon_revendeur , u.role_id,u.role_id,u.typeuser , u.gouvernorat_id , u.ville_id ,u.enabled, u.affected_to , gvs.gouvernorat_name,assignedUser.first_name, assignedUser.last_name, assignedUser.code_user   HAVING  u.typeuser = 'REVENDEUR' and (u.gouvernorat_id = :gouvernorat or :gouvernorat is null ) and (u.ville_id = :villes or :villes is null) and (LOWER( u.first_name ) = :nomUser or :nomUser is null ) and ( LOWER(u.last_name) = :prenomUser or :prenomUser is null ) and ( u.code_user =:refUser or :refUser is null ) and ( u.affected_to =:distributeur or :distributeur is null ) and ( u.enabled =:statusEnabled or :statusEnabled is null )",
      nativeQuery = true)
  List<RevendeurRecap> exportRecapeRevendeur(Long gouvernorat, Long villes, String nomUser,
      String prenomUser, String refUser, Long distributeur, Boolean statusEnabled);

  @Query(value = "SELECT COUNT(f.date) as sumfacture,"
      + "SUM( CASE WHEN f.is_chifco_payed = 0  and f.facture_id  is not null  THEN f.montant_facture else 0 END ) as montantnonpayer,"
      + " SUM( CASE WHEN f.is_chifco_payed = 1  and f.facture_id  is not null   THEN  f.montant_facture else 0 END  ) as montantpayer ,"
      + "SUM( CASE WHEN f.is_chifco_payed = 1 and f.avoir_client_id  is not null  THEN f.montant_facture else 0 END   ) as avoirConsomme,"
      + "SUM( CASE WHEN f.avoir_client_id  is not null  THEN f.montant_facture else 0 END  ) as totalAvoir ,"
      + " ISNULL(SUM(CASE WHEN f.facture_id  is not null  THEN f.montant_facture else 0 END),0) as montant ,"
      + " count( CASE WHEN f.is_chifco_payed = 'true' THEN 1 END) as nbrFacturepayer ,count( CASE WHEN f.is_chifco_payed = 'false' THEN 1 END) as nbrFactureNonpayer  ,"
      + "count( CASE WHEN f.idbordaureau is not null THEN 1 END) as nbrBordereau  ,assignedUser.first_name as  assignedUserFirstName , assignedUser.last_name as  assignedUserLastName ,"
      + " u.first_name as firstname  , u.last_name as lastname ,u.adresse as adresse ,u.userid  as userid, u.code_user as code_user,u.plafon_revendeur as plafon_revendeur , vls.ville_name ,gvs.gouvernorat_name "
      + "From users u   Left JOIN Encaissement f  On u.userid = f.userid Left JOIN  villes vls   On vls.ville_id = u.ville_id   Left JOIN  gouvernorats gvs   On gvs.gouvernorat_id = u.gouvernorat_id Left JOIN users assignedUser on u.affected_to = assignedUser.userid  GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.adresse ,u.plafon_revendeur , u.role_id,u.role_id,u.typeuser,u.affected_to,u.gouvernorat_id,u.ville_id,vls.ville_name ,gvs.gouvernorat_name,assignedUser.first_name, assignedUser.last_name,u.enabled  "
      + "HAVING  u.typeuser = 'REVENDEUR' and u.affected_to = :userid and (u.gouvernorat_id = :gouvernorat or :gouvernorat is null ) and (u.ville_id = :villes or :villes is null) and ( LOWER(u.first_name) = :nomUser or :nomUser is null ) and ( LOWER(u.last_name) = :prenomUser or :prenomUser is null ) and ( u.code_user =:refUser or :refUser is null ) and ( u.enabled =:statusEnabled or :statusEnabled is null )",
      countQuery = "SELECT COUNT(u.userid)  From  users u Left JOIN  Encaissement f   On u.userid = f.userid   GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.adresse ,u.plafon_revendeur , u.role_id,u.role_id,u.typeuser ,u.affected_to , u.gouvernorat_id,u.ville_id,vls.ville_name ,gvs.gouvernorat_name,assignedUser.first_name, assignedUser.last_name,u.enabled   HAVING  u.typeuser = 'REVENDEUR' and u.affected_to= :userid and (u.gouvernorat_id = :gouvernorat or :gouvernorat is null ) and (u.ville_id = :villes or :villes is null) and ( LOWER(u.first_name) = :nomUser or :nomUser is null ) and ( LOWER(u.last_name) = :prenomUser or :prenomUser is null ) and ( u.code_user =:refUser or :refUser is null )  and ( u.enabled =:statusEnabled or :statusEnabled is null )",
      nativeQuery = true)

  List<RevendeurRecap> exportRecapeRevendeurbyDistributeur(Long userid, Long gouvernorat,
      Long villes, String nomUser, String prenomUser, String refUser, Boolean statusEnabled);

  @Query(
      value = "select  bd.reference_bordereau as referenceBordereau,fact.ref_facture, cls.first_name,cls.last_name,cls.cin,dmdab.type_de_payment,dmdab.date,fact.created_date,montant_payer,date_echeance , us.first_name as first_nameRevendeur , us.last_name as last_nameRevendeur , us.code_user as codeRevendeur ,dmdab.is_chifco_payed as isChifcoPayed , avc.ref_avoir_client , avc.created_date as creadted_date_avoir , avc.montant_avoir as montantAvoir from encaissement dmdab join abonnement  cls  on cls.clientid = dmdab.client   left join factures  fact  on fact.facture_id = dmdab.facture_id join users us"
          + "  on dmdab.userid = us.userid Left join bordereau bd on bd.bordereau_id = dmdab.idbordaureau left join avoir_client avc on avc.avoir_id = dmdab.avoir_client_id"
          + " where ( " + "( dmdab.montant_facture >= :prixmin or :prixmin is null ) and "
          + "(dmdab.montant_facture <= :prixmax or :prixmax is null)  and "
          + "(dmdab.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null  )   and "
          + "(dmdab.created_date <=  CAST(:datefin AS datetime2) or :datefin is null )"
          + " and (fact.ref_facture = :ref_facture or :ref_facture is null)  and (avC.ref_avoir_client = :ref_avoir or :ref_avoir is null)  )",

      countQuery = "select count(dmdab.type_de_payment) from encaissement dmdab join abonnement  cls  on cls.clientid = dmdab.client  left  join factures  fact  on fact.facture_id = dmdab.facture_id"
          + " left join avoir_client avc on avc.avoir_id = dmdab.avoir_client_id where ( "
          + "( dmdab.montant_facture >= :prixmin or :prixmin is null ) and "
          + "(dmdab.montant_facture <= :prixmax or :prixmax is null)  and "
          + "(dmdab.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null  )   and "
          + "(dmdab.created_date <=  CAST(:datefin AS datetime2) or :datefin is null )  "
          + " and (fact.ref_facture = :ref_facture or :ref_facture is null)  and (avc.ref_avoir_client = :ref_avoir or :ref_avoir is null) )",
      nativeQuery = true)
  List<EncaissementNonPayee> exportAllEncaisseentAdmin(@Param("prixmin") Double prixmin,
      @Param("prixmax") Double prixmax, @Param("datedebut") String datedebut,
      @Param("datefin") String datefin, @Param("ref_facture") String ref_facture, String ref_avoir);

  @Query(
      value = "select encaissement_id,fact.ref_facture, first_name,last_name,cin,dmdab.type_de_payment,dmdab.date,fact.created_date,montant_payer,date_echeance ,  avc.ref_avoir_client,avc.montant_avoir as montantAvoir from encaissement dmdab join abonnement  cls  on cls.clientid = dmdab.client left join factures fact on fact.facture_id = dmdab.facture_id  left join avoir_client avC on avc.avoir_id = dmdab.avoir_client_id "
          + "where ( " + "dmdab.userid = :userid   and dmdab.has_bordereau= 0  and "
          + "( dmdab.montant_facture >= :prixmin or :prixmin is null ) and "
          + "(dmdab.montant_facture <= :prixmax or :prixmax is null)  and "
          + "(dmdab.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null  )   and "
          + "(dmdab.created_date <=  CAST(:datefin AS datetime2) or :datefin is null )   )",

      countQuery = "select COUNT(encaissement_id) from encaissement dmdab  where ( "
          + "dmdab.userid = :userid   and dmdab.has_bordereau= 0  and "
          + "( dmdab.montant_facture >= :prixmin or :prixmin is null ) and "
          + "(dmdab.montant_facture <= :prixmax or :prixmax is null)  and "
          + "(dmdab.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null  )   and "
          + "(dmdab.created_date <=  CAST(:datefin AS datetime2) or :datefin is null )   )",
      nativeQuery = true)
  List<EncaissementNonPayee> exportEncaisseentbyUserAndHasbordereauAndfilter(Long userid,
      @Param("prixmin") Double prixmin, @Param("prixmax") Double prixmax,
      @Param("datedebut") String datedebut, @Param("datefin") String datefin);

  @Query(value = "select * from encaissement where avoir_client_id = :avoirId", nativeQuery = true)
  Encaissement getByAvoirId(Long avoirId);

  @Query(value = "select encaissement_id from encaissement en "
      + " left join avoir_client av on en.avoir_client_id = av.avoir_id where en.userid = :userId "
      + " and en.has_bordereau = 0 and (av.can_revendeur_viewed = 1 or av.can_revendeur_viewed is null) "
      + " AND (en.montant_facture >= :prixmin OR :prixmin IS NULL) "
      + " AND (en.montant_facture <= :prixmax OR :prixmax IS NULL)"
      + " AND (en.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null  ) and "
      + "(en.created_date <=  CAST(:datefin AS datetime2) or :datefin is null )",
      nativeQuery = true)
  List<Long> getAllIds(Long userId, @Param("prixmin") Double prixmin,
      @Param("prixmax") Double prixmax, String datedebut, String datefin);

  @Query(
      value = "SELECT SUM(CASE WHEN en.facture_id IS NOT NULL THEN en.montant_facture ELSE -en.montant_facture END) AS somme_montant_facture from encaissement en "
          + " left join avoir_client av on en.avoir_client_id = av.avoir_id where en.userid = :userId "
          + " and en.has_bordereau = 0 and (av.can_revendeur_viewed = 1 or av.can_revendeur_viewed is null) "
          + "AND (en.montant_facture >= :prixmin OR :prixmin IS NULL) "
          + "AND (en.montant_facture <= :prixmax OR :prixmax IS NULL)"
          + "AND (en.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null  ) and "
          + "(en.created_date <=  CAST(:datefin AS datetime2) or :datefin is null )",
      nativeQuery = true)
  Double getSumMontant(Long userId, @Param("prixmin") Double prixmin,
      @Param("prixmax") Double prixmax, String datedebut, String datefin);

  @Query(value = "select * from encaissement en "
      + "join factures f on f.facture_id = en.facture_id where "
      + "en.created_date >= CAST(:dateDebut AS datetime2) and en.created_date <= CAST(:dateFin AS datetime2) "
      + "and en.userid = :id and f.is_first_facture = 0 and en.facture_id IS NOT NULL and (f.is_facture_resilation = 'false' or  f.is_facture_resilation is null)"
      + "and en.has_bordereau = 1 and en.is_chifco_payed = 1 and f.date_de_versement IS NOT NULL ",
      nativeQuery = true)
  List<Encaissement> getEncaissementToCommission(Long id, String dateDebut, String dateFin);

  @Query(
      value = "select count(*) from encaissement en join factures f on f.facture_id = en.facture_id "
          + "where en.created_date >= CAST(:dateDebut AS datetime2) "
          + "and en.created_date <= CAST(:dateFin AS datetime2) and en.userid = :id "
          + "and f.is_first_facture = 0 and en.facture_id IS NOT NULL "
          + "and (f.is_facture_resilation = 'false' or  f.is_facture_resilation is null)"
          + "and en.is_chifco_payed = 0 and f.date_de_versement IS NULL",
      nativeQuery = true)
  Integer getNombreEncaissementNonVerse(Long id, String dateDebut, String dateFin);

  @Query(
      value = "SELECT ISNULL(SUM(montant_facture),0) FROM Encaissement f  where f.userid = :userid  and f.is_chifco_payed  = :b and f.avoir_client_id is not null",
      nativeQuery = true)
  Long sumAvoirByUserAndIschifcopayed(Long userid, boolean b);

  @Query(
      value = "SELECT en.encaissement_id as encaissementId, f.ref_facture as ref_facture, u.first_name as firstName, u.last_name as lastName, u.code_user as codeUser, u.nom_commercial as nomCommercial, a.reference_client as referenceClient, a.clientid as clientid "
          + "FROM encaissement en " + "JOIN factures f ON en.facture_id = f.facture_id "
          + "JOIN abonnement a ON f.clientid = a.clientid "
          + "JOIN demandesabonnement dm ON a.demande_abonnement = dm.demandeid "
          + "JOIN users u ON en.userid = u.userid WHERE f.is_first_facture = 1 "
          + "AND f.date_de_payement IS NOT NULL AND f.date_de_versement IS NULL "
          + "AND a.userid = :userId AND dm.created_date >= CAST(:dateDebut AS datetime2) "
          + "AND dm.created_date <= CAST(:dateFin AS datetime2)",
      nativeQuery = true)
  List<EncaissementDto> findAllFirstFactureNonVerse(Long userId, String dateDebut, String dateFin);



  @Query(
      value = "SELECT DISTINCT en.user.userid FROM Encaissement en WHERE en.createdDate <= :dateCreation and en.isChifcoPayed = false and en.facture is not null and  en.user.isLocked = false ORDER BY en.user.userid")
  List<Long> findEncaismentNotChifcoPayedDistaintByUser(Date dateCreation);


  @Query(
      value = "SELECT en FROM Encaissement en WHERE en.createdDate <= :dateCreation and en.isChifcoPayed = false and en.facture is not null and  en.user.userid = :userid")
  List<Encaissement> findEncaismentNotChifcoPayedAndUser(Date dateCreation, Long userid);


  @Query(
      value = "SELECT DISTINCT en.user.userid FROM Encaissement en WHERE en.createdDate <= :dateCreationMin and en.createdDate > :dateCreationMax and en.isChifcoPayed = false and en.facture is not null and (en.secondReminderRevendeur = :secondRimender or :secondRimender is null ) and  en.user.isLocked = false ORDER BY en.user.userid")
  List<Long> findEncaismentNotChifcoPayedDistaintByUserBeweenDateForSendNotifcation(
      Date dateCreationMin, Date dateCreationMax, Boolean secondRimender);


  @Query(
      value = "SELECT en FROM Encaissement en WHERE en.createdDate <= :dateCreationMin and en.createdDate > :dateCreationMax  and en.isChifcoPayed = false and en.facture is not null  and (en.secondReminderRevendeur = :secondRimender or :secondRimender is null ) and  en.user.userid = :userid")
  List<Encaissement> findEncaismentNotChifcoPayedByUserBeweenDateForSendNotifcation(
      Date dateCreationMin, Date dateCreationMax, Long userid, Boolean secondRimender);

  @Query(
      value = "SELECT DISTINCT en.user.userid FROM Encaissement en WHERE en.createdDate <= :dateCreationMin  and en.isChifcoPayed = false and en.facture is not null and (en.blockCompteReminderRevendeur = :thiredRimender  or :thiredRimender is null ) and en.user.isLocked = false and en.user.typeUser = 'REVENDEUR' ORDER BY en.user.userid")
  List<Long> findEncaismentNotChifcoPayedDistaintByUserBeweenDateForSendNotifcationAndBolck(
      Date dateCreationMin, Boolean thiredRimender);


  @Query(
      value = "SELECT en FROM Encaissement en WHERE en.createdDate <= :dateCreationMin   and en.isChifcoPayed = false and en.facture is not null  and (en.blockCompteReminderRevendeur = :thiredRimender  or :thiredRimender is null )  and  en.user.userid = :userid")
  List<Encaissement> findEncaismentNotChifcoPayedByUserBeweenDateForSendNotifcationAndBlock(
      Date dateCreationMin, Long userid, Boolean thiredRimender);


  @Query(value = "SELECT en FROM Encaissement en WHERE en.facture.factureId IN :facture")
  List<Encaissement> findByFacturesIds(List<Long> facture);

  @Query(value = "SELECT "
      + "COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END) as quantitehorsecheance,"
      + "COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) BETWEEN 15 AND 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END) as quantiteonecheance ,"
      + "SUM(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 15) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END) as montanttotal,"
      + " u.first_name as firstname  , u.last_name as lastname  ,u.userid  as userid, u.code_user as code_user "
      + "From users u   Left JOIN Encaissement f  On u.userid = f.userid WHERE u.typeuser = 'REVENDEUR' AND (u.affected_to = :idDist OR :idDist IS NULL) AND  "
      + "((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid) = :m  AND"
      + "(SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) BETWEEN 15 AND 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END) FROM Encaissement f WHERE f.userid = u.userid) >:x OR :m IS NULL AND :x IS NULL) AND "
      + "((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid)> :y OR :y IS NULL) AND "
      + "((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid) = :z AND "
      + "(SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) BETWEEN 15 AND 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END) FROM Encaissement f WHERE f.userid = u.userid) =:z OR :z IS NULL) "
      + "GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.typeuser  "
      + "HAVING  u.typeuser = 'REVENDEUR' ",
      countQuery = "SELECT COUNT(u.userid)  From  users u Left JOIN  Encaissement f   On u.userid = f.userid  WHERE u.typeuser = 'REVENDEUR' AND (u.affected_to = :idDist OR :idDist IS NULL) AND "
          + " ((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid) = :m  AND "
          + " (SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) BETWEEN 15 AND 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END) FROM Encaissement f WHERE f.userid = u.userid) >:x OR :m IS NULL AND :x IS NULL) AND "
          + " ((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid)> :y OR :y IS NULL) AND "
          + " ((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) BETWEEN 15 AND 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid) = :z  OR :z IS NULL)"
          + " GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.typeuser  HAVING  u.typeuser = 'REVENDEUR' ",
      nativeQuery = true)
  Page<RecapFactureLimitDTO> getRevendeursWithSummaryForAdmin(@Param("idDist") Long idDist,
      @Param("CURRENT_DATE") Date CURRENT_DATE, @Param("x") Integer x, @Param("y") Integer y,
      @Param("z") Integer z, @Param("m") Integer m, Pageable pageable);

  @Query(value = "SELECT "
      + "COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END) as quantitehorsecheance,"
      + "COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) BETWEEN 15 AND 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END) as quantiteonecheance ,"
      + "SUM(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 15) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END) as montanttotal,"
      + " u.first_name as firstname  , u.last_name as lastname  ,u.userid  as userid, u.code_user as code_user "
      + "From users u   Left JOIN Encaissement f  On u.userid = f.userid WHERE u.typeuser = 'REVENDEUR' AND (u.userid = :idRev OR :idRev IS NULL) AND  "
      + "((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid) = :m  AND"
      + "(SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) BETWEEN 15 AND 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END) FROM Encaissement f WHERE f.userid = u.userid) >:x OR :m IS NULL AND :x IS NULL) AND "
      + "((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid)> :y OR :y IS NULL) AND "
      + "((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid) = :z AND "
      + "(SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) BETWEEN 15 AND 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END) FROM Encaissement f WHERE f.userid = u.userid) =:z OR :z IS NULL) "
      + "GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.typeuser  "
      + "HAVING  u.typeuser = 'REVENDEUR' AND (u.userid = :idRev OR :idRev IS NULL) ",
      countQuery = "SELECT COUNT(u.userid)  From  users u Left JOIN  Encaissement f   On u.userid = f.userid  WHERE u.typeuser = 'REVENDEUR' AND (u.userid = :idRev OR :idRev IS NULL) AND "
          + " ((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid) = :m  AND "
          + " (SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) BETWEEN 15 AND 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END) FROM Encaissement f WHERE f.userid = u.userid) >:x OR :m IS NULL AND :x IS NULL) AND "
          + " ((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid)> :y OR :y IS NULL) AND "
          + " ((SELECT COUNT(CASE WHEN (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) > 25) AND (DATEDIFF(DAY, f.created_date, :CURRENT_DATE) BETWEEN 15 AND 25) AND f.is_chifco_payed = 0 AND f.has_bordereau = 0 AND f.facture_id IS NOT NULL THEN 1 ELSE NULL END)FROM Encaissement f WHERE f.userid = u.userid) = :z  OR :z IS NULL)"
          + " GROUP BY u.userid,u.code_user,u.first_name,u.last_name,u.typeuser  HAVING  u.typeuser = 'REVENDEUR' (u.userid = :idRev OR :idRev IS NULL) ",
      nativeQuery = true)
  RecapFactureLimitDTO getRevendeurWithSummaryForRevAlerte(@Param("idRev") Long idRev,
      @Param("CURRENT_DATE") Date CURRENT_DATE, @Param("x") Integer x, @Param("y") Integer y,
      @Param("z") Integer z, @Param("m") Integer m);



  @Query(value = "SELECT "
      + "SUM( CASE WHEN f.is_chifco_payed = 0  and f.facture_id  is not null  THEN f.montant_facture else 0 END ) as montantnonpayerAll,"
      + "SUM( CASE WHEN f.is_chifco_payed = 0  and f.facture_id  is not null  AND f.created_date BETWEEN :startOfLastMonth AND :endOfLastMonth  THEN f.montant_facture else 0 END ) as montantnonpayerLastMonth,"
      + "SUM( CASE WHEN f.is_chifco_payed = 0  and f.facture_id  is not null AND f.created_date BETWEEN :startOfThistMonth AND :endOfThisMonth THEN f.montant_facture else 0 END ) as montantnonpayerThisMonth,"
      + " SUM( CASE WHEN f.is_chifco_payed = 1 and f.facture_id  is not null  THEN  f.montant_facture else 0 END  ) as montantpayerAll ,"
      + " SUM( CASE WHEN f.is_chifco_payed = 1 and f.facture_id  is not null AND f.created_date BETWEEN :startOfLastMonth AND :endOfLastMonth THEN  f.montant_facture else 0 END  ) as montantpayerLastMonth ,"
      + " SUM( CASE WHEN f.is_chifco_payed = 1 and f.facture_id  is not null AND f.created_date BETWEEN :startOfThistMonth AND :endOfThisMonth THEN  f.montant_facture else 0 END  ) as montantpayerThisMonth ,"
      + "SUM( CASE WHEN f.is_chifco_payed = 1 and f.avoir_client_id  is not null  THEN f.montant_facture else 0 END   ) as avoirConsommeAll,"
      + "SUM( CASE WHEN f.is_chifco_payed = 1 and f.avoir_client_id  is not null AND f.created_date BETWEEN :startOfLastMonth AND :endOfLastMonth  THEN f.montant_facture else 0 END   ) as avoirConsommeLastMonth,"
      + "SUM( CASE WHEN f.is_chifco_payed = 1 and f.avoir_client_id  is not null AND f.created_date BETWEEN :startOfThistMonth AND :endOfThisMonth THEN f.montant_facture else 0 END   ) as avoirConsommeThisMonth,"
      + "SUM( CASE WHEN f.avoir_client_id  is not null  THEN f.montant_facture else 0 END  ) as totalAvoir ,"
      + "SUM( CASE WHEN f.avoir_client_id  is not null  AND f.created_date BETWEEN :startOfLastMonth AND :endOfLastMonth  THEN f.montant_facture else 0 END  ) as totalAvoirLastMonth ,"
      + "SUM( CASE WHEN f.avoir_client_id  is not null AND f.created_date BETWEEN :startOfThistMonth AND :endOfThisMonth THEN f.montant_facture else 0 END  ) as totalAvoirThisMonth ,"
      + " ISNULL(SUM(CASE WHEN f.facture_id  is not null  THEN f.montant_facture else 0 END),0) as montant, "
      + " ISNULL(SUM(CASE WHEN f.facture_id  is not null AND f.created_date BETWEEN :startOfLastMonth AND :endOfLastMonth THEN f.montant_facture else 0 END),0) as montantLastMonth, "
      + " ISNULL(SUM(CASE WHEN f.facture_id  is not null AND f.created_date BETWEEN :startOfThistMonth AND :endOfThisMonth THEN f.montant_facture else 0 END),0) as montantThisMonth "
      + "From users u INNER JOIN Encaissement f  On u.userid = f.userid WHERE (u.typeuser = :typeUser OR :typeUser IS NULL)  AND (u.affected_to = :userId OR :userId IS NULL) AND (u.userid=:revId OR :revId IS NULL) ",
      nativeQuery = true)
  Map<String, Object> getRevendeurTransactionInfoAvoir(Long userId, Long revId, String typeUser,
      @Param("startOfLastMonth") Date startOfLastMonth,
      @Param("endOfLastMonth") Date endOfLastMonth,
      @Param("startOfThistMonth") Date startOfThistMonth,
      @Param("endOfThisMonth") Date endOfThisMonth);

  // Encaissement total dashboard

  @Query(value = "SELECT "
      + " SUM( CASE WHEN e.is_chifco_payed = 1 and e.facture_id  is not null  THEN  f.montant_ht else 0 END  ) as montantpayerAll ,"
      + "SUM( CASE WHEN e.is_chifco_payed = 1 and e.avoir_client_id  is not null  THEN a.montant_ht else 0 END   ) as avoirConsommeAll,"
      + " SUM( CASE WHEN e.is_chifco_payed = 1 and e.facture_id  is not null AND e.created_date BETWEEN :startOfcurrentDate AND :endOfcurrentDate THEN  f.montant_ht else 0 END  ) as montantpayerThisMonth ,"
      + "SUM( CASE WHEN e.is_chifco_payed = 1 and e.avoir_client_id  is not null AND e.created_date BETWEEN :startOfcurrentDate AND :endOfcurrentDate THEN a.montant_ht else 0 END   ) as avoirConsommeThisMonth,"
      + " SUM( CASE WHEN e.is_chifco_payed = 1 and e.facture_id  is not null AND e.created_date BETWEEN :startOfSelectedDate AND :endOfSelectedDate THEN  f.montant_ht else 0 END  ) as montantpayerselectedMonth ,"
      + "SUM( CASE WHEN e.is_chifco_payed = 1 and e.avoir_client_id  is not null AND e.created_date BETWEEN :startOfSelectedDate AND :endOfSelectedDate  THEN a.montant_ht else 0 END   ) as avoirConsommeselectedMonth "
      + "From users u INNER JOIN Encaissement e  On u.userid = e.userid   LEFT JOIN factures f ON e.facture_id = f.facture_id "
      + " LEFT JOIN avoir_client a ON e.avoir_client_id = a.avoir_id  WHERE (u.typeuser = :typeUser OR :typeUser IS NULL)  AND (u.affected_to = :userId OR :userId IS NULL) AND (u.userid=:revId OR :revId IS NULL) ",
      nativeQuery = true)
  Map<String, Object> getEncaissementTotaldash(Long userId, Long revId, String typeUser,
      @Param("startOfSelectedDate") Date startOfSelectedDate,
      @Param("endOfSelectedDate") Date endOfSelectedDate,
      @Param("startOfcurrentDate") Date startOfcurrentDate,
      @Param("endOfcurrentDate") Date endOfcurrentDate);

  // chiffre d'affaire
  @Query(value = "SELECT " + " YEAR(f.created_date) as year, MONTH(f.created_date) as month, "
      + " SUM(CASE WHEN f.etat_facture = 1 THEN f.montant_ht ELSE 0 END) as facturepayer, "
      + " SUM(CASE WHEN a.is_client_payed = 1 THEN a.montant_ht ELSE 0 END) as totalavoir, "
      + " (SUM(CASE WHEN f.etat_facture = 1 THEN f.montant_ht ELSE 0 END) - SUM(CASE WHEN a.is_client_payed = 1 THEN a.montant_ht ELSE 0 END)) as difference "
      + "FROM users u " + "LEFT JOIN factures f ON u.userid = f.userid "
      + "LEFT JOIN avoir_client a ON u.avoir_client_id = a.avoir_id "
      + "WHERE (u.typeuser = :typeUser OR :typeUser IS NULL) "
      + "AND (u.affected_to = :userId OR :userId IS NULL) "
      + "AND (u.userid = :revId OR :revId IS NULL) "
      + "AND YEAR(f.created_date) = :year AND YEAR(a.created_date) = :year"
      + "GROUP BY YEAR(f.date), MONTH(f.date) " + "ORDER BY YEAR(f.date), MONTH(f.date)",
      nativeQuery = true)
  List<Map<String, Object>> getMonthlyChiffredaffaire(@Param("userId") Long userId,
      @Param("revId") Long revId, @Param("typeUser") String typeUser, @Param("year") int year);

  // top10 revendeurs
  @Query(value = "SELECT COUNT(f.date) as sumfacture, "
      + " SUM(CASE WHEN f.is_chifco_payed = 0 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END) as montantnonpayer, "
      + " SUM(CASE WHEN f.is_chifco_payed = 1 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END) as montantpayer, "
      + " (SELECT COUNT(*) FROM demandesabonnement d WHERE d.assigned_to = u.userid AND (d.created_date >= CAST(:startOfMonth AS datetime2) OR :startOfMonth IS NULL) AND (d.created_date <= CAST(:endOfMonth AS datetime2) OR :endOfMonth IS NULL) AND "
      + " d.decision_demande_classification_id = (SELECT classification_id FROM classification_demande cd WHERE cd.code_classification = 'ACCEPTATION')) AS countAllDemandeAccepted, "
      + " SUM(CASE WHEN f.is_chifco_payed = 1 AND f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) as avoirConsomme, "
      + " SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) as totalAvoir, "
      + " ISNULL(SUM(CASE WHEN f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END), 0) as montant, "
      + " u.first_name as firstname, u.last_name as lastname, u.adresse as adresse, u.userid as userid, "
      + " u.code_user as code_user, u.plafon_revendeur as plafon_revendeur, u.enabled as enabled, "
      + " (ISNULL(SUM(CASE WHEN f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END), 0) - "
      + " SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END)) as chiffre_affaire "
      + " FROM users u " + " LEFT JOIN Encaissement f ON u.userid = f.userid "
      + " WHERE (f.created_date >= CAST(:startOfMonth AS datetime2) OR :startOfMonth IS NULL) AND "
      + " (f.created_date <= CAST(:endOfMonth AS datetime2) OR :endOfMonth IS NULL) "
      + " GROUP BY u.userid, u.code_user, u.first_name, u.last_name, u.adresse, "
      + " u.plafon_revendeur, u.role_id, u.typeuser, u.gouvernorat_id, u.ville_id, u.enabled, u.affected_to "
      + " HAVING u.typeuser = 'REVENDEUR' AND "
      + " (u.gouvernorat_id = :gouvernorat OR :gouvernorat IS NULL) AND "
      + " (u.affected_to = :distributeur OR :distributeur IS NULL) ORDER BY chiffre_affaire DESC ",
      countQuery = "SELECT COUNT(DISTINCT u.userid) FROM users u LEFT JOIN Encaissement f ON u.userid = f.userid "
          + " WHERE (f.created_date >= CAST(:startOfMonth AS datetime2) OR :startOfMonth IS NULL) AND "
          + " (f.created_date <= CAST(:endOfMonth AS datetime2) OR :endOfMonth IS NULL) "
          + " GROUP BY u.userid, u.code_user, u.first_name, u.last_name, u.adresse, "
          + " u.plafon_revendeur, u.role_id, u.typeuser, u.gouvernorat_id, u.ville_id, u.enabled, u.affected_to "
          + " HAVING u.typeuser = 'REVENDEUR' AND "
          + " (u.gouvernorat_id = :gouvernorat OR :gouvernorat IS NULL) AND "
          + " (u.affected_to = :distributeur OR :distributeur IS NULL) ",
      nativeQuery = true)
  Page<TopRevendeur> findTopRevByChiffreAffairAndRealDemand(Pageable pageable, Long gouvernorat,
      Date startOfMonth, Date endOfMonth, Long distributeur);


  @Query(value = "SELECT COUNT(f.date) as sumfacture, "
      + " SUM(CASE WHEN f.is_chifco_payed = 0 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END) as montantnonpayer, "
      + " SUM(CASE WHEN f.is_chifco_payed = 1 AND f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END) as montantpayer, "
      + " (SELECT COUNT(*) FROM demandesabonnement d WHERE d.assigned_to = u.userid AND (d.created_date >= CAST(:startOfMonth AS datetime2) OR :startOfMonth IS NULL) AND (d.created_date <= CAST(:endOfMonth AS datetime2) OR :endOfMonth IS NULL) AND "
      + " d.decision_demande_classification_id = (SELECT classification_id FROM classification_demande cd WHERE cd.code_classification = 'ACCEPTATION')) AS countAllDemandeAccepted, "
      + " SUM(CASE WHEN f.is_chifco_payed = 1 AND f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) as avoirConsomme, "
      + " SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) as totalAvoir, "
      + " ISNULL(SUM(CASE WHEN f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END), 0) as montant, "
      + " u.first_name as firstname, u.last_name as lastname, u.adresse as adresse, u.userid as userid, "
      + " u.code_user as code_user, u.plafon_revendeur as plafon_revendeur, u.enabled as enabled, "
      + " (ISNULL(SUM(CASE WHEN f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END), 0) - "
      + " SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END)) as chiffre_affaire "
      + " FROM users u " + " LEFT JOIN Encaissement f ON u.userid = f.userid "
      + " WHERE (f.created_date >= CAST(:startOfMonth AS datetime2) OR :startOfMonth IS NULL) AND "
      + " (f.created_date <= CAST(:endOfMonth AS datetime2) OR :endOfMonth IS NULL) "
      + " GROUP BY u.userid, u.code_user, u.first_name, u.last_name, u.adresse, "
      + " u.plafon_revendeur, u.role_id, u.typeuser, u.gouvernorat_id, u.ville_id, u.enabled, u.affected_to "
      + " HAVING u.typeuser = 'REVENDEUR' AND "
      + " (u.gouvernorat_id = :gouvernorat OR :gouvernorat IS NULL) AND "
      + " (u.affected_to = :distributeur OR :distributeur IS NULL) ORDER BY countAllDemandeAccepted DESC",
      countQuery = "SELECT COUNT(DISTINCT u.userid) FROM users u LEFT JOIN Encaissement f ON u.userid = f.userid "
          + " WHERE (f.created_date >= CAST(:startOfMonth AS datetime2) OR :startOfMonth IS NULL) AND "
          + " (f.created_date <= CAST(:endOfMonth AS datetime2) OR :endOfMonth IS NULL) "
          + " GROUP BY u.userid, u.code_user, u.first_name, u.last_name, u.adresse, "
          + " u.plafon_revendeur, u.role_id, u.typeuser, u.gouvernorat_id, u.ville_id, u.enabled, u.affected_to "
          + " HAVING u.typeuser = 'REVENDEUR' AND "
          + " (u.gouvernorat_id = :gouvernorat OR :gouvernorat IS NULL) AND "
          + " (u.affected_to = :distributeur OR :distributeur IS NULL) ",
      nativeQuery = true)
  Page<TopRevendeur> findTopRevByChiffreAffairAndRealDemand2(Pageable pageable, Long gouvernorat,
      Date startOfMonth, Date endOfMonth, Long distributeur);


  @Query(value = "SELECT cs.code_user AS chefSecteurId,"
      + "CONCAT(cs.first_name, ' ', cs.last_name) AS chefSecteurName,COUNT(DISTINCT CASE WHEN revendeur.chiffreAffaire > 0 and revendeur.enabled=1 THEN revendeur.userid "
      + "  ELSE NULL END) AS activeRevendeurCount, totalRevendeur.totalRevendeurCount, (totalRevendeur.totalRevendeurCount - COUNT(DISTINCT CASE "
      + " WHEN revendeur.chiffreAffaire > 0 and revendeur.enabled=1 THEN revendeur.userid ELSE NULL  END)) AS inactiveRevendeurCount, SUM(CASE WHEN f.montant IS NOT NULL AND f.ischifco_payed = 0 THEN f.montant ELSE 0 END) AS montantPayéNonVérsé,"
      + " SUM(revendeur.chiffreAffaire) AS chiffredaffaire"
      + " FROM  users u LEFT JOIN payement f ON u.userid = f.user_id LEFT JOIN users cs ON u.affected_to = cs.userid LEFT JOIN (SELECT u1.userid,"
      + " u1.affected_to,u1.enabled, SUM(CASE WHEN f1.facture_id IS NOT NULL THEN f1.montant ELSE 0 END) - SUM(CASE WHEN f1.avoir_client_id IS NOT NULL THEN f1.montant ELSE 0 END) AS chiffreAffaire"
      + " FROM users u1 LEFT JOIN payement f1 ON u1.userid = f1.user_id  WHERE u1.typeuser='REVENDEUR'"
      + " GROUP BY u1.userid, u1.affected_to,u1.enabled "
      + " HAVING SUM(CASE WHEN f1.facture_id IS NOT NULL THEN f1.montant ELSE 0 END) -SUM(CASE WHEN f1.avoir_client_id IS NOT NULL THEN f1.montant ELSE 0 END) > 0 "
      + "    ) AS revendeur ON revendeur.userid = u.userid JOIN ( SELECT u2.affected_to AS chefSecteurId, "
      + "   COUNT(DISTINCT u2.userid) AS totalRevendeurCount FROM users u2 WHERE u2.typeuser='REVENDEUR'  AND u2.enabled = 1 GROUP BY u2.affected_to "
      + "    ) AS totalRevendeur ON totalRevendeur.chefSecteurId = cs.userid "
      + " WHERE u.typeuser='REVENDEUR' AND cs.typeuser='DISTRIBUTEUR' AND (cs.userid=:distributeur OR :distributeur IS NULL) "
      + " GROUP BY cs.code_user,cs.first_name,cs.last_name,totalRevendeur.totalRevendeurCount",
      nativeQuery = true)
  List<Object[]> findRevendeurStatsByChefSecteur(Long distributeur);

  @Query(value = "SELECT "
      + "    YEAR(COALESCE(f.created_date, e.created_date,a.created_date)) AS year,"
      + "    MONTH(COALESCE(f.created_date, e.created_date,a.created_date)) AS month,"
      + "       COALESCE(SUM(f.montant_ht), 0) AS chiffreAffaireBrute,"
      + "     COALESCE(SUM(f.montant_ht), 0) - COALESCE(SUM(a.montant_ht), 0) AS chiffreAffaireNette   ,"
      + "    COALESCE(SUM(CASE WHEN e.ischifco_payed = 1 AND e.montant IS NOT NULL  THEN e.montant ELSE 0 END)- COALESCE(SUM(CASE WHEN e.ischifco_payed=1 AND e.avoir_client_id IS NOT NULL then e.montant end), 0), 0) AS encaissementVersé,"
      + "      COALESCE(SUM( a.montant_avoir), 0) AS AvoirPayé " + " FROM factures f"
      + "    full join payement e " + "   ON e.facture_id = f.facture_id full JOIN "
      + "    avoir_client a ON a.avoir_id = e.avoir_client_id "
      + "WHERE  YEAR(COALESCE(f.created_date, e.created_date,a.created_date)) = :year GROUP BY "
      + "    YEAR(COALESCE(f.created_date, e.created_date,a.created_date)),"
      + "    MONTH(COALESCE(f.created_date, e.created_date,a.created_date)) "
      + "ORDER BY year, month;", nativeQuery = true)
  List<Object[]> calculateChiffreAffaire(@Param("year") int year);

  @Query(value = "SELECT DAY(COALESCE(f.created_date, e.created_date,a.created_date)) AS day, "
      + " COALESCE(SUM(f.montant_ht), 0) AS chiffreAffaireBrute   ,"
      + " COALESCE(SUM(f.montant_ht), 0) - COALESCE(SUM(a.montant_ht), 0) AS chiffreAffaireNette ,"
      + " COALESCE(SUM(CASE WHEN e.ischifco_payed = 1 AND e.facture_id IS NOT NULL  THEN e.montant ELSE 0 END)- COALESCE(SUM(CASE WHEN e.ischifco_payed=1 AND e.avoir_client_id IS NOT NULL then e.montant end), 0), 0) AS encaissementVersé,"
      + " COALESCE(SUM( CASE WHEN e.ischifco_payed = 1 AND e.avoir_client_id IS NOT NULL  THEN e.montant ELSE 0 END ), 0) AS AvoirPayé "
      + " FROM factures f" + "    full join payement e "
      + " ON e.facture_id = f.facture_id full JOIN "
      + " avoir_client a ON a.avoir_id = e.avoir_client_id "
      + "WHERE YEAR(COALESCE(f.created_date, e.created_date,a.created_date)) = :year AND MONTH(COALESCE(f.created_date, e.created_date,a.created_date)) = :month "
      + "GROUP BY DAY(COALESCE(f.created_date, e.created_date,a.created_date)) ORDER BY day",
      nativeQuery = true)
  List<Object[]> calculateChiffreAffaireForDaysInMonth(@Param("year") int year,
      @Param("month") int month);

  @Query(value = "SELECT "
      + " COALESCE(YEAR(f.created_date), YEAR(fact.created_date), YEAR(av.created_date)) AS year,"
      + "    COALESCE(MONTH(f.created_date), MONTH(fact.created_date),MONTH(av.created_date)) AS month,"
      + "    SUM(CASE WHEN f.ischifco_payed = 1 AND f.facture_id IS NOT NULL AND f.transaction_id IS NOT NULL THEN f.montant ELSE 0 END) AS totalFactureVerse,"
      + "    SUM(CASE WHEN f.ischifco_payed = 0 AND f.facture_id IS NOT NULL THEN f.montant ELSE 0 END) AS totalFactureNonverse,"
      + "    SUM(CASE WHEN fact.etat_facture = 0 AND fact.date_echeance >= GETDATE() THEN fact.montant_payer ELSE 0 END) AS totalfactureImpEch,"
      + "    SUM(CASE WHEN fact.etat_facture = 0 AND fact.date_echeance < GETDATE() THEN fact.montant_payer ELSE 0 END) AS TotalFactureImpNoEch,"
      + "    SUM(CASE WHEN av.avoir_id IS NOT NULL THEN av.montant_avoir ELSE 0 END) AS totalAvoir "
      + " FROM users u FULL JOIN payement f ON u.userid = f.user_id "
      + "    FULL JOIN factures fact ON fact.facture_id = f.facture_id FULL JOIN avoir_client av on av.avoir_id=avoir_client_id  WHERE "
      + "    (u.typeuser = :typeUser OR :typeUser IS NULL) "
      + "    AND (u.affected_to = :userId OR :userId IS NULL) "
      + "    AND (u.userid = :revId OR :revId IS NULL) "
      + "    AND (YEAR(f.created_date) = :year OR YEAR(fact.created_date) = :year "
      + "  OR YEAR(av.created_date) = :year) GROUP BY "
      + "    COALESCE(YEAR(f.created_date), YEAR(fact.created_date), YEAR(av.created_date)), "
      + "    COALESCE(MONTH(f.created_date), MONTH(fact.created_date), MONTH(av.created_date)); ",
      nativeQuery = true)
  List<Map<String, Object>> getMonthlyFinancialsForYear(@Param("year") int year,
      @Param("userId") Long userId, @Param("revId") Long revId, @Param("typeUser") String typeUser);

  @Query(value = "SELECT cs.userid AS chefSecteurId, "
      + "CONCAT(cs.first_name, ' ', cs.last_name) AS chefSecteurName, "
      + "SUM(CASE WHEN f.montant IS NOT NULL AND f.ischifco_payed = 0 AND f.facture_id IS NOT NULL THEN f.montant ELSE 0 END)"
      + "- SUM(CASE WHEN f.montant IS NOT NULL AND f.ischifco_payed = 0 AND f.avoir_client_id IS NOT NULL THEN f.montant ELSE 0 END) AS montantPayéNonVersé "
      + "FROM users u " + "LEFT JOIN payement f ON u.userid = f.user_id "
      + "LEFT JOIN users cs ON u.affected_to = cs.userid "
      + "WHERE u.typeuser IN('REVENDEUR','POS') "
      + "AND cs.typeuser='DISTRIBUTEUR'  AND (cs.userid=:distributeur or :distributeur IS NULL) "
      + "GROUP BY cs.userid, cs.first_name, cs.last_name " + "ORDER BY montantPayéNonVersé DESC",
      nativeQuery = true)
  List<Map<String, Object>> findMontantPayéNonVerséByChefSecteur(Long distributeur);

  // encaissement non versé
  @Query(value = "WITH EncaissementData AS ("
      + " SELECT YEAR(f.created_date) AS year_encaissement, MONTH(f.created_date) AS month_encaissement,"
      + "  SUM(CASE WHEN f.is_chifco_payed = 0 AND f.facture_id IS NOT NULL  THEN f.montant_facture  ELSE 0 END) AS totalFacture,"
      + "  SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture  ELSE 0  END) AS totalAvoirClient,"
      + "  SUM(CASE WHEN f.is_chifco_payed = 1 AND f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END) AS totalChifcoPayedAvoir "
      + "  FROM encaissement f WHERE YEAR(f.created_date) = :year GROUP BY YEAR(f.created_date), MONTH(f.created_date)),"
      + "BordereauData AS ("
      + " SELECT YEAR(b.date_versement) AS year_bordereau,MONTH(b.date_versement) AS month_bordereau,SUM(CASE "
      + "WHEN b.status = 'versement confirmé' THEN b.montant  ELSE 0  END) AS montantPayéVersé  FROM Bordereau b"
      + " WHERE YEAR(b.date_versement) = :year  GROUP BY YEAR(b.date_versement), MONTH(b.date_versement)) "
      + "SELECT e.year_encaissement AS year,e.month_encaissement AS month,(e.totalFacture - (e.totalAvoirClient - e.totalChifcoPayedAvoir)) AS montantPayéNonVersé,"
      + " COALESCE(b.montantPayéVersé, 0) AS montantPayéVersé FROM EncaissementData e "
      + "FULL OUTER JOIN BordereauData b ON e.year_encaissement = b.year_bordereau AND e.month_encaissement = b.month_bordereau ORDER BY e.year_encaissement, e.month_encaissement;",
      nativeQuery = true)
  List<Object[]> calculateEncaissementTotalAndNonVerse(@Param("year") int year);

  @Query(
      value = "WITH EncaissementData AS (SELECT DAY(f.created_date) AS day_encaissement,SUM(CASE WHEN f.is_chifco_payed = 0 AND f.facture_id IS NOT NULL "
          + " THEN f.montant_facture ELSE 0  END) AS montantNonPayer,SUM(CASE WHEN f.avoir_client_id IS NOT NULL "
          + " THEN f.montant_facture ELSE 0 END) AS totalAvoir,SUM(CASE WHEN f.is_chifco_payed = 1 AND f.avoir_client_id IS NOT NULL "
          + " THEN f.montant_facture ELSE 0 END) AS avoirConsomme  FROM encaissement f  WHERE YEAR(f.created_date) = :year AND MONTH(f.created_date) = :month"
          + "  GROUP BY DAY(f.created_date)),BordereauData AS ( SELECT  DAY(b.date_versement) AS day_bordereau,"
          + " SUM(CASE WHEN b.status = 'versement confirmé' THEN b.montant ELSE 0  END) AS montantPayéVersé"
          + " FROM Bordereau b WHERE YEAR(b.date_versement) = :year AND MONTH(b.date_versement) = :month "
          + " GROUP BY DAY(b.date_versement))"
          + "SELECT COALESCE(e.day_encaissement, b.day_bordereau) AS day,"
          + " COALESCE((e.montantNonPayer - (e.totalAvoir - e.avoirConsomme)), 0) AS montantPayéNonVersé,"
          + " COALESCE(b.montantPayéVersé, 0) AS montantPayéVersé" + " FROM EncaissementData e "
          + " FULL OUTER JOIN BordereauData b" + "  ON e.day_encaissement = b.day_bordereau "
          + " ORDER BY COALESCE(e.day_encaissement, b.day_bordereau);",
      nativeQuery = true)
  List<Object[]> calculateencaissementtotalANDNonVerseDaysInMonth(@Param("year") int year,
      @Param("month") int month);

  @Query(value = "SELECT YEAR(f.date_de_versement) AS year,"
      + "      MONTH(f.date_de_versement) AS month, SUM(e.montant_facture) AS bordereauCount "
      + "      FROM encaissement e LEFT JOIN factures f on e.facture_id=f.facture_id  "
      + "     WHERE e.is_chifco_payed = 1  AND e.facture_id IS NOT NULL "
      + "      AND f.date_de_versement IS NOT NULL AND  YEAR(f.date_de_versement) = :year "
      + "GROUP BY YEAR(f.date_de_versement), MONTH(f.date_de_versement)", nativeQuery = true)
  List<Map<String, Object>> countBordereauVersements(@Param("year") int year);

  @Query("SELECT "
      + "SUM(CASE WHEN e.isChifcoPayed = 0 AND e.createdDate >= :startOfMonth AND e.createdDate <= :endOfMonth THEN e.montantFacture ELSE 0 END) as montantnonpayer, "
      + "SUM(CASE WHEN e.isChifcoPayed = 1 AND e.facture IS NOT NULL AND f.dateDeVersement IS NOT NULL AND f.dateDeVersement >= :startOfMonth AND f.dateDeVersement <= :endOfMonth THEN e.montantFacture ELSE 0 END) as montantpayer, "
      + "SUM(CASE WHEN e.isChifcoPayed = 0 AND e.createdDate >= :startLastMonth AND e.createdDate <= :endLastMonth THEN e.montantFacture ELSE 0 END) as montantnonpayerLastMonth, "
      + "SUM(CASE WHEN e.isChifcoPayed = 1 AND e.facture IS NOT NULL AND f.dateDeVersement IS NOT NULL AND f.dateDeVersement >= :startLastMonth AND f.dateDeVersement <= :endLastMonth THEN e.montantFacture ELSE 0 END) as montantpayerLastMonth "
      + "FROM Encaissement e LEFT JOIN e.facture f ")
  Object getEncaissementSummary(@Param("startOfMonth") Date startOfMonth,
      @Param("endOfMonth") Date endOfMonth, @Param("startLastMonth") Date startLastMonth,
      @Param("endLastMonth") Date endLastMonth);

  // top revendeur par chiffre d'affaire pour chef secteur mobile
  // top1 revendeurs
  @Query(value = "SELECT TOP 1 "
      + " u.first_name as firstname, u.last_name as lastname, u.userid as userid, "
      + " u.code_user as code_user, u.plafon_revendeur as plafon_revendeur, u.enabled as enabled, "
      + " (ISNULL(SUM(CASE WHEN f.facture_id IS NOT NULL THEN f.montant_facture ELSE 0 END), 0) - "
      + " SUM(CASE WHEN f.avoir_client_id IS NOT NULL THEN f.montant_facture ELSE 0 END)) as chiffre_affaire "
      + " FROM users u " + " LEFT JOIN Encaissement f ON u.userid = f.userid "
      + " WHERE (f.created_date >= CAST(:startOfMonth AS datetime2) OR :startOfMonth IS NULL) "
      + " AND (f.created_date <= CAST(:endOfMonth AS datetime2) OR :endOfMonth IS NULL) "
      + " GROUP BY u.userid, u.code_user, u.first_name, u.last_name, "
      + " u.plafon_revendeur, u.role_id, u.typeuser, u.enabled, u.affected_to "
      + " HAVING u.typeuser = 'REVENDEUR' AND (u.affected_to = :distributeur OR :distributeur IS NULL) "
      + " ORDER BY chiffre_affaire DESC ", nativeQuery = true)
  Map<String, Object> findTopRevByChiffreAffairParChefSecteur(Date startOfMonth, Date endOfMonth,
      Long distributeur);

  @Query(value = "SELECT enc.* FROM [CRM_CHIFCO].[dbo].[encaissement] enc    JOIN factures fct ON fct.facture_id = enc.facture_id "
		+  "JOIN abonnement abn ON abn.clientid = fct.clientid WHERE fct.is_first_facture = 1   AND fct.date_de_versement IS NOT NULL "
		+ "AND abn.userid != enc.userid  AND enc.userid = :revendeur   AND DATEDIFF(day, fct.date_de_payement, fct.date_de_versement) > 15"
		+ "AND  fct.date_de_versement BETWEEN :startOfMonth AND :endOfMonth " ,  nativeQuery = true)

  List<Encaissement> findByCommsionMothAndUserAndEchanceFacturesRetard(String startOfMonth, String endOfMonth,
	      Long revendeur);

  @Query(value = "SELECT enc.* FROM [CRM_CHIFCO].[dbo].[encaissement] enc    JOIN factures fct ON fct.facture_id = enc.facture_id "
		+  "JOIN abonnement abn ON abn.clientid = fct.clientid WHERE fct.is_first_facture = 1   AND fct.date_de_versement IS NOT NULL "
		+ "AND abn.userid != enc.userid  AND enc.userid = :revendeur   AND DATEDIFF(day, fct.date_de_payement, fct.date_de_versement) <= 15"
		+ "AND  fct.date_de_versement BETWEEN :startOfMonth AND :endOfMonth " ,  nativeQuery = true)
  List<Encaissement> findByCommsionMothAndUserAndEchanceFactures(String startOfMonth, String endOfMonth,
	      Long revendeur);





}

