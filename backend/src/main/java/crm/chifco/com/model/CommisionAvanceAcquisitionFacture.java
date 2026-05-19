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
@Table(name = "CommisionAvanceAcquisitionFacture")
@EntityListeners(AuditingEntityListener.class)
public class CommisionAvanceAcquisitionFacture implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long idFactureAcquisition;

  private String referenceFacture;

  private String referenceDemandeDeCommision;

  public CommisionAvanceAcquisitionFacture() {
    super();
    // TODO Auto-generated constructor stub
  }

  public Long getIdFactureAcquisition() {
    return idFactureAcquisition;
  }

  public void setIdFactureAcquisition(Long idFactureAcquisition) {
    this.idFactureAcquisition = idFactureAcquisition;
  }

  public String getReferenceFacture() {
    return referenceFacture;
  }

  public void setReferenceFacture(String referenceFacture) {
    this.referenceFacture = referenceFacture;
  }

  public String getReferenceDemandeDeCommision() {
    return referenceDemandeDeCommision;
  }

  public void setReferenceDemandeDeCommision(String referenceDemandeDeCommision) {
    this.referenceDemandeDeCommision = referenceDemandeDeCommision;
  }

  @Override
  public String toString() {
    return "CommisionAvanceFacture [idFactureAcquisition=" + idFactureAcquisition
        + ", referenceFacture=" + referenceFacture + ", referenceDemandeDeCommision="
        + referenceDemandeDeCommision + "]";
  }



}
