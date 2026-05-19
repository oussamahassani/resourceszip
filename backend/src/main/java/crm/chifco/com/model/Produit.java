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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "produits")
@EntityListeners(AuditingEntityListener.class)
public class Produit implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long produitId;

  private String produitNom;

  @Column(unique = true)
  private String produitCode;



  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private Boolean withIpFix = false;


  private Boolean isExtra = false;
  private Boolean isRacordement = false;
  private Boolean isDefault = true;
  private Boolean isActive = true;

  public Produit() {}

  public Produit(Long produitId, String produitNom, String produitCode, Date createdDate,
      Date modifiedDate, Boolean withIpFix, Boolean isExtra, Boolean isRacordement,
      Boolean isDefault, Boolean isActive) {
    super();
    this.produitId = produitId;
    this.produitNom = produitNom;
    this.produitCode = produitCode;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.withIpFix = withIpFix;
    this.isExtra = isExtra;
    this.isRacordement = isRacordement;
    this.isDefault = isDefault;
    this.isActive = isActive;
  }

  public Long getProduitId() {
    return produitId;
  }

  public void setProduitId(Long produitId) {
    this.produitId = produitId;
  }

  public String getProduitNom() {
    return produitNom;
  }

  public void setProduitNom(String produitNom) {
    this.produitNom = produitNom;
  }

  public String getProduitCode() {
    return produitCode;
  }

  public void setProduitCode(String produitCode) {
    this.produitCode = produitCode;
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

  public Boolean getWithIpFix() {
    return withIpFix;
  }

  public void setWithIpFix(Boolean withIpFix) {
    this.withIpFix = withIpFix;
  }

  public Boolean getIsExtra() {
    return isExtra;
  }

  public void setIsExtra(Boolean isExtra) {
    this.isExtra = isExtra;
  }

  public Boolean getIsRacordement() {
    return isRacordement;
  }

  public void setIsRacordement(Boolean isRacordement) {
    this.isRacordement = isRacordement;
  }

  public Boolean getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(Boolean isDefault) {
    this.isDefault = isDefault;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  @Override
  public String toString() {
    return "Produit [produitId=" + produitId + ", produitNom=" + produitNom + ", produitCode="
        + produitCode + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate
        + ", withIpFix=" + withIpFix + ", isExtra=" + isExtra + ", isRacordement=" + isRacordement
        + ", isDefault=" + isDefault + ", isActive=" + isActive + "]";
  }

}
