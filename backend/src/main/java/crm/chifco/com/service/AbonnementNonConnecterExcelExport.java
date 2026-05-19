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
import crm.chifco.com.model.Abonnement;

public class AbonnementNonConnecterExcelExport extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();
    // define excel file name to be exported
    response.addHeader("Content-Disposition",
        "attachment;fileName=AbbonmentNonConnecter" + "_" + dtf.format(now) + ".xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<Abonnement> list = (List<Abonnement>) model.get("list");

    // create one sheet
    Sheet sheet = workbook.createSheet("Abonnement Liste");
    // create row0 as a header
    Row row0 = sheet.createRow(0);


    row0.createCell(0).setCellValue("Réf. Demande");
    row0.createCell(1).setCellValue("Téléphone");
    row0.createCell(2).setCellValue("Nouveau Tél");
    row0.createCell(3).setCellValue("Contrat");
    row0.createCell(4).setCellValue("Date Dépôt");
    row0.createCell(5).setCellValue("Type Demande");
    row0.createCell(6).setCellValue("Client");
    row0.createCell(7).setCellValue("Pack titre");
    row0.createCell(8).setCellValue("Date Prochain facture");
    row0.createCell(9).setCellValue("Date Etat");
    row0.createCell(10).setCellValue("Type modem");
    row0.createCell(11).setCellValue("N° serie modem");
    row0.createCell(12).setCellValue("Adresse");
    row0.createCell(13).setCellValue("Contact");
    row0.createCell(14).setCellValue("CIN");
    row0.createCell(15).setCellValue("Région");
    row0.createCell(16).setCellValue("CSC");
    row0.createCell(17).setCellValue("Email");
    row0.createCell(19).setCellValue("Code Postale");
    row0.createCell(20).setCellValue("Téléphone mobile 2");
    row0.createCell(21).setCellValue("Profession");
    row0.createCell(22).setCellValue("Status demande");
    row0.createCell(23).setCellValue("Type de paiement");
    row0.createCell(24).setCellValue("Date de création");
    row0.createCell(25).setCellValue("Date de la dernière modification");
    row0.createCell(26).setCellValue("Creé par");
    row0.createCell(27).setCellValue("Code Créateur");
    row0.createCell(28).setCellValue("Assigné à");
    row0.createCell(29).setCellValue("Code Assigné");
    row0.createCell(32).setCellValue("Date affectation modem");
    row0.createCell(33).setCellValue("Date 1er connexion");

    // create row1 onwards from List<T>
    int rowNum = 1;

    for (Abonnement spec : list) {
      Row row = sheet.createRow(rowNum++);
      row.createCell(0).setCellValue(spec.getReferenceClient());
      if (spec.getTelFixe() != null) {
        row.createCell(1).setCellValue(spec.getTelFixe());
      } else
        row.createCell(1).setCellValue("");
      if (spec.getTelFixe() != null) {
        row.createCell(2).setCellValue(spec.getTelFixe());
      } else {
        row.createCell(2).setCellValue("");
      }

      if (spec.getCin() != null)
        row.createCell(3).setCellValue(" ");
      if (spec.getEmail() != null)
        row.createCell(4).setCellValue(" ");
      row.createCell(5).setCellValue(" ");
      row.createCell(6).setCellValue(spec.getFirstName() + " " + spec.getLastName());
      row.createCell(7).setCellValue(spec.getPack().getTitle());

      DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      if (spec.getDateProchainFacturation() != null) {
        row.createCell(8).setCellValue(dateFormat.format(spec.getDateProchainFacturation()));
      }

      row.createCell(9).setCellValue("");
      if (spec.getPack() != null) {
        row.createCell(10)
            .setCellValue(spec.getPack().getCategoriePack().getCategorieProduitInternetNom());
      } else {
        row.createCell(10).setCellValue("");
      }
      if (spec.getModem() != null) {
        row.createCell(11).setCellValue(spec.getModem().getNumSerie());
      } else {
        row.createCell(11).setCellValue("");
      }
      row.createCell(12).setCellValue(spec.getAdresse());

      row.createCell(13).setCellValue(spec.getTelMobile());

      row.createCell(14).setCellValue(spec.getCin());
      row.createCell(15).setCellValue(spec.getGouvernorat().getGouvernoratName());
      row.createCell(16).setCellValue(spec.getVille().getVilleName());
      row.createCell(17).setCellValue(spec.getEmail());
      row.createCell(19).setCellValue(spec.getCodePostale().getCode());

      if (spec.getTelMobile2() != null) {
        row.createCell(20).setCellValue(spec.getTelMobile2());
      }

      if (spec.getProfession() != null) {
        row.createCell(21).setCellValue(spec.getProfession().getName());
      }
      row.createCell(22).setCellValue(spec.getStatut().getDesignation());
      row.createCell(23).setCellValue(spec.getTypePaiement().getNomTypePaiement());

      if (spec.getCreatedDate() != null) {
        row.createCell(24).setCellValue(dateFormat.format(spec.getCreatedDate()));
      }
      if (spec.getModifiedDate() != null) {
        row.createCell(25).setCellValue(dateFormat.format(spec.getModifiedDate()));
      }
      if (spec.getUser() != null) {
        row.createCell(26)
            .setCellValue(spec.getUser().getFirstName() + " " + spec.getUser().getLastName());
        row.createCell(27).setCellValue(spec.getUser().getCodeUser());
      }
      if (spec.getAssignedTo() != null) {
        row.createCell(28).setCellValue(
            spec.getAssignedTo().getFirstName() + " " + spec.getAssignedTo().getLastName());
        row.createCell(29).setCellValue(spec.getAssignedTo().getCodeUser());
      }

      if (spec.getModemAffectedDate() != null) {
        row.createCell(32).setCellValue(spec.getModemAffectedDate().toString());
      }
      if (spec.getFirstConnectionDate() != null) {
        row.createCell(33).setCellValue(spec.getFirstConnectionDate().toString());
      }
    }
    // Ajuster la largeur des colonnes automatiquement
    for (int i = 0; i < 30; i++) {
      sheet.autoSizeColumn(i);
    }

  }


}
