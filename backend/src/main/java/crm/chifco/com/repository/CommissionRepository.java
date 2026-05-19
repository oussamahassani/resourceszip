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
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.User;

public interface CommissionRepository extends JpaRepository<Commission, Long> {

  @Query("SELECT c FROM Commission c WHERE (annee = :annee OR :annee IS NULL)"
      + "AND (mois = :numMois OR :numMois IS NULL)"
      + "AND(c.revendeur.codeUser = :codeRevendeur OR :codeRevendeur IS NULL)"
      + "AND (c.createdDate >= :startCreatedDate OR :startCreatedDate IS NULL) "
      + "AND (c.createdDate <= :endCreatedDate OR :endCreatedDate IS NULL) "
      + "AND (c.refCommission = :reference OR :reference IS NULL) "
      + "AND (c.statut = :statut OR :statut IS NULL) "
      + "AND (c.isFreelance = :typeC OR :typeC IS NULL)")
  Page<Commission> getAllCommission(Pageable page, Integer annee, Integer numMois,
      String codeRevendeur, String statut, Date startCreatedDate, Date endCreatedDate,
      String reference ,Boolean typeC);

  @Query("SELECT c FROM Commission c " + "WHERE (c.annee = :annee OR :annee IS NULL) "
      + "AND (c.mois = :numMois OR :numMois IS NULL) "
      + "AND (c.revendeur.codeUser = :codeRevendeur OR :codeRevendeur IS NULL) "
      + "AND (c.createdDate >= :startCreatedDate OR :startCreatedDate IS NULL) "
      + "AND (c.createdDate <= :endCreatedDate OR :endCreatedDate IS NULL) "
      + "AND (c.refCommission = :reference OR :reference IS NULL) "
      + "AND (c.statut = :statut OR :statut IS NULL) "
      + "AND (c.revendeur.affectedTo = :idUserConnected)")
  Page<Commission> getAllCommissionArea(Pageable page, Integer annee, Integer numMois,
      String codeRevendeur, String statut, Date startCreatedDate, Date endCreatedDate,
      String reference, Long idUserConnected);


  @Query("SELECT c FROM Commission c where c.revendeur.userid = :idRevendeur "
      + "AND (annee = :annee OR :annee IS NULL) " + "AND (mois = :numMois OR :numMois IS NULL)"
      + "AND (c.refCommission = :reference OR :reference IS NULL) "
      + "AND (c.statut = :statut OR :statut IS NULL)")
  Page<Commission> getAllCommissionByRev(Pageable page, Long idRevendeur, Integer annee,
      Integer numMois, String statut, String reference);

  @Modifying
  @Transactional
  @Query(value = "UPDATE commission SET statut = :statut WHERE id = :id", nativeQuery = true)
  void updateStatut(Long id, String statut);

  @Query("SELECT (CASE WHEN COUNT(cms.id) > 0 THEN false ELSE true END ) FROM Commission cms "
      + " WHERE ( cms.revendeur = :user and cms.mois = :numMois and cms.annee = :annee and cms.statut != 'CANCELLED') and"
      + "(cms.isPromo = false or cms.isPromo is null )")
  Boolean findExiteCommision(User user, Integer annee, Integer numMois);

  @Query("SELECT cms FROM Commission cms "
      + " WHERE ( cms.revendeur.userid = :user and cms.mois = :numMois and cms.annee = :annee and cms.statut != 'CANCELLED')")
  Commission findCommisionByUserId(Long user, Integer annee, Integer numMois);


  @Query("SELECT c FROM Commission c WHERE" + "(annee = :annee OR :annee IS NULL)"
      + "AND (mois = :numMois OR :numMois IS NULL)"
      + "AND(c.revendeur.codeUser = :codeRevendeur OR :codeRevendeur IS NULL)"
      + "AND (c.createdDate >= :startCreatedDate OR :startCreatedDate IS NULL) "
      + "AND (c.createdDate <= :endCreatedDate OR :endCreatedDate IS NULL) "
      + "AND (c.refCommission = :reference OR :reference IS NULL) "
      + "AND (c.statut = :statut OR :statut IS NULL) "
      + "AND (c.isFreelance = :typeCommision OR :typeCommision IS NULL)")
  List<Commission> getAllCommission(Integer annee, Integer numMois, String codeRevendeur,
      String statut, Date startCreatedDate, Date endCreatedDate, String reference ,Boolean typeCommision);

  List<Commission> findAllByRevendeurUserid(Long Userid);



  Commission findCommisionPromoByRevendeur_useridAndPeriodPromoDebutAndPeriodPromoFinAndIsPromoAndStatutNot(
      long userId, Date dateDebut, Date dateFin, boolean isPromo, String status);

  @Query("SELECT (CASE WHEN COUNT(cms.id) > 0 THEN false ELSE true END ) FROM Commission cms "
      + " WHERE ( cms.revendeur = :user and cms.periodPromoDebut = :dateDebut and cms.periodPromoFin = :dateFin and cms.statut != 'CANCELLED')")
  Boolean findExiteCommisionPromo(User user, Date dateDebut, Date dateFin);

  @Query("SELECT cms FROM Commission cms "
      + " WHERE ( cms.revendeur.userid = :userId and cms.mois = :numMois and cms.annee = :annee and cms.statut != 'CANCELLED') and (cms.isPromo = false or cms.isPromo is null )")
  Commission findCommisionByUserIdAndNotIsPromo(long userId, Integer annee, Integer numMois);


  @Query(
      value = "SELECT top 10 * FROM commission c WHERE c.statut = 'PAID' and total_ttc  <> 0 ORDER BY c.total_ttc DESC ",
      nativeQuery = true)
  List<Commission> findTop5PaidRevendeurs();

  @Query(
      value = "SELECT top 10 * FROM commission c WHERE c.statut = 'PAID' and total_ttc  <> 0 And c.revendeur_userid= :userId ORDER BY c.total_ttc DESC ",
      nativeQuery = true)
  List<Commission> findLast5comissionForRev(Long userId);

  @Query(value = "SELECT TOP 10 * FROM commission c WHERE c.statut = 'PAID' AND c.total_ttc <> 0 "
      + "AND c.annee = :yearFilter " + "ORDER BY c.total_ttc DESC", nativeQuery = true)
  List<Commission> findTop5PaidRevendeursCurrentYear(@Param("yearFilter") Integer yearFilter);

  @Query(
      value = "SELECT TOP 10 * FROM commission c WHERE c.statut = 'PAID' AND c.total_ttc <> 0 "
          + "AND c.annee = :lastYear AND c.mois = :lastMonth " + "ORDER BY c.total_ttc DESC",
      nativeQuery = true)
  List<Commission> findTop5PaidRevendeursLastMonth(@Param("lastYear") Integer lastYear,
      @Param("lastMonth") Integer lastMonth);

  @Query(value = "SELECT "
      + "SUM(CASE WHEN c.statut = 'PAID' THEN c.totalTtc ELSE 0 END) AS totalPaidAllTime, "
      + "SUM(CASE WHEN c.statut = 'PAID' AND c.annee = :yearFilter THEN c.totalTtc ELSE 0 END) AS totalPaidCurrentYear, "
      + "SUM(CASE WHEN c.statut = 'PAID' AND c.annee = :lastYear AND c.mois = :lastMonth THEN c.totalTtc ELSE 0 END) AS totalPaidLastMonth, "
      + "SUM(CASE WHEN c.statut = 'NOT_PAID' AND c.annee = :yearFilter THEN c.totalTtc ELSE 0 END) AS totalNotPaidCurrentYear, "
      + "SUM(CASE WHEN c.statut = 'NOT_PAID' AND c.annee = :lastYear AND c.mois = :lastMonth THEN c.totalTtc ELSE 0 END) AS totalNotPaidLastMonth, "
      + "SUM(CASE WHEN c.statut = 'NOT_PAID' THEN c.totalTtc ELSE 0 END) AS totalNotPaidAllTime, "
      + "SUM(CASE WHEN c.statut = 'CANCELLED' THEN c.totalTtc ELSE 0 END) AS totalCancelledAllTime, "
      + "SUM(CASE WHEN c.statut = 'CANCELLED' AND c.annee = :yearFilter THEN c.totalTtc ELSE 0 END) AS totalCancelledCurrentYear, "
      + "SUM(CASE WHEN c.statut = 'CANCELLED' AND c.annee = :lastYear AND c.mois = :lastMonth THEN c.totalTtc ELSE 0 END) AS totalCancelledLastMonth, "
      + "SUM(c.totalTtc) AS totalTtc "
      + "FROM Commission c where (c.revendeur.userid = :revId OR :revId IS NULL)")
  Map<String, Object> calculateCommissionSums(@Param("yearFilter") Integer yearFilter,
      @Param("lastMonth") Integer lastMonth, @Param("lastYear") Integer lastYear,
      @Param("revId") Long revId);

  @Query(value = "SELECT "
      + "SUM(CASE WHEN c.statut = 'PAID' THEN c.totalTtc ELSE 0 END) AS totalPaidAllTime, "
      + "SUM(CASE WHEN c.statut = 'PAID' AND c.annee = :yearFilter THEN c.totalTtc ELSE 0 END) AS totalPaidCurrentYear, "
      + "SUM(CASE WHEN c.statut = 'PAID' AND c.annee = :lastYear AND c.mois = :lastMonth THEN c.totalTtc ELSE 0 END) AS totalPaidLastMonth, "
      + "SUM(CASE WHEN c.statut = 'NOT_PAID' AND c.annee = :yearFilter THEN c.totalTtc ELSE 0 END) AS totalNotPaidCurrentYear, "
      + "SUM(CASE WHEN c.statut = 'NOT_PAID' AND c.annee = :lastYear AND c.mois = :lastMonth THEN c.totalTtc ELSE 0 END) AS totalNotPaidLastMonth, "
      + "SUM(CASE WHEN c.statut = 'NOT_PAID' THEN c.totalTtc ELSE 0 END) AS totalNotPaidAllTime, "
      + "SUM(CASE WHEN c.statut = 'CANCELLED' THEN c.totalTtc ELSE 0 END) AS totalCancelledAllTime, "
      + "SUM(CASE WHEN c.statut = 'CANCELLED' AND c.annee = :yearFilter THEN c.totalTtc ELSE 0 END) AS totalCancelledCurrentYear, "
      + "SUM(CASE WHEN c.statut = 'CANCELLED' AND c.annee = :lastYear AND c.mois = :lastMonth THEN c.totalTtc ELSE 0 END) AS totalCancelledLastMonth, "
      + "SUM(c.totalTtc) AS totalTtc " + "FROM Commission c where c.revendeur.userid = :userId ")
  Map<String, Object> calculateCommissionSumRev(@Param("yearFilter") Integer yearFilter,
      @Param("lastMonth") Integer lastMonth, @Param("lastYear") Integer lastYear, Long userId);

}
