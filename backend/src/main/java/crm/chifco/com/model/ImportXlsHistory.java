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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ImportXlsHistory")
@EntityListeners(AuditingEntityListener.class)
public class ImportXlsHistory implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  private Long id;

  @Column(name = "referencett", length = 75)
  private String referencett;

  @Column(name = "referenceChifco", length = 80)
  private String referenceChifco;

  @Column(name = "status", length = 115)
  private String status;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private String description;

  private String statusDemande;

  private Long idfile;

  public ImportXlsHistory() {

  }



  public ImportXlsHistory(Long id, String referencett, String referenceChifco, String status,
      Date createdDate, Date modifiedDate, String description, String statusDemande, Long idfile) {
    super();
    this.id = id;
    this.referencett = referencett;
    this.referenceChifco = referenceChifco;
    this.status = status;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.description = description;
    this.statusDemande = statusDemande;
    this.idfile = idfile;
  }



  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the referencett
   */
  public String getReferencett() {
    return referencett;
  }

  /**
   * @param referencett the referencett to set
   */
  public void setReferencett(String referencett) {
    this.referencett = referencett;
  }

  /**
   * @return the referenceChifco
   */
  public String getReferenceChifco() {
    return referenceChifco;
  }

  /**
   * @param referenceChifco the referenceChifco to set
   */
  public void setReferenceChifco(String referenceChifco) {
    this.referenceChifco = referenceChifco;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }



  /**
   * @return the idfile
   */
  public Long getIdfile() {
    return idfile;
  }

  /**
   * @param idfile the idfile to set
   */
  public void setIdfile(Long idfile) {
    this.idfile = idfile;
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



  /**
   * @return the statusDemande
   */
  public String getStatusDemande() {
    return statusDemande;
  }



  /**
   * @param statusDemande the statusDemande to set
   */
  public void setStatusDemande(String statusDemande) {
    this.statusDemande = statusDemande;
  }



  @Override
  public String toString() {
    return "ImportXlsHistory [id=" + id + ", referencett=" + referencett + ", referenceChifco="
        + referenceChifco + ", status=" + status + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + ", description=" + description + ", statusDemande="
        + statusDemande + ", idfile=" + idfile + "]";
  }



}
