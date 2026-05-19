package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import crm.chifco.com.model.DemandeCommissionGroup;

@Repository
public interface DemandeCommissionGroupRepository extends JpaRepository<DemandeCommissionGroup, Long> {

  DemandeCommissionGroup findByRefGroup(String refGroup);

  List<DemandeCommissionGroup> findByCreatedByUserId(Long userId);

  List<DemandeCommissionGroup> findByStatut(String statut);

  Page<DemandeCommissionGroup> findByCreatedByUserIdOrderByCreatedDateDesc(Pageable pageable, Long userId);

  @Query("SELECT g FROM DemandeCommissionGroup g WHERE g.createdByUserId = :userId AND (g.statut = :statut OR :statut IS NULL)")
  Page<DemandeCommissionGroup> findByUserIdAndStatut(Pageable pageable, Long userId, String statut);

  @Query("SELECT g FROM DemandeCommissionGroup g WHERE g.statut = 'AWAINTING_INVOICING'")
  List<DemandeCommissionGroup> findAllAwaitingValidation();

  @Query("SELECT COUNT(g) FROM DemandeCommissionGroup g WHERE g.createdByUserId = :userId AND g.statut = :statut")
  Long countByUserIdAndStatut(Long userId, String statut);
}