package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import crm.chifco.com.model.PhoneNumbers;

public interface PhoneNumbersNotificationRepository extends JpaRepository<PhoneNumbers, Long> {

  @Query(
      value = "select * from phone_numbers_notification where notificaionid = :idconfig and  phone_numbers = :phone",
      nativeQuery = true)
  PhoneNumbers findByNotificaionidAndPhoneNumbers(Long idconfig, String phone);

  @Modifying
  @Query(
      value = "DELETE from phone_numbers_notification where notificaionid = :configid and  phone_numbers = :phone",
      nativeQuery = true)
  void deletebyconfigIdandPhone(Long configid, String phone);

}
