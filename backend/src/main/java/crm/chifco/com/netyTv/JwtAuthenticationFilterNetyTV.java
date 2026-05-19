package crm.chifco.com.netyTv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

public class JwtAuthenticationFilterNetyTV extends OncePerRequestFilter {
  @Autowired
  private JwtServiceNetyTv jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String path = request.getServletPath();
    if (!path.startsWith("/netytv") || path.startsWith("/netytv/auth/")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String jwt = parseJwt(request);
      if (jwt == null) {
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token non trouvé", null);
        return;
      }

      if (!isValidJwtFormat(jwt)) {
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Malformé JWT token", null);
        return;
      }

      try {
        if (jwtService.validateJwtToken(jwt)) {
          setAuthentication(jwt);
        } else if (path.startsWith("/netytv/refresh-token")) {
          jwtService.refreshToken(request, response);
          return;
        } else {
          sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token invalide ou expiré",
              null);
          return;
        }
      } catch (ExpiredJwtException | SignatureException e) {
        handleJwtException(e, response);
        return;
      }
    } catch (Exception e) {
      handleUnexpectedException(e, response);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isValidJwtFormat(String jwt) {
    long periodCount = jwt.chars().filter(ch -> ch == '.').count();
    return periodCount == 2;
  }

  private void setAuthentication(String jwt) {
    String ip = jwtService.getIPFromJwtToken(jwt);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(ip, null, new ArrayList<>());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private void handleJwtException(Exception e, HttpServletResponse response) throws IOException {
    if (e instanceof ExpiredJwtException) {
      logger.error("JWT token is expired: {}" + e.getMessage());
      sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token expiré", null);
    } else if (e instanceof SignatureException) {
      logger.error("JWT signature does not match: {}" + e.getMessage());
      sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Signature JWT invalide", null);
    }
  }

  private void handleUnexpectedException(Exception e, HttpServletResponse response)
      throws IOException {
    Throwable cause = e.getCause();
    if (cause instanceof ExpiredJwtException) {
      logger.error("JWT token is expired (wrapped): {}" + cause.getMessage());
      sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token expiré", null);
    } else {
      logger.error("Cannot set user authentication: {}" + e.getMessage());
      sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR,
          "Erreur de traitement du jeton JWT/ou invalide JWT.", null);
    }
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }
    return null;
  }

  private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message,
      Object data) throws IOException {
    Map<String, Object> body = new HashMap<>();
    body.put("status", false);
    body.put("code", status.value());
    body.put("message", message);
    body.put("data", data);
    response.setStatus(status.value());
    response.setContentType("application/json");
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getWriter(), body);
  }
}
