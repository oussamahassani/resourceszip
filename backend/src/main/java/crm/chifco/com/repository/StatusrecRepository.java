package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.Statusrec;

public interface StatusrecRepository extends JpaRepository<Statusrec, Long> {
  Statusrec findByNomStatut(String nomStatut);

  Statusrec findByDesignation(String designation);
}
