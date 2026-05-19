package crm.chifco.com.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.EntryAvoirClient;

public class AvoirClientExcelExport extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();
    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    // define excel file name to be exported
    response.addHeader("Content-Disposition",
        "attachment;fileName=Avoir liste" + "_" + dtf.format(now) + ".xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<AvoirClient> list = (List<AvoirClient>) model.get("list");
    // create one sheet
    Sheet sheet = workbook.createSheet("Avoir Liste");
    Sheet sheet2 = workbook.createSheet("Tva Avoir Liste");
    // Create a new row for the title
    Row titleRow = sheet.createRow(0);

    // Create a cell for the title
    Cell titleCell = titleRow.createCell(0);
    titleCell.setCellValue("Liste factures d'avoirs");

    // Créez un style de cellule pour formater le montant avec 3 chiffres après la virgule
    CellStyle style = workbook.createCellStyle();
    DataFormat dataFormat = workbook.createDataFormat();
    style.setDataFormat(dataFormat.getFormat("#,##0.000"));

    // Create a cell style for the title
    CellStyle titleCellStyle = workbook.createCellStyle();
    Font titleFont = workbook.createFont();
    titleFont.setBold(true);
    titleFont.setFontHeightInPoints((short) 18); // Set the font size to 18 points
    titleCellStyle.setFont(titleFont);
    titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
    titleCell.setCellStyle(titleCellStyle);

    // Merge cells from A1 to O1
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));

    // create row0 as a header
    Row row0 = sheet.createRow(1);
    row0.createCell(0).setCellValue("Date de Creation");
    row0.createCell(1).setCellValue("Référence de l'avoir");
    row0.createCell(2).setCellValue("Référence Client");
    row0.createCell(3).setCellValue("Nom et prénom client");
    row0.createCell(4).setCellValue("Motif avoir");
    row0.createCell(5).setCellValue("Statut Avoir");
    row0.createCell(6).setCellValue("Montant Avoir");
    row0.createCell(7).setCellValue("Montant HT");
    row0.createCell(8).setCellValue("Montant Tva");
    row0.createCell(9).setCellValue("Timber Fiscale");
    row0.createCell(10).setCellValue("Créé par");
    row0.createCell(11).setCellValue("Code du Créateur");
    row0.createCell(12).setCellValue("Utilisé par");
    row0.createCell(13).setCellValue("Code de l'Utilisateur");
    row0.createCell(14).setCellValue("Autorité d'ajout dans les bordereaux");
    row0.createCell(15).setCellValue("Statut du Bordereau");
    row0.createCell(16).setCellValue("Reference Facture");
    row0.createCell(17).setCellValue("Reference Reclamation");
    row0.createCell(18).setCellValue("Type Avoir");
    row0.createCell(19).setCellValue("Date debut coupure");
    row0.createCell(20).setCellValue("Date fin coupure");
    // create row1 onwards from List<T>


    Row headerRow = sheet2.createRow(0);
    headerRow.createCell(0).setCellValue("Numéro Facture Avoir");
    headerRow.createCell(1).setCellValue("Base Tva");
    headerRow.createCell(2).setCellValue("Montant Tva");

    // Auto-size columns based on content length
    for (int i = 0; i < row0.getLastCellNum(); i++) {
      sheet.autoSizeColumn(i);
    }
    int rowNum = 2;
    int rowEntryNum = 2;
    for (AvoirClient spec : list) {
      Row row = sheet.createRow(rowNum++);
      row.createCell(0).setCellValue(formatDate.format(spec.getCreatedDate()));
      row.createCell(1).setCellValue(spec.getRefAvoirClient());
      if (spec.getAbonnement() != null) {
        row.createCell(2).setCellValue(spec.getAbonnement().getReferenceClient());
        row.createCell(3).setCellValue(
            spec.getAbonnement().getFirstName() + " " + spec.getAbonnement().getLastName());

      }
      row.createCell(4).setCellValue(spec.getMotifAvoir());
      if (spec.getIsClientPayed()) {
        row.createCell(5).setCellValue("Payee par client");
      }

      else {
        row.createCell(5).setCellValue("Non payee par le client");
      }

      row.createCell(5).setCellValue(spec.getIsClientPayed() == true ? "Payé" : "Non payé");

      row.createCell(6).setCellValue(-spec.getMontantAvoir());
      row.getCell(6).setCellStyle(style);

      row.createCell(7).setCellValue(-spec.getMontantHt());
      row.getCell(7).setCellStyle(style);

      row.createCell(8).setCellValue(-spec.getMontantTva());
      row.getCell(8).setCellStyle(style);
      if (spec.getTimbrefiscale() != null) {
        row.createCell(9).setCellValue(-spec.getTimbrefiscale());
      }
      
      if (spec.getCreePar() != null) {
        row.createCell(10)
            .setCellValue(spec.getCreePar().getLastName() + " " + spec.getCreePar().getFirstName());
        row.createCell(11).setCellValue(spec.getCreePar().getCodeUser());
      }
      if (spec.getUsedBy() != null) {
        row.createCell(12)
            .setCellValue(spec.getUsedBy().getLastName() + " " + spec.getUsedBy().getFirstName());
        row.createCell(13).setCellValue(spec.getUsedBy().getCodeUser());
      }

      row.createCell(14)
          .setCellValue(spec.getCanRevendeurViewed() == true ? "Autorisé" : "Non autorisé");
      row.createCell(15).setCellValue(spec.getHas_bordereau() == true ? "Utilisé" : "Non utilisé");
      row.createCell(16).setCellValue(spec.getFacture() != null ? spec.getFacture().toString()  : "----");
      row.createCell(17).setCellValue(spec.getRefReclamation() != null ? spec.getRefReclamation() : "---");
      row.createCell(18).setCellValue(spec.getIsJestCo() != null && spec.getIsJestCo()  == true ? "Jest Co" : "Avoir Reclamation");
      if (spec.getDateDebutCoupure() != null) {
          row.createCell(19).setCellValue(spec.getDateDebutCoupure().toString());
        }
      if (spec.getDateFinCoupure() != null) {
          row.createCell(20).setCellValue(spec.getDateFinCoupure().toString());
        }
      for (EntryAvoirClient entryAvoir : spec.getAvoiClientEntry()) {
        Row rowentryAvoir = sheet2.createRow(rowEntryNum++);
        rowentryAvoir.createCell(0).setCellValue(spec.getRefAvoirClient());
        if (entryAvoir != null && entryAvoir.getBaseTva() != null) {
          rowentryAvoir.createCell(1).setCellValue(entryAvoir.getBaseTva());
        }
        if (entryAvoir != null && entryAvoir.getMontantTva() != null) {
          rowentryAvoir.createCell(2).setCellValue(entryAvoir.getMontantTva());
        }

      }

    }



  }
}
