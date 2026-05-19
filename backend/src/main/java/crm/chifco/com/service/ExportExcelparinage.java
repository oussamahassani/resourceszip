package crm.chifco.com.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import crm.chifco.com.model.Parinage;

public class ExportExcelparinage extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    response.addHeader("Content-Disposition", "attachment;fileName=ExportExcelparrainages_.xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<Parinage> list = (List<Parinage>) model.get("list");
    // create one sheet
    Sheet sheet = workbook.createSheet("Liste parrainages");
    // create row0 as a header
    Row row0 = sheet.createRow(0);
    row0.createCell(0).setCellValue("Liste parrainages");
    // permiere ligne

    Row row1 = sheet.createRow(1); // creation de la deuxieme ligne du ficheir csv

    // set les colonnes du deuxiieme lignes
    row1.createCell(0).setCellValue("Parrainage ref"); // colonne ref_facture
    row1.createCell(1).setCellValue("Date de création"); // colonne createddate
    row1.createCell(2).setCellValue("Numéro de téléphone du Parrainé"); // colonne Payement
    row1.createCell(3).setCellValue("Email");
    row1.createCell(4).setCellValue("Nom complet Parrainé");
    row1.createCell(5).setCellValue("cin parrainé");
    row1.createCell(6).setCellValue("Nom complet parrain");
    row1.createCell(7).setCellValue("Cin Parrain");
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    row1.createCell(8).setCellValue("Statut");


    int rowCount = 2; // insertion à partir du 3eme ligne

    for (Parinage p : list) {
      Row row = sheet.createRow(rowCount++);

      // ligne , numero de colonne , data , style
      row.createCell(0).setCellValue(p.getReferenceParinage()); // inserer ref_facture
      row.createCell(1).setCellValue(dateFormat.format(p.getCreatedDate())); // inserer createddate
      row.createCell(2).setCellValue(p.getTelFixe()); // inserer date_echeance
      row.createCell(3).setCellValue(p.getEmail()); // inserer Payement
      row.createCell(5).setCellValue(p.getNomParinee());
      row.createCell(5).setCellValue(p.getCinParinee()); // inserer remise
      row.createCell(6).setCellValue(p.getNomParrain()); // inserer taux_tva
      row.createCell(7).setCellValue(p.getCinParrain());
      row.createCell(8).setCellValue(p.getStatut()); // inserer timbrefiscale

    }

  }

}
