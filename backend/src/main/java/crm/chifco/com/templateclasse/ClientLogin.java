package crm.chifco.com.templateclasse;

public class ClientLogin {
  private String clientSecret;
  private String grant_type;



  public ClientLogin() {
    super();
  }

  public ClientLogin(String clientSecret, String grant_type) {
    super();
    this.clientSecret = clientSecret;
    this.grant_type = grant_type;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getGrant_type() {
    return grant_type;
  }

  public void setGrant_type(String grant_type) {
    this.grant_type = grant_type;
  }

}
