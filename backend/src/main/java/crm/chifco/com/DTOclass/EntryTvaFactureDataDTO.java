package crm.chifco.com.DTOclass;

public class EntryTvaFactureDataDTO {
  private Long entryTvaFactureId;

  private Double tauxTva;
  private Double base;
  private Double montant;
  private Long factureId;

  public EntryTvaFactureDataDTO(Long entryTvaFactureId, Double tauxTva, Double base, Double montant,
      Long factureId) {

    this.entryTvaFactureId = entryTvaFactureId;
    this.tauxTva = tauxTva;
    this.base = base;
    this.montant = montant;
    this.factureId = factureId;
  }

  public EntryTvaFactureDataDTO(Long entryTvaFactureId, Double tauxTva, Double base,
      Double montant) {

    this.entryTvaFactureId = entryTvaFactureId;
    this.tauxTva = tauxTva;
    this.base = base;
    this.montant = montant;

  }


  public Long getEntryTvaFactureId() {
    return entryTvaFactureId;
  }

  public void setEntryTvaFactureId(Long entryTvaFactureId) {
    this.entryTvaFactureId = entryTvaFactureId;
  }

  public Double getTauxTva() {
    return tauxTva;
  }

  public void setTauxTva(Double tauxTva) {
    this.tauxTva = tauxTva;
  }

  public Double getBase() {
    return base;
  }

  public void setBase(Double base) {
    this.base = base;
  }

  public Double getMontant() {
    return montant;
  }

  public void setMontant(Double montant) {
    this.montant = montant;
  }

  public Long getFactureId() {
    return factureId;
  }

  public void setFactureId(Long factureId) {
    this.factureId = factureId;
  }

  @Override
  public String toString() {
    return "EntryTvaFactureDataDTO [entryTvaFactureId=" + entryTvaFactureId + ", tauxTva=" + tauxTva
        + ", base=" + base + ", montant=" + montant + ", factureId=" + factureId + "]";
  }



}
