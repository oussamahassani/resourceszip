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
@Table(name = "OffreCommissionPromo")
@EntityListeners(AuditingEntityListener.class)
public class OffreCommissionPromo implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String nomCommisionPromo;

  @Temporal(TemporalType.DATE)
  private Date dateDebut;

  @Temporal(TemporalType.DATE)
  private Date dateFin;



  private Double montantDemandePalier1;
  private Double montantDemandePalier2;
  private Double montantDemandePalier3;
  private Double montantDemandePalier4;
  private Double montantDemandePalier5;

  private Double montantActivationPalier1;
  private Double montantActivationPalier2;
  private Double montantActivationPalier3;
  private Double montantActivationPalier4;
  private Double montantActivationPalier5;

  private Double montantPayementPalier1;
  private Double montantPayementPalier2;
  private Double montantPayementPalier3;
  private Double montantPayementPalier4;
  private Double montantPayementPalier5;

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

  public OffreCommissionPromo() {
    super();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNomCommisionPromo() {
    return nomCommisionPromo;
  }

  public void setNomCommisionPromo(String nomCommisionPromo) {
    this.nomCommisionPromo = nomCommisionPromo;
  }

  public Date getDateDebut() {
    return dateDebut;
  }

  public void setDateDebut(Date dateDebut) {
    this.dateDebut = dateDebut;
  }



  public Date getDateFin() {
    return dateFin;
  }

  public void setDateFin(Date dateFin) {
    this.dateFin = dateFin;
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

  public Double getMontantDemandePalier1() {
    return montantDemandePalier1;
  }

  public void setMontantDemandePalier1(Double montantDemandePalier1) {
    this.montantDemandePalier1 = montantDemandePalier1;
  }

  public Double getMontantDemandePalier2() {
    return montantDemandePalier2;
  }

  public void setMontantDemandePalier2(Double montantDemandePalier2) {
    this.montantDemandePalier2 = montantDemandePalier2;
  }

  public Double getMontantDemandePalier3() {
    return montantDemandePalier3;
  }

  public void setMontantDemandePalier3(Double montantDemandePalier3) {
    this.montantDemandePalier3 = montantDemandePalier3;
  }

  public Double getMontantDemandePalier4() {
    return montantDemandePalier4;
  }

  public void setMontantDemandePalier4(Double montantDemandePalier4) {
    this.montantDemandePalier4 = montantDemandePalier4;
  }

  public Double getMontantDemandePalier5() {
    return montantDemandePalier5;
  }

  public void setMontantDemandePalier5(Double montantDemandePalier5) {
    this.montantDemandePalier5 = montantDemandePalier5;
  }

  public Double getMontantActivationPalier1() {
    return montantActivationPalier1;
  }

  public void setMontantActivationPalier1(Double montantActivationPalier1) {
    this.montantActivationPalier1 = montantActivationPalier1;
  }

  public Double getMontantActivationPalier2() {
    return montantActivationPalier2;
  }

  public void setMontantActivationPalier2(Double montantActivationPalier2) {
    this.montantActivationPalier2 = montantActivationPalier2;
  }

  public Double getMontantActivationPalier3() {
    return montantActivationPalier3;
  }

  public void setMontantActivationPalier3(Double montantActivationPalier3) {
    this.montantActivationPalier3 = montantActivationPalier3;
  }

  public Double getMontantActivationPalier4() {
    return montantActivationPalier4;
  }

  public void setMontantActivationPalier4(Double montantActivationPalier4) {
    this.montantActivationPalier4 = montantActivationPalier4;
  }

  public Double getMontantActivationPalier5() {
    return montantActivationPalier5;
  }

  public void setMontantActivationPalier5(Double montantActivationPalier5) {
    this.montantActivationPalier5 = montantActivationPalier5;
  }

  public Double getMontantPayementPalier1() {
    return montantPayementPalier1;
  }

  public void setMontantPayementPalier1(Double montantPayementPalier1) {
    this.montantPayementPalier1 = montantPayementPalier1;
  }

  public Double getMontantPayementPalier2() {
    return montantPayementPalier2;
  }

  public void setMontantPayementPalier2(Double montantPayementPalier2) {
    this.montantPayementPalier2 = montantPayementPalier2;
  }

  public Double getMontantPayementPalier3() {
    return montantPayementPalier3;
  }

  public void setMontantPayementPalier3(Double montantPayementPalier3) {
    this.montantPayementPalier3 = montantPayementPalier3;
  }

  public Double getMontantPayementPalier4() {
    return montantPayementPalier4;
  }

  public void setMontantPayementPalier4(Double montantPayementPalier4) {
    this.montantPayementPalier4 = montantPayementPalier4;
  }

  public Double getMontantPayementPalier5() {
    return montantPayementPalier5;
  }

  public void setMontantPayementPalier5(Double montantPayementPalier5) {
    this.montantPayementPalier5 = montantPayementPalier5;
  }

  @Override
  public String toString() {
    return "OffreCommissionPromo [id=" + id + ", nomCommisionPromo=" + nomCommisionPromo
        + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin + ", montantDemandePalier1="
        + montantDemandePalier1 + ", montantDemandePalier2=" + montantDemandePalier2
        + ", montantDemandePalier3=" + montantDemandePalier3 + ", montantDemandePalier4="
        + montantDemandePalier4 + ", montantDemandePalier5=" + montantDemandePalier5
        + ", montantActivationPalier1=" + montantActivationPalier1 + ", montantActivationPalier2="
        + montantActivationPalier2 + ", montantActivationPalier3=" + montantActivationPalier3
        + ", montantActivationPalier4=" + montantActivationPalier4 + ", montantActivationPalier5="
        + montantActivationPalier5 + ", montantPayementPalier1=" + montantPayementPalier1
        + ", montantPayementPalier2=" + montantPayementPalier2 + ", montantPayementPalier3="
        + montantPayementPalier3 + ", montantPayementPalier4=" + montantPayementPalier4
        + ", montantPayementPalier5=" + montantPayementPalier5 + ", createdBy=" + createdBy
        + ", modifyBy=" + modifyBy + ", createdDate=" + createdDate + ", modifiedDate="
        + modifiedDate + ", isActive=" + isActive + "]";
  }



}
