package crm.chifco.com.controller.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.Motifrec;
import crm.chifco.com.service.MotifrecService;

@RestController
@RequestMapping("motif")
public class MotifRestController {

  @Autowired
  MotifrecService motifrecService;

  @GetMapping("/allmotifs")
  public ResponseEntity<List<Motifrec>> getAllMotifs() {
    return ResponseEntity.ok(motifrecService.getAllMotifrec());
  }

  @GetMapping("/byServiceType")
  public ResponseEntity<List<Motifrec>> getByServiceType(
      @RequestParam Long serviceTypeId,
      @RequestParam(required = false, defaultValue = "") String category) {
    return ResponseEntity.ok(motifrecService.findMotifsByServiceType(serviceTypeId, category));
  }
}
