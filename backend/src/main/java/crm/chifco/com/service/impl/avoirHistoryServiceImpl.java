package crm.chifco.com.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.DemandeAbonnementHistory;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AvoirHistoryRepository;
import crm.chifco.com.repository.DemandeAbonnementHistoryRepository;
import crm.chifco.com.model.AvoirHistory;
import crm.chifco.com.service.AvoirHistoryService;

@Service("AvoirHistoryService")
public class avoirHistoryServiceImpl implements AvoirHistoryService {
	
	  @Autowired
	  AvoirHistoryRepository avoirHistoryRepository;
	  
	  @Override
	  public void saveHistoryToDataBase(User createdBy, Long avoidId,
	      String description) {
		  AvoirHistory avoirHistory = new AvoirHistory();
		  avoirHistory.setAvoirId(avoidId);
		  
		  avoirHistory.setDescription(description);
		
		  avoirHistory.setCreatedBy(createdBy);
	    if (!description.equals(""))
	    	avoirHistoryRepository.save(avoirHistory);
	  }
 
}
