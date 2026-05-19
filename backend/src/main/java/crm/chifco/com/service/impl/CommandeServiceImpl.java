package crm.chifco.com.service.impl;

import crm.chifco.com.model.Commande;
import crm.chifco.com.repository.CommandeRepository;
import crm.chifco.com.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("commandeService")
public class CommandeServiceImpl implements CommandeService {
  @Autowired
  CommandeRepository commandeRepository;

  @Override
  public Page<Commande> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.commandeRepository.findAll(pageable);
  }
}
