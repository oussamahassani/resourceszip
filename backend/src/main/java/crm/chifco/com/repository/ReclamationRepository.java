package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.model.Reclamation;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
  List<Reclamation> findByStatusNomStatut(String status);

  List<Reclamation> findByClient_clientid(Long ClientId);

  List<Reclamation> findByServiceTypeCategorytype(String serviceType);

  @Query("select rec from Reclamation rec where rec.category = :category and rec.serviceType.categorytype= :serviceType ")
  Page<Reclamation> findByCategoryAndServiceType(String category, String serviceType,
      Pageable pageable);

  @Query(value = "select cls from Reclamation cls "
      + "where ( ( cls.ref_reclamation = :ref_reclamation or :ref_reclamation is null ) "
      + "and ( cls.category = :category or :category is null ) "
      + "and ( cls.status.nomStatut = :status or :status is null ) "
      + " and( ( cls.createdby.userid = :createdbyuserid  or :createdbyuserid is null)or( cls.user.userid = :createdbyuserid  or :createdbyuserid is null) ) "
      + "and ( cls.serviceType.categorytype = :type or :type is null ) "
      + "and ( cls.createdby.affectedTo = :affecterTo or :affecterTo is null ) "
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) " + ") ")
  Page<Reclamation> findReclamationsByCreatedBy_AffectedTo(Pageable pageable, Long createdbyuserid,
      String ref_reclamation, String status, Long affecterTo, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, String category, String type);

  @Query(value = "select cls from Reclamation cls "
      + "where ( ( cls.ref_reclamation = :ref_reclamation or :ref_reclamation is null ) "
      + "and (:telfixe is null or (cls.client is not null and cls.client.telFixe = :telfixe)) "
      + "and (:identifiant is null or (cls.client is not null and cls.client.cin = :identifiant)) "
      + "and (:referencenety is null or (cls.client is not null and cls.client.referenceClient = :referencenety)) "
      + "and ( cls.referencett = :referencett or :referencett is null ) "
      + "and ( cls.category = :category or :category is null ) "
      + "and ( cls.status.nomStatut = :status or :status is null ) "
      + " and( ( cls.createdby.userid = :createdbyuserid  or :createdbyuserid is null)or( cls.user.userid = :createdbyuserid  or :createdbyuserid is null) ) "
      + "and ( cls.serviceType.categorytype = :type or :type is null ) "
      + "and ( cls.createdby.affectedTo = :affecterTo or :affecterTo is null ) "
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) " + ") ")
  Page<Reclamation> findReclamationsByCreatedBy_AffectedToClient(Pageable pageable,
      Long createdbyuserid, String ref_reclamation, String status, Long affecterTo, Date datedebut,
      Date datefin, Date dateDebutModification, Date dateFinModification, String category,
      String type, Long telfixe, String identifiant, String referencenety, String referencett);

  @Query(value = "select cls from Reclamation cls "
      + "where ( ( cls.ref_reclamation = :ref_reclamation or :ref_reclamation is null ) "
      + "and ( cls.category = :category or :category is null ) "
      + "and ( cls.serviceType.categorytype = :type or :type is null ) "
      + "and ( cls.status.nomStatut = :status or :status is null ) "
      + "and ( (cls.user.userid = :creepar or :creepar is null ) or (cls.createdby.userid = :createdbyuserid or :createdbyuserid is null)) "
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) " + ") ")
  Page<Reclamation> findReclamationRevendeurbyuserandfilter(Pageable pageable, Long createdbyuserid,
      String ref_reclamation, String status, Long creepar, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, String category, String type);

  @Query(value = "select cls from Reclamation cls "
      + "where ( ( cls.ref_reclamation = :ref_reclamation or :ref_reclamation is null ) "
      + "and (:telfixe is null or (cls.client is not null and cls.client.telFixe = :telfixe)) "
      + "and (:identifiant is null or (cls.client is not null and cls.client.cin = :identifiant)) "
      + "and (:referencenety is null or (cls.client is not null and cls.client.referenceClient = :referencenety)) "
      + "and ( cls.referencett = :referencett or :referencett is null ) "
      + "and ( cls.category = :category or :category is null ) "
      + "and ( cls.serviceType.categorytype = :type or :type is null ) "
      + "and ( cls.status.nomStatut = :status or :status is null ) "
      + "and ( (cls.user.userid = :creepar or :creepar is null ) or (cls.createdby.userid = :createdbyuserid or :createdbyuserid is null)) "
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) " + ") ")
  Page<Reclamation> findReclamationRevendeurbyuserandfilterClient(Pageable pageable,
      Long createdbyuserid, String ref_reclamation, String status, Long creepar, Date datedebut,
      Date datefin, Date dateDebutModification, Date dateFinModification, String category,
      String type, Long telfixe, String identifiant, String referencenety, String referencett);


  @Query(value = "select cls from Reclamation cls "
      + "where ( ( cls.ref_reclamation = :ref_reclamation or :ref_reclamation is null ) "
      + "and (:telfixe is null or (cls.client is not null and cls.client.telFixe = :telfixe)) "
      + "and (:identifiant is null or (cls.client is not null and cls.client.cin = :identifiant)) "
      + "and (:referencenety is null or (cls.client is not null and cls.client.referenceClient = :referencenety)) "
      + "and ( cls.referencett = :referencett or :referencett is null ) "
      + "and ( cls.statuttech = :statusTech or :statusTech is null ) "
      + "and ( cls.gouvernorat = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.central = :centralrelclamtion or :centralrelclamtion is null ) "
      + "and ( cls.etattt = :etattt or :etattt is null ) "
      + "and ( cls.source = :source or :source is null ) "
      + "and ( cls.category = :category or :category is null ) "
      + "and ( cls.serviceType.categorytype = :type or :type is null ) "
      + "and ( cls.createdby.userid = :createdbyuserid or :createdbyuserid is null ) "
      + "and ( cls.status.nomStatut = :status or :status is null ) "
      + "and ( cls.createdby.affectedTo = :affecterTo or :affecterTo is null ) "
      + "and ( cls.treatedBy.userid = :agentsav or :agentsav is null ) "
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.date_reclamationtt  >= :datereclamationttdebut or :datereclamationttdebut is null ) "
      + "and ( cls.date_reclamationtt <=  :datereclamationttfin or :datereclamationttfin is null ) "
      + "and ( cls.date_verificationfsi  >= :dateverificationfsidebut or :dateverificationfsidebut is null ) "
      + "and ( cls.date_verificationfsi <=  :dateverificationfsifin or :dateverificationfsifin is null ) "
      + "and ( cls.motif.motifId  <= :motifRec or :motifRec is null ) " + ") ")
  Page<Reclamation> findReclamationsAllClient(Pageable pageable, Long createdbyuserid,
      String ref_reclamation, String status, Long affecterTo, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, String category, String type,
      Long motifRec, Long telfixe, String identifiant, String referencenety, String referencett,
      String source, Long agentsav, String etattt, Date datereclamationttdebut,
      Date datereclamationttfin, Date dateverificationfsidebut, Date dateverificationfsifin,
      String centralrelclamtion, String gouvernorat, String statusTech);

  @Query(value = "select cls from Reclamation cls "
      + "where ( ( cls.ref_reclamation = :ref_reclamation or :ref_reclamation is null ) "
      + "and ( cls.category = :category or :category is null ) "
      + "and ( cls.serviceType.categorytype = :type or :type is null ) "
      + "and ( cls.createdby.userid = :createdbyuserid or :createdbyuserid is null ) "
      + "and ( cls.user.codeUser = :codeUser or :codeUser is null ) "
      + "and ( cls.createdby.codeUser = :codeUserCom or :codeUserCom is null ) "
      + "and ( cls.status.nomStatut = :status or :status is null ) "
      + "and ( cls.createdby.affectedTo = :affecterTo or :affecterTo is null ) "
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.motif.motifId  <= :motifRec or :motifRec is null ) " + ")")
  Page<Reclamation> findReclamationsAll(Pageable pageable, Long createdbyuserid,
      String ref_reclamation, String status, Long affecterTo, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, String category, String type,
      Long motifRec, String codeUser, String codeUserCom);



  @Query("SELECT r FROM Reclamation r LEFT JOIN FETCH r.justificatifs WHERE r.reclamationid = :id")
  Optional<Reclamation> findByIdWithJustificatifs(@Param("id") Long id);

  @Query(value = "select cls from Reclamation cls where (  "
      + " ( cls.createdby.userid = :createdbyuserid or cls.user.userid= :createdbyuserid ) "
      + " and ( :statutId is null or cls.status.statutId = :statutId ) "
      + " and ( :affectedTo is null or cls.createdby.affectedTo = :affectedTo ) "
      + " and ( :category is null or cls.category = :category ) " + " and cls.category != 'Client' "
      + ")")
  Page<Reclamation> findlistReclamationBychefsecteur(@Param("createdbyuserid") Long createdbyuserid,
      @Param("affectedTo") Long affectedTo, @Param("statutId") Long statutId, String category,
      Pageable pageable);

  Page<Reclamation> findByClient_Clientid(Long clientId, Pageable pageable);

  // For admin: all reclamations that belong to clients
  Page<Reclamation> findByClientIsNotNull(Pageable pageable);

  List<Reclamation> findByReclamationidIn(List<Long> ids);

  Reclamation findByReferencett(String referencett);

  @Query(value = "select TOP 1  * from reclamation dmdab " + "where dmdab.referencett =:referencett"
      + " ORDER BY dmdab.created_date DESC", nativeQuery = true)
  Reclamation findReclamationByuniquereferencett(String referencett);

  @Query(value = "SELECT TOP 1 r.* FROM reclamation r "
      + "INNER JOIN abonnement a ON r.client_id = a.clientid " + "WHERE a.tel_fixe = :tel "
      + "ORDER BY r.created_date DESC", nativeQuery = true)
  Reclamation findReclamationByTelFixe(@Param("tel") Long tel);

  @Query(value = "select TOP 1  * from reclamation dmdab " + "where dmdab.client_id =:clientId"
      + " ORDER BY dmdab.created_date DESC", nativeQuery = true)
  Reclamation findlastreclamationByclientId(Long clientId);

  @Query(
      value = "select TOP 1  * from reclamation dmdab  left join serviceType ser on ser.servicetype_id=dmdab.servicetype_id"
          + " where dmdab.client_id =:clientId and dmdab.category = :category and ser.categorytype= :type"
          + " ORDER BY dmdab.created_date DESC",
      nativeQuery = true)
  Reclamation findlastreclamationByclientIdServiceAndCategory(String type, String category,
      Long clientId);

  @Query("SELECT DISTINCT r.source FROM Reclamation r WHERE r.source IS NOT NULL and r.source !='' ")
  List<String> findDistinctSource();

  @Query("select rec from Reclamation rec where rec.category = 'Client' and rec.client.referenceClient=:refClient order by createdDate desc")
  Page<Reclamation> findReclamationsClients(String refClient, Pageable pageable);

  @Query("SELECT r FROM Reclamation r WHERE " + "(:category IS NULL OR r.category = :category) AND "
      + "(:serviceTypeId IS NULL OR r.serviceType.servicetypeId = :serviceTypeId) AND "
      + "(:statusId IS NULL OR r.status.statutId = :statusId)"
      + "AND (:client_id IS NULL OR (r.client is not null and r.client.clientid = :client_id)) "
      + "AND (:ref_reclamation IS NULL OR  r.ref_reclamation = :ref_reclamation) "
      + " AND ( :status IS NULL "
      + "  OR ( :status = 'Clôturé'  AND r.status.nomStatut IN ('Clôturée','Refusée','Annulée','Fermée','Résolue') )"
      + "  OR ( :status = 'Ouvert' "
      + "    AND r.status.nomStatut IN ('Ouverte','Traitement en cours','Reouverte', 'Vérification FSI','Enregistrée','Relancée') "
      + "  )  OR ( :status NOT IN ('Clôturé','Ouvert') "
      + "    AND r.status.nomStatut = :status )) "
      + " AND (:identifiant IS NULL OR (r.client is not null and r.client.cin = :identifiant)) "
      + "AND (:referencenety IS NULL OR (r.client is not null and r.client.referenceClient = :referencenety)) "
      + "AND (:source IS NULL OR  r.source = :source) "
      + "and ( r.createdDate  >= :startDate or :startDate is null ) "
      + "and ( r.createdDate <=  :endDate or :endDate is null ) "
      + "AND (:telfixe IS NULL OR (r.client is not null and r.client.telFixe = :telfixe)) ORDER BY r.createdDate DESC ")
  Page<Reclamation> findReclamationsByFilters(@Param("client_id") Long client_id,
      @Param("ref_reclamation") String ref_reclamation, @Param("category") String category,
      @Param("serviceTypeId") Long serviceTypeId, @Param("statusId") Long statusId,
      @Param("identifiant") String identifiant, @Param("telfixe") Long telfixe,
      @Param("referencenety") String referencenety, @Param("source") String source,
      @Param("startDate") Date startDate, @Param("endDate") Date endDate,
      @Param("status") String status, Pageable pageable);

  @Query(
      value = "SELECT TOP 1 r.* FROM reclamation r  left join serviceType ser  on ser.servicetype_id=r.servicetype_id "
          + "INNER JOIN abonnement a ON r.client_id = a.clientid "
          + "WHERE a.tel_fixe = :tel and r.category = :category and ser.categorytype= :type "
          + "ORDER BY r.created_date DESC",
      nativeQuery = true)
  Reclamation findReclamationByTelFixeByCategoryByClient(@Param("tel") Long tel,
      @Param("category") String category, @Param("type") String type);


  @Query("SELECT DISTINCT r.central FROM Reclamation r WHERE r.central IS NOT NULL")
  List<String> findAllDistinctCentral();

  @Query("SELECT DISTINCT r.central FROM Reclamation r WHERE r.gouvernorat = :gouv AND r.central IS NOT NULL")
  List<String> findDistinctCentralByGouvernorat(@Param("gouv") String gouvernorat);

  @Query("SELECT r FROM Reclamation r " + "WHERE r.category = 'Client' "
      + "AND r.serviceType.categorytype = 'Technique' " + "AND r.createdDate IS NOT NULL "
      + " AND r.createdDate BETWEEN :minDate AND :maxDate " + "AND r.status.nomStatut NOT IN ("
      + " 'Clôturée','Fermée','Résolue','Annulée'" + ")")
  List<Reclamation> findReclamationsForReminder(@Param("minDate") Date minDate,
      @Param("maxDate") Date maxDate);

  @Query("SELECT r FROM Reclamation r WHERE (r.isEmailSent = false Or r.isEmailSent IS NULL) AND r.gouvernorat IS NOT NULL And r.category='Client' AND r.serviceType.categorytype = 'Technique'  AND (r.createdDate <= DATEADD(DAY, -4, GETDATE()))  AND r.status.nomStatut NOT IN ( "
      + " 'Clôturée','Fermée','Résolue','Annulée') ")
  List<Reclamation> findAllUnsentReclamations();

  @Modifying
  @Query("UPDATE Reclamation r SET r.isEmailSent = true WHERE r.reclamationid IN :ids")
  void markReclamationsAsSent(@Param("ids") List<Long> ids);

  @Query(value = "select cls from Reclamation cls "
      + "where ( ( cls.ref_reclamation = :ref_reclamation or :ref_reclamation is null ) "
      + "and (:telfixe is null or (cls.client is not null and cls.client.telFixe = :telfixe)) "
      + "and (:identifiant is null or (cls.client is not null and cls.client.cin = :identifiant)) "
      + "and (:referencenety is null or (cls.client is not null and cls.client.referenceClient = :referencenety)) "
      + "and ( cls.referencett = :referencett or :referencett is null ) "
      + "and ( cls.gouvernorat = :gouvernorat or :gouvernorat is null ) "
      + "and ( cls.central = :centralrelclamtion or :centralrelclamtion is null ) "
      + "and ( cls.etattt = :etattt or :etattt is null ) "
      + "and ( cls.statuttech = :statusTech or :statusTech is null ) "
      + "and ( cls.source = :source or :source is null ) "
      + "and ( cls.category = :category or :category is null ) "
      + "and ( cls.serviceType.categorytype != :type or :type is null ) "
      + "and ( cls.createdby.userid = :createdbyuserid or :createdbyuserid is null ) "
      + "and ( cls.status.nomStatut = :status or :status is null ) "
      + "and ( cls.createdby.affectedTo = :affecterTo or :affecterTo is null ) "
      + "and ( cls.treatedBy.userid = :agentsav or :agentsav is null ) "
      + "and ( cls.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( cls.createdDate <=  :datefin or :datefin is null ) "
      + "and ( cls.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( cls.date_reclamationtt  >= :datereclamationttdebut or :datereclamationttdebut is null ) "
      + "and ( cls.date_reclamationtt <=  :datereclamationttfin or :datereclamationttfin is null ) "
      + "and ( cls.date_verificationfsi  >= :dateverificationfsidebut or :dateverificationfsidebut is null ) "
      + "and ( cls.date_verificationfsi <=  :dateverificationfsifin or :dateverificationfsifin is null ) "
      + "and ( cls.motif.motifId  <= :motifRec or :motifRec is null ) " + ") ")
  Page<Reclamation> findReclamationsAllClientAgents(Pageable pageable, Long createdbyuserid,
      String ref_reclamation, String status, Long affecterTo, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, String category, String type,
      Long motifRec, Long telfixe, String identifiant, String referencenety, String referencett,
      String source, Long agentsav, String etattt, Date datereclamationttdebut,
      Date datereclamationttfin, Date dateverificationfsidebut, Date dateverificationfsifin,
      String centralrelclamtion, String gouvernorat, String statusTech);
}
