package crm.chifco.com.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
@Table(name = "Motifrec")
@EntityListeners(AuditingEntityListener.class)
public class Motifrec {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long motifId;

  @ManyToOne
  @JoinColumn(name = "service_id", nullable = true)
  private Servicetype servicetype;

  @Column(length = 50)
  private String nomMotif;
  @Column(length = 50)
  private String nomMotifar;
  @Column(length = 50)
  private String nomMotifen;

  @Column(length = 50)
  private String category;

  @Column(length = 100)
  private String designation;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Long getMotifId() {
    return motifId;
  }

  public void setMotifId(Long motifId) {
    this.motifId = motifId;
  }

  public Servicetype getServicetype() {
    return servicetype;
  }

  public void setServicetype(Servicetype servicetype) {
    this.servicetype = servicetype;
  }

  public String getNomMotif() {
    return nomMotif;
  }

  public void setNomMotif(String nomMotif) {
    this.nomMotif = nomMotif;
  }

  public String getDesignation() {
    return designation;
  }

  public void setDesignation(String designation) {
    this.designation = designation;
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

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }



  public String getNomMotifar() {
    return nomMotifar;
  }

  public void setNomMotifar(String nomMotifar) {
    this.nomMotifar = nomMotifar;
  }

  public String getNomMotifen() {
    return nomMotifen;
  }

  public void setNomMotifen(String nomMotifen) {
    this.nomMotifen = nomMotifen;
  }

  public Motifrec(Long motifId, Servicetype servicetype, String nomMotif, String category,
      String designation, Date createdDate, Date modifiedDate) {
    super();
    this.motifId = motifId;
    this.servicetype = servicetype;
    this.nomMotif = nomMotif;
    this.category = category;
    this.designation = designation;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Motifrec(Long motifId, Servicetype servicetype, String nomMotif, String nomMotifar,
      String nomMotifen, String category, String designation, Date createdDate, Date modifiedDate) {
    super();
    this.motifId = motifId;
    this.servicetype = servicetype;
    this.nomMotif = nomMotif;
    this.nomMotifar = nomMotifar;
    this.nomMotifen = nomMotifen;
    this.category = category;
    this.designation = designation;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Motifrec() {
    super();
  }

}
