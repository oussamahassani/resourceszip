package crm.chifco.com.model;

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
@Table(name = "TypeVisite")
@EntityListeners(AuditingEntityListener.class)
public class TypeVisite {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long typevisiteid;

  @Column(length = 20)
  private String nomType;

  @Column(length = 100)
  private String designation;


  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Long getTypevisiteid() {
    return typevisiteid;
  }

  public void setTypevisiteid(Long typevisiteid) {
    this.typevisiteid = typevisiteid;
  }

  public String getNomType() {
    return nomType;
  }

  public void setNomType(String nomType) {
    this.nomType = nomType;
  }

  public String getDesignation() {
    return designation;
  }

  public void setDesignation(String designation) {
    this.designation = designation;
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

  public TypeVisite(Long typevisiteid, String nomType, String designation, Date createdDate,
      Date modifiedDate) {
    super();
    this.typevisiteid = typevisiteid;
    this.nomType = nomType;
    this.designation = designation;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public TypeVisite() {
    super();
  }


}
