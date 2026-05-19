package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.model.AntivirusKey;

public interface AntivirusKeyRepository extends JpaRepository<AntivirusKey, Long> {

  @Query("SELECT c FROM AntivirusKey c WHERE licenseKey IN :listClé")
  List<AntivirusKey> findAllByListClé(List<String> listClé);

  @Modifying
  @Transactional
  @Query(
      value = " UPDATE antivirus_key SET active = CASE "
          + " WHEN active = 1 THEN 0  WHEN active = 0 THEN 1  END  WHERE id = :id ",
      nativeQuery = true)
  void changeEtat(Long id);

  @Query(value = " SELECT TOP(1)* from antivirus_key "
      + " WHERE active = 1 AND abonnement_clientid IS NULL and type=:type", nativeQuery = true)
  AntivirusKey getFirstAntivirusKey(String type);

  @Query(
      value = " SELECT * FROM antivirus_key k WHERE k.abonnement_clientid = :abonnementId and k.type=:type",
      nativeQuery = true)
  AntivirusKey getKeyByClient(Long abonnementId, String type);

  @Query("SELECT a.firstName, a.lastName, a.referenceClient FROM Abonnement a")
  List<Object[]> findAllAbonnementInfo();

  @Query("SELECT k FROM AntivirusKey k WHERE abonnement.clientid = :id and k.type=:type")
  AntivirusKey findByAbonnementId(Long id, String type);

  @Query("SELECT k FROM AntivirusKey k LEFT JOIN k.abonnement a WHERE"
      + " (k.licenseKey LIKE CONCAT('%'+ :key + '%') OR :key IS NULL) "
      + " AND (k.active = :etat OR :etat IS NULL) " + " AND (k.type = :type OR :type IS NULL) "
      + " AND (k.dateAffectation >= :startDate OR :startDate IS NULL) "
      + " AND (k.dateAffectation <= :endDateendDate OR :endDateendDate IS NULL) "
      + " AND (k.createdDate >= :startCreatedDate OR :startCreatedDate IS NULL) "
      + " AND (k.createdDate <= :endCreatedDate OR :endCreatedDate IS NULL) "
      + " AND ((k.abonnement IS NOT NULL AND :statut = true) OR (k.abonnement IS NULL AND :statut = false) OR :statut IS NULL) "
      + " AND (a.referenceClient LIKE CONCAT('%'+ :referenceClient + '%') OR :referenceClient IS NULL)")
  Page<AntivirusKey> findAllFiltrer(Pageable pageable, String key, String referenceClient,
      Boolean etat, Boolean statut, Date startDate, Date endDateendDate, Date startCreatedDate,
      Date endCreatedDate, String type);

  @Query("SELECT k FROM AntivirusKey k LEFT JOIN k.abonnement a WHERE"
      + " (k.licenseKey LIKE CONCAT('%'+ :key + '%') OR :key IS NULL) "
      + " AND (k.type = :type OR :type IS NULL) " + " AND (k.active = :etat OR :etat IS NULL) "
      + " AND (k.dateAffectation >= :startDate OR :startDate IS NULL) "
      + " AND (k.dateAffectation <= :endDateendDate OR :endDateendDate IS NULL) "
      + " AND (k.createdDate >= :startCreatedDate OR :startCreatedDate IS NULL) "
      + " AND (k.createdDate <= :endCreatedDate OR :endCreatedDate IS NULL) "
      + " AND ((k.abonnement IS NOT NULL AND :statut = true) OR (k.abonnement IS NULL AND :statut = false) OR :statut IS NULL) "
      + " AND (a.referenceClient LIKE CONCAT('%'+ :referenceClient + '%') OR :referenceClient IS NULL)")
  List<AntivirusKey> findAllExportExcel(String key, String referenceClient, Boolean etat,
      Boolean statut, Date startDate, Date endDateendDate, Date startCreatedDate,
      Date endCreatedDate, String type);

  @Query("SELECT DISTINCT a.type FROM AntivirusKey a WHERE a.type IS NOT NULL")
  List<String> findDistinctTypes();



}
