package crm.chifco.com.service.impl;


import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import crm.chifco.com.service.Utilsjwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class JwtUtilsServiceImpl implements Utilsjwt {
  private static Logger logger = LogManager.getLogger(JwtUtilsServiceImpl.class);
  @Value("${crm.app.jwtSecret}")
  private String jwtSecret;

  @Value("${crm.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  @Override
  public String generateJwtToken(String authentication) {
    try {
      Instant expirationInstant = Instant.now().plusSeconds(jwtExpirationMs);
      logger.info("jwt expirationMs" + jwtExpirationMs);
      logger.info("jwt jwtSecret" + jwtSecret);
      logger.info("expirationInstant" + expirationInstant);
      logger.info("Date.from(expirationInstant)" + Date.from(expirationInstant));
      logger.info("setSubject" + authentication);
      logger.info("signWith" + Jwts.builder());



      byte[] apiKeySecretBytes = Base64.getDecoder().decode(jwtSecret);
      Key key = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.getJcaName());
      JwtBuilder builder = Jwts.builder().setSubject((authentication)).setIssuedAt(new Date())
          .signWith(SignatureAlgorithm.HS512, key).setExpiration(Date.from(expirationInstant));
      /*
       * JwtBuilder builder = Jwts.builder().setSubject("sami").setIssuedAt(new Date())
       * .setExpiration(new Date(System.currentTimeMillis() + 950000))
       * .signWith(SignatureAlgorithm.HS512, key);
       */

      return builder.compact();

    } catch (Exception e) {
      // Handle exception gracefully
      e.printStackTrace();
      return null;
    }

  }

  @Override
  public boolean validateJwtToken(String authToken) {
    try {

      Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
      return true;
    }
    /*
     * catch (SignatureException e) { logger.error("Invalid JWT signature: {}", e.getMessage()); }
     */
    catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    }
    /*
     * catch (ExpiredJwtException e) { logger.error("JWT token is expired: {}", e.getMessage()); }
     */

    catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

  public String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7, headerAuth.length());
    }

    return null;
  }
}
