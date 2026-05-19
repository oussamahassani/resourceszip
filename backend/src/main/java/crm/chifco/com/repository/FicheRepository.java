package crm.chifco.com.repository;


import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import crm.chifco.com.model.FicheStock;
import crm.chifco.com.templateclasse.ModemAffectationFiches;


@Repository
public interface FicheRepository extends JpaRepository<FicheStock, Long> {

  // recuperer une fiche de stock par sa reference (champs unique )
  @Query("select f from FicheStock f where f.ref_fiche=:x or f.IdFiche like :x")
  public FicheStock getFicheByRef(@Param("x") String ref);

  @Query(
      value = "SELECT f.id_fiche AS id_fiche, f.created_date AS createdDate, f.ref_fiche AS ref_fiche, u1.first_name as affectedToFirstName, "
          + "u1.last_name as affectedToLastName, u1.nom_commercial AS nom_commercial, u1.code_user as affectedToCode , "
          + "f.affectequantite as affectequantite, u2.first_name as affectedByFirstName, u2.last_name AS affectedByLastName, "
          + "u2.code_user AS affectedByCode FROM fiches_stock f "
          + "INNER JOIN users u1 ON f.affecteid = u1.userid "
          + "INNER JOIN users u2 ON f.affectedbyuser = u2.userid  and f.affectedbyuser= :idUser"
          + " where (f.created_date >= CAST(:datedebut AS datetime2) or :datedebut is null )"
          + "and (f.created_date <= CAST(:datefin AS datetime2) or :datefin is null )"
          + "and (f.affecteid = :affectA or :affectA IS NULL)",
      countQuery = "SELECT count(f.id_fiche) FROM fiches_stock f "
          + "INNER JOIN users u1 ON f.affecteid = u1.userid "
          + "INNER JOIN users u2 ON f.affectedbyuser = u2.userid  and f.affectedbyuser= :idUser"
          + " where (f.created_date >= CAST(:datedebut AS datetime2) or :datedebut is null )"
          + "and (f.created_date <= CAST(:datefin AS datetime2) or :datefin is null )"
          + "and (f.affecteid = :affectA or :affectA IS NULL)",
      nativeQuery = true)
  Page<ModemAffectationFiches> getFicheByUserID(Pageable pageable, Long idUser, String datedebut,
      String datefin, Long affectA);

  @Query(
      value = "SELECT f.id_fiche AS id_fiche, CONVERT(date, f.created_date, 105) AS createdDate, f.ref_fiche AS ref_fiche, u1.first_name AS affectedToFirstName, "
          + "u1.last_name AS affectedToLastName, u1.nom_commercial AS nom_commercial, u1.code_user AS affectedToCode , "
          + "f.affectequantite AS affectequantite, u2.first_name AS affectedByFirstName, u2.last_name AS affectedByLastName, "
          + "u2.code_user AS affectedByCode FROM fiches_stock f "
          + "INNER JOIN users u1 ON f.affecteid = u1.userid "
          + "INNER JOIN users u2 ON f.affectedbyuser = u2.userid"
          + " where (f.created_date >= CAST(:datedebut AS datetime2) or :datedebut is null )"
          + "and (f.created_date <= CAST(:datefin AS datetime2) or :datefin is null )"
          + "and (f.affectedbyuser = :distId or :distId IS NULL)"
          + "and (f.affecteid = :affectA or :affectA IS NULL)"
          + "and(f.affectedbyuser = :myAffect OR :myAffect IS NULL)",
      countQuery = "SELECT count(*) FROM fiches_stock f"
          + " where (f.created_date >= CAST(:datedebut AS datetime2) or :datedebut is null )"
          + "and (f.created_date <= CAST(:datefin AS datetime2) or :datefin is null )"
          + "and (f.affectedbyuser = :distId or :distId IS NULL)"
          + "and (f.affecteid = :affectA or :affectA IS NULL)"
          + "and(f.affectedbyuser = :myAffect OR :myAffect IS NULL)",
      nativeQuery = true)
  Page<ModemAffectationFiches> findAllFiches(Pageable pageable, String datedebut, String datefin,
      Long distId, Long affectA, Long myAffect);


  @Query(value = "SELECT "
      + "SUM(CASE WHEN CONVERT(date, f.createdDate) = CONVERT(date, :today) THEN f.affectequantite ELSE 0 END) AS todaySum, "
      + "SUM(CASE WHEN CONVERT(date, f.createdDate) BETWEEN CONVERT(date, :startOfMonth) AND CONVERT(date, :endOfMonth) THEN f.affectequantite ELSE 0 END) AS monthSum "
      + "FROM FicheStock f where affectedBYuser=:userId")
  List<Map<String, Object>> getTodayAndMonthSumModemAffectationByUser(@Param("today") Date today,
      @Param("startOfMonth") Date startOfMonth, @Param("endOfMonth") Date endOfMonth,
      @Param("userId") Long userId);

  @Query(value = "SELECT "
      + "SUM(CASE WHEN CONVERT(date, f.createdDate) = CONVERT(date, :today) THEN f.affectequantite ELSE 0 END) AS todaySum, "
      + "SUM(CASE WHEN CONVERT(date, f.createdDate) BETWEEN CONVERT(date, :startOfMonth) AND CONVERT(date, :endOfMonth) THEN f.affectequantite ELSE 0 END) AS monthSum "
      + "FROM FicheStock f where affecteid=:userId")
  List<Map<String, Object>> getTodayAndMonthSumModemRecievedByUser(@Param("today") Date today,
      @Param("startOfMonth") Date startOfMonth, @Param("endOfMonth") Date endOfMonth,
      @Param("userId") Long userId);

  // repos for commercial api
  @Query(
      value = "SELECT f.id_fiche AS id_fiche, f.created_date AS createdDate, f.ref_fiche AS ref_fiche, u1.first_name as affectedToFirstName, "
          + "u1.last_name as affectedToLastName, u1.nom_commercial AS nom_commercial, u1.code_user as affectedToCode , "
          + "f.affectequantite as affectequantite, u2.first_name as affectedByFirstName, u2.last_name AS affectedByLastName, "
          + "u2.code_user AS affectedByCode FROM fiches_stock f "
          + "INNER JOIN users u1 ON f.affecteid = u1.userid "
          + "INNER JOIN users u2 ON f.affectedbyuser = u2.userid "
          + " where (f.affectedbyuser= :idUser or :idUser is null) and (f.created_date >= CAST(:datedebut AS datetime2) or :datedebut is null )"
          + "and (f.created_date <= CAST(:datefin AS datetime2) or :datefin is null )"
          + "and (f.affecteid = :affectA or :affectA IS NULL) order by f.created_date desc",
      countQuery = "SELECT COUNT(*) " + "FROM fiches_stock f "
          + "INNER JOIN users u1 ON f.affecteid = u1.userid "
          + "INNER JOIN users u2 ON f.affectedbyuser = u2.userid "
          + "WHERE (f.affectedbyuser = :idUser or :idUser is null) "
          + "AND (:datedebut IS NULL OR f.created_date >= CAST(:datedebut AS datetime2)) "
          + "AND (:datefin IS NULL OR f.created_date <= CAST(:datefin AS datetime2)) "
          + "AND (:affectA IS NULL OR f.affecteid = :affectA)",
      nativeQuery = true)
  Page<ModemAffectationFiches> getFicheByUserIDComm(Long idUser, String datedebut, String datefin,
      Long affectA, Pageable page);

}
