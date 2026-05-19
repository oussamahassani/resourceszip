package crm.chifco.com.service;

import crm.chifco.com.model.RecuNumeroSequence;
import crm.chifco.com.model.User;

public interface RecuNumeroSequenceService {

  public String generateCode(User user);


  public RecuNumeroSequence save(RecuNumeroSequence recuNumeroSequence);

}
