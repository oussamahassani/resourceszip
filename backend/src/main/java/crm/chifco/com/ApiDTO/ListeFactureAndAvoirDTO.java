package crm.chifco.com.ApiDTO;

import java.util.Date;

public interface ListeFactureAndAvoirDTO {
  Long getFacture_id();

  Long getAvoir_id();

  String getRef_facture();

  Double getMontant_payer();

  Date getDateDeDebut();

  Date getEcheance();

  Date getDateDeFin();

  Date getDateDePayement();

  String getTypeFacture();

  Boolean getIsFactureResilation();


}
