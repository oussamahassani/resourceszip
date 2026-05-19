package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author oussama.hassani
 *
 */
/**
 * @author oussama.hassani
 *
 */
@Entity
@Table(name = "bordereau")
@EntityListeners(AuditingEntityListener.class)
public class Bordereau implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long bordereauId;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate; // now ;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "check_by")
  private User checkBy;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private Double montant;
  private String status;
  private Long numfacure;
  private String photoRecu;
  private String commentaire;

  @Temporal(TemporalType.DATE)
  private Date dateUplodeJustificatif;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "bordereau_id")
  private List<EntryBordereau> entry;

  private String referenceBordereau;

  @Temporal(TemporalType.DATE)
  private Date dateVersement;

  @Temporal(TemporalType.DATE)
  private Date dateValidationBrd;

  private String typeDePayement;

  public Bordereau() {}

  public Bordereau(Long bordereauId, Date createdDate, User user, User checkBy, Date modifiedDate,
      Double montant, String status, Long numfacure, String photoRecu, String commentaire,
      List<EntryBordereau> entry, String referenceBordereau) {
    super();
    this.bordereauId = bordereauId;
    this.createdDate = createdDate;
    this.user = user;
    this.checkBy = checkBy;
    this.modifiedDate = modifiedDate;
    this.montant = montant;
    this.status = status;
    this.numfacure = numfacure;
    this.photoRecu = photoRecu;
    this.commentaire = commentaire;
    this.entry = entry;
    this.referenceBordereau = referenceBordereau;
  }

  /**
   * @return the bordereauId
   */
  public Long getBordereauId() {
    return bordereauId;
  }

  /**
   * @param bordereauId the bordereauId to set
   */
  public void setBordereauId(Long bordereauId) {
    this.bordereauId = bordereauId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getStatus() {
    return status;
  }

  public void setstatus(String status) {
    this.status = status;
  }

  public Double getMontant() {
    return montant;
  }

  public void setmontant(Double montant) {
    this.montant = montant;
  }

  public Long getNumfacure() {
    return numfacure;
  }

  public void setnumfacure(Long numfacure) {
    this.numfacure = numfacure;
  }

  /**
   * @return the entry
   */
  public List<EntryBordereau> getEntry() {
    return entry;
  }

  /**
   * @param entry the entry to set
   */
  public void setEntry(List<EntryBordereau> entry) {
    this.entry = entry;
  }

  /**
   * @return the commentaire
   */
  public String getCommentaire() {
    return commentaire;
  }

  /**
   * @param commentaire the commentaire to set
   */
  public void setCommentaire(String commentaire) {
    this.commentaire = commentaire;
  }

  /**
   * @return the referenceBordereau
   */
  public String getReferenceBordereau() {
    return referenceBordereau;
  }

  /**
   * @param referenceBordereau the referenceBordereau to set
   */
  public void setReferenceBordereau(String referenceBordereau) {
    this.referenceBordereau = referenceBordereau;
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

  public String getPhotoRecu() {
    return photoRecu;
  }

  public void setPhotoRecu(String photoRecu) {
    this.photoRecu = photoRecu;
  }

  public User getCheckBy() {
    return checkBy;
  }

  public void setCheckBy(User checkBy) {
    this.checkBy = checkBy;
  }

  public Date getDateUplodeJustificatif() {
    return dateUplodeJustificatif;
  }

  public void setDateUplodeJustificatif(Date dateUplodeJustificatif) {
    this.dateUplodeJustificatif = dateUplodeJustificatif;
  }

  public Date getDateVersement() {
    return dateVersement;
  }

  public void setDateVersement(Date dateVersement) {
    this.dateVersement = dateVersement;
  }

  public Date getDateValidationBrd() {
    return dateValidationBrd;
  }

  public void setDateValidationBrd(Date dateValidationBrd) {
    this.dateValidationBrd = dateValidationBrd;
  }

  public String getTypeDePayement() {
    return typeDePayement;
  }

  public void setTypeDePayement(String typeDePayement) {
    this.typeDePayement = typeDePayement;
  }

  @Override
  public String toString() {
    return "Bordereau [bordereauId=" + bordereauId + ", createdDate=" + createdDate + ", user="
        + user + ", checkBy=" + checkBy + ", modifiedDate=" + modifiedDate + ", montant=" + montant
        + ", status=" + status + ", numfacure=" + numfacure + ", photoRecu=" + photoRecu
        + ", commentaire=" + commentaire + ", dateUplodeJustificatif=" + dateUplodeJustificatif
        + ", entry=" + entry + ", referenceBordereau=" + referenceBordereau + ", dateVersement="
        + dateVersement + "]";
  }

}
