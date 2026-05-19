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
@Table(name = "Users")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "userid")
  private Long userid;

  @Column(name = "first_name", length = 75)
  private String firstName;

  @Column(name = "last_name", length = 80)
  private String lastName;

  @Column(name = "password", length = 64)
  private String password;

  @Column(name = "email", unique = true, length = 115)
  private String email;

  @Column(name = "enabled")
  private boolean enabled;

  @Column(name = "photo", length = 65)
  private String photo;

  @Column(name = "telephone", length = 65)
  private String telephone;
  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id")
  private Role role;

  @JsonIgnore
  @Column(name = "createdByUserId")
  private Long createdByUserId;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private Long pcActivationCommision;
  private Long pcRefusCommision;

  private Boolean withStock = true;

  @Column(name = "adresse", length = 115)
  private String adresse;

  @Column(name = "codePostale", length = 80)
  private Long codePostale;

  @Column(name = "plafonRevendeur", length = 180)
  private Long plafonRevendeur;

  @Column(name = "coordonneesBancaires", length = 115)
  private String coordonneesBancaires;

  @Column(name = "activitePrincipale", length = 180)
  private String activitePrincipale;

  @Column(name = "regimeFiscal", length = 180)
  private String regimeFiscal;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "villeId")
  private Ville ville;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gouvernoratId")
  private Gouvernorat gouvernorat;

  @Column(name = "isLocked")
  private boolean isLocked = false;

  @Column(name = "RNE")
  private String RNE;

  @Column(name = "carteFiscale")
  private String carteFiscale;

  @Column(name = "contrat")
  private String contrat;

  @Column(name = "typeuser")
  private String typeUser;

  @Column(name = "classuser")
  private String classUser;

  @Column(name = "identificationFiscale")
  private String identificationFiscale;

  @Column(name = "formeJuridique")
  private String formeJuridique;

  @Column(name = "cin", unique = true, length = 115)
  private String cin;

  @Column(name = "codeUser")
  private String codeUser;

  @Column(name = "nomCommercial")
  private String nomCommercial;
  @Column(name = "interlocuteur")
  private String interlocuteur;

  @Column(name = "affectedTo")
  private Long affectedTo;

  private Boolean desactivatedByCron = false;


  @Temporal(TemporalType.TIMESTAMP)
  private Date desactivationDate;

  private String classification = "activer";
  
  private Boolean  isExonoree = false ;

  private String codePGHUser;
  
  @Temporal(TemporalType.DATE)
  private Date   dateUpdateclassification;
  public User() {}

  public User(Long userid, String firstName, String lastName, String password, String email,
      boolean enabled, String photo, String telephone, Role role, Long createdByUserId,
      Date createdDate, Date modifiedDate, Long pcFirstFacture, Long pcFactureRecurent,
      Boolean withStock, String adresse, Long codePostale, Long plafonRevendeur,
      String coordonneesBancaires, String activitePrincipale, String regimeFiscal, Ville ville,
      Gouvernorat gouvernorat, boolean isLocked, String rNE, String carteFiscale, String contrat,
      String typeUser, String classUser, String identificationFiscale, String formeJuridique,
      String cin, String codeUser) {
    super();
    this.userid = userid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
    this.email = email;
    this.enabled = enabled;
    this.photo = photo;
    this.telephone = telephone;
    this.role = role;
    this.createdByUserId = createdByUserId;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.withStock = withStock;
    this.adresse = adresse;
    this.codePostale = codePostale;
    this.plafonRevendeur = plafonRevendeur;
    this.coordonneesBancaires = coordonneesBancaires;
    this.activitePrincipale = activitePrincipale;
    this.regimeFiscal = regimeFiscal;
    this.ville = ville;
    this.gouvernorat = gouvernorat;
    this.isLocked = isLocked;
    RNE = rNE;
    this.carteFiscale = carteFiscale;
    this.contrat = contrat;
    this.typeUser = typeUser;
    this.classUser = classUser;
    this.identificationFiscale = identificationFiscale;
    this.formeJuridique = formeJuridique;
    this.cin = cin;
    this.codeUser = codeUser;
  }

  /**
   * @return the userid
   */
  public Long getUserid() {
    return userid;
  }

  /**
   * @param userid the userid to set
   */
  public void setUserid(Long userid) {
    this.userid = userid;
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
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return the enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * @param enabled the enabled to set
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * @return the photo
   */
  public String getPhoto() {
    return photo;
  }

  /**
   * @param photo the photo to set
   */
  public void setPhoto(String photo) {
    this.photo = photo;
  }

  /**
   * @return the telephone
   */
  public String getTelephone() {
    return telephone;
  }

  /**
   * @param telephone the telephone to set
   */
  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  /**
   * @return the role
   */
  public Role getRole() {
    return role;
  }

  /**
   * @param role the role to set
   */
  public void setRole(Role role) {
    this.role = role;
  }

  /**
   * @return the createdByUserId
   */
  public Long getCreatedByUserId() {
    return createdByUserId;
  }

  /**
   * @param createdByUserId the createdByUserId to set
   */
  public void setCreatedByUserId(Long createdByUserId) {
    this.createdByUserId = createdByUserId;
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
   * @return the withStock
   */
  public Boolean getWithStock() {
    return withStock;
  }

  /**
   * @param withStock the withStock to set
   */
  public void setWithStock(Boolean withStock) {
    this.withStock = withStock;
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
   * @return the codePostale
   */
  public Long getCodePostale() {
    return codePostale;
  }

  /**
   * @param codePostale the codePostale to set
   */
  public void setCodePostale(Long codePostale) {
    this.codePostale = codePostale;
  }

  /**
   * @return the plafonRevendeur
   */
  public Long getPlafonRevendeur() {
    return plafonRevendeur;
  }

  /**
   * @param plafonRevendeur the plafonRevendeur to set
   */
  public void setPlafonRevendeur(Long plafonRevendeur) {
    this.plafonRevendeur = plafonRevendeur;
  }

  /**
   * @return the coordonneesBancaires
   */
  public String getCoordonneesBancaires() {
    return coordonneesBancaires;
  }

  /**
   * @param coordonneesBancaires the coordonneesBancaires to set
   */
  public void setCoordonneesBancaires(String coordonneesBancaires) {
    this.coordonneesBancaires = coordonneesBancaires;
  }

  /**
   * @return the activitePrincipale
   */
  public String getActivitePrincipale() {
    return activitePrincipale;
  }

  /**
   * @param activitePrincipale the activitePrincipale to set
   */
  public void setActivitePrincipale(String activitePrincipale) {
    this.activitePrincipale = activitePrincipale;
  }

  /**
   * @return the regimeFiscal
   */
  public String getRegimeFiscal() {
    return regimeFiscal;
  }

  /**
   * @param regimeFiscal the regimeFiscal to set
   */
  public void setRegimeFiscal(String regimeFiscal) {
    this.regimeFiscal = regimeFiscal;
  }

  /**
   * @return the ville
   */
  public Ville getVille() {
    return ville;
  }

  /**
   * @param ville the ville to set
   */
  public void setVille(Ville ville) {
    this.ville = ville;
  }

  /**
   * @return the gouvernorat
   */
  public Gouvernorat getGouvernorat() {
    return gouvernorat;
  }

  /**
   * @param gouvernorat the gouvernorat to set
   */
  public void setGouvernorat(Gouvernorat gouvernorat) {
    this.gouvernorat = gouvernorat;
  }

  /**
   * @return the isLocked
   */
  public boolean isLocked() {
    return isLocked;
  }

  /**
   * @param isLocked the isLocked to set
   */
  public void setLocked(boolean isLocked) {
    this.isLocked = isLocked;
  }

  /**
   * @return the rNE
   */
  public String getRNE() {
    return RNE;
  }

  /**
   * @param rNE the rNE to set
   */
  public void setRNE(String rNE) {
    RNE = rNE;
  }

  /**
   * @return the carteFiscale
   */
  public String getCarteFiscale() {
    return carteFiscale;
  }

  /**
   * @param carteFiscale the carteFiscale to set
   */
  public void setCarteFiscale(String carteFiscale) {
    this.carteFiscale = carteFiscale;
  }

  /**
   * @return the contrat
   */
  public String getContrat() {
    return contrat;
  }

  /**
   * @param contrat the contrat to set
   */
  public void setContrat(String contrat) {
    this.contrat = contrat;
  }

  /**
   * @return the typeUser
   */
  public String getTypeUser() {
    return typeUser;
  }

  /**
   * @param typeUser the typeUser to set
   */
  public void setTypeUser(String typeUser) {
    this.typeUser = typeUser;
  }

  /**
   * @return the classUser
   */
  public String getClassUser() {
    return classUser;
  }

  /**
   * @param classUser the classUser to set
   */
  public void setClassUser(String classUser) {
    this.classUser = classUser;
  }

  /**
   * @return the identificationFiscale
   */
  public String getIdentificationFiscale() {
    return identificationFiscale;
  }

  /**
   * @param identificationFiscale the identificationFiscale to set
   */
  public void setIdentificationFiscale(String identificationFiscale) {
    this.identificationFiscale = identificationFiscale;
  }

  /**
   * @return the formeJuridique
   */
  public String getFormeJuridique() {
    return formeJuridique;
  }

  /**
   * @param formeJuridique the formeJuridique to set
   */
  public void setFormeJuridique(String formeJuridique) {
    this.formeJuridique = formeJuridique;
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
   * @return the codeUser
   */
  public String getCodeUser() {
    return codeUser;
  }

  /**
   * @param codeUser the codeUser to set
   */
  public void setCodeUser(String codeUser) {
    this.codeUser = codeUser;
  }

  public String getNomCommercial() {
    return nomCommercial;
  }

  public void setNomCommercial(String nomCommercial) {
    this.nomCommercial = nomCommercial;
  }

  public String getInterlocuteur() {
    return interlocuteur;
  }

  public void setInterlocuteur(String interlocuteur) {
    this.interlocuteur = interlocuteur;
  }

  public Long getAffectedTo() {
    return affectedTo;
  }

  public void setAffectedTo(Long affectedTo) {
    this.affectedTo = affectedTo;
  }



  public Boolean getDesactivatedByCron() {
    return desactivatedByCron;
  }

  public void setDesactivatedByCron(Boolean desactivatedByCron) {
    this.desactivatedByCron = desactivatedByCron;
  }

  public Long getPcActivationCommision() {
    return pcActivationCommision;
  }

  public void setPcActivationCommision(Long pcActivationCommision) {
    this.pcActivationCommision = pcActivationCommision;
  }

  public Long getPcRefusCommision() {
    return pcRefusCommision;
  }

  public void setPcRefusCommision(Long pcRefusCommision) {
    this.pcRefusCommision = pcRefusCommision;

  }

  public Date getDesactivationDate() {
    return desactivationDate;
  }

  public void setDesactivationDate(Date desactivationDate) {
    this.desactivationDate = desactivationDate;
  }

  public String getClassification() {
    return classification;
  }

  public void setClassification(String classification) {
    this.classification = classification;
  }

  public Boolean getIsExonoree() {
	return isExonoree;
}

public void setIsExonoree(Boolean isExonoree) {
	this.isExonoree = isExonoree;
}

public String getCodePGHUser() {
	return codePGHUser;
}

public void setCodePGHUser(String codePGHUser) {
	this.codePGHUser = codePGHUser;
}

public Date getDateUpdateclassification() {
	return dateUpdateclassification;
}

public void setDateUpdateclassification(Date dateUpdateclassification) {
	this.dateUpdateclassification = dateUpdateclassification;
}

@Override
public String toString() {
	return String.format(
			"User [userid=%s, firstName=%s, lastName=%s, password=%s, email=%s, enabled=%s, photo=%s, telephone=%s, role=%s, createdByUserId=%s, createdDate=%s, modifiedDate=%s, pcActivationCommision=%s, pcRefusCommision=%s, withStock=%s, adresse=%s, codePostale=%s, plafonRevendeur=%s, coordonneesBancaires=%s, activitePrincipale=%s, regimeFiscal=%s, ville=%s, gouvernorat=%s, isLocked=%s, RNE=%s, carteFiscale=%s, contrat=%s, typeUser=%s, classUser=%s, identificationFiscale=%s, formeJuridique=%s, cin=%s, codeUser=%s, nomCommercial=%s, interlocuteur=%s, affectedTo=%s, desactivatedByCron=%s, desactivationDate=%s, classification=%s, isExonoree=%s]",
			userid, firstName, lastName, password, email, enabled, photo, telephone, role, createdByUserId, createdDate,
			modifiedDate, pcActivationCommision, pcRefusCommision, withStock, adresse, codePostale, plafonRevendeur,
			coordonneesBancaires, activitePrincipale, regimeFiscal, ville, gouvernorat, isLocked, RNE, carteFiscale,
			contrat, typeUser, classUser, identificationFiscale, formeJuridique, cin, codeUser, nomCommercial,
			interlocuteur, affectedTo, desactivatedByCron, desactivationDate, classification, isExonoree);
}



}
