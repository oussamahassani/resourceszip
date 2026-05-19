package crm.chifco.com.model;

import java.io.Serializable;
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
@Table(name = "villes")
@EntityListeners(AuditingEntityListener.class)
public class Ville implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long villeId;

  @Column(length = 75)
  private String villeName;

  @Column(length = 15)
  private String abreviation;

  @ManyToOne
  @JoinColumn(name = "gouvernorat_id", nullable = false)
  private Gouvernorat gouvernerat;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Ville() {}

  public Ville(Long villeId, String villeName, String abreviation, Gouvernorat gouvernerat,
      Date createdDate, Date modifiedDate) {
    super();
    this.villeId = villeId;
    this.villeName = villeName;
    this.abreviation = abreviation;
    this.gouvernerat = gouvernerat;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }

  public Long getVilleId() {
    return villeId;
  }

  public void setVilleId(Long villeId) {
    this.villeId = villeId;
  }

  public String getVilleName() {
    return villeName;
  }

  public void setVilleName(String villeName) {
    this.villeName = villeName;
  }

  public String getAbreviation() {
    return abreviation;
  }

  public void setAbreviation(String abreviation) {
    this.abreviation = abreviation;
  }

  public Gouvernorat getGouvernerat() {
    return gouvernerat;
  }

  public void setGouvernerat(Gouvernorat gouvernerat) {
    this.gouvernerat = gouvernerat;
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
    return "Ville [villeId=" + villeId + ", villeName=" + villeName + ", abreviation=" + abreviation
        + ", gouvernerat=" + gouvernerat + ", createdDate=" + createdDate + ", modifiedDate="
        + modifiedDate + "]";
  }

}
