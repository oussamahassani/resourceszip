package crm.chifco.com.templateclasse;

import java.util.Date;

public interface ListeFactureAndAvoirNonPayeDTO {

  Long getFacture_id();

  Long getAvoir_id();

  String getRef_facture();

  Double getMontant_payer();

  Date getDateDeDebut();

  Date getEcheance();

  Date getDateDeFin();

  String getTypeFacture();

  Boolean getIsFactureResilation();
 
  public byte[] getPdf_facture();

}
