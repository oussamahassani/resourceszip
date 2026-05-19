package crm.chifco.com.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.lowagie.text.DocumentException;
import crm.chifco.com.model.Bordereau;
import crm.chifco.com.model.User;
import crm.chifco.com.templateclasse.ListeBordereau;
import net.sf.jasperreports.engine.JRException;

public interface BordereauService {

  Long BordereauCreation(String factureliste) throws IOException, DocumentException;

  HashMap<String, Object> listepathBordereau(int draw, int start, int length, String search,
      String filterrecherche);

  void getBordereau(Long id, Model model);

  void anulationPathBordereaux(Long idbordereau, String commentaire);

  void accpetBordereauByAdmin(Bordereau bordereau, User checkby, String commentaire,
      String dateVersement, String typedePayement, RedirectAttributes redirectAttrs);

  Bordereau findBordereauxById(Long idbordereau);

  Page<ListeBordereau> AdminHistoriqueBordereau(int i, int length, String numeroBordereau,
      String status, String userCode, String affecterTo, Date dateDebut, Date dateFin,Date datevalideDebut,Date datevalideFin, String sort,
      String orderdir);

  void addJustificatifBordereaux(MultipartFile imageFile2, Long idbordereau);

  Page<ListeBordereau> findPaginatedbordereauxRevendeur(int i, int length, User user, String statut,
      Long ville, Long governorate);

  File createPDFBordereauA4(Long id) throws JRException, IOException, DocumentException;

  String generateBordereauSequence(String codeRevendeur);

  Page<ListeBordereau> findBordereaubyDistributeur(int pageNo, int pageSize, String userId,
      String numeroBordereau, String userCode, String affecterTo, Date datedebut, Date dateFin,Date datevalideDebut,
      Date datevalideFin, String sort, String orderdir);
     

  Void deleteAvoirByBordereau(Long bordereauId, Long avoirId);

  String demandeAvanceBordereau(User user, Long idbordereau);

  Page<Map<String, Object>> bordereauListBychefsecteur(Boolean isEnInstance, Boolean isConfirmed,
      Long userid, Boolean isAnomalie, Boolean isJustificatif, Long revId, Pageable pageable);

}
