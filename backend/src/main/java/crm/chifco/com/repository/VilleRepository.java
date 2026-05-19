package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.Ville;

public interface VilleRepository extends JpaRepository<Ville, Long> {

  Ville findVilleByVilleId(Long villeId);

  List<Ville> findGouvernoratsByGouvernerat_GouvernoratId(Long villeid);

  Ville findByAbreviation(String abreviation);

}
