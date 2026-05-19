package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OffreCommission")
@EntityListeners(AuditingEntityListener.class)
public class OffreCommission implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private int annee;

  private int mois;

  private String type;

  private Double montant;

  private int debit;

  private Integer palierMin;

  private Integer palierMax;

  @ManyToOne
  private User createdBy;

  @ManyToOne
  private User modifyBy;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private Boolean isActive = true;

  public OffreCommission() {
    super();
  }

  public OffreCommission(Long id, int annee, int mois, String type, Double montant, int debit,
      Integer palierMin, Integer palierMax, User createdBy, User modifyBy, Date createdDate,
      Date modifiedDate, Boolean isActive) {
    super();
    this.id = id;
    this.annee = annee;
    this.mois = mois;
    this.type = type;
    this.montant = montant;
    this.debit = debit;
    this.palierMin = palierMin;
    this.palierMax = palierMax;
    this.createdBy = createdBy;
    this.modifyBy = modifyBy;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.isActive = isActive;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getAnnee() {
    return annee;
  }

  public void setAnnee(int annee) {
    this.annee = annee;
  }

  public int getMois() {
    return mois;
  }

  public void setMois(int mois) {
    this.mois = mois;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Double getMontant() {
    return montant;
  }

  public void setMontant(Double montant) {
    this.montant = montant;
  }

  public int getDebit() {
    return debit;
  }

  public void setDebit(int debit) {
    this.debit = debit;
  }

  public Integer getPalierMin() {
    return palierMin;
  }

  public void setPalierMin(Integer palierMin) {
    this.palierMin = palierMin;
  }

  public Integer getPalierMax() {
    return palierMax;
  }

  public void setPalierMax(Integer palierMax) {
    this.palierMax = palierMax;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public User getModifyBy() {
    return modifyBy;
  }

  public void setModifyBy(User modifyBy) {
    this.modifyBy = modifyBy;
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

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  @Override
  public String toString() {
    return "OffreCommission [id=" + id + ", annee=" + annee + ", mois=" + mois + ", type=" + type
        + ", montant=" + montant + ", debit=" + debit + ", palierMin=" + palierMin + ", palierMax="
        + palierMax + ", createdBy=" + createdBy + ", modifyBy=" + modifyBy + ", createdDate="
        + createdDate + ", modifiedDate=" + modifiedDate + ", isActive=" + isActive + "]";
  }



}
