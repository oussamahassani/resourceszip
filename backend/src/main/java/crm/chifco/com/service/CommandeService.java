package crm.chifco.com.service;


import crm.chifco.com.model.Commande;
import org.springframework.data.domain.Page;

public interface CommandeService {
  Page<Commande> findPaginated(int pageNo, int pageSize);
}
