package crm.chifco.com.service;

import java.util.List;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.Profession;

public interface ProfessionService {
  Page<Profession> findPaginated(int pageNo, int pageSize);

  List<Profession> findlistProfession();

  Profession findById(Long id);

}
