package crm.chifco.com.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import crm.chifco.com.ApiDTO.getLoginAndPassswordModem;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.User;
import crm.chifco.com.templateclasse.AdminModem;
import crm.chifco.com.templateclasse.ModemDistributeur;
import crm.chifco.com.templateclasse.ModemEtatStockDist;
import crm.chifco.com.templateclasse.ModemEtatStockRev;
import crm.chifco.com.templateclasse.ModemRevendeur;

public interface ModemService {

  Page<AdminModem> findPaginatedModemAdmin(int pageNo, int pageSize, String filterrecherche);

  Page<ModemRevendeur> modemPosFindPaginated(Long idpos, int pageNo, int pageSize,
      String filterrecherche);

  Page<ModemDistributeur> modemDistFindPaginated(Long iddist, int pageNo, int pageSize,
      String filterrecherche);

  Page<ModemRevendeur> modemRevFindPaginated(Long idrev, int pageNo, int pageSize,
      String filterrecherche);

  Modem getmodemById(Long id) throws Exception;

  Boolean createOrUpdatemodem(Modem modem, User user);

  List<String> listNumSerie(List<Modem> modems);

  List<Long> getAllModemIds(String filterrecherche, List<String> StringsRole, User user);

  List<String> getAllCodeUserStock(String filterrecherche, String type, User userConnected);

  Page<ModemEtatStockDist> etatStockDist(int pageNo, int pageSize, String filterrecherche);

  Page<ModemEtatStockRev> etatStockRev(int pageNo, int pageSize, List<String> StringsRole,
      Long idConnected, String filterrecherche);

  Map<String, List<Modem>> getDetailsStockDist(Long idUser);

  Map<String, List<Modem>> getDetailsStockRev(Long idUser);

  getLoginAndPassswordModem getLoginAndPassswordModem(String numSerie);

  List<Modem> getListeModemsDisponiblesByUser(String codeProduit, User user);

  String controleParental(String telephoneFix, boolean activer);

  String changerStatus(Long modemId, User user, String newEmail, String newPassword,
      Boolean keepCredentials, String commentaire);

  List<Modem> getAllModemsAvailableByType(String type);

}
