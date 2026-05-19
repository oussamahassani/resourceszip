package crm.chifco.com.templateclasse;

import java.util.Date;

public interface EncaissementNonPayee {

  String getref_facture();

  String getType();

  String getRef_avoir_client();

  String getlast_name();

  String getFirst_name();

  String getCin();

  String getType_de_payment();

  Date getdate();

  Date getcreated_date();

  Double getmontant_payer();

  Double getMontant_facture();

  Date getdate_echeance();

  Long getencaissement_id();

  String getlast_nameRevendeur();

  String getfirst_nameRevendeur();

  String getCodeRevendeur();

  Boolean getIsChifcoPayed();

  String getReferenceBordereau();

  Long getClient();

  Double getMontantAvoir();

  Date getCreadted_date_avoir();

}
