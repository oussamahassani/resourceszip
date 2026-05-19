package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "commandes")
@EntityListeners(AuditingEntityListener.class)
public class Commande implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "commandeId")
  private Long commandeId;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userid")
  private User user;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "abonnement_id")
  private Abonnement abonnement;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private String dateFin;

  private String dateEchance;

  private Boolean isActive = true;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "commandeid")
  List<EntryCommande> entryCommande;

  public Commande() {}

  public Commande(User user, List<Produit> produit, DemandeAbonnement demandeAbonnement,
      Abonnement abonnement, Date createddate, Date modifieddate, String dateechance,
      String datefin, Boolean active, List<EntryCommande> entryCommande) {
    this.user = user;
    this.entryCommande = entryCommande;
    this.abonnement = abonnement;
    this.createdDate = createddate;
    this.modifiedDate = modifieddate;
    this.dateEchance = dateechance;
    this.dateFin = datefin;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Abonnement getClient() {
    return abonnement;
  }

  public void setClient(Abonnement abonnement) {
    this.abonnement = abonnement;
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
   * @return the modifiedDate
   */
  public Date getModifiedDate() {
    return modifiedDate;
  }

  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return the dateFin
   */
  public String getDateFin() {
    return dateFin;
  }

  /**
   * @param dateFin the dateFin to set
   */
  public void setDateFin(String dateFin) {
    this.dateFin = dateFin;
  }

  /**
   * @return the dateEchance
   */
  public String getDateEchance() {
    return dateEchance;
  }

  /**
   * @param dateEchance the dateEchance to set
   */
  public void setDateEchance(String dateEchance) {
    this.dateEchance = dateEchance;
  }

  /**
   * @return the entryCommande
   */
  public List<EntryCommande> getEntryCommande() {
    return entryCommande;
  }

  /**
   * @param entryCommande the entryCommande to set
   */
  public void setEntryCommande(List<EntryCommande> entryCommande) {
    this.entryCommande = entryCommande;
  }

  public Long getCommandeId() {
    return commandeId;
  }

  public void setCommandeId(Long commandeId) {
    this.commandeId = commandeId;
  }

  @Override
  public String toString() {
    return "Commande [commandeId=" + commandeId + ", user=" + user + ", abonnement=" + abonnement
        + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", dateFin=" + dateFin
        + ", dateEchance=" + dateEchance + ", isActive=" + isActive + ", entryCommande="
        + entryCommande + "]";
  }

}
