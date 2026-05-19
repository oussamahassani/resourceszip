package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "AvoirHistory")
@EntityListeners(AuditingEntityListener.class)
public class AvoirHistory implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "idAvoirHistory")
  private Long idAvoirHistory;




  private Long avoirId;

  

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Column(length = 8000)
  private String description;

  // @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "createdBy")
  private User createdBy;

  public AvoirHistory() {}

public Long getIdAvoirHistory() {
	return idAvoirHistory;
}

public void setIdAvoirHistory(Long idAvoirHistory) {
	this.idAvoirHistory = idAvoirHistory;
}

public Long getAvoirId() {
	return avoirId;
}

public void setAvoirId(Long avoirId) {
	this.avoirId = avoirId;
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

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public User getCreatedBy() {
	return createdBy;
}

public void setCreatedBy(User createdBy) {
	this.createdBy = createdBy;
}

@Override
public String toString() {
	return String.format(
			"AvoirHistory [idAvoirHistory=%s, avoirId=%s, createdDate=%s, modifiedDate=%s, description=%s, createdBy=%s]",
			idAvoirHistory, avoirId, createdDate, modifiedDate, description, createdBy);
}


}
