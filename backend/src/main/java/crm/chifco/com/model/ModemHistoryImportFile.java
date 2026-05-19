package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class ModemHistoryImportFile implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long idLigneModemImportXlsHistory;

  private String numSerie;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  private String description;

  private String status;



  public ModemHistoryImportFile() {
    super();
  }

  public ModemHistoryImportFile(Long idLigneModemImportXlsHistory, String numSerie,
      Date createdDate, Date modifiedDate, String description, String status,
      ModemHistoryImport modemImportXlsHistory) {
    super();
    this.idLigneModemImportXlsHistory = idLigneModemImportXlsHistory;
    this.numSerie = numSerie;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.description = description;
    this.status = status;

  }

  public Long getIdLigneModemImportXlsHistory() {
    return idLigneModemImportXlsHistory;
  }

  public void setIdLigneModemImportXlsHistory(Long idLigneModemImportXlsHistory) {
    this.idLigneModemImportXlsHistory = idLigneModemImportXlsHistory;
  }

  public String getNumSerie() {
    return numSerie;
  }

  public void setNumSerie(String numSerie) {
    this.numSerie = numSerie;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "LigneModemImportXlsHistory [idLigneModemImportXlsHistory="
        + idLigneModemImportXlsHistory + ", numSerie=" + numSerie + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + ", description=" + description + ", status=" + status
        + "]";
  }



}
