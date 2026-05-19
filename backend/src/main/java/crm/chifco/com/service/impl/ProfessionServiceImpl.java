package crm.chifco.com.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Profession;
import crm.chifco.com.repository.ProfessionRepository;
import crm.chifco.com.service.ProfessionService;

@Service("professionService")
public class ProfessionServiceImpl implements ProfessionService {
  @Autowired
  ProfessionRepository professionRepository;

  @Override
  public Page<Profession> findPaginated(int pageNo, int pageSize) {
    Sort sort = Sort.by("name").ascending();
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.professionRepository.findAll(pageable);
  }

  public List<Profession> findlistProfession() {
    return this.professionRepository.findByisActive(true);
  }

  @Override
  public Profession findById(Long id) {
    // TODO Auto-generated method stub
    if (id == null) {
      return null;
    }
    return professionRepository.findById(id).orElse(null);
  }
}
