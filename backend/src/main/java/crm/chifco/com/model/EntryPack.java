package crm.chifco.com.model;

import java.io.Serializable;
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
@Table(name = "EntryPack")
@EntityListeners(AuditingEntityListener.class)
public class EntryPack implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long entryPackId;

  @ManyToOne
  @JoinColumn(name = "idProduit", nullable = false)
  private Produit produit;

  private Boolean showProduitFacture;

  @ManyToOne
  @JoinColumn(name = "pack_id")
  private Pack pack;

  public EntryPack() {
    super();
    // TODO Auto-generated constructor stub
  }



  public EntryPack(Long entryPackId, Produit produit, Boolean showProduitFacture) {
    super();
    this.entryPackId = entryPackId;
    this.produit = produit;
    this.showProduitFacture = showProduitFacture;
  }



  public EntryPack(Long entryPackId, Produit produit, Boolean showProduitFacture, Pack packId) {
    super();
    this.entryPackId = entryPackId;
    this.produit = produit;
    this.showProduitFacture = showProduitFacture;
    this.pack = packId;
  }



  public Long getEntryPackId() {
    return entryPackId;
  }

  public void setEntryPackId(Long entryPackId) {
    this.entryPackId = entryPackId;
  }

  public Produit getProduit() {
    return produit;
  }

  public void setProduit(Produit produit) {
    this.produit = produit;
  }

  public Boolean getShowProduitFacture() {
    return showProduitFacture;
  }

  public void setShowProduitFacture(Boolean showProduitFacture) {
    this.showProduitFacture = showProduitFacture;
  }



  public Pack getPack() {
    return pack;
  }



  public void setPack(Pack pack) {
    this.pack = pack;
  }



  @Override
  public String toString() {
    return "EntryPack [entryPackId=" + entryPackId + ", produit=" + produit
        + ", showProduitFacture=" + showProduitFacture + ", pack=" + pack + "]";
  }



}
