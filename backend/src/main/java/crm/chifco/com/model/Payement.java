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
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import crm.chifco.com.service.GenerateSequenceRecu;

@Entity
@Table(name = "payement")
@EntityListeners(AuditingEntityListener.class)
public class Payement implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "payementid")
  private Long payementid;

  @Column(length = 80)
  private Double montant;

  @Column(length = 80)
  private String typePayment;
  @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userId")
  private User user;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "factureId")
  private Facture facture;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AvoirClientId")
  private AvoirClient AvoirClient;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private Boolean ischifcoPayed;

  private String numeroCarte;
  private String numeroCheque;
  private String nomBank;

  @ManyToOne(optional = true)
  @JoinColumn(name = "RecuNumeroSequenceId")
  private RecuNumeroSequence recuNumeroSequence;

  @GeneratorType(type = GenerateSequenceRecu.class, when = GenerationTime.INSERT)
  @Column(name = "codePayement", unique = true, updatable = false)
  private String codePayement;

  private String transactionId;

  public Payement() {}

  public Payement(Long payementid, Double montant, String typePayment, User user, Facture facture,
      Date createdDate, Date modifiedDate, Boolean ischifcoPayed, String numeroCarte,
      String numeroCheque, String nomBank, RecuNumeroSequence recuNumeroSequence,
      String codePayement) {
    super();
    this.payementid = payementid;
    this.montant = montant;
    this.typePayment = typePayment;
    this.user = user;
    this.facture = facture;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.ischifcoPayed = ischifcoPayed;
    this.numeroCarte = numeroCarte;
    this.numeroCheque = numeroCheque;
    this.nomBank = nomBank;
    this.recuNumeroSequence = recuNumeroSequence;
    this.codePayement = codePayement;
  }

  /**
   * @return the payementid
   */
  public Long getPayementid() {
    return payementid;
  }

  /**
   * @param payementid the payementid to set
   */
  public void setPayementid(Long payementid) {
    this.payementid = payementid;
  }

  /**
   * @return the montant
   */
  public Double getMontant() {
    return montant;
  }

  /**
   * @param montant the montant to set
   */
  public void setMontant(Double montant) {
    this.montant = montant;
  }

  /**
   * @return the typePayment
   */
  public String getTypePayment() {
    return typePayment;
  }

  /**
   * @param typePayment the typePayment to set
   */
  public void setTypePayment(String typePayment) {
    this.typePayment = typePayment;
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
   * @return the ischifcoPayed
   */
  public Boolean getIschifcoPayed() {
    return ischifcoPayed;
  }

  /**
   * @param ischifcoPayed the ischifcoPayed to set
   */
  public void setIschifcoPayed(Boolean ischifcoPayed) {
    this.ischifcoPayed = ischifcoPayed;
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
   * @return the recuNumeroSequence
   */
  public RecuNumeroSequence getRecuNumeroSequence() {
    return recuNumeroSequence;
  }

  /**
   * @param recuNumeroSequence the recuNumeroSequence to set
   */
  public void setRecuNumeroSequence(RecuNumeroSequence recuNumeroSequence) {
    this.recuNumeroSequence = recuNumeroSequence;
  }

  /**
   * @return the codePayement
   */
  public String getCodePayement() {
    return codePayement;
  }

  /**
   * @param codePayement the codePayement to set
   */
  public void setCodePayement(String codePayement) {
    this.codePayement = codePayement;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public AvoirClient getAvoirClient() {
    return AvoirClient;
  }

  public void setAvoirClient(AvoirClient avoirClient) {
    AvoirClient = avoirClient;
  }

}
