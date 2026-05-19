package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "notificationConfig")
@EntityListeners(AuditingEntityListener.class)
public class NotificationConfig implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long notificationConfigId;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "createdByUserId")
  private User createdByUserId;

  @OneToMany
  @JoinColumn(name = "notificaionId")
  private List<PhoneNumbers> phoneNumbers;

  @OneToMany
  @JoinColumn(name = "notificaionId")
  private List<EmailAdresse> emailAdresse;

  @JoinColumn(name = "eventAction")
  private String eventAction;

  @Column(name = "villeName", length = 75)
  private String villeName;

  public NotificationConfig() {
    // TODO Auto-generated constructor stub
  }

  public NotificationConfig(Long notificationConfigId, Date createdDate, Date modifiedDate,
      User createdByUserId, List<PhoneNumbers> phoneNumbers, List<EmailAdresse> emailAdresse,
      String eventAction, String villeName) {
    super();
    this.notificationConfigId = notificationConfigId;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.createdByUserId = createdByUserId;
    this.phoneNumbers = phoneNumbers;
    this.emailAdresse = emailAdresse;
    this.eventAction = eventAction;
    this.villeName = villeName;
  }

  /**
   * @return the notificationConfigId
   */
  public Long getNotificationConfigId() {
    return notificationConfigId;
  }

  /**
   * @param notificationConfigId the notificationConfigId to set
   */
  public void setNotificationConfigId(Long notificationConfigId) {
    this.notificationConfigId = notificationConfigId;
  }

  /**
   * @return the createdDate
   */
  public Date getCreatedDate() {
    return createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return the modifiedDate
   */
  public Date getModifiedDate() {
    return modifiedDate;
  }

  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return the createdByUserId
   */
  public User getCreatedByUserId() {
    return createdByUserId;
  }

  /**
   * @param createdByUserId the createdByUserId to set
   */
  public void setCreatedByUserId(User createdByUserId) {
    this.createdByUserId = createdByUserId;
  }

  /**
   * @return the phoneNumbers
   */
  public List<PhoneNumbers> getPhoneNumbers() {
    return phoneNumbers;
  }

  /**
   * @param phoneNumbers the phoneNumbers to set
   */
  public void setPhoneNumbers(List<PhoneNumbers> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  /**
   * @return the emailAdresse
   */
  public List<EmailAdresse> getEmailAdresse() {
    return emailAdresse;
  }

  /**
   * @param emailAdresse the emailAdresse to set
   */
  public void setEmailAdresse(List<EmailAdresse> emailAdresse) {
    this.emailAdresse = emailAdresse;
  }

  /**
   * @return the eventAction
   */
  public String getEventAction() {
    return eventAction;
  }

  /**
   * @param eventAction the eventAction to set
   */
  public void setEventAction(String eventAction) {
    this.eventAction = eventAction;
  }

  /**
   * @return the villeName
   */
  public String getVilleName() {
    return villeName;
  }

  /**
   * @param villeName the villeName to set
   */
  public void setVilleName(String villeName) {
    this.villeName = villeName;
  }

  @Override
  public String toString() {
    return "NotificationConfig [notificationConfigId=" + notificationConfigId + ", createdDate="
        + createdDate + ", modifiedDate=" + modifiedDate + ", createdByUserId=" + createdByUserId
        + ", phoneNumbers=" + phoneNumbers + ", emailAdresse=" + emailAdresse + ", eventAction="
        + eventAction + ", villeName=" + villeName + "]";
  }



}
