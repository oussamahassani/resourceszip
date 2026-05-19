package crm.chifco.com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.DTOclass.RecapFactureLimitDTO;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.User;
import crm.chifco.com.templateclasse.EncaissementNonPayee;
import crm.chifco.com.templateclasse.RevendeurRecap;

public interface EncaissementService {

  Page<EncaissementNonPayee> findPaginatedadmin(int pageNo, int pageSize, Double prixmin,
      Double prixmax, String datedebut, String datefin, String ref_facture, String ref_avoir,
      String sortvar, String sorttype);

  Page<EncaissementNonPayee> findPaginatedRevendeur(int pageNo, int pageSize, Long userid,
      Double prixmin, Double prixmax, String datedebut, String datefin, String sortvar,
      String sorttype);

  List<Encaissement> findEncaismentByIdBordereau(Long idbordreau);

  Page<RevendeurRecap> findRecapeRevendeur(int pageNo, int pageSize, Long gouvernorat, Long villes,
      String nomUser, String prenomUser, String refUser, Boolean status, String datedebut,
      String datefin, Long distributeur);

  HashMap<String, Object> findTopRevendeurSearch(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche);

  List<Encaissement> findEncaissementByRevendeur(User user);



  List<Encaissement> findEncaissementPayedbyRevendeur(User user);



  Page<Encaissement> findListEncaissementNotPyedByRevendeur(int i, int length, User user);

  Page<Encaissement> findListEncaissementPyedByRevendeur(int i, int length, User user);

  Double countByUserAndIschifcopayed(User user);

  Page<RevendeurRecap> findRecapeRevendeurByDistributeur(int i, int length, Long userid,
      Long gouvernorat, Long villes, String nomUser, String prenomUser, String refUser,
      Boolean status);

  ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response, String nom,
      String prenom, String refUser, Boolean status, Long Gouvernorats, Long Villes,
      Long distributeur, String datedebut, String datefin);

  ModelAndView exportToExcelFactureEncaisse(HttpServletRequest request, Double prixMin,
      Double prixmax, String datedebut, String datefin, String ref_facture, String ref_avoir,
      HttpServletResponse response);

  public Map<String, Object> getAllFactureId(Long userId, String filterrecherche);

  Double countAvoirByUser(User user);

  Page<RecapFactureLimitDTO> getRevendeursWithSummaryForAdmin(int pageNo, int pageSize,
      Date CurrentDate, String filterrecherche);

}
