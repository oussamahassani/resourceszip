package crm.chifco.com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.OffreCommissionPromo;
import crm.chifco.com.model.User;

public interface CommissionPromoService {

  HashMap<String, Object> getAll(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche);

  HashMap<String, Object> getAllByRevendeurId(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, Long idRevendeur);

  Optional<Commission> getDetailsCommission(Long id);

  HashMap<String, Object> CalculeCommisionFront(long UserId, Long IdOffrePromo);

  Boolean findExiteCommision(User user, Integer annee, Integer numMois);

  public ModelAndView exportListCommissionToExcel(Integer annee, Integer numMois, String statut,
      String codeRevendeur, Date startCreatedDate, Date endCreatedDate, String reference,
      HttpServletRequest request, HttpServletResponse response);

  List<Commission> finAllByUserID(Long id);

  String annulerCommission(Long idCommission, User userConnected);

  OffreCommissionPromo getOffreCommissionPromoByIdAndIsActive(Long idOffrePromo);

  OffreCommissionPromo getOffreCommissionPromoById(Long idOffrePromo);

  String changementEtatOffre(Long idOffre, User user);

  String ajouterOffreCommission(String namepromo, String datedebut, String datefin,
      Double montantdemande1, Double montantactivation1, Double montantpayement1,
      Double montantdemande2, Double montantactivation2, Double montantpayement2,
      Double montantdemande3, Double montantactivation3, Double montantpayement3,
      Double montantdemande4, Double montantactivation4, Double montantpayement4,
      Double montantdemande5, Double montantactivation5, Double montantpayement5, User user);

  List<OffreCommissionPromo> getALLactiveCommision(boolean b);

}
