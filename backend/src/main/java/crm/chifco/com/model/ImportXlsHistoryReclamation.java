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
@Table(name = "ImportXlsHistoryReclamation")
@EntityListeners(AuditingEntityListener.class)
public class ImportXlsHistoryReclamation implements Serializable {
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

  private String statusReclamation;

  private Long idfile;

  public String getStatusReclamation() {
    return statusReclamation;
  }

  public void setStatusReclamation(String statusReclamation) {
    this.statusReclamation = statusReclamation;
  }

  public ImportXlsHistoryReclamation() {
    super();
  }

  public ImportXlsHistoryReclamation(Long id, String referencett, String referenceChifco,
      String status, Date createdDate, Date modifiedDate, String description,
      String statusReclamation, Long idfile) {
    super();
    this.id = id;
    this.referencett = referencett;
    this.referenceChifco = referenceChifco;
    this.status = status;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.description = description;
    this.statusReclamation = statusReclamation;
    this.idfile = idfile;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getReferencett() {
    return referencett;
  }

  public void setReferencett(String referencett) {
    this.referencett = referencett;
  }

  public String getReferenceChifco() {
    return referenceChifco;
  }

  public void setReferenceChifco(String referenceChifco) {
    this.referenceChifco = referenceChifco;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getIdfile() {
    return idfile;
  }

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

  @Override
  public String toString() {
    return "ImportXlsHistory [id=" + id + ", referencett=" + referencett + ", referenceChifco="
        + referenceChifco + ", status=" + status + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + ", description=" + description + ", statusReclamation="
        + statusReclamation + ", idfile=" + idfile + "]";
  }

}
