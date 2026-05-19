package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.TypeVisite;

public interface TypeVisiteRepository extends JpaRepository<TypeVisite, Long> {
  TypeVisite findByNomType(String nomType);

  TypeVisite findByDesignation(String designation);
}
