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
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import crm.chifco.com.service.GenerateSequenceClient;

@Entity
@Table(name = "demandesabonnement")
@EntityListeners(AuditingEntityListener.class)
public class DemandeAbonnement implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "demandeid")
  private Long demandeId;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "statut_id")
  private Statut statut;

  @Column(length = 75)
  private String firstName;

  @Column(length = 80)
  private String lastName;

  @Column(length = 115)
  private String email;

  @Column(length = 115)
  private String cin;

  @Column(length = 115)
  private String adresse;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "gouvernoratId")
  private Gouvernorat gouvernorat;

  @OneToOne
  @JoinColumn(name = "code_postale")
  private PostalCode codePostale;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "villeId")
  private Ville ville;

  @Column(length = 80)
  private Long telFixe;

  @Column(length = 80)
  private Long telMobile;

  @Column(length = 80)
  private Long telMobile2;

  @Column(length = 205)
  private String photoCin1;

  @Column(length = 205)
  private String photoCin2;

  // @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;

  @JsonIgnore
  @OneToOne()
  @JoinColumn(name = "categorie_produit_internet_id")
  private CategorieProduitInternet categorieProduitInternet;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profession_id")
  private Profession profession;

  @Column(length = 80)
  private Long fax;

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

  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modem_id")
  private Modem modem;

  @GeneratorType(type = GenerateSequenceClient.class, when = GenerationTime.INSERT)
  @Column(name = "reference_chifco", unique = true, nullable = false, updatable = false)
  private String referenceChifco;

  @Column(name = "referenceTT", length = 80)

  private String referenceTT;

  @Column(length = 80)
  private String positionxy;

  @Temporal(TemporalType.DATE)
  private Date dateNaissance;

  @OneToOne()
  @JoinColumn(name = "type_de_paiement_id")
  private Typepaiement typePaiement;

  @Column(length = 80)
  private String etatTT;

  private String dateFinContrat;

  private String dateDesactivation;

  private Boolean isActive = false;

  private Boolean hasRaccordement = true;

  private Integer nbFaisApayeReccardement = 0;

  private Boolean withIpFix = false;
  //////

  private Boolean proprietaire;

  private String motifRefus;

  @Temporal(TemporalType.DATE)
  private Date dateDecisionDemande;

  @OneToOne()
  private ClassificationDemande decisionDemande;

  @Temporal(TemporalType.DATE)
  private Date dateDeMiseEnService;


  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "demandeId")
  private List<EntryDemandeAbonnement> entriesDemandeAbonnement;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "packId")
  private Pack pack;

  private String situationFamiliale;

  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignedTo")
  private User assignedTo;

  @JoinColumn(name = "editedBy")
  private Long editedBy;

  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "treatedBy")
  private User treatedBy;

  private Boolean isSmsVerification = false;

  private Boolean houseHolder;

  private Boolean hasBankCard;

  private Boolean isSmsSend = false;

  private Long demandeAbonnementParents;

  private Boolean isCommisionSaved = false;
  private String origin;
  private String typeAbonnment;
  public DemandeAbonnement() {}

  public DemandeAbonnement(Long demandeId, Statut statut, String firstName, String lastName,
      String email, String cin, String adresse, Gouvernorat gouvernorat, PostalCode codePostale,
      Ville ville, Long telFixe, Long telMobile, Long telMobile2, String photoCin1,
      String photoCin2, User user, CategorieProduitInternet categorieProduitInternet,
      Profession profession, Long fax, String demandePdf, String contratPdf, Date createdDate,
      Date modifiedDate, Modem modem, String referenceChifco, String referenceTT, String positionxy,
      Date dateNaissance, Typepaiement typePaiement, String etatTT, String dateFinContrat,
      String dateDesactivation, Boolean isActive, Boolean hasRaccordement,
      Integer nbFaisApayeReccardement, Boolean withIpFix, Boolean proprietaire, String motifRefus,
      Date dateDecisionDemande, ClassificationDemande decisionDemande,
      List<EntryDemandeAbonnement> entriesDemandeAbonnement, Pack pack, String situationFamiliale) {
    super();
    this.demandeId = demandeId;
    this.statut = statut;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.cin = cin;
    this.adresse = adresse;
    this.gouvernorat = gouvernorat;
    this.codePostale = codePostale;
    this.ville = ville;
    this.telFixe = telFixe;
    this.telMobile = telMobile;
    this.telMobile2 = telMobile2;
    this.photoCin1 = photoCin1;
    this.photoCin2 = photoCin2;
    this.user = user;
    this.categorieProduitInternet = categorieProduitInternet;
    this.profession = profession;
    this.fax = fax;
    this.demandePdf = demandePdf;
    this.contratPdf = contratPdf;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.modem = modem;
    this.referenceChifco = referenceChifco;
    this.referenceTT = referenceTT;
    this.positionxy = positionxy;
    this.dateNaissance = dateNaissance;
    this.typePaiement = typePaiement;
    this.etatTT = etatTT;
    this.dateFinContrat = dateFinContrat;
    this.dateDesactivation = dateDesactivation;
    this.isActive = isActive;
    this.hasRaccordement = hasRaccordement;
    this.nbFaisApayeReccardement = nbFaisApayeReccardement;
    this.withIpFix = withIpFix;
    this.proprietaire = proprietaire;
    this.motifRefus = motifRefus;
    this.dateDecisionDemande = dateDecisionDemande;
    this.decisionDemande = decisionDemande;
    this.entriesDemandeAbonnement = entriesDemandeAbonnement;
    this.pack = pack;
    this.situationFamiliale = situationFamiliale;
  }

  public Long getDemandeId() {
    return demandeId;
  }

  public void setDemandeId(Long demandeId) {
    this.demandeId = demandeId;
  }

  public Statut getStatut() {
    return statut;
  }

  public void setStatut(Statut statut) {
    this.statut = statut;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCin() {
    return cin;
  }

  public void setCin(String cin) {
    this.cin = cin;
  }

  public String getAdresse() {
    return adresse;
  }

  public void setAdresse(String adresse) {
    this.adresse = adresse;
  }

  public Gouvernorat getGouvernorat() {
    return gouvernorat;
  }

  public void setGouvernorat(Gouvernorat gouvernorat) {
    this.gouvernorat = gouvernorat;
  }

  public PostalCode getCodePostale() {
    return codePostale;
  }

  public void setCodePostale(PostalCode codePostale) {
    this.codePostale = codePostale;
  }

  public Ville getVille() {
    return ville;
  }

  public void setVille(Ville ville) {
    this.ville = ville;
  }

  public Long getTelFixe() {
    return telFixe;
  }

  public void setTelFixe(Long telFixe) {
    this.telFixe = telFixe;
  }

  public Long getTelMobile() {
    return telMobile;
  }

  public void setTelMobile(Long telMobile) {
    this.telMobile = telMobile;
  }

  public Long getTelMobile2() {
    return telMobile2;
  }

  public void setTelMobile2(Long telMobile2) {
    this.telMobile2 = telMobile2;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public CategorieProduitInternet getCategorieProduitInternet() {
    return categorieProduitInternet;
  }

  public void setCategorieProduitInternet(CategorieProduitInternet categorieProduitInternet) {
    this.categorieProduitInternet = categorieProduitInternet;
  }

  public Profession getProfession() {
    return profession;
  }

  public void setProfession(Profession profession) {
    this.profession = profession;
  }

  public Long getFax() {
    return fax;
  }

  public void setFax(Long fax) {
    this.fax = fax;
  }

  public String getDemandePdf() {
    return demandePdf;
  }

  public void setDemandePdf(String demandePdf) {
    this.demandePdf = demandePdf;
  }

  public String getContratPdf() {
    return contratPdf;
  }

  public void setContratPdf(String contratPdf) {
    this.contratPdf = contratPdf;
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

  public Modem getModem() {
    return modem;
  }

  public void setModem(Modem modem) {
    this.modem = modem;
  }

  public String getReferenceChifco() {
    return referenceChifco;
  }

  public void setReferenceChifco(String referenceChifco) {
    this.referenceChifco = referenceChifco;
  }

  public String getReferenceTT() {
    return referenceTT;
  }

  public void setReferenceTT(String referenceTT) {
    this.referenceTT = referenceTT;
  }

  public String getPositionxy() {
    return positionxy;
  }

  public void setPositionxy(String positionxy) {
    this.positionxy = positionxy;
  }

  public Date getDateNaissance() {
    return dateNaissance;
  }

  public void setDateNaissance(Date dateNaissance) {
    this.dateNaissance = dateNaissance;
  }

  public Typepaiement getTypePaiement() {
    return typePaiement;
  }

  public void setTypePaiement(Typepaiement typePaiement) {
    this.typePaiement = typePaiement;
  }

  public String getEtatTT() {
    return etatTT;
  }

  public void setEtatTT(String etatTT) {
    this.etatTT = etatTT;
  }

  public String getDateFinContrat() {
    return dateFinContrat;
  }

  public void setDateFinContrat(String dateFinContrat) {
    this.dateFinContrat = dateFinContrat;
  }

  public String getDateDesactivation() {
    return dateDesactivation;
  }

  public void setDateDesactivation(String dateDesactivation) {
    this.dateDesactivation = dateDesactivation;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public Boolean getHasRaccordement() {
    return hasRaccordement;
  }

  public void setHasRaccordement(Boolean hasRaccordement) {
    this.hasRaccordement = hasRaccordement;
  }

  public Integer getNbFaisApayeReccardement() {
    return nbFaisApayeReccardement;
  }

  public void setNbFaisApayeReccardement(Integer nbFaisApayeReccardement) {
    this.nbFaisApayeReccardement = nbFaisApayeReccardement;
  }

  public Boolean getWithIpFix() {
    return withIpFix;
  }

  public void setWithIpFix(Boolean withIpFix) {
    this.withIpFix = withIpFix;
  }

  public Boolean getProprietaire() {
    return proprietaire;
  }

  public void setProprietaire(Boolean proprietaire) {
    this.proprietaire = proprietaire;
  }

  public String getMotifRefus() {
    return motifRefus;
  }

  public void setMotifRefus(String motifRefus) {
    this.motifRefus = motifRefus;
  }

  public Date getDateDecisionDemande() {
    return dateDecisionDemande;
  }

  public void setDateDecisionDemande(Date dateDecisionDemande) {
    this.dateDecisionDemande = dateDecisionDemande;
  }

  public ClassificationDemande getDecisionDemande() {
    return decisionDemande;
  }

  public void setDecisionDemande(ClassificationDemande decisionDemande) {
    this.decisionDemande = decisionDemande;
  }

  public List<EntryDemandeAbonnement> getEntriesDemandeAbonnement() {
    return entriesDemandeAbonnement;
  }

  public void setEntriesDemandeAbonnement(List<EntryDemandeAbonnement> entriesDemandeAbonnement) {
    this.entriesDemandeAbonnement = entriesDemandeAbonnement;
  }

  public Pack getPack() {
    return pack;
  }

  public void setPack(Pack pack) {
    this.pack = pack;
  }

  public String getSituationFamiliale() {
    return situationFamiliale;
  }

  public void setSituationFamiliale(String situationFamiliale) {
    this.situationFamiliale = situationFamiliale;
  }

  public User getAssignedTo() {
    return assignedTo;
  }

  public void setAssignedTo(User assignedTo) {
    this.assignedTo = assignedTo;
  }

  public Long getEditedBy() {
    return editedBy;
  }

  public void setEditedBy(Long editedBy) {
    this.editedBy = editedBy;
  }

  public Boolean getIsSmsVerification() {
    return isSmsVerification;
  }

  public void setIsSmsVerification(Boolean isSmsVerification) {
    this.isSmsVerification = isSmsVerification;
  }

  public Boolean getIsSmsSend() {
    return isSmsSend;
  }

  public void setIsSmsSend(Boolean isSmsSend) {
    this.isSmsSend = isSmsSend;
  }

  public Long getDemandeAbonnementParents() {
    return demandeAbonnementParents;
  }

  public void setDemandeAbonnementParents(Long demandeAbonnementParents) {
    this.demandeAbonnementParents = demandeAbonnementParents;
  }

  public Boolean getHouseHolder() {
    return houseHolder;
  }

  public void setHouseHolder(Boolean houseHolder) {
    this.houseHolder = houseHolder;
  }

  public Boolean getHasBankCard() {
    return hasBankCard;
  }

  public void setHasBankCard(Boolean hasBankCard) {
    this.hasBankCard = hasBankCard;
  }


  public Date getDateDeMiseEnService() {
    return dateDeMiseEnService;
  }

  public void setDateDeMiseEnService(Date dateDeMiseEnService) {
    this.dateDeMiseEnService = dateDeMiseEnService;
  }

  public Boolean getIsCommisionSaved() {
    return isCommisionSaved;
  }

  public void setIsCommisionSaved(Boolean isCommisionSaved) {
    this.isCommisionSaved = isCommisionSaved;

  }


  public User getTreatedBy() {
    return treatedBy;
  }

  public void setTreatedBy(User treatedBy) {
    this.treatedBy = treatedBy;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  
  
  
  public String getTypeAbonnment() {
	return typeAbonnment;
}

public void setTypeAbonnment(String typeAbonnment) {
	this.typeAbonnment = typeAbonnment;
}

@Override
  public String toString() {
    return "DemandeAbonnement [demandeId=" + demandeId + ", statut=" + statut + ", firstName="
        + firstName + ", lastName=" + lastName + ", email=" + email + ", cin=" + cin + ", adresse="
        + adresse + ", gouvernorat=" + gouvernorat + ", codePostale=" + codePostale + ", ville="
        + ville + ", telFixe=" + telFixe + ", telMobile=" + telMobile + ", telMobile2=" + telMobile2
        + ", photoCin1=" + photoCin1 + ", photoCin2=" + photoCin2 + ", user=" + user
        + ", categorieProduitInternet=" + categorieProduitInternet + ", profession=" + profession
        + ", fax=" + fax + ", demandePdf=" + demandePdf + ", contratPdf=" + contratPdf
        + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", modem=" + modem
        + ", referenceChifco=" + referenceChifco + ", referenceTT=" + referenceTT + ", positionxy="
        + positionxy + ", dateNaissance=" + dateNaissance + ", typePaiement=" + typePaiement
        + ", etatTT=" + etatTT + ", dateFinContrat=" + dateFinContrat + ", dateDesactivation="
        + dateDesactivation + ", isActive=" + isActive + ", hasRaccordement=" + hasRaccordement
        + ", nbFaisApayeReccardement=" + nbFaisApayeReccardement + ", withIpFix=" + withIpFix
        + ", proprietaire=" + proprietaire + ", motifRefus=" + motifRefus + ", dateDecisionDemande="
        + dateDecisionDemande + ", decisionDemande=" + decisionDemande
        + ", entriesDemandeAbonnement=" + entriesDemandeAbonnement + ", pack=" + pack
        + ", situationFamiliale=" + situationFamiliale + ", assignedTo=" + assignedTo
        + ", editedBy=" + editedBy + ", isSmsVerification=" + isSmsVerification + ", isSmsSend="
        + isSmsSend + ", demandeAbonnementParents=" + demandeAbonnementParents + "]";
  }

}
