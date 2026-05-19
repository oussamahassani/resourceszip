package crm.chifco.com.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.ModemHistory;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ModemHistoryRepository;
import crm.chifco.com.service.ModemHistoryService;

@Service
public class ModemHistoryServiceImpl implements ModemHistoryService {

  @Autowired
  private ModemHistoryRepository repository;

  @Override
  public List<ModemHistory> listModemHistoryByModemId(Long idModem) {
    // TODO Auto-generated method stub
    return repository.findByModemId(idModem);
  }

  @Override
  public void save(String action, User user, Modem modem) {
    // TODO Auto-generated method stub

    ModemHistory modemHistory = new ModemHistory();
    modemHistory.setAction(action);
    modemHistory.setModem(modem);
    modemHistory.setUser(user);

    repository.save(modemHistory);
  }

  @Override
  public void editHistory(Modem oldModem, Modem newModem, User user) {
    // TODO Auto-generated method stub
    ModemHistory modemHistory = new ModemHistory();
    modemHistory.setAction("Modification : ");

    if (!oldModem.getModelModem().equals(newModem.getModelModem())) {
      modemHistory.setAction(modemHistory.getAction() + ", modelModem changé de "
          + oldModem.getModelModem() + " à " + newModem.getModelModem());
    }
    if (!oldModem.getNumSerie().equals(newModem.getNumSerie())) {
      modemHistory.setAction(modemHistory.getAction() + ", numSerie changé de "
          + oldModem.getNumSerie() + " à " + newModem.getNumSerie());
    }
    if (!oldModem.getMarque().equals(newModem.getMarque())) {
      modemHistory.setAction(modemHistory.getAction() + ", marque changé de " + oldModem.getMarque()
          + " à " + newModem.getMarque());
    }
    if (!oldModem.getEmail().equals(newModem.getEmail())) {
      modemHistory.setAction(modemHistory.getAction() + ", email changé de " + oldModem.getEmail()
          + " à " + newModem.getEmail());
    }
    if (!oldModem.getPassword().equals(newModem.getPassword())) {
      modemHistory.setAction(modemHistory.getAction() + ", password changé de "
          + oldModem.getPassword() + " à " + newModem.getPassword());
    }

    modemHistory.setModem(newModem);
    modemHistory.setUser(user);
    repository.save(modemHistory);
  }

}
