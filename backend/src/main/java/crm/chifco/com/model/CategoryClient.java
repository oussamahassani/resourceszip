package crm.chifco.com.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "categoryClient")
@EntityListeners(AuditingEntityListener.class)
public class CategoryClient implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;


  @Column(length = 75)
  private String label;


  public CategoryClient() {
    super();
    // TODO Auto-generated constructor stub
  }


  public CategoryClient(Long id, String label) {
    super();
    this.id = id;
    this.label = label;
  }


  public Long getId() {
    return id;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public String getLabel() {
    return label;
  }


  public void setLabel(String label) {
    this.label = label;
  }


  @Override
  public String toString() {
    return "CategoryClient [id=" + id + ", label=" + label + "]";
  }



}
