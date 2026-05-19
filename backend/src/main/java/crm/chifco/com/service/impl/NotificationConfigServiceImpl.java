package crm.chifco.com.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.EmailAdresse;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.NotificationConfig;
import crm.chifco.com.model.PhoneNumbers;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.EmailAdresseNotificationRepository;
import crm.chifco.com.repository.NotificationConfigRepository;
import crm.chifco.com.repository.PhoneNumbersNotificationRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.NotificationConfigService;

@Service
@Transactional
public class NotificationConfigServiceImpl implements NotificationConfigService {

  @Autowired
  private NotificationConfigRepository notificationConfigRepository;

  @Autowired
  private EmailAdresseNotificationRepository emailAdresseNotificationRepository;

  @Autowired
  private PhoneNumbersNotificationRepository phoneNumbersNotificationRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public Page<NotificationConfig> findPaginatedadmin(int pageNo, int pageSize, String orderdir) {
    // TODO Auto-generated method stub
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.notificationConfigRepository.findAll(pageable);
  }

  @Override
  public void addNewConfigNotifiation(String actionNotification, String listeEmail,
      String listePhone, Gouvernorat ville) {
    // TODO Auto-generated method stub
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<EmailAdresse> EmailAdresse = new ArrayList<EmailAdresse>();
    List<PhoneNumbers> PhoneNumbers = new ArrayList<PhoneNumbers>();
    String[] ListeEmail = listeEmail.split(",");
    String[] ListePhone = listePhone.split(",");

    for (int i = 0; i < ListeEmail.length; ++i) {
      EmailAdresse emailAdresse = new EmailAdresse();
      emailAdresse.setEmailAdresse(ListeEmail[i]);
      EmailAdresse.add(emailAdresse);
      emailAdresseNotificationRepository.save(emailAdresse);
    }
    for (int i = 0; i < ListePhone.length; ++i) {
      PhoneNumbers phoneNumbers = new PhoneNumbers();
      phoneNumbers.setPhoneNumbers(ListePhone[i]);
      PhoneNumbers.add(phoneNumbers);
      phoneNumbersNotificationRepository.save(phoneNumbers);
    }

    NotificationConfig notificationConfig = new NotificationConfig();
    notificationConfig.setEmailAdresse(EmailAdresse);
    notificationConfig.setPhoneNumbers(PhoneNumbers);
    notificationConfig.setVilleName(ville.getGouvernoratName());
    notificationConfig.setCreatedByUserId(user);
    notificationConfig.setEventAction(actionNotification);
    notificationConfigRepository.save(notificationConfig);
  }

  @Override
  public HashMap<String, Object> getNotificationConfig(int draw, int start, int length,
      String search, int ordercolumnaram, String orderdir, String filterrecherche) {
    int page = start / length;
    Page<NotificationConfig> responseData = null;
    responseData = this.findPaginatedadmin(page + 1, length, orderdir);

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    return myGreetings;
  }

  @Override
  public NotificationConfig getOneNotificationConfigById(Long configid) {
    // TODO Auto-generated method stub
    return this.notificationConfigRepository.getBynotificationConfigId(configid);
  }

  @Override
  public String Updatedemandeabonnement(Long configid, String actionNotification, Gouvernorat ville,
      String listeEmail, String listetélephone, Model model, RedirectAttributes redirectAttrs) {
    // TODO Auto-generated method stub
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    NotificationConfig oneNotificationConfig =
        this.notificationConfigRepository.getBynotificationConfigId(configid);
    List<EmailAdresse> EmailAdresse = new ArrayList<EmailAdresse>();
    List<PhoneNumbers> PhoneNumbers = new ArrayList<PhoneNumbers>();
    String[] ListeEmail = listeEmail.split(",");
    String[] ListePhone = listetélephone.split(",");

    for (int i = 0; i < ListeEmail.length; ++i) {
      EmailAdresse findemailadress =
          emailAdresseNotificationRepository.getByNotificaionidAndEmailAdresse(
              oneNotificationConfig.getNotificationConfigId(), ListeEmail[i]);
      if (findemailadress == null) {
        EmailAdresse emailAdresse = new EmailAdresse();
        emailAdresse.setEmailAdresse(ListeEmail[i]);

        emailAdresseNotificationRepository.save(emailAdresse);

        EmailAdresse.add(emailAdresse);
      } else {
        EmailAdresse.add(findemailadress);
      }
    }
    for (int i = 0; i < ListePhone.length; ++i) {
      PhoneNumbers findPhoneNumbers =
          phoneNumbersNotificationRepository.findByNotificaionidAndPhoneNumbers(
              oneNotificationConfig.getNotificationConfigId(), ListePhone[i]);
      if (findPhoneNumbers == null) {
        PhoneNumbers phoneNumbers = new PhoneNumbers();
        phoneNumbers.setPhoneNumbers(ListePhone[i]);

        phoneNumbersNotificationRepository.save(phoneNumbers);

        PhoneNumbers.add(phoneNumbers);
      } else {
        PhoneNumbers.add(findPhoneNumbers);
      }
    }
    oneNotificationConfig.setVilleName(ville.getGouvernoratName());
    oneNotificationConfig.setCreatedByUserId(user);
    oneNotificationConfig.setEventAction(actionNotification);
    oneNotificationConfig.setEmailAdresse(EmailAdresse);
    oneNotificationConfig.setPhoneNumbers(PhoneNumbers);
    notificationConfigRepository.save(oneNotificationConfig);
    return "redirect:/NotificationConfig/getListConfigNotif";
  }

  @Override
  public String removePhoneNumber(Long configid, String phone) {
    // TODO Auto-generated method stub
    phoneNumbersNotificationRepository.deletebyconfigIdandPhone(configid, phone);
    return "Phone Number deleted";
  }

  @Override
  public String removeEmailAdress(Long configid, String email) {
    emailAdresseNotificationRepository.deletebyconfigIdandEmail(configid, email);
    // TODO Auto-generated method stub
    return "EmailAdress deleted";
  }

}
