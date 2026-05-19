package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "gouvernorats")
@EntityListeners(AuditingEntityListener.class)
public class Gouvernorat implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long gouvernoratId;

  @Column(length = 75)
  private String gouvernoratName;


  @Column(length = 10)
  private String abreviation;


  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;


  public Gouvernorat() {}

  public Gouvernorat(Long gouvernoratId, String gouvernoratName) {
    this.gouvernoratId = gouvernoratId;
    this.gouvernoratName = gouvernoratName;

  }

  public Gouvernorat(String gouvernoratName) {
    this.gouvernoratName = gouvernoratName;
  }


  public Long getGouvernoratId() {
    return gouvernoratId;
  }

  public void setGouvernoratId(Long gouvernoratId) {
    this.gouvernoratId = gouvernoratId;
  }

  public String getGouvernoratName() {
    return gouvernoratName;
  }

  public void setGouvernoratName(String gouvernoratName) {
    this.gouvernoratName = gouvernoratName;
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

  public String getAbreviation() {
    return abreviation;
  }

  public void setAbreviation(String abreviation) {
    this.abreviation = abreviation;
  }

  @Override
  public String toString() {
    return "Ville [gouvernoratId=" + gouvernoratId + ", gouvernoratName=" + gouvernoratName
        + ", abreviation=" + abreviation + ", createdDate=" + createdDate + ", modifiedDate="
        + modifiedDate + "]";
  }


}
