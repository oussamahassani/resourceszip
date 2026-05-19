package crm.chifco.com.radius.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "radacct")
@EntityListeners(AuditingEntityListener.class)
public class Radacct {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "radacctid")
  private Long radacctid;
  String username;

  Date acctstarttime;

  Date acctstoptime;

  public Radacct() {
    super();
    // TODO Auto-generated constructor stub
  }

  public Radacct(String username, Date acctstarttime, Date acctstoptime) {
    super();
    this.username = username;
    this.acctstarttime = acctstarttime;
    this.acctstoptime = acctstoptime;
  }

  public Radacct(Date acctstarttime, Date acctstoptime) {
    this.acctstarttime = acctstarttime;
    this.acctstoptime = acctstoptime;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Date getAcctstarttime() {
    return acctstarttime;
  }

  public void setAcctstarttime(Date acctstarttime) {
    this.acctstarttime = acctstarttime;
  }

  public Date getAcctstoptime() {
    return acctstoptime;
  }

  public void setAcctstoptime(Date acctstoptime) {
    this.acctstoptime = acctstoptime;
  }

  @Override
  public String toString() {
    return "Radacct [username=" + username + ", acctstarttime=" + acctstarttime + ", acctstoptime="
        + acctstoptime + "]";
  }

}
