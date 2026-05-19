package crm.chifco.com.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.RoleRepository;
import crm.chifco.com.repository.UserRepository;

@Controller
@RequestMapping(value = "others/*")
public class DistributeurController {
  @Autowired
  UserRepository userRepository;
  @Autowired
  RoleRepository roleRepository;

  @Value("${pathphoto}")
  private String pathphoto;

  private final Logger logger = LogManager.getLogger(this.getClass());

  @GetMapping(value = "dashboard")
  public ModelMap mmDashboard(Model model, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");
    }
    return new ModelMap();
  }
}
