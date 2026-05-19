package crm.chifco.com.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.DTOclass.AvoirDto;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.Facture;

public interface AvoirRepository extends JpaRepository<AvoirClient, Long> {

  @Query("SELECT new crm.chifco.com.DTOclass.AvoirDto(a.refAvoirClient, a.abonnement.referenceClient, a.createdDate, a.montantAvoir, a.motifAvoir, a.usedBy.firstName, a.usedBy.lastName, a.canRevendeurViewed, a.isClientPayed, a.has_bordereau, a.creePar.firstName, a.creePar.lastName, a.avoirId, a.dateDePaiement, a.isJestCo,a.commentaireAvoir ,  a.facture)"
      + " FROM AvoirClient a LEFT JOIN a.usedBy where (( a.abonnement.referenceClient = :abonnement or :abonnement is null ) and  ( a.commentaireAvoir LIKE CONCAT('%'+ :motifAvoir+ '%') or :motifAvoir is null )"
      + " and ( a.montantAvoir = :montantAvoir or :montantAvoir is null )"
      + " and ( a.isClientPayed = :avoirStatut or :avoirStatut is null )"
      + " and ( a.canRevendeurViewed = :authorizationAdd or :authorizationAdd is null )"
      + "and (a.refAvoirClient LIKE CONCAT('%'+ :reference+ '%') or :reference is null) "
      + "and (a.usedBy.userid = :usedBy or :usedBy is null) "
      + "and (a.isJestCo = :typeAVr or :typeAVr is null) "
      + "and (a.creePar.userid = :createdBy or :createdBy is null) "
      + "and ( a.createdDate  >= :startDate or :startDate is null ) "
      + "and ( a.createdDate <=  :endDate or :endDate is null ) "
      + "and ( a.dateDePaiement >=  :datePayementDebut or :datePayementDebut is null )"
      + "and ( a.dateDePaiement <=  :datePayementFin or :datePayementFin is null ) and a.isPublish = true )")
  Page<AvoirDto> findAvoirDetailsWithFilter(Pageable pageable, String abonnement, String motifAvoir,
      Double montantAvoir, Boolean avoirStatut, Date startDate, Date endDate, String reference,
      Long usedBy, Boolean authorizationAdd, Long createdBy, Date datePayementDebut,
      Date datePayementFin, Boolean typeAVr);

  @Query(
      value = "select a from AvoirClient a where (( a.abonnement.referenceClient = :abonnement or :abonnement is null ) and  ( a.motifAvoir LIKE CONCAT('%'+ :motifAvoir+ '%') or :motifAvoir is null )"
          + "  and ( a.montantAvoir = :montantAvoir or :montantAvoir is null )"
          + "  and ( a.isClientPayed = :avoirStatut or :avoirStatut is null )"
          + "  and ( a.canRevendeurViewed = :authorizationAdd or :authorizationAdd is null )"
          + "  and (a.refAvoirClient LIKE CONCAT('%'+ :reference+ '%') or :reference is null) "
          + "  and (a.usedBy.userid = :usedBy or :usedBy is null) "
          + "  and (a.creePar.userid = :createdBy or :createdBy is null) "
          + "  and ( a.createdDate  >= :startDate or :startDate is null ) "
          + "  and ( a.createdDate <=  :endDate or :endDate is null )"
          + "  and ( a.dateDePaiement >=  :DateDebutPayement or :DateDebutPayement is null )"
          + "  and ( a.dateDePaiement <=  :DateFinPayement or :DateFinPayement is null )  and a.isPublish = true)")
  List<AvoirClient> findAllAvoirToExport(@Param("startDate") Date startDate,
      @Param("endDate") Date endDate, @Param("reference") String reference,
      @Param("usedBy") Long usedBy, @Param("avoirStatut") Boolean avoirStatut,
      @Param("authorizationAdd") Boolean authorizationAdd, @Param("createdBy") Long createdBy,
      @Param("montantAvoir") Double montantAvoir, @Param("motifAvoir") String motifAvoir,
      @Param("abonnement") String abonnement, Date DateDebutPayement, Date DateFinPayement);

  @Query(value = "select avr from AvoirClient avr  where avr.abonnement.clientid = :clientid and avr.isPublish = 1 ")
  List<AvoirClient> findAllAvoirbyClient(Long clientid);

  @Query(
      value = "select avr from AvoirClient avr where ( avr.abonnement.telFixe = :telephone or  avr.abonnement.cin = :recherche or  avr.abonnement.referenceClient = :recherche   ) and avr.isClientPayed = false and avr.isPublish = true ")
  List<AvoirClient> findAllAvoirbyPayementRecherche(String recherche, Long telephone);

  @Query(
      value = "select avr from AvoirClient avr  where avr.abonnement.clientid = :clientid and avr.isClientPayed = false and avr.isPublish = true")
  List<AvoirClient> findAllAvoirbyClientNotPayed(Long clientid);

  @Query("select avr  FROM AvoirClient avr  where avr.avoirId  in :listId ")
  List<AvoirClient> findAllAvoirbyListeId(List<Long> listId);

  @Query("select avr  FROM AvoirClient avr  where avr.refAvoirClient  in :listRef and avr.isClientPayed = false and avr.isPublish = true ")
  List<AvoirClient> findAllAvoirbyListeReferenceAndNotPayed(List<String> listRef);

  @Query(
      value = "select * from avoir_client av where av.has_bordereau = 0 and av.can_revendeur_viewed = 0 and av.used_by_userid = :idUser and av.is_publish = 1",
      nativeQuery = true)
  List<AvoirClient> getAvoirsToAddInBordureauByIdUser(Long idUser);

  AvoirClient getByRefAvoirClient(String ref_facture);

  @Query(value = "select avr from AvoirClient avr  where avr.avoirId in :avoirsIds ")
  List<AvoirClient> findAllAvoirbyClientNotPayedInListe(List<Long> avoirsIds);

  @Query(
      value = "SELECT TOP 1 brd.reference_bordereau FROM bordereau brd "
          + "JOIN entry_bordereau ent ON brd.bordereau_id = ent.bordereau_id "
          + "JOIN encaissement en ON ent.encaissement_id = en.encaissement_id "
          + "WHERE en.avoir_client_id = :avoirId ORDER BY ent.bordereau_id DESC",
      nativeQuery = true)
  String getReferenceBorderauByAvoirId(Long avoirId);

  @Modifying
  @Transactional
  @Query(value = "update avoir_client set has_bordereau = 0 where avoir_id = :avoirId",
      nativeQuery = true)
  void updateHasBordereauByAvoirId(Long avoirId);

  AvoirClient findAvoirClientByRefAvoirClient(String refavoir);


  @Query(
      value = "select avr.avoirId from AvoirClient avr  where avr.isClientPayed = false and  avr.abonnement.clientid = :clientid and avr.isPublish = true")
  List<String> getallavoirNonPayeeByClient(Long clientid);

  @Query(
      value = "select avr from AvoirClient avr  where avr.isClientPayed = false and  avr.abonnement.clientid = :clientid and avr.isPublish = true")
  List<AvoirClient> getallAvoirNonPayeeByClient(Long clientid);

  @Query(
      value = "select  ISNULL(SUM(avr.montantAvoir),0) from AvoirClient avr  where avr.isClientPayed = false and  avr.abonnement.clientid = :clientid and avr.isPublish = true")
  Double getSumallAvoirNonPayeeByClient(Long clientid);


  AvoirClient findAvoirClientByAvoirId(Long idavoir);

  @Query(value = "SELECT "
      + "SUM(CASE WHEN a.isClientPayed = true THEN a.montantAvoir ELSE 0 END) AS allAvoirPayed, "
      + "SUM(CASE WHEN a.isClientPayed = false THEN a.montantAvoir ELSE 0 END) AS allAvoirNotPayed, "
      + "SUM(a.montantAvoir) AS totalAvoir, "
      + "SUM(CASE WHEN a.isClientPayed = false AND a.createdDate BETWEEN :startOfLastMonth AND :endOfLastMonth THEN a.montantAvoir ELSE 0 END) AS nonpayedLastMonth, "
      + "SUM(CASE WHEN a.isClientPayed = true AND a.createdDate BETWEEN :startOfLastMonth AND :endOfLastMonth THEN a.montantAvoir ELSE 0 END) AS avoirPayedLastMonth, "
      + "SUM(CASE WHEN a.createdDate BETWEEN :startOfLastMonth AND :endOfLastMonth THEN a.montantAvoir ELSE 0 END) AS totalAvoirLastMonth, "
      + "SUM(CASE WHEN a.isClientPayed = true AND YEAR(a.createdDate) = :yearFilter THEN a.montantAvoir ELSE 0 END) AS avoirPayedCurrentYear, "
      + "SUM(CASE WHEN a.isClientPayed = false AND YEAR(a.createdDate) = :yearFilter THEN a.montantAvoir ELSE 0 END) AS notPayedCurrentYear, "
      + "SUM(CASE WHEN YEAR(a.createdDate) = :yearFilter THEN a.montantAvoir ELSE 0 END) AS avoirCurrentYear "
      + "FROM AvoirClient a")
  List<Map<String, Object>> calculateAvoirSums(@Param("yearFilter") Integer yearFilter,
      @Param("startOfLastMonth") Date startOfLastMonth,
      @Param("endOfLastMonth") Date endOfLastMonth);

  
  @Query("SELECT new crm.chifco.com.DTOclass.AvoirDto(a.refAvoirClient, a.abonnement.referenceClient, a.createdDate, a.montantAvoir, a.motifAvoir, a.usedBy.firstName, a.usedBy.lastName, a.canRevendeurViewed, a.isClientPayed, a.has_bordereau, a.creePar.firstName, a.creePar.lastName, a.avoirId, a.dateDePaiement ,a.isJestCo ,a.commentaireAvoir , a.facture)"
	      + " FROM AvoirClient a LEFT JOIN a.usedBy where (( a.abonnement.referenceClient = :abonnement or :abonnement is null ) and  ( a.commentaireAvoir LIKE CONCAT('%'+ :motifAvoir+ '%') or :motifAvoir is null )"
	      + " and ( a.montantAvoir = :montantAvoir or :montantAvoir is null )"
	      + " and ( a.isClientPayed = :avoirStatut or :avoirStatut is null )"
	      + " and ( a.canRevendeurViewed = :authorizationAdd or :authorizationAdd is null )"
	      + "and (a.refAvoirClient LIKE CONCAT('%'+ :reference+ '%') or :reference is null) "
	      + "and (a.usedBy.userid = :usedBy or :usedBy is null) "
	      + "and (a.isJestCo = :typeAVr or :typeAVr is null) "
	      + "and (a.creePar.userid = :createdBy or :createdBy is null) "
	      + "and ( a.createdDate  >= :startDate or :startDate is null ) "
	      + "and ( a.createdDate <=  :endDate or :endDate is null ) "
	      + "and ( a.dateDePaiement >=  :datePayementDebut or :datePayementDebut is null )"
	      + "and ( a.dateDePaiement <=  :datePayementFin or :datePayementFin is null ) and a.isPublish = false )")
	  Page<AvoirDto> findAvoirDetailsWithFilterAndNotPublish(Pageable pageable, String abonnement, String motifAvoir,
	      Double montantAvoir, Boolean avoirStatut, Date startDate, Date endDate, String reference,
	      Long usedBy, Boolean authorizationAdd, Long createdBy, Date datePayementDebut,
	      Date datePayementFin ,Boolean typeAVr);

  @Query(value ="SELECT * FROM avoir_client f WHERE f.ref_reclamation =:referenceReclamation and  f.is_publish is not null" , nativeQuery = true)

  List<AvoirClient>  findAllAvoirByRefReclamation(String referenceReclamation);

List<AvoirClient> findAllAvoirByFactureInAndIsJestCo(List<String> referenceFacture , Boolean isJestCo);

@Query(value ="SELECT * FROM avoir_client f WHERE f.facture in (:referenceFacture) and  f.is_publish is not null" , nativeQuery = true)
List<AvoirClient> findAllAvoirByFactureIn(List<String> referenceFacture );




@Query(
	      value = "select a from AvoirClient a where (( a.abonnement.referenceClient = :abonnement or :abonnement is null ) and  ( a.motifAvoir LIKE CONCAT('%'+ :motifAvoir+ '%') or :motifAvoir is null )"
	          + "  and ( a.montantAvoir = :montantAvoir or :montantAvoir is null )"
	          + "  and ( a.isClientPayed = :avoirStatut or :avoirStatut is null )"
	          + "  and ( a.canRevendeurViewed = :authorizationAdd or :authorizationAdd is null )"
	          + "  and (a.refAvoirClient LIKE CONCAT('%'+ :reference+ '%') or :reference is null) "
	          + "  and (a.usedBy.userid = :usedBy or :usedBy is null) "
	          + "  and (a.creePar.userid = :createdBy or :createdBy is null) "
	          + "  and ( a.createdDate  >= :startDate or :startDate is null ) "
	          + "  and ( a.createdDate <=  :endDate or :endDate is null )"
	          + "  and ( a.dateDePaiement >=  :DateDebutPayement or :DateDebutPayement is null )"
	          + "  and ( a.dateDePaiement <=  :DateFinPayement or :DateFinPayement is null )  and a.isPublish = false)")
	  List<AvoirClient> findAllAvoirToExportisNotPublic(@Param("startDate") Date startDate,
	      @Param("endDate") Date endDate, @Param("reference") String reference,
	      @Param("usedBy") Long usedBy, @Param("avoirStatut") Boolean avoirStatut,
	      @Param("authorizationAdd") Boolean authorizationAdd, @Param("createdBy") Long createdBy,
	      @Param("montantAvoir") Double montantAvoir, @Param("motifAvoir") String motifAvoir,
	      @Param("abonnement") String abonnement, Date DateDebutPayement, Date DateFinPayement);


  @Query("SELECT new crm.chifco.com.DTOclass.AvoirDto(a.refAvoirClient, a.abonnement.referenceClient, a.createdDate, a.montantAvoir, a.motifAvoir, a.usedBy.firstName, a.usedBy.lastName, a.canRevendeurViewed, a.isClientPayed, a.has_bordereau, a.creePar.firstName, a.creePar.lastName, a.avoirId, a.dateDePaiement ,a.isJestCo ,a.commentaireAvoir , a.facture)"
	      + " FROM AvoirClient a LEFT JOIN a.usedBy where (( a.abonnement.referenceClient = :abonnement or :abonnement is null ) and  ( a.commentaireAvoir LIKE CONCAT('%'+ :motifAvoir+ '%') or :motifAvoir is null )"
	      + " and ( a.montantAvoir = :montantAvoir or :montantAvoir is null )"
	      + " and ( a.isClientPayed = :avoirStatut or :avoirStatut is null )"
	      + " and ( a.canRevendeurViewed = :authorizationAdd or :authorizationAdd is null )"
	      + "and (a.refAvoirClient LIKE CONCAT('%'+ :reference+ '%') or :reference is null) "
	      + "and (a.usedBy.userid = :usedBy or :usedBy is null) "
	      + "and (a.creePar.userid = :createdBy or :createdBy is null) "
	      + "and ( a.createdDate  >= :startDate or :startDate is null ) "
	      + "and ( a.createdDate <=  :endDate or :endDate is null ) "
	      + "and ( a.dateDePaiement >=  :datePayementDebut or :datePayementDebut is null )"
	      + "and ( a.dateDePaiement <=  :datePayementFin or :datePayementFin is null ) and a.isPublish is null  )")
	  Page<AvoirDto> findAvoirDetailsWithFilterAndRefused(Pageable pageable, String abonnement, String motifAvoir,
	      Double montantAvoir, Boolean avoirStatut, Date startDate, Date endDate, String reference,
	      Long usedBy, Boolean authorizationAdd, Long createdBy, Date datePayementDebut,
	      Date datePayementFin);

  @Query(value ="SELECT * FROM avoir_client f WHERE f.facture in (:referenceFacture) and f.is_jest_co =:typeAvr and  f.is_publish is not null" , nativeQuery = true)

List<AvoirClient> findAllAvoirByFactureInAndIsJestCoAndIsPublishNot(List<String> referenceFacture, boolean typeAvr);
	List<AvoirClient> findFactureByCreatedDateBetween(Date start , Date endBetween);

}
