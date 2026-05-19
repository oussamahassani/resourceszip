/*
 * created by hatem ghozzi on 7 10 2022
 */
package crm.chifco.com.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.ClassificationDemande;
import crm.chifco.com.model.Commande;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.EntryCommande;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.ImportXlsHistoryFile;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.OperationAbonnement;
import crm.chifco.com.model.Smstemplate;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.ClassificationDemandeRepository;
import crm.chifco.com.repository.CommandeRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.ImportXlsHistoryFileRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.OperationAbonnementRepository;
import crm.chifco.com.repository.SmstemplateRepository;
import crm.chifco.com.repository.StatutRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.utils.ClassificationCode;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.DBEtatTT;
import crm.chifco.com.utils.NomStatutChifco;
import crm.chifco.com.utils.StatutTTConstants;
import net.sf.jasperreports.engine.JRException;

@Service
public class ImportExcel {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  UserRepository userRepository;
  @Value("${pathuploadxlsx}")
  private String pathuploadxlsx;
  @Autowired
  DemandeAbonnementRepository demandeAbonnementRepository;
  @Autowired
  StatutRepository statutRepository;

  @Autowired
  ReportService reportService;

  @Autowired
  SmstemplateRepository templatesmsRepository;
  @Autowired
  Notification notificationservice;
  @Autowired
  AbonnementRepository abonnementRepository;
  @Autowired
  AbonnementService AbonnementService;
  @Autowired
  ProduitService ProduitService;

  @Autowired
  CommandeRepository commandeRepository;

  @Autowired
  private FactureService factureService;

  @Autowired
  ModemRepository modemRepository;
  @Autowired
  DemandeAbonnementService demandeAbonnementService;

  @Autowired
  private AbonnementHistoriqueService AbonnementHistoriqueservice;

  @Autowired
  private ImportXlsHistoryService ImportXlsHistoryService;

  @Autowired
  private ImportXlsHistoryFileRepository ImportXlsHistoryFileRepository;

  @Autowired
  ImportXlsHistoryFileService ImportXlsHistoryFileService;
  @Autowired
  private ClientHistoryService ClientHistoryService;

  @Autowired
  private ClassificationDemandeRepository classificationDemandeRepository;
  @Autowired
  private OperationAbonnementRepository operationAbonnementRepository;
  @Autowired
  private OperationAbonnementService operationAbonnementService;

  public void uploadEditAbonnementEnMasse(MultipartFile file, RedirectAttributes redirectAttrs,
      HttpServletResponse response) throws JRException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      LOGGER.info("user connected EditAbonnement with xls: " + user);
      int anuller = 0;
      int etude = 0;
      int confirmationclient = 0;

      int construction = 0;
      int reservation = 0;
      int notfound = 0;
      int fixeNonExisteClacule = 0;
      int errorRow = 0;
      int successRow = 0;

      if (!file.isEmpty()) {
        String extension =
            file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (extension.equals("xlsx") || extension.equals("xls")) {
          try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();
            String folder = pathuploadxlsx;
            File uploadDir = new File(folder);
            if (!uploadDir.exists()) {
              uploadDir.mkdirs();
            }

            String nameFile = "Changerstatut_" + dtf.format(now) + ".xlsx";
            Path path = Paths.get(pathuploadxlsx + nameFile);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            // XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            // XSSFSheet worksheet = workbook.getSheetAt(0);

            Workbook workbook; // Declare XSSF WorkBook
            Sheet worksheet = null; // sheet can be used as common for XSSF and HSSF WorkBook
            if (extension.equalsIgnoreCase("xls")) {
              workbook = new HSSFWorkbook(file.getInputStream());

              worksheet = workbook.getSheetAt(0);
            } else if (extension.equals("xlsx")) {
              workbook = new XSSFWorkbook(file.getInputStream());
              worksheet = workbook.getSheetAt(0);
            }

            if (worksheet.getRow(0) != null) {
              String cinCheckHader = String.valueOf(worksheet.getRow(0).getCell(14));
              String telFixeCheckHader = String.valueOf(worksheet.getRow(0).getCell(1));
              String referenceCheckHader = String.valueOf(worksheet.getRow(0).getCell(0));
              String etatcheckHader = String.valueOf(worksheet.getRow(0).getCell(8));
              LOGGER.warn("Hader  xls file: " + cinCheckHader + telFixeCheckHader
                  + referenceCheckHader + etatcheckHader);

              if (Objects.equals(cinCheckHader, "CIN") && Objects.equals(telFixeCheckHader, "Tél")
                  && Objects.equals(referenceCheckHader, "Réf. Demande")
                  && Objects.equals(etatcheckHader, "Etat")) {
                ImportXlsHistoryFile HistoryFileXls = new ImportXlsHistoryFile();
                ImportXlsHistoryFileRepository.save(HistoryFileXls);
                Statut statutinstalled =
                    statutRepository.findStatutByNomStatut(NomStatutChifco.INSTALLED);
                Statut statutrefused =
                    statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);
                Statut statutcanceled =
                    statutRepository.findStatutByNomStatut(NomStatutChifco.CANCELED);
                Smstemplate findTemplateSmsClientMiseService =
                    templatesmsRepository.findSmstemplateByname("clientmiseenservice");
                String TemplateSmsClientMiseService =
                    findTemplateSmsClientMiseService.getTemplate();
                ClassificationDemande classificationOk = classificationDemandeRepository
                    .findClassificationDemandeByCodeClassification(ClassificationCode.ACCEPTATION);
                ClassificationDemande classificationRefus = classificationDemandeRepository
                    .findClassificationDemandeByCodeClassification(ClassificationCode.RCommercial);

                ClassificationDemande classificationEnAttente = classificationDemandeRepository
                    .findClassificationDemandeByCodeClassification(ClassificationCode.DEnAttente);
                ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
                ArrayList<Map<String, Object>> ObjectSmsIfNonStock =
                    new ArrayList<Map<String, Object>>();
                for (int index = 1; index < worksheet.getPhysicalNumberOfRows(); index++) {
                  Row row = worksheet.getRow(index);

                  if (row != null) {
                    String motifRefus = String.valueOf(row.getCell(17));
                    Cell cinCell = row.getCell(14);
                    Cell refCell = row.getCell(0);
                    Cell etatCell = row.getCell(8);
                    Cell telFixeCell = row.getCell(1);
                    Cell dateEtat = row.getCell(9);

                    String cin = "";
                    String referencett = "";
                    if (cinCell != null) {
                      DataFormatter formatter = new DataFormatter();
                      cin = formatter.formatCellValue(cinCell);
                    }
                    DemandeAbonnement demandeAbonnement = null;

                    if (refCell != null) {
                      refCell.setCellType(CellType.STRING);
                      DataFormatter formatter = new DataFormatter();
                      referencett = formatter.formatCellValue(refCell);
                      DemandeAbonnement demandeAbonnementbyref = demandeAbonnementRepository
                          .findDemandeAbonnementsByuniquereferencett(referencett);
                      if (demandeAbonnementbyref != null
                          && demandeAbonnementbyref.getDemandeId() != null) {
                        demandeAbonnement = demandeAbonnementbyref;
                      }
                    }

                    if (demandeAbonnement != null) {

                      if (demandeAbonnement.getStatut().getNomStatut()
                          .equals(NomStatutChifco.WAIT_TT)) {
                        String checkDescription =
                            ImportXlsHistoryService.insertNewRowImportXlsHistoryStatutSendTT(
                                etatCell.getStringCellValue(), demandeAbonnement.getEtatTT());
                        if (etatCell != null && checkDescription == null) {

                          String reference = "";
                          long telFixe = 0L;
                          if (telFixeCell != null) {
                            if (!row.getCell(1).toString().equals("")
                                && CrmUtils.isNumeric(telFixeCell.toString()))
                              telFixe = Long.parseLong(telFixeCell.toString());
                          }


                          if (refCell != null) {

                            reference = refCell.toString();
                            // reference = refCell.getStringCellValue();
                          }
                          String etat = etatCell.getStringCellValue();

                          boolean StatutTTcancledExiste = Arrays
                              .stream(StatutTTConstants.StatutTTcancled).anyMatch(etat::equals);
                          boolean StatutTTrefusedExiste = Arrays
                              .stream(StatutTTConstants.StatutTTAnnulee).anyMatch(etat::equals);
                          boolean StatutTTMiseenserviceExiste =
                              Arrays.stream(StatutTTConstants.StatutTTMiseenservice)
                                  .anyMatch(etat::equals);

                          if (StatutTTMiseenserviceExiste == true) {

                            String referenceChifco = demandeAbonnement.getReferenceChifco();
                            LOGGER.info("mise en service par ficher xls => reference chifco : "
                                + referenceChifco);

                            String numeroUsercreated =
                                demandeAbonnement.getAssignedTo().getTelephone();

                            Long iduserCreted = demandeAbonnement.getUser().getUserid();
                            String nomCategoryInternet = demandeAbonnement
                                .getCategorieProduitInternet().getCategorieProduitInternetNom();

                            if (telFixe != 0L && demandeAbonnement.getTelFixe() == null
                                && String.valueOf(telFixe).length() == 8) {

                              demandeAbonnement.setTelFixe(telFixe);
                            }

                            if (demandeAbonnement.getTelFixe() != null) {
                              Map<String, Object> smsMessageTosend = new HashMap<String, Object>();
                              ArrayList<String> arrayTelephoneStock = new ArrayList<>();
                              // arrayTelephoneStock.add(numerotelephoenereadmin);
                              arrayTelephoneStock.add(numeroUsercreated);
                              ArrayList<Map<String, Object>> smsNonStockExiste =
                                  demandeAbonnementService.sendSmsIfNonStock(nomCategoryInternet,
                                      iduserCreted, arrayTelephoneStock, referenceChifco, true);
                              if (smsNonStockExiste.size() > 0) {
                                ObjectSmsIfNonStock.add(smsNonStockExiste.get(0));
                              }
                              if (!Objects.equals(reference, "")
                                  && demandeAbonnement.getReferenceTT() == null)
                                demandeAbonnement.setReferenceTT(reference);;
                              demandeAbonnement.setEtatTT(DBEtatTT.Mise_en_service);
                              demandeAbonnement.setStatut(statutinstalled);
                              String dateStr = dateEtat.getStringCellValue().trim();
                              if (dateStr != null && !dateStr.isEmpty()) {

                              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                              LocalDate localDate = LocalDate.parse(dateStr, formatter);

                              demandeAbonnement.setDateDeMiseEnService(java.sql.Date.valueOf(localDate));
                              }
                              else {
                            	  demandeAbonnement.setDateDeMiseEnService(new Date()) ;
                              }
                              // historique classification
                              String message;
                              if (demandeAbonnement.getDecisionDemande() == null) {
                                message = "La classification a été changée à '"
                                    + classificationOk.getValue() + "'";
                              } else {
                                message = "La classification a été changée de '"
                                    + demandeAbonnement.getDecisionDemande().getValue() + "' à '"
                                    + classificationOk.getValue() + "'";
                              }

                              AbonnementHistoriqueservice.saveNewHistorique(user,
                                  demandeAbonnement.getDemandeId(), message);

                              demandeAbonnement.setDecisionDemande(classificationOk);
                              demandeAbonnement.setDateDecisionDemande(new Date());

                              AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
                              successRow += 1;
                              ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                  demandeAbonnement.getStatut().getDesignation(),
                                  "Changement de statut TT à « Mise en service TT » est effectué avec succès",
                                  demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());

                              if (demandeAbonnement.getTelFixe() != null) {
                                String NewfindTemplateSmsClientMiseService =
                                    TemplateSmsClientMiseService.replace("{referencedemande}",
                                        demandeAbonnement.getTelFixe().toString());
                            	String codePromoSms= "";
                            
                              // String codePromo = notificationservice.getCodePromo();
                             //  codePromoSms= " Votre code promo est : "+ codePromo ;
                            	
                                smsMessageTosend.put("number", demandeAbonnement.getTelMobile());

                                smsMessageTosend.put("message",
                                    NewfindTemplateSmsClientMiseService + " "+ codePromoSms);
                                smsToSend.add(smsMessageTosend );
                              }



                              Abonnement abonnement = abonnementRepository
                                  .findAbonnementByCin(demandeAbonnement.getCin());
                              if (abonnement == null) {
                                abonnement = AbonnementService.saveNewAbonnement(demandeAbonnement);
                                ClientHistoryService.insertNewHistoryclient(demandeAbonnement,
                                    "Abonnement creé", user);

                              }



                              Commande findCommandeby =
                                  commandeRepository.findFirstByabonnement(abonnement);

                              if (findCommandeby == null) {

                                int typdedepaymentmonth =
                                    demandeAbonnement.getTypePaiement().getNombreMoisTypePaiement();

                                Date nouvauDateFin =
                                    CrmUtils.calculeDateFin(typdedepaymentmonth, null);

                                List<EntryCommande> EntryCommande = factureService
                                    .setEntriesCommande(abonnement, abonnement.getPack());

                                Commande commande = factureService.setCommande(abonnement,
                                    nouvauDateFin.toString(), null, user, EntryCommande);
                                Date prochaineDateFacture = null;
                                if (abonnement.getPack().getPayLater() != null
                                    && abonnement.getPack().getPayLater()) {
                                  nouvauDateFin = Date.from(CrmUtils.dateLimitePromo().plusDays(1)
                                      .atStartOfDay(ZoneId.systemDefault()).toInstant());
                                  prochaineDateFacture = nouvauDateFin;
                                }
                                Facture premiereFacture = factureService.generateFacture(commande,
                                    user, true, prochaineDateFacture, null);

                                factureService.setEntryTvaFacture(premiereFacture);

                              }

                            } else {
                              ImportXlsHistoryService.insertNewImportXlsHistory("Erreur",
                                  demandeAbonnement.getStatut().getDesignation(),
                                  " Le numéro de fixe n'existe pas ", demandeAbonnement,
                                  HistoryFileXls.getXlsHistoriqueFile());
                              fixeNonExisteClacule += 1;
                              errorRow += 1;
                            }
                          }
                          // else if(Objects.equals(etat, "Annulée : Demande non faisable"))
                          else if (StatutTTcancledExiste == true) {
                            if (!Objects.equals(reference, "")
                                && demandeAbonnement.getReferenceTT() == null)
                              demandeAbonnement.setReferenceTT(reference);
                            demandeAbonnementService.confirmeAbonnement(DBEtatTT.Cancled,
                                demandeAbonnement.getDemandeId(), user);
                            demandeAbonnement.setStatut(statutcanceled);
                            demandeAbonnement.setMotifRefus(motifRefus);

                            // historique de classification
                            String message;
                            if (demandeAbonnement.getDecisionDemande() == null) {
                              message = "La classification a été changée à '"
                                  + classificationRefus.getValue() + "'";
                              demandeAbonnement.setDecisionDemande(classificationRefus);
                              demandeAbonnement.setDateDecisionDemande(new Date());
                              AbonnementHistoriqueservice.saveNewHistorique(user,
                                  demandeAbonnement.getDemandeId(), message);
                            } /*
                               * else { message = "La classification a été changée de '" +
                               * demandeAbonnement.getDecisionDemande().getValue() + "' à '" +
                               * classificationRefus.getValue() + "'"; }
                               */



                            demandeAbonnementRepository.save(demandeAbonnement);
                            AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
                            anuller += 1;
                            successRow += 1;
                            ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                demandeAbonnement.getStatut().getDesignation(),
                                "Demande anuller avec succee", demandeAbonnement,
                                HistoryFileXls.getXlsHistoriqueFile());

                            LOGGER.info("anuller par ficher xls => reference chifco : "
                                + demandeAbonnement.getReferenceChifco());
                          }

                          // else if (Objects.equals(etat, "Annulée : Demande non éligible au
                          // débit demandé"))
                          else if (StatutTTrefusedExiste == true) {
                            if (!Objects.equals(reference, "")
                                && demandeAbonnement.getReferenceTT() == null)
                              demandeAbonnement.setReferenceTT(reference);

                            demandeAbonnementService.confirmeAbonnement(DBEtatTT.Refused,
                                demandeAbonnement.getDemandeId(), user);
                            demandeAbonnement.setStatut(statutrefused);
                            demandeAbonnement.setMotifRefus(motifRefus);
                            if (demandeAbonnement.getDecisionDemande() == null) {

                              // historique de classification
                              String message;
                              if (demandeAbonnement.getDecisionDemande() == null) {
                                message = "La classification a été changée à '"
                                    + classificationEnAttente.getValue() + "'";

                                AbonnementHistoriqueservice.saveNewHistorique(user,
                                    demandeAbonnement.getDemandeId(), message);

                                demandeAbonnement.setDecisionDemande(classificationEnAttente);
                                demandeAbonnement.setDateDecisionDemande(new Date());
                              }
                              /*
                               * else { message = "La classification a été changée de '" +
                               * demandeAbonnement.getDecisionDemande().getValue() + "' à '" +
                               * classificationEnAttente.getValue() + "'"; }
                               */

                            }
                            demandeAbonnementRepository.save(demandeAbonnement);
                            AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
                            anuller += 1;
                            successRow += 1;
                            ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                demandeAbonnement.getStatut().getDesignation(),
                                "Changement de statut TT à « Annulée » est effectué avec succès",
                                demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());

                            LOGGER.info("anuller par ficher xls => reference chifco : "
                                + demandeAbonnement.getReferenceChifco());
                          } else {
                            boolean StatuEtudeExiste = Arrays
                                .stream(StatutTTConstants.statutttetude).anyMatch(etat::equals);
                            boolean ConfirmationClientExiste =
                                Arrays.stream(StatutTTConstants.StatutTTConfirmationClient)
                                    .anyMatch(etat::equals);
                            boolean ConstructionLigneExiste =
                                Arrays.stream(StatutTTConstants.StatutTTConstruction)
                                    .anyMatch(etat::equals);
                            boolean ReservationExiste =
                                Arrays.stream(StatutTTConstants.StatutTTReservation)
                                    .anyMatch(etat::equals);

                            boolean ConfirmationClientOKNety =
                                Arrays.stream(StatutTTConstants.StatutTTConfirmationClientOKNety)
                                    .anyMatch(etat::equals);

                            boolean ConfirmationClientAnnulerNety = Arrays
                                .stream(StatutTTConstants.StatutTTConfirmationClientAnnulerNety)
                                .anyMatch(etat::equals);

                            boolean StatutTTInstance = Arrays
                                .stream(StatutTTConstants.StatutTTInstance).anyMatch(etat::equals);

                            if (StatuEtudeExiste == true) {
                              etat = "Etude";
                              etude += 1;
                              ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                  demandeAbonnement.getStatut().getDesignation(),
                                  "Changement de statut TT à « Etude » est effectué avec succès",
                                  demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());

                              demandeAbonnementService.confirmeAbonnement("etude",
                                  demandeAbonnement.getDemandeId(), user);
                              successRow += 1;
                            }

                            if (ConfirmationClientExiste == true) {

                              confirmationclient += 1;
                              ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                  demandeAbonnement.getStatut().getDesignation(),
                                  "Changement de statut TT à « Confirmation client » est effectué avec succès",
                                  demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());
                              successRow += 1;
                              if (telFixe != 0L && demandeAbonnement.getTelFixe() == null
                                  && String.valueOf(telFixe).length() == 8) {

                                demandeAbonnement.setTelFixe(telFixe);
                              }
                              demandeAbonnement.setEtatTT(DBEtatTT.ConfirmationClient);

                              demandeAbonnementRepository.save(demandeAbonnement);
                              LOGGER.info("ConfirmationClient par ficher xls => reference chifco : "
                                  + demandeAbonnement.getReferenceChifco());
                            }
                            if (ConstructionLigneExiste == true) {

                              ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                  demandeAbonnement.getStatut().getDesignation(),
                                  "Changement de statut TT à « Construction ligne » est effectué avec succès",
                                  demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());
                              construction += 1;
                              successRow += 1;
                              if (telFixe != 0L && demandeAbonnement.getTelFixe() == null
                                  && String.valueOf(telFixe).length() == 8) {

                                demandeAbonnement.setTelFixe(telFixe);
                              }
                              // demandeAbonnement.setEtatTT(DBEtatTT.ConstructionLigne);

                              demandeAbonnementRepository.save(demandeAbonnement);
                              LOGGER.info("ConstructionLigne par ficher xls => reference chifco : "
                                  + demandeAbonnement.getReferenceChifco());

                              demandeAbonnementService.confirmeAbonnement(
                                  DBEtatTT.ConstructionLigne, demandeAbonnement.getDemandeId(),
                                  user);
                            }
                            if (ReservationExiste == true) {
                              etat = "Reservation";
                              ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                  referencett, "Reservation", demandeAbonnement,
                                  HistoryFileXls.getXlsHistoriqueFile());
                              reservation += 1;
                              successRow += 1;
                              demandeAbonnement.setEtatTT(etat);
                              LOGGER.info("Reservation Ligne par ficher xls => reference chifco : "
                                  + demandeAbonnement.getReferenceChifco());
                              demandeAbonnementRepository.save(demandeAbonnement);
                            }
                            if (ConfirmationClientOKNety == true) {

                              ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                  referencett, "StatutTT Confirmation Client OK", demandeAbonnement,
                                  HistoryFileXls.getXlsHistoriqueFile());
                              confirmationclient += 1;
                              successRow += 1;
                              demandeAbonnementService.confirmeAbonnement(DBEtatTT.ConfirmationOK,
                                  demandeAbonnement.getDemandeId(), user);

                              // demandeAbonnementRepository.save(demandeAbonnement);
                            }

                            if (ConfirmationClientAnnulerNety == true) {

                              ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                  referencett, "StatutTT Confirmation Client Annuler",
                                  demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());
                              confirmationclient += 1;
                              successRow += 1;
                              demandeAbonnementService.confirmeAbonnement(
                                  DBEtatTT.ConfirmationAnnuler, demandeAbonnement.getDemandeId(),
                                  user);
                              demandeAbonnementRepository.save(demandeAbonnement);
                            }
                            if (StatutTTInstance == true) {

                              ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                  referencett, "StatutTT Instance", demandeAbonnement,
                                  HistoryFileXls.getXlsHistoriqueFile());
                              confirmationclient += 1;
                              successRow += 1;
                              demandeAbonnementService.confirmeAbonnement(DBEtatTT.Instance,
                                  demandeAbonnement.getDemandeId(), user);

                              demandeAbonnement.setMotifRefus(motifRefus);
                              demandeAbonnementRepository.save(demandeAbonnement);
                            }

                          }

                        } else {
                          errorRow += 1;
                          ImportXlsHistoryService.insertNewImportXlsHistory("Erreur",
                              demandeAbonnement.getStatut().getDesignation(), checkDescription,
                              demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());
                        }

                      } else if (demandeAbonnement.getStatut().getNomStatut()
                          .equals(NomStatutChifco.INSTALLED)
                          || demandeAbonnement.getStatut().getNomStatut()
                              .equals(NomStatutChifco.ASSIGNED)
                          || demandeAbonnement.getStatut().getNomStatut()
                              .equals(NomStatutChifco.ACTIVE)
                          || demandeAbonnement.getStatut().getNomStatut()
                              .equals(NomStatutChifco.VALID)
                          || demandeAbonnement.getStatut().getNomStatut()
                              .equals(NomStatutChifco.RESILIATION)) {
                        String checkDescription =
                            ImportXlsHistoryService.insertNewRowImportXlsHistoryStatutSendTT(
                                etatCell.getStringCellValue(), demandeAbonnement.getEtatTT());
                        if (etatCell != null && checkDescription == null) {

                          String etat = etatCell.getStringCellValue();
                          boolean StatutTTClotureeExiste = Arrays
                              .stream(StatutTTConstants.StatutTTCloturee).anyMatch(etat::equals);
                          boolean StatutTTResilationExiste = Arrays
                              .stream(StatutTTConstants.StatutTTResilation).anyMatch(etat::equals);

                          if (StatutTTClotureeExiste == true) {
                            if (!demandeAbonnement.getEtatTT().equals(DBEtatTT.Resilation)) {
                              demandeAbonnement.setEtatTT(DBEtatTT.Clôturée);
                              demandeAbonnementRepository.save(demandeAbonnement);
                              LOGGER.info("clotureé par ficher xls => reference chifco : "
                                  + demandeAbonnement.getReferenceChifco());
                              ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                  referencett,
                                  "Changement de statut TT à « Clôturée » est effectué avec succès",
                                  demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());
                              successRow += 1;
                            }
                          }

                          if (StatutTTResilationExiste == true) {

                            demandeAbonnement.setEtatTT(DBEtatTT.Resilation);
                            demandeAbonnementRepository.save(demandeAbonnement);
                            LOGGER.info("clotureé par ficher xls => reference chifco : "
                                + demandeAbonnement.getReferenceChifco());
                            ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                referencett,
                                "Le statut TT de la demande d'abonnement est passé à « Résiliation »",
                                demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());
                            AbonnementHistoriqueservice.saveNewHistorique(user,
                                demandeAbonnement.getDemandeId(),
                                "Le statut TT de la demande d'abonnement est passé à « Résiliation »");
                            successRow += 1;
                          }
                        } else {
                          errorRow += 1;
                          ImportXlsHistoryService.insertNewImportXlsHistory("Erreur", referencett,
                              checkDescription, demandeAbonnement,
                              HistoryFileXls.getXlsHistoriqueFile());

                        }
                      } else if (!demandeAbonnement.getStatut().getNomStatut()
                          .equals(NomStatutChifco.WAIT_TT)
                          && demandeAbonnement.getStatut().getNomStatut()
                              .equals(NomStatutChifco.INSTALLED)) {
                        ImportXlsHistoryService.insertNewImportXlsHistory("Erreur", referencett,
                            "Demande D'abonnement deja traiter", demandeAbonnement,
                            HistoryFileXls.getXlsHistoriqueFile());

                        errorRow += 1;

                      }

                      else {
                        notfound += 1;
                        errorRow += 1;
                        ImportXlsHistoryService.insertNewImportXlsHistory("Erreur", referencett,
                            "Demande D'abonnement non trouveé ou n'est plus au statut Envoyer TT  ou refuser",
                            null, HistoryFileXls.getXlsHistoriqueFile());

                      }
                    }

                    ImportXlsHistoryFileService.insertNewFileImportXlsHistory(
                        Integer.toString(errorRow), Integer.toString(successRow),
                        file.getOriginalFilename(), nameFile,
                        Integer.toString(worksheet.getLastRowNum()), HistoryFileXls, user);
                  }
                  redirectAttrs.addFlashAttribute("message", "uploadeditabonnementenmasse");
                }
                if (smsToSend.size() > 0) {
                  notificationservice.sendsmsnotification(smsToSend);
                }
                if (ObjectSmsIfNonStock.size() > 0) {
                  notificationservice.sendsmsnotification(ObjectSmsIfNonStock);
                }

              } else {
                redirectAttrs.addFlashAttribute("message", "erreuruploadeditabonnementenmasse");

              }

            }

          } catch (IOException e) {

            LOGGER.error("change statut xls " + e.getMessage());

          }
        } else {
          redirectAttrs.addFlashAttribute("message", "fileempty");
        }
      } else {

        redirectAttrs.addFlashAttribute("message", "fileempty");
      }
      if (notfound > 0) {
        redirectAttrs.addFlashAttribute("notfoundcalcul", notfound);
      }
      if (fixeNonExisteClacule > 0) {
        redirectAttrs.addFlashAttribute("fixeNonExisteClacule", fixeNonExisteClacule);
      }

      if (reservation > 0) {

        redirectAttrs.addFlashAttribute("reservationcalcul", reservation);
      }
      if (construction > 0) {
        redirectAttrs.addFlashAttribute("constricutioncalcul", construction);
      }
      if (confirmationclient > 0) {
        redirectAttrs.addFlashAttribute("confirmationclientcalcul", confirmationclient);
      }
      if (etude > 0) {
        redirectAttrs.addFlashAttribute("etudecalcul", etude);

      }
      if (anuller > 0) {

        redirectAttrs.addFlashAttribute("anullercalcul", anuller);
      }

    }
  }

  private boolean isRowEmpty(Row row) {
    if (row == null) {
      return true;
    }
    for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
      Cell cell = row.getCell(cellIndex);
      if (cell != null && cell.getCellType() != CellType.BLANK) {
        return false;
      }
    }
    return true;
  }

  public void uploadEditOperationEnMasse(MultipartFile file, RedirectAttributes redirectAttrs,
      HttpServletResponse response) throws JRException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      LOGGER.info("user connected EditMigration with xls: " + user);
      int annuler = 0;
      int Exécution = 0;
      int Clôturée = 0;
      int attenteConstruction = 0;
      int MiseEnServiceTT = 0;
      int Instance = 0;
      int InstanceCommercial = 0;
      int Migration = 0;
      int Raccordement = 0;
      int RéservationModem = 0;
      int Activation_de_service = 0;
      int Rejetée = 0;
      int errorRow = 0;
      int successRow = 0;
      int notfound = 0;
      int fixeNonExisteClacule = 0;
      int emptycell = 0;

      if (!file.isEmpty()) {
        String extension =
            file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (extension.equals("xlsx") || extension.equals("xls")) {
          try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();
            String folder = pathuploadxlsx;
            File uploadDir = new File(folder);
            if (!uploadDir.exists()) {
              uploadDir.mkdirs();
            }

            String nameFile = "ChangerstatutOpeationDemande_" + dtf.format(now) + ".xlsx";
            Path path = Paths.get(pathuploadxlsx + nameFile);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            Workbook workbook;
            Sheet worksheet = null;
            if (extension.equalsIgnoreCase("xls")) {
              workbook = new HSSFWorkbook(file.getInputStream());
              worksheet = workbook.getSheetAt(0);
            } else if (extension.equals("xlsx")) {
              workbook = new XSSFWorkbook(file.getInputStream());
              worksheet = workbook.getSheetAt(0);
            }
            // Read from excel
            if (worksheet.getRow(0) != null) {
              String cinCheckHader = String.valueOf(worksheet.getRow(0).getCell(14));
              String telFixeCheckHader = String.valueOf(worksheet.getRow(0).getCell(1));
              String referenceCheckHader = String.valueOf(worksheet.getRow(0).getCell(0));
              String etatcheckHader = String.valueOf(worksheet.getRow(0).getCell(8));
              LOGGER.warn("Header  xls file: " + cinCheckHader + "/" + telFixeCheckHader + "/"
                  + referenceCheckHader + "/" + etatcheckHader);
              if (Objects.equals(cinCheckHader, "CIN") && Objects.equals(telFixeCheckHader, "Tél")
                  && Objects.equals(referenceCheckHader, "Réf. Demande")
                  && Objects.equals(etatcheckHader, "Etat")) {
                ImportXlsHistoryFile HistoryFileXls = new ImportXlsHistoryFile();
                ImportXlsHistoryFileRepository.save(HistoryFileXls);
                // début boucle for
                for (int index = 1; index < worksheet.getPhysicalNumberOfRows(); index++) {
                  Row row = worksheet.getRow(index);
                  if (row != null && !isRowEmpty(row)) {
                    if (row.getCell(14) != null && row.getCell(0) != null && row.getCell(8) != null
                        && row.getCell(1) != null) {
                      String motifRefus = String.valueOf(row.getCell(17));
                      Cell cinCell = row.getCell(14);
                      Cell refCell = row.getCell(0);
                      Cell etatCell = row.getCell(8);
                      Cell telFixeCell = row.getCell(1);
                      Cell NouvTelFixeCell = row.getCell(2);
                      Cell serieModem = row.getCell(11);
                      String cin = "";
                      String referencett = "";
                      OperationAbonnement operationDemande = new OperationAbonnement();
                   
                      if (refCell != null) {
                        refCell.setCellType(CellType.STRING);
                        DataFormatter formatter = new DataFormatter();
                        referencett = formatter.formatCellValue(refCell);
                        OperationAbonnement operationDemandeByRef = operationAbonnementRepository
                            .findDemandeMigrationByuniquereferencett(referencett);
                        if (operationDemandeByRef != null
                            && operationDemandeByRef.getOperationId() != null) {
                          operationDemande = operationDemandeByRef;
                        }
                      }
                      // check if demande operation exist or not
                      if (operationDemande != null && !operationDemande.getEtatTT().equals(DBEtatTT.Clôturée)) {
                        Boolean result = false;
                        // Status chifco WAIT_TT (Envoyée à TT)
                        if (operationDemande.getStatut().getNomStatut()
                            .equals(NomStatutChifco.WAIT_TT)
                            || operationDemande.getStatut().getNomStatut()
                                .equals(NomStatutChifco.INSTALLED)
                            || operationDemande.getStatut().getNomStatut()
                                .equals(NomStatutChifco.VALID)) {
                          String checkDescription =
                              ImportXlsHistoryService.insertNewRowImportXlsHistoryStatutSendTT2(
                                  etatCell.getStringCellValue(), operationDemande.getEtatTT());
                          // when etat != null
                          if (etatCell != null && checkDescription == null) {
                            String reference = "";
                            long telFixe = 0L;
                            if (telFixeCell != null) {
                              DataFormatter formatter = new DataFormatter();
                              String telFixeString = formatter.formatCellValue(telFixeCell);
                              if (!telFixeString.isEmpty() && CrmUtils.isNumeric(telFixeString)) {
                                telFixe = Long.parseLong(telFixeString);
                              }
                            }
                            long NouvtelFixe = 0L;
                            if (NouvTelFixeCell != null) {
                              DataFormatter formatter = new DataFormatter();
                              String NouvtelFixeString = formatter.formatCellValue(NouvTelFixeCell);
                              if (!NouvtelFixeString.isEmpty()
                                  && CrmUtils.isNumeric(NouvtelFixeString)) {
                                NouvtelFixe = Long.parseLong(NouvtelFixeString);
                              }
                            }
                            LOGGER.info("num2" + telFixeCell.toString());
                            if (refCell != null) {
                              reference = refCell.toString();
                            }
                            String etat = etatCell.getStringCellValue();
                            boolean StatutTTcancledExiste = Arrays
                                .stream(StatutTTConstants.StatutTTcancled).anyMatch(etat::equals);
                            boolean StatutTTMiseenserviceExiste =
                                Arrays.stream(StatutTTConstants.StatutTTMiseenservice)
                                    .anyMatch(etat::equals);
                            boolean StatutTTExecutionExiste = Arrays
                                .stream(StatutTTConstants.StatutTTExecution).anyMatch(etat::equals);
                            boolean StatutTTAttenteConstructionExiste =
                                Arrays.stream(StatutTTConstants.StatutTTAttenteConstruction)
                                    .anyMatch(etat::equals);
                            boolean StatutTTMigrationExiste = Arrays
                                .stream(StatutTTConstants.StatutTTMigration).anyMatch(etat::equals);
                            boolean StatutTTInstanceCommercialExiste =
                                Arrays.stream(StatutTTConstants.StatutTTInstanceCommercial)
                                    .anyMatch(etat::equals);
                            boolean StatutTTRaccordementExiste =
                                Arrays.stream(StatutTTConstants.StatutTTRaccordement)
                                    .anyMatch(etat::equals);
                            boolean StatutTTActivationServiceExiste =
                                Arrays.stream(StatutTTConstants.StatutTTActivationService)
                                    .anyMatch(etat::equals);
                            boolean StatutTTRejectExiste = Arrays
                                .stream(StatutTTConstants.StatutTTReject).anyMatch(etat::equals);
                            boolean StatutTTInstanceExiste = Arrays
                                .stream(StatutTTConstants.StatutTTInstance).anyMatch(etat::equals);
                            boolean StatutTTClotureeExiste = Arrays
                                .stream(StatutTTConstants.StatutTTCloturee).anyMatch(etat::equals);
                            boolean StatutTTRéservationModem =
                                Arrays.stream(StatutTTConstants.StatutTTReservedModem)
                                    .anyMatch(etat::equals);
                            // Different case of Modification débit
                            if (operationDemande.getTypeDemande().equals("CH")) {
                              if (StatutTTExecutionExiste == true) {
                                etat = "execution";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, null,
                                    redirectAttrs);
                                if (result) {
                                  Exécution += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  // here put the insertion of non valid etat
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Demande non trouveé ou n'est plus au statut Envoyer TT ou Statut "
                                          + etat + " erroné",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTcancledExiste == true) {
                                etat = "annuler";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, null,
                                    redirectAttrs);
                                if (result) {
                                  annuler += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Demande non trouveé ou n'est plus au statut Envoyer TT ou Statut "
                                          + etat + " erroné",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTClotureeExiste == true) {
                                etat = "Clôturée";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, null,
                                    redirectAttrs);
                                if (result) {
                                  Clôturée += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Demande non trouveé ou n'est plus au statut Envoyer TT ou Statut "
                                          + etat + " erroné",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else {
                                errorRow += 1;
                                ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                    operationDemande.getStatut().getDesignation(),
                                    "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                    operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                              }
                              // fin workflow changement débit
                              // start workflow transfert
                            } else if (operationDemande.getTypeDemande().equals("T")) {
                              if (StatutTTAttenteConstructionExiste == true) {
                                etat = "attenteConstruction";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, null,
                                    redirectAttrs);
                                if (result) {
                                  attenteConstruction += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTMiseenserviceExiste == true) {
                                etat = "Mise en service TT";
                                if (NouvtelFixe != 0) {
                                  result = operationAbonnementService.updateStatutMigration(
                                      operationDemande.getOperationId(), null, null, NouvtelFixe,
                                      redirectAttrs);
                                  if (result) {
                                    MiseEnServiceTT += 1;
                                    ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                        operationDemande.getStatut().getDesignation(),
                                        "Changement de statut TT à «" + etat
                                            + " » est effectué avec succès",
                                        operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                    successRow += 1;
                                  } else {
                                    errorRow += 1;
                                    ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                        operationDemande.getStatut().getDesignation(),
                                        "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                        operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  }
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Impossible de changer la Statut en " + etat
                                          + ",Tél fixe obligatoire",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTInstanceExiste == true) {
                                etat = "Instance";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, motifRefus,
                                    redirectAttrs);
                                if (result) {
                                  Instance += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès,avec motif «" + motifRefus
                                          + "»",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTInstanceCommercialExiste == true) {
                                etat = "instancecomercial";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, motifRefus,
                                    redirectAttrs);
                                if (result) {
                                  InstanceCommercial += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès,avec motif «" + motifRefus
                                          + "»",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTcancledExiste == true) {
                                etat = "annuler";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, null,
                                    redirectAttrs);
                                if (result) {
                                  annuler += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTClotureeExiste == true && operationDemande
                                  .getEtatTT().equals(DBEtatTT.Mise_en_service)) {
                                etat = "Clôturée";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, null,
                                    redirectAttrs);
                                if (result) {
                                  Clôturée += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else {
                                errorRow += 1;
                                ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                    operationDemande.getStatut().getDesignation(), etat
                                        + "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                    operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                              }
                            } else if (operationDemande.getTypeDemande().equals("M")) {
                              if (StatutTTMigrationExiste == true
                                  && operationDemande.getPack().getCategoriePack()
                                      .getCategorieProduitInternetCode().equals("VDSL")) {
                                etat = "migration";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, null,
                                    redirectAttrs);
                                if (result) {
                                  Migration += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTMiseenserviceExiste == true
                                  && operationDemande.getPack().getCategoriePack()
                                      .getCategorieProduitInternetCode().equals("VDSL")) {
                                etat = "Mise en service TT";
                                result = operationAbonnementService.updateStatutMigration(
                                    operationDemande.getOperationId(), null, null, null,
                                    redirectAttrs);
                                if (result) {
                                  MiseEnServiceTT += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTInstanceExiste == true) {
                                etat = "Instance";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, motifRefus,
                                    redirectAttrs);
                                if (result) {
                                  Instance += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTcancledExiste == true) {
                                etat = "annuler";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, motifRefus,
                                    redirectAttrs);
                                if (result) {
                                  annuler += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTClotureeExiste == true) {
                                if ((operationDemande.getEtatTT().equals(DBEtatTT.ActivationService)
                                    || operationDemande.getEtatTT()
                                        .equals(DBEtatTT.Mise_en_service))
                                    && operationDemande.getStatut().getNomStatut()
                                        .equals(NomStatutChifco.VALID)) {
                                  etat = "Clôturée";
                                  result = operationAbonnementService.confirmeAbonnement(etat,
                                      operationDemande.getOperationId(), user, null, null,
                                      redirectAttrs);
                                  if (result) {
                                    Clôturée += 1;
                                    ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                        operationDemande.getStatut().getDesignation(),
                                        "Changement de statut TT à «" + etat
                                            + " » est effectué avec succès",
                                        operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                    successRow += 1;
                                  } else {
                                    errorRow += 1;
                                    ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                        operationDemande.getStatut().getDesignation(),
                                        "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                        operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  }

                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTRejectExiste == true
                                  && operationDemande.getPack().getCategoriePack()
                                      .getCategorieProduitInternetCode().equals("GPON")) {
                                etat = "Rejetée";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, null,
                                    redirectAttrs);
                                if (result) {
                                  Rejetée += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTRaccordementExiste == true
                                  && operationDemande.getPack().getCategoriePack()
                                      .getCategorieProduitInternetCode().equals("GPON")) {
                                etat = "raccordement";
                                result = operationAbonnementService.updateStatutMigration(
                                    operationDemande.getOperationId(), null, null, NouvtelFixe,
                                    redirectAttrs);
                                if (result) {
                                  Raccordement += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTActivationServiceExiste == true
                                  && operationDemande.getPack().getCategoriePack()
                                      .getCategorieProduitInternetCode().equals("GPON")
                                  && operationDemande.getEtatTT()
                                      .equals(DBEtatTT.Affectation_Modem)) {
                                etat = "activationService";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, null, null,
                                    redirectAttrs);
                                if (result) {
                                  Activation_de_service += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «" + etat
                                          + " » est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else if (StatutTTRéservationModem == true
                                  && operationDemande.getPack().getCategoriePack()
                                      .getCategorieProduitInternetCode().equals("GPON")
                                  && operationDemande.getEtatTT().equals(DBEtatTT.Raccordement)
                                  && serieModem != null) {
                                DataFormatter formatter = new DataFormatter();
                                String mod = formatter.formatCellValue(serieModem);
                                Modem modemRes = modemRepository
                                    .findModemByNumSerieOptionalAdmin(mod, "GPON").get();
                                etat = "reservermodem";
                                result = operationAbonnementService.confirmeAbonnement(etat,
                                    operationDemande.getOperationId(), user, modemRes.getModemId(),
                                    null, redirectAttrs);
                                if (result) {
                                  RéservationModem += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                      operationDemande.getStatut().getDesignation(),
                                      "Changement de statut TT à «Affecter un modem au demande de migration» est effectué avec succès",
                                      operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                                  successRow += 1;
                                } else {
                                  errorRow += 1;
                                  ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                      operationDemande.getReferenceChifco(),
                                      "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                      null, HistoryFileXls.getXlsHistoriqueFile());
                                }
                              } else {
                                errorRow += 1;
                                ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                    operationDemande.getStatut().getDesignation(), etat
                                        + " n'est pas conforme à la demande de migration ou num série modem n'existe pas.",
                                    operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                              }
                              // end
                            } else {
                              errorRow += 1;
                              ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur",
                                  operationDemande.getStatut().getDesignation(),
                                  "Changement de statut interdit sans passer par un statut intermédiaire. Veuillez corriger l'ordre des statuts.",
                                  operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                            }
                          } // end etat != null
                          else {// else of end etat
                            successRow += 1;
                            ImportXlsHistoryService.insertNewImportXlsHistory2("Success",
                                operationDemande.getStatut().getDesignation(), checkDescription,
                                operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                          }
                        } else {
                          errorRow += 1;
                          ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur", referencett,
                              "Demande non trouveé ou n'est plus au statut Envoyer TT ou Statut erroné",
                              operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                        } // end statut chifco wait_tt Envoyer à TT
                      } else {
                        // notfound += 1;
                        errorRow += 1;
                        ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur", referencett,
                            "Demande non trouveé ou n'est plus au statut Envoyer TT ou Statut erroné",
                            operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                      } // end of existing demande operation
                      ImportXlsHistoryFileService.insertNewFileImportXlsHistory(
                          Integer.toString(errorRow), Integer.toString(successRow),
                          file.getOriginalFilename(), nameFile,
                          Integer.toString(successRow + errorRow), HistoryFileXls, user);
                      // end of traitement of different of insert status for different demande
                      // operation
                    } else {
                      emptycell += 1;
                      String cin = "";
                      String referencett = "";
                      Cell cinCell = row.getCell(14);
                      Cell refCell = row.getCell(0);
                      Cell telFixeCell = row.getCell(1);
                      OperationAbonnement operationDemande = new OperationAbonnement();
                      if (cinCell != null) {
                        DataFormatter formatter = new DataFormatter();
                        cin = formatter.formatCellValue(cinCell);
                        operationDemande =
                            operationAbonnementRepository.findDemandeMigrationByuniquecin(cin);
                      }
                      if (refCell != null) {
                        refCell.setCellType(CellType.STRING);
                        DataFormatter formatter = new DataFormatter();
                        referencett = formatter.formatCellValue(refCell);
                        OperationAbonnement operationDemandeByRef = operationAbonnementRepository
                            .findDemandeMigrationByuniquereferencett(referencett);
                        operationDemande = operationDemandeByRef;
                      }
                      long telFixe = 0L;
                      if (telFixeCell != null) {
                        DataFormatter formatter = new DataFormatter();
                        String telFixeString = formatter.formatCellValue(telFixeCell);
                        if (!telFixeString.isEmpty() && CrmUtils.isNumeric(telFixeString)) {
                          telFixe = Long.parseLong(telFixeString);
                        }
                      }
                      ImportXlsHistoryService.insertNewImportXlsHistory2("Erreur", null,
                          "Un ou plusieurs paramètres sont manquants(CIN:" + cin + "/reference TT: "
                              + referencett + "/Tel Fixe: " + telFixe + " )",
                          operationDemande, HistoryFileXls.getXlsHistoriqueFile());
                    }
                    ImportXlsHistoryFileService.insertNewFileImportXlsHistory(
                        Integer.toString(errorRow + emptycell), Integer.toString(successRow),
                        file.getOriginalFilename(), nameFile,
                        Integer.toString(successRow + errorRow + emptycell), HistoryFileXls, user);
                  } // row empty check

                } // fin boucle for
              } // fin check cin and ref abonnement header
              else {
                redirectAttrs.addFlashAttribute("message", "erreuruploadeditabonnementenmasse");

              }
            } // fin read from excel
          } catch (Exception e) {
            LOGGER.info("Upload status en masse(uploadEditOperationEnMasse) catch exception :"
                + e.getMessage());
          }
        } // end xls check
        else {
          notfound += 1;
          redirectAttrs.addFlashAttribute("message", "fileempty");
        }
      } // end of check empty
      else {
        notfound += 1;
        redirectAttrs.addFlashAttribute("message", "fileempty");
      }
      if (notfound > 0 || fixeNonExisteClacule > 0 || errorRow > 0 || emptycell > 0) {
        StringBuilder dangerAlerts = new StringBuilder();
        if (emptycell > 0) {
          dangerAlerts.append("Un ou plusieurs paramètres sont manquants(ref TT/Tel fixe/CIN) : ")
              .append(emptycell).append(", ");
        }
        if (notfound > 0) {
          dangerAlerts.append("Fichier excel est vide").append(notfound).append(", ");
        }
        if (errorRow > 0) {
          dangerAlerts.append("Opération demande n'existe pas ou statut erroné: ").append(errorRow)
              .append(", ");
        }
        if (fixeNonExisteClacule > 0) {
          dangerAlerts.append("Fixe Non Existe Calcule: ").append(fixeNonExisteClacule)
              .append(", ");
        }
        if (dangerAlerts.length() > 0) {
          dangerAlerts.setLength(dangerAlerts.length() - 2);
          redirectAttrs.addFlashAttribute("dangerAlerts", dangerAlerts.toString());
        }
      }
      StringBuilder successAlerts = new StringBuilder();
      if (annuler > 0) {
        successAlerts.append("Annuler: ").append(annuler).append(", ");
      }
      if (Exécution > 0) {
        successAlerts.append("Exécution: ").append(Exécution).append(", ");
      }
      if (Clôturée > 0) {
        successAlerts.append("Clôturée: ").append(Clôturée).append(", ");
      }
      if (RéservationModem > 0) {
        successAlerts.append("Réservation modem: ").append(RéservationModem).append(", ");
      }
      if (attenteConstruction > 0) {
        successAlerts.append("AttenteConstruction: ").append(attenteConstruction).append(", ");
      }
      if (Instance > 0) {
        successAlerts.append("Instance: ").append(Instance).append(", ");
      }
      if (InstanceCommercial > 0) {
        successAlerts.append("InstanceCommercial: ").append(InstanceCommercial).append(", ");
      }
      if (MiseEnServiceTT > 0) {
        successAlerts.append("MiseEnServiceTT: ").append(MiseEnServiceTT).append(", ");
      }
      if (Migration > 0) {
        successAlerts.append("Migration: ").append(Migration).append(", ");
      }
      if (Rejetée > 0) {
        successAlerts.append("Rejetée: ").append(Rejetée).append(", ");
      }
      if (Activation_de_service > 0) {
        successAlerts.append("Activation_de_service: ").append(Activation_de_service).append(", ");
      }
      if (Raccordement > 0) {
        successAlerts.append("Raccordement: ").append(Raccordement).append(", ");
        redirectAttrs.addFlashAttribute("Raccordement", Raccordement);
      }
      if (successAlerts.length() > 0) {
        successAlerts.setLength(successAlerts.length() - 2);
        redirectAttrs.addFlashAttribute("successAlerts", successAlerts.toString());
      }

    }

  }


}
