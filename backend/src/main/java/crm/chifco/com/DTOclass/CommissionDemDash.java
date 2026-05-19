package crm.chifco.com.DTOclass;

public interface CommissionDemDash {
  String getFirstname();

  String getLastname();

  Long getUserid();

  String getCodeuser();

  Integer getCountAllDemandeEnAttente();

  Integer getCountAllDemandeAccecpted();

  Integer getCountAllDemandeRejected();

  Integer getCountAllDemandeClassification();

  Integer getCountDemandePasseToActivate();


}
