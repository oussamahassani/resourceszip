package crm.chifco.com.configuration;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  private static Logger logger =
      LogManager.getLogger(MySimpleUrlAuthenticationSuccessHandler.class);

  private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  public MySimpleUrlAuthenticationSuccessHandler() {
    super();
  }

  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    handle(request, response, authentication);
    clearAuthenticationAttributes(request);
  }

  protected void handle(final HttpServletRequest request, final HttpServletResponse response,
      final Authentication authentication) throws IOException {
    String targetUrl = determineTargetUrl(authentication);
    // String redirectUrl = request.getParameter("redirect");
    // final String finalRedirectUrl = redirectUrl != null ? redirectUrl : targetUrl;

    // If the redirect parameter is present, use it; otherwise, use the determined target URL
    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    redirectStrategy.sendRedirect(request, response, targetUrl);
  }

  protected String determineTargetUrl(final Authentication authentication) {

    Map<String, String> roleTargetUrlMap = new HashMap<>();

    roleTargetUrlMap.put("VIEW_DASHBORD_ADMIN", "/admin/dashboard");

    roleTargetUrlMap.put("VIEW_DASHBORD_OTHER", "/alluser/dashboard");

    final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    for (final GrantedAuthority grantedAuthority : authorities) {

      String authorityName = grantedAuthority.getAuthority();

      if (roleTargetUrlMap.containsKey(authorityName)) {

        return roleTargetUrlMap.get(authorityName);
      }
    }

    throw new IllegalStateException();
  }

  protected final void clearAuthenticationAttributes(final HttpServletRequest request) {
    final HttpSession session = request.getSession(false);

    if (session == null) {
      return;
    }

    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
  }

}
