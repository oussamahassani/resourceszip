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
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import crm.chifco.com.service.AdslLoginGenerator;

@Entity
@Table(name = "Abonnement")
@EntityListeners(AuditingEntityListener.class)
public class Abonnement implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "clientid")
  private Long clientid;

  @Column(length = 75)
  private String firstName;

  @Column(length = 80)
  private String lastName;

  @Column(length = 80)
  private String password;

  @Column(length = 80)
  private String loginModem;

  @Column(name = "email", length = 115)
  private String email;

  @Column(name = "cin", unique = true, length = 115)
  private String cin;

  @Column(name = "adresse", length = 115)
  private String adresse;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "gouvernoratId")
  private Gouvernorat gouvernorat;

  @OneToOne
  @JoinColumn(name = "codePostale")
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

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userid")
  private User user;

  private boolean enabled;

  @Column(length = 205)
  private String photoCin1;

  @Column(length = 205)
  private String photoCin2;

  @Column(length = 205)
  private String ancienLogin;

  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profession_id")
  private Profession profession;

  @Column(length = 80)
  private Long fax;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  // @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "statutid")
  private Statut statut;

  // @JsonIgnore
  // @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "demandeid")
  private Long demandeAbonnement;

  @Temporal(TemporalType.DATE)
  private Date dateProchainFacturation;

  private Boolean isActive = true;

  @Temporal(TemporalType.DATE)
  private Date dateResiliation;

  private Long resiliePar;

  @GeneratorType(type = AdslLoginGenerator.class, when = GenerationTime.INSERT)
  @Column(name = "codeclient", unique = true, updatable = false)
  private String codeClient;

  @Column(name = "referenceClient", unique = true)
  private String referenceClient;

  @Column(unique = true)
  private String refClientSite;

  private Boolean hasRaccordement = true;
  private String dateFinContrat;
  private String dateDesactivation;
  @Column(name = "trancheRaccordementSelected", columnDefinition = "int default 0")
  private Integer trancheRaccordementSelected = 0;
  @Column(columnDefinition = "int default 0")
  private Integer trancheRaccordement = 0;

  private Boolean withIpFix = false;

  @OneToOne
  @JoinColumn(name = "packId")
  private Pack pack;

  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modemid")
  private Modem modem;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "typepaiementId")
  private Typepaiement typePaiement;

  private Date dateFinPromotion;

  private String situationFamiliale;

  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignedTo")

  private User assignedTo;


  private Boolean houseHolder;

  private Boolean hasBankCard;

  private Boolean proprietaire;

  @Temporal(TemporalType.DATE)
  private Date dateNaissance;

  private Boolean isFraisRaccordementTT;

  private Boolean isSmsClientSend = true;

  @Temporal(TemporalType.DATE)
  private Date dateCalculeFraisServies;


  @Temporal(TemporalType.DATE)
  private Date dateDeMiseEnService;

  private Boolean calculeIsFirstSession = false;
  private Boolean calculeServiceResiliation = false;


  @Temporal(TemporalType.DATE)
  private Date firstConnectionDate;
  @Temporal(TemporalType.DATE)
  private Date modemAffectedDate;

  private Boolean isClient = false;
  private Boolean isMigration = false;
  private Boolean isChangementDebit = false;
  private Double creditFacture = 0.00;
  private Long tranchcreditFacture = 0L;

  private Double comisionActivationFreelancer = 0.0 ;
  private Boolean comissionActivationIsPayed = true ;
  private String typeAbonnment;

  public Abonnement() {}

  public Abonnement(Long clientid, String firstName, String lastName, String password,
      String loginModem, String email, String cin, String adresse, Gouvernorat gouvernorat,
      PostalCode codePostale, Ville ville, Long telFixe, Long telMobile, Long telMobile2, User user,
      boolean enabled, String photoCin1, String photoCin2, Profession profession, Long fax,
      Date createdDate, Date modifiedDate, Statut statut, Long demandeAbonnement,
      Date dateProchainFacturation, Boolean isActive, String codeClient, String referenceClient,
      String refClientSite, Boolean hasRaccordement, String dateFinContrat,
      String dateDesactivation, Integer trancheRaccordementSelected, Integer trancheRaccordement,
      Boolean withIpFix, Pack pack, Modem modem, Typepaiement typePaiement, Date dateFinPromotion,
      String situationFamiliale, User assignedTo) {
    super();
    this.clientid = clientid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
    this.loginModem = loginModem;
    this.email = email;
    this.cin = cin;
    this.adresse = adresse;
    this.gouvernorat = gouvernorat;
    this.codePostale = codePostale;
    this.ville = ville;
    this.telFixe = telFixe;
    this.telMobile = telMobile;
    this.telMobile2 = telMobile2;
    this.user = user;
    this.enabled = enabled;
    this.photoCin1 = photoCin1;
    this.photoCin2 = photoCin2;
    this.profession = profession;
    this.fax = fax;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.statut = statut;
    this.demandeAbonnement = demandeAbonnement;
    this.dateProchainFacturation = dateProchainFacturation;
    this.isActive = isActive;
    this.codeClient = codeClient;
    this.referenceClient = referenceClient;
    this.refClientSite = refClientSite;
    this.hasRaccordement = hasRaccordement;
    this.dateFinContrat = dateFinContrat;
    this.dateDesactivation = dateDesactivation;
    this.trancheRaccordementSelected = trancheRaccordementSelected;
    this.trancheRaccordement = trancheRaccordement;
    this.withIpFix = withIpFix;
    this.pack = pack;
    this.modem = modem;
    this.typePaiement = typePaiement;
    this.dateFinPromotion = dateFinPromotion;
    this.situationFamiliale = situationFamiliale;
    this.assignedTo = assignedTo;
  }

  public String getAncienLogin() {
    return ancienLogin;
  }

  public void setAncienLogin(String ancienLogin) {
    this.ancienLogin = ancienLogin;
  }

  public Long getClientid() {
    return clientid;
  }

  public void setClientid(Long clientid) {
    this.clientid = clientid;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getLoginModem() {
    return loginModem;
  }

  public void setLoginModem(String loginModem) {
    this.loginModem = loginModem;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
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

  public Statut getStatut() {
    return statut;
  }

  public void setStatut(Statut statut) {
    this.statut = statut;
  }

  public Long getDemandeAbonnement() {
    return demandeAbonnement;
  }

  public void setDemandeAbonnement(Long demandeAbonnement) {
    this.demandeAbonnement = demandeAbonnement;
  }

  public Date getDateProchainFacturation() {
    return dateProchainFacturation;
  }

  public void setDateProchainFacturation(Date dateProchainFacturation) {
    this.dateProchainFacturation = dateProchainFacturation;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public String getCodeClient() {
    return codeClient;
  }

  public void setCodeClient(String codeClient) {
    this.codeClient = codeClient;
  }

  public String getReferenceClient() {
    return referenceClient;
  }

  public void setReferenceClient(String referenceClient) {
    this.referenceClient = referenceClient;
  }

  public String getRefClientSite() {
    return refClientSite;
  }

  public void setRefClientSite(String refClientSite) {
    this.refClientSite = refClientSite;
  }

  public Boolean getHasRaccordement() {
    return hasRaccordement;
  }

  public void setHasRaccordement(Boolean hasRaccordement) {
    this.hasRaccordement = hasRaccordement;
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

  public Boolean getWithIpFix() {
    return withIpFix;
  }

  public void setWithIpFix(Boolean withIpFix) {
    this.withIpFix = withIpFix;
  }

  public Pack getPack() {
    return pack;
  }

  public void setPack(Pack pack) {
    this.pack = pack;
  }

  public Modem getModem() {
    return modem;
  }

  public void setModem(Modem modem) {
    this.modem = modem;
  }

  public Typepaiement getTypePaiement() {
    return typePaiement;
  }

  public void setTypePaiement(Typepaiement typePaiement) {
    this.typePaiement = typePaiement;
  }

  public Date getDateFinPromotion() {
    return dateFinPromotion;
  }

  public void setDateFinPromotion(Date dateFinPromotion) {
    this.dateFinPromotion = dateFinPromotion;
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

  public Date getDateResiliation() {
    return dateResiliation;
  }

  public void setDateResiliation(Date dateResiliation) {
    this.dateResiliation = dateResiliation;
  }

  public Long getResiliePar() {
    return resiliePar;
  }

  public void setResiliePar(Long resiliePar) {
    this.resiliePar = resiliePar;
  }

  public Boolean getIsFraisRaccordementTT() {
    return isFraisRaccordementTT;
  }

  public void setIsFraisRaccordementTT(Boolean isFraisRaccordementTT) {
    this.isFraisRaccordementTT = isFraisRaccordementTT;
  }

  public Date getDateCalculeFraisServies() {
    return dateCalculeFraisServies;
  }

  public void setDateCalculeFraisServies(Date dateCalculeFraisServies) {
    this.dateCalculeFraisServies = dateCalculeFraisServies;
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


  public Boolean getProprietaire() {
    return proprietaire;
  }

  public void setProprietaire(Boolean proprietaire) {
    this.proprietaire = proprietaire;
  }



  public Date getDateNaissance() {
    return dateNaissance;
  }

  public void setDateNaissance(Date dateNaissance) {
    this.dateNaissance = dateNaissance;
  }



  public Date getDateDeMiseEnService() {
    return dateDeMiseEnService;
  }

  public void setDateDeMiseEnService(Date dateDeMiseEnService) {
    this.dateDeMiseEnService = dateDeMiseEnService;
  }

  public Date getFirstConnectionDate() {
    return firstConnectionDate;
  }

  public void setFirstConnectionDate(Date firstConnectionDate) {
    this.firstConnectionDate = firstConnectionDate;
  }

  public Date getModemAffectedDate() {
    return modemAffectedDate;
  }

  public void setModemAffectedDate(Date modemAffectedDate) {
    this.modemAffectedDate = modemAffectedDate;

  }



  public Boolean getCalculeIsFirstSession() {
    return calculeIsFirstSession;
  }

  public void setCalculeIsFirstSession(Boolean calculeIsFirstSession) {
    this.calculeIsFirstSession = calculeIsFirstSession;
  }



  public Boolean getCalculeServiceResiliation() {
    return calculeServiceResiliation;
  }

  public void setCalculeServiceResiliation(Boolean calculeServiceResiliation) {
    this.calculeServiceResiliation = calculeServiceResiliation;
  }

  public Boolean getIsSmsClientSend() {
    return isSmsClientSend;
  }

  public void setIsSmsClientSend(Boolean isSmsClientSend) {
    this.isSmsClientSend = isSmsClientSend;
  }



  public Boolean getIsClient() {
    return isClient;
  }

  public void setIsClient(Boolean isClient) {
    this.isClient = isClient;
  }

  public Boolean getIsMigration() {
    return isMigration;
  }

  public void setIsMigration(Boolean isMigration) {
    this.isMigration = isMigration;
  }

  public Boolean getIsChangementDebit() {
    return isChangementDebit;
  }

  public void setIsChangementDebit(Boolean isChangementDebit) {
    this.isChangementDebit = isChangementDebit;
  }

  public Double getCreditFacture() {
    return creditFacture;
  }

  public void setCreditFacture(Double creditFacture) {
    this.creditFacture = creditFacture;
  }

  public Long getTranchcreditFacture() {
    return tranchcreditFacture;
  }

  public void setTranchcreditFacture(Long tranchcreditFacture) {
    this.tranchcreditFacture = tranchcreditFacture;
  }

  
  
  public Double getComisionActivationFreelancer() {
	return comisionActivationFreelancer;
}

public void setComisionActivationFreelancer(Double comisionActivationFreelancer) {
	this.comisionActivationFreelancer = comisionActivationFreelancer;
}

public Boolean getComissionActivationIsPayed() {
	return comissionActivationIsPayed;
}

public void setComissionActivationIsPayed(Boolean comissionActivationIsPayed) {
	this.comissionActivationIsPayed = comissionActivationIsPayed;
}

public String getTypeAbonnment() {
	return typeAbonnment;
}

public void setTypeAbonnment(String typeAbonnment) {
	this.typeAbonnment = typeAbonnment;
}

@Override
  public String toString() {
    return "Abonnement [clientid=" + clientid + ", firstName=" + firstName + ", lastName="
        + lastName + ", password=" + password + ", loginModem=" + loginModem + ", email=" + email
        + ", cin=" + cin + ", adresse=" + adresse + ", gouvernorat=" + gouvernorat
        + ", codePostale=" + codePostale + ", ville=" + ville + ", telFixe=" + telFixe
        + ", telMobile=" + telMobile + ", telMobile2=" + telMobile2 + ", user=" + user
        + ", enabled=" + enabled + ", photoCin1=" + photoCin1 + ", photoCin2=" + photoCin2
        + ", profession=" + profession + ", fax=" + fax + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + ", statut=" + statut + ", demandeAbonnement="
        + demandeAbonnement + ", dateProchainFacturation=" + dateProchainFacturation + ", isActive="
        + isActive + ", dateResiliation=" + dateResiliation + ", resiliePar=" + resiliePar
        + ", codeClient=" + codeClient + ", referenceClient=" + referenceClient + ", refClientSite="
        + refClientSite + ", hasRaccordement=" + hasRaccordement + ", dateFinContrat="
        + dateFinContrat + ", dateDesactivation=" + dateDesactivation
        + ", trancheRaccordementSelected=" + trancheRaccordementSelected + ", trancheRaccordement="
        + trancheRaccordement + ", withIpFix=" + withIpFix + ", pack=" + pack + ", modem=" + modem
        + ", typePaiement=" + typePaiement + ", dateFinPromotion=" + dateFinPromotion
        + ", situationFamiliale=" + situationFamiliale + ", assignedTo=" + assignedTo
        + ", houseHolder=" + houseHolder + ", hasBankCard=" + hasBankCard + ", proprietaire="
        + proprietaire + ", dateNaissance=" + dateNaissance + ", isFraisRaccordementTT="
        + isFraisRaccordementTT + ", isSmsClientSend=" + isSmsClientSend
        + ", dateCalculeFraisServies=" + dateCalculeFraisServies + "]";
  }



}
