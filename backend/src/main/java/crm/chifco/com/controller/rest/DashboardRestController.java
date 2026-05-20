package crm.chifco.com.controller.rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.BordereaurRepository;
import crm.chifco.com.repository.CommissionRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.UserRepository;

@RestController
@RequestMapping("dashboard")
public class DashboardRestController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  AbonnementRepository abonnementRepository;
  @Autowired
  DemandeAbonnementRepository demandeAbonnementRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  ModemRepository modemRepository;
  @Autowired
  CommissionRepository commissionRepository;
  @Autowired
  BordereaurRepository bordereaurRepository;
  @Autowired
  GouvernoratRepository gouvernoratRepository;

  private User getConnectedUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth instanceof AnonymousAuthenticationToken)) {
      return userRepository.findUsersByEmail(auth.getName());
    }
    return null;
  }

  @GetMapping("/admin/stats")
  public ResponseEntity<Map<String, Object>> getAdminStats() {
    Map<String, Object> stats = new HashMap<>();
    try {
      stats.put("totalAbonnements", abonnementRepository.count());
      stats.put("totalDemandes", demandeAbonnementRepository.count());
      stats.put("totalUsers", userRepository.count());
      stats.put("totalModems", modemRepository.count());
      stats.put("totalBordereaux", bordereaurRepository.count());

      String today = new java.text.SimpleDateFormat("yyyy-MM-dd")
          .format(Calendar.getInstance().getTime());
      List<Map<String, Object>> demandesStats =
          demandeAbonnementRepository.countDemandeAbonnementTodayAndYesterday(today);
      stats.put("demandesStats", demandesStats != null ? demandesStats : new ArrayList<>());
    } catch (Exception e) {
      logger.error("Error fetching admin stats", e);
    }
    return ResponseEntity.ok(stats);
  }

  @GetMapping("/other/stats")
  public ResponseEntity<Map<String, Object>> getOtherStats() {
    Map<String, Object> stats = new HashMap<>();
    try {
      User user = getConnectedUser();
      if (user != null) {
        stats.put("userId", user.getUserid());
        stats.put("userRole", user.getRole() != null ? user.getRole().getRoleName() : null);
      }
      stats.put("totalDemandes", demandeAbonnementRepository.count());
      stats.put("totalAbonnements", abonnementRepository.count());
    } catch (Exception e) {
      logger.error("Error fetching other stats", e);
    }
    return ResponseEntity.ok(stats);
  }

  @GetMapping("/chiffre-affaire")
  public ResponseEntity<Map<String, Object>> getChiffreAffaire(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month) {
    Map<String, Object> result = new HashMap<>();
    if (year == null) year = Calendar.getInstance().get(Calendar.YEAR);
    result.put("year", year);
    result.put("month", month);
    result.put("data", new ArrayList<>());
    result.put("total", 0);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/encaissement")
  public ResponseEntity<Map<String, Object>> getEncaissement(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month) {
    Map<String, Object> result = new HashMap<>();
    if (year == null) year = Calendar.getInstance().get(Calendar.YEAR);
    result.put("year", year);
    result.put("month", month);
    result.put("data", new ArrayList<>());
    result.put("total", 0);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/techno-repartition")
  public ResponseEntity<List<Map<String, Object>>> getTechnoRepartition() {
    List<Map<String, Object>> result = new ArrayList<>();
    Map<String, Object> ftth = new HashMap<>();
    ftth.put("type", "FTTH");
    ftth.put("count", 0);
    result.add(ftth);
    Map<String, Object> adsl = new HashMap<>();
    adsl.put("type", "ADSL");
    adsl.put("count", 0);
    result.add(adsl);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/bordereau-status")
  public ResponseEntity<Map<String, Object>> getBordereauStatus() {
    Map<String, Object> result = new HashMap<>();
    try {
      result.put("total", bordereaurRepository.count());
    } catch (Exception e) {
      result.put("total", 0);
    }
    result.put("data", new ArrayList<>());
    return ResponseEntity.ok(result);
  }

  @GetMapping("/abonnements-by-gouvernorat")
  public ResponseEntity<List<Map<String, Object>>> getAbonnementsByGouvernorat(
      @RequestParam(required = false) String type) {
    List<Map<String, Object>> result = new ArrayList<>();
    try {
      List<Gouvernorat> gouvernorats = gouvernoratRepository.findAll();
      for (Gouvernorat gov : gouvernorats) {
        Map<String, Object> item = new HashMap<>();
        item.put("gouvernorat", gov.getGouvernoratName());
        item.put("gouvernoratId", gov.getGouvernoratId());
        item.put("count", 0);
        result.add(item);
      }
    } catch (Exception e) {
      logger.error("Error fetching abonnements by gouvernorat", e);
    }
    return ResponseEntity.ok(result);
  }

  @GetMapping("/top-revendeurs")
  public ResponseEntity<List<Map<String, Object>>> getTopRevendeurs(
      @RequestParam(defaultValue = "all") String filter) {
    List<Map<String, Object>> result = new ArrayList<>();
    try {
      List<Commission> topRevendeurs;
      int currentYear = Calendar.getInstance().get(Calendar.YEAR);
      Calendar cal = Calendar.getInstance();

      if ("year".equals(filter)) {
        topRevendeurs = commissionRepository.findTop5PaidRevendeursCurrentYear(currentYear);
      } else if ("month".equals(filter)) {
        int lastMonth = cal.get(Calendar.MONTH);
        int lastYear = lastMonth == 0 ? currentYear - 1 : currentYear;
        if (lastMonth == 0) lastMonth = 12;
        topRevendeurs = commissionRepository.findTop5PaidRevendeursLastMonth(lastYear, lastMonth);
      } else {
        topRevendeurs = commissionRepository.findTop5PaidRevendeurs();
      }

      for (Commission commission : topRevendeurs) {
        Map<String, Object> item = new HashMap<>();
        if (commission.getRevendeur() != null) {
          User rev = commission.getRevendeur();
          item.put("revendeurId", rev.getUserid());
          item.put("nom", rev.getLastName());
          item.put("prenom", rev.getFirstName());
          item.put("codeUser", rev.getCodeUser());
        }
        item.put("annee", commission.getAnnee());
        try {
          item.put("montant", commission.getMontantCommissionPaiements());
        } catch (Exception e2) {
          item.put("montant", 0);
        }
        result.add(item);
      }
    } catch (Exception e) {
      logger.error("Error fetching top revendeurs", e);
    }
    return ResponseEntity.ok(result);
  }
}
