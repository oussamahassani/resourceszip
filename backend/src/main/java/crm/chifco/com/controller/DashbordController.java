package crm.chifco.com.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.DashboardService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.UserTypeConstant;

@Controller
@RequestMapping(value = "alluser/*")
public class DashbordController {
  @Autowired
  UserRepository userRepository;
  @Autowired
  DashboardService dashboardService;
  @Autowired
  UserService UserService;
  private final Logger logger = LogManager.getLogger(this.getClass());

  @GetMapping(value = "dashboard")
  public String otherDashboard(Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<User> listeDistributeur =
          userRepository.findUsersByTypeUser(UserTypeConstant.DISTRIBUTEUR);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("listeDistributeur", listeDistributeur);
      dashboardService.returnDashbordStatsRevPosDist(model);
    }
    return "others/dashboard";

  }


}
