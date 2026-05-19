package crm.chifco.com.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.DTOclass.RecapFactureLimitDTO;
import crm.chifco.com.DTOclass.TopRevendeur;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.EncaissementService;
import crm.chifco.com.service.FactureEncaissesExcelExport;
import crm.chifco.com.service.RecapExcelExport;
import crm.chifco.com.templateclasse.EncaissementNonPayee;
import crm.chifco.com.templateclasse.RevendeurRecap;
import crm.chifco.com.utils.CrmUtils;

@Service
@Transactional
public class EncaissementServiceimpl implements EncaissementService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  private Double Totalcommisionrevendeur = 0.0;
  @Autowired
  private EncaissementRepository encaismentRepo;

  @Autowired
  UserRepository userRepository;

  @Override
  public Page<EncaissementNonPayee> findPaginatedadmin(int pageNo, int pageSize, Double prixmin,
      Double prixmax, String datedebut, String datefin, String ref_facture, String ref_avoir,
      String sortvar, String sorttype) {
    Sort sort = Sort.by("created_date");
    if (sortvar != null) {
      if (sorttype.equals("desc")) {
        sort = Sort.by(sortvar).descending();
      } else if (!sorttype.equals("desc")) {
        sort = Sort.by(sortvar).ascending();
      }
    }
    if (prixmax == 0) {
      prixmax = Double.MAX_VALUE;
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.encaismentRepo.findAllAdmin(pageable, prixmin, prixmax, datedebut, datefin,
        ref_facture, ref_avoir);
  }

  @Override
  public Page<EncaissementNonPayee> findPaginatedRevendeur(int pageNo, int pageSize, Long userid,
      Double prixmin, Double prixmax, String datedebut, String datefin, String sortvar,
      String sorttype) {
    Sort sort = Sort.by("modifieddate");

    if (sortvar != null) {
      if (sorttype.equals("desc")) {
        sort = Sort.by(sortvar).descending();
      } else if (!sorttype.equals("desc")) {
        sort = Sort.by(sortvar).ascending();
      }
    }

    if (prixmax == 0) {
      prixmax = Double.MAX_VALUE;
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.encaismentRepo.findbyuserAndHasbordereauAndfilter(pageable, userid, prixmin,
        prixmax, datedebut, datefin);
  }

  @Override
  public List<Encaissement> findEncaismentByIdBordereau(Long idbordreau) {
    return this.encaismentRepo.findByidbordaureau_bordereauId(idbordreau);
  }

  @Override
  public Page<RevendeurRecap> findRecapeRevendeur(int pageNo, int pageSize, Long gouvernorat,
      Long villes, String nomUser, String prenomUser, String refUser, Boolean statusEnabled,
      String datedebut, String datefin, Long distributeur) {
    Sort sort = Sort.by("userid");
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.encaismentRepo.findByusernotpayedgrouby(pageable, gouvernorat, villes, nomUser,
        prenomUser, refUser, distributeur, statusEnabled);

  }

  @Override
  public List<Encaissement> findEncaissementByRevendeur(User user) {
    return this.encaismentRepo.findlastencaissementbyRevendeur(user);
  }



  @Override
  public List<Encaissement> findEncaissementPayedbyRevendeur(User user) {

    return this.encaismentRepo.findEncaissementByUserAndIsChifcoPayed(user, true);
  }

  @Override
  public Double countByUserAndIschifcopayed(User user) {

    return this.encaismentRepo.sumFactureByUserAndIschifcopayed(user.getUserid(), false);
  }


  @Override
  public Page<Encaissement> findListEncaissementNotPyedByRevendeur(int i, int length, User user) {
    // TODO Auto-generated method stub
    Sort sort = Sort.by("date").ascending();
    Pageable pageable = PageRequest.of(i - 1, length, sort);

    return this.encaismentRepo.findEncaissementByUserAndIsChifcoPayed(pageable, user, false);

  }

  @Override
  public Page<Encaissement> findListEncaissementPyedByRevendeur(int i, int length, User user) {
    // TODO Auto-generated method stub
    Pageable pageable = PageRequest.of(i - 1, length);
    return this.encaismentRepo.findEncaissementByUserAndIsChifcoPayed(pageable, user, true);

  }

  @Override
  public Page<RevendeurRecap> findRecapeRevendeurByDistributeur(int i, int length, Long userid,
      Long gouvernorat, Long villes, String nomUser, String prenomUser, String refUser,
      Boolean statusEnabled) {
    // TODO Auto-generated method stub
    Sort sort = Sort.by("userid");
    Pageable pageable = PageRequest.of(i - 1, length, sort);
    return this.encaismentRepo.findByusernotpayedgroubyDistributeur(pageable, userid, gouvernorat,
        villes, nomUser, prenomUser, refUser, statusEnabled);
  }

  @Override
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      String nom, String prenom, String refUser, Boolean status, Long Gouvernorats, Long Villes,
      Long distributeur, String datedebut, String datefin) {
    // TODO Auto-generated method stub
    ModelAndView mav = new ModelAndView();
    String firstName = null;
    String lastName = null;
    String referenceUser = null;

    if (!nom.isEmpty()) {
      firstName = nom;
    }
    if (!prenom.isEmpty()) {
      lastName = prenom;
    }
    if (!refUser.isEmpty()) {
      referenceUser = refUser;
    }
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
        List<RevendeurRecap> myList = new ArrayList<>();
        if (StringsRole.contains("READ_RETAIL_SUMMARY_LIST_ALL")) {
          myList = encaismentRepo.exportRecapeRevendeur(Gouvernorats, Villes, firstName, lastName,
              referenceUser, distributeur, status);

        }
        if (StringsRole.contains("READ_RETAIL_SUMMARY_LIST_AREA")) {
          myList = encaismentRepo.exportRecapeRevendeurbyDistributeur(user.getUserid(),
              Gouvernorats, Villes, firstName, lastName, firstName, status);
        }
        if (myList.size() > 0) {
          mav.setView(new RecapExcelExport());
          mav.addObject("list", myList);
        } else {
          mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR); // Set the desired HTTP status code
          mav.addObject("errorMessage", "No data found");
          // Add an error message

          request.getRequestDispatcher("/payement/viewlisterecaperevendeur").forward(request,
              response);
          return null;
          // mav.setViewName("payement/recaprevendeur"); // Set the view name for the error

        }
      }
    } catch (Exception e) {
      LOGGER.error(" demandeabonnement.affectationClientt Error:" + e);

    }
    return mav;
  }

  @Override
  public ModelAndView exportToExcelFactureEncaisse(HttpServletRequest request, Double prixmin,
      Double prixmax, String datedebut, String datefin, String ref_facture, String ref_avoir,
      HttpServletResponse response) {
    // TODO Auto-generated method stub

    ModelAndView mav = new ModelAndView();
    String DateDebut = null;
    String DateFin = null;
    String RefFacture = null;
    String RefAvoir = null;
    if (!datedebut.isEmpty()) {
      DateDebut = datedebut;
    }
    if (!datefin.isEmpty()) {
      DateFin = datefin;
    }
    Double prixMax = Double.MAX_VALUE;
    Double prixMin = 0.0;
    if (prixmin != null) {
      prixMin = prixmin;
    }
    if (prixmax != null) {
      prixMax = prixmax;
    }
    if (ref_facture != null && !ref_facture.equals("")) {
      RefFacture = ref_facture;
    }

    if (ref_avoir != null && !ref_avoir.equals("")) {
      RefAvoir = ref_avoir;
    }

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
        List<EncaissementNonPayee> myList = new ArrayList<>();
        if (StringsRole.contains("READ_PAYED_INVOICE_ALL")) {
          myList = encaismentRepo.exportAllEncaisseentAdmin(prixMin, prixMax, DateDebut, DateFin,
              RefFacture, RefAvoir);

        }
        if (StringsRole.contains("CREATE_SLIP")) {
          myList = encaismentRepo.exportEncaisseentbyUserAndHasbordereauAndfilter(user.getUserid(),
              prixMin, prixMax, DateDebut, DateFin);
        }
        if (myList.size() > 0) {
          mav.setView(new FactureEncaissesExcelExport());
          mav.addObject("list", myList);
          boolean isAdmin = StringsRole.contains("READ_PAYED_INVOICE_ALL");
          mav.addObject("isAdmin", isAdmin);
        } else {
          request.getRequestDispatcher("/payement/viewlistepayement").forward(request, response);
          return null;
        }
      }
    } catch (Exception e) {
      LOGGER.error(" demandeabonnement.affectationClientt Error:" + e);

    }
    return mav;
  }

  @Override
  public Map<String, Object> getAllFactureId(Long userId, String filterrecherche) {
    // TODO Auto-generated method stub

    Double prixmin = 0.0;
    Double prixmax = Double.MAX_VALUE;;
    String datedebut = null;
    String datefin = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("prixmin"), "") && obj.getString("prixmin") != null) {
        prixmin = obj.getDouble("prixmin");
      }
      if (!Objects.equals(obj.getString("prixmax"), "") && obj.getString("prixmax") != null) {
        prixmax = obj.getDouble("prixmax");

      }
      if (!Objects.equals(obj.getString("datedebut"), "") && obj.getString("datedebut") != null) {
        datedebut = obj.getString("datedebut").trim() + " 00:00:00.000";

      }
      if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
        datefin = obj.getString("datefin").trim() + " 23:59:59.999";

      }
    }
    List<Long> ids = encaismentRepo.getAllIds(userId, prixmin, prixmax, datedebut, datefin);
    double sommeMontantFacture = 0.0;
    if (ids.size() > 0)
      sommeMontantFacture =
          encaismentRepo.getSumMontant(userId, prixmin, prixmax, datedebut, datefin);

    Map<String, Object> resultat = new HashMap<>();
    resultat.put("ids", ids);
    resultat.put("sommeMontantFacture", sommeMontantFacture);

    return resultat;
  }

  @Override
  public Double countAvoirByUser(User user) {
    // TODO Auto-generated method stub
    return encaismentRepo.countAvoirByUser(user.getUserid());
  }

  @Override
  public Page<RecapFactureLimitDTO> getRevendeursWithSummaryForAdmin(int pageNo, int pageSize,
      Date CURRENT_DATE, String filterrecherche) {
    String statusFilt = "";
    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("status"), "") && obj.getString("status") != null) {
        statusFilt = obj.getString("status");
      }

    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    Integer y = null, x = null, z = null, m = null;
    if (statusFilt.equals("Hors échéance")) {
      y = 0;
    } else if (statusFilt.equals("Toutes versées")) {
      z = 0;
    } else if (statusFilt.equals("Limite")) {
      x = 0;
      m = 0;
    }
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    if ((user.getTypeUser()).equals("DISTRIBUTEUR")) {
      Long idDist = user.getUserid();
      Page<RecapFactureLimitDTO> ddd = encaismentRepo.getRevendeursWithSummaryForAdmin(idDist,
          CURRENT_DATE, x, y, z, m, pageable);
      return ddd;
    } else {
      Page<RecapFactureLimitDTO> ddd =
          encaismentRepo.getRevendeursWithSummaryForAdmin(null, CURRENT_DATE, x, y, z, m, pageable);
      return ddd;
    }


  }

  @Override
  public HashMap<String, Object> findTopRevendeurSearch(int draw, int start, int length,
      String search, int ordercolumnaram, String orderdir, String filterrecherche) {
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    int currentpage = start / length;
    Page<TopRevendeur> responseData = null;
    String selectedDate = null;
    Long gouvernorat = null;
    Long distributeur = null;
    LocalDate startOfMonth = null;
    LocalDate endOfMonth = null;
    String orderBy = null;
    if (filterrecherche != null && filterrecherche != "") {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("gouvernorat"), "")
          && obj.getString("gouvernorat") != null) {
        gouvernorat = obj.getLong("gouvernorat");
      }
      if (!Objects.equals(obj.getString("distributeur"), "")
          && obj.getString("distributeur") != null) {
        distributeur = obj.getLong("distributeur");
      }
      if (!Objects.equals(obj.getString("selectedDate"), "")
          && obj.getString("selectedDate") != null) {
        selectedDate = obj.getString("selectedDate");
      }
      if (!Objects.equals(obj.getString("orderBy"), "") && obj.getString("orderBy") != null) {
        orderBy = obj.getString("orderBy");
      }
    }
    YearMonth yearMonth = null;
    String sort = "";

    switch (ordercolumnaram) {

      case 1:
        sort = "chiffre_affaire";
        break;
      case 2:
        sort = "countAllDemandeAccepted";

        break;

      default:
        sort = "chiffre_affaire";
    }
    if (selectedDate != null) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
      yearMonth = YearMonth.parse(selectedDate, formatter);
      startOfMonth = yearMonth.atDay(1);
      endOfMonth = yearMonth.atEndOfMonth();
      Date startD = CrmUtils.convertStringToDate(startOfMonth.toString());
      Date endD = CrmUtils.convertStringToDate(endOfMonth.toString());

      responseData = this.toprevendeurAdmin(currentpage + 1, length, gouvernorat, startD, endD,
          distributeur, orderBy, sort, orderdir);
    } else {
      responseData = this.toprevendeurAdmin(currentpage + 1, length, gouvernorat, null, null,
          distributeur, orderBy, sort, orderdir);
    }


    if (responseData.getContent() != null) {
      myGreetings.put("data", responseData.getContent());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    return myGreetings;
  }

  private Page<TopRevendeur> toprevendeurAdmin(int pageNo, int pageSize, Long gouvernorat,
      Date startD, Date endD, Long distributeur, String orderBy, String sortvar, String sorttype) {

    Sort sort;
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    if ("desc".equals(sorttype)) {
      sort = Sort.by(sortvar).descending();
    } else {
      sort = Sort.by(sortvar).ascending();
    }
    if (orderBy == null || orderBy.equals("chiffre_affaire")) {
      orderBy = "chiffre_affaire";

      return encaismentRepo.findTopRevByChiffreAffairAndRealDemand(pageable, gouvernorat, startD,
          endD, distributeur);
    } else {
      return encaismentRepo.findTopRevByChiffreAffairAndRealDemand2(pageable, gouvernorat, startD,
          endD, distributeur);
    }

  }


}
