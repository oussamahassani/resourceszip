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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "factures")
@EntityListeners(AuditingEntityListener.class)
public class Facture implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long factureId;

  // @GeneratorType(type = GenerateSequenceFacture.class, when = GenerationTime.INSERT)
  // @Column(unique = true)
  private String ref_facture;

  private Double montant_payer;

  private Double montantHt;
  private Double montantTva;
  private Double prixBaseTva;

  // private Double tauxTva;
  private Double timbrefiscale;

  private Boolean etat_facture = false;
  private Boolean visibility = false;
  private Boolean isDelete = false;
  private Boolean isFirstFacture = false;
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Temporal(TemporalType.DATE)
  private Date date_echeance;

  @Temporal(TemporalType.DATE)
  private Date dateDeFin;

  @Temporal(TemporalType.DATE)
  private Date dateDeDebut;

  // @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userid")
  private User user;

  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "commandeid")
  private Commande commande;

  @Column(name = "remise")
  private Double remise;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "clientid")
  private Abonnement abonnement;
  // targetEntity = EntryFactures.class,
  @JsonIgnore
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "factureId")
  private List<EntryFactures> entriesFacture;

  private Boolean firstReminder = false;
  private Boolean secondReminder = false;
  private Boolean suspensionServices = false;
  private Boolean thirdReminderReactivate = false;

  @Temporal(TemporalType.TIMESTAMP)
  private Date dateDePayement;

  @Temporal(TemporalType.DATE)
  private Date dateDeVersement;
  private Boolean isFactureResilation = false;

  private Boolean isCommisionSaved = false;

  private Boolean isProformat = false;

private  String refFactQAD;
private String codeTTN;
  public Facture() {}

  public Facture(Long factureId, String ref_facture, Double montant_payer, Double montantHt,
      Double montantTva, Double prixBaseTva, Double timbrefiscale, Boolean etat_facture,
      Boolean visibility, Boolean isDelete, Boolean isFirstFacture, Date createdDate,
      Date modifiedDate, Date date_echeance, Date dateDeFin, Date dateDeDebut, User user,
      Commande commande, Double remise, Abonnement abonnement, List<EntryFactures> entriesFacture) {
    super();
    this.factureId = factureId;
    this.ref_facture = ref_facture;
    this.montant_payer = montant_payer;
    this.montantHt = montantHt;
    this.montantTva = montantTva;
    this.prixBaseTva = prixBaseTva;
    this.timbrefiscale = timbrefiscale;
    this.etat_facture = etat_facture;
    this.visibility = visibility;
    this.isDelete = isDelete;
    this.isFirstFacture = isFirstFacture;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.date_echeance = date_echeance;
    this.dateDeFin = dateDeFin;
    this.dateDeDebut = dateDeDebut;
    this.user = user;
    this.commande = commande;
    this.remise = remise;
    this.abonnement = abonnement;
    this.entriesFacture = entriesFacture;
  }

  /**
   * @return the factureId
   */
  public Long getFactureId() {
    return factureId;
  }

  /**
   * @param factureId the factureId to set
   */
  public void setFactureId(Long factureId) {
    this.factureId = factureId;
  }

  /**
   * @return the ref_facture
   */
  public String getRef_facture() {
    return ref_facture;
  }

  /**
   * @param ref_facture the ref_facture to set
   */
  public void setRef_facture(String ref_facture) {
    this.ref_facture = ref_facture;
  }

  /**
   * @return the montant_payer
   */
  public Double getMontant_payer() {
    return montant_payer;
  }

  /**
   * @param montant_payer the montant_payer to set
   */
  public void setMontant_payer(Double montant_payer) {
    this.montant_payer = montant_payer;
  }

  /**
   * @return the montantHt
   */
  public Double getMontantHt() {
    return montantHt;
  }

  /**
   * @param montantHt the montantHt to set
   */
  public void setMontantHt(Double montantHt) {
    this.montantHt = montantHt;
  }

  /**
   * @return the montantTva
   */
  public Double getMontantTva() {
    return montantTva;
  }

  /**
   * @param montantTva the montantTva to set
   */
  public void setMontantTva(Double montantTva) {
    this.montantTva = montantTva;
  }

  /**
   * @return the prixBaseTva
   */
  public Double getPrixBaseTva() {
    return prixBaseTva;
  }

  /**
   * @param prixBaseTva the prixBaseTva to set
   */
  public void setPrixBaseTva(Double prixBaseTva) {
    this.prixBaseTva = prixBaseTva;
  }

  /**
   * @return the timbrefiscale
   */
  public Double getTimbrefiscale() {
    return timbrefiscale;
  }

  /**
   * @param timbrefiscale the timbrefiscale to set
   */
  public void setTimbrefiscale(Double timbrefiscale) {
    this.timbrefiscale = timbrefiscale;
  }

  /**
   * @return the etat_facture
   */
  public Boolean getEtat_facture() {
    return etat_facture;
  }

  /**
   * @param etat_facture the etat_facture to set
   */
  public void setEtat_facture(Boolean etat_facture) {
    this.etat_facture = etat_facture;
  }

  /**
   * @return the visibility
   */
  public Boolean getVisibility() {
    return visibility;
  }

  /**
   * @param visibility the visibility to set
   */
  public void setVisibility(Boolean visibility) {
    this.visibility = visibility;
  }

  /**
   * @return the isDelete
   */
  public Boolean getIsDelete() {
    return isDelete;
  }

  /**
   * @param isDelete the isDelete to set
   */
  public void setIsDelete(Boolean isDelete) {
    this.isDelete = isDelete;
  }

  /**
   * @return the isFirstFacture
   */
  public Boolean getIsFirstFacture() {
    return isFirstFacture;
  }

  /**
   * @param isFirstFacture the isFirstFacture to set
   */
  public void setIsFirstFacture(Boolean isFirstFacture) {
    this.isFirstFacture = isFirstFacture;
  }

  /**
   * @return the createdDate
   */
  public Date getCreatedDate() {
    return createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return the modifiedDate
   */
  public Date getModifiedDate() {
    return modifiedDate;
  }

  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return the date_echeance
   */
  public Date getDate_echeance() {
    return date_echeance;
  }

  /**
   * @param date_echeance the date_echeance to set
   */
  public void setDate_echeance(Date date_echeance) {
    this.date_echeance = date_echeance;
  }

  /**
   * @return the dateDeFin
   */
  public Date getDateDeFin() {
    return dateDeFin;
  }

  /**
   * @param dateDeFin the dateDeFin to set
   */
  public void setDateDeFin(Date dateDeFin) {
    this.dateDeFin = dateDeFin;
  }

  /**
   * @return the dateDeDebut
   */
  public Date getDateDeDebut() {
    return dateDeDebut;
  }

  /**
   * @param dateDeDebut the dateDeDebut to set
   */
  public void setDateDeDebut(Date dateDeDebut) {
    this.dateDeDebut = dateDeDebut;
  }

  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * @return the commande
   */
  public Commande getCommande() {
    return commande;
  }

  /**
   * @param commande the commande to set
   */
  public void setCommande(Commande commande) {
    this.commande = commande;
  }

  /**
   * @return the remise
   */
  public Double getRemise() {
    return remise;
  }

  /**
   * @param remise the remise to set
   */
  public void setRemise(Double remise) {
    this.remise = remise;
  }

  /**
   * @return the abonnement
   */
  public Abonnement getAbonnement() {
    return abonnement;
  }

  /**
   * @param abonnement the abonnement to set
   */
  public void setAbonnement(Abonnement abonnement) {
    this.abonnement = abonnement;
  }

  /**
   * @return the entriesFacture
   */
  public List<EntryFactures> getEntriesFacture() {
    return entriesFacture;
  }

  /**
   * @param entriesFacture the entriesFacture to set
   */
  public void setEntriesFacture(List<EntryFactures> entriesFacture) {
    this.entriesFacture = entriesFacture;
  }

  public boolean isFirstReminder() {
    return firstReminder;
  }

  public void setFirstReminder(boolean firstReminder) {
    this.firstReminder = firstReminder;
  }

  public boolean isSecondReminder() {
    return secondReminder;
  }

  public void setSecondReminder(boolean secondReminder) {
    this.secondReminder = secondReminder;
  }

  public boolean isSuspensionServices() {
    return suspensionServices;
  }

  public void setSuspensionServices(boolean suspensionServices) {
    this.suspensionServices = suspensionServices;
  }

  public boolean isThirdReminderReactivate() {
    return thirdReminderReactivate;
  }

  public void setThirdReminderReactivate(boolean thirdReminderReactivate) {
    this.thirdReminderReactivate = thirdReminderReactivate;
  }

  public Date getDateDePayement() {
    return dateDePayement;
  }

  public void setDateDePayement(Date dateDePayement) {
    this.dateDePayement = dateDePayement;
  }

  public Date getDateDeVersement() {
    return dateDeVersement;
  }

  public void setDateDeVersement(Date dateDeVersement) {
    this.dateDeVersement = dateDeVersement;
  }

  public Boolean getIsFactureResilation() {
    return isFactureResilation;
  }

  public void setIsFactureResilation(Boolean isFactureResilation) {
    this.isFactureResilation = isFactureResilation;
  }


  public Boolean getIsCommisionSaved() {
    return isCommisionSaved;
  }

  public void setIsCommisionSaved(Boolean isCommisionSaved) {
    this.isCommisionSaved = isCommisionSaved;
  }

  public Boolean getIsProformat() {
    return isProformat;
  }

  public void setIsProformat(Boolean isProformat) {
    this.isProformat = isProformat;

  }

  public Boolean getFirstReminder() {
	return firstReminder;
}

public void setFirstReminder(Boolean firstReminder) {
	this.firstReminder = firstReminder;
}

public Boolean getSecondReminder() {
	return secondReminder;
}

public void setSecondReminder(Boolean secondReminder) {
	this.secondReminder = secondReminder;
}

public Boolean getSuspensionServices() {
	return suspensionServices;
}

public void setSuspensionServices(Boolean suspensionServices) {
	this.suspensionServices = suspensionServices;
}

public Boolean getThirdReminderReactivate() {
	return thirdReminderReactivate;
}

public void setThirdReminderReactivate(Boolean thirdReminderReactivate) {
	this.thirdReminderReactivate = thirdReminderReactivate;
}

public String getRefFactQAD() {
	return refFactQAD;
}

public void setRefFactQAD(String refFactQAD) {
	this.refFactQAD = refFactQAD;
}

public String getCodeTTN() {
	return codeTTN;
}

public void setCodeTTN(String codeTTN) {
	this.codeTTN = codeTTN;
}

@Override
public String toString() {
	return String.format(
			"Facture [factureId=%s, ref_facture=%s, montant_payer=%s, montantHt=%s, montantTva=%s, prixBaseTva=%s, timbrefiscale=%s, etat_facture=%s, visibility=%s, isDelete=%s, isFirstFacture=%s, createdDate=%s, modifiedDate=%s, date_echeance=%s, dateDeFin=%s, dateDeDebut=%s, user=%s, commande=%s, remise=%s, abonnement=%s, entriesFacture=%s, firstReminder=%s, secondReminder=%s, suspensionServices=%s, thirdReminderReactivate=%s, dateDePayement=%s, dateDeVersement=%s, isFactureResilation=%s, isCommisionSaved=%s, isProformat=%s, refFactQAD=%s, codeTTN=%s]",
			factureId, ref_facture, montant_payer, montantHt, montantTva, prixBaseTva, timbrefiscale, etat_facture,
			visibility, isDelete, isFirstFacture, createdDate, modifiedDate, date_echeance, dateDeFin, dateDeDebut,
			user, commande, remise, abonnement, entriesFacture, firstReminder, secondReminder, suspensionServices,
			thirdReminderReactivate, dateDePayement, dateDeVersement, isFactureResilation, isCommisionSaved,
			isProformat, refFactQAD, codeTTN);
}


}
