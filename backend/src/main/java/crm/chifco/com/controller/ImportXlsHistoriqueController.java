package crm.chifco.com.controller;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import crm.chifco.com.model.ImportXlsHistory;
import crm.chifco.com.model.ImportXlsHistoryFile;
import crm.chifco.com.model.ImportXlsHistoryFileReclamation;
import crm.chifco.com.model.ImportXlsHistoryReclamation;
import crm.chifco.com.model.ModemHistoryImport;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ModemHistoryImportFileRepository;
import crm.chifco.com.repository.ModemHistoryImportRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.ImportXlsHistoryFileService;
import crm.chifco.com.service.ImportXlsHistoryFileServiceReclamation;
import crm.chifco.com.service.ImportXlsHistoryReclamationService;
import crm.chifco.com.service.ImportXlsHistoryService;
import crm.chifco.com.service.UserService;

@Controller
@RequestMapping(value = "historyXls/*")
public class ImportXlsHistoriqueController {

  @Autowired
  ImportXlsHistoryService ImportXlsHistoryService;
  @Autowired
  ImportXlsHistoryReclamationService ImportXlsHistoryReclamationService;

  @Autowired
  UserService UserService;

  @Autowired
  ImportXlsHistoryFileService ImportXlsHistoryFileService;
  @Autowired
  ImportXlsHistoryFileServiceReclamation ImportXlsHistoryFileServiceReclamation;

  @Autowired
  ModemHistoryImportRepository modemImportXlsHistoryRepository;

  @Autowired
  ModemHistoryImportFileRepository ligneModemImportXlsHistoryRepository;
  @Autowired
  UserRepository userRepository;

  @RequestMapping(method = RequestMethod.GET, value = "getAlldetailHistoryXls")
  @ResponseBody
  public HashMap<String, Object> getAlldetailHistoryXls(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) String ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    int currentpage = start / length;
    HashMap<String, Object> myGreetings = new HashMap<>();
    Page<ImportXlsHistory> responseData = null;
    responseData = ImportXlsHistoryService.getallImportXlsHistory(currentpage + 1, length);
    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;
  }

  @RequestMapping(method = RequestMethod.GET, value = "getAllHistoryXls")
  @ResponseBody
  public HashMap<String, Object> getAllHistoryXls(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) String ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam(value = "columns[0][search][value]", required = false) String filterrecherche) {
    int currentpage = start / length;
    HashMap<String, Object> myGreetings = new HashMap<>();
    Page<ImportXlsHistoryFile> responseData = null;
    responseData = ImportXlsHistoryFileService.getallImportXlsHistory(currentpage + 1, length);
    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;
  }

  @GetMapping(value = "viewhistoriqueXls")
  public String clients(Model model, HttpServletRequest request) {

    UserService.returnInfoUserConnected(model);

    return "historique/historiqueXls";
  }

  @GetMapping(value = "viewdetailHistoryXls/{xlsid}")
  public String viewdetailHistoryXls(@PathVariable("xlsid") Long xlsid, Model model,
      HttpServletRequest request) {
    UserService.returnInfoUserConnected(model);
    ImportXlsHistoryFile importXlsHistoryFile =
        ImportXlsHistoryFileService.getImportXlsHistoryById(xlsid);
    List<ImportXlsHistory> ImportXlsHistory =
        ImportXlsHistoryService.getImportXlsHistoryById(xlsid);
    model.addAttribute("importXlsHistoryFile", importXlsHistoryFile);
    model.addAttribute("ImportXlsHistory", ImportXlsHistory);
    return "historique/detailshistoryxls";

  }

  @PreAuthorize("hasAnyAuthority('READ_MODEM_IMPORT_HISTORY')")
  @GetMapping(value = "modemhistorique")
  public String getModemHistorique(Model model, HttpServletRequest request) {

    UserService.returnInfoUserConnected(model);
    return "historique/historique_modem";
  }

  @GetMapping(value = "getAllModemHistorique")
  @ResponseBody
  public HashMap<String, Object> getAllHistoriqueModem(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) String ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam(value = "columns[0][search][value]", required = false) String filterrecherche) {
    int currentpage = start / length;
    HashMap<String, Object> myGreetings = new HashMap<>();
    // List<ModemImportXlsHistory> modemList = modemImportXlsHistoryRepository.findAll();
    Page<ModemHistoryImport> responseData = null;
    Pageable pageable = PageRequest.of(currentpage, length);
    responseData = modemImportXlsHistoryRepository.findAll(pageable);
    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;
  }

  @GetMapping(value = "modemhistoriqueDetails/{xlsid}")
  public String getModemHistoriqueDetails(@PathVariable("xlsid") Long xlsid, Model model,
      HttpServletRequest request) {

    UserService.returnInfoUserConnected(model);
    ModemHistoryImport ligneModem = modemImportXlsHistoryRepository.getById(xlsid);
    /*
     * List<LigneModemImportXlsHistory> LigneModem =
     * ligneModemImportXlsHistoryRepository.findByModemImportXlsHistoryId(xlsid);
     */
    model.addAttribute("modem", ligneModem);
    model.addAttribute("ligneModem", ligneModem.getLigneModemImportXlsHistories());
    return "historique/historique_modem_ligne";
  }

  @GetMapping(value = "viewhistoriqueXlsReclamation")
  public String reclamationtt(Model model, HttpServletRequest request) {

    UserService.returnInfoUserConnected(model);
    List<User> creepar = userRepository.findUsersByPrivilegeName("IMPORT_TECH_CLIENT_COMPLAINT");
    model.addAttribute("creepar", creepar);
    return "historique/historiqueXlsReclamation";
  }

  @RequestMapping(method = RequestMethod.GET, value = "getAllHistoryReclamationXls")
  @ResponseBody
  public HashMap<String, Object> getAllHistoryReclamationXls(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) String ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam(value = "columns[0][search][value]", required = false) String filterrecherche) {
    int currentpage = start / length;
    HashMap<String, Object> myGreetings = new HashMap<>();
    Page<ImportXlsHistoryFileReclamation> responseData = null;
    responseData = ImportXlsHistoryFileServiceReclamation.getallImportXlsHistory(filterrecherche,
        currentpage + 1, length);
    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;
  }

  @GetMapping(value = "viewdetailHistoryXlsReclamation/{xlsid}")
  public String viewdetailHistoryXlsReclamation(@PathVariable("xlsid") Long xlsid, Model model,
      HttpServletRequest request) {
    UserService.returnInfoUserConnected(model);
    ImportXlsHistoryFileReclamation importXlsHistoryFile =
        ImportXlsHistoryFileServiceReclamation.getImportXlsHistoryById(xlsid);
    List<ImportXlsHistoryReclamation> ImportXlsHistory =
        ImportXlsHistoryReclamationService.getImportXlsHistoryById(xlsid);
    model.addAttribute("importXlsHistoryFile", importXlsHistoryFile);
    model.addAttribute("ImportXlsHistory", ImportXlsHistory);
    return "historique/detailshistoryreclamationxls";

  }

}
