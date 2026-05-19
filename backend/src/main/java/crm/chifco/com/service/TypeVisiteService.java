package crm.chifco.com.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.TypeVisite;
import crm.chifco.com.repository.TypeVisiteRepository;

@Service
public class TypeVisiteService {
  @Autowired
  private TypeVisiteRepository typeVisiteRepository;

  public List<TypeVisite> getAllStatusrec() {
    return typeVisiteRepository.findAll();
  }

  public TypeVisite getStatusrecByName(String nomStatut) {
    return typeVisiteRepository.findByNomType(nomStatut);
  }

  public TypeVisite getStatusrecByDesignation(String designation) {
    return typeVisiteRepository.findByDesignation(designation);
  }

  public TypeVisite findById(Long id) {
    return typeVisiteRepository.findById(id).get();
  }

  public Page<TypeVisite> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo, pageSize);
    return this.typeVisiteRepository.findAll(pageable);
  }
}
