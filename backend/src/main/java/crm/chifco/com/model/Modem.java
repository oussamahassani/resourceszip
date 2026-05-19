package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "Modem")
@EntityListeners(AuditingEntityListener.class)
public class Modem implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGen")
  @SequenceGenerator(name = "seqGen", sequenceName = "modem_id_seqs", allocationSize = 1)

  private Long modemId;

  private String modelModem;

  @Column(unique = true)
  private String numSerie;

  // private String reference;

  private String marque;

  private Boolean status = false;

  private Long affectePointdeVente;

  private Long affecteDistributeur;

  private Long affecteRevendeur;

  private Long affecteClient;

  private String email;

  private String password;

  private String adresseMac;

  private String statutReservation;

  private String loginControleParental;
  private Boolean controleParentaleActiver;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Modem() {
    super();
  }

  public Modem(Long modemId, String modelModem, String numSerie, String marque, Boolean status,
      Long affectePointdeVente, Long affecteDistributeur, Long affecteRevendeur, Long affecteClient,
      String email, String password, String adresseMac, String statutReservation, Date createdDate,
      Date modifiedDate) {
    super();
    this.modemId = modemId;
    this.modelModem = modelModem;
    this.numSerie = numSerie;
    this.marque = marque;
    this.status = status;
    this.affectePointdeVente = affectePointdeVente;
    this.affecteDistributeur = affecteDistributeur;
    this.affecteRevendeur = affecteRevendeur;
    this.affecteClient = affecteClient;
    this.email = email;
    this.password = password;
    this.adresseMac = adresseMac;
    this.statutReservation = statutReservation;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Long getModemId() {
    return modemId;
  }

  public void setModemId(Long modemId) {
    this.modemId = modemId;
  }

  public String getModelModem() {
    return modelModem;
  }

  public void setModelModem(String modelModem) {
    this.modelModem = modelModem;
  }

  public String getNumSerie() {
    return numSerie;
  }

  public void setNumSerie(String numSerie) {
    this.numSerie = numSerie;
  }

  public String getMarque() {
    return marque;
  }

  public void setMarque(String marque) {
    this.marque = marque;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public Long getAffectePointdeVente() {
    return affectePointdeVente;
  }

  public void setAffectePointdeVente(Long affectePointdeVente) {
    this.affectePointdeVente = affectePointdeVente;
  }

  public Long getAffecteDistributeur() {
    return affecteDistributeur;
  }

  public void setAffecteDistributeur(Long affecteDistributeur) {
    this.affecteDistributeur = affecteDistributeur;
  }

  public Long getAffecteRevendeur() {
    return affecteRevendeur;
  }

  public void setAffecteRevendeur(Long affecteRevendeur) {
    this.affecteRevendeur = affecteRevendeur;
  }

  public Long getAffecteClient() {
    return affecteClient;
  }

  public void setAffecteClient(Long affecteClient) {
    this.affecteClient = affecteClient;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAdresseMac() {
    return adresseMac;
  }

  public void setAdresseMac(String adresseMac) {
    this.adresseMac = adresseMac;
  }

  public String getStatutReservation() {
    return statutReservation;
  }

  public void setStatutReservation(String statutReservation) {
    this.statutReservation = statutReservation;
  }

  public String getLoginControleParental() {
    return loginControleParental;
  }

  public void setLoginControleParental(String loginControleParental) {
    this.loginControleParental = loginControleParental;
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

  public Boolean getControleParentaleActiver() {
	return controleParentaleActiver;
}

public void setControleParentaleActiver(Boolean controleParentaleActiver) {
	this.controleParentaleActiver = controleParentaleActiver;
}

@Override
  public String toString() {
    return "Modem [modemId=" + modemId + ", modelModem=" + modelModem + ", numSerie=" + numSerie
        + ", marque=" + marque + ", status=" + status + ", affectePointdeVente="
        + affectePointdeVente + ", affecteDistributeur=" + affecteDistributeur
        + ", affecteRevendeur=" + affecteRevendeur + ", affecteClient=" + affecteClient + ", email="
        + email + ", password=" + password + ", adresseMac=" + adresseMac + ", statutReservation="
        + statutReservation + ", loginControleParental=" + loginControleParental
        +  ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + "]";
  }

}
