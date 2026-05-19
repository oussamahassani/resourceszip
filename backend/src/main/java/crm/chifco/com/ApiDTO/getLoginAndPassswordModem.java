package crm.chifco.com.ApiDTO;

public class getLoginAndPassswordModem {

  private String password;

  private String loginModem;
  private String loginControleParental;
  private String type;
  public Boolean controleParentaleActiver = false;
 

  public getLoginAndPassswordModem(String password, String loginModem, String loginControleParental, String type , Boolean controleParentaleActiver) {
	super();
	this.password = password;
	this.loginModem = loginModem;
	this.loginControleParental = loginControleParental;
	this.type = type;
	this.controleParentaleActiver =controleParentaleActiver;
}

public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getLoginModem() {
    return loginModem;
  }

  public void setLoginModem(String loginModem) {
    this.loginModem = loginModem;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getLoginControleParental() {
	return loginControleParental;
}

public void setLoginControleParental(String loginControleParental) {
	this.loginControleParental = loginControleParental;
}

public Boolean getControleParentaleActiver() {
	return controleParentaleActiver;
}

public void setControleParentaleActiver(Boolean controleParentaleActiver) {
	this.controleParentaleActiver = controleParentaleActiver;
}

@Override
public String toString() {
	return String.format(
			"getLoginAndPassswordModem [password=%s, loginModem=%s, loginControleParental=%s, type=%s, controleParentaleActiver=%s]",
			password, loginModem, loginControleParental, type, controleParentaleActiver);
}





}
