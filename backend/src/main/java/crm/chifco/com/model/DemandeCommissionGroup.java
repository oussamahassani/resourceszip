package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "DemandeCommissionGroup")
@EntityListeners(AuditingEntityListener.class)
public class DemandeCommissionGroup implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String refGroup; // Reference starts with DPM



 @OneToMany(fetch = FetchType.LAZY)
 @JoinColumn(name = "groupe_id")
 private List<DemandeCommission> demandeCommissions;
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date dateDecision;

  private String commentaire;

  private String motifRejet;

  private String statut; // IN_PROGRESS, AWAINTING_INVOICING, PAID, REJECTED

  private String invoiceFilePath; // Path to the uploaded PDF invoice

  private String decisionFilePath; // Path to the uploaded decision file

  private String decisionFileName; // Name of the uploaded decision file

  private Long createdByUserId;

  private Long validatedByUserId;

  public DemandeCommissionGroup() {
    super();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getRefGroup() {
    return refGroup;
  }

  public void setRefGroup(String refGroup) {
    this.refGroup = refGroup;
  }

  public List<DemandeCommission> getDemandeCommissions() {
    return demandeCommissions;
  }

  public void setDemandeCommissions(List<DemandeCommission> demandeCommissions) {
    this.demandeCommissions = demandeCommissions;
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

  public Date getDateDecision() {
    return dateDecision;
  }

  public void setDateDecision(Date dateDecision) {
    this.dateDecision = dateDecision;
  }

  public String getCommentaire() {
    return commentaire;
  }

  public void setCommentaire(String commentaire) {
    this.commentaire = commentaire;
  }

  public String getMotifRejet() {
    return motifRejet;
  }

  public void setMotifRejet(String motifRejet) {
    this.motifRejet = motifRejet;
  }

  public String getStatut() {
    return statut;
  }

  public void setStatut(String statut) {
    this.statut = statut;
  }

  public Long getCreatedByUserId() {
    return createdByUserId;
  }

  public void setCreatedByUserId(Long createdByUserId) {
    this.createdByUserId = createdByUserId;
  }

  public Long getValidatedByUserId() {
    return validatedByUserId;
  }

  public void setValidatedByUserId(Long validatedByUserId) {
    this.validatedByUserId = validatedByUserId;
  }

  public String getInvoiceFilePath() {
    return invoiceFilePath;
  }

  public void setInvoiceFilePath(String invoiceFilePath) {
    this.invoiceFilePath = invoiceFilePath;
  }

  public String getDecisionFilePath() {
    return decisionFilePath;
  }

  public void setDecisionFilePath(String decisionFilePath) {
    this.decisionFilePath = decisionFilePath;
  }

  public String getDecisionFileName() {
    return decisionFileName;
  }

  public void setDecisionFileName(String decisionFileName) {
    this.decisionFileName = decisionFileName;
  }
}