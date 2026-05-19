package crm.chifco.com.DTOclass;

public interface MoreThanOneInvoiceRecap {
  String getReferenceClient();

  String getFirst_name();

  String getLast_name();

  String getCin();

  Boolean getEtat_facture();

  // String getNom_type_paiement();

  Double getTotalFactures();

  Integer getCountfactures();
}
