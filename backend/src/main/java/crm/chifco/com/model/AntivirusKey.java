package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class AntivirusKey implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String licenseKey;

  private boolean active;

  @ManyToOne
  private Abonnement abonnement;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date dateAffectation;

  @OneToOne
  private User affectedBy;

  @OneToOne
  private User createdBy;

  private Integer duree;
  private String type;

  public AntivirusKey() {
    super();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public AntivirusKey(Long id, String licenseKey, boolean active, Abonnement abonnement,
      Date createdDate, Date dateAffectation, User affectedBy, User createdBy, Integer duree,
      String type) {
    super();
    this.id = id;
    this.licenseKey = licenseKey;
    this.active = active;
    this.abonnement = abonnement;
    this.createdDate = createdDate;
    this.dateAffectation = dateAffectation;
    this.affectedBy = affectedBy;
    this.createdBy = createdBy;
    this.duree = duree;
    this.type = type;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLicenseKey() {
    return licenseKey;
  }

  public void setLicenseKey(String licenseKey) {
    this.licenseKey = licenseKey;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Abonnement getAbonnement() {
    return abonnement;
  }

  public void setAbonnement(Abonnement abonnement) {
    this.abonnement = abonnement;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getDateAffectation() {
    return dateAffectation;
  }

  public void setDateAffectation(Date dateAffectation) {
    this.dateAffectation = dateAffectation;
  }

  public User getAffectedBy() {
    return affectedBy;
  }

  public void setAffectedBy(User affectedBy) {
    this.affectedBy = affectedBy;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public Integer getDuree() {
    return duree;
  }

  public void setDuree(Integer duree) {
    this.duree = duree;
  }

  @Override
  public String toString() {
    return "AntivirusKey [id=" + id + ", licenseKey=" + licenseKey + ", active=" + active
        + ", abonnement=" + abonnement + ", createdDate=" + createdDate + ", dateAffectation="
        + dateAffectation + ", affectedBy=" + affectedBy + ", createdBy=" + createdBy + ", duree="
        + duree + "]";
  }

}
