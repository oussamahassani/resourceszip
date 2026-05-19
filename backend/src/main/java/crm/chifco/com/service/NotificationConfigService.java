package crm.chifco.com.service;

import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import crm.chifco.com.model.NotificationConfig;
import crm.chifco.com.model.Gouvernorat;

public interface NotificationConfigService {
  Page<NotificationConfig> findPaginatedadmin(int pageNo, int pageSize, String orderdir);

  void addNewConfigNotifiation(String actionNotification, String listeEmail, String listetélephone,
      Gouvernorat ville);

  HashMap<String, Object> getNotificationConfig(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche);

  NotificationConfig getOneNotificationConfigById(Long configid);

  String Updatedemandeabonnement(Long configid, String actionNotification, Gouvernorat ville,
      String listeEmail, String listetélephone, Model model, RedirectAttributes redirectAttrs);

  String removePhoneNumber(Long configid, String phone);

  String removeEmailAdress(Long configid, String email);
}
