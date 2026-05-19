package crm.chifco.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import crm.chifco.com.model.DetailsCommissionFactureMiseEnService;

public interface DetailsCommissionFactureMiseEnServiceRepository extends JpaRepository<DetailsCommissionFactureMiseEnService, Long>  {

	 List<DetailsCommissionFactureMiseEnService> findAllByCommissionId(Long id);

}
