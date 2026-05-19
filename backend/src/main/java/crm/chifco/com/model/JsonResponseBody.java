package crm.chifco.com.model;

import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

public class JsonResponseBody {
  @JsonView(Views.Public.class)
  String msg;

  @JsonView(Views.Public.class)
  String code;

  @JsonView(Views.Public.class)
  List<Long> result;

  @JsonView(Views.Public.class)
  HashSet<Long> resultset;

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

  public List<Long> getResult() {
    return result;
  }

  public void setResult(List<Long> result) {
    this.result = result;
  }

  public void setResultSet(HashSet<Long> result) {
    this.resultset = resultset;
  }

  @Override
  public String toString() {
    return "JsonResponseBody{" + "msg='" + msg + '\'' + ", code='" + code + '\'' + ", result="
        + result + ",resultset=" + resultset + '}';
  }
}
