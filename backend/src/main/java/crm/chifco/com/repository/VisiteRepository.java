package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.model.Visite;



public interface VisiteRepository extends JpaRepository<Visite, Long> {
  @Query(value = "SELECT v FROM Visite v WHERE "
      + " (v.createdBy.userid = :chefsecteurId OR :chefsecteurId IS NULL) "
      + " AND (:typevisiteid IS NULL OR v.typeVisite.typevisiteid =:typevisiteid) "
      + " AND (v.editedBy.userid = :editedBy OR :editedBy IS NULL) "
      + " AND (v.revndeurId = :revendeurId OR :revendeurId IS NULL) "
      + " AND (v.createdDate >= :startDate OR :startDate IS NULL) "
      + " AND (v.createdDate <= :endDate OR :endDate IS NULL) " + " ORDER BY v.createdDate DESC")
  Page<Visite> findBychefsecteurCreatedDateDesc(Long chefsecteurId, Long typevisiteid,
      Long editedBy, Long revendeurId, Date startDate, Date endDate, Pageable pageable);


  boolean existsById(Long id);

  @Query("SELECT v FROM Visite v WHERE "
      + "  (:visitType IS NULL OR v.typeVisite.nomType =:visitType) "
      + " AND (v.reference_visite = :reference_visite OR :reference_visite IS NULL) "
      + " AND (v.revndeurId = :revendeur OR :revendeur IS NULL) "
      + " AND (v.status = :status OR :status IS NULL) "
      + " AND (v.createdDate >= :visitDateStart OR :visitDateStart IS NULL) "
      + " AND (v.createdDate <= :visitDateEnd OR :visitDateEnd IS NULL) "
      + " AND (v.createdBy.userid= :creePar OR :creePar IS NULL) ")
  Page<Visite> findAllByFilters(String visitType, String reference_visite, Date visitDateStart,
      Date visitDateEnd, Long creePar, Long revendeur, String status, Pageable pageable);

  @Query("SELECT v FROM Visite v WHERE "
      + "  (:visitType IS NULL OR v.typeVisite.nomType = :visitType) "
      + " AND (:reference_visite IS NULL OR v.reference_visite = :reference_visite) "
      + " AND (:revendeur IS NULL OR v.revndeurId = :revendeur) "
      + " AND (:status IS NULL OR v.status = :status) "
      + " AND (:visitDateStart IS NULL OR v.createdDate >= :visitDateStart) "
      + " AND (:visitDateEnd IS NULL OR v.createdDate <= :visitDateEnd) "
      + " AND (:creePar IS NULL OR v.createdBy.userid = :creePar) ")
  List<Visite> findAllVisitByFiltersForExport(String visitType, String reference_visite,
      Date visitDateStart, Date visitDateEnd, Long creePar, Long revendeur, String status);

  @Query(
      value = "WITH months AS ("
          + "  SELECT 1 as month_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 "
          + "  UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 "
          + "  UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 " + "), "
          + "visites_par_mois AS ( " + "  SELECT " + "    MONTH(v.created_date) as mois, "
          + "    COUNT(v.id) as nombre_visites " + "  FROM visite v "
          + "  WHERE v.created_date >= DATEFROMPARTS(?1,1,1) "
          + "    AND v.created_date < DATEFROMPARTS(?1+1,1,1) "
          + "    AND CAST(v.created_date AS DATE) <= CAST(GETDATE() AS DATE) "
          + "  GROUP BY MONTH(v.created_date) " + ") " + "SELECT " + "  m.month_num as mois, "
          + "  CASE m.month_num " + "    WHEN 1 THEN 'Janvier' " + "    WHEN 2 THEN 'Février' "
          + "    WHEN 3 THEN 'Mars' " + "    WHEN 4 THEN 'Avril' " + "    WHEN 5 THEN 'Mai' "
          + "    WHEN 6 THEN 'Juin' " + "    WHEN 7 THEN 'Juillet' " + "    WHEN 8 THEN 'Août' "
          + "    WHEN 9 THEN 'Septembre' " + "    WHEN 10 THEN 'Octobre' "
          + "    WHEN 11 THEN 'Novembre' " + "    WHEN 12 THEN 'Décembre' " + "  END as mois_nom, "
          + "  ISNULL(vpm.nombre_visites, 0) as nombre_visites_enregistrees " + "FROM months m "
          + "LEFT JOIN visites_par_mois vpm ON m.month_num = vpm.mois "
          + "WHERE m.month_num <= CASE " + "      WHEN YEAR(GETDATE()) > ?1 THEN 12 "
          + "      ELSE MONTH(GETDATE()) " + "    END " + "ORDER BY m.month_num",
      nativeQuery = true)
  List<Object[]> getVisitesByYear(int year);

  @Query(
      value = "WITH days AS ("
          + "  SELECT 1 as day_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 "
          + "  UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 "
          + "  UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 "
          + "  UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 "
          + "  UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 "
          + "  UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 "
          + "  UNION SELECT 25 UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 "
          + "  UNION SELECT 29 UNION SELECT 30 UNION SELECT 31 " + "), " + "visites_par_jour AS ( "
          + "  SELECT " + "    DAY(v.created_date) as jour, " + "    COUNT(v.id) as nombre_visites "
          + "  FROM visite v " + "  WHERE v.created_date >= DATEFROMPARTS(?1,?2,1) "
          + "    AND v.created_date < DATEADD(month, 1, DATEFROMPARTS(?1,?2,1)) "
          + "    AND CAST(v.created_date AS DATE) <= CAST(GETDATE() AS DATE) "
          + "  GROUP BY DAY(v.created_date) " + ") " + "SELECT " + "  d.day_num as jour, "
          + "  ISNULL(vpj.nombre_visites, 0) as nombre_visites_enregistrees " + "FROM days d "
          + "LEFT JOIN visites_par_jour vpj ON d.day_num = vpj.jour "
          + "WHERE d.day_num <= DAY(EOMONTH(DATEFROMPARTS(?1,?2,1))) " + "ORDER BY d.day_num",
      nativeQuery = true)
  List<Object[]> getVisitesByMonthAndYear(@Param("year") int year, @Param("month") int month);

}


