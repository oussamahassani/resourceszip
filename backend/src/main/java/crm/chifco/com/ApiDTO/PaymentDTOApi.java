package crm.chifco.com.ApiDTO;

import java.util.Date;

public class PaymentDTOApi {

  private Double montant;


  private String typePayment;


  private String facture;


  private String refAvoirClient;


  private Date createdDate;


  private Date modifiedDate;


  private String numeroCarte;
  private String numeroCheque;
  private String nomBank;



  public PaymentDTOApi(Double montant, String typePayment, String facture, String avoirClient,
      Date createdDate, Date modifiedDate, String numeroCarte, String numeroCheque,
      String nomBank) {
    super();
    this.montant = montant;
    this.typePayment = typePayment;
    this.facture = facture;
    this.refAvoirClient = avoirClient;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.numeroCarte = numeroCarte;
    this.numeroCheque = numeroCheque;
    this.nomBank = nomBank;

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

  public String getFacture() {
    return facture;
  }

  public void setFacture(String facture) {
    this.facture = facture;
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

  public Date getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
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


  @Override
  public String toString() {
    return "PayementDataDTO [ montant=" + montant + ", typePayment=" + typePayment + ", facture="
        + facture + ", refAvoirClient=" + refAvoirClient + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + ", numeroCarte=" + numeroCarte + ", numeroCheque="
        + numeroCheque + ", nomBank=" + nomBank + "]";
  }



}
