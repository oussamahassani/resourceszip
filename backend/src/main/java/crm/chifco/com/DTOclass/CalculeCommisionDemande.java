package crm.chifco.com.DTOclass;

public class CalculeCommisionDemande {

  private Long countAllDemandeClassification;
  private Long countAllDemandeRejected;
  private Long countAllDemandeAccecpted;
  private Long countDemandeAcceptedDebit10Or12;
  private Long countDemandeAcceptedDebit20;
  private Long countDemandeAcceptedDebit30;
  private Long countDemandeAcceptedDebit50;
  private Long countDemandeAcceptedDebit100;
  private Long countDemandePasseToActivate;
  private Long countAllDemandeEnAttente;

  public CalculeCommisionDemande(Long countAllDemandeAccecpted, Long countAllDemandeRejected,
      Long countAllDemandeClassification) {
    super();

    this.countAllDemandeAccecpted = countAllDemandeAccecpted;
    this.countAllDemandeRejected = countAllDemandeRejected;

    this.countAllDemandeClassification = countAllDemandeClassification;
  }

  public CalculeCommisionDemande(Long countAllDemandeEnAttente, Long countAllDemandeAccecpted,
      Long countAllDemandeRejected, Long countAllDemandeClassification,
      Long countDemandeAcceptedDebit10Or12, Long countDemandeAcceptedDebit20,
      Long countDemandeAcceptedDebit30, Long countDemandeAcceptedDebit50,
      Long countDemandeAcceptedDebit100, Long countDemandePasseToActivate) {
    super();
    this.countAllDemandeEnAttente = countAllDemandeEnAttente;
    this.countAllDemandeClassification = countAllDemandeClassification;
    this.countAllDemandeRejected = countAllDemandeRejected;
    this.countAllDemandeAccecpted = countAllDemandeAccecpted;
    this.countDemandeAcceptedDebit10Or12 = countDemandeAcceptedDebit10Or12;
    this.countDemandeAcceptedDebit30 = countDemandeAcceptedDebit30;
    this.countDemandeAcceptedDebit20 = countDemandeAcceptedDebit20;
    this.countDemandeAcceptedDebit50 = countDemandeAcceptedDebit50;
    this.countDemandeAcceptedDebit100 = countDemandeAcceptedDebit100;
    this.countDemandePasseToActivate = countDemandePasseToActivate;

  }

  public Long getCountAllDemandeClassification() {
    return countAllDemandeClassification;
  }

  public void setCountAllDemandeClassification(Long countAllDemandeClassification) {
    this.countAllDemandeClassification = countAllDemandeClassification;
  }

  public Long getCountAllDemandeRejected() {
    return countAllDemandeRejected;
  }

  public void setCountAllDemandeRejected(Long countAllDemandeRejected) {
    this.countAllDemandeRejected = countAllDemandeRejected;
  }

  public Long getCountAllDemandeAccecpted() {
    return countAllDemandeAccecpted;
  }

  public void setCountAllDemandeAccecpted(Long countAllDemandeAccecpted) {
    this.countAllDemandeAccecpted = countAllDemandeAccecpted;
  }

  public Long getCountDemandeAcceptedDebit10Or12() {
    return countDemandeAcceptedDebit10Or12;
  }

  public void setCountDemandeAcceptedDebit10Or12(Long countDemandeAcceptedDebit10Or12) {
    this.countDemandeAcceptedDebit10Or12 = countDemandeAcceptedDebit10Or12;
  }

  public Long getCountDemandeAcceptedDebit20() {
    return countDemandeAcceptedDebit20;
  }

  public void setCountDemandeAcceptedDebit20(Long countDemandeAcceptedDebit20) {
    this.countDemandeAcceptedDebit20 = countDemandeAcceptedDebit20;
  }

  public Long getCountDemandeAcceptedDebit50() {
    return countDemandeAcceptedDebit50;
  }

  public void setCountDemandeAcceptedDebit50(Long countDemandeAcceptedDebit50) {
    this.countDemandeAcceptedDebit50 = countDemandeAcceptedDebit50;
  }

  public Long getCountDemandeAcceptedDebit100() {
    return countDemandeAcceptedDebit100;
  }

  public void setCountDemandeAcceptedDebit100(Long countDemandeAcceptedDebit100) {
    this.countDemandeAcceptedDebit100 = countDemandeAcceptedDebit100;
  }

  public Long getCountDemandeAcceptedDebit30() {
    return countDemandeAcceptedDebit30;
  }

  public void setCountDemandeAcceptedDebit30(Long countDemandeAcceptedDebit30) {
    this.countDemandeAcceptedDebit30 = countDemandeAcceptedDebit30;
  }

  public Long getCountDemandePasseToActivate() {
    return countDemandePasseToActivate;
  }

  public void setCountDemandePasseToActivate(Long countDemandePasseToActivate) {
    this.countDemandePasseToActivate = countDemandePasseToActivate;
  }

  public Long getCountAllDemandeEnAttente() {
    return countAllDemandeEnAttente;
  }

  public void setCountAllDemandeEnAttente(Long countAllDemandeEnAttente) {
    this.countAllDemandeEnAttente = countAllDemandeEnAttente;
  }

}
