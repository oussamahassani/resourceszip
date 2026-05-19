package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.model.OffreCommission;

public interface OffreCommissionRepository extends JpaRepository<OffreCommission, Long> {

  Page<OffreCommission> findAll(Pageable pageable);

  List<OffreCommission> findAllByAnneeAndMoisAndIsActive(int Annee, int mois, Boolean isActive);

  OffreCommission findAllByAnneeAndMoisAndDebitAndPalierMinAndPalierMaxAndTypeAndIsActive(int Annee,
      int mois, int debit, Integer PalierMin, Integer PalierMax, String type, Boolean isActive);


  @Modifying
  @Transactional
  @Query(
      value = "UPDATE offre_commission SET is_active = CASE WHEN is_active = 0 THEN 1 ELSE 0 END, modify_by_userid = :userId WHERE id = :idOffre",
      nativeQuery = true)
  void changementEtat(@Param("idOffre") Long idOffre, @Param("userId") Long userId);



}
