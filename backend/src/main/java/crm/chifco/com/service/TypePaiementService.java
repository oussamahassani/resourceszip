package crm.chifco.com.service;

import crm.chifco.com.model.Typepaiement;
import crm.chifco.com.repository.TypepaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface TypePaiementService {
  public Optional<Typepaiement> gettypepaiement(Long id);

  public Page<Typepaiement> getalltypepaiementpaginated(int pageNo, int pageSize);

  public List<Typepaiement> getalltypepaiements();

  public void savetypepaiement(Typepaiement typepaiement);

  public Typepaiement gettypepaiementbyref(String ref);
}
