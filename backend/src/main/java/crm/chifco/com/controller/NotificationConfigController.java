package crm.chifco.com.controller;

import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.NotificationConfig;
import crm.chifco.com.service.GouverneratsService;
import crm.chifco.com.service.NotificationConfigService;
import crm.chifco.com.service.UserService;

@Controller
@RequestMapping(value = "NotificationConfig/*")
public class NotificationConfigController {

  @Autowired
  UserService UserService;

  @Autowired
  GouverneratsService VilleService;

  @Autowired
  NotificationConfigService NotificationConfigService;

  @RequestMapping(method = RequestMethod.GET, value = "getListConfigNotif")
  public String createVille(Model model) {
    UserService.returnInfoUserConnected(model);
    return "notification/configNotification";
  }

  @GetMapping("add-notification")
  public String adduser(Model model) {
    UserService.returnInfoUserConnected(model);
    List<Gouvernorat> villes = VilleService.findAllGouvernorat();
    model.addAttribute("villes", villes);
    return "notification/addNotification";
  }

  @RequestMapping(method = RequestMethod.POST, value = "add-notifications")
  public String adduser(
      @RequestParam(value = "actionNotification", required = false) String actionNotification,
      @RequestParam("listeEmail") String listeEmail,
      @RequestParam("Listetélephone") String Listetélephone,

      @RequestParam("gouvernorat") Gouvernorat ville

  ) {
    NotificationConfigService.addNewConfigNotifiation(actionNotification, listeEmail,
        Listetélephone, ville);
    return "redirect:/NotificationConfig/getListConfigNotif";
  }

  @RequestMapping(method = RequestMethod.GET, value = "getallNotificationConfig")
  @ResponseBody
  public HashMap<String, Object> getallNotificationConfigt(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    return NotificationConfigService.getNotificationConfig(draw, start, length, search,
        ordercolumnaram, orderdir, filterrecherche);

  }

  @RequestMapping(method = RequestMethod.GET, value = "getDetailsConfig/{configid}")
  public String getDetailsConfig(@PathVariable("configid") Long configid, Model model) {
    UserService.returnInfoUserConnected(model);
    NotificationConfig oneNotification =
        NotificationConfigService.getOneNotificationConfigById(configid);

    model.addAttribute("notificationconfig", oneNotification);
    return "notification/viewNotifaction";
  }

  @RequestMapping(method = RequestMethod.GET, value = "editConfig/{configid}")
  public String editConfig(@PathVariable("configid") Long configid, Model model) {
    UserService.returnInfoUserConnected(model);
    NotificationConfig oneNotification =
        NotificationConfigService.getOneNotificationConfigById(configid);
    List<Gouvernorat> villes = VilleService.findAllGouvernorat();
    model.addAttribute("villes", villes);
    model.addAttribute("notificationconfig", oneNotification);
    return "notification/editNotification";
  }

  @RequestMapping(method = RequestMethod.POST, value = "editConfig/{configid}")
  public String updateConfig(@PathVariable("configid") Long configid,
      @RequestParam("gouvernorat") Gouvernorat ville,
      @RequestParam("eventAction") String eventAction,
      @RequestParam("listeEmail") String listeEmail,
      @RequestParam("Listetélephone") String Listetélephone, Model model,
      RedirectAttributes redirectAttrs) {
    return NotificationConfigService.Updatedemandeabonnement(configid, eventAction, ville,
        listeEmail, Listetélephone, model, redirectAttrs);
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.DELETE, value = "removePhoneNumber/{configid}")
  public String removePhoneNumber(@PathVariable("configid") Long configid,
      @RequestParam("phone") String phone) {
    return NotificationConfigService.removePhoneNumber(configid, phone);
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.DELETE, value = "removeEmailAdress/{configid}")
  public String removeEmailAdress(@PathVariable("configid") Long configid,
      @RequestParam("Email") String Email) {
    return NotificationConfigService.removeEmailAdress(configid, Email);
  }
}
