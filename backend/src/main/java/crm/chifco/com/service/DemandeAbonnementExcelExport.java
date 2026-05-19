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
import crm.chifco.com.DTOclass.DemandeAbbonementaAndAffectedToUserObjectDataDTO;
import crm.chifco.com.model.User;

public class DemandeAbonnementExcelExport extends AbstractXlsxView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();
    // define excel file name to be exported
    response.addHeader("Content-Disposition",
        "attachment;fileName=D.A.en.masse" + "_" + dtf.format(now) + ".xlsx");
    // read data provided by controller
    @SuppressWarnings("unchecked")
    List<DemandeAbbonementaAndAffectedToUserObjectDataDTO> list =
        (List<DemandeAbbonementaAndAffectedToUserObjectDataDTO>) model.get("list");
    // create one sheet

    // user connected
    User userConnected = (User) model.get("user");
    List<String> StringsRole =
        userConnected.getRole().getStringsRole(userConnected.getRole().getPrivileges());
    Boolean EDIT_HEAD_OF_Family_DEMANDE = StringsRole.contains("EDIT_HEAD_OF_Family_DEMANDE");
    Boolean ASSIGN_SUBSCRIPTION_AGENT = StringsRole.contains("ASSIGN_SUBSCRIPTION_AGENT");
    Sheet sheet = workbook.createSheet("Demande Abonnement");
    // create row0 as a header
    Row row0 = sheet.createRow(0);

    if (StringsRole.contains("UPDATE_SUBSCRIPTION_REQUEST_STATUS")) {
      row0.createCell(0).setCellValue("Réf. Demande");
      row0.createCell(1).setCellValue("Téléphone");
      row0.createCell(2).setCellValue("Nouveau Téléphone");
      row0.createCell(3).setCellValue("Contrat");
      row0.createCell(4).setCellValue("Date Dépôt");
      row0.createCell(5).setCellValue("Type Demande");
      row0.createCell(6).setCellValue("Client");
      row0.createCell(7).setCellValue("pack titre");
      row0.createCell(8).setCellValue("Etat");
      row0.createCell(9).setCellValue("Date Etat");
      row0.createCell(10).setCellValue("Type modem");
      row0.createCell(11).setCellValue("N° serie modem");
      row0.createCell(12).setCellValue("Adresse");
      row0.createCell(13).setCellValue("Contact");
      row0.createCell(14).setCellValue("CIN");
      row0.createCell(15).setCellValue("Région");
      row0.createCell(16).setCellValue("CSC");
      row0.createCell(17).setCellValue("Motif d’instance");
      row0.createCell(18).setCellValue("Date d’entrée à la phase Étude");
      row0.createCell(19).setCellValue("Date de validation de l’Étude par TT");
      row0.createCell(20).setCellValue("Date de Confirmation Client");
      row0.createCell(21).setCellValue("Date MES TT");
      row0.createCell(22).setCellValue("Email");
      row0.createCell(23).setCellValue("Adresse");
      row0.createCell(24).setCellValue("Code Postale");
      row0.createCell(25).setCellValue("Proprietaire");
      row0.createCell(26).setCellValue("Téléphone Mobile 2");
      row0.createCell(27).setCellValue("Position XY");
      row0.createCell(28).setCellValue("Date de naissance");
      row0.createCell(29).setCellValue("Profession");
      row0.createCell(30).setCellValue("Status demande");
      row0.createCell(31).setCellValue("Réference Nety");
      row0.createCell(32).setCellValue("Type de paiement");
      row0.createCell(33).setCellValue("Date de création");
      row0.createCell(34).setCellValue("Date de la Dernière Modification");
      row0.createCell(35).setCellValue("Creé par");
      row0.createCell(36).setCellValue("Code Créateur");
      row0.createCell(37).setCellValue("Assigné à");
      row0.createCell(38).setCellValue("Code Assigné");

      row0.createCell(41).setCellValue("Classification Demande");
      row0.createCell(42).setCellValue("Distribiteur");
      row0.createCell(43).setCellValue("Code Distribiteur");

      if (EDIT_HEAD_OF_Family_DEMANDE) {
        row0.createCell(39).setCellValue("Chef de famille");
        row0.createCell(40).setCellValue("A une carte bancaire");
      }
      row0.createCell(44).setCellValue("Date de mise en service");
      if (ASSIGN_SUBSCRIPTION_AGENT) {
        row0.createCell(45).setCellValue("Traité par");
      }
      row0.createCell(46).setCellValue("Source");
      // create row1 onwards from List<T>

      int rowNum = 1;
      for (DemandeAbbonementaAndAffectedToUserObjectDataDTO spec : list) {


        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(spec.toString());

        row.createCell(0).setCellValue(spec.getDemandeAbonnement().getReferenceTT());
        if (spec.getDemandeAbonnement().getTelFixe() != null) {
          row.createCell(1).setCellValue(spec.getDemandeAbonnement().getTelFixe());
        } else
          row.createCell(1).setCellValue("");
        if (spec.getDemandeAbonnement().getTelFixe() != null) {
          row.createCell(2).setCellValue(spec.getDemandeAbonnement().getTelFixe());
        } else {
          row.createCell(2).setCellValue("");
        }

        if (spec.getDemandeAbonnement().getCin() != null)
          row.createCell(3).setCellValue(" ");
        if (spec.getDemandeAbonnement().getEmail() != null)
          row.createCell(4).setCellValue(" ");
        row.createCell(5).setCellValue(" ");
        row.createCell(6).setCellValue(spec.getDemandeAbonnement().getFirstName() + " "
            + spec.getDemandeAbonnement().getLastName());
        row.createCell(7).setCellValue(spec.getDemandeAbonnement().getPack().getTitle());

        row.createCell(8).setCellValue(spec.getDemandeAbonnement().getEtatTT());

        row.createCell(9).setCellValue("");
        if (spec.getDemandeAbonnement().getCategorieProduitInternet() != null) {
          row.createCell(10).setCellValue(spec.getDemandeAbonnement().getCategorieProduitInternet()
              .getCategorieProduitInternetCode());
        } else {
          row.createCell(10).setCellValue("");
        }
        row.createCell(11).setCellValue("");
        row.createCell(12).setCellValue(spec.getDemandeAbonnement().getAdresse());

        if (spec.getDemandeAbonnement().getTelMobile() != null) {
          row.createCell(13).setCellValue(spec.getDemandeAbonnement().getTelMobile());
        } else {
          row.createCell(13).setCellValue("");
        }


        row.createCell(14).setCellValue(spec.getDemandeAbonnement().getCin());
        row.createCell(15)
            .setCellValue(spec.getDemandeAbonnement().getGouvernorat().getGouvernoratName());
        row.createCell(16).setCellValue(spec.getDemandeAbonnement().getVille().getVilleName());
        row.createCell(17).setCellValue(spec.getDemandeAbonnement().getMotifRefus());
        row.createCell(18).setCellValue("");
        row.createCell(19).setCellValue("");
        row.createCell(20).setCellValue("");
        row.createCell(21).setCellValue("");
        row.createCell(22).setCellValue(spec.getDemandeAbonnement().getEmail());
        row.createCell(23).setCellValue(spec.getDemandeAbonnement().getAdresse());
        row.createCell(24).setCellValue(spec.getDemandeAbonnement().getCodePostale().getCode());

        if (spec.getDemandeAbonnement().getProprietaire() == true) {
          row.createCell(25).setCellValue("Oui");
        } else {
          row.createCell(25).setCellValue("Non");
        }
        if (spec.getDemandeAbonnement().getTelMobile2() != null) {
          row.createCell(26).setCellValue(spec.getDemandeAbonnement().getTelMobile2());
        }
        row.createCell(27).setCellValue(spec.getDemandeAbonnement().getPositionxy());

        if (spec.getDemandeAbonnement().getDateNaissance() != null) {
          DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
          row.createCell(28)
              .setCellValue(dateFormat.format(spec.getDemandeAbonnement().getDateNaissance()));
        }

        if (spec.getDemandeAbonnement().getProfession() != null) {
          row.createCell(29).setCellValue(spec.getDemandeAbonnement().getProfession().getName());
        }
        row.createCell(30).setCellValue(spec.getDemandeAbonnement().getStatut().getDesignation());
        row.createCell(31).setCellValue(spec.getDemandeAbonnement().getReferenceChifco());
        row.createCell(32)
            .setCellValue(spec.getDemandeAbonnement().getTypePaiement().getNomTypePaiement());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        row.createCell(33)
            .setCellValue(dateFormat.format(spec.getDemandeAbonnement().getCreatedDate()));
        row.createCell(34)
            .setCellValue(dateFormat.format(spec.getDemandeAbonnement().getModifiedDate()));
        if (spec.getDemandeAbonnement().getUser() != null) {
          row.createCell(35).setCellValue(spec.getDemandeAbonnement().getUser().getFirstName() + " "
              + spec.getDemandeAbonnement().getUser().getLastName());
          row.createCell(36).setCellValue(spec.getDemandeAbonnement().getUser().getCodeUser());
        }
        if (spec.getDemandeAbonnement().getAssignedTo() != null) {
          row.createCell(37).setCellValue(spec.getDemandeAbonnement().getAssignedTo().getFirstName()
              + " " + spec.getDemandeAbonnement().getAssignedTo().getLastName());
          row.createCell(38)
              .setCellValue(spec.getDemandeAbonnement().getAssignedTo().getCodeUser());
        }
        if (EDIT_HEAD_OF_Family_DEMANDE) {
          if (spec.getDemandeAbonnement().getHouseHolder() != null) {
            row.createCell(39)
                .setCellValue(spec.getDemandeAbonnement().getHouseHolder() ? "Oui" : "Non");
          }
          if (spec.getDemandeAbonnement().getHasBankCard() != null) {
            row.createCell(40)
                .setCellValue(spec.getDemandeAbonnement().getHasBankCard() ? "Oui" : "Non");
          }
        }

        if (spec.getDemandeAbonnement().getDecisionDemande() != null) {
          row.createCell(41)
              .setCellValue(spec.getDemandeAbonnement().getDecisionDemande().getValue());

        }
        if (spec.getAffectedTo() != null) {
          row.createCell(42).setCellValue(spec.getCreatedByNom() + " " + spec.getCreatedByPrenom());

        }
        if (spec.getAffectedTo() != null) {
          row.createCell(43).setCellValue(spec.getCreatedByCode());

        }
        if (spec.getDemandeAbonnement().getDateDeMiseEnService() != null) {
          row.createCell(44).setCellValue(
              dateFormat.format(spec.getDemandeAbonnement().getDateDeMiseEnService()));

        }
        if (ASSIGN_SUBSCRIPTION_AGENT) {
          row.createCell(45).setCellValue(
              spec.getDemandeAbonnement().getTreatedBy() != null
                  ? spec.getDemandeAbonnement().getTreatedBy().getFirstName() + " "
                      + spec.getDemandeAbonnement().getTreatedBy().getLastName()
                  : "");
        }
        if (spec.getDemandeAbonnement().getOrigin() != null) {
          row.createCell(46).setCellValue(spec.getDemandeAbonnement().getOrigin());

        }

      }
    } else if (StringsRole.contains("EXPORT_SUBSCRIPTION_REQUEST_RETAIL")) {

      row0.createCell(0).setCellValue("Client");
      row0.createCell(1).setCellValue("pack titre");
      row0.createCell(2).setCellValue("Etat");
      row0.createCell(3).setCellValue("Date Etat");
      row0.createCell(4).setCellValue("Type modem");
      row0.createCell(5).setCellValue("Région");
      row0.createCell(6).setCellValue("CSC");
      row0.createCell(7).setCellValue("Motif d’instance");
      row0.createCell(8).setCellValue("Status demande");
      row0.createCell(9).setCellValue("Réference Nety");
      row0.createCell(10).setCellValue("Type de paiement");
      row0.createCell(11).setCellValue("Date de création");
      row0.createCell(12).setCellValue("Date de la Dernière Modification");
      row0.createCell(13).setCellValue("Creé par");
      row0.createCell(14).setCellValue("Code Créateur");
      row0.createCell(15).setCellValue("Assigné à");
      row0.createCell(16).setCellValue("Code Assigné");

      if (EDIT_HEAD_OF_Family_DEMANDE) {
        row0.createCell(17).setCellValue("Chef de famille");
        row0.createCell(18).setCellValue("A une carte bancaire");
      }
      row0.createCell(19).setCellValue("Date mise en service");
      row0.createCell(20).setCellValue("Tel fixe");


      int rowNum = 1;
      for (DemandeAbbonementaAndAffectedToUserObjectDataDTO spec : list) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(spec.getDemandeAbonnement().getFirstName() + " "
            + spec.getDemandeAbonnement().getLastName());
        row.createCell(1).setCellValue(spec.getDemandeAbonnement().getPack().getTitle());
        row.createCell(2).setCellValue(spec.getDemandeAbonnement().getEtatTT());
        row.createCell(3).setCellValue("");
        if (spec.getDemandeAbonnement().getCategorieProduitInternet() != null) {
          row.createCell(4).setCellValue(spec.getDemandeAbonnement().getCategorieProduitInternet()
              .getCategorieProduitInternetCode());
        } else {
          row.createCell(4).setCellValue("");
        }
        row.createCell(5)
            .setCellValue(spec.getDemandeAbonnement().getGouvernorat().getGouvernoratName());
        row.createCell(6).setCellValue(spec.getDemandeAbonnement().getVille().getVilleName());
        row.createCell(7).setCellValue(spec.getDemandeAbonnement().getMotifRefus());
        row.createCell(8).setCellValue(spec.getDemandeAbonnement().getStatut().getDesignation());
        row.createCell(9).setCellValue(spec.getDemandeAbonnement().getReferenceChifco());
        row.createCell(10)
            .setCellValue(spec.getDemandeAbonnement().getTypePaiement().getNomTypePaiement());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        row.createCell(11)
            .setCellValue(dateFormat.format(spec.getDemandeAbonnement().getCreatedDate()));
        row.createCell(12)
            .setCellValue(dateFormat.format(spec.getDemandeAbonnement().getModifiedDate()));
        if (spec.getDemandeAbonnement().getUser() != null) {
          row.createCell(13).setCellValue(spec.getDemandeAbonnement().getUser().getFirstName() + " "
              + spec.getDemandeAbonnement().getUser().getLastName());
          row.createCell(14).setCellValue(spec.getDemandeAbonnement().getUser().getCodeUser());
        }
        if (spec.getDemandeAbonnement().getAssignedTo() != null) {
          row.createCell(15).setCellValue(spec.getDemandeAbonnement().getAssignedTo().getFirstName()
              + " " + spec.getDemandeAbonnement().getAssignedTo().getLastName());
          row.createCell(16)
              .setCellValue(spec.getDemandeAbonnement().getAssignedTo().getCodeUser());
        }

        if (EDIT_HEAD_OF_Family_DEMANDE) {
          row.createCell(17)
              .setCellValue(spec.getDemandeAbonnement().getHouseHolder() ? "Oui" : "Non");
          row.createCell(18)
              .setCellValue(spec.getDemandeAbonnement().getHasBankCard() ? "Oui" : "Non");
        }

        if (spec.getDemandeAbonnement().getDateDeMiseEnService() != null) {
          row.createCell(19).setCellValue(
              dateFormat.format(spec.getDemandeAbonnement().getDateDeMiseEnService()));

        }
        if (spec.getDemandeAbonnement().getTelFixe() != null) {
          row.createCell(20).setCellValue(spec.getDemandeAbonnement().getTelFixe());

        }
      }

    }
  }
}
