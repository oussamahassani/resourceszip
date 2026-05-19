package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.model.OffreCommissionPromo;

public interface OffreCommissionPromoRepository extends JpaRepository<OffreCommissionPromo, Long> {

  Page<OffreCommissionPromo> findAll(Pageable pageable);

  List<OffreCommissionPromo> findAllByDateDebutAndDateFinAndIsActive(int dateDebut, int DateFin,
      Boolean isActive);

  OffreCommissionPromo findAllByNomCommisionPromo(String nomCommisionPromo);

  OffreCommissionPromo findAllByIdAndIsActive(Long IdCommisionPromo, Boolean isActive);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE offre_commission_promo SET is_active = CASE WHEN is_active = 0 THEN 1 ELSE 0 END, modify_by_userid = :userId WHERE id = :idOffre",
      nativeQuery = true)
  void changementEtat(@Param("idOffre") Long idOffre, @Param("userId") Long userId);

  List<OffreCommissionPromo> findAllByIsActive(boolean isActive);

  OffreCommissionPromo getOffreCommissionPromoById(Long idOffrePromo);


  @Query(
      value = "select * from offre_commission_promo where (( YEAR(date_debut) = :annee  and MONTH(date_debut) = :numMois)   or ( YEAR(date_fin) = :annee  and MONTH(date_fin) = :numMois)) and is_active = 'true' ",
      nativeQuery = true)
  List<OffreCommissionPromo> findCommisionPromoInMonth(Integer numMois, Integer annee);

}
