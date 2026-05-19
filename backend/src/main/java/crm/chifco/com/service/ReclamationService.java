package crm.chifco.com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ReclamationRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.utils.CrmUtils;

@Service
public class ReclamationService {
  @Autowired
  private ReclamationRepository reclamationRepository;
  @Autowired
  private UserRepository userRepository;

  public List<Reclamation> getAllReclamations() {
    return reclamationRepository.findAll();
  }

  public Reclamation getReclamationById(Long id) {
    return reclamationRepository.findById(id).orElse(null);
  }

  public List<Reclamation> getReclamationsByStatus(String status) {
    return reclamationRepository.findByStatusNomStatut(status);
  }

  public List<Reclamation> getReclamationsByServiceType(String serviceType) {
    return reclamationRepository.findByServiceTypeCategorytype(serviceType);
  }

  public void saveReclamation(Reclamation reclamation) {
    reclamationRepository.save(reclamation);
  }

  public void deleteReclamation(Long id) {
    reclamationRepository.deleteById(id);
  }

  public Page<Reclamation> findReclamations(String category, String serviceType, int page,
      int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return reclamationRepository.findByCategoryAndServiceType(category, serviceType, pageable);
  }

  public Page<Reclamation> findReclamationsClients(String refclient, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return reclamationRepository.findReclamationsClients(refclient, pageable);
  }

  private String getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    return currentUser;
  }

  public HashMap<String, Object> findAlltechReclamations(int draw, int start, int length,
      String search, int ordercolumnaram, String orderdir, String filterrecherche, String category,
      String type) {
    String status = null;

    Date datedebut = null;
    Date datefin = null;
    Date dateDebutModification = null;
    Date dateFinModification = null;
    Long AffecterTo = null;
    Long creePar = null;
    Long TelFixe = null;
    String ref_reclamation = null;
    Long motifRec = null;
    String referencett = null;
    String referencenety = null;
    String identifiant = null;
    String codeUser = null;
    String codeUserCom = null;
    String source = null;
    Long agentsav = null;
    String etattt = null;
    String currentUser = getCurrentUser();
    Date datereclamationttdebut = null;
    Date datereclamationttfin = null;
    Date dateverificationfsidebut = null;
    Date dateverificationfsifin = null;
    String centralrelclamtion = null;
    String gouvernorat = null;
    String statusTech = null;
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    String sort = "";

    Page<Reclamation> responseData = null;
    int currentpage = start / length;
    HashMap<String, Object> myGreetings = new HashMap<>();

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (obj.keySet().contains("Status") && !Objects.equals(obj.getString("Status"), "")
          && obj.getString("Status") != null) {
        status = obj.getString("Status");
      }

      if (obj.keySet().contains("reference") && !Objects.equals(obj.getString("reference"), "")
          && obj.getString("reference") != null) {
        ref_reclamation = obj.getString("reference").trim();
      }
      if (obj.keySet().contains("gouvernorat") && !Objects.equals(obj.getString("gouvernorat"), "")
          && obj.getString("gouvernorat") != null) {
        gouvernorat = obj.getString("gouvernorat").trim();
      }
      if (obj.keySet().contains("centralrelclamtion")
          && !Objects.equals(obj.getString("centralrelclamtion"), "")
          && obj.getString("centralrelclamtion") != null) {
        centralrelclamtion = obj.getString("centralrelclamtion").trim();
      }
      if (obj.keySet().contains("etattt") && !Objects.equals(obj.getString("etattt"), "")
          && obj.getString("etattt") != null) {
        etattt = obj.getString("etattt").trim();
      }
      if (obj.keySet().contains("statusTech") && !Objects.equals(obj.getString("statusTech"), "")
          && obj.getString("statusTech") != null) {
        statusTech = obj.getString("statusTech").trim();
      }
      if (obj.keySet().contains("codeUser") && !Objects.equals(obj.getString("codeUser"), "")
          && obj.getString("codeUser") != null) {
        codeUser = obj.getString("codeUser").trim();
      }
      if (obj.keySet().contains("codeUserCom") && !Objects.equals(obj.getString("codeUserCom"), "")
          && obj.getString("codeUserCom") != null) {
        codeUserCom = obj.getString("codeUserCom").trim();
      }
      if (obj.keySet().contains("referencett") && !Objects.equals(obj.getString("referencett"), "")
          && obj.getString("referencett") != null) {
        referencett = obj.getString("referencett").trim();
      }
      if (obj.keySet().contains("referencenety")
          && !Objects.equals(obj.getString("referencenety"), "")
          && obj.getString("referencenety") != null) {
        referencenety = obj.getString("referencenety").trim();
      }
      if (obj.keySet().contains("identifiant") && !Objects.equals(obj.getString("identifiant"), "")
          && obj.getString("identifiant") != null) {
        identifiant = obj.getString("identifiant").trim();
      }
      if (obj.keySet().contains("telFixe") && !Objects.equals(obj.get("telFixe"), "")
          && obj.getString("telFixe") != null) {
        TelFixe = obj.getLong("telFixe");
      }
      if (obj.keySet().contains("datedebut") && !Objects.equals(obj.getString("datedebut"), "")
          && obj.getString("datedebut") != null) {
        datedebut = CrmUtils.convertStringToDate(obj.getString("datedebut"));
      }
      if (obj.keySet().contains("datefin") && !Objects.equals(obj.getString("datefin"), "")
          && obj.getString("datefin") != null) {
        datefin = CrmUtils.convertStringToLocalDateTime(obj.getString("datefin"));
      }

      if (obj.keySet().contains("dateDebutModification")
          && !Objects.equals(obj.getString("dateDebutModification"), "")
          && obj.getString("dateDebutModification") != null) {
        dateDebutModification =
            CrmUtils.convertStringToDate(obj.getString("dateDebutModification"));
      }
      if (obj.keySet().contains("dateFinModification")
          && !Objects.equals(obj.getString("dateFinModification"), "")
          && obj.getString("dateFinModification") != null) {
        dateFinModification =
            CrmUtils.convertStringToLocalDateTime(obj.getString("dateFinModification"));
      }
      if (obj.keySet().contains("AffecterTo") && !Objects.equals(obj.get("AffecterTo"), "")
          && obj.getString("AffecterTo") != null) {
        AffecterTo = obj.getLong("AffecterTo");
      }
      if (obj.keySet().contains("agentsav") && !Objects.equals(obj.get("agentsav"), "")
          && obj.getString("agentsav") != null) {
        agentsav = obj.getLong("agentsav");
      }
      if (obj.keySet().contains("Creepar") && !Objects.equals(obj.get("Creepar"), "")
          && obj.getString("Creepar") != null) {
        creePar = obj.getLong("Creepar");
      }
      if (obj.keySet().contains("source") && !Objects.equals(obj.getString("source"), "")
          && obj.getString("source") != null) {
        source = obj.getString("source").trim();
      }
      if (obj.keySet().contains("motifRec") && !Objects.equals(obj.get("motifRec"), "")
          && obj.getString("motifRec") != null) {
        motifRec = obj.getLong("motifRec");
      }
      if (obj.keySet().contains("datereclamationttdebut")
          && !Objects.equals(obj.getString("datereclamationttdebut"), "")
          && obj.getString("datereclamationttdebut") != null) {
        datereclamationttdebut =
            CrmUtils.convertStringToDate(obj.getString("datereclamationttdebut"));
      }
      if (obj.keySet().contains("datereclamationttfin")
          && !Objects.equals(obj.getString("datereclamationttfin"), "")
          && obj.getString("datereclamationttfin") != null) {
        datereclamationttfin =
            CrmUtils.convertStringToLocalDateTime(obj.getString("datereclamationttfin"));
      }
      if (obj.keySet().contains("dateverificationfsifin")
          && !Objects.equals(obj.getString("dateverificationfsifin"), "")
          && obj.getString("dateverificationfsifin") != null) {
        dateverificationfsifin =
            CrmUtils.convertStringToLocalDateTime(obj.getString("dateverificationfsifin"));
      }
      if (obj.keySet().contains("dateverificationfsidebut")
          && !Objects.equals(obj.getString("dateverificationfsidebut"), "")
          && obj.getString("dateverificationfsidebut") != null) {
        dateverificationfsidebut =
            CrmUtils.convertStringToDate(obj.getString("dateverificationfsidebut"));
      }
    }

    // admin, pos, finance
    if (StringsRole.contains("READ-RECLAMATION-ALL")) {
      if (ordercolumnaram <= 0) {
        sort = "createdDate";
        orderdir = "desc";
      }
      switch (ordercolumnaram) {

        case 1:
          sort = "ref_reclamation";
          break;
        case 2:
          sort = "description";

          break;
        case 3:
          sort = "status";
          break;
        case 4:
          sort = "serviceType";
          break;
        case 5:
          sort = "serviceType";
          break;
        case 6:
          sort = "client";
        case 7:
          sort = "user";
          break;
        case 8:
          sort = "createdDate";
          break;
        default:
          sort = "createdDate";

      }

      responseData = this.findPaginatedwithfilter(currentpage + 1, length, null, sort, orderdir,
          ref_reclamation, status, datedebut, datefin, dateDebutModification, dateFinModification,
          AffecterTo, creePar, category, type, TelFixe, motifRec, identifiant, referencenety,
          referencett, codeUser, codeUserCom, source, agentsav, etattt, datereclamationttdebut,
          datereclamationttfin, dateverificationfsidebut, dateverificationfsifin,
          centralrelclamtion, gouvernorat, statusTech);
    }
    // revendeur
    else if (StringsRole.contains("READ-RECLAMATION-OWNER")) {
      if (ordercolumnaram <= 0) {
        sort = "createdDate";
        orderdir = "desc";
      }
      switch (ordercolumnaram) {

        case 1:
          sort = "ref_reclamation";
          break;
        case 2:
          sort = "description";

          break;
        case 3:
          sort = "status";
          break;
        case 4:
          sort = "serviceType";
          break;
        case 5:
          sort = "serviceType";
          break;
        case 6:
          sort = "client";
        case 7:
          sort = "user";
          break;
        case 8:
          sort = "createdDate";
          break;
        default:
          sort = "createdDate";

      }
      responseData = this.findPaginatedByRevendeur(currentpage + 1, length, user.getUserid(), sort,
          orderdir, ref_reclamation, status, datedebut, datefin, dateDebutModification,
          dateFinModification, AffecterTo, user.getUserid(), category, type, TelFixe, identifiant,
          referencenety, referencett);

    }
    // distributeur
    else if (StringsRole.contains("READ-RECLAMATION-AREA")) {
      if (ordercolumnaram <= 0) {
        sort = "createdDate";
        orderdir = "desc";
      }
      switch (ordercolumnaram) {

        case 1:
          sort = "ref_reclamation";
          break;
        case 2:
          sort = "description";

          break;
        case 3:
          sort = "status";
          break;
        case 4:
          sort = "serviceType";
          break;
        case 5:
          sort = "serviceType";
          break;
        case 6:
          sort = "client";
          break;
        case 7:
          sort = "user";
          break;
        case 8:
          sort = "createdDate";
          break;
        default:
          sort = "createdDate";

      }
      responseData = this.findPaginatedByDistributeur(currentpage + 1, length, user.getUserid(),
          sort, orderdir, ref_reclamation, status, datedebut, datefin, dateDebutModification,
          dateFinModification, user.getUserid(), creePar, category, type, TelFixe, identifiant,
          referencenety, referencett);

    }


    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;
  }

  public Page<Reclamation> findPaginatedByDistributeur(int pageNo, int pageSize,
      Long createdbyuserid, String sortvar, String sorttype, String ref_reclamation, String status,
      Date datedebut, Date datefin, Date dateDebutModification, Date dateFinModification,
      Long affecterTo, Long creepar, String category, String type, Long TelFixe, String identifiant,
      String referencenety, String referencett) {

    Sort sort;
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else {
      sort = Sort.by(sortvar).ascending();
    }

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    if (category.equals("Client"))
      return this.reclamationRepository.findReclamationsByCreatedBy_AffectedToClient(pageable,
          createdbyuserid, ref_reclamation, status, null, datedebut, datefin, dateDebutModification,
          dateFinModification, category, type, TelFixe, identifiant, referencenety, referencett);
    else
      return this.reclamationRepository.findReclamationsByCreatedBy_AffectedTo(pageable,
          createdbyuserid, ref_reclamation, status, null, datedebut, datefin, dateDebutModification,
          dateFinModification, category, type);
  }

  public Page<Reclamation> findPaginatedByRevendeur(int pageNo, int pageSize, Long createdbyuserid,
      String sortvar, String sorttype, String ref_reclamation, String status, Date datedebut,
      Date datefin, Date dateDebutModification, Date dateFinModification, Long affecterTo,
      Long creepar, String category, String type, Long TelFixe, String identifiant,
      String referencenety, String referencett) {

    Sort sort;
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else {
      sort = Sort.by(sortvar).ascending();
    }

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    if (category.equals("Client"))
      return this.reclamationRepository.findReclamationRevendeurbyuserandfilterClient(pageable,
          createdbyuserid, ref_reclamation, status, creepar, datedebut, datefin,
          dateDebutModification, dateFinModification, category, type, TelFixe, identifiant,
          referencenety, referencett);
    else
      return this.reclamationRepository.findReclamationRevendeurbyuserandfilter(pageable,
          createdbyuserid, ref_reclamation, status, creepar, datedebut, datefin,
          dateDebutModification, dateFinModification, category, type);
  }

  public Page<Reclamation> findPaginatedwithfilter(int pageNo, int pageSize, Long createdbyuserid,
      String sortvar, String sorttype, String ref_reclamation, String status, Date datedebut,
      Date datefin, Date dateDebutModification, Date dateFinModification, Long affecterTo,
      Long creepar, String category, String type, Long TelFixe, Long motifRec, String identifiant,
      String referencenety, String referencett, String codeUser, String codeUserCom, String source,
      Long agentsav, String etattt, Date datereclamationttdebut, Date datereclamationttfin,
      Date dateverificationfsidebut, Date dateverificationfsifin, String centralrelclamtion,
      String gouvernorat, String statusTech) {

    Sort sort;
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else {
      sort = Sort.by(sortvar).ascending();
    }

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    if (category.equals("Client"))
      return this.reclamationRepository.findReclamationsAllClient(pageable, createdbyuserid,
          ref_reclamation, status, affecterTo, datedebut, datefin, dateDebutModification,
          dateFinModification, category, type, motifRec, TelFixe, identifiant, referencenety,
          referencett, source, agentsav, etattt, datereclamationttdebut, datereclamationttfin,
          dateverificationfsidebut, dateverificationfsifin, centralrelclamtion, gouvernorat,
          statusTech);
    else
      return this.reclamationRepository.findReclamationsAll(pageable, createdbyuserid,
          ref_reclamation, status, affecterTo, datedebut, datefin, dateDebutModification,
          dateFinModification, category, type, motifRec, codeUser, codeUserCom);
  }

  public List<Reclamation> getReclamatiobByIDclient(Long clientid) {
    return this.reclamationRepository.findByClient_clientid(clientid);
  }

  public Page<Reclamation> getAllClientReclamations(Pageable pageable) {

    return reclamationRepository.findByClientIsNotNull(pageable);
  }

  public Page<Reclamation> getReclamationsByClientId(Long clientId, Pageable pageable) {

    return reclamationRepository.findByClient_Clientid(clientId, pageable);
  }


  public JsonResponseBody removeFiled(Long id, HttpServletRequest request) {
    Object checklisteDesIdsAExporter =
        request.getSession().getAttribute("listedes_ids_reclamations");
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      List<Long> listesdesIds = new ArrayList<>();
      JsonResponseBody jrb = new JsonResponseBody();
      jrb.setCode(String.valueOf(200));
      jrb.setMsg("liste des ids est vide");
      jrb.setResult(listesdesIds);
      return jrb;
    } else {
      List<Long> listedesIds =
          (List<Long>) request.getSession().getAttribute("listedes_ids_reclamations");
      int pos = listedesIds.indexOf(id);
      listedesIds.remove(pos);
      request.getSession().setAttribute("listedes_ids_reclamations", listedesIds);

      JsonResponseBody jrb1 = new JsonResponseBody();
      jrb1.setCode(String.valueOf(200));
      jrb1.setMsg("Suppression de l'id de liste avec succes");
      jrb1.setResult(listedesIds);
      return jrb1;
    }
  }

  public JsonResponseBody addFiled(Long id, HttpServletRequest request) {
    Object checkliste = request.getSession().getAttribute("listedes_ids_reclamations");
    List<Long> listesdesIds = new ArrayList<>();
    if (checkliste == null || checkliste.equals("")) {
      if (id == null || id.equals("")) {
        request.getSession().setAttribute("listedes_ids_reclamations", listesdesIds);
      } else {
        listesdesIds.add(id);
        request.getSession().setAttribute("listedes_ids_reclamations", listesdesIds);
      }

    } else {
      listesdesIds = (List<Long>) request.getSession().getAttribute("listedes_ids_reclamations");
      if (listesdesIds.contains(id) == false) {
        listesdesIds.add(id);
      }

      request.getSession().setAttribute("listedes_ids_reclamations", listesdesIds);
    }
    JsonResponseBody jrb = new JsonResponseBody();
    jrb.setCode(String.valueOf(200));
    jrb.setMsg("Ajout de l'id à liste avec succes");
    jrb.setResult(listesdesIds);
    return jrb;
  }

  public List<Reclamation> findReclamationByListeReclamationId(List<String> listeReclamationId) {
    List<Long> ids = listeReclamationId.stream().filter(id -> id != null && !id.trim().isEmpty())
        .map(Long::parseLong).collect(Collectors.toList());
    return reclamationRepository.findByReclamationidIn(ids);
  }



  public JsonResponseBody removeAllFromListReclamation(HttpServletRequest request) {
    Object checklisteDesIdsAExporter =
        request.getSession().getAttribute("listedes_ids_reclamations");
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      List<Long> listesdesIds = new ArrayList<>();
      JsonResponseBody jrb = new JsonResponseBody();
      jrb.setCode(String.valueOf(200));
      jrb.setMsg("liste des ids est vide");
      jrb.setResult(listesdesIds);
      return jrb;
    } else {
      List<Long> listedesIds =
          (List<Long>) request.getSession().getAttribute("listedes_ids_reclamations");
      listedesIds.clear();
      request.getSession().setAttribute("listedes_ids_reclamations", listedesIds);
      JsonResponseBody jrb1 = new JsonResponseBody();
      jrb1.setCode(String.valueOf(200));
      jrb1.setMsg("Vider liste avec succes");
      jrb1.setResult(listedesIds);
      return jrb1;
    }
  }

  public List<Long> addAllIdReclamation(String filterrecherche, HttpServletRequest request,
      String type, String category) {
    // String category = "Client"; // default value
    // String type = "Technique"; // default value
    String status = null;
    Date datedebut = null;
    Date datefin = null;
    Date dateDebutModification = null;
    Date dateFinModification = null;
    Long AffecterTo = null;
    Long creePar = null;
    Long TelFixe = null;
    String ref_reclamation = null;
    Long motifRec = null;
    String referencett = null;
    String referencenety = null;
    String identifiant = null;
    String codeUser = null;
    String codeUserCom = null;
    String source = null;
    Long agentsav = null;
    String etattt = null;
    Date datereclamationttdebut = null;
    Date datereclamationttfin = null;
    Date dateverificationfsidebut = null;
    Date dateverificationfsifin = null;
    String centralrelclamtion = null;
    String gouvernorat = null;
    String statusTech = null;
    String currentUser = getCurrentUser();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (obj.keySet().contains("Status") && !Objects.equals(obj.getString("Status"), "")
          && obj.getString("Status") != null) {
        status = obj.getString("Status");
      }
      if (obj.keySet().contains("reference") && !Objects.equals(obj.getString("reference"), "")
          && obj.getString("reference") != null) {
        ref_reclamation = obj.getString("reference").trim();
      }
      if (obj.keySet().contains("statusTech") && !Objects.equals(obj.getString("statusTech"), "")
          && obj.getString("statusTech") != null) {
        statusTech = obj.getString("statusTech").trim();
      }
      if (obj.keySet().contains("gouvernorat") && !Objects.equals(obj.getString("gouvernorat"), "")
          && obj.getString("gouvernorat") != null) {
        gouvernorat = obj.getString("gouvernorat").trim();
      }
      if (obj.keySet().contains("centralrelclamtion")
          && !Objects.equals(obj.getString("centralrelclamtion"), "")
          && obj.getString("centralrelclamtion") != null) {
        centralrelclamtion = obj.getString("centralrelclamtion").trim();
      }
      if (obj.keySet().contains("etattt") && !Objects.equals(obj.getString("etattt"), "")
          && obj.getString("etattt") != null) {
        etattt = obj.getString("etattt").trim();
      }
      if (obj.keySet().contains("codeUser") && !Objects.equals(obj.getString("codeUser"), "")
          && obj.getString("codeUser") != null) {
        codeUser = obj.getString("codeUser").trim();
      }
      if (obj.keySet().contains("codeUserCom") && !Objects.equals(obj.getString("codeUserCom"), "")
          && obj.getString("codeUserCom") != null) {
        codeUserCom = obj.getString("codeUserCom").trim();
      }
      if (obj.keySet().contains("referencett") && !Objects.equals(obj.getString("referencett"), "")
          && obj.getString("referencett") != null) {
        referencett = obj.getString("referencett").trim();
      }
      if (obj.keySet().contains("referencenety")
          && !Objects.equals(obj.getString("referencenety"), "")
          && obj.getString("referencenety") != null) {
        referencenety = obj.getString("referencenety").trim();
      }
      if (obj.keySet().contains("source") && !Objects.equals(obj.getString("source"), "")
          && obj.getString("source") != null) {
        source = obj.getString("source").trim();
      }
      if (obj.keySet().contains("identifiant") && !Objects.equals(obj.getString("identifiant"), "")
          && obj.getString("identifiant") != null) {
        identifiant = obj.getString("identifiant").trim();
      }
      if (obj.keySet().contains("telFixe") && !Objects.equals(obj.get("telFixe"), "")
          && obj.getString("telFixe") != null) {
        TelFixe = obj.getLong("telFixe");
      }
      if (obj.keySet().contains("datedebut") && !Objects.equals(obj.getString("datedebut"), "")
          && obj.getString("datedebut") != null) {
        datedebut = CrmUtils.convertStringToDate(obj.getString("datedebut"));
      }
      if (obj.keySet().contains("datefin") && !Objects.equals(obj.getString("datefin"), "")
          && obj.getString("datefin") != null) {
        datefin = CrmUtils.convertStringToLocalDateTime(obj.getString("datefin"));
      }
      if (obj.keySet().contains("dateDebutModification")
          && !Objects.equals(obj.getString("dateDebutModification"), "")
          && obj.getString("dateDebutModification") != null) {
        dateDebutModification =
            CrmUtils.convertStringToDate(obj.getString("dateDebutModification"));
      }
      if (obj.keySet().contains("dateFinModification")
          && !Objects.equals(obj.getString("dateFinModification"), "")
          && obj.getString("dateFinModification") != null) {
        dateFinModification =
            CrmUtils.convertStringToLocalDateTime(obj.getString("dateFinModification"));
      }
      if (obj.keySet().contains("AffecterTo") && !Objects.equals(obj.get("AffecterTo"), "")
          && obj.getString("AffecterTo") != null) {
        AffecterTo = obj.getLong("AffecterTo");
      }
      if (obj.keySet().contains("agentsav") && !Objects.equals(obj.get("agentsav"), "")
          && obj.getString("agentsav") != null) {
        agentsav = obj.getLong("agentsav");
      }
      if (obj.keySet().contains("Creepar") && !Objects.equals(obj.get("Creepar"), "")
          && obj.getString("Creepar") != null) {
        creePar = obj.getLong("Creepar");
      }
      if (obj.keySet().contains("motifRec") && !Objects.equals(obj.get("motifRec"), "")
          && obj.getString("motifRec") != null) {
        motifRec = obj.getLong("motifRec");
      }
      if (obj.keySet().contains("datereclamationttdebut")
          && !Objects.equals(obj.getString("datereclamationttdebut"), "")
          && obj.getString("datereclamationttdebut") != null) {
        datereclamationttdebut =
            CrmUtils.convertStringToDate(obj.getString("datereclamationttdebut"));
      }
      if (obj.keySet().contains("datereclamationttfin")
          && !Objects.equals(obj.getString("datereclamationttfin"), "")
          && obj.getString("datereclamationttfin") != null) {
        datereclamationttfin =
            CrmUtils.convertStringToLocalDateTime(obj.getString("datereclamationttfin"));
      }
      if (obj.keySet().contains("datereclamationttdebut")
          && !Objects.equals(obj.getString("datereclamationttdebut"), "")
          && obj.getString("datereclamationttdebut") != null) {
        datereclamationttdebut =
            CrmUtils.convertStringToDate(obj.getString("datereclamationttdebut"));
      }
      if (obj.keySet().contains("dateverificationfsidebut")
          && !Objects.equals(obj.getString("dateverificationfsidebut"), "")
          && obj.getString("dateverificationfsidebut") != null) {
        datereclamationttfin =
            CrmUtils.convertStringToLocalDateTime(obj.getString("dateverificationfsidebut"));
      }


    }

    List<Long> listesdesIdsfromrequest = new ArrayList<>();
    int largePageSize = Integer.MAX_VALUE;

    // admin, pos, finance
    if (StringsRole.contains("READ-RECLAMATION-ALL")) {
      Page<Reclamation> page = this.findPaginatedwithfilter(1, largePageSize, null, "createdDate",
          "desc", ref_reclamation, status, datedebut, datefin, dateDebutModification,
          dateFinModification, AffecterTo, creePar, category, type, TelFixe, motifRec, identifiant,
          referencenety, referencett, codeUser, codeUserCom, source, agentsav, etattt,
          datereclamationttdebut, datereclamationttfin, dateverificationfsidebut,
          dateverificationfsifin, centralrelclamtion, gouvernorat, statusTech);
      listesdesIdsfromrequest = page.getContent().stream().map(Reclamation::getReclamationid)
          .collect(Collectors.toList());
    }
    // revendeur
    else if (StringsRole.contains("READ-RECLAMATION-OWNER")) {
      Page<Reclamation> page = this.findPaginatedByRevendeur(1, largePageSize, user.getUserid(),
          "createdDate", "desc", ref_reclamation, status, datedebut, datefin, dateDebutModification,
          dateFinModification, AffecterTo, user.getUserid(), category, type, TelFixe, identifiant,
          referencenety, referencett);
      listesdesIdsfromrequest = page.getContent().stream().map(Reclamation::getReclamationid)
          .collect(Collectors.toList());
    }
    // distributeur
    else if (StringsRole.contains("READ-RECLAMATION-AREA")) {
      Page<Reclamation> page = this.findPaginatedByDistributeur(1, largePageSize, user.getUserid(),
          "createdDate", "desc", ref_reclamation, status, datedebut, datefin, dateDebutModification,
          dateFinModification, user.getUserid(), creePar, category, type, TelFixe, identifiant,
          referencenety, referencett);
      listesdesIdsfromrequest = page.getContent().stream().map(Reclamation::getReclamationid)
          .collect(Collectors.toList());
    }

    List<Long> listesdesIds = new ArrayList<>();
    Object checklisteDesIdsAExporter =
        request.getSession().getAttribute("listedes_ids_reclamations");

    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter == "") {
      listesdesIds.addAll(listesdesIdsfromrequest);
    } else {
      listesdesIds = (List<Long>) request.getSession().getAttribute("listedes_ids_reclamations");
      for (Long id : listesdesIdsfromrequest) {
        if (!listesdesIds.contains(id)) {
          listesdesIds.add(id);
        }
      }
    }

    request.getSession().setAttribute("listedes_ids_reclamations", listesdesIds);
    return listesdesIds;
  }

  public Page<Reclamation> findAllReclamationAPI(Long client_id, String ref_reclamation,
      String category, Long serviceTypeId, Long statusId, String identifiant, Long telfixe,
      String referencenety, String source, Date startDate, Date endDate, String status,
      Pageable pageable) {
    return reclamationRepository.findReclamationsByFilters(client_id, ref_reclamation, category,
        serviceTypeId, statusId, identifiant, telfixe, referencenety, source, startDate, endDate,
        status, pageable);
  }

  public HashMap<String, Object> findAlltechReclamationsAgent(int draw, int start, int length,
      String search, int ordercolumnaram, String orderdir, String filterrecherche, String category,
      String type) {
    String status = null;

    Date datedebut = null;
    Date datefin = null;
    Date dateDebutModification = null;
    Date dateFinModification = null;
    Long AffecterTo = null;
    Long creePar = null;
    Long TelFixe = null;
    String ref_reclamation = null;
    Long motifRec = null;
    String referencett = null;
    String referencenety = null;
    String identifiant = null;
    String codeUser = null;
    String codeUserCom = null;
    String source = null;
    Long agentsav = null;
    String etattt = null;
    String currentUser = getCurrentUser();
    Date datereclamationttdebut = null;
    Date datereclamationttfin = null;
    Date dateverificationfsidebut = null;
    Date dateverificationfsifin = null;
    String centralrelclamtion = null;
    String gouvernorat = null;
    String statusTech = null;
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    String sort = "";

    Page<Reclamation> responseData = null;
    int currentpage = start / length;
    HashMap<String, Object> myGreetings = new HashMap<>();

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (obj.keySet().contains("Status") && !Objects.equals(obj.getString("Status"), "")
          && obj.getString("Status") != null) {
        status = obj.getString("Status");
      }

      if (obj.keySet().contains("reference") && !Objects.equals(obj.getString("reference"), "")
          && obj.getString("reference") != null) {
        ref_reclamation = obj.getString("reference").trim();
      }
      if (obj.keySet().contains("statusTech") && !Objects.equals(obj.getString("statusTech"), "")
          && obj.getString("statusTech") != null) {
        statusTech = obj.getString("statusTech").trim();
      }
      if (obj.keySet().contains("gouvernorat") && !Objects.equals(obj.getString("gouvernorat"), "")
          && obj.getString("gouvernorat") != null) {
        gouvernorat = obj.getString("gouvernorat").trim();
      }
      if (obj.keySet().contains("centralrelclamtion")
          && !Objects.equals(obj.getString("centralrelclamtion"), "")
          && obj.getString("centralrelclamtion") != null) {
        centralrelclamtion = obj.getString("centralrelclamtion").trim();
      }
      if (obj.keySet().contains("etattt") && !Objects.equals(obj.getString("etattt"), "")
          && obj.getString("etattt") != null) {
        etattt = obj.getString("etattt").trim();
      }
      if (obj.keySet().contains("codeUser") && !Objects.equals(obj.getString("codeUser"), "")
          && obj.getString("codeUser") != null) {
        codeUser = obj.getString("codeUser").trim();
      }
      if (obj.keySet().contains("codeUserCom") && !Objects.equals(obj.getString("codeUserCom"), "")
          && obj.getString("codeUserCom") != null) {
        codeUserCom = obj.getString("codeUserCom").trim();
      }
      if (obj.keySet().contains("referencett") && !Objects.equals(obj.getString("referencett"), "")
          && obj.getString("referencett") != null) {
        referencett = obj.getString("referencett").trim();
      }
      if (obj.keySet().contains("referencenety")
          && !Objects.equals(obj.getString("referencenety"), "")
          && obj.getString("referencenety") != null) {
        referencenety = obj.getString("referencenety").trim();
      }
      if (obj.keySet().contains("identifiant") && !Objects.equals(obj.getString("identifiant"), "")
          && obj.getString("identifiant") != null) {
        identifiant = obj.getString("identifiant").trim();
      }
      if (obj.keySet().contains("telFixe") && !Objects.equals(obj.get("telFixe"), "")
          && obj.getString("telFixe") != null) {
        TelFixe = obj.getLong("telFixe");
      }
      if (obj.keySet().contains("datedebut") && !Objects.equals(obj.getString("datedebut"), "")
          && obj.getString("datedebut") != null) {
        datedebut = CrmUtils.convertStringToDate(obj.getString("datedebut"));
      }
      if (obj.keySet().contains("datefin") && !Objects.equals(obj.getString("datefin"), "")
          && obj.getString("datefin") != null) {
        datefin = CrmUtils.convertStringToLocalDateTime(obj.getString("datefin"));
      }

      if (obj.keySet().contains("dateDebutModification")
          && !Objects.equals(obj.getString("dateDebutModification"), "")
          && obj.getString("dateDebutModification") != null) {
        dateDebutModification =
            CrmUtils.convertStringToDate(obj.getString("dateDebutModification"));
      }
      if (obj.keySet().contains("dateFinModification")
          && !Objects.equals(obj.getString("dateFinModification"), "")
          && obj.getString("dateFinModification") != null) {
        dateFinModification =
            CrmUtils.convertStringToLocalDateTime(obj.getString("dateFinModification"));
      }
      if (obj.keySet().contains("AffecterTo") && !Objects.equals(obj.get("AffecterTo"), "")
          && obj.getString("AffecterTo") != null) {
        AffecterTo = obj.getLong("AffecterTo");
      }
      if (obj.keySet().contains("agentsav") && !Objects.equals(obj.get("agentsav"), "")
          && obj.getString("agentsav") != null) {
        agentsav = obj.getLong("agentsav");
      }
      if (obj.keySet().contains("Creepar") && !Objects.equals(obj.get("Creepar"), "")
          && obj.getString("Creepar") != null) {
        creePar = obj.getLong("Creepar");
      }
      if (obj.keySet().contains("source") && !Objects.equals(obj.getString("source"), "")
          && obj.getString("source") != null) {
        source = obj.getString("source").trim();
      }
      if (obj.keySet().contains("motifRec") && !Objects.equals(obj.get("motifRec"), "")
          && obj.getString("motifRec") != null) {
        motifRec = obj.getLong("motifRec");
      }
      if (obj.keySet().contains("datereclamationttdebut")
          && !Objects.equals(obj.getString("datereclamationttdebut"), "")
          && obj.getString("datereclamationttdebut") != null) {
        datereclamationttdebut =
            CrmUtils.convertStringToDate(obj.getString("datereclamationttdebut"));
      }
      if (obj.keySet().contains("datereclamationttfin")
          && !Objects.equals(obj.getString("datereclamationttfin"), "")
          && obj.getString("datereclamationttfin") != null) {
        datereclamationttfin =
            CrmUtils.convertStringToLocalDateTime(obj.getString("datereclamationttfin"));
      }
      if (obj.keySet().contains("dateverificationfsifin")
          && !Objects.equals(obj.getString("dateverificationfsifin"), "")
          && obj.getString("dateverificationfsifin") != null) {
        dateverificationfsifin =
            CrmUtils.convertStringToLocalDateTime(obj.getString("dateverificationfsifin"));
      }
      if (obj.keySet().contains("dateverificationfsidebut")
          && !Objects.equals(obj.getString("dateverificationfsidebut"), "")
          && obj.getString("dateverificationfsidebut") != null) {
        dateverificationfsidebut =
            CrmUtils.convertStringToDate(obj.getString("dateverificationfsidebut"));
      }
    }

    if (StringsRole.contains("LIST_MY_COMPLAINT")) {

      if (ordercolumnaram <= 0) {
        sort = "createdDate";
        orderdir = "desc";
      }
      switch (ordercolumnaram) {

        case 1:
          sort = "ref_reclamation";
          break;
        case 2:
          sort = "description";

          break;
        case 3:
          sort = "status";
          break;
        case 4:
          sort = "serviceType";
          break;
        case 5:
          sort = "serviceType";
          break;
        case 6:
          sort = "client";
        case 7:
          sort = "user";
          break;
        case 8:
          sort = "createdDate";
          break;
        default:
          sort = "createdDate";

      }
      agentsav = user.getUserid();
      responseData = this.findPaginatedwithfilterAgent(currentpage + 1, length, null, sort,
          orderdir, ref_reclamation, status, datedebut, datefin, dateDebutModification,
          dateFinModification, AffecterTo, creePar, category, type, TelFixe, motifRec, identifiant,
          referencenety, referencett, codeUser, codeUserCom, source, agentsav, etattt,
          datereclamationttdebut, datereclamationttfin, dateverificationfsidebut,
          dateverificationfsifin, centralrelclamtion, gouvernorat, statusTech);
    }


    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;
  }

  public Page<Reclamation> findPaginatedwithfilterAgent(int pageNo, int pageSize,
      Long createdbyuserid, String sortvar, String sorttype, String ref_reclamation, String status,
      Date datedebut, Date datefin, Date dateDebutModification, Date dateFinModification,
      Long affecterTo, Long creepar, String category, String type, Long TelFixe, Long motifRec,
      String identifiant, String referencenety, String referencett, String codeUser,
      String codeUserCom, String source, Long agentsav, String etattt, Date datereclamationttdebut,
      Date datereclamationttfin, Date dateverificationfsidebut, Date dateverificationfsifin,
      String centralrelclamtion, String gouvernorat, String statusTech) {

    Sort sort;
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    if (category.equals("Client"))
      return this.reclamationRepository.findReclamationsAllClientAgents(pageable, createdbyuserid,
          ref_reclamation, status, affecterTo, datedebut, datefin, dateDebutModification,
          dateFinModification, category, type, motifRec, TelFixe, identifiant, referencenety,
          referencett, source, agentsav, etattt, datereclamationttdebut, datereclamationttfin,
          dateverificationfsidebut, dateverificationfsifin, centralrelclamtion, gouvernorat,
          statusTech);
    else
      return this.reclamationRepository.findReclamationsAll(pageable, createdbyuserid,
          ref_reclamation, status, affecterTo, datedebut, datefin, dateDebutModification,
          dateFinModification, category, type, motifRec, codeUser, codeUserCom);
  }


}
