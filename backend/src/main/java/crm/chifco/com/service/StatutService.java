package crm.chifco.com.service;

import org.springframework.data.domain.Page;
import crm.chifco.com.model.Statut;

public interface StatutService {
  Page<Statut> findPaginated(int pageNo, int pageSize);



  Statut findStatutByNomstatut(String valid);



}
