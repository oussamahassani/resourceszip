package crm.chifco.com.service;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.model.TypeVisite;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Visite;

public interface VisiteService {
  Visite addVisite(Visite visite, User createdBy, Long revendeurId, TypeVisite typevisite);

  Page<Visite> findVisitesBychefsecteur(Long chefsecteurId, Long typevisiteid, Long editedBy,
      Long revendeurId, String startDate, String endDate, Pageable pageable);

  Visite editVisite(Visite visite, User user, Long revendeurId, Long visiteId, Long typevisiteid);

  Visite modifyStatus(String status, User user, Long visiteId);

  String generateReferenceVisite();

  HashMap<String, Object> getAllvisits(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche);

  Visite findVisiteById(Long id);

  ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      String reference, String typeVisite, String status, Long creepar, Long revendeur,
      String datedebut, String datefin);
}
