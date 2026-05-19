package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import crm.chifco.com.model.Historique;

public interface HistoriqueRepository extends JpaRepository<Historique, Long> {

  @Query("SELECT h FROM Historique h where h.user.userid = :x")
  public List<Historique> historiqueByUser(@Param("x") Long client);

  @Query("SELECT h FROM Historique h where h.user.affectedTo = :x")
  public List<Historique> historiqueByIdConnected(@Param("x") Long idconnected);

}
