package crm.chifco.com.service;

import javax.servlet.http.HttpServletRequest;

public interface Utilsjwt {

  String generateJwtToken(String authentication);

  boolean validateJwtToken(String authToken);

  String parseJwt(HttpServletRequest request);

}
