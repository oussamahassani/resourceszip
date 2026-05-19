package crm.chifco.com.crmMobile;

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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.UserRepository;
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
public class JwtService {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expirationMs}")
  private int jwtExpirationMs;

  @Value("${jwt.refreshExpirationMs}")
  private int jwtRefreshExpirationMs;
  @Autowired
  private UserRepository userRepository;

  private static final Logger logger = LogManager.getLogger(JwtService.class);

  public String generateJwtToken(String phoneNumber) {
    return Jwts.builder().setSubject(phoneNumber).setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
  }

  public String generateRefreshToken(String phoneNumber) {
    return Jwts.builder().setSubject(phoneNumber).setIssuedAt(new Date())
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


  public String getPhoneNumberFromJwtToken(String token) {
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
        responseBody.put("status", false);
        responseBody.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        responseBody.put("message", "Jeton de rafraîchissement non valide ou manquant.");
        responseBody.put("data", null);
        return responseBody;
      }

      String refreshToken = authHeader.substring(7);
      if (validateJwtToken(refreshToken)) {
        String phoneNumber = getPhoneNumberFromJwtToken(refreshToken);

        if (phoneNumber != null) {
          User user =
              userRepository.findTop1UsersByUserid(Long.parseLong(phoneNumber), "DISTRIBUTEUR");
          if (user == null) {
            responseBody.put("status", false);
            responseBody.put("code", HttpStatus.NOT_FOUND);
            responseBody.put("message", "L'utilisateur avec ce numéro de téléphone n'existe pas.");
            responseBody.put("data", null);
            return responseBody;

          }
          UserDtoApp userDto = UserDtoApp.fromEntity(user);
          String newAccessToken = generateJwtToken(phoneNumber);
          Date exiprationDateToken = getExpirationDateFromToken(newAccessToken);
          Date exiprationDateRefreshToken = getExpirationDateFromToken(refreshToken);
          JwtResponse authResponse = new JwtResponse(newAccessToken, refreshToken,
              user.getTelephone(), exiprationDateToken, exiprationDateRefreshToken, userDto);

          responseBody.put("status", true);
          responseBody.put("code", HttpServletResponse.SC_OK);
          responseBody.put("message", "Le jeton a été actualisé avec succès.");
          responseBody.put("data", authResponse);
          return responseBody;
        } else {
          responseBody.put("status", false);
          responseBody.put("code", HttpServletResponse.SC_UNAUTHORIZED);
          responseBody.put("message", "Impossible d'extraire le numéro de téléphone du jeton.");
          responseBody.put("data", null);
          return responseBody;
        }
      } else {
        responseBody.put("status", false);
        responseBody.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        responseBody.put("message", "Jeton de rafraîchissement invalide/expiré.");
        responseBody.put("data", null);
        return responseBody;
      }
    } catch (Exception e) {
      responseBody.put("status", false);
      responseBody.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      responseBody.put("message", "Erreur lors du traitement du jeton JWT ou jeton JWT invalide.");
      responseBody.put("data", null);
      return responseBody;
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
}
