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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "smstemplate")
@EntityListeners(AuditingEntityListener.class)
public class Smstemplate implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long smstemplate_id;

  @CreatedDate

  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate = new Date();



  private String name;
  private String template;


  public Smstemplate() {}


  public Smstemplate(Long smstemplate_id, String name, String template) {

    this.smstemplate_id = smstemplate_id;
    this.name = name;
    this.template = template;
  }


  public Long getSmstemplate_id() {
    return smstemplate_id;
  }


  public void setSmstemplate_id(Long smstemplate_id) {
    this.smstemplate_id = smstemplate_id;
  }


  public Date getCreatedDate() {
    return createdDate;
  }


  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public String getTemplate() {
    return template;
  }


  public void setTemplate(String template) {
    this.template = template;
  }


  @Override
  public String toString() {
    return "Smstemplate [smstemplate_id=" + smstemplate_id + ", createdDate=" + createdDate
        + ", name=" + name + ", template=" + template + "]";
  }



  
}
