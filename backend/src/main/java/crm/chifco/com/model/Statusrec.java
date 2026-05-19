package crm.chifco.com.model;

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
@Table(name = "Statusrec")
@EntityListeners(AuditingEntityListener.class)
public class Statusrec {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long statutId;

  @Column(length = 50)
  private String nomStatut;
  @Column(length = 50)
  private String nomStatutar;
  @Column(length = 50)
  private String nomStatuten;

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

  public Long getStatutId() {
    return statutId;
  }

  public void setStatutId(Long statutId) {
    this.statutId = statutId;
  }

  public String getNomStatut() {
    return nomStatut;
  }

  public void setNomStatut(String nomStatut) {
    this.nomStatut = nomStatut;
  }

  public String getDesignation() {
    return designation;
  }

  public void setDesignation(String designation) {
    this.designation = designation;
  }

  public String getCouleur() {
    return couleur;
  }

  public void setCouleur(String couleur) {
    this.couleur = couleur;
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

  public String getNomStatutar() {
    return nomStatutar;
  }

  public void setNomStatutar(String nomStatutar) {
    this.nomStatutar = nomStatutar;
  }

  public String getNomStatuten() {
    return nomStatuten;
  }

  public void setNomStatuten(String nomStatuten) {
    this.nomStatuten = nomStatuten;
  }

  public Statusrec(Long statutId, String nomStatut, String designation, String couleur,
      Date createdDate, Date modifiedDate) {
    super();
    this.statutId = statutId;
    this.nomStatut = nomStatut;
    this.designation = designation;
    this.couleur = couleur;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Statusrec(Long statutId, String nomStatut, String nomStatutar, String nomStatuten,
      String designation, String couleur, Date createdDate, Date modifiedDate) {
    super();
    this.statutId = statutId;
    this.nomStatut = nomStatut;
    this.nomStatutar = nomStatutar;
    this.nomStatuten = nomStatuten;
    this.designation = designation;
    this.couleur = couleur;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Statusrec() {
    super();
  }

}
