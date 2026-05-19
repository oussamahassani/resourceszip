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
@Table(name = "EmailAdresseNotification")
@EntityListeners(AuditingEntityListener.class)
public class EmailAdresse implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long emailAdresseId;

  @Column(name = "emailAdresse")
  private String emailAdresse;

  /**
   * @return the emailAdresseId
   */
  public Long getEmailAdresseId() {
    return emailAdresseId;
  }

  /**
   * @param emailAdresseId the emailAdresseId to set
   */
  public void setEmailAdresseId(Long emailAdresseId) {
    this.emailAdresseId = emailAdresseId;
  }

  /**
   * @return the emailAdresse
   */
  public String getEmailAdresse() {
    return emailAdresse;
  }

  /**
   * @param emailAdresse the emailAdresse to set
   */
  public void setEmailAdresse(String emailAdresse) {
    this.emailAdresse = emailAdresse;
  }

}
