package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import crm.chifco.com.service.CodePackGenerator;

@Entity
@Table(name = "pack")
@EntityListeners(AuditingEntityListener.class)
public class Pack implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "packId")
  private Long packId;

  @ManyToOne
  @JoinColumn(name = "offreId")
  private Offre offre;

  private String title;
  private String description;

  private Long idPackBase;
  private Boolean payLater = false;

  @ManyToOne
  @JoinColumn(name = "categorie_pack", nullable = false)
  private CategorieProduitInternet categoriePack;


  @ManyToOne
  @JoinColumn(name = "engagement")
  private Engagement engagement;

  @GeneratorType(type = CodePackGenerator.class, when = GenerationTime.INSERT)
  private String codePack;

  private String debitPack;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Pack() {
    super();
    // TODO Auto-generated constructor stub
  }

  public Pack(Long packId, Offre offre, String title, String description, Long idPackBase,
      CategorieProduitInternet categoriePack, String codePack, String debitPack, Date createdDate,
      Date modifiedDate) {
    super();
    this.packId = packId;
    this.offre = offre;
    this.title = title;
    this.description = description;
    this.idPackBase = idPackBase;
    this.categoriePack = categoriePack;
    this.codePack = codePack;
    this.debitPack = debitPack;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Long getPackId() {
    return packId;
  }

  public void setPackId(Long packId) {
    this.packId = packId;
  }

  public Offre getOffre() {
    return offre;
  }

  public void setOffre(Offre offre) {
    this.offre = offre;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getIdPackBase() {
    return idPackBase;
  }

  public void setIdPackBase(Long idPackBase) {
    this.idPackBase = idPackBase;
  }

  public CategorieProduitInternet getCategoriePack() {
    return categoriePack;
  }

  public void setCategoriePack(CategorieProduitInternet categoriePack) {
    this.categoriePack = categoriePack;
  }

  public String getCodePack() {
    return codePack;
  }

  public void setCodePack(String codePack) {
    this.codePack = codePack;
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

  public String getDebitPack() {
    return debitPack;
  }

  public void setDebitPack(String debitPack) {
    this.debitPack = debitPack;
  }



  public Boolean getPayLater() {
    return payLater;
  }

  public void setPayLater(Boolean payLater) {
    this.payLater = payLater;
  }



  public Engagement getEngagement() {
    return engagement;
  }

  public void setEngagement(Engagement engagement) {
    this.engagement = engagement;
  }

  @Override
  public String toString() {
    return "Pack [packId=" + packId + ", offre=" + offre + ", title=" + title + ", description="
        + description + ", idPackBase=" + idPackBase + ", categoriePack=" + categoriePack
        + ", codePack=" + codePack + ", createdDate=" + createdDate + ", modifiedDate="
        + modifiedDate + "]";
  }

}
