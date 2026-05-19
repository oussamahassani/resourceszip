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
import crm.chifco.com.model.DemandeCommission;

public class DemandeCommissionExcelExport extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    // TODO Auto-generated method stub
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();
    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    // define excel file name to be exported
    response.addHeader("Content-Disposition",
        "attachment;fileName=Demande Commission liste" + "_" + dtf.format(now) + ".xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<DemandeCommission> list = (List<DemandeCommission>) model.get("list");
    // create one sheet
    Sheet sheet = workbook.createSheet("Demande Commission Liste");

    // Créez un style de cellule pour formater le montant avec 3 chiffres après la virgule
    CellStyle montantStyle = workbook.createCellStyle();
    DataFormat montantFormat = workbook.createDataFormat();
    montantStyle.setDataFormat(montantFormat.getFormat("#,##0.000"));


    // create row0 as a header
    Row row0 = sheet.createRow(0);
    row0.createCell(0).setCellValue("Année / Mois");
    row0.createCell(1).setCellValue("Référence");
    row0.createCell(2).setCellValue("Revendeur");
    row0.createCell(3).setCellValue("Commission des demandes");
    row0.createCell(4).setCellValue("Commission sur Activation");
    row0.createCell(5).setCellValue("Commission des paiements");
    row0.createCell(6).setCellValue("Total HT");
    row0.createCell(7).setCellValue("TVA");
    row0.createCell(8).setCellValue("Montant TVA");
    row0.createCell(9).setCellValue("Total TTC");
    row0.createCell(10).setCellValue("Statut");

    int rowNum = 1;
    for (DemandeCommission spec : list) {
      Row row = sheet.createRow(rowNum++);

      // Formatter le numéro de mois avec deux chiffres
      String formattedMonth = String.format("%02d", spec.getCommission().getMois());
      row.createCell(0).setCellValue(spec.getCommission().getAnnee() + " / " + formattedMonth);
      row.createCell(1).setCellValue(spec.getRefDemandeCommission());
      row.createCell(2).setCellValue(spec.getDemandeBy().getFirstName() + " "
          + spec.getDemandeBy().getLastName() + " (" + spec.getDemandeBy().getCodeUser() + ")");
      row.createCell(3).setCellValue(spec.getCommission().getMontantCommissionDemandes());
      row.createCell(4).setCellValue(spec.getCommission().getMontantCommissionPremiereFacture());
      row.createCell(5).setCellValue(spec.getCommission().getMontantCommissionPaiements());
      row.createCell(6).setCellValue(spec.getCommission().getTotalHt());
      row.createCell(7).setCellValue(spec.getCommission().getTva() + "%");
      row.createCell(8).setCellValue(spec.getCommission().getMontantTva());
      row.createCell(9).setCellValue(spec.getCommission().getTotalTtc());

      if (spec.getStatut().equals("PAID")) {
        row.createCell(10).setCellValue("Payé");
      } else if (spec.getStatut().equals("IN_PROGRESS") || spec.getStatut().equals("AWAINTING_INVOICING")) {
        row.createCell(10).setCellValue("En cours");
      } else if (spec.getStatut().equals("REFUSED")) {
        row.createCell(10).setCellValue("Refusée");
      } else {
        row.createCell(10).setCellValue("Annulé");
      }

      // Appliquer le format numérique aux cellules
      row.getCell(3).setCellStyle(montantStyle);
      row.getCell(4).setCellStyle(montantStyle);
      row.getCell(5).setCellStyle(montantStyle);
      row.getCell(6).setCellStyle(montantStyle);
      row.getCell(8).setCellStyle(montantStyle);
      row.getCell(9).setCellStyle(montantStyle);


    }


  }
}
