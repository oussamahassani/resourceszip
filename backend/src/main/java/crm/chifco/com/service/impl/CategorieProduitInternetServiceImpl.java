package crm.chifco.com.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.CategorieProduitInternet;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.service.CategorieProduitInternetService;

@Service("categorieproduitinternetService")
public class CategorieProduitInternetServiceImpl implements CategorieProduitInternetService {
  @Autowired
  CategorieProduitInternetRepository categorieProduitInternetRepository;

  @Override
  public Page<CategorieProduitInternet> findPaginated(int pageNo, int pageSize) {
    Sort sort = Sort.by("categorieProduitInternetNom").ascending();
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.categorieProduitInternetRepository.findAll(pageable);
  }

  @Override
  public List<CategorieProduitInternet> findAllCategorie() {
    // TODO Auto-generated method stub
    return this.categorieProduitInternetRepository.findAll();
  }
}
