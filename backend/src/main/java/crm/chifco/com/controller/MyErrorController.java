package crm.chifco.com.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.UserRepository;

@Controller
public class MyErrorController implements ErrorController {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  UserRepository userRepository;

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request) {
    // do something like logging
    Exception e = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    // LOGGER.error("exeption" + e);
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    if (status != null) {

      Integer statusCode = Integer.valueOf(status.toString());

      if (statusCode == HttpStatus.NOT_FOUND.value()) {
        return "error/404";
      } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
        return "error/403";
      } else if (statusCode == HttpStatus.BAD_GATEWAY.value()) {
        return "error/500";
      }
    }
    return "error/error";
  }

  @GetMapping("/access-denied")
  public String accessDenied(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      model.addAttribute("isAdmin", user.getTypeUser().equals("ADMINISTRATEUR"));
    }
    return "error/access-denied";
  }

  /*
   * private int getErrorCode(HttpServletRequest httpRequest) { return (Integer)
   * httpRequest.getAttribute("javax.servlet.error.status_code"); }
   */
}
