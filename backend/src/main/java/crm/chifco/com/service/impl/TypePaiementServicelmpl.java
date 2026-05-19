/*
 * created by hatem ghozzi on 11 11 2022
 */

package crm.chifco.com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import crm.chifco.com.model.Typepaiement;
import crm.chifco.com.repository.TypepaiementRepository;
import crm.chifco.com.service.TypePaiementService;

@Service("TypePaiementService")
public class TypePaiementServicelmpl implements TypePaiementService {

  @Autowired
  TypepaiementRepository typepaiementRepository;

  @Override
  public Optional<Typepaiement> gettypepaiement(Long id) {
    return typepaiementRepository.findById(id);
  }

  @Override
  public Page<Typepaiement> getalltypepaiementpaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.typepaiementRepository.findAll(pageable);
  }

  @Override
  public List<Typepaiement> getalltypepaiements() {
    return typepaiementRepository.findAll();
  }

  @Override
  public void savetypepaiement(Typepaiement typepaiement) {
    typepaiementRepository.save(typepaiement);
  }

  @Override
  public Typepaiement gettypepaiementbyref(String ref) {
    return typepaiementRepository.findTypepaiementByreferenceTypePaiement(ref);
  }
}
