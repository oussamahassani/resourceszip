package crm.chifco.com.service;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.model.DemandeCommissionGroup;
import crm.chifco.com.model.User;

public interface DemandeCommissionService {

  HashMap<String, Object> getAll(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche);

  HashMap<String, Object> getAllByRev(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, User user);

  String demandeCommissionParRev(Long id, MultipartFile image, String commentaire, User user);

  String demandeCommissionMultiple(List<Long> commissionIds, MultipartFile image, String commentaire, User user);

  String validerCommissionMultiple(List<Long> commissionIds, String motif, User user);

  String validationCommission(String decision, MultipartFile image, String commentaire,
      String raison, Long id, User acceptedBy);

  public ModelAndView exportListDemandeCommissionToExcel(HttpServletRequest request,
      HttpServletResponse response);

  // Grouped demand methods
  String createDemandeGroup(List<Long> demandeIds, String commentaire, User user);

  String validateDemandeGroup(Long groupId, String decision, String motif, String commentaire, MultipartFile decisionFile, User user);

  HashMap<String, Object> getAllGroupedDemandes(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, User user);

  DemandeCommissionGroup getGroupedDemandeById(Long id);

  String uploadInvoiceForGroup(Long groupId, MultipartFile invoiceFile, User user);

  void generateAndDownloadFactureMultiple(Long groupId, HttpServletResponse response);

}
