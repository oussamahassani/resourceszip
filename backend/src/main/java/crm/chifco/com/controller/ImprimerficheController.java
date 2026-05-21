package crm.chifco.com.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import crm.chifco.com.model.FicheStock;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.User;
import crm.chifco.com.service.FicheStockService;
import crm.chifco.com.service.ImprimerFichePDF;

@Controller
public class ImprimerficheController {

  @Autowired
  private ImprimerFichePDF imprimerservice;
  @Autowired
  private FicheStockService ficheservice;

  // ************************************************************ api pour
  // imprimer une fiche de stock en utilisant sa reference

  @RequestMapping(path = "/imprimer/{ref}")
  public void downloadPDFResource(HttpServletResponse response, @PathVariable("ref") String ref,
      Model model) throws Exception {

    String path = imprimerservice.createPdf(ref); // utiliser la methode creatPdf de la classe :
                                                  // ImprimerService ,
                                                  // package : service
    // affiuche une message d'impression
    model.addAttribute("message",
        "La fiche est bien telechargée dans le dossier : D:/Telechargement D/PDFfile");
    response.setContentType("application/pdf");

    // methode de la classe: FactureService package :service
    InputStream inputStream = new FileInputStream(new File(path));

    int nRead;
    while ((nRead = inputStream.read()) != -1) {
      response.getWriter().write(nRead);
    }
  }

  // ******************************************************************* api pour
  // visualiser une fiche avant l'impression

  @RequestMapping(path = {"/pdf_fiche/{ref}"})
  public String getfiche(Model model, @PathVariable("ref") String ref) {
    FicheStock fiche = ficheservice.getFiche(ref);
    List<Modem> modems = ficheservice.listmodemfiche(ref);
    User u = ficheservice.usersfiche(fiche.getAffecteID());
    model.addAttribute("fiche", fiche);
    model.addAttribute("modems", modems);
    model.addAttribute("users", u);

    model.addAttribute("quantite", modems.size());
    return "fiche/pdf_fiche";
  }

}
