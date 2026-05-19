package crm.chifco.com.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.model.AvanceCommissionAcquisition;

@Repository
public interface AvanceCommissionAcquisitionRepository
    extends JpaRepository<AvanceCommissionAcquisition, Long> {


  @Modifying
  @Transactional
  @Query(value = "UPDATE avance_commission_acquisition SET statut = :statut WHERE id = :id",
      nativeQuery = true)
  void updateStatut(Long id, String statut);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM  avance_commission_acquisition  WHERE id = :id and statut = :status",
      nativeQuery = true)
  void DeleteAvanceCommision(Long id, String status);

  AvanceCommissionAcquisition findByBordereauAndStatut(Long bordereauId, String string);

  List<AvanceCommissionAcquisition> findByStatutAndRevendeur_useridAndBrdValidatedAndDateVersementBrdBetween(
      String string, Long revendeurId, Boolean isBrdAccepted, Date startDate, Date endDate);

  List<AvanceCommissionAcquisition> findByStatutAndRevendeur_userid(String commisionStatut,
      Long revendeurId);

  AvanceCommissionAcquisition findAvanceCommissionByBordereauAndStatutAndRevendeur_userid(
      Long bordereauId, String string, Long revendeurId);

  @Query(value = "select * from avance_commission_acquisition  f    "
      + "where f.statut  = :avancePayed "
      + "and f.date_versement_brd is not null and f.revendeur_userid = :id "
      + "and  f.date_versement_brd >= CAST(:datedebut AS datetime2) "
      + "and f.date_versement_brd <= CAST(:datefin AS datetime2)", nativeQuery = true)
  List<AvanceCommissionAcquisition> findByMonthAndStatusAndUser(Long id, String datedebut,
      String datefin, String avancePayed);

  List<AvanceCommissionAcquisition> findByStatutAndRevendeur_useridAndIdBrdDemande(
      String avanceInstence, Long userid, Long id);


}
