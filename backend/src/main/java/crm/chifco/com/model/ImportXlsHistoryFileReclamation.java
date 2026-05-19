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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ImportXlsHistoryFileReclamation")
public class ImportXlsHistoryFileReclamation implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long xlsHistoriqueFile;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  private String nameFile;

  private String origineNameFile;

  private String successRow;

  private String errorRow;

  private String totalRow;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userId")
  private User user;

  public ImportXlsHistoryFileReclamation() {
    // TODO Auto-generated constructor stub
  }

  public ImportXlsHistoryFileReclamation(Long xlsHistoriqueFile, Date createdDate, String nameFile,
      String origineNameFile, String successRow, String errorRow, String totalRow, User user) {
    super();
    this.xlsHistoriqueFile = xlsHistoriqueFile;
    this.createdDate = createdDate;
    this.nameFile = nameFile;
    this.origineNameFile = origineNameFile;
    this.successRow = successRow;
    this.errorRow = errorRow;
    this.totalRow = totalRow;
    this.user = user;
  }

  /**
   * @return the xlsHistoriquFile
   */
  public Long getXlsHistoriqueFile() {
    return xlsHistoriqueFile;
  }

  /**
   * @param xlsHistoriquFile the xlsHistoriquFile to set
   */
  public void setXlsHistoriqueFile(Long xlsHistoriqueFile) {
    this.xlsHistoriqueFile = xlsHistoriqueFile;
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
   * @return the nameFile
   */
  public String getNameFile() {
    return nameFile;
  }

  /**
   * @param nameFile the nameFile to set
   */
  public void setNameFile(String nameFile) {
    this.nameFile = nameFile;
  }

  /**
   * @return the origineNameFile
   */
  public String getOrigineNameFile() {
    return origineNameFile;
  }

  /**
   * @param origineNameFile the origineNameFile to set
   */
  public void setOrigineNameFile(String origineNameFile) {
    this.origineNameFile = origineNameFile;
  }

  /**
   * @return the successRow
   */
  public String getSuccessRow() {
    return successRow;
  }

  /**
   * @param successRow the successRow to set
   */
  public void setSuccessRow(String successRow) {
    this.successRow = successRow;
  }

  /**
   * @return the errorRow
   */
  public String getErrorRow() {
    return errorRow;
  }

  /**
   * @param errorRow the errorRow to set
   */
  public void setErrorRow(String errorRow) {
    this.errorRow = errorRow;
  }

  /**
   * @return the totalRow
   */
  public String getTotalRow() {
    return totalRow;
  }

  /**
   * @param totalRow the totalRow to set
   */
  public void setTotalRow(String totalRow) {
    this.totalRow = totalRow;
  }

  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "ImportXlsHistoryFileReclamation [xlsHistoriquFile=" + xlsHistoriqueFile
        + ", createdDate=" + createdDate + ", nameFile=" + nameFile + ", origineNameFile="
        + origineNameFile + ", successRow=" + successRow + ", errorRow=" + errorRow + ", totalRow="
        + totalRow + ", user=" + user + "]";
  }



}
