package crm.chifco.com.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import crm.chifco.com.model.ModemAccess;
import crm.chifco.com.model.User;

public interface AcessModemService {
  Page<ModemAccess> findPaginated(int pageNo, int pageSize);



  ModemAccess findStatutByNomstatut(Boolean valid);



  Map<String, List<ModemAccess>> importerModemfromExcel(MultipartFile file, User user);



}
