package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.Engagement;

public interface EngagementRepository extends JpaRepository<Engagement, Long> {
  List<Engagement> findByNomEngagementContainingIgnoreCase(String nomEngagement);

  Engagement findByNomEngagement(String nomEngagement);

}
