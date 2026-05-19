package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import crm.chifco.com.service.GenerateSequenceCommission;

@Entity
@Table(name = "commission")
@EntityListeners(AuditingEntityListener.class)
public class Commission implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Integer annee;

  private Integer mois;

  @ManyToOne
  private User revendeur;

  @ManyToOne
  private User cancelledBy;

  private Date cancelledDate;

  @GeneratorType(type = GenerateSequenceCommission.class, when = GenerationTime.INSERT)
  @Column(unique = true)
  private String refCommission;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  // Commision des demande
  private Integer nbrTotalDemandes;
  private Integer nbrDemandesAcceptees;
  private Integer nbrDemandesRejetees;
  private Integer nbrDemandesEnAttente;
  private Integer nbrDemandesNonRealisee;
  private Double montantCommissionDemandes;

//Commision Mise en service
 private Integer nbrFactureMiseService;
 private Double montantCommissionPremiereFactureMiseService;


  // Commission premiereFacture
  private Integer nbrDemandesActivees;
  private Double montantCommissionPremiereFacture;
  
  private Double totalCommissionsActivationNewFreelance =0.0;
  
  private Double montantAvancePremiereFacture;
  private Double montantTotalPremiereFacture;

//retard payement commision premiere facture
 private Double montantRetardPayemnt = 0.0 ;



  // Commission des paiements
  private Integer nbrFacturesPayees;
  private Integer nbrFacturesNonVerseePayement;
  private Double montantCommissionPaiements;

  // total commission
  private Double totalHt;
  private Integer tva;
  private Double montantTva;
  private Double totalTtc;

  private String statut;
  private Boolean isPromo = false;
  private Boolean isFreelance = false;
  @Temporal(TemporalType.DATE)
  private Date periodPromoDebut;
  @Temporal(TemporalType.DATE)
  private Date periodPromoFin;

  private Double retunuSource;
  private Double primeCommision = 0.0;


  public Commission() {
    super();
  }



  public Commission(Long id, Integer annee, Integer mois, User revendeur, User cancelledBy,
      Date cancelledDate, String refCommission, Date createdDate, Date modifiedDate,
      Integer nbrTotalDemandes, Integer nbrDemandesAcceptees, Integer nbrDemandesRejetees,
      Integer nbrDemandesEnAttente, Integer nbrDemandesNonRealisee,
      Double montantCommissionDemandes, Integer nbrDemandesActivees,
      Double montantCommissionPremiereFacture, Integer nbrFacturesPayees,
      Integer nbrFacturesNonVerseePayement, Double montantCommissionPaiements, Double totalHt,
      Integer tva, Double montantTva, Double totalTtc, String statut, Boolean isPromo,
      Date periodPromoDebut, Date periodPromoFin) {
    super();
    this.id = id;
    this.annee = annee;
    this.mois = mois;
    this.revendeur = revendeur;
    this.cancelledBy = cancelledBy;
    this.cancelledDate = cancelledDate;
    this.refCommission = refCommission;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.nbrTotalDemandes = nbrTotalDemandes;
    this.nbrDemandesAcceptees = nbrDemandesAcceptees;
    this.nbrDemandesRejetees = nbrDemandesRejetees;
    this.nbrDemandesEnAttente = nbrDemandesEnAttente;
    this.nbrDemandesNonRealisee = nbrDemandesNonRealisee;
    this.montantCommissionDemandes = montantCommissionDemandes;
    this.nbrDemandesActivees = nbrDemandesActivees;
    this.montantCommissionPremiereFacture = montantCommissionPremiereFacture;
    this.nbrFacturesPayees = nbrFacturesPayees;
    this.nbrFacturesNonVerseePayement = nbrFacturesNonVerseePayement;
    this.montantCommissionPaiements = montantCommissionPaiements;
    this.totalHt = totalHt;
    this.tva = tva;
    this.montantTva = montantTva;
    this.totalTtc = totalTtc;
    this.statut = statut;
    this.isPromo = isPromo;
    this.periodPromoDebut = periodPromoDebut;
    this.periodPromoFin = periodPromoFin;
  }



  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getAnnee() {
    return annee;
  }

  public void setAnnee(Integer annee) {
    this.annee = annee;
  }

  public Integer getMois() {
    return mois;
  }

  public void setMois(Integer mois) {
    this.mois = mois;
  }

  public User getRevendeur() {
    return revendeur;
  }

  public void setRevendeur(User revendeur) {
    this.revendeur = revendeur;
  }

  public User getCancelledBy() {
    return cancelledBy;
  }

  public void setCancelledBy(User cancelledBy) {
    this.cancelledBy = cancelledBy;
  }

  public Date getCancelledDate() {
    return cancelledDate;
  }

  public void setCancelledDate(Date cancelledDate) {
    this.cancelledDate = cancelledDate;
  }

  public String getRefCommission() {
    return refCommission;
  }

  public void setRefCommission(String refCommission) {
    this.refCommission = refCommission;
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

  public Integer getNbrTotalDemandes() {
    return nbrTotalDemandes;
  }

  public void setNbrTotalDemandes(Integer nbrTotalDemandes) {
    this.nbrTotalDemandes = nbrTotalDemandes;
  }

  public Integer getNbrDemandesAcceptees() {
    return nbrDemandesAcceptees;
  }

  public void setNbrDemandesAcceptees(Integer nbrDemandesAcceptees) {
    this.nbrDemandesAcceptees = nbrDemandesAcceptees;
  }

  public Integer getNbrDemandesRejetees() {
    return nbrDemandesRejetees;
  }

  public void setNbrDemandesRejetees(Integer nbrDemandesRejetees) {
    this.nbrDemandesRejetees = nbrDemandesRejetees;
  }

  public Integer getNbrDemandesEnAttente() {
    return nbrDemandesEnAttente;
  }

  public void setNbrDemandesEnAttente(Integer nbrDemandesEnAttente) {
    this.nbrDemandesEnAttente = nbrDemandesEnAttente;
  }

  public Integer getNbrDemandesNonRealisee() {
    return nbrDemandesNonRealisee;
  }

  public void setNbrDemandesNonRealisee(Integer nbrDemandesNonRealisee) {
    this.nbrDemandesNonRealisee = nbrDemandesNonRealisee;
  }

  public Double getMontantCommissionDemandes() {
    return montantCommissionDemandes;
  }

  public void setMontantCommissionDemandes(Double montantCommissionDemandes) {
    this.montantCommissionDemandes = montantCommissionDemandes;
  }

  public Integer getNbrDemandesActivees() {
    return nbrDemandesActivees;
  }

  public void setNbrDemandesActivees(Integer nbrDemandesActivees) {
    this.nbrDemandesActivees = nbrDemandesActivees;
  }

  public Double getMontantCommissionPremiereFacture() {
    return montantCommissionPremiereFacture;
  }

  public void setMontantCommissionPremiereFacture(Double montantCommissionPremiereFacture) {
    this.montantCommissionPremiereFacture = montantCommissionPremiereFacture;
  }

  public Integer getNbrFacturesPayees() {
    return nbrFacturesPayees;
  }

  public void setNbrFacturesPayees(Integer nbrFacturesPayees) {
    this.nbrFacturesPayees = nbrFacturesPayees;
  }

  public Integer getNbrFacturesNonVerseePayement() {
    return nbrFacturesNonVerseePayement;
  }

  public void setNbrFacturesNonVerseePayement(Integer nbrFacturesNonVerseePayement) {
    this.nbrFacturesNonVerseePayement = nbrFacturesNonVerseePayement;
  }

  public Double getMontantCommissionPaiements() {
    return montantCommissionPaiements;
  }

  public void setMontantCommissionPaiements(Double montantCommissionPaiements) {
    this.montantCommissionPaiements = montantCommissionPaiements;
  }

  public Double getTotalHt() {
    return totalHt;
  }

  public void setTotalHt(Double totalHt) {
    this.totalHt = totalHt;
  }

  public Integer getTva() {
    return tva;
  }

  public void setTva(Integer tva) {
    this.tva = tva;
  }

  public Double getMontantTva() {
    return montantTva;
  }

  public void setMontantTva(Double montantTva) {
    this.montantTva = montantTva;
  }

  public Double getTotalTtc() {
    return totalTtc;
  }

  public void setTotalTtc(Double totalTtc) {
    this.totalTtc = totalTtc;
  }

  public String getStatut() {
    return statut;
  }

  public void setStatut(String statut) {
    this.statut = statut;
  }

  public Boolean getIsPromo() {
    return isPromo;
  }

  public void setIsPromo(Boolean isPromo) {
    this.isPromo = isPromo;
  }

  public Date getPeriodPromoDebut() {
    return periodPromoDebut;
  }

  public void setPeriodPromoDebut(Date periodPromoDebut) {
    this.periodPromoDebut = periodPromoDebut;
  }

  public Date getPeriodPromoFin() {
    return periodPromoFin;
  }

  public void setPeriodPromoFin(Date periodPromoFin) {
    this.periodPromoFin = periodPromoFin;
  }

  public Double getMontantAvancePremiereFacture() {
    return montantAvancePremiereFacture;
  }



  public void setMontantAvancePremiereFacture(Double montantAvancePremiereFacture) {
    this.montantAvancePremiereFacture = montantAvancePremiereFacture;
  }



  public Double getMontantTotalPremiereFacture() {
    return montantTotalPremiereFacture;
  }



  public void setMontantTotalPremiereFacture(Double montantTotalPremiereFacture) {
    this.montantTotalPremiereFacture = montantTotalPremiereFacture;
  }



  public Double getRetunuSource() {
    return retunuSource;
  }



  public void setRetunuSource(Double retunuSource) {
    this.retunuSource = retunuSource;
  }




public Integer getNbrFactureMiseService() {
	return nbrFactureMiseService;
}



public void setNbrFactureMiseService(Integer nbrFactureMiseService) {
	this.nbrFactureMiseService = nbrFactureMiseService;
}



public Double getMontantCommissionPremiereFactureMiseService() {
	return montantCommissionPremiereFactureMiseService;
}



public void setMontantCommissionPremiereFactureMiseService(Double montantCommissionPremiereFactureMiseService) {
	this.montantCommissionPremiereFactureMiseService = montantCommissionPremiereFactureMiseService;
}





  public Double getMontantRetardPayemnt() {

	return montantRetardPayemnt;
}



public void setMontantRetardPayemnt(Double montantRetardPayemnt) {
	this.montantRetardPayemnt = montantRetardPayemnt;
}




public Boolean getIsFreelance() {
	return isFreelance;
}



public void setIsFreelance(Boolean isFreelance) {
	this.isFreelance = isFreelance;
}



public Double getPrimeCommision() {
	return primeCommision;
}



public void setPrimeCommision(Double primeCommision) {
	this.primeCommision = primeCommision;
}



public Double getTotalCommissionsActivationNewFreelance() {
	return totalCommissionsActivationNewFreelance;
}



public void setTotalCommissionsActivationNewFreelance(Double totalCommissionsActivationNewFreelance) {
	this.totalCommissionsActivationNewFreelance = totalCommissionsActivationNewFreelance;
}



@Override
public String toString() {
	return String.format(
			"Commission [id=%s, annee=%s, mois=%s, revendeur=%s, cancelledBy=%s, cancelledDate=%s, refCommission=%s, createdDate=%s, modifiedDate=%s, nbrTotalDemandes=%s, nbrDemandesAcceptees=%s, nbrDemandesRejetees=%s, nbrDemandesEnAttente=%s, nbrDemandesNonRealisee=%s, montantCommissionDemandes=%s, nbrFactureMiseService=%s, montantCommissionPremiereFactureMiseService=%s, nbrDemandesActivees=%s, montantCommissionPremiereFacture=%s, totalCommissionsActivationNewFreelance=%s, montantAvancePremiereFacture=%s, montantTotalPremiereFacture=%s, montantRetardPayemnt=%s, nbrFacturesPayees=%s, nbrFacturesNonVerseePayement=%s, montantCommissionPaiements=%s, totalHt=%s, tva=%s, montantTva=%s, totalTtc=%s, statut=%s, isPromo=%s, isFreelance=%s, periodPromoDebut=%s, periodPromoFin=%s, retunuSource=%s, primeCommision=%s]",
			id, annee, mois, revendeur, cancelledBy, cancelledDate, refCommission, createdDate, modifiedDate,
			nbrTotalDemandes, nbrDemandesAcceptees, nbrDemandesRejetees, nbrDemandesEnAttente, nbrDemandesNonRealisee,
			montantCommissionDemandes, nbrFactureMiseService, montantCommissionPremiereFactureMiseService,
			nbrDemandesActivees, montantCommissionPremiereFacture, totalCommissionsActivationNewFreelance,
			montantAvancePremiereFacture, montantTotalPremiereFacture, montantRetardPayemnt, nbrFacturesPayees,
			nbrFacturesNonVerseePayement, montantCommissionPaiements, totalHt, tva, montantTva, totalTtc, statut,
			isPromo, isFreelance, periodPromoDebut, periodPromoFin, retunuSource, primeCommision);
}




}
