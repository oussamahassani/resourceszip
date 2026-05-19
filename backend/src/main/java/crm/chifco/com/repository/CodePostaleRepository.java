
package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.PostalCode;

public interface CodePostaleRepository extends JpaRepository<PostalCode, Long> {


  List<PostalCode> findPostalCodeByVille_VilleId(Long id);

  PostalCode findByAbreviation(String abreviation);

}
