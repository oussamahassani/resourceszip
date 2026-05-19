package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import crm.chifco.com.model.EntryDemandeAbonnement;

public interface EntryDemandeAbonnementRepository
    extends JpaRepository<EntryDemandeAbonnement, Long> {

  @Query(
      value = "select  *   from  entry_demande_abonnement WHERE entry_demande_abonnement.demande_id = :demandeAbonnementId",
      nativeQuery = true)
  List<EntryDemandeAbonnement> getListeDemandeAbonnementByIdDemandeAbonnement(
      Long demandeAbonnementId);

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE entry_demande_abonnement  SET entry_demande_abonnement.demande_id = null WHERE entry_demande_abonnement.demande_id = :demandeAbonnementId",
      nativeQuery = true)
  void updateListeIdDemandeAbonnement(Long demandeAbonnementId);

}
