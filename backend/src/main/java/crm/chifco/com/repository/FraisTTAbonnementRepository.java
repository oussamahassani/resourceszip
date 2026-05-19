package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.FraisTTAbonnement;
import crm.chifco.com.templateclasse.FraisTTData;

public interface FraisTTAbonnementRepository extends JpaRepository<FraisTTAbonnement, Long> {

  @Query(
      value = "select * from  fraisttabonnement where YEAR(date_connection) = YEAR(:formattedCurrentYearMonth)  and MONTH(date_connection) = MONTH(:formattedCurrentYearMonth)  ORDER BY created_date  DESC ",
      nativeQuery = true)
  List<FraisTTData> getAllDataBycretedDate(String formattedCurrentYearMonth);
}
