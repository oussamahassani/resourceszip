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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "DemandeCommission")
@EntityListeners(AuditingEntityListener.class)
public class DemandeCommission implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String photoRecuRev;

  private String photoRecuAdmin;

  private String commentaireRev;

  private String commentaireAdmin;

  private String refDemandeCommission;

  @OneToOne
  private User demandeBy;

  @OneToOne
  private Commission commission;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date dateDecission;

  @OneToOne
  private User acceptedBy;

  String statut;
 Long groupe_id ;
  public DemandeCommission() {
    super();
  }

  public DemandeCommission(Long id, String photoRecuRev, String photoRecuAdmin,
      String commentaireRev, String commentaireAdmin, String refDemandeCommission, User demandeBy,
      Commission commission, Date createdDate, Date modifiedDate, Date dateDecission,
      User acceptedBy, String statut) {
    super();
    this.id = id;
    this.photoRecuRev = photoRecuRev;
    this.photoRecuAdmin = photoRecuAdmin;
    this.commentaireRev = commentaireRev;
    this.commentaireAdmin = commentaireAdmin;
    this.refDemandeCommission = refDemandeCommission;
    this.demandeBy = demandeBy;
    this.commission = commission;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.dateDecission = dateDecission;
    this.acceptedBy = acceptedBy;
    this.statut = statut;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPhotoRecuRev() {
    return photoRecuRev;
  }

  public void setPhotoRecuRev(String photoRecuRev) {
    this.photoRecuRev = photoRecuRev;
  }

  public String getPhotoRecuAdmin() {
    return photoRecuAdmin;
  }

  public void setPhotoRecuAdmin(String photoRecuAdmin) {
    this.photoRecuAdmin = photoRecuAdmin;
  }

  public String getCommentaireRev() {
    return commentaireRev;
  }

  public void setCommentaireRev(String commentaireRev) {
    this.commentaireRev = commentaireRev;
  }

  public String getCommentaireAdmin() {
    return commentaireAdmin;
  }

  public void setCommentaireAdmin(String commentaireAdmin) {
    this.commentaireAdmin = commentaireAdmin;
  }

  public String getRefDemandeCommission() {
    return refDemandeCommission;
  }

  public void setRefDemandeCommission(String refDemandeCommission) {
    this.refDemandeCommission = refDemandeCommission;
  }

  public User getDemandeBy() {
    return demandeBy;
  }

  public void setDemandeBy(User demandeBy) {
    this.demandeBy = demandeBy;
  }

  public Commission getCommission() {
    return commission;
  }

  public void setCommission(Commission commission) {
    this.commission = commission;
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

  public Date getDateDecission() {
    return dateDecission;
  }

  public void setDateDecission(Date dateDecission) {
    this.dateDecission = dateDecission;
  }

  public User getAcceptedBy() {
    return acceptedBy;
  }

  public void setAcceptedBy(User acceptedBy) {
    this.acceptedBy = acceptedBy;
  }

  public String getStatut() {
    return statut;
  }

  public void setStatut(String statut) {
    this.statut = statut;
  }


public Long getGroupe_id() {
	return groupe_id;
}

public void setGroupe_id(Long groupe_id) {
	this.groupe_id = groupe_id;
}

@Override
  public String toString() {
    return "DemandeCommission [id=" + id + ", photoRecuRev=" + photoRecuRev + ", photoRecuAdmin="
        + photoRecuAdmin + ", commentaireRev=" + commentaireRev + ", commentaireAdmin="
        + commentaireAdmin + ", refDemandeCommission=" + refDemandeCommission + ", demandeBy="
        + demandeBy + ", commission=" + commission + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + ", dateDecission=" + dateDecission + ", acceptedBy="
        + acceptedBy + ", statut=" + statut + "]";
  }



}
