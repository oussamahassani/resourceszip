package crm.chifco.com.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Engagement;
import crm.chifco.com.repository.EngagementRepository;

@Service
public class EngagementService {

  @Autowired
  private EngagementRepository engagementRepository;

  public List<Engagement> getAllEngagements() {
    return engagementRepository.findAll();
  }

  public Optional<Engagement> getEngagementById(Long id) {
    return engagementRepository.findById(id);
  }

  public Engagement createEngagement(Engagement engagement) {
    return engagementRepository.save(engagement);
  }

  public Engagement updateEngagement(Long id, Engagement newData) {
    return engagementRepository.findById(id).map(e -> {
      e.setNomEngagement(newData.getNomEngagement());
      e.setNombre(newData.getNombre());
      return engagementRepository.save(e);
    }).orElse(null);
  }

  public void deleteEngagement(Long id) {
    engagementRepository.deleteById(id);
  }

  public List<Engagement> searchByNomEngagement(String nom) {
    return engagementRepository.findByNomEngagementContainingIgnoreCase(nom);
  }

  public Page<Engagement> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo, pageSize);
    return engagementRepository.findAll(pageable);
  }

  public Engagement searchByNom(String nom) {
    return engagementRepository.findByNomEngagement(nom);
  }
}
