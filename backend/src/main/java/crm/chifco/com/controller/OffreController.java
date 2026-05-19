package crm.chifco.com.controller;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import crm.chifco.com.model.Offre;
import crm.chifco.com.service.OffreService;
import crm.chifco.com.service.UserService;

@Controller
@RequestMapping(value = "offre/*")
public class OffreController {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  OffreService offreService;
  @Autowired
  UserService userService;

  @PreAuthorize("hasAuthority('READ_PRODUCT')")
  @RequestMapping(method = RequestMethod.GET, value = "allOffres")
  public String allOffres(Model model) {
    userService.returnInfoUserConnected(model);
    return "offre/listeOffre";
  }

  @PreAuthorize("hasAuthority('ADD_OFFRES')")
  @RequestMapping(method = RequestMethod.GET, value = "createOffres")
  public String createOffres(Model model) {
    userService.returnInfoUserConnected(model);
    return "offre/ajouterOffre";
  }

  @RequestMapping(method = RequestMethod.POST, value = "add-offre")
  public String addOffres(@RequestParam("nom") String nom, @RequestParam("type") String type,
      @RequestParam(value = "isPromo", required = false) String isPromo,
      @RequestParam(value = "isActive", required = false) String isActive,
      @RequestParam(value = "dateDebutPromo", required = false) String dateDebutPromo,
      @RequestParam(value = "dateFinPromo", required = false) String dateFinPromo,
      @RequestParam(value = "periodeValidPromo", required = false) Long periodeValidPromo,
      @RequestParam(value = "idOffre", required = false) Long idOffre,
      @RequestParam(value = "IsPrivate", required = false) Boolean IsPrivate,
      @RequestParam(value = "isRevSelected", required = false) Boolean isRevSelected,


      Model model) {

    offreService.createNewOffre(nom, type, isPromo, isActive, IsPrivate, dateDebutPromo,
        dateFinPromo, periodeValidPromo, idOffre, isRevSelected);
    return "redirect:/offre/allOffres";
  }

  @PreAuthorize("hasAuthority('ADD_OFFRES')")
  @RequestMapping(method = RequestMethod.POST, value = "updateOffre/{offreId}")
  public String updateOffre(@PathVariable("offreId") Long offreId,
      @RequestParam(value = "title", required = false) String nom,
      @RequestParam(value = "isPromo", required = false) String isPromo,
      @RequestParam(value = "isActive", required = false) String isActive,
      @RequestParam(value = "dateDebutPromo", required = false) String dateDebutPromo,
      @RequestParam(value = "dateFinPromo", required = false) String dateFinPromo,
      @RequestParam(value = "periodeValidPromo", required = false) Long periodeValidPromo,
      @RequestParam(value = "idOffre", required = false) Long idOffreBase,
      @RequestParam(value = "isPrivate", required = false) Boolean isPrivate,
      @RequestParam("type") String type, Model model) {

    offreService.updateOffre(offreId, nom, isPromo, isActive, isPrivate, dateDebutPromo,
        dateFinPromo, periodeValidPromo, idOffreBase, type);
    return "redirect:/offre/allOffres";
  }

  @RequestMapping(method = RequestMethod.GET, value = "allMyOffres")
  @ResponseBody
  public HashMap<String, Object> allMyOffres(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return offreService.allMyOffres(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche);
  }

  @RequestMapping(method = RequestMethod.GET, value = "viewOffre/{offreId}")
  public String oneOffre(@PathVariable("offreId") Long offreId, Model model) {
    Offre oneOffre = offreService.getOneOffre(offreId);
    model.addAttribute("offre", oneOffre);
    return "offre/viewOffre";
  }

  @ResponseBody
  @PostMapping(value = "/oneOffre")
  public ResponseEntity<HashMap<String, Object>> JsonOffre(@RequestBody Long id,
      HttpServletRequest request) {
    Offre findMyOffre = offreService.getOneOffre(id);
    HashMap<String, Object> returnapi = new HashMap<String, Object>();
    returnapi.put("isPromo", findMyOffre.getIsPromo());
    return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "updateOffre/{offreId}")
  public String updateOffre(@PathVariable("offreId") Long offreId, Model model) {
    userService.returnInfoUserConnected(model);
    Offre oneOffre = offreService.getOneOffre(offreId);
    if (oneOffre.getIdOffreBase() != null) {
      Offre packparant = offreService.getOneOffre(oneOffre.getIdOffreBase());
      model.addAttribute("packparant", packparant);
    }

    model.addAttribute("offre", oneOffre);
    return "offre/updateOffre";
  }

  @RequestMapping(method = RequestMethod.GET, value = "offreParent")
  public ResponseEntity<HashMap<String, Object>> listeParentPack() {
    List<Offre> ListeOffre = offreService.findAllOffreByIdOffreBase(null);
    HashMap<String, Object> returnapi = new HashMap<String, Object>();
    returnapi.put("listeOffre", ListeOffre);
    return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);
  }
}
