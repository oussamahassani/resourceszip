package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.DetailsRetardCommissionPremiereFacture;

public interface DetailsRetardCommissionPremiereFactureRepository
    extends JpaRepository<DetailsRetardCommissionPremiereFacture, Long> {

  List<DetailsRetardCommissionPremiereFacture> findAllByCommissionId(Long CommissionId);

}
