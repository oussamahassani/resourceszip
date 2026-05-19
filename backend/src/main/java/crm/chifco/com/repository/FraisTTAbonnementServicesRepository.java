package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.FraisTTAbonnementServices;
import crm.chifco.com.templateclasse.FraisTTData;

public interface FraisTTAbonnementServicesRepository
    extends JpaRepository<FraisTTAbonnementServices, Long> {

  @Query(
      value = "select * from  fraisttabonnement_services where SUBSTRING(recheche_date, 1, 4) = YEAR(:formattedCurrentYearMonth)  and SUBSTRING(recheche_date, 6, 2) = MONTH(:formattedCurrentYearMonth) and user_name = :userName  ORDER BY created_date  DESC ",
      nativeQuery = true)
  List<FraisTTAbonnementServices> getAllDataByUserNameIfExist(String formattedCurrentYearMonth,
      String userName);

  @Query(
      value = "select * from  fraisttabonnement_services where SUBSTRING(recheche_date, 1, 4) = YEAR(:formattedCurrentYearMonth)  and SUBSTRING(recheche_date, 6, 2) = MONTH(:formattedCurrentYearMonth)  ORDER BY created_date  DESC ",
      nativeQuery = true)
  List<FraisTTData> getAllDataBycretedDate(String formattedCurrentYearMonth);

}
