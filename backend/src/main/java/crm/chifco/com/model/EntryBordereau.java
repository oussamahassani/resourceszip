package crm.chifco.com.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "EntryBordereau")
public class EntryBordereau {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long entryId;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "encaissement_id")
  private Encaissement encaissement;

  private Boolean eligibleCommision = false;

  public EntryBordereau(Date createdDate, Encaissement encaissement) {
    this.createdDate = createdDate;
    this.encaissement = encaissement;
  }

  public EntryBordereau() {
    // TODO Auto-generated constructor stub
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }


  public Encaissement getEncaissement() {
    return encaissement;
  }

  public void setEncaissement(Encaissement encaissement) {
    this.encaissement = encaissement;
  }

  /**
   * @return the entryId
   */
  public Long getEntryId() {
    return entryId;
  }

  /**
   * @param entryId the entryId to set
   */
  public void setEntryId(Long entryId) {
    this.entryId = entryId;
  }

  public Boolean getEligibleCommision() {
    return eligibleCommision;
  }

  public void setEligibleCommision(Boolean eligibleCommision) {
    this.eligibleCommision = eligibleCommision;
  }

  @Override
  public String toString() {
    return "EntryBordereau [entryId=" + entryId + ", createdDate=" + createdDate + ", encaissement="
        + encaissement + "]";
  }


}
