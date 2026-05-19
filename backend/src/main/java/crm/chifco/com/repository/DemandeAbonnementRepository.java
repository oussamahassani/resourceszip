package crm.chifco.com.repository;

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
import crm.chifco.com.DTOclass.CalculeCommisionDemande;
import crm.chifco.com.DTOclass.CommissionDemDash;
import crm.chifco.com.DTOclass.DemandeAbbonementaAndAffectedToUserObjectDataDTO;
import crm.chifco.com.DTOclass.DemandeAbbonmentDataDTO;
import crm.chifco.com.DTOclass.DemandeAbbonmentDataDTOv2;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.Statut;
import crm.chifco.com.templateclasse.DemandeAbonnementInterface;

public interface DemandeAbonnementRepository extends JpaRepository<DemandeAbonnement, Long> {

  DemandeAbonnement findDemandeAbonnementByDemandeId(Long id);

  @Query(
      value = "select new crm.chifco.com.DTOclass.DemandeAbbonementaAndAffectedToUserObjectDataDTO(dmd,a,created.firstName,created.lastName,created.codeUser) from DemandeAbonnement dmd  JOIN dmd.assignedTo  assigneduser  "
          + "LEFT JOIN User a ON assigneduser.affectedTo = a.userid JOIN dmd.user  createdby   LEFT JOIN User created ON createdby.affectedTo = created.userid   where dmd.demandeId =:id ")
  DemandeAbbonementaAndAffectedToUserObjectDataDTO findDemandeAbonnementByDemandeIdLeftJoinDistribiteur(
      Long id);

  @Query(value = "select  * from demandesabonnement dmdab "
      + "where dmdab.demandeid in :listeDemandeAbonnementId", nativeQuery = true)
  List<DemandeAbonnement> findDemandeAbonnementByListeDemandeId(
      List<String> listeDemandeAbonnementId);

  List<DemandeAbonnement> findDemandeAbonnementByCin(String cin);

  @Query(value = "SELECT "
      + "COUNT(CASE WHEN CONVERT(DATE, d.createdDate) = CONVERT(DATE, GETDATE()) THEN d.demandeId ELSE NULL END) AS todayCountDemande, "
      + "COUNT(CASE WHEN CONVERT(DATE, d.createdDate) =  CONVERT(DATE, :hier) THEN d.demandeId ELSE NULL END) AS yesterdayCountDemande "
      + "FROM DemandeAbonnement d")
  List<Map<String, Object>> countDemandeAbonnementTodayAndYesterday(String hier);


  @Query(
      value = "select dmd.demandeId from DemandeAbonnement dmd LEFT JOIN dmd.decisionDemande cls "
          + "where (( dmd.firstName = :firstName or :firstName is null )"
          + "and ( dmd.statut.statutId = :statutListfiltre or :statutListfiltre is null ) "
          + "and (dmd.etatTT = :statutTTListfiltre or :statutTTListfiltre is null) "
          + "and ( dmd.ville.villeId = :villes or :villes is null ) "
          + "and ( dmd.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
          + "and ( dmd.cin   =:cin  or :cin is null) "
          + "and ( dmd.origin   =:source  or :source is null) "
          + "and ((dmd.telMobile  = :tel or :tel is null ) or (dmd.telFixe  = :tel or :tel is null ))"
          + "and (dmd.referenceTT =:refTT  or :refTT is null )"
          + "and ( dmd.referenceChifco   =:refChif  or :refChif is null)"
          + "and ( dmd.profession.professionId= :professions or :professions is null ) "
          + "and ( dmd.createdDate  >= :datedebut or :datedebut is null ) "
          + "and ( dmd.createdDate <=  :datefin or :datefin is null ) "
          + "and ( dmd.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
          + "and ( dmd.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
          + "and ( dmd.dateDeMiseEnService  >= :datedebutMiseService or :datedebutMiseService is null ) "
          + "and ( dmd.dateDeMiseEnService  <= :datefinMiseService or :datefinMiseService is null ) "
          + "and ( dmd.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null)  "
          + "and ( dmd.pack.packId = :produit or :produit is null ) "
          + "and ( dmd.assignedTo.userid  = :AffecterTo or :AffecterTo is null ) "
          + "and ( dmd.user.userid  = :CreePar or :CreePar is null ) "
          + "and ( dmd.typeAbonnment  = :typeAbonnement or :typeAbonnement is null ) "
          + "and ((:agentId = 0 and dmd.treatedBy is null) or (:agentId <> 0 and dmd.treatedBy.userid = :agentId) or :agentId is null) "
          + "and (dmd.lastName = :lastName or :lastName is null)" + ""
          + "and (cls.codeClassification = :classification or :classification is null)"
          + "and (dmd.motifRefus IN :motifInstance or :motifInstanceIfEmpty is null)" + ")")
  List<Long> findAllToExportAdmin(String lastName, String firstName, Long statutListfiltre,
      String statutTTListfiltre, Long villes, Long gouvernorat, String cin, Long tel,
      String refChif, String refTT, Long professions, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, Long categories, Long produit,
      Long AffecterTo, Long CreePar, String classification, List<String> motifInstance,
      String motifInstanceIfEmpty, Date datedebutMiseService, Date datefinMiseService,
      Integer agentId, String source , String typeAbonnement);

  @Query(value = "select dmd.demandeId from DemandeAbonnement dmd "
      + "join User us on dmd.user.userid = us.userid "
      + "join Role ro on ro.roleId = us.role.roleId " + "where ro.roleId = :roleid "
      + "and dmd.assignedTo.userid = :userid "
      + "and (( dmd.firstName = :firstName or :firstName is null )"
      + "and ( dmd.statut.statutId = :statutListfiltre or :statutListfiltre is null ) "
      + "and (dmd.etatTT = :statutTTListfiltre or :statutTTListfiltre is null) "
      + "and ( dmd.ville.villeId = :villes or :villes is null ) "
      + "and ( dmd.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( dmd.cin   =:cin  or :cin is null) " + "and (dmd.telMobile  = :tel or :tel is null )"
      + "and (dmd.referenceTT =:refTT  or :refTT is null )"
      + "and ( dmd.referenceChifco   =:refChif  or :refChif is null)"
      + "and ( dmd.profession.professionId= :professions or :professions is null ) "
      + "and ( dmd.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( dmd.createdDate <=  :datefin or :datefin is null ) "
      + "and ( dmd.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( dmd.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( dmd.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null)  "
      + "and ( dmd.pack.packId = :produit or :produit is null ) "
      + "and ( dmd.typeAbonnment = :typeAbonnment or :typeAbonnment is null ) "

      + "and (dmd.lastName = :lastName or :lastName is null))")
  List<Long> findAllToExportRevendeur(String firstName, String lastName, Long statutListfiltre,
      String statutTTListfiltre, Long villes, Long gouvernorat, String cin, Long tel,
      String refChif, String refTT, Long professions, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, Long roleid, Long userid,
      Long categories, Long produit , String typeAbonnment);

  @Query(value = "select dmd.demandeId from DemandeAbonnement dmd "
      + "join User us on dmd.user.userid = us.userid "
      + " where (us.affectedTo = :affectedTo or dmd.assignedTo.userid = :userid) "
      + "and (( dmd.firstName = :firstName or :firstName is null )"
      + "and ( dmd.statut.statutId = :statutListfiltre or :statutListfiltre is null ) "
      + "and (dmd.etatTT = :statutTTListfiltre or :statutTTListfiltre is null) "
      + "and ( dmd.ville.villeId = :villes or :villes is null ) "
      + "and ( dmd.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( dmd.cin   =:cin  or :cin is null) "
      + "and ((dmd.telMobile  = :tel or :tel is null )or(dmd.telFixe =:tel  or :tel is null  ))"
      + "and (dmd.referenceTT =:refTT  or :refTT is null )"
      + "and ( dmd.referenceChifco   =:refChif  or :refChif is null)"
      + "and ( dmd.profession.professionId= :professions or :professions is null ) "
      + "and ( dmd.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( dmd.createdDate <=  :datefin or :datefin is null ) "
      + "and ( dmd.modifiedDate  >= :dateDebutModification or :dateDebutModification is null ) "
      + "and ( dmd.modifiedDate  <= :dateFinModification or :dateFinModification is null ) "
      + "and ( dmd.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null)  "
      + "and ( dmd.pack.packId = :produit or :produit is null ) "
      + "and ( dmd.typeAbonnment = :typeAbonnment or :typeAbonnment is null ) "
      + "and ( dmd.assignedTo.userid  = :AffecterTo or :AffecterTo is null ) "
      + "and ( dmd.user.userid  = :CreePar or :CreePar is null ) "
      + "and (dmd.lastName = :lastName or :lastName is null))")
  List<Long> findAllToExportDistributeur(String lastName, String firstName, Long statutListfiltre,
      String statutTTListfiltre, Long villes, Long gouvernorat, String cin, Long tel,
      String refChif, String refTT, Long professions, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, Long userid, Long affectedTo,
      Long categories, Long produit, Long AffecterTo, Long CreePar , String typeAbonnment);

  List<DemandeAbonnement> findDemandeAbonnementByreferenceTT(String referenceTT);

  Page<DemandeAbonnement> findDemandeAbonnementsByAssignedTo_Role_RoleIdAndAssignedTo_Userid(
      Long roleid, Long userid, Pageable pageable);

  DemandeAbonnement findDemandeAbonnementByReferenceChifco(String refchifco);

  Page<DemandeAbonnement> findDemandeAbonnementsByAssignedTo_AffectedToOrAssignedTo_Userid(
      Pageable pageable, Long createdbyuserid, Long userid);

  @Query(
      value = "select TOP 1  * from demandesabonnement dmdab " + "where dmdab.referencett =:reftt",
      nativeQuery = true)
  DemandeAbonnement findDemandeAbonnementsByuniquereferencett(@Param("reftt") String reftt);

  @Query("select  new crm.chifco.com.DTOclass.DemandeAbbonmentDataDTO(dmdab.demandeId ,dmdab.firstName ,dmdab.lastName, dmdab.telFixe, "
      + "dmdab.telMobile ,dmdab.createdDate ,dmdab.referenceTT , dmdab.etatTT, "
      + "dmdab.statut.nomStatut,dmdab.pack.title , dmdab.referenceChifco , dmdab.statut.couleur , dmdab.statut.statutId , dmdab.statut.designation , dmdab.cin , dmdab.user.firstName ,dmdab.user.lastName , dmdab.motifRefus ,  dmdab.decisionDemande.codeClassification ,dmdab.decisionDemande.value ) from DemandeAbonnement dmdab "
      + "LEFT JOIN dmdab.decisionDemande cls where ( (dmdab.assignedTo.userid = :userid) and (dmdab.referenceChifco =:refchif  or :refchif is null)"
      + "and (dmdab.referenceTT =:refTT  or :refTT is null )"
      + "and (dmdab.cin   =:cin  or :cin is null)"
      + "and (dmdab.lastName =:prenom  or :prenom is null)"
      + "and (dmdab.firstName  =:nom   or :nom is null)"
      + "and (dmdab.etatTT = :statutTTListfiltre or :statutTTListfiltre is null)"
      + "and ( dmdab.ville.villeId = :villes or :villes is null )"
      + "and ( dmdab.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( dmdab.statut.statutId = :statutListfiltre or :statutListfiltre is null ) "
      + "and ( dmdab.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null ) "

      + "and ( dmdab.pack.packId = :produit or :produit is null ) "
      + "and ( dmdab.profession.professionId = :professions or :professions is null ) "
      + "and ( dmdab.createdDate  >= :datedebut   or :datedebut is null ) "
      + "and ( dmdab.createdDate <=  :datefin or :datefin is null ) "
      + "and ( dmdab.modifiedDate  >= :dateDebutModification  or :dateDebutModification is null ) "
      + "and ( dmdab.dateDeMiseEnService  <= :dateFinModification   or :dateFinModification is null ) "
      + "and ( dmdab.dateDeMiseEnService  >= :dateDebutMiseService  or :dateDebutMiseService is null ) "
      + "and ( dmdab.modifiedDate  <= :dateFinMiseService   or :dateFinMiseService is null ) "
      + "and ( dmdab.typeAbonnment  = :typeAbonnement   or :typeAbonnement is null ) "

      + "and ( (dmdab.telMobile =:tel  or :tel is null )"
      + "or(dmdab.telFixe =:tel  or :tel is null  ))  " + ")")
  Page<DemandeAbbonmentDataDTO> findDemandeAbonnementsByKeywordbyrevendeursearchparamsnotempty(
      @Param("refchif") String refchif, @Param("refTT") String refTT, @Param("cin") String cin,
      @Param("prenom") String prenom, @Param("nom") String nom, @Param("tel") Long tel,
      @Param("villes") Long villes, @Param("gouvernorat") Long gouvernorat,
      @Param("professions") Long professions, @Param("categories") Long categories,
      @Param("produit") Long produit, @Param("statutListfiltre") Long statutListfiltre,
      String statutTTListfiltre, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, Long userid, Date dateDebutMiseService, Date dateFinMiseService,
      String typeAbonnement,
      Pageable pageable);


  @Query("select  new crm.chifco.com.DTOclass.DemandeAbbonmentDataDTO(dmdab.demandeId ,dmdab.firstName ,dmdab.lastName, dmdab.telFixe, "
      + "dmdab.telMobile ,dmdab.createdDate ,dmdab.referenceTT , dmdab.etatTT, "
      + "dmdab.statut.nomStatut,dmdab.pack.title , dmdab.referenceChifco , dmdab.statut.couleur , dmdab.statut.statutId , dmdab.statut.designation , dmdab.cin , dmdab.user.firstName ,dmdab.user.lastName , dmdab.motifRefus ,  dmdab.decisionDemande.codeClassification ,dmdab.decisionDemande.value ) from DemandeAbonnement dmdab "
      + "LEFT JOIN dmdab.decisionDemande cls where ( (dmdab.user.userid = :userid) and (dmdab.assignedTo.userid != :userid) and (dmdab.referenceChifco =:refchif  or :refchif is null)"
      + "and (dmdab.referenceTT =:refTT  or :refTT is null )"
      + "and (dmdab.cin   =:cin  or :cin is null)"
      + "and (dmdab.lastName =:prenom  or :prenom is null)"
      + "and (dmdab.firstName  =:nom   or :nom is null)"
      + "and (dmdab.etatTT = :statutTTListfiltre or :statutTTListfiltre is null)"
      + "and ( dmdab.ville.villeId = :villes or :villes is null )"
      + "and ( dmdab.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( dmdab.statut.statutId = :statutListfiltre or :statutListfiltre is null ) "
      + "and ( dmdab.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null ) "

      + "and ( dmdab.pack.packId = :produit or :produit is null ) "
      + "and ( dmdab.profession.professionId = :professions or :professions is null ) "
      + "and ( dmdab.createdDate  >= :datedebut   or :datedebut is null ) "
      + "and ( dmdab.createdDate <=  :datefin or :datefin is null ) "
      + "and ( dmdab.modifiedDate  >= :dateDebutModification  or :dateDebutModification is null ) "
      + "and ( dmdab.dateDeMiseEnService  <= :dateFinModification   or :dateFinModification is null ) "
      + "and ( dmdab.dateDeMiseEnService  >= :dateDebutMiseService  or :dateDebutMiseService is null ) "
      + "and ( dmdab.modifiedDate  <= :dateFinMiseService   or :dateFinMiseService is null ) "
      + "and ( (dmdab.telMobile =:tel  or :tel is null )"
      + "or(dmdab.telFixe =:tel  or :tel is null  ))  " + ")")
  Page<DemandeAbbonmentDataDTO> findByRevendeurSuiviDesDemandesTransfByKeywordbyrevendeursearchparamsnotempty(
      @Param("refchif") String refchif, @Param("refTT") String refTT, @Param("cin") String cin,
      @Param("prenom") String prenom, @Param("nom") String nom, @Param("tel") Long tel,
      @Param("villes") Long villes, @Param("gouvernorat") Long gouvernorat,
      @Param("professions") Long professions, @Param("categories") Long categories,
      @Param("produit") Long produit, @Param("statutListfiltre") Long statutListfiltre,
      String statutTTListfiltre, Date datedebut, Date datefin, Date dateDebutModification,
      Date dateFinModification, Long userid, Date dateDebutMiseService, Date dateFinMiseService,
      Pageable pageable);

  @Query("select  new crm.chifco.com.DTOclass.DemandeAbbonmentDataDTO(dmdab.demandeId ,dmdab.firstName ,dmdab.lastName, dmdab.telFixe, "
      + "dmdab.telMobile ,dmdab.createdDate ,dmdab.referenceTT , dmdab.etatTT, "
      + "dmdab.statut.nomStatut,dmdab.pack.title , dmdab.referenceChifco , dmdab.statut.couleur , dmdab.statut.statutId , dmdab.statut.designation , dmdab.cin , dmdab.user.firstName ,dmdab.user.lastName  , dmdab.motifRefus ,  dmdab.decisionDemande.codeClassification ,dmdab.decisionDemande.codeClassification ) from DemandeAbonnement dmdab "
      + "LEFT JOIN dmdab.decisionDemande cls where ( (  dmdab.user.userid = :createdbyuserid or dmdab.user.affectedTo  = :userid  ) and (dmdab.referenceChifco =:refchif  or :refchif is null)"
      + "and (dmdab.referenceTT =:refTT  or :refTT is null )"
      + "and (dmdab.cin   =:cin  or :cin is null)"
      + "and (dmdab.lastName =:prenom  or :prenom is null)"
      + "and (dmdab.firstName  =:nom   or :nom is null)"
      + "and (dmdab.etatTT = :statutTTListfiltre or :statutTTListfiltre is null)"
      + "and ( dmdab.ville.villeId = :villes or :villes is null )"
      + "and ( dmdab.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "

      + "and ( dmdab.user.userid = :CreeParRecherche or :CreeParRecherche is null ) "
      + "and ( dmdab.assignedTo.userid = :AffecterTo or :AffecterTo is null ) "
      + "and ( dmdab.statut.statutId = :statutListfiltre or :statutListfiltre is null ) "
      + "and ( dmdab.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null ) "

      + "and ( dmdab.pack.packId = :produit or :produit is null ) "
      + "and ( dmdab.profession.professionId = :professions or :professions is null ) "

      + "and ( dmdab.modifiedDate  >= :dateDebutModification  or :dateDebutModification is null ) "
      + "and ( dmdab.modifiedDate  <= :dateFinModification   or :dateFinModification is null ) "
      + "and ( dmdab.createdDate  >= :dateDebutCreation  or :dateDebutCreation is null ) "
      + "and ( dmdab.createdDate  <= :dateFinCreation   or :dateFinCreation is null ) "
      + "and ( dmdab.dateDeMiseEnService  >= :dateDebutMiseService  or :dateDebutMiseService is null ) "
      + "and ( dmdab.dateDeMiseEnService  <= :dateFinMiseService   or :dateFinMiseService is null ) "
      + "and ( dmdab.typeAbonnment  <= :typeDabonnement   or :typeDabonnement is null ) "

      + "and ( (dmdab.telMobile =:tel  or :tel is null )"
      + "or(dmdab.telFixe =:tel  or :tel is null  ))  " + ")")

  Page<DemandeAbbonmentDataDTO> findDemandeAbonnementsByKeywordbydistributeursearchparamsnotempty(
      String refchif, String refTT, String cin, @Param("prenom") String prenom,
      @Param("nom") String nom, @Param("tel") Long tel, @Param("villes") Long villes,
      @Param("gouvernorat") Long gouvernorat, @Param("professions") Long professions,
      @Param("categories") Long categories, @Param("produit") Long produit,
      @Param("statutListfiltre") Long statutListfiltre, String statutTTListfiltre,
      Date dateDebutModification, Date dateFinModification, Date dateDebutCreation,
      Date dateFinCreation, Long createdbyuserid, Long userid, Long CreeParRecherche,
      Long AffecterTo, Date dateDebutMiseService, Date dateFinMiseService,String typeDabonnement , Pageable pageable);

  @Query(value = "select * from demandesabonnement dmdab "

      + "where (  (dmdab.etattt = :statutTTListfiltre or :statutTTListfiltre is null)  and ( dmdab.ville_id = :villes or :villes is null ) "
      + "and ( dmdab.gouvernorat_id = :gouvernorat or :gouvernorat is null ) "
      + "and ( dmdab.assigned_to = :AffecterTo or :AffecterTo is null ) "
      + "and ( dmdab.user_id = :CreeParRecherche or :CreeParRecherche is null ) "
      + "and ( dmdab.statut_id = :statutListfiltre or :statutListfiltre is null ) "
      + "and ( dmdab.categorie_produit_internet_id = :categories or :categories is null ) "

      + "and ( dmdab.pack_id = :produit or :produit is null ) "
      + "and (LOWER(dmdab.first_name)  =:nom   or :nom is null)"
      + "and (LOWER(dmdab.last_name) =:prenom  or :prenom is null)"

      + "and  (dmdab.reference_chifco =:refchif  or :refchif is null)"
      + "and ( dmdab.profession_id= :professions or :professions is null ) "
      + "and ( dmdab.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null ) "
      + "and ( dmdab.created_date <=  CAST(:datefin AS datetime2) or :datefin is null ) "
      + "and ( dmdab.modified_date  >= CAST(:dateDebutModification AS datetime2) or :dateDebutModification is null ) "
      + "and ( dmdab.modified_date  <= CAST(:dateFinModification AS datetime2)  or :dateFinModification is null ) "
      + "and (dmdab.referencett =:refTT  or :refTT is null )"
      + "and ( dmdab.cin   =:cin  or :cin is null)) "
      + "and ( (dmdab.tel_mobile =:tel  or :tel is null )"
      + "or(dmdab.tel_fixe =:tel  or :tel is null  )  " + " )", nativeQuery = true)

  Page<DemandeAbonnement> findDemandeAbonnementsByPramsnotempty(@Param("refchif") String refchif,
      @Param("refTT") String refTT, @Param("cin") String cin, @Param("prenom") String prenom,
      @Param("nom") String nom, @Param("tel") String tel, @Param("villes") Long villes,
      @Param("gouvernorat") Long gouvernorat, @Param("professions") Long professions,
      @Param("categories") Long categories, @Param("produit") Long produit,
      @Param("statutListfiltre") Long statutListfiltre,
      @Param("statutTTListfiltre") String statutTTListfiltre, String datedebut, String datefin,
      String dateDebutModification, String dateFinModification, Long AffecterTo,
      Long CreeParRecherche, Pageable pageable);

  @Query("select  new crm.chifco.com.DTOclass.DemandeAbbonmentDataDTO(dmdab.demandeId ,dmdab.firstName ,dmdab.lastName, dmdab.telFixe, "
      + "dmdab.telMobile ,dmdab.createdDate ,dmdab.referenceTT , dmdab.etatTT, "
      + "dmdab.statut.nomStatut,dmdab.pack.title , dmdab.referenceChifco , dmdab.statut.couleur , dmdab.statut.statutId , dmdab.statut.designation, dmdab.cin , dmdab.user.firstName ,dmdab.user.lastName , dmdab.motifRefus , dmdab.decisionDemande.codeClassification ,dmdab.decisionDemande.value ,dmdab.origin ) from DemandeAbonnement dmdab "
      + "LEFT JOIN dmdab.decisionDemande cls where ((dmdab.referenceChifco =:refchif  or :refchif is null)"
      + "and (dmdab.referenceTT =:refTT  or :refTT is null )"
      + "and (dmdab.cin   =:cin  or :cin is null)"
      + "and (dmdab.lastName =:prenom  or :prenom is null)"
      + "and (dmdab.firstName  =:nom   or :nom is null)"
      + "and (dmdab.origin  =:source   or :source is null)"
      + "and (dmdab.etatTT = :statutTTListfiltre or :statutTTListfiltre is null)"
      + "and ( dmdab.ville.villeId = :villes or :villes is null )"
      + "and ( dmdab.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( dmdab.assignedTo.userid = :AffecterTo or :AffecterTo is null ) "
      + "and ( dmdab.user.userid = :CreeParRecherche or :CreeParRecherche is null ) "
      + "and ( dmdab.statut.statutId = :statutListfiltre or :statutListfiltre is null ) "
      + "and ( dmdab.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null ) "
      + "and ( dmdab.pack.packId = :produit or :produit is null ) "
      + "and ( dmdab.decisionDemande.codeClassification = :classification or :classification is null ) "
      + "and ( dmdab.motifRefus in  :motifInstance or :motifInstanceIfEmpty is null ) "
      + "and ( dmdab.profession.professionId = :professions or :professions is null ) "
      + "and ( dmdab.createdDate  >= :datedebut   or :datedebut is null ) "
      + "and ( dmdab.createdDate <=  :datefin or :datefin is null ) "
      + "and ( dmdab.modifiedDate  >= :dateDebutModification  or :dateDebutModification is null ) "
      + "and ( dmdab.modifiedDate  <= :dateFinModification   or :dateFinModification is null ) "
      + "and ( dmdab.dateDeMiseEnService  >= :datedebutMiseService   or :datedebutMiseService is null ) "
      + "and ( dmdab.dateDeMiseEnService <=  :dateFinMiseService or :dateFinMiseService is null ) "
       + "and ( dmdab.typeAbonnment = :typeAbonnement or :typeAbonnement is null ) "

      + "and ( (dmdab.telMobile =:tel  or :tel is null )"
      + "or(dmdab.telFixe =:tel  or :tel is null  ))  " + ")")
  Page<DemandeAbbonmentDataDTO> findDemandeAbonnementsByAdmin(String refchif, String refTT,
      String cin, String prenom, String nom, String statutTTListfiltre, Long villes,
      Long gouvernorat, Long AffecterTo, Long CreeParRecherche, Long statutListfiltre,
      Long categories, Long produit, Long professions, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, Long tel, String classification,
      List<String> motifInstance, String motifInstanceIfEmpty, Date datedebutMiseService,
      Date dateFinMiseService, String source,String typeAbonnement, Pageable pageable);

  @Query("select  new crm.chifco.com.DTOclass.DemandeAbbonmentDataDTOv2(dmdab.demandeId ,dmdab.firstName ,dmdab.lastName, dmdab.telFixe, "
      + "dmdab.telMobile ,dmdab.createdDate ,dmdab.referenceTT , dmdab.etatTT, "
      + "dmdab.statut.nomStatut,dmdab.pack.title , dmdab.referenceChifco , dmdab.statut.couleur , dmdab.statut.statutId , dmdab.statut.designation, dmdab.cin , dmdab.user.firstName ,dmdab.user.lastName , dmdab.motifRefus , dmdab.decisionDemande.codeClassification ,dmdab.decisionDemande.value,  "
      + " CASE WHEN treated IS NOT NULL THEN treated.firstName ELSE '' END, "
      + " CASE WHEN treated IS NOT NULL THEN treated.lastName ELSE '' END,dmdab.origin "
      + ") from DemandeAbonnement dmdab " + "LEFT JOIN dmdab.decisionDemande cls "
      + " LEFT JOIN dmdab.treatedBy treated "
      + "where ((dmdab.referenceChifco =:refchif  or :refchif is null)"
      + "and (dmdab.referenceTT =:refTT  or :refTT is null )"
      + "and (dmdab.cin   =:cin  or :cin is null)"
      + "and (dmdab.lastName =:prenom  or :prenom is null)"
      + "and (dmdab.firstName  =:nom   or :nom is null)"
      + "and (dmdab.origin  =:source   or :source is null)"
      + "and (dmdab.etatTT = :statutTTListfiltre or :statutTTListfiltre is null)"
      + "and ( dmdab.ville.villeId = :villes or :villes is null )"
      + "and ( dmdab.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( dmdab.assignedTo.userid = :AffecterTo or :AffecterTo is null ) "
      + "and ( dmdab.user.userid = :CreeParRecherche or :CreeParRecherche is null ) "
      + "and ((:agentId = 0 and dmdab.treatedBy is null) or (:agentId <> 0 and dmdab.treatedBy.userid = :agentId) or :agentId is null)"
      + "and ( dmdab.statut.statutId = :statutListfiltre or :statutListfiltre is null ) "
      + "and ( dmdab.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null ) "
      + "and ( dmdab.pack.packId = :produit or :produit is null ) "
      + "and ( dmdab.decisionDemande.codeClassification = :classification or :classification is null ) "
      + "and ( dmdab.motifRefus in  :motifInstance or :motifInstanceIfEmpty is null ) "
      + "and ( dmdab.profession.professionId = :professions or :professions is null ) "
      + "and ( dmdab.createdDate  >= :datedebut   or :datedebut is null ) "
      + "and ( dmdab.createdDate <=  :datefin or :datefin is null ) "
      + "and ( dmdab.modifiedDate  >= :dateDebutModification  or :dateDebutModification is null ) "
      + "and ( dmdab.modifiedDate  <= :dateFinModification   or :dateFinModification is null ) "
      + "and ( dmdab.dateDeMiseEnService  >= :datedebutMiseService   or :datedebutMiseService is null ) "
      + "and ( dmdab.dateDeMiseEnService <=  :dateFinMiseService or :dateFinMiseService is null ) "
      + "and ( dmdab.typeAbonnment =  :typeAbonnement or :typeAbonnement is null ) "

      + "and ( (dmdab.telMobile =:tel  or :tel is null )"
      + "or(dmdab.telFixe =:tel  or :tel is null  ))  " + ")")
  Page<DemandeAbbonmentDataDTOv2> findDemandeAbonnementsByAdminv2(String refchif, String refTT,
      String cin, String prenom, String nom, String statutTTListfiltre, Long villes,
      Long gouvernorat, Long AffecterTo, Long CreeParRecherche, Long statutListfiltre,
      Long categories, Long produit, Long professions, Date datedebut, Date datefin,
      Date dateDebutModification, Date dateFinModification, Long tel, String classification,
      List<String> motifInstance, String motifInstanceIfEmpty, Date datedebutMiseService,
      Date dateFinMiseService, Integer agentId, String source,String typeAbonnement, Pageable pageable);



  @Query("SELECT p FROM DemandeAbonnement  p where "
      + "((p.referenceChifco  = :refchifco or :refchifco is null)  and "
      + "(p.cin   = :cin or :cin is null) and "
      + "(p.referenceTT   = :referenceTT or :referenceTT is null) and "
      + "(p.telFixe   = :telfix or :telfix is null) )")
  public List<DemandeAbonnement> findoneDemandeAbonnementsbykeyword(
      @Param("refchifco") String refchifco, @Param("cin") String cin,
      @Param("referenceTT") String referenceTT, @Param("telfix") Long telfix);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE demandesabonnement  SET demandesabonnement.nb_fais_apaye_reccardement = :nbFaisAPayeRecardement WHERE demandesabonnement.demandeId = :demandeId  ",
      nativeQuery = true)
  void UpdateNbFaisApayeReccardement(@Param("nbFaisAPayeRecardement") int nbFaisAPayeRecardement,
      @Param("demandeId") Long demandeId);

  @Query(value = "select * from demandesabonnement dmdab " + "where  dmdab.demandeid = :id ",

      nativeQuery = true)
  DemandeAbonnementInterface findDemandeAbonnementsByid(@Param("id") Long id);

  @Query("SELECT a FROM DemandeAbonnement a where a.cin = :cin and a.statut.nomStatut NOT LIKE  'REFUSED' and  a.statut.nomStatut NOT LIKE  'CANCELED' and  a.statut.nomStatut NOT LIKE  'SAISIE_INFAISABLE' ")
  DemandeAbonnement findDemandeAbonnementByCinAndStatusAvaibled(String cin);

  @Query("SELECT a FROM DemandeAbonnement a where a.telFixe = :telefixe and a.statut.nomStatut NOT LIKE  'REFUSED' and  a.statut.nomStatut NOT LIKE  'CANCELED' and  a.statut.nomStatut NOT LIKE  'RESILIATION' and  a.statut.nomStatut NOT LIKE  'SAISIE_INFAISABLE' ")
  DemandeAbonnement findDemandeAbonnementByTelfixeAndStatusAvaibled(Long telefixe);

  @Query("SELECT a FROM DemandeAbonnement a where a.cin = :cin and a.statut.nomStatut NOT LIKE  'REFUSED' and  a.statut.nomStatut NOT LIKE  'CANCELED' and  a.statut.nomStatut NOT LIKE  'RESILIATION' and  a.statut.nomStatut NOT LIKE  'SAISIE_INFAISABLE'  ")
  List<DemandeAbonnement> findDemandeAbonnementsByCinAndStatusAvaibled(String cin);

  @Query(value = "select dmd.referenceChifco from DemandeAbonnement dmd where demandeId =:id")
  String findReferenceChifco(Long id);

  @Query(
      value = "select dmd.contratPdf from DemandeAbonnement dmd where referenceChifco =:ReferenceChifco")
  String findContratPdfByReferenceChifco(String ReferenceChifco);

  @Query(
      value = "SELECT CASE WHEN EXISTS ( SELECT * FROM demandesabonnement ab JOIN users us ON us.userid = ab.assigned_to"
          + " WHERE us.affected_to = :idUser AND ab.demandeid = :idDemande ) THEN CAST(1 AS bit) ELSE CAST(0 AS bit) END ",
      nativeQuery = true)
  Boolean findDemandeAbonnementAffectedToUser(Long idDemande, Long idUser);

  @Query("SELECT abn.cin FROM Abonnement abn WHERE abn.referenceClient = :reference")
  String getCinByReference(String reference);

  @Query("SELECT abn.cin FROM DemandeAbonnement abn WHERE abn.demandeId = :demandeId")
  String getCinById(Long demandeId);

  @Query(" SELECT new crm.chifco.com.DTOclass.CalculeCommisionDemande( "
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'DEnAttente' AND d.user.userid = :userId THEN 1 ELSE NULL END ) AS countAllDemandeEnAttente,"
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId THEN 1 ELSE NULL END ) AS countAllDemandeAccecpted,"
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'RCommercial' AND d.user.userid = :userId THEN 1 ELSE NULL END) AS countAllDemandeRejected,"
      + "  COUNT(CASE WHEN ( d.decisionDemande.codeClassification != 'DDEMANDE') AND d.user.userid = :userId THEN 1 ELSE NULL END) AS countAllDemandeClassification,"
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId  AND (d.pack.debitPack = '10' or d.pack.debitPack = '12') THEN 1 ELSE NULL END  ) AS countDemandeAcceptedDebit10Or12,"
      + " COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId  AND d.pack.debitPack = '20'  THEN 1 ELSE NULL END  ) AS countDemandeAcceptedDebit20, "
      + " COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId  AND d.pack.debitPack = '30'  THEN 1 ELSE NULL END  ) AS countDemandeAcceptedDebit30,"
      + " COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId  AND d.pack.debitPack = '50'  THEN 1 ELSE NULL END  ) AS countDemandeAcceptedDebit50 ,"
      + " COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId  AND d.pack.debitPack = '100'  THEN 1 ELSE NULL END  ) AS countDemandeAcceptedDebit100 ,"
      + " COUNT(CASE WHEN  (d.statut.nomStatut = 'ACTIVE' or d.statut.nomStatut = 'UNPAID' or d.statut.nomStatut = 'RESILIATION') AND d.user.userid = :userId    THEN 1 ELSE NULL END  ) AS countDemandePasseToActivate ) "
      + "FROM DemandeAbonnement d WHERE d.createdDate >= :startOfMonth AND d.createdDate <= :endOfMonth")
  CalculeCommisionDemande calculeCommDemande(Long userId, Date startOfMonth, Date endOfMonth);

  @Query("SELECT d.referenceChifco, d.statut.nomStatut, d.statut.designation FROM DemandeAbonnement d "
      + " WHERE d.decisionDemande.codeClassification = 'ACCEPTATION' AND "
      + " d.user.userid = :userId AND d.createdDate >= :startOfMonth AND "
      + " d.createdDate <= :endOfMonth")
  List<Object[]> getDemandeDtailsCommission(Long userId, Date startOfMonth, Date endOfMonth);


  @Query("SELECT d.referenceChifco, d.statut.nomStatut, d.statut.designation FROM DemandeAbonnement d "
      + " WHERE d.decisionDemande.codeClassification = 'ACCEPTATION' AND "
      + " d.user.userid = :userId AND d.createdDate >= :startOfMonth AND "
      + " d.createdDate <= :endOfMonth AND (d.isCommisionSaved = false or d.isCommisionSaved is null)")
  List<Object[]> getDemandeDtailsCommissionNotInPromo(Long userId, Date startOfMonth,
      Date endOfMonth);

  @Query(
      value = "select d.proprietaire from demandesabonnement d join abonnement a on a.demande_abonnement = d.demandeid where a.clientid = :id",
      nativeQuery = true)
  Boolean findProprietaireByClientId(Long id);

  @Query(
      value = "select distinct motif_refus from demandesabonnement where motif_refus is not null",
      nativeQuery = true)
  List<String> findMotifInstance();

  @Query(
      value = "select distinct motif_refus from demandesabonnement where etattt = :StatutTT and motif_refus is not null",
      nativeQuery = true)
  List<String> findMotifInstanceByStatutTT(String StatutTT);


  List<DemandeAbonnement> findDemandeAbonnementBydateDeMiseEnService(Date date);



  @Modifying
  @Transactional
  @Query(
      value = "UPDATE demandesabonnement  SET demandesabonnement.is_commision_saved = :statutCommision WHERE demandesabonnement.reference_chifco in :stringListOfReferenceDemande  ",
      nativeQuery = true)
  void updateIsCommisionSaved(List<String> stringListOfReferenceDemande, Boolean statutCommision);

  @Query(" SELECT new crm.chifco.com.DTOclass.CalculeCommisionDemande( "
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'DEnAttente' AND d.user.userid = :userId THEN 1 ELSE NULL END ) AS countAllDemandeEnAttente,"
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId THEN 1 ELSE NULL END ) AS countAllDemandeAccecpted,"
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'RCommercial' AND d.user.userid = :userId THEN 1 ELSE NULL END) AS countAllDemandeRejected,"
      + "  COUNT(CASE WHEN ( d.decisionDemande.codeClassification != 'DDEMANDE') AND d.user.userid = :userId THEN 1 ELSE NULL END) AS countAllDemandeClassification,"
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId  AND (d.pack.debitPack = '10' or d.pack.debitPack = '12') THEN 1 ELSE NULL END  ) AS countDemandeAcceptedDebit10Or12,"
      + " COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId  AND d.pack.debitPack = '20'  THEN 1 ELSE NULL END  ) AS countDemandeAcceptedDebit20, "
      + " COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId  AND d.pack.debitPack = '30'  THEN 1 ELSE NULL END  ) AS countDemandeAcceptedDebit30,"
      + " COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId  AND d.pack.debitPack = '50'  THEN 1 ELSE NULL END  ) AS countDemandeAcceptedDebit50 ,"
      + " COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId  AND d.pack.debitPack = '100'  THEN 1 ELSE NULL END  ) AS countDemandeAcceptedDebit100 ,"
      + " COUNT(CASE WHEN  (d.statut.nomStatut = 'ACTIVE' or d.statut.nomStatut = 'UNPAID' or d.statut.nomStatut = 'RESILIATION'  or d.statut.nomStatut = 'VALID' or d.statut.nomStatut = 'POROFORMA' or d.statut.nomStatut = 'ASSIGNED') AND d.user.userid = :userId    THEN 1 ELSE NULL END  ) AS countDemandePasseToActivate ) "
      + "FROM DemandeAbonnement d WHERE d.createdDate >= :startOfMonth AND d.createdDate <= :endOfMonth AND (d.isCommisionSaved = false or d.isCommisionSaved is null )")
  CalculeCommisionDemande calculeCommDemandeNotInCommisionPromo(Long userId, Date startOfMonth,
      Date endOfMonth);

  @Query("SELECT COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId THEN 1 ELSE NULL END )"
      + "FROM DemandeAbonnement d WHERE d.dateDeMiseEnService >= :startOfMonth AND d.dateDeMiseEnService  <= :endOfMonth")
  Long calculeTotalDemandeAccepterByMiseEnService(Long userId, Date startOfMonth, Date endOfMonth);

  @Query("SELECT COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION' AND d.user.userid = :userId THEN 1 ELSE NULL END )"
      + "FROM DemandeAbonnement d WHERE d.createdDate >= :startOfMonth AND d.createdDate <= :endOfMonth")
  Long calculeTotalDemandeAccepter(Long userId, Date startOfMonth, Date endOfMonth);

  @Query("SELECT d.referenceChifco, d.statut.nomStatut, d.statut.designation FROM DemandeAbonnement d "
      + " WHERE " + " d.user.userid = :userId AND d.createdDate >= :startOfMonth AND "
      + " d.createdDate <= :endOfMonth")
  List<Object[]> getDemandeDtailsCommissionPromo(Long userId, Date startOfMonth, Date endOfMonth);

  @Modifying
  @Transactional
  @Query("UPDATE DemandeAbonnement SET statut = :status WHERE demandeId in :demandeAbonnementResiler")
  void findDemandeAbonementAndResilier(List<Long> demandeAbonnementResiler, Statut status);


  @Query("SELECT d.gouvernorat.gouvernoratName, COUNT(d) FROM DemandeAbonnement d GROUP BY d.gouvernorat.gouvernoratName")
  List<Object[]> countDemandeByGouvernorat();


  @Query(value = "SELECT DISTINCT YEAR(d.createdDate) FROM DemandeAbonnement d")
  List<Integer> getDistinctYears();

  @Query(value = "SELECT " + "YEAR(d.createdDate) AS year, " + "COUNT(d.demandeId) AS totalCount, "
      + "COUNT(CASE WHEN d.etatTT IN ('Annulée', 'Annulée par client') OR d.statut.nomStatut='CANCELED' THEN 1 END) AS annuleeCount, "
      + "COUNT(CASE WHEN d.etatTT IN ('Instance', 'Résilié par TT', 'REFUSED') OR d.statut.nomStatut='REFUSED' THEN 1 END) AS refuseCount, "
      + "COUNT(CASE WHEN d.statut.nomStatut IN ('ACTIVE') THEN 1 END) AS activeCount "
      + "FROM DemandeAbonnement d " + "LEFT JOIN d.statut s "
      + "WHERE (d.assignedTo.userid = :revId OR :revId IS NULL) AND (d.assignedTo.affectedTo = :distId OR :distId IS NULL) "
      + "AND YEAR(d.createdDate) IN :years " + "GROUP BY YEAR(d.createdDate)")
  List<Map<String, Object>> getDemandeAbonnementStatsForOthers(@Param("years") List<Integer> years,
      @Param("revId") Long revId, @Param("distId") Long distId);

  @Query(" SELECT "
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'DEnAttente' THEN 1 ELSE NULL END ) AS countAllDemandeEnAttente,"
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'ACCEPTATION'  THEN 1 ELSE NULL END ) AS countAllDemandeAccecpted,"
      + "  COUNT(CASE WHEN d.decisionDemande.codeClassification = 'RCommercial' THEN 1 ELSE NULL END) AS countAllDemandeRejected,"
      + "  COUNT(CASE WHEN ( d.decisionDemande.codeClassification != 'DDEMANDE')  THEN 1 ELSE NULL END) AS countAllDemandeClassification,"
      + " COUNT(CASE WHEN  (d.statut.nomStatut = 'ACTIVE' or d.statut.nomStatut = 'UNPAID' or d.statut.nomStatut = 'RESILIATION') THEN 1 ELSE NULL END  ) AS countDemandePasseToActivate , "
      + " u.firstName as firstname  , u.lastName as lastname  ,u.userid  as userid, u.codeUser as codeuser "
      + "From User u LEFT JOIN DemandeAbonnement d  On u.userid = d.assignedTo.userid WHERE u.typeUser = 'REVENDEUR' AND (d.assignedTo.affectedTo=:userId OR :userId IS NULL) AND "
      + "d.createdDate >= :startOfMonth AND d.createdDate <= :endOfMonth GROUP BY u.userid,u.codeUser,u.firstName,u.lastName,u.typeUser  HAVING  u.typeUser = 'REVENDEUR' ")
  Page<CommissionDemDash> calculeCommDemandeDash(Long userId, Date startOfMonth, Date endOfMonth,
      Pageable pageable);

  @Query("SELECT new map(CONCAT(u.firstName, ' ', u.lastName) as chefSecteur, "
      + "COUNT(da.id) as demandeAbonnementCount, "
      + "COUNT(CASE WHEN da.etatTT IN( 'Mise en service TT','Clôturée') AND da.statut.nomStatut = 'INSTALLED' AND da.dateDeMiseEnService IS NOT NULL AND da.dateDesactivation IS NULL THEN 1 END) as demandeMiseService, "
      + "COUNT(CASE WHEN da.statut.nomStatut = 'ASSIGNED' AND da.dateDeMiseEnService IS NOT NULL "
      + "AND da.dateDesactivation IS NULL AND da.modem IS NOT NULL THEN 1 END) as demandeModemAffected, "
      + "COUNT(CASE WHEN da.etatTT = 'Instance' THEN 1 END) as instanceCount,"
      + "COUNT(CASE WHEN da.etatTT = 'Construction Ligne' THEN 1 END) as constructionCount, "
      + "COUNT(CASE WHEN da.statut.nomStatut = 'POROFORMA' THEN 1 END) as proformatCount,"
      + "COUNT(CASE WHEN da.statut.nomStatut = 'CANCELED' THEN 1 END) as canceledCount,"
      + "COUNT(CASE WHEN da.statut.nomStatut = 'REFUSED' THEN 1 END) as refusedCount "
      + ") FROM User u " + "LEFT JOIN User revendeur ON revendeur.affectedTo = u.userid "
      + "LEFT JOIN DemandeAbonnement da ON da.user = revendeur.userid WHERE u.typeUser='DISTRIBUTEUR' "
      + "  AND (:chefsecteurId IS NULL OR u.userid = :chefsecteurId) "
      + "  AND (:startOfDate IS NULL AND :endOfDate IS NULL OR da.createdDate BETWEEN :startOfDate AND :endOfDate) "
      + "  AND u.enabled = true " + "  AND da.decisionDemande.classificationId != 1 "
      + "GROUP BY u.userid, u.firstName, u.lastName")
  List<Map<String, Object>> getDemandeAbonnementCountByChefSecteurAndDateRange(
      @Param("startOfDate") Date startOfDate, @Param("endOfDate") Date endOfDate,
      @Param("chefsecteurId") Long chefsecteurId);

  @Query("SELECT new map( " + "COUNT(da.id) as demandeAbonnementCount, "
      + "COUNT(CASE WHEN da.etatTT IN( 'Mise en service TT','Clôturée') AND da.statut.nomStatut = 'INSTALLED' AND da.dateDeMiseEnService IS NOT NULL AND da.dateDesactivation IS NULL THEN 1 END) as demandeMiseService, "
      + "COUNT(CASE WHEN da.statut.nomStatut = 'ASSIGNED' AND da.dateDeMiseEnService IS NOT NULL "
      + "AND da.dateDesactivation IS NULL AND da.modem IS NOT NULL THEN 1 END) as demandeModemAffected, "
      + "COUNT(CASE WHEN da.etatTT = 'Instance' THEN 1 END) as instanceCount,"
      + "COUNT(CASE WHEN da.etatTT = 'Construction Ligne' THEN 1 END) as constructionCount, "
      + "COUNT(CASE WHEN da.statut.nomStatut = 'POROFORMA' THEN 1 END) as proformatCount,"
      + "COUNT(CASE WHEN da.statut.nomStatut = 'CANCELED' THEN 1 END) as canceledCount,"
      + "COUNT(CASE WHEN da.statut.nomStatut = 'REFUSED' THEN 1 END) as refusedCount " + ")  "
      + " From DemandeAbonnement da WHERE  "
      + "   (:startOfDate IS NULL AND :endOfDate IS NULL OR da.createdDate BETWEEN :startOfDate AND :endOfDate) "
      + "  AND da.decisionDemande.classificationId != 1 ")
  List<Map<String, Object>> getDemandeAbonnementCountAndDateRange(
      @Param("startOfDate") Date startOfDate, @Param("endOfDate") Date endOfDate);



  @Query("SELECT new map(CONCAT(u.firstName, ' ', u.lastName) as chefSecteur, "
      + "COUNT(da.id) as totalDemandeAbonnement, "
      + "COUNT(CASE WHEN da.decisionDemande.classificationId = 3 THEN 1 ELSE NULL END) as rejectedCommercialCount, "
      + "COUNT(CASE WHEN da.decisionDemande.classificationId = 2 THEN 1 ELSE NULL END) as acceptCommercialCount, "
      + "COUNT(CASE WHEN da.decisionDemande.classificationId = 4 THEN 1 ELSE NULL END) as attenteCommercialCount) "
      + "FROM User u LEFT JOIN User revendeur ON revendeur.affectedTo = u.userid "
      + "LEFT JOIN DemandeAbonnement da ON da.user = revendeur "
      + "WHERE  u.typeUser='DISTRIBUTEUR' AND (u.userid = :chefsecteurId OR :chefsecteurId IS NULL) "
      + "AND ((da.createdDate BETWEEN :startOfDate AND :endOfDate) OR (:startOfDate IS NULL AND :endOfDate IS NULL)) "
      + "AND u.enabled = true AND da.decisionDemande.classificationId != 1 "
      + "GROUP BY u.userid, u.firstName, u.lastName")
  List<Map<String, Object>> getRejectCommerCountByChefSecteurAndDateRange(
      @Param("startOfDate") Date startOfDate, @Param("endOfDate") Date endOfDate,
      @Param("chefsecteurId") Long chefsecteurId);


  @Query("SELECT new map(COUNT(da.id) as totalDemandeAbonnement, "
      + "COUNT(CASE WHEN da.decisionDemande.classificationId = 3 THEN 1 ELSE NULL END) as rejectedCommercialCount, "
      + "COUNT(CASE WHEN da.decisionDemande.classificationId = 2 THEN 1 ELSE NULL END) as acceptCommercialCount, "
      + "COUNT(CASE WHEN da.decisionDemande.classificationId = 4 THEN 1 ELSE NULL END) as attenteCommercialCount) "
      + "FROM DemandeAbonnement da "
      + "WHERE ((da.createdDate BETWEEN :startOfDate AND :endOfDate) OR (:startOfDate IS NULL AND :endOfDate IS NULL)) "
      + "AND da.decisionDemande.classificationId != 1")
  List<Map<String, Object>> getRejectCommerCountTotalAndDateRange(
      @Param("startOfDate") Date startOfDate, @Param("endOfDate") Date endOfDate);


  @Query("SELECT new map(MONTH(da.createdDate) as month, "
      + "COALESCE(COUNT(da.id), 0) as demandeAbonnementCount) FROM DemandeAbonnement da "
      + "WHERE YEAR(da.createdDate) = :year AND (:revId IS NULL OR da.user.userid = :revId) AND (da.user.affectedTo=:chefSecteurId) "
      + "GROUP BY MONTH(da.createdDate) " + "ORDER BY MONTH(da.createdDate)")
  List<Map<String, Object>> getDemandeAbonnementCountByMonthForYear(@Param("year") int year,
      @Param("revId") Long revId, Long chefSecteurId);

  @Query(
      value = "SELECT TOP 1 da.demandeid,da.reference_chifco, da.first_name, da.last_name, s.designation AS statut_designation, "
          + " da.created_date, da.tel_fixe, u.first_name AS user_first_name, u.last_name AS user_last_name, "
          + " da.etattt, p.title AS pack_title,o.title as offreName,creater.first_name as firstNameCreator,"
          + " creater.last_name as lastNameCreator,editor.first_name as firstNameEditor,editor.last_name as lastNameEditor,"
          + " assigner.first_name as firstNameAssigner,assigner.last_name as lastNameAssigner,da.adresse,da.tel_mobile,gov.gouvernorat_name as gouvernorat,"
          + " vil.ville_name as ville "
          + " FROM demandesabonnement da LEFT JOIN Users u ON da.user_id = u.userid "
          + " LEFT JOIN status s ON s.statut_id = da.statut_id "
          + " LEFT JOIN pack p ON da.pack_id = p.pack_id left join offre o on o.offre_id=p.offre_id"
          + " LEFT JOIN users as creater on creater.userid=da.user_id Left join users as editor on editor.userid=da.edited_by "
          + " LEFT JOIN users as assigner on assigner.userid=da.assigned_to "
          + " LEFT JOIN gouvernorats as gov on da.gouvernorat_id=gov.gouvernorat_id "
          + " LEFT JOIN villes as vil on vil.ville_id=da.ville_id "
          + "WHERE (u.affected_to = :chefSecteurId OR :chefSecteurId IS NULL) "
          + "AND (da.cin = :cin OR :cin IS NULL) "
          + "AND (da.tel_fixe = :telfixe OR :telfixe IS NULL) "
          + "AND (da.reference_chifco = :referenceChifco OR :referenceChifco IS NULL) "
          + "AND s.nom_statut != 'RESILIATION' " + "AND da.decision_demande_classification_id != 1 "
          + "ORDER BY da.created_date",
      nativeQuery = true)
  Map<String, Object> getDemandeAbonnementByCinByTelFixeByReferenceNety(
      @Param("chefSecteurId") Long chefSecteurId, @Param("cin") String cin,
      @Param("telfixe") String telfixe, @Param("referenceChifco") String referenceChifco);


  @Query("SELECT new map(MONTH(da.dateDeMiseEnService) as month, "
      + "COALESCE(COUNT(da.id), 0) as demandeAbonnementCount) FROM DemandeAbonnement da "
      + "WHERE YEAR(da.dateDeMiseEnService) = :year AND (:revId IS NULL OR da.user.userid = :revId) AND (da.user.affectedTo=:chefSecteurId) "
      + "AND da.dateDeMiseEnService IS NOT NULL AND da.etatTT IN ('Mise en service TT','Clôturée') "
      + "GROUP BY MONTH(da.dateDeMiseEnService) " + "ORDER BY MONTH(da.dateDeMiseEnService)")
  List<Map<String, Object>> getDemandeAbonnementRealiserCountByMonthForYear(@Param("year") int year,
      @Param("revId") Long revId, Long chefSecteurId);

  @Query(value = "SELECT * " + "FROM demandesabonnement a "
      + "LEFT JOIN abonnement c ON a.demandeid = c.demande_abonnement "
      + "WHERE a.statut_id IN ('279', '288', '285') "
      + "AND c.modemid IS NULL  ORDER BY a.date_de_mise_en_service", nativeQuery = true)
  List<DemandeAbonnement> findAllClientNonConnecterMiseEnService();

  @Query(
      value = "SELECT top 1 * FROM demandesabonnement d " + "WHERE (d.cin = :cin OR :cin IS NULL) "
          + "AND (d.tel_fixe = :telfixe OR :telfixe IS NULL) " + "ORDER BY d.created_date DESC",
      nativeQuery = true)
  DemandeAbonnement findDemandeAbonnementByCinOrByTelFix(@Param("cin") String cin,
      @Param("telfixe") Long telfixe);

  @Query(value = "WITH months AS ("
      + "SELECT 1 as month_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 "
      + "UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 "
      + "UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 " + "), "
      + "stats_created AS ( " + "SELECT " + "  MONTH(d.created_date) as mois, "
      + "  COUNT(DISTINCT d.demandeid) as nouvelles_demandes, "
      + "  COUNT(DISTINCT CASE WHEN s.nom_statut = 'CLIENT_INJOIGNABLE' THEN d.demandeid END) as injoignables, "
      + "  COUNT(DISTINCT CASE WHEN s.nom_statut = 'SAISIE_INFAISABLE' THEN d.demandeid END) as infaisables, "
      + "  COUNT(DISTINCT CASE WHEN s.nom_statut = 'CANCELED' THEN d.demandeid END) as annulees, "
      + "  COUNT(DISTINCT CASE WHEN d.referencett IS NOT NULL AND d.referencett != '' THEN d.demandeid END) as commandes_saisies, "
      + "  COUNT(DISTINCT CASE WHEN d.etattt = 'Instance' THEN d.demandeid END) as instances "
      + "FROM demandesabonnement d " + "LEFT JOIN status s ON d.statut_id = s.statut_id "
      + "LEFT JOIN classification_demande c ON d.decision_demande_classification_id = c.classification_id "
      + "WHERE d.created_date >= DATEFROMPARTS(?1,1,1) "
      + "  AND d.created_date < DATEFROMPARTS(?1+1,1,1) "
      + "  AND CAST(d.created_date AS DATE) <= CAST(GETDATE() AS DATE) "
      + "  AND c.code_classification != 'DDEMANDE' " + "GROUP BY MONTH(d.created_date) " + "), "
      + "stats_mise_service AS ( " + "SELECT " + "  MONTH(d.date_de_mise_en_service) as mois, "
      + "  COUNT(DISTINCT d.demandeid) as mises_en_service " + "FROM demandesabonnement d "
      + "LEFT JOIN classification_demande c ON d.decision_demande_classification_id = c.classification_id "
      + "WHERE d.date_de_mise_en_service IS NOT NULL "
      + "  AND d.date_de_mise_en_service >= DATEFROMPARTS(?1,1,1) "
      + "  AND d.date_de_mise_en_service < DATEFROMPARTS(?1+1,1,1) "
      + "  AND CAST(d.date_de_mise_en_service AS DATE) <= CAST(GETDATE() AS DATE) "
      + "  AND c.code_classification != 'DDEMANDE' " + "GROUP BY MONTH(d.date_de_mise_en_service) "
      + "), " + "stats_resiliation AS ( " + "SELECT " + "  MONTH(a.date_resiliation) as mois, "
      + "  COUNT(DISTINCT d.demandeid) as resiliees " + "FROM demandesabonnement d "
      + "LEFT JOIN abonnement a ON d.demandeid = a.demande_abonnement "
      + "LEFT JOIN classification_demande c ON d.decision_demande_classification_id = c.classification_id "
      + "WHERE a.date_resiliation IS NOT NULL "
      + "  AND a.date_resiliation >= DATEFROMPARTS(?1,1,1) "
      + "  AND a.date_resiliation < DATEFROMPARTS(?1+1,1,1) "
      + "  AND CAST(a.date_resiliation AS DATE) <= CAST(GETDATE() AS DATE) "
      + "  AND c.code_classification != 'DDEMANDE' " + "GROUP BY MONTH(a.date_resiliation) " + "), "
      + "stats_modem_affecte AS ( " + "SELECT " + "  MONTH(a.modem_affected_date) as mois, "
      + "  COUNT(DISTINCT a.demande_abonnement) as nombre_modem_affecter " + "FROM abonnement a "
      + "WHERE a.modem_affected_date IS NOT NULL "
      + "  AND a.modem_affected_date >= DATEFROMPARTS(?1,1,1) "
      + "  AND a.modem_affected_date < DATEFROMPARTS(?1+1,1,1) "
      + "  AND CAST(a.modem_affected_date AS DATE) <= CAST(GETDATE() AS DATE) "
      + "GROUP BY MONTH(a.modem_affected_date) " + ") " + "SELECT " + "  m.month_num as mois, "
      + "  CASE m.month_num " + "    WHEN 1 THEN 'Janvier' " + "    WHEN 2 THEN 'Février' "
      + "    WHEN 3 THEN 'Mars' " + "    WHEN 4 THEN 'Avril' " + "    WHEN 5 THEN 'Mai' "
      + "    WHEN 6 THEN 'Juin' " + "    WHEN 7 THEN 'Juillet' " + "    WHEN 8 THEN 'Août' "
      + "    WHEN 9 THEN 'Septembre' " + "    WHEN 10 THEN 'Octobre' "
      + "    WHEN 11 THEN 'Novembre' " + "    WHEN 12 THEN 'Décembre' " + "  END as mois_nom, "
      + "  ISNULL(sc.nouvelles_demandes,0) as nouvelles_demandes, "
      + "  ISNULL(sm.mises_en_service,0) as mises_en_service, "
      + "  ISNULL(sma.nombre_modem_affecter,0) as nombre_modem_affecter, "
      + "  ISNULL(sc.injoignables,0) as injoignables, "
      + "  ISNULL(sc.infaisables,0) as saisies_infaisables, "
      + "  ISNULL(sc.annulees,0) as annulees_par_client, "
      + "  ISNULL(sc.commandes_saisies,0) as commandes_saisies, "
      + "  ISNULL(sr.resiliees,0) as resiliees, " + "  ISNULL(sc.instances,0) as instances "
      + "FROM months m " + "LEFT JOIN stats_created sc ON m.month_num = sc.mois "
      + "LEFT JOIN stats_mise_service sm ON m.month_num = sm.mois "
      + "LEFT JOIN stats_resiliation sr ON m.month_num = sr.mois "
      + "LEFT JOIN stats_modem_affecte sma ON m.month_num = sma.mois "
      + "WHERE m.month_num <= CASE " + "      WHEN YEAR(GETDATE()) > ?1 THEN 12 "
      + "      ELSE MONTH(GETDATE()) " + "    END " + "ORDER BY m.month_num", nativeQuery = true)
  List<Object[]> getGlobalStatisticsByYearForReport(int year);

  @Query(value = "WITH months AS ("
      + "  SELECT 1 as month_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 "
      + "  UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 "
      + "  UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 " + "), "
      + "distributeurs AS ( " + "  SELECT " + "    userid, "
      + "    CONCAT(first_name, ' ', last_name) as nom, " + "    code_user " + "  FROM users  "
      + "  WHERE typeuser = 'DISTRIBUTEUR' "
      + " AND is_locked=0   AND enabled = 1 and code_user not in ('P057-SOU','P058-SOU','P059-SOU') "
      + "), " + "stats_nouvelles_demandes AS ( " + "  SELECT "
      + "    MONTH(d.created_date) as mois, " + "    u_revendeur.affected_to as dist_id, "
      + "    COUNT(DISTINCT d.demandeid) as nouvelles_demandes " + "  FROM demandesabonnement d "
      + "  LEFT JOIN classification_demande c ON d.decision_demande_classification_id = c.classification_id "
      + "  LEFT JOIN users u_revendeur ON d.user_id = u_revendeur.userid "
      + "  WHERE d.created_date >= DATEFROMPARTS(?1,1,1) "
      + "    AND d.created_date < DATEFROMPARTS(?1+1,1,1) "
      + "    AND d.created_date < DATEADD(day, 1, CAST(GETDATE() AS DATE)) "
      + "    AND (c.code_classification != 'DDEMANDE' OR c.code_classification IS NULL) "
      + "    AND u_revendeur.affected_to IS NOT NULL "
      + "  GROUP BY MONTH(d.created_date), u_revendeur.affected_to " + "), "
      + "stats_mises_en_service AS ( " + "  SELECT "
      + "    MONTH(d.date_de_mise_en_service) as mois, "
      + "    u_revendeur.affected_to as dist_id, "
      + "    COUNT(DISTINCT d.demandeid) as mises_en_service " + "  FROM demandesabonnement d "
      + "  LEFT JOIN classification_demande c ON d.decision_demande_classification_id = c.classification_id "
      + "  LEFT JOIN users u_revendeur ON d.user_id = u_revendeur.userid "
      + "  WHERE d.date_de_mise_en_service IS NOT NULL "
      + "    AND d.date_de_mise_en_service >= DATEFROMPARTS(?1,1,1) "
      + "    AND d.date_de_mise_en_service < DATEFROMPARTS(?1+1,1,1) "
      + "    AND d.date_de_mise_en_service < DATEADD(day, 1, CAST(GETDATE() AS DATE)) "
      + "    AND (c.code_classification != 'DDEMANDE' OR c.code_classification IS NULL) "
      + "    AND u_revendeur.affected_to IS NOT NULL "
      + "  GROUP BY MONTH(d.date_de_mise_en_service), u_revendeur.affected_to " + ") " + "SELECT "
      + "  m.month_num as mois, " + "  CASE m.month_num " + "    WHEN 1 THEN 'Janvier' "
      + "    WHEN 2 THEN 'Février' " + "    WHEN 3 THEN 'Mars' " + "    WHEN 4 THEN 'Avril' "
      + "    WHEN 5 THEN 'Mai' " + "    WHEN 6 THEN 'Juin' " + "    WHEN 7 THEN 'Juillet' "
      + "    WHEN 8 THEN 'Août' " + "    WHEN 9 THEN 'Septembre' " + "    WHEN 10 THEN 'Octobre' "
      + "    WHEN 11 THEN 'Novembre' " + "    WHEN 12 THEN 'Décembre' " + "  END as mois_nom, "
      + "  d.nom as chef_secteur_nom, " + "  d.code_user as chef_secteur_code, "
      + "  d.userid as chef_secteur_id, "
      + "  ISNULL(snd.nouvelles_demandes, 0) as nouvelles_demandes, "
      + "  ISNULL(smes.mises_en_service, 0) as mises_en_service " + "FROM months m "
      + "CROSS JOIN distributeurs d "
      + "LEFT JOIN stats_nouvelles_demandes snd ON m.month_num = snd.mois AND d.userid = snd.dist_id "
      + "LEFT JOIN stats_mises_en_service smes ON m.month_num = smes.mois AND d.userid = smes.dist_id "
      + "WHERE m.month_num <= CASE " + "      WHEN YEAR(GETDATE()) > ?1 THEN 12 "
      + "      ELSE MONTH(GETDATE()) " + "    END " + "ORDER BY m.month_num, d.nom",
      nativeQuery = true)
  List<Object[]> getChefSecteurStatisticsByYearReport(int year);

  @Query(value = "WITH days AS ("
      + "  SELECT 1 as day_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 "
      + "  UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 "
      + "  UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 "
      + "  UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 "
      + "  UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 "
      + "  UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 "
      + "  UNION SELECT 25 UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 "
      + "  UNION SELECT 29 UNION SELECT 30 UNION SELECT 31 " + "), " + "stats_created AS ( "
      + "SELECT " + "  DAY(d.created_date) as jour, "
      + "  COUNT(DISTINCT d.demandeid) as nouvelles_demandes, "
      + "  COUNT(DISTINCT CASE WHEN s.nom_statut = 'CLIENT_INJOIGNABLE' THEN d.demandeid END) as injoignables, "
      + "  COUNT(DISTINCT CASE WHEN s.nom_statut = 'SAISIE_INFAISABLE' THEN d.demandeid END) as infaisables, "
      + "  COUNT(DISTINCT CASE WHEN s.nom_statut = 'CANCELED' THEN d.demandeid END) as annulees, "
      + "  COUNT(DISTINCT CASE WHEN d.referencett IS NOT NULL AND d.referencett != '' THEN d.demandeid END) as commandes_saisies, "
      + "  COUNT(DISTINCT CASE WHEN d.etattt = 'Instance' THEN d.demandeid END) as instances "
      + "FROM demandesabonnement d " + "LEFT JOIN status s ON d.statut_id = s.statut_id "
      + "LEFT JOIN classification_demande c ON d.decision_demande_classification_id = c.classification_id "
      + "WHERE d.created_date >= DATEFROMPARTS(?1,?2,1) "
      + "  AND d.created_date < DATEADD(month, 1, DATEFROMPARTS(?1,?2,1)) "
      + "  AND CAST(d.created_date AS DATE) <= CAST(GETDATE() AS DATE) "
      + "  AND c.code_classification != 'DDEMANDE' " + "GROUP BY DAY(d.created_date) " + "), "
      + "stats_mise_service AS ( " + "SELECT " + "  DAY(d.date_de_mise_en_service) as jour, "
      + "  COUNT(DISTINCT d.demandeid) as mises_en_service " + "FROM demandesabonnement d "
      + "LEFT JOIN classification_demande c ON d.decision_demande_classification_id = c.classification_id "
      + "WHERE d.date_de_mise_en_service IS NOT NULL "
      + "  AND d.date_de_mise_en_service >= DATEFROMPARTS(?1,?2,1) "
      + "  AND d.date_de_mise_en_service < DATEADD(month, 1, DATEFROMPARTS(?1,?2,1)) "
      + "  AND CAST(d.date_de_mise_en_service AS DATE) <= CAST(GETDATE() AS DATE) "
      + "  AND c.code_classification != 'DDEMANDE' " + "GROUP BY DAY(d.date_de_mise_en_service) "
      + "), " + "stats_resiliation AS ( " + "SELECT " + "  DAY(a.date_resiliation) as jour, "
      + "  COUNT(DISTINCT d.demandeid) as resiliees " + "FROM demandesabonnement d "
      + "LEFT JOIN abonnement a ON d.demandeid = a.demande_abonnement "
      + "LEFT JOIN classification_demande c ON d.decision_demande_classification_id = c.classification_id "
      + "WHERE a.date_resiliation IS NOT NULL "
      + "  AND a.date_resiliation >= DATEFROMPARTS(?1,?2,1) "
      + "  AND a.date_resiliation < DATEADD(month, 1, DATEFROMPARTS(?1,?2,1)) "
      + "  AND CAST(a.date_resiliation AS DATE) <= CAST(GETDATE() AS DATE) "
      + "  AND c.code_classification != 'DDEMANDE' " + "GROUP BY DAY(a.date_resiliation) " + "), "
      + "stats_modem_affecte AS ( " + "SELECT " + "  DAY(a.modem_affected_date) as jour, "
      + "  COUNT(DISTINCT a.demande_abonnement) as nombre_modem_affecter " + "FROM abonnement a "
      + "WHERE a.modem_affected_date IS NOT NULL "
      + "  AND a.modem_affected_date >= DATEFROMPARTS(?1,?2,1) "
      + "  AND a.modem_affected_date < DATEADD(month, 1, DATEFROMPARTS(?1,?2,1)) "
      + "  AND CAST(a.modem_affected_date AS DATE) <= CAST(GETDATE() AS DATE) "
      + "GROUP BY DAY(a.modem_affected_date) " + ") " + "SELECT " + "  d.day_num as jour, "
      + "  ISNULL(sc.nouvelles_demandes,0) as nouvelles_demandes, "
      + "  ISNULL(sm.mises_en_service,0) as mises_en_service, "
      + "  ISNULL(sma.nombre_modem_affecter,0) as nombre_modem_affecter, "
      + "  ISNULL(sc.injoignables,0) as injoignables, "
      + "  ISNULL(sc.infaisables,0) as saisies_infaisables, "
      + "  ISNULL(sc.annulees,0) as annulees_par_client, "
      + "  ISNULL(sc.commandes_saisies,0) as commandes_saisies, "
      + "  ISNULL(sr.resiliees,0) as resiliees, " + "  ISNULL(sc.instances,0) as instances "
      + "FROM days d " + "LEFT JOIN stats_created sc ON d.day_num = sc.jour "
      + "LEFT JOIN stats_mise_service sm ON d.day_num = sm.jour "
      + "LEFT JOIN stats_resiliation sr ON d.day_num = sr.jour "
      + "LEFT JOIN stats_modem_affecte sma ON d.day_num = sma.jour "
      + "WHERE d.day_num <= DAY(EOMONTH(DATEFROMPARTS(?1,?2,1))) " + "ORDER BY d.day_num",
      nativeQuery = true)
  List<Object[]> getGlobalStatisticsByMonthAndYear(@Param("year") int year,
      @Param("month") int month);

  @Query(value = "WITH days AS ("
      + "  SELECT 1 as day_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 "
      + "  UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 "
      + "  UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 "
      + "  UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 "
      + "  UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 "
      + "  UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 "
      + "  UNION SELECT 25 UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 "
      + "  UNION SELECT 29 UNION SELECT 30 UNION SELECT 31 " + "), " + "distributeurs AS ( "
      + "  SELECT " + "    userid, " + "    CONCAT(first_name, ' ', last_name) as nom, "
      + "    code_user " + "  FROM users " + "  WHERE typeuser = 'DISTRIBUTEUR' "
      + "    AND enabled = 1 AND is_locked=0 and code_user not in ('P057-SOU','P058-SOU','P059-SOU') "
      + "), " + "stats_nouvelles_demandes AS ( " + "  SELECT " + "    DAY(d.created_date) as jour, "
      + "    u_revendeur.affected_to as dist_id, "
      + "    COUNT(DISTINCT d.demandeid) as nouvelles_demandes " + "  FROM demandesabonnement d "
      + "  LEFT JOIN classification_demande c ON d.decision_demande_classification_id = c.classification_id "
      + "  LEFT JOIN users u_revendeur ON d.user_id = u_revendeur.userid "
      + "  WHERE d.created_date >= DATEFROMPARTS(?1,?2,1) "
      + "    AND d.created_date < DATEADD(month, 1, DATEFROMPARTS(?1,?2,1)) "
      + "    AND d.created_date < DATEADD(day, 1, CAST(GETDATE() AS DATE)) "
      + "    AND (c.code_classification != 'DDEMANDE' OR c.code_classification IS NULL) "
      + "    AND u_revendeur.affected_to IS NOT NULL "
      + "  GROUP BY DAY(d.created_date), u_revendeur.affected_to " + "), "
      + "stats_mises_en_service AS ( " + "  SELECT "
      + "    DAY(d.date_de_mise_en_service) as jour, " + "    u_revendeur.affected_to as dist_id, "
      + "    COUNT(DISTINCT d.demandeid) as mises_en_service " + "  FROM demandesabonnement d "
      + "  LEFT JOIN classification_demande c ON d.decision_demande_classification_id = c.classification_id "
      + "  LEFT JOIN users u_revendeur ON d.user_id = u_revendeur.userid "
      + "  WHERE d.date_de_mise_en_service IS NOT NULL "
      + "    AND d.date_de_mise_en_service >= DATEFROMPARTS(?1,?2,1) "
      + "    AND d.date_de_mise_en_service < DATEADD(month, 1, DATEFROMPARTS(?1,?2,1)) "
      + "    AND d.date_de_mise_en_service < DATEADD(day, 1, CAST(GETDATE() AS DATE)) "
      + "    AND (c.code_classification != 'DDEMANDE' OR c.code_classification IS NULL) "
      + "    AND u_revendeur.affected_to IS NOT NULL "
      + "  GROUP BY DAY(d.date_de_mise_en_service), u_revendeur.affected_to " + ") " + "SELECT "
      + "  d.day_num as jour, " + "  dist.nom as chef_secteur_nom, "
      + "  dist.code_user as chef_secteur_code, " + "  dist.userid as chef_secteur_id, "
      + "  ISNULL(snd.nouvelles_demandes, 0) as nouvelles_demandes, "
      + "  ISNULL(smes.mises_en_service, 0) as mises_en_service " + "FROM days d "
      + "CROSS JOIN distributeurs dist "
      + "LEFT JOIN stats_nouvelles_demandes snd ON d.day_num = snd.jour AND dist.userid = snd.dist_id "
      + "LEFT JOIN stats_mises_en_service smes ON d.day_num = smes.jour AND dist.userid = smes.dist_id "
      + "WHERE d.day_num <= DAY(EOMONTH(DATEFROMPARTS(?1,?2,1))) " + "ORDER BY d.day_num, dist.nom",
      nativeQuery = true)
  List<Object[]> getChefSecteurStatisticsByMonthAndYear(@Param("year") int year,
      @Param("month") int month);
}
