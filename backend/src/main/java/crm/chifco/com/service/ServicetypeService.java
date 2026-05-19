package crm.chifco.com.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Servicetype;
import crm.chifco.com.repository.ServicetypeRepository;

@Service
public class ServicetypeService {
  @Autowired
  private ServicetypeRepository servicetypeRepository;

  public List<Servicetype> getAllServicetypes() {
    return servicetypeRepository.findAll();
  }

  public List<Servicetype> getAllServicetypesPublics() {
    return servicetypeRepository.findByIsPrivateFalse();
  }

  public Servicetype getServicetypeByCategory(String categoryType) {
    return servicetypeRepository.findByCategorytype(categoryType);
  }

  public Page<Servicetype> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo, pageSize);
    return this.servicetypeRepository.findAll(pageable);
  }

  public void save(Servicetype servicetype) {
    this.servicetypeRepository.save(servicetype);

  }

  public Servicetype findbyServicetypeId(Long servicetypeId) {
    return this.servicetypeRepository.findById(servicetypeId).get();
  }
}
