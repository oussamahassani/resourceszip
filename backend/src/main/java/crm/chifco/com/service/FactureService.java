package crm.chifco.com.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.DTOclass.FactureDataDTO;
import crm.chifco.com.DTOclass.MoreThanOneInvoiceRecap;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.Commande;
import crm.chifco.com.model.EntryCommande;
import crm.chifco.com.model.EntryFactures;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.Pack;
import crm.chifco.com.model.Produit;
import crm.chifco.com.model.User;
import crm.chifco.com.templateclasse.FactureNonPayee;
import crm.chifco.com.templateclasse.ListeFactureNonPayeDTO;
import crm.chifco.com.templateclasse.Recouvrement;
import net.sf.jasperreports.engine.JRException;

@Service
@Transactional
public interface FactureService {
  public Facture getFacture(Long id);

  public List<Facture> getFacturesByClient(Long Clientid);

  public List<Facture> getAllFacturesByClient(Long Clientid);
  // ***********************recuperer la liste des factures selon id revendeur

  // ***********************recuperer une facture par son ID en verifiant si l'ID existe dans la BDD
  // ou non
  public Facture getFactureById(Long id) throws Exception;

  public Page<FactureDataDTO> findPaginateFacture(int pageNo, int pageSize, String sortvar,
      String sorttype, String filterrecherche, boolean isProformat);

  public Page<FactureDataDTO> findByConnecteduserAndVisibility(int pageNo, int pageSize,
      String sortvar, String sorttype, String filterrecherche, Long iduser, Boolean isvisible);

  // methode pour ajouter entries tva factures base sur la table entries factures
  public void setEntryTvaFacture(Facture facture);

  public List<EntryFactures> setEntriesfactures(List<EntryCommande> entryCommande);

  public Facture generateFacture(Commande commande, User user, Boolean isferstfacture,
      Date dateProchainFacture, Boolean isFactureResilation);

  public Commande setCommande(Abonnement oneClient, String instantdatefin, String echancedate,
      User user, List<EntryCommande> EntryCommande);

  public List<EntryCommande> setEntriesCommande(Abonnement Abonnment, Pack pack);

  public EntryCommande saveEntriescommande(Double PrixUnitaire, Double Prixttc,
      int NombreMoistypepaiement, Produit produit, Pack pack, String Produitnom, Double prixtva,
      Long tauxtva);

  public List<Recouvrement> findrecouvrementliste(int pageNo, int pageSize, String sortt,
      String orderdir);

  public FactureNonPayee findmontantFactureNonPayee();

  public File createPDFFactureA4(Facture facture) throws Exception, JRException;

  List<Facture> findnonpayerfacture(String recherche, Long telephone);

  List<ListeFactureNonPayeDTO> findListeFactureNonPayeeByCin(String recherche);

  Facture findFactureNonPayeeByReference(String referenceFacture);

  Facture findFacturePayeeByReference(String referenceFacture);

  public Facture ChekIfIsFirstFactureExist(Long clientid);

  public Facture findFirstByAbonnement_clientid(Long clientid);

  public List<Long> addAllIdToExport(String filterrecherche, HttpServletRequest request,
      boolean isProformat);

  public JsonResponseBody removeAllFromListExport(HttpServletRequest request);

  public JsonResponseBody addfiled(@RequestBody Long id, HttpServletRequest request);

  public JsonResponseBody removefiled(@RequestBody Long id, HttpServletRequest request);

  public ListeFactureNonPayeDTO findListeFactureNonPayeByReference(String recherche);

  public List<Facture> findListeFactureNonPayeeByRefFacture(List<String> factures);

  public List<ListeFactureNonPayeDTO> findListeFactureNonPayeeByFixeNumber(Long telephone);

  public Double getSumFactureNonPayee(Long clientid);

  public ModelAndView exportToExcelRecouvrement(HttpServletRequest request);


  Long findAbonnementByListFacture(List<String> facturelist);


  public List<Facture> getFacturesByClientAndIsFactureResilation(Long Clientid,
      Boolean IsFactureResilation);

  public List<Facture> getFacturesByClientAndEtat_facture(Long Clientid, Boolean IsFacturePayed);

  Page<MoreThanOneInvoiceRecap> getTotalSumsFacturesNonPayerByClients(int pageNo, int pageSize,
      String filterrecherche);


  public Page<Map<String, Object>> getAllFacturesByClientForMobile(Long Clientid,
      Pageable pageable);

  public void updateVisibilityFirstFacture(Long isClient);

  public List<Map<String, Object>> getClientsFacturesImpayeesRetard3Jours();

}
