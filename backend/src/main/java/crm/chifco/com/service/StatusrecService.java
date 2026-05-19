package crm.chifco.com.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Statusrec;
import crm.chifco.com.repository.StatusrecRepository;

@Service
public class StatusrecService {
  @Autowired
  private StatusrecRepository statusrecRepository;

  public List<Statusrec> getAllStatusrec() {
    return statusrecRepository.findAll();
  }

  public Statusrec getStatusrecByName(String nomStatut) {
    return statusrecRepository.findByNomStatut(nomStatut);
  }

  public Statusrec getStatusrecByDesignation(String designation) {
    return statusrecRepository.findByDesignation(designation);
  }

  public Statusrec findById(Long id) {
    return statusrecRepository.findById(id).get();
  }

  public Page<Statusrec> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo, pageSize);
    return this.statusrecRepository.findAll(pageable);
  }
}
