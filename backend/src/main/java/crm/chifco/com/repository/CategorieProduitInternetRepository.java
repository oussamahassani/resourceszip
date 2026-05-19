package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.CategorieProduitInternet;

public interface CategorieProduitInternetRepository
    extends JpaRepository<CategorieProduitInternet, Long> {
  CategorieProduitInternet findCategorieProduitInternetByCategorieProduitInternetCode(String code);

  CategorieProduitInternet findCategorieProduitInternetByCategorieProduitInternetId(Long id);

  List<CategorieProduitInternet> findByCategorieProduitInternetNomNot(String categoryName);
}
