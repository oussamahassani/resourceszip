package crm.chifco.com.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tarification")
@EntityListeners(AuditingEntityListener.class)
public class Tarification implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long tarificationId;

  @Column(name = "packId")
  private Long packId;

  @Column(name = "produitId")
  private Long produitId;

  private Double prixUnitaire;
  private Long taxe;
  private String typeRemise;
  private Double remise;

  private Double prixTTc;


  @ManyToOne
  @JoinColumn(name = "categoryClient")
  private CategoryClient categoryClient;

  public Tarification() {
    super();
    // TODO Auto-generated constructor stub
  }


  public Tarification(Long tarificationId, Long packId, Long produitId, Double prixUnitaire,
      Long taxe, String typeRemise, Double remise, Double prixTTc, CategoryClient categoryClient) {
    super();
    this.tarificationId = tarificationId;
    this.packId = packId;
    this.produitId = produitId;
    this.prixUnitaire = prixUnitaire;
    this.taxe = taxe;
    this.typeRemise = typeRemise;
    this.remise = remise;
    this.prixTTc = prixTTc;
    this.categoryClient = categoryClient;
  }


  public Long getTarificationId() {
    return tarificationId;
  }

  public void setTarificationId(Long tarificationId) {
    this.tarificationId = tarificationId;
  }

  public Long getPackId() {
    return packId;
  }

  public void setPackId(Long packId) {
    this.packId = packId;
  }

  public Long getProduitId() {
    return produitId;
  }

  public void setProduitId(Long produitId) {
    this.produitId = produitId;
  }

  public Double getPrixUnitaire() {
    return prixUnitaire;
  }

  public void setPrixUnitaire(Double prixUnitaire) {
    this.prixUnitaire = prixUnitaire;
  }

  public Long getTaxe() {
    return taxe;
  }

  public void setTaxe(Long taxe) {
    this.taxe = taxe;
  }

  public String getTypeRemise() {
    return typeRemise;
  }

  public void setTypeRemise(String typeRemise) {
    this.typeRemise = typeRemise;
  }

  public Double getRemise() {
    return remise;
  }

  public void setRemise(Double remise) {
    this.remise = remise;
  }



  public Double getPrixTTc() {
    return prixTTc;
  }

  public void setPrixTTc(Double prixTTc) {
    this.prixTTc = prixTTc;
  }


  public CategoryClient getCategoryClient() {
    return categoryClient;
  }

  public void setCategoryClient(CategoryClient categoryClient) {
    this.categoryClient = categoryClient;
  }

  @Override
  public String toString() {
    return "Tarification [tarificationId=" + tarificationId + ", packId=" + packId + ", produitId="
        + produitId + ", prixUnitaire=" + prixUnitaire + ", taxe=" + taxe + ", typeRemise="
        + typeRemise + ", remise=" + remise + ", prixTTc=" + prixTTc + ", categoryClient="
        + categoryClient + "]";
  }



}
