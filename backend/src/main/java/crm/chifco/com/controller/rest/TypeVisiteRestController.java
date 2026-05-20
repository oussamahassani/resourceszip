package crm.chifco.com.controller.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.TypeVisite;
import crm.chifco.com.service.TypeVisiteService;

@RestController
@RequestMapping("typevisite")
public class TypeVisiteRestController {

  @Autowired
  TypeVisiteService typeVisiteService;

  @GetMapping("/alltypevisites")
  public ResponseEntity<List<TypeVisite>> getAllTypeVisites() {
    return ResponseEntity.ok(typeVisiteService.getAllStatusrec());
  }
}
