package crm.chifco.com.controller;

import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.EntryPack;
import crm.chifco.com.model.Offre;
import crm.chifco.com.model.Pack;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.EntryPackRepository;
import crm.chifco.com.service.CategorieProduitInternetService;
import crm.chifco.com.service.EngagementService;
import crm.chifco.com.service.OffreService;
import crm.chifco.com.service.PackService;
import crm.chifco.com.service.ProduitService;
import crm.chifco.com.service.TarificationServices;

@RestController
@RequestMapping(value = "pack")
public class PackController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  PackService packService;

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
  CategorieProduitInternetRepository categorieProduitInternetRepository;

  @Autowired
  EngagementService engagementService;

  @GetMapping("/allPack")
  public ResponseEntity<List<Pack>> getAllPacks() {
    try {
      return ResponseEntity.ok(packService.findPaginatedwithfilter(1, 10000, "packId", "asc").getContent());
    } catch (Exception e) {
      return ResponseEntity.ok(packService.findPackByIdPackBase(null));
    }
  }

  @GetMapping("/allPacksFlat")
  public ResponseEntity<HashMap<String, Object>> allPacksDataTable(
      @RequestParam("draw") int draw, @RequestParam("start") int start,
      @RequestParam("length") int length, @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return ResponseEntity
        .ok(packService.allMyPack(draw, start, length, search, ordercolumnaram, orderdir,
            filterrecherche));
  }

  @RequestMapping(method = RequestMethod.GET, value = "allMyPack")
  public HashMap<String, Object> allMyPacks(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return packService.allMyPack(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche);
  }

  @GetMapping("/getpack/{offreid}")
  public List<Pack> getPacksByOffre(@PathVariable("offreid") Long offreid) {
    return packService.getPackSByOffre_offreId(offreid);
  }

  @GetMapping("/packByCategory/{category}")
  public List<Pack> getPacksByCategory(@PathVariable("category") Long category) {
    return packService.getPackSByCategoriePack_categorieProduitInternetId(category);
  }

  @GetMapping("/packparent")
  public ResponseEntity<HashMap<String, Object>> listeParentPack() {
    List<Pack> listePack = packService.findPackByIdPackBase(null);
    HashMap<String, Object> returnapi = new HashMap<>();
    returnapi.put("pack", listePack);
    return new ResponseEntity<>(returnapi, HttpStatus.OK);
  }

  @GetMapping("/findEntryByIdPack/{packId}")
  public List<EntryPack> entryByIdPack(@PathVariable("packId") Long packId) {
    return entryPackRepository.getEntryPackByPackId(packId);
  }

  @GetMapping("/{packId}")
  public ResponseEntity<Pack> getById(@PathVariable Long packId) {
    Pack pack = packService.findPackBypackId(packId);
    return pack != null ? ResponseEntity.ok(pack) : ResponseEntity.notFound().build();
  }

  @PreAuthorize("hasAuthority('ADD_OFFRES')")
  @PostMapping
  public ResponseEntity<?> createPack(@RequestBody Pack pack) {
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("hasAuthority('ADD_OFFRES')")
  @PutMapping("/{packId}")
  public ResponseEntity<?> updatePack(@PathVariable Long packId, @RequestBody Pack pack) {
    return ResponseEntity.ok().build();
  }

  @GetMapping("/offres")
  public ResponseEntity<List<Offre>> getActiveOffres() {
    return ResponseEntity.ok(offreService.findAllOffreByIsActive(true));
  }
}
