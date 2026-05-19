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
@Table(name = "MigrationFacture")
@EntityListeners(AuditingEntityListener.class)
public class MigrationFacture implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "factureMigrationId")
  private Long factureMigrationId;

  @Column(name = "clientid")
  private Long clientid;

  private Double montantHt;
  private Double montantTva;
  private Long percentTva;
  private String typeCalcule;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private String nameMigration;


  public MigrationFacture() {
    super();
    // TODO Auto-generated constructor stub
  }

  public MigrationFacture(Long clientid, Double montantHt, Double montantTva, Date createdDate,
      Date modifiedDate) {
    super();
    this.clientid = clientid;
    this.montantHt = montantHt;
    this.montantTva = montantTva;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Long getClientid() {
    return clientid;
  }

  public void setClientid(Long clientid) {
    this.clientid = clientid;
  }

  public Double getMontantHt() {
    return montantHt;
  }

  public void setMontantHt(Double montantHt) {
    this.montantHt = montantHt;
  }

  public Double getMontantTva() {
    return montantTva;
  }

  public void setMontantTva(Double montantTva) {
    this.montantTva = montantTva;
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

  public Long getFactureMigrationId() {
    return factureMigrationId;
  }

  public void setFactureMigrationId(Long factureMigrationId) {
    this.factureMigrationId = factureMigrationId;
  }

  public String getTypeCalcule() {
    return typeCalcule;
  }

  public void setTypeCalcule(String typeCalcule) {
    this.typeCalcule = typeCalcule;
  }



  public String getNameMigration() {
    return nameMigration;
  }

  public void setNameMigration(String nameMigration) {
    this.nameMigration = nameMigration;
  }

  public Long getPercentTva() {
    return percentTva;
  }

  public void setPercentTva(Long percentTva) {
    this.percentTva = percentTva;
  }

  @Override
  public String toString() {
    return "MigrationFacture [clientid=" + clientid + ", montantHt=" + montantHt + ", montantTva="
        + montantTva + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
  }



}
