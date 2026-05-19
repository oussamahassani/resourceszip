package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.DetailsCommissionFacture;

public interface DetailsCommissionFactureRepository
    extends JpaRepository<DetailsCommissionFacture, Long> {

  List<DetailsCommissionFacture> findAllByCommissionId(Long CommissionId);
}
