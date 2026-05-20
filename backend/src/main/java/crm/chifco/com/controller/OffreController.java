package crm.chifco.com.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.Offre;
import crm.chifco.com.service.OffreService;

@RestController
@RequestMapping(value = "offre")
public class OffreController {

  private final Logger logger = LogManager.getLogger(this.getClass());
  private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  @Autowired
  OffreService offreService;

  @GetMapping("/allOffres")
  public ResponseEntity<List<Offre>> getAllOffres() {
    return ResponseEntity.ok(offreService.findAllOffre());
  }

  @GetMapping("/activeOffres")
  public ResponseEntity<List<Offre>> getActiveOffres() {
    return ResponseEntity.ok(offreService.findAllOffreByIsActive(true));
  }

  @GetMapping("/offreParent")
  public ResponseEntity<HashMap<String, Object>> listeParentOffre() {
    List<Offre> listeOffre = offreService.findAllOffreByIdOffreBase(null);
    HashMap<String, Object> returnapi = new HashMap<>();
    returnapi.put("listeOffre", listeOffre);
    return new ResponseEntity<>(returnapi, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Offre> getById(@PathVariable Long id) {
    Offre offre = offreService.getOneOffre(id);
    return offre != null ? ResponseEntity.ok(offre) : ResponseEntity.notFound().build();
  }

  @RequestMapping(method = RequestMethod.GET, value = "allMyOffres")
  public HashMap<String, Object> allMyOffres(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return offreService.allMyOffres(draw, start, length, search, ordercolumnaram, orderdir,
        filterrecherche);
  }

  @PostMapping("/oneOffre")
  public ResponseEntity<HashMap<String, Object>> getOffreJson(@RequestBody Long id) {
    Offre findMyOffre = offreService.getOneOffre(id);
    HashMap<String, Object> returnapi = new HashMap<>();
    returnapi.put("isPromo", findMyOffre.getIsPromo());
    return new ResponseEntity<>(returnapi, HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADD_OFFRES')")
  @PostMapping
  public ResponseEntity<?> createOffre(@RequestBody Offre offre) {
    try {
      String dateDebut = offre.getDateDebutPromo() != null ? sdf.format(offre.getDateDebutPromo()) : null;
      String dateFin = offre.getDateFinPromo() != null ? sdf.format(offre.getDateFinPromo()) : null;
      offreService.createNewOffre(
          offre.getTitle(),
          offre.getType(),
          offre.getIsPromo() != null ? String.valueOf(offre.getIsPromo()) : "false",
          offre.getIsActive() != null ? String.valueOf(offre.getIsActive()) : "false",
          offre.getIsPrivate(),
          dateDebut,
          dateFin,
          offre.getPeriodeValidPromo(),
          offre.getIdOffreBase(),
          offre.getIsRevSelected());
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      logger.error("Error creating offre", e);
      return ResponseEntity.badRequest().body("Erreur lors de la création de l'offre");
    }
  }

  @PreAuthorize("hasAuthority('ADD_OFFRES')")
  @PutMapping("/{id}")
  public ResponseEntity<?> updateOffre(@PathVariable Long id, @RequestBody Offre offre) {
    try {
      String dateDebut = offre.getDateDebutPromo() != null ? sdf.format(offre.getDateDebutPromo()) : null;
      String dateFin = offre.getDateFinPromo() != null ? sdf.format(offre.getDateFinPromo()) : null;
      offreService.updateOffre(
          id,
          offre.getTitle(),
          offre.getIsPromo() != null ? String.valueOf(offre.getIsPromo()) : "false",
          offre.getIsActive() != null ? String.valueOf(offre.getIsActive()) : "false",
          offre.getIsPrivate(),
          dateDebut,
          dateFin,
          offre.getPeriodeValidPromo(),
          offre.getIdOffreBase(),
          offre.getType());
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      logger.error("Error updating offre", e);
      return ResponseEntity.badRequest().body("Erreur lors de la mise à jour de l'offre");
    }
  }

  @PreAuthorize("hasAuthority('ADD_OFFRES')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOffre(@PathVariable Long id) {
    return ResponseEntity.ok().build();
  }
}
