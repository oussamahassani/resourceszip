package crm.chifco.com.DTOclass;

import crm.chifco.com.utils.CrmUtils;

public interface TopRevendeur {
  String getFirstname();

  String getAdresse();

  String getLastname();

  Long getSumfacture();

  Double getMontant();

  Double getAvoirConsomme();

  Double getTotalAvoir();

  Double getMontantpayer();

  Integer getCountAllDemandeAccepted();

  Long getPlafon_revendeur();

  Long getUserid();

  String getCode_user();

  String getVille_name();

  String getGouvernorat_name();

  Boolean getEnabled();

  Double getMontantnonpayer();

  String getAssignedUserFirstName();

  String getAssignedUserLastName();

  Long getNbrFacturepayer();

  Long getNbrFactureNonpayer();

  Long getNbrBordereau();

  String getCodeUserAssignee();

  Double getChiffre_affaire();


  default Double getMontantNonpayer() {
    Double montantNonPayer = getMontantnonpayer();
    // Parse the formatted value back to Double
    return CrmUtils.formatDoubleInput(montantNonPayer);
  }
}
