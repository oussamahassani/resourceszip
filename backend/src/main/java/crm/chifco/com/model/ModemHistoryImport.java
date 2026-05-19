package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class ModemHistoryImport implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long idModemImportXlsHistory;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  private String nameFile;

  private String origineNameFile;

  private int successRow;

  private int errorRow;

  private int totalRow;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "idModemImportXlsHistory")
  private List<ModemHistoryImportFile> ligneModemImportXlsHistories;

  @ManyToOne
  @JoinColumn
  private User user;

  public ModemHistoryImport() {
    super();
  }

  public ModemHistoryImport(Long idModemImportXlsHistory, Date createdDate, String nameFile,
      String origineNameFile, int successRow, int errorRow, int totalRow,
      List<ModemHistoryImportFile> ligneModemImportXlsHistories, User user) {
    super();
    this.idModemImportXlsHistory = idModemImportXlsHistory;
    this.createdDate = createdDate;
    this.nameFile = nameFile;
    this.origineNameFile = origineNameFile;
    this.successRow = successRow;
    this.errorRow = errorRow;
    this.totalRow = totalRow;
    this.ligneModemImportXlsHistories = ligneModemImportXlsHistories;
    this.user = user;
  }

  public Long getIdModemImportXlsHistory() {
    return idModemImportXlsHistory;
  }

  public void setIdModemImportXlsHistory(Long idModemImportXlsHistory) {
    this.idModemImportXlsHistory = idModemImportXlsHistory;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getNameFile() {
    return nameFile;
  }

  public void setNameFile(String nameFile) {
    this.nameFile = nameFile;
  }

  public String getOrigineNameFile() {
    return origineNameFile;
  }

  public void setOrigineNameFile(String origineNameFile) {
    this.origineNameFile = origineNameFile;
  }

  public int getSuccessRow() {
    return successRow;
  }

  public void setSuccessRow(int successRow) {
    this.successRow = successRow;
  }

  public int getErrorRow() {
    return errorRow;
  }

  public void setErrorRow(int errorRow) {
    this.errorRow = errorRow;
  }

  public int getTotalRow() {
    return totalRow;
  }

  public void setTotalRow(int totalRow) {
    this.totalRow = totalRow;
  }

  public List<ModemHistoryImportFile> getLigneModemImportXlsHistories() {
    return ligneModemImportXlsHistories;
  }

  public void setLigneModemImportXlsHistories(
      List<ModemHistoryImportFile> ligneModemImportXlsHistories) {
    this.ligneModemImportXlsHistories = ligneModemImportXlsHistories;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "ModemImportXlsHistory [idModemImportXlsHistory=" + idModemImportXlsHistory
        + ", createdDate=" + createdDate + ", nameFile=" + nameFile + ", origineNameFile="
        + origineNameFile + ", successRow=" + successRow + ", errorRow=" + errorRow + ", totalRow="
        + totalRow + ", ligneModemImportXlsHistories=" + ligneModemImportXlsHistories + ", user="
        + user + "]";
  }

}
