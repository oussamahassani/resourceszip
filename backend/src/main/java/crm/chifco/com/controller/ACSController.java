package crm.chifco.com.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.ApiDTO.getLoginAndPassswordModem;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.service.AbonnementService;
import crm.chifco.com.service.CronService;
import crm.chifco.com.service.ModemService;
import crm.chifco.com.service.Utilsjwt;
import crm.chifco.com.templateclasse.AcsInfo;
import crm.chifco.com.templateclasse.LoginDtoAcs;

@RestController
@RequestMapping(value = "acs/*")

public class ACSController {

  private static final Logger logger = LogManager.getLogger(ACSController.class);

  @Autowired
  AbonnementService abonnementService;

  @Autowired
  ModemService modemService;

  @Autowired
  ModemRepository modemRepository;
  
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  Utilsjwt utilsJwt;

  @Autowired
  CronService cronService;


  @RequestMapping(method = RequestMethod.POST, value = "signin")
  public ResponseEntity<HashMap<String, Object>> authenticateUser(
      @RequestBody LoginDtoAcs loginDto) {
    HashMap<String, Object> returnapi = new HashMap<String, Object>();
    try {

      Authentication authentication =
          authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
              loginDto.getUsernameOrEmail(), loginDto.getPassword()));

      String jwt = utilsJwt.generateJwtToken(authentication.getName());

      SecurityContextHolder.getContext().setAuthentication(authentication);
      returnapi.put("msg", "Connexion réussie de l’utilisateur!.");
      returnapi.put("jwt", jwt);
      return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);
    } catch (BadCredentialsException e) {
      logger.error("signin Acs Api : " + e);
      returnapi.put("msg", "Échec de la connexion utilisateur !!.");
      return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);

    }

  }

  @RequestMapping(method = RequestMethod.GET, value = "getlisteclient")
  public ResponseEntity<HashMap<String, Object>> getListeClient(HttpServletRequest request,
      @RequestParam(value = "dateCreation", required = false) String dateCreation) {

    HashMap<String, Object> returnapi = new HashMap<String, Object>();
    try {
      String jwt = utilsJwt.parseJwt(request);
      if (jwt != null && utilsJwt.validateJwtToken(jwt)) {
        if (dateCreation == null) {
          returnapi.put("mesage", "date creation obligatoire");
          return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);
        } else {
          LocalDate Localdatenow = LocalDate.parse(dateCreation);
          Instant instancedatenowfacture =
              Localdatenow.atStartOfDay(ZoneId.systemDefault()).toInstant();
          List<AcsInfo> ListClient =
              abonnementService.findListClientToAcs(instancedatenowfacture, "ACTIVE");
          returnapi.put("data", ListClient);
          return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);
        }
      } else {
        String jwtresult = "token invalide ou inexistante ";
        returnapi.put("mesage", jwtresult);
        return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.BAD_REQUEST);
      }

    } catch (Exception e) {
      logger.error("Acs api getlisteclient: " + e);
      returnapi.put("mesage", e.getMessage());
      return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.FORBIDDEN);
    }

  }

  @RequestMapping(method = RequestMethod.GET, value = "getLoginModem")
  public ResponseEntity<HashMap<String, Object>> getLoginModem(HttpServletRequest request,
      @RequestParam(value = "numSerie", required = false) String numSerie) {
    HashMap<String, Object> returnapi = new HashMap<String, Object>();
    try {
    
        getLoginAndPassswordModem loginModem = modemService.getLoginAndPassswordModem(numSerie);

        Map<String, Object> data = new HashMap<>();

        data.put("password", loginModem.getPassword());
        data.put("type", loginModem.getType());

        if (loginModem.getControleParentaleActiver()) {
            data.put("login", loginModem.getLoginControleParental());

        
        }
                else {
                    data.put("login", loginModem.getLoginModem());

                }
        
      returnapi.put("data", data);

    } catch (Exception e) {
      returnapi.put("mesage", e.getMessage());
      returnapi.put("data", null);
    }
    return new ResponseEntity<HashMap<String, Object>>(returnapi, HttpStatus.OK);
  }

  @GetMapping("calculeTTservice/{day}/{yearMonth}")
  public String CalculeTTservice(@PathVariable(value = "day") String day,
      @PathVariable(value = "yearMonth") String yearMonth) {

    cronService.serviceTTCalcule(day, yearMonth);
    return "facturation service pour la date " + yearMonth + " est calculer";
  }

  @GetMapping("insertalltoradus")
  public String insretModemToRadus() {

    String result = abonnementService.insertRadusIfNotExiste();
    return result;
  }
}
