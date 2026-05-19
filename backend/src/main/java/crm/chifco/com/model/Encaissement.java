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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "encaissement")
@EntityListeners(AuditingEntityListener.class)
public class Encaissement implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long encaissementId;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date date; // now ;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "commandeid")
  private Commande commande;

  @JsonIgnore
  @JoinColumn(name = "client")
  private Long client;

  @Temporal(TemporalType.DATE)
  private Date dateDebutFacturation;

  @Temporal(TemporalType.DATE)
  private Date dateFinFacturation;

  private String typeDePayment;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userid")
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "payementid")
  private Payement payement;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "factureId")
  private Facture facture;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AvoirClientId")
  private AvoirClient avoirClient;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private Boolean hasBordereau = false;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "idbordaureau")
  private Bordereau idbordaureau;
  private Double montantFacture;

  private Boolean isChifcoPayed = false;

  private String numeroCarte;
  private String numeroCheque;
  private String nomBank;
  private Boolean firstReminderRevendeur = false;
  private Boolean secondReminderRevendeur = false;
  private Boolean blockCompteReminderRevendeur = false;

  /**
   * @return the commissionrevendeur
   */

  public Encaissement() {}

  public Encaissement(Long encaissementId, Date date, Commande commande, Long client,
      Date dateDebutFacturation, Date dateFinFacturation, String typeDePayment, User user,
      Payement payement, Facture facture, Date createdDate, Date modifiedDate, Boolean hasBordereau,
      Bordereau idbordaureau, Double montantFacture, Boolean isChifcoPayed, String numeroCarte,
      String numeroCheque, String nomBank) {
    super();
    this.encaissementId = encaissementId;
    this.date = date;
    this.commande = commande;
    this.client = client;
    this.dateDebutFacturation = dateDebutFacturation;
    this.dateFinFacturation = dateFinFacturation;
    this.typeDePayment = typeDePayment;
    this.user = user;
    this.payement = payement;
    this.facture = facture;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.hasBordereau = hasBordereau;
    this.idbordaureau = idbordaureau;
    this.montantFacture = montantFacture;

    this.isChifcoPayed = isChifcoPayed;
    this.numeroCarte = numeroCarte;
    this.numeroCheque = numeroCheque;
    this.nomBank = nomBank;
  }

  /**
   * @return the encaissementId
   */
  public Long getEncaissementId() {
    return encaissementId;
  }

  /**
   * @param encaissementId the encaissementId to set
   */
  public void setEncaissementId(Long encaissementId) {
    this.encaissementId = encaissementId;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return date;
  }

  /**
   * @param date the date to set
   */
  public void setDate(Date date) {
    this.date = date;
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
   * @return the dateDebutFacturation
   */
  public Date getDateDebutFacturation() {
    return dateDebutFacturation;
  }

  /**
   * @param dateDebutFacturation the dateDebutFacturation to set
   */
  public void setDateDebutFacturation(Date dateDebutFacturation) {
    this.dateDebutFacturation = dateDebutFacturation;
  }

  /**
   * @return the dateFinFacturation
   */
  public Date getDateFinFacturation() {
    return dateFinFacturation;
  }

  /**
   * @param dateFinFacturation the dateFinFacturation to set
   */
  public void setDateFinFacturation(Date dateFinFacturation) {
    this.dateFinFacturation = dateFinFacturation;
  }

  /**
   * @return the typeDePayment
   */
  public String getTypeDePayment() {
    return typeDePayment;
  }

  /**
   * @param typeDePayment the typeDePayment to set
   */
  public void setTypeDePayment(String typeDePayment) {
    this.typeDePayment = typeDePayment;
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
   * @return the payement
   */
  public Payement getPayement() {
    return payement;
  }

  /**
   * @param payement the payement to set
   */
  public void setPayement(Payement payement) {
    this.payement = payement;
  }

  /**
   * @return the facture
   */
  public Facture getFacture() {
    return facture;
  }

  /**
   * @param facture the facture to set
   */
  public void setFacture(Facture facture) {
    this.facture = facture;
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
   * @return the hasBordereau
   */
  public Boolean getHasBordereau() {
    return hasBordereau;
  }

  /**
   * @param hasBordereau the hasBordereau to set
   */
  public void setHasBordereau(Boolean hasBordereau) {
    this.hasBordereau = hasBordereau;
  }

  /**
   * @return the idbordaureau
   */
  public Bordereau getIdbordaureau() {
    return idbordaureau;
  }

  /**
   * @param idbordaureau the idbordaureau to set
   */
  public void setIdbordaureau(Bordereau idbordaureau) {
    this.idbordaureau = idbordaureau;
  }

  /**
   * @return the montantFacture
   */
  public Double getMontantFacture() {
    return montantFacture;
  }

  /**
   * @param montantFacture the montantFacture to set
   */
  public void setMontantFacture(Double montantFacture) {
    this.montantFacture = montantFacture;
  }



  /**
   * @return the isChifcoPayed
   */
  public Boolean getIsChifcoPayed() {
    return isChifcoPayed;
  }

  /**
   * @param isChifcoPayed the isChifcoPayed to set
   */
  public void setIsChifcoPayed(Boolean isChifcoPayed) {
    this.isChifcoPayed = isChifcoPayed;
  }

  /**
   * @return the numeroCarte
   */
  public String getNumeroCarte() {
    return numeroCarte;
  }

  /**
   * @param numeroCarte the numeroCarte to set
   */
  public void setNumeroCarte(String numeroCarte) {
    this.numeroCarte = numeroCarte;
  }

  /**
   * @return the numeroCheque
   */
  public String getNumeroCheque() {
    return numeroCheque;
  }

  /**
   * @param numeroCheque the numeroCheque to set
   */
  public void setNumeroCheque(String numeroCheque) {
    this.numeroCheque = numeroCheque;
  }

  /**
   * @return the nomBank
   */
  public String getNomBank() {
    return nomBank;
  }

  /**
   * @param nomBank the nomBank to set
   */
  public void setNomBank(String nomBank) {
    this.nomBank = nomBank;
  }

  /**
   * @return the client
   */
  public Long getClient() {
    return client;
  }

  /**
   * @param client the client to set
   */
  public void setClient(Long client) {
    this.client = client;
  }

  public AvoirClient getAvoirClient() {
    return avoirClient;
  }

  public void setAvoirClient(AvoirClient avoirClient) {
    this.avoirClient = avoirClient;
  }



  public Boolean getFirstReminderRevendeur() {
    return firstReminderRevendeur;
  }

  public void setFirstReminderRevendeur(Boolean firstReminderRevendeur) {
    this.firstReminderRevendeur = firstReminderRevendeur;
  }


  public Boolean getSecondReminderRevendeur() {
    return secondReminderRevendeur;
  }

  public void setSecondReminderRevendeur(Boolean secondReminderRevendeur) {
    this.secondReminderRevendeur = secondReminderRevendeur;
  }

  public Boolean getBlockCompteReminderRevendeur() {
    return blockCompteReminderRevendeur;
  }

  public void setBlockCompteReminderRevendeur(Boolean blockCompteReminderRevendeur) {
    this.blockCompteReminderRevendeur = blockCompteReminderRevendeur;
  }

  @Override
  public String toString() {
    return "Encaissement [encaissementId=" + encaissementId + ", date=" + date + ", commande="
        + commande + ", client=" + client + ", dateDebutFacturation=" + dateDebutFacturation
        + ", dateFinFacturation=" + dateFinFacturation + ", typeDePayment=" + typeDePayment
        + ", user=" + user + ", payement=" + payement + ", facture=" + facture + ", createdDate="
        + createdDate + ", modifiedDate=" + modifiedDate + ", hasBordereau=" + hasBordereau
        + ", idbordaureau=" + idbordaureau + ", montantFacture=" + montantFacture
        + ", isChifcoPayed=" + isChifcoPayed + ", numeroCarte=" + numeroCarte + ", numeroCheque="
        + numeroCheque + ", nomBank=" + nomBank + "]";
  }

}
