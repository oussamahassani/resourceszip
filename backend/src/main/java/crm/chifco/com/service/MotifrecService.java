package crm.chifco.com.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Motifrec;
import crm.chifco.com.repository.MotifrecRepository;

@Service
public class MotifrecService {
  @Autowired
  private MotifrecRepository motifrecRepository;

  public List<Motifrec> getAllMotifrec() {
    return motifrecRepository.findAll();
  }

  /*
   * public List<Motifrec> getMotifrecByServicetype(Long serviceTypeId) { return
   * motifrecRepository.findByServicetypeId(serviceTypeId); }
   */

  public Page<Motifrec> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo, pageSize);
    return this.motifrecRepository.findAll(pageable);
  }

  public Motifrec getMotifByName(String nomMotif) {
    return this.motifrecRepository.findByNomMotif(nomMotif);
  }

  public Motifrec getMotifByNameByserviceByCategory(String nomMotif, Long servicetypeId,
      String category) {
    return this.motifrecRepository.findByNomMotifAndServiceTypeAndCategory(nomMotif, servicetypeId,
        category);
  }

  public void save(Motifrec motifrec) {
    this.motifrecRepository.save(motifrec);

  }

  public Motifrec findById(Long id) {
    // TODO Auto-generated method stub
    return this.motifrecRepository.findById(id).get();
  }

  public List<Motifrec> findMotifsByServiceType(Long serviceTypeId, String category) {

    return this.motifrecRepository.findMotifsByServiceType(serviceTypeId, category);
  }
}
