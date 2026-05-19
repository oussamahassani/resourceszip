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
@Table(name = "Servicetype")
@EntityListeners(AuditingEntityListener.class)
public class Servicetype {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long servicetypeId;

  @Column(length = 50)
  private String categorytype;
  @Column(length = 50)
  private String categorytypear;
  @Column(length = 50)
  private String categorytypeen;
  private Boolean isPrivate;

  @Column(length = 100)
  private String designation;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Boolean getIsPrivate() {
    return isPrivate;
  }

  public void setIsPrivate(Boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  public Long getServicetypeId() {
    return servicetypeId;
  }

  public String getCategorytypear() {
    return categorytypear;
  }

  public void setCategorytypear(String categorytypear) {
    this.categorytypear = categorytypear;
  }

  public String getCategorytypeen() {
    return categorytypeen;
  }

  public void setCategorytypeen(String categorytypeen) {
    this.categorytypeen = categorytypeen;
  }

  public void setServicetypeId(Long servicetypeId) {
    this.servicetypeId = servicetypeId;
  }

  public String getCategorytype() {
    return categorytype;
  }

  public void setCategorytype(String categorytype) {
    this.categorytype = categorytype;
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



  public Servicetype(Long servicetypeId, String categorytype, String categorytypear,
      String categorytypeen, Boolean isPrivate, String designation, Date createdDate,
      Date modifiedDate) {
    super();
    this.servicetypeId = servicetypeId;
    this.categorytype = categorytype;
    this.categorytypear = categorytypear;
    this.categorytypeen = categorytypeen;
    this.isPrivate = isPrivate;
    this.designation = designation;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Servicetype(Long servicetypeId, String categorytype, String categorytypear,
      String categorytypeen, String designation, Date createdDate, Date modifiedDate) {
    super();
    this.servicetypeId = servicetypeId;
    this.categorytype = categorytype;
    this.categorytypear = categorytypear;
    this.categorytypeen = categorytypeen;
    this.designation = designation;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Servicetype() {
    super();
  }

}
