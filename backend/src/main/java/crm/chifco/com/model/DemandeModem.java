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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "demandemodem")
@EntityListeners(AuditingEntityListener.class)

public class DemandeModem implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "idDemandeModem")
  private Long idDemandeModem;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userid")
  private User user;

  @Column(name = "quantiter")
  private String quantiter;

  private String typeModem;

  public DemandeModem() {
    // TODO Auto-generated constructor stub
  }

  public DemandeModem(Long idDemandeModem, Date createdDate, Date modifiedDate, User user,
      String quantiter, String typeModem) {
    super();
    this.idDemandeModem = idDemandeModem;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.user = user;
    this.quantiter = quantiter;
    this.typeModem = typeModem;
  }

  /**
   * @return the idDemandeModem
   */
  public Long getIdDemandeModem() {
    return idDemandeModem;
  }

  /**
   * @param idDemandeModem the idDemandeModem to set
   */
  public void setIdDemandeModem(Long idDemandeModem) {
    this.idDemandeModem = idDemandeModem;
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
   * @return the modifiedDate
   */
  public Date getModifiedDate() {
    return modifiedDate;
  }

  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
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

  /**
   * @return the quantiter
   */
  public String getQuantiter() {
    return quantiter;
  }

  /**
   * @param quantiter the quantiter to set
   */
  public void setQuantiter(String quantiter) {
    this.quantiter = quantiter;
  }

  /**
   * @return the typeModem
   */
  public String getTypeModem() {
    return typeModem;
  }

  /**
   * @param typeModem the typeModem to set
   */
  public void setTypeModem(String typeModem) {
    this.typeModem = typeModem;
  }

  @Override
  public String toString() {
    return "DemandeModem [idDemandeModem=" + idDemandeModem + ", createdDate=" + createdDate
        + ", modifiedDate=" + modifiedDate + ", user=" + user + ", quantiter=" + quantiter
        + ", typeModem=" + typeModem + "]";
  }


}
