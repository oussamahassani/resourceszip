package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.Gouvernorat;

public interface GouvernoratRepository extends JpaRepository<Gouvernorat, Long> {
  Gouvernorat findByGouvernoratName(String villename);

  Gouvernorat findGouverneratByGouvernoratId(Long villeid);

  Gouvernorat findByAbreviation(String abreviation);
}
