package crm.chifco.com.ApiDTO;

public class entryAvoirClient {
  private Double montantHt;
  private Double baseTva;
  private String labelle;

  public entryAvoirClient() {
    super();
    // TODO Auto-generated constructor stub
  }

  public entryAvoirClient(Double montantHt, Double baseTva, String labelle) {
    super();
    this.montantHt = montantHt;
    this.baseTva = baseTva;
    this.labelle = labelle;
  }

  public Double getMontantHt() {
    return montantHt;
  }

  public void setMontantHt(Double montantHt) {
    this.montantHt = montantHt;
  }

  public Double getBaseTva() {
    return baseTva;
  }

  public void setBaseTva(Double baseTva) {
    this.baseTva = baseTva;
  }

  public String getLabelle() {
    return labelle;
  }

  public void setLabelle(String labelle) {
    this.labelle = labelle;
  }

  @Override
  public String toString() {
    return "entryAvoirClient [montantHt=" + montantHt + ", baseTva=" + baseTva + ", labelle="
        + labelle + "]";
  }



}
