package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "UserHistory")
@EntityListeners(AuditingEntityListener.class)
public class UserHistory implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long userHistoryId;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private String action;

  @ManyToOne
  @JoinColumn(name = "editById")
  private User editBy;

  private Long userEditId;

  public UserHistory() {
    super();
  }

  public UserHistory(Long userHistoryId, Date createdDate, Date modifiedDate, String action,
      User editBy, Long userEditId) {
    super();
    this.userHistoryId = userHistoryId;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.action = action;
    this.editBy = editBy;
    this.userEditId = userEditId;
  }

  public Long getUserHistoryId() {
    return userHistoryId;
  }

  public void setUserHistoryId(Long userHistoryId) {
    this.userHistoryId = userHistoryId;
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

  public User getEditBy() {
    return editBy;
  }

  public void setEditBy(User editBy) {
    this.editBy = editBy;
  }

  public Long getUserEditId() {
    return userEditId;
  }

  public void setUserEditId(Long userEditId) {
    this.userEditId = userEditId;
  }

  @Override
  public String toString() {
    return "UserHistory [userHistoryId=" + userHistoryId + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + ", action=" + action + ", editBy=" + editBy
        + ", userEditId=" + userEditId + "]";
  }

}
