package crm.chifco.com.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "modelePaiementCommission")
public class ModelePaiementCommission {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long ligneId;

  private Long idProduit;

  private Double tauxHorsEcheance;
  private Double tauxEcheance;



  public ModelePaiementCommission() {
    super();
    // TODO Auto-generated constructor stub
  }

  public ModelePaiementCommission(Long ligneId, Long idProduit, Double tauxHorsEcheance,
      Double tauxEcheance) {
    super();
    this.ligneId = ligneId;
    this.idProduit = idProduit;
    this.tauxHorsEcheance = tauxHorsEcheance;
    this.tauxEcheance = tauxEcheance;
  }

  public Long getLigneId() {
    return ligneId;
  }

  public void setLigneId(Long ligneId) {
    this.ligneId = ligneId;
  }

  public Long getIdProduit() {
    return idProduit;
  }

  public void setIdProduit(Long idProduit) {
    this.idProduit = idProduit;
  }

  public Double getTauxHorsEcheance() {
    return tauxHorsEcheance;
  }

  public void setTauxHorsEcheance(Double tauxHorsEcheance) {
    this.tauxHorsEcheance = tauxHorsEcheance;
  }

  public Double getTauxEcheance() {
    return tauxEcheance;
  }

  public void setTauxEcheance(Double tauxEcheance) {
    this.tauxEcheance = tauxEcheance;
  }

  @Override
  public String toString() {
    return "ModelePaiementCommission [ligneId=" + ligneId + ", idProduit=" + idProduit
        + ", tauxHorsEcheance=" + tauxHorsEcheance + ", tauxEcheance=" + tauxEcheance + "]";
  }



}
