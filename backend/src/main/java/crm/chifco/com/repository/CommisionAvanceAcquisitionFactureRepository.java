package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import crm.chifco.com.model.CommisionAvanceAcquisitionFacture;

@Repository
public interface CommisionAvanceAcquisitionFactureRepository
    extends JpaRepository<CommisionAvanceAcquisitionFacture, Long> {

}
