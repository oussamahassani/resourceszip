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
import crm.chifco.com.model.Commission;

public class CommissionExcelExport extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    // TODO Auto-generated method stub
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();
    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    // define excel file name to be exported
    response.addHeader("Content-Disposition",
        "attachment;fileName=Commission liste" + "_" + dtf.format(now) + ".xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<Commission> list = (List<Commission>) model.get("list");
    // create one sheet
    Sheet sheet = workbook.createSheet("Commission Liste");

    // Créez un style de cellule pour formater le montant avec 3 chiffres après la virgule
    CellStyle montantStyle = workbook.createCellStyle();
    DataFormat montantFormat = workbook.createDataFormat();
    montantStyle.setDataFormat(montantFormat.getFormat("#,##0.000"));

    // create row0 as a header
    Row row0 = sheet.createRow(0);
    row0.createCell(0).setCellValue("Référence");
    row0.createCell(1).setCellValue("Année / Mois");
    row0.createCell(2).setCellValue("Code");
    row0.createCell(3).setCellValue("Nom et prénom");
    row0.createCell(4).setCellValue("Nom commercial");
    row0.createCell(5).setCellValue("Commission des demandes");
    row0.createCell(6).setCellValue("Commission sur Activation");
    row0.createCell(7).setCellValue("Commission des paiements");
    row0.createCell(8).setCellValue("Total HT");
    row0.createCell(9).setCellValue("TVA");
    row0.createCell(10).setCellValue("Montant TVA");
    row0.createCell(11).setCellValue("Total TTC");
    row0.createCell(12).setCellValue("Statut");
    row0.createCell(13).setCellValue("Type");
    // create row1 onwards from List<T>

    int rowNum = 1;
    for (Commission spec : list) {
      Row row = sheet.createRow(rowNum++);

      // Formatter le numéro de mois avec deux chiffres
      String formattedMonth = String.format("%02d", spec.getMois());
      row.createCell(0).setCellValue(spec.getRefCommission());
      row.createCell(1).setCellValue(spec.getAnnee() + " / " + formattedMonth);
      row.createCell(2).setCellValue(spec.getRevendeur().getCodeUser());
      row.createCell(3).setCellValue(
          spec.getRevendeur().getFirstName() + " " + spec.getRevendeur().getLastName());
      row.createCell(4).setCellValue(spec.getRevendeur().getNomCommercial());
      row.createCell(5).setCellValue(spec.getMontantCommissionDemandes());
      row.createCell(6).setCellValue(spec.getMontantCommissionPremiereFacture());
      row.createCell(7).setCellValue(spec.getMontantCommissionPaiements());
      row.createCell(8).setCellValue(spec.getTotalHt());
      row.createCell(9).setCellValue(spec.getTva()+"%");
      row.createCell(10).setCellValue(spec.getMontantTva());
      row.createCell(11).setCellValue(spec.getTotalTtc());
      row.createCell(12)
          .setCellValue(spec.getStatut().equals("PAID") ? "Payé"
              : spec.getStatut().equals("NOT_PAID") ? "Non Payé"
                  : spec.getStatut().equals("CANCELLED") ? "Annulé"  : spec.getStatut().equals("AWAINTING_INVOICING") ? "En Attente de Règlement": "En cours");
	  if(spec.getIsFreelance() != null && spec.getIsFreelance()){
		   row.createCell(13).setCellValue("Freelance");
	 
	  }else{
		   row.createCell(13).setCellValue("Normal");
	  }
      // Appliquer le format numérique aux cellules
      row.getCell(5).setCellStyle(montantStyle);
      row.getCell(6).setCellStyle(montantStyle);
      row.getCell(7).setCellStyle(montantStyle);
      row.getCell(8).setCellStyle(montantStyle);
      row.getCell(10).setCellStyle(montantStyle);
      row.getCell(11).setCellStyle(montantStyle);
      row.getCell(12).setCellStyle(montantStyle);
 

    }
  }
}
