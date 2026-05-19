package crm.chifco.com.netyTv;

import java.util.Date;

public class JwtResponseNetyTv {
  private String token;
  private String refreshToken;
  private String ip;
  private String type = "Bearer";
  private Date expirationDateToken;
  private Date expirationDateRefreshToken;
  private String referenceClient ;
  private Long telMobile;
private String cin;
private Long telFixe;
private String firstName;
private String lastName;



public JwtResponseNetyTv() {
	super();
	// TODO Auto-generated constructor stub
}



public String getToken() {
	return token;
}



public void setToken(String token) {
	this.token = token;
}



public String getRefreshToken() {
	return refreshToken;
}



public void setRefreshToken(String refreshToken) {
	this.refreshToken = refreshToken;
}



public String getIp() {
	return ip;
}



public void setIp(String ip) {
	this.ip = ip;
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



public Date getExpirationDateRefreshToken() {
	return expirationDateRefreshToken;
}



public void setExpirationDateRefreshToken(Date expirationDateRefreshToken) {
	this.expirationDateRefreshToken = expirationDateRefreshToken;
}



public String getReferenceClient() {
	return referenceClient;
}



public void setReferenceClient(String referenceClient) {
	this.referenceClient = referenceClient;
}



public Long getTelMobile() {
	return telMobile;
}



public void setTelMobile(Long telMobile) {
	this.telMobile = telMobile;
}



public String getCin() {
	return cin;
}



public void setCin(String cin) {
	this.cin = cin;
}



public Long getTelFixe() {
	return telFixe;
}



public void setTelFixe(Long telFixe) {
	this.telFixe = telFixe;
}



public String getFirstName() {
	return firstName;
}



public void setFirstName(String firstName) {
	this.firstName = firstName;
}



public String getLastName() {
	return lastName;
}



public void setLastName(String lastName) {
	this.lastName = lastName;
}



public JwtResponseNetyTv(String token2, String refreshToken2, String ip2, Date expiredDateToken,
		Date expiredDateRefreshToken, String referenceClient2, Long telFixe2, Long telMobile2, String cin2,
		String firstName2, String lastName2) {
	super();
	this.token = token2;
	this.refreshToken = refreshToken2;
	this.ip = ip2;

	this.expirationDateToken = expiredDateToken;
	this.expirationDateRefreshToken = expiredDateRefreshToken;
	this.referenceClient = referenceClient2;
	this.telMobile = telMobile2;
	this.cin = cin2;
	this.telFixe = telFixe2;
	this.firstName = firstName2;
	this.lastName = lastName2;
}



@Override
public String toString() {
	return "JwtResponseNetyTv [token=" + token + ", refreshToken=" + refreshToken + ", ip=" + ip + ", type=" + type
			+ ", expirationDateToken=" + expirationDateToken + ", expirationDateRefreshToken="
			+ expirationDateRefreshToken + ", referenceClient=" + referenceClient + ", telMobile=" + telMobile
			+ ", cin=" + cin + ", telFixe=" + telFixe + ", firstName=" + firstName + ", lastName=" + lastName + "]";
}







}
