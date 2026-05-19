package crm.chifco.com.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.DTOclass.CalculeCommisionDemande;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.DemandeCommission;
import crm.chifco.com.model.DetailsCommissionDemande;
import crm.chifco.com.model.DetailsCommissionFacture;
import crm.chifco.com.model.DetailsCommissionPremiereFacture;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.OffreCommissionPromo;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.CommissionRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.DemandeCommissionRepository;
import crm.chifco.com.repository.DetailsCommissionDemandeRepository;
import crm.chifco.com.repository.DetailsCommissionFactureRepository;
import crm.chifco.com.repository.DetailsCommissionPremiereFactureRepository;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.OffreCommissionPromoRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.CommissionExcelExport;
import crm.chifco.com.service.CommissionPromoService;
import crm.chifco.com.utils.CrmUtils;

@Service
public class CommissionPromoServiceImpl implements CommissionPromoService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  @Autowired
  CommissionRepository commissionRepository;

  @Autowired
  private DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  private EncaissementRepository encaissementRepository;

  @Autowired
  private FactureRepository factureRepository;

  @Autowired
  private DemandeCommissionRepository demandeCommissionRepository;

  @Autowired
  private OffreCommissionPromoRepository offreCommissionPromoRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  private DetailsCommissionDemandeRepository detailsCommissionDemandeRepository;


  @Autowired
  private DetailsCommissionFactureRepository detailsCommissionFactureRepository;

  @Autowired
  private DetailsCommissionPremiereFactureRepository detailsCommissionPremiereFactureRepository;
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

  @Value("${promo.commision.palier.one.min}")
  String promoCommisionPalierOneMins;

  @Value("${promo.commision.palier.one.max}")
  String promoCommisionPalierOneMAxs;


  @Value("${promo.commision.palier.two.min}")
  String promoCommisionPalierTwoMins;

  @Value("${promo.commision.palier.two.max}")
  String promoCommisionPalierTwoMAxs;

  @Value("${promo.commision.palier.three.min}")
  String promoCommisionPalierThreeMins;

  @Value("${promo.commision.palier.three.max}")
  String promoCommisionPalierThreeMAxs;

  @Value("${promo.commision.palier.four.min}")
  String promoCommisionPalierFourMins;

  @Value("${promo.commision.palier.four.max}")
  String promoCommisionPalierFourMAxs;

  @Value("${promo.commision.palier.five.min}")
  String promoCommisionPalierFiveMins;



  @Override
  public HashMap<String, Object> getAll(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche) {
    // TODO Auto-generated method stub

    String codeRevendeur = null;
    Integer numMois = null;
    Integer annee = null;
    String statut = null;
    Date startCreatedDate = null;
    Date endCreatedDate = null;
    String reference = null;

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
    }

    int currentpage = start / length;
    Page<OffreCommissionPromo> responseData = null;
    HashMap<String, Object> myHmapData = new HashMap<>();

    Sort sort = Sort.by(Sort.Direction.DESC, "id");
    Pageable pageable = PageRequest.of(currentpage, length, sort);


    responseData = offreCommissionPromoRepository.findAll(pageable);



    if (responseData != null)

    {
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
  public HashMap<String, Object> CalculeCommisionFront(long UserId, Long IdOffrePromo) {
    // TODO Auto-generated method stub

    HashMap<String, Object> result = new HashMap<>();
    OffreCommissionPromo detailOffrePromos =
        offreCommissionPromoRepository.findAllByIdAndIsActive(IdOffrePromo, true);

    HashMap<String, Object> hHapClaculeDemandeCommision = new HashMap<>();
    HashMap<String, Object> commissionPaiements = new HashMap<>();
    HashMap<String, Object> commissionFirstResults = new HashMap<>();


    Commission existeCommision = commissionRepository
        .findCommisionPromoByRevendeur_useridAndPeriodPromoDebutAndPeriodPromoFinAndIsPromoAndStatutNot(
            UserId, detailOffrePromos.getDateDebut(), detailOffrePromos.getDateFin(), true,
            "CANCELLED");
    if (existeCommision == null) {
      hHapClaculeDemandeCommision = calculeCommDemande(UserId, detailOffrePromos);

      // commissionPaiements = commissionFacture(UserId, detailOffrePromos);
      // commissionFirstResults = calculecommisionFirstFacture(UserId, detailOffrePromos);

    } else {

      hHapClaculeDemandeCommision.put("totalDemandes", existeCommision.getNbrTotalDemandes());
      hHapClaculeDemandeCommision.put("demandesAccepte", existeCommision.getNbrDemandesAcceptees());
      hHapClaculeDemandeCommision.put("demandesRejete", existeCommision.getNbrDemandesRejetees());
      hHapClaculeDemandeCommision.put("demandeEnAttente",
          existeCommision.getNbrDemandesEnAttente());
      hHapClaculeDemandeCommision.put("nbFirsFactureNonPayed",
          existeCommision.getNbrDemandesNonRealisee());

      hHapClaculeDemandeCommision.put("total", existeCommision.getMontantCommissionDemandes());
      hHapClaculeDemandeCommision.put("detailsCommissionDemande",
          detailsCommissionDemandeRepository.findAllByCommissionId(existeCommision.getId()));
      /* commissionFirstResults */
      commissionFirstResults.put("nbCommissionActivation",
          existeCommision.getNbrDemandesActivees());
      commissionFirstResults.put("totalCommissions",
          existeCommision.getMontantCommissionPremiereFacture());
      commissionFirstResults.put("detailsCommissionFirstFactures",
          detailsCommissionPremiereFactureRepository
              .findAllByCommissionId(existeCommision.getId()));
      commissionFirstResults.put("nbFirsFactureNonVerse", 0);

      /* commissionPaiements */
      commissionPaiements.put("nbFacturePayee", existeCommision.getNbrFacturesPayees());
      commissionPaiements.put("totalCommissionsPaiement",
          existeCommision.getMontantCommissionPaiements());
      commissionPaiements.put("detailsCommissionFacture",
          detailsCommissionFactureRepository.findAllByCommissionId(existeCommision.getId()));
      commissionPaiements.put("nombreFactureNonVerse",
          existeCommision.getNbrFacturesNonVerseePayement());

    }
    // result.put("commissionActivation", commissionFirstResults);
    // result.put("commissionPaiements", commissionPaiements);
    result.put("commissionDemandes", hHapClaculeDemandeCommision);

    return result;
  }

  public HashMap<String, Object> calculeCommDemande(Long userId,
      OffreCommissionPromo OffreCommissionPromo) {
    LOGGER.info("Début de calculeCommDemande...");
    LOGGER.debug("Paramètres - UserId: {}, Date: {}", userId,
        OffreCommissionPromo.getNomCommisionPromo());

    // calculeCommDemande = new CalculeCommDemande(null, null, null);

    long promoCommisionPalierOneMin = Long.parseLong(promoCommisionPalierOneMins);
    long promoCommisionPalierOneMAx = Long.parseLong(promoCommisionPalierOneMAxs);
    long promoCommisionPalierTwoMin = Long.parseLong(promoCommisionPalierTwoMins);
    long promoCommisionPalierTwoMAx = Long.parseLong(promoCommisionPalierTwoMAxs);
    long promoCommisionPalierThreeMin = Long.parseLong(promoCommisionPalierThreeMins);
    long promoCommisionPalierThreeMAx = Long.parseLong(promoCommisionPalierThreeMAxs);
    long promoCommisionPalierFourMin = Long.parseLong(promoCommisionPalierFourMins);
    long promoCommisionPalierFourMAx = Long.parseLong(promoCommisionPalierFourMAxs);
    long promoCommisionPalierFiveMin = Long.parseLong(promoCommisionPalierFiveMins);
    List<DetailsCommissionDemande> listDetailsCommissionDemandes = new ArrayList<>();


    Calendar calendar = Calendar.getInstance();
    calendar.setTime(OffreCommissionPromo.getDateFin());

    // Add one day
    calendar.add(Calendar.DAY_OF_MONTH, 1);

    // Convert Calendar back to Date
    Date newDateFin = calendar.getTime();

    LOGGER.debug("Appel de demandeAbonnementRepository.calculeCommDemande(UserId)...");
    CalculeCommisionDemande calcule = demandeAbonnementRepository.calculeCommDemande(userId,
        OffreCommissionPromo.getDateDebut(), newDateFin);

    User userConnected = userRepository.findUsersByUserid(userId);
    HashMap<String, Object> result = new HashMap<>();
    if (calcule != null) {
      Double toTaleCommision = 0.0;
      result.put("totalDemandes", calcule.getCountAllDemandeClassification().toString());
      result.put("demandesAccepte", calcule.getCountAllDemandeAccecpted().toString());
      result.put("demandesRejete", calcule.getCountAllDemandeRejected().toString());
      result.put("demandeEnAttente", calcule.getCountAllDemandeEnAttente().toString());

      // int totalRefus = 20;
      int totalActivation = 25;

      if (userConnected.getPcActivationCommision() != null) {
        totalActivation = userConnected.getPcActivationCommision().intValue();
      }

      /*
       * if (userConnected.getPcRefusCommision() != null) { totalRefus =
       * userConnected.getPcRefusCommision().intValue(); }
       */
      /*
       * Long TwentyPercentOfDemandeRejacted = (calcule.getCountAllDemandeClassification() *
       * totalRefus / 100);
       */
      Double TwentyFivePrecentOfDemandeRealised =
          (calcule.getCountAllDemandeAccecpted() * totalActivation) / 100.0;


      if (TwentyFivePrecentOfDemandeRealised <= calcule.getCountDemandePasseToActivate()) {
        LOGGER.debug("Calcul des commissions en fonction de la décision...");



        if (calcule.getCountAllDemandeAccecpted() >= promoCommisionPalierFiveMin) {
          toTaleCommision = toTaleCommision
              + (OffreCommissionPromo.getMontantDemandePalier5()
                  * calcule.getCountDemandeAcceptedDebit10Or12())
              + (OffreCommissionPromo.getMontantDemandePalier5()
                  * calcule.getCountDemandeAcceptedDebit20())
              + (OffreCommissionPromo.getMontantDemandePalier5()
                  * calcule.getCountDemandeAcceptedDebit30())
              + (OffreCommissionPromo.getMontantDemandePalier5()
                  * calcule.getCountDemandeAcceptedDebit50())
              + (OffreCommissionPromo.getMontantDemandePalier5()
                  * calcule.getCountDemandeAcceptedDebit100());

        } else if (calcule.getCountAllDemandeAccecpted() >= promoCommisionPalierFourMin
            && calcule.getCountAllDemandeAccecpted() <= promoCommisionPalierFourMAx) {
          toTaleCommision = toTaleCommision
              + (OffreCommissionPromo.getMontantDemandePalier4()
                  * calcule.getCountDemandeAcceptedDebit10Or12())
              + (OffreCommissionPromo.getMontantDemandePalier4()
                  * calcule.getCountDemandeAcceptedDebit20())
              + (OffreCommissionPromo.getMontantDemandePalier4()
                  * calcule.getCountDemandeAcceptedDebit30())
              + (OffreCommissionPromo.getMontantDemandePalier4()
                  * calcule.getCountDemandeAcceptedDebit50())
              + (OffreCommissionPromo.getMontantDemandePalier4()
                  * calcule.getCountDemandeAcceptedDebit100());

        } else if (calcule.getCountAllDemandeAccecpted() >= promoCommisionPalierThreeMin
            && calcule.getCountAllDemandeAccecpted() <= promoCommisionPalierThreeMAx) {
          toTaleCommision = toTaleCommision
              + (OffreCommissionPromo.getMontantDemandePalier3()
                  * calcule.getCountDemandeAcceptedDebit10Or12())
              + (OffreCommissionPromo.getMontantDemandePalier3()
                  * calcule.getCountDemandeAcceptedDebit20())
              + (OffreCommissionPromo.getMontantDemandePalier3()
                  * calcule.getCountDemandeAcceptedDebit30())
              + (OffreCommissionPromo.getMontantDemandePalier3()
                  * calcule.getCountDemandeAcceptedDebit50())
              + (OffreCommissionPromo.getMontantDemandePalier3()
                  * calcule.getCountDemandeAcceptedDebit100());

        } else if (calcule.getCountAllDemandeAccecpted() >= promoCommisionPalierTwoMin
            && calcule.getCountAllDemandeAccecpted() <= promoCommisionPalierTwoMAx) {
          toTaleCommision = toTaleCommision
              + (OffreCommissionPromo.getMontantDemandePalier2()
                  * calcule.getCountDemandeAcceptedDebit10Or12())
              + (OffreCommissionPromo.getMontantDemandePalier2()
                  * calcule.getCountDemandeAcceptedDebit20())
              + (OffreCommissionPromo.getMontantDemandePalier2()
                  * calcule.getCountDemandeAcceptedDebit30())
              + (OffreCommissionPromo.getMontantDemandePalier2()
                  * calcule.getCountDemandeAcceptedDebit50())
              + (OffreCommissionPromo.getMontantDemandePalier2()
                  * calcule.getCountDemandeAcceptedDebit100());

        } else if (calcule.getCountAllDemandeAccecpted() >= promoCommisionPalierOneMin
            && calcule.getCountAllDemandeAccecpted() <= promoCommisionPalierOneMAx) {
          toTaleCommision = toTaleCommision
              + (OffreCommissionPromo.getMontantDemandePalier1()
                  * calcule.getCountDemandeAcceptedDebit10Or12())
              + (OffreCommissionPromo.getMontantDemandePalier1()
                  * calcule.getCountDemandeAcceptedDebit20())
              + (OffreCommissionPromo.getMontantDemandePalier1()
                  * calcule.getCountDemandeAcceptedDebit30())
              + (OffreCommissionPromo.getMontantDemandePalier1()
                  * calcule.getCountDemandeAcceptedDebit50())
              + (OffreCommissionPromo.getMontantDemandePalier1()
                  * calcule.getCountDemandeAcceptedDebit100());

        }
        // if (toTaleCommision > 0) {
        List<Object[]> results = demandeAbonnementRepository.getDemandeDtailsCommissionPromo(userId,
            OffreCommissionPromo.getDateDebut(), newDateFin);
        for (Object[] res : results) {

          String reference = (String) res[0];
          String statut = (String) res[1];
          String nomStatut = (String) res[2];

          DetailsCommissionDemande d = new DetailsCommissionDemande();
          d.setReferenceClient(reference);
          if (statut.equals("INSTALLED") || statut.equals("ASSIGNED") || statut.equals("VALID")) {
            d.setEtatCommission(false);
            d.setStatutCommission(nomStatut);
          }
          d.setStatutCommission(nomStatut);
          listDetailsCommissionDemandes.add(d);

        }

        // }

      }
      result.put("total", toTaleCommision.toString());
    } else {
      result.put("totalDemandes", "0");
      result.put("demandesAccepte", "0");
      result.put("nbFirsFactureNonPayed", 0);
      result.put("total", "0");
    }

    // demande non réalisée
    Integer firstFactureNonpayeeandNonVerse = factureRepository.nombreFirstFactureNonpayeeandVerse(
        userId, OffreCommissionPromo.getDateDebut().toString(),
        OffreCommissionPromo.getDateFin().toString());
    result.put("nbFirsFactureNonPayed", firstFactureNonpayeeandNonVerse);

    result.put("detailsCommissionDemande", listDetailsCommissionDemandes);
    LOGGER.info("calculeCommDemande terminée.");
    return result;
  }



  public HashMap<String, Object> calculecommisionFirstFacture(Long id,
      OffreCommissionPromo detailOffrePromos) {
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



    long promoCommisionPalierOneMin = Long.parseLong(promoCommisionPalierOneMins);
    long promoCommisionPalierOneMAx = Long.parseLong(promoCommisionPalierOneMAxs);
    long promoCommisionPalierTwoMin = Long.parseLong(promoCommisionPalierTwoMins);
    long promoCommisionPalierTwoMAx = Long.parseLong(promoCommisionPalierTwoMAxs);
    long promoCommisionPalierThreeMin = Long.parseLong(promoCommisionPalierThreeMins);
    long promoCommisionPalierThreeMAx = Long.parseLong(promoCommisionPalierThreeMAxs);
    long promoCommisionPalierFourMin = Long.parseLong(promoCommisionPalierFourMins);
    long promoCommisionPalierFourMAx = Long.parseLong(promoCommisionPalierFourMAxs);
    long promoCommisionPalierFiveMin = Long.parseLong(promoCommisionPalierFiveMins);

    Double commissionPremierFacture = 0.0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");
    AtomicReference<String> debitpack = new AtomicReference<>("10");
    List<Facture> ListeFirstFacture =
        factureRepository.findListFirstFactureByVersementDateToCalculeCommision(id,
            detailOffrePromos.getDateDebut().toString(), detailOffrePromos.getDateFin().toString());
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
    if (sumPremiereFacture >= 0 && sumPremiereFacture <= promoCommisionPalierThreeMAx) {
      commissionPremierFacture = calculeCommisionActivation(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);

    } else if (sumPremiereFacture >= promoCommisionPalierFourMin
        && sumPremiereFacture <= promoCommisionPalierFourMAx) {

      commissionPremierFacture = commisionactivationpalier51To99(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);

    } else if (sumPremiereFacture >= promoCommisionPalierFiveMin) {
      commissionPremierFacture = comissionActivationPaliersup100(countPremiereFactureEcheance10,
          countPremiereFactureEcheance12, countPremiereFactureEcheance20,
          countPremiereFactureEcheance30, countPremiereFactureEcheance50,
          countPremiereFactureEcheance100, countPremiereFactureHorsEcheance10,
          countPremiereFactureHorsEcheance12, countPremiereFactureHorsEcheance20,
          countPremiereFactureHorsEcheance30, countPremiereFactureHorsEcheance50,
          countPremiereFactureHorsEcheance100, commissionPremierFacture);
    }

    LOGGER.debug("Total de première facture calculé: {}", sumPremiereFacture);
    LOGGER.debug("Total commissions de paiement de première facture calculé: {}",
        commissionPremierFacture);
    HashMap<String, Object> commissionActivation = new HashMap<>();
    commissionActivation.put("nbCommissionActivation", sumPremiereFacture.toString());
    commissionActivation.put("totalCommissions", commissionPremierFacture.toString());
    commissionActivation.put("detailsCommissionFirstFactures", listeDetailsCommissionFirstFactures);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    Integer firstFactureNonVerse = factureRepository.nombreFirstFactureNonVerse(id,
        detailOffrePromos.getDateDebut().toString(), sdf.format(
            CrmUtils.convertStringToLocalDateTime(detailOffrePromos.getDateFin().toString())));

    commissionActivation.put("nbFirsFactureNonVerse", firstFactureNonVerse);
    return commissionActivation;
  }

  public HashMap<String, Object> commissionFacture(Long id,
      OffreCommissionPromo detailOffrePromos) {

    LOGGER.info("Début de commissionFacture...");
    LOGGER.info("Paramètres - id: {}, Nom offre: {}", id, detailOffrePromos.getNomCommisionPromo());

    HashMap<String, Object> result = new HashMap<>();
    List<DetailsCommissionFacture> listDetailsCommissionFactures = new ArrayList<>();



    List<Encaissement> listEncaissements = encaissementRepository.getEncaissementToCommission(id,
        detailOffrePromos.getDateDebut().toString(), detailOffrePromos.getDateFin().toString());

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
        detailOffrePromos.getDateDebut().toString(), detailOffrePromos.getDateFin().toString());
    commissionPaiements.put("nombreFactureNonVerse", nombreFactureNonVerse);

    LOGGER.debug("Total commissions de paiement de facture calculé: {}", commissionFacture);



    LOGGER.info("commissionFacture terminée.");

    return commissionPaiements;
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

  private Double commisionactivationpalier51To99(AtomicInteger countPremiereFactureEcheance10,
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
      } else if (countFirstFacture >= 100) {
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
      } else if (countFirstFacture >= 100) {
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
      String codeRevendeur, Date startCreatedDate, Date endCreatedDate, String reference,
      HttpServletRequest request, HttpServletResponse response) {
    // TODO Auto-generated method stub
    ModelAndView mav = new ModelAndView();

    List<Commission> myList = new ArrayList<>();
    Boolean typeCommision = null ;
    myList = commissionRepository.getAllCommission(annee, numMois, codeRevendeur, statut,
        startCreatedDate, endCreatedDate, reference,typeCommision);
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

    commission.setCancelledBy(userConnected);
    commission.setCancelledDate(new Date());
    commission.setStatut("CANCELLED");
    commissionRepository.save(commission);

    return "success";
  }



  @Override
  public String changementEtatOffre(Long idOffre, User user) {
    // TODO Auto-generated method stub
    OffreCommissionPromo offre = offreCommissionPromoRepository.getById(idOffre);

    if (offre != null) {
      offreCommissionPromoRepository.changementEtat(idOffre, user.getUserid());
      return "success";
    } else {
      return "erreur";
    }

  }

  @Override
  public String ajouterOffreCommission(String namepromo, String datedebut, String datefin,
      Double montantdemande1, Double montantactivation1, Double montantpayement1,
      Double montantdemande2, Double montantactivation2, Double montantpayement2,
      Double montantdemande3, Double montantactivation3, Double montantpayement3,
      Double montantdemande4, Double montantactivation4, Double montantpayement4,
      Double montantdemande5, Double montantactivation5, Double montantpayement5, User user) {
    // TODO Auto-generated method stub

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date dateDebut;
    try {
      dateDebut = dateFormat.parse(datedebut);

      Date dateFin = dateFormat.parse(datefin);



      OffreCommissionPromo verif =
          offreCommissionPromoRepository.findAllByNomCommisionPromo(namepromo);
      if (verif != null) {
        return "OFFER_ALREADY_EXISTS";
      }

      OffreCommissionPromo offreCommission = new OffreCommissionPromo();
      offreCommission.setNomCommisionPromo(namepromo);
      offreCommission.setDateDebut(dateDebut);
      offreCommission.setDateFin(dateFin);
      offreCommission.setMontantDemandePalier1(montantdemande1);
      offreCommission.setMontantActivationPalier1(montantactivation1);
      offreCommission.setMontantPayementPalier1(montantpayement1);

      offreCommission.setMontantDemandePalier2(montantdemande2);
      offreCommission.setMontantActivationPalier2(montantactivation2);
      offreCommission.setMontantPayementPalier2(montantpayement2);

      offreCommission.setMontantDemandePalier3(montantdemande3);
      offreCommission.setMontantActivationPalier3(montantactivation3);
      offreCommission.setMontantPayementPalier3(montantpayement3);


      offreCommission.setMontantDemandePalier4(montantdemande4);
      offreCommission.setMontantActivationPalier4(montantactivation4);
      offreCommission.setMontantPayementPalier4(montantpayement4);


      offreCommission.setMontantDemandePalier5(montantdemande5);
      offreCommission.setMontantActivationPalier5(montantactivation5);
      offreCommission.setMontantPayementPalier5(montantpayement5);

      offreCommission.setCreatedBy(user);
      offreCommissionPromoRepository.save(offreCommission);

      return "success";

    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "false";
    }
  }

  @Override
  public OffreCommissionPromo getOffreCommissionPromoByIdAndIsActive(Long idOffrePromo) {
    // TODO Auto-generated method stub
    return offreCommissionPromoRepository.findAllByIdAndIsActive(idOffrePromo, true);
  }

  @Override
  public List<OffreCommissionPromo> getALLactiveCommision(boolean isActive) {
    // TODO Auto-generated method stub
    return offreCommissionPromoRepository.findAllByIsActive(isActive);
  }

  @Override
  public OffreCommissionPromo getOffreCommissionPromoById(Long idOffrePromo) {
    // TODO Auto-generated method stub
    return offreCommissionPromoRepository.getOffreCommissionPromoById(idOffrePromo);
  }

}
