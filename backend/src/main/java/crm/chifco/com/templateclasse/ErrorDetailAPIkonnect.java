package crm.chifco.com.templateclasse;

public class ErrorDetailAPIkonnect {
  private String code;
  private String target;
  private String message;
  private Object source;


  public ErrorDetailAPIkonnect() {
    super();
    // TODO Auto-generated constructor stub
  }

  public ErrorDetailAPIkonnect(String code, String target, String message, Object source) {
    super();
    this.code = code;
    this.target = target;
    this.message = message;
    this.source = source;
  }

  public Object getSource() {
    return source;
  }

  public void setSource(Object source) {
    this.source = source;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }



}


