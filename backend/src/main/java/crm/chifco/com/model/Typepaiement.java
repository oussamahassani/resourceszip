package crm.chifco.com.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "typepaiement")
@EntityListeners(AuditingEntityListener.class)
public class Typepaiement {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  private Long typePaiementId;

  @Column(unique = true)
  private String referenceTypePaiement;

  private String nomTypePaiement;

  private Integer nombreMoisTypePaiement;

  public Typepaiement() {}

  public Typepaiement(String referenceTypePaiement, String nomTypePaiement,
      Integer nombreMoisTypePaiement) {
    this.referenceTypePaiement = referenceTypePaiement;
    this.nomTypePaiement = nomTypePaiement;
    this.nombreMoisTypePaiement = nombreMoisTypePaiement;
  }

  public Long getTypePaiementId() {
    return typePaiementId;
  }

  public void setTypePaiementId(Long typePaiementId) {
    this.typePaiementId = typePaiementId;
  }

  public String getReferenceTypePaiement() {
    return referenceTypePaiement;
  }

  public void setReferenceTypePaiement(String referenceTypePaiement) {
    this.referenceTypePaiement = referenceTypePaiement;
  }

  public String getNomTypePaiement() {
    return nomTypePaiement;
  }

  public void setNomTypePaiement(String nomTypePaiement) {
    this.nomTypePaiement = nomTypePaiement;
  }

  public Integer getNombreMoisTypePaiement() {
    return nombreMoisTypePaiement;
  }

  public void setNombreMoisTypePaiement(Integer nombreMoisTypePaiement) {
    this.nombreMoisTypePaiement = nombreMoisTypePaiement;
  }

  @Override
  public String toString() {
    return "Typepaiement [typePaiementId=" + typePaiementId + ", referenceTypePaiement="
        + referenceTypePaiement + ", nomTypePaiement=" + nomTypePaiement
        + ", nombreMoisTypePaiement=" + nombreMoisTypePaiement + "]";
  }

}
