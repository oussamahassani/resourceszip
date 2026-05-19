package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.model.Bordereau;
import crm.chifco.com.templateclasse.ListeBordereau;

public interface BordereaurRepository extends JpaRepository<Bordereau, Long> {

  @Query(
      value = "select brd.created_date, bordereau_id,montant,numfacure,status,telephone,adresse,first_name,last_name,reference_bordereau,commentaire from bordereau brd join Users us on brd.user_id = us.userid   where brd.status like %:Status%  and us.userid =  :userid",
      countQuery = "select  count(bordereau_id) from bordereau brd join Users us on brd.user_id = us.userid   where brd.status like %:Status%  and us.userid =  :userid",
      nativeQuery = true)
  Page<ListeBordereau> findByuserAndStatus(Pageable pageable, String userid, String Status);

  @Query(
      value = "select brd.created_date,bordereau_id,montant,numfacure,status,telephone,adresse,first_name,last_name,reference_bordereau,commentaire from bordereau brd join Users us on brd.user_id = us.userid   where  us.userid =  :userid",
      countQuery = "select count(bordereau_id) from bordereau brd join Users us on brd.user_id = us.userid   where  us.userid =  :userid",
      nativeQuery = true)
  Page<ListeBordereau> findByuser(Pageable pageable, String userid);

  @Query(
      value = "select  bordereau_id, type_de_payement as typeDePayement , montant,numfacure,status,telephone,adresse,first_name,last_name,reference_bordereau,commentaire from bordereau brd join Users us on brd.user_id = us.userid  "
          + "where brd.status like %:statusbordereau%   and ( ( us.ville_id = :ville or :ville is null ) "
          + "and ( us.gouvernorat_id = :gouvernorat or :gouvernorat is null ))",
      countQuery = "select  count(bordereau_id) from bordereau brd join Users us on brd.user_id = us.userid  "
          + "where brd.status like %:statusbordereau%   and ( ( us.ville_id = :ville or :ville is null ) "
          + "and ( us.gouvernorat_id = :gouvernorat or :gouvernorat is null ))",
      nativeQuery = true)
  Page<ListeBordereau> findBordereaubyadmin(Pageable pageable, String statusbordereau, Long ville,
      Long gouvernorat);

  @Query(
      value = "select  brd.created_date,bordereau_id,type_de_payement as typeDePayement,montant,numfacure,status,us.telephone,us.adresse,us.first_name,us.last_name,reference_bordereau,commentaire,cr.first_name  as confirmedByfirstName, cr.last_name as confirmedByLastName "
          + "from bordereau brd  join Users us on brd.user_id = us.userid   "
          + "join Users cr on brd.check_by = cr.userid   "
          + "where (brd.status like %:versementenanomalie%  or brd.status like '%versement confirmé%')  and ( brd.reference_bordereau = :numeroBordereau or :numeroBordereau is null) and (us.affected_to = :affecterTo or :affecterTo is null) and ( us.code_user = :userCode or :userCode is null) and (brd.created_date >= CAST(:dateDebut AS DATETIME2 ) or  :dateDebut is null) and (brd.created_date <= CAST(:dateFin AS DATETIME2 ) or  :dateFin is null) "
          + " and (brd.date_validation_brd >= CAST(:datevalideDebut AS DATETIME2 ) or  :datevalideDebut is null) and (brd.date_validation_brd <= CAST(:datevalideFin AS DATETIME2 ) or  :datevalideFin is null)",
      countQuery = "select count(bordereau_id) from bordereau brd  join Users us on brd.user_id = us.userid   where (brd.status like %:versementenanomalie%  or brd.status like '%versement confirmé%' ) and ( brd.reference_bordereau = :numeroBordereau or :numeroBordereau is null) and (us.affected_to = :affecterTo or :affecterTo is null) and ( us.code_user = :userCode or :userCode is null) and (brd.created_date >= CAST(:dateDebut AS DATETIME2 ) or  :dateDebut is null) "
      		+ "and (brd.created_date <= CAST(:dateFin AS DATETIME2 ) or  :dateFin is null)  "
    		 + " and (brd.date_validation_brd >= CAST(:datevalideDebut AS DATETIME2 ) or  :datevalideDebut is null) and (brd.date_validation_brd <= CAST(:datevalideFin AS DATETIME2 ) or  :datevalideFin is null)",
      nativeQuery = true)
  Page<ListeBordereau> findBordereaubyStatusadmin(Pageable pageable, String versementenanomalie,
      String numeroBordereau, String userCode, String affecterTo, Date dateDebut, Date dateFin ,Date datevalideDebut ,Date datevalideFin);

 

  
  @Query(
		  value = "select brd.created_date, bordereau_id, type_de_payement as typeDePayement, montant, numfacure, status, telephone, adresse, first_name, last_name, reference_bordereau, commentaire " +
		          "from bordereau brd join Users us on brd.user_id = us.userid " +
		          "where us.affected_to = :userid " +
		          "and (brd.reference_bordereau = :numeroBordereau or :numeroBordereau is null) " +
		          "and (us.affected_to = :affecterTo or :affecterTo is null) " +
		          "and (us.code_user = :userCode or :userCode is null) " +
		          "and (brd.created_date >= CAST(:dateDebut AS DATETIME2) or :dateDebut is null) " +
		          "and (brd.created_date <= CAST(:dateFin AS DATETIME2) or :dateFin is null) " +
		          "and brd.status != :Status " +
		          "and (brd.date_validation_brd >= CAST(:datevalideDebut AS DATETIME2) or :datevalideDebut is null) " +
		          "and (brd.date_validation_brd <= CAST(:datevalideFin AS DATETIME2) or :datevalideFin is null)",
		  countQuery = "select count(bordereau_id) from bordereau brd join Users us on brd.user_id = us.userid " +
		               "where us.affected_to = :userid " +
		               "and (brd.reference_bordereau = :numeroBordereau or :numeroBordereau is null) " +
		               "and (us.affected_to = :affecterTo or :affecterTo is null) " +
		               "and (us.code_user = :userCode or :userCode is null) " +
		               "and (brd.created_date >= CAST(:dateDebut AS DATETIME2) or :dateDebut is null) " +
		               "and (brd.created_date <= CAST(:dateFin AS DATETIME2) or :dateFin is null) " +
		               "and brd.status != :Status " +
		               "and (brd.date_validation_brd >= CAST(:datevalideDebut AS DATETIME2) or :datevalideDebut is null) " +
		               "and (brd.date_validation_brd <= CAST(:datevalideFin AS DATETIME2) or :datevalideFin is null)",
		  nativeQuery = true
		)
		Page<ListeBordereau> findBordereaubyDistributeur(
		    Pageable pageable,
		    @Param("userid") String userid,
		    @Param("numeroBordereau") String numeroBordereau,
		    @Param("userCode") String userCode,
		    @Param("affecterTo") String affecterTo,
		    @Param("dateDebut") Date dateDebut,
		    @Param("dateFin") Date dateFin,
		    @Param("Status") String Status,
		    @Param("datevalideDebut") Date datevalideDebut,
		    @Param("datevalideFin") Date datevalideFin
		);

  Bordereau findBybordereauId(Long id);

  @Query(
      value = "select  bordereau_id,montant,numfacure,status,telephone,adresse,first_name,last_name,reference_bordereau,commentaire from bordereau brd "
          + "join Users us on brd.user_id = us.userid " + "where  brd.userid = :userid "
          + "and ( ( us.ville_id = :ville or :ville is null ) "
          + "and ( us.gouvernorat_id = :gouvernorat or :gouvernorat is null ) "
          + "and ( brd.status = :statut or :statut is null ))",
      countQuery = "select  count(bordereau_id) from bordereau brd "
          + "join Users us on brd.user_id = us.userid " + "where  and brd.userid = :userid "
          + "and ( ( us.ville_id = :ville or :ville is null ) "
          + "and ( us.gouvernorat_id = :gouvernorat or :gouvernorat is null ) "
          + "and ( brd.status = :statut or :statut is null ))",
      nativeQuery = true)
  Page<ListeBordereau> findPaginatedbordereauxRevendeurbyDataUser(Pageable pageable, Long user,
      String statut, Long gouvernorat, Long ville);

  @Query(
      value = "select Count(*) from bordereau brd  where brd.reference_bordereau like %:codeRevendeur%  ",
      nativeQuery = true)
  int countbyReferenceBordereau(String codeRevendeur);

  @Query(
      value = "DELETE eb FROM entry_bordereau eb "
          + "JOIN encaissement en ON eb.encaissement_id = en.encaissement_id "
          + "WHERE en.avoir_client_id = :avoirID AND eb.bordereau_id = :bordereauId ",
      nativeQuery = true)
  void deleteFromEntry(Long avoirID, Long bordereauId);

  @Query(
      value = "select  bordereau_id,montant,numfacure,status,telephone,adresse,first_name,last_name,reference_bordereau,commentaire from bordereau brd "
          + "join Users us on brd.user_id = us.userid " + "where  us.affected_to = :userid "
          + "and ( ( us.ville_id = :ville or :ville is null ) "
          + "and ( us.gouvernorat_id = :governorate or :governorate is null ) "
          + "and ( brd.status = :statut or :statut is null ))",
      countQuery = "select  count(bordereau_id) from bordereau brd "
          + "join Users us on brd.user_id = us.userid " + "where us.affected_to = :userid "
          + "and ( ( us.ville_id = :ville or :ville is null ) "
          + "and ( us.gouvernorat_id = :governorate or :governorate is null ) "
          + "and ( brd.status = :statut or :statut is null ))",
      nativeQuery = true)
  Page<ListeBordereau> findPaginatedbordereauxByDistributeur(Pageable pageable, Long userid,
      String statut, Long ville, Long governorate);

  @Query(
      value = "SELECT * FROM bordereau b where b.created_date >= CAST(:dateDebut AS datetime2) AND b.created_date <= CAST(:dateFin AS datetime2) and b.status = 'versement confirmé' and b.user_id =:id",
      nativeQuery = true)
  List<Bordereau> getBorderauToCommission(Long id, String dateDebut, String dateFin);

  // Bordereau bordereauFindByReferenceBordereau(String refBd);


  Bordereau findBordereauByReferenceBordereau(String refBd);

  @Query(value = "SELECT NEXT VALUE FOR clicToPayOrderNumberSeq", nativeQuery = true)
  int getNextValueClicToPayOrderNumberSeq();


  @Query(value = "SELECT "
      + "SUM(CASE WHEN c.status IN('versement confirmé') THEN c.montant ELSE 0 END) AS totalBordAllConfirm, "
      + "SUM(CASE WHEN c.status IN('versement confirmé') AND c.createdDate BETWEEN :startOfthisMonth AND :endOfThisMonth THEN c.montant ELSE 0 END) AS bordConfirmThisMonth, "
      + "SUM(CASE WHEN c.status IN('versement confirmé') AND c.createdDate BETWEEN :startOfLastMonth AND :endOfLastMonth THEN c.montant ELSE 0 END) AS bordConfirmLastMonth, "
      + "SUM(CASE WHEN c.status IN('versement en instance') AND c.createdDate BETWEEN :startOfthisMonth AND :endOfThisMonth THEN c.montant ELSE 0 END) AS totalInstanceBordThisMonth, "
      + "SUM(CASE WHEN c.status IN('versement en instance') AND c.createdDate BETWEEN :startOfLastMonth AND :endOfLastMonth THEN c.montant ELSE 0 END) AS totalInstanceBordLastMonth, "
      + "SUM(CASE WHEN c.status IN('versement en instance') THEN c.montant ELSE 0 END) AS totalInstanceAllTime, "
      + "COUNT(CASE WHEN c.status IN('versement en instance') THEN 1 END) AS totalNumberBordInstance, "
      + "COUNT(CASE WHEN c.status IN('versement en instance') AND c.createdDate BETWEEN :startOfthisMonth AND :endOfThisMonth THEN 1 END) AS totalNumberBordInstanceThisMonth, "
      + "COUNT(CASE WHEN c.status IN('versement en instance') AND c.createdDate BETWEEN :startOfLastMonth AND :endOfLastMonth THEN 1 END) AS totalNumberBordInstanceLastMonth "
      + "FROM Bordereau c where (c.user.userid=:revId OR :revId IS NULL) AND (c.user.affectedTo=:distId OR :distId IS NULL ) ")
  Map<String, Object> calculateBordSumsDashAdmin(@Param("startOfthisMonth") Date startOfthisMonth,
      @Param("endOfThisMonth") Date endOfThisMonth,
      @Param("startOfLastMonth") Date startOfLastMonth,
      @Param("endOfLastMonth") Date endOfLastMonth, Long revId, Long distId);


  @Query(value = "SELECT "
      + "SUM(CASE WHEN c.status = 'versement confirmé' THEN c.montant ELSE 0 END) AS totalBordAllConfirm, "
      + "SUM(CASE WHEN c.status = 'versement en instance' THEN c.montant ELSE 0 END) AS totalBordAllInstance "
      + "FROM Bordereau c WHERE (c.user.userid = :revId OR :revId IS NULL) AND (c.user.affectedTo = :distId OR :distId IS NULL)")
  Map<String, Object> calculateBordSumsWithoutDate(Long revId, Long distId);

  @Query(
      value = "select brd.bordereau_id as bordereau_id, brd.montant as montant, brd.created_date as created_date, "
          + "brd.numfacure as numfacure, brd.status as status, us.telephone as telephone, us.adresse as adresse, "
          + "us.first_name as first_name, us.last_name as last_name, brd.reference_bordereau as reference_bordereau, "
          + "brd.commentaire as commentaire " + "from bordereau brd "
          + "join Users us on brd.user_id = us.userid " + "where us.affected_to = :userid "
          + "and (us.ville_id = :ville or :ville is null) "
          + "and (us.gouvernorat_id = :governorate or :governorate is null) "
          + "and (brd.status = :statut or :statut is null) "
          + "and (brd.user_id = :revId or :revId is null)",
      countQuery = "select count(*) from bordereau brd "
          + "join Users us on brd.user_id = us.userid " + "where us.affected_to = :userid "
          + "and (us.ville_id = :ville or :ville is null) "
          + "and (us.gouvernorat_id = :governorate or :governorate is null) "
          + "and (brd.status = :statut or :statut is null) "
          + "and (brd.user_id = :revId or :revId is null)",
      nativeQuery = true)
  Page<Map<String, Object>> findListbordereauxByDistributeurMobile(Long userid, String statut,
      Long ville, Long governorate, Long revId, Pageable pageable);



}
