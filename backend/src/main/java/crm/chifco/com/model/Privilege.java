package crm.chifco.com.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "Privilege")
@EntityListeners(AuditingEntityListener.class)
public class Privilege implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "privilegeId")
  private Long privilegeId;

  @Column(name = "privilege_name", length = 65)
  private String privilegeName;

  @ManyToMany(mappedBy = "privileges")
  private Collection<Role> roles;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Privilege() {}



  public Privilege(Long privilegeId, String privilegeName, Collection<Role> roles, Date createdDate,
      Date modifiedDate) {
    super();
    this.privilegeId = privilegeId;
    this.privilegeName = privilegeName;
    this.roles = roles;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
  }



  public Privilege(String privilegeName) {
    this.privilegeName = privilegeName;
  }



  public String getPrivilegeName() {
    return privilegeName;
  }

  public void setPrivilegeName(String privilegeName) {
    this.privilegeName = privilegeName;
  }

  public Collection<Role> getRoles() {
    return roles;

  }

  public void setRoles(Collection<Role> roles) {
    this.roles = roles;
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
   * @return the privilegeId
   */
  public Long getPrivilegeId() {
    return privilegeId;
  }



  /**
   * @param privilegeId the privilegeId to set
   */
  public void setPrivilegeId(Long privilegeId) {
    this.privilegeId = privilegeId;
  }



  @Override
  public String toString() {
    return "Privilege [privilegeId=" + privilegeId + ", privilegeName=" + privilegeName + ", roles="
        + roles + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
  }



}
