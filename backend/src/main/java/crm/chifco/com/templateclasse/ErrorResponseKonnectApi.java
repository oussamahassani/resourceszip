package crm.chifco.com.templateclasse;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ErrorResponseKonnectApi {
  private List<ErrorDetailAPIkonnect> errors;

  /*
   * public String getMessage() { return message; }
   * 
   * public void setMessage(String message) { this.message = message; }
   */
  public List<ErrorDetailAPIkonnect> getErrors() {
    return errors;
  }

  public void setErrors(List<ErrorDetailAPIkonnect> errors) {
    this.errors = errors;
  }

  public ErrorResponseKonnectApi() {
    super();
    // TODO Auto-generated constructor stub
  }



}
