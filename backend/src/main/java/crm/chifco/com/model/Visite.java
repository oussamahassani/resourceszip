package crm.chifco.com.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "visite")
public class Visite {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "typevisite_id")
  private TypeVisite typeVisite;
  private String revendeur;
  private int dureeVisiteHeures;
  private int dureeVisiteMinutes;
  private Double longitude;
  private Double latitude;
  private String reference_visite;
  private String autreType;
  private String autreLieu;


  @Column(length = 2000)
  private String commentaire;
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;
  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;
  private String status;
  @OneToOne
  @JoinColumn(name = "created_by_id")
  private User createdBy;
  @OneToOne
  @JoinColumn(name = "edited_by_id")
  private User editedBy;



  public String getReference_visite() {
    return reference_visite;
  }

  public void setReference_visite(String reference_visite) {
    this.reference_visite = reference_visite;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  private Long revndeurId;

  public String getAutreType() {
    return autreType;
  }

  public void setAutreType(String autreType) {
    this.autreType = autreType;
  }

  public String getAutreLieu() {
    return autreLieu;
  }

  public void setAutreLieu(String autreLieu) {
    this.autreLieu = autreLieu;
  }

  public Visite() {}


  public Visite(Long id, TypeVisite typeVisite, String revendeur, int dureeVisiteHeures,
      int dureeVisiteMinutes, Double longitude, Double latitude, String reference_visite,
      String autreType, String autreLieu, String commentaire, Date createdDate, Date modifiedDate,
      String status, User createdBy, User editedBy, Long revndeurId) {
    super();
    this.id = id;
    this.typeVisite = typeVisite;
    this.revendeur = revendeur;
    this.dureeVisiteHeures = dureeVisiteHeures;
    this.dureeVisiteMinutes = dureeVisiteMinutes;
    this.longitude = longitude;
    this.latitude = latitude;
    this.reference_visite = reference_visite;
    this.autreType = autreType;
    this.autreLieu = autreLieu;
    this.commentaire = commentaire;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.status = status;
    this.createdBy = createdBy;
    this.editedBy = editedBy;
    this.revndeurId = revndeurId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }



  public TypeVisite getTypeVisite() {
    return typeVisite;
  }

  public void setTypeVisite(TypeVisite typeVisite) {
    this.typeVisite = typeVisite;
  }

  public Long getRevndeurId() {
    return revndeurId;
  }

  public void setRevndeurId(Long revndeurId) {
    this.revndeurId = revndeurId;
  }

  public String getRevendeur() {
    return revendeur;
  }

  public void setRevendeur(String revendeur) {
    this.revendeur = revendeur;
  }

  public int getDureeVisiteHeures() {
    return dureeVisiteHeures;
  }

  public void setDureeVisiteHeures(int dureeVisiteHeures) {
    this.dureeVisiteHeures = dureeVisiteHeures;
  }

  public int getDureeVisiteMinutes() {
    return dureeVisiteMinutes;
  }

  public void setDureeVisiteMinutes(int dureeVisiteMinutes) {
    this.dureeVisiteMinutes = dureeVisiteMinutes;
  }

  public String getCommentaire() {
    return commentaire;
  }

  public void setCommentaire(String commentaire) {
    this.commentaire = commentaire;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
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

  public User getEditedBy() {
    return editedBy;
  }

  public void setEditedBy(User editedBy) {
    this.editedBy = editedBy;
  }

}

