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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import crm.chifco.com.service.GenerateSequenceParinage;

@Entity
@Table(name = "Parinage")
@EntityListeners(AuditingEntityListener.class)
public class Parinage implements Serializable {
  private static long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id_parinage")
  private Long idParinage;

  @GeneratorType(type = GenerateSequenceParinage.class, when = GenerationTime.INSERT)
  @Column(name = "reference_parinage", updatable = false)
  private String referenceParinage;

  private String cinParrain;
  private String cinParinee;
  private String statut;
  private String nomParrain;
  private String nomParinee;
  private String commentaire;

  @Column(length = 80)
  private String telFixe;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  private String cinVerso;
  private String cinRecto;
  @ManyToOne(fetch = FetchType.EAGER)
  private User createdBy;
  private String email;

  public Parinage() {
    super();
    // TODO Auto-generated constructor stub
  }

  public Parinage(Long idParinage, String referenceParinage, String cinParrain, String cinParinee,
      String statut, String nomParrain, String nomParinee, String telFixe, Date createdDate) {
    super();
    this.idParinage = idParinage;
    this.referenceParinage = referenceParinage;
    this.cinParrain = cinParrain;
    this.cinParinee = cinParinee;
    this.statut = statut;
    this.nomParrain = nomParrain;
    this.nomParinee = nomParinee;
    this.telFixe = telFixe;
    this.createdDate = createdDate;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public static void setSerialversionuid(long serialversionuid) {
    serialVersionUID = serialversionuid;
  }

  public Long getIdParinage() {
    return idParinage;
  }

  public void setIdParinage(Long idParinage) {
    this.idParinage = idParinage;
  }

  public String getReferenceParinage() {
    return referenceParinage;
  }

  public void setReferenceParinage(String referenceParinage) {
    this.referenceParinage = referenceParinage;
  }

  public String getCinParrain() {
    return cinParrain;
  }

  public void setCinParrain(String cinParrain) {
    this.cinParrain = cinParrain;
  }

  public String getCinParinee() {
    return cinParinee;
  }

  public void setCinParinee(String cinParinee) {
    this.cinParinee = cinParinee;
  }

  public String getStatut() {
    return statut;
  }

  public void setStatut(String statut) {
    this.statut = statut;
  }

  public String getNomParrain() {
    return nomParrain;
  }

  public void setNomParrain(String nomParrain) {
    this.nomParrain = nomParrain;
  }



  public String getNomParinee() {
    return nomParinee;
  }

  public void setNomParinee(String nomParinee) {
    this.nomParinee = nomParinee;
  }


  public String getTelFixe() {
    return telFixe;
  }

  public void setTelFixe(String telFixe) {
    this.telFixe = telFixe;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getCinVerso() {
    return cinVerso;
  }

  public void setCinVerso(String cinVerso) {
    this.cinVerso = cinVerso;
  }

  public String getCinRecto() {
    return cinRecto;
  }

  public void setCinRecto(String cinRecto) {
    this.cinRecto = cinRecto;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCommentaire() {
    return commentaire;
  }

  public void setCommentaire(String commentaire) {
    this.commentaire = commentaire;
  }

  @Override
  public String toString() {
    return "Parinage [idParinage=" + idParinage + ", referenceParinage=" + referenceParinage
        + ", cinParrain=" + cinParrain + ", cinParinee=" + cinParinee + ", statut=" + statut
        + ", nomParrain=" + nomParrain + ", nomParinee=" + nomParinee + ", telFixe=" + telFixe
        + ", createdDate=" + createdDate + ", cinVerso=" + cinVerso + ", cinRecto=" + cinRecto
        + ", createdBy=" + createdBy + "]";
  }



}
