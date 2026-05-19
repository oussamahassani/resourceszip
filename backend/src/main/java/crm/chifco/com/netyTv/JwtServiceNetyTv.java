package crm.chifco.com.netyTv;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import crm.chifco.com.model.Abonnement;
import crm.chifco.com.radius.repository.RadacctRepository;
import crm.chifco.com.repository.AbonnementRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtServiceNetyTv {

  @Value("${jwt.secretnetytv}")
  private String jwtSecret;

  @Value("${jwt.expirationMsnetytv}")
  private int jwtExpirationMs;

  @Value("${jwt.refreshExpirationMsnetytv}")
  private int jwtRefreshExpirationMs;

  @Autowired
  public AbonnementRepository abonnementRepository;
  
  @Autowired
  public RadacctRepository radacctRepository;
  
  private static final Logger logger = LogManager.getLogger(JwtServiceNetyTv.class);

  public String generateJwtTokenNetyTV(String ip) {

    return Jwts.builder().setSubject(ip).setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
  }

  public String generateRefreshToken(String ip) {
    return Jwts.builder().setSubject(ip).setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationMs))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
  }

  public boolean validateJwtToken(String authToken) {
    logger.info("Validating JWT token: {}", authToken);
    long periodCount = authToken.chars().filter(ch -> ch == '.').count();

    if (periodCount != 2) {
      logger.error("Invalid JWT token format: Compact JWSs must contain exactly 2 periods.");
      return false;
    }

    try {
      Jwts.parser().setSigningKey(getSignInKey()).build().parseClaimsJws(authToken);
      return true; // Token is valid
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token (Malformed): {}", e.getMessage());
    } catch (SignatureException e) {
      logger.error("JWT signature does not match: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    } catch (Exception e) {
      logger.error("JWT token validation error: {}", e.getMessage());
    }
    return false;
  }


  public String getIPFromJwtToken(String token) {
    Claims claims =
        Jwts.parser().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    return claims.getSubject();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public Map<String, Object> refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    Map<String, Object> responseBody = new HashMap<>();
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    try {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return createErrorResponse(HttpServletResponse.SC_UNAUTHORIZED,
            "Jeton de rafraîchissement non valide ou manquant.");
      }

      String refreshToken = authHeader.substring(7);
      if (!validateJwtToken(refreshToken)) {
        return createErrorResponse(HttpServletResponse.SC_UNAUTHORIZED,
            "Jeton de rafraîchissement invalide ou expiré.");
      }

      String ip = getIPFromJwtToken(refreshToken);
      if (ip == null) {
        return createErrorResponse(HttpServletResponse.SC_UNAUTHORIZED,
            "Impossible d'extraire l'adresse IP du jeton.");
      }

      // 🔹 Generate a new access token
      String newAccessToken = generateNewAccessToken(ip);

      // 🔹 Get expiration dates
      Date expirationDateAccessToken = getExpirationDateFromToken(newAccessToken);
      Date expirationDateRefreshToken = getExpirationDateFromToken(refreshToken);
      String userName = radacctRepository.getUsernameexistsByIpAddress(ip);

      Abonnement client = abonnementRepository.findClientByLoginModem(userName);
      
      JwtResponseNetyTv authResponse = new JwtResponseNetyTv(newAccessToken, refreshToken, ip,
    		  expirationDateAccessToken,expirationDateRefreshToken ,client.getReferenceClient() , 
          client.getTelFixe(), client.getTelMobile(),client.getCin() , client.getFirstName() , client.getLastName() );
   

      return createSuccessResponse(HttpServletResponse.SC_OK,
          "Le jeton a été actualisé avec succès.", authResponse);

    } catch (Exception e) {
      return createErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "Erreur lors du traitement du jeton JWT: " + e.getMessage());
    }
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
  }

  public Date getExpirationDateFromToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.getExpiration();
  }

  public Date getExpirationDateFromRefreshToken(String refreshtoken) {
    Claims claims = extractAllClaims(refreshtoken);
    return claims.getExpiration();
  }

  private Map<String, Object> createSuccessResponse(int code, String message, Object data) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", true);
    response.put("code", code);
    response.put("message", message);
    response.put("data", data);
    return response;
  }

  /**
   * Helper method to create an error response.
   */
  private Map<String, Object> createErrorResponse(int code, String message) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", false);
    response.put("code", code);
    response.put("message", message);
    response.put("data", null);
    return response;
  }

  private String generateNewAccessToken(String ip) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + jwtExpirationMs); // Define expiration time

    return Jwts.builder().setSubject(ip) // Use IP as subject or change based on your needs
        .setIssuedAt(now).setExpiration(expirationDate)
        .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Signing with secret key
        .compact();
  }

}
