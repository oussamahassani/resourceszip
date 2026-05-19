package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.EntryAbonnement;

public interface EntryAbonnementRepository extends JpaRepository<EntryAbonnement, Long> {

  List<EntryAbonnement> getEntryAbonnementByAbonnement(Abonnement abonnement);

}
