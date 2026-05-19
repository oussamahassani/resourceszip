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
@Table(name = "DetailsCommissionDemande")
@EntityListeners(AuditingEntityListener.class)
public class DetailsCommissionDemande implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String referenceClient;

  private String statutCommission;

  private Boolean etatCommission;

  private Double montantCommission;

  private Long commissionId;

  public DetailsCommissionDemande() {
    super();
  }

  public DetailsCommissionDemande(Long id, String referenceClient, String statutCommission,
      Boolean etatCommission, Double montantCommission, Long commissionId) {
    super();
    this.id = id;
    this.referenceClient = referenceClient;
    this.statutCommission = statutCommission;
    this.etatCommission = etatCommission;
    this.montantCommission = montantCommission;
    this.commissionId = commissionId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getReferenceClient() {
    return referenceClient;
  }

  public void setReferenceClient(String referenceClient) {
    this.referenceClient = referenceClient;
  }

  public String getStatutCommission() {
    return statutCommission;
  }

  public void setStatutCommission(String statutCommission) {
    this.statutCommission = statutCommission;
  }

  public Boolean getEtatCommission() {
    return etatCommission;
  }

  public void setEtatCommission(Boolean etatCommission) {
    this.etatCommission = etatCommission;
  }

  public Double getMontantCommission() {
    return montantCommission;
  }

  public void setMontantCommission(Double montantCommission) {
    this.montantCommission = montantCommission;
  }

  public Long getCommissionId() {
    return commissionId;
  }

  public void setCommissionId(Long commissionId) {
    this.commissionId = commissionId;
  }

  @Override
  public String toString() {
    return "DetailsCommissionDemande [id=" + id + ", referenceClient=" + referenceClient
        + ", statutCommission=" + statutCommission + ", etatCommission=" + etatCommission
        + ", montantCommission=" + montantCommission + ", commissionId=" + commissionId + "]";
  }

}
