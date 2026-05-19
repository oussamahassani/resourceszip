package crm.chifco.com.service;

import java.util.List;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.Gouvernorat;

public interface GouverneratsService {
  Page<Gouvernorat> findPaginated(int pageNo, int pageSize);

  List<Gouvernorat> findAllGouvernorat();

  Gouvernorat findByGouvernoratId(Long id);
}
