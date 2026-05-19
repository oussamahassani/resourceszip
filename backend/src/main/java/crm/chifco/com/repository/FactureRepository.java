package crm.chifco.com.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.ApiDTO.FactureDTO;
import crm.chifco.com.ApiDTO.ListeFactureAndAvoirDTO;
import crm.chifco.com.DTOclass.ExtractionFactureDTO;
import crm.chifco.com.DTOclass.FactureDataDTO;
import crm.chifco.com.DTOclass.MoreThanOneInvoiceRecap;
import crm.chifco.com.model.EntryFactures;
import crm.chifco.com.model.Facture;
import crm.chifco.com.templateclasse.FactureNonPayee;
import crm.chifco.com.templateclasse.InvoiceAvoir;
import crm.chifco.com.templateclasse.ListeFactureAndAvoirNonPayeDTO;
import crm.chifco.com.templateclasse.Recouvrement;
import crm.chifco.com.templateclasse.SumRecouvrement;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
  Facture findByfactureId(Long factureId);

  @Query(
      value = "select  new crm.chifco.com.DTOclass.FactureDataDTO(fact.factureId,fact.ref_facture, fact.createdDate,fact.date_echeance, fact.user.firstName , fact.user.lastName , fact.montant_payer , fact.montantHt,fact.montantTva,fact.etat_facture ,fact.abonnement.referenceClient , fact.abonnement.firstName, fact.abonnement.lastName , fact.dateDePayement )  from Facture fact "
          + "where (( fact.ref_facture = :ref_facture or :ref_facture is null )"
          + "and ( fact.montant_payer >= :montantMinimum or :montantMinimum is null )"
          + "and ( fact.montant_payer <= :montantMaximum or :montantMaximum is null )"
          + "and ( fact.etat_facture = :status or :status is null )"
          + "and ( fact.dateDeDebut  >= :dateDebut or :dateDebut is null ) "
          + "and ( fact.dateDeFin  <= :dateFin or :dateFin is null ) "
          + "and ( fact.date_echeance  >= :dateEcheanceDebut or :dateEcheanceDebut is null ) "
          + "and ( fact.date_echeance  <= :dateEcheanceFin or :dateEcheanceFin is null ) "
          + "and ( fact.visibility = :y ) and (fact.user.userid = :x))")
  Page<FactureDataDTO> findByConnecteduserAndVisibility(Pageable pageable, @Param("x") Long idusers,
      @Param("y") Boolean visibility, String ref_facture, Double montantMinimum,
      Double montantMaximum, Boolean status, Date dateDebut, Date dateFin, Date dateEcheanceDebut,
      Date dateEcheanceFin);

  @Query(
      value = "select new crm.chifco.com.DTOclass.FactureDataDTO(fact.factureId,fact.ref_facture, fact.createdDate,fact.date_echeance, fact.user.firstName , fact.user.lastName , fact.montant_payer , fact.montantHt,fact.montantTva,fact.etat_facture ,fact.abonnement.referenceClient , fact.abonnement.firstName, fact.abonnement.lastName , fact.dateDePayement )  from Facture fact "
          + "where (( fact.ref_facture = :ref_facture or :ref_facture is null )"
          + "and ( fact.montant_payer >= :montantMinimum or :montantMinimum is null )"
          + "and ( fact.montant_payer <= :montantMaximum or :montantMaximum is null )"
          + "and ( fact.etat_facture = :status or :status is null )"
          + "and ( fact.createdDate  >= :dateCreationDebut or :dateCreationDebut is null ) "
          + "and ( fact.createdDate  <= :dateCreationFin or :dateCreationFin is null ) "
          + "and ( fact.date_echeance  >= :dateEcheanceDebut or :dateEcheanceDebut is null ) "
          + "and ( fact.date_echeance  <= :dateEcheanceFin or :dateEcheanceFin is null ) "
          + "and ( fact.abonnement.referenceClient  = :codeClient or :codeClient is null ) "
          + "and ( fact.abonnement.telFixe  = :telFixe or :telFixe is null ) "
          + "and ( fact.abonnement.cin  = :cin or :cin is null ) "

          + "and ( fact.visibility = :visibility  or :visibility is null )"
          + "and ( fact.dateDePayement  >= :datePayementDebut or :datePayementDebut is null )"
          + "and ( fact.dateDePayement  <= :datePayementFin or :datePayementFin is null ) and fact.isProformat = :isProformat )")
  Page<FactureDataDTO> findFacturesByvisibility(Pageable pageable, Boolean visibility,
      String ref_facture, Double montantMinimum, Double montantMaximum, Boolean status,
      Date dateCreationDebut, Date dateCreationFin, Date dateEcheanceDebut, Date dateEcheanceFin,
      String codeClient, Long telFixe, String cin, Date datePayementDebut, Date datePayementFin,
      boolean isProformat);



  Facture findFirstByAbonnement_clientid(Long demandeId);

  Facture findTopByAbonnement_clientidOrderByFactureIdDesc(Long demandeId);

  List<Facture> findByvisibilityAndAbonnement_clientid(Boolean bool, Long clsid);

  List<Facture> findByAbonnement_clientid(Long clsid);

  Facture findByvisibilityAndAbonnement_clientidAndIsFirstFacture(Boolean bool, Long clsid,
      Boolean isFirstFacture);

  @Query("SELECT f FROM Facture f where ( f.abonnement.telFixe = :telephone or  f.abonnement.cin = :recherche or  f.abonnement.referenceClient = :recherche   )  and f.etat_facture = false and f.visibility = true ")
  List<Facture> findnonpayerfacture(String recherche, Long telephone);

  @Query(
      value = " select * from (SELECT c.cin as cin ,c.clientid as clientid,c.last_name as  lastname,c.first_name as  firstname  ,(SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 0  and datediff(DAY, fact.date_echeance, getdate()) <= 30) and fact.clientid=c.clientid  and fact.etat_facture = 0   ) AS depasseDate30 ,    depasseDate60 = (SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 30  and datediff(DAY, fact.date_echeance, getdate()) <= 60) and fact.clientid=c.clientid and fact.etat_facture = 0    ),      depasseDate90 = (SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 60  and datediff(DAY, fact.date_echeance, getdate()) <= 90) and fact.clientid=c.clientid and fact.etat_facture = 0  ),  depasseDate120 = (SELECT sum(fact.montant_payer) FROM factures fact  WHERE (datediff(DAY, fact.date_echeance, getdate()) > 90  and datediff(DAY, fact.date_echeance, getdate()) <= 120 ) and fact.clientid=c.clientid and fact.etat_facture = 0   ),     plus120 = (SELECT sum(fact.montant_payer)   FROM factures fact  WHERE (datediff(DAY, fact.date_echeance, getdate()) > 120 and fact.etat_facture = 0  ) and fact.clientid=c.clientid )FROM  abonnement c  ) t  WHERE  depasseDate30 is not NULL or depasseDate60 is not NULL or depasseDate90  is not NULL or depasseDate120 is not NULL or plus120 is not NULL",
      nativeQuery = true)
  List<Recouvrement> findrecouvrement(Pageable pageable);

  @Query(
      value = "select * from (SELECT c.cin as cin ,c.clientid as clientid,c.last_name as  lastname , c.first_name as  firstname ,(SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 0  and datediff(DAY, fact.date_echeance, getdate()) <= 30) and fact.clientid=c.clientid   and fact.etat_facture = 0  ) AS depasseDate30 ,    depasseDate60 = (SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 30  and datediff(DAY, fact.date_echeance, getdate()) <= 60) and fact.clientid=c.clientid  and fact.etat_facture = 0   ),      depasseDate90 = (SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 60  and datediff(DAY, fact.date_echeance, getdate()) <= 90) and fact.clientid=c.clientid  and fact.etat_facture = 0 ),  depasseDate120 = (SELECT sum(fact.montant_payer) FROM factures fact  WHERE (datediff(DAY, fact.date_echeance, getdate()) > 90  and datediff(DAY, fact.date_echeance, getdate()) <= 120 ) and fact.clientid=c.clientid and fact.etat_facture = 0   ),     plus120 = (SELECT sum(fact.montant_payer)   FROM factures fact  WHERE (datediff(DAY, fact.date_echeance, getdate()) > 120  ) and fact.clientid=c.clientid and fact.etat_facture = 0  )FROM abonnement c    ) t  WHERE  depasseDate30 is not NULL or depasseDate60 is not NULL or depasseDate90  is not NULL or depasseDate120 is not NULL or plus120 is not NULL",
      nativeQuery = true)
  List<Recouvrement> findallrecouvrement();

  @Query(value = "select * from ("
      + "SELECT totalDate30 = (SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 0  and datediff(DAY, fact.date_echeance, getdate()) <= 30) and (fact.etat_facture = 0  )),"
      + "totalDate60 = (SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 30  and datediff(DAY, fact.date_echeance, getdate()) <= 60) and (fact.etat_facture = 0  )),"
      + "totalDate90 = (SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 60  and datediff(DAY, fact.date_echeance, getdate()) <= 90) and (fact.etat_facture = 0  )),"
      + "totalDate120 = (SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 90  and datediff(DAY, fact.date_echeance, getdate()) <= 120) and (fact.etat_facture = 0  )),"
      + "totalDatePlus120 = (SELECT sum(fact.montant_payer) FROM factures fact WHERE (datediff(DAY, fact.date_echeance, getdate()) > 120) and (fact.etat_facture = 0  ))"
      + ")t", nativeQuery = true)
  SumRecouvrement sumRecouvrementParDate();

  @Query(
      value = "select sum (montant_payer)  as montantTotal , count (montant_payer)  as NombreFacture FROM factures fact  Left JOIN abonnement c On fact.clientid = c.clientid   where fact.etat_facture = 0  and datediff(DAY, fact.date_echeance, getdate()) > 0 and fact.clientid IS NOT NULL",
      nativeQuery = true)
  FactureNonPayee findmontantFactureNonPayee();

  @Query(
      value = "select sum (montant_payer)  as montantTotal FROM factures where factures.facture_id  in :factureids ",
      nativeQuery = true)
  Double findSumByListFactureIdS(@Param("factureids") List<String> factureids);

  @Query("SELECT NEW crm.chifco.com.DTOclass.ExtractionFactureDTO(" + "fact, " + // Include the
                                                                                 // owner entity
  // (Facture) in the select list
      "fact.factureId, " + "fact.ref_facture, " + "fact.montant_payer, " + "fact.montantHt, "
      + "fact.montantTva, " + "fact.prixBaseTva, " + "fact.timbrefiscale, " + "fact.etat_facture, "
      + "fact.visibility, " + "fact.isDelete, " + "fact.isFirstFacture, " + "fact.createdDate, "
      + "fact.modifiedDate, " + "fact.date_echeance, " + "fact.user.firstName, "
      + "fact.user.lastName, " + "fact.user.codeUser, " + "fact.abonnement.referenceClient, "
      + "fact.abonnement.firstName, " + "fact.abonnement.lastName, " + "fact.abonnement.telFixe, "
      + "fact.dateDeVersement, " + "user.firstName, " + "user.lastName, " + "user.codeUser" + ") "
      + "FROM Facture fact " + "LEFT JOIN User user ON fact.user.affectedTo = user.userid "
      + "WHERE fact.factureId IN :factureids")
  List<ExtractionFactureDTO> findfactureByListFactureIdS(List<Long> factureids);

  @Query("select new crm.chifco.com.DTOclass.FactureDataDTO(fact.factureId,fact.ref_facture,fact.montant_payer,fact.montantHt,fact.montantTva,fact.prixBaseTva, fact.timbrefiscale,fact.etat_facture,fact.visibility,fact.isDelete,fact.isFirstFacture, fact.createdDate,fact.modifiedDate,fact.date_echeance, fact.user.firstName , fact.user.lastName , fact.user.codeUser ,fact.abonnement.referenceClient , fact.abonnement.firstName, fact.abonnement.lastName , fact.abonnement.telFixe , fact.dateDeVersement , user.firstName , user.lastName , user.codeUser)   FROM Facture fact  LEFT JOIN User  user ON fact.user.affectedTo =  user.userid  where fact.factureId  in :factureids ")
  List<FactureDataDTO> findFactureDataDTOByListFactureIds(
      @Param("factureids") List<Long> factureids);

  @Query("select f  FROM Facture f   where f.factureId  in :factureids ")
  List<Facture> findfactureByListFactureids(@Param("factureids") List<Long> factureids);

  @Query(
      value = "select *  FROM factures f join abonnement ab on  f.clientid = ab.clientid  where  f.date_echeance = :datedebut  and f.etat_facture = 'false' and ab.enabled = 'true' and (f.first_reminder = :firstReminder or :firstReminder is null )  and (f.second_reminder = :secondReminder  or :secondReminder is null ) and (f.suspension_services = :suspensionServices or :suspensionServices is null ) and (ab.statutid != '288' and ab.statutid != '279')",
      nativeQuery = true)
  public List<Facture> listFactureRapelles(@Param("datedebut") String datedebut,
      @Param("firstReminder") Boolean firstReminder,
      @Param("secondReminder") Boolean secondReminder,
      @Param("suspensionServices") Boolean suspensionServices);

  @Query("SELECT  f  FROM  Facture f where f.date_echeance BETWEEN :dateDebutProchainFacture AND :finDateProchainFacture and f.etat_facture= 0 and f.abonnement.statut.nomStatut = :nomStatut and (third_reminder_reactivate = :thirdReminderReactivate or :thirdReminderReactivate is null ) ")
  List<Facture> rappelleSmsReactivate(Date dateDebutProchainFacture, Date finDateProchainFacture,
      String nomStatut, Boolean thirdReminderReactivate);

  @Query(
      value = "SELECT  *  FROM  factures f where f.clientid = :clientid and f.is_first_facture = 'true'",
      nativeQuery = true)
  Facture ChekIfIsFirstFactureExist(Long clientid);

  @Query(value = "select fact.montant_payer from Facture fact where fact.factureId = :parseLong")
  Double findMontanTTCByIdFacture(long parseLong);

  // @Query(value = "select f from Facture f")
  @EntityGraph(attributePaths = {"abonnement.firstName", "abonnement.lastName"},
      type = EntityGraph.EntityGraphType.LOAD)
  List<Facture> findFacturesssByvisibility(Boolean visibility);

  @Query(value = "select fact from Facture fact "
      + "where (( fact.ref_facture = :ref_facture or :ref_facture is null )"
      + "and ( fact.montant_payer >= :montantMinimum or :montantMinimum is null )"
      + "and ( fact.montant_payer <= :montantMaximum or :montantMaximum is null )"
      + "and ( fact.etat_facture = :status or :status is null )"
      + "and ( fact.createdDate  >= :dateCreationDebut or :dateCreationDebut is null ) "
      + "and ( fact.createdDate  <= :dateCreationFin or :dateCreationFin is null ) "
      + "and ( fact.date_echeance  >= :dateEcheanceDebut or :dateEcheanceDebut is null ) "
      + "and ( fact.date_echeance  <= :dateEcheanceFin or :dateEcheanceFin is null ) "
      + "and ( fact.abonnement.referenceClient  = :codeClient or :codeClient is null ) "
      + "and ( fact.abonnement.telFixe  = :telFixe or :telFixe is null ) "
      + "and ( fact.abonnement.cin  = :cin or :cin is null ) "
      + "and ( fact.visibility = :visibility ))")
  List<Facture> findAllToExport(Boolean visibility, String ref_facture, Double montantMinimum,
      Double montantMaximum, Boolean status, Date dateCreationDebut, Date dateCreationFin,
      Date dateEcheanceDebut, Date dateEcheanceFin, String codeClient, Long telFixe, String cin);

  @Query(value = "select  fact.factureId from Facture fact "
      + "where (( fact.ref_facture = :ref_facture or :ref_facture is null )"
      + "and ( fact.montant_payer >= :montantMinimum or :montantMinimum is null )"
      + "and ( fact.montant_payer <= :montantMaximum or :montantMaximum is null )"
      + "and ( fact.etat_facture = :status or :status is null )"
      + "and ( fact.createdDate  >= :dateCreationDebut or :dateCreationDebut is null ) "
      + "and ( fact.createdDate  <= :dateCreationFin or :dateCreationFin is null ) "
      + "and ( fact.date_echeance  >= :dateEcheanceDebut or :dateEcheanceDebut is null ) "
      + "and ( fact.date_echeance  <= :dateEcheanceFin or :dateEcheanceFin is null ) "
      + "and ( fact.abonnement.referenceClient  = :codeClient or :codeClient is null ) "
      + "and ( fact.abonnement.telFixe  = :telFixe or :telFixe is null ) "
      + "and ( fact.abonnement.cin  = :cin or :cin is null ) "
      + "and ( fact.visibility = :visibility or :visibility is null  )"
      + "and ( fact.dateDePayement  >= :datePayementDebut or :datePayementDebut is null )"
      + "and ( fact.dateDePayement  <= :datePayementFin or :datePayementFin is null ) and fact.isProformat =:isProformat)")
  List<Long> findAllToExportWithFiltreAdminUser(Boolean visibility, String ref_facture,
      Double montantMinimum, Double montantMaximum, Boolean status, Date dateCreationDebut,
      Date dateCreationFin, Date dateEcheanceDebut, Date dateEcheanceFin, String codeClient,
      Long telFixe, String cin, Date datePayementDebut, Date datePayementFin, boolean isProformat);


  @Query(value = "select fact.factureId from Facture fact "
      + "where (( fact.ref_facture = :ref_facture or :ref_facture is null )"
      + "and ( fact.montant_payer >= :montantMinimum or :montantMinimum is null )"
      + "and ( fact.montant_payer <= :montantMaximum or :montantMaximum is null )"
      + "and ( fact.etat_facture = :status or :status is null )"
      + "and ( fact.createdDate  >= :dateCreationDebut or :dateCreationDebut is null ) "
      + "and ( fact.createdDate  <= :dateCreationFin or :dateCreationFin is null ) "
      + "and ( fact.date_echeance  >= :dateEcheanceDebut or :dateEcheanceDebut is null ) "
      + "and ( fact.date_echeance  <= :dateEcheanceFin or :dateEcheanceFin is null ) "
      + "and ( fact.abonnement.referenceClient  = :codeClient or :codeClient is null ) "
      + "and ( fact.abonnement.telFixe  = :telFixe or :telFixe is null ) "
      + "and ( fact.abonnement.cin  = :cin or :cin is null ) "
      + "and ( fact.visibility = :visibility ))")
  List<Long> findAllToExportWithFiltreOtherUser(Boolean visibility, String ref_facture,
      Double montantMinimum, Double montantMaximum, Boolean status, Date dateCreationDebut,
      Date dateCreationFin, Date dateEcheanceDebut, Date dateEcheanceFin, String codeClient,
      Long telFixe, String cin);

  @Query(
      value = "select fact from Facture fact where fact.visibility = :visibility and fact.abonnement.clientid = :clsid and fact.etat_facture = :etatFacture")
  List<Facture> findlisteFacturesNonPayee(Boolean visibility, Long clsid, Boolean etatFacture);

  @Query("SELECT f FROM Facture f where ( f.abonnement.telFixe = :telephone or  f.abonnement.cin = :recherche or  f.abonnement.referenceClient = :recherche   )  and f.etat_facture = false and f.visibility = true ")
  List<Facture> findListeFactureNonPayee(String recherche, Long telephone);

  @Query("SELECT f FROM Facture f where f.etat_facture = false and f.visibility = true  and f.ref_facture = :referenceFacture ")
  Facture findFactureNonPayeeByReference(String referenceFacture);

  @Query("SELECT f FROM Facture f where f.etat_facture = true and f.visibility = true  and f.ref_facture = :referenceFacture ")
  Facture findFacturePayeeByReference(String referenceFacture);

  @Query("SELECT f FROM Facture f where f.etat_facture = false and f.ref_facture in :factures ")
  List<Facture> findListeFactureNonPayeeByRefFacture(List<String> factures);

  @Query("SELECT f FROM Facture f where ( f.abonnement.cin = :recherche)  and f.etat_facture = false and f.visibility = true")
  List<Facture> findListeFactureNonPayeeByCin(String recherche);

  @Query("SELECT f FROM Facture f where ( f.abonnement.telFixe = :telephone)  and f.etat_facture = false and f.visibility = true")
  List<Facture> findListeFactureNonPayeeByFixeNumber(Long telephone);

  @Query(
      value = "select ISNULL(SUM(fact.montant_payer),0)  from Facture fact where fact.abonnement.clientid = :demandeAbonnement  and fact.etat_facture = false ")
  Double getSumFactureNonPayee(Long demandeAbonnement);

  @Query(
      value = "select fact.factureId  from Facture fact where fact.abonnement.clientid = :clientid and fact.isFirstFacture=true and fact.etat_facture = false ")
  Long getFirstFactureIdNotPayed(Long clientid);

  @Query("SELECT new crm.chifco.com.ApiDTO.FactureDTO(f.ref_facture, f.dateDeDebut, f.dateDeFin, f.etat_facture, f.montant_payer, date_echeance) "
      + "FROM Facture f WHERE f.abonnement.clientid = :abonnementId "
      + "AND (f.ref_facture LIKE CONCAT('%', :search, '%') "
      + "OR f.dateDeDebut LIKE CONCAT('%', :search, '%') "
      + "OR f.dateDeFin LIKE CONCAT('%', :search, '%') "
      + "OR f.date_echeance LIKE CONCAT('%', :search, '%') "
      + "OR f.montant_payer LIKE CONCAT('%', :search, '%'))")
  Page<FactureDTO> findByAbonnementId(Pageable page, Long abonnementId, String search);

  @Query("select fact from Facture fact where fact.ref_facture = :ref_facture")
  Facture findByRefFacture(String ref_facture);

  @Query(value = "SELECT * FROM ("
      + "  SELECT   f.facture_id, NULL, f.ref_facture, f.date_de_debut, f.date_de_fin, f.montant_payer, f.date_echeance, f.etat_facture,'facture' as typeFacture"
      + "  FROM factures f " + " JOIN abonnement ab" + "  on ab.clientid = f.clientid"
      + " WHERE ab.clientid = :abonnementId AND (f.ref_facture LIKE CONCAT('%', :search, '%')"
      + " OR f.date_de_debut LIKE CONCAT('%', :search, '%') "
      + " OR f.date_de_fin LIKE CONCAT('%', :search, '%')  "
      + " OR f.date_echeance LIKE CONCAT('%', :search, '%') "
      + " OR f.montant_payer LIKE CONCAT('%', :search, '%'))" + " UNION "
      + " SELECT NULL, a.avoir_id, a.ref_avoir_client, a.created_date, null, -a.montant_avoir, null,  a.is_client_payed as etat_facture , 'avoire' as typeFacture"
      + " FROM avoir_client a " + " JOIN abonnement ab " + "  on ab.clientid = a.abonnement_id"
      + "   WHERE ab.clientid = :abonnementId  and a.is_publish = 'true' AND (a.created_date LIKE CONCAT('%', :search, '%') OR a.montant_avoir LIKE CONCAT('%', :search, '%') OR a.ref_avoir_client LIKE CONCAT('%', :search, '%') )) AS f (facture_id, avoir_id, ref_facture, date_de_debut, date_de_fin, montant_payer, date_echeance, etat_facture , typeFacture)  ",
      countQuery = "SELECT count(*) FROM ("
          + "  SELECT   f.facture_id, NULL, f.ref_facture, f.date_de_debut, f.date_de_fin, f.montant_payer, f.date_echeance, f.etat_facture "
          + "  FROM factures f " + " JOIN abonnement ab" + "  on ab.clientid = f.clientid"
          + "  WHERE ab.clientid = :abonnementId "
          + "AND (f.ref_facture LIKE CONCAT('%', :search, '%') "
          + "OR f.date_de_debut LIKE CONCAT('%', :search, '%') "
          + "OR f.date_de_fin LIKE CONCAT('%', :search, '%') "
          + "OR f.date_echeance LIKE CONCAT('%', :search, '%') "
          + "OR f.montant_payer LIKE CONCAT('%', :search, '%')) UNION "
          + " SELECT NULL, a.avoir_id, a.ref_avoir_client, null, null, -a.montant_avoir, null,  a.is_client_payed as etat_facture"
          + " FROM avoir_client a " + "            JOIN abonnement ab "
          + " on ab.clientid = a.abonnement_id"
          + "  WHERE ab.clientid = :abonnementId  and a.is_publish = 'true' AND (a.created_date LIKE CONCAT('%', :search, '%') OR a.montant_avoir LIKE CONCAT('%', :search, '%') OR a.ref_avoir_client LIKE CONCAT('%', :search, '%'))) AS f (facture_id, avoir_id, ref_facture, date_de_debut, date_de_fin, montant_payer, date_echeance, etat_facture)",
      nativeQuery = true)
  Page<InvoiceAvoir> findfactureAvoirByAbonnementId(Pageable page, Long abonnementId,
      @Param("search") String search);

  @Query(value = "SELECT * FROM ("
      + "  SELECT   f.facture_id, NULL, f.ref_facture,f.date_de_debut as dateDeDebut, f.date_de_fin as dateDeFin,f.montant_payer, f.date_echeance as echeance, f.etat_facture,'facture' as typeFacture , f.is_facture_resilation"
      + "  FROM factures f " + "  JOIN abonnement ab" + "  on ab.clientid = f.clientid"
      + " WHERE ab.cin = :cinAbonnment and f.etat_facture = 'false'  " + " UNION "
      + " SELECT NULL, a.avoir_id, a.ref_avoir_client as ref_facture, null , null, -a.montant_avoir, null,  a.is_client_payed as etat_facture , 'avoire' as typeFacture,null"
      + " FROM avoir_client a " + " JOIN abonnement ab " + "  on ab.clientid = a.abonnement_id"
      + "  WHERE ab.cin = :cinAbonnment and a.is_client_payed = 'false' and a.is_publish = 'true') AS f (facture_id, avoir_id, ref_facture, dateDeDebut, dateDeFin, montant_payer, echeance, etat_facture , typeFacture , IsFactureResilation)  ",
      countQuery = "SELECT count(*) FROM ("
          + "  SELECT   f.facture_id, NULL, f.ref_facture,  f.montant_payer,f.date_de_debut, f.date_de_fin, f.date_echeance, f.etat_facture "
          + "  FROM factures f " + "            JOIN abonnement ab"
          + "   on ab.clientid = f.clientid and f.etat_facture = 'false'"
          + "   WHERE ab.cin = :cinAbonnment" + "       UNION "
          + "   SELECT NULL, a.avoir_id, a.ref_avoir_client,  -a.montant_avoir, null, null,null,  a.is_client_payed as etat_facture"
          + "  FROM avoir_client a " + "            JOIN abonnement ab "
          + "   on ab.clientid = a.abonnement_id"
          + "  WHERE ab.cin = :cinAbonnment and a.is_client_payed = 'false' and a.is_publish = 'true') AS f (facture_id, avoir_id, ref_facture, date_de_debut, date_de_fin, montant_payer, date_echeance, etat_facture)",
      nativeQuery = true)
  List<ListeFactureAndAvoirNonPayeDTO> findListeFactureNonPayeeByCinForApi(String cinAbonnment);


  @Query(value = "SELECT * FROM ("
      + "  SELECT   f.facture_id, NULL, f.ref_facture,f.date_de_debut as dateDeDebut, f.date_de_fin as dateDeFin,f.montant_payer, f.date_echeance as echeance, f.etat_facture,'facture' as typeFacture , f.is_facture_resilation,f.date_de_payement as dateDePayement "
      + "  FROM factures f " + "  JOIN abonnement ab" + "  on ab.clientid = f.clientid"
      + " WHERE ab.cin = :cinAbonnment and f.etat_facture = :is_payed  " + " UNION "
      + " SELECT NULL, a.avoir_id, a.ref_avoir_client as ref_facture, null , null, -a.montant_avoir, null,  a.is_client_payed as etat_facture , 'avoire' as typeFacture,null, null AS dateDePayement "
      + " FROM avoir_client a " + " JOIN abonnement ab " + "  on ab.clientid = a.abonnement_id"
      + "  WHERE ab.cin = :cinAbonnment and a.is_client_payed = :is_payed ) AS f (facture_id, avoir_id, ref_facture, dateDeDebut, dateDeFin, montant_payer, echeance, etat_facture , typeFacture , IsFactureResilation,dateDePayement)  ",
      countQuery = "SELECT count(*) FROM ("
          + "  SELECT   f.facture_id, NULL, f.ref_facture,  f.montant_payer,f.date_de_debut, f.date_de_fin, f.date_echeance, f.etat_facture ,f.is_facture_resilation,f.date_de_payement "
          + "  FROM factures f " + " JOIN abonnement ab"
          + "   on ab.clientid = f.clientid and f.etat_facture = :is_payed "
          + "   WHERE ab.cin = :cinAbonnment" + " UNION "
          + "   SELECT NULL, a.avoir_id, a.ref_avoir_client,  -a.montant_avoir, null, null,null,  a.is_client_payed as etat_facture,null,null "
          + "  FROM avoir_client a " + "            JOIN abonnement ab "
          + "   on ab.clientid = a.abonnement_id"
          + "  WHERE ab.cin = :cinAbonnment and a.is_client_payed = :is_payed) AS f (facture_id, avoir_id, ref_facture, date_de_debut, date_de_fin, montant_payer, date_echeance, etat_facture)",
      nativeQuery = true)
  List<ListeFactureAndAvoirDTO> findListeFactureByCinForApi(String cinAbonnment, Boolean is_payed);

  @Query(value = "SELECT * FROM ("
      + "  SELECT   f.facture_id, NULL, f.ref_facture,f.date_de_debut as dateDeDebut, f.date_de_fin as dateDeFin,f.montant_payer, f.date_echeance as echeance, f.etat_facture,'facture' as typeFacture"

      + "  FROM factures f " + "JOIN abonnement ab" + "  on ab.clientid = f.clientid"
      + " WHERE ab.tel_fixe = :telephone and f.etat_facture = 'false'" + " UNION "

      + " SELECT NULL, a.avoir_id, a.ref_avoir_client as ref_facture, null , null, -a.montant_avoir, null,  a.is_client_payed as etat_facture , 'avoire' as typeFacture"
      + " FROM avoir_client a " + "            JOIN abonnement ab "
      + "  on ab.clientid = a.abonnement_id"
      + "  WHERE ab.tel_fixe = :telephone and a.is_client_payed = 'false' and a.is_publish = 'true') AS f (facture_id, avoir_id, ref_facture, dateDeDebut, dateDeFin, montant_payer, echeance, etat_facture , typeFacture)  ",
      countQuery = "SELECT count(*) FROM ("
          + "  SELECT   f.facture_id, NULL, f.ref_facture,  f.montant_payer,f.date_de_debut, f.date_de_fin, f.date_echeance, f.etat_facture "
          + "  FROM factures f " + "            JOIN abonnement ab"
          + "   on ab.clientid = f.clientid"
          + "   WHERE ab.tel_fixe = :telephone and f.etat_facture = 'false'"
          + " and f.visibility = 'true'      UNION "
          + "   SELECT NULL, a.avoir_id, a.ref_avoir_client,  -a.montant_avoir, null, null,null,  a.is_client_payed as etat_facture"
          + "  FROM avoir_client a " + "            JOIN abonnement ab "
          + "   on ab.clientid = a.abonnement_id"
          + "  WHERE ab.tel_fixe = :telephone and a.is_client_payed = 'false' and a.is_publish = 'true') AS f (facture_id, avoir_id, ref_facture, date_de_debut, date_de_fin, montant_payer, date_echeance, etat_facture)",
      nativeQuery = true)
  List<ListeFactureAndAvoirNonPayeDTO> findListeFactureNonPayeeByFixeNumberForApi(Long telephone);

  @Query(value = "SELECT * FROM ("
      + "  SELECT f.facture_id, NULL AS avoir_id, f.ref_facture, f.date_de_debut AS dateDeDebut, "
      + "         f.date_de_fin AS dateDeFin, f.montant_payer, f.date_echeance AS echeance, "
      + "         f.etat_facture, 'facture' AS typeFacture, NULL AS dateCreation, f.date_de_payement AS dateDePayement "
      + "  FROM factures f " + "  JOIN abonnement ab ON ab.clientid = f.clientid "
      + "  WHERE ab.tel_fixe = :telephone AND f.etat_facture = :is_payed " + "  UNION "
      + "  SELECT NULL AS facture_id, a.avoir_id, a.ref_avoir_client AS ref_facture, NULL AS dateDeDebut, "
      + "         NULL AS dateDeFin, -a.montant_avoir AS montant_payer, NULL AS echeance, "
      + "         a.is_client_payed AS etat_facture, 'avoire' AS typeFacture, NULL AS dateCreation, NULL AS dateDePayement "
      + "  FROM avoir_client a " + "  JOIN abonnement ab ON ab.clientid = a.abonnement_id "
      + "  WHERE ab.tel_fixe = :telephone AND a.is_client_payed = :is_payed "
      + ") AS f (facture_id, avoir_id, ref_facture, dateDeDebut, dateDeFin, montant_payer, echeance, etat_facture, typeFacture, dateCreation, dateDePayement)",

      countQuery = "SELECT count(*) FROM (" + "  SELECT f.facture_id " + "  FROM factures f "
          + "  JOIN abonnement ab ON ab.clientid = f.clientid "
          + "  WHERE ab.tel_fixe = :telephone AND f.etat_facture = :is_payed AND f.visibility = 'true' "
          + "  UNION " + "  SELECT a.avoir_id " + "  FROM avoir_client a "
          + "  JOIN abonnement ab ON ab.clientid = a.abonnement_id "
          + "  WHERE ab.tel_fixe = :telephone AND a.is_client_payed = :is_payed " + ") AS f",
      nativeQuery = true)
  List<ListeFactureAndAvoirDTO> findListeFactureByFixeNumberForApi(Long telephone,
      Boolean is_payed);



  @Query(nativeQuery = true, value = "SELECT  * " + "FROM factures f   "
      + "WHERE f.clientid = :abonnementId "
      + "AND f.facture_id > ALL (SELECT CAST(id_value AS INTEGER) FROM (VALUES :factures) AS id_list(id_value)) "
      + "AND f.visibility = 1 " + "AND f.etat_facture = 0")
  List<Facture> findByExistFactureOlderThanSelecter(Long abonnementId, String factures);

  @Query(nativeQuery = true,
      value = "select TOP 1  clientid from factures where facture_id in :factureids")
  Long findAbonnementByListFacture(List<String> factureids);

  @Query("SELECT COUNT(f) FROM Facture f WHERE f.abonnement.clientid = :clientId "
      + "AND f.etat_facture = false " + "AND f.visibility = true "
      + "AND f.date_echeance <= :dateEcheance")
  Integer getNumFactureNonPayee(Long clientId, Date dateEcheance);



  @Query(
      value = "select * from factures f join abonnement  a  on f.clientid = a.clientid join  demandesabonnement dm on a.demande_abonnement = dm.demandeid "
          + "where f.is_first_facture = 1 and f.date_de_payement is not null "
          + "and f.date_de_versement is not null and a.userid = :id "
          + "and  f.date_de_versement >= CAST(:dateDebut AS datetime2) "
          + "and f.date_de_versement <= CAST(:dateFin AS datetime2)",
      nativeQuery = true)
  List<Facture> findListFirstFactureByVersementDateToCalculeCommision(Long id, String dateDebut,
      String dateFin);

  @Query(
      value = "select * from factures f join abonnement  a  on f.clientid = a.clientid join  demandesabonnement dm on a.demande_abonnement = dm.demandeid "
          + "where f.is_first_facture = 1 and f.date_de_payement is not null "
          + "and a.userid = :id " + "and  f.date_de_payement >= CAST(:dateDebut AS datetime2) "
          + "and f.date_de_payement <= CAST(:dateFin AS datetime2)",
      nativeQuery = true)
  List<Facture> findListFirstFactureByPayementtDateToCalculeCommision(Long id, String dateDebut,
      String dateFin);


  @Query(
      value = "select count(*) from factures f join abonnement a on f.clientid = a.clientid join  demandesabonnement dm on a.demande_abonnement = dm.demandeid "
          + "where f.is_first_facture = 1 and f.date_de_payement is not null and f.date_de_versement is  null and a.userid = :id "
          + "and dm.created_date >= CAST(:dateDebut AS datetime2) "
          + "and dm.created_date <= CAST(:dateFin AS datetime2)",
      nativeQuery = true)
  Integer nombreFirstFactureNonVerse(Long id, String dateDebut, String dateFin);

  @Query(
      value = "select count(*) from factures f join abonnement a on f.clientid = a.clientid join  demandesabonnement dm on a.demande_abonnement = dm.demandeid "
          + "where f.is_first_facture = 1 and f.date_de_payement is null and f.date_de_versement is  null and a.userid = :id "
          + "and dm.created_date >= CAST(:dateDebut AS datetime2) "
          + "and dm.created_date <= CAST(:dateFin AS datetime2)",
      nativeQuery = true)
  Integer nombreFirstFactureNonpayeeandVerse(Long id, String dateDebut, String dateFin);


  @Query("SELECT f FROM Facture f where f.etat_facture = false   and f.ref_facture = :referenceFacture ")
  Facture findFactureNonPayeeByReferenceforApi(String referenceFacture);

  @Query("SELECT f FROM Facture f where f.etat_facture = false   and f.ref_facture = :referenceFacture ")
  Facture findFacturePayeeByReferenceforApi(String referenceFacture);

  List<Facture> getFacturesByAbonnement_clientidAndIsFactureResilation(Long clientid,
      Boolean isFactureResilation);

  @Query("SELECT "
      + "SUM(CASE WHEN f.etat_facture = true THEN f.montant_payer ELSE 0 END) AS totalPayee, "
      + "SUM(CASE WHEN f.etat_facture = true AND f.createdDate BETWEEN :lastMonthStart AND :lastMonthEnd THEN f.montant_payer ELSE 0 END) AS totalPayeeLastMonth, "
      + "SUM(CASE WHEN f.etat_facture = true AND YEAR(f.createdDate) = :yearFilter THEN f.montant_payer ELSE 0 END) AS totalPayeeCurrentYear, "
      + "SUM(CASE WHEN f.etat_facture = false THEN f.montant_payer ELSE 0 END) AS totalNonPayee, "
      + "SUM(CASE WHEN f.etat_facture = false AND f.createdDate BETWEEN :lastMonthStart AND :lastMonthEnd THEN f.montant_payer ELSE 0 END) AS totalNonPayeeLastMonth, "
      + "SUM(CASE WHEN f.etat_facture = false AND YEAR(f.createdDate) = :yearFilter THEN f.montant_payer ELSE 0 END) AS totalNonPayeeCurrentYear, "
      + "SUM(f.montant_payer) AS totalFactures, "
      + "SUM(CASE WHEN f.createdDate BETWEEN :lastMonthStart AND :lastMonthEnd THEN f.montant_payer ELSE 0 END) AS totalFacturesLastMonth, "
      + "SUM(CASE WHEN YEAR(f.createdDate) = :yearFilter THEN f.montant_payer ELSE 0 END) AS totalFacturesCurrentYear "
      + "FROM Facture f where (f.user.userid=:userId OR :userId IS NULL) AND (f.user.affectedTo= :distId or :distId IS NULL )")
  List<Map<String, Object>> getTotalSumsFactures(@Param("yearFilter") Integer yearFilter,
      @Param("lastMonthStart") Date lastMonthStart, @Param("lastMonthEnd") Date lastMonthEnd,
      Long userId, Long distId);

  @Query("SELECT "
      + "SUM(CASE WHEN f.etat_facture = true THEN f.montant_payer ELSE 0 END) AS totalPayee, "
      + "SUM(CASE WHEN f.etat_facture = true AND f.createdDate BETWEEN :lastMonthStart AND :lastMonthEnd THEN f.montant_payer ELSE 0 END) AS totalPayeeLastMonth, "
      + "SUM(CASE WHEN f.etat_facture = true AND f.createdDate BETWEEN :startOfThistMonth AND :endOfThisMonth  THEN f.montant_payer ELSE 0 END) AS totalPayeeThisMonth, "
      + "SUM(CASE WHEN f.etat_facture = false THEN f.montant_payer ELSE 0 END) AS totalNonPayee, "
      + "SUM(CASE WHEN f.etat_facture = false AND f.createdDate BETWEEN :lastMonthStart AND :lastMonthEnd THEN f.montant_payer ELSE 0 END) AS totalNonPayeeLastMonth, "
      + "SUM(CASE WHEN f.etat_facture = false AND f.createdDate BETWEEN :startOfThistMonth AND :endOfThisMonth  THEN f.montant_payer ELSE 0 END) AS totalNonPayeeThisMonth, "
      + "SUM(f.montant_payer) AS totalFactures, "
      + "SUM(CASE WHEN f.createdDate BETWEEN :lastMonthStart AND :lastMonthEnd THEN f.montant_payer ELSE 0 END) AS totalFacturesLastMonth, "
      + "SUM(CASE WHEN  f.createdDate BETWEEN :startOfThistMonth AND :endOfThisMonth THEN f.montant_payer ELSE 0 END) AS totalFacturesThisMonth "
      + "FROM Facture f where (f.user.userid=:userId OR :userId IS NULL) AND (f.user.affectedTo= :distId or :distId IS NULL )")
  List<Map<String, Object>> getTotalSumsFacturesOthers(@Param("lastMonthStart") Date lastMonthStart,
      @Param("lastMonthEnd") Date lastMonthEnd, @Param("startOfThistMonth") Date startOfThistMonth,
      @Param("endOfThisMonth") Date endOfThisMonth, Long userId, Long distId);

  @Query(value = "SELECT a.referenceClient AS referenceClient, " + "a.firstName AS first_name, "
      + "a.lastName AS last_name, " + "a.cin AS cin, "
      + "SUM(CASE WHEN f.etat_facture = false THEN f.montant_payer ELSE 0 END) AS totalFactures, "
      + "COUNT(CASE WHEN f.etat_facture = false THEN 1 END) AS countfactures " + "FROM Facture f "
      + "LEFT JOIN Abonnement a ON f.abonnement.clientid = a.clientid "
      + "GROUP BY a.referenceClient, a.firstName, a.lastName, a.cin "
      + "HAVING COUNT(CASE WHEN f.etat_facture = false THEN 1 END) > 1 AND (a.firstName=:prenom OR :prenom IS NULL) "
      + "AND (a.lastName=:nom OR :nom IS NULL) AND (a.referenceClient=:reference OR :reference IS NULL) AND "
      + "(a.cin=:cin OR :cin IS NULL)  " + "ORDER BY totalFactures DESC")
  Page<MoreThanOneInvoiceRecap> getTotalSumsFacturesNonPayerByClients(String prenom, String nom,
      String cin, String reference, Pageable pageable);



  @Modifying
  @Transactional
  @Query(
      value = "UPDATE factures  SET factures.is_commision_saved = true WHERE factures.ref_facture in :stringListOfReferenceFirstFacture  ",
      nativeQuery = true)
  void updateIsCommisionSaved(List<String> stringListOfReferenceFirstFacture);

  List<Facture> findByIsProformat(boolean isProformatb);


  @Query("SELECT new map(f.factureId as factureId, f.ref_facture as refFacture, f.montant_payer as montantPayer, "
      + "f.montantHt as montantHt, f.montantTva as montantTva, f.prixBaseTva as prixBaseTva, "
      + "f.timbrefiscale as timbrefiscale, f.etat_facture as etatFacture, f.visibility as visibility, "
      + "f.isDelete as isDelete, f.isFirstFacture as isFirstFacture, f.createdDate as createdDate, "
      + "f.modifiedDate as modifiedDate, f.date_echeance as dateEcheance, f.dateDeFin as dateDeFin, "
      + "f.dateDeDebut as dateDeDebut, f.remise as remise, f.firstReminder as firstReminder, "
      + "f.secondReminder as secondReminder, f.suspensionServices as suspensionServices, "
      + "f.thirdReminderReactivate as thirdReminderReactivate, f.dateDePayement as dateDePayement, "
      + "f.dateDeVersement as dateDeVersement, f.isFactureResilation as isFactureResilation, "
      + "f.isCommisionSaved as isCommisionSaved, f.isProformat as isProformat) "
      + "FROM Facture f WHERE f.abonnement.clientid = :clientid")
  Page<Map<String, Object>> findByAbonnement_clientidForMobile(@Param("clientid") Long clientid,
      Pageable pageable);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE factures  SET factures.visibility = 'true' , factures.is_first_facture = 'false'   WHERE factures.clientid = :clientId  ",
      nativeQuery = true)
  void updateVisibilityFirstFacture(Long clientId);


  @Query(value = "SELECT fact.*"
      + "  FROM factures fact join abonnement ab on ab.clientid = fact.clientid"
      + "  where ab.date_de_mise_en_service BETWEEN :startOfMonth AND :endOfMonth and fact.is_first_facture = 1 and ab.userid = :id  ",
      nativeQuery = true)
  List<Facture> getFactureByMiseEnServiceToCommission(Long id, String startOfMonth,
      String endOfMonth);


  @Query(value = "SELECT * FROM factures f " + "JOIN abonnement a ON f.clientid = a.clientid "
      + "JOIN demandesabonnement dm ON a.demande_abonnement = dm.demandeid "
      + "WHERE f.is_first_facture = 1 " + "AND f.date_de_payement IS NOT NULL "
      + "AND a.userid = :id "
      + "AND (a.comission_activation_is_payed = 1 OR a.comission_activation_is_payed IS NULL) "
      + "AND f.date_de_payement >= CAST(:dateDebut AS datetime2) "
      + "AND f.date_de_payement <= CAST(:dateFin AS datetime2)", nativeQuery = true)
  List<Facture> findListFirstFactureByPayementtDateToCalculeCommisionAndNotCommissionIsPayed(
      @Param("id") Long id, @Param("dateDebut") String dateDebut, @Param("dateFin") String dateFin);

  @Query(value = "SELECT * FROM factures f " + "JOIN abonnement a ON f.clientid = a.clientid "
      + "JOIN demandesabonnement dm ON a.demande_abonnement = dm.demandeid "
      + "WHERE f.is_first_facture = 1 " + "AND f.date_de_payement IS NOT NULL "
      + "AND a.userid = :id " + "AND (a.comission_activation_is_payed = 0) "
      + "AND f.date_de_payement >= CAST(:dateDebut AS datetime2) "
      + "AND f.date_de_payement <= CAST(:dateFin AS datetime2)", nativeQuery = true)

  List<Facture> findAbonnementsByIsPayedAndDemandeAbonnementStatutIn(@Param("id") Long id,
      @Param("dateDebut") String dateDebut, @Param("dateFin") String dateFin);

  @Query("SELECT f FROM Facture f WHERE f.ref_facture in :ref_facture")
  List<Facture> getFactureByRef_facture(List<String> ref_facture);


  @Query(
      value = "SELECT * FROM factures f where f.etat_facture = :isFacturePayed   and f.clientid = :clientid ",
      nativeQuery = true)
  List<Facture> getFacturesByClientAndEtat_facture(Long clientid, Boolean isFacturePayed);



  @Query(value = "WITH months AS ("
      + "  SELECT 1 as month_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 "
      + "  UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 "
      + "  UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 " + "), "
      + "factures_payees_apres_3jrs AS ( " + "  SELECT " + "    MONTH(f.date_de_payement) as mois, "
      + "    SUM(f.montant_payer) as montant_total_ttc_paye_apres_3jrs " + "  FROM factures f "
      + "  WHERE f.date_echeance IS NOT NULL " + "    AND f.date_de_payement IS NOT NULL "
      + "    AND CAST(f.date_de_payement AS DATE) >= DATEADD(day, 3, CAST(f.date_echeance AS DATE)) "
      + "    AND (f.etat_facture = 1) " + "    AND f.date_de_payement >= DATEFROMPARTS(?1,1,1) "
      + "    AND f.date_de_payement < DATEFROMPARTS(?1+1,1,1) "
      + "    AND (f.is_delete = 0 OR f.is_delete IS NULL) "
      + "  GROUP BY MONTH(f.date_de_payement) " + ") " + "SELECT " + "  m.month_num as mois, "
      + "  CASE m.month_num " + "    WHEN 1 THEN 'Janvier' " + "    WHEN 2 THEN 'Février' "
      + "    WHEN 3 THEN 'Mars' " + "    WHEN 4 THEN 'Avril' " + "    WHEN 5 THEN 'Mai' "
      + "    WHEN 6 THEN 'Juin' " + "    WHEN 7 THEN 'Juillet' " + "    WHEN 8 THEN 'Août' "
      + "    WHEN 9 THEN 'Septembre' " + "    WHEN 10 THEN 'Octobre' "
      + "    WHEN 11 THEN 'Novembre' " + "    WHEN 12 THEN 'Décembre' " + "  END as mois_nom, "
      + "  ISNULL(fpr.montant_total_ttc_paye_apres_3jrs, 0) as montant_factures_payees_apres_3jrs_ttc "
      + "FROM months m " + "LEFT JOIN factures_payees_apres_3jrs fpr ON m.month_num = fpr.mois "
      + "WHERE m.month_num <= CASE " + "      WHEN YEAR(GETDATE()) > ?1 THEN 12 "
      + "      ELSE MONTH(GETDATE()) " + "    END " + "ORDER BY m.month_num", nativeQuery = true)
  List<Object[]> getFacturesPayeesApres3JoursByYearReport(int year);

	List<Facture> findFactureByCreatedDate(LocalDate yesterday);
	List<Facture> findFactureByCreatedDateBetween(Date start ,Date end );


  @Query(value = "WITH days AS ("
      + "  SELECT 1 as day_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 "
      + "  UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 "
      + "  UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 "
      + "  UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 "
      + "  UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 "
      + "  UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 "
      + "  UNION SELECT 25 UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 "
      + "  UNION SELECT 29 UNION SELECT 30 UNION SELECT 31 " + "), "
      + "factures_payees_apres_3jrs AS ( " + "  SELECT " + "    DAY(f.date_de_payement) as jour, "
      + "    SUM(f.montant_payer) as montant_total_ttc_paye_apres_3jrs " + "  FROM factures f "
      + "  WHERE f.date_echeance IS NOT NULL " + "    AND f.date_de_payement IS NOT NULL "
      + "    AND CAST(f.date_de_payement AS DATE) >= DATEADD(day, 3, CAST(f.date_echeance AS DATE)) "
      + "    AND (f.etat_facture = 1) " + "    AND f.date_de_payement >= DATEFROMPARTS(?1,?2,1) "
      + "    AND f.date_de_payement < DATEADD(month, 1, DATEFROMPARTS(?1,?2,1)) "
      + "    AND (f.is_delete = 0 OR f.is_delete IS NULL) " + "  GROUP BY DAY(f.date_de_payement) "
      + ") " + "SELECT " + "  d.day_num as jour, "
      + "  ISNULL(fpr.montant_total_ttc_paye_apres_3jrs, 0) as montant_factures_payees_apres_3jrs_ttc "
      + "FROM days d " + "LEFT JOIN factures_payees_apres_3jrs fpr ON d.day_num = fpr.jour "
      + "WHERE d.day_num <= DAY(EOMONTH(DATEFROMPARTS(?1,?2,1))) " + "ORDER BY d.day_num",
      nativeQuery = true)
  List<Object[]> getFacturesPayeesApres3JoursByMonthAndYear(@Param("year") int year,
      @Param("month") int month);

  @Query(value = "SELECT " + "c.clientid as clientId, " + "c.first_name as firstName, "
      + "c.last_name as lastName, " + "c.cin as cin, " + "c.tel_mobile as telMobile, "
      + "c.reference_client as referenceClient, " + "COUNT(f.facture_id) as nbFacturesImpayees, "
      + "SUM(f.montant_payer) as montantTotalTTCImpaye, "
      + "MIN(f.date_echeance) as dateEcheancePlusAncienne, "
      + "MAX(DATEDIFF(DAY, f.date_echeance, GETDATE())) as retardEnJours, " + "CASE "
      + "    WHEN MAX(DATEDIFF(DAY, f.date_echeance, GETDATE())) >= 30 THEN 'Retard critique (>30j)' "
      + "    WHEN MAX(DATEDIFF(DAY, f.date_echeance, GETDATE())) >= 15 THEN 'Retard important (15-30j)' "
      + "    WHEN MAX(DATEDIFF(DAY, f.date_echeance, GETDATE())) >= 7 THEN 'Retard modéré (7-14j)' "
      + "    ELSE 'Retard léger (3-6j)' " + "END as niveauRetard " + "FROM abonnement c "
      + "INNER JOIN factures f ON c.clientid = f.clientid left join status s on s.statut_id=c.statutid "
      + "WHERE f.etat_facture = 0 " + "  AND f.date_de_payement IS NULL " + "  AND f.is_delete = 0 "
      + "  AND f.is_first_facture = 0 " + "  AND f.is_proformat = 0 "
      + "  AND f.date_echeance IS NOT NULL " + "  AND s.nom_statut !='RESILIATION' "
      + "  AND f.date_echeance <= DATEADD(DAY, -3, CAST(GETDATE() AS DATE)) "
      + "GROUP BY c.clientid, c.first_name, c.last_name, c.cin, c.tel_mobile, c.reference_client "
      + "HAVING COUNT(f.facture_id) = 1 " + "ORDER BY retardEnJours DESC", nativeQuery = true)
  List<Map<String, Object>> getClientsFacturesImpayeesRetard3Jours();
}
