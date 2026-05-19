package crm.chifco.com.service;

import java.util.List;
import crm.chifco.com.model.User;
import crm.chifco.com.model.UserHistory;

public interface UserHistoryService {

  void addHistoryEntry(Long userEdit, String action, User editBy);

  List<UserHistory> getHistoryByUser(Long userEdit);

  void checkAndSaveHistory(User oldUser, User newUser, User editBy);

}
