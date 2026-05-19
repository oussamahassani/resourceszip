package crm.chifco.com.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "RecuNumeroSequence")
@EntityListeners(AuditingEntityListener.class)
public class RecuNumeroSequence implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long recuNumeroSequenceId;

  private Double montantTotal;

  @Column(name = "codePayement")
  private String codePayement;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;


  

  public RecuNumeroSequence() {}

  public RecuNumeroSequence(Long recuNumeroSequenceId, Double montantTotal, String codePayement,
      User user) {
    super();
    this.recuNumeroSequenceId = recuNumeroSequenceId;
    this.montantTotal = montantTotal;
    this.codePayement = codePayement;
    this.user = user;
  }

  public Long getRecuNumeroSequenceId() {
    return recuNumeroSequenceId;
  }

  public void setRecuNumeroSequenceId(Long recuNumeroSequenceId) {
    this.recuNumeroSequenceId = recuNumeroSequenceId;
  }

  public Double getMontantTotal() {
    return montantTotal;
  }

  public void setMontantTotal(Double montantTotal) {
    this.montantTotal = montantTotal;
  }

  public String getCodePayement() {
    return codePayement;
  }

  public void setCodePayement(String codePayement) {
    this.codePayement = codePayement;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "RecuNumeroSequence [recuNumeroSequenceId=" + recuNumeroSequenceId + ", montantTotal="
        + montantTotal + ", codePayement=" + codePayement + ", user=" + user + "]";
  }


 


}
