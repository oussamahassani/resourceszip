package crm.chifco.com.service;

import java.util.List;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.Produit;

public interface ProduitService {
  Page<Produit> findPaginated(int pageNo, int pageSize);

  Produit findProduitbyCode(String code);

  List<Produit> findAllProduit();

  public List<Produit> findAllActiveProduit();

  void activationProduit(Long id, Boolean isActive);
  
  List<Produit> findAllProduitIsExtratAndActive(boolean IsEXtract , boolean isActive);

}
