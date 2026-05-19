package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.OtpSms;

public interface OtpRepository extends JpaRepository<OtpSms, Long> {

  @Query(value ="SELECT TOP  1 * FROM Otp_sms otp where otp.phone_number = :number ORDER BY otp.otp_id DESC ",nativeQuery = true)
  OtpSms findFirstOtpSmsByPhoneNumberDesc(String number);


}
