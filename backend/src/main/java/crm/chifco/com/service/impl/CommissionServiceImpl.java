package crm.chifco.com.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.DTOclass.CalculeCommisionDemande;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AvanceCommissionAcquisition;

import crm.chifco.com.DTOclass.CommissionDemDash;

import crm.chifco.com.model.Commission;
import crm.chifco.com.model.DemandeCommission;
import crm.chifco.com.model.DemandeCommissionGroup;
import crm.chifco.com.model.DetailsCommissionDemande;
import crm.chifco.com.model.DetailsCommissionFacture;
import crm.chifco.com.model.DetailsCommissionPremiereFacture;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.OffreCommission;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AvanceCommissionAcquisitionRepository;
import crm.chifco.com.repository.BordereaurRepository;
import crm.chifco.com.repository.CommissionRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.DemandeCommissionGroupRepository;
import crm.chifco.com.repository.DemandeCommissionRepository;
import crm.chifco.com.repository.DetailsCommissionDemandeRepository;
import crm.chifco.com.repository.DetailsCommissionFactureMiseEnServiceRepository;
import crm.chifco.com.repository.DetailsCommissionFactureRepository;
import crm.chifco.com.repository.DetailsCommissionPremiereFactureRepository;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.OffreCommissionRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.CommissionExcelExport;
import crm.chifco.com.service.CommissionService;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.StatutAvanceBordereau;

@Service
public class CommissionServiceImpl implements CommissionService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  CommissionRepository commissionRepository;
  
  @Autowired
  AbonnementRepository abonnementRepository;

  @Autowired
  private DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  private EncaissementRepository encaissementRepository;

  @Autowired
  private FactureRepository factureRepository;

  @Autowired
  private DemandeCommissionRepository demandeCommissionRepository;

  @Autowired
  private OffreCommissionRepository offreCommissionRepository;

  @Autowired
  UserRepository userRepository;


  @Value("${commission.acquisition.demande.centplus10Mo}")
  String commissionAcquisitionDemandeCentPlus10Mo;

  @Value("${commission.acquisition.demande.centplus20Mo}")
  String commissionAcquisitionDemandeCentPlus20Mo;

  @Value("${commission.acquisition.demande.centplus30Mo}")
  String commissionAcquisitionDemandeCentPlus30Mo;

  @Value("${commission.acquisition.demande.centplus50Mo}")
  String commissionAcquisitioDemandeCentplus50Mo;

  @Value("${commission.acquisition.demande.centplus100Mo}")
  String commissionAcquisitionDemandeCentplus100Mo;

  @Value("${commision.acquisition.demande.0.49.10Mo}")
  String commisionAcquisitionDemande10MoPalier0A49;

  @Value("${commision.acquisition.demande.50.99.10Mo}")
  String commisionAcquisitionDemande10MoPalier50A99;

  @Value("${commision.acquisition.demande.0.49.20Mo}")
  String commisionAcquisitionDemande20MoPalier0A49;

  @Value("${commision.acquisition.demande.50.99.20Mo}")
  String commisionAcquisitionDemande20MoPalier50A99;

  @Value("${commision.acquisition.demande.0.49.30Mo}")
  String commisionAcquisitionDemande30MoPalier0A49;

  @Value("${commision.acquisition.demande.50.99.30Mo}")
  String commisionAcquisitionDemande30MoPalier50A99;

  @Value("${commision.acquisition.demande.0.49.50Mo}")
  String commisionAcquisitionDemande50MoPalier0A49;

  @Value("${commision.acquisition.demande.50.99.50Mo}")
  String commisionAcquisitionDemande50MoPalier50A99;

  @Value("${commision.acquisition.demande.0.49.100Mo}")
  String commisionAcquisitionDemande100MoPalier0A49;

  @Value("${commision.acquisition.demande.50.99.100Mo}")
  String commisionAcquisitionDemande100MoPalier50A99;

  /* fin variable commision sur demande */
  @Value("${commission.acquisition.activation.12Mo.0.49.echance}")
  String commissionAcquisitionActivatio12MoPalier0A49EnEchance;

  @Value("${commission.acquisition.activation.12Mo.0.49.hechance}")
  String commissionAcquisitionActivation12MoPalier0A49EnHechance;

  @Value("${commission.acquisition.activation.12Mo.50.99.echance}")
  String commissionAcquisitionActivation12MoPalier50A99EnEchance;

  @Value("${commission.acquisition.activation.12Mo.50.99.hechance}")
  String commissionAcquisitionActivation12MoPalier50A99EnHechance;

  @Value("${commission.acquisition.activation.12Mo.100.echance}")
  String commissionAcquisitionActivation12MoPalier100EnEchance;

  @Value("${commission.acquisition.activation.12Mo.100.hechance}")
  String commissionAcquisitionActivation12MoPalier100EnHechance;

  @Value("${commission.acquisition.activation.20Mo.0.49.echance}")
  String commissionAcquisitionActivation20MoPalier0A49EnEchance;

  @Value("${commission.acquisition.activation.20Mo.0.49.hechance}")
  String commissionAcquisitionActivation20MoPalier0A49Enhechance;

  @Value("${commission.acquisition.activation.20Mo.50.99.echance}")
  String commissionAcquisitionActivation20MoPalier50A99Enechance;

  @Value("${commission.acquisition.activation.20Mo.50.99.hechance}")
  String commissionAcquisitionActivation20MoPalier50A99EnHechance;

  @Value("${commission.acquisition.activation.20Mo.100.echance}")
  String commissionAcquisitionActivation20MoPalier100Echance;

  @Value("${commission.acquisition.activation.20Mo.100.hechance}")
  String commissionAcquisitionActivation20MoPalier100Hechance;

  @Value("${commission.acquisition.activation.30Mo.0.49.echance}")
  String commissionAcquisitionActivation30MoPalier0A49EnEchance;

  @Value("${commission.acquisition.activation.30Mo.0.49.hechance}")
  String commissionAcquisitionActivation30MoPalier0A49EnHechance;

  @Value("${commission.acquisition.activation.30Mo.50.99.echance}")
  String commissionAcquisitionActivation30MoPalier50A99EnEchance;

  @Value("${commission.acquisition.activation.30Mo.50.99.hechance}")
  String commissionAcquisitionActivation30MoPalier50En99EnHechance;

  @Value("${commission.acquisition.activation.30Mo.100.echance}")
  String commissionAcquisitionActivation30MoPalier100EnEchance;

  @Value("${commission.acquisition.activation.30Mo.100.hechance}")
  String commissionAcquisitionActivation30MoPalier100EnHechance;

  @Value("${commission.acquisition.activation.50Mo.0.49.echance}")
  String commissionAcquisitionActivation50MoPalier0A49EnEchance;

  @Value("${commission.acquisition.activation.50Mo.0.49.hechance}")
  String commissionAcquisitionActivation50MoPalier0A49EnHechance;

  @Value("${commission.acquisition.activation.50Mo.50.99.echance}")
  String commissionAcquisitionActivation50MoPalier50A99EnEchance;

  @Value("${commission.acquisition.activation.50Mo.50.99.hechance}")
  String commissionAcquisitionActivation50MoPalier50A99EnHechance;

  @Value("${commission.acquisition.activation.50Mo.100.echance}")
  String commissionAcquisitionActivation50MoPalier100EnEchance;

  @Value("${commission.acquisition.activation.50Mo.100.hechance}")
  String commissionAcquisitionActivation50MoPalier100EnHechance;

  @Value("${commission.acquisition.activation.100Mo.0.49.echance}")
  String commissionAcquisitionActivation100MoPalier0A49EnEchance;

  @Value("${commission.acquisition.activation.100Mo.0.49.hechance}")
  String commissionAcquisitionActivationPalier100MoPalier0A49EnHechance;

  @Value("${commission.acquisition.activation.100Mo.50.99.echance}")
  String commissionAcquisitionActivation100MoPalier50A99EnEchance;

  @Value("${commission.acquisition.activation.100Mo.50.99.hechance}")
  String commissionAcquisitionActivation100MoPalier50A99EnHechance;

  @Value("${commission.acquisition.activation.100Mo.100.echance}")
  String commissionAcquisitionActivation100MoPalier100EnEchance;

  @Value("${commission.acquisition.activation.100Mo.100.hechance}")
  String commissionAcquisitionActivation100MoPalier100Hechance;

  /* fin variable commision activation demande */
  @Value("${commission.paiement.echance12Mo.pourcent}")
  String commissionPaiementEnEchancePourcentFor12Mo;

  @Value("${commission.paiement.hechance12Mo.pourcent}")
  String commissionPaiementEnHechancePourcentFor12Mo;

  @Value("${commission.paiement.echance20Mo.pourcent}")
  String commissionPaiementEnEchancePourcentFor20Mo;

  @Value("${commission.paiement.hechance20Mo.pourcent}")
  String commissionPaiementEnHechancePourcentFor20Mo;

  @Value("${commission.paiement.echance30Mo.pourcent}")
  String commissionPaiementEnEchancePourcentFor30Mo;

  @Value("${commission.paiement.hechance30Mo.pourcent}")
  String commissionPaiementEnHechancePourcentFor30Mo;

  @Value("${commission.paiement.echance50Mo.pourcent}")
  String commissionPaiementEnEchancePourcentFor50Mo;

  @Value("${commission.paiement.hechance50Mo.pourcent}")
  String commissionPaiementEnHechancePourcentFor50Mo;

  @Value("${commission.paiement.echance100Mo.pourcent}")
  String commissionPaiementEnEchancePourcentFor100Mo;

  @Value("${commission.paiement.hechance100Mo.pourcent}")
  String commissionPaiementEnHechancePourcentFor100Mo;
  /* fin variable commision en echance demande */

  /* nouvaux palier demande abonnement commision */
  @Value("${commission.acquisition.demande.deuxCentplus10Mo}")
  String commisionAcquisitionDemande10MoPalierdeuxCentplus;

  @Value("${commission.acquisition.demande.deuxCentplus20Mo}")
  String commisionAcquisitionDemande20MoPalierdeuxCentplus;

  @Value("${commission.acquisition.demande.deuxCentplus30Mo}")
  String commisionAcquisitionDemande30MoPalierdeuxCentplus;

  @Value("${commission.acquisition.demande.deuxCentplus50Mo}")
  String commisionAcquisitionDemande50MoPalierdeuxCentplus;

  @Value("${commission.acquisition.demande.deuxCentplus100Mo}")
  String commisionAcquisitionDemande100MoPalierdeuxCentplus;

  @Value("${commission.acquisition.demande.troisCentplus10Mo}")
  String commisionAcquisitionDemande10MoPaliertroisCentplus;

  @Value("${commission.acquisition.demande.troisCentplus20Mo}")
  String commisionAcquisitionDemande20MoPaliertroisCentplus;

  @Value("${commission.acquisition.demande.troisCentplus30Mo}")
  String commisionAcquisitionDemande30MoPaliertroisCentplus;

  @Value("${commission.acquisition.demande.troisCentplus50Mo}")
  String commisionAcquisitionDemande50MoPaliertroisCentplus;

  @Value("${commission.acquisition.demande.troisCentplus100Mo}")
  String commisionAcquisitionDemande100MoPaliertroisCentplus;

  /* nouvaux palier 1er facture commision */
  @Value("${commission.acquisition.activation.12Mo.200.echance}")
  String commissionAcquisitionActivation12MoPalier200EnEchance;

  @Value("${commission.acquisition.activation.12Mo.200.hechance}")
  String commissionAcquisitionActivation12MoPalier200EnHechance;

  @Value("${commission.acquisition.activation.12Mo.300.echance}")
  String commissionAcquisitionActivation12MoPalier300EnEchance;

  @Value("${commission.acquisition.activation.12Mo.300.hechance}")
  String commissionAcquisitionActivation12MoPalier300EnHechance;


  @Value("${commission.acquisition.activation.20Mo.200.echance}")
  String commissionAcquisitionActivation20MoPalier200EnEchance;

  @Value("${commission.acquisition.activation.20Mo.200.hechance}")
  String commissionAcquisitionActivation20MoPalier200EnHechance;

  @Value("${commission.acquisition.activation.20Mo.300.echance}")
  String commissionAcquisitionActivation20MoPalier300EnEchance;
  @Value("${commission.acquisition.activation.20Mo.300.hechance}")
  String commissionAcquisitionActivation20MoPalier300EnHechance;


  @Value("${commission.acquisition.activation.30Mo.200.hechance}")
  String commissionAcquisitionActivation30MoPalier200EnHechance;

  @Value("${commission.acquisition.activation.30Mo.200.echance}")
  String commissionAcquisitionActivation30MoPalier200EnEechance;

  @Value("${commission.acquisition.activation.30Mo.300.echance}")
  String commissionAcquisitionActivation30MoPalier300EnEechance;

  @Value("${commission.acquisition.activation.30Mo.300.hechance}")
  String commissionAcquisitionActivation30MoPalier300EnHechance;

  @Value("${commission.acquisition.activation.50Mo.200.echance}")
  String commissionAcquisitionActivation50MoPalier200EnEchance;

  @Value("${commission.acquisition.activation.50Mo.200.hechance}")
  String commissionAcquisitionActivation50MoPalier200EnHechance;

  @Value("${commission.acquisition.activation.50Mo.300.echance}")
  String commissionAcquisitionActivation50MoPalier300EnEchance;

  @Value("${commission.acquisition.activation.50Mo.300.hechance}")
  String commissionAcquisitionActivation50MoPalier300EnHechance;

  @Value("${commission.acquisition.activation.100Mo.200.echance}")
  String commissionAcquisitionActivation100MoPalier200EnEchance;

  @Value("${commission.acquisition.activation.50Mo.200.hechance}")
  String commissionAcquisitionActivation100MoPalier200EnHechance;

  @Value("${commission.acquisition.activation.100Mo.300.echance}")
  String commissionAcquisitionActivation100MoPalier300EnEchance;

  @Value("${commission.acquisition.activation.100Mo.300.hechance}")
  String commissionAcquisitionActivation100MoPalier300EnHechance;

  @Autowired
  private BordereaurRepository bordereaurRepository;


  @Autowired
  private DetailsCommissionDemandeRepository detailsCommissionDemandeRepository;


  @Autowired
  private DetailsCommissionFactureRepository detailsCommissionFactureRepository;

  @Autowired
  private DetailsCommissionPremiereFactureRepository detailsCommissionPremiereFactureRepository;

  @Autowired
  private crm.chifco.com.repository.DetailsRetardCommissionPremiereFactureRepository DetailsRetardCommissionPremiereFactureRepository;
  
  @Autowired
  AvanceCommissionAcquisitionRepository avanceCommissionAcquisitionRepository;

  @Autowired
  private DetailsCommissionFactureMiseEnServiceRepository detailsCommissionFactureMiseEnServiceRepository;

  
  
  @Autowired
  DemandeCommissionGroupRepository demandeCommissionGroupRepository;

  @Override
  public HashMap<String, Object> getAll(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, Boolean isCommissionAdmin,
      Boolean isCommissionArea, Long idUserConnected) {
    // TODO Auto-generated method stub

    String codeRevendeur = null;
    Integer numMois = null;
    Integer annee = null;
    String statut = null;
    Date startCreatedDate = null;
    Date endCreatedDate = null;
    String reference = null;
    Boolean typeC = null ;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("date"), "") && obj.getString("date") != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(obj.getString("date").trim(), formatter);

        annee = yearMonth.getYear();
        numMois = yearMonth.getMonth().getValue();
      }
      if (!Objects.equals(obj.getString("codeRevendeur"), "")
          && obj.getString("codeRevendeur") != null) {
        codeRevendeur = obj.getString("codeRevendeur").trim();
      }
      if (!Objects.equals(obj.getString("statut"), "") && obj.getString("statut") != null) {
        statut = obj.getString("statut").trim();
      }
      if (!Objects.equals(obj.getString("startCreatedDate"), "")
          && obj.getString("startCreatedDate") != null) {
        startCreatedDate = CrmUtils.convertStringToDate(obj.getString("startCreatedDate"));
      }
      if (!Objects.equals(obj.getString("endCreatedDate"), "")
          && obj.getString("endCreatedDate") != null) {
        endCreatedDate = CrmUtils.convertStringToLocalDateTime(obj.getString("endCreatedDate"));
      }
      if (!Objects.equals(obj.getString("reference"), "") && obj.getString("reference") != null) {
        reference = obj.getString("reference").trim();
      }
      if (!Objects.equals(obj.getString("type"), "") && obj.getString("type") != null) {
    	  typeC = obj.getBoolean("type");
        }
    }

    int currentpage = start / length;
    Page<Commission> responseData = null;
    HashMap<String, Object> myHmapData = new HashMap<>();

    Sort sort = Sort.by(Sort.Direction.DESC, "id");
    Pageable pageable = PageRequest.of(currentpage, length, sort);

    if (isCommissionAdmin == true) {
      responseData = commissionRepository.getAllCommission(pageable, annee, numMois, codeRevendeur,
          statut, startCreatedDate, endCreatedDate, reference , typeC);
    } else if (isCommissionArea) {
      responseData = commissionRepository.getAllCommissionArea(pageable, annee, numMois,
          codeRevendeur, statut, startCreatedDate, endCreatedDate, reference, idUserConnected);
    }


    if (responseData != null) {
      myHmapData.put("data", responseData.getContent());
      myHmapData.put("recordsTotal", responseData.getTotalElements());
      myHmapData.put("recordsFiltered", responseData.getTotalElements());
    }
    myHmapData.put("draw", draw);
    myHmapData.put("start", start);

    return myHmapData;
  }

  @Override
  public Optional<Commission> getDetailsCommission(Long id) {
    // TODO Auto-generated method stub
    Optional<Commission> commission =
        Optional.ofNullable(commissionRepository.findById(id).orElse(null));
    return commission;
  }

  @Override
  public HashMap<String, Object> getAllByRevendeurId(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, Long idRevendeur) {
    // TODO Auto-generated method stub

    Integer numMois = null;
    Integer annee = null;
    String statut = null;
    String reference = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("date"), "") && obj.getString("date") != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(obj.getString("date").trim(), formatter);

        annee = yearMonth.getYear();
        numMois = yearMonth.getMonth().getValue();
      }
      if (!Objects.equals(obj.getString("statut"), "") && obj.getString("statut") != null) {
        statut = obj.getString("statut").trim();
      }
      if (!Objects.equals(obj.getString("reference"), "") && obj.getString("reference") != null) {
        reference = obj.getString("reference").trim();
      }
    }

    int currentpage = start / length;
    Page<Commission> responseData = null;
    HashMap<String, Object> myHmapData = new HashMap<>();

    Sort sort = Sort.by(Sort.Direction.DESC, "id");
    Pageable pageable = PageRequest.of(currentpage, length, sort);

    responseData = commissionRepository.getAllCommissionByRev(pageable, idRevendeur, annee, numMois,
        statut, reference);

    if (responseData != null) {
      myHmapData.put("data", responseData.getContent());
      myHmapData.put("recordsTotal", responseData.getTotalElements());
      myHmapData.put("recordsFiltered", responseData.getTotalElements());
    }
    myHmapData.put("draw", draw);
    myHmapData.put("start", start);

    return myHmapData;
  }

  @Override
  public HashMap<String, Object> CalculeCommisionFront(Boolean isFreelancer , long UserId, String Date) {
    // TODO Auto-generated method stub

    HashMap<String, Object> result = new HashMap<>();


    HashMap<String, Object> hHapClaculeDemandeCommision = new HashMap<>();
    HashMap<String, Object> commissionPaiements = new HashMap<>();
    HashMap<String, Object> commissionFirstResults = new HashMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    YearMonth yearMonth = YearMonth.parse(Date.trim(), formatter);
    Integer numMois = null;
    Integer annee = null;
    annee = yearMonth.getYear();
    numMois = yearMonth.getMonth().getValue();
    Commission existeCommision =
        commissionRepository.findCommisionByUserIdAndNotIsPromo(UserId, annee, numMois);
    if (existeCommision == null) {
      hHapClaculeDemandeCommision = calculeCommDemande(isFreelancer,UserId, Date);

      commissionPaiements = commissionFacture(UserId, Date);
      commissionFirstResults = calculecommisionFirstFacture(UserId, Date);
      

    } else {

      hHapClaculeDemandeCommision.put("totalDemandes", existeCommision.getNbrTotalDemandes());
      hHapClaculeDemandeCommision.put("demandesAccepte", existeCommision.getNbrDemandesAcceptees());
      hHapClaculeDemandeCommision.put("demandesRejete", existeCommision.getNbrDemandesRejetees());
      hHapClaculeDemandeCommision.put("demandeEnAttente",
          existeCommision.getNbrDemandesEnAttente());
      hHapClaculeDemandeCommision.put("nbFirsFactureNonPayed",
          existeCommision.getNbrDemandesNonRealisee());
      
      hHapClaculeDemandeCommision.put("primeMs", existeCommision.getPrimeCommision());

      hHapClaculeDemandeCommision.put("total", existeCommision.getMontantCommissionDemandes());
      hHapClaculeDemandeCommision.put("detailsCommissionDemande",
          detailsCommissionDemandeRepository.findAllByCommissionId(existeCommision.getId()));
      /* commissionFirstResults */
      commissionFirstResults.put("nbCommissionActivation",
          existeCommision.getNbrDemandesActivees());
      commissionFirstResults.put("totalCommissions",
          existeCommision.getMontantCommissionPremiereFacture());

      commissionFirstResults.put("totalCommissionsAvancePayee",
          existeCommision.getMontantAvancePremiereFacture());
      commissionFirstResults.put("totalCommissionsSansAvance",
          existeCommision.getMontantTotalPremiereFacture());
      commissionFirstResults.put("detailsCommissionFirstFactures",
          detailsCommissionPremiereFactureRepository
              .findAllByCommissionId(existeCommision.getId()));
      commissionFirstResults.put("nbFirsFactureNonVerse", 0);

      commissionFirstResults.put("listeRetardPayement",
    		  DetailsRetardCommissionPremiereFactureRepository
                  .findAllByCommissionId(existeCommision.getId()));
          commissionFirstResults.put("totalRetardPayement",existeCommision.getMontantRetardPayemnt());
      /* commissionPaiements */
      commissionPaiements.put("nbFacturePayee", existeCommision.getNbrFacturesPayees());
      commissionPaiements.put("totalCommissionsPaiement",
          existeCommision.getMontantCommissionPaiements());
      commissionPaiements.put("detailsCommissionFacture",
          detailsCommissionFactureRepository.findAllByCommissionId(existeCommision.getId()));
      commissionPaiements.put("nombreFactureNonVerse",
          existeCommision.getNbrFacturesNonVerseePayement());

    }
    result.put("commissionActivation", commissionFirstResults);
    result.put("commissionPaiements", commissionPaiements);
    result.put("commissionDemandes", hHapClaculeDemandeCommision);

    return result;
  }

  public HashMap<String, Object> calculeCommDemande(Boolean IsRole ,Long userId, String date) {
    LOGGER.info("Début de calculeCommDemande...");
    LOGGER.debug("Paramètres - UserId: {}, Date: {}", userId, date);

    // calculeCommDemande = new CalculeCommDemande(null, null, null);
    Long countPrime = 0L ; 



    List<DetailsCommissionDemande> listDetailsCommissionDemandes = new ArrayList<>();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    YearMonth yearMonth = YearMonth.parse(date, formatter);

    LocalDate startOfMonth = yearMonth.atDay(1);
    LocalDate endOfMonth = yearMonth.atEndOfMonth();

    LOGGER.debug("Appel de demandeAbonnementRepository.calculeCommDemande(UserId)...");
    CalculeCommisionDemande calcule =
        demandeAbonnementRepository.calculeCommDemandeNotInCommisionPromo(userId,
            CrmUtils.convertStringToDate(startOfMonth.toString()),
            CrmUtils.convertStringToLocalDateTime(endOfMonth.toString()));
    Long totalDemandeAccepeter = demandeAbonnementRepository.calculeTotalDemandeAccepter(userId,
        CrmUtils.convertStringToDate(startOfMonth.toString()),
        CrmUtils.convertStringToLocalDateTime(endOfMonth.toString()));
    
    Long totalDemandeMiseService = demandeAbonnementRepository.calculeTotalDemandeAccepterByMiseEnService(userId,
            CrmUtils.convertStringToDate(startOfMonth.toString()),
            CrmUtils.convertStringToLocalDateTime(endOfMonth.toString()));
    User userConnected = userRepository.findUsersByUserid(userId);
    HashMap<String, Object> result = new HashMap<>();
    if (calcule != null) {
      Double toTaleCommision = 0.0;
      result.put("totalDemandes", calcule.getCountAllDemandeClassification().toString());
      result.put("demandesAccepte", calcule.getCountAllDemandeAccecpted().toString());
      result.put("demandesRejete", calcule.getCountAllDemandeRejected().toString());
      result.put("demandeEnAttente", calcule.getCountAllDemandeEnAttente().toString());
      int totalRefus = 100;
      int totalActivation = 20;

      if (userConnected.getPcActivationCommision() != null) {
        totalActivation = userConnected.getPcActivationCommision().intValue();
      }

      if (userConnected.getPcRefusCommision() != null) {
        totalRefus = userConnected.getPcRefusCommision().intValue();
      }
      Long TwentyPercentOfDemandeRejacted =
          (calcule.getCountAllDemandeClassification() * totalRefus / 100);
      Double TwentyFivePrecentOfDemandeRealised =
          (calcule.getCountAllDemandeAccecpted() * totalActivation) / 100.0;

      LOGGER.debug("TwentyPercentOfDemandeRejacted: {}", TwentyPercentOfDemandeRejacted);

      if (TwentyPercentOfDemandeRejacted >= calcule.getCountAllDemandeRejected()) {
        if (TwentyFivePrecentOfDemandeRealised <= calcule.getCountDemandePasseToActivate()) {
          LOGGER.debug("Calcul des commissions en fonction de la décision...");

          List<OffreCommission> listOffreCommissions =
              offreCommissionRepository.findAllByAnneeAndMoisAndIsActive(yearMonth.getYear(),
                  yearMonth.getMonth().getValue(), true);

          // Montant offre commission
          Double prix0_49_10Mbps = rechercherPrix(listOffreCommissions, 10, 0, 49);
          Double prix50_99_10Mbps = rechercherPrix(listOffreCommissions, 10, 50, 99);
          Double prixPlus100_10Mbps = rechercherPrix(listOffreCommissions, 10, 100, 199);
          Double prixPlus200_10Mbps = rechercherPrix(listOffreCommissions, 10, 200, 299);
          Double prixPlus300_10Mbps = rechercherPrix(listOffreCommissions, 10, 300, null);

          Double prix0_49_20Mbps = rechercherPrix(listOffreCommissions, 20, 0, 49);
          Double prix50_99_20Mbps = rechercherPrix(listOffreCommissions, 20, 50, 99);
          Double prixPlus100_20Mbps = rechercherPrix(listOffreCommissions, 20, 100, 199);
          Double prixPlus200_20Mbps = rechercherPrix(listOffreCommissions, 10, 200, 299);
          Double prixPlus300_20Mbps = rechercherPrix(listOffreCommissions, 10, 300, null);

          Double prix0_49_30Mbps = rechercherPrix(listOffreCommissions, 30, 0, 49);
          Double prix50_99_30Mbps = rechercherPrix(listOffreCommissions, 30, 50, 99);
          Double prixPlus100_30Mbps = rechercherPrix(listOffreCommissions, 30, 100, 199);
          Double prixPlus200_30Mbps = rechercherPrix(listOffreCommissions, 10, 200, 299);
          Double prixPlus300_30Mbps = rechercherPrix(listOffreCommissions, 10, 300, null);

          Double prix0_49_50Mbps = rechercherPrix(listOffreCommissions, 50, 0, 49);
          Double prix50_99_50Mbps = rechercherPrix(listOffreCommissions, 50, 50, 99);
          Double prixPlus100_50Mbps = rechercherPrix(listOffreCommissions, 50, 100, 199);
          Double prixPlus200_50Mbps = rechercherPrix(listOffreCommissions, 10, 200, 299);
          Double prixPlus300_50Mbps = rechercherPrix(listOffreCommissions, 10, 300, null);

          Double prix0_49_100Mbps = rechercherPrix(listOffreCommissions, 100, 0, 49);
          Double prix50_99_100Mbps = rechercherPrix(listOffreCommissions, 100, 50, 99);
          Double prixPlus100_100Mbps = rechercherPrix(listOffreCommissions, 100, 100, 199);
          Double prixPlus200_100Mbps = rechercherPrix(listOffreCommissions, 10, 200, 299);
          Double prixPlus300_100Mbps = rechercherPrix(listOffreCommissions, 10, 300, null);

          if (totalDemandeAccepeter >= 300) {
            toTaleCommision = toTaleCommision
                + (prixPlus300_10Mbps * calcule.getCountDemandeAcceptedDebit10Or12())
                + (prixPlus300_20Mbps * calcule.getCountDemandeAcceptedDebit20())
                + (prixPlus300_30Mbps * calcule.getCountDemandeAcceptedDebit30())
                + (prixPlus300_50Mbps * calcule.getCountDemandeAcceptedDebit50())
                + (prixPlus300_100Mbps * calcule.getCountDemandeAcceptedDebit100());

          } else if (totalDemandeAccepeter >= 200 && totalDemandeAccepeter < 300) {
            toTaleCommision = toTaleCommision
                + (prixPlus200_10Mbps * calcule.getCountDemandeAcceptedDebit10Or12())
                + (prixPlus200_20Mbps * calcule.getCountDemandeAcceptedDebit20())
                + (prixPlus200_30Mbps * calcule.getCountDemandeAcceptedDebit30())
                + (prixPlus200_50Mbps * calcule.getCountDemandeAcceptedDebit50())
                + (prixPlus200_100Mbps * calcule.getCountDemandeAcceptedDebit100());

          } else if (totalDemandeAccepeter >= 100 && totalDemandeAccepeter < 200) {
            toTaleCommision = toTaleCommision
                + (prixPlus100_10Mbps * calcule.getCountDemandeAcceptedDebit10Or12())
                + (prixPlus100_20Mbps * calcule.getCountDemandeAcceptedDebit20())
                + (prixPlus100_30Mbps * calcule.getCountDemandeAcceptedDebit30())
                + (prixPlus100_50Mbps * calcule.getCountDemandeAcceptedDebit50())
                + (prixPlus100_100Mbps * calcule.getCountDemandeAcceptedDebit100());

          } else if (totalDemandeAccepeter >= 0 && totalDemandeAccepeter <= 49) {
            toTaleCommision =
                toTaleCommision + (prix0_49_10Mbps * calcule.getCountDemandeAcceptedDebit10Or12())
                    + (prix0_49_20Mbps * calcule.getCountDemandeAcceptedDebit20())
                    + (prix0_49_30Mbps * calcule.getCountDemandeAcceptedDebit30())
                    + (prix0_49_50Mbps * calcule.getCountDemandeAcceptedDebit50())
                    + (prix0_49_100Mbps * calcule.getCountDemandeAcceptedDebit100());

          } else if (totalDemandeAccepeter >= 50 && totalDemandeAccepeter <= 99) {
            toTaleCommision =
                toTaleCommision + (prix50_99_10Mbps * calcule.getCountDemandeAcceptedDebit10Or12())
                    + (prix50_99_20Mbps * calcule.getCountDemandeAcceptedDebit20())
                    + (prix50_99_30Mbps * calcule.getCountDemandeAcceptedDebit30())
                    + (prix50_99_50Mbps * calcule.getCountDemandeAcceptedDebit50())
                    + (prix50_99_100Mbps * calcule.getCountDemandeAcceptedDebit100());

          }

          if (toTaleCommision > 0) {
            List<Object[]> results =
                demandeAbonnementRepository.getDemandeDtailsCommissionNotInPromo(userId,
                    CrmUtils.convertStringToDate(startOfMonth.toString()),
                    CrmUtils.convertStringToLocalDateTime(endOfMonth.toString()));
            for (Object[] res : results) {

              String reference = (String) res[0];
              String statut = (String) res[1];
              String nomStatut = (String) res[2];

              DetailsCommissionDemande d = new DetailsCommissionDemande();
              d.setReferenceClient(reference);
              if (statut.equals("INSTALLED") || statut.equals("ASSIGNED")
                      || statut.equals("VALID") || statut.equals("UNPAID")||
                      statut.equals("POROFORMA")|| statut.equals("ACTIVE")) {
                    d.setEtatCommission(false);
                    d.setStatutCommission(nomStatut);
                    countPrime ++ ;
                  }

              d.setStatutCommission(nomStatut);
              listDetailsCommissionDemandes.add(d);

            }

          }
        }
      }
      result.put("total", toTaleCommision.toString());
      
      if(IsRole && totalDemandeMiseService >=100) {
  	     result.put("primeMs",800 );
    }
    else {
 	     result.put("primeMs",0 );

    }

    } else {
      result.put("totalDemandes", "0");
      result.put("demandesAccepte", "0");
      result.put("nbFirsFactureNonPayed", 0);
      result.put("total", "0");
    }

    // demande non réalisée
    Integer firstFactureNonpayeeandNonVerse = factureRepository
        .nombreFirstFactureNonpayeeandVerse(userId, startOfMonth.toString(), endOfMonth.toString());
    result.put("nbFirsFactureNonPayed", firstFactureNonpayeeandNonVerse);

    result.put("detailsCommissionDemande", listDetailsCommissionDemandes);
    LOGGER.info("calculeCommDemande terminée.");
    return result;
  }

  private Double getMontantCommissionDemande(Integer palier, String debitPack) {
    if (palier >= 300) {
      switch (debitPack) {
        case "10":
        case "12":
          return Double.parseDouble(commisionAcquisitionDemande10MoPaliertroisCentplus);
        case "20":
          return Double.parseDouble(commisionAcquisitionDemande20MoPaliertroisCentplus);
        case "30":
          return Double.parseDouble(commisionAcquisitionDemande30MoPaliertroisCentplus);
        case "50":
          return Double.parseDouble(commisionAcquisitionDemande50MoPaliertroisCentplus);
        case "100":
          return Double.parseDouble(commisionAcquisitionDemande100MoPaliertroisCentplus);
        default:
      }
    } else if (palier >= 200 && palier <= 299) {
      switch (debitPack) {
        case "10":
        case "12":
          return Double.parseDouble(commisionAcquisitionDemande10MoPalierdeuxCentplus);
        case "20":
          return Double.parseDouble(commisionAcquisitionDemande20MoPalierdeuxCentplus);
        case "30":
          return Double.parseDouble(commisionAcquisitionDemande30MoPalierdeuxCentplus);
        case "50":
          return Double.parseDouble(commisionAcquisitionDemande50MoPalierdeuxCentplus);
        case "100":
          return Double.parseDouble(commisionAcquisitionDemande100MoPalierdeuxCentplus);
        default:
      }
    } else if (palier >= 100 && palier <= 199) {
      switch (debitPack) {
        case "10":
        case "12":
          return Double.parseDouble(commissionAcquisitionDemandeCentPlus10Mo);
        case "20":
          return Double.parseDouble(commissionAcquisitionDemandeCentPlus20Mo);
        case "30":
          return Double.parseDouble(commissionAcquisitionDemandeCentPlus30Mo);
        case "50":
          return Double.parseDouble(commissionAcquisitioDemandeCentplus50Mo);
        case "100":
          return Double.parseDouble(commissionAcquisitionDemandeCentplus100Mo);
        default:
      }
    } else if (palier >= 0 && palier <= 49) {
      switch (debitPack) {
        case "10":
        case "12":
          return Double.parseDouble(commisionAcquisitionDemande10MoPalier0A49);
        case "20":
          return Double.parseDouble(commisionAcquisitionDemande20MoPalier0A49);
        case "30":
          return Double.parseDouble(commisionAcquisitionDemande30MoPalier0A49);
        case "50":
          return Double.parseDouble(commisionAcquisitionDemande50MoPalier0A49);
        case "100":
          return Double.parseDouble(commisionAcquisitionDemande100MoPalier0A49);
        default:
      }
    } else if (palier >= 50 && palier <= 99) {
      switch (debitPack) {
        case "10":
        case "12":
          return Double.parseDouble(commisionAcquisitionDemande10MoPalier50A99);
        case "20":
          return Double.parseDouble(commisionAcquisitionDemande20MoPalier50A99);
        case "30":
          return Double.parseDouble(commisionAcquisitionDemande30MoPalier50A99);
        case "50":
          return Double.parseDouble(commisionAcquisitionDemande50MoPalier50A99);
        case "100":
          return Double.parseDouble(commisionAcquisitionDemande100MoPalier50A99);
        default:
      }
    }
    return null;
  }

  public Double rechercherPrix(List<OffreCommission> offres, Integer debit, Integer palierMin,
      Integer palierMax) {

    if (offres.size() > 0) {
      for (OffreCommission offre : offres) {
        if (offre.getPalierMin() == palierMin && offre.getPalierMax() == palierMax
            && offre.getDebit() == debit) {
          return offre.getMontant();
        }
      }
    }
    return getMontantCommissionDemande(palierMin, debit.toString());
  }

  public HashMap<String, Object> calculecommisionFirstFacture(Long id, String date) {
    List<DetailsCommissionPremiereFacture> listeDetailsCommissionFirstFactures = new ArrayList<>();
    AtomicInteger countPremiereFactureEcheance10 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureEcheance12 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureEcheance20 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureEcheance30 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureEcheance50 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureEcheance100 = new AtomicInteger(0);


    AtomicInteger countPremiereFactureHorsEcheance10 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureHorsEcheance12 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureHorsEcheance20 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureHorsEcheance30 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureHorsEcheance50 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureHorsEcheance100 = new AtomicInteger(0);


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    YearMonth yearMonth = YearMonth.parse(date, formatter);

    LocalDate startOfMonth = yearMonth.atDay(1);
    LocalDate endOfMonth = yearMonth.atEndOfMonth();
    Double commissionPremierFacture = 0.0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");
    AtomicReference<String> debitpack = new AtomicReference<>("10");
    List<Facture> ListeFirstFacture = factureRepository
        .findListFirstFactureByVersementDateToCalculeCommision(id, startOfMonth.toString(),
            dateFormat.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString())));
    ListeFirstFacture.forEach(facture -> {
      if (facture.getIsFirstFacture() == true) {
        facture.getEntriesFacture().forEach(ent -> {

          if (ent.getPack() != null) {
            debitpack.updateAndGet(value -> ent.getPack().getDebitPack());
          }
        });
        // detail commission sur facture
        DetailsCommissionPremiereFacture detailsCommissionPremiereFacture =
            new DetailsCommissionPremiereFacture();
        detailsCommissionPremiereFacture.setReferenceFacture(facture.getRef_facture());
        // detail commission sur facture
        Boolean isEcheance = null;
        Double montantCommission = null;

        Date dateDeVersement = facture.getDateDeVersement();
        Date dateDePayement = facture.getDateDePayement();

        detailsCommissionPremiereFacture
            .setReferenceClient(facture.getAbonnement().getReferenceClient());
        long differenceInMilliseconds =
            Math.abs(dateDeVersement.getTime() - dateDePayement.getTime());
        long differenceInDays =
            TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);
        if (differenceInDays <= 15) {
          isEcheance = true;
          if (debitpack.get().equals("10")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 10, isEcheance);
            countPremiereFactureEcheance10.incrementAndGet();
          } else if (debitpack.get().equals("12")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 12, isEcheance);
            countPremiereFactureEcheance12.incrementAndGet();
          } else if (debitpack.get().equals("20")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 20, isEcheance);
            countPremiereFactureEcheance20.incrementAndGet();
          } else if (debitpack.get().equals("30")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 30, isEcheance);
            countPremiereFactureEcheance30.incrementAndGet();
          } else if (debitpack.get().equals("50")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 50, isEcheance);
            countPremiereFactureEcheance50.incrementAndGet();
          } else if (debitpack.get().equals("100")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 100, isEcheance);
            countPremiereFactureEcheance100.incrementAndGet();
          }

        } else {
          isEcheance = false;
          if (debitpack.get().equals("10")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 10, isEcheance);
            countPremiereFactureHorsEcheance10.incrementAndGet();
          } else if (debitpack.get().equals("12")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 12, isEcheance);
            countPremiereFactureHorsEcheance12.incrementAndGet();
          } else if (debitpack.get().equals("20")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 20, isEcheance);
            countPremiereFactureHorsEcheance20.incrementAndGet();
          } else if (debitpack.get().equals("30")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 30, isEcheance);
            countPremiereFactureHorsEcheance30.incrementAndGet();
          } else if (debitpack.get().equals("50")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 50, isEcheance);
            countPremiereFactureHorsEcheance50.incrementAndGet();
          } else if (debitpack.get().equals("100")) {

            montantCommission =
                getMontantFirstFactureByPalier(ListeFirstFacture.size(), 100, isEcheance);
            countPremiereFactureHorsEcheance100.incrementAndGet();
          }

        }
        detailsCommissionPremiereFacture.setIsEcheance(isEcheance);
        detailsCommissionPremiereFacture.setMontantCommisison(montantCommission);
        listeDetailsCommissionFirstFactures.add(detailsCommissionPremiereFacture);

      }
    });
    Integer sumPremiereFacture =
        countPremiereFactureEcheance10.get() + countPremiereFactureEcheance12.get()
            + countPremiereFactureEcheance20.get() + countPremiereFactureEcheance30.get()
            + countPremiereFactureEcheance50.get() + countPremiereFactureEcheance100.get()
            + countPremiereFactureHorsEcheance10.get() + countPremiereFactureHorsEcheance12.get()
            + countPremiereFactureHorsEcheance20.get() + countPremiereFactureHorsEcheance30.get()
            + countPremiereFactureHorsEcheance50.get() + countPremiereFactureHorsEcheance100.get();
    // Echeance 10
    if (sumPremiereFacture >= 0 && sumPremiereFacture <= 49) {
      commissionPremierFacture = calculeCommisionActivation(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);

    } else if (sumPremiereFacture >= 50 && sumPremiereFacture <= 99) {

      commissionPremierFacture = commisionactivationpalier50To99(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);

    } else if (sumPremiereFacture >= 100 && sumPremiereFacture <= 199) {
      commissionPremierFacture = comissionActivationPaliersup100(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);
    } else if (sumPremiereFacture >= 200 && sumPremiereFacture <= 299) {
      commissionPremierFacture = comissionActivationPaliersup200(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);
    } else if (sumPremiereFacture >= 300) {
      commissionPremierFacture = comissionActivationPaliersup300(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);
    }

    List<AvanceCommissionAcquisition> listCommisionEnPayee = avanceCommissionAcquisitionRepository
        .findByMonthAndStatusAndUser(id, startOfMonth.toString(),
            dateFormat.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString())),
            StatutAvanceBordereau.AVANCE_PAYED);
    Double totalcommisionPayed = listCommisionEnPayee.stream()
        .mapToDouble(commision -> commision.getMontantCommissionPremiereFacture()).sum();
    LOGGER.debug("Total de première facture calculé: {}", sumPremiereFacture);
    LOGGER.debug("Total commissions de paiement de première facture calculé: {}",
        commissionPremierFacture);
    
    List<Encaissement> listeRetardPayement =  encaissementRepository.findByCommsionMothAndUserAndEchanceFactures(startOfMonth.toString() ,
    	endOfMonth.toString() , id);
    Double restePayement = commissionPremierFacture - totalcommisionPayed;
    
    HashMap<String, Object> commissionActivation = new HashMap<>();
    commissionActivation.put("nbCommissionActivation", sumPremiereFacture.toString());
    commissionActivation.put("totalCommissions", restePayement.toString());
    commissionActivation.put("totalCommissionsAvancePayee", totalcommisionPayed.toString());
    commissionActivation.put("totalCommissionsSansAvance", commissionPremierFacture.toString());
    commissionActivation.put("totalRetardPayement", listeRetardPayement.size() *4 );

    commissionActivation.put("listeRetardPayement", listeRetardPayement);

    commissionActivation.put("detailsCommissionFirstFactures", listeDetailsCommissionFirstFactures);

    /*
     * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
     * 
     * Integer firstFactureNonVerse = factureRepository.nombreFirstFactureNonVerse(id,
     * startOfMonth.toString(),
     * sdf.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString())));
     * 
     * commissionActivation.put("nbFirsFactureNonVerse", firstFactureNonVerse);
     */
    return commissionActivation;
  }

  public HashMap<String, Object> commissionFacture(Long id, String date) {

    LOGGER.info("Début de commissionFacture...");
    LOGGER.info("Paramètres - id: {}, date: {}", id, date);

    HashMap<String, Object> result = new HashMap<>();
    List<DetailsCommissionFacture> listDetailsCommissionFactures = new ArrayList<>();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    YearMonth yearMonth = YearMonth.parse(date, formatter);

    LocalDate startOfMonth = yearMonth.atDay(1);
    LocalDate endOfMonth = yearMonth.atEndOfMonth();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");

    LOGGER.debug("Appel de bordereaurRepository.getBorderauToCommission(id, {}, {})...",
        startOfMonth, endOfMonth);

    List<Encaissement> listEncaissements =
        encaissementRepository.getEncaissementToCommission(id, startOfMonth.toString(),
            dateFormat.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString())));

    AtomicInteger countFacturesEcheance = new AtomicInteger(0);
    AtomicInteger countFacturesHorsEcheance = new AtomicInteger(0);

    // Double totalMoantantFactureEcheance = 0.0;
    AtomicReference<Double> totalMoantantFactureEcheance10 = new AtomicReference<>(0.0);
    AtomicReference<Double> totalMoantantHorsFactureEcheance10 = new AtomicReference<>(0.0);

    AtomicReference<Double> totalMoantantFactureEcheance20 = new AtomicReference<>(0.0);
    AtomicReference<Double> totalMoantantHorsFactureEcheance20 = new AtomicReference<>(0.0);

    AtomicReference<Double> totalMoantantFactureEcheance30 = new AtomicReference<>(0.0);
    AtomicReference<Double> totalMoantantHorsFactureEcheance30 = new AtomicReference<>(0.0);

    AtomicReference<Double> totalMoantantFactureEcheance50 = new AtomicReference<>(0.0);
    AtomicReference<Double> totalMoantantHorsFactureEcheance50 = new AtomicReference<>(0.0);

    AtomicReference<Double> totalMoantantFactureEcheance100 = new AtomicReference<>(0.0);
    AtomicReference<Double> totalMoantantHorsFactureEcheance100 = new AtomicReference<>(0.0);
    AtomicReference<Integer> totalMontantNonVerse = new AtomicReference<>(0);
    // Double totalMoantantHorsFactureEcheance = 0.0;

    listEncaissements.forEach(en -> {

      Facture facture = en.getFacture();
      AtomicReference<String> debitpack = new AtomicReference<>("10");
      Date date1 = facture.getDateDeVersement();
      Date date2 = facture.getDateDePayement();

      long differenceInMilliseconds = Math.abs(date1.getTime() - date2.getTime());
      long differenceInDays =
          TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);

      // detail commission sur facture
      DetailsCommissionFacture detailsCommissionFacture = new DetailsCommissionFacture();
      detailsCommissionFacture.setReferenceFacture(facture.getRef_facture());

      AtomicReference<Double> montant = new AtomicReference<>(facture.getMontantHt());

      facture.getEntriesFacture().forEach(ent -> {
        if (ent.getProduit() != null && ent.getProduit().getIsRacordement() == true) {
          montant.updateAndGet(value -> value - ent.getPrixUnitaireHT());
        }
        if (ent.getPack() != null) {
          debitpack.updateAndGet(value -> ent.getPack().getDebitPack());
        }
      });
      // detail commission sur facture
      Boolean isEcheance = null;
      Double montantCommission = null;

      switch (debitpack.get()) {
        case "10":
        case "12":
          if (differenceInDays <= 15) {
            totalMoantantFactureEcheance10.updateAndGet(value -> value + montant.get());
            countFacturesEcheance.incrementAndGet();
            isEcheance = true;
            montantCommission =
                (montant.get() * Double.parseDouble(commissionPaiementEnEchancePourcentFor12Mo))
                    / 100;
          } else {
            totalMoantantHorsFactureEcheance10.updateAndGet(value -> value + montant.get());
            countFacturesHorsEcheance.incrementAndGet();
            isEcheance = false;
            montantCommission =
                (montant.get() * Double.parseDouble(commissionPaiementEnHechancePourcentFor12Mo))
                    / 100;
          }
          break;
        case "20":
          if (differenceInDays <= 15) {
            totalMoantantFactureEcheance20.updateAndGet(value -> value + montant.get());
            countFacturesEcheance.incrementAndGet();
            isEcheance = true;
            montantCommission =
                (montant.get() * Double.parseDouble(commissionPaiementEnEchancePourcentFor20Mo))
                    / 100;
          } else {
            totalMoantantHorsFactureEcheance20.updateAndGet(value -> value + montant.get());
            countFacturesHorsEcheance.incrementAndGet();
            isEcheance = false;
            montantCommission =
                (montant.get() * Double.parseDouble(commissionPaiementEnHechancePourcentFor20Mo))
                    / 100;
          }
          break;
        case "30":
          if (differenceInDays <= 15) {
            totalMoantantFactureEcheance30.updateAndGet(value -> value + montant.get());
            countFacturesEcheance.incrementAndGet();
            isEcheance = true;
            montantCommission =
                (montant.get() * Double.parseDouble(commissionPaiementEnEchancePourcentFor30Mo))
                    / 100;
          } else {
            totalMoantantHorsFactureEcheance30.updateAndGet(value -> value + montant.get());
            countFacturesHorsEcheance.incrementAndGet();
            isEcheance = false;
            montantCommission =
                (montant.get() * Double.parseDouble(commissionPaiementEnHechancePourcentFor30Mo))
                    / 100;
          }
          break;
        case "50":
          if (differenceInDays <= 15) {
            totalMoantantFactureEcheance50.updateAndGet(value -> value + montant.get());
            countFacturesEcheance.incrementAndGet();
            isEcheance = true;
            montantCommission =
                (montant.get() * Double.parseDouble(commissionPaiementEnEchancePourcentFor50Mo))
                    / 100;
          } else {
            totalMoantantHorsFactureEcheance50.updateAndGet(value -> value + montant.get());
            countFacturesHorsEcheance.incrementAndGet();
            isEcheance = false;
            montantCommission =
                (montant.get() * Double.parseDouble(commissionPaiementEnHechancePourcentFor50Mo))
                    / 100;
          }
          break;
        case "100":
          if (differenceInDays <= 15) {
            totalMoantantFactureEcheance100.updateAndGet(value -> value + montant.get());
            countFacturesEcheance.incrementAndGet();
            isEcheance = true;
            montantCommission =
                (montant.get() * Double.parseDouble(commissionPaiementEnEchancePourcentFor100Mo))
                    / 100;
          } else {
            totalMoantantHorsFactureEcheance100.updateAndGet(value -> value + montant.get());
            countFacturesHorsEcheance.incrementAndGet();
            isEcheance = false;
            montantCommission =
                (montant.get() * Double.parseDouble(commissionPaiementEnHechancePourcentFor100Mo))
                    / 100;
          }
          break;
        default:
      }

      // detail commission sur facture
      detailsCommissionFacture.setMontantFacture(montant.get());
      detailsCommissionFacture.setIsEcheance(isEcheance);
      detailsCommissionFacture.setMontantCommisison(montantCommission);
      listDetailsCommissionFactures.add(detailsCommissionFacture);

    });

    Double commissionPayementFacture12Mo = ((totalMoantantFactureEcheance10.get()
        * Double.parseDouble(commissionPaiementEnEchancePourcentFor12Mo)) / 100)
        + ((totalMoantantHorsFactureEcheance10.get()
            * Double.parseDouble(commissionPaiementEnHechancePourcentFor12Mo)) / 100);
    Double commissionPayementFacture20Mo = ((totalMoantantFactureEcheance20.get()
        * Double.parseDouble(commissionPaiementEnEchancePourcentFor20Mo)) / 100)
        + ((totalMoantantHorsFactureEcheance20.get()
            * Double.parseDouble(commissionPaiementEnHechancePourcentFor20Mo)) / 100);
    Double commissionPayementFacture30Mo = ((totalMoantantFactureEcheance30.get()
        * Double.parseDouble(commissionPaiementEnEchancePourcentFor30Mo)) / 100)
        + ((totalMoantantHorsFactureEcheance30.get()
            * Double.parseDouble(commissionPaiementEnHechancePourcentFor30Mo)) / 100);
    Double commissionPayementFacture50Mo = ((totalMoantantFactureEcheance50.get()
        * Double.parseDouble(commissionPaiementEnEchancePourcentFor50Mo)) / 100)
        + ((totalMoantantHorsFactureEcheance50.get()
            * Double.parseDouble(commissionPaiementEnHechancePourcentFor50Mo)) / 100);

    Double commissionPayementFacture100Mo = ((totalMoantantFactureEcheance100.get()
        * Double.parseDouble(commissionPaiementEnEchancePourcentFor100Mo)) / 100)
        + ((totalMoantantHorsFactureEcheance100.get()
            * Double.parseDouble(commissionPaiementEnHechancePourcentFor100Mo)) / 100);

    Double commissionFacture = commissionPayementFacture12Mo + commissionPayementFacture20Mo
        + commissionPayementFacture30Mo + commissionPayementFacture50Mo
        + commissionPayementFacture100Mo;

    HashMap<String, Object> commissionPaiements = new HashMap<>();
    commissionPaiements.put("nbFacturePayee",
        String.valueOf(countFacturesEcheance.get() + countFacturesHorsEcheance.get()));
    commissionPaiements.put("totalCommissionsPaiement", commissionFacture.toString());
    commissionPaiements.put("detailsCommissionFacture", listDetailsCommissionFactures);

    Integer nombreFactureNonVerse = encaissementRepository.getNombreEncaissementNonVerse(id,
        startOfMonth.toString(), endOfMonth.toString());
    commissionPaiements.put("nombreFactureNonVerse", nombreFactureNonVerse);

    LOGGER.debug("Total commissions de paiement de facture calculé: {}", commissionFacture);



    LOGGER.info("commissionFacture terminée.");

    return commissionPaiements;
  }

  private Double comissionActivationPaliersup200(AtomicInteger countPremiereFactureEcheance10,
      AtomicInteger countPremiereFactureEcheance12, AtomicInteger countPremiereFactureEcheance20,
      AtomicInteger countPremiereFactureEcheance30, AtomicInteger countPremiereFactureEcheance50,
      AtomicInteger countPremiereFactureEcheance100,
      AtomicInteger countPremiereFactureHorsEcheance10,
      AtomicInteger countPremiereFactureHorsEcheance12,
      AtomicInteger countPremiereFactureHorsEcheance20,
      AtomicInteger countPremiereFactureHorsEcheance30,
      AtomicInteger countPremiereFactureHorsEcheance50,
      AtomicInteger countPremiereFactureHorsEcheance100, Double commissionPremierFacture) {
    commissionPremierFacture += countPremiereFactureEcheance10.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier200EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance10.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier200EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance12.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier200EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance12.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier200EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance20.get()
        * Double.parseDouble(commissionAcquisitionActivation20MoPalier200EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance20.get()
        * Double.parseDouble(commissionAcquisitionActivation20MoPalier200EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance30.get()
        * Double.parseDouble(commissionAcquisitionActivation30MoPalier200EnEechance);
    commissionPremierFacture += countPremiereFactureHorsEcheance30.get()
        * Double.parseDouble(commissionAcquisitionActivation30MoPalier200EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance50.get()
        * Double.parseDouble(commissionAcquisitionActivation50MoPalier200EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance50.get()
        * Double.parseDouble(commissionAcquisitionActivation50MoPalier200EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance100.get()
        * Double.parseDouble(commissionAcquisitionActivation100MoPalier200EnEchance);

    commissionPremierFacture += countPremiereFactureHorsEcheance100.get()
        * Double.parseDouble(commissionAcquisitionActivation100MoPalier200EnHechance);
    return commissionPremierFacture;
  }

  private Double comissionActivationPaliersup300(AtomicInteger countPremiereFactureEcheance10,
      AtomicInteger countPremiereFactureEcheance12, AtomicInteger countPremiereFactureEcheance20,
      AtomicInteger countPremiereFactureEcheance30, AtomicInteger countPremiereFactureEcheance50,
      AtomicInteger countPremiereFactureEcheance100,
      AtomicInteger countPremiereFactureHorsEcheance10,
      AtomicInteger countPremiereFactureHorsEcheance12,
      AtomicInteger countPremiereFactureHorsEcheance20,
      AtomicInteger countPremiereFactureHorsEcheance30,
      AtomicInteger countPremiereFactureHorsEcheance50,
      AtomicInteger countPremiereFactureHorsEcheance100, Double commissionPremierFacture) {
    commissionPremierFacture += countPremiereFactureEcheance10.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier300EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance10.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier300EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance12.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier300EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance12.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier300EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance20.get()
        * Double.parseDouble(commissionAcquisitionActivation20MoPalier300EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance20.get()
        * Double.parseDouble(commissionAcquisitionActivation20MoPalier300EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance30.get()
        * Double.parseDouble(commissionAcquisitionActivation30MoPalier300EnEechance);
    commissionPremierFacture += countPremiereFactureHorsEcheance30.get()
        * Double.parseDouble(commissionAcquisitionActivation30MoPalier300EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance50.get()
        * Double.parseDouble(commissionAcquisitionActivation50MoPalier300EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance50.get()
        * Double.parseDouble(commissionAcquisitionActivation50MoPalier300EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance100.get()
        * Double.parseDouble(commissionAcquisitionActivation100MoPalier300EnEchance);

    commissionPremierFacture += countPremiereFactureHorsEcheance100.get()
        * Double.parseDouble(commissionAcquisitionActivation100MoPalier300EnHechance);
    return commissionPremierFacture;
  }

  private Double comissionActivationPaliersup100(AtomicInteger countPremiereFactureEcheance10,
      AtomicInteger countPremiereFactureEcheance12, AtomicInteger countPremiereFactureEcheance20,
      AtomicInteger countPremiereFactureEcheance30, AtomicInteger countPremiereFactureEcheance50,
      AtomicInteger countPremiereFactureEcheance100,
      AtomicInteger countPremiereFactureHorsEcheance10,
      AtomicInteger countPremiereFactureHorsEcheance12,
      AtomicInteger countPremiereFactureHorsEcheance20,
      AtomicInteger countPremiereFactureHorsEcheance30,
      AtomicInteger countPremiereFactureHorsEcheance50,
      AtomicInteger countPremiereFactureHorsEcheance100, Double commissionPremierFacture) {
    commissionPremierFacture += countPremiereFactureEcheance10.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier100EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance10.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier100EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance12.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier100EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance12.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier100EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance20.get()
        * Double.parseDouble(commissionAcquisitionActivation20MoPalier100Echance);
    commissionPremierFacture += countPremiereFactureHorsEcheance20.get()
        * Double.parseDouble(commissionAcquisitionActivation20MoPalier100Hechance);
    commissionPremierFacture += countPremiereFactureEcheance30.get()
        * Double.parseDouble(commissionAcquisitionActivation30MoPalier100EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance30.get()
        * Double.parseDouble(commissionAcquisitionActivation30MoPalier100EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance50.get()
        * Double.parseDouble(commissionAcquisitionActivation50MoPalier100EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance50.get()
        * Double.parseDouble(commissionAcquisitionActivation50MoPalier100EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance100.get()
        * Double.parseDouble(commissionAcquisitionActivation100MoPalier100EnEchance);

    commissionPremierFacture += countPremiereFactureHorsEcheance100.get()
        * Double.parseDouble(commissionAcquisitionActivation100MoPalier100Hechance);
    return commissionPremierFacture;
  }

  private Double commisionactivationpalier50To99(AtomicInteger countPremiereFactureEcheance10,
      AtomicInteger countPremiereFactureEcheance12, AtomicInteger countPremiereFactureEcheance20,
      AtomicInteger countPremiereFactureEcheance30, AtomicInteger countPremiereFactureEcheance50,
      AtomicInteger countPremiereFactureEcheance100,
      AtomicInteger countPremiereFactureHorsEcheance10,
      AtomicInteger countPremiereFactureHorsEcheance12,
      AtomicInteger countPremiereFactureHorsEcheance20,
      AtomicInteger countPremiereFactureHorsEcheance30,
      AtomicInteger countPremiereFactureHorsEcheance50,
      AtomicInteger countPremiereFactureHorsEcheance100, Double commissionPremierFacture) {
    commissionPremierFacture += countPremiereFactureEcheance10.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier50A99EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance10.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier50A99EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance12.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier50A99EnEchance);

    commissionPremierFacture += countPremiereFactureHorsEcheance12.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier50A99EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance20.get()
        * Double.parseDouble(commissionAcquisitionActivation20MoPalier50A99Enechance);

    commissionPremierFacture += countPremiereFactureHorsEcheance20.get()
        * Double.parseDouble(commissionAcquisitionActivation20MoPalier50A99EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance30.get()
        * Double.parseDouble(commissionAcquisitionActivation30MoPalier50A99EnEchance);

    commissionPremierFacture += countPremiereFactureHorsEcheance30.get()
        * Double.parseDouble(commissionAcquisitionActivation30MoPalier50En99EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance50.get()
        * Double.parseDouble(commissionAcquisitionActivation50MoPalier50A99EnEchance);

    commissionPremierFacture += countPremiereFactureHorsEcheance50.get()
        * Double.parseDouble(commissionAcquisitionActivation50MoPalier50A99EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance100.get()
        * Double.parseDouble(commissionAcquisitionActivation100MoPalier50A99EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance100.get()
        * Double.parseDouble(commissionAcquisitionActivation100MoPalier50A99EnHechance);
    return commissionPremierFacture;
  }

  private Double calculeCommisionActivation(AtomicInteger countPremiereFactureEcheance10,
      AtomicInteger countPremiereFactureEcheance12, AtomicInteger countPremiereFactureEcheance20,
      AtomicInteger countPremiereFactureEcheance30, AtomicInteger countPremiereFactureEcheance50,
      AtomicInteger countPremiereFactureEcheance100,
      AtomicInteger countPremiereFactureHorsEcheance10,
      AtomicInteger countPremiereFactureHorsEcheance12,
      AtomicInteger countPremiereFactureHorsEcheance20,
      AtomicInteger countPremiereFactureHorsEcheance30,
      AtomicInteger countPremiereFactureHorsEcheance50,
      AtomicInteger countPremiereFactureHorsEcheance100, Double commissionPremierFacture) {
    commissionPremierFacture += countPremiereFactureEcheance10.get()
        * Double.parseDouble(commissionAcquisitionActivatio12MoPalier0A49EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance10.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier0A49EnHechance);
    commissionPremierFacture += countPremiereFactureHorsEcheance12.get()
        * Double.parseDouble(commissionAcquisitionActivation12MoPalier0A49EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance12.get()
        * Double.parseDouble(commissionAcquisitionActivatio12MoPalier0A49EnEchance);
    commissionPremierFacture += countPremiereFactureEcheance20.get()
        * Double.parseDouble(commissionAcquisitionActivation20MoPalier0A49EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance20.get()
        * Double.parseDouble(commissionAcquisitionActivation20MoPalier0A49Enhechance);

    commissionPremierFacture += countPremiereFactureEcheance30.get()
        * Double.parseDouble(commissionAcquisitionActivation30MoPalier0A49EnEchance);
    commissionPremierFacture += countPremiereFactureHorsEcheance30.get()
        * Double.parseDouble(commissionAcquisitionActivation30MoPalier0A49EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance50.get()
        * Double.parseDouble(commissionAcquisitionActivation50MoPalier0A49EnEchance);

    commissionPremierFacture += countPremiereFactureHorsEcheance50.get()
        * Double.parseDouble(commissionAcquisitionActivation50MoPalier0A49EnHechance);
    commissionPremierFacture += countPremiereFactureEcheance100.get()
        * Double.parseDouble(commissionAcquisitionActivation100MoPalier0A49EnEchance);

    commissionPremierFacture += countPremiereFactureHorsEcheance100.get()
        * Double.parseDouble(commissionAcquisitionActivationPalier100MoPalier0A49EnHechance);
    return commissionPremierFacture;
  }

  private Double getMontantFirstFactureByPalier(int countFirstFacture, int debit,
      Boolean isEcheance) {
    if (isEcheance == true) {
      if (countFirstFacture >= 0 && countFirstFacture <= 49) {
        switch (debit) {
          case 10:
          case 12:
            return Double.parseDouble(commissionAcquisitionActivatio12MoPalier0A49EnEchance);
          case 20:
            return Double.parseDouble(commissionAcquisitionActivation20MoPalier0A49EnEchance);
          case 30:
            return Double.parseDouble(commissionAcquisitionActivation30MoPalier0A49EnEchance);
          case 50:
            return Double.parseDouble(commissionAcquisitionActivation50MoPalier0A49EnEchance);
          case 100:
            return Double.parseDouble(commissionAcquisitionActivation100MoPalier0A49EnEchance);
          default:
        }
      } else if (countFirstFacture >= 50 && countFirstFacture < 100) {
        switch (debit) {
          case 10:
          case 12:
            return Double.parseDouble(commissionAcquisitionActivation12MoPalier50A99EnEchance);
          case 20:
            return Double.parseDouble(commissionAcquisitionActivation20MoPalier50A99Enechance);
          case 30:
            return Double.parseDouble(commissionAcquisitionActivation30MoPalier50A99EnEchance);
          case 50:
            return Double.parseDouble(commissionAcquisitionActivation50MoPalier50A99EnEchance);
          case 100:
            return Double.parseDouble(commissionAcquisitionActivation100MoPalier50A99EnEchance);
          default:
        }
      } else if (countFirstFacture >= 100 && countFirstFacture <= 199) {
        switch (debit) {
          case 10:
          case 12:
            return Double.parseDouble(commissionAcquisitionActivation12MoPalier100EnEchance);
          case 20:
            return Double.parseDouble(commissionAcquisitionActivation20MoPalier100Echance);
          case 30:
            return Double.parseDouble(commissionAcquisitionActivation30MoPalier100EnEchance);
          case 50:
            return Double.parseDouble(commissionAcquisitionActivation50MoPalier100EnEchance);
          case 100:
            return Double.parseDouble(commissionAcquisitionActivation100MoPalier100EnEchance);
          default:
        }
      } else if (countFirstFacture >= 200 && countFirstFacture <= 299) {
        switch (debit) {
          case 10:
          case 12:
            return Double.parseDouble(commissionAcquisitionActivation12MoPalier200EnEchance);
          case 20:
            return Double.parseDouble(commissionAcquisitionActivation20MoPalier200EnEchance);
          case 30:
            return Double.parseDouble(commissionAcquisitionActivation30MoPalier200EnEechance);
          case 50:
            return Double.parseDouble(commissionAcquisitionActivation50MoPalier200EnEchance);
          case 100:
            return Double.parseDouble(commissionAcquisitionActivation100MoPalier200EnEchance);
          default:
        }
      } else if (countFirstFacture >= 300) {
        switch (debit) {
          case 10:
          case 12:
            return Double.parseDouble(commissionAcquisitionActivation12MoPalier300EnEchance);
          case 20:
            return Double.parseDouble(commissionAcquisitionActivation20MoPalier300EnEchance);
          case 30:
            return Double.parseDouble(commissionAcquisitionActivation30MoPalier300EnEechance);
          case 50:
            return Double.parseDouble(commissionAcquisitionActivation50MoPalier300EnEchance);
          case 100:
            return Double.parseDouble(commissionAcquisitionActivation100MoPalier300EnEchance);
          default:
        }
      }
    } else {
      if (countFirstFacture >= 0 && countFirstFacture <= 49) {
        switch (debit) {
          case 10:
          case 12:
            return Double.parseDouble(commissionAcquisitionActivation12MoPalier0A49EnHechance);
          case 20:
            return Double.parseDouble(commissionAcquisitionActivation20MoPalier0A49Enhechance);
          case 30:
            return Double.parseDouble(commissionAcquisitionActivation30MoPalier0A49EnHechance);
          case 50:
            return Double.parseDouble(commissionAcquisitionActivation50MoPalier0A49EnHechance);
          case 100:
            return Double
                .parseDouble(commissionAcquisitionActivationPalier100MoPalier0A49EnHechance);
          default:
        }
      } else if (countFirstFacture >= 50 && countFirstFacture < 100) {
        switch (debit) {
          case 10:
          case 12:
            return Double.parseDouble(commissionAcquisitionActivation12MoPalier50A99EnHechance);
          case 20:
            return Double.parseDouble(commissionAcquisitionActivation20MoPalier50A99EnHechance);
          case 30:
            return Double.parseDouble(commissionAcquisitionActivation30MoPalier50En99EnHechance);
          case 50:
            return Double.parseDouble(commissionAcquisitionActivation50MoPalier50A99EnHechance);
          case 100:
            return Double.parseDouble(commissionAcquisitionActivation100MoPalier50A99EnHechance);
          default:
        }
      } else if (countFirstFacture >= 100 && countFirstFacture <= 199) {
        switch (debit) {
          case 10:
          case 12:
            return Double.parseDouble(commissionAcquisitionActivation12MoPalier100EnHechance);
          case 20:
            return Double.parseDouble(commissionAcquisitionActivation20MoPalier100Hechance);
          case 30:
            return Double.parseDouble(commissionAcquisitionActivation30MoPalier100EnHechance);
          case 50:
            return Double.parseDouble(commissionAcquisitionActivation50MoPalier100EnHechance);
          case 100:
            return Double.parseDouble(commissionAcquisitionActivation100MoPalier100Hechance);
          default:
        }
      } else if (countFirstFacture >= 200 && countFirstFacture <= 299) {
        switch (debit) {
          case 10:
          case 12:
            return Double.parseDouble(commissionAcquisitionActivation12MoPalier200EnHechance);
          case 20:
            return Double.parseDouble(commissionAcquisitionActivation20MoPalier200EnHechance);
          case 30:
            return Double.parseDouble(commissionAcquisitionActivation30MoPalier200EnHechance);
          case 50:
            return Double.parseDouble(commissionAcquisitionActivation50MoPalier200EnHechance);
          case 100:
            return Double.parseDouble(commissionAcquisitionActivation100MoPalier200EnHechance);
          default:
        }
      } else if (countFirstFacture >= 300) {
        switch (debit) {
          case 10:
          case 12:
            return Double.parseDouble(commissionAcquisitionActivation12MoPalier300EnHechance);
          case 20:
            return Double.parseDouble(commissionAcquisitionActivation20MoPalier300EnHechance);
          case 30:
            return Double.parseDouble(commissionAcquisitionActivation30MoPalier300EnHechance);
          case 50:
            return Double.parseDouble(commissionAcquisitionActivation50MoPalier300EnHechance);
          case 100:
            return Double.parseDouble(commissionAcquisitionActivation100MoPalier300EnHechance);
          default:
        }
      }
    }
    return null;
  }

  @Override
  public Boolean findExiteCommision(User user, Integer annee, Integer numMois) {
    // TODO Auto-generated method stub
    return commissionRepository.findExiteCommision(user, annee, numMois);
  }

  @Override
  public ModelAndView exportListCommissionToExcel(Integer annee, Integer numMois, String statut,
      String codeRevendeur, Date startCreatedDate, Date endCreatedDate, String reference,Boolean typeCommision ,
      HttpServletRequest request, HttpServletResponse response) {
    // TODO Auto-generated method stub
    ModelAndView mav = new ModelAndView();

    List<Commission> myList = new ArrayList<>();

    myList = commissionRepository.getAllCommission(annee, numMois, codeRevendeur, statut,
        startCreatedDate, endCreatedDate, reference , typeCommision);
    if (myList.size() > 0) {
      mav.setView(new CommissionExcelExport());
      mav.addObject("list", myList);
    } else {
      mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR); // Set the desired HTTP status code
      mav.addObject("errorMessage", "No data found");
      // Add an error message

      try {
        request.getRequestDispatcher("/commission/all_commission_page").forward(request, response);
      } catch (ServletException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return null;

    }
    return mav;
  }

  @Override
  public List<Commission> finAllByUserID(Long id) {
    // TODO Auto-generated method stub
    if (id == null) {
      return null;
    }
    List<Commission> lCommissions = commissionRepository.findAllByRevendeurUserid(id);
    return lCommissions;
  }

  @Override
  public String annulerCommission(Long idCommission, User userConnected) {
    // TODO Auto-generated method stub
    Commission commission = commissionRepository.findById(idCommission).get();

    if (commission.getStatut().equals("PAID") || commission.getStatut().equals("AWAINTING_INVOICING")) {
      return "erreur";
    }

    DemandeCommission demandeCommission =
        demandeCommissionRepository.findByCommissionIdAndStatut(idCommission, "IN_PROGRESS");
    if (demandeCommission != null) {

      demandeCommission.setStatut("CANCELLED");
      demandeCommission.setAcceptedBy(userConnected);
      demandeCommission.setDateDecission(new Date());
      demandeCommissionRepository.save(demandeCommission);
    }
    if (commission.getIsPromo()) {
      List<DetailsCommissionDemande> demandeAbonnemeentCommision =
          detailsCommissionDemandeRepository.findAllByCommissionId(commission.getId());
      if (demandeAbonnemeentCommision.size() > 0) {
        List<String> stringListOfReferenceDemande =
            demandeAbonnemeentCommision.stream().map(DetailsCommissionDemande::getReferenceClient) // Replace
                                                                                                   // with
                                                                                                   // the
                                                                                                   // actual
                                                                                                   // method
                                                                                                   // to
                // get the string
                .collect(Collectors.toList());


        demandeAbonnementRepository.updateIsCommisionSaved(stringListOfReferenceDemande, false);
      }
    }
    commission.setCancelledBy(userConnected);
    commission.setCancelledDate(new Date());
    commission.setStatut("CANCELLED");
    commissionRepository.save(commission);

    return "success";
  }

  @Override
  public String ajouterOffreCommission(String date, String type, Double montant, Integer debit,
      Integer palierMin, Integer palierMax, User user) {
    // TODO Auto-generated method stub
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    YearMonth yearMonth = YearMonth.parse(date, formatter);

    OffreCommission verif = offreCommissionRepository
        .findAllByAnneeAndMoisAndDebitAndPalierMinAndPalierMaxAndTypeAndIsActive(
            yearMonth.getYear(), yearMonth.getMonth().getValue(), debit, palierMin, palierMax, type,
            true);
    if (verif != null) {
      return "OFFER_ALREADY_EXISTS";
    }

    OffreCommission offreCommission = new OffreCommission();
    offreCommission.setAnnee(yearMonth.getYear());
    offreCommission.setMois(yearMonth.getMonth().getValue());
    offreCommission.setType(type);
    offreCommission.setMontant(montant);
    offreCommission.setDebit(debit);
    offreCommission.setCreatedBy(user);
    offreCommission.setPalierMin(palierMin);
    offreCommission.setPalierMax(palierMax);

    offreCommissionRepository.save(offreCommission);

    return "success";
  }

  @Override
  public String changementEtatOffre(Long idOffre, User user) {
    // TODO Auto-generated method stub
    OffreCommission offre = offreCommissionRepository.getById(idOffre);

    if (offre.getIsActive() || offreCommissionRepository
        .findAllByAnneeAndMoisAndIsActive(offre.getAnnee(), offre.getMois(), true).size() == 0) {
      offreCommissionRepository.changementEtat(idOffre, user.getUserid());
      return "success";
    } else {
      return "erreur";
    }

  }


  @Override
  public Boolean findExiteCommisionPromo(User user, Date dateDebut, Date dateFin) {
    // TODO Auto-generated method stub
    return commissionRepository.findExiteCommisionPromo(user, dateDebut, dateFin);
  }


  @Override
  public Page<CommissionDemDash> AllCommissionDashboard(int pageNo, int pageSize,
      String filterrecherche) {
    String date = null;
    YearMonth yearMonth = null;
    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("date"), "") && obj.getString("date") != null) {
        date = obj.getString("date");
      }
    }
    if (date == null) {
      LocalDate currrentDate = LocalDate.now();
      String formattedDate = currrentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
      yearMonth = YearMonth.parse(formattedDate, DateTimeFormatter.ofPattern("yyyy-MM"));
    } else {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
      yearMonth = YearMonth.parse(date, formatter);
    }
    LocalDate startOfMonth = yearMonth.atDay(1);
    LocalDate endOfMonth = yearMonth.atEndOfMonth();
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    if ((user.getRole().getRoleName()).equals("ROLE_CHEF_SECTEUR")) {
      Page<CommissionDemDash> calcul = demandeAbonnementRepository.calculeCommDemandeDash(
          user.getUserid(), CrmUtils.convertStringToDate(startOfMonth.toString()),
          CrmUtils.convertStringToLocalDateTime(endOfMonth.toString()), pageable);
      return calcul;
    } else {
      Page<CommissionDemDash> calcul = demandeAbonnementRepository.calculeCommDemandeDash(null,
          CrmUtils.convertStringToDate(startOfMonth.toString()),
          CrmUtils.convertStringToLocalDateTime(endOfMonth.toString()), pageable);
      return calcul;
    }
  }

@Override
public HashMap<String, Object> CalculeCommisionFrontFreelancer(Boolean isFreelancer   ,Long userid, String date) {
	HashMap<String, Object> result = new HashMap<>();


    HashMap<String, Object> hHapClaculeDemandeCommision = new HashMap<>();
    HashMap<String, Object> commissionPaiements = new HashMap<>();
    HashMap<String, Object> commissionFirstResults = new HashMap<>();
    HashMap<String, Object> commissionMiseEnservicePaiements = new HashMap<>();
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    YearMonth yearMonth = YearMonth.parse(date.trim(), formatter);
    Integer numMois = null;
    Integer annee = null;
    annee = yearMonth.getYear();
    numMois = yearMonth.getMonth().getValue();
    Commission existeCommision =
        commissionRepository.findCommisionByUserIdAndNotIsPromo(userid, annee, numMois);
    if (existeCommision == null) {
      hHapClaculeDemandeCommision = calculeCommDemande(isFreelancer,userid, date);
      hHapClaculeDemandeCommision.put("isFreelance", true);

      commissionPaiements = commissionFacture(userid, date);
      commissionMiseEnservicePaiements = commissionMiseEnservicePaiements(userid, date);
      commissionFirstResults = calculecommisionFirstFactureFreelancer(userid, date);
      

    } else {

      hHapClaculeDemandeCommision.put("totalDemandes", existeCommision.getNbrTotalDemandes());
      hHapClaculeDemandeCommision.put("demandesAccepte", existeCommision.getNbrDemandesAcceptees());
      hHapClaculeDemandeCommision.put("demandesRejete", existeCommision.getNbrDemandesRejetees());
      hHapClaculeDemandeCommision.put("demandeEnAttente",
          existeCommision.getNbrDemandesEnAttente());
      hHapClaculeDemandeCommision.put("nbFirsFactureNonPayed",
          existeCommision.getNbrDemandesNonRealisee());
      
      hHapClaculeDemandeCommision.put("primeMs", existeCommision.getPrimeCommision());

      hHapClaculeDemandeCommision.put("total", existeCommision.getMontantCommissionDemandes());
      hHapClaculeDemandeCommision.put("isFreelance", existeCommision.getIsFreelance());

      
      hHapClaculeDemandeCommision.put("detailsCommissionDemande",
          detailsCommissionDemandeRepository.findAllByCommissionId(existeCommision.getId()));
      /* commissionFirstResults */
      commissionFirstResults.put("nbCommissionActivation",
          existeCommision.getNbrDemandesActivees());
      commissionFirstResults.put("totalCommissions",
          existeCommision.getMontantCommissionPremiereFacture());
      commissionFirstResults.put("totalCommissionsActivationNewFreelance",
              existeCommision.getTotalCommissionsActivationNewFreelance());
      
      commissionFirstResults.put("totalCommissionsAvancePayee",
          existeCommision.getMontantAvancePremiereFacture());
      commissionFirstResults.put("totalCommissionsSansAvance",
          existeCommision.getMontantTotalPremiereFacture());
      commissionFirstResults.put("detailsCommissionFirstFactures",
          detailsCommissionPremiereFactureRepository
              .findAllByCommissionId(existeCommision.getId()));
      commissionFirstResults.put("nbFirsFactureNonVerse", 0);

      commissionFirstResults.put("listeRetardPayement",
    		  DetailsRetardCommissionPremiereFactureRepository
                  .findAllByCommissionId(existeCommision.getId()));
          commissionFirstResults.put("totalRetardPayement",existeCommision.getMontantRetardPayemnt());
      /* commissionPaiements */
      commissionPaiements.put("nbFacturePayee", existeCommision.getNbrFacturesPayees());
      commissionPaiements.put("totalCommissionsPaiement",
          existeCommision.getMontantCommissionPaiements());
      commissionPaiements.put("detailsCommissionFacture",
          detailsCommissionFactureRepository.findAllByCommissionId(existeCommision.getId()));
      commissionPaiements.put("nombreFactureNonVerse",
          existeCommision.getNbrFacturesNonVerseePayement());
      
      commissionMiseEnservicePaiements.put("nbrFactureMiseService", existeCommision.getNbrFactureMiseService());
      commissionMiseEnservicePaiements.put("montantCommissionPremiereFactureMiseService", existeCommision.getMontantCommissionPremiereFactureMiseService());
      commissionMiseEnservicePaiements.put("detailsCommissionFacture", detailsCommissionFactureMiseEnServiceRepository.findAllByCommissionId(existeCommision.getId()));

    }
    result.put("commissionActivation", commissionFirstResults);
    result.put("commissionPaiements", commissionPaiements);
    result.put("commissionDemandes", hHapClaculeDemandeCommision);
    result.put("commissionMiseEnservicePaiements", commissionMiseEnservicePaiements);

    return result;
}

public HashMap<String, Object>  commissionMiseEnservicePaiements(Long id, String date) {
	  LOGGER.info("Début de commissionFacture...");
	    LOGGER.info("Paramètres - id: {}, date: {}", id, date);

	    HashMap<String, Object> result = new HashMap<>();
	    List<DetailsCommissionPremiereFacture> listDetailsCommissionFactures = new ArrayList<>();

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
	    YearMonth yearMonth = YearMonth.parse(date, formatter);

	    LocalDate startOfMonth = yearMonth.atDay(1);
	    LocalDate endOfMonth = yearMonth.atEndOfMonth();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");

	    LOGGER.debug("Appel de bordereaurRepository.getBorderauToCommission(id, {}, {})...",
	        startOfMonth, endOfMonth);

	    List<Facture> listFactures =
	    		factureRepository.getFactureByMiseEnServiceToCommission(id, startOfMonth.toString(),
	            dateFormat.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString())));

	  

	    // Double totalMoantantFactureEcheance = 0.0;
	    AtomicReference<Double> totalMoantantFactureEcheance10 = new AtomicReference<>(0.0);

	    AtomicReference<Double> totalMoantantFactureEcheance20 = new AtomicReference<>(0.0);

	    AtomicReference<Double> totalMoantantFactureEcheance30 = new AtomicReference<>(0.0);

	    AtomicReference<Double> totalMoantantFactureEcheance50 = new AtomicReference<>(0.0);

	    AtomicReference<Double> totalMoantantFactureEcheance100 = new AtomicReference<>(0.0);
	    // Double totalMoantantHorsFactureEcheance = 0.0;

	    listFactures.forEach(en -> {

	      Facture facture = en;
	      AtomicReference<String> debitpack = new AtomicReference<>("10");
	    

	      // detail commission sur facture
	      DetailsCommissionPremiereFacture detailsCommissionFacture = new DetailsCommissionPremiereFacture();
	      detailsCommissionFacture.setReferenceFacture(facture.getRef_facture());
	      detailsCommissionFacture.setReferenceClient(facture.getAbonnement().getReferenceClient());
	      AtomicReference<Double> montant = new AtomicReference<>(facture.getMontantHt());

	
          debitpack.updateAndGet(value -> en.getAbonnement().getPack().getDebitPack());

	      // detail commission sur facture
	      Boolean isEcheance = true;
	      AtomicReference<Double> montantCommission = new AtomicReference<>(0.0);
	      switch (debitpack.get()) {
	        case "10":

	        	 montantCommission.set( 
	                getMontantFirstFactureByPalier(listFactures.size(), 10, isEcheance));
	        	  totalMoantantFactureEcheance10.updateAndGet(value -> value + montantCommission.get());
	        	  break;
	        case "12":

	        	 montantCommission.set( 
	                getMontantFirstFactureByPalier(listFactures.size(), 12, isEcheance));
	        	  totalMoantantFactureEcheance10.updateAndGet(value -> value + montantCommission.get());
	            break;
	        case "20":

	        	 montantCommission.set( 
	                getMontantFirstFactureByPalier(listFactures.size(), 20, isEcheance));
	        	  totalMoantantFactureEcheance20.updateAndGet(value -> value + montantCommission.get());
	            break;
	        case "30":

	        	 montantCommission.set( 
	                getMontantFirstFactureByPalier(listFactures.size(), 30, isEcheance));
	        	  totalMoantantFactureEcheance30.updateAndGet(value -> value + montantCommission.get());
	            
		          break;
		        case "50":

		        	 montantCommission.set( 
	                getMontantFirstFactureByPalier(listFactures.size(), 50, isEcheance));
		        	  totalMoantantFactureEcheance50.updateAndGet(value -> value + montantCommission.get());
	            break;
		        case "100":

		         	 montantCommission.set( 
	                getMontantFirstFactureByPalier(listFactures.size(), 100, isEcheance));
		        	  totalMoantantFactureEcheance100.updateAndGet(value -> value + montantCommission.get());
		          break;

	          }
	  
	 

	      // detail commission sur facture
	   
	      detailsCommissionFacture.setIsEcheance(isEcheance);
	      detailsCommissionFacture.setMontantCommisison(CrmUtils.truncatedDouble(montantCommission.get() *.3));
	      listDetailsCommissionFactures.add(detailsCommissionFacture);

	    });

	    Double commissionPayementFacture12Mo = totalMoantantFactureEcheance10.get();
	    Double commissionPayementFacture20Mo = totalMoantantFactureEcheance20.get();
	    Double commissionPayementFacture30Mo = totalMoantantFactureEcheance30.get();
	    Double commissionPayementFacture50Mo = totalMoantantFactureEcheance50.get();

	    Double commissionPayementFacture100Mo = totalMoantantFactureEcheance100.get();

	    Double commissionFacture = CrmUtils.truncatedDouble((commissionPayementFacture12Mo + commissionPayementFacture20Mo
	        + commissionPayementFacture30Mo + commissionPayementFacture50Mo
	        + commissionPayementFacture100Mo) *.3 );

	    HashMap<String, Object> commissionPaiements = new HashMap<>();
	    commissionPaiements.put("nbrFactureMiseService",
	        String.valueOf(listFactures.size()));
	    commissionPaiements.put("montantCommissionPremiereFactureMiseService", commissionFacture.toString());
	    commissionPaiements.put("detailsCommissionFacture", listDetailsCommissionFactures);



	    LOGGER.debug("Total commissions de paiement de facture calculé: {}", commissionFacture);



	    LOGGER.info("commissionFacture terminée.");

	    return commissionPaiements;
	  
}

public HashMap<String, Object> calculecommisionFirstFactureFreelancer(Long id, String date) {

 
	List<DetailsCommissionPremiereFacture> listeDetailsCommissionFirstFactures = new ArrayList<>();
    AtomicInteger countPremiereFactureEcheance10 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureEcheance12 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureEcheance20 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureEcheance30 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureEcheance50 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureEcheance100 = new AtomicInteger(0);


    AtomicInteger countPremiereFactureHorsEcheance10 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureHorsEcheance12 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureHorsEcheance20 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureHorsEcheance30 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureHorsEcheance50 = new AtomicInteger(0);
    AtomicInteger countPremiereFactureHorsEcheance100 = new AtomicInteger(0);


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    YearMonth yearMonth = YearMonth.parse(date, formatter);

    LocalDate startOfMonth = yearMonth.atDay(1);
    LocalDate endOfMonth = yearMonth.atEndOfMonth();
    Double commissionPremierFacture = 0.0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");
    AtomicReference<String> debitpack = new AtomicReference<>("10");
    List<Facture> ListeFirstFacture = factureRepository
        .findListFirstFactureByPayementtDateToCalculeCommisionAndNotCommissionIsPayed(id, startOfMonth.toString(),
            dateFormat.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString())));
    List<Long> factureIds = ListeFirstFacture.stream()
    	    .map(Facture::getFactureId) 
    	    .collect(Collectors.toList()); 
    List<Encaissement> ListeencasementByFirstFacture = encaissementRepository.findByFacturesIds(factureIds);
    List<Facture> listFacturesMiseEnService =
    		factureRepository.getFactureByMiseEnServiceToCommission(id, startOfMonth.toString(),
            dateFormat.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString())));
    ListeFirstFacture.forEach(facture -> {
      if (facture.getIsFirstFacture() == true) {
        facture.getEntriesFacture().forEach(ent -> {

          if (ent.getPack() != null) {
            debitpack.updateAndGet(value -> ent.getPack().getDebitPack());
          }
        });
        Optional<Encaissement> matchingEncaissement = ListeencasementByFirstFacture.stream()
        	    .filter(e -> e.getFacture().getFactureId().equals(facture.getFactureId()))
        	    .findFirst();
        
        Encaissement encaissementFirstFacture = null ;
        if (matchingEncaissement.isPresent()) {
        	encaissementFirstFacture = matchingEncaissement.get();
        }
        DetailsCommissionPremiereFacture detailsCommissionPremiereFacture =
            new DetailsCommissionPremiereFacture();
        detailsCommissionPremiereFacture.setReferenceFacture(facture.getRef_facture());
        // detail commission sur facture
        Boolean isEcheance = null;
        Double montantCommission = null;
if(encaissementFirstFacture != null) {
	System.out.println(encaissementFirstFacture.getEncaissementId());
}
     /*   Date dateDeVersement = facture.getDateDeVersement();
        Date dateDePayement = facture.getDateDePayement();
*/
        detailsCommissionPremiereFacture
            .setReferenceClient(facture.getAbonnement().getReferenceClient());
   //     long differenceInMilliseconds =
     //       Math.abs(dateDeVersement.getTime() - dateDePayement.getTime());
      //  long differenceInDays =
       //     TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);
        if ((
        		(encaissementFirstFacture != null && encaissementFirstFacture.getUser().getUserid().equals(facture.getAbonnement().getUser().getUserid())))
        		
        		|| encaissementFirstFacture == null ||
        		(encaissementFirstFacture != null && !encaissementFirstFacture.getUser().getUserid().equals(facture.getAbonnement().getUser().getUserid()))) {
          isEcheance = true;
          if (debitpack.get().equals("10")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 10, isEcheance);
            countPremiereFactureEcheance10.incrementAndGet();
          } else if (debitpack.get().equals("12")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 12, isEcheance);
            countPremiereFactureEcheance12.incrementAndGet();
          } else if (debitpack.get().equals("20")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 20, isEcheance);
            countPremiereFactureEcheance20.incrementAndGet();
          } else if (debitpack.get().equals("30")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 30, isEcheance);
            countPremiereFactureEcheance30.incrementAndGet();
          } else if (debitpack.get().equals("50")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 50, isEcheance);
            countPremiereFactureEcheance50.incrementAndGet();
          } else if (debitpack.get().equals("100")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 100, isEcheance);
            countPremiereFactureEcheance100.incrementAndGet();
          }

        } else {
          isEcheance = false;
          if (debitpack.get().equals("10")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 10, isEcheance);
            countPremiereFactureHorsEcheance10.incrementAndGet();
          } else if (debitpack.get().equals("12")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 12, isEcheance);
            countPremiereFactureHorsEcheance12.incrementAndGet();
          } else if (debitpack.get().equals("20")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 20, isEcheance);
            countPremiereFactureHorsEcheance20.incrementAndGet();
          } else if (debitpack.get().equals("30")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 30, isEcheance);
            countPremiereFactureHorsEcheance30.incrementAndGet();
          } else if (debitpack.get().equals("50")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 50, isEcheance);
            countPremiereFactureHorsEcheance50.incrementAndGet();
          } else if (debitpack.get().equals("100")) {

            montantCommission =
                getMontantFirstFactureByPalier(listFacturesMiseEnService.size(), 100, isEcheance);
            countPremiereFactureHorsEcheance100.incrementAndGet();
          }

        }
        detailsCommissionPremiereFacture.setIsEcheance(isEcheance);
        detailsCommissionPremiereFacture.setMontantCommisison( CrmUtils.truncatedDouble(montantCommission *0.7));
        detailsCommissionPremiereFacture.setFromClient(false);
        listeDetailsCommissionFirstFactures.add(detailsCommissionPremiereFacture);

      }
    });
    
    List<Facture> factSauvagrder =  factureRepository.findAbonnementsByIsPayedAndDemandeAbonnementStatutIn(id, startOfMonth.toString(),
            dateFormat.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString()))) ;

    AtomicInteger sumPremiereFactureWithAbn = new AtomicInteger(0);
    AtomicReference<Double> totalMoantantsumPremiereFactureWithAbn= new AtomicReference<>(0.0);

    factSauvagrder.forEach(abn -> {
    	DetailsCommissionPremiereFacture detailsCommissionPremiereFacture =
                new DetailsCommissionPremiereFacture();
            detailsCommissionPremiereFacture.setReferenceFacture(abn.getRef_facture());
    	  detailsCommissionPremiereFacture.setIsEcheance(true);
          detailsCommissionPremiereFacture
          .setReferenceClient(abn.getAbonnement().getReferenceClient());
          detailsCommissionPremiereFacture.setFromClient(true);
          listeDetailsCommissionFirstFactures.add(detailsCommissionPremiereFacture);
          if(abn.getAbonnement().getComisionActivationFreelancer() != null) {
              detailsCommissionPremiereFacture.setMontantCommisison( CrmUtils.truncatedDouble(abn.getAbonnement().getComisionActivationFreelancer()));

              totalMoantantsumPremiereFactureWithAbn.updateAndGet(value -> value +abn.getAbonnement().getComisionActivationFreelancer());

          }
          else {
              detailsCommissionPremiereFacture.setMontantCommisison(0.0);

          }
          sumPremiereFactureWithAbn.incrementAndGet();

    });
    Integer sumPremiereFacture =
        countPremiereFactureEcheance10.get() + countPremiereFactureEcheance12.get()
            + countPremiereFactureEcheance20.get() + countPremiereFactureEcheance30.get()
            + countPremiereFactureEcheance50.get() + countPremiereFactureEcheance100.get()
            + countPremiereFactureHorsEcheance10.get() + countPremiereFactureHorsEcheance12.get()
            + countPremiereFactureHorsEcheance20.get() + countPremiereFactureHorsEcheance30.get()
            + countPremiereFactureHorsEcheance50.get() + countPremiereFactureHorsEcheance100.get() + sumPremiereFactureWithAbn.get();
            
    // Echeance 10
    if (listFacturesMiseEnService.size() >= 0 && listFacturesMiseEnService.size() <= 49) {
      commissionPremierFacture = calculeCommisionActivation(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture) ;

    } else if (listFacturesMiseEnService.size() >= 50 && listFacturesMiseEnService.size() <= 99) {

      commissionPremierFacture = commisionactivationpalier50To99(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);

    } else if (listFacturesMiseEnService.size() >= 100 && listFacturesMiseEnService.size() <= 199) {
      commissionPremierFacture = comissionActivationPaliersup100(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);
    } else if (listFacturesMiseEnService.size() >= 200 && listFacturesMiseEnService.size() <= 299) {
      commissionPremierFacture = comissionActivationPaliersup200(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);
    } else if (listFacturesMiseEnService.size() >= 300) {
      commissionPremierFacture = comissionActivationPaliersup300(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);
    }

    List<AvanceCommissionAcquisition> listCommisionEnPayee = avanceCommissionAcquisitionRepository
        .findByMonthAndStatusAndUser(id, startOfMonth.toString(),
            dateFormat.format(CrmUtils.convertStringToLocalDateTime(endOfMonth.toString())),
            StatutAvanceBordereau.AVANCE_PAYED);
    Double totalcommisionPayed = listCommisionEnPayee.stream()
        .mapToDouble(commision -> commision.getMontantCommissionPremiereFacture()).sum();
    LOGGER.debug("Total de première facture calculé: {}", listFacturesMiseEnService.size());
    LOGGER.debug("Total commissions de paiement de première facture calculé: {}",
        commissionPremierFacture * 0.7);
    
    List<Encaissement> listeRetardPayement =  encaissementRepository.findByCommsionMothAndUserAndEchanceFacturesRetard(startOfMonth.toString() ,
    	endOfMonth.toString() , id);
    List<Encaissement> listeechancePayement =  encaissementRepository.findByCommsionMothAndUserAndEchanceFactures(startOfMonth.toString() ,
        	endOfMonth.toString() , id);
    Double restePayement = ( CrmUtils.truncatedDouble(totalMoantantsumPremiereFactureWithAbn.get() + (commissionPremierFacture *.7)) - totalcommisionPayed) ;
    
    HashMap<String, Object> commissionActivation = new HashMap<>();
    Double Commisionfinale = commissionPremierFacture * 0.7 + totalMoantantsumPremiereFactureWithAbn.get() ;
    Double CommisionfinaleActivation = commissionPremierFacture * 0.7 + totalMoantantsumPremiereFactureWithAbn.get();

    commissionActivation.put("nbCommissionActivation", sumPremiereFacture.toString());
    commissionActivation.put("totalCommissions", restePayement.toString());
    commissionActivation.put("totalCommissionsAvancePayee", totalcommisionPayed.toString());
    commissionActivation.put("totalCommissionsSansAvance",Commisionfinale.toString() );
    commissionActivation.put("totalRetardPayement", listeRetardPayement.size() * 3 );
    commissionActivation.put("totalCommissionsActivationNewFreelance",CommisionfinaleActivation.toString() );
    commissionActivation.put("totalechancePayement", listeechancePayement.size() * 3 );
    commissionActivation.put("listeRetardPayement", listeRetardPayement);
    commissionActivation.put("listeechancePayement", listeechancePayement);
    commissionActivation.put("detailsCommissionFirstFactures", listeDetailsCommissionFirstFactures);

 
    return commissionActivation;

 
  }

@Override
public List<DemandeCommission> finAllByFilterForFactureMultiple(Integer dateFacts, Integer month , String codeRevendeurFacts,
		Date startCreatedDateFacts, Date endCreatedDateFacts, String referenceFacts) {
	// TODO Auto-generated method stub
	return    demandeCommissionRepository.findCommisionForFazctureMultiple(dateFacts,month,codeRevendeurFacts,startCreatedDateFacts ,endCreatedDateFacts,referenceFacts );
}

@Override
public String changeCommissionToAwaiting(Long id, User user) {
	// TODO Auto-generated method stub
	 Commission commission = commissionRepository.findById(id).get();
	  DemandeCommission demandeCommission =
		        demandeCommissionRepository.findByCommissionIdAndStatut(id, "IN_PROGRESS");
		    if (demandeCommission != null) {
		    	commission.setStatut("AWAINTING_INVOICING");
			    commissionRepository.save(commission);
			    demandeCommission.setStatut("AWAINTING_INVOICING");
			    demandeCommissionRepository.save(demandeCommission);
				return "true";

		    }
		    return "false";
}

@Override
public String changeCommissionGroupToAwaiting(Long commissionGoupId, User user) {
	DemandeCommissionGroup commission = demandeCommissionGroupRepository.findById(commissionGoupId).get();
	  
	commission.getDemandeCommissions().forEach(el -> {
		el.setStatut("AWAINTING_INVOICING");
		el.getCommission().setStatut("AWAINTING_INVOICING");
	});
	commission.setStatut("AWAINTING_INVOICING");
	
	demandeCommissionGroupRepository.save(commission);
	
		    return "true";

}




}
