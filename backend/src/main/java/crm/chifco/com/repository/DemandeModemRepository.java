package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.model.DemandeModem;

public interface DemandeModemRepository extends JpaRepository<DemandeModem, Long> {


  @Query("SELECT d FROM DemandeModem d WHERE " + "(user.typeUser = :type OR :type IS NULL)"
      + "AND (user.userid = :userId OR :userId Is NULL)"
      + "AND (createdDate >= :dateDebut OR :dateDebut Is NULL)"
      + "AND (createdDate <= :dateFin OR :dateFin Is NULL)")
  Page<DemandeModem> findAll(Pageable pageable, String type, Long userId, Date dateDebut,
      Date dateFin);

  @Query("SELECT d FROM DemandeModem d WHERE (user.userid IN (SELECT userid FROM User u WHERE affectedTo = :id) OR (user.userid = :idConnected OR :idConnected IS NULL))"
      + "AND (user.typeUser = :type OR :type IS NULL)"
      + "AND (user.userid = :userId OR :userId Is NULL)"
      + "AND (createdDate >= :dateDebut OR :dateDebut Is NULL)"
      + "AND (createdDate <= :dateFin OR :dateFin Is NULL)")
  Page<DemandeModem> findAllArea(Pageable pageable, Long id, Long userId, Date dateDebut,
      Date dateFin, String type, Long idConnected);

  @Query("SELECT d FROM DemandeModem d WHERE user.userid = :id "
      + "AND (createdDate >= :dateDebut OR :dateDebut Is NULL)"
      + "AND (createdDate <= :dateFin OR :dateFin Is NULL)")
  Page<DemandeModem> findAllOwner(Pageable pageable, Long id, Date dateDebut, Date dateFin);

  @Query(value = "SELECT "
      + "SUM(CASE WHEN CONVERT(date, d.createdDate) = CONVERT(date, :today) THEN CAST(d.quantiter AS int) ELSE 0 END) AS todaySum, "
      + "SUM(CASE WHEN CONVERT(date, d.createdDate) BETWEEN CONVERT(date, :startOfMonth) AND CONVERT(date, :endOfMonth) THEN CAST(d.quantiter AS int) ELSE 0 END) AS monthSum "
      + "FROM DemandeModem d WHERE (user.userid IN (SELECT userid FROM User u WHERE affectedTo = :idDist OR :idDist IS NULL) OR (user.userid = :idConnected OR :idConnected IS NULL))")
  List<Map<String, Object>> getTodayAndMonthSumOfDemandModem(@Param("today") Date today,
      @Param("startOfMonth") Date startOfMonth, @Param("endOfMonth") Date endOfMonth,
      @Param("idDist") Long idDist, @Param("idConnected") Long idConnected);

  @Query(value = "SELECT "
      + "SUM(CASE WHEN CONVERT(date, d.createdDate) = CONVERT(date, :today) THEN CAST(d.quantiter AS int) ELSE 0 END) AS todaySum, "
      + "SUM(CASE WHEN CONVERT(date, d.createdDate) BETWEEN CONVERT(date, :startOfMonth) AND CONVERT(date, :endOfMonth) THEN CAST(d.quantiter AS int) ELSE 0 END) AS monthSum "
      + "FROM DemandeModem d WHERE user.userid= :iduser OR :iduser IS NULL")
  List<Map<String, Object>> getTodayAndMonthSumOfDemandModemDistPosRev(@Param("today") Date today,
      @Param("startOfMonth") Date startOfMonth, @Param("endOfMonth") Date endOfMonth,
      @Param("iduser") Long iduser);
  // for commercial app

  @Query("SELECT d FROM DemandeModem d WHERE (user.userid IN (SELECT userid FROM User u WHERE affectedTo = :id) OR (user.userid = :idConnected OR :idConnected IS NULL))"
      + "AND (user.typeUser = :type OR :type IS NULL)"
      + "AND (user.userid = :userId OR :userId Is NULL)"
      + "AND (d.idDemandeModem = :idDemandeModem OR :idDemandeModem Is NULL)"
      + "AND (createdDate >= :dateDebut OR :dateDebut Is NULL)"
      + "AND (createdDate <= :dateFin OR :dateFin Is NULL) order by createdDate desc")
  List<DemandeModem> findAllAreaDist(Long id, Long userId, Date dateDebut, Date dateFin,
      String type, Long idConnected, Long idDemandeModem);



}
