package crm.chifco.com.service.impl;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.DTOclass.FactureDataDTO;
import crm.chifco.com.DTOclass.MoreThanOneInvoiceRecap;
import crm.chifco.com.converter.FactureToListeFactureNonPayeDTOConverter;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.Commande;
import crm.chifco.com.model.EntryAbonnement;
import crm.chifco.com.model.EntryCommande;
import crm.chifco.com.model.EntryFactures;
import crm.chifco.com.model.EntryPack;
import crm.chifco.com.model.EntryTvaFacture;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Historique;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.MigrationFacture;
import crm.chifco.com.model.Pack;
import crm.chifco.com.model.Produit;
import crm.chifco.com.model.Smstemplate;
import crm.chifco.com.model.Tarification;
import crm.chifco.com.model.User;
import crm.chifco.com.model.jasper.FactureDataSet;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.CommandeRepository;
import crm.chifco.com.repository.EntriesfacturesRepository;
import crm.chifco.com.repository.EntryAbonnementRepository;
import crm.chifco.com.repository.EntryCommandeRepository;
import crm.chifco.com.repository.EntryPackRepository;
import crm.chifco.com.repository.EntryTvaFactureRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.MigrationFactureRepository;
import crm.chifco.com.repository.PackRepository;
import crm.chifco.com.repository.ProduitRepository;
import crm.chifco.com.repository.SmstemplateRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.ExportExcelRecouvrement;
import crm.chifco.com.service.FactureService;
import crm.chifco.com.service.GenerateFactureService;
import crm.chifco.com.service.GenerateSequenceFacture5G;
import crm.chifco.com.service.GenerateSequenceFactureProformat;
import crm.chifco.com.service.GenerateSequenceNewFacture;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.TarificationServices;
import crm.chifco.com.templateclasse.FactureNonPayee;
import crm.chifco.com.templateclasse.ListeFactureNonPayeDTO;
import crm.chifco.com.templateclasse.Recouvrement;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.PrefixDocument;
import crm.chifco.com.utils.TypeAbonnment;
import crm.chifco.com.utils.typeCalcluleMigrationFacture;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service("FactureService")
public class FactureServiceimpl implements FactureService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  @Autowired
  private FactureRepository factureRepository;

  @Autowired
  private GenerateSequenceFactureProformat generateSequenceFactureProformat;

  @Autowired
  private GenerateSequenceNewFacture generateSequenceNewFacture;

  @Autowired
  private EntryTvaFactureRepository entryTvaFactureRepository;
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CommandeRepository commandeRepository;

  @Autowired
  private EntryCommandeRepository entryCommandeRepository;

  @Autowired
  private EntriesfacturesRepository entriesfacturesRepository;

  @Autowired
  private ProduitRepository produitRepository;

  @Autowired
  private AbonnementRepository abonnementRepository;

  @Value("${pathFacture}")
  private String pathFacture;

  @Autowired
  private GenerateFactureService GenerateFactureService;

  @Autowired
  private GenerateSequenceFacture5G generateSequenceFacture5G;

  @Autowired
  TarificationServices tarificationServices;

  @Value("${timbrefiscale}")
  Double timbrefiscale;

  @Value("${avanceFacture5g}")
  Double avanceFacture5G;

  @Value("${delaiDePaiement}")
  private int delaiDePaiement;

  @Value("${cron.invoice.generation:}")
  private String cronFacturation;
  @Value("${firstFactureGracePeriod}")
  private String firstFactureGracePeriod;

  @Value("${tva}")
  private String tva;

  @Autowired
  MigrationFactureRepository migrationFactureRepository;

  @Autowired
  crm.chifco.com.repository.HistoriqueRepository historiqueRepository;

  @Autowired
  Notification notificationservice;

  @Autowired
  SmstemplateRepository templatesmsRepository;

  @Autowired
  EntryAbonnementRepository entryAbonnementRepository;

  @Autowired
  PackRepository packRepository;
  @Autowired
  EntryPackRepository entryPackRepository;

  Boolean entryPackHasraccordement = false;

  @Autowired
  private FactureToListeFactureNonPayeDTOConverter converter;

  public Facture saveFacture(Facture facture) {
    return factureRepository.save(facture);
  }

  public boolean deleteFature(Long id) {
    Facture facture = factureRepository.getById(id);
    if (facture != null) {
      // fRepo.delete(f);
      facture.setIsDelete(true);
      factureRepository.save(facture);
      return true;
    }

    return false;
  }

  public Facture getFacture(Long id) {
    return factureRepository.getById(id);
  }

  public List<Facture> getFacturesByClient(Long Clientid) {
    return factureRepository.findByvisibilityAndAbonnement_clientid(true, Clientid);
  }

  public List<Facture> getAllFacturesByClient(Long Clientid) {
    return factureRepository.findByAbonnement_clientid(Clientid);
  }

  public Facture getFactureById(Long id) throws Exception {
    Optional<Facture> facture = factureRepository.findById(id);

    if (facture.isPresent()) {
      return facture.get(); // retourne la facture
    } else {
      throw new Exception("No Facture record exist for given id"); // message exception si la
                                                                   // facture n'existe pas
    }
  }

  @Scheduled(cron = "0 0 9,14 * * ?") // 9,14
  public void recuringFacture() throws ParseException {
    Smstemplate findtemplatesms =
        templatesmsRepository.findSmstemplateByname("generationfacturesSMS");
    String Template = findtemplatesms.getTemplate();
    ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();

    if (cronFacturation.equals("true")) {
      LOGGER.info("Calculating recurrent billing started");

      // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
      User user = userRepository.findUsersByEmail("system@chifco.com");

      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String dateNow = simpleDateFormat.format(new Date());
      Date queryDate = simpleDateFormat.parse(dateNow);
      List<Abonnement> allClient = abonnementRepository
          .findByDateProchainFacturationLessThanEqualAndIsActive(queryDate, true);

      LOGGER.info(
          "Calculating recurrent billing for " + allClient.size() + " clients by date " + dateNow);
      // ArrayList<String> arrayTelephoneClient = new ArrayList<>();
      // ArrayList<String> numeroFixeAbonnement = new ArrayList<>();
      allClient.forEach(client -> {
        LOGGER.info("Calculating billing for " + client.getReferenceClient());
        int typDePaymentMonth = client.getTypePaiement().getNombreMoisTypePaiement();
        Date nouvauDateFin =
            CrmUtils.calculeDateFin(typDePaymentMonth, client.getDateProchainFacturation());
        LOGGER.info("billing next date " + nouvauDateFin);

        /// methode_pour_creation_des_entries_commandes_avec_save
        List<EntryCommande> entryCommandes = null;

        if (client.getDateFinPromotion() != null
            && nouvauDateFin.compareTo(client.getDateFinPromotion()) > 0) {
          Pack packParent = packRepository.getBypackId(client.getPack().getIdPackBase());
          if (packParent == null) {
            packParent = client.getPack();
          }


          entryCommandes = setEntriesCommande(client, packParent);

        } else {
          entryCommandes = setEntriesCommande(client, client.getPack());
        }

        Commande nouvelleCommande =
            setCommande(client, nouvauDateFin.toString(), null, user, entryCommandes);
        /// methode_pour_creation_nouvaux_facture_avec_save
        Facture nouvauxFacture =
            generateFacture(nouvelleCommande, client.getUser(), false, nouvauDateFin, null);
        /// methode_pour_creation_nouvaux_Entries_tva_facture_avec_save
        setEntryTvaFacture(nouvauxFacture);
        // client.setDateProchainFacturation(nouvauDateFin);
        Historique historique = new Historique();
        historique.setAbonnement(client);
        historique.setPrix(nouvauxFacture.getMontant_payer());
        historique.setFacture(nouvauxFacture);
        historique.setUser(nouvauxFacture.getUser());
        historique.setMailClient(nouvauxFacture.getCommande().getClient().getEmail());

        historique.setDescription(
            "Generation la facture" + nouvauxFacture.getFactureId() + " (avec forfait de"
                + nouvauxFacture.getAbonnement().getTypePaiement().getNomTypePaiement() + ") "
                + LocalDate.now().getMonth());

        historiqueRepository.save(historique);


        /*
         * if (client.getTelMobile() != null && client.getIsSmsClientSend() != null &&
         * client.getIsSmsClientSend() == true) {
         * arrayTelephoneClient.add(client.getTelMobile().toString()); }
         */
        if (client != null && client.getTelFixe() != null && client.getTelMobile() != null
            && client.getIsSmsClientSend() != null && client.getIsSmsClientSend() == true) {
          Map<String, Object> Message = new HashMap<String, Object>();
          // numeroFixeAbonnement.add(nouvauxFacture.getAbonnement().getTelFixe().toString());
          String NewTemplate = Template.replace("{numfacture}",
              nouvauxFacture.getAbonnement().getTelFixe().toString());

          Message.put("number", client.getTelMobile().toString());
          Message.put("message", NewTemplate);
          smsToSend.add(Message);
        }

        abonnementRepository.updateDateProchainFacture(nouvauDateFin, client.getClientid());

      });
      if (smsToSend.size() > 0) {
        sendSmsGenerateNewFacture(smsToSend);
      }

      LOGGER.info("Calculating recurrent billing Ended");
    }
  }

  public Page<FactureDataDTO> findPaginateFacture(int pageNo, int pageSize, String sortvar,
      String sorttype, String filterrecherche, boolean isProformat) {

    String ref_facture = null;
    Double montantMinimum = null;
    Double montantMaximum = null;
    Boolean status = null;
    Date dateCreationDebut = null;
    Date dateCreationFin = null;
    Date dateEcheanceDebut = null;
    Date dateEcheanceFin = null;
    String codeClient = null;
    Long telFixe = null;
    String cin = null;
    Date datePayementDebut = null;
    Date datePayementFin = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("ref_facture"), "")
          && obj.getString("ref_facture") != null) {
        ref_facture = obj.getString("ref_facture").trim();
      }
      if (!Objects.equals(obj.getString("montantMinimum"), "")
          && obj.getString("montantMinimum") != null) {
        montantMinimum = Double.parseDouble(obj.getString("montantMinimum").trim());
      }
      if (!Objects.equals(obj.getString("montantMaximum"), "")
          && obj.getString("montantMaximum") != null) {
        montantMaximum = Double.parseDouble(obj.getString("montantMaximum").trim());
      }
      if (!Objects.equals(obj.getString("status"), "") && obj.getString("status") != null) {
        status = obj.getString("status").trim().equals("0") ? false : true;
      }
      if (!Objects.equals(obj.getString("dateCreationDebut"), "")
          && obj.getString("dateCreationDebut") != null) {
        String dateBefaureConverted = obj.getString("dateCreationDebut");
        dateCreationDebut = CrmUtils.convertStringToLocalDataTimeStart(dateBefaureConverted);
      }
      if (!Objects.equals(obj.getString("dateCreationFin"), "")
          && obj.getString("dateCreationFin") != null) {
        String dateBefaureConverted = obj.getString("dateCreationFin");
        dateCreationFin = CrmUtils.convertStringToLocalDateTime(dateBefaureConverted);
      }
      if (!Objects.equals(obj.getString("dateEcheanceDebut"), "")
          && obj.getString("dateEcheanceDebut") != null) {
        dateEcheanceDebut = CrmUtils.convertStringToDate(obj.getString("dateEcheanceDebut"));
      }
      if (!Objects.equals(obj.getString("dateEcheanceFin"), "")
          && obj.getString("dateEcheanceFin") != null) {
        dateEcheanceFin = CrmUtils.convertStringToDate(obj.getString("dateEcheanceFin"));
      }
      if (!Objects.equals(obj.getString("codeClient"), "") && obj.getString("codeClient") != null) {
        codeClient = obj.getString("codeClient").trim();
      }
      if (!Objects.equals(obj.getString("numeroFixe"), "") && obj.getString("numeroFixe") != null) {
        telFixe = Long.parseLong(obj.getString("numeroFixe").trim());
      }
      if (!Objects.equals(obj.getString("identifientClient"), "")
          && obj.getString("identifientClient") != null) {
        cin = obj.getString("identifientClient").trim();
      }
      if (!Objects.equals(obj.getString("datePayementDebut"), "")
          && obj.getString("datePayementDebut") != null) {
        datePayementDebut =
            CrmUtils.convertStringToLocalDataTimeStart(obj.getString("datePayementDebut").trim());
      }
      if (!Objects.equals(obj.getString("datePayementFin"), "")
          && obj.getString("datePayementFin") != null) {
        datePayementFin =
            CrmUtils.convertStringToLocalDateTime(obj.getString("datePayementFin").trim());
      }
    }
    Sort sort = Sort.by("modifiedDate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    // this.factureRepository.findAll(pageable);
    return this.factureRepository.findFacturesByvisibility(pageable, null, ref_facture,
        montantMinimum, montantMaximum, status, dateCreationDebut, dateCreationFin,

        dateEcheanceDebut, dateEcheanceFin, codeClient, telFixe, cin, datePayementDebut,
        datePayementFin, isProformat);

  }

  public Page<FactureDataDTO> findByConnecteduserAndVisibility(int pageNo, int pageSize,
      String sortvar, String sorttype, String filterrecherche, Long iduser, Boolean isvisible) {

    String ref_facture = null;
    Double montantMinimum = null;
    Double montantMaximum = null;
    Boolean status = null;
    Date dateDebut = null;
    Date dateFin = null;
    Date dateEcheanceDebut = null;
    Date dateEcheanceFin = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("ref_facture"), "")
          && obj.getString("ref_facture") != null) {
        ref_facture = obj.getString("ref_facture").trim();
      }
      if (!Objects.equals(obj.getString("montantMinimum"), "")
          && obj.getString("montantMinimum") != null) {
        montantMinimum = Double.parseDouble(obj.getString("montantMinimum").trim());
      }
      if (!Objects.equals(obj.getString("status"), "") && obj.getString("status") != null) {
        status = obj.getString("status").trim().equals("0") ? false : true;
      }
      if (!Objects.equals(obj.getString("dateDebut"), "") && obj.getString("dateDebut") != null) {
        dateDebut = CrmUtils.convertStringToDate(obj.getString("dateDebut"));
      }
      if (!Objects.equals(obj.getString("dateFin"), "") && obj.getString("dateFin") != null) {
        dateFin = CrmUtils.convertStringToDate(obj.getString("dateFin"));
      }
      if (!Objects.equals(obj.getString("dateEcheanceDebut"), "")
          && obj.getString("dateEcheanceDebut") != null) {
        dateEcheanceDebut = CrmUtils.convertStringToDate(obj.getString("dateEcheanceDebut"));
      }
      if (!Objects.equals(obj.getString("dateEcheanceFin"), "")
          && obj.getString("dateEcheanceFin") != null) {
        dateEcheanceFin = CrmUtils.convertStringToDate(obj.getString("dateEcheanceFin"));
      }
    }

    Sort sort = Sort.by("modifiedDate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

    return this.factureRepository.findByConnecteduserAndVisibility(pageable, iduser, isvisible,
        ref_facture, montantMinimum, montantMaximum, status, dateDebut, dateFin, dateEcheanceDebut,
        dateEcheanceFin);
  }

  public void setEntryTvaFacture(Facture facture) {

    List<EntryFactures> entryFactures = facture.getEntriesFacture();
    Set<Long> linkedHashSetTVA = new LinkedHashSet<>();

    for (EntryFactures entriesfacture : entryFactures) {
      if (entriesfacture.getPourcentageTva() != null
          && !entriesfacture.getPourcentageTva().equals(0L))
        linkedHashSetTVA.add(entriesfacture.getPourcentageTva());
    }

    if (!linkedHashSetTVA.isEmpty())
      for (Long onelinkedHashSetTVA : linkedHashSetTVA) {
        Double base = 0.0;
        Double montant = 0.0;

        for (EntryFactures entriesfacture : entryFactures) {
          if (entriesfacture.getPourcentageTva().equals(onelinkedHashSetTVA)
              && entriesfacture.getPrixTva() != null && entriesfacture.getPrixTotalHT() != null) {
            base = base + entriesfacture.getPrixTotalHT();
            montant = montant + entriesfacture.getPrixTva();
          }
        }
        if (!base.equals(0.0) && !montant.equals(0.0)) {
          EntryTvaFacture entryTvaFacture = new EntryTvaFacture();
          entryTvaFacture.setTauxTva(onelinkedHashSetTVA.doubleValue());
          entryTvaFacture.setBase(base);
          entryTvaFacture.setMontant(montant);
          entryTvaFacture.setFacture(facture);
          entryTvaFactureRepository.save(entryTvaFacture);

        }
      }

  }

  public List<EntryFactures> setEntriesfactures(List<EntryCommande> entryCommandes) {
    List<EntryFactures> ListEnty = new ArrayList<EntryFactures>();
    entryCommandes.forEach(commandeentry -> {
      EntryFactures entriesFactures = new EntryFactures();
      entriesFactures.setPrixTtc(commandeentry.getPrixTtc());
      entriesFactures.setPrixTva(commandeentry.getPrixTva());
      entriesFactures.setPrixTotalHT(commandeentry.getPrixTotalHt());
      // EntryFactures.setpourcentagetva(tauxtva.longValue());
      entriesFactures.setPrixUnitaireHT(commandeentry.getPrixUnitaire());
      entriesFactures.setProduit(commandeentry.getProduit());
      entriesFactures.setPack(commandeentry.getPack());
      entriesFactures.setQuantiter(commandeentry.getQuantiter());
      entriesFactures.setProductName(commandeentry.getProductName());
      entriesFactures.setPourcentageTva(commandeentry.getPourcentageTva());
      entriesfacturesRepository.save(entriesFactures);
      ListEnty.add(entriesFactures);
    });
    return ListEnty;

  }

  public Facture generateFacture(Commande commande, User user, Boolean isferstfacture,
      Date dateProchainFacture, Boolean isFactureResilation) {
    Abonnement abonnement = commande.getClient();
    List<EntryCommande> commandeentrys = commande.getEntryCommande();
    // SimpleDateFormat datefinformater = new SimpleDateFormat("dd/MM/yyyy");
    Facture premierefacture = new Facture();
    // factureRepository.save(premierefacture);
    // LOGGER.info("id facture", premierefacture.getFactureId());

    List<EntryFactures> entriesFactures = setEntriesfactures(commandeentrys);

    premierefacture.setCommande(commande);
    Double timbreFiscal = timbrefiscale;
    /*
     * if(isferstfacture &&
     * commande.getClient().getPack().getCategoriePack().getCategorieProduitInternetCode().equals(
     * "5G")){ timbreFiscal = timbreFiscal ; }
     */
    premierefacture.setTimbrefiscale(timbreFiscal);
    // premierefacture.setRemise(commande.getClient().getProduit().getRemise());
    premierefacture.setEntriesFacture(entriesFactures);
    premierefacture.setUser(user); // set le champs user de la facture
    premierefacture.setAbonnement(abonnement);
    premierefacture.setEtat_facture(false); // set l'etat de la facture
    if (abonnement.getTypePaiement() != null) {
      LocalDateTime date = LocalDateTime.now();

      if (isferstfacture) {

        Instant debutFacturation = date.atZone(ZoneId.systemDefault())
            .plusDays(Long.parseLong(firstFactureGracePeriod)).toInstant();
        if (abonnement.getPack().getCategoriePack().getCategorieProduitInternetCode()
            .equals(TypeAbonnment.Box)) {
          debutFacturation = date.atZone(ZoneId.systemDefault()).toInstant();
        }

        Instant dateecheance =
            debutFacturation.plus(Long.valueOf(delaiDePaiement), ChronoUnit.DAYS);

        if (abonnement.getPack() != null && abonnement.getPack().getPayLater() != null
            && abonnement.getPack().getPayLater()) {
          debutFacturation = date.atZone(ZoneId.systemDefault()).toInstant();
          dateecheance = debutFacturation.plus(Long.valueOf(5), ChronoUnit.DAYS);
        }

        premierefacture.setDateDeDebut(Date.from(debutFacturation));
        premierefacture.setDate_echeance(Date.from(dateecheance));
        premierefacture.setIsFirstFacture(true);
        premierefacture.setVisibility(false);

        int typdedepaymentmonth = abonnement.getTypePaiement().getNombreMoisTypePaiement();

        Date factureRecurentDebut = null;
        if (isferstfacture && commande.getClient().getPack().getCategoriePack()
            .getCategorieProduitInternetCode().equals("5G")) {

          typdedepaymentmonth = typdedepaymentmonth * avanceFacture5G.intValue();
          factureRecurentDebut = premierefacture.getDateDeDebut();

        }
        Date dateFinFacture = CrmUtils.calculeDateFin(typdedepaymentmonth, factureRecurentDebut);

        premierefacture.setDateDeFin(dateFinFacture);
        if (commande.getClient().getPack().getPayLater() != null
            && commande.getClient().getPack().getPayLater()) {
          premierefacture.setDateDeFin(dateProchainFacture);
        }
        if (commande.getClient().getPack().getCategoriePack().getCategorieProduitInternetCode()
            .equals(TypeAbonnment.Box)) {
          premierefacture.setIsProformat(false);
          String generateSequenceFacture5g = generateSequenceFacture5G.generateWithPrefix();
          premierefacture.setRef_facture(generateSequenceFacture5g);

        } else {
          premierefacture.setIsProformat(true);
          String proformatReference = generateSequenceFactureProformat.generateWithPrefix();
          premierefacture.setRef_facture(proformatReference);

        }



      } else if (isFactureResilation != null && isFactureResilation == true) {
        premierefacture.setIsFactureResilation(isFactureResilation);
        premierefacture.setDateDeFin(CrmUtils.addDayTocurentDate(15L));
        premierefacture.setDate_echeance(CrmUtils.addDayTocurentDate(15L));
        premierefacture.setVisibility(true);
        String proformatReference = generateSequenceNewFacture.generateWithPrefix();
        premierefacture.setRef_facture(proformatReference);
      } else {
        Date safeDate = new Date(abonnement.getDateProchainFacturation().getTime());
        Instant debutFacturation = safeDate.toInstant().plus(1, ChronoUnit.DAYS);
        premierefacture.setDateDeFin(dateProchainFacture);
        premierefacture.setVisibility(true);
        if (commande.getClient().getPack().getCategoriePack().getCategorieProduitInternetCode()
            .equals(TypeAbonnment.Box)) {
          premierefacture.setIsProformat(false);
          String generateSequenceFacture5g = generateSequenceFacture5G.generateWithPrefix();
          premierefacture.setRef_facture(generateSequenceFacture5g);

        } else {
          String proformatReference = generateSequenceNewFacture.generateWithPrefix();
          premierefacture.setRef_facture(proformatReference);
        }


        premierefacture.setDateDeDebut(Date.from(debutFacturation));
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
        premierefacture.setDate_echeance(CrmUtils.convertStringToDate(formatter
            .format(debutFacturation.plus(Long.valueOf(delaiDePaiement), ChronoUnit.DAYS))));

      }
    }

    Facture FactureWithMontant = GenerateFactureService.calculeMontantFacture(premierefacture);

    factureRepository.save(FactureWithMontant);

    ///
    LOGGER.info("abonnement.has raccoredement() " + abonnement.getTrancheRaccordement()
        + " client id " + abonnement.getClientid() + " "
        + (abonnement.getHasRaccordement() && abonnement.getTrancheRaccordement() > 0));
    if (abonnement.getHasRaccordement() && abonnement.getTrancheRaccordement() > 0) {
      LOGGER.info("true racordemnt" + (abonnement.getTrancheRaccordement() - 1));
      abonnementRepository.updateTranshRaccordement((abonnement.getTrancheRaccordement() - 1),
          abonnement.getClientid());

    }
    ///
    return premierefacture;
  }

  public Commande setCommande(Abonnement oneClient, String instantdatefin, String echancedate,
      User user, List<EntryCommande> EntryCommande) {

    Commande commande = new Commande();
    if (oneClient != null) {

      commande.setClient(oneClient);
    }
    if (user != null)
      commande.setUser(user);

    if (instantdatefin != null)
      commande.setDateFin(instantdatefin);
    if (echancedate != null)
      commande.setDateEchance(echancedate);

    commande.setEntryCommande(EntryCommande);
    // LOGGER.info(commande);
    commandeRepository.save(commande);
    return commande;
  }

  public List<EntryCommande> setEntriesCommande(Abonnement Abonnment, Pack pack) {
    List<EntryCommande> EntryCommandes = new ArrayList<>();
    if (pack == null) {
      LOGGER.info(Abonnment.getCin() + "with pack Id is null");
      return EntryCommandes;
    }

    Tarification tarificationpack = tarificationServices.getTarificationBypackId(pack.getPackId());
    Double produitprixHt = tarificationpack.getPrixUnitaire();

    int nombreMois = Abonnment.getTypePaiement().getNombreMoisTypePaiement();

    // int nombreMoisAnnuel = (nombreMois == 12) ? 11 : nombreMois;
    Double packPrixTTC = tarificationpack.getPrixTTc() * nombreMois;
    String packnom = pack.getTitle();
    Double totalPrixtva = (produitprixHt * (tarificationpack.getTaxe() * 0.01)) * nombreMois;

    EntryCommandes.add(this.saveEntriescommande(CrmUtils.formatDoubleInput(produitprixHt),
        CrmUtils.formatDoubleInput(packPrixTTC), nombreMois, null, pack, packnom,
        CrmUtils.formatDoubleInput(totalPrixtva), tarificationpack.getTaxe()));

    entryPackHasraccordement = false;
    List<EntryPack> entyPackAbonnement = entryPackRepository.getEntryPackByPack(pack);
    entyPackAbonnement.forEach(entrypack -> {
      if (entrypack.getShowProduitFacture()) {
        Tarification tarificationProduitPack =
            tarificationServices.getTarificationByProduitId(entrypack.getProduit().getProduitId());
        if (tarificationProduitPack != null) {
          if (entrypack.getProduit().getIsRacordement() && Abonnment.getTrancheRaccordement() > 0
              && Abonnment.getHasRaccordement()) {
            Double prixHt = tarificationProduitPack.getPrixUnitaire()
                / Abonnment.getTrancheRaccordementSelected();
            Long pourcentTva = tarificationProduitPack.getTaxe();
            Double prixTva = ((pourcentTva * prixHt) * 0.01);
            Double prixttc = prixHt + prixTva;
            EntryCommandes.add(this.saveEntriescommande(CrmUtils.formatDoubleInput(prixHt),
                CrmUtils.formatDoubleInput(prixttc), 1, entrypack.getProduit(), null,
                entrypack.getProduit().getProduitNom(), CrmUtils.formatDoubleInput(prixTva),
                pourcentTva));
            entryPackHasraccordement = true;
          }
          if (!entrypack.getProduit().getIsRacordement()) {

            HashMap<String, Object> calculePrix =
                ObjectTarificationFacturation(tarificationProduitPack, nombreMois);

            String produitnom = entrypack.getProduit().getProduitNom();
            EntryCommandes.add(this.saveEntriescommande(
                CrmUtils.formatDoubleInput(tarificationProduitPack.getPrixUnitaire()),
                CrmUtils.formatDoubleInput((Double) (calculePrix.get("prixTTCProduit"))),
                nombreMois, entrypack.getProduit(), null, produitnom,
                CrmUtils.formatDoubleInput((Double) (calculePrix.get("prixTvaProduit"))),
                tarificationProduitPack.getTaxe()));
          }
        }
      } else {
        if (entrypack.getProduit().getIsRacordement() && Abonnment.getTrancheRaccordement() > 0
            && Abonnment.getHasRaccordement()) {
          entryPackHasraccordement = true;
        }
      }
    });

    List<EntryAbonnement> abonnementEntry =
        entryAbonnementRepository.getEntryAbonnementByAbonnement(Abonnment);
    if (abonnementEntry.size() > 0) {
      abonnementEntry.forEach(produitAbonnement -> {
        Tarification tarificationProduitAbonnement = tarificationServices
            .getTarificationByProduitId(produitAbonnement.getProduit().getProduitId());
        if (tarificationProduitAbonnement != null) {
          String produitnom = produitAbonnement.getProduit().getProduitNom();
          HashMap<String, Object> calculePrix =
              ObjectTarificationFacturation(tarificationProduitAbonnement, nombreMois);
          EntryCommandes.add(this.saveEntriescommande(
              CrmUtils.formatDoubleInput(tarificationProduitAbonnement.getPrixUnitaire()),
              CrmUtils.formatDoubleInput((Double) (calculePrix.get("prixTTCProduit"))), nombreMois,
              produitAbonnement.getProduit(), null, produitnom,
              CrmUtils.formatDoubleInput((Double) (calculePrix.get("prixTvaProduit"))),
              tarificationProduitAbonnement.getTaxe()));
        }
      });
    }

    if (Abonnment.getHasRaccordement() && Abonnment.getTrancheRaccordement() > 0
        && Abonnment.getTrancheRaccordementSelected() > 0 && !entryPackHasraccordement) {
      Produit produitRaccordement =
          produitRepository.getFirstProduitByIsDefaultAndIsRacordement(true, true);
      Tarification tarificationProduitRaccordement =
          tarificationServices.getTarificationByProduitId(produitRaccordement.getProduitId());
      Double prixHt = tarificationProduitRaccordement.getPrixUnitaire()
          / Abonnment.getTrancheRaccordementSelected();
      Long pourcentTva = tarificationProduitRaccordement.getTaxe();
      Double prixTva = ((pourcentTva * prixHt) / 100);
      Double prixttc = prixHt + prixTva;
      EntryCommandes.add(this.saveEntriescommande(CrmUtils.formatDoubleInput(prixHt),
          CrmUtils.formatDoubleInput(prixttc), 1, produitRaccordement, pack,
          produitRaccordement.getProduitNom(), CrmUtils.formatDoubleInput(prixTva), pourcentTva));

    }
    if (Abonnment.getIsMigration() != null && Abonnment.getIsMigration() == true) {
      MigrationFacture migrationFacture =
          migrationFactureRepository.findFirstByClientidAndTypeCalculeOrderByFactureMigrationIdDesc(
              Abonnment.getClientid(), typeCalcluleMigrationFacture.MIGRATION);
      if (migrationFacture != null) {
        Double prixttcMigration =
            migrationFacture.getMontantHt() + migrationFacture.getMontantTva();
        EntryCommandes.add(
            this.saveEntriescommande(CrmUtils.formatDoubleInput(migrationFacture.getMontantHt()),
                CrmUtils.formatDoubleInput(prixttcMigration), 1, null, pack,
                migrationFacture.getNameMigration(),
                CrmUtils.formatDoubleInput(migrationFacture.getMontantTva()),
                migrationFacture.getPercentTva()));
        Abonnment.setIsMigration(false);
        abonnementRepository.save(Abonnment);
      }
    }
    if (Abonnment.getIsChangementDebit() != null && Abonnment.getIsChangementDebit() == true) {
      MigrationFacture migrationFacture =
          migrationFactureRepository.findFirstByClientidAndTypeCalculeOrderByFactureMigrationIdDesc(
              Abonnment.getClientid(), typeCalcluleMigrationFacture.CHANGEMENTDEBIT);
      if (migrationFacture != null) {
        Double prixttcMigration =
            migrationFacture.getMontantHt() + migrationFacture.getMontantTva();
        EntryCommandes.add(
            this.saveEntriescommande(CrmUtils.formatDoubleInput(migrationFacture.getMontantHt()),
                CrmUtils.formatDoubleInput(prixttcMigration), 1, null, pack,
                migrationFacture.getNameMigration(),
                CrmUtils.formatDoubleInput(migrationFacture.getMontantTva()),
                migrationFacture.getPercentTva()));
      }
      Abonnment.setIsChangementDebit(false);
      abonnementRepository.save(Abonnment);
    }
    if (Abonnment.getTranchcreditFacture() != null && Abonnment.getTranchcreditFacture() > 0
        && Abonnment.getDateFinPromotion() != null && Abonnment.getPack().getPayLater() != null
        && Abonnment.getPack().getPayLater()) {
      // Convertir Date en LocalDate
      LocalDate localDateFinPromotion =
          Abonnment.getDateFinPromotion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

      // Date actuelle
      LocalDate dateActuelle = LocalDate.now();
      if (localDateFinPromotion.isBefore(dateActuelle)) {
        Double TranchcreditFactureTVA =
            (Abonnment.getCreditFacture() * (tarificationpack.getTaxe() * 0.01)) * nombreMois;
        Double TranchcreditFactureTTc =
            (Abonnment.getCreditFacture() * nombreMois) + TranchcreditFactureTVA;
        EntryCommandes.add(this.saveEntriescommande(
            CrmUtils.formatDoubleInput((Abonnment.getCreditFacture() * nombreMois)),
            CrmUtils.formatDoubleInput(TranchcreditFactureTTc), 1, null, pack,
            "Reliquat de l offre " + Abonnment.getPack().getOffre().getTitle(),
            CrmUtils.formatDoubleInput(TranchcreditFactureTVA), tarificationpack.getTaxe()));

        Abonnment.setTranchcreditFacture(Abonnment.getTranchcreditFacture() - nombreMois);
        abonnementRepository.save(Abonnment);
      }
    }
    return EntryCommandes;
  }

  public EntryCommande saveEntriescommande(Double prixUnitaire, Double Prixttc,
      int nombreMoisTypePaiement, Produit produit, Pack pack, String produitnom, Double prixtva,
      Long tauxtva) {
    EntryCommande entryCommande = new EntryCommande();
    entryCommande.setPrixUnitaire(prixUnitaire);
    entryCommande.setPrixTotalHt(prixUnitaire * nombreMoisTypePaiement);
    entryCommande.setPrixTtc(Prixttc);
    entryCommande.setQuantiter(nombreMoisTypePaiement);
    entryCommande.setProduit(produit);
    entryCommande.setPack(pack);
    entryCommande.setProductName(produitnom);
    entryCommande.setPourcentageTva(tauxtva);
    entryCommande.setPrixTva(prixtva);
    entryCommandeRepository.save(entryCommande);
    // LOGGER.info(entryCommande);
    return entryCommande;
  }

  @Override
  public File createPDFFactureA4(Facture facture) throws Exception {
    File file1 = null;
    String nomfile = "";

    Map<String, Object> parametes = new HashMap<>();

    if (facture != null && facture.getFactureId() != null) {

      try {

        parametes.put("timbrefiscal", CrmUtils.fixNBZero(timbrefiscale));

        Collection<FactureDataSet> factureDataSetArrayList = new ArrayList<>();
        FactureDataSet factureDataSet = new FactureDataSet();
        List<EntryFactures> factureentry = facture.getEntriesFacture();
        if (factureentry.isEmpty() == false) {
          Collection<EntryFactures> entyFactureDataSetList =
              Collections.synchronizedList(factureentry);
          factureDataSet.setEntriesfactures(entyFactureDataSetList);
        }
        ///
        List<EntryTvaFacture> entryTvaFactureList = entryTvaFactureRepository
            .findEntryTvaFacturesByFactureFactureId(facture.getFactureId());
        if (entryTvaFactureList.isEmpty() == false) {
          parametes.put("isset_tva", true);
          Collection<EntryTvaFacture> EntryTvaFactureDataSetList =
              Collections.synchronizedList(entryTvaFactureList);
          factureDataSet.setEntryTvaFactures(EntryTvaFactureDataSetList);
        }

        List<Facture> factureList = new ArrayList<>();
        factureList.add(facture);
        // if (entryTvaFactureList.isEmpty() == false) {
        Collection<Facture> factures = Collections.synchronizedList(factureList);
        factureDataSet.setFactures(factures);
        // }

        factureDataSetArrayList.add(factureDataSet);

        // end

        // fichedemandefactureA4.jrxml
        File file = ResourceUtils.getFile("classpath:reports/fichedemandefactureA4.jrxml");
        if (facture.getIsFactureResilation() != null && facture.getIsFactureResilation() == true) {
          file = ResourceUtils.getFile("classpath:reports/factureResiliationA4.jrxml");
        } else if (facture.getIsProformat()) {
          file = ResourceUtils.getFile("classpath:reports/factureProformaA4.jrxml");
        }
        JRBeanCollectionDataSource dataSource =
            new JRBeanCollectionDataSource(factureDataSetArrayList);
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametes, dataSource);

        int year = CrmUtils.getYearFromDate(facture.getCreatedDate());
        int month = CrmUtils.getMonthFromDate(facture.getCreatedDate());
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String formattedMonth = decimalFormat.format(month);

        File pathFolder = new File(pathFacture + year + "/" + formattedMonth);
        if (!pathFolder.exists()) {
          pathFolder.mkdirs();
          pathFolder.setWritable(true);

        }

        nomfile = pathFacture + year + "/" + formattedMonth + "/" + PrefixDocument.NOMEFILE_FACTURE
            + facture.getRef_facture() + ".pdf";

        JasperExportManager.exportReportToPdfFile(jasperPrint, nomfile);
        file1 = ResourceUtils.getFile(nomfile);

      } catch (JRException e) {
        // TODO Auto-generated catch block
        LOGGER.warn("error creation facture: " + e.getMessage());

      }
    }
    return file1;
  }

  @Override
  public List<Facture> findnonpayerfacture(String recherche, Long telephone) {

    return factureRepository.findnonpayerfacture(recherche, telephone);
  }

  public List<Recouvrement> findrecouvrementliste(int pageNo, int pageSize, String sortt,
      String orderdir) {

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.factureRepository.findrecouvrement(pageable);
  }

  public FactureNonPayee findmontantFactureNonPayee() {
    return this.factureRepository.findmontantFactureNonPayee();
  }

  public void sendSmsGenerateNewFacture(ArrayList<Map<String, Object>> smsToSend) {
    Smstemplate findtemplatesms =
        templatesmsRepository.findSmstemplateByname("generationfacturesSMS");
    String Template = findtemplatesms.getTemplate();

    /*
     * ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
     * 
     * for (int i = 0; i < numerosTelephone.size(); i++) { String NewTemplate =
     * Template.replace("{numfacture}", numeroFixeAbonnement.get(i).toString()); Map<String, Object>
     * Message = new HashMap<String, Object>(); Message.put("number", numerosTelephone.get(i));
     * Message.put("message", NewTemplate); smsToSend.add(Message);
     * 
     * }
     */
    notificationservice.sendsmsnotification(smsToSend);
  }

  @Override
  public Facture ChekIfIsFirstFactureExist(Long clientid) {
    // TODO Auto-generated method stub
    return this.factureRepository.ChekIfIsFirstFactureExist(clientid);
  }

  @Override
  public Facture findFirstByAbonnement_clientid(Long demandid) {
    // TODO Auto-generated method stub
    return this.factureRepository.findFirstByAbonnement_clientid(demandid);
  }

  public HashMap<String, Object> ObjectTarificationFacturation(Tarification tarification,
      int nombreMois) {
    HashMap<String, Object> ObjectTarificationFacturation = new HashMap<String, Object>();
    ObjectTarificationFacturation.put("prixTTCProduit", tarification.getPrixTTc() * nombreMois);
    ObjectTarificationFacturation.put("prixUnitaireProduit", tarification.getPrixUnitaire());
    ObjectTarificationFacturation.put("prixTvaProduit",
        (tarification.getPrixUnitaire() * (tarification.getTaxe() * 0.01)) * nombreMois);
    return ObjectTarificationFacturation;

  }

  @ResponseBody
  @PostMapping(value = "/facture/addall")
  public List<Long> addAllIdToExport(@RequestBody String filterrecherche,
      HttpServletRequest request, boolean isProformat) {

    String ref_facture = null;
    Double montantMinimum = null;
    Double montantMaximum = null;
    Boolean status = null;
    Date dateCreationDebut = null;
    Date dateCreationFin = null;
    Date dateEcheanceDebut = null;
    Date dateEcheanceFin = null;
    String codeClient = null;
    Long telFixe = null;
    String cin = null;
    Date datePayementDebut = null;
    Date datePayementFin = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("ref_facture"), "")
          && obj.getString("ref_facture") != null) {
        ref_facture = obj.getString("ref_facture").trim();
      }
      if (!Objects.equals(obj.getString("montantMinimum"), "")
          && obj.getString("montantMinimum") != null) {
        montantMinimum = Double.parseDouble(obj.getString("montantMinimum").trim());
      }
      if (!Objects.equals(obj.getString("montantMaximum"), "")
          && obj.getString("montantMaximum") != null) {
        montantMaximum = Double.parseDouble(obj.getString("montantMaximum").trim());
      }
      if (!Objects.equals(obj.getString("status"), "") && obj.getString("status") != null) {
        status = obj.getString("status").trim().equals("0") ? false : true;
      }
      if (!Objects.equals(obj.getString("dateCreationDebut"), "")
          && obj.getString("dateCreationDebut") != null) {
        String dateBefaureConverted = obj.getString("dateCreationDebut");
        dateCreationDebut = CrmUtils.convertStringToLocalDataTimeStart(dateBefaureConverted);
      }
      if (!Objects.equals(obj.getString("dateCreationFin"), "")
          && obj.getString("dateCreationFin") != null) {
        String dateBefaureConverted = obj.getString("dateCreationFin");
        dateCreationFin = CrmUtils.convertStringToLocalDateTime(dateBefaureConverted);
      }
      if (!Objects.equals(obj.getString("dateEcheanceDebut"), "")
          && obj.getString("dateEcheanceDebut") != null) {
        dateEcheanceDebut = CrmUtils.convertStringToDate(obj.getString("dateEcheanceDebut"));
      }
      if (!Objects.equals(obj.getString("dateEcheanceFin"), "")
          && obj.getString("dateEcheanceFin") != null) {
        dateEcheanceFin = CrmUtils.convertStringToDate(obj.getString("dateEcheanceFin"));
      }
      if (!Objects.equals(obj.getString("codeClient"), "") && obj.getString("codeClient") != null) {
        codeClient = obj.getString("codeClient").trim();
      }
      if (!Objects.equals(obj.getString("numeroFixe"), "") && obj.getString("numeroFixe") != null) {
        telFixe = Long.parseLong(obj.getString("numeroFixe").trim());
      }
      if (!Objects.equals(obj.getString("identifientClient"), "")
          && obj.getString("identifientClient") != null) {
        cin = obj.getString("identifientClient").trim();
      }
      if (!Objects.equals(obj.getString("datePayementDebut"), "")
          && obj.getString("datePayementDebut") != null) {
        datePayementDebut =
            CrmUtils.convertStringToLocalDataTimeStart(obj.getString("datePayementDebut").trim());
      }
      if (!Objects.equals(obj.getString("datePayementFin"), "")
          && obj.getString("datePayementFin") != null) {
        datePayementFin =
            CrmUtils.convertStringToLocalDateTime(obj.getString("datePayementFin").trim());
      }
    }

    List<Long> listesdesIds = new ArrayList<>();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    Long idconnected = user.getUserid();

    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    if (StringsRole.contains("READ_INVOICE_ALL")) {
      listesdesIds = this.factureRepository.findAllToExportWithFiltreAdminUser(null, ref_facture,
          montantMinimum, montantMaximum, status, dateCreationDebut, dateCreationFin,

          dateEcheanceDebut, dateEcheanceFin, codeClient, telFixe, cin, datePayementDebut,
          datePayementFin, isProformat);


    } else if (StringsRole.contains("READ_FACTURE_PERSO")) {
      // factures =fRepo.listFactureByConnecteduser(idconnected);

      listesdesIds = this.factureRepository.findAllToExportWithFiltreOtherUser(true, ref_facture,
          montantMinimum, montantMaximum, status, dateCreationDebut, dateCreationFin,
          dateEcheanceDebut, dateEcheanceFin, codeClient, telFixe, cin);

    }

    request.getSession().setAttribute("liste_facture_ids", listesdesIds);
    listesdesIds.add((long) 0);
    return listesdesIds;
  }

  @ResponseBody
  @PostMapping(value = "/facture/removeall")
  public JsonResponseBody removeAllFromListExport(HttpServletRequest request) {
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("liste_facture_ids");
    LOGGER.info("checkliste_des_ids_facture_a_removeall: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      LOGGER.info("liste des ids est vide ");
      List<Long> listesdesIds = new ArrayList<>();
      JsonResponseBody jrb = new JsonResponseBody();
      jrb.setCode(String.valueOf(200));
      jrb.setMsg("liste des ids est vide");
      jrb.setResult(listesdesIds);
      return jrb;
    } else {
      List<Long> listedesIds = (List<Long>) request.getSession().getAttribute("liste_facture_ids");
      listedesIds.clear();
      LOGGER.info("nouvelle liste session attribut des id: " + listedesIds);
      request.getSession().setAttribute("liste_facture_ids", listedesIds);
      JsonResponseBody jrb1 = new JsonResponseBody();
      jrb1.setCode(String.valueOf(200));
      jrb1.setMsg("Vider liste avec succes");
      jrb1.setResult(listedesIds);
      return jrb1;
    }
  }

  @ResponseBody
  @PostMapping(value = "/facture/addid")
  public JsonResponseBody addfiled(@RequestBody Long id, HttpServletRequest request) {
    LOGGER.info("id: " + id);
    Object checkliste = request.getSession().getAttribute("liste_facture_ids");
    LOGGER.info("checkliste_des_ids_facture_a_exporter: " + checkliste);
    List<Long> listesdesIds = new ArrayList<>();
    if (checkliste == null || checkliste.equals("")) {
      if (id == null || id.equals("")) {
        request.getSession().setAttribute("liste_facture_ids", listesdesIds);
        LOGGER.info("listedes_ids facture if: " + listesdesIds);
      } else {
        listesdesIds.add(id);
        request.getSession().setAttribute("liste_facture_ids", listesdesIds);
      }

    } else {
      listesdesIds = (List<Long>) request.getSession().getAttribute("liste_facture_ids");
      LOGGER.info("listedes_ids facture else: " + listesdesIds.contains(id));
      if (listesdesIds.contains(id) == false) {
        listesdesIds.add(id);
      }

      LOGGER.info("listedes_ids else: " + listesdesIds);
      request.getSession().setAttribute("liste_facture_ids", listesdesIds);
    }
    JsonResponseBody jrb = new JsonResponseBody();
    jrb.setCode(String.valueOf(200));
    jrb.setMsg("Ajout de l'id à liste avec succes");
    jrb.setResult(listesdesIds);
    return jrb;
  }


  public JsonResponseBody removefiled(@RequestBody Long id, HttpServletRequest request) {
    LOGGER.info("removefiled from session attribut id: " + id);
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("liste_facture_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      LOGGER.info("liste des ids est vide ");
      List<Long> listesdesIds = new ArrayList<>();
      JsonResponseBody jrb = new JsonResponseBody();
      jrb.setCode(String.valueOf(200));
      jrb.setMsg("liste des ids facture est vide");
      jrb.setResult(listesdesIds);
      return jrb;
    } else {
      List<Long> listedesIds = (List<Long>) request.getSession().getAttribute("liste_facture_ids");
      int pos = listedesIds.indexOf(id);
      listedesIds.remove(pos);
      LOGGER.info("nouvelle liste session attribut des id facture : " + listedesIds);
      request.getSession().setAttribute("liste_facture_ids", listedesIds);

      JsonResponseBody jrb1 = new JsonResponseBody();
      jrb1.setCode(String.valueOf(200));
      jrb1.setMsg("Suppression de l'id de liste avec succes");
      jrb1.setResult(listedesIds);
      return jrb1;
    }
  }

  @Override
  public List<ListeFactureNonPayeDTO> findListeFactureNonPayeeByCin(String recherche) {
    List<Facture> factures = factureRepository.findListeFactureNonPayeeByCin(recherche);
    List<ListeFactureNonPayeDTO> dtos = converter.convertToListDTO(factures);
    return dtos;

  }

  @Override
  public Facture findFactureNonPayeeByReference(String referenceFacture) {
    // TODO Auto-generated method stub
    return factureRepository.findFactureNonPayeeByReference(referenceFacture);
  }

  @Override
  public Facture findFacturePayeeByReference(String referenceFacture) {
    return factureRepository.findFacturePayeeByReference(referenceFacture);
  }

  @Override
  public ListeFactureNonPayeDTO findListeFactureNonPayeByReference(String recherche) {
    // TODO Auto-generated method stub
    Facture facture = factureRepository.findFactureNonPayeeByReference(recherche);
    ListeFactureNonPayeDTO FactureNonPayeDTO = converter.convert(facture);
    return FactureNonPayeDTO;

  }

  @Override
  public List<Facture> findListeFactureNonPayeeByRefFacture(List<String> factures) {
    // TODO Auto-generated method stub
    return factureRepository.findListeFactureNonPayeeByRefFacture(factures);
  }

  @Override
  public List<ListeFactureNonPayeDTO> findListeFactureNonPayeeByFixeNumber(Long telephone) {
    // TODO Auto-generated method stub
    List<Facture> factures = factureRepository.findListeFactureNonPayeeByFixeNumber(telephone);
    List<ListeFactureNonPayeDTO> FactureNonPayeDTO = converter.convertToListDTO(factures);
    return FactureNonPayeDTO;
  }

  @Override
  public Double getSumFactureNonPayee(Long clientid) {
    // TODO Auto-generated method stub
    return factureRepository.getSumFactureNonPayee(clientid);
  }

  @Override
  public ModelAndView exportToExcelRecouvrement(HttpServletRequest request) {
    // TODO Auto-generated method stub
    ModelAndView mav = new ModelAndView();
    try {
      List<Recouvrement> myList = factureRepository.findallrecouvrement();
      if (myList.size() > 0) {
        mav.setView(new ExportExcelRecouvrement());
        mav.addObject("list", myList);
      }
    } catch (Exception e) {
      LOGGER.error(" facture recouvrement  Error:" + e);

    }
    return mav;
  }

  @Override
  public Long findAbonnementByListFacture(List<String> facturelist) {
    // TODO Auto-generated method stub
    return factureRepository.findAbonnementByListFacture(facturelist);
  }

  public List<Facture> getFacturesByClientAndIsFactureResilation(Long Clientid,
      Boolean IsFactureResilation) {
    // TODO Auto-generated method stub
    return factureRepository.getFacturesByAbonnement_clientidAndIsFactureResilation(Clientid,
        IsFactureResilation);

  }


  @Override
  public Page<MoreThanOneInvoiceRecap> getTotalSumsFacturesNonPayerByClients(int pageNo,
      int pageSize, String filterrecherche) {
    String statusFilt = "";
    String prenom = null;
    String nom = null;
    String cin = null;
    String reference = null;
    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("firstName"), "") && obj.getString("firstName") != null) {
        prenom = obj.getString("firstName");
      }
      if (!Objects.equals(obj.getString("lastName"), "") && obj.getString("lastName") != null) {
        nom = obj.getString("lastName");
      }
      if (!Objects.equals(obj.getString("reference"), "") && obj.getString("reference") != null) {
        reference = obj.getString("reference");
      }
      if (!Objects.equals(obj.getString("cin"), "") && obj.getString("cin") != null) {
        cin = obj.getString("cin");
      }
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    Page<MoreThanOneInvoiceRecap> ddd = factureRepository
        .getTotalSumsFacturesNonPayerByClients(prenom, nom, cin, reference, pageable);
    return ddd;


  }

  @Override
  public Page<Map<String, Object>> getAllFacturesByClientForMobile(Long Clientid,
      Pageable pageable) {
    return factureRepository.findByAbonnement_clientidForMobile(Clientid, pageable);
  }

  public void updateVisibilityFirstFacture(Long clientId) {
    // TODO Auto-generated method stub
    factureRepository.updateVisibilityFirstFacture(clientId);

  }

  @Override
  public List<Facture> getFacturesByClientAndEtat_facture(Long Clientid, Boolean IsFacturePayed) {
    // TODO Auto-generated method stub
    return factureRepository.getFacturesByClientAndEtat_facture(Clientid, IsFacturePayed);
  }

  public List<Map<String, Object>> getClientsFacturesImpayeesRetard3Jours() {
    return factureRepository.getClientsFacturesImpayeesRetard3Jours();
  }
}
