package crm.chifco.com.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import crm.chifco.com.model.ImportXlsHistoryFileReclamation;
import crm.chifco.com.model.Motifrec;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.model.Servicetype;
import crm.chifco.com.model.Statusrec;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.ImportXlsHistoryFileReclamationRepository;
import crm.chifco.com.repository.ReclamationRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.DBEtatTT;
import crm.chifco.com.utils.NomStatutReclamation;

@Service
public class ImportReclamationService {

  @Autowired
  UserRepository userRepository;
  @Autowired
  Notification notificationservice;

  @Value("${pathuploadxlsx}")
  private String pathuploadxlsx;

  @Autowired
  private ImportXlsHistoryFileReclamationRepository importXlsHistoryFileRepository;

  @Autowired
  private ImportXlsHistoryReclamationService importXlsHistoryService;

  @Autowired
  private ReclamationRepository reclamationRepository;

  @Autowired
  ImportXlsHistoryFileServiceReclamation importXlsHistoryFileService;
  @Autowired
  AbonnementRepository abonnementRepository;
  @Autowired
  StatusrecService statusService;
  @Autowired
  ServicetypeService servicetypeService;
  @Autowired
  MotifrecService motifrecService;
  @Autowired
  private ReclamationHistoryService reclamationHistoryService;

  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  public void uploadReclamationsTTenmasse(MultipartFile file, RedirectAttributes redirectAttrs,
      HttpServletResponse response) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      LOGGER.info("user connected upload reclamations with xls: " + user);

      // Statistics counters
      int annuler = 0;
      int cloturee = 0;
      int errorRow = 0;
      int successRow = 0;
      int notfound = 0;
      int fixeNonExisteClacule = 0;
      int emptycell = 0;
      int exceptionRow = 0;
      ArrayList<Map<String, Object>> allSmsToSend = new ArrayList<>();
      if (!file.isEmpty()) {
        String extension =
            file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

        if (extension.equals("xlsx") || extension.equals("xls")) {
          Workbook workbook = null;
          try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();
            File uploadDir = new File(pathuploadxlsx);
            if (!uploadDir.exists()) {
              uploadDir.mkdirs();
            }

            String nameFile = "ImportReclamationFilesFromTT_" + dtf.format(now) + ".xlsx";
            Path path = Paths.get(pathuploadxlsx + nameFile);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            if (extension.equalsIgnoreCase("xls")) {
              workbook = new HSSFWorkbook(file.getInputStream());
            } else {
              workbook = new XSSFWorkbook(file.getInputStream());
            }
            Sheet worksheet = workbook.getSheetAt(0);

            if (worksheet.getRow(0) != null && validateHeaders(worksheet.getRow(0))) {
              // if line depass 300 line reject the file
              int totalRows = worksheet.getPhysicalNumberOfRows();
              int maxAllowedRows = 1001; // 1 header + 300 data rows

              // Reject file if it exceeds 300 lines
              if (totalRows > maxAllowedRows) {
                redirectAttrs.addFlashAttribute("message", "filetoomanylines");
                redirectAttrs.addFlashAttribute("lineCount", totalRows - 1); // Subtract header
                redirectAttrs.addFlashAttribute("maxLines", 1000);
                LOGGER.warn("File rejected: {} lines exceed maximum allowed (1000)", totalRows - 1);
                return; // Reject the file entirely
              }

              ImportXlsHistoryFileReclamation historyFileXls =
                  new ImportXlsHistoryFileReclamation();
              historyFileXls.setUser(user);
              importXlsHistoryFileRepository.save(historyFileXls);

              for (int index = 1; index < worksheet.getPhysicalNumberOfRows(); index++) {
                try {
                  Row row = worksheet.getRow(index);
                  if (row != null && !isRowEmpty(row)) {
                    ProcessResult result =
                        processRowSafely(row, historyFileXls, index, allSmsToSend);

                    // Update counters based on processing result
                    switch (result.getStatus()) {
                      case "SUCCESS":
                        successRow++;
                        break;
                      case "ERROR":
                        errorRow++;
                        break;
                      case "EMPTY_CELL":
                        emptycell++;
                        break;
                      case "NOT_FOUND":
                        notfound++;
                        break;
                      case "EXCEPTION":
                        exceptionRow++;
                        break;
                    }

                    if ("Annuler".equals(result.getEtat()))
                      annuler++;
                    if ("Clôturée".equals(result.getEtat()))
                      cloturee++;
                  }
                } catch (Exception rowException) {
                  exceptionRow++;
                  LOGGER.error("Error processing row {}: {}", index, rowException.getMessage(),
                      rowException);
                  try {
                    importXlsHistoryService.insertNewImportXlsHistory("Exception", "Row " + index,
                        "Error: " + rowException.getMessage(), null,
                        historyFileXls.getXlsHistoriqueFile());
                  } catch (Exception historyException) {
                    LOGGER.error("Failed to record history for row {}: {}", index,
                        historyException.getMessage());
                  }
                }
              }
              // Send all SMS messages together after processing all rows
              if (!allSmsToSend.isEmpty()) {
                try {
                  notificationservice.sendsmsnotification(allSmsToSend);
                  LOGGER.info("Sent {} SMS messages in array", allSmsToSend.size());
                } catch (Exception smsException) {
                  LOGGER.error("Failed to send bulk SMS: {}", smsException.getMessage(),
                      smsException);
                }
              }
              importXlsHistoryFileService.insertNewFileImportXlsHistory(
                  Integer.toString(errorRow + emptycell + exceptionRow),
                  Integer.toString(successRow), file.getOriginalFilename(), nameFile,
                  Integer.toString(successRow + errorRow + emptycell + exceptionRow),
                  historyFileXls, user);

            } else {
              redirectAttrs.addFlashAttribute("message", "erreuruploadeditabonnementenmasse");
            }

          } catch (Exception e) {
            LOGGER.error("Upload status en masse(uploadEditOperationEnMasse) catch exception: "
                + e.getMessage(), e);
            redirectAttrs.addFlashAttribute("message", "uploaderror");
          } finally {
            // prevent memory leaks
            if (workbook != null) {
              try {
                workbook.close();
              } catch (Exception e) {
                LOGGER.error("Error closing workbook: " + e.getMessage());
              }
            }
          }
        } else {
          redirectAttrs.addFlashAttribute("message", "invalidfileformat");
        }
      } else {
        redirectAttrs.addFlashAttribute("message", "fileempty");
      }

      buildResultMessages(redirectAttrs, emptycell, notfound, errorRow, fixeNonExisteClacule,
          annuler, cloturee, exceptionRow, successRow);
    }
  }

  private ProcessResult processRowSafely(Row row, ImportXlsHistoryFileReclamation historyFileXls,
      int rowIndex, ArrayList<Map<String, Object>> allSmsToSend) {
    try {
      return processRow(row, historyFileXls, allSmsToSend);
    } catch (Exception e) {
      LOGGER.error("Error in processRow for row {}: {}", rowIndex, e.getMessage(), e);

      String reference = "";
      String etat = "";
      try {
        DataFormatter formatter = new DataFormatter();
        reference = row.getCell(1) != null ? formatter.formatCellValue(row.getCell(1)) : "";
        etat = row.getCell(6) != null ? formatter.formatCellValue(row.getCell(6)) : "";
      } catch (Exception extractException) {
        LOGGER.error("Could not extract row data for error reporting: {}",
            extractException.getMessage());
      }
      importXlsHistoryService.insertNewImportXlsHistory("Exception", reference,
          "Row " + rowIndex + " - Error: " + e.getMessage(), null,
          historyFileXls.getXlsHistoriqueFile());

      return new ProcessResult("EXCEPTION", etat);
    }
  }

  private boolean validateHeaders(Row headerRow) {
    try {
      String telfixeCheckHader = String.valueOf(headerRow.getCell(0));
      String referenceCheckHader = String.valueOf(headerRow.getCell(1));
      String etatcheckHader = String.valueOf(headerRow.getCell(6));

      LOGGER.warn("Header xls file: " + telfixeCheckHader + "/" + referenceCheckHader + "/"
          + etatcheckHader);

      return Objects.equals(telfixeCheckHader, "N°téléphone")
          && Objects.equals(referenceCheckHader, "Réference")
          && Objects.equals(etatcheckHader, "ETAT");
    } catch (Exception e) {
      LOGGER.error("Error validating headers: " + e.getMessage());
      return false;
    }
  }

  private ProcessResult processRow(Row row, ImportXlsHistoryFileReclamation historyFileXls,
      ArrayList<Map<String, Object>> allSmsToSend) {
    if (row.getCell(0) == null || row.getCell(1) == null || row.getCell(6) == null) {
      return processRowWithMissingData(row, historyFileXls);
    }
    DataFormatter formatter = new DataFormatter();
    String telFixeString = formatter.formatCellValue(row.getCell(0));
    String referencett = formatter.formatCellValue(row.getCell(1));
    String etat = formatter.formatCellValue(row.getCell(6));
    String motif = formatter.formatCellValue(row.getCell(4));
    String dateReclamation = formatter.formatCellValue(row.getCell(3));
    String dateEtat = formatter.formatCellValue(row.getCell(7));
    String dateVerificationFSI = formatter.formatCellValue(row.getCell(10));
    String central = formatter.formatCellValue(row.getCell(9));
    String Gouvernorat = formatter.formatCellValue(row.getCell(8));
    if (telFixeString == null || telFixeString.trim().isEmpty()
        || !CrmUtils.isNumeric(telFixeString)) {
      importXlsHistoryService.insertNewImportXlsHistory("Erreur", referencett,
          "Numéro de téléphone invalide: " + telFixeString, null,
          historyFileXls.getXlsHistoriqueFile());
      return new ProcessResult("ERROR", etat);
    }

    Long telFixe = Long.parseLong(telFixeString);
    Reclamation reclamationByTelfix = reclamationRepository
        .findReclamationByTelFixeByCategoryByClient(telFixe, "Client", "Technique");
    Reclamation reclamationByRef =
        reclamationRepository.findReclamationByuniquereferencett(referencett);
    if (reclamationByTelfix == null) {
      return processNewReclamation(telFixe, referencett, reclamationByRef, etat, historyFileXls,
          motif, dateReclamation, dateEtat, dateVerificationFSI, Gouvernorat, central,
          allSmsToSend);
    } else {
      if (reclamationByTelfix.getStatus().getNomStatut().equals(DBEtatTT.Clôturée)) {
        return processNewReclamation(telFixe, referencett, reclamationByRef, etat, historyFileXls,
            motif, dateReclamation, dateEtat, dateVerificationFSI, Gouvernorat, central,
            allSmsToSend);
      }
      return processExistingReclamation(reclamationByTelfix, referencett, etat, historyFileXls,
          motif, dateReclamation, dateEtat, dateVerificationFSI, Gouvernorat, central,
          allSmsToSend);
    }
  }

  private ProcessResult processNewReclamation(Long telFixe, String referencett,
      Reclamation reclamationByRef, String etat, ImportXlsHistoryFileReclamation historyFileXls,
      String motif, String dateReclamation, String dateEtat, String dateVerificationFSI,
      String Gouvernorat, String central, ArrayList<Map<String, Object>> allSmsToSend) {
    if (reclamationByRef != null) {
      importXlsHistoryService.insertNewImportXlsHistory("Erreur", referencett,
          "Cette référence TT est déjà associée à une réclamation "
              + reclamationByRef.getStatus().getNomStatut(),
          reclamationByRef, historyFileXls.getXlsHistoriqueFile());
      return new ProcessResult("ERROR", etat);
    } else {
      Servicetype serviceType = servicetypeService.getServicetypeByCategory("Technique");

      Reclamation newReclamation = new Reclamation();
      if (dateReclamation != null && !dateReclamation.trim().isEmpty()) {
        Date dateReclamationDate = parseExcelDate(dateReclamation);
        newReclamation.setDate_reclamationtt(dateReclamationDate);
        newReclamation.setCreatedDate(dateReclamationDate);
      } else {
        newReclamation.setCreatedDate(new Date());
      }
      if (dateEtat != null && !dateEtat.trim().isEmpty()) {
        Date dateEtatDate = parseExcelDate(dateEtat);
        newReclamation.setDate_etattt(dateEtatDate);

      }
      if (dateVerificationFSI != null && !dateVerificationFSI.trim().isEmpty()) {
        Date dateVerificationFSIDate = parseExcelDate(dateVerificationFSI);
        newReclamation.setDate_verificationfsi(dateVerificationFSIDate);

      }
      if (motif != null) {
        Motifrec motifdb = motifrecService.getMotifByName(motif);
        newReclamation.setMotif(motifdb);
      }
      if (Gouvernorat != null) {
        newReclamation.setGouvernorat(Gouvernorat);
      }
      if (central != null) {
        newReclamation.setCentral(central);
      }
      Abonnement ab = abonnementRepository.findTopByTelFixeOrderByCreatedDateDesc(telFixe);
      if (ab == null) {
        importXlsHistoryService.insertNewImportXlsHistory("Erreur", referencett,
            "Numéro fixe introuvable ou modifié suite à un transfert. Veuillez vérifier.",
            reclamationByRef, historyFileXls.getXlsHistoriqueFile());
        return new ProcessResult("ERROR", etat);
      }
      newReclamation.setCategory("Client");
      newReclamation.setClient(ab);
      newReclamation.setCreatedby(historyFileXls.getUser());
      newReclamation.setEditedby(historyFileXls.getUser());
      newReclamation.setDescription(motif);
      Statusrec statut = statusService.getStatusrecByName(etat);
      newReclamation.setEtattt(etat);
      newReclamation.setReferencett(referencett);
      newReclamation.setServiceType(serviceType);
      newReclamation.setSource("Import TT");
      newReclamation.setStatus(statut);

      reclamationRepository.save(newReclamation);
      importXlsHistoryService.insertNewImportXlsHistory("Success", etat,
          "Nouvelle réclamation crée avec succès", newReclamation,
          historyFileXls.getXlsHistoriqueFile());
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String currentUser = authentication.getName();
      User userconnected = userRepository.findUsersByEmail(currentUser);
      String description = "Création d'une réclamation via import sous référence:"
          + newReclamation.getRef_reclamation();
      reclamationHistoryService.saveNewHistorique(userconnected, newReclamation.getReclamationid(),
          description);
      // prepareSmsMessage(newReclamation, allSmsToSend);
      return new ProcessResult("SUCCESS", etat);
    }
  }

  private ProcessResult processExistingReclamation(Reclamation reclamation, String referencett,
      String etat, ImportXlsHistoryFileReclamation historyFileXls, String motif,
      String dateReclamation, String dateEtat, String dateVerificationFSI, String Gouvernorat,
      String central, ArrayList<Map<String, Object>> allSmsToSend) {
    if (reclamation.getReclamationid() != null
        && !reclamation.getStatus().getNomStatut().equals(DBEtatTT.Clôturée)) {

      if (reclamation.getReferencett() != null
          && !reclamation.getReferencett().equals(referencett)) {
        importXlsHistoryService.insertNewImportXlsHistory("Erreur", referencett,
            "On a une réclamation en cours avec une référence TT différente", reclamation,
            historyFileXls.getXlsHistoriqueFile());
        return new ProcessResult("ERROR", etat);
      } else {
        Date oldDateReclamation = reclamation.getDate_reclamationtt();
        Date oldDateEtat = reclamation.getDate_etattt();
        Date oldDateVerificationFSI = reclamation.getDate_verificationfsi();
        Motifrec oldMotif = reclamation.getMotif();
        String oldEtat = reclamation.getEtattt();
        String oldReferenceTT = reclamation.getReferencett();
        Boolean NotsameStatut = true;
        if (oldEtat != null) {
          NotsameStatut = !oldEtat.equals(etat);
        }
        StringBuilder historyDesc = new StringBuilder();
        DateFormat HISTORY_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (dateReclamation != null && !dateReclamation.trim().isEmpty()) {
          Date dateReclamationDate = parseExcelDate(dateReclamation);
          if (oldDateReclamation == null || !oldDateReclamation.equals(dateReclamationDate)) {
            historyDesc.append("Date réclamation [")
                .append(oldDateReclamation != null ? HISTORY_DATE_FORMAT.format(oldDateReclamation)
                    : "Aucune")
                .append(" à ").append(HISTORY_DATE_FORMAT.format(dateReclamationDate))
                .append("], ");
          }
          reclamation.setDate_reclamationtt(dateReclamationDate);
        }
        if (dateEtat != null && !dateEtat.trim().isEmpty()) {
          Date dateEtatDate = parseExcelDate(dateEtat);
          if (oldDateEtat == null || !oldDateEtat.equals(dateEtatDate)) {
            historyDesc.append("Date état [")
                .append(oldDateEtat != null ? HISTORY_DATE_FORMAT.format(oldDateEtat) : "Aucune")
                .append(" à ").append(HISTORY_DATE_FORMAT.format(dateEtatDate)).append("], ");
          }
          reclamation.setDate_etattt(dateEtatDate);

        }
        if (dateVerificationFSI != null && !dateVerificationFSI.trim().isEmpty()) {
          Date dateVerificationFSIDate = parseExcelDate(dateVerificationFSI);
          if (oldDateVerificationFSI == null
              || !oldDateVerificationFSI.equals(dateVerificationFSIDate)) {
            historyDesc.append("Date vérification FSI [")
                .append(oldDateVerificationFSI != null
                    ? HISTORY_DATE_FORMAT.format(oldDateVerificationFSI)
                    : "Aucune")
                .append(" à ").append(HISTORY_DATE_FORMAT.format(dateVerificationFSIDate))
                .append("], ");
          }
          reclamation.setDate_verificationfsi(dateVerificationFSIDate);

        }
        if (motif != null && !motif.trim().isEmpty()) {
          Motifrec newMotif = motifrecService.getMotifByName(motif);

          if (newMotif != null
              && (oldMotif == null || !newMotif.getMotifId().equals(oldMotif.getMotifId()))) {

            historyDesc.append("Motif [")
                .append(oldMotif != null ? oldMotif.getNomMotif() : "Aucun").append(" à ")
                .append(newMotif.getNomMotif()).append("], ");
          }

          reclamation.setMotif(newMotif);
        }
        reclamation.setDescription(motif);
        if (etat != null && !etat.trim().isEmpty() && !etat.equals(oldEtat)) {
          historyDesc.append("État [").append(oldEtat != null ? oldEtat : "Aucun").append(" à ")
              .append(etat).append("], ");

          reclamation.setEtattt(etat);
          Statusrec statut = statusService.getStatusrecByName(etat);
          reclamation.setStatus(statut);
        }
        if (referencett != null && !referencett.trim().isEmpty()
            && !referencett.equals(oldReferenceTT)) {

          historyDesc.append("Référence TT [")
              .append(oldReferenceTT != null ? oldReferenceTT : "Aucune").append(" à ")
              .append(referencett).append("], ");

          reclamation.setReferencett(referencett);
        }
        if (Gouvernorat != null) {
          reclamation.setGouvernorat(Gouvernorat);
        }
        if (central != null) {
          reclamation.setCentral(central);
        }
        reclamationRepository.save(reclamation);

        importXlsHistoryService.insertNewImportXlsHistory("Success", etat,
            "Changement de statut TT à «" + etat + " » est effectué avec succès", reclamation,
            historyFileXls.getXlsHistoriqueFile());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        User userconnected = userRepository.findUsersByEmail(currentUser);
        if (historyDesc.length() > 0) {
          historyDesc.setLength(historyDesc.length() - 2);
          reclamationHistoryService.saveNewHistorique(userconnected, reclamation.getReclamationid(),
              "Modification via import XLS : " + historyDesc.toString());
        }
        // do not add it to send sms
        if (NotsameStatut) {
          // prepareSmsMessage(reclamation, allSmsToSend);
        }
        return new ProcessResult("SUCCESS", etat);

      }
    } else {
      importXlsHistoryService.insertNewImportXlsHistory("Success", referencett,
          "Cette réclamation est déjà Clôturée ", reclamation,
          historyFileXls.getXlsHistoriqueFile());
      return new ProcessResult("Success", etat);
    }
  }

  private ProcessResult processRowWithMissingData(Row row,
      ImportXlsHistoryFileReclamation historyFileXls) {
    DataFormatter formatter = new DataFormatter();

    String referencett = row.getCell(1) != null ? formatter.formatCellValue(row.getCell(1)) : "";
    String etat = row.getCell(6) != null ? formatter.formatCellValue(row.getCell(6)) : "";
    String telFixeString = row.getCell(0) != null ? formatter.formatCellValue(row.getCell(0)) : "";

    long telFixe = 0L;
    if (!telFixeString.isEmpty() && CrmUtils.isNumeric(telFixeString)) {
      telFixe = Long.parseLong(telFixeString);
    }

    importXlsHistoryService.insertNewImportXlsHistory(
        "Erreur", referencett, "Un ou plusieurs paramètres sont manquants(Etat:" + etat
            + "/reference TT: " + referencett + "/Tel Fixe: " + telFixe + " )",
        null, historyFileXls.getXlsHistoriqueFile());

    return new ProcessResult("EMPTY_CELL", etat);
  }

  private void buildResultMessages(RedirectAttributes redirectAttrs, int emptycell, int notfound,
      int errorRow, int fixeNonExisteClacule, int annuler, int cloturee, int exceptionRow,
      int successRow) {
    StringBuilder dangerAlerts = new StringBuilder();
    if (emptycell > 0) {
      dangerAlerts.append("Lignes avec données manquantes: ").append(emptycell).append(", ");
    }
    if (errorRow > 0) {
      dangerAlerts.append("Erreurs de traitement: ").append(errorRow).append(", ");
    }
    if (exceptionRow > 0) {
      dangerAlerts.append("Exceptions: ").append(exceptionRow).append(", ");
    }
    if (fixeNonExisteClacule > 0) {
      dangerAlerts.append("Fixe Non Existe: ").append(fixeNonExisteClacule).append(", ");
    }
    if (dangerAlerts.length() > 0) {
      dangerAlerts.setLength(dangerAlerts.length() - 2);
      redirectAttrs.addFlashAttribute("dangerAlerts", dangerAlerts.toString());
    }

    StringBuilder successAlerts = new StringBuilder();
    if (successRow > 0) {
      successAlerts.append("Lignes traitées avec succès: ").append(successRow).append(", ");
    }
    if (annuler > 0) {
      successAlerts.append("Annuler: ").append(annuler).append(", ");
    }
    if (cloturee > 0) {
      successAlerts.append("Clôturée: ").append(cloturee).append(", ");
    }
    if (successAlerts.length() > 0) {
      successAlerts.setLength(successAlerts.length() - 2);
      redirectAttrs.addFlashAttribute("successAlerts", successAlerts.toString());
    }
    redirectAttrs.addFlashAttribute("summary",
        String.format("Total: %d lignes traitées (%d succès, %d erreurs)",
            successRow + errorRow + emptycell + exceptionRow, successRow,
            errorRow + emptycell + exceptionRow));
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

  private static class ProcessResult {
    private final String status;
    private final String etat;

    public ProcessResult(String status, String etat) {
      this.status = status;
      this.etat = etat;
    }

    public String getStatus() {
      return status;
    }

    public String getEtat() {
      return etat;
    }
  }

  private Date parseExcelDate(String dateString) {
    if (dateString == null || dateString.trim().isEmpty()) {
      return null;
    }

    try {
      try {
        double excelDateValue = Double.parseDouble(dateString);
        return convertExcelDateToJavaDate(excelDateValue);
      } catch (NumberFormatException e) {
      }

      String[] possibleFormats = {"M/d/yy H:mm", "M/d/yy HH:mm", "M/d/yyyy H:mm", "M/d/yyyy HH:mm",
          "dd/MM/yy H:mm", "dd/MM/yy HH:mm", "dd/MM/yyyy H:mm", "dd/MM/yyyy HH:mm", "MM/dd/yy H:mm",
          "MM/dd/yy HH:mm", "MM/dd/yyyy H:mm", "MM/dd/yyyy HH:mm"};

      for (String format : possibleFormats) {
        try {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
          java.time.LocalDateTime localDateTime =
              java.time.LocalDateTime.parse(dateString, formatter);
          return java.util.Date
              .from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
          continue;
        }
      }

      return CrmUtils.convertStringToDate(dateString);

    } catch (Exception e) {
      LOGGER.warn("Could not parse date: '{}'. Using current date instead. Error: {}", dateString,
          e.getMessage());
      return new Date();
    }
  }

  private Date convertExcelDateToJavaDate(double excelDate) {
    long millisSince1900 = (long) ((excelDate - 25569) * 86400 * 1000);
    return new Date(millisSince1900);
  }

  private void prepareSmsMessage(Reclamation reclamation,
      ArrayList<Map<String, Object>> allSmsToSend) {
    String statutCheck = reclamation.getStatus().getNomStatut();

    if (statutCheck != null && (statutCheck.equalsIgnoreCase(NomStatutReclamation.Clôturée)
        || statutCheck.equalsIgnoreCase(NomStatutReclamation.OPENED)
        || statutCheck.equalsIgnoreCase(NomStatutReclamation.SAVED)
        || statutCheck.equalsIgnoreCase(NomStatutReclamation.IN_PROGRESS))) {

      if (reclamation.getClient() != null && reclamation.getClient().getTelMobile() != null) {
        Map<String, Object> smsMessage = new HashMap<>();
        smsMessage.put("number", reclamation.getClient().getTelMobile().toString());
        smsMessage.put("message", generateSmsMessage(reclamation));
        // allSmsToSend.add(smsMessage);
      }
    }
  }

  private String generateSmsMessage(Reclamation reclamation) {
    String statut = reclamation.getStatus().getNomStatut();
    String ref = reclamation.getRef_reclamation();

    switch (statut) {
      case NomStatutReclamation.SAVED:
        return "Nety vous informe que votre réclamation " + ref + " a été enregistrée. "
            + "Nous restons à votre disposition pour toute information.";
      case NomStatutReclamation.IN_PROGRESS:
        return "Nety vous informe que votre réclamation " + ref
            + " est en cours de traitement. Merci pour votre patience et votre confiance.";
      case NomStatutReclamation.Clôturée:
        return "Nety a le plaisir de vous informer que votre réclamation " + ref
            + " est cloturée. Merci pour votre confiance et votre fidélité.";
      case NomStatutReclamation.RELANCEE:
        return "Nety vous informe que votre réclamation " + ref
            + " a été relancée. Nous faisons le nécessaire et restons à votre disposition.";
      case NomStatutReclamation.OPENED:
        return "Nety vous informe que votre réclamation " + ref + " a été reçue. "
            + "Nous traitons votre demande dans les meilleurs délais.";
      default:
        return null;
    }
  }


}
