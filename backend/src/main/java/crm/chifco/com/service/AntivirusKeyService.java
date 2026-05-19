package crm.chifco.com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.model.AntivirusKey;
import crm.chifco.com.model.User;

public interface AntivirusKeyService {

  HashMap<String, Object> getAll(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche);

  public Map<String, List<AntivirusKey>> importerKey(MultipartFile file, User user);

  boolean changeEtat(Long id);

  HashMap<String, String> affecter(Long keyId, String referenceClient, User user, String type);

  ModelAndView exportListClé(HttpServletRequest request, HttpServletResponse response, String key,
      String referenceClient, Boolean etat, Boolean statut, Date startAffectedDate,
      Date endAffectedDate, Date startCreatedDate, Date endCreatedDate, String type);

  public List<String> getAllAntivirusTypes();
}
