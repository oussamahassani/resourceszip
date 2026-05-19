package crm.chifco.com.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "classificationDemande")
public class ClassificationDemande implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long classificationId;

  private String value;

  private Boolean isCommision = false;
  private String codeClassification;

  public ClassificationDemande() {
    super();
    // TODO Auto-generated constructor stub
  }

  public ClassificationDemande(Long classificationId, String value, Boolean isCommision) {
    super();
    this.classificationId = classificationId;
    this.value = value;
    this.isCommision = isCommision;
  }

  public Long getClassificationId() {
    return classificationId;
  }

  public void setClassificationId(Long classificationId) {
    this.classificationId = classificationId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Boolean getIsCommision() {
    return isCommision;
  }

  public void setIsCommision(Boolean isCommision) {
    this.isCommision = isCommision;
  }

  public String getCodeClassification() {
    return codeClassification;
  }

  public void setCodeClassification(String codeClassification) {
    this.codeClassification = codeClassification;
  }

  @Override
  public String toString() {
    return "ClassificationDemande [classificationId=" + classificationId + ", value=" + value
        + ", isCommision=" + isCommision + "]";
  }

}
