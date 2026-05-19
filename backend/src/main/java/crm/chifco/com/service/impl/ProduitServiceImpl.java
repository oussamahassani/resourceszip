package crm.chifco.com.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Produit;
import crm.chifco.com.repository.ProduitRepository;
import crm.chifco.com.service.ProduitService;

@Service("produitService")
public class ProduitServiceImpl implements ProduitService {
  @Autowired
  ProduitRepository produitRepository;

  @Override
  public Page<Produit> findPaginated(int pageNo, int pageSize) {
    Sort sort = Sort.by("produitNom").ascending();
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.produitRepository.findAll(pageable);
  }

  @Override
  public Produit findProduitbyCode(String code) {

    return this.produitRepository.findProduitByProduitCode(code);
  }

  @Override
  public List<Produit> findAllProduit() {
    // TODO Auto-generated method stub
    return this.produitRepository.findAll();
  }

  @Override
  public List<Produit> findAllActiveProduit() {
    // TODO Auto-generated method stub
    return this.produitRepository.findByIsActive(true);
  }

  public void activationProduit(Long id, Boolean isActive) {
    if (isActive == false) {
      this.produitRepository.activerProduit(id);
    } else {
      this.produitRepository.desactiverProduit(id);
    }
  }
  @Override
  public List<Produit> findAllProduitIsExtratAndActive(boolean IsEXtract, boolean isActive) {
  	// TODO Auto-generated method stub
  	return this.produitRepository.findDistinctByIsExtraAndIsActive(IsEXtract, isActive);
  }

}
