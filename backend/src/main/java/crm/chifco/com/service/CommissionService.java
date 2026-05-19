package crm.chifco.com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.DTOclass.CommissionDemDash;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.DemandeCommission;
import crm.chifco.com.model.User;

public interface CommissionService {

  HashMap<String, Object> getAll(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, Boolean isCommissionAdmin,
      Boolean isCommissionArea, Long idUserConnected);

  HashMap<String, Object> getAllByRevendeurId(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, Long idRevendeur);

  Optional<Commission> getDetailsCommission(Long id);

  HashMap<String, Object> CalculeCommisionFront(Boolean isfreelance,long UserId, String Date);

  Boolean findExiteCommision(User user, Integer annee, Integer numMois);

  Boolean findExiteCommisionPromo(User user, Date dateDebut, Date dateFin);

  public ModelAndView exportListCommissionToExcel(Integer annee, Integer numMois, String statut,
      String codeRevendeur, Date startCreatedDate, Date endCreatedDate, String reference,Boolean typeCommision ,
      HttpServletRequest request, HttpServletResponse response);

  List<Commission> finAllByUserID(Long id);

  String annulerCommission(Long idCommission, User userConnected);

  String ajouterOffreCommission(String date, String type, Double montant, Integer debit,
      Integer palierMin, Integer palierMax, User user);

  String changementEtatOffre(Long idOffre, User user);


  Page<CommissionDemDash> AllCommissionDashboard(int pageNo, int pageSize, String filterrecherche);

HashMap<String, Object> CalculeCommisionFrontFreelancer(Boolean isfreelancer,Long userid, String date);

List<DemandeCommission> finAllByFilterForFactureMultiple(Integer dateFacts , Integer month ,  String codeRevendeurFacts ,Date startCreatedDateFacts , Date endCreatedDateFacts , String referenceFacts );

String changeCommissionToAwaiting(Long id, User user);

String changeCommissionGroupToAwaiting(Long commissionId, User user);

}
