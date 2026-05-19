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
import crm.chifco.com.templateclasse.RevendeurRecap;

public class RecapExcelExport extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();
    // define excel file name to be exported
    response.addHeader("Content-Disposition",
        "attachment;fileName=Recap revendeur" + "_" + dtf.format(now) + ".xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<RevendeurRecap> list = (List<RevendeurRecap>) model.get("list");
    // create one sheet
    Sheet sheet = workbook.createSheet("Recap revendeur Liste");
    // create row0 as a header
    Row row0 = sheet.createRow(0);
    row0.createCell(0).setCellValue("Référence  revendeur");
    row0.createCell(1).setCellValue("Nom et prénom");
    row0.createCell(2).setCellValue("Adresse");
    row0.createCell(3).setCellValue("Plafon  Revendeur");
    row0.createCell(4).setCellValue("Chiffre d'affaire");
    row0.createCell(5).setCellValue("Montant total payé et versé");
    row0.createCell(6).setCellValue("Montant total payé et non  versé ");
    row0.createCell(7).setCellValue("Atteinte (%)");
    row0.createCell(8).setCellValue("Ville");
    row0.createCell(9).setCellValue("Gouvernorat");
    row0.createCell(10).setCellValue("Assignée à");
    row0.createCell(11).setCellValue("Code de l'assignée");
    row0.createCell(12).setCellValue("Nombre  de factures  non versé");
    row0.createCell(13).setCellValue("Nombre  de factures  versé ");
    // row0.createCell(13).setCellValue("Nbr de bordereau ");
    // create row1 onwards from List<T>
    int rowNum = 1;
    for (RevendeurRecap spec : list) {
      Row row = sheet.createRow(rowNum++);
      row.createCell(0).setCellValue(spec.getCode_user());
      row.createCell(1).setCellValue(spec.getFirstname() + " " + spec.getLastname());
      row.createCell(2).setCellValue(spec.getAdresse());
      if (spec.getPlafon_revendeur() != null) {
        row.createCell(3).setCellValue(spec.getPlafon_revendeur());
      }

      row.createCell(4).setCellValue(spec.getMontant() - spec.getTotalAvoir());
      row.createCell(5).setCellValue(spec.getMontantpayer() - spec.getAvoirConsomme());
      row.createCell(6).setCellValue(
          spec.getMontantnonpayer() - (spec.getTotalAvoir() - spec.getAvoirConsomme()));
      if (spec.getPlafon_revendeur() != null) {
        row.createCell(7).setCellValue(
            (spec.getMontant() - spec.getMontantpayer()) / spec.getPlafon_revendeur() * 100);
      }
      row.createCell(8).setCellValue(spec.getVille_name());
      row.createCell(9).setCellValue(spec.getGouvernorat_name());
      row.createCell(10)
          .setCellValue(spec.getAssignedUserFirstName() + " " + spec.getAssignedUserLastName());
      row.createCell(11).setCellValue(spec.getCodeUserAssignee());
      row.createCell(12).setCellValue(spec.getNbrFactureNonpayer());
      row.createCell(13).setCellValue(spec.getNbrFacturepayer());
      // row.createCell(13).setCellValue(spec.getNbrBordereau());
    }
  }
}
