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
import crm.chifco.com.model.AntivirusKey;

public class AntiVirusKeyExcelExport extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    // TODO Auto-generated method stub

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();
    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    // define excel file name to be exported
    response.addHeader("Content-Disposition",
        "attachment;fileName=Liste des clés" + "_" + dtf.format(now) + ".xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<AntivirusKey> list = (List<AntivirusKey>) model.get("list");
    // create one sheet
    Sheet sheet = workbook.createSheet("Liste des clés ");

    // Create a new row for the title
    Row titleRow = sheet.createRow(0);

    // Create a cell for the title
    Cell titleCell = titleRow.createCell(0);
    titleCell.setCellValue("Liste des clés ");

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
    row0.createCell(0).setCellValue("Clé");
    row0.createCell(1).setCellValue("Duréé");
    row0.createCell(2).setCellValue("Client");
    row0.createCell(3).setCellValue("Date d'affectation");
    row0.createCell(4).setCellValue("Affectée par");
    row0.createCell(5).setCellValue("Date d'insertion");
    row0.createCell(6).setCellValue("Inséré par");
    row0.createCell(7).setCellValue("Etat");
    row0.createCell(8).setCellValue("Type");
    // create row1 onwards from List<T>

    int rowNum = 2;
    for (AntivirusKey spec : list) {
      Row row = sheet.createRow(rowNum++);
      row.createCell(0).setCellValue(spec.getLicenseKey());
      row.createCell(1).setCellValue(spec.getDuree() + " mois");
      if (spec.getAbonnement() != null) {
        row.createCell(2).setCellValue(
            spec.getAbonnement().getFirstName() + " " + spec.getAbonnement().getLastName() + " ("
                + spec.getAbonnement().getReferenceClient() + ")");
        row.createCell(3).setCellValue(formatDate.format(spec.getDateAffectation()));
        row.createCell(4).setCellValue(spec.getAffectedBy().getFirstName() + " "
            + spec.getAffectedBy().getLastName() + " (" + spec.getAffectedBy().getCodeUser() + ")");
      }

      row.createCell(5).setCellValue(formatDate.format(spec.getCreatedDate()));
      row.createCell(6).setCellValue(spec.getCreatedBy().getFirstName() + " "
          + spec.getCreatedBy().getLastName() + " (" + spec.getCreatedBy().getCodeUser() + ")");
      if (spec.isActive()) {
        row.createCell(7).setCellValue("Activé");
      } else {
        row.createCell(7).setCellValue("Désactivé");
      }
      row.createCell(8).setCellValue(spec.getType());

    }
    // Auto-size columns for better view
    for (int i = 0; i < 7; i++) {
      sheet.autoSizeColumn(i);
    }
  }

}
