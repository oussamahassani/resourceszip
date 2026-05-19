package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.Profession;

public interface ProfessionRepository extends JpaRepository<Profession, Long> {
  Profession findProfessionByName(String name);

  Profession findProfessionByProfessionId(Long id);

  List<Profession> findByisActive(boolean b);
}
