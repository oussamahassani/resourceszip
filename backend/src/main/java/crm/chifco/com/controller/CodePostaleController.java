package crm.chifco.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import crm.chifco.com.model.PostalCode;
import crm.chifco.com.repository.CodePostaleRepository;

@Controller
@RequestMapping(value = "codepostale/*")
public class CodePostaleController {

  @Autowired
  CodePostaleRepository CodePostaleRepository;

  @RequestMapping(method = RequestMethod.GET, value = "codepostale/{villeid}")
  @ResponseBody
  public List<PostalCode> getcodepostale(@PathVariable("villeid") Long villeid) {

    List<PostalCode> PostalCode =
        CodePostaleRepository.findPostalCodeByVille_VilleId(villeid);
    return PostalCode;
  }

}
