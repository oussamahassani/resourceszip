package crm.chifco.com.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.service.CronService;
import crm.chifco.com.service.FacturationTTCalculeService;
import crm.chifco.com.service.UserService;

@Controller
@RequestMapping(value = "facturationTT/*")
public class facturationTTCalculeController {
  private final Logger Logger = LogManager.getLogger(this.getClass());

  @Autowired
  FacturationTTCalculeService facturationTTCalculeService;

  @Autowired
  private UserService userService;

  @Autowired
  CronService cronService;


  @PreAuthorize("hasAnyAuthority('EXPORT_TT_FACTURE')")
  @GetMapping("homeView")
  public String homeView(Model model) {

    userService.returnInfoUserConnected(model);
    return "facturationTT/home";
  }

  
  @PreAuthorize("hasAnyAuthority('EXPORT_TT_FACTURE')")
  @GetMapping("extractenmasse")
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,

      @RequestParam(value = "date", required = false) String date) {
    return facturationTTCalculeService.exportToExcel(request, response, date);
  }

}
