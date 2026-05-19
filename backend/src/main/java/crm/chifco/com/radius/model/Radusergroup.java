package crm.chifco.com.radius.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "radusergroup")
@EntityListeners(AuditingEntityListener.class)
public class Radusergroup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "username")
  private String username;
  @Column(name = "groupname")
  private String groupname;
  @Column(name = "priority")
  private String priority;

  public Radusergroup() {}

  public Radusergroup(Long id, String username, String groupname, String priority) {
    super();
    this.id = id;
    this.username = username;
    this.groupname = groupname;
    this.priority = priority;
  }

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the groupname
   */
  public String getGroupname() {
    return groupname;
  }

  /**
   * @param groupname the groupname to set
   */
  public void setGroupname(String groupname) {
    this.groupname = groupname;
  }

  /**
   * @return the priority
   */
  public String getPriority() {
    return priority;
  }

  /**
   * @param priority the priority to set
   */
  public void setPriority(String priority) {
    this.priority = priority;
  }

  @Override
  public String toString() {
    return "Radusergroup [id=" + id + ", username=" + username + ", groupname=" + groupname
        + ", priority=" + priority + "]";
  }

}
