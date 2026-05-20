package crm.chifco.com.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "alluser")
public class DashbordController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @GetMapping("/dashboard")
  public ResponseEntity<String> otherDashboard() {
    return ResponseEntity.ok("ok");
  }
}
