package crm.chifco.com.model;

import com.fasterxml.jackson.annotation.JsonView;

public class VerifCINJsonResponseBody {
  @JsonView(Views.Public.class)
  String msg;

  @JsonView(Views.Public.class)
  String code;

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return "VerifCINJsonResponseBody{" + "msg='" + msg + '\'' + ", code='" + code + '\'' + '}';
  }
}
