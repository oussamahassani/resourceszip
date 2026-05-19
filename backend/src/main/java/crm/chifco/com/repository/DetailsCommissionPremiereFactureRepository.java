package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.DetailsCommissionPremiereFacture;

public interface DetailsCommissionPremiereFactureRepository
    extends JpaRepository<DetailsCommissionPremiereFacture, Long> {

  List<DetailsCommissionPremiereFacture> findAllByCommissionId(Long CommissionId);

}
