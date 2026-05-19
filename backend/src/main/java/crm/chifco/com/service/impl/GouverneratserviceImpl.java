package crm.chifco.com.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.service.GouverneratsService;

@Service("GouverneratsService")
public class GouverneratserviceImpl implements GouverneratsService {
  @Autowired
  GouvernoratRepository gouverneratRepository;

  @Override
  public Page<Gouvernorat> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.gouverneratRepository.findAll(pageable);
  }

  public List<Gouvernorat> findAllGouvernorat() {
    return this.gouverneratRepository.findAll();
  }

  @Override
  public Gouvernorat findByGouvernoratId(Long id) {
    // TODO Auto-generated method stub
    return gouverneratRepository.findById(id).get();
  }
}
