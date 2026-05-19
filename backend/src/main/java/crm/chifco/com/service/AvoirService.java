package crm.chifco.com.service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.ApiDTO.entryAvoirClient;
import crm.chifco.com.model.AvoirClient;

public interface AvoirService {

  HashMap<String, Object> getAllAvoir(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche ,Boolean isPublich);

  ModelAndView exportListAvoirToExcel(Date startDate, Date endDate, String reference, Long usedBy,
      Boolean avoirStatut, Boolean authorizationAdd, Long createdBy, Double montantAvoir,
      String motifAvoir, String abonnement, Date DateDebutPayement, Date DateFinPayement,
      String isNotPublic ,
      HttpServletRequest request, HttpServletResponse response);

  List<AvoirClient> getAllAvoirByClient(Long clientid);

  List<AvoirClient> findnonpayerfacture(String recherche, Long telephone);

  void downloadAvoir(Long avoirId, HttpServletResponse response);

  File createPDFAvoirA4(AvoirClient monAvoir);

  public Map<String, Object> ajouterAvoir(Long clientId, String motifAvoir,
      List<entryAvoirClient> entryAvoirClient, String codeUser, String isClientPayed,

      String isUsedBrd, String hasRaccordement, String commentaireAvoir , 
    Boolean  typeAVr ,String RefReclamation,List<String> RefFacture ,String dateDebutCoupur,String dateFinCoupur,
    String dateMiseService,String validePar);



  HashMap<String, Object> EditAvoirToPayed(Long avoirId, String codeUser, String isClientPayed,
      String isUsedBrd);


  List<AvoirClient> findnonpayerfactureAvoirNotgreatherThenFacture(String recherche, Long telephone,
      Double montantTotalFacture);

void avoirPublish(Long avoirId);





HashMap<String, Object> verifMontantAndFactureExiste(String montant, List<String> referenceFacture, String referenceReclamation,
		boolean b);

void avoirRefused(Long avoirId , String commentaire);

 void updateAvoirClientNotPublic(AvoirClient dto, List<String> refFacture , String dateMiseService ,String datePayementDebut ,String datePayementFin);


}
