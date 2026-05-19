package crm.chifco.com.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Ville;
import crm.chifco.com.repository.VilleRepository;
import crm.chifco.com.service.VillesService;

@Service("VillesService")
public class VillesServiceImpl implements VillesService {
  @Autowired
  VilleRepository villeRepository;

  @Override
  public Page<Ville> findPaginated(int pageNo, int pageSize) {
    Sort sort = Sort.by("gouvernerat.gouvernoratName").ascending();
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.villeRepository.findAll(pageable);
  }

  public List<Ville> findAllByIdGrouvernerat(Long idville) {
    return this.villeRepository.findGouvernoratsByGouvernerat_GouvernoratId(idville);
  }

  @Override
  public Ville findbyVilleId(Long id) {
    // TODO Auto-generated method stub
    return this.villeRepository.findById(id).get();
  }
}
