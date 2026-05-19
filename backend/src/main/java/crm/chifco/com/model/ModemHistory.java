package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class ModemHistory implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long modemHistoryId;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private String action;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modem_id")
  private Modem modem;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  public ModemHistory() {
    super();
  }


  public ModemHistory(Long modemHistoryId, Date createdDate, Date modifiedDate, String action,
      Modem modem, User user) {
    super();
    this.modemHistoryId = modemHistoryId;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.action = action;
    this.modem = modem;
    this.user = user;
  }

  public Long getModemHistoryId() {
    return modemHistoryId;
  }

  public void setModemHistoryId(Long modemHistoryId) {
    this.modemHistoryId = modemHistoryId;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Modem getModem() {
    return modem;
  }

  public void setModem(Modem modem) {
    this.modem = modem;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "ModemHistory [modemHistoryId=" + modemHistoryId + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + ", action=" + action + ", modem=" + modem + ", user="
        + user + "]";
  }



}
