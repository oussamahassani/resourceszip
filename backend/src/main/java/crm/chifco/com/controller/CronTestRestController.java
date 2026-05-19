package crm.chifco.com.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.service.CronService;

@RestController
@RequestMapping("/api/cron-test")
public class CronTestRestController {

  @Autowired
  private CronService distributionService;



  @GetMapping("/run-distribution")
  public ResponseEntity<Map<String, Object>> runDistribution() {
    Map<String, Object> response = new HashMap<>();

    try {
      distributionService.distributeReclamationsToTechnicians();
      response.put("success", true);
      response.put("message", "Distribution cron executed successfully");
      response.put("timestamp", new Date());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error: " + e.getMessage());
      return ResponseEntity.internalServerError().body(response);
    }
  }



}
