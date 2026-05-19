package crm.chifco.com.service;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import crm.chifco.com.templateclasse.ExportModem;

public class ExportExcelModem extends AbstractXlsxView {
  private Boolean isAdmin;
  private Boolean isDistributeur;
  private Boolean isRevendeur;
  private Boolean isPos;

  public ExportExcelModem(Boolean isAdmin, Boolean isDistributeur, Boolean isRevendeur,
      Boolean isPos) {

    this.isAdmin = isAdmin;
    this.isDistributeur = isDistributeur;
    this.isRevendeur = isRevendeur;
    this.isPos = isPos;

  }

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.addHeader("Content-Disposition", "attachment;fileName=modem.xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<ExportModem> list = (List<ExportModem>) model.get("list");
    Sheet sheet = workbook.createSheet("Liste Modem");
    // create row0 as a header
    Row rowHader = sheet.createRow(0);
    // sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));

    // set les colonnes du deuxiieme lignes
    rowHader.createCell(0).setCellValue("Numéro de serie"); // colonne Numserie
    rowHader.createCell(1).setCellValue("Model"); // colonne Model
    rowHader.createCell(2).setCellValue("Marque"); // colonne Marque
    if (isAdmin) {
      rowHader.createCell(3).setCellValue("Date de creation");
      rowHader.createCell(4).setCellValue("Date de modification");
      rowHader.createCell(5).setCellValue("Email"); // colonne Email
      rowHader.createCell(6).setCellValue("Password"); // colonne Password
      rowHader.createCell(7).setCellValue("Distributeur"); // colonne affect Distributeur
      rowHader.createCell(9).setCellValue("POS"); // colonne affect POS
      rowHader.createCell(8).setCellValue("Revendeur"); // colonne affect Revendeur
      rowHader.createCell(10).setCellValue("Abonnement"); // colonne affect Abonnement
      rowHader.createCell(11).setCellValue("Etat");
    } else if (isDistributeur) {
      rowHader.createCell(3).setCellValue("Revendeur"); // colonne affect Revendeur
      rowHader.createCell(4).setCellValue("Abonnement"); // colonne affect Abonnement
      rowHader.createCell(5).setCellValue("Etat");
    } else if (isRevendeur || isPos) {
      rowHader.createCell(3).setCellValue("Abonnement"); // colonne affect Abonnement
      rowHader.createCell(4).setCellValue("Etat");
    }

    int rowCount = 1;
    for (ExportModem p : list) {

      Row row = sheet.createRow(rowCount++);
      int columnCount = 0;
      // ligne , numero de colonne , data , style
      row.createCell(0).setCellValue(p.getNum_serie()); // inserer modem numero de serie
      row.createCell(1).setCellValue(p.getModel_modem()); // inserer modem model
      row.createCell(2).setCellValue(p.getMarque()); // inserer modem marque
      if (isAdmin) {
        row.createCell(3).setCellValue(p.getDateCreation());
        row.createCell(4).setCellValue(p.getDateModification());
        row.createCell(5).setCellValue(p.getEmail()); // inserer modem email
        row.createCell(6).setCellValue(p.getPassword()); // inserer modem password
        row.createCell(7)
            .setCellValue(p.getAffecte_distributeur() != null ? p.getAffecte_distributeur() : "");
        row.createCell(8)
            .setCellValue(p.getAffecte_revendeur() != null ? p.getAffecte_revendeur() : "");
        row.createCell(9).setCellValue(p.getAffecte_Pos() != null ? p.getAffecte_Pos() : "");
        row.createCell(10).setCellValue(p.getAffecte_client() != null ? p.getAffecte_client() : "");
        row.createCell(11).setCellValue(p.getStatus() ? "Non actif" : "Actif");
      } else if (isDistributeur) {
        row.createCell(3)
            .setCellValue(p.getAffecte_revendeur() != null ? p.getAffecte_revendeur() : "");
        row.createCell(4).setCellValue(p.getAffecte_client() != null ? p.getAffecte_client() : "");
        row.createCell(5).setCellValue(p.getStatus() ? "Non actif" : "Actif");
      } else if (isRevendeur || isPos) {
        row.createCell(3).setCellValue(p.getAffecte_client() != null ? p.getAffecte_client() : "");
        row.createCell(4).setCellValue(p.getStatus() ? "Non actif" : "Actif");
      }
    }

    // Ajuster automatiquement la largeur des colonnes en fonction du contenu
    for (Cell cell : sheet.getRow(0)) {
      sheet.autoSizeColumn(cell.getColumnIndex());
    }

  }

}
