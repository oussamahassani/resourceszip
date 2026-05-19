package crm.chifco.com.crmMobile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.Motifrec;
import crm.chifco.com.model.Servicetype;
import crm.chifco.com.model.Statusrec;
import crm.chifco.com.model.TypeVisite;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.MotifrecRepository;
import crm.chifco.com.repository.StatusrecRepository;
import crm.chifco.com.repository.TypeVisiteRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.ServicetypeService;
import crm.chifco.com.utils.UserTypeConstant;

@RestController
@RequestMapping("/mobileapp/auth")
public class AuthController {
  @Autowired
  private JwtService jwtService;
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private OtpServiceMobileApp otpService;
  @Autowired
  private TypeVisiteRepository typeVisiteRepository;
  @Autowired
  private StatusrecRepository statusrecRepository;
  @Autowired
  private MotifrecRepository motifrecRepository;
  @Autowired
  private ServicetypeService servicetypeService;
  @Autowired
  private ModemRepository modemRepository;

  @GetMapping("/typevisites")
  public Map<String, Object> typevisites() {
    List<TypeVisite> typevisites = typeVisiteRepository.findAll();
    if (!typevisites.isEmpty()) {
      return createResponse(true, HttpStatus.OK, "liste de type visites", typevisites);
    } else {
      return createResponse(false, HttpStatus.BAD_REQUEST, "Aucune valeur", null);
    }
  }

  @GetMapping("/typemodems")
  public Map<String, Object> typemodems() {
    List<String> modelsModem = modemRepository.findDistinctModeles();
    if (!modelsModem.isEmpty()) {
      return createResponse(true, HttpStatus.OK, "liste de modèle modems", modelsModem);
    } else {
      return createResponse(false, HttpStatus.BAD_REQUEST, "Aucune valeur", null);
    }
  }

  @GetMapping("/statusreclamations")
  public Map<String, Object> statusreclamations() {
    List<Statusrec> status = statusrecRepository.findAll();
    if (!status.isEmpty()) {
      return createResponse(true, HttpStatus.OK, "liste des status de reclamation ", status);
    } else {
      return createResponse(false, HttpStatus.BAD_REQUEST, "Aucune valeur", null);
    }
  }

  @GetMapping("/motifs")
  public Map<String, Object> motifs(@RequestParam Long servicetypeId,
      @RequestParam String category) {
    List<Motifrec> motifs = motifrecRepository.findMotifsByServiceType(servicetypeId, category);
    if (!motifs.isEmpty()) {
      return createResponse(true, HttpStatus.OK, "liste des motifs de reclamation ", motifs);
    } else {
      return createResponse(false, HttpStatus.BAD_REQUEST, "Aucune valeur", null);
    }
  }

  @GetMapping("/typeCategories")
  public Map<String, Object> servicetypeList() {
    List<Servicetype> servicetypeList = servicetypeService.getAllServicetypesPublics();
    if (!servicetypeList.isEmpty()) {
      return createResponse(true, HttpStatus.OK, "liste des types de catégorie ", servicetypeList);
    } else {
      return createResponse(false, HttpStatus.BAD_REQUEST, "Aucune valeur", null);
    }
  }



  @PostMapping("/generateOtp")
  public Map<String, Object> generateOtp(@RequestBody Map<String, String> request) {
    String phoneNumber = request.get("phoneNumber");
    // String type = request.get("type");
    String type = UserTypeConstant.DISTRIBUTEUR;
    int otp = 0;
    if (phoneNumber == null || phoneNumber.isEmpty()) {
      return createResponse(false, HttpStatus.BAD_REQUEST,
          "Le numéro de téléphone est obligatoire.", null);
    }
    User user = userRepository.findTop1UsersByTelephone(phoneNumber, type);
    if (user == null) {
      return createResponse(false, HttpStatus.NOT_FOUND,
          "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
    }
    if (!"DISTRIBUTEUR".equals(user.getTypeUser())) {
      return createResponse(false, HttpStatus.BAD_REQUEST,
          "Vous n'êtes pas un chef secteur pour crée un compte", null);
    }

    /*
     * otp = otpService.generateOTP(); if (otp == 0) { return createResponse(false,
     * HttpStatus.INTERNAL_SERVER_ERROR, "Échec de la génération de l'OTP.", null); }
     */
    ResponseEntity<Map<String, Object>> smsResponse = otpService.SendSmsVerifForMobile(phoneNumber);
    Map<String, Object> responseBody = smsResponse.getBody();
    if (responseBody == null || !Boolean.TRUE.equals(responseBody.get("status"))) {
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, "Échec de l'envoi de l'OTP.",
          null);
    }
    Map<String, Object> responseData = new HashMap<>();
    responseData.put("otp", responseBody.get("data").toString());


    return createResponse(true, HttpStatus.OK, "envoyé avec succès. ", responseData);
  }

  @PostMapping("/resendOtp")
  public Map<String, Object> resendOtp(@RequestBody Map<String, String> request) {
    String phoneNumber = request.get("phoneNumber");
    // String type = request.get("type");
    String type = UserTypeConstant.DISTRIBUTEUR;
    int otp = 0;
    if (phoneNumber == null || phoneNumber.isEmpty()) {
      return createResponse(false, HttpStatus.BAD_REQUEST,
          "Le numéro de téléphone est obligatoire.", null);
    }
    User user = userRepository.findTop1UsersByTelephone(phoneNumber, type);
    if (user == null) {
      return createResponse(false, HttpStatus.NOT_FOUND,
          "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
    }
    if (!"DISTRIBUTEUR".equals(type)) {
      return createResponse(false, HttpStatus.BAD_REQUEST,
          "Vous n'êtes pas un chef secteur pour créer un compte", null);
    }

    /*
     * otp = otpService.generateOTP(); if (otp == 0) { return createResponse(false,
     * HttpStatus.INTERNAL_SERVER_ERROR, "Échec de la génération de l'OTP.", null); }
     */
    ResponseEntity<Map<String, Object>> smsResponse = otpService.SendSmsVerifForMobile(phoneNumber);
    Map<String, Object> responseBody = smsResponse.getBody();
    if (responseBody == null || !Boolean.TRUE.equals(responseBody.get("status"))) {
      return createResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, "Échec de l'envoi de l'OTP.",
          null);
    }
    Map<String, Object> responseData = new HashMap<>();
    responseData.put("otp", responseBody.get("data").toString());
    return createResponse(true, HttpStatus.OK, "OTP renvoyé avec succès. ", responseData);
  }

  @PostMapping("/verifyOtp")
  public Map<String, Object> verifyOtp(@RequestBody Map<String, String> request) {
    String phoneNumber = request.get("phoneNumber");
    // String otpString = request.get("otp");
    if (phoneNumber == null || phoneNumber.isEmpty()) {
      return createResponse(false, HttpStatus.BAD_REQUEST,
          "Le numéro de téléphone est obligatoire.", null);
    }
    User user = userRepository.findTop1UsersByTelephone(phoneNumber, "DISTRIBUTEUR");
    if (user == null) {
      return createResponse(false, HttpStatus.NOT_FOUND,
          "L'utilisateur avec ce numéro de téléphone n'existe pas.", null);
    }
    /*
     * if (otpString == null || otpString.isEmpty()) { return createResponse(false,
     * HttpStatus.BAD_REQUEST, "OTP est obligatoire.", null); }
     */
    try {
      // int otp = Integer.parseInt(otpString);
      // otpService.verifyOtp(phoneNumber, otp)
      if (true) {
        UserDtoApp userDto = UserDtoApp.fromEntity(user);
        String token = jwtService.generateJwtToken(user.getUserid().toString());
        String refreshToken = jwtService.generateRefreshToken(user.getUserid().toString());
        Date expiredDateToken = jwtService.getExpirationDateFromToken(token);
        Date expiredDateRefreshToken = jwtService.getExpirationDateFromRefreshToken(refreshToken);
        JwtResponse jwtResponse = new JwtResponse(token, refreshToken, phoneNumber,
            expiredDateToken, expiredDateRefreshToken, userDto);
        return createResponse(true, HttpStatus.OK, "OTP verifié.", jwtResponse);
      } else {
        return createResponse(false, HttpStatus.UNAUTHORIZED, "OTP non valide ou expiré.", null);
      }
    } catch (NumberFormatException e) {
      return createResponse(false, HttpStatus.BAD_REQUEST, "L'OTP doit être un numéro valide.",
          null);
    }
  }

  private Map<String, Object> createResponse(boolean status, HttpStatus code, String message,
      Object data) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", status);
    response.put("code", code.value());
    response.put("message", message);
    response.put("data", data);
    return response;
  }
}
