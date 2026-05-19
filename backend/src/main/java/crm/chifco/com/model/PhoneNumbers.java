package crm.chifco.com.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "PhoneNumbersNotification")
@EntityListeners(AuditingEntityListener.class)
public class PhoneNumbers implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long phoneNumbersId;

  @Column(name = "phoneNumbers")
  private String phoneNumbers;

  /**
   * @return the phoneNumbersId
   */
  public Long getPhoneNumbersId() {
    return phoneNumbersId;
  }

  /**
   * @param phoneNumbersId the phoneNumbersId to set
   */
  public void setPhoneNumbersId(Long phoneNumbersId) {
    this.phoneNumbersId = phoneNumbersId;
  }

  /**
   * @return the phoneNumbers
   */
  public String getPhoneNumbers() {
    return phoneNumbers;
  }

  /**
   * @param phoneNumbers the phoneNumbers to set
   */
  public void setPhoneNumbers(String phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

}
