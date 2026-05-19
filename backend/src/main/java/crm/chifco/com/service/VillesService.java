package crm.chifco.com.service;

import java.util.List;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.Ville;

public interface VillesService {
  Page<Ville> findPaginated(int pageNo, int pageSize);

  List<Ville> findAllByIdGrouvernerat(Long idville);

  Ville findbyVilleId(Long id);
}
