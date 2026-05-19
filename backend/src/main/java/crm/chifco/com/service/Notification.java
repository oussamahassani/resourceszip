package crm.chifco.com.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.EmailAdresse;
import crm.chifco.com.model.PhoneNumbers;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.utils.NomStatutReclamation;


@Service
@Transactional
public class Notification {
  private final Logger logger = LogManager.getLogger(this.getClass());
  private static HttpURLConnection conn;
  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String sender;

  @Value("${webSiteNety}")
  private String webSiteNety;

  @Autowired
  private NotificationConfigService notificationConfigService;


  public String sendotp(String mobilenum, String Message) {
    //
    String returnResult = "";

    try {

      URL url = new URL("https://smsingotp.chifco.com/api/Contact/SENDOTP");
      conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      JSONObject bodyjson = new JSONObject();
      bodyjson.put("otpValidity", 2500);
      bodyjson.put("otpToken", "6271608B-9C06-4EDC-9ED9-E245928A11B1");
      bodyjson.put("message", Message);
      bodyjson.put("source", "Nety");
      bodyjson.put("phoneNb", mobilenum);

      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

      wr.write(bodyjson.toString());

      wr.flush();

      int status = conn.getResponseCode();
      logger.info("response code: " + status);

      if (status == 200) {

        if (status == HttpURLConnection.HTTP_OK) {

          returnResult = fetchJsonObjectFormApi(conn);
        }
        return returnResult;
      } else {
        returnResult = fetchJsonObjectFormApi(conn);
        return returnResult;
      }
    } catch (MalformedURLException e) {

      logger.error("Notification.sendotp MalformedURLException: " + e.getMessage());

    } catch (IOException e) {
      logger.error("Notification.sendotp IOException: " + e.getMessage());
      logger.error("Notification.sendotp getStackTrace: " + e.getStackTrace());

    } finally {
      conn.disconnect();
      return returnResult;
    }
  }

  public Boolean sendsmsnotification(ArrayList<Map<String, Object>> Message) {
    Boolean returnResult = false;
    String respenseMessage = "";

    try {

      URL url = new URL("http://smsing.chifco.com/api/Contact/SENDSMSLISTBYSender");
      conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      JSONObject Messagejson = new JSONObject();
      // Map<String, Object> Message = new HashMap<String, Object>();

      Object[] arrayMessage = {Message};
      // arrayMessage[0] = Message ;
      JSONObject bodyjson = new JSONObject();
      bodyjson.put("sender", "Nety");
      bodyjson.put("APIKEY", "6271608B-9C06-4EDC-9ED9-E245928A11B1");
      bodyjson.put("Messages", Message);
      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

      wr.write(bodyjson.toString());

      wr.flush();

      int status = conn.getResponseCode();
      if (status == 200) {

        respenseMessage = fetchJsonObjectFormApi(conn);
        // Analyser la réponse JSON
        JSONObject jsonResponse = new JSONObject(respenseMessage.toString());
        logger.info("return from api" + jsonResponse);
        if (jsonResponse.get("Success") != null && jsonResponse.get("Success").equals(true)) {
          returnResult = true;
          logger.info("Notification.sendsmsnotification: " + jsonResponse);
        }

        else {
          returnResult = false;
          logger.error("Notification.sendsmsnotification: " + jsonResponse);
        }
        logger.info("message envoyee: " + Message);
        return returnResult;
      } else {
        respenseMessage = fetchJsonObjectFormApi(conn);
        logger.info("returnResult status erreur: " + respenseMessage);
        return returnResult;
      }
    } catch (MalformedURLException e) {

      logger.error("Notification.sendsmsnotification MalformedURLException: " + e.getMessage());
    } catch (IOException e) {
      logger.error("Notification.sendsmsnotification MalformedURLException: " + e.getMessage());

    } finally {
      conn.disconnect();
      return returnResult;
    }
  }

  public String sendSimpleMail(String recipient, String msgBody, String subject) {

    try {

      // Creating a simple mail message
      SimpleMailMessage mailMessage = new SimpleMailMessage();

      // Setting up necessary details
      mailMessage.setFrom(sender);
      mailMessage.setTo(recipient);
      mailMessage.setText(msgBody);
      mailMessage.setSubject(subject);

      mailSender.send(mailMessage);
      return "Mail Sent Successfully...";
    }

    // Catch block to handle the exceptions
    catch (Exception e) {
      logger.info("status" + e);
      return "Error while Sending Mail";
    }

  }

  public String sendSimpleMailHtml(String recipient, String template, String subject) {
    // Try block to check for exceptions
    try {

      // Creating a simple mail message
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      MimeMessage message = mailSender.createMimeMessage();

      message.setSubject(subject);
      MimeMessageHelper helper;
      helper = new MimeMessageHelper(message, true);
      helper.setFrom(sender);
      helper.setTo(recipient);
      helper.setText(template, true);

      // Sending the mail
      mailSender.send(message);
      return "SUCCESS_EMAIL_SEND";
    }

    // Catch block to handle the exceptions
    catch (Exception e) {
      logger.info("status" + e);
      return "ERR_EMAIL_SEND";
    }
  }

  public String sendSimpleMailHtmlWithAttachement(String recipient, String cc, String template,
      String subject, File file) {
    // Try block to check for exceptions
    try {

      // Creating a simple mail message
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      MimeMessage message = mailSender.createMimeMessage();

      message.setSubject(subject);
      MimeMessageHelper helper;
      helper = new MimeMessageHelper(message, true);
      helper.setFrom(sender);
      helper.setTo(recipient);
      if (cc != null) {
        helper.addCc(cc);
      }

      helper.setText(template, true);
      helper.addAttachment(subject, file);
      // Sending the mail
      mailSender.send(message);
      return "SUCCESS_EMAIL_SEND";
    }

    // Catch block to handle the exceptions
    catch (Exception e) {
      logger.info("status" + e);
      return "ERR_EMAIL_SEND";
    }
  }

  public String sendSimpleMailHtmlWithCC(List<String> cc, String recipient, String template,
      String subject) {
    // Try block to check for exceptions
    try {

      // Creating a simple mail message
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      MimeMessage message = mailSender.createMimeMessage();

      message.setSubject(subject);
      MimeMessageHelper helper;
      helper = new MimeMessageHelper(message, true);
      helper.setFrom(sender);
      helper.setTo(recipient);
      helper.setText(template, true);
      if (cc != null && !cc.isEmpty()) {
        String[] ccArray = new String[cc.size()];
        ccArray = cc.toArray(ccArray);
        helper.setCc(ccArray);
      }
      // Sending the mail
      mailSender.send(message);
      return "SUCCESS_EMAIL_SEND";
    }

    // Catch block to handle the exceptions
    catch (Exception e) {
      logger.info("status" + e);
      return "ERR_EMAIL_SEND";
    }
  }

  // added by oussama abid
  // send email for a list of emails
  public String sendMultipleMail(List<String> recipients, String msgBody, String subject) {
    try {

      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setFrom(sender);
      mailMessage.setTo(recipients.toArray(new String[0])); // Convert the list to an array
                                                            // [email1,email2,email3]
      mailMessage.setText(msgBody);
      mailMessage.setSubject(subject);
      mailSender.send(mailMessage);
      return "Mail Sent Successfully...";
    } catch (Exception e) {
      logger.error("Error while Sending Mail", e);
      return "Error while Sending Mail";
    }
  }

  // added by oussama abid
  // send multiple sms for list of telephone
  public String sendOtpTelephones(List<String> mobileNumbers, String message) {
    Boolean resultaSms = false;
    logger.info("enter method send otp telephones ");
    ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
    Map<String, Object> Message = new HashMap<String, Object>();
    for (String mobileNumber : mobileNumbers) {
      Message.put("number", mobileNumber);
      Message.put("message", message);
      smsToSend.add(Message);
    }
    if (smsToSend.size() > 0) {

      resultaSms = this.sendsmsnotification(smsToSend);

    }
    if (resultaSms) {
      logger.info("Liste des sms envoyé : " + smsToSend);
      return "Liste des sms envoyé : " + smsToSend;
    } else {
      logger.error("Liste des sms n'ont pas pu etre envoyé : " + smsToSend);
      return "Liste des sms n'ont pas pu etre envoyé : " + smsToSend;
    }
  }

  // added by oussama abid
  // combine the 2 method send list emails and telephones
  public String sendNotifications(List<EmailAdresse> emails, List<PhoneNumbers> mobileNumbers,
      String message, String subject) {
    String emailResult = "Aucun email";
    String smsResult = "Aucun numéro de téléphone";
    List<String> emailss =
        emails.stream().map(EmailAdresse::getEmailAdresse).collect(Collectors.toList());
    List<String> mobileNumberss =
        mobileNumbers.stream().map(PhoneNumbers::getPhoneNumbers).collect(Collectors.toList());
    if (!emails.isEmpty()) {
      emailResult = sendMultipleMail(emailss, message, subject);
      logger.info("Notification.sendNotifications infoEmailSent: " + emailResult);
    }
    if (!mobileNumbers.isEmpty()) {
      smsResult = sendOtpTelephones(mobileNumberss, message);
      logger.info("Notification.sendNotifications infoSmsSent: " + smsResult);
    }
    return "Email Result: " + emailResult + "\nSMS Result: " + smsResult;
  }

  // added by oussama abid

  public String sendMultipleMailHtml(List<String> recipients, String htmlBody, String subject) {
    try {
      List<String> validEmails =
          recipients.stream().filter(email -> email != null && email.contains("@")).distinct()
              .collect(Collectors.toList());
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(sender);
      helper.setBcc(validEmails.toArray(new String[0]));
      helper.setSubject(subject);
      helper.setText(htmlBody, true);
      mailSender.send(message);
      return "SUCCESS_EMAIL_SEND";
    } catch (Exception e) {
      logger.error("Error while Sending HTML Mail", e);
      return "ERR_EMAIL_SEND";
    }
  }

  public String fetchJsonObjectFormApi(HttpURLConnection connction) {
    InputStreamReader isr;

    StringBuilder response = new StringBuilder();
    try {
      isr = new InputStreamReader(connction.getInputStream());
      BufferedReader br = new BufferedReader(isr);

      String line;
      while ((line = br.readLine()) != null) {
        response.append(line);
      }
      br.close();
    } catch (IOException e) {

      logger.info("fetchJsonObjectFormApi service notification" + e);
    }
    return response.toString();
  }

  public String getCodePromo() {
    String returnResult = null;
    String respenseMessage = "";

    try {

      URL url = new URL(webSiteNety + "/rest/promocode");
      conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      JSONObject Messagejson = new JSONObject();
      // Map<String, Object> Message = new HashMap<String, Object>();

      Object[] arrayMessage = {};
      // arrayMessage[0] = Message ;
      JSONObject bodyjson = new JSONObject();
      bodyjson.put("sender", "crm");


      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

      wr.write(bodyjson.toString());

      wr.flush();

      int status = conn.getResponseCode();
      if (status == 200) {

        respenseMessage = fetchJsonObjectFormApi(conn);
        // Analyser la réponse JSON
        JSONObject jsonResponse = new JSONObject(respenseMessage.toString());
        logger.info("return from api" + jsonResponse);
        if (jsonResponse.has("data") && !jsonResponse.isNull("data")) {
          JSONObject dataObject = jsonResponse.getJSONObject("data");
          if (dataObject.has("code")) {
            returnResult = dataObject.getString("code");
            System.out.println("Promo code WEB SITE: " + returnResult);

          } else {
            System.out.println("The 'code' key is not found in the 'data' object.");

          }
          logger.info("promocode WEB SITE: " + jsonResponse);
          return returnResult;
        }

        else {
          returnResult = null;
          logger.error("promocode WEB SITE: " + jsonResponse);
        }
        return returnResult;
      } else {
        respenseMessage = fetchJsonObjectFormApi(conn);
        logger.info("promocode WEB SITE erreur: " + respenseMessage);
        return null;
      }
    } catch (MalformedURLException e) {
      conn.disconnect();
      logger.error("promocode WEB SITE MalformedURLException: " + e.getMessage());

      return returnResult;
    } catch (IOException e) {
      logger.error("promocode WEB SITE Exception: " + e.getMessage());
      conn.disconnect();
      return returnResult;
    }

  }

  public Boolean sendsmsToClient(Reclamation reclamation, String telephone) {
    ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
    Map<String, Object> Message = new HashMap<String, Object>();
    if (reclamation == null || reclamation.getStatus() == null || telephone == null) {
      return false;
    }
    String statut = reclamation.getStatus().getNomStatut();
    String ref = reclamation.getRef_reclamation();
    String sms = null;
    switch (statut) {

      case NomStatutReclamation.SAVED:
        sms = "Nety vous informe que votre réclamation " + ref + " a été enregistrée. "
            + "Nous restons à votre disposition pour toute information.";

        break;
      case NomStatutReclamation.IN_PROGRESS:
        sms = "Nety vous informe que votre réclamation " + ref
            + " est en cours de traitement. Merci pour votre patience et votre confiance.";
        break;
      case NomStatutReclamation.Clôturée:
        sms = "Nety a le plaisir de vous informer que votre réclamation " + ref
            + " est cloturée. Merci pour votre confiance et votre fidélité.";

        break;
      case NomStatutReclamation.OPENED:
        sms = "Nety vous informe que votre réclamation " + ref + " a été recue. "
            + "Nous traitons votre demande dans les meilleurs délais.";
        break;
      case NomStatutReclamation.RELANCEE:
        sms = "Nety vous informe que votre réclamation " + ref
            + " a été relancée. Nous faisons le nécessaire et restons à votre disposition.";
        break;


      default:
        return false;
    }
    Message.put("number", telephone);
    Message.put("message", sms);
    smsToSend.add(Message);
    if (smsToSend.size() > 0) {
      Boolean resultaSms = this.sendsmsnotification(smsToSend);
      return true;
    } else {
      return false;
    }

  }
}
