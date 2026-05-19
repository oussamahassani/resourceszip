package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.DetailsCommissionDemande;

public interface DetailsCommissionDemandeRepository
    extends JpaRepository<DetailsCommissionDemande, Long> {

  List<DetailsCommissionDemande> findAllByCommissionId(Long CommissionId);

}
