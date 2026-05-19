package crm.chifco.com.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import crm.chifco.com.model.Privilege;
import crm.chifco.com.model.Role;
import crm.chifco.com.repository.PrivilegeRepository;
import crm.chifco.com.repository.RoleRepository;
import crm.chifco.com.utils.CaptchaResponse;

@Controller
@Transactional
@RequestMapping(value = "")
public class LoginController {

  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private PrivilegeRepository privilegeRepository;

  @Value("${serverNameCRM}")
  private String serverNameCRM;

  @Value("${recaptcha.secretkey}")
  private String recaptchaSecretKey;

  @Autowired
  private RestTemplate restTemplate;

  private final Logger logger = LogManager.getLogger(this.getClass());

  @GetMapping(value = "/")
  public String redirect() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    for (final GrantedAuthority grantedAuthority : authorities) {
      String authorityName = grantedAuthority.getAuthority();

      if (authorityName.equals("VIEW_DASHBORD_ADMIN")) {
        return "redirect:/admin/dashboard";
      }
      if (authorityName.equals("VIEW_DASHBORD_OTHER")) {
        return "redirect:/alluser/dashboard";
      }
    }
    // "redirect:/";
    return "login";
  }

  @GetMapping(value = "/login")
  public String login(Principal principal, Model model) {

    model.addAttribute("serverName", serverNameCRM);
    return principal == null ? "login" : "redirect:/";
  }

  @Transactional
  Privilege createPrivilegeIfNotFound(String name) {

    Privilege privilege = privilegeRepository.findPrivilegeByPrivilegeName(name);
    if (privilege == null) {
      privilege = new Privilege(name);
      privilegeRepository.save(privilege);
    }
    return privilege;
  }

  @Transactional
  Role createRoleIfNotFound(String name, List<Privilege> privileges) {

    Role role = roleRepository.findRoleByRoleName(name);
    if (role == null) {
      role = new Role(name);
      role.setPrivileges(privileges);
      roleRepository.save(role);
    }
    return role;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/login")
  public String login(@RequestParam Map<String, String> requestParams, HttpServletRequest request)
      throws IOException {

    String username = requestParams.get("username");
    String password = requestParams.get("password");
    String recaptchaResponse = requestParams.get("g-recaptcha-response");

    // Validate the reCAPTCHA response using Google's reCAPTCHA API endpoint
    String recaptchaUrl = "https://www.google.com/recaptcha/api/siteverify";
    String data = "secret=" + recaptchaSecretKey + "&response=" + recaptchaResponse;
    ResponseEntity<CaptchaResponse> responseEntity =
        restTemplate.postForEntity(recaptchaUrl, data, CaptchaResponse.class);
    CaptchaResponse recaptchaResponseObj = responseEntity.getBody();

    if (recaptchaResponseObj != null && recaptchaResponseObj.getSuccess()) {
      // Simulate authentication - Replace this with your actual authentication logic
      // For example, authenticate against a database or an external service
      if ("user".equals(username) && "password".equals(password)) {
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(username, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return redirect();

        // authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
      } else {
        // Redirect back to login page with an error message
        return "login?=true";

      }
    } else {
      // Redirect back to login page with an error message
      return "login?=true";

    }
  }

  @ResponseBody
  @PostMapping(value = "/validateToken")
  public boolean validateRecaptchaToken(@RequestBody HashMap<String, String> hmap) {
    try {

      // String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + recaptchaSecretKey
      // + "&response=" + token;

      // URL url = new URL("https://www.google.com/recaptcha/api/siteverify?secret="
      // + recaptchaSecretKey + "&response=" + token);

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
        return false;
      }
      logger.info(name+"Captcha api response {}",email);
      logger.info("Captcha api response {}", apiResponse.getHostname() + " adresse: "
          + apiResponse.getSuccess() + " score " + apiResponse.getScore());
      return Boolean.TRUE.equals(apiResponse.getSuccess());
    } catch (Exception e) {
      logger.error(e);
      // e.printStackTrace();
    }
    return false;
  }

}
