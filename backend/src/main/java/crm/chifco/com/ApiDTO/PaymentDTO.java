package crm.chifco.com.ApiDTO;

import java.util.List;

public class PaymentDTO {
  private List<String> factures;
  private String methodePayment;
  private String bankname;
  private String transactionId;
  private String createdBy;
  private List<String> avoirListe;

  public List<String> getFactures() {
    return factures;
  }

  public void setFactures(List<String> factures) {
    this.factures = factures;
  }

  public String getMethodePayment() {
    return methodePayment;
  }

  public void setMethodePayment(String methodePayment) {
    this.methodePayment = methodePayment;
  }

  public String getBankname() {
    return bankname;
  }

  public void setBankname(String bankname) {
    this.bankname = bankname;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public List<String> getAvoirListe() {
    return avoirListe;
  }

  public void setAvoirListe(List<String> avoirListe) {
    this.avoirListe = avoirListe;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

}
