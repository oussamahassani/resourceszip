package crm.chifco.com.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import crm.chifco.com.templateclasse.EncaissementNonPayee;

public class FactureEncaissesExcelExport extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    boolean isAdmin = (boolean) model.get("isAdmin");

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dateFormatTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();

    // Créez un style de cellule pour formater le montant avec 3 chiffres après la virgule
    CellStyle style = workbook.createCellStyle();
    DataFormat dataFormat = workbook.createDataFormat();
    style.setDataFormat(dataFormat.getFormat("#,##0.000"));

    // define excel file name to be exported
    response.addHeader("Content-Disposition",
        "attachment;fileName=FactureEncaisses" + "_" + dtf.format(now) + ".xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<EncaissementNonPayee> list = (List<EncaissementNonPayee>) model.get("list");
    // create one sheet
    Sheet sheet = workbook.createSheet("Facture Encaisses");
    // Set date format
    CellStyle dateStyle = workbook.createCellStyle();
    dateStyle
        .setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd"));

    // create row0 as a header
    Row row0 = sheet.createRow(0);
    row0.createCell(0).setCellValue("Type");
    row0.createCell(1).setCellValue("Réf. Facture");
    row0.createCell(2).setCellValue("Prenom");
    row0.createCell(3).setCellValue("Nom");
    row0.createCell(4).setCellValue("Cin");
    row0.createCell(5).setCellValue("Type de paiement");
    row0.createCell(6).setCellValue("Date de paiement");
    row0.createCell(7).setCellValue("Date de creation ");
    row0.createCell(8).setCellValue("Montant payee");
    row0.createCell(9).setCellValue("Date d'echeance");
    row0.createCell(10).setCellValue("Payee par");
    row0.createCell(11).setCellValue("Code  Revendeur");
    row0.createCell(12).setCellValue("Statut de Facture");
    row0.createCell(13).setCellValue("Bordereau facture versée");

    // Auto-size columns based on content length
    for (int i = 0; i < row0.getLastCellNum(); i++) {
      sheet.autoSizeColumn(i);
    }


    // create row1 onwards from List<T>
    int rowNum = 1;
    for (EncaissementNonPayee spec : list) {
      Row row = sheet.createRow(rowNum++);

      // Ajouter la colonne "Type"
      String type = spec.getref_facture() != null ? "Facture" : "Avoir";
      row.createCell(0).setCellValue(type);

      if (spec.getref_facture() != null) {
        row.createCell(1).setCellValue(spec.getref_facture());
      } else {
        row.createCell(1).setCellValue(spec.getRef_avoir_client());
      }

      row.createCell(2).setCellValue(spec.getlast_name());
      row.createCell(3).setCellValue(spec.getFirst_name());
      row.createCell(4).setCellValue(spec.getCin());
      row.createCell(5).setCellValue(spec.getType_de_payment());
      row.createCell(6).setCellValue(dateFormatTime.format(spec.getdate()));

      if (spec.getcreated_date() != null) {
        row.createCell(7).setCellValue(dateFormat.format(spec.getcreated_date()));
      } else {
        row.createCell(7).setCellValue(dateFormat.format(spec.getCreadted_date_avoir()));
      }

      if (spec.getmontant_payer() != null) {
        row.createCell(8).setCellValue(spec.getmontant_payer());
        row.getCell(8).setCellStyle(style);
      } else if (spec.getMontantAvoir() != null) {
        row.createCell(8).setCellValue(-spec.getMontantAvoir());
        row.getCell(8).setCellStyle(style);
      }

      if (spec.getdate_echeance() != null) {
        row.createCell(9).setCellValue(dateFormat.format(spec.getdate_echeance()));
      }

      if (isAdmin) {
        row.createCell(10)
            .setCellValue(spec.getfirst_nameRevendeur() + " " + spec.getlast_nameRevendeur());
      } else {
        row.createCell(10).setCellValue("");
      }

      row.createCell(11).setCellValue(spec.getCodeRevendeur());

      if (spec.getIsChifcoPayed() != null && spec.getIsChifcoPayed() == true) {
        row.createCell(12).setCellValue("Facture versée");
        row.createCell(13).setCellValue(spec.getReferenceBordereau());
      } else {
        row.createCell(12).setCellValue("Facture non versée");
      }
    }

  }
}
