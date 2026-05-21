package crm.chifco.com.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crm.chifco.com.ApiDTO.entryAvoirClient;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.RecuNumeroSequence;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.RecuNumeroSequenceRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AvoirService;
import crm.chifco.com.service.RecuNumeroSequenceService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.CrmUtils;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping(value = "AvoirClient/*")
public class AvoirClientController {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  private UserService userService;

  @Autowired
  AbonnementRepository abonnementRepository;

  @Autowired
  AvoirRepository avoirRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  private AvoirService avoirService;

  @Autowired
  RecuNumeroSequenceRepository recuNumeroSequenceRepository;

  @Autowired
  RecuNumeroSequenceService recuNumeroSequenceService;

  @Autowired
  FactureRepository factureRepository ;
  
  @PreAuthorize("hasAnyAuthority('READ_INVOICE_AVOIR')")
  @RequestMapping(method = RequestMethod.GET, value = "AllAvoirClient")
  public String allAvoirClient(Model model, RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    model.addAttribute("users", userRepository.findAll());
    return "avoirClient/AllAvoirClient";
  }

  @PreAuthorize("hasAnyAuthority('READ_INVOICE_AVOIR' ,'READ_BLUR_INVOICE_AVOIR')")
  @RequestMapping(method = RequestMethod.GET, value = "AllAvoirClientNotPublic")
  public String AllAvoirClientNotPublic(Model model, RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    model.addAttribute("users", userRepository.findAll());
    return "avoirClient/ListavoirNotPublish";
  }
  @PreAuthorize("hasAnyAuthority('READ_INVOICE_AVOIR' ,'READ_BLUR_INVOICE_AVOIR')")
  @RequestMapping(method = RequestMethod.GET, value = "AllAvoirRefused")
  public String AllAvoirRefused(Model model, RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    model.addAttribute("users", userRepository.findAll());
    return "avoirClient/ListavoirReffused";
  }
  
  @RequestMapping(method = RequestMethod.GET, value = "getAllAvoirClientNotPublic")
  @ResponseBody
  public HashMap<String, Object> getAllAvoirClientNotPublic(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return avoirService.getAllAvoir(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche, false );
  }
  @RequestMapping(method = RequestMethod.GET, value = "getAllAvoirClienRefused")
  @ResponseBody
  public HashMap<String, Object> getAllAvoirClienRefused(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return avoirService.getAllAvoir(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche, null );
  }
  
  
  @PreAuthorize("hasAnyAuthority('CREATE_INVOICE_AVOIR')")
  @RequestMapping(method = RequestMethod.POST, value = "addAvoirClient")
  public String AddAvoirClient(@RequestParam("clientId") Long clientId, Model model,
      RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    model.addAttribute("clientId", clientId);
    model.addAttribute("factures", factureRepository.getFacturesByClientAndEtat_facture(clientId,false));
    model.addAttribute("revendeur", userRepository.findAll());
    return "/avoirClient/addNewAvoir";
  }

  @PreAuthorize("hasAnyAuthority('APROVE_INVOICE_AVOIR_NOT_PUBLIC')")
  @RequestMapping(method = RequestMethod.POST, value = "avoirPublish")
  public String avoirPublish(@RequestParam("avoirId") Long avoirId, Model model,
      RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    avoirService.avoirPublish(avoirId);

   
    return  "redirect:/AvoirClient/viewDetail/"+avoirId;
  }
 
  @PreAuthorize("hasAnyAuthority('APROVE_INVOICE_AVOIR_NOT_PUBLIC' )")
  @RequestMapping(method = RequestMethod.POST, value = "avoirRefused")
  public String avoirRefused(@RequestParam("avoirId") Long avoirId,@RequestParam("commentaire") String commentaire,  Model model,
      RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    avoirService.avoirRefused(avoirId ,commentaire);

   
    return  "redirect:/AvoirClient/viewDetail/"+avoirId;
  }
  
  @PreAuthorize("hasAnyAuthority('CREATE_INVOICE_AVOIR')")
  @RequestMapping(method = RequestMethod.POST, value = "saveNewAvoirClient")
  @ResponseBody
  public Map<String, Object> saveNewAvoirClient(@RequestParam("clientId") Long clientId,
      @RequestParam("motifAvoir") String motifAvoir, @RequestParam("data") String entryAvoirClient,
      @RequestParam("rev") String codeUser, @RequestParam("IsClientPayed") String isClientPayed,
      @RequestParam(value = "isUsedBrd", required = false) String isUsedBrd,
      @RequestParam(value = "commAvoir", required = false) String commAvoir,
      @RequestParam(value = "hasRaccordement", required = false) String hasRaccordement,
      @RequestParam("typeAVr") Boolean typeAVr,
      @RequestParam("RefReclamation") String RefReclamation,
      @RequestParam("RefFacture")  List<String>  RefFacture,
      @RequestParam(value = "dateDebutCoupur", required = false) String dateDebutCoupur,
      @RequestParam(value = "dateFinCoupur", required = false) String dateFinCoupur,
      @RequestParam(value = "dateMs", required = false) String dateMiseService,
      @RequestParam(value = "validePar", required = false) String validePar,
      
      
      Model model, RedirectAttributes redirectAttrs)
      throws JsonMappingException, JsonProcessingException {
    Map<String, Object> response = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();
    List<entryAvoirClient> myObjects = objectMapper.readValue(entryAvoirClient,
        objectMapper.getTypeFactory().constructCollectionType(List.class, entryAvoirClient.class));
    response = avoirService.ajouterAvoir(clientId, motifAvoir, myObjects, codeUser, isClientPayed,
        isUsedBrd, hasRaccordement, commAvoir , typeAVr , RefReclamation,RefFacture , dateDebutCoupur,dateFinCoupur ,dateMiseService, validePar);


      return response;
    

  }

  RecuNumeroSequence genererCodeRecu(User user, Double montant) {
    RecuNumeroSequence recuPayementSequence = new RecuNumeroSequence();
    String codeRecu = recuNumeroSequenceService.generateCode(user);
    recuPayementSequence.setCodePayement(codeRecu);
    recuPayementSequence.setMontantTotal(montant);
    recuPayementSequence.setUser(user);
    recuNumeroSequenceRepository.save(recuPayementSequence);
    return recuPayementSequence;
  }

  @RequestMapping(method = RequestMethod.GET, value = "getAllavoirClient")
  @ResponseBody
  public HashMap<String, Object> getallAvoir(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return avoirService.getAllAvoir(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche, true);
  }

  
  @RequestMapping(method = RequestMethod.POST, value = "verifMontantAndFactureExiste")
  @ResponseBody
  public HashMap<String, Object> verifMontantAndFactureExiste(@RequestParam(value = "montant"  , required = false) String montant,
   
      @RequestParam(value = "referenceFacture", required = false) List<String> referenceFacture,
      @RequestParam(value = "referenceReclamation", required = false) String  referenceReclamation,
      @RequestParam(value = "type", required = false) Boolean typeAvr
      ) {
    return avoirService.verifMontantAndFactureExiste(montant, referenceFacture, referenceReclamation,
    		typeAvr);
  }
  
  // @PreAuthorize("hasAnyAuthority('CREATE_INVOICE_AVOIR')")
  @RequestMapping(method = RequestMethod.GET, value = "viewDetail/{id}")
  public String viewDetail(@PathVariable(value = "id") Long id, Model model,
      RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    AvoirClient client = avoirRepository.getById(id);
    model.addAttribute("client", client);

    String referenceBrd =
        client.getHas_bordereau() ? avoirRepository.getReferenceBorderauByAvoirId(id) : null;
    model.addAttribute("referenceBrd", referenceBrd);

    return "avoirClient/viewDetailAvoir";
  }

  @PreAuthorize("hasAnyAuthority('READ_INVOICE_AVOIR')")
  @GetMapping("/extractenmasse")
  public ModelAndView exportToExcel(
      @RequestParam(name = "startDate", required = false, defaultValue = "") String startDateString,
      @RequestParam(name = "endDate", required = false, defaultValue = "") String endDateString,
      @RequestParam(name = "reference", required = false, defaultValue = "") String reference,
      @RequestParam(name = "usedBy", required = false) Long usedBy,
      @RequestParam(name = "avoirStatut", required = false) Boolean avoirStatut,
      @RequestParam(name = "authorizationAdd", required = false) Boolean authorizationAdd,
      @RequestParam(name = "createdBy", required = false) Long createdBy,
      @RequestParam(name = "montantAvoir", required = false) Double montantAvoir,
      @RequestParam(name = "motifAvoir", required = false, defaultValue = "") String motifAvoir,
      @RequestParam(name = "abonnement", required = false, defaultValue = "") String abonnement,
      @RequestParam(name = "datePayementDebut", required = false,
          defaultValue = "") String datePayementDebut,
      @RequestParam(name = "datePayementFin", required = false,
          defaultValue = "") String datePayementFin,
      @RequestParam(name = "isNotPublic", required = false) String isNotPublic,
      HttpServletRequest request, HttpServletResponse response) {

    Date startDate = null;
    Date endDate = null;

    Date datePayementDebutAvoir = null;
    Date datePayementFinAvoir = null;
    if (!"".equals(startDateString)) {
      startDate = CrmUtils.convertStringToDate(startDateString);
    } else {
      startDate = null;
    }
    if (!"".equals(datePayementDebut)) {
      datePayementDebutAvoir = CrmUtils.convertStringToDate(datePayementDebut);
    }
    if (!"".equals(datePayementFin)) {
      datePayementFinAvoir = CrmUtils.convertStringToDate(datePayementFin);
    }
    if (!"".equals(endDateString)) {
      endDate = CrmUtils.convertStringToLocalDateTime(endDateString);
    } else {
      endDate = null;
    }

    if ("".equals(reference)) {
      reference = null;
    }

    if ("".equals(motifAvoir)) {
      motifAvoir = null;
    }

    if ("".equals(abonnement)) {
      abonnement = null;
    }

    return avoirService.exportListAvoirToExcel(startDate, endDate, reference, usedBy, avoirStatut,
        authorizationAdd, createdBy, montantAvoir, motifAvoir, abonnement, datePayementDebutAvoir,
        datePayementFinAvoir,isNotPublic, request, response);
  }

  @PostMapping("/downloadAvoirClient")
  public void downloadcontrat(Long avoirId, HttpServletResponse response)
      throws JRException, IOException {
    avoirService.downloadAvoir(avoirId, response);
  }

  @PreAuthorize("hasAnyAuthority('CREATE_INVOICE_AVOIR')")
  @RequestMapping(method = RequestMethod.GET, value = "editAvoir/{avoirId}")
  public String editAvoir(@PathVariable("avoirId") Long avoirId, Model model,
      RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    AvoirClient avoir = avoirRepository.getById(avoirId);
    model.addAttribute("avoir", avoir);

    model.addAttribute("revendeur", userRepository.findAll());
    return "/avoirClient/editAvoir";
  }
  @PreAuthorize("hasAnyAuthority('CREATE_INVOICE_AVOIR')")
  @GetMapping("editAvoirNotPublich/{id}")
  public String editAvoirNotPublich(@PathVariable Long id, Model model) {

	  AvoirClient avoir = avoirRepository.getById(id);

      model.addAttribute("avoir", avoir);
      model.addAttribute("factures", factureRepository.getFacturesByClientAndEtat_facture(avoir.getAbonnement().getClientid(),false));


      return "/avoirClient/editAvoirNotPublich";
  }
      
  @PreAuthorize("hasAnyAuthority('CREATE_INVOICE_AVOIR')")
  @RequestMapping(method = RequestMethod.GET, value = "editAvoirToValidate/{avoirId}")
  public String editAvoirToValidate(@PathVariable("avoirId") Long avoirId, Model model,
      RedirectAttributes redirectAttrs) {
    userService.returnInfoUserConnected(model);
    AvoirClient avoir = avoirRepository.getById(avoirId);
    if(avoir.getFacture() != null) {
    	  model.addAttribute("facture", avoir.getFacture());
    }
    model.addAttribute("avoir", avoir);

    model.addAttribute("revendeur", userRepository.findAll());
    return "/avoirClient/editAvoirToValidate";
  }

  @PreAuthorize("hasAnyAuthority('READ_BLUR_INVOICE_AVOIR')")
  @RequestMapping(method = RequestMethod.POST, value = "saveEditAvoirClient")
  public String saveEditAvoirClient(@RequestParam("avoirId") Long avoirId,
      @RequestParam("rev") String codeUser, @RequestParam("IsClientPayed") String isClientPayed,
      @RequestParam(value = "isUsedBrd", required = false) String isUsedBrd,
      RedirectAttributes redirectAttrs) {

    Map<String, Object> response =
        avoirService.EditAvoirToPayed(avoirId, codeUser, isClientPayed, isUsedBrd);
    if (response.get("statut").equals("true")) {

      return "redirect:/AvoirClient/AllAvoirClient";
    } else {
      Object erreurMapObj = response.get("erreur");
      if (erreurMapObj instanceof Object) {
        // Cast to HashMap
        HashMap<String, Object> erreurMap = (HashMap<String, Object>) erreurMapObj;
        redirectAttrs.addFlashAttribute("message", erreurMap.get("erreur"));
        return "redirect:/AvoirClient/editAvoir/" + avoirId;
        // Now you can use erreurMap as a HashMap

      } else
        return "redirect:/AvoirClient/AllAvoirClient";
    }
  }

  @PreAuthorize("hasAnyAuthority('READ_BLUR_INVOICE_AVOIR')")

  @RequestMapping(method = RequestMethod.POST, value ="updateAvoirNotPublic")
  public String updateAvoirNotPublic( AvoirClient dto ,     @RequestParam("RefFacture")  List<String>  RefFacture ,@RequestParam("dateMiseServices") String dateMiseService ,
	      @RequestParam(name = "dateDebutCoupur", required = false,
          defaultValue = "") String dateDebutCoupur,
      @RequestParam(name = "dateFinCoupur", required = false,
          defaultValue = "") String dateFinCoupur) {

      avoirService.updateAvoirClientNotPublic(dto, RefFacture , dateMiseService , dateDebutCoupur ,dateFinCoupur );

      return "redirect:/AvoirClient/AllAvoirClientNotPublic";
  }
}
