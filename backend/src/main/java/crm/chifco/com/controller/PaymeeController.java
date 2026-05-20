package crm.chifco.com.controller;
import  crm.chifco.com.controller.PaymeeClient;
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

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

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

@Controller
@RequestMapping("/payment/paymee/*")
public class PaymeeController {

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
	
	  private static final String RETURN_URL = "feedback";
      private static final String urlKonnect="https://app.paymee.tn/api/v2";
      private static final String returnAdresse="https://customer.chifco.com";
      private static final String token = "b84039920aed434c7330ad684454f3f5b3fa4bcb";
      private static final String tokenGet = "Token b84039920aed434c7330ad684454f3f5b3fa4bcb";


	  @RequestMapping(RETURN_URL)
	  public ModelAndView clictopayFeedback(
			  @RequestParam(value = "payment_token", required = false) String token,
		      @RequestParam(value = "transaction", required = false) String statusorder ,
	      RedirectAttributes redirectAttributes) {
	    ModelAndView modelAndView = new ModelAndView();
	    LOGGER.info("clictopayFeedback" + token) ;
	    LOGGER.info("clictopayFeedback" + statusorder) ;
	    // String orderStatusUrl = "https://test.clictopay.com/payment/rest/getOrderStatus.do";
	    String orderStatusUrl = urlKonnect +"/payments/"+ token + "/check" ;
	    try {

	      // Create a URL object
	      URL url = new URL(orderStatusUrl);

	      // Open a connection to the URL
	      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	      // Set the request method to GET
	      connection.setRequestMethod("GET");
	      connection.setRequestProperty("Authorization", tokenGet); // Replace

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
	        ClicToPay exitClickToPay = clickToPayRepository.findAbonnementByOrderId(token);
	        if (exitClickToPay != null) {
	          exitClickToPay.setIsPassed(true);
	          ObjectMapper objectMapper = new ObjectMapper();
	          JsonNode rootNode = objectMapper.readTree(responseText);
	          JsonNode paymentNode = rootNode.get("data");
	          if (paymentNode != null && paymentNode.get("payment_status") != null
	              && paymentNode.get("payment_status").asText().equals("true")) {
	            exitClickToPay.setApprovalCode(paymentNode.get("transaction_id").asText());


	            clickToPayRepository.save(exitClickToPay);
	            Bordereau existBrd = bordereaurRepository
	                .findBordereauByReferenceBordereau(exitClickToPay.getBordereau());
	            if (existBrd != null) {
	              User user = userRepository.findTop1UsersByTypeuser("SYSTEM");
	           
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
	        LOGGER.info("Response: " + responseText);
	      } else {
	        // Handle the error response
	    	  LOGGER.info("HTTP Error: " + responseCode);
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
	      @RequestParam(value = "payment_token", required = false) String orderId,
	      @RequestParam(value = "transaction", required = false) String statusorder,
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
	    String orderStatusUrl =urlKonnect +"/payments/"+ orderId + "/check"   ;

	    try {
	
	      URL url = new URL(orderStatusUrl);

	      // Open a connection to the URL
	      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	      // Set the request method to GET
	      connection.setRequestMethod("GET");
	      connection.setRequestProperty("Authorization", tokenGet); 

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
	          JsonNode paymentNode = rootNode.get("data");
	          if (paymentNode.has("payment_status")) {
	            exitClickToPay.setErrorCode(paymentNode.get("payment_status").asText());
	            exitClickToPay.setErrorMessage(paymentNode.get("payment_status").asText());
	            if (paymentNode.get("payment_status").asText().equals("true")) {
	              exitClickToPay.setApprovalCode(paymentNode.get("transaction_id").asText());
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

	          if (paymentNode.has("payment_status") && paymentNode.get("payment_status").asText().equals("true")) {
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

	  @PostMapping("/payement")
	  public void initPayment(@RequestParam(value = "montantBd", required = false) Double montantBd,
	      @RequestParam(value = "refBordereau", required = false) String refBordereau,
	      HttpServletResponse httpResponse, HttpServletRequest httpRequest) throws IOException {
	    try {
	      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	      if (!(authentication instanceof AnonymousAuthenticationToken)) {
	        String currentUser = authentication.getName();
	        User user = userRepository.findUsersByEmail(currentUser);
	       

	        URL url = new URL(urlKonnect + "/payments/create");
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Authorization", "Token "+token); // Replace
	        // with
	        // your
	        // API
	        // key
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setDoOutput(true);
	        Map<String, Object> paymentData = new HashMap<>();
	        double roundedValue = Math.round(montantBd);


	    
	        paymentData.put("amount", roundedValue);
	        paymentData.put("order_id", user.getCodeUser() + "-" + new Date().getTime());
	        paymentData.put("theme", "light");
	        paymentData.put("silentWebhook", true);
	        paymentData.put("return_url", returnAdresse  + "/payment/clictopay/feedback");
	        paymentData.put("webhook_url", returnAdresse + "/payment/clictopay/feedback");
	        paymentData.put("cancel_url", returnAdresse + "/payment/clictopay/failed");
	        paymentData.put("checkoutForm", true);
	        paymentData.put("addPaymentFeesToAmount", false);
	        paymentData.put("first_name", user.getFirstName() + " " + user.getLastName());
	        paymentData.put("last_name", user.getCodeUser());
	        paymentData.put("phone", user.getTelephone());
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

	          if (rootNode.has("data")) {
	            // int orderNumberQuery = bordereaurRepository.getNextValueClicToPayOrderNumberSeq();
	            // Date date = new Date();
	            // SimpleDateFormat df = new SimpleDateFormat("yyyy");
	            // String year = df.format(date);
	            // String format = "%1$07d";
	            // String result = String.format(format, orderNumberQuery) + "-" + year;
	            newClickToPay.setOrderId(rootNode.get("data").get("token").asText());
	            newClickToPay.setOrderNumber(rootNode.get("data").get("order_id").asText());
	          }
	          newClickToPay.setProvider("paymee");
	          clickToPayRepository.save(newClickToPay);
	          if (rootNode.has("data")) {
	        	  JsonNode dataNode = rootNode.path("data");
	        	  if(dataNode.has("payment_url")) {
	        		  
	            String formUrl = dataNode.get("payment_url").asText();
	            httpResponse.sendRedirect(formUrl);
	        	  }
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





/*
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> body) {
        try {
            String token = body.get("token").toString();
            boolean paymentStatus = "1".equals(body.get("payment_status").toString());
            String checkSum = body.get("check_sum").toString();

            boolean verified = paymeeClient.verifyWebhookCallback(token, paymentStatus, checkSum);
            if (!verified) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid checksum"));
            }

            if (paymentStatus) {
                // ✅ Payment successful → update your order in DB
                System.out.println("Payment success for token " + token);
            } else {
                // ❌ Payment failed / canceled
                System.out.println("Payment failed for token " + token);
            }

            return ResponseEntity.ok(Map.of("status", "received"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
*/
 

}
