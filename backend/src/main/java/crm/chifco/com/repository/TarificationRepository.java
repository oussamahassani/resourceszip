package crm.chifco.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.Tarification;

public interface TarificationRepository extends JpaRepository<Tarification, Long> {
  Tarification getTarificationBypackIdAndCategoryClient(Long packid, Long categoryClient);

  Tarification getTarificationByProduitIdAndCategoryClient(Long idProduit, Long categoryClient);

  Tarification getTarificationByTarificationId(Long tarificationId);

  Tarification findByProduitId(Long produitId);

List<Tarification> getTarificationByProduitIdIn(List<Long> listIds);
}
