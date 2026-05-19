package crm.chifco.com.service;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import crm.chifco.com.templateclasse.Recouvrement;
import crm.chifco.com.utils.CrmUtils;

public class ExportExcelRecouvrement extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    response.addHeader("Content-Disposition", "attachment;fileName=recouvrement.xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<Recouvrement> list = (List<Recouvrement>) model.get("list");
    // create one sheet
    Sheet sheet = workbook.createSheet("Liste Recouvrement");
    // create row0 as a header
    Row row0 = sheet.createRow(0);
    row0.createCell(0).setCellValue("Liste Recouvrement");
    // permiere ligne

    Row row1 = sheet.createRow(1); // creation de la deuxieme ligne du ficheir csv

    // set les colonnes du deuxiieme lignes
    row1.createCell(0).setCellValue("Nom"); // colonne ref_facture
    row1.createCell(1).setCellValue("Prenom"); // colonne createddate
    row1.createCell(2).setCellValue("Cin"); // colonne date_echeance
    row1.createCell(3).setCellValue("0-30 jrs"); // colonne Payement
    row1.createCell(4).setCellValue("31-60 jrs");
    row1.createCell(5).setCellValue("61-90 jrs");
    row1.createCell(6).setCellValue("91-120 jours");
    row1.createCell(7).setCellValue("plus de 120 jours");
    row1.createCell(8).setCellValue("Total");
    int rowCount = 2; // insertion à partir du 3eme ligne

    for (Recouvrement p : list) {
      Row row = sheet.createRow(rowCount++);
      String payee = "non";

      row.createCell(0).setCellValue(p.getFirstname());
      row.createCell(1).setCellValue(p.getLastname());
      row.createCell(2).setCellValue(p.getCin());
      String depassement30 = "0";
      if (p.getDepasseDate30() != null)
        depassement30 = CrmUtils.formatDoubleInputToString(p.getDepasseDate30());
      row.createCell(3).setCellValue(depassement30);

      String depassement60 = "0";
      if (p.getDepasseDate60() != null)
        depassement60 = CrmUtils.formatDoubleInputToString(p.getDepasseDate60());
      row.createCell(4).setCellValue(depassement60);

      String depassement90 = "0";
      if (p.getDepasseDate90() != null)
        depassement90 = CrmUtils.formatDoubleInputToString(p.getDepasseDate90());
      row.createCell(5).setCellValue(depassement90);

      String depassement120 = "0";
      if (p.getDepasseDate120() != null)
        depassement120 = CrmUtils.formatDoubleInputToString(p.getDepasseDate120());
      row.createCell(6).setCellValue(depassement120);

      String getPlus120 = "0";
      if (p.getPlus120() != null)
        getPlus120 = p.getPlus120().toString();
      row.createCell(7).setCellValue(getPlus120);

      row.createCell(8)
          .setCellValue(Double.parseDouble(depassement30) + Double.parseDouble(depassement60)
              + Double.parseDouble(depassement90) + Double.parseDouble(depassement120)
              + Double.parseDouble(getPlus120));
    }
  }

}
