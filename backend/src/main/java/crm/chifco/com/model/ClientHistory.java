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
@Table(name = "Clientshistoriques")
@EntityListeners(AuditingEntityListener.class)
public class ClientHistory implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long clienHistoriquId;

  @Column(length = 75)
  private String firstName;

  @Column(length = 80)
  private String lastName;

  @Column(length = 115)
  private String cin;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Column(length = 8000)
  private String description;

  private String typePaiement;

  private String photoCin1;

  private String photoCin2;

  private String contratPdf;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "createdBy")
  private User createdBy;

  public ClientHistory() {

  }

  public ClientHistory(Long clienHistoriquId, String firstName, String lastName, String cin,
      Date createdDate, Date modifiedDate, String description, String typePaiement,
      String photoCin1, String photoCin2, String contratPdf, User createdBy) {
    super();
    this.clienHistoriquId = clienHistoriquId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.cin = cin;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.description = description;
    this.typePaiement = typePaiement;
    this.photoCin1 = photoCin1;
    this.photoCin2 = photoCin2;
    this.contratPdf = contratPdf;
    this.createdBy = createdBy;
  }

  public Long getClienHistoriquId() {
    return clienHistoriquId;
  }

  public void setClienHistoriquId(Long clienHistoriquId) {
    this.clienHistoriquId = clienHistoriquId;
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

  public String getCin() {
    return cin;
  }

  public void setCin(String cin) {
    this.cin = cin;
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

  public String getTypePaiement() {
    return typePaiement;
  }

  public void setTypePaiement(String typePaiement) {
    this.typePaiement = typePaiement;
  }

  public String getPhotoCin1() {
    return photoCin1;
  }

  public void setPhotoCin1(String photoCin1) {
    this.photoCin1 = photoCin1;
  }

  public String getPhotoCin2() {
    return photoCin2;
  }

  public void setPhotoCin2(String photoCin2) {
    this.photoCin2 = photoCin2;
  }

  public String getContratPdf() {
    return contratPdf;
  }

  public void setContratPdf(String contratPdf) {
    this.contratPdf = contratPdf;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

}
