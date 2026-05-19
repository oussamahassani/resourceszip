package crm.chifco.com.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.DTOclass.EntryTvaFactureDataDTO;
import crm.chifco.com.DTOclass.ExtractionFactureDTO;
import crm.chifco.com.DTOclass.FactureDataDTO;
import crm.chifco.com.DTOclass.MoreThanOneInvoiceRecap;
import crm.chifco.com.DTOclass.PayementDataDTO;
import crm.chifco.com.DTOclass.RecapFactureLimitDTO;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.EntryFactures;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.BordereaurRepository;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.EntryTvaFactureRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.PayementRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AvoirService;
import crm.chifco.com.service.EncaissementService;
import crm.chifco.com.service.FactureService;
import crm.chifco.com.service.RapportExportExcelService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.templateclasse.FactureNonPayee;
import crm.chifco.com.templateclasse.Recouvrement;
import crm.chifco.com.templateclasse.SumRecouvrement;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.PrefixDocument;

@Controller
public class FactureController {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  private FactureService factureService;

  @Autowired
  private FactureRepository factureRepository;

  @Autowired
  private PayementRepository payementRepository;

  @Autowired
  private AvoirRepository avoirRepository;
  @Autowired
  private EncaissementRepository encaissementRepository;

  @Autowired
  private EntryTvaFactureRepository entryTvaFactureRepository;

  @Autowired
  UserRepository userRepository;

  @Value("${pathFacture}")
  private String pathFacture;

  @Autowired
  private AvoirService avoirService;

  @Autowired
  private UserService userService;
  @Autowired
  private EncaissementService encaissementService;
  @Autowired
  private BordereaurRepository bordereauRepository;
  @Autowired
  private RapportExportExcelService exportService;

  @RequestMapping(method = RequestMethod.GET, value = "factureliste")
  @ResponseBody
  public HashMap<String, Object> getfactureliste(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) Long ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    Page<FactureDataDTO> responseData = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    Long idconnected = user.getUserid();

    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    int currentpage = start / length;
    String sort = "modifiedDate";
    if (orderdir == null)
      orderdir = "desc";
    if (StringsRole.contains("READ_INVOICE_ALL")) {
      responseData = factureService.findPaginateFacture(currentpage + 1, length, sort, orderdir,
          filterrecherche, false);// fRepo.findAll();

    } else if (StringsRole.contains("READ_FACTURE_PERSO")) { // factures
                                                             // =fRepo.listFactureByConnecteduser(idconnected);

      responseData = factureService.findByConnecteduserAndVisibility(currentpage + 1, length, sort,
          orderdir, filterrecherche, idconnected, true);

    }

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());

    return myGreetings;
  }

  @RequestMapping(method = RequestMethod.GET, value = "factureProformatListe")
  @ResponseBody
  public HashMap<String, Object> getfactureProformatListe(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) Long ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    Page<FactureDataDTO> responseData = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    Long idconnected = user.getUserid();

    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    int currentpage = start / length;
    String sort = "modifiedDate";
    if (orderdir == null)
      orderdir = "desc";
    if (StringsRole.contains("READ_INVOICE_PROFORMAT")) {
      responseData = factureService.findPaginateFacture(currentpage + 1, length, sort, orderdir,
          filterrecherche, true);// fRepo.findAll();
    }
    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());

    return myGreetings;
  }

  // ******************************************** api pour afficher la liste des
  // factures
  @RequestMapping(value = "/facture")
  public String factures(Model model) {

    Long idconnected = null;
    String role = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      idconnected = user.getUserid();
      role = user.getRole().getRoleName();
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

    }

    return "facture/facture";
  }

  @RequestMapping(value = "/facturePoformat")
  public String facturePoformat(Model model) {

    Long idconnected = null;
    String role = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);

      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      idconnected = user.getUserid();
      role = user.getRole().getRoleName();
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());

    }

    return "facture/proformatFacture";
  }

  @PreAuthorize("hasAuthority('INVOICE_PAYMENT')")
  @RequestMapping(path = {"/paymentFacture/{id}"})
  public String paymentFactureFacureById(Model model, @PathVariable("id") Optional<Long> id)
      throws Exception {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }
    Double montant_total = 0.0;
    Facture facture = factureService.getFactureById(id.get());
    if (facture != null) {
      montant_total = facture.getMontant_payer();
      List<AvoirClient> listAvoir =
          avoirRepository.getallAvoirNonPayeeByClient(facture.getAbonnement().getClientid());
      AtomicReference<Double> montant_totalAvoir = new AtomicReference<>(0.0);

      listAvoir.forEach(avoir -> {
        montant_totalAvoir.updateAndGet(currentValue -> currentValue + avoir.getMontantAvoir());



      });
      montant_total = montant_total - montant_totalAvoir.get();
      model.addAttribute("listAvoir", listAvoir);

    }

    Abonnement abonnement = facture.getCommande().getClient();
    model.addAttribute("facture", facture);
    model.addAttribute("montant_total", montant_total);

    model.addAttribute("abonnement", abonnement);
    model.addAttribute("produit", abonnement.getPack());

    return "facture/paymentFacture";
  }

  // ***************************************************** api pour la
  // visualisation d'une facture
  @RequestMapping(path = {"/facture/{ref}"})
  public String getfacture(Model model, @PathVariable("ref") Long id) {
    userService.returnInfoUserConnected(model);
    Facture facture = factureService.getFacture(id); // recuperer la facture

    Abonnement abonnement = facture.getCommande().getClient(); // recuperer
    // l'abonnement de la
    // facture
    Modem modem = abonnement.getModem(); // recuperer le modem de l'abonnement
    User user = facture.getUser(); // recuperer le client de l'abonnement
    model.addAttribute("facture", facture);
    model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
    model.addAttribute("userphoto", user.getPhoto());
    model.addAttribute("userrole", user.getRole().getRoleName());
    model.addAttribute("useremail", user.getEmail());
    model.addAttribute("abonnement", abonnement);
    model.addAttribute("produit", abonnement.getPack());
    model.addAttribute("modem", modem);
    model.addAttribute("user", user);

    return "facture/OpenFacture";
  }

  // ********************************************************** api pour imprimer
  // une facture en pdf

  @RequestMapping(path = "/imprimer_facture_A4/{id}")
  public void downloadPDFFactureA4(HttpServletResponse response, @PathVariable("id") Long id,
      Model model) throws Exception {

    try {
      Facture monFacture = this.factureService.getFacture(id);

      File fileFacture = new File(pathFacture + CrmUtils.getYear() + "/" + CrmUtils.getMonth() + "/"
          + PrefixDocument.NOMEFILE_FACTURE + monFacture.getRef_facture() + ".pdf");
      if (!fileFacture.exists()) {

        fileFacture = this.factureService.createPDFFactureA4(monFacture);
      }
      // set file facure

      response
          .setContentType("application/x-pdf ; charset=" + Charset.forName("utf-8").displayName());
      response.setHeader("Content-disposition", "inline; filename=" + fileFacture.getName());
      // get your file as InputStream
      InputStream targetStream = new FileInputStream(fileFacture);
      // copy it to response's OutputStream
      org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
      response.flushBuffer();

      // close input stream file
      targetStream.close();
      // delete file
      // CrmUtils.deleteFile(file);

    } catch (IOException ex) {

      logger.error("FactureController.downloadPDFFactureA4 IOException: " + ex.getMessage());
      throw new RuntimeException("IOError writing file to output stream");
    }

  }

  // ********************************************************* api pour exporter
  // la liste des modems
  private static String[] convertToStringArray(Object[] array) {
    String[] stringArray = new String[array.length];
    for (int i = 0; i < array.length; i++) {
      stringArray[i] = String.valueOf(array[i]);
    }
    return stringArray;
  }

  @RequestMapping("/factureexporterData")
  // @ResponseBody
  public void exportToExcel(HttpServletResponse response, HttpServletRequest request)
      throws IOException {

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=facture_info.xlsx"); // exporter
    List<ExtractionFactureDTO> listfact = new ArrayList<ExtractionFactureDTO>();
    List<PayementDataDTO> payementFacture = new ArrayList<PayementDataDTO>();
    List<Encaissement> EncaisementFacture = new ArrayList<Encaissement>();



    List<Long> listedes_ids = (List<Long>) request.getSession().getAttribute("liste_facture_ids");



    if (listedes_ids != null && listedes_ids.size() > 0) {
      List<List<Long>> batches = new ArrayList<>();
      int batchSize = 1500;
      // Diviser la liste en sous-listes de taille batchSize
      for (int i = 0; i < listedes_ids.size(); i += batchSize) {
        int end = Math.min(i + batchSize, listedes_ids.size());
        List<Long> batch = listedes_ids.subList(i, end);
        batches.add(batch);
      }

      // Parcourir les sous-listes et exécuter la requête pour chaque sous-liste
      for (List<Long> batch : batches) {
        System.out.println(batch.size());
        List<ExtractionFactureDTO> factures = factureRepository.findfactureByListFactureIdS(batch);
        listfact.addAll(factures);
        List<PayementDataDTO> payement = payementRepository.findPayementDataDTOByIds(batch);
        payementFacture.addAll(payement);
        List<Encaissement> encaisement = encaissementRepository.findByFacturesIds(batch);
        EncaisementFacture.addAll(encaisement);
      }
      // listfact = factureRepository.findfactureByListFactureIdS(listedes_ids);
    }
    // PrintWriter writer = response.getWriter();
    Workbook workbook = new XSSFWorkbook();

    // Feuille 1
    Sheet sheet1 = workbook.createSheet("Feuille1");


    // Feuille 2
    Sheet sheet2 = workbook.createSheet("Feuille2");


    // Define the headers for your CSV file (optional)

    List<Map<String, String>> myListMap = new ArrayList<>();
    try (PrintWriter writer = new PrintWriter("feuille1.csv")) {
      Row headerRow = sheet1.createRow(0);
      headerRow.createCell(0).setCellValue("Numéro Facture");
      headerRow.createCell(1).setCellValue("Total HT");
      headerRow.createCell(2).setCellValue("Total TVA");
      headerRow.createCell(3).setCellValue("Timbre");
      headerRow.createCell(4).setCellValue("Total TTC");
      headerRow.createCell(5).setCellValue("Date de création");
      headerRow.createCell(6).setCellValue("Date d'échéance");
      headerRow.createCell(7).setCellValue("Payé");
      headerRow.createCell(8).setCellValue("Date de paiement");
      headerRow.createCell(9).setCellValue("Date de versement");
      headerRow.createCell(10).setCellValue("Payeé par");
      headerRow.createCell(11).setCellValue("Numéro Bordereau");
      headerRow.createCell(12).setCellValue("statut Bordereau");
      headerRow.createCell(13).setCellValue("Payé par (Code)");
      headerRow.createCell(14).setCellValue("Code client");
      headerRow.createCell(15).setCellValue("Nom et Prenom client");
      headerRow.createCell(16).setCellValue("Numero fixe");
      headerRow.createCell(17).setCellValue("Distributeur");

      headerRow.createCell(18).setCellValue(" Code distributeur");
      headerRow.createCell(19).setCellValue("Taux Tva Pack");

      String ListetauxTvaTotal = "";
      String ListePrixtauxTvaTotal = "";

      int rowNum = 1;
      // Iterate over your data and write each row to the CSV file
      for (ExtractionFactureDTO obj : listfact) {
        String payee = "non";
        String DatePayement = "";
        String payeePar = "";
        String codeRevendeur = "";
        String numBrd = "";
        String statutBRD = "";
        String DateVersement = "";
        String tauxTva = "";
        String numFixeClient = "";
        String dateEcheneceFacture = "";

        for (EntryFactures entryFacture : obj.getEntriesFacture()) {
          if (entryFacture.getPack() != null && entryFacture.getProduit() == null) {
            tauxTva = entryFacture.getPourcentageTva().toString();
          }
        }
        List<EntryTvaFactureDataDTO> ListeentryTvaFacture =
            entryTvaFactureRepository.findEntrysTvaFacturesByFactureId(obj.getFactureId());
        // entryTvaFactureRepository.findEntrysTvaFacturesByFactureId(obj.getFactureId());
        for (EntryTvaFactureDataDTO entryTvaFacture : ListeentryTvaFacture) {
          Map<String, String> myMap = new HashMap<>();

          myMap.put("tva", entryTvaFacture.getTauxTva().toString());
          myMap.put("montant", entryTvaFacture.getMontant().toString());
          myMap.put("facture", obj.getRef_facture().toString());
          myMap.put("baseTva", entryTvaFacture.getBase().toString());

          myListMap.add(myMap);
        }

        if (obj.getEtat_facture() == true) {
          payee = "oui";
          PayementDataDTO mypayement =
              payementFacture.stream().filter(payment -> Objects.nonNull(payment.getFacture())
                  && obj.getFactureId().equals(payment.getFacture())).findFirst().orElse(null);
          if (mypayement != null) {
            DatePayement = mypayement.getCreatedDate().toString();
            payeePar = mypayement.getFirstNameUser() + mypayement.getLastNameUser() + "("
                + mypayement.getCodeUser() + ")";
            codeRevendeur = mypayement.getCodeUser();

            Encaissement myEncaisementFacture = EncaisementFacture.stream()
                .filter(enc -> mypayement != null && mypayement.getPayementid() != null
                    && enc.getPayement() != null
                    && mypayement.getPayementid().equals(enc.getPayement().getPayementid()))
                .findFirst().orElse(null);

            if (myEncaisementFacture != null) {
              if (myEncaisementFacture.getIdbordaureau() != null) {
                numBrd = myEncaisementFacture.getIdbordaureau().getReferenceBordereau();
                statutBRD = myEncaisementFacture.getIdbordaureau().getStatus();
              }

            }
          }

        }
        String UserFirstNameAndLastName = " ";
        if (obj.getClientFirstName() != null) {
          UserFirstNameAndLastName = obj.getFirstName() + ' ' + obj.getLastName();
        }
        if (obj.getDateDeVersment() != null) {
          DateVersement = obj.getDateDeVersment().toString();
        }
        if (obj.getNumeroFixeClient() != null) {
          numFixeClient = obj.getNumeroFixeClient().toString();
        }

        if (obj.getDate_echeance() != null) {
          dateEcheneceFacture = obj.getDate_echeance().toString();
        }
        // Convert the object fields to an array of strings
        String[] data = {obj.getRef_facture().toString(), obj.getMontantHt(), obj.getMontantTva(),
            obj.getTimbrefiscale(), obj.getMontant_payer(), obj.getCreatedDate().toString(),
            dateEcheneceFacture, payee, DatePayement, DateVersement, payeePar, numBrd, statutBRD,
            codeRevendeur, obj.getReferenceClient(),
            obj.getClientFirstName() + " " + obj.getClientLastName(), numFixeClient,
            obj.getDistribiteurfirstName() + " " + obj.getDistribiteurlastName(), obj.getCodeUser(),
            tauxTva, ListetauxTvaTotal, ListePrixtauxTvaTotal};


        Row row = sheet1.createRow(rowNum++);
        for (int i = 0; i < data.length; i++) {
          Cell cell = row.createCell(i);
          cell.setCellValue(data[i]);
        }

      }
      writer.println();
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }


    // Section 2 header
    try (PrintWriter writer = new PrintWriter("feuille2.csv")) {
      Row headerRow = sheet2.createRow(0);
      headerRow.createCell(0).setCellValue("Numéro Facture");
      headerRow.createCell(1).setCellValue(" Tva");
      headerRow.createCell(2).setCellValue("Montant Tva");
      headerRow.createCell(3).setCellValue("Base Tva");

      int rowNum = 1;
      for (

      Map<String, String> hmap : myListMap) {
        Row Row = sheet2.createRow(rowNum++);
        Row.createCell(0).setCellValue(hmap.get("facture").toString());
        Row.createCell(1).setCellValue(hmap.get("tva").toString());
        Row.createCell(2).setCellValue(hmap.get("montant").toString());
        Row.createCell(3).setCellValue(hmap.get("baseTva").toString());
      }
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Close the writer
    // writer.flush();
    // writer.close();
    workbook.write(response.getOutputStream());
    workbook.close();
    // ExportExcelfacture exp = new ExportExcelfacture(listfact); // initialiser un objet
    // ExportExcel
    // avec la liste des
    // modems ( la classe : ExportExcel , package
    // :service)
    // exp.export(response); // terminer l'exportation en appelant la methode export( la classe :
    // ExportExcel
    // , package :service)
    request.getSession().setAttribute("liste_facture_ids", null);

    // return "Export Successfully"; // afficher un message si l'exportation terminée avec succés
  }

  // ******************************************** api pour rechercher liste
  // factures

  @PreAuthorize("hasAuthority('INVOICE_PAYMENT')")
  @RequestMapping(value = "/facture/recherchefactures")
  public String recherchefactures(Model model) {

    // List<Facture> listfact = fRepo.findAll(); // recuperer tout les modems dans
    // le BDD

    userService.returnInfoUserConnected(model);
    return "facture/recherchefactures";
  }

  @PreAuthorize("hasAuthority('INVOICE_PAYMENT')")
  @RequestMapping(path = "/PaymentFacturesrecherche", method = RequestMethod.POST)
  @ResponseBody
  public HashMap<String, Object> PaymentFacturesrecherche(
      @RequestParam("recherche") String recherche) {
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    String referenceClient = null;
    Long telephone = null;
    if (CrmUtils.Isnumber(recherche)) {
      telephone = Long.parseLong(recherche);
      referenceClient = String.format("%06d", telephone);
    }

    if (recherche.contains("-")) {
      String[] strcodeclient = recherche.split("-");
      String lastnumbercodeclient = null;
      String year = null;
      if (strcodeclient.length > 2) {
        year = strcodeclient[1];
        lastnumbercodeclient = strcodeclient[2];
      } else {
        year = strcodeclient[0];
        lastnumbercodeclient = strcodeclient[1];
      }
      if (CrmUtils.Isnumber(lastnumbercodeclient))
        referenceClient = String.format("%06d", Long.parseLong(lastnumbercodeclient));
      if (CrmUtils.Isnumber(year))
        referenceClient = year + "-" + referenceClient;

    }
    List<Facture> factureNonPayee = factureService.findnonpayerfacture(recherche, telephone);
    List<AvoirClient> avoirNonConsomeé = new ArrayList<>();;
    if (!factureNonPayee.isEmpty()) {
      Double montantTotalFacture =
          factureNonPayee.stream().mapToDouble(el -> el.getMontant_payer()).sum();
      avoirNonConsomeé = avoirService.findnonpayerfactureAvoirNotgreatherThenFacture(recherche,
          telephone, montantTotalFacture);

    }
    myGreetings.put("dataFacture", factureNonPayee);
    myGreetings.put("dataAvoir", avoirNonConsomeé);
    return myGreetings;
  }

  @PreAuthorize("hasAuthority('READ_AGED_BALANCE')")
  @RequestMapping(path = {"/getrecouvrement"})
  public String getrecouvrement(Model model) {

    userService.returnInfoUserConnected(model);
    FactureNonPayee facturedata = factureService.findmontantFactureNonPayee();
    model.addAttribute("facturedata", facturedata);

    SumRecouvrement sumRecouvrementParDate = factureRepository.sumRecouvrementParDate();
    model.addAttribute("sumRecouvrementParDate", sumRecouvrementParDate);

    return "recouvrement/recouvrementfacture";
  }

  @PreAuthorize("hasAuthority('READ_AGED_BALANCE')")
  @RequestMapping(method = RequestMethod.GET, value = "getrecouvrementlist")
  @ResponseBody
  public HashMap<String, Object> getrecouvrementliste(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) Long ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam(value = "columns[0][search][value]", required = false) String filterrecherche) {
    List<Recouvrement> responseData = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);

    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    int currentpage = start / length;
    String sort = "modifieddate";
    responseData = factureService.findrecouvrementliste(currentpage + 1, length, sort, orderdir);
    List<Recouvrement> datarecouvrement = factureRepository.findallrecouvrement();
    myGreetings.put("data", responseData);
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", datarecouvrement.size());
    myGreetings.put("recordsFiltered", datarecouvrement.size());

    return myGreetings;
  }

  @ResponseBody
  @PostMapping(value = "/facture/addall")
  public List<Long> addAllIdToExport(@RequestBody String filterrecherche,
      HttpServletRequest request) {

    return factureService.addAllIdToExport(filterrecherche, request, false);

  }

  @ResponseBody
  @PostMapping(value = "/facture/addallProformat")
  public List<Long> addallProformatIdToExport(@RequestBody String filterrecherche,
      HttpServletRequest request) {

    return factureService.addAllIdToExport(filterrecherche, request, true);

  }

  @ResponseBody
  @PostMapping(value = "/facture/removeall")
  public JsonResponseBody removeAllFromListExport(HttpServletRequest request) {
    return factureService.removeAllFromListExport(request);
  }

  @ResponseBody
  @PostMapping(value = "/facture/addid")
  public JsonResponseBody addfiled(@RequestBody Long id, HttpServletRequest request) {
    return factureService.addfiled(id, request);
  }

  @ResponseBody
  @PostMapping(value = "/facture/removeid")
  public JsonResponseBody removefiled(@RequestBody Long id, HttpServletRequest request) {
    return factureService.removefiled(id, request);
  }

  @PostMapping("/recouvrement/extractenmasse")
  public ModelAndView exportToExcel(HttpServletRequest request) {
    return factureService.exportToExcelRecouvrement(request);
  }


  @RequestMapping(method = RequestMethod.GET, value = "/recapFactureLimit")
  @ResponseBody
  public HashMap<String, Object> recapFactureLimit(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) Long ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    int currentpage = start / length;
    Date CURRENT_DATE = new Date();
    Page<RecapFactureLimitDTO> responseData = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    responseData = encaissementService.getRevendeursWithSummaryForAdmin(currentpage + 1, length,
        CURRENT_DATE, filterrecherche);
    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    return myGreetings;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/clientWithMoreThanInvoice")
  @ResponseBody
  public HashMap<String, Object> clientWithMoreThanInvoice(@RequestParam("draw") int draw,
      @RequestParam("start") int start, @RequestParam("length") int length,
      @RequestParam("search[value]") String search,
      @RequestParam(value = "order[0][column]", required = false) Long ordercolumnaram,
      @RequestParam(value = "order[0][dir]", required = false) String orderdir,
      @RequestParam("columns[0][search][value]") String filterrecherche) {
    int currentpage = start / length;
    Page<MoreThanOneInvoiceRecap> responseData = null;
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    responseData = factureService.getTotalSumsFacturesNonPayerByClients(currentpage + 1, length,
        filterrecherche);
    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    return myGreetings;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/BordereauConfirm")
  @ResponseBody
  public Map<String, Object> chiffredaffaire(@RequestParam(required = false) Long userId,
      @RequestParam(required = false) Long revId, @RequestParam(required = false) String typeUser) {
    Map<String, Object> immutableResponse =
        bordereauRepository.calculateBordSumsWithoutDate(revId, null);
    Map<String, Object> response = new HashMap<>(immutableResponse);
    Double totalBordAllConfirm = (Double) response.get("totalBordAllConfirm");
    Double totalBordAllInstance = (Double) response.get("totalBordAllInstance");
    Double totalBordereau = totalBordAllConfirm + totalBordAllInstance;
    double percentageConfirm = 0;
    double percentageInstance = 0;
    if (totalBordereau != 0) {
      percentageConfirm = (totalBordAllConfirm / totalBordereau) * 100;
      percentageInstance = (totalBordAllInstance / totalBordereau) * 100;
    }
    response.put("percentageConfirm", percentageConfirm);
    response.put("percentageInstance", percentageInstance);
    response.put("totalBordereau", String.format("%.3f TND", totalBordereau));
    response.put("totalBordAllConfirm", String.format("%.3f TND", totalBordAllConfirm));
    response.put("totalBordAllInstance", String.format("%.3f TND", totalBordAllInstance));

    return response;
  }

  @PreAuthorize("hasAnyAuthority('EXPORT_RAPPORT_STATS')")
  @GetMapping("/rapport/export")
  public ResponseEntity<byte[]> exportRapport(@RequestParam int annee) throws IOException {
    byte[] excelContent = exportService.exportRapportToExcel(annee);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("attachment", "rapport_" + annee + ".xlsx");

    return ResponseEntity.ok().headers(headers).body(excelContent);
  }

  @PreAuthorize("hasAnyAuthority('EXPORT_RAPPORT_STATS')")
  @GetMapping("/rapport/exportByMonth")
  public ResponseEntity<byte[]> exportRapportByMonth(@RequestParam int annee,
      @RequestParam int mois) throws IOException {
    byte[] excelContent = exportService.exportRapportToExcelByMonth(annee, mois);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("attachment", "rapport_" + annee + "_" + mois + ".xlsx");

    return ResponseEntity.ok().headers(headers).body(excelContent);
  }

  @PreAuthorize("hasAnyAuthority('EXPORT_RAPPORT_STATS')")
  @GetMapping("/rapport/statistique")
  public String rapport(Model model) {

    userService.returnInfoUserConnected(model);
    return "facturationTT/rapport";
  }

  @PreAuthorize("hasAnyAuthority('EXPORT_RAPPORT_CLIENT_RELAUNCH')")
  @GetMapping("/rapport/clientFactureImpEchue")
  public String clientImp(Model model) {

    userService.returnInfoUserConnected(model);
    return "facturationTT/clientfactureimp";
  }

  @PreAuthorize("hasAnyAuthority('EXPORT_RAPPORT_CLIENT_RELAUNCH')")
  @GetMapping("/rapport/exportClientsImpayes")
  public ResponseEntity<byte[]> exportClientsFacturesImpayees() throws IOException {
    List<Map<String, Object>> clients = factureService.getClientsFacturesImpayeesRetard3Jours();
    byte[] excelContent = exportService.exportClientsFacturesImpayeesToExcel(clients);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("attachment", "clients_factures_impayees_echues.xlsx");

    return ResponseEntity.ok().headers(headers).body(excelContent);
  }

}
