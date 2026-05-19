package crm.chifco.com.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "EntryTvaFacture")
public class EntryTvaFacture implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long entryTvaFactureId;

  private Double tauxTva;
  private Double base;
  private Double montant;



  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "factureid")
  private Facture facture;

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

  public Facture getFacture() {
    return facture;
  }

  public void setFacture(Facture facture) {
    this.facture = facture;
  }
}
