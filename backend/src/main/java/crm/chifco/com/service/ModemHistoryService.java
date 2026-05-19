package crm.chifco.com.service;

import java.util.List;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.ModemHistory;
import crm.chifco.com.model.User;

public interface ModemHistoryService {

  List<ModemHistory> listModemHistoryByModemId(Long idModem);

  void save(String action, User user, Modem modem);

  void editHistory(Modem oldModem, Modem newModem, User user);

}
