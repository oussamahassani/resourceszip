package crm.chifco.com.DTOclass;

import java.util.Date;

public class PayementDataDTO {
  private Long payementid;

  private Double montant;


  private String typePayment;


  private Long facture;


  private Long AvoirClient;


  private Date createdDate;


  private Date modifiedDate;

  private Boolean ischifcoPayed;

  private String numeroCarte;
  private String numeroCheque;
  private String nomBank;


  private String lastNameUser;
  private String firstNameUser;
  private String codeUser;


  public PayementDataDTO(Long payementid, Double montant, String typePayment, Long facture,
      Long avoirClient, Date createdDate, Date modifiedDate, Boolean ischifcoPayed,
      String numeroCarte, String numeroCheque, String nomBank) {
    super();
    this.payementid = payementid;
    this.montant = montant;
    this.typePayment = typePayment;
    this.facture = facture;
    AvoirClient = avoirClient;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.ischifcoPayed = ischifcoPayed;
    this.numeroCarte = numeroCarte;
    this.numeroCheque = numeroCheque;
    this.nomBank = nomBank;
  }

  public PayementDataDTO(Long payementid, Double montant, String typePayment, Long facture,
      Long avoirClient, Date createdDate, Date modifiedDate, Boolean ischifcoPayed,
      String numeroCarte, String numeroCheque, String nomBank, String lastNameUser,
      String firstNameUser, String codeUser) {
    super();
    this.payementid = payementid;
    this.montant = montant;
    this.typePayment = typePayment;
    this.facture = facture;
    AvoirClient = avoirClient;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.ischifcoPayed = ischifcoPayed;
    this.numeroCarte = numeroCarte;
    this.numeroCheque = numeroCheque;
    this.nomBank = nomBank;
    this.lastNameUser = lastNameUser;
    this.firstNameUser = firstNameUser;
    this.codeUser = codeUser;
  }

  public Long getPayementid() {
    return payementid;
  }

  public void setPayementid(Long payementid) {
    this.payementid = payementid;
  }

  public Double getMontant() {
    return montant;
  }

  public void setMontant(Double montant) {
    this.montant = montant;
  }

  public String getTypePayment() {
    return typePayment;
  }

  public void setTypePayment(String typePayment) {
    this.typePayment = typePayment;
  }

  public Long getFacture() {
    return facture;
  }

  public void setFacture(Long facture) {
    this.facture = facture;
  }

  public Long getAvoirClient() {
    return AvoirClient;
  }

  public void setAvoirClient(Long avoirClient) {
    AvoirClient = avoirClient;
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

  public Boolean getIschifcoPayed() {
    return ischifcoPayed;
  }

  public void setIschifcoPayed(Boolean ischifcoPayed) {
    this.ischifcoPayed = ischifcoPayed;
  }

  public String getNumeroCarte() {
    return numeroCarte;
  }

  public void setNumeroCarte(String numeroCarte) {
    this.numeroCarte = numeroCarte;
  }

  public String getNumeroCheque() {
    return numeroCheque;
  }

  public void setNumeroCheque(String numeroCheque) {
    this.numeroCheque = numeroCheque;
  }

  public String getNomBank() {
    return nomBank;
  }

  public void setNomBank(String nomBank) {
    this.nomBank = nomBank;
  }

  public String getLastNameUser() {
    return lastNameUser;
  }

  public void setLastNameUser(String lastNameUser) {
    this.lastNameUser = lastNameUser;
  }

  public String getFirstNameUser() {
    return firstNameUser;
  }

  public void setFirstNameUser(String firstNameUser) {
    this.firstNameUser = firstNameUser;
  }

  public String getCodeUser() {
    return codeUser;
  }

  public void setCodeUser(String codeUser) {
    this.codeUser = codeUser;
  }

  @Override
  public String toString() {
    return "PayementDataDTO [payementid=" + payementid + ", montant=" + montant + ", typePayment="
        + typePayment + ", facture=" + facture + ", AvoirClient=" + AvoirClient + ", createdDate="
        + createdDate + ", modifiedDate=" + modifiedDate + ", ischifcoPayed=" + ischifcoPayed
        + ", numeroCarte=" + numeroCarte + ", numeroCheque=" + numeroCheque + ", nomBank=" + nomBank
        + "]";
  }



}
