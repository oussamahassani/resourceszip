package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ligneCommission")
public class LigneCommission implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long ligneId;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;


  private Double montantCommision;
  private String typeCommision;
  private Long idFacture;


  public LigneCommission() {
    super();
    // TODO Auto-generated constructor stub
  }

  public LigneCommission(Long ligneId, Date createdDate, Double montantCommision,
      String typeCommision, Long idFacture) {
    super();
    this.ligneId = ligneId;
    this.createdDate = createdDate;
    this.montantCommision = montantCommision;
    this.typeCommision = typeCommision;
    this.idFacture = idFacture;
  }

  public Long getLigneId() {
    return ligneId;
  }

  public void setLigneId(Long ligneId) {
    this.ligneId = ligneId;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Double getMontantCommision() {
    return montantCommision;
  }

  public void setMontantCommision(Double montantCommision) {
    this.montantCommision = montantCommision;
  }

  public String getTypeCommision() {
    return typeCommision;
  }

  public void setTypeCommision(String typeCommision) {
    this.typeCommision = typeCommision;
  }

  public Long getIdFacture() {
    return idFacture;
  }

  public void setIdFacture(Long idFacture) {
    this.idFacture = idFacture;
  }

  @Override
  public String toString() {
    return "LigneCommission [ligneId=" + ligneId + ", createdDate=" + createdDate
        + ", montantCommision=" + montantCommision + ", typeCommision=" + typeCommision
        + ", idFacture=" + idFacture + "]";
  }



}
