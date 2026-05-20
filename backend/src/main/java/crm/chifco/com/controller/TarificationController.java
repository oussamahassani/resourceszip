package crm.chifco.com.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import crm.chifco.com.model.Tarification;
import crm.chifco.com.service.TarificationServices;

@Controller
@RequestMapping(value = "tarification/*")
public class TarificationController {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  TarificationServices tarificationServices;

  @RequestMapping(method = RequestMethod.GET, value = "gettarification/{packId}")
  @ResponseBody
  public Tarification getTarificationBypackId(@PathVariable("packId") Long packId) {
    return tarificationServices.getTarificationBypackId(packId);
  }
}
