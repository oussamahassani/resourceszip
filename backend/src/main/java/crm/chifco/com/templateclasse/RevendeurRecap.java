package crm.chifco.com.templateclasse;

import crm.chifco.com.utils.CrmUtils;

public interface RevendeurRecap {

  String getFirstname();

  String getAdresse();

  String getLastname();

  Long getSumfacture();

  Double getMontant();

  Double getAvoirConsomme();

  Double getTotalAvoir();

  Double getMontantpayer();

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

  default Double getMontantNonpayer() {
    Double montantNonPayer = getMontantnonpayer();
    // Parse the formatted value back to Double
    return CrmUtils.formatDoubleInput(montantNonPayer);
  }

}
