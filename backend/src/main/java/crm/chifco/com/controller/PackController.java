package crm.chifco.com.controller;

import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import crm.chifco.com.model.CategorieProduitInternet;
import crm.chifco.com.model.Engagement;
import crm.chifco.com.model.EntryPack;
import crm.chifco.com.model.Offre;
import crm.chifco.com.model.Pack;
import crm.chifco.com.model.Produit;
import crm.chifco.com.model.Tarification;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.EntryPackRepository;
import crm.chifco.com.service.CategorieProduitInternetService;
import crm.chifco.com.service.EngagementService;
import crm.chifco.com.service.OffreService;
import crm.chifco.com.service.PackService;
import crm.chifco.com.service.ProduitService;
import crm.chifco.com.service.TarificationServices;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.CrmUtils;

@Controller
@RequestMapping(value = "pack/*")
public class PackController {
  private final Logger logger = LogManager.getLogger(this.getClass());
  @Autowired
  PackService packService;

  @Autowired
  UserService userService;

  @Autowired
  CategorieProduitInternetService categorieProduitInternetService;

  @Autowired
  OffreService offreService;

  @Autowired
  ProduitService produitService;

  @Autowired
  TarificationServices tarificationServices;

  @Autowired
  EntryPackRepository entryPackRepository;
  @Autowired
  private CategorieProduitInternetRepository categorieProduitInternetRepository;

  @Autowired
  EngagementService engagementService;

  @RequestMapping(method = RequestMethod.GET, value = "getpack/{offreid}")
  @ResponseBody
  public List<Pack> getPackSByOffreByIdOffre(@PathVariable("offreid") Long offreid) {
    List<Pack> packs = packService.getPackSByOffre_offreId(offreid);
    return packs;
  }

  @RequestMapping(method = RequestMethod.GET, value = "packByCategory/{category}")
  @ResponseBody
  public List<Pack> getPackSByOffreByCategoryId(@PathVariable("category") Long category) {
    return packService.getPackSByCategoriePack_categorieProduitInternetId(category);
  }


  @PreAuthorize("hasAuthority('READ_PRODUCT')")
  @RequestMapping(method = RequestMethod.GET, value = "allPack")
  public String allPack(Model model) {
    userService.returnInfoUserConnected(model);
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    model.addAttribute("categories", categorieProduitInternets);
    List<Offre> offres = offreService.findAllOffre();
    model.addAttribute("offres", offres);

    return "pack/listePack";
  }

  @RequestMapping(method = RequestMethod.GET, value = "allMyPack")
  @ResponseBody
  public HashMap<String, Object> allMyPacks(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return packService.allMyPack(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche);

  }


  @RequestMapping(method = RequestMethod.GET, value = "createPack")
  public String createPack(Model model) {
    userService.returnInfoUserConnected(model);
    List<Offre> listeOffre = offreService.findAllOffreByIsActive(true);
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetService.findAllCategorie();
    List<Produit> listeProduit = produitService.findAllActiveProduit();
    List<Engagement> listeEngagement = engagementService.getAllEngagements();

    model.addAttribute("offres", listeOffre);
    model.addAttribute("engagements", listeEngagement);

    model.addAttribute("categories", categorieProduitInternets);
    model.addAttribute("produits", listeProduit);
    return "pack/ajouterPack";
  }

  @RequestMapping(method = RequestMethod.POST, value = "addPack")
  public String addPack(@RequestParam("nom") String nom,
      @RequestParam("description") String description, @RequestParam("offre") Long offre,
      @RequestParam("categorie") Long categorie, @RequestParam("produits") Long[] produits,
      @RequestParam("isShowProduits") String[] isShow, @RequestParam("Prix") Double Prix,
      @RequestParam("remise") Double remise, @RequestParam("pourcentTva") Long pourcentTva,
      @RequestParam(value = "idEngagement", required = true) Long idEngagement,
      @RequestParam(value = "idPackBase", required = false) Long idPackBase,
      @RequestParam(value = "debitPack", required = false) String debitPack,
      @RequestParam(value = "payLater", required = false) Boolean payLater,

      Model model) {
    packService.addNewPack(nom, description, offre, categorie, produits, isShow, Prix, remise,
        pourcentTva, idPackBase, debitPack, payLater, idEngagement);
    return "redirect:/pack/allPack";
  }

  @PreAuthorize("hasAuthority('READ_PRODUCT')")
  @RequestMapping(method = RequestMethod.GET, value = "viewPack/{packId}")
  public String onePack(@PathVariable("packId") Long packId, Model model) {
    Pack onePack = packService.findPackBypackId(packId);
    if (onePack.getIdPackBase() != null) {

      Pack packParent = packService.findPackBypackId(onePack.getIdPackBase());
      model.addAttribute("packParent", packParent);
    }
    List<EntryPack> entryPack = packService.findEntryPackBypack(onePack);
    Tarification tarification = tarificationServices.getTarificationBypackId(onePack.getPackId());
    model.addAttribute("pack", onePack);
    model.addAttribute("tarification", tarification);
    model.addAttribute("entryPacks", entryPack);
    return "pack/viewPack";
  }

  @PreAuthorize("hasAuthority('ADD_OFFRES')")
  @RequestMapping(method = RequestMethod.GET, value = "updatePack/{packId}")
  public String updatePack(@PathVariable("packId") Long packId, Model model) {
    Pack onePack = packService.findPackBypackId(packId);
    List<EntryPack> entryPack = packService.findEntryPackBypack(onePack);
    Tarification tarification = tarificationServices.getTarificationBypackId(onePack.getPackId());
    List<Offre> listeOffre = offreService.findAllOffreByIsActive(true);
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetService.findAllCategorie();
    List<Produit> listeProduit = produitService.findAllActiveProduit();
    model.addAttribute("offres", listeOffre);
    model.addAttribute("categories", categorieProduitInternets);
    model.addAttribute("tarification", tarification);
    model.addAttribute("pack", onePack);
    model.addAttribute("produits", listeProduit);
    model.addAttribute("entryPacks", entryPack);
    model.addAttribute("packId", packId);

    return "pack/updatePack";
  }

  @RequestMapping(method = RequestMethod.POST, value = "updatePack/{packId}")
  public String updateMyPack(@PathVariable("packId") Long packId, Pack pack,
      @RequestParam("produits") Long[] produits, @RequestParam("isShowProduits") String[] isShow,
      @RequestParam("categorie") Long categorie, @RequestParam("offre") Long offreId,
      Tarification tarification) {
    Pack onePack = packService.findPackBypackId(packId);

    packService.updatePack(onePack, pack.getTitle(), pack.getDescription(), offreId, categorie,
        produits, isShow, pack.getIdPackBase(), pack.getDebitPack());
    Tarification myTarification = tarificationServices.getTarificationBypackId(onePack.getPackId());
    Double produitPrixTTC = ((tarification.getPrixUnitaire() * (tarification.getTaxe() * 0.01))
        + tarification.getPrixUnitaire());
    produitPrixTTC = CrmUtils.formatDoubleInput(produitPrixTTC);

    tarificationServices.updateTarification(myTarification.getTarificationId(),
        tarification.getPrixUnitaire(), tarification.getTaxe(), tarification.getTypeRemise(),
        tarification.getRemise(), produitPrixTTC, tarification.getCategoryClient());
    return "redirect:/pack/allPack";
  }

  @RequestMapping(method = RequestMethod.GET, value = "packparent")
  public ResponseEntity<HashMap<String, Object>> listeParentPack() {
    List<Pack> ListePack = packService.findPackByIdPackBase(null);
    HashMap<String, Object> returnapi = new HashMap<String, Object>();
    returnapi.put("pack", ListePack);
    return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "findEntryByIdPack/{packId}")
  @ResponseBody
  public List<EntryPack> EntryByIdPack(@PathVariable("packId") Long packId) {
    // Pack pack = packService.findPackBypackId(packId);
    List<EntryPack> ListeEntryPack = entryPackRepository.getEntryPackByPackId(packId);
    HashMap<String, Object> returnapi = new HashMap<String, Object>();
    returnapi.put("entryPack", ListeEntryPack);
    return ListeEntryPack;
  }

}
