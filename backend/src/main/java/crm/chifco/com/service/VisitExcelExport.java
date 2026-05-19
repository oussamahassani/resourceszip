package crm.chifco.com.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import crm.chifco.com.model.User;
import crm.chifco.com.model.Visite;


public class VisitExcelExport extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();
    response.addHeader("Content-Disposition",
        "attachment;fileName=Visite" + "_" + dtf.format(now) + ".xlsx");
    @SuppressWarnings("unchecked")
    List<Visite> list = (List<Visite>) model.get("list");
    User userConnected = (User) model.get("user");
    List<String> StringsRole =
        userConnected.getRole().getStringsRole(userConnected.getRole().getPrivileges());
    // create one sheet
    Sheet sheet = workbook.createSheet("Liste des visites");
    // create row0 as a header
    Row row0 = sheet.createRow(0);
    row0.createCell(0).setCellValue("Réf. visite");
    row0.createCell(1).setCellValue("Type visite");
    row0.createCell(2).setCellValue("Autre type");
    row0.createCell(3).setCellValue("Au revendeur");
    row0.createCell(4).setCellValue("Autre lieu");
    row0.createCell(5).setCellValue("Durée visite(Heures)");
    row0.createCell(6).setCellValue("Durée visite(Minutes)");
    row0.createCell(8).setCellValue("Date de création");
    row0.createCell(10).setCellValue("Crée par");
    row0.createCell(11).setCellValue("Latitude");
    row0.createCell(12).setCellValue("Longitude");
    row0.createCell(13).setCellValue("Commentaire");

    int rowNum = 1;
    for (Visite spec : list) {
      Row row = sheet.createRow(rowNum++);
      row.createCell(0)
          .setCellValue(spec.getReference_visite() != null ? spec.getReference_visite() : "");
      row.createCell(1)
          .setCellValue(spec.getTypeVisite() != null ? spec.getTypeVisite().getNomType() : "");
      row.createCell(2).setCellValue(spec.getAutreType() != null ? spec.getAutreType() : "");
      row.createCell(3).setCellValue(spec.getRevendeur() != null ? spec.getRevendeur() : "");
      row.createCell(4).setCellValue(spec.getAutreLieu() != null ? spec.getAutreLieu() : "");
      row.createCell(5).setCellValue(spec.getDureeVisiteHeures());
      row.createCell(6).setCellValue(spec.getDureeVisiteMinutes());

      DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

      if (spec.getCreatedDate() != null) {
        row.createCell(8).setCellValue(dateFormat.format(spec.getCreatedDate()));
      }



      if (spec.getCreatedBy() != null) {
        row.createCell(10).setCellValue(
            spec.getCreatedBy().getFirstName() + " " + spec.getCreatedBy().getLastName());
      } else {
        row.createCell(10).setCellValue("");
      }


      if (spec.getLatitude() != null) {
        row.createCell(11).setCellValue(spec.getLatitude());
      } else {
        row.createCell(11).setCellValue("");
      }
      if (spec.getLongitude() != null) {
        row.createCell(12).setCellValue(spec.getLongitude());
      } else {
        row.createCell(12).setCellValue("");
      }
      if (spec.getCommentaire() != null) {
        row.createCell(13).setCellValue(spec.getCommentaire());
      } else {
        row.createCell(13).setCellValue("");
      }
    }
    for (int i = 0; i < 30; i++) {
      sheet.autoSizeColumn(i);
    }

  }

}

