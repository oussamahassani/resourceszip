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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "offre")
@EntityListeners(AuditingEntityListener.class)
public class Offre implements Serializable {
  private static final long serialVersionUID = 1L;


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "offreId")
  private Long offreId;

  private String type;
  private Boolean isPromo = false;
  private Boolean isRevSelected = false;
  @Temporal(TemporalType.DATE)
  private Date dateDebutPromo;

  @Temporal(TemporalType.DATE)
  private Date dateFinPromo;

  private Long periodeValidPromo;

  private Long idOffreBase;
  private Boolean isActive = false;

  private String title;


  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  private Boolean isPrivate = false;

  public Offre() {
    super();
    // TODO Auto-generated constructor stub
  }

  public Offre(Long offreId, String type, Boolean is_promo, Date dateDebutPromo, Date dateFinPromo,
      Long periodeValidPromo, Long idOffreBase, Boolean isActive) {
    super();
    this.offreId = offreId;
    this.type = type;
    this.isPromo = is_promo;
    this.dateDebutPromo = dateDebutPromo;
    this.dateFinPromo = dateFinPromo;
    this.periodeValidPromo = periodeValidPromo;
    this.idOffreBase = idOffreBase;
    this.isActive = isActive;
  }

  public Long getOffreId() {
    return offreId;
  }

  public void setOffreId(Long offreId) {
    this.offreId = offreId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public Date getDateDebutPromo() {
    return dateDebutPromo;
  }

  public void setDateDebutPromo(Date dateDebutPromo) {
    this.dateDebutPromo = dateDebutPromo;
  }

  public Date getDateFinPromo() {
    return dateFinPromo;
  }

  public void setDateFinPromo(Date dateFinPromo) {
    this.dateFinPromo = dateFinPromo;
  }

  public Long getPeriodeValidPromo() {
    return periodeValidPromo;
  }

  public void setPeriodeValidPromo(Long periodeValidPromo) {
    this.periodeValidPromo = periodeValidPromo;
  }

  public Long getIdOffreBase() {
    return idOffreBase;
  }

  public void setIdOffreBase(Long idOffreBase) {
    this.idOffreBase = idOffreBase;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }



  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Boolean getIsPromo() {
    return isPromo;
  }

  public void setIsPromo(Boolean isPromo) {
    this.isPromo = isPromo;
  }


  public Boolean getIsPrivate() {
    return isPrivate;
  }

  public void setIsPrivate(Boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  public Boolean getIsRevSelected() {
    return isRevSelected;
  }

  public void setIsRevSelected(Boolean isRevSelected) {
    this.isRevSelected = isRevSelected;
  }

  @Override
  public String toString() {
    return "Offre [offreId=" + offreId + ", type=" + type + ", isPromo=" + isPromo
        + ", dateDebutPromo=" + dateDebutPromo + ", dateFinPromo=" + dateFinPromo
        + ", periodeValidPromo=" + periodeValidPromo + ", idOffreBase=" + idOffreBase
        + ", isActive=" + isActive + ", title=" + title + ", createdDate=" + createdDate + "]";
  }



}
