package crm.chifco.com.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "Reclamationhistoriques")
@EntityListeners(AuditingEntityListener.class)
public class ReclamationHistorique {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long reclamationHistoriquId;

  @Column(length = 75)
  private String firstName;

  @Column(length = 80)
  private String lastName;

  @Column(length = 115)
  private String ref_reclamation;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Column(length = 8000)
  private String description;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "createdBy")
  private User createdBy;

  public Long getReclamationHistoriquId() {
    return reclamationHistoriquId;
  }

  public void setReclamationHistoriquId(Long reclamationHistoriquId) {
    this.reclamationHistoriquId = reclamationHistoriquId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getRef_reclamation() {
    return ref_reclamation;
  }

  public void setRef_reclamation(String ref_reclamation) {
    this.ref_reclamation = ref_reclamation;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public ReclamationHistorique(Long reclamationHistoriquId, String firstName, String lastName,
      String ref_reclamation, Date createdDate, Date modifiedDate, String description,
      User createdBy) {
    super();
    this.reclamationHistoriquId = reclamationHistoriquId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.ref_reclamation = ref_reclamation;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.description = description;
    this.createdBy = createdBy;
  }

  public ReclamationHistorique() {
    super();
  }


}
