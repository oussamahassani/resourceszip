package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.model.Statut;

public interface StatutRepository extends JpaRepository<Statut, Long> {
  Statut findStatutByNomStatut(String nom);

  Statut findStatutByStatutId(Long id);

  @Query("SELECT s FROM Statut s WHERE s.nomStatut IN :statuses")
  List<Statut> findByNomStatutIn(@Param("statuses") List<String> statuses);
}
