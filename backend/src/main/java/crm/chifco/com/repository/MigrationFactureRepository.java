package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.MigrationFacture;

public interface MigrationFactureRepository extends JpaRepository<MigrationFacture, Long> {


  MigrationFacture findFirstByClientidAndTypeCalculeOrderByFactureMigrationIdDesc(Long clientid,
      String typeDemande);

}
