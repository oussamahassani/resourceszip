package crm.chifco.com.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "PostalCode")
@EntityListeners(AuditingEntityListener.class)
public class PostalCode implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postalCodeId;

  @Column(length = 115)
  private String name;

  @Column(length = 115)
  private String code;

  @Column(length = 15)
  private String abreviation;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "villeId", nullable = false)
  private Ville ville;

  public PostalCode() {
    // TODO Auto-generated constructor stub
  }

  public PostalCode(Long postalCodeId, String name, String code, String abreviation, Ville ville) {
    super();
    this.postalCodeId = postalCodeId;
    this.name = name;
    this.code = code;
    this.abreviation = abreviation;
    this.ville = ville;
  }

  public Long getPostalCodeId() {
    return postalCodeId;
  }

  public void setPostalCodeId(Long postalCodeId) {
    this.postalCodeId = postalCodeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getAbreviation() {
    return abreviation;
  }

  public void setAbreviation(String abreviation) {
    this.abreviation = abreviation;
  }

  public Ville getVille() {
    return ville;
  }

  public void setVille(Ville ville) {
    this.ville = ville;
  }

  @Override
  public String toString() {
    return "PostalCode [postalCodeId=" + postalCodeId + ", name=" + name + ", code=" + code
        + ", abreviation=" + abreviation + ", ville=" + ville + "]";
  }

}
