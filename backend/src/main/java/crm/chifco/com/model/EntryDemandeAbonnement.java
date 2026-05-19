package crm.chifco.com.model;

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
@Table(name = "entryDemandeAbonnement")
@EntityListeners(AuditingEntityListener.class)
public class EntryDemandeAbonnement {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "produitId")
  private Produit produit;

  public EntryDemandeAbonnement() {
    super();
    // TODO Auto-generated constructor stub
  }

  public EntryDemandeAbonnement(Long id, Produit produit) {
    super();
    this.id = id;
    this.produit = produit;
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

  @Override
  public String toString() {
    return "EntryDemandeAbonnement [id=" + id + ", produit=" + produit + "]";
  }

}
