package crm.chifco.com.templateclasse;

import java.io.Serializable;
import java.time.Instant;

public class facturestemplate implements Serializable {

  private Double prix_unitaire;
  private Double prix_Total;
  private Integer quantiter;
  private Double Prixttc;
  private Instant createddate;
  private Double prixtva;
  private String nameproduit;
  // private long ptva ;

  public facturestemplate() {}

  public facturestemplate(Double prix_unitaire, Double prix_Total, Integer quantiter,
      Double Prixttc, Instant createddate, Double prixtva, String nameproduit) {
    this.prix_unitaire = prix_unitaire;
    this.prix_Total = prix_Total;
    this.quantiter = quantiter;
    this.Prixttc = Prixttc;
    this.createddate = createddate;
    // this.ptva = pourcentagetva ;
    this.prixtva = prixtva;
    this.nameproduit = nameproduit;

  }

  public Double getPrixUnitaire() {
    return prix_unitaire;
  }

  public void setPrixUnitaire(Double prixUnitaire) {
    this.prix_unitaire = prixUnitaire;
  }

  public void setprix_Total(Double prix_Total) {
    this.prix_Total = prix_Total;
  }

  public Double getprix_Total() {
    return this.prix_Total;
  }

  public void setquantite(Integer quantiter) {
    this.quantiter = quantiter;
  }

  public Integer getquantiter() {
    return quantiter;
  }

  public void setPrixttc(Double Prixttc) {
    this.Prixttc = Prixttc;
  }

  public Double getPrixttc() {
    return Prixttc;
  }

  public Instant getCreatedDate() {
    return createddate;
  }

  public void setCreatedDate(Instant createddate) {
    this.createddate = createddate;
  }

  public void setprixtva(Double prixtva) {
    this.prixtva = prixtva;
  }

  public Double getprixtva() {
    return prixtva;
  }

  public void setnameproduit(String nameproduit) {
    this.nameproduit = nameproduit;
  }

  public String getNameproduit() {
    return nameproduit;

  }

  // public void setpourcentagetva(Long pourcentagetva) {
  // this.ptva= pourcentagetva;
  // }
  // public Long getPourcentagetva() {
  // return ptva ;
  // }
  @Override
  public String toString() {
    return "EntryCommande{" + ", prix_unitaire=" + prix_unitaire + ", prix_Total=" + prix_Total
        + ", quantiter=" + quantiter + ", createddate=" + createddate + ", Prixttc=" + Prixttc
        + ", createddate=" + createddate + +prixtva + ", nameproduit=" + nameproduit +
        // ", ptva="+ptva+

        '}';
  }
}
