package crm.chifco.com.crmMobile;

public class DemandeModemRequest {
  private Long quantite;
  private String typemodem;

  public Long getQuantite() {
    return quantite;
  }

  public void setQuantite(Long quantite) {
    this.quantite = quantite;
  }

  public String getTypemodem() {
    return typemodem;
  }

  public void setTypemodem(String typemodem) {
    this.typemodem = typemodem;
  }

  public DemandeModemRequest(Long quantite, String typemodem) {
    super();
    this.quantite = quantite;
    this.typemodem = typemodem;
  }

  public DemandeModemRequest() {
    super();
  }

}
