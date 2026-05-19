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
@Table(name = "EntryAvoirClient")
@EntityListeners(AuditingEntityListener.class)
public class EntryAvoirClient {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "entryAvoirId")
  private Long entryAvoirId;

  private Double montantHt;
  private Double montantTva;
  private Double montantTTc;
  private Double baseTva;
  @Column(length = 8000)
  private String libele;

  public EntryAvoirClient(Long entryAvoirId, Double montantHt, Double montantTva, Double baseTva,
      String libele, Double montantTTc) {
    super();
    this.entryAvoirId = entryAvoirId;
    this.montantHt = montantHt;
    this.montantTva = montantTva;
    this.baseTva = baseTva;
    this.libele = libele;
    this.montantTTc = montantTTc;
  }

  public EntryAvoirClient() {
    // TODO Auto-generated constructor stub
  }

  public Long getEntryAvoirId() {
    return entryAvoirId;
  }

  public void setEntryAvoirId(Long entryAvoirId) {
    this.entryAvoirId = entryAvoirId;
  }

  public Double getMontantHt() {
    return montantHt;
  }

  public void setMontantHt(Double montantHt) {
    this.montantHt = montantHt;
  }

  public Double getMontantTva() {
    return montantTva;
  }

  public void setMontantTva(Double montantTva) {
    this.montantTva = montantTva;
  }

  public Double getBaseTva() {
    return baseTva;
  }

  public void setBaseTva(Double baseTva) {
    this.baseTva = baseTva;
  }

  public String getLibele() {
    return libele;
  }

  public void setLibele(String libele) {
    this.libele = libele;
  }

  public Double getMontantTTc() {
    return montantTTc;
  }

  public void setMontantTTc(Double montantTTc) {
    this.montantTTc = montantTTc;
  }

  @Override
  public String toString() {
    return "EntryAvoirClient [entryAvoirId=" + entryAvoirId + ", montantHt=" + montantHt
        + ", montantTva=" + montantTva + ", baseTva=" + baseTva + ", libele=" + libele + "]";
  }

}
