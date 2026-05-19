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
@EntityListeners(AuditingEntityListener.class)
@Table(name = "categorieproduitinternet")
public class CategorieProduitInternet implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long categorieProduitInternetId;

  private String categorieProduitInternetNom;

  @Column(unique = true)
  private String categorieProduitInternetCode;

  @CreatedDate

  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;;



  public CategorieProduitInternet() {}

  public CategorieProduitInternet(String categorieProduitInternetNom,
      String categorieProduitInternetCode) {
    super();
    this.categorieProduitInternetNom = categorieProduitInternetNom;
    this.categorieProduitInternetCode = categorieProduitInternetCode;
  }

  public Long getCategorieProduitInternetId() {
    return categorieProduitInternetId;
  }



  /**
   * @return the categorieProduitInternetNom
   */
  public String getCategorieProduitInternetNom() {
    return categorieProduitInternetNom;
  }

  /**
   * @param categorieProduitInternetId the categorieProduitInternetId to set
   */
  public void setCategorieProduitInternetId(Long categorieProduitInternetId) {
    this.categorieProduitInternetId = categorieProduitInternetId;
  }

  public void setCategorieProduitInternetNom(String categorieProduitInternetNom) {
    this.categorieProduitInternetNom = categorieProduitInternetNom;
  }

  public String getCategorieProduitInternetCode() {
    return categorieProduitInternetCode;
  }


  public void setCategorieProduitInternetCode(String categorieProduitInternetCode) {
    this.categorieProduitInternetCode = categorieProduitInternetCode;
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

  @Override
  public String toString() {
    return "CategorieProduitInternet [categorieProduitInternetId=" + categorieProduitInternetId
        + ", categorieProduitInternetNom=" + categorieProduitInternetNom
        + ", categorieProduitInternetCode=" + categorieProduitInternetCode + ", createdDate="
        + createdDate + ", modifiedDate=" + modifiedDate + "]";
  }



}
