package crm.chifco.com.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import crm.chifco.com.service.GenerateSequenceRefFicheAffectation;

@Entity
@Table(name = "fichesStock")
@EntityListeners(AuditingEntityListener.class)
public class FicheStock implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long IdFiche;

  @GeneratorType(type = GenerateSequenceRefFicheAffectation.class, when = GenerationTime.INSERT)
  @Column(unique = true)
  private String ref_fiche;

  @ElementCollection(targetClass = String.class)
  private List<String> numSerieModem = new ArrayList<>();
  private Long affecteID;

  private int affectequantite;

  private Long affectedBYuser;

  private Boolean isdelete = false;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public FicheStock() {}

  public FicheStock(Long idFiche, String ref_fiche, List<String> numSerieModem, Long affecteID,
      int affectequantite, Long affectedBYuser, Boolean isdelete, Date createdDate,
      Date modifiedDate) {
    super();
    IdFiche = idFiche;
    this.ref_fiche = ref_fiche;
    this.numSerieModem = numSerieModem;
    this.affecteID = affecteID;
    this.affectequantite = affectequantite;
    this.affectedBYuser = affectedBYuser;
    this.isdelete = isdelete;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Long getIdFiche() {
    return IdFiche;
  }

  public void setIdFiche(Long idFiche) {
    IdFiche = idFiche;
  }

  public String getRef_fiche() {
    return ref_fiche;
  }

  public void setRef_fiche(String ref_fiche) {
    this.ref_fiche = ref_fiche;
  }

  public List<String> getNumSerieModem() {
    return numSerieModem;
  }

  public void setNumSerieModem(List<String> numSerieModem) {
    this.numSerieModem = numSerieModem;
  }

  public Long getAffecteID() {
    return affecteID;
  }

  public void setAffecteID(Long affecteID) {
    this.affecteID = affecteID;
  }

  public int getAffectequantite() {
    return affectequantite;
  }

  public void setAffectequantite(int affectequantite) {
    this.affectequantite = affectequantite;
  }

  public Long getAffectedBYuser() {
    return affectedBYuser;
  }

  public void setAffectedBYuser(Long affectedBYuser) {
    this.affectedBYuser = affectedBYuser;
  }

  public Boolean getIsdelete() {
    return isdelete;
  }

  public void setIsdelete(Boolean isdelete) {
    this.isdelete = isdelete;
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
    return "FicheStock [IdFiche=" + IdFiche + ", ref_fiche=" + ref_fiche + ", numSerieModem="
        + numSerieModem + ", affecteID=" + affecteID + ", affectequantite=" + affectequantite
        + ", affectedBYuser=" + affectedBYuser + ", isdelete=" + isdelete + ", createdDate="
        + createdDate + ", modifiedDate=" + modifiedDate + "]";
  }

}
