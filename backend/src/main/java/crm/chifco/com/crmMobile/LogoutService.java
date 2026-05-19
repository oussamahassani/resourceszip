package crm.chifco.com.crmMobile;

import java.util.Date;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class LogoutService implements LogoutHandler {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    SecurityContextHolder.clearContext();
    Date now = new Date();
    Date expiration = new Date(now.getTime() + 60000);
    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    String shortLivedToken = Jwts.builder().setSubject(authentication.getName()).setIssuedAt(now)
        .setExpiration(expiration).signWith(key, SignatureAlgorithm.HS256).compact();
    response.setHeader("Authorization", "Bearer " + shortLivedToken);
  }
}
