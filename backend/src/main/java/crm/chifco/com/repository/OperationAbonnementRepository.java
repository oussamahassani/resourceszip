package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.model.OperationAbonnement;

public interface OperationAbonnementRepository extends JpaRepository<OperationAbonnement, Long> {
  @Query(value = "select * from operationabonnement dmdab "
      + "where (  (dmdab.etattt = :statutTTListfiltre or :statutTTListfiltre is null) "
      + "and (dmdab.type_demande =:typeDemande OR :typeDemande IS NULL   ) "
      + "and ( dmdab.gouvernorat_id = :gouvernoratid or :gouvernoratid is null ) "
      + "and ( dmdab.ville_id = :villeid or :villeid is null ) "
      + "and ( dmdab.statut_id = :statutListfiltre or :statutListfiltre is null ) "
      + "and ( dmdab.categorie_produit_internet_id = :categorieid or :categorieid is null ) "
      + "and ( dmdab.pack_id = :produitid or :produitid is null ) "
      + "and (LOWER(dmdab.first_name)  =:nom   or :nom is null)"
      + "and (LOWER(dmdab.last_name) =:prenom  or :prenom is null)"
      + "and  (dmdab.reference_chifco =:refchif  or :refchif is null)"
      + "and ( dmdab.profession_id= :professionid or :professionid is null ) "
      + "and ( dmdab.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null ) "
      + "and ( dmdab.created_date <=  CAST(:datefin AS datetime2) or :datefin is null ) "
      + "and ( dmdab.modified_date  >= CAST(:dateDebutModification AS datetime2) or :dateDebutModification is null ) "
      + "and ( dmdab.modified_date  <= CAST(:dateFinModification AS datetime2)  or :dateFinModification is null ) "
      + "and (dmdab.referencett =:refTT  or :refTT is null )"
      + "and ( dmdab.cin   =:cin  or :cin is null) "
      + "and ( (dmdab.tel_mobile =:tel  or :tel is null )"
      + "or(dmdab.tel_fixe =:tel  or :tel is null  ) ))", nativeQuery = true)
  Page<OperationAbonnement> findDemandesMigrationsByPramsnotempty(String statutTTListfiltre,
      String refchif, String refTT, String cin, String prenom, String nom, Long tel, Long villeid,
      Long gouvernoratid, Long professionid, Long categorieid, Long produitid,
      Long statutListfiltre, String datedebut, String datefin, String dateDebutModification,
      String dateFinModification, Pageable pageable, String typeDemande);

  @Query(value = "select * from operationabonnement dmdab  "
      + "where (  (dmdab.etattt = :statutTTListfiltre or :statutTTListfiltre is null)"
      + "and ( dmdab.type_demande =:typeDemande OR :typeDemande IS NULL  ) "
      + "and ( dmdab.assigned_to =:userid OR :userid IS NULL  ) "
      + "and ( dmdab.gouvernorat_id = :gouvernoratid or :gouvernoratid is null ) "
      + "and ( dmdab.ville_id = :villeid or :villeid is null ) "
      + "and ( dmdab.statut_id = :statutListfiltre or :statutListfiltre is null ) "
      + "and ( dmdab.categorie_produit_internet_id = :categorieid or :categorieid is null ) "
      + "and ( dmdab.pack_id = :produitid or :produitid is null ) "
      + "and (LOWER(dmdab.first_name)  =:nom   or :nom is null)"
      + "and (LOWER(dmdab.last_name) =:prenom  or :prenom is null)"
      + "and  (dmdab.reference_chifco =:refchif  or :refchif is null)"
      + "and ( dmdab.profession_id= :professionid or :professionid is null ) "
      + "and ( dmdab.created_date  >= CAST(:datedebut AS datetime2)  or :datedebut is null ) "
      + "and ( dmdab.created_date <=  CAST(:datefin AS datetime2) or :datefin is null ) "
      + "and ( dmdab.modified_date  >= CAST(:dateDebutModification AS datetime2) or :dateDebutModification is null ) "
      + "and ( dmdab.modified_date  <= CAST(:dateFinModification AS datetime2)  or :dateFinModification is null ) "
      + "and (dmdab.referencett =:refTT  or :refTT is null ) "
      + "and ( dmdab.cin   =:cin  or :cin is null) "
      + "and ( (dmdab.tel_mobile =:tel  or :tel is null )"
      + "or(dmdab.tel_fixe =:tel  or :tel is null  )  " + " ))", nativeQuery = true)
  Page<OperationAbonnement> findDemandeMigrationByRev(Long userid, String statutTTListfiltre,
      String refchif, String refTT, String cin, String prenom, String nom, Long tel, Long villeid,
      Long gouvernoratid, Long professionid, Long categorieid, Long produitid,
      Long statutListfiltre, String datedebut, String datefin, String dateDebutModification,
      String dateFinModification, Pageable pageable, String typeDemande);

  @Query(
      value = "select * from operationabonnement dmdab LEFT JOIN users u on u.userid = dmdab.assigned_to "
          + "where (  (dmdab.etattt = :statutTTListfiltre or :statutTTListfiltre is null)  and ( dmdab.ville_id = :villes or :villes is null ) "
          + "and ( dmdab.type_demande =:typeDemande OR :typeDemande IS NULL  ) "
          + "and ( dmdab.gouvernorat_id = :gouvernorat or :gouvernorat is null ) "
          + "and ( dmdab.statut_id = :statutListfiltre or :statutListfiltre is null ) "
          + "and ( dmdab.categorie_produit_internet_id = :categories or :categories is null ) "
          + "and ( dmdab.pack_id = :produit or :produit is null ) "
          + "and (LOWER(dmdab.first_name)  =:nom   or :nom is null)"
          + "and (LOWER(dmdab.last_name) =:prenom  or :prenom is null)"
          + "and  (dmdab.reference_chifco =:refchif  or :refchif is null)"
          + "and ( dmdab.profession_id= :professions or :professions is null ) "
          + "and ( dmdab.created_date  >= CAST(:dateDebutCreation AS datetime2)  or :dateDebutCreation is null ) "
          + "and ( dmdab.created_date <=  CAST(:dateFinCreation AS datetime2) or :dateFinCreation is null ) "
          + "and ( dmdab.modified_date  >= CAST(:dateDebutModification AS datetime2) or :dateDebutModification is null ) "
          + "and ( dmdab.modified_date  <= CAST(:dateFinModification AS datetime2)  or :dateFinModification is null ) "
          + "and (dmdab.referencett =:refTT  or :refTT is null )"
          + "and ( dmdab.cin   =:cin  or :cin is null) and (u.affected_to = :userid or :userid IS NULL) "
          + "and (dmdab.user_id = :CreeParRecherche or :CreeParRecherche is null )"
          + "and ( (dmdab.tel_mobile =:tel  or :tel is null )"
          + "or(dmdab.tel_fixe =:tel  or :tel is null  ) ))",
      nativeQuery = true)
  Page<OperationAbonnement> findDemandMigrationByDistributeur(Long userid, String refchif,
      String refTT, String cin, @Param("prenom") String prenom, @Param("nom") String nom,
      @Param("tel") Long tel, @Param("villes") Long villes, @Param("gouvernorat") Long gouvernorat,
      @Param("professions") Long professions, @Param("categories") Long categories,
      @Param("produit") Long produit, @Param("statutListfiltre") Long statutListfiltre,
      String statutTTListfiltre, Date dateDebutModification, Date dateFinModification,
      Date dateDebutCreation, Date dateFinCreation, Long CreeParRecherche, Pageable pageable,
      String typeDemande);

  OperationAbonnement findByCin(String cin);

  OperationAbonnement findByReferenceTT(String reftt);

  @Query(value = "select TOP 1  * from operationabonnement dmdab "
      + "where dmdab.referencett =:reftt" + " ORDER BY dmdab.created_date DESC", nativeQuery = true)
  OperationAbonnement findDemandeMigrationByuniquereferencett(@Param("reftt") String reftt);

  @Query(value = "SELECT TOP 1 * FROM operationabonnement dmdab " + "WHERE dmdab.cin = :cin "
      + "ORDER BY dmdab.created_date DESC", nativeQuery = true)
  OperationAbonnement findDemandeMigrationByuniquecin(@Param("cin") String cin);

  @Query(value = "SELECT CASE WHEN " + "(SELECT TOP 1 d.statut_id " + " FROM operationabonnement d "
      + " WHERE d.reference_chifco = :referenceClient  AND d.type_demande = :typeDemande  ORDER BY d.created_date DESC) "
      + "IN (SELECT statut_id FROM status WHERE nom_statut IN ('REFUSED', 'CANCELED')) "
      + "AND (SELECT a.statutid  FROM Abonnement a  WHERE a.reference_client = :referenceClient) "
      + "IN (SELECT statut_id FROM status WHERE nom_statut IN ('ACTIVE','RECOUVREMENT', 'VALID', 'UNPAID')) "
      + "THEN 1 ELSE 0 END", nativeQuery = true)
  Integer checkOperationAbonnementAndAbonnementStatus(String referenceClient, String typeDemande);


  @Query(value = "select dmd.operationId from OperationAbonnement dmd  "
      + "where (( dmd.firstName = :firstName or :firstName is null )"
      + "and ( dmd.statut.statutId = :statutListfiltre or :statutListfiltre is null ) "
      + "and (dmd.etatTT = :statutTTListfiltre or :statutTTListfiltre is null) "
      + "and ( dmd.ville.villeId = :villes or :villes is null ) "
      + "and ( dmd.gouvernorat.gouvernoratId = :gouvernorat or :gouvernorat is null ) "
      + "and ( dmd.cin   =:cin  or :cin is null) "
      + "and ((dmd.telMobile  = :tel or :tel is null ) or (dmd.telFixe  = :tel or :tel is null ))"
      + "and (dmd.referenceTT =:refTT  or :refTT is null )"
      + "and ( dmd.referenceChifco   =:refChif  or :refChif is null)"
      + "and ( dmd.profession.professionId= :professions or :professions is null ) "
      + "and ( dmd.createdDate  >= :datedebut or :datedebut is null ) "
      + "and ( dmd.createdDate <=  :datefin or :datefin is null ) "
      + "and ( dmd.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null)  "
      + "and ( dmd.pack.packId = :produit or :produit is null ) "
      + "and ( dmd.user.userid  = :CreePar or :CreePar is null ) "
      + "and (dmd.lastName = :lastName or :lastName is null)"
      + " and (dmd.typeDemande=:typeDemande) )")
  List<Long> findAllToExportAdmin(String lastName, String firstName, Long statutListfiltre,
      String statutTTListfiltre, Long villes, Long gouvernorat, String cin, Long tel,
      String refChif, String refTT, Long professions, Date datedebut, Date datefin, Long categories,
      Long produit, Long CreePar, String typeDemande);

  @Query(value = "select dmd.operationId from OperationAbonnement dmd "
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
      + "and ( dmd.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null)  "
      + "and ( dmd.pack.packId = :produit or :produit is null ) "
      + "and (dmd.lastName = :lastName or :lastName is null)"
      + " and (dmd.typeDemande=:typeDemande) )")
  List<Long> findAllToExportRevendeur(String firstName, String lastName, Long statutListfiltre,
      String statutTTListfiltre, Long villes, Long gouvernorat, String cin, Long tel,
      String refChif, String refTT, Long professions, Date datedebut, Date datefin, Long roleid,
      Long userid, Long categories, Long produit, String typeDemande);

  @Query(value = "select dmd.operationId from OperationAbonnement dmd "
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
      + "and ( dmd.categorieProduitInternet.categorieProduitInternetId = :categories or :categories is null)  "
      + "and ( dmd.pack.packId = :produit or :produit is null ) "
      + "and ( dmd.user.userid  = :CreePar or :CreePar is null ) "
      + "and (dmd.lastName = :lastName or :lastName is null)"
      + " and (dmd.typeDemande=:typeDemande) )")
  List<Long> findAllToExportDistributeur(String lastName, String firstName, Long statutListfiltre,
      String statutTTListfiltre, Long villes, Long gouvernorat, String cin, Long tel,
      String refChif, String refTT, Long professions, Date datedebut, Date datefin, Long userid,
      Long categories, Long produit, Long CreePar, String typeDemande);

  @Query(value = "select dmd from OperationAbonnement dmd where dmd.operationId in :batche")
  List<OperationAbonnement> findByIds(List<Long> batche);


  @Query("SELECT MONTH(o.createdDate) AS month, COUNT(o) AS totalOperations, "
      + "SUM(CASE WHEN o.statut.nomStatut = 'VALID' OR o.statut.nomStatut = 'ACTIVE' THEN 1 ELSE 0 END) AS succeededOperations "
      + "FROM OperationAbonnement o "
      + "WHERE YEAR(o.createdDate) = :year  AND o.typeDemande=:typeOperation "
      + "GROUP BY MONTH(o.createdDate)")
  List<Map<String, Object>> countOperationsAndSucceededByMonth(@Param("year") int year,
      String typeOperation);


}
