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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "historiques")
@EntityListeners(AuditingEntityListener.class)
public class Historique implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long historique_id;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date date = new Date(); // now

  private String Description;

  private String mailClient;
  private Double prix;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userId")
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "demandeId")
  private Abonnement abonnement;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "factureId")
  private Facture facture;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Historique() {}

  public Historique(Long historique_id, Date date, String description, String mailClient,
      Double prix, User user, Abonnement abonnement, Facture facture, Date createddate,
      Date modifieddate) {
    this.historique_id = historique_id;
    this.date = date;
    Description = description;
    this.mailClient = mailClient;
    this.prix = prix;
    this.user = user;
    this.abonnement = abonnement;
    this.facture = facture;
    this.createdDate = createddate;
    this.modifiedDate = modifieddate;
  }

  public Long getHistorique_id() {
    return historique_id;
  }

  public void setHistorique_id(Long historique_id) {
    this.historique_id = historique_id;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getDescription() {
    return Description;
  }

  public void setDescription(String description) {
    Description = description;
  }

  public Double getPrix() {
    return prix;
  }

  public void setPrix(Double prix) {
    this.prix = prix;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Abonnement getAbonnement() {
    return abonnement;
  }

  public void setAbonnement(Abonnement abonnement) {
    this.abonnement = abonnement;
  }



  public String getMailClient() {
    return mailClient;
  }

  public void setMailClient(String mailClient) {
    this.mailClient = mailClient;
  }

  public Facture getFacture() {
    return facture;
  }

  public void setFacture(Facture facture) {
    this.facture = facture;
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
    return "Historique [historique_id=" + historique_id + ", date=" + date + ", Description="
        + Description + ", mailClient=" + mailClient + ", prix=" + prix + ", user=" + user
        + ", abonnement=" + abonnement + ", facture=" + facture + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + "]";
  }

}
