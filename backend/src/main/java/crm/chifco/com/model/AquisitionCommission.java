package crm.chifco.com.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "aquisitionCommission")
public class AquisitionCommission implements Serializable {


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long aquisitionId;


  private Long idProduit;

  private Long startInterval;

  private Long endInterval;

  private Double montantDemande;
  private Double montantActivateEcheance;
  private Double montantActivateHorsEcheance;



  public AquisitionCommission() {
    super();
    // TODO Auto-generated constructor stub
  }

  public AquisitionCommission(Long aquisitionId, Long idProduit, Long startInterval,
      Long endinterval, Double montantDemande, Double montantActivateEcheance,
      Double montantActivateHorsEcheance) {
    super();
    this.aquisitionId = aquisitionId;
    this.idProduit = idProduit;
    this.startInterval = startInterval;
    this.endInterval = endinterval;
    this.montantDemande = montantDemande;
    this.montantActivateEcheance = montantActivateEcheance;
    this.montantActivateHorsEcheance = montantActivateHorsEcheance;
  }

  public Long getAquisitionId() {
    return aquisitionId;
  }

  public void setAquisitionId(Long aquisitionId) {
    this.aquisitionId = aquisitionId;
  }

  public Long getIdProduit() {
    return idProduit;
  }

  public void setIdProduit(Long idProduit) {
    this.idProduit = idProduit;
  }

  public Long getStartInterval() {
    return startInterval;
  }

  public void setStartInterval(Long startInterval) {
    this.startInterval = startInterval;
  }

  public Long getEndInterval() {
    return endInterval;
  }

  public void setEndInterval(Long endInterval) {
    this.endInterval = endInterval;
  }

  public Double getMontantDemande() {
    return montantDemande;
  }

  public void setMontantDemande(Double montantDemande) {
    this.montantDemande = montantDemande;
  }

  public Double getMontantActivateEcheance() {
    return montantActivateEcheance;
  }

  public void setMontantActivateEcheance(Double montantActivateEcheance) {
    this.montantActivateEcheance = montantActivateEcheance;
  }

  public Double getMontantActivateHorsEcheance() {
    return montantActivateHorsEcheance;
  }

  public void setMontantActivateHorsEcheance(Double montantActivateHorsEcheance) {
    this.montantActivateHorsEcheance = montantActivateHorsEcheance;
  }

  @Override
  public String toString() {
    return "AquisitionCommission [aquisitionId=" + aquisitionId + ", idProduit=" + idProduit
        + ", startInterval=" + startInterval + ", endInterval=" + endInterval + ", montantDemande="
        + montantDemande + ", montantActivateEcheance=" + montantActivateEcheance
        + ", montantActivateHorsEcheance=" + montantActivateHorsEcheance + "]";
  }



}
