package crm.chifco.com.service;

import crm.chifco.com.model.User;

public interface AvoirHistoryService {

	 void saveHistoryToDataBase(User createdBy, Long avoidId,
		      String description);
}
