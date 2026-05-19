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
@Table(name = "Status")
@EntityListeners(AuditingEntityListener.class)
public class Statut implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long statutId;

  @Column(length = 20)
  private String nomStatut;

  @Column(length = 100)
  private String designation;

  @Column(length = 100)
  private String couleur;


  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Statut() {}

  public Statut(Long statutId, String nomStatut, String designation) {
    this.statutId = statutId;
    this.nomStatut = nomStatut;
    this.designation = designation;
  }

  /**
   * @return the statutId
   */
  public Long getStatutId() {
    return statutId;
  }

  /**
   * @param statutId the statutId to set
   */
  public void setStatutId(Long statutId) {
    this.statutId = statutId;
  }

  /**
   * @return the nomStatut
   */
  public String getNomStatut() {
    return nomStatut;
  }

  /**
   * @param nomStatut the nomStatut to set
   */
  public void setNomStatut(String nomStatut) {
    this.nomStatut = nomStatut;
  }

  /**
   * @return the designation
   */
  public String getDesignation() {
    return designation;
  }

  /**
   * @param designation the designation to set
   */
  public void setDesignation(String designation) {
    this.designation = designation;
  }

  /**
   * @return the couleur
   */
  public String getCouleur() {
    return couleur;
  }

  /**
   * @param couleur the couleur to set
   */
  public void setCouleur(String couleur) {
    this.couleur = couleur;
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

  @Override
  public String toString() {
    return "Statut [statutId=" + statutId + ", nomStatut=" + nomStatut + ", designation="
        + designation + ", couleur=" + couleur + ", createdDate=" + createdDate + ", modifiedDate="
        + modifiedDate + "]";
  }

  /**
   * @return the modifieddate
   */



}
