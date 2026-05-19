package crm.chifco.com.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.OperationAbonnement;
import crm.chifco.com.repository.OperationAbonnementRepository;
import crm.chifco.com.service.ExportOperationAbonnementService;

@Service("ExportOperationAbonnementService")
public class ExportOperationAbonnementServiceImpl implements ExportOperationAbonnementService {
  @Autowired
  private OperationAbonnementRepository operationAbonnementRepository;

  @Override
  public void exportDemande(List<List<Long>> batches, String type, HttpServletResponse response)
      throws IOException {
    List<OperationAbonnement> demandes = new ArrayList<>();
    for (List<Long> batche : batches) {
      List<OperationAbonnement> demande = operationAbonnementRepository.findByIds(batche);
      demandes.addAll(demande);
    }
    Workbook workbook = WorkbookFactory.create(true);
    Sheet sheet = workbook.createSheet("Demandes");
    Row row = sheet.createRow(0);
    row.createCell(0).setCellValue("Réf. Demande");
    row.createCell(1).setCellValue("Tél");
    row.createCell(2).setCellValue("Nouveau Tél");
    row.createCell(3).setCellValue("Date Création");
    row.createCell(4).setCellValue("Type Demande");
    row.createCell(5).setCellValue("Client");
    row.createCell(6).setCellValue("Nouvelle Offre");
    if (type.equals("M")) {
      row.createCell(7).setCellValue("Ancienne Catégorie");
    } else if (type.equals("CH")) {
      row.createCell(7).setCellValue("Ancien débit");
    } else {
      row.createCell(7).setCellValue("Ancienne adresse");
    }
    row.createCell(8).setCellValue("Nouvelle Catégorie");
    row.createCell(9).setCellValue("Nouveau pack");
    row.createCell(10).setCellValue("Nouveau Débit");
    row.createCell(11).setCellValue("Statut TT");
    row.createCell(12).setCellValue("Date Etat");
    row.createCell(13).setCellValue("Type modem");
    if (type.equals("M")) {
      row.createCell(14).setCellValue("Nouveau modem affecté");
    } else {
      row.createCell(14).setCellValue("N° serie modem");
    }
    row.createCell(15).setCellValue("Nouvelle Adresse");
    row.createCell(16).setCellValue("Contact");
    row.createCell(17).setCellValue("CIN");
    row.createCell(18).setCellValue("Région");
    row.createCell(19).setCellValue("CSC");
    row.createCell(20).setCellValue("Motif d’instance");
    row.createCell(21).setCellValue("Nouvelle position");

    int rowNum = 1;
    for (OperationAbonnement demande : demandes) {
      Row newRow = sheet.createRow(rowNum++);
      this.remplirSheetExcel(demande, newRow, type);
    }
    CellStyle cellStyle = workbook.createCellStyle();
    DataFormat dataFormat = workbook.createDataFormat();
    cellStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));
    sheet.getRow(1).createCell(2).setCellStyle(cellStyle);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment; filename=Demandes" + type + ".xlsx");
    org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());

  }

  public void remplirSheetExcel(OperationAbonnement demande, Row newRow, String type) {
    if (demande.getReferenceTT() != null || demande.getReferenceTT() != "") {
      newRow.createCell(0).setCellValue(demande.getReferenceTT());
    } else {
      newRow.createCell(0).setCellValue("");
    }
    newRow.createCell(1).setCellValue(demande.getTelFixe());
    newRow.createCell(2).setCellValue(demande.getTelFixe());
    newRow.createCell(3).setCellValue(demande.getCreatedDate().toString());

    if (type.equals("M")) {
      if (demande.getCategorieProduitInternet().getCategorieProduitInternetNom().equals("GPON")) {
        type = "Migration GPON";
      } else {
        type = "Migration VDSL OG";
      }
      newRow.createCell(4).setCellValue(type);
    } else {
      newRow.createCell(4).setCellValue(type);
    }

    newRow.createCell(5).setCellValue(demande.getFirstName() + " " + demande.getLastName());
    newRow.createCell(6).setCellValue(demande.getPack().getOffre().getTitle());
    newRow.createCell(7).setCellValue(demande.getAncien_value());
    newRow.createCell(8)
        .setCellValue(demande.getPack().getCategoriePack().getCategorieProduitInternetNom());
    newRow.createCell(9).setCellValue(demande.getPack().getTitle());
    newRow.createCell(10).setCellValue(demande.getPack().getDebitPack());
    newRow.createCell(11).setCellValue(demande.getEtatTT());
    if (demande.getModifiedDate() != null) {
      newRow.createCell(12).setCellValue(demande.getModifiedDate().toString());
    } else {
      newRow.createCell(12).setCellValue("");
    }
    if (demande.getModem() != null) {
      newRow.createCell(13).setCellValue(demande.getModem().getModelModem());
      newRow.createCell(14).setCellValue(demande.getModem().getNumSerie());
    } else {
      newRow.createCell(13).setCellValue("");
      newRow.createCell(14).setCellValue("");
    }
    newRow.createCell(15).setCellValue(demande.getAdresse());
    newRow.createCell(16).setCellValue(demande.getTelMobile());
    newRow.createCell(17).setCellValue(demande.getCin());
    newRow.createCell(18).setCellValue(demande.getGouvernorat().getGouvernoratName());
    newRow.createCell(19).setCellValue(demande.getVille().getVilleName());
    newRow.createCell(20).setCellValue(demande.getMotifRefus());
    newRow.createCell(21).setCellValue(demande.getPositionxy());

  }

}
