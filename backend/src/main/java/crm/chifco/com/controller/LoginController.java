package crm.chifco.com.controller;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import crm.chifco.com.utils.CaptchaResponse;

@RestController
@RequestMapping(value = "")
public class LoginController {

  @Value("${recaptcha.secretkey}")
  private String recaptchaSecretKey;

  private final Logger logger = LogManager.getLogger(this.getClass());

  @PostMapping(value = "/validateToken")
  public ResponseEntity<Boolean> validateRecaptchaToken(@RequestBody HashMap<String, String> hmap) {
    try {
      RestTemplate restTemplate = new RestTemplate();
      String token = hmap.get("token");
      String name = hmap.get("name");
      String email = hmap.get("email");
      MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
      requestMap.add("secret", recaptchaSecretKey);
      requestMap.add("response", token);

      CaptchaResponse apiResponse = restTemplate.postForObject(
          "https://www.google.com/recaptcha/api/siteverify", requestMap, CaptchaResponse.class);

      if (apiResponse == null) {
        return ResponseEntity.ok(false);
      }
      logger.info("{} Captcha api response {}", name, email);
      logger.info("Captcha api response {}", apiResponse.getHostname() + " adresse: "
          + apiResponse.getSuccess() + " score " + apiResponse.getScore());
      return ResponseEntity.ok(Boolean.TRUE.equals(apiResponse.getSuccess()));
    } catch (Exception e) {
      logger.error(e);
    }
    return ResponseEntity.ok(false);
  }
}
