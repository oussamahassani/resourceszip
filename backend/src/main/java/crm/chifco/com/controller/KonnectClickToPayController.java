package crm.chifco.com.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import crm.chifco.com.model.Bordereau;
import crm.chifco.com.model.ClicToPay;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.BordereaurRepository;
import crm.chifco.com.repository.ClickToPayRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.BordereauService;
import crm.chifco.com.templateclasse.ErrorResponseKonnectApi;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.typePayementBordereau;

@Controller
@RequestMapping("/payment/clictopay/*")
public class KonnectClickToPayController {
  @Value("${returnAdresse}")
  private String returnAdresse;

  @Value("${click.to.pay.login}")
  private String clickToPayLogin;

  @Value("${click.to.pay.password}")
  private String clickToPayPassword;

  @Value("${konnectKey}")
  private String konnectKey;

  @Value("${urlKonnect}")
  private String urlKonnect;

  @Value("${waltId}")
  private String waltId;

  // private static SessionFactory sessionFactory;
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ClickToPayRepository clickToPayRepository;

  @Autowired
  private BordereaurRepository bordereaurRepository;

  @Autowired
  BordereauService bordereauService;

  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  private static final String PAYTABS_FEEDBACK_URL = "checkout";
  private static final String RETURN_URL = "feedback";

  @PostMapping(PAYTABS_FEEDBACK_URL)
  public void clictopayPayment(
      @RequestParam(value = "montantBd", required = false) Double montantBd,
      @RequestParam(value = "refBordereau", required = false) String refBordereau,
      HttpServletResponse httpResponse, HttpServletRequest httpRequest) {
    // Map<String, Object> response = new HashMap<>();
    StringBuilder response = new StringBuilder();
    String currency = "788";
    // Implement logic to get merchant details and transaction values
    ModelAndView modelAndView = new ModelAndView();
    String localHost;

    localHost = httpRequest.getServerName();
    // Get the host name

    // Print the host name
    LOGGER.info("Host Name: " + localHost);

    int orderNumberQuery = bordereaurRepository.getNextValueClicToPayOrderNumberSeq();
    Date date = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy");
    String year = df.format(date);
    String format = "%1$07d";
    String result = String.format(format, orderNumberQuery) + "-" + year;
    // Execute the query
    // Statement statement = connection.createStatement();
    /*
     * try (ResultSet resultSet = statement.executeQuery(orderNumberQuery)) { if (resultSet.next())
     * { long nextValue = resultSet.getLong(1); System.out.println("Next sequence value: " +
     * nextValue); } else { System.out.println("Failed to retrieve sequence value."); } } long
     * longSeqValue = seqValue.longValue();
     */

    String montant = CrmUtils.formatDoubleInputToString(montantBd).replace(".", "");
    Map<String, Object> clictopayTxValues = new HashMap<>();
    clictopayTxValues.put("userName", clickToPayLogin);
    clictopayTxValues.put("password", clickToPayPassword);
    clictopayTxValues.put("returnUrl", returnAdresse + "/payment/clictopay/feedback");
    clictopayTxValues.put("orderNumber", result);
    clictopayTxValues.put("currency", currency);
    clictopayTxValues.put("amount", montant);
    clictopayTxValues.put("failUrl", returnAdresse + "/payment/clictopay/failed");

    String payPageUrl = "https://test.clictopay.com/payment/rest/register.do"; // Replace with your
    // actual code to get
    // the URL
    String apiUrl = payPageUrl + "?";
    for (Map.Entry<String, Object> entry : clictopayTxValues.entrySet()) {
      apiUrl += entry.getKey() + "=" + entry.getValue() + "&";
    }
    try {
      // Create a URL object
      URL url = new URL(apiUrl);

      // Open a connection to the URL
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      // Set the request method to POST
      connection.setRequestMethod("POST");

      // Enable input and output streams
      connection.setDoOutput(true);

      // Write the POST data to the connection's output stream
      OutputStream os = connection.getOutputStream();
      os.write(apiUrl.getBytes());
      os.flush();
      os.close();

      // Get the response from the server
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // Read the response data
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // Parse the response as needed (e.g., JSON parsing)
        String responseText = response.toString();
        // Use responseText as needed

        // Log the response
        LOGGER.info("ClickToPay Response: " + responseText);
        // Redirect to formUrl
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseText.toString());
        ClicToPay newClickToPay = new ClicToPay();
        newClickToPay.setBordereau(refBordereau);
        newClickToPay.setOrderNumber(result);
        if (rootNode.has("orderId")) {
          newClickToPay.setOrderId(rootNode.get("orderId").asText());
        }
        if (rootNode.has("errorMessage")) {
          newClickToPay.setErrorMessage(rootNode.get("errorMessage").asText());
        }
        clickToPayRepository.save(newClickToPay);
        if (rootNode.has("formUrl")) {
          // Extract the formUrl field from JSON
          String formUrl = rootNode.get("formUrl").asText();
          httpResponse.sendRedirect(formUrl);
        } else {
          String originalUrl = httpRequest.getHeader("referer");
          if (originalUrl != null) {
            httpResponse.sendRedirect(originalUrl);
          }
        }

      } else {
        // Handle the error response
        String originalUrl = httpRequest.getHeader("referer");
        if (originalUrl != null) {
          httpResponse.sendRedirect(originalUrl);
        }
        LOGGER.info("HTTP Error: " + responseCode);
      }

      // Disconnect the connection
      connection.disconnect();
    } catch (Exception e) {
      // Handle exceptions
      LOGGER.info("error payement en ligne", e);
      e.printStackTrace();
      // String formUrl = "ffff";
      // httpResponse.sendRedirect(formUrl);
    }

    // return response;
  }

  @RequestMapping(RETURN_URL)
  public ModelAndView clictopayFeedback(
      @RequestParam(value = "payment_ref", required = false) String orderId,
      RedirectAttributes redirectAttributes) {
    ModelAndView modelAndView = new ModelAndView();

    // String orderStatusUrl = "https://test.clictopay.com/payment/rest/getOrderStatus.do";
    String orderStatusUrl = urlKonnect + "payments/" + orderId;
    try {
      // Construct the URL with query parameters
      // String userName = clickToPayLogin; // "0402392401";
      // String password = clickToPayPassword; // "7VRJpvM5";

      // String apiUrl = orderStatusUrl + "?userName=" + userName + "&password=" + password
      // + "&orderId=" + orderId;

      // Create a URL object
      URL url = new URL(orderStatusUrl);

      // Open a connection to the URL
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      // Set the request method to GET
      connection.setRequestMethod("GET");

      // Get the response from the server
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // Read the response data
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // Parse the response as needed (e.g., JSON parsing)
        String responseText = response.toString();
        // Use responseText as needed
        ClicToPay exitClickToPay = clickToPayRepository.findAbonnementByOrderId(orderId);
        if (exitClickToPay != null) {
          exitClickToPay.setIsPassed(true);
          ObjectMapper objectMapper = new ObjectMapper();
          JsonNode rootNode = objectMapper.readTree(responseText);
          JsonNode paymentNode = rootNode.get("payment");
          if (paymentNode != null && paymentNode.get("status") != null
              && paymentNode.get("status").asText().equals("completed")) {
            exitClickToPay.setApprovalCode(paymentNode.get("orderId").asText());


            clickToPayRepository.save(exitClickToPay);
            Bordereau existBrd = bordereaurRepository
                .findBordereauByReferenceBordereau(exitClickToPay.getBordereau());
            if (existBrd != null) {
              User user = userRepository.findTop1UsersByTypeuser("SYSTEM");
              // existBrd.setstatus(StatutBordereau.VERSEMENT_CONFIRME);
              // existBrd.setCheckBy(user);
              // existBrd.setDateVersement(new Date());
              // existBrd.setDateValidationBrd(new Date());
              // .setTypeDePayement(typePayementBordereau.PayementParCarte);
              // bordereaurRepository.save(existBrd);
              Date date = new Date();
              SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
              String formattedDate = formatter.format(date);
              bordereauService.accpetBordereauByAdmin(existBrd, user, "", formattedDate,
                  typePayementBordereau.PayementParCarte, redirectAttributes);
            }
          }
          if (paymentNode.has("status")) {
            exitClickToPay.setErrorCode(paymentNode.get("status").asText());
          }
          if (paymentNode.has("status")) {
            exitClickToPay.setErrorMessage(paymentNode.get("status").asText());
          }
        }

        // Log the response
        System.out.println("Response: " + responseText);
      } else {
        // Handle the error response
        System.err.println("HTTP Error: " + responseCode);
      }

      // Disconnect the connection
      connection.disconnect();
    } catch (Exception e) {
      // Handle exceptions
      e.printStackTrace();
    }

    modelAndView.setViewName("redirect:/payment/clictopay/statusPayement");
    return modelAndView;
  }

  @RequestMapping("failed")
  public String clictopayFailed(Model model,
      @RequestParam(value = "payment_ref", required = false) String orderId,
      RedirectAttributes redirectAttributes) {
    // Implement logic for failed payments
    LOGGER.info("failed to payement");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
    model.addAttribute("userphoto", user.getPhoto());
    model.addAttribute("userrole", user.getRole().getRoleName());
    model.addAttribute("useremail", user.getEmail());
    // String orderStatusUrl = "https://test.clictopay.com/payment/rest/getOrderStatus.do";
    String orderStatusUrl = urlKonnect + "payments/" + orderId;

    try {
      // Construct the URL with query parameters
      // String userName = clickToPayLogin; // "0402392401";
      // String password = clickToPayPassword; // "7VRJpvM5";

      // String apiUrl = orderStatusUrl + "?userName=" + userName + "&password=" + password
      // + "&orderId=" + orderId;

      // Create a URL object
      URL url = new URL(orderStatusUrl);

      // Open a connection to the URL
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      // Set the request method to GET
      connection.setRequestMethod("GET");

      // Get the response from the server
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        // Read the response data
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();

        // Parse the response as needed (e.g., JSON parsing)
        String responseText = response.toString();
        // Use responseText as needed
        ClicToPay exitClickToPay = clickToPayRepository.findAbonnementByOrderId(orderId);
        if (exitClickToPay != null) {
          exitClickToPay.setIsPassed(false);
          ObjectMapper objectMapper = new ObjectMapper();
          JsonNode rootNode = objectMapper.readTree(responseText);
          JsonNode paymentNode = rootNode.get("payment");
          if (paymentNode.has("status")) {
            exitClickToPay.setErrorCode(paymentNode.get("status").asText());
            exitClickToPay.setErrorMessage(paymentNode.get("status").asText());
            if (paymentNode.get("status").asText().equals("completed")) {
              exitClickToPay.setApprovalCode(paymentNode.get("orderId").asText());
              exitClickToPay.setIsPassed(true);

              Bordereau existBrd = bordereaurRepository
                  .findBordereauByReferenceBordereau(exitClickToPay.getBordereau());
              if (existBrd != null) {
                User userSystem = userRepository.findTop1UsersByTypeuser("SYSTEM");

                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = formatter.format(date);
                bordereauService.accpetBordereauByAdmin(existBrd, userSystem, "", formattedDate,
                    typePayementBordereau.PayementParCarte, redirectAttributes);
              }

            }
          }

          clickToPayRepository.save(exitClickToPay);

          if (paymentNode.has("status") && paymentNode.get("status").asText().equals("completed")) {
            return "bordereaux/statusPayementSuccess";
          }

        }

        // Log the response
        System.out.println("Response: " + responseText);
      } else {
        // Handle the error response
        System.err.println("HTTP Error: " + responseCode);
      }

      // Disconnect the connection
      connection.disconnect();
    } catch (Exception e) {
      // Handle exceptions
      e.printStackTrace();
    }
    return "bordereaux/statusPayementError";
  }

  @GetMapping(value = "statusPayement")
  public String statusPayement(Model model, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
    model.addAttribute("userphoto", user.getPhoto());
    model.addAttribute("userrole", user.getRole().getRoleName());
    model.addAttribute("useremail", user.getEmail());

    return "bordereaux/statusPayementSuccess";
  }

  @RequestMapping("returnToBordereau")
  public ModelAndView returnToBordereau(
      @RequestParam(value = "orderId", required = false) String orderId,
      RedirectAttributes redirectAttributes) {
    ModelAndView modelAndView = new ModelAndView();
    ClicToPay exitClickToPay = clickToPayRepository.findAbonnementByOrderId(orderId);
    Bordereau existBrd =
        bordereaurRepository.findBordereauByReferenceBordereau(exitClickToPay.getBordereau());
    if (existBrd != null) {
      modelAndView.setViewName("redirect:/bordereau/editbordereaux/" + existBrd.getBordereauId());
    } else {
      modelAndView.setViewName("redirect:/bordereau/viewlisteBordereau");
    }
    return modelAndView;
  }

  @PostMapping("/konnectPayement")
  public void initPayment(@RequestParam(value = "montantBd", required = false) Double montantBd,
      @RequestParam(value = "refBordereau", required = false) String refBordereau,
      HttpServletResponse httpResponse, HttpServletRequest httpRequest) throws IOException {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        String apiUrl = urlKonnect; // or
                                    // "https://api.konnect.network/api/v2"
                                    // for production

        URL url = new URL(apiUrl + "payments/init-payment");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("x-api-key", konnectKey); // Replace
        // with
        // your
        // API
        // key
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        Map<String, Object> paymentData = new HashMap<>();
        double roundedValue = Math.round(montantBd * 1000);


        paymentData.put("receiverWalletId", waltId);
        paymentData.put("token", "TND");
        paymentData.put("amount", roundedValue);
        paymentData.put("type", "immediate");
        paymentData.put("description", "payment description");
        String[] acceptedPaymentMethods = {"wallet", "bank_card", "e-DINAR"};
        paymentData.put("acceptedPaymentMethods", acceptedPaymentMethods);
        paymentData.put("orderId", user.getCodeUser() + "-" + new Date().getTime());
        paymentData.put("theme", "light");
        paymentData.put("webhook", returnAdresse + "payment/clictopay/failed");
        paymentData.put("silentWebhook", true);
        paymentData.put("successUrl", returnAdresse + "/payment/clictopay/feedback");
        paymentData.put("failUrl", returnAdresse + "/payment/clictopay/failed");
        paymentData.put("checkoutForm", true);
        paymentData.put("addPaymentFeesToAmount", false);
        paymentData.put("firstName", user.getFirstName() + " " + user.getLastName());
        paymentData.put("lastName", user.getCodeUser());
        paymentData.put("phoneNumber", user.getTelephone());
        paymentData.put("email", user.getEmail());

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(paymentData);
        conn.getOutputStream().write(jsonBody.getBytes());

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
          StringBuilder response = new StringBuilder();
          BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

          String line;
          while ((line = reader.readLine()) != null) {
            response.append(line);
          }
          reader.close();
          String responseText = response.toString();
          JsonNode rootNode = objectMapper.readTree(responseText.toString());
          ClicToPay newClickToPay = new ClicToPay();
          newClickToPay.setBordereau(refBordereau);

          if (rootNode.has("paymentRef")) {
            // int orderNumberQuery = bordereaurRepository.getNextValueClicToPayOrderNumberSeq();
            // Date date = new Date();
            // SimpleDateFormat df = new SimpleDateFormat("yyyy");
            // String year = df.format(date);
            // String format = "%1$07d";
            // String result = String.format(format, orderNumberQuery) + "-" + year;
            newClickToPay.setOrderId(rootNode.get("paymentRef").asText());
            newClickToPay.setOrderNumber("12345");
          }
          newClickToPay.setProvider("konnect");
          clickToPayRepository.save(newClickToPay);
          if (rootNode.has("payUrl")) {
            // Extract the formUrl field from JSON
            String formUrl = rootNode.get("payUrl").asText();
            httpResponse.sendRedirect(formUrl);
          } else {
            LOGGER.error("erreur payyement brd " + refBordereau);
            LOGGER.error("payUrl not exist");
            String originalUrl = httpRequest.getHeader("referer");
            httpResponse.sendRedirect(originalUrl);
          }
        } else if (conn.getResponseCode() == 422) {
          ObjectMapper mapper = new ObjectMapper();
          ErrorResponseKonnectApi errorResponse =
              mapper.readValue(conn.getErrorStream(), ErrorResponseKonnectApi.class);


          LOGGER.error("erreur payyement brd " + refBordereau);
          LOGGER.error(errorResponse);
          String originalUrl = httpRequest.getHeader("referer");
          httpResponse.sendRedirect(originalUrl);
        } else if (conn.getResponseCode() == 404) {

          Map<String, Object> ResultErreur = new HashMap<>();

          ObjectMapper mapper = new ObjectMapper();

          ResultErreur.put("code", 404);
          ResultErreur.put("message", conn.getErrorStream().toString());
          LOGGER.error("erreur payyement brd " + refBordereau);
          LOGGER.error(conn.getErrorStream().toString());
          String originalUrl = httpRequest.getHeader("referer");
          httpResponse.sendRedirect(originalUrl);
        } else {
          throw new RuntimeException("Failed to initialize payment. HTTP error code: "
              + conn.getResponseCode() + conn.getErrorStream());

        }
      }
    } catch (IOException e) {
      throw e;
    }
  }

}
