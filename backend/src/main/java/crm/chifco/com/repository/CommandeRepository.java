package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.Commande;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

  Commande findFirstByabonnement(Abonnement Abonnement);

}
