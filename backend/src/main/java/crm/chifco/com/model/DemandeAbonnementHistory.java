package crm.chifco.com.model;

import java.io.Serializable;
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
@Table(name = "demandesabonnementhistoriques")
@EntityListeners(AuditingEntityListener.class)
public class DemandeAbonnementHistory implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "demandeidhistoriques")
  private Long demandeHistoriquesId;

  @Column(length = 75)
  private String firstName;

  @Column(length = 80)
  private String lastName;

  @Column(length = 115)
  private String cin;

  @Column(length = 115)
  private String adresse;

  @Column(length = 80)
  private String demandePdf;

  @Column(length = 80)
  private String contratPdf;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Column(length = 8000)
  private String description;

  // @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "createdBy")
  private User createdBy;

  public DemandeAbonnementHistory() {}

  public DemandeAbonnementHistory(Long demandeidHistoriques, String firstName, String lastName,
      String email, String cin, String adresse, String demandePdf, String contratpdf,
      Date createddate, Date modifieddate, String description) {
    super();
    this.demandeHistoriquesId = demandeidHistoriques;
    this.firstName = firstName;
    this.lastName = lastName;

    this.cin = cin;
    this.adresse = adresse;
    this.demandePdf = demandePdf;
    this.contratPdf = contratpdf;
    this.createdDate = createddate;
    this.modifiedDate = modifieddate;
    this.description = description;
  }

  /**
   * @return the demandeidHistoriques
   */
  public Long getDemandeidHistoriques() {
    return demandeHistoriquesId;
  }

  /**
   * @param demandeidHistoriques the demandeidHistoriques to set
   */
  public void setDemandeidHistoriques(Long demandeidHistoriques) {
    this.demandeHistoriquesId = demandeidHistoriques;
  }

  /**
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * @param firstName the firstName to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * @param lastName the lastName to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * @return the cin
   */
  public String getCin() {
    return cin;
  }

  /**
   * @param cin the cin to set
   */
  public void setCin(String cin) {
    this.cin = cin;
  }

  /**
   * @return the adresse
   */
  public String getAdresse() {
    return adresse;
  }

  /**
   * @param adresse the adresse to set
   */
  public void setAdresse(String adresse) {
    this.adresse = adresse;
  }

  /**
   * @return the demandePdf
   */
  public String getDemandepdf() {
    return demandePdf;
  }

  /**
   * @param demandePdf the demandePdf to set
   */
  public void setDemandepdf(String demandePdf) {
    this.demandePdf = demandePdf;
  }

  /**
   * @return the contratpdf
   */
  public String getContratpdf() {
    return contratPdf;
  }

  /**
   * @param contratpdf the contratpdf to set
   */
  public void setContratpdf(String contratpdf) {
    this.contratPdf = contratpdf;
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
   * @return the serialversionuid
   */
  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  /**
   * @return the createdBy
   */
  public User getCreatedBy() {
    return createdBy;
  }

  /**
   * @param createdBy the createdBy to set
   */
  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public String toString() {
    return "DemandeAbonnementHistory [demandeidHistoriques=" + demandeHistoriquesId + ", firstName="
        + firstName + ", lastName=" + lastName + ", cin=" + cin + ", adresse=" + adresse
        + ", demandePdf=" + demandePdf + ", contratpdf=" + contratPdf + ", createddate="
        + createdDate + ", modifieddate=" + modifiedDate + ", description=" + description + "]";
  }

}
