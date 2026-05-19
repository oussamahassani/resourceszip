package crm.chifco.com.crmMobile;

import java.util.List;
import crm.chifco.com.model.Modem;

public class ModemVerificationResult {
  private String status;
  private List<Modem> modemValid;
  private List<Modem> modemNotValid;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<Modem> getModemValid() {
    return modemValid;
  }

  public void setModemValid(List<Modem> modemValid) {
    this.modemValid = modemValid;
  }

  public List<Modem> getModemNotValid() {
    return modemNotValid;
  }

  public void setModemNotValid(List<Modem> modemNotValid) {
    this.modemNotValid = modemNotValid;
  }

  public ModemVerificationResult() {
    super();
  }

  public ModemVerificationResult(String status, List<Modem> modemValid, List<Modem> modemNotValid) {
    super();
    this.status = status;
    this.modemValid = modemValid;
    this.modemNotValid = modemNotValid;
  }



}
