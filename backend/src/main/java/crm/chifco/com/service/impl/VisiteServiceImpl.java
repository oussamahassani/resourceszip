package crm.chifco.com.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.crmMobile.NomStatusVisite;
import crm.chifco.com.model.TypeVisite;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Visite;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.repository.VisiteRepository;
import crm.chifco.com.service.TypeVisiteService;
import crm.chifco.com.service.VisitExcelExport;
import crm.chifco.com.service.VisiteService;
import crm.chifco.com.utils.CrmUtils;


@Service("VisiteService")
public class VisiteServiceImpl implements VisiteService {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  VisiteRepository visiteRepository;
  @Autowired
  TypeVisiteService typeVisiteService;
  @Autowired
  UserRepository userRepository;
  @Value("${app.datasource.crm.url}")
  private String url;
  @Value("${app.datasource.crm.username}")
  private String login;
  @Value("${app.datasource.crm.password}")
  private String password;

  @Override
  public Visite addVisite(Visite vistenew, User createdBy, Long revendeurId,
      TypeVisite typevisite) {
    String reference = this.generateReferenceVisite();
    vistenew.setReference_visite(reference);
    vistenew.setTypeVisite(typevisite);
    vistenew.setCreatedBy(createdBy);
    vistenew.setEditedBy(null);
    vistenew.setRevndeurId(revendeurId);
    vistenew.setStatus(NomStatusVisite.OPENED);
    return visiteRepository.save(vistenew);
  }

  @Override
  public Page<Visite> findVisitesBychefsecteur(Long chefsecteurId, Long typevisiteid, Long editedBy,
      Long revendeurId, String startDateString, String endDateString, Pageable pageable) {
    Date startDate = null;
    Date endDate = null;
    if (startDateString != null && startDateString != "") {
      startDate = convertStringToDate(startDateString);
    }
    if (endDateString != null && endDateString != "") {
      endDate = convertStringToEndOfDay(endDateString);
    }
    return visiteRepository.findBychefsecteurCreatedDateDesc(chefsecteurId, typevisiteid, editedBy,
        revendeurId, startDate, endDate, pageable);
  }

  @Override
  public Visite editVisite(Visite visite, User user, Long revendeurId, Long visiteId,
      Long typevisiteid) {
    if (visiteRepository.existsById(visiteId)) {
      Visite oldVisite = visiteRepository.findById(visiteId).get();
      oldVisite.setEditedBy(user);
      TypeVisite TypeVisitenew = typeVisiteService.findById(typevisiteid);
      if (visite.getRevendeur() != null) {
        oldVisite.setRevendeur(visite.getRevendeur());
      }
      if (visite.getStatus() != null) {
        oldVisite.setStatus(visite.getStatus());
      }
      if (visite.getCommentaire() != null) {
        oldVisite.setCommentaire(visite.getCommentaire());
      }
      if (visite.getAutreLieu() != null) {
        oldVisite.setAutreLieu(visite.getAutreLieu());
      }
      if (visite.getAutreType() != null) {
        oldVisite.setAutreType(visite.getAutreType());
      }

      oldVisite.setTypeVisite(TypeVisitenew);

      if (visite.getDureeVisiteHeures() != 0) {
        oldVisite.setDureeVisiteHeures(visite.getDureeVisiteHeures());
      }
      if (visite.getDureeVisiteMinutes() != 0) {
        oldVisite.setDureeVisiteMinutes(visite.getDureeVisiteMinutes());
      }
      if (revendeurId != null) {
        oldVisite.setRevndeurId(revendeurId);
      }
      if (visite.getLatitude() != null) {
        oldVisite.setLatitude(visite.getLatitude());
      }
      if (visite.getLongitude() != null) {
        oldVisite.setLongitude(visite.getLongitude());
      }
      visiteRepository.save(oldVisite);
      return oldVisite;
    } else {
      return null;
    }
  }

  @Override
  public Visite modifyStatus(String status, User user, Long visiteId) {
    if (visiteRepository.existsById(visiteId)) {
      Visite oldVisite = visiteRepository.findById(visiteId).get();
      oldVisite.setEditedBy(user);

      if (status != null) {
        oldVisite.setStatus(status);
      }
      if (user != null) {
        oldVisite.setEditedBy(user);
      }
      visiteRepository.save(oldVisite);
      return oldVisite;
    } else {
      return null;
    }
  }

  public static Date convertStringToDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate localDate = LocalDate.parse(date, formatter);
    ZoneId defaultZoneId = ZoneId.systemDefault();
    return Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
  }

  public static Date convertStringToEndOfDay(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate localDate = LocalDate.parse(date, formatter);
    ZoneId defaultZoneId = ZoneId.systemDefault();
    return Date.from(localDate.atTime(23, 59, 59).atZone(defaultZoneId).toInstant());
  }

  public String generateReferenceVisite() {
    try {
      Date date = new Date();
      SimpleDateFormat df = new SimpleDateFormat("yyyy");
      String year = df.format(date);
      Connection con = DriverManager.getConnection(url, login, password);
      String query = "SELECT NEXT VALUE FOR visiteSeq";

      PreparedStatement preparedStatement = con.prepareStatement(query);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        int seqValue = resultSet.getInt(1);
        String formattedSeq = String.format("%1$07d", seqValue);
        return "VS-" + formattedSeq + "-" + year;
      } else {
        throw new RuntimeException("No value retrieved from sequence.");
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error generating reference for Visite: " + e.getMessage(), e);
    }
  }

  @Override
  public HashMap<String, Object> getAllvisits(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche) {
    String visitType = null;
    String reference_visite = null;
    Date visitDateStart = null;
    Date visitDateEnd = null;
    Long creePar = null;
    Long revendeur = null;
    String status = null;
    String sort = "";
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    HashMap<String, Object> response = new HashMap<>();
    if (filterrecherche != null && !filterrecherche.isEmpty()) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (obj.keySet().contains("typeVisite") && obj.get("typeVisite") != null
          && !Objects.equals(obj.getString("typeVisite"), "")
          && obj.getString("typeVisite") != null) {
        visitType = obj.getString("typeVisite").trim();
      }
      if (obj.keySet().contains("Status") && obj.get("Status") != null
          && !Objects.equals(obj.getString("Status"), "") && obj.getString("Status") != null) {
        status = obj.getString("Status").trim();
      }
      if (obj.keySet().contains("Revendeur") && obj.get("Revendeur") != null
          && !Objects.equals(obj.getString("Revendeur"), "")
          && obj.getString("Revendeur") != null) {
        revendeur = obj.getLong("Revendeur");
      }
      if (obj.keySet().contains("reference_visite") && obj.get("reference_visite") != null
          && !Objects.equals(obj.getString("reference_visite"), "")
          && obj.getString("reference_visite") != null) {
        reference_visite = obj.getString("reference_visite").trim();
      }
      if (obj.keySet().contains("reference_visite") && obj.get("reference_visite") != null
          && !Objects.equals(obj.getString("reference_visite"), "")
          && obj.getString("reference_visite") != null) {
        reference_visite = obj.getString("reference_visite").trim();
      }
      if (obj.keySet().contains("datedebut") && !Objects.equals(obj.getString("datedebut"), "")
          && obj.getString("datedebut") != null) {
        visitDateStart = CrmUtils.convertStringToDate(obj.getString("datedebut"));
      }
      if (obj.keySet().contains("datefin") && !Objects.equals(obj.getString("datefin"), "")
          && obj.getString("datefin") != null) {
        // String datefinWithTime = obj.getString("datefin") + " 23:59:59";
        String datefin = obj.getString("datefin") + "  23:59:59";

        try {
          visitDateEnd = dateFormat.parse(datefin);
        } catch (ParseException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        // visitDateEnd = CrmUtils.convertStringToDate(datefinWithTime);
      }

      if (obj.keySet().contains("Creepar") && !Objects.equals(obj.get("Creepar"), "")
          && obj.getString("Creepar") != null) {
        creePar = obj.getLong("Creepar");
      }

    }
    if (StringsRole.contains("READ_VISIT_LIST_OWNER")) {
      creePar = user.getUserid();
    }
    int currentpage = start / length;
    switch (ordercolumnaram) {
      case 0:
        sort = "reference_visite";
      case 1:
        sort = "typeVisite";
      case 2:
        sort = "revendeur";
      default:
        sort = "createdDate";
    }
    Page<Visite> responseData = this.findAllWithFilters(currentpage + 1, length, visitType,
        reference_visite, visitDateStart, visitDateEnd, creePar, revendeur, status, sort, orderdir);
    response.put("data", responseData.getContent());
    response.put("recordsTotal", responseData.getTotalElements());
    response.put("recordsFiltered", responseData.getTotalElements());
    response.put("draw", draw);
    response.put("start", start);

    return response;
  }

  public Page<Visite> findAllWithFilters(int pageNo, int pageSize, String visitType,
      String reference_visite, Date visitDateStart, Date visitDateEnd, Long creePar, Long revendeur,
      String status, String sortvar, String sorttype) {
    Sort sort = Sort.by("createdDate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return visiteRepository.findAllByFilters(visitType, reference_visite, visitDateStart,
        visitDateEnd, creePar, revendeur, status, pageable);
  }

  @Override
  public Visite findVisiteById(Long id) {
    Visite visite = visiteRepository.findById(id).get();
    return visite;
  }

  @Override
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      String reference, String typeVisite, String status, Long creepar, Long revendeur,
      String datedebut, String datefin) {
    ModelAndView mav = new ModelAndView();
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String visitType = null;
      String reference_visite = null;
      Date visitDateStart = null;
      Date visitDateEnd = null;
      Long creePar = null;
      Long rev = null;
      String stat = null;


      if (reference != null && !reference.isEmpty()) {
        reference_visite = reference;
      }
      if (typeVisite != null && !typeVisite.isEmpty()) {
        visitType = typeVisite;
      }
      if (status != null && !status.isEmpty()) {
        stat = status;
      }
      if (datedebut != null && !datedebut.isEmpty()) {
        try {
          datedebut = datedebut + " 00:00:00";

          visitDateStart = dateFormat.parse(datedebut);
        } catch (ParseException e) {
          logger
              .warn("findAllVisitByFilter (catching parsing debut date visit) : " + e.getMessage());
        }
      }

      if (datefin != null && !datefin.isEmpty()) {
        try {
          datefin = datefin + "  23:59:59";

          visitDateEnd = dateFormat.parse(datefin);
        } catch (ParseException e) {
          logger.warn("findAllVisitByFilter (catching parsing fin date visit) : " + e.getMessage());
        }
      }

      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
        List<Visite> myList = new ArrayList<>();
        if (StringsRole.contains("READ_VISIT_LIST_OWNER")) {
          creePar = user.getUserid();
          myList = visiteRepository.findAllVisitByFiltersForExport(visitType, reference_visite,
              visitDateStart, visitDateEnd, creePar, revendeur, stat);
        }
        if (StringsRole.contains("READ_VISIT_LIST")) {
          myList = visiteRepository.findAllVisitByFiltersForExport(visitType, reference_visite,
              visitDateStart, visitDateEnd, creepar, revendeur, stat);
        }
        if (myList.size() > 0) {

          mav.setView(new VisitExcelExport());
          mav.addObject("list", myList);
          mav.addObject("user", user);
          return mav;
        } else {
          request.getRequestDispatcher("/visite/allvisites/1").forward(request, response);
          return null;
        }
      }
    } catch (Exception e) {
      logger.error(" visite.exportation visite Error:" + e);

    }
    return mav;
  }

}
