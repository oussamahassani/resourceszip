package crm.chifco.com.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.lowagie.text.DocumentException;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.Bordereau;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.EntryBordereau;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.BordereaurRepository;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.BordereauService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.templateclasse.ListeBordereau;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.UserTypeConstant;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping(value = "bordereau/*")
public class BordereauController {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  BordereauService bordereauService;

  @Autowired
  private UserService userService;
  @Value("${pathBordereau}")
  private String pathBordereau;
  @Autowired
  private GouvernoratRepository gouvernoratRepository;

  @Autowired
  private AvoirRepository avoirRepository;

  @Autowired
  private BordereaurRepository bordereaurRepository;

  @Autowired
  private EncaissementRepository encaissementRepository;

  @Autowired
  private crm.chifco.com.repository.EntryBordereauRepository entryBordereauRepository;

  @Autowired
  UserRepository userRepository;

  @GetMapping("/encaiss")
  @ResponseBody
  public List<Map<String, Object>> getEncaissementData(@RequestParam("year") int year,
      @RequestParam(value = "month", required = false) Integer month) {
    List<Map<String, Object>> fullYearData = new ArrayList<>();
    LocalDate currentDate = LocalDate.now();
    int currentYear = currentDate.getYear();
    int currentMonth = currentDate.getMonthValue();
    int currentDay = currentDate.getDayOfMonth();
    if (month == null) {
      List<Object[]> results = encaissementRepository.calculateChiffreAffaire(year);

      Map<Integer, Map<String, Object>> monthDataMap = new HashMap<>();
      for (Object[] result : results) {
        int resultMonth = (int) result[1];
        Map<String, Object> data = new HashMap<>();
        data.put("year", result[0]);
        data.put("month", resultMonth);
        data.put("chiffreAffaireBrute", result[2]);
        data.put("chiffreAffaireNette", result[3]);
        data.put("encaissementVersé", result[4]);
        monthDataMap.put(resultMonth, data);
      }
      int maxMonth = (year == currentYear) ? currentMonth : 12;
      for (int i = 1; i <= maxMonth; i++) {
        if (monthDataMap.containsKey(i)) {
          fullYearData.add(monthDataMap.get(i));
        } else {
          Map<String, Object> emptyData = new HashMap<>();
          emptyData.put("year", year);
          emptyData.put("month", i);
          emptyData.put("chiffreAffaireNette", 0);
          emptyData.put("chiffreAffaireBrute", 0);
          emptyData.put("encaissementVersé", 0);
          fullYearData.add(emptyData);
        }
      }
    } else {
      YearMonth yearMonth = YearMonth.of(year, month);
      int maxDay =
          (year == currentYear && month == currentMonth) ? currentDay : yearMonth.lengthOfMonth();

      List<Object[]> results =
          encaissementRepository.calculateChiffreAffaireForDaysInMonth(year, month);
      Map<Integer, Map<String, Object>> dayDataMap = new HashMap<>();
      for (Object[] result : results) {
        int resultDay = ((Number) result[0]).intValue();
        Map<String, Object> data = new HashMap<>();
        data.put("year", year);
        data.put("month", month);
        data.put("day", resultDay);
        data.put("chiffreAffaireBrute", result[1] != null ? ((Number) result[1]).doubleValue() : 0);
        data.put("chiffreAffaireNette", result[2] != null ? ((Number) result[2]).doubleValue() : 0);
        data.put("encaissementVersé", result[3] != null ? ((Number) result[3]).doubleValue() : 0);
        dayDataMap.put(resultDay, data);
      }

      for (int i = 1; i <= maxDay; i++) {
        if (dayDataMap.containsKey(i)) {
          fullYearData.add(dayDataMap.get(i));
        } else {
          Map<String, Object> emptyData = new HashMap<>();
          emptyData.put("year", year);
          emptyData.put("month", month);
          emptyData.put("day", i);
          emptyData.put("chiffreAffaireNette", 0);
          emptyData.put("chiffreAffaireBrute", 0);
          emptyData.put("encaissementVersé", 0);
          fullYearData.add(emptyData);
        }
      }
    }
    return fullYearData;
  }

  @GetMapping("/encaissementEtAvoir")
  @ResponseBody
  public List<Map<String, Object>> getEncaissementAvoirData(@RequestParam("year") int year,
      @RequestParam(value = "month", required = false) Integer month) {
    List<Map<String, Object>> fullYearData = new ArrayList<>();
    LocalDate currentDate = LocalDate.now();
    int currentYear = currentDate.getYear();
    int currentMonth = currentDate.getMonthValue();
    int currentDay = currentDate.getDayOfMonth();
    if (month == null) {
      List<Object[]> results = encaissementRepository.calculateEncaissementTotalAndNonVerse(year);

      Map<Integer, Map<String, Object>> monthDataMap = new HashMap<>();
      for (Object[] result : results) {
        Integer resultMonth = (Integer) result[1];
        Map<String, Object> data = new HashMap<>();
        data.put("year", result[0]);
        data.put("month", resultMonth);
        data.put("MontantPayéNonversé", result[2]);
        data.put("MontantPayéVersé", result[3]);
        monthDataMap.put(resultMonth, data);
      }
      int maxMonth = (year == currentYear) ? currentMonth : 12;
      for (int i = 1; i <= maxMonth; i++) {
        if (monthDataMap.containsKey(i)) {
          fullYearData.add(monthDataMap.get(i));
        } else {
          Map<String, Object> emptyData = new HashMap<>();
          emptyData.put("year", year);
          emptyData.put("month", i);
          emptyData.put("MontantPayéVersé", 0);
          emptyData.put("MontantPayéNonversé", 0);
          fullYearData.add(emptyData);
        }
      }
    } else {
      YearMonth yearMonth = YearMonth.of(year, month);
      int maxDay =
          (year == currentYear && month == currentMonth) ? currentDay : yearMonth.lengthOfMonth();

      List<Object[]> results =
          encaissementRepository.calculateencaissementtotalANDNonVerseDaysInMonth(year, month);


      Map<Integer, Map<String, Object>> dayDataMap = new HashMap<>();
      for (Object[] result : results) {
        int resultDay = ((Number) result[0]).intValue();
        Map<String, Object> data = new HashMap<>();
        data.put("year", year);
        data.put("month", month);
        data.put("day", resultDay);
        data.put("MontantPayéNonversé", result[1] != null ? ((Number) result[1]).doubleValue() : 0);
        data.put("MontantPayéVersé", result[2] != null ? ((Number) result[2]).doubleValue() : 0);

        dayDataMap.put(resultDay, data);
      }

      for (int i = 1; i <= maxDay; i++) {
        if (dayDataMap.containsKey(i)) {
          fullYearData.add(dayDataMap.get(i));
        } else {
          Map<String, Object> emptyData = new HashMap<>();
          emptyData.put("year", year);
          emptyData.put("month", month);
          emptyData.put("day", i);
          emptyData.put("MontantPayéVersé", 0);
          emptyData.put("MontantPayéNonversé", 0);
          fullYearData.add(emptyData);
        }
      }
    }
    return fullYearData;
  }

  @GetMapping("/financials")
  @ResponseBody
  public List<Map<String, Object>> getFinancialData(@RequestParam("year") int year,
      @RequestParam(value = "userId", required = false) Long userId,
      @RequestParam(value = "revId", required = false) Long revId,
      @RequestParam(value = "typeUser", required = false) String typeUser) {
    List<Map<String, Object>> results =
        encaissementRepository.getMonthlyFinancialsForYear(year, userId, revId, typeUser);
    List<Map<String, Object>> bordereauResults =
        encaissementRepository.countBordereauVersements(year);
    Map<Integer, Map<String, Object>> mergedData = new HashMap<>();
    for (Map<String, Object> result : results) {
      int month = (int) result.get("month");
      Map<String, Object> newData = new HashMap<>(result);

      mergedData.put(month, newData);
    }
    for (Map<String, Object> bordereauResult : bordereauResults) {
      int month = (int) bordereauResult.get("month");
      float bordereauCount = ((Number) bordereauResult.get("bordereauCount")).floatValue();
      if (mergedData.containsKey(month)) {
        mergedData.get(month).put("bordereauCount", bordereauCount);
      } else {
        Map<String, Object> newData = new HashMap<>();
        newData.put("year", year);
        newData.put("month", month);
        newData.put("totalFactureVerse", null);
        newData.put("totalFactureNonverse", null);
        newData.put("TotalFactureImpNoEch", null);
        newData.put("totalfactureImpEch", null);
        newData.put("totalAvoir", null);
        newData.put("bordereauCount", bordereauCount);

        mergedData.put(month, newData);
      }
    }
    List<Map<String, Object>> fullYearData = new ArrayList<>();
    LocalDate currentDate = LocalDate.now();
    int maxMonth = (year == currentDate.getYear()) ? currentDate.getMonthValue() : 12;

    for (int i = 1; i <= maxMonth; i++) {
      if (mergedData.containsKey(i)) {
        fullYearData.add(mergedData.get(i));
      } else {
        Map<String, Object> emptyData = new HashMap<>();
        emptyData.put("year", year);
        emptyData.put("month", i);
        emptyData.put("totalFactureVerse", null);
        emptyData.put("totalFactureNonverse", null);
        emptyData.put("TotalFactureImpNoEch", null);
        emptyData.put("totalfactureImpEch", null);
        emptyData.put("totalAvoir", null);
        emptyData.put("bordereauCount", null);
        fullYearData.add(emptyData);
      }
    }
    return fullYearData;
  }

  @RequestMapping(method = RequestMethod.POST, value = "BordereauCreation")
  @ResponseBody

  public Long bordereauCreation(Model model, String factureliste)
      throws IOException, DocumentException {

    return bordereauService.BordereauCreation(factureliste);

  }

  @RequestMapping(method = RequestMethod.GET, value = "/RevendeursStats")
  @ResponseBody
  public List<Map<String, Object>> revendeurStats(
      @RequestParam(required = false) Long distributeur) {
    List<Object[]> results = encaissementRepository.findRevendeurStatsByChefSecteur(distributeur);
    List<Map<String, Object>> response = new ArrayList<>();

    for (Object[] result : results) {
      Map<String, Object> map = new HashMap<>();
      int activeRevendeurCount = ((Number) result[2]).intValue();
      int totalRevendeurCount = ((Number) result[3]).intValue();
      int inactiveRevendeurCount = ((Number) result[4]).intValue();
      float montantpayéenonversé;
      if (result[5] != null) {
        montantpayéenonversé = ((Number) result[5]).floatValue();
      } else {
        montantpayéenonversé = 0;
      }
      String percentActiv = "0%";
      String percentInactiv = "0%";
      if (totalRevendeurCount != 0) {
        percentActiv =
            String.format("%.2f", (activeRevendeurCount * 100.0 / totalRevendeurCount)) + "%";
        percentInactiv =
            String.format("%.2f", (inactiveRevendeurCount * 100.0 / totalRevendeurCount)) + "%";
      }
      map.put("chefSecteurId", result[0]);
      map.put("chefSecteurName", result[1]);
      map.put("activeRevendeurCount", activeRevendeurCount);
      map.put("totalRevendeurCount", totalRevendeurCount);
      map.put("inactiveRevendeurCount", inactiveRevendeurCount);
      map.put("percentActiv", percentActiv);
      map.put("percentInactiv", percentInactiv);
      map.put("montantpayéenonversé", montantpayéenonversé);
      response.add(map);
    }
    return response;
  }

  @GetMapping("/montantPayéNonVersé")
  @ResponseBody
  public List<Map<String, Object>> showMontantPayéNonVersé(
      @RequestParam(required = false) Long distributeur) {
    List<Map<String, Object>> montantPayéNonVersé =
        encaissementRepository.findMontantPayéNonVerséByChefSecteur(distributeur);

    return montantPayéNonVersé;

  }

  @RequestMapping(path = "/imprimerBordereauA4/{id}")
  public void downloadPDFBordereauA4(HttpServletResponse response, @PathVariable("id") Long id,
      Model model) throws JRException, IOException, DocumentException {
    // cette methode pour telecharge pdf de une facture
    // enregistrer sur la base
    try {

      // vérifier si c'est la premier fais
      File fileBordereau = ResourceUtils.getFile(pathBordereau + id + "/" + "b_d_R_" + id + ".pdf");
      if (!fileBordereau.exists()) {
        fileBordereau = this.bordereauService.createPDFBordereauA4(id);
      }

      response
          .setContentType("application/x-pdf ; charset=" + StandardCharsets.UTF_8.displayName());
      response.setHeader("Content-disposition",
          "inline; filename=b_d_R_" + fileBordereau.getName());
      // get your file as InputStream
      InputStream targetStream = new FileInputStream(fileBordereau);
      // copy it to response's OutputStream
      org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
      response.flushBuffer();
    } catch (IOException ex) {

      logger
          .error("BordereauController.downloadPDFBordereauA4:" + id + "erreur:" + ex.getMessage());

      throw new RuntimeException("IOError writing file to output stream");
    } catch (Exception e) {
      logger.error("BordereauController.downloadPDFBordereauA4:" + id + "erreur:" + e.getMessage());

    }

  }


  @PreAuthorize("hasAuthority('READ_SLIP_LIST_RETAIL')" + "|| hasAuthority('READ_SLIP_LIST')"
      + "|| hasAuthority('READ_SLIP_HISTORY_AREA')")
  @RequestMapping(method = RequestMethod.GET, value = "viewlisteBordereau")
  public String viewlistepayement(Model model) {

    userService.returnInfoUserConnected(model);
    List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
    model.addAttribute("gouvernorats", listGouvernorats);

    return "bordereaux/Listebordereaux";
  }

  @RequestMapping(method = RequestMethod.GET, value = "listeBordereau")
  @ResponseBody
  public HashMap<String, Object> listepathBordereau(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    return bordereauService.listepathBordereau(draw, start, length, search, filterrecherche);
  }

  // @PreAuthorize("hasAuthority('RESILIATION_ABONNEMENT')")
  @RequestMapping(method = RequestMethod.GET, value = "editbordereaux/{bordereauid}")
  public String editbordereaux(@PathVariable(value = "bordereauid") Long id, Model model) {
    userService.returnInfoUserConnected(model);
    bordereauService.getBordereau(id, model);
    return "bordereaux/editbordereaux";

  }

  @RequestMapping(method = RequestMethod.GET, value = "detailsbordereaux/{bordereauid}")
  public String detailsbordereaux(@PathVariable(value = "bordereauid") Long id, Model model) {
    userService.returnInfoUserConnected(model);
    bordereauService.getBordereau(id, model);
    return "bordereaux/detailsbordereau";

  }

  @PostMapping("/addjustification")
  public String addpaymenttobordereaux(Long idbordereau,
      @RequestParam(value = "imageFile2") MultipartFile imageFile2,
      RedirectAttributes redirectAttrs) {
    final String MESSAGE = "message";
    if (imageFile2.isEmpty()) {
      redirectAttrs.addFlashAttribute(MESSAGE, "fileempty");
      return "redirect:/bordereaux/editbordereaux/" + idbordereau;
    } else {
      bordereauService.addJustificatifBordereaux(imageFile2, idbordereau);
      redirectAttrs.addFlashAttribute(MESSAGE, "fileuploded");
    }

    redirectAttrs.addFlashAttribute(MESSAGE, "justificationBordereaux");
    return "redirect:/bordereau/viewlisteBordereau";

  }

  @PostMapping("/anulationBordereaux")
  public String anulationpathBordereaux(Long idbordereau, String commentaire,
      RedirectAttributes redirectAttrs) {
    bordereauService.anulationPathBordereaux(idbordereau, commentaire);
    redirectAttrs.addFlashAttribute("message", "anulationpathBordereaux");

    return "redirect:/bordereau/viewlisteBordereau";

  }

  @PostMapping("/validationBordereaux")
  public String validationpathBordereaux(Long idbordereau, String commentaire, String dateVersement,
      RedirectAttributes redirectAttrs) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userService.findUsersByEmail(currentUser);
    Bordereau bordereau = bordereauService.findBordereauxById(idbordereau);
    bordereauService.accpetBordereauByAdmin(bordereau, user, commentaire, dateVersement, null,
        redirectAttrs);



    return "redirect:/bordereau/viewlisteBordereau";

  }

  @PreAuthorize("hasAuthority('READ_SLIP_HISTORY')" + "|| hasAuthority('READ_SLIP_HISTORY_AREA')")
  @RequestMapping(method = RequestMethod.GET, value = "viewHistoriqueListeBordereau")
  public String viewHistoriqueListeBordereau(Model model) {
    userService.returnInfoUserConnected(model);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    List<User> User = new ArrayList<User>();
    if (StringsRole.contains("READ_SLIP_HISTORY")) {
      User = userRepository.findUsersByTypeUserNotIn(
          Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
    } else if (StringsRole.contains("READ_SLIP_HISTORY_AREA")) {
      User = userRepository.findUsersByAffectedTo(user.getUserid());
    }
    model.addAttribute("AffectedTo", User);
    return "bordereaux/HistoriqueListeBordereau";
  }

  @PreAuthorize("hasAuthority('READ_SLIP_HISTORY')" + "|| hasAuthority('READ_SLIP_HISTORY_AREA')")

  @RequestMapping(method = RequestMethod.GET, value = "HistoriqueListeBordereau")
  @ResponseBody
  public HashMap<String, Object> HistoriqueListeBordereau(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) int ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    final String NUMEROBORDEREAU = "NumeroBordereau";
    final String STATUS = "Status";
    final String USERCODE = "userCode";
    final String AFFCTEDTO = "AffecterTo";
    final String DATEDEBUT = "dateDebut";
    final String DATEFIN = "dateFin";

    String currentUser = authentication.getName();
    User user = userService.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    int currentpage = start / length;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    Page<ListeBordereau> responseData = null;
    String numeroBordereau = null;
    String status = null;
    String userCode = null;
    String affecterTo = null;
    Date dateDebut = null;
    Date dateFin = null;
    
    Date datevalideDebut = null ;
    Date datevalideFin = null ; 
    if (search != null && !search.equals("")) {
      JSONObject obj = new JSONObject(search);

      if (obj.has(NUMEROBORDEREAU) && !Objects.equals(obj.getString(NUMEROBORDEREAU), "")
          && obj.getString(NUMEROBORDEREAU) != null) {
        numeroBordereau = obj.getString(NUMEROBORDEREAU).trim();
      }
      if (obj.has(STATUS) && !Objects.equals(obj.getString(STATUS), "")
          && obj.getString(STATUS) != null) {
        status = obj.getString(STATUS).trim();
      }

      if (obj.has(USERCODE) && !Objects.equals(obj.getString(USERCODE), "")
          && obj.getString(USERCODE) != null) {
        userCode = obj.getString(USERCODE).trim();
      }

      if (obj.has(AFFCTEDTO) && !Objects.equals(obj.getString(AFFCTEDTO), "")
          && obj.getString(AFFCTEDTO) != null) {
        affecterTo = obj.getString(AFFCTEDTO).trim();
      }

      if (obj.has(DATEDEBUT) && !Objects.equals(obj.getString(DATEDEBUT), "")
          && obj.getString(DATEDEBUT) != null) {
        dateDebut = CrmUtils.convertStringToDate(obj.getString(DATEDEBUT));
      }
      if (obj.has(DATEFIN) && !Objects.equals(obj.getString(DATEFIN), "")
          && obj.getString(DATEFIN) != null) {
        dateFin = CrmUtils.convertStringToDate(obj.getString(DATEFIN));
      }
      
      if (obj.has("datevalideDebut") && !Objects.equals(obj.getString("datevalideDebut"), "")
              && obj.getString(DATEDEBUT) != null) {
    	  datevalideDebut = CrmUtils.convertStringToDate(obj.getString("datevalideDebut"));
          }
          if (obj.has("datevalideFin") && !Objects.equals(obj.getString("datevalideFin"), "")
              && obj.getString(DATEFIN) != null) {
        	  datevalideFin = CrmUtils.convertStringToDate(obj.getString("datevalideFin"));
          }
        
    }
    String sort = "";

    switch (ordercolumnaram) {

      case 0:
        sort = "created_date";
        break;
      case 1:
        sort = "reference_bordereau";

        break;
      case 2:
        sort = "numfacure";
        break;
      case 3:
        sort = "montant";
        break;
      case 4:
        sort = "status";
        break;
      case 5:
        sort = "commentaire";
        break;
      case 6:
        sort = "user_id";

        break;
      case 7:
        sort = "check_by";
        break;

    }
    if (StringsRole.contains("READ_SLIP_HISTORY"))
      responseData = bordereauService.AdminHistoriqueBordereau(currentpage + 1, length,
          numeroBordereau, status, userCode, affecterTo, dateDebut, dateFin,datevalideDebut,datevalideFin, sort, orderdir);
    else if (StringsRole.contains("READ_SLIP_HISTORY_AREA"))
      responseData = bordereauService.findBordereaubyDistributeur(currentpage + 1, length,
          user.getUserid().toString(), numeroBordereau, userCode, affecterTo, dateDebut, dateFin,datevalideDebut,datevalideFin,
          sort, orderdir);

    if (responseData != null)
      myGreetings.put("data", responseData.getContent());
    else
      myGreetings.put("data", null);
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    return myGreetings;

  }

  @RequestMapping(value = "/ajouterAvoir")
  public String ajouterAvoir(@RequestParam("avoirIdsField") String avoirIds,
      @RequestParam("bordereauIdField") Long bordereauId, Model model,
      RedirectAttributes redirectAttrs, HttpServletRequest request) {

    logger.info(
        "Ajouter avoir a bordereau : Methode ajouterAvoir appelee avec les parametres : avoirIds={}, bordereauId={}",
        avoirIds, bordereauId);

    List<Long> idList =
        Arrays.stream(avoirIds.split(",")).map(Long::parseLong).collect(Collectors.toList());

    Bordereau bordereau = bordereauService.findBordereauxById(bordereauId);
    List<AvoirClient> listAvoir = avoirRepository.findAllAvoirbyListeId(idList);

    for (AvoirClient av : listAvoir) {
      // AvoirClient avoir = avoirRepository.getById(av.getAvoirId());
      if (av.getHas_bordereau() == true) {
        redirectAttrs.addFlashAttribute("message", "ADD_AVOIR_ERROR");
        redirectAttrs.addAttribute("bordereauId", bordereauId);
        return "redirect:/bordereau/editbordereaux/{bordereauId}";
      }
    }

    Double montant = bordereau.getMontant();

    for (AvoirClient av : listAvoir) {
      Encaissement en = encaissementRepository.getByAvoirId(av.getAvoirId());
      en.setIdbordaureau(bordereau);
      en.setHasBordereau(true);
      encaissementRepository.save(en);
      en.getAvoirClient().setHas_bordereau(true);
      avoirRepository.save(en.getAvoirClient());
      EntryBordereau entry = new EntryBordereau();
      entry.setEncaissement(en);
      bordereau.getEntry().add(entryBordereauRepository.save(entry));
      montant -= av.getMontantAvoir();
    }
    bordereau.setnumfacure(bordereau.getNumfacure() + listAvoir.size());
    bordereau.setmontant(montant);

    bordereaurRepository.save(bordereau);

    logger.info("Ajouter avoir a bordereau : Avoir ajoute avec succes. AvoirIds={}, BordereauId={}",
        avoirIds, bordereauId);

    redirectAttrs.addFlashAttribute("message", "ADD_AVOIR");
    redirectAttrs.addAttribute("bordereauId", bordereauId);
    return "redirect:/bordereau/editbordereaux/{bordereauId}";

  }

  @GetMapping("/deleteAvoirByBordereau/{bordereauId}/{avoirId}")
  public String deleteAvoirByBordereau(@PathVariable Long bordereauId, @PathVariable Long avoirId,
      RedirectAttributes redirectAttrs, HttpServletRequest request) {
    bordereauService.deleteAvoirByBordereau(bordereauId, avoirId);
    redirectAttrs.addFlashAttribute("message", "DELETE_AVOIR");
    return "redirect:/bordereau/editbordereaux/{bordereauId}";
  }

  @GetMapping("/demandeAvance")
  public String demandeAvanceBordereau(@RequestParam("idbordereau") Long idbordereau,
      RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    String resultDemande = bordereauService.demandeAvanceBordereau(user, idbordereau);
    if (resultDemande.equals("true")) {
      redirectAttrs.addFlashAttribute("message", "AVANCE_DEMANDER");

    } else {
      redirectAttrs.addFlashAttribute("message", "AVANCE_DEJA_DEMANDER");

    }
    return "redirect:/bordereau/editbordereaux/" + idbordereau;
  }
}
