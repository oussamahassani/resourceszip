package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.Servicetype;

public interface ServicetypeRepository extends JpaRepository<Servicetype, Long> {
  Servicetype findByCategorytype(String categoryType);

  List<Servicetype> findByIsPrivateFalse();
}
