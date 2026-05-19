package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import crm.chifco.com.service.GenerateSequenceReferenceCommisionAvance;
import crm.chifco.com.utils.StatutAvanceBordereau;

@Entity
@Table(name = "avanceCommissionAcquisition")
@EntityListeners(AuditingEntityListener.class)
public class AvanceCommissionAcquisition implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  private User revendeur;

  private Long bordereau;

  @GeneratorType(type = GenerateSequenceReferenceCommisionAvance.class,
      when = GenerationTime.INSERT)
  @Column(name = "referenceCommisionAvance", unique = true, nullable = false, updatable = false)
  private String referenceCommisionAvance;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  // Commission premiereFacture
  private Integer nbrPermiereFacture;
  private Double montantCommissionPremiereFacture;

  private Double totalHt;
  private Integer tva;

  private String statut = StatutAvanceBordereau.CEREATE_AVANCE;

  private Boolean brdValidated = false;
  private Long idBrdDemande;

  @Temporal(TemporalType.TIMESTAMP)
  private Date dateVersementBrd;

  public AvanceCommissionAcquisition() {
    super();
  }



  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }



  public User getRevendeur() {
    return revendeur;
  }

  public void setRevendeur(User revendeur) {
    this.revendeur = revendeur;
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


  public Double getMontantCommissionPremiereFacture() {
    return montantCommissionPremiereFacture;
  }

  public void setMontantCommissionPremiereFacture(Double montantCommissionPremiereFacture) {
    this.montantCommissionPremiereFacture = montantCommissionPremiereFacture;
  }



  public Double getTotalHt() {
    return totalHt;
  }

  public void setTotalHt(Double totalHt) {
    this.totalHt = totalHt;
  }

  public Integer getTva() {
    return tva;
  }

  public void setTva(Integer tva) {
    this.tva = tva;
  }



  public String getStatut() {
    return statut;
  }

  public void setStatut(String statut) {
    this.statut = statut;
  }



  public Long getBordereau() {
    return bordereau;
  }



  public void setBordereau(Long bordereau) {
    this.bordereau = bordereau;
  }



  public Integer getNbrPermiereFacture() {
    return nbrPermiereFacture;
  }



  public void setNbrPermiereFacture(Integer nbrPermiereFacture) {
    this.nbrPermiereFacture = nbrPermiereFacture;
  }



  public String getReferenceCommisionAvance() {
    return referenceCommisionAvance;
  }



  public void setReferenceCommisionAvance(String referenceCommisionAvance) {
    this.referenceCommisionAvance = referenceCommisionAvance;
  }



  public Boolean getBrdValidated() {
    return brdValidated;
  }



  public void setBrdValidated(Boolean brdValidated) {
    this.brdValidated = brdValidated;
  }



  public Date getDateVersementBrd() {
    return dateVersementBrd;
  }



  public void setDateVersementBrd(Date dateVersementBrd) {
    this.dateVersementBrd = dateVersementBrd;
  }



  public Long getIdBrdDemande() {
    return idBrdDemande;
  }



  public void setIdBrdDemande(Long idBrdDemande) {
    this.idBrdDemande = idBrdDemande;
  }



  @Override
  public String toString() {
    return "AvanceCommissionAcquisition [id=" + id + ", revendeur=" + revendeur + ", bordereau="
        + bordereau + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate
        + ", nbrPermiereFacture=" + nbrPermiereFacture + ", montantCommissionPremiereFacture="
        + montantCommissionPremiereFacture + ", totalHt=" + totalHt + ", tva=" + tva + ", statut="
        + statut + "]";
  }



}
