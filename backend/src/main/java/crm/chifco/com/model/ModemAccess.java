package crm.chifco.com.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ModemAccess")
@EntityListeners(AuditingEntityListener.class)
public class ModemAccess implements Serializable {
  private static long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGen")
  @SequenceGenerator(name = "seqGen", sequenceName = "modem_id_seqs", allocationSize = 1)

  private Long modemAccessId;

  private String modelModem;

  private Boolean status = true;


  private String email;

  private String password;

  private Long idModem;



  public ModemAccess() {
    super();
  }



  public ModemAccess(Long modemAccessId, String modelModem, Boolean status, String email,
      String password, Long idModem) {
    super();
    this.modemAccessId = modemAccessId;
    this.modelModem = modelModem;
    this.status = status;
    this.email = email;
    this.password = password;
    this.idModem = idModem;
  }



  public static long getSerialversionuid() {
    return serialVersionUID;
  }



  public static void setSerialversionuid(long serialversionuid) {
    serialVersionUID = serialversionuid;
  }



  public Long getModemAccessId() {
    return modemAccessId;
  }



  public void setModemAccessId(Long modemAccessId) {
    this.modemAccessId = modemAccessId;
  }



  public String getModelModem() {
    return modelModem;
  }



  public void setModelModem(String modelModem) {
    this.modelModem = modelModem;
  }



  public Boolean getStatus() {
    return status;
  }



  public void setStatus(Boolean status) {
    this.status = status;
  }



  public String getEmail() {
    return email;
  }



  public void setEmail(String email) {
    this.email = email;
  }



  public String getPassword() {
    return password;
  }



  public void setPassword(String password) {
    this.password = password;
  }



  public Long getIdModem() {
    return idModem;
  }



  public void setIdModem(Long idModem) {
    this.idModem = idModem;
  }



  @Override
  public String toString() {
    return "ModemAccess [modemAccessId=" + modemAccessId + ", modelModem=" + modelModem
        + ", status=" + status + ", email=" + email + ", password=" + password + ", idModem="
        + idModem + "]";
  }



}
