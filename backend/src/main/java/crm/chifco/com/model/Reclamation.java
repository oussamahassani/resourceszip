package crm.chifco.com.model;

import java.util.Date;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import crm.chifco.com.service.GenerateSequenceReclamation;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Reclamation {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long reclamationid;
  @GeneratorType(type = GenerateSequenceReclamation.class, when = GenerationTime.INSERT)
  @Column(name = "ref_reclamation", unique = true, nullable = false, updatable = false)
  private String ref_reclamation;
  private String description;
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "statut_id")
  private Statusrec status;
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "servicetype_id")
  private Servicetype serviceType;
  private String autre;
  private Date date_reclamationtt;
  private Date date_etattt;
  private String etattt;
  private Date date_verificationfsi;
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "treatedBy")
  private User treatedBy;
  private String source;
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @ManyToOne
  @JoinColumn(name = "createdby_id", nullable = false)
  private User createdby;
  @ManyToOne
  @JoinColumn(name = "edit_id", nullable = false)
  private User editedby;

  @ManyToOne
  @JoinColumn(name = "client_id", nullable = true)
  private Abonnement client;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = true)
  private User user;

  private String category;
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "motif_id")
  private Motifrec motif;
  private String referencett;
  private String gouvernorat;
  private String central;
  @ElementCollection
  @CollectionTable(name = "reclamation_justificatifs",
      joinColumns = @JoinColumn(name = "reclamation_id"))
  @Column(name = "justificatif")
  private List<String> justificatifs;
  private Boolean isEmailSent = false;
  private String statuttech;

  public String getStatuttech() {
    return statuttech;
  }

  public void setStatuttech(String statuttech) {
    this.statuttech = statuttech;
  }

  public Boolean getIsEmailSent() {
    return isEmailSent;
  }

  public void setIsEmailSent(Boolean isEmailSent) {
    this.isEmailSent = isEmailSent;
  }

  public String getEtattt() {
    return etattt;
  }

  public void setEtattt(String etattt) {
    this.etattt = etattt;
  }

  public String getAutre() {
    return autre;
  }

  public void setAutre(String autre) {
    this.autre = autre;
  }

  public User getEditedby() {
    return editedby;
  }

  public void setEditedby(User editedby) {
    this.editedby = editedby;
  }

  public String getReferencett() {
    return referencett;
  }

  public void setReferencett(String referencett) {
    this.referencett = referencett;
  }

  public Long getReclamationid() {
    return reclamationid;
  }

  public void setReclamationid(Long reclamationid) {
    this.reclamationid = reclamationid;
  }

  public String getRef_reclamation() {
    return ref_reclamation;
  }

  public void setRef_reclamation(String ref_reclamation) {
    this.ref_reclamation = ref_reclamation;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public User getCreatedby() {
    return createdby;
  }

  public void setCreatedby(User createdby) {
    this.createdby = createdby;
  }



  public Abonnement getClient() {
    return client;
  }

  public void setClient(Abonnement client) {
    this.client = client;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }



  public List<String> getJustificatifs() {
    return justificatifs;
  }

  public void setJustificatifs(List<String> justificatifs) {
    this.justificatifs = justificatifs;
  }

  public Statusrec getStatus() {
    return status;
  }

  public void setStatus(Statusrec status) {
    this.status = status;
  }

  public Servicetype getServiceType() {
    return serviceType;
  }

  public void setServiceType(Servicetype serviceType) {
    this.serviceType = serviceType;
  }

  public Motifrec getMotif() {
    return motif;
  }

  public void setMotif(Motifrec motif) {
    this.motif = motif;
  }

  public Reclamation() {
    super();
  }

  public Date getDate_reclamationtt() {
    return date_reclamationtt;
  }

  public void setDate_reclamationtt(Date date_reclamationtt) {
    this.date_reclamationtt = date_reclamationtt;
  }

  public Date getDate_etattt() {
    return date_etattt;
  }

  public void setDate_etattt(Date date_etattt) {
    this.date_etattt = date_etattt;
  }

  public Date getDate_verificationfsi() {
    return date_verificationfsi;
  }

  public void setDate_verificationfsi(Date date_verificationfsi) {
    this.date_verificationfsi = date_verificationfsi;
  }

  public User getTreatedBy() {
    return treatedBy;
  }

  public void setTreatedBy(User treatedBy) {
    this.treatedBy = treatedBy;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getGouvernorat() {
    return gouvernorat;
  }

  public void setGouvernorat(String gouvernorat) {
    this.gouvernorat = gouvernorat;
  }

  public String getCentral() {
    return central;
  }

  public void setCentral(String central) {
    this.central = central;
  }



  public Reclamation(Long reclamationid, String ref_reclamation, String description,
      Statusrec status, Servicetype serviceType, String autre, Date date_reclamationtt,
      Date date_etattt, String etattt, Date date_verificationfsi, User treatedBy, String source,
      Date createdDate, Date modifiedDate, User createdby, User editedby, Abonnement client,
      User user, String category, Motifrec motif, String referencett, String gouvernorat,
      String central, List<String> justificatifs, Boolean isEmailSent, String statuttech) {
    super();
    this.reclamationid = reclamationid;
    this.ref_reclamation = ref_reclamation;
    this.description = description;
    this.status = status;
    this.serviceType = serviceType;
    this.autre = autre;
    this.date_reclamationtt = date_reclamationtt;
    this.date_etattt = date_etattt;
    this.etattt = etattt;
    this.date_verificationfsi = date_verificationfsi;
    this.treatedBy = treatedBy;
    this.source = source;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.createdby = createdby;
    this.editedby = editedby;
    this.client = client;
    this.user = user;
    this.category = category;
    this.motif = motif;
    this.referencett = referencett;
    this.gouvernorat = gouvernorat;
    this.central = central;
    this.justificatifs = justificatifs;
    this.isEmailSent = isEmailSent;
    this.statuttech = statuttech;
  }

  public Reclamation(Long reclamationid, String ref_reclamation, String description,
      Statusrec status, Servicetype serviceType, String autre, Date date_reclamationtt,
      Date date_etattt, String etattt, Date date_verificationfsi, User treatedBy, String source,
      Date createdDate, Date modifiedDate, User createdby, User editedby, Abonnement client,
      User user, String category, Motifrec motif, String referencett, String gouvernorat,
      String central, List<String> justificatifs) {
    super();
    this.reclamationid = reclamationid;
    this.ref_reclamation = ref_reclamation;
    this.description = description;
    this.status = status;
    this.serviceType = serviceType;
    this.autre = autre;
    this.date_reclamationtt = date_reclamationtt;
    this.date_etattt = date_etattt;
    this.etattt = etattt;
    this.date_verificationfsi = date_verificationfsi;
    this.treatedBy = treatedBy;
    this.source = source;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.createdby = createdby;
    this.editedby = editedby;
    this.client = client;
    this.user = user;
    this.category = category;
    this.motif = motif;
    this.referencett = referencett;
    this.gouvernorat = gouvernorat;
    this.central = central;
    this.justificatifs = justificatifs;
  }

  @Override
  public String toString() {
    return "Reclamation [reclamationid=" + reclamationid + ", ref_reclamation=" + ref_reclamation
        + ", description=" + description + ", status=" + status + ", serviceType=" + serviceType
        + ", autre=" + autre + ", date_reclamationtt=" + date_reclamationtt + ", date_etattt="
        + date_etattt + ", etattt=" + etattt + ", date_verificationfsi=" + date_verificationfsi
        + ", treatedBy=" + treatedBy + ", source=" + source + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + ", createdby=" + createdby + ", editedby=" + editedby
        + ", client=" + client + ", user=" + user + ", category=" + category + ", motif=" + motif
        + ", referencett=" + referencett + ", gouvernorat=" + gouvernorat + ", central=" + central
        + ", justificatifs=" + justificatifs + ", isEmailSent=" + isEmailSent + ", statuttech="
        + statuttech + "]";
  }



}
