package crm.chifco.com.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Statut;
import crm.chifco.com.repository.StatutRepository;
import crm.chifco.com.service.StatutService;

@Service("statutService")
public class StatutServiceImpl implements StatutService {

  @Autowired
  private StatutRepository statutRepository;

  @Override
  public Page<Statut> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo, pageSize);
    return this.statutRepository.findAll(pageable);
  }

  @Override
  public Statut findStatutByNomstatut(String statut) {
    // TODO Auto-generated method stub
    return this.statutRepository.findStatutByNomStatut(statut);
  }
}
