package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import crm.chifco.com.model.EmailAdresse;

public interface EmailAdresseNotificationRepository extends JpaRepository<EmailAdresse, Long> {

  @Query(
      value = "select * from email_adresse_notification where notificaionid = :notificationConfigId and email_adresse = :emailadress ",
      nativeQuery = true)
  EmailAdresse getByNotificaionidAndEmailAdresse(Long notificationConfigId, String emailadress);

  @Modifying
  @Query(
      value = "DELETE email_adresse_notification where notificaionid = :configid and email_adresse = :email ",
      nativeQuery = true)
  void deletebyconfigIdandEmail(Long configid, String email);

}
