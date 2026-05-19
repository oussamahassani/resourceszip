package crm.chifco.com.templateclasse;

import java.util.Date;

public interface InvoiceAvoir {

  Long getFacture_id();

  Long getAvoir_id();

  String getRef_facture();

  Date getDate_de_debut();

  Date getDate_de_fin();

  // @Value("#{myCustomTypeConverter.formatMontantPayer(target.getMontant_payer())}")
  double getMontant_payer();

  Date getDate_echeance();

  Boolean getEtat_facture();

  String getTypeFacture();
}
