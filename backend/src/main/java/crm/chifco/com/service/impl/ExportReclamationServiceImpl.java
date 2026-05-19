package crm.chifco.com.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.repository.ReclamationRepository;
import crm.chifco.com.service.ExportReclamationService;

@Service
public class ExportReclamationServiceImpl implements ExportReclamationService {

  @Autowired
  private ReclamationRepository reclamationRepository;

  @Override
  public void exportReclamations(List<Long> reclamationIds, HttpServletResponse response)
      throws IOException {

    List<Reclamation> reclamations = reclamationRepository.findAllById(reclamationIds);
    String Category = null;
    String serviceName = null;
    if (!reclamations.isEmpty()) {
      Category = reclamations.get(0).getCategory();
      serviceName = reclamations.get(0).getServiceType().getCategorytype();
    }
    Workbook workbook = WorkbookFactory.create(true);

    Sheet sheet = workbook.createSheet("Réclamations TT");

    createHeader(sheet, Category, serviceName);
    fillData(sheet, reclamations, Category, serviceName);

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    if (Category != null && serviceName != null && Category.equals("Client")
        && serviceName.equals("Technique")) {
      response.setHeader("Content-Disposition", "attachment; filename=Reclamations_TT_Export.xlsx");
    } else {
      response.setHeader("Content-Disposition", "attachment; filename=Reclamations_Export.xlsx");
    }

    workbook.write(response.getOutputStream());
    workbook.close();
  }

  private void createHeader(Sheet sheet, String Category, String serviceName) {
    Row header = sheet.createRow(0);
    if (Category != null && serviceName != null && Category.equals("Client")
        && serviceName.equals("Technique")) {
      header.createCell(0).setCellValue("Ref réclamation");
      header.createCell(1).setCellValue("N° téléphone");
      header.createCell(2).setCellValue("Référence TT");
      header.createCell(3).setCellValue("État TT");
      header.createCell(4).setCellValue("Motif");
      header.createCell(5).setCellValue("Identifiant");
      header.createCell(6).setCellValue("Client");
      header.createCell(7).setCellValue("Référence Nety");
      header.createCell(8).setCellValue("Catégorie");
      header.createCell(9).setCellValue("Service Type");
      header.createCell(10).setCellValue("Source");
      header.createCell(11).setCellValue("Créé par");
      header.createCell(12).setCellValue("Traité par");
      header.createCell(13).setCellValue("Date création CRM");

      header.createCell(14).setCellValue("Date réclamation TT");
      header.createCell(15).setCellValue("Date état");
      header.createCell(16).setCellValue("Date vérification FSI");
      header.createCell(17).setCellValue("Gouvernorat");
      header.createCell(18).setCellValue("Central");
      header.createCell(19).setCellValue("Tél mobile");
    } else {
      header.createCell(0).setCellValue("Ref réclamation");
      header.createCell(1).setCellValue("N° téléphone");
      header.createCell(2).setCellValue("Motif");
      header.createCell(3).setCellValue("Identifiant");
      header.createCell(4).setCellValue("Client");
      header.createCell(5).setCellValue("Référence Nety");

      header.createCell(6).setCellValue("Catégorie");
      header.createCell(7).setCellValue("Service Type");
      header.createCell(8).setCellValue("Source");
      header.createCell(9).setCellValue("Créé par");
      header.createCell(10).setCellValue("Traité par");
      header.createCell(11).setCellValue("Date création CRM");
      header.createCell(12).setCellValue("Tél mobile");

    }
  }

  private void fillData(Sheet sheet, List<Reclamation> reclamations, String Category,
      String serviceName) {
    int rowNum = 1;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    for (Reclamation r : reclamations) {
      Row row = sheet.createRow(rowNum++);
      if (Category != null && serviceName != null && Category.equals("Client")
          && serviceName.equals("Technique")) {
        row.createCell(0)
            .setCellValue(r.getRef_reclamation() != null ? r.getRef_reclamation() : "");
        row.createCell(1)
            .setCellValue(r.getClient() != null && r.getClient().getTelFixe() != null
                ? r.getClient().getTelFixe().toString()
                : "");

        row.createCell(2).setCellValue(r.getReferencett() != null ? r.getReferencett() : "");
        row.createCell(3).setCellValue(r.getEtattt() != null ? r.getEtattt() : "");
        row.createCell(4).setCellValue(r.getMotif() != null ? r.getMotif().getNomMotif() : "");

        row.createCell(5).setCellValue(r.getClient() != null ? r.getClient().getCin() : "");
        row.createCell(6)
            .setCellValue(r.getClient() != null
                ? r.getClient().getFirstName() + " " + r.getClient().getLastName()
                : "");

        row.createCell(7)
            .setCellValue(r.getClient() != null ? r.getClient().getReferenceClient() : "");
        row.createCell(8).setCellValue(r.getCategory() != null ? r.getCategory() : "");
        row.createCell(9)
            .setCellValue(r.getServiceType() != null ? r.getServiceType().getCategorytype() : "");
        row.createCell(10).setCellValue(r.getSource() != null ? r.getSource() : "");

        row.createCell(11)
            .setCellValue(r.getCreatedby() != null
                ? r.getCreatedby().getFirstName() + " " + r.getCreatedby().getLastName()
                : "");
        row.createCell(12)
            .setCellValue(r.getTreatedBy() != null
                ? r.getTreatedBy().getFirstName() + " " + r.getTreatedBy().getLastName()
                : "");
        row.createCell(13)
            .setCellValue(r.getCreatedDate() != null ? sdf.format(r.getCreatedDate()) : "");

        row.createCell(14).setCellValue(
            r.getDate_reclamationtt() != null ? sdf.format(r.getDate_reclamationtt()) : "");
        row.createCell(15)
            .setCellValue(r.getDate_etattt() != null ? sdf.format(r.getDate_etattt()) : "");
        row.createCell(16).setCellValue(
            r.getDate_verificationfsi() != null ? sdf.format(r.getDate_verificationfsi()) : "");
        row.createCell(17).setCellValue(r.getGouvernorat() != null ? r.getGouvernorat() : "");
        row.createCell(18).setCellValue(r.getCentral() != null ? r.getCentral() : "");
        row.createCell(19).setCellValue(
            r.getClient().getTelMobile() != null ? r.getClient().getTelMobile() : null);

      } else {
        row.createCell(0)
            .setCellValue(r.getRef_reclamation() != null ? r.getRef_reclamation() : "");
        row.createCell(1)
            .setCellValue(r.getClient() != null && r.getClient().getTelFixe() != null
                ? r.getClient().getTelFixe().toString()
                : "");



        row.createCell(2).setCellValue(r.getMotif() != null ? r.getMotif().getNomMotif() : "");

        row.createCell(3).setCellValue(r.getClient() != null ? r.getClient().getCin() : "");
        row.createCell(4)
            .setCellValue(r.getClient() != null
                ? r.getClient().getFirstName() + " " + r.getClient().getLastName()
                : "");

        row.createCell(5)
            .setCellValue(r.getClient() != null ? r.getClient().getReferenceClient() : "");
        row.createCell(6).setCellValue(r.getCategory() != null ? r.getCategory() : "");
        row.createCell(7)
            .setCellValue(r.getServiceType() != null ? r.getServiceType().getCategorytype() : "");
        row.createCell(8).setCellValue(r.getSource() != null ? r.getSource() : "");

        row.createCell(9)
            .setCellValue(r.getCreatedby() != null
                ? r.getCreatedby().getFirstName() + " " + r.getCreatedby().getLastName()
                : "");
        row.createCell(10)
            .setCellValue(r.getTreatedBy() != null
                ? r.getTreatedBy().getFirstName() + " " + r.getTreatedBy().getLastName()
                : "");
        row.createCell(11)
            .setCellValue(r.getCreatedDate() != null ? sdf.format(r.getCreatedDate()) : "");
        row.createCell(12)
            .setCellValue(r.getClient() != null && r.getClient().getTelMobile() != null
                ? r.getClient().getTelMobile().toString()
                : "");
      }
    }
  }

}
