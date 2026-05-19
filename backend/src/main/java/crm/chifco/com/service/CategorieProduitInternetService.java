package crm.chifco.com.service;

import java.util.List;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.CategorieProduitInternet;

public interface CategorieProduitInternetService {

  Page<CategorieProduitInternet> findPaginated(int pageNo, int pageSize);

  List<CategorieProduitInternet> findAllCategorie();

}
