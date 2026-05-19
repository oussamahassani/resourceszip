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
@Table(name = "engagement")
@EntityListeners(AuditingEntityListener.class)
public class Engagement {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long engagementId;

  @Column(length = 520)
  private String nomEngagement;

  @Column(length = 400)
  private String nombre;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Long getEngagementId() {
    return engagementId;
  }

  public void setEngagementId(Long engagementId) {
    this.engagementId = engagementId;
  }

  public String getNomEngagement() {
    return nomEngagement;
  }

  public void setNomEngagement(String nomEngagement) {
    this.nomEngagement = nomEngagement;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
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

  public Engagement(Long engagementId, String nomEngagement, String nombre, Date createdDate,
      Date modifiedDate) {
    super();
    this.engagementId = engagementId;
    this.nomEngagement = nomEngagement;
    this.nombre = nombre;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Engagement() {
    super();
  }

}
