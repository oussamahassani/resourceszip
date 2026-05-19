package crm.chifco.com.crmMobile;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import crm.chifco.com.model.OtpSms;
import crm.chifco.com.repository.OtpRepository;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.OtpService;

@Service
public class OtpServiceMobileApp {
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private OtpService otpService;
  @Autowired
  private Notification notification;
  @Autowired
  private OtpRepository otpDataRepository;
  private static final int VERIFICATION_CODE_EXPIRATION_SECONDS = 180; // 3 minutes
  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  public int generateOTP() {
    Random random = new Random();
    int otp = 100000 + random.nextInt(900000);
    return otp;
  }

  public boolean isVerificationCodeExpired(OtpSms otpData) {
    Instant expirationTime = otpData.getOtpValidity().toInstant();
    return Instant.now().isAfter(expirationTime);
  }


  public boolean verifyOtp(String telephone, int inputOtp) {
    OtpSms otpData = otpDataRepository.findFirstOtpSmsByPhoneNumberDesc(telephone);
    if (Integer.parseInt(otpData.getCodeOtp()) == inputOtp && !isVerificationCodeExpired(otpData)) {

      return true;
    }

    return false;
  };

  public ResponseEntity<Map<String, Object>> SendSmsVerifForMobile(String telephone) {
	    try {
	     String repenseNotification =
	          notification.sendotp(telephone, "Votre code de sécurité est ");

	      final Map<String, Object> response = new HashMap<>();
	      // logger.info("send otp message repense Notification: " + repenseNotification);
	      if (!repenseNotification.isEmpty()) {
	        JSONObject jsonResponse = new JSONObject(repenseNotification.toString());
	        otpService.saveNewOtpMessage(jsonResponse.getString("PhoneNumber"),
	            jsonResponse.getString("otpCode"), jsonResponse.getNumber("expiryDate"));


	        response.put("status", true);
	        response.put("code", "200");
	        response.put("message", "votre vérification code a été envoyé ");
	        response.put("data", jsonResponse.getString("otpCode"));
	        return ResponseEntity.ok().body(response);
	      }
	      response.put("status", false);
	      response.put("code", "400");
	      response.put("message", "Erreur dans l'envoie du sms ");
	      response.put("data", null);
	      return ResponseEntity.badRequest().body(response);

	    } catch (Exception e) {

	      final Map<String, Object> errorBody = new HashMap<>();
	      errorBody.put("status", false);
	      errorBody.put("code", "400");
	      errorBody.put("message", "exception erreur: " + e.getMessage());
	      errorBody.put("data", null);
	      return ResponseEntity.ok().body(errorBody);
	    }
	  }

  public ResponseEntity<Map<String, Object>> SendSmsVerif(int otp, String telephone) {
    try {
     String repenseNotification =

          notification.sendotp(telephone, "Votre code de sécurité est ");

      final Map<String, Object> response = new HashMap<>();
      // logger.info("send otp message repense Notification: " + repenseNotification);
      if (!repenseNotification.isEmpty()) {
        JSONObject jsonResponse = new JSONObject(repenseNotification.toString());
        otpService.saveNewOtpMessage(jsonResponse.getString("PhoneNumber"),
            jsonResponse.getString("otpCode"), jsonResponse.getNumber("expiryDate"));


        response.put("status", true);
        response.put("code", "200");
        response.put("message", "votre vérification code a été envoyé ");
        response.put("data", null);
        return ResponseEntity.ok().body(response);
      }
      response.put("status", false);
      response.put("code", "400");
      response.put("message", "Erreur dans l'envoie du sms ");
      response.put("data", null);
      return ResponseEntity.badRequest().body(response);

    } catch (Exception e) {

      final Map<String, Object> errorBody = new HashMap<>();
      errorBody.put("status", false);
      errorBody.put("code", "400");
      errorBody.put("message", "exception erreur: " + e.getMessage());
      errorBody.put("data", null);
      return ResponseEntity.ok().body(errorBody);
    }
  }
}
