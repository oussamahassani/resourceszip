package crm.chifco.com.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import crm.chifco.com.model.FicheStock;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.UserRepository;

@Service
@Transactional
public class ImprimerFichePDF {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  private FicheStockService ficheservice;
  @Value("${pathfichestock}")
  private String pathfichestock;

  @Autowired
  private UserRepository userRepository;

  // imprimer une fiche de stock PDF
  public String createPdf(String ref) throws Exception {

    // Vérifier l'existence du répertoire
    Path directoryPath = Paths.get(pathfichestock);
    if (!Files.exists(directoryPath)) {
      try {
        // Créer le répertoire s'il n'existe pas
        Files.createDirectories(directoryPath);

        LOGGER.info("Répertoire de fiche de stock créé : " + directoryPath);
        System.out.println("Répertoire créé : " + directoryPath);
      } catch (IOException e) {
        LOGGER.error("Erreur lors de la création du répertoire : " + e.getMessage());
      }
    }

    // recuperer les informations necessaires à imprimer
    FicheStock fiche = ficheservice.getFiche(ref); // fiche à imprimer recuperé par sa reference
    List<Modem> modems = ficheservice.listmodemfiche(ref); // liste des modems affectés dans la
                                                           // fiche
    User u = ficheservice.usersfiche(fiche.getAffecteID()); // user : à qui on a affecté les modems
    User userBy = userRepository.findByUserId(fiche.getAffectedBYuser());
    String filePdf = pathfichestock + "/" + fiche.getRef_fiche() + ".pdf";// preparé l'emplacement
                                                                          // de telechargement

    Border b1 = new DashedBorder(Color.LIGHT_GRAY, 0); // styler les bordure de la tableau des
                                                       // modems
    Border b2 = new DashedBorder(Color.DARK_GRAY, 0);
    // ************************************************************

    try {
      PdfWriter writer = new PdfWriter(filePdf); // objet *PdfWriter pour remplir le document à
                                                 // telecharger
      PdfDocument pdfDoc = new PdfDocument(writer);
      Document document = new Document(pdfDoc);// preparer un new objet document
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

      // ****** info fiche
      // font style de document
      PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
      DeviceCmyk color1 = new DeviceCmyk(100, 100, 0, 0); // coluer de font
      document.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));

      PdfFont font2 = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);

      Paragraph title = new Paragraph("Fiche d'affectation modem");
      title.setFontSize(20f);
      title.setTextAlignment(TextAlignment.CENTER);
      title.setFontColor(color1);
      title.setMarginBottom(20);
      title.setBold();

      // Créer une table à deux colonnes
      float[] columnWidth2 = {300f, 300f}; // preparer les
      // dimensions de
      // colonnes
      Table table2 = new Table(columnWidth2);
      // Ajouter les informations de la fiche à la première colonne
      Paragraph tabP1 = new Paragraph("Information Fiche");
      tabP1.setFont(font); // paragraphe 1 avec font gras
      // p1.setBold();
      tabP1.setFontColor(Color.convertCmykToRgb(color1)); // coleur de la paragraphe
      tabP1.setUnderline(); // paragraphe soulignée
      tabP1.setMarginBottom(5);
      Paragraph tabP2 =
          new Paragraph("Reference Fiche : " + fiche.getRef_fiche()).setMarginBottom(2);
      Paragraph tabP3 =
          new Paragraph("Date Creation : " + dateFormat.format(fiche.getCreatedDate()));
      Paragraph tabUserBy = new Paragraph("Affecté Par : " + userBy.getFirstName() + " "
          + userBy.getLastName() + " / " + userBy.getCodeUser());

      Cell cell1Tab2 = new Cell();

      cell1Tab2.add(tabP1);
      cell1Tab2.add(tabP2);
      cell1Tab2.add(tabP3);
      cell1Tab2.add(tabUserBy);
      cell1Tab2.setBorder(Border.NO_BORDER);
      table2.addCell(cell1Tab2);

      // Ajouter les informations du destinataire à la deuxième colonne
      Paragraph tabP4 = new Paragraph("Information Destinataire");
      tabP4.setFont(font); // paragraphe 1 avec font gras
      // p1.setBold();
      tabP4.setFontColor(Color.convertCmykToRgb(color1)); // coleur de la paragraphe
      tabP4.setUnderline(); // paragraphe soulignée
      tabP1.setMarginBottom(5);
      Paragraph tabP5 =
          new Paragraph("Nom : " + u.getFirstName() + ' ' + u.getLastName()).setMarginBottom(2);

      Paragraph tabP6 = null;
      if (u.getNomCommercial() != null) {
        tabP6 = new Paragraph("Nom commerical : " + u.getNomCommercial()).setMarginBottom(2);
      }
      Paragraph tabP7 = new Paragraph("Email : " + u.getEmail()).setMarginBottom(2);// email de user
      Paragraph tabP8 = new Paragraph("Code : " + u.getCodeUser()).setMarginBottom(2); // le role de
                                                                                       // user

      Cell cell2Tab2 = new Cell();

      cell2Tab2.add(tabP4);
      cell2Tab2.add(tabP5);
      if (u.getNomCommercial() != null) {
        cell2Tab2.add(tabP6);
      }
      cell2Tab2.add(tabP7);
      cell2Tab2.add(tabP8);
      cell2Tab2.setBorder(Border.NO_BORDER);
      table2.addCell(cell2Tab2);

      Paragraph listmodem = new Paragraph("Liste de modems affectés");// creer une 3eme secton
      listmodem.setMarginTop(20);

      Paragraph sign1 = new Paragraph("Signature Source");
      Paragraph sign2 = new Paragraph("Signature Destinataire");

      // inserer le tableau des modems dans le document
      float[] columnWidth = {200f, 200f, 200f}; // preparer les
                                                // dimensions de
                                                // colonnes
      Table table = new Table(columnWidth);

      Cell cell = new Cell();

      table.addCell(new Cell().add("Numero de serie").setBorder(b1)
          .setBackgroundColor(Color.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

      table.addCell(new Cell().add("Modele").setBorder(b1).setBackgroundColor(Color.LIGHT_GRAY)
          .setTextAlignment(TextAlignment.CENTER));

      table.addCell(new Cell().add("Marque").setBorder(b1).setBackgroundColor(Color.LIGHT_GRAY)
          .setTextAlignment(TextAlignment.CENTER));

      // commencer à remplir les colonnes par les informations de chaque modems
      for (Modem p : modems) {
        table.addCell(
            new Cell().add(String.valueOf(p.getNumSerie())).setTextAlignment(TextAlignment.CENTER)); // numero
                                                                                                     // de
                                                                                                     // serie
                                                                                                     // modem
        table.addCell(new Cell().add(p.getModelModem()).setTextAlignment(TextAlignment.CENTER)); // model
                                                                                                 // modem
        table.addCell(new Cell().add(p.getMarque()).setTextAlignment(TextAlignment.CENTER)); // marque
                                                                                             // modem
      }
      table.setMarginBottom(50);
      // *****************************style

      // inserer une image dans le document
      // inserer une image dans le document
      String imFile = "classpath:static/img/netylogo.png";
      Image image = null;

      try {
        ImageData data = ImageDataFactory.create(imFile);
        image = new Image(data);
        image.setWidth(20);
        image.scale(5, 5);
      } catch (Exception e) {
        LOGGER.error("logo nety fiche d'affectation modem n'existe pas");
      }

      sign1.setBold();
      sign2.setBold();


      listmodem.setFont(font);
      listmodem.setFontColor(Color.convertCmykToRgb(color1));
      listmodem.setUnderline();

      sign2.setTextAlignment(TextAlignment.RIGHT); // position d'un text (droite)


      // ajouter tout les paragraphes et les informations dans le document
      if (image != null) {
        document.add(image);
      }
      document.add(title);
      document.add(table2);
      document.add(listmodem);
      document.add(table);
      document.add(sign1);
      document.add(sign2);
      // fermer le document
      document.close();

    } catch (FileNotFoundException e) {

      LOGGER.error("ImprimerFichePDF.createPdf FileNotFoundExceptio: " + e.getMessage());

    }
    return filePdf;
  }

}
