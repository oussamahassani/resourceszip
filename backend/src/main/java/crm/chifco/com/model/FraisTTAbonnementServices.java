package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
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
@Table(name = "FraisTTAbonnementServices")
@EntityListeners(AuditingEntityListener.class)
public class FraisTTAbonnementServices implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "fraisTTAbonnementId")
  private Long fraisTTAbonnementId;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @Column(length = 175)
  private String numeroTelephone;

  @Column(length = 180)
  private String referenceTelecom;

  @Column(length = 200)
  private String dateConnection;

  @Column(length = 200)
  private String forfaitInternt;

  @Column(length = 200)
  private String catagorieInternt;

  @Column(length = 200)
  private Double prixService;

  @Column(length = 200)
  private Boolean isRaccordement;

  @Column(length = 200)
  private String codeFrais;

  @Column(length = 200)
  private String rechecheDate;

  private String userName;

  public FraisTTAbonnementServices() {
    super();
    // TODO Auto-generated constructor stub
  }

  public Long getFraisTTAbonnementId() {
    return fraisTTAbonnementId;
  }

  public void setFraisTTAbonnementId(Long fraisTTAbonnementId) {
    this.fraisTTAbonnementId = fraisTTAbonnementId;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getNumeroTelephone() {
    return numeroTelephone;
  }

  public void setNumeroTelephone(String numeroTelephone) {
    this.numeroTelephone = numeroTelephone;
  }

  public String getReferenceTelecom() {
    return referenceTelecom;
  }

  public void setReferenceTelecom(String referenceTelecom) {
    this.referenceTelecom = referenceTelecom;
  }

  public String getDateConnection() {
    return dateConnection;
  }

  public void setDateConnection(String dateConnection) {
    this.dateConnection = dateConnection;
  }

  public String getForfaitInternt() {
    return forfaitInternt;
  }

  public void setForfaitInternt(String forfaitInternt) {
    this.forfaitInternt = forfaitInternt;
  }

  public String getCatagorieInternt() {
    return catagorieInternt;
  }

  public void setCatagorieInternt(String catagorieInternt) {
    this.catagorieInternt = catagorieInternt;
  }

  public Double getPrixService() {
    return prixService;
  }

  public void setPrixService(Double prixService) {
    this.prixService = prixService;
  }

  public String getCodeFrais() {
    return codeFrais;
  }

  public void setCodeFrais(String codeFrais) {
    this.codeFrais = codeFrais;
  }

  public Boolean getIsRaccordement() {
    return isRaccordement;
  }

  public void setIsRaccordement(Boolean isRaccordement) {
    this.isRaccordement = isRaccordement;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getRechecheDate() {
    return rechecheDate;
  }

  public void setRechecheDate(String rechecheDate) {
    this.rechecheDate = rechecheDate;
  }

  @Override
  public String toString() {
    return "FraisTTAbonnementServices [fraisTTAbonnementId=" + fraisTTAbonnementId
        + ", createdDate=" + createdDate + ", numeroTelephone=" + numeroTelephone
        + ", referenceTelecom=" + referenceTelecom + ", dateConnection=" + dateConnection
        + ", forfaitInternt=" + forfaitInternt + ", catagorieInternt=" + catagorieInternt
        + ", prixService=" + prixService + ", isRaccordement=" + isRaccordement + ", codeFrais="
        + codeFrais + "]";
  }

}
