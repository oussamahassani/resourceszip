package crm.chifco.com.service;

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
import crm.chifco.com.templateclasse.FraisTTData;

public class FacturationTTCalculeServiceExcelExport extends AbstractXlsxView {
  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();
    // define excel file name to be exported
    response.addHeader("Content-Disposition",
        "attachment;fileName=factureTT" + "_" + dtf.format(now) + ".xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<FraisTTData> list = (List<FraisTTData>) model.get("list");
    // create one sheet
    Sheet sheet = workbook.createSheet("Facture TT Liste");
    Row row0 = sheet.createRow(0);
    row0.createCell(0).setCellValue("Date de connection");
    row0.createCell(1).setCellValue("Date d'exportation");
    row0.createCell(2).setCellValue("Numero telphone");
    row0.createCell(3).setCellValue("Categorie Internet");
    row0.createCell(4).setCellValue("Forfait Internet");
    row0.createCell(5).setCellValue("Prix Service");
    row0.createCell(6).setCellValue("Code Service");
    row0.createCell(7).setCellValue("Date TT Calcule");

    int rowNum = 1;
    for (FraisTTData spec : list) {
      Row row = sheet.createRow(rowNum++);
      row.createCell(0).setCellValue(spec.getDate_connection());
      row.createCell(1).setCellValue(spec.getCreated_date().toString());
      row.createCell(2).setCellValue(spec.getNumero_telephone().toString());
      row.createCell(3).setCellValue(spec.getCatagorie_internt());
      row.createCell(4).setCellValue(spec.getForfait_internt());
      if (spec.getPrix_service() != null) {
        row.createCell(5).setCellValue(spec.getPrix_service());
      } else {
        row.createCell(5).setCellValue("");
      }

      row.createCell(6).setCellValue(spec.getCode_frais());

      if (spec.getRecheche_date() != null) {
        row.createCell(7).setCellValue(spec.getRecheche_date());
      }
    }
  }
}
