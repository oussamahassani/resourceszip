package crm.chifco.com.templateclasse;

public class LoginDtoAcs {

  private String usernameOrEmail;
  private String password;

  public LoginDtoAcs() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @return the usernameOrEmail
   */
  public String getUsernameOrEmail() {
    return usernameOrEmail;
  }

  /**
   * @param usernameOrEmail the usernameOrEmail to set
   */
  public void setUsernameOrEmail(String usernameOrEmail) {
    this.usernameOrEmail = usernameOrEmail;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "LoginDtoAcs [usernameOrEmail=" + usernameOrEmail + ", password=" + password + "]";
  }

}
