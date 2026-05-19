package crm.chifco.com.crmMobile;

import java.util.Date;

public class JwtResponse {
  private String token;
  private String refreshToken;
  private String phoneNumber;
  private String type = "Bearer";
  private Date expirationDateToken;
  private Date expirationDateRefreshToken;
  private UserDtoApp userDto;


  public UserDtoApp getUserDto() {
    return userDto;
  }

  public void setUserDto(UserDtoApp userDto) {
    this.userDto = userDto;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Date getExpirationDateToken() {
    return expirationDateToken;
  }

  public void setExpirationDateToken(Date expirationDateToken) {
    this.expirationDateToken = expirationDateToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public Date getExpirationDateRefreshToken() {
    return expirationDateRefreshToken;
  }

  public void setExpirationDateRefreshToken(Date expirationDateRefreshToken) {
    this.expirationDateRefreshToken = expirationDateRefreshToken;
  }

  public JwtResponse(String token, String refreshToken, String phoneNumber,
      Date expirationDateToken, Date expirationDateRefreshToken, UserDtoApp userDto) {
    super();
    this.token = token;
    this.refreshToken = refreshToken;
    this.phoneNumber = phoneNumber;
    this.type = "Bearer";
    this.userDto = userDto;
    this.expirationDateToken = expirationDateToken;
    this.expirationDateRefreshToken = expirationDateRefreshToken;
  }



}
