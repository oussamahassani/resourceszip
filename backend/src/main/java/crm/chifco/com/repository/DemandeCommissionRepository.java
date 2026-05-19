package crm.chifco.com.repository;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import crm.chifco.com.model.Commission;
import crm.chifco.com.model.DemandeCommission;

public interface DemandeCommissionRepository extends JpaRepository<DemandeCommission, Long> {

  @Query("SELECT c FROM DemandeCommission c WHERE "

      + "(c.commission.annee = :annee OR :annee IS NULL) "
      + "AND (c.commission.mois = :numMois OR :numMois IS NULL) "
      + "AND ((:statut IS NULL AND (c.statut = 'IN_PROGRESS' OR c.statut = 'AWAINTING_INVOICING')) "
      + "OR (:statut IS NOT NULL AND c.statut = :statut)) "
      + "AND (c.createdDate >= :startCreatedDate OR :startCreatedDate IS NULL) "
      + "AND (c.createdDate <= :endCreatedDate OR :endCreatedDate IS NULL) "
      + "AND (c.refDemandeCommission = :reference OR :reference IS NULL) "
      + "AND (c.demandeBy.codeUser = :codeRevendeur OR :codeRevendeur IS NULL)  and c.groupe_id is null  ")
  Page<DemandeCommission> getAllDemandeCommission(Pageable page, Integer annee, Integer numMois,
      String codeRevendeur, Date startCreatedDate, Date endCreatedDate, String reference,
      String statut);

  @Query("SELECT c FROM DemandeCommission c where c.demandeBy.userid = :idRev "
      + "AND (c.commission.annee = :annee OR :annee IS NULL) "
      + "AND (c.commission.mois = :numMois OR :numMois IS NULL) "
      + "AND (c.refDemandeCommission = :reference OR :reference IS NULL) "
      + "AND (statut IN ('IN_PROGRESS', 'AWAINTING_INVOICING'))")

  Page<DemandeCommission> getDemandeCommissionByRev(Pageable page, Long idRev, Integer annee,
      Integer numMois, String reference);



  @Query("SELECT c FROM DemandeCommission c")
  List<DemandeCommission> getAllDemandeCommission();

  // Méthode alternative pour déboguer
  // List<DemandeCommission> getAllDemandeCommission();

  @Query(value = "SELECT COUNT(*) FROM demande_commission", nativeQuery = true)
  Long countAllDemandes();

  List<DemandeCommission> findAllByCommissionId(Long commission);

  @Query("SELECT COUNT(d) FROM DemandeCommission d WHERE d.commission.id = :commissionId")
  Integer countByCommissionId(Long commissionId);

  DemandeCommission findByCommissionIdAndStatut(Long id, String statut);

  @Query(
      value = "SELECT COUNT(*) FROM demande_commission d WHERE d.commission_id = :commissionId AND (d.statut = 'IN_PROGRESS'  OR d.statut = 'PAID')",
      nativeQuery = true)
  int countDemandesByCommissionAndStatut(Long commissionId);

  @Query("SELECT c FROM DemandeCommission c WHERE "
	      + "(c.commission.annee = :annee OR :annee IS NULL) "
	      + "AND (c.commission.mois = :numMois OR :numMois IS NULL) " + "AND (c.statut = 'IN_PROGRESS') "
	      + "AND (c.createdDate >= :startCreatedDate OR :startCreatedDate IS NULL) "
	      + "AND (c.createdDate <= :endCreatedDate OR :endCreatedDate IS NULL) "
	      + "AND (c.refDemandeCommission = :referenceFacts OR :referenceFacts IS NULL) "
	      + "AND (c.demandeBy.codeUser = :codeRevendeurFacts OR :codeRevendeurFacts IS NULL) ")
List<DemandeCommission> findCommisionForFazctureMultiple(Integer annee,Integer numMois, String codeRevendeurFacts,
		Date startCreatedDate, Date endCreatedDate, String referenceFacts);

//  List<DemandeCommission> findByGroupe_Id(Long groupId);

}
