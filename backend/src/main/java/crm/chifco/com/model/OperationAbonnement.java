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
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "operationabonnement")
@EntityListeners(AuditingEntityListener.class)
public class OperationAbonnement implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "operationid", nullable = false, updatable = false)
  private Long operationId;

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

  @Temporal(TemporalType.DATE)
  private Date dateProchainFacturation;

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

  @Column(length = 65)
  private String photoCin1;

  @Column(length = 65)
  private String photoCin2;

  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;
  @JsonIgnore
  @OneToOne()
  @JoinColumn(name = "categorie_produit_internet_id")
  private CategorieProduitInternet categorieProduitInternet;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "profession_id")
  private Profession profession;

  @Column(length = 80)
  private Long fax;

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

  @Column(name = "reference_chifco", unique = false, nullable = false, updatable = false)
  private String referenceChifco;

  @Column(name = "referenceTT", length = 80)
  private String referenceTT;

  @Column(length = 250)
  private String ancien_value;
  @Column(length = 250)
  private String positionxy;

  @Temporal(TemporalType.DATE)
  private Date dateNaissance;

  @OneToOne()
  @JoinColumn(name = "type_de_paiement_id")
  private Typepaiement typePaiement;

  @Temporal(TemporalType.DATE)
  private Date dateDeMiseEnService;

  @Column(length = 80)
  private String etatTT;

  private String dateFinContrat;

  private String dateDesactivation;

  private Boolean isActive = false;

  private Boolean hasRaccordement = true;

  @JoinColumn(name = "abonnementid")
  private Long abonnementId;

  private Integer nbFaisApayeReccardement = 0;

  private Boolean withIpFix = false;
  @Column(name = "trancheRaccordementSelected", columnDefinition = "int default 0")
  private Integer trancheRaccordementSelected = 0;
  @Column(columnDefinition = "int default 0")
  private Integer trancheRaccordement = 0;

  private Boolean proprietaire;

  private String motifRefus;

  @Temporal(TemporalType.DATE)
  private Date dateDecisionDemande;


  private Long IdDecisionDemande;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "packId")
  private Pack pack;
  @Column(name = "refClientSite")
  private String refClientSite;
  private String situationFamiliale;

  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignedTo")
  private User assignedTo;

  @JoinColumn(name = "editedBy")
  private Long editedBy;

  private Boolean isFraisRaccordementTT;
  private Boolean isSmsVerification = false;

  private Boolean isSmsSend = false;

  private String typeDemande;

  public String getAncien_value() {
    return ancien_value;
  }

  public void setAncien_value(String ancien_value) {
    this.ancien_value = ancien_value;
  }

  public Date getDateProchainFacturation() {
    return dateProchainFacturation;
  }

  public void setDateProchainFacturation(Date dateProchainFacturation) {
    this.dateProchainFacturation = dateProchainFacturation;
  }

  public Long getOperationId() {
    return operationId;
  }

  public void setOperationId(Long operationId) {
    this.operationId = operationId;
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

  public Date getDateDeMiseEnService() {
    return dateDeMiseEnService;
  }

  public void setDateDeMiseEnService(Date dateDeMiseEnService) {
    this.dateDeMiseEnService = dateDeMiseEnService;
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

  public Long getAbonnementId() {
    return abonnementId;
  }

  public void setAbonnementId(Long abonnementId) {
    this.abonnementId = abonnementId;
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

  public Long getIdDecisionDemande() {
    return IdDecisionDemande;
  }

  public void setIdDecisionDemande(Long idDecisionDemande) {
    IdDecisionDemande = idDecisionDemande;
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

  public String getTypeDemande() {
    return typeDemande;
  }

  public void setTypeDemande(String typeDemande) {
    this.typeDemande = typeDemande;
  }

  public Integer getTrancheRaccordementSelected() {
    return trancheRaccordementSelected;
  }

  public void setTrancheRaccordementSelected(Integer trancheRaccordementSelected) {
    this.trancheRaccordementSelected = trancheRaccordementSelected;
  }

  public Integer getTrancheRaccordement() {
    return trancheRaccordement;
  }

  public void setTrancheRaccordement(Integer trancheRaccordement) {
    this.trancheRaccordement = trancheRaccordement;
  }

  public String getRefClientSite() {
    return refClientSite;
  }

  public void setRefClientSite(String refClientSite) {
    this.refClientSite = refClientSite;
  }

  public User getAssignedTo() {
    return assignedTo;
  }

  public void setAssignedTo(User assignedTo) {
    this.assignedTo = assignedTo;
  }

  public Boolean getIsFraisRaccordementTT() {
    return isFraisRaccordementTT;
  }

  public void setIsFraisRaccordementTT(Boolean isFraisRaccordementTT) {
    this.isFraisRaccordementTT = isFraisRaccordementTT;
  }

  public OperationAbonnement(Long operationId, Statut statut, String firstName, String lastName,
      String email, String cin, String adresse, Gouvernorat gouvernorat, PostalCode codePostale,
      Ville ville, Long telFixe, Long telMobile, Long telMobile2, String photoCin1,
      String photoCin2, User user, CategorieProduitInternet categorieProduitInternet,
      Profession profession, Long fax, String demandePdf, String contratPdf, Date createdDate,
      Date modifiedDate, Modem modem, String referenceChifco, String referenceTT, String positionxy,
      Date dateNaissance, Typepaiement typePaiement, Date dateDeMiseEnService, String etatTT,
      String dateFinContrat, String dateDesactivation, Boolean isActive, Boolean hasRaccordement,
      Long abonnementId, Integer nbFaisApayeReccardement, Boolean withIpFix,
      Integer trancheRaccordementSelected, Integer trancheRaccordement, Boolean proprietaire,
      String motifRefus, Date dateDecisionDemande, Long idDecisionDemande, Pack pack,
      String refClientSite, String situationFamiliale, User assignedTo, Long editedBy,
      Boolean isFraisRaccordementTT, Boolean isSmsVerification, Boolean isSmsSend,
      String typeDemande) {
    super();
    this.operationId = operationId;
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
    this.contratPdf = contratPdf;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.modem = modem;
    this.referenceChifco = referenceChifco;
    this.referenceTT = referenceTT;
    this.positionxy = positionxy;
    this.dateNaissance = dateNaissance;
    this.typePaiement = typePaiement;
    this.dateDeMiseEnService = dateDeMiseEnService;
    this.etatTT = etatTT;
    this.dateFinContrat = dateFinContrat;
    this.dateDesactivation = dateDesactivation;
    this.isActive = isActive;
    this.hasRaccordement = hasRaccordement;
    this.abonnementId = abonnementId;
    this.nbFaisApayeReccardement = nbFaisApayeReccardement;
    this.withIpFix = withIpFix;
    this.trancheRaccordementSelected = trancheRaccordementSelected;
    this.trancheRaccordement = trancheRaccordement;
    this.proprietaire = proprietaire;
    this.motifRefus = motifRefus;
    this.dateDecisionDemande = dateDecisionDemande;
    IdDecisionDemande = idDecisionDemande;
    this.pack = pack;
    this.refClientSite = refClientSite;
    this.situationFamiliale = situationFamiliale;
    this.assignedTo = assignedTo;
    this.editedBy = editedBy;
    this.isFraisRaccordementTT = isFraisRaccordementTT;
    this.isSmsVerification = isSmsVerification;
    this.isSmsSend = isSmsSend;
    this.typeDemande = typeDemande;
  }

  public OperationAbonnement() {
    super();
  }

}
