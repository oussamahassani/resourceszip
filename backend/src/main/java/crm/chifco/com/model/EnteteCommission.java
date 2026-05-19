package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "enteteCommission")
public class EnteteCommission implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long enteteId;


  private Long idRevendeur;
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Temporal(TemporalType.DATE)
  private Date payedDate;


  private Boolean isPayed;

  private Double totalHt;

  private Double totalTtc;

  private Long nbPalier;


  @OneToMany
  @JoinColumn(name = "comissionId")
  private List<LigneCommission> ligneCommission;


  public EnteteCommission() {
    super();
    // TODO Auto-generated constructor stub
  }


  public EnteteCommission(Long enteteId, Long idRevendeur, Date createdDate, Date modifiedDate,
      Date payedDate, Boolean isPayed, Double totalHt, Double totalTtc, Long nbPalier,
      List<LigneCommission> ligneCommission) {
    super();
    this.enteteId = enteteId;
    this.idRevendeur = idRevendeur;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.payedDate = payedDate;
    this.isPayed = isPayed;
    this.totalHt = totalHt;
    this.totalTtc = totalTtc;
    this.nbPalier = nbPalier;
    this.ligneCommission = ligneCommission;
  }


  public Long getEnteteId() {
    return enteteId;
  }


  public void setEnteteId(Long enteteId) {
    this.enteteId = enteteId;
  }


  public Long getIdRevendeur() {
    return idRevendeur;
  }


  public void setIdRevendeur(Long idRevendeur) {
    this.idRevendeur = idRevendeur;
  }


  public Date getCreatedDate() {
    return createdDate;
  }


  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }


  public Date getModifiedDate() {
    return modifiedDate;
  }


  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  public Date getPayedDate() {
    return payedDate;
  }


  public void setPayedDate(Date payedDate) {
    this.payedDate = payedDate;
  }


  public Boolean getIsPayed() {
    return isPayed;
  }


  public void setIsPayed(Boolean isPayed) {
    this.isPayed = isPayed;
  }


  public Double getTotalHt() {
    return totalHt;
  }


  public void setTotalHt(Double totalHt) {
    this.totalHt = totalHt;
  }


  public Double getTotalTtc() {
    return totalTtc;
  }


  public void setTotalTtc(Double totalTtc) {
    this.totalTtc = totalTtc;
  }


  public Long getNbPalier() {
    return nbPalier;
  }


  public void setNbPalier(Long nbPalier) {
    this.nbPalier = nbPalier;
  }


  public List<LigneCommission> getLigneCommission() {
    return ligneCommission;
  }


  public void setLigneCommission(List<LigneCommission> ligneCommission) {
    this.ligneCommission = ligneCommission;
  }


  @Override
  public String toString() {
    return "EnteteCommission [enteteId=" + enteteId + ", idRevendeur=" + idRevendeur
        + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", payedDate="
        + payedDate + ", isPayed=" + isPayed + ", totalHt=" + totalHt + ", totalTtc=" + totalTtc
        + ", nbPalier=" + nbPalier + ", ligneCommission=" + ligneCommission + "]";
  }


}
