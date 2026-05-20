package crm.chifco.com.controller.rest;

import java.util.Collections;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("servicetype")
public class ServiceTypeRestController {

  @GetMapping("/allservicetypes")
  public ResponseEntity<List<Object>> getAllServiceTypes() {
    return ResponseEntity.ok(Collections.emptyList());
  }
}
