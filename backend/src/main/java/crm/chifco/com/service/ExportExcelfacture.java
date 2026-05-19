package crm.chifco.com.service;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import crm.chifco.com.model.Facture;

public class ExportExcelfacture extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    response.addHeader("Content-Disposition", "attachment;fileName=ExportExcelfacture_.xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<Facture> list = (List<Facture>) model.get("list");
    // create one sheet
    Sheet sheet = workbook.createSheet("Liste Facture");
    // create row0 as a header
    Row row0 = sheet.createRow(0);
    row0.createCell(0).setCellValue("Liste Facture");
    // permiere ligne

    Row row1 = sheet.createRow(1); // creation de la deuxieme ligne du ficheir csv

    // set les colonnes du deuxiieme lignes
    row1.createCell(0).setCellValue("Facture numero"); // colonne ref_facture
    row1.createCell(1).setCellValue("Create date"); // colonne createddate
    row1.createCell(2).setCellValue("Date Echeance"); // colonne date_echeance
    row1.createCell(3).setCellValue("Payement"); // colonne Payement
    row1.createCell(4).setCellValue("Remise");
    row1.createCell(5).setCellValue("montant_total");
    row1.createCell(6).setCellValue("montant_tva");
    row1.createCell(7).setCellValue("timbrefiscale");
    row1.createCell(8).setCellValue("Reference demande");
    row1.createCell(9).setCellValue("user");

    int rowCount = 2; // insertion à partir du 3eme ligne

    for (Facture p : list) {
      Row row = sheet.createRow(rowCount++);
      String payee = "non";
      if (p.getEtat_facture() == true)
        payee = "oui";

      // ligne , numero de colonne , data , style
      row.createCell(0).setCellValue(p.getRef_facture()); // inserer ref_facture
      row.createCell(1).setCellValue(p.getCreatedDate()); // inserer createddate
      row.createCell(2).setCellValue(p.getDate_echeance()); // inserer date_echeance
      row.createCell(3).setCellValue(payee); // inserer Payement

      row.createCell(4).setCellValue(p.getRemise()); // inserer remise
      row.createCell(5).setCellValue(p.getMontant_payer());
      row.createCell(6).setCellValue(p.getMontantTva()); // inserer taux_tva
      row.createCell(7).setCellValue(p.getTimbrefiscale()); // inserer timbrefiscale
      if (p.getCommande() != null)
        row.createCell(8).setCellValue(p.getCommande().getClient().getReferenceClient()); // affect
      // demande id

      else
        row.createCell(8).setCellValue("");
      if (p.getUser() != null)
        row.createCell(9)
            .setCellValue(p.getUser().getFirstName() + ' ' + p.getUser().getLastName()); // inserer
      // user
      // id
      else
        row.createCell(9).setCellValue(' ');

    }

  }

}
