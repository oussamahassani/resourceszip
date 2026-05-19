package crm.chifco.com.model;

import java.io.Serializable;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "entryAbonnement")
@EntityListeners(AuditingEntityListener.class)
public class EntryAbonnement implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long id;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "produitId")
  private Produit produit;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "abonnementId")
  private Abonnement abonnement;

  public EntryAbonnement() {
    super();
    // TODO Auto-generated constructor stub
  }

  public EntryAbonnement(Long id, Produit produit) {
    super();
    this.id = id;
    this.produit = produit;
  }

  public EntryAbonnement(Long id, Produit produit, Abonnement abonnementId) {
    super();
    this.id = id;
    this.produit = produit;
    this.abonnement = abonnementId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Produit getProduit() {
    return produit;
  }

  public void setProduit(Produit produit) {
    this.produit = produit;
  }

  public Abonnement getAbonnement() {
    return abonnement;
  }

  public void setAbonnement(Abonnement abonnement) {
    this.abonnement = abonnement;
  }

  @Override
  public String toString() {
    return "EntryAbonnement [id=" + id + ", produit=" + produit + ", abonnement=" + abonnement
        + "]";
  }



}
