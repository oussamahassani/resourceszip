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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "avoirClient")
@EntityListeners(AuditingEntityListener.class)
public class AvoirClient implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long avoirId;

  // @GeneratorType(type = GenerateSequenceAvoirClient.class, when = GenerationTime.INSERT)
  @Column(unique = true)
  private String refAvoirClient;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate; // now ;

  private Double montantAvoir;

  private Double montantHt;
  private Double montantTva;
  private Double baseTva;
  private Boolean canRevendeurViewed = true;

  private Double timbrefiscale = 0.0;

  @Column(length = 8000)
  private String motifAvoir;


  @Column(length = 8000)
  private String commentaireAvoir;
  private Boolean isClientPayed = false;
  private Boolean has_bordereau = false;
  private Boolean hasRaccordment = false;

  @OneToOne(fetch = FetchType.EAGER)
  private User usedBy;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "abonnementId")
  private Abonnement abonnement;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "creeParId")
  private User creePar;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "avoir_client_id")
  private List<EntryAvoirClient> avoiClientEntry;

  @Temporal(TemporalType.DATE)
  private Date dateDePaiement;

  private Boolean remainsToBePaid = false;
  private Boolean isJestCo = false;
  private Boolean isPublish = false;
  

  private String facture;
  
  private String refReclamation;
  
  @Temporal(TemporalType.DATE)
  private Date dateDebutCoupure;
  @Temporal(TemporalType.DATE)
  private Date dateFinCoupure;
  
  @Temporal(TemporalType.DATE)
  private Date dateMiseService;
  
  
  private  String validePar;
  private  String raisonAannulation;
  
  @OneToOne(fetch = FetchType.EAGER)
  private User publierPar;
  public AvoirClient() {
    super();
    // TODO Auto-generated constructor stub
  }

  public AvoirClient(Long avoirId, String refAvoirClient, Date createdDate, Double montantAvoir,
      Double montantHt, Double montantTva, Double baseTva, Boolean canRevendeurViewed,
      String motifAvoir, Boolean isClientPayed, Boolean has_bordereau, Boolean hasRaccordment,
      User usedBy, Abonnement abonnement, User creePar, List<EntryAvoirClient> avoiClientEntry,
      Date dateDePaiement) {
    super();
    this.avoirId = avoirId;
    this.refAvoirClient = refAvoirClient;
    this.createdDate = createdDate;
    this.montantAvoir = montantAvoir;
    this.montantHt = montantHt;
    this.montantTva = montantTva;
    this.baseTva = baseTva;
    this.canRevendeurViewed = canRevendeurViewed;
    this.motifAvoir = motifAvoir;
    this.isClientPayed = isClientPayed;
    this.has_bordereau = has_bordereau;
    this.hasRaccordment = hasRaccordment;
    this.usedBy = usedBy;
    this.abonnement = abonnement;
    this.creePar = creePar;
    this.avoiClientEntry = avoiClientEntry;
    this.dateDePaiement = dateDePaiement;
  }

  public Long getAvoirId() {
    return avoirId;
  }

  public void setAvoirId(Long avoirId) {
    this.avoirId = avoirId;
  }

  public String getRefAvoirClient() {
    return refAvoirClient;
  }

  public void setRefAvoirClient(String refAvoirClient) {
    this.refAvoirClient = refAvoirClient;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Double getMontantAvoir() {
    return montantAvoir;
  }

  public void setMontantAvoir(Double montantAvoir) {
    this.montantAvoir = montantAvoir;
  }

  public Double getMontantHt() {
    return montantHt;
  }

  public void setMontantHt(Double montantHt) {
    this.montantHt = montantHt;
  }

  public Double getMontantTva() {
    return montantTva;
  }

  public void setMontantTva(Double montantTva) {
    this.montantTva = montantTva;
  }

  public Double getBaseTva() {
    return baseTva;
  }

  public void setBaseTva(Double baseTva) {
    this.baseTva = baseTva;
  }

  public Boolean getCanRevendeurViewed() {
    return canRevendeurViewed;
  }

  public void setCanRevendeurViewed(Boolean canRevendeurViewed) {
    this.canRevendeurViewed = canRevendeurViewed;
  }

  public String getMotifAvoir() {
    return motifAvoir;
  }

  public void setMotifAvoir(String motifAvoir) {
    this.motifAvoir = motifAvoir;
  }

  public Boolean getIsClientPayed() {
    return isClientPayed;
  }

  public void setIsClientPayed(Boolean isClientPayed) {
    this.isClientPayed = isClientPayed;
  }

  public Boolean getHas_bordereau() {
    return has_bordereau;
  }

  public void setHas_bordereau(Boolean has_bordereau) {
    this.has_bordereau = has_bordereau;
  }

  public Boolean getHasRaccordment() {
    return hasRaccordment;
  }

  public void setHasRaccordment(Boolean hasRaccordment) {
    this.hasRaccordment = hasRaccordment;
  }

  public User getUsedBy() {
    return usedBy;
  }

  public void setUsedBy(User usedBy) {
    this.usedBy = usedBy;
  }

  public Abonnement getAbonnement() {
    return abonnement;
  }

  public void setAbonnement(Abonnement abonnement) {
    this.abonnement = abonnement;
  }

  public User getCreePar() {
    return creePar;
  }

  public void setCreePar(User creePar) {
    this.creePar = creePar;
  }

  public List<EntryAvoirClient> getAvoiClientEntry() {
    return avoiClientEntry;
  }

  public void setAvoiClientEntry(List<EntryAvoirClient> avoiClientEntry) {
    this.avoiClientEntry = avoiClientEntry;
  }

  public Date getDateDePaiement() {
    return dateDePaiement;
  }

  public void setDateDePaiement(Date dateDePaiement) {
    this.dateDePaiement = dateDePaiement;
  }


  public Double getTimbrefiscale() {
    return timbrefiscale;
  }

  public void setTimbrefiscale(Double timbrefiscale) {
    this.timbrefiscale = timbrefiscale;
  }

  public Boolean getRemainsToBePaid() {
    return remainsToBePaid;
  }

  public void setRemainsToBePaid(Boolean remainsToBePaid) {
    this.remainsToBePaid = remainsToBePaid;
  }

  public String getCommentaireAvoir() {
    return commentaireAvoir;
  }

  public void setCommentaireAvoir(String commentaireAvoir) {
    this.commentaireAvoir = commentaireAvoir;
  }

  public Boolean getIsJestCo() {
	return isJestCo;
}

public void setIsJestCo(Boolean isJestCo) {
	this.isJestCo = isJestCo;
}

public Boolean getIsPublish() {
	return isPublish;
}

public void setIsPublish(Boolean isPublish) {
	this.isPublish = isPublish;
}


public String getFacture() {
	return facture;
}

public void setFacture(String facture) {
	this.facture = facture;
}

public String getRefReclamation() {
	return refReclamation;
}

public void setRefReclamation(String refReclamation) {
	this.refReclamation = refReclamation;
}

public Date getDateDebutCoupure() {
	return dateDebutCoupure;
}

public void setDateDebutCoupure(Date dateDebutCoupure) {
	this.dateDebutCoupure = dateDebutCoupure;
}

public Date getDateFinCoupure() {
	return dateFinCoupure;
}

public void setDateFinCoupure(Date dateFinCoupure) {
	this.dateFinCoupure = dateFinCoupure;
}



public Date getDateMiseService() {
	return dateMiseService;
}

public void setDateMiseService(Date dateMiseService) {
	this.dateMiseService = dateMiseService;
}

public String getValidePar() {
	return validePar;
}

public void setValidePar(String validePar) {
	this.validePar = validePar;
}

public String getRaisonAannulation() {
	return raisonAannulation;
}

public void setRaisonAannulation(String raisonAannulation) {
	this.raisonAannulation = raisonAannulation;
}

public User getPublierPar() {
	return publierPar;
}

public void setPublierPar(User publierPar) {
	this.publierPar = publierPar;
}

@Override
public String toString() {
	return String.format(
			"AvoirClient [avoirId=%s, refAvoirClient=%s, createdDate=%s, montantAvoir=%s, montantHt=%s, montantTva=%s, baseTva=%s, canRevendeurViewed=%s, timbrefiscale=%s, motifAvoir=%s, commentaireAvoir=%s, isClientPayed=%s, has_bordereau=%s, hasRaccordment=%s, usedBy=%s, abonnement=%s, creePar=%s, avoiClientEntry=%s, dateDePaiement=%s, remainsToBePaid=%s, isJestCo=%s, isPublish=%s, facture=%s, refReclamation=%s, dateDebutCoupure=%s, dateFinCoupure=%s, dateMiseService=%s, validePar=%s, raisonAannulation=%s, publierPar=%s]",
			avoirId, refAvoirClient, createdDate, montantAvoir, montantHt, montantTva, baseTva, canRevendeurViewed,
			timbrefiscale, motifAvoir, commentaireAvoir, isClientPayed, has_bordereau, hasRaccordment, usedBy,
			abonnement, creePar, avoiClientEntry, dateDePaiement, remainsToBePaid, isJestCo, isPublish, facture,
			refReclamation, dateDebutCoupure, dateFinCoupure, dateMiseService, validePar, raisonAannulation,
			publierPar);
}



}
