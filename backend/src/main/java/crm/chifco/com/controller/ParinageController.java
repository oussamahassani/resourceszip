package crm.chifco.com.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.Parinage;
import crm.chifco.com.service.ParinageService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.StatusParinage;

@Controller
@RequestMapping(value = "parinage/*")
public class ParinageController {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  private UserService userService;

  @Autowired
  ParinageService parinageService;

  @GetMapping(value = "dataparinage")
  @ResponseBody
  public HashMap<String, Object> listeParinages(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {

    HashMap<String, Object> myGreetings = new HashMap<>();
    Page<Parinage> responseData = parinageService.findPaginatedwithfilter(start, length,
        ordercolumnaram, orderdir, filterrecherche);

    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;



  }


  @RequestMapping(method = RequestMethod.GET, value = "allparinage")
  public String viewlisteallparinage(Model model) {

    userService.returnInfoUserConnected(model);


    return "parinage/allParinage";
  }


  @RequestMapping(method = RequestMethod.GET, value = "updateStatusParinage/{id}")
  public String updateStatusParinage(Model model, @PathVariable("id") Long id) {

    userService.returnInfoUserConnected(model);
    Parinage findParinage = parinageService.findParinageById(id);
    Map<String, String> items = new HashMap<>();
    items.put(StatusParinage.ATTENTE, "En attente");
    items.put(StatusParinage.CANCAL, "Annulée");
    items.put(StatusParinage.VALIDATE, "Validée");


    model.addAttribute("items", items);

    model.addAttribute("findParinage", findParinage);

    return "parinage/editstatutParinage";
  }

  @RequestMapping(method = RequestMethod.POST, value = "updateStatus/{id}")
  public String updateClient(@PathVariable("id") Long id, @RequestParam("status") String status,
      @RequestParam(value = "commentaire", required = false) String commentaire,
      RedirectAttributes redirectAttributes) {

    boolean success = parinageService.findParinageByIdAndUpdtaeStatus(id, status, commentaire);

    if (!success) {

      redirectAttributes.addFlashAttribute("message", "comment is required");
      return "redirect:/parinage/updateStatusParinage/" + id;
    } else {
      redirectAttributes.addFlashAttribute("message", "parrainage modified");
      return "redirect:/parinage/viewparinage/" + id;
    }
  }


  @RequestMapping(method = RequestMethod.GET, value = "viewparinage/{id}")
  public String viewParinage(@PathVariable("id") Long id, Model model) {
    Parinage findParinage = parinageService.findParinageById(id);
    model.addAttribute("findParinage", findParinage);
    return "parinage/detailParinage";
  }

  @GetMapping("/extractenmasse")
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(value = "datedebut", required = false) String datedebut,
      @RequestParam(value = "datefin", required = false) String datefin,
      @RequestParam(value = "statutfiltre", required = false) String statutfiltre) {
    return parinageService.extractEnMasse(request, response, datedebut, datefin, statutfiltre);
  }

}
