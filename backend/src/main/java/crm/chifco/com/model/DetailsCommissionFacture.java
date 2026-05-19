package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
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
@Table(name = "DetailsCommissionFacture")
@EntityListeners(AuditingEntityListener.class)
public class DetailsCommissionFacture implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String referenceFacture;

  private Double montantFacture;

  private Boolean isEcheance;

  private Double montantCommisison;

  private Long commissionId;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public DetailsCommissionFacture() {
    super();
  }

  public DetailsCommissionFacture(Long id, String referenceFacture, Double montantFacture,
      Boolean isEcheance, Double montantCommisison, Long commissionId, Date createdDate,
      Date modifiedDate) {
    super();
    this.id = id;
    this.referenceFacture = referenceFacture;
    this.montantFacture = montantFacture;
    this.isEcheance = isEcheance;
    this.montantCommisison = montantCommisison;
    this.commissionId = commissionId;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getReferenceFacture() {
    return referenceFacture;
  }

  public void setReferenceFacture(String referenceFacture) {
    this.referenceFacture = referenceFacture;
  }

  public Double getMontantFacture() {
    return montantFacture;
  }

  public void setMontantFacture(Double montantFacture) {
    this.montantFacture = montantFacture;
  }

  public Boolean getIsEcheance() {
    return isEcheance;
  }

  public void setIsEcheance(Boolean isEcheance) {
    this.isEcheance = isEcheance;
  }

  public Double getMontantCommisison() {
    return montantCommisison;
  }

  public void setMontantCommisison(Double montantCommisison) {
    this.montantCommisison = montantCommisison;
  }

  public Long getCommissionId() {
    return commissionId;
  }

  public void setCommissionId(Long commissionId) {
    this.commissionId = commissionId;
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



}
