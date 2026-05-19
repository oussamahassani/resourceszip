/*
 * created by hatem ghozzi on 11 11 2022
 */

package crm.chifco.com.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.EntryAbonnement;
import crm.chifco.com.model.EntryDemandeAbonnement;
import crm.chifco.com.model.Produit;
import crm.chifco.com.model.jasper.ContratDataSet;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.EntryAbonnementRepository;
import crm.chifco.com.repository.EntryDemandeAbonnementRepository;
import crm.chifco.com.repository.ProduitRepository;
import crm.chifco.com.service.ReportService;
import crm.chifco.com.utils.PrefixDocument;
import crm.chifco.com.utils.TypeAbonnment;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service("ReportService")
public class ReportServiceimpl implements ReportService {
  @Autowired
  private DemandeAbonnementRepository demandeAbonnementRepository;
  @Autowired
  private ProduitRepository produitRepository;

  @Autowired
  private EntryAbonnementRepository entryAbonnementRepository;

  @Autowired
  private EntryDemandeAbonnementRepository entryDemandeAbonnementRepository;

  @Value("${pathDemandesAbonnement}")
  private String pathDemandesAbonnement;
  @Autowired
  private crm.chifco.com.repository.AbonnementRepository AbonnementRepository;

  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  @Override
  public File generateContratNonsigne(Long demandeId) throws IOException, JRException {
    DemandeAbonnement demandeAbonnement =
        demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
    String referencechifco = demandeAbonnementRepository.findReferenceChifco(demandeId);
    // Abonnement abonnement =
    // AbonnementRepository.findAbonnementByReferenceClient(referencechifco);
    File file1 = null;
    String nomfile = "";

    List<DemandeAbonnement> demandeAbonnementList = new ArrayList<>();
    demandeAbonnementList.add(demandeAbonnement);



    Collection<ContratDataSet> contratDataSetArrayList = new ArrayList<>();
    ContratDataSet contratDataSet = new ContratDataSet();
    List<Abonnement> abonnement = new ArrayList<>();
    abonnement.add(AbonnementRepository.findAbonnementByReferenceClient(referencechifco));
    contratDataSet.setAbonnement(abonnement);

    List<EntryAbonnement> entryAbonnements =
        entryAbonnementRepository.getEntryAbonnementByAbonnement(abonnement.get(0));
    String produitCode = ",";
    for (int i = 0; i < entryAbonnements.size(); i++) {
      produitCode = produitCode + entryAbonnements.get(i).getProduit().getProduitCode() + ",";
    }
    contratDataSetArrayList.add(contratDataSet);
    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(contratDataSetArrayList);
    Map<String, Object> parametes = new HashMap<>();

    parametes.put("produitCode", produitCode);
    /*
     * parametes.put("CodeClient",
     * AbonnementRepository.getReferenceAbonnementByCin(demandeAbonnement.getCin()));
     */

    Produit racordementProduit =
        produitRepository.getFirstProduitByIsDefaultAndIsRacordement(true, true);
    if (racordementProduit != null) {
      parametes.put("prixttcByRacordement",
          produitRepository.getProduitprixttcByRacordement(racordementProduit.getProduitId()));
    } else {
      parametes.put("prixttcByRacordement", 0);
    } ;

    File file = ResourceUtils.getFile("classpath:reports/contratabonnementA4.jrxml");

    JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametes, dataSource);

    File foldertocreate = new File(pathDemandesAbonnement);

    if (!foldertocreate.exists()) {
      foldertocreate.mkdirs();

      foldertocreate.setWritable(true);
    }

    nomfile = pathDemandesAbonnement + PrefixDocument.NOMEFILE_CONTRAT_NONSING
        + demandeAbonnement.getDemandeId();

    JasperExportManager.exportReportToPdfFile(jasperPrint, nomfile + ".pdf");
    file1 = ResourceUtils.getFile(nomfile + ".pdf");

    return file1;

  }

  @Override
  public File generateReport(DemandeAbonnement demandeAbonnement) throws Exception, JRException {

    demandeAbonnement = demandeAbonnementRepository
        .findDemandeAbonnementByDemandeId(demandeAbonnement.getDemandeId());
    File file1 = null;
    String nomfile = "";
    List<DemandeAbonnement> demandeAbonnementList = new ArrayList<>();
    demandeAbonnementList.add(demandeAbonnement);

    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(demandeAbonnementList);
    Map<String, Object> parametes = new HashMap<>();

    for (EntryDemandeAbonnement entryDmd : demandeAbonnement.getEntriesDemandeAbonnement()) {
      switch (entryDmd.getProduit().getProduitCode()) {
        case "OFFICE_VPS_SSL":
          parametes.put("OFFICE_VPS_SSL", true);
          break;
        case "NOM_DE_DOMAINE":
          parametes.put("NOM_DE_DOMAINE", true);
          break;
        case "ANTIVIRUS":
          parametes.put("ANTIVIRUS", true);
          break;
        case "HEBERGEMENT":
          parametes.put("HEBERGEMENT", true);
          break;
        case "IP_FIXE":
          parametes.put("IP_FIXE", true);
          break;

      }
    }

    Produit racordementProduit =
        produitRepository.getFirstProduitByIsDefaultAndIsRacordement(true, true);
    if (racordementProduit != null) {
      parametes.put("prixttcByRacordement",
          produitRepository.getProduitprixttcByRacordement(racordementProduit.getProduitId()));
    } else {
      parametes.put("prixttcByRacordement", 0);
    }
    if(!demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {
    if (demandeAbonnement.getPack().getEngagement() != null
        && demandeAbonnement.getPack().getEngagement().getNombre().equals("365")) {
      parametes.put("mois12", true);
      parametes.put("mois24", false);

    } else {
      parametes.put("mois12", false);
      parametes.put("mois24", true);
    }
  }
    else {
        parametes.put("mois12", false);
        parametes.put("mois24", false);
    }

    // demandeabonnement.jrxml
    File file = ResourceUtils.getFile("classpath:reports/fichedemandedabonnementA4.jrxml");
    if (demandeAbonnement.getPack().getPayLater() != null
        && demandeAbonnement.getPack().getPayLater()) {
      file = ResourceUtils.getFile("classpath:reports/fichedemandedabonnementA4payleter.jrxml");

    }
    JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametes, dataSource);

    File foldertocreate = new File(pathDemandesAbonnement);

    if (!foldertocreate.exists()) {
      foldertocreate.mkdirs();
      LOGGER.info("creating folder");
      foldertocreate.setWritable(true);
    }
    nomfile = pathDemandesAbonnement + PrefixDocument.NOMEFILE_DEMANDE_ABONNEMENT
        + demandeAbonnement.getReferenceChifco();

    JasperExportManager.exportReportToPdfFile(jasperPrint, nomfile + ".pdf");
    file1 = ResourceUtils.getFile(nomfile + ".pdf");

    return file1;

  }
}
