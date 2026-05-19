package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import crm.chifco.com.model.ClicToPay;

public interface ClickToPayRepository extends JpaRepository<ClicToPay, Long> {

  ClicToPay findAbonnementByOrderId(String orderId);
  
  List<ClicToPay> findListByIsPassedAndProvider(Boolean status , String provider);

  List<ClicToPay> findListByIsPassed(Boolean status);
  
  
  @Query(value = "SELECT * FROM clic_to_pay  cp where  created_date >= CAST(GETDATE() AS DATE) "
  		+ "AND created_date < DATEADD(DAY, 1, CAST(GETDATE() AS DATE)) and provider='paymee' and is_passed= 'false'", nativeQuery = true)
  List<ClicToPay> findListByIsNotPassed();
}
