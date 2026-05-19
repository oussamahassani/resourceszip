package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "entriesFacture")
@EntityListeners(AuditingEntityListener.class)
public class EntryFactures implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long entriesId;

  private Double prixUnitaireHT;
  private Integer quantiter;
  private Double prixTtc;

  private Double prixTotalHT;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "produitId")
  private Produit produit;

  private Double prixTva;
  private String productName;
  private Long pourcentageTva;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "packId")
  private Pack pack;

  public EntryFactures() {}

  public EntryFactures(Long entriesId, Double prixUnitaireHT, Integer quantiter, Double prixTtc,
      Double prixTotalHT, Date createdDate, Produit produit, Double prixTva, String productName,
      Long pourcentageTva) {
    super();
    this.entriesId = entriesId;
    this.prixUnitaireHT = prixUnitaireHT;
    this.quantiter = quantiter;
    this.prixTtc = prixTtc;
    this.prixTotalHT = prixTotalHT;
    this.createdDate = createdDate;
    this.produit = produit;
    this.prixTva = prixTva;
    this.productName = productName;
    this.pourcentageTva = pourcentageTva;
  }

  /**
   * @return the entriesId
   */
  public Long getEntriesId() {
    return entriesId;
  }

  /**
   * @param entriesId the entriesId to set
   */
  public void setEntriesId(Long entriesId) {
    this.entriesId = entriesId;
  }

  /**
   * @return the prixUnitaireHT
   */
  public Double getPrixUnitaireHT() {
    return prixUnitaireHT;
  }

  /**
   * @param prixUnitaireHT the prixUnitaireHT to set
   */
  public void setPrixUnitaireHT(Double prixUnitaireHT) {
    this.prixUnitaireHT = prixUnitaireHT;
  }

  /**
   * @return the quantiter
   */
  public Integer getQuantiter() {
    return quantiter;
  }

  /**
   * @param quantiter the quantiter to set
   */
  public void setQuantiter(Integer quantiter) {
    this.quantiter = quantiter;
  }

  /**
   * @return the prixTtc
   */
  public Double getPrixTtc() {
    return prixTtc;
  }

  /**
   * @param prixTtc the prixTtc to set
   */
  public void setPrixTtc(Double prixTtc) {
    this.prixTtc = prixTtc;
  }

  /**
   * @return the prixTotalHT
   */
  public Double getPrixTotalHT() {
    return prixTotalHT;
  }

  /**
   * @param prixTotalHT the prixTotalHT to set
   */
  public void setPrixTotalHT(Double prixTotalHT) {
    this.prixTotalHT = prixTotalHT;
  }

  /**
   * @return the createdDate
   */
  public Date getCreatedDate() {
    return createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return the produit
   */
  public Produit getProduit() {
    return produit;
  }

  /**
   * @param produit the produit to set
   */
  public void setProduit(Produit produit) {
    this.produit = produit;
  }

  /**
   * @return the prixTva
   */
  public Double getPrixTva() {
    return prixTva;
  }

  /**
   * @param prixTva the prixTva to set
   */
  public void setPrixTva(Double prixTva) {
    this.prixTva = prixTva;
  }

  /**
   * @return the productName
   */
  public String getProductName() {
    return productName;
  }

  /**
   * @param productName the productName to set
   */
  public void setProductName(String productName) {
    this.productName = productName;
  }

  /**
   * @return the pourcentageTva
   */
  public Long getPourcentageTva() {
    return pourcentageTva;
  }

  /**
   * @param pourcentageTva the pourcentageTva to set
   */
  public void setPourcentageTva(Long pourcentageTva) {
    this.pourcentageTva = pourcentageTva;
  }

  public Pack getPack() {
    return pack;
  }

  public void setPack(Pack pack) {
    this.pack = pack;
  }

  @Override
  public String toString() {
    return "EntryFactures [entriesId=" + entriesId + ", prixUnitaireHT=" + prixUnitaireHT
        + ", quantiter=" + quantiter + ", prixTtc=" + prixTtc + ", prixTotalHT=" + prixTotalHT
        + ", createdDate=" + createdDate + ", produit=" + produit + ", prixTva=" + prixTva
        + ", productName=" + productName + ", pourcentageTva=" + pourcentageTva + "]";
  }


}
