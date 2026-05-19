package crm.chifco.com.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;
import crm.chifco.com.ApiDTO.entryAvoirClient;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.Bordereau;
import crm.chifco.com.model.ClicToPay;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.EntryAvoirClient;
import crm.chifco.com.model.EntryFactures;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.FraisTTAbonnement;
import crm.chifco.com.model.FraisTTAbonnementServices;
import crm.chifco.com.model.Reclamation;
import crm.chifco.com.model.Role;
import crm.chifco.com.model.Smstemplate;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.User;
import crm.chifco.com.radius.model.Radacct;
import crm.chifco.com.radius.model.Radcheck;
import crm.chifco.com.radius.service.RadcheckService;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.BordereaurRepository;
import crm.chifco.com.repository.ClickToPayRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.EntriesfacturesRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.FraisTTAbonnementRepository;
import crm.chifco.com.repository.FraisTTAbonnementServicesRepository;
import crm.chifco.com.repository.ReclamationRepository;
import crm.chifco.com.repository.SmstemplateRepository;
import crm.chifco.com.repository.StatutRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.utils.ClassificationRevendeur;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.GouvernoratMapping;
import crm.chifco.com.utils.HtmlTemplateEmail;
import crm.chifco.com.utils.NomStatutChifco;
import crm.chifco.com.utils.RedchekConstant;
import crm.chifco.com.utils.TechnicienStatus;
import crm.chifco.com.utils.TypeAbonnment;
import crm.chifco.com.utils.typePayementBordereau;
import javax.xml.transform.stream.StreamResult;

import java.io.BufferedWriter;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.text.DecimalFormatSymbols;
import java.io.File;
import java.io.FileInputStream;
@Configuration
@EnableScheduling
public class CronService {

  private final Logger logger = LogManager.getLogger(this.getClass());
  @Value("${delaiDePaiement}")
  private int delaiDePaiement;

  @Autowired
  private StatutRepository statutRepository;

  @Autowired
  private AbonnementRepository abonnementRepository;

  @Autowired
  private DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  private GenerateSequenceNewFacture generateSequenceNewFacture;

  @Autowired
  RoleService roleService;

  @Value("${rappelleJour1}")
  private String rappelleJour1;
  @Value("${rappelleJour2}")
  private String rappelleJour2;
  @Value("${reactivationservices}")
  private String reactivationservices;

  @Autowired
  UserService userservice;

  @Autowired
  Notification notificationservice;

  @Autowired
  SmstemplateRepository templatesmsRepository;

  @Autowired
  private UserHistoryService userHistoryService;

  @Autowired
  FactureRepository FactureRepository;
  
  @Autowired
  AvoirRepository avoirRepository;

  @Value("${cron.payment.disconnection.remind.before:}")
  private String cronPaymentDisconnectionRemindBefore;
  @Value("${cron.payment.disconnection.remind:}")
  private String remindCronPayement;
  @Value("${cron.payment.disconnection.remind.after:}")
  private String remindCronPayementAfterdDisconnection;

  @Autowired
  RadcheckService radcheckService;

  @Autowired
  FraisTTAbonnementRepository fraisTTAbonnementRepository;

  @Autowired
  FraisTTAbonnementServicesRepository fraisTTAbonnementServicesRepository;

  @Autowired
  ClickToPayRepository clickToPayRepository;
  @Autowired
  private EncaissementRepository encaisementRepo;
  @Autowired
  private ZoneService zoneService;
  @Autowired
  private ReclamationRepository reclamationRepository;
  @Autowired
  private ReclamationHistoryService reclamationHistoryService;



  @Value("${calcule.TT.raccordement}")
  private String racordementFraisTT;
  @Value("${clacule.TT.Service.8mo}")
  private String serviceTT8mo;
  @Value("${clacule.TT.Service.10mo}")
  private String serviceTT10mo;
  @Value("${clacule.TT.Service.12mo}")
  private String serviceTT12mo;
  @Value("${clacule.TT.Service.20mo}")
  private String serviceTT20mo;
  @Value("${clacule.TT.Service.30mo}")
  private String serviceTT30mo;
  @Value("${clacule.TT.Service.50mo}")
  private String serviceTT50mo;

  @Value("${cron.TTservice.racordement}")
  private String serviceTTRacordmentCron;
  @Value("${cron.TTservice.service}")
  private String serviceTTServicementCron;

  @Value("${blocage.mail.revendeur.responsable.finance}")
  private String blocageMailRevendeurResponsableFinance;
  @Value("${blocage.mail.revendeur.responsable.nety}")
  private String blocageMailRevendeurResponsableNety;

  @Autowired
  private UserRepository userRepository;

  @Value("${firstFactureGracePeriod}")
  private String firstFactureGracePeriod;


  @Value("${xls.mail.responsable}")
  private String xlsMailResponsable;

  @Value("${xls.mail.responsable.cc}")
  private String xlsMailCC;
  @Value("${urlKonnect}")
  private String urlKonnect;

  @Value("${xls.mail.sav}")
  private String xlsMailSAV;

  @Value("${PaymeeUrl}")
  private String PaymeeUrl;
  @Autowired
  private BordereaurRepository bordereaurRepository;

  @Autowired
  BordereauService bordereauService;

  @Autowired
  AvoirService avoirService;
  
  @Autowired
  ReclamationService recService;

  @Value("${pathPgh}")
  private String pathPgh;

  // les abonnements non payé
  @Scheduled(cron = "0 0 10,15 * * ?") // Run At 10 and 12 and 16 past every hour
  public void firstReminder() {

    // recuperer liste facture non payée avec date d'echeance depassée une semaine
    logger.info("firstReminder Cron started" + cronPaymentDisconnectionRemindBefore);

    logger.info("cron firstReminder is active" + cronPaymentDisconnectionRemindBefore.equals(true));
    if ("true".equals(cronPaymentDisconnectionRemindBefore)) {
      String instantdatedebut = CrmUtils.calculeDateSendsms(Long.parseLong(rappelleJour1));
      // String datedebut = instantdatedebut.trim() + " 00:00:00.000";
      Smstemplate findtemplatesms =
          templatesmsRepository.findSmstemplateByname("rappelleSmsClient");
      String Template = findtemplatesms.getTemplate();
      logger.info("cron firstReminder date Rechercher est : " + instantdatedebut);

      List<Facture> listfacturenonpaye =
          FactureRepository.listFactureRapelles(instantdatedebut, false, false, false);

      ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
      for (Facture Facture : listfacturenonpaye) {
        Map<String, Object> Message = new HashMap<String, Object>();
        // On va desactiver les abonnements pour chaque facture non payée
        if (Facture.getAbonnement().getTelFixe() != null
            && Facture.getAbonnement().getIsSmsClientSend() != null
            && Facture.getAbonnement().getIsSmsClientSend() == true) {
          String NewTemplate = Template.replace("{referencedemande}",
              Facture.getAbonnement().getTelFixe().toString());
          Message.put("number", Facture.getAbonnement().getTelMobile());
          Message.put("message", NewTemplate);
          smsToSend.add(Message);
        }

      }
      logger.info("cron firstReminder size message to send est" + smsToSend.size());
      if (smsToSend.size() > 0) {
        Boolean resultaSms = notificationservice.sendsmsnotification(smsToSend);
        if (resultaSms) {
          for (Facture facture : listfacturenonpaye) {
            facture.setFirstReminder(true);
            FactureRepository.save(facture);
          }
        }

      }
      logger.info("cron firstReminder  ended");
    }

  }

  // * *
  @Scheduled(cron = "0 0 11,16 * * ?") // Run At 10:30:05 and 10:30:05 and 16:30:05 evry day
  public void secondReminder() {
    if (cronPaymentDisconnectionRemindBefore.equals("true")) {
      logger.info("cron secondReminder  strated");
      // recuperer liste facture non payée avec date d'echeance depassée une semaine
      String dateechance = CrmUtils.calculeDateSendsms(Long.parseLong(rappelleJour2));

      Smstemplate findtemplatesms =
          templatesmsRepository.findSmstemplateByname("rappelleSmsClient2");
      String Template = findtemplatesms.getTemplate();

      List<Facture> listfacturenonpaye =
          FactureRepository.listFactureRapelles(dateechance, null, false, false);
      logger.info("cron secondReminder liste facture size" + listfacturenonpaye.size());

      ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
      for (Facture facture : listfacturenonpaye) {
        Map<String, Object> Message = new HashMap<String, Object>();
        // On va desactiver les abonnements pour chaque facture non payée
        if (facture.getAbonnement().getTelFixe() != null
            && facture.getAbonnement().getIsSmsClientSend() != null
            && facture.getAbonnement().getIsSmsClientSend() == true) {
          String NewTemplate = Template.replace("{referencedemande}",
              facture.getAbonnement().getTelFixe().toString());
          NewTemplate = NewTemplate.replace("{jour}",
              CrmUtils
                  .SmsFormatDateEchance(
                      CrmUtils.addOneDayToSmsSecondReminder(facture.getDate_echeance()))
                  .toString());
          Message.put("number", facture.getAbonnement().getTelMobile());
          Message.put("message", NewTemplate);
          smsToSend.add(Message);
        }
      }
      if (smsToSend.size() > 0) {
        Boolean resultaSms = notificationservice.sendsmsnotification(smsToSend);
        if (resultaSms) {
          for (Facture facture : listfacturenonpaye) {
            facture.setSecondReminder(true);
            FactureRepository.save(facture);
          }
        }
      }
      logger.info("cron firstReminder  ended");
    }

  }

  @Scheduled(cron = "0 0 12,17 * * ?") // Run At 10:50:10 and 12:15:45 and 16:50:10 evry day
  public void suspensionServices() {
    if (remindCronPayement.equals("true")) {
      logger.info("cron suspensionServices  started");
      // recuperer liste facture non payée avec date d'echeance depassée une semaine
      String dateechance = CrmUtils.calculeDateSendsmsNegativeNumber(1L);
      Smstemplate findtemplatesms =
          templatesmsRepository.findSmstemplateByname("rappelleSmsCoupure");
      String Template = findtemplatesms.getTemplate();

      List<Facture> listfacturenonpaye =
          FactureRepository.listFactureRapelles(dateechance, null, null, false);
      ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();

      for (Facture Facture : listfacturenonpaye) {
        Map<String, Object> Message = new HashMap<String, Object>();
        if (Facture.getAbonnement().getTelFixe() != null
            && Facture.getAbonnement().getIsSmsClientSend() != null
            && Facture.getAbonnement().getIsSmsClientSend() == true) {
          // On va desactiver les abonnements pour chaque facture non payée
          String NewTemplate =
              Template.replace("{reference}", Facture.getAbonnement().getTelFixe().toString());
          Message.put("number", Facture.getAbonnement().getTelMobile());
          Message.put("message", NewTemplate);
          smsToSend.add(Message);
        }
        Statut changedStatus = statutRepository.findStatutByNomStatut(NomStatutChifco.UNPAID);
        Facture.getAbonnement().setStatut(changedStatus);
        abonnementRepository.save(Facture.getAbonnement());
      }
      logger.info("SuspensionServices" + smsToSend.size());
      if (smsToSend.size() > 0) {
        Boolean resultaSms = notificationservice.sendsmsnotification(smsToSend);
        if (resultaSms) {
          for (Facture facture : listfacturenonpaye) {
            facture.setSuspensionServices(true);
            FactureRepository.save(facture);
          }
        }
      }
      logger.info("cron suspensionServices  ended");
    }
  }

  @Scheduled(cron = "0 0 13,15 * * ? ") // // Run At 10:15:45 and 12:15:45 and 16:15:45 evry day
  public void thirdReminderReactivate() {
    if (remindCronPayementAfterdDisconnection.equals("true")) {
      logger.info("cron thirdReminderReactivate  started");
      // recuperer liste facture non payée avec date d'echeance depassée une semaine
      String instantdatefin =
          CrmUtils.calculeDateSendsmsNegativeNumber(Long.parseLong(reactivationservices));

      Smstemplate findtemplatesms =
          templatesmsRepository.findSmstemplateByname("rappelleSmsReactivate");
      String Template = findtemplatesms.getTemplate();

      String datedebut = instantdatefin.trim() + " 00:00:00.000";
      String findate = instantdatefin.trim() + " 23:59:59.999";
      Instant instantprochainfacture =
          CrmUtils.toInstant(findate).atZone(ZoneId.systemDefault()).toInstant();
      Instant instantDateDebutProchainFacture =
          CrmUtils.toInstant(datedebut).atZone(ZoneId.systemDefault()).toInstant();
      List<Facture> listfacturenonpaye =
          FactureRepository.rappelleSmsReactivate(Date.from(instantDateDebutProchainFacture),
              Date.from(instantprochainfacture), NomStatutChifco.UNPAID, false);

      ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();

      for (Facture factures : listfacturenonpaye) {
        Map<String, Object> Message = new HashMap<String, Object>();
        // On va desactiver les abonnements pour chaque facture non payée
        if (factures.getAbonnement().getTelFixe() != null
            && factures.getAbonnement().getIsSmsClientSend() != null
            && factures.getAbonnement().getIsSmsClientSend() == true) {
          String NewTemplate =
              Template.replace("{reference}", factures.getAbonnement().getTelFixe().toString());
          Message.put("number", factures.getAbonnement().getTelMobile());
          Message.put("message", NewTemplate);
          smsToSend.add(Message);
        }
      }
      logger.info("remindCronPayementAfterdDisconnection" + smsToSend);
      if (smsToSend.size() > 0) {
        Boolean resultaSms = notificationservice.sendsmsnotification(smsToSend);
        if (resultaSms) {
          for (Facture facture : listfacturenonpaye) {
            facture.setThirdReminderReactivate(true);
            FactureRepository.save(facture);
          }
        }
      }
      logger.info("cron thirdReminderReactivate  ended");
    }
  }

  @Scheduled(cron = "0 00 10 * * ? ") //
  public void calculeRaccordmentTTFromDateMiseEnService() {
    // try {
    if (serviceTTRacordmentCron.equals("true")) {
      logger.info("cron claclue RacordementTT  mise en service srart");
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
      DateTimeFormatter outputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM");
      List<Abonnement> miseenserviceDate = abonnementRepository
          .findAbonnementByisFraisRaccordementTTOrIsFraisRaccordementTTIsNullAndHasRaccordement(
              false, true);

      miseenserviceDate.forEach(el -> {
        if (el.getDateDeMiseEnService() != null) {
          FraisTTAbonnement fraisTTAbonnement = new FraisTTAbonnement();
          fraisTTAbonnement.setCatagorieInternt(
              el.getPack().getCategoriePack().getCategorieProduitInternetNom());
          fraisTTAbonnement.setCodeFrais("FRAIS_RACCORDMENT");
          fraisTTAbonnement.setForfaitInternt(el.getPack().getTitle());
          fraisTTAbonnement.setIsRaccordement(true);
          Date DateDeMiseEnService = new Date(el.getDateDeMiseEnService().getTime());
          fraisTTAbonnement.setDateConnection(formatter.format(DateDeMiseEnService.toInstant()
              .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()));

          if (el.getTelFixe() != null) {
            fraisTTAbonnement.setNumeroTelephone(el.getTelFixe().toString());
          }

          fraisTTAbonnement.setPrixService(Double.parseDouble(racordementFraisTT));
          fraisTTAbonnement.setReferenceTelecom("");

          fraisTTAbonnement.setRechecheDate(outputDateFormat.format(
              new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()));
          fraisTTAbonnement.setUserName("");
          fraisTTAbonnementRepository.save(fraisTTAbonnement);
          el.setIsFraisRaccordementTT(true);
          abonnementRepository.save(el);
          logger.info("new  RacordementTT create   cron  RacordementTT" + el.getReferenceClient());
        }

      });
      logger.info("cron claclue RacordementTT  mise en service end ");
    }



    // } catch (Exception e) {
    // logger.info("calculeRacordementTT " + e);

    // }

  }

  @Scheduled(cron = "0 15 16,14 * * ? ") //
  public void calculeFirstDateOfConection() {
    try {
      if (serviceTTRacordmentCron.equals("true")) {
        logger.info("calcule calculeFirstDateOfConection cron Strat At  " + new Date());
        List<Abonnement> listeAbonnement =
            abonnementRepository.findAbonnementByCalculeIsFirstSession(false);
        logger.info("nombre abonnement calcule calculeFirstDateOfConection cron  "
            + listeAbonnement.size());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");

        listeAbonnement.forEach(el -> {
          Radacct firstConnection =
              radcheckService.getRadacctConnectionToClaculateFraisTT(el.getLoginModem());


          if (firstConnection != null) {
            List<FraisTTAbonnementServices> serviceTTifExsite =
                fraisTTAbonnementServicesRepository.getAllDataByUserNameIfExist(
                    firstConnection.getAcctstarttime().toString(), el.getLoginModem());
            long restOfDayFirstConnection =
                CrmUtils.getRemainingDaysOfMonth(firstConnection.getAcctstarttime().toString());
            long dayofMonth =
                CrmUtils.getLastDaysOfMonth(firstConnection.getAcctstarttime().toString());
            if (serviceTTifExsite.isEmpty() && restOfDayFirstConnection != -1 && dayofMonth != -1) {
              FraisTTAbonnementServices serviceCalculeFirstDateOfConection =
                  new FraisTTAbonnementServices();
              serviceCalculeFirstDateOfConection.setCatagorieInternt(
                  el.getPack().getCategoriePack().getCategorieProduitInternetNom());
              serviceCalculeFirstDateOfConection.setCodeFrais("FRAIS_SERVICE");
              serviceCalculeFirstDateOfConection.setForfaitInternt(el.getPack().getTitle());
              serviceCalculeFirstDateOfConection.setIsRaccordement(false);
              serviceCalculeFirstDateOfConection
                  .setDateConnection(firstConnection.getAcctstarttime().toString());
              serviceCalculeFirstDateOfConection.setNumeroTelephone(el.getTelFixe().toString());
              Double calculePrixServicesTTFirstConnection =
                  calculePrixServicesTT(el.getPack().getDebitPack());

              Double prixServiceTT = (calculePrixServicesTTFirstConnection / dayofMonth)
                  * (restOfDayFirstConnection + 1);

              serviceCalculeFirstDateOfConection.setPrixService(prixServiceTT);
              serviceCalculeFirstDateOfConection.setReferenceTelecom("");
              String rechercheDta = outputDateFormat.format(firstConnection.getAcctstarttime());
              serviceCalculeFirstDateOfConection.setRechecheDate(rechercheDta);
              serviceCalculeFirstDateOfConection.setUserName(firstConnection.getUsername());
              fraisTTAbonnementServicesRepository.save(serviceCalculeFirstDateOfConection);
              el.setDateCalculeFraisServies(new Date());
              el.setCalculeIsFirstSession(true);
              abonnementRepository.save(el);
              logger.info("new  calculeFirstDateOfConection create   cron  FirstDateOfconextion"
                  + el.getClientid());
            }
            if (!serviceTTifExsite.isEmpty()) {
              el.setCalculeIsFirstSession(true);
              el.setFirstConnectionDate(firstConnection.getAcctstarttime());
            }
          }
        });
        logger.info("calcule calculeFirstDateOfConection cron End At  " + new Date());
      }
    } catch (Exception e) {
      logger.info("calculeFirstDateOfConection " + e);

    }

  }

  // @Scheduled(cron = "0 0 0 10,27 * ? ") // // Run le 10 et 27 jours de chaque mois
  /*
   * public void calculeRacordementTT() { try { if (serviceTTRacordmentCron.equals("true")) {
   * logger.info("calcule RacordementTT cron Strat At  " + new Date()); List<Abonnement>
   * listeAbonnement = abonnementRepository
   * .findAbonnementByisFraisRaccordementTTOrIsFraisRaccordementTTIsNullAndHasRaccordementAndLoginModemNotNull(
   * false, true); logger.info("nombre abonnement calcule RacordementTT cron  " +
   * listeAbonnement.size()); SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM");
   * listeAbonnement.forEach(el -> { Radacct firstConnection =
   * radcheckService.getRadacctConnectionToClaculateFraisTT(el.getLoginModem()); if (firstConnection
   * != null) { FraisTTAbonnement fraisTTAbonnement = new FraisTTAbonnement();
   * fraisTTAbonnement.setCatagorieInternt(
   * el.getPack().getCategoriePack().getCategorieProduitInternetNom());
   * fraisTTAbonnement.setCodeFrais("FRAIS_RACCORDMENT");
   * fraisTTAbonnement.setForfaitInternt(el.getPack().getTitle());
   * fraisTTAbonnement.setIsRaccordement(true);
   * fraisTTAbonnement.setDateConnection(firstConnection.getAcctstarttime().toString());
   * fraisTTAbonnement.setNumeroTelephone(el.getTelFixe().toString());
   * fraisTTAbonnement.setPrixService(Double.parseDouble(racordementFraisTT));
   * fraisTTAbonnement.setReferenceTelecom(""); String rechercheDta =
   * outputDateFormat.format(firstConnection.getAcctstarttime());
   * fraisTTAbonnement.setRechecheDate(rechercheDta);
   * fraisTTAbonnement.setUserName(firstConnection.getUsername());
   * fraisTTAbonnementRepository.save(fraisTTAbonnement); el.setIsFraisRaccordementTT(true);
   * abonnementRepository.save(el); logger.info("new  RacordementTT create   cron  RacordementTT" +
   * el.getClientid()); }
   * 
   * }); logger.info("calcule RacordementTT cron End At  " + new Date()); } } catch (Exception e) {
   * logger.info("calculeRacordementTT " + e);
   * 
   * }
   * 
   * }
   */

  @Scheduled(cron = "47 */30 16,18 * * ?") // // Run At 03h evry day
  public void calculeServicesTTrunOnSpecificDays() {

    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    LocalDate currentDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
    DateTimeFormatter outputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM");
    String formattedCurrentDate = currentDate.format(formatter);
    String rechercheDta = currentDate.format(outputDateFormat);

    serviceTTCalcule(formattedCurrentDate, rechercheDta);
  }

  @Scheduled(cron = "0 50 23 L * ?") // // Run At 03h evry day
  public void calculeServicesTTrunOnLastDayOfMonth() {

    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    LocalDate currentDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
    DateTimeFormatter outputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM");
    String formattedCurrentDate = currentDate.format(formatter);
    String rechercheDta = currentDate.format(outputDateFormat);

    serviceTTCalcule(formattedCurrentDate, rechercheDta);
  }

  public Boolean serviceTTCalcule(String formattedCurrentDate, String rechercheDta) {
    // try {
    if (serviceTTServicementCron.equals("true")) {
      logger.info("Cron serviceTTCalcule Start At" + new Date());
      // List<String> connectionServices = radcheckService
      // .getListeConnectionToClaculateFraisTTAndDateDeConnection(formattedCurrentDate);

      List<Abonnement> connectionServices = abonnementRepository
          .findAllByLoginModemNotNullAndNotCalculeResilationTTAndFirtConectionIsTrue();
      logger.info("number of connectionServices at Month" + rechercheDta + "egale a"
          + connectionServices.size());
      connectionServices.forEach(el -> {
        List<FraisTTAbonnementServices> serviceTTifExsite = fraisTTAbonnementServicesRepository
            .getAllDataByUserNameIfExist(formattedCurrentDate, el.getLoginModem());
        if (serviceTTifExsite.isEmpty()) {

          if (el.getIsActive() != null) {
            FraisTTAbonnementServices fraisTTAbonnement = new FraisTTAbonnementServices();
            fraisTTAbonnement.setCatagorieInternt(
                el.getPack().getCategoriePack().getCategorieProduitInternetNom());
            fraisTTAbonnement.setCodeFrais("FRAIS_SERVICE");
            fraisTTAbonnement.setForfaitInternt(el.getPack().getTitle());
            fraisTTAbonnement.setIsRaccordement(false);
            fraisTTAbonnement.setDateConnection("");
            fraisTTAbonnement.setNumeroTelephone(el.getTelFixe().toString());
            if (el.getDateProchainFacturation() != null && el.getIsActive() == true
                && el.getPack() != null && el.getPack().getDebitPack() != null) {
              fraisTTAbonnement.setPrixService(calculePrixServicesTT(el.getPack().getDebitPack()));
            } else if (el.getIsActive() == false && el.getDateResiliation() != null) {



              LocalDate dateconnecteddayofMonth =
                  LocalDate.parse(el.getDateResiliation().toString(),
                      java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));

              LocalDate endOfMonth =
                  dateconnecteddayofMonth.withDayOfMonth(dateconnecteddayofMonth.lengthOfMonth());
              Double prixServiceTT = (calculePrixServicesTT(el.getPack().getDebitPack())
                  * dateconnecteddayofMonth.getDayOfMonth() / endOfMonth.getDayOfMonth());

              fraisTTAbonnement.setPrixService(prixServiceTT);
              el.setCalculeServiceResiliation(true);
            }

            fraisTTAbonnement.setReferenceTelecom("");
            fraisTTAbonnement.setUserName(el.getLoginModem());
            fraisTTAbonnement.setRechecheDate(rechercheDta);
            fraisTTAbonnementServicesRepository.save(fraisTTAbonnement);
            el.setDateCalculeFraisServies(new Date());
            abonnementRepository.save(el);
            logger.info("inseration new service TT for id client is " + el.getClientid());
          }

        }

      });
      logger.info("Cron serviceTTCalcule End At" + new Date());
      return true;
    } else {
      return false;
    }
    // } catch (Exception e) {
    // logger.info("calcule TT service " + e);
    // logger.info("Cron serviceTTCalcule End At" + new Date());
    // return false;
    // }
  }


  Double calculePrixServicesTT(String DebitPack) {
    Double prix;
    switch (DebitPack) {
      case "8":
        prix = Double.parseDouble(serviceTT8mo);
        break;
      case "10":
        prix = Double.parseDouble(serviceTT10mo);
        break;
      case "12":
        prix = Double.parseDouble(serviceTT12mo);
        break;
      case "20":
        prix = Double.parseDouble(serviceTT20mo);
        break;
      case "30":
        prix = Double.parseDouble(serviceTT30mo);
        break;
      case "50":
        prix = Double.parseDouble(serviceTT50mo);
        break;
      // Add cases for other values
      default:
        prix = Double.parseDouble(serviceTT8mo);
        break;
    }
    return prix;
  }


  @Scheduled(cron = "00 10 10 */6 * ?") // // Run At 03h evry day
  @Transactional
  public void desactiverRevendeurIfFactureNotVersed() {

    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    LocalDate currentDate = LocalDate.now();


    LocalDate resultDateFaterRemoveDay = currentDate.minusDays(25);
    Date date =
        Date.from(resultDateFaterRemoveDay.atStartOfDay(ZoneId.systemDefault()).toInstant());

    List<Long> listeAbonnement = encaisementRepo
        .findEncaismentNotChifcoPayedDistaintByUserBeweenDateForSendNotifcationAndBolck(date,
            false);
    // Print the result
    logger.info(
        "desactiverRevendeurIfFactureNotVersed  cron strart with Current Date: " + currentDate);
    ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
    logger.info("desactiverRevendeurIfFactureNotVersed: " + listeAbonnement.size());
    for (Long idUser : listeAbonnement) {
      if (idUser != null) {
        logger.info("idUser: " + idUser);
        User userSlected = userRepository.findUsersByUserid(idUser);
        Role userRole = userSlected.getRole();
        logger.info("userRole: " + userRole.getRoleName());
        if (userRole != null && userRole.getRoleName() != null
            && !userRole.getRoleName().contains("_DESACTIVE")) {
          logger.info("not DESACTIVE");
          Role newrole =
              roleService.findRoleByRoleName(userSlected.getRole().getRoleName() + "_DESACTIVE");
          List<Encaissement> findEncaismentNotChifcoPayedAndUser = encaisementRepo
              .findEncaismentNotChifcoPayedByUserBeweenDateForSendNotifcationAndBlock(date, idUser,
                  false);
          if (newrole != null) {
            logger.info("new role existe");
            logger.info("payement: new role" + newrole.getRoleName() + userSlected.getCodeUser());
            Map<String, Object> Message = new HashMap<String, Object>();

            String sms =
                "Votre compte a été bloqué suite à des factures non versées dépassant les 25 jours. Veuillez régulariser votre situation dans les plus brefs délais.";
            Message.put("number", userSlected.getTelephone());
            Message.put("message", sms);
            smsToSend.add(Message);
            logger.info("sms add");
            User userSystem = userRepository.findTop1UsersByTypeuser("SYSTEM");
            userSlected.setRole(newrole);
            userSlected.setLocked(true);
         
            if(userSlected.getClassification() == null  || (userSlected.getClassification() != null &&
            		!userSlected.getClassification().equals(ClassificationRevendeur.precontentieux)	) )
            {
                userSlected.setClassification(ClassificationRevendeur.suspendu);	
                userSlected.setDateUpdateclassification(new Date());
                userHistoryService.addHistoryEntry(idUser,
                        "status passe au  " + ClassificationRevendeur.suspendu, userSystem);
                    logger.info("user saved");
                    userSlected.setDesactivationDate(new Date());

            }
      
            userSlected.setDesactivatedByCron(true);
            userRepository.save(userSlected);
         
            userHistoryService.addHistoryEntry(idUser,
                "Désactivation en raison de factures impayées dépassant la période de 25 jours",
                userSystem);
    
            String templateRevToAdmin =
                HtmlTemplateEmail.HtmlNotificationFactureNonPyaeeRevendeur25jr(
                    findEncaismentNotChifcoPayedAndUser, userSlected.getFirstName() + " "
                        + userSlected.getLastName() + " (" + userSlected.getCodeUser() + ")");
            User chefsecteur = userRepository.findUsersByUserid(userSlected.getAffectedTo());


            List<String> ccEmail = new ArrayList<>();

            ccEmail.add(blocageMailRevendeurResponsableFinance);
            ccEmail.add(blocageMailRevendeurResponsableNety);
            if (chefsecteur != null && chefsecteur.getEmail() != null) {
              ccEmail.add(chefsecteur.getEmail());
            }

            notificationservice.sendSimpleMailHtmlWithCC(ccEmail, userSlected.getEmail(),
                templateRevToAdmin, "Facture(s) non versée(s) depuis 25 jours");
            logger.info("email send");
            findEncaismentNotChifcoPayedAndUser.forEach(encaisment -> {
              logger.info("encaisment", encaisment.getEncaissementId());
              encaisment.setFirstReminderRevendeur(true);
              encaisment.setSecondReminderRevendeur(true);
              encaisment.setBlockCompteReminderRevendeur(true);
              encaisementRepo.save(encaisment);
            });
          } else {
            logger.info("new role n'existe pas " + userRole.getRoleName());
          }
        } else {
          List<Encaissement> findEncaismentNotChifcoPayedAndUser = encaisementRepo
              .findEncaismentNotChifcoPayedByUserBeweenDateForSendNotifcationAndBlock(date, idUser,
                  false);

          findEncaismentNotChifcoPayedAndUser.forEach(encaisment -> {
            logger.info("encaisment", encaisment.getEncaissementId());
            encaisment.setFirstReminderRevendeur(true);
            encaisment.setSecondReminderRevendeur(true);
            encaisment.setBlockCompteReminderRevendeur(true);
            encaisementRepo.save(encaisment);
          });
        }
      }
    }

    logger.info("sms resilation send" + smsToSend.size());
    if (smsToSend.size() > 0) {
      Boolean resultaSms = notificationservice.sendsmsnotification(smsToSend);
      logger.info("sms resilation send " + smsToSend + "resulta" + resultaSms);
    }
    logger
        .info("desactiverRevendeurIfFactureNotVersed  cron end with Current Date: " + currentDate);
  }

  @Scheduled(cron = "00 30 15 */5 * ?") //
  @Transactional
  public void notifRevendeurIfFactureNotVersedAfter15jr() {

    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    LocalDate currentDate = LocalDate.now();


    LocalDate resultDateFaterRemoveDay = currentDate.minusDays(15);
    Date datemin =
        Date.from(resultDateFaterRemoveDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
    LocalDate resultDateFaterRemoveDayToFindMax = currentDate.minusDays(25);
    Date datemmax = Date
        .from(resultDateFaterRemoveDayToFindMax.atStartOfDay(ZoneId.systemDefault()).toInstant());
    List<Long> listeAbonnement =
        encaisementRepo.findEncaismentNotChifcoPayedDistaintByUserBeweenDateForSendNotifcation(
            datemin, datemmax, false);
    // Print the result
    logger.info(
        "notifRevendeurIfFactureNotVersedAfter15jr  cron strart with Current Date: " + currentDate);


    for (Long idUser : listeAbonnement) {
      if (idUser != null) {
        User userSlected = userRepository.findUsersByUserid(idUser);
        Map<String, Object> Message = new HashMap<String, Object>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        /*
         * String sms =
         * "Vous avez des factures payé non versé hors délai (15 jours) en cas de non versement votre compte sera bloqué le"
         * + resultDateAFaterAddDayToBlock.format(formatter); Message.put("number",
         * userSlected.getTelephone()); Message.put("message", sms); smsToSend.add(Message);
         */
        List<Encaissement> findEncaismentNotChifcoPayedAndUser =
            encaisementRepo.findEncaismentNotChifcoPayedByUserBeweenDateForSendNotifcation(datemin,
                datemmax, idUser, false);

        String templateRevToAdmin = HtmlTemplateEmail
            .HtmlNotificationFactureNonPyaeeRevendeur15jr(findEncaismentNotChifcoPayedAndUser);
        notificationservice.sendSimpleMailHtml(userSlected.getEmail(), templateRevToAdmin,
            "Facture(s) non versée(s)");

        findEncaismentNotChifcoPayedAndUser.forEach(encaisment -> {
          Date dateOfPayment = encaisment.getDate();

          LocalDate localDateOfPayment =
              dateOfPayment.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


          // Calculate the difference in days
          long daysDifference = ChronoUnit.DAYS.between(localDateOfPayment, currentDate);
          if (daysDifference >= 15 && daysDifference < 20)
            encaisment.setFirstReminderRevendeur(true);
          else {
            encaisment.setFirstReminderRevendeur(true);
            encaisment.setSecondReminderRevendeur(true);
          }
          encaisementRepo.save(encaisment);
        });

      }
    }
    /*
     * if (smsToSend.size() > 0) { Boolean resultaSms =
     * notificationservice.sendsmsnotification(smsToSend); logger.info("sms resilation send " +
     * smsToSend + "resulta" + resultaSms); }
     */
  }

  // @Scheduled(cron = "00 00 11,12,13,14,15,16 * * ?") // // Run At 03h evry day
  // @Transactional
  /*
   * public void notifRevendeurIfFactureNotVersedAfter20jr() {
   * 
   * // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM"); LocalDate currentDate
   * = LocalDate.now();
   * 
   * 
   * LocalDate resultDateFaterRemoveDay = currentDate.minusDays(20); Date datemin =
   * Date.from(resultDateFaterRemoveDay.atStartOfDay(ZoneId.systemDefault()).toInstant()); LocalDate
   * resultDateFaterRemoveDayToFindMax = currentDate.minusDays(25); Date datemmax = Date
   * .from(resultDateFaterRemoveDayToFindMax.atStartOfDay(ZoneId.systemDefault()).toInstant());
   * List<Long> listeAbonnement =
   * encaisementRepo.findEncaismentNotChifcoPayedDistaintByUserBeweenDateForSendNotifcation(
   * datemin, datemmax, false);
   * 
   * // Print the result logger.info(
   * "notifRevendeurIfFactureNotVersedAfter20jr  cron strart with Current Date: " + currentDate);
   * 
   * 
   * LocalDate resultDateAFaterAddDayToBlock = currentDate.plusDays(5); ArrayList<Map<String,
   * Object>> smsToSend = new ArrayList<Map<String, Object>>(); for (Long idUser : listeAbonnement)
   * { if (idUser != null) { User userSlected = userRepository.findUsersByUserid(idUser);
   * List<Encaissement> findEncaismentNotChifcoPayedAndUser =
   * encaisementRepo.findEncaismentNotChifcoPayedByUserBeweenDateForSendNotifcation(datemin,
   * datemmax, idUser, false);
   * 
   * String templateRevToAdmin = HtmlTemplateEmail
   * .HtmlNotificationFactureNonPyaeeRevendeur15jr(findEncaismentNotChifcoPayedAndUser); User
   * chefsecteur = userRepository.findUsersByUserid(userSlected.getAffectedTo()); Map<String,
   * Object> Message = new HashMap<String, Object>(); DateTimeFormatter formatter =
   * DateTimeFormatter.ofPattern("yyyy-MM-dd");
   * 
   * String sms =
   * "Vous avez des factures payé non versé hors délai (15 jours) en cas de non versement votre compte sera bloqué le"
   * + resultDateAFaterAddDayToBlock.format(formatter); Message.put("number",
   * userSlected.getTelephone()); Message.put("message", sms); smsToSend.add(Message);
   * 
   * List<String> ccEmail = new ArrayList<>();
   * 
   * ccEmail.add(blocageMailRevendeurResponsableFinance); //
   * ccEmail.add(blocageMailRevendeurResponsableNety); ccEmail.add(chefsecteur.getEmail());
   * notificationservice.sendSimpleMailHtmlWithCC(ccEmail, userSlected.getEmail(),
   * templateRevToAdmin, "Notification facture  Non Payee 20 jours");
   * 
   * 
   * findEncaismentNotChifcoPayedAndUser.forEach(encaisment -> {
   * encaisment.setFirstReminderRevendeur(true); encaisment.setSecondReminderRevendeur(true);
   * encaisementRepo.save(encaisment); }); } } if (smsToSend.size() > 0) { Boolean resultaSms =
   * notificationservice.sendsmsnotification(smsToSend); logger.info("sms resilation send " +
   * smsToSend + "resulta" + resultaSms); } }
   */
  // 00 00 11,12,13,14,15,16 * * ?
  @Scheduled(cron = "00 00 11,12,13,14,15,16 * * ?") // // Run At 03h evry day 11,12,13,14,15,16
  @Transactional
  public void CheckFirstConnectionIfProforma() {

    List<Facture> factureProformat = FactureRepository.findByIsProformat(true);
    logger.info("CheckFirstConnectionIfProforma start ");
    for (Facture facture : factureProformat) {
      try {
        Radacct firstConnection = null;
        if (facture.getAbonnement().getLoginModem() != null) {
          firstConnection = radcheckService
              .getRadacctConnectionToClaculateFraisTT(facture.getAbonnement().getLoginModem());
        }

        // Vérifier si la date de facturation a plus de 10 jours
        boolean plusDe5Jours = false;
        if (facture.getAbonnement().getDateDeMiseEnService() != null) {
          try {
           

            plusDe5Jours = CrmUtils
                .estPlusDeNombreDeJours(facture.getAbonnement().getDateDeMiseEnService(), 5);
          } catch (Exception err) {
            logger.info("CheckFirstConnectionIfProforma errror 1 " + err.getMessage());

          }
        }
        if (firstConnection != null || plusDe5Jours) {
          facture.setIsProformat(false);
          String factureReference = generateSequenceNewFacture.generateWithPrefix();
          facture.setRef_facture(factureReference);
          if (!(facture.getAbonnement().getPack() != null
              && facture.getAbonnement().getPack().getPayLater())) {
            LocalDateTime date = LocalDateTime.now();
            Instant debutFacturation = date.atZone(ZoneId.systemDefault()).toInstant();
            facture.setDateDeDebut(Date.from(debutFacturation));
            // facture.setCreatedDate(Date.from(debutFacturation));
            Instant dateecheance =
                debutFacturation.plus(Long.valueOf(delaiDePaiement), ChronoUnit.DAYS);
            facture.setDate_echeance(Date.from(dateecheance));
            int typdedepaymentmonth =
                facture.getAbonnement().getTypePaiement().getNombreMoisTypePaiement();
            Date dateProchainFacture =
                CrmUtils.calculeDateFin(typdedepaymentmonth, Date.from(debutFacturation));
            LocalDate DateProchaine =
                dateProchainFacture.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate adjustedEndDate = DateProchaine.minus(1, ChronoUnit.DAYS);
            Date finaleDateFin = Date
                .from(adjustedEndDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            facture.setDateDeFin(finaleDateFin);
            facture.getAbonnement().setDateProchainFacturation(finaleDateFin);
            facture.getAbonnement().setIsClient(true);
            logger.info("idProformatFacture " + firstConnection);
            logger.info("date 1er connction " + facture.getFactureId());
            logger.info("finaleDateFin ProformatFacture after change" + finaleDateFin);


            if (facture.getAbonnement().getDateFinPromotion() != null) {
              long diffDays =
                  CrmUtils.diffJoursBetween(facture.getAbonnement().getDateDeMiseEnService());
              logger.info("diff day between mise en service and today " + diffDays);
              Date addDaysToDate = CrmUtils.addDaysToDate(
                  facture.getAbonnement().getDateFinPromotion().getTime(), (int) diffDays);
              logger.info("diff day between mise en service and today " + diffDays);
              facture.getAbonnement().setDateFinPromotion(addDaysToDate);
            }
          }
          Radcheck radcheck = radcheckService.getRadchecksByUsernameAndAttribute(
              facture.getAbonnement().getLoginModem(), RedchekConstant.Expiration);
          if (radcheck != null) {
            radcheckService.updateDateExpiration(
                CrmUtils.RadusDateDexpiration(facture.getDateDeFin()),
                facture.getAbonnement().getLoginModem());
          }
          if (firstConnection != null) {
            facture.getAbonnement().setFirstConnectionDate(firstConnection.getAcctstarttime());
            // facture.getAbonnement().setIsDateFirstConnection(true);
          }
          abonnementRepository.save(facture.getAbonnement());
          FactureRepository.save(facture);
        }

      } catch (Exception err) {
        logger.info("CheckFirstConnectionIfProforma error 2 " + err.getMessage());

      }

    }
    logger.info("CheckFirstConnectionIfProforma end ");

  }

  @Scheduled(cron = "0 30 13,14,15,16 * * ?") // // Run At 03h evry day 11,12,13,14,15,16
  @Transactional
  public void CheckClasificationRevendeurifdesactivated() {
	    logger.info("CheckClasificationRevendeurifdesactivated start ");
    User userSystem = userRepository.findTop1UsersByTypeuser("SYSTEM");

    List<User> userSlected =
        userRepository.findUsersByClassification(ClassificationRevendeur.suspendu);
    logger.info("CheckClasificationRevendeurifdesactivated length user " + userSlected.size());

    for (User user : userSlected) {
    	if(user.getDesactivationDate() != null) {
      LocalDate localDateToCheck =
          user.getDesactivationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

      // Get current date
      LocalDate today = LocalDate.now();

      // Calculate weeks between
      long weeksBetween = ChronoUnit.WEEKS.between(localDateToCheck, today);
      logger.info("CheckClasificationRevendeurifdesactivated weeksBetween " + weeksBetween);

      if (weeksBetween >= 4) {
        user.setClassification(ClassificationRevendeur.Senrecouvrement);
        user.setDateUpdateclassification(new Date());
        userRepository.save(user);
        userHistoryService.addHistoryEntry(user.getUserid(),
            "classification passe au " + ClassificationRevendeur.Senrecouvrement, userSystem);
      }
    	}
    }
    logger.info("CheckClasificationRevendeurifdesactivated end ");

  }

  @Scheduled(cron = "00 00 07 * * ?") // // Run At 03h evry day 11,12,13,14,15,16
  @Transactional
  public void CheckModemMiseEnServiceAndDateInterruptionConnection() throws IOException {
    try {
      LocalDate currentDate = LocalDate.now();
      LocalDate datecreationPlusOneDay = currentDate.plusDays(1);

      Date dateCreationPlusOneDay =
          Date.from(datecreationPlusOneDay.atStartOfDay(ZoneId.systemDefault()).toInstant());

      SimpleDateFormat sm = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
      List<String> listeUserNonConnecter =
          radcheckService.findExpirationByDate(sm.format(dateCreationPlusOneDay));
      List<Abonnement> listeUser =
          abonnementRepository.findClientsbyloginModem(listeUserNonConnecter);

      File filePath = createExcelFile(listeUser);
      notificationservice.sendSimpleMailHtmlWithAttachement(xlsMailResponsable,xlsMailCC,
          "Veuillez trouver ci-joint la liste des clients ayant subi une interruption de connexion la veille.Le nombre total est"
              + listeUserNonConnecter.size(),
          "Clients avec expiration de connexion demain", filePath);
      CrmUtils.deleteFile(filePath);



      // Subtract one day
      LocalDate yesterday = currentDate.minusDays(1);
      Date dateYesteday = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
      List<DemandeAbonnement> MisEnService =
          demandeAbonnementRepository.findAllClientNonConnecterMiseEnService();
      File filePathAbboenenemtMisEnService = createExcelDemandeAbonnementFile(MisEnService);
      notificationservice.sendSimpleMailHtmlWithAttachement(xlsMailResponsable,xlsMailCC,
          "Veuillez trouver ci-joint les demandes mises en service hier. Le nombre total de demandes est de"
              + MisEnService.size(),
          "Abonnement Mise en service", filePathAbboenenemtMisEnService);
      CrmUtils.deleteFile(filePathAbboenenemtMisEnService);

      LocalDate datecreationMinus9Day = currentDate.minusDays(8);

      Date dateCreationMinus9Day =
          Date.from(datecreationMinus9Day.atStartOfDay(ZoneId.systemDefault()).toInstant());

      // List<Abonnement> MisEnServiceDepasse9Day =
      // abonnementRepository.findAllClientNonConnecterMiseEnService(dateCreationMinus9Day);
      // File filePathAbboenenemtMisEnServiceDepasse9Day = createExcelFile(MisEnServiceDepasse9Day);
      // notificationservice.sendSimpleMailHtmlWithAttachement(xlsMailResponsable,
      // "Veuillez trouver ci-joint les demandes mises en service depuis 9 jours, dont les clients
      // n'ont pas encore récupéré leurs modems. Le nombre total de demandes est "
      // + MisEnServiceDepasse9Day.size(),
      // "Abonnement Mise en service depuis 9 jours", filePathAbboenenemtMisEnServiceDepasse9Day);
      // CrmUtils.deleteFile(filePathAbboenenemtMisEnServiceDepasse9Day);
      // List<Long> abonnementResiler = MisEnServiceDepasse9Day.stream().map(obj ->
      // obj.getClientid())
      // .collect(Collectors.toList());
      // User user = userservice.findTop1UsersByEmail("system@chifco.com");
      // Statut status = statutRepository.findStatutByNomStatut(NomStatutChifco.RESILIATION);
      // abonnementRepository.findClientsansetItToResiler(abonnementResiler, user, status, new
      // Date());
      // demandeAbonnementRepository.findDemandeAbonementAndResilier(abonnementResiler, status);
    } catch (IOException e) {
      logger.error("CheckModemMiseEnServiceAndDateInterruptionConnection" + e);

    }
  }

  public File createExcelFile(List<Abonnement> listeUser) throws IOException {
    File file = new File("abonnements.xls");

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Abonnements");
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    // Create Header Row
    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("Cin");
    headerRow.createCell(1).setCellValue("Name");
    headerRow.createCell(2).setCellValue("Last Name");
    headerRow.createCell(3).setCellValue("Tel fix");
    headerRow.createCell(4).setCellValue("Tel Mobile");
    headerRow.createCell(5).setCellValue("Date mise en service");
    // Add more headers as needed based on Abonnement fields

    // Populate Data Rows
    int rowNum = 1;
    for (Abonnement abonnement : listeUser) {
      Row row = sheet.createRow(rowNum++);
      row.createCell(0).setCellValue(abonnement.getCin());
      row.createCell(1).setCellValue(abonnement.getFirstName());
      row.createCell(2).setCellValue(abonnement.getLastName());
      row.createCell(3).setCellValue(abonnement.getTelFixe());
      row.createCell(4).setCellValue(abonnement.getTelMobile());
      row.createCell(5).setCellValue(dateFormat.format(abonnement.getDateDeMiseEnService()));
      // Add more cells based on Abonnement fields
    }

    // Write to file
    try (FileOutputStream fileOut = new FileOutputStream(file)) {
      workbook.write(fileOut);
    }

    workbook.close();
    return file;
  }

  public File createExcelDemandeAbonnementFile(List<DemandeAbonnement> listeUser)
      throws IOException {
    File file = new File("abonnements.xls");
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Abonnements");

    // Create Header Row
    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("Cin");
    headerRow.createCell(1).setCellValue("Name");
    headerRow.createCell(2).setCellValue("Last Name");
    headerRow.createCell(3).setCellValue("Tel fix");
    headerRow.createCell(4).setCellValue("Tel Mobile");
    headerRow.createCell(5).setCellValue("Date mise en service");
    // Add more headers as needed based on Abonnement fields

    // Populate Data Rows
    int rowNum = 1;
    for (DemandeAbonnement abonnement : listeUser) {
      Row row = sheet.createRow(rowNum++);
      row.createCell(0).setCellValue(abonnement.getCin());
      row.createCell(1).setCellValue(abonnement.getFirstName());
      row.createCell(2).setCellValue(abonnement.getLastName());
      row.createCell(3).setCellValue(abonnement.getTelFixe());
      row.createCell(4).setCellValue(abonnement.getTelMobile());
      row.createCell(5).setCellValue(dateFormat.format(abonnement.getDateDeMiseEnService()));
      // Add more cells based on Abonnement fields
    }

    // Write to file
    try (FileOutputStream fileOut = new FileOutputStream(file)) {
      workbook.write(fileOut);
    }

    workbook.close();
    return file;
  }

  @Scheduled(cron = "00 10 08,09,10,11,12,14,15,16,17,18,19,20 * * ?") // // Run At 03h evry day

  @Transactional
  public void CheckIfPayementBRDIsStillNull() {
    try {
      logger.info("click to pay start ");
      List<ClicToPay> clickToNotValidate = clickToPayRepository.findListByIsPassed(null);
      for (ClicToPay clicToPay : clickToNotValidate) {
        String orderStatusUrl = urlKonnect + "payments/" + clicToPay.getOrderId();
        try {

          URL url = new URL(orderStatusUrl);


          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("GET");

          // Get the response from the server
          int responseCode = connection.getResponseCode();
          ClicToPay exitClickToPay =
              clickToPayRepository.findAbonnementByOrderId(clicToPay.getOrderId());
          if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response data
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
              response.append(line);
            }
            reader.close();

            // Parse the response as needed (e.g., JSON parsing)
            String responseText = response.toString();
            // Use responseText as needed

            if (exitClickToPay != null) {
              exitClickToPay.setIsPassed(true);
              ObjectMapper objectMapper = new ObjectMapper();
              JsonNode rootNode = objectMapper.readTree(responseText);
              JsonNode paymentNode = rootNode.get("payment");
              if (paymentNode != null && paymentNode.get("status") != null
                  && paymentNode.get("status").asText().equals("completed")) {
                exitClickToPay.setApprovalCode(paymentNode.get("orderId").asText());
                exitClickToPay.setIsPassed(true);

                clickToPayRepository.save(exitClickToPay);
                Bordereau existBrd = bordereaurRepository
                    .findBordereauByReferenceBordereau(exitClickToPay.getBordereau());
                if (existBrd != null) {
                  User user = userRepository.findTop1UsersByTypeuser("SYSTEM");
                  Date date = new Date();
                  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                  String formattedDate = formatter.format(date);
                  bordereauService.accpetBordereauByAdmin(existBrd, user, "", formattedDate,
                      typePayementBordereau.PayementParCarte, null);
                }
              } else {
                exitClickToPay.setIsPassed(false);
              }
              if (paymentNode.has("status")) {
                exitClickToPay.setErrorCode(paymentNode.get("status").asText());
                exitClickToPay.setErrorMessage(paymentNode.get("status").asText());

              }

            }

            // Log the response
            System.out.println("cron validate Konnect Response: " + responseText);
          } else {
            // Handle the error response
            System.err.println("cron validate Konnect  HTTP Error: " + responseCode);
            if (exitClickToPay != null) {
              exitClickToPay.setIsPassed(false);

            }
          }

          // Disconnect the connection
          connection.disconnect();
          logger.info("click to pay end ");

        } catch (Exception err) {
          logger.error("cron validate Konnect 1 catch" + err);
        }
      }

    } catch (Exception err) {
      logger.error("cron validate Konnect 2 catch" + err);
    }

  }

  @Scheduled(cron = "00 */30  09,10,11,12,13,14,15,16,17,18 * * ?") // // Run At 03h evry day
  // 11,12,13,14,15,16
  @Transactional
  public void PayeMeeCheckIfPayementBRDIsStillNull() {
    try {
      logger.info("paymee cron start ");
      List<ClicToPay> clickToNotValidate = clickToPayRepository.findListByIsNotPassed();
      for (ClicToPay clicToPay : clickToNotValidate) {
        String orderStatusUrl = PaymeeUrl  + clicToPay.getOrderId() + "/check";
        try {

          URL url = new URL(orderStatusUrl);
           final String tokenGet = "Token b84039920aed434c7330ad684454f3f5b3fa4bcb";

          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("GET");
	      connection.setRequestProperty("Authorization", tokenGet); 
          System.out.println("cron validate paymee token payement: " + clicToPay.getOrderId());

          // Get the response from the server
          int responseCode = connection.getResponseCode();
          ClicToPay exitClickToPay =
              clickToPayRepository.findAbonnementByOrderId(clicToPay.getOrderId());
          if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response data
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
              response.append(line);
            }
            reader.close();

            // Parse the response as needed (e.g., JSON parsing)
            String responseText = response.toString();
            // Use responseText as needed

            if (exitClickToPay != null) {
              exitClickToPay.setIsPassed(true);
              ObjectMapper objectMapper = new ObjectMapper();
              JsonNode rootNode = objectMapper.readTree(responseText);
              JsonNode paymentNode = rootNode.get("data");
              if (paymentNode != null && paymentNode.get("payment_status") != null
                  && paymentNode.get("payment_status").asText().equals("true")) {
                exitClickToPay.setApprovalCode(paymentNode.get("transaction_id").asText());
                exitClickToPay.setIsPassed(true);

                clickToPayRepository.save(exitClickToPay);
                Bordereau existBrd = bordereaurRepository
                    .findBordereauByReferenceBordereau(exitClickToPay.getBordereau());
                if (existBrd != null) {
                  User user = userRepository.findTop1UsersByTypeuser("SYSTEM");
                  Date date = new Date();
                  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                  String formattedDate = formatter.format(date);
                  bordereauService.accpetBordereauByAdmin(existBrd, user, "", formattedDate,
                      typePayementBordereau.PayementParCarte, null);
                }
              } else {
                exitClickToPay.setIsPassed(false);
              }
              if (paymentNode.has("payment_status")) {
                exitClickToPay.setErrorCode(paymentNode.get("payment_status").asText());
                exitClickToPay.setErrorMessage(paymentNode.get("payment_status").asText());

              }

            }

            // Log the response
            System.out.println("cron validate paymee Response: " + responseText);
          } else {
            // Handle the error response
            System.err.println("cron validate paymee  HTTP Error: " + responseCode);
            if (exitClickToPay != null) {
              exitClickToPay.setIsPassed(false);

            }
          }

          // Disconnect the connection
          connection.disconnect();
          logger.info("cron paymee pay end ");

        } catch (Exception err) {
          logger.error("cron validate paymee 1 catch" + err);
        }
      }

    } catch (Exception err) {
      logger.error("cron validate paymee 2 catch" + err);
    }

  }
  
  @Scheduled(cron = "00 08 08 * * ?") // // Run At 03h evry day
  @Transactional
  public void echanceFacture5g() {
    try {
        LocalDate currentDate = LocalDate.now();

      

        SimpleDateFormat sm = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
      
        List<Abonnement> listeUser =
            abonnementRepository.findClientsbyFactureNonPayeeAndTypeAbonnement(currentDate , TypeAbonnment.Box);
if(listeUser.size() > 0) {
        File filePath = createExcelFile(listeUser);
        notificationservice.sendSimpleMailHtmlWithAttachement(xlsMailResponsable,xlsMailCC,
            "Vous trouverez en pièce jointe la liste des clients abonnés à l’offre 5G présentant des factures impayées échues."
            
                ,
            "Liste des clients 5G avec factures impayées échues", filePath);
        CrmUtils.deleteFile(filePath);
      }
      logger.info("email send ");



    } catch (Exception err) {
      logger.error("cron validate paymee 2 catch" + err);

    }
  }



  @Scheduled(cron = "0 15 8,18 * * *")
  @Transactional
  public void distributeReclamationsToTechnicians() {
    logger.info("CRON: distributeReclamationsToTechnicians START");

    try {
      List<User> listTechniciens = userRepository.findEnabledUsersByRole("ROLE_TECHNICIEN");

      if (listTechniciens.isEmpty()) {
        logger.info("No technicians found in the system");
        return;
      }

      logger.info("Found {} technicians", listTechniciens.size());

      List<Reclamation> unsentReclamations = reclamationRepository.findAllUnsentReclamations();

      if (unsentReclamations.isEmpty()) {
        logger.info("No unsent reclamations found");
        return;
      }

      logger.info("Found {} unsent reclamations", unsentReclamations.size());

      Map<User, List<String>> techniciansWithGouvernorats =
          zoneService.getAllTechniciansWithGouvernorats(listTechniciens);

      if (techniciansWithGouvernorats.isEmpty()) {
        logger.info("No technicians have gouvernorats assigned to their zones");
        return;
      }

      Map<User, List<Reclamation>> reclamationsByTechnician = new HashMap<>();
      Map<User, List<String>> technicianGouvernoratsMap = new HashMap<>();

      for (Map.Entry<User, List<String>> entry : techniciansWithGouvernorats.entrySet()) {
        User technician = entry.getKey();
        List<String> technicianGouvernorats = entry.getValue();
        Set<String> upperTechnicianGouvernorats =
            technicianGouvernorats.stream().map(String::toUpperCase).collect(Collectors.toSet());

        List<Reclamation> matchingReclamations = unsentReclamations.stream().filter(reclamation -> {
          String reclamationGouv = reclamation.getGouvernorat();
          if (reclamationGouv == null || reclamationGouv.trim().isEmpty()) {
            return false;
          }
          String mappedGouv = GouvernoratMapping.getDatabaseGouvernorat(reclamationGouv);
          if (mappedGouv == null) {
            logger.warn("Unknown gouvernorat in reclamation {}: {}",
                reclamation.getRef_reclamation(), reclamationGouv);
            return false;
          }
          return upperTechnicianGouvernorats.contains(mappedGouv.toUpperCase());
        }).collect(Collectors.toList());

        if (!matchingReclamations.isEmpty()) {
          reclamationsByTechnician.put(technician, matchingReclamations);
          technicianGouvernoratsMap.put(technician, technicianGouvernorats);
          logger.info("Technician {} will receive {} reclamations for gouvernorats: {}",
              technician.getEmail(), matchingReclamations.size(), technicianGouvernorats);
        }
      }

      if (reclamationsByTechnician.isEmpty()) {
        logger.info("No matching reclamations found for any technician");
        return;
      }

      Map<User, List<Reclamation>> successfullySent = new HashMap<>();
      List<Long> allSentReclamationIds = new ArrayList<>();

      for (Map.Entry<User, List<Reclamation>> entry : reclamationsByTechnician.entrySet()) {
        User technician = entry.getKey();
        List<Reclamation> technicianReclamations = entry.getValue();
        List<String> technicianGouvernorats = technicianGouvernoratsMap.get(technician);

        try {
          for (Reclamation reclamation : technicianReclamations) {
            reclamation.setTreatedBy(technician);
            reclamation.setModifiedDate(new Date());
            reclamation.setStatuttech(TechnicienStatus.Affected);
            reclamationRepository.save(reclamation);
            String message = (reclamation.getTreatedBy() == null)
                ? "La réclamation a été assignée à l'agent : " + technician.getFirstName() + " "
                    + technician.getLastName()
                : "L'assignation de la réclamation a été changée à : " + technician.getFirstName()
                    + " " + technician.getLastName();
            User userSystem = userRepository.findTop1UsersByTypeuser("SYSTEM");
            reclamationHistoryService.saveNewHistorique(userSystem, reclamation.getReclamationid(),
                message);
          }

          logger.info("Assigned technician {} to {} reclamations", technician.getEmail(),
              technicianReclamations.size());
          String htmlBody = HtmlTemplateEmail.HtmlEmailTechnicianDistribution(technician,
              technicianReclamations, technicianGouvernorats);

          List<String> technicianEmails = new ArrayList<>();
          technicianEmails.add(technician.getEmail());

          notificationservice.sendMultipleMailHtml(technicianEmails, htmlBody,
              "📋 Nouvelles Réclamations Assignées  ");
          List<Long> sentIds = technicianReclamations.stream().map(Reclamation::getReclamationid)
              .collect(Collectors.toList());
          allSentReclamationIds.addAll(sentIds);
          successfullySent.put(technician, technicianReclamations);

          logger.info("Email sent to technician: {} ({} reclamations)", technician.getEmail(),
              technicianReclamations.size());

        } catch (Exception e) {
          logger.error("Failed to send email to technician: {}", technician.getEmail(), e);
        }
      }

      if (!allSentReclamationIds.isEmpty()) {
        reclamationRepository.markReclamationsAsSent(allSentReclamationIds);
        logger.info("Marked {} reclamations as sent", allSentReclamationIds.size());
      }
      /*
       * if (!successfullySent.isEmpty()) { String summaryHtml =
       * HtmlTemplateEmail.HtmlEmailDistributionSummary(successfullySent); List<String> adminMails =
       * new ArrayList<>(); adminMails.add(xlsMailSAV);
       * notificationservice.sendSimpleMailHtml(xlsMailSAV, summaryHtml,
       * "📊 Rapport de Distribution des Réclamations - ");
       * 
       * logger.info("Summary report sent to admins"); }
       * 
       * List<Reclamation> unsentAfterDistribution = unsentReclamations.stream() .filter(rec ->
       * !allSentReclamationIds.contains(rec.getReclamationid())) .collect(Collectors.toList());
       * 
       * if (!unsentAfterDistribution.isEmpty()) {
       * logger.warn("{} reclamations have no matching technicians:",
       * unsentAfterDistribution.size()); for (Reclamation rec : unsentAfterDistribution) {
       * logger.warn("  - Reclamation {} (Gouvernorat: {}) has no technician assigned",
       * rec.getRef_reclamation(), rec.getGouvernorat()); } }
       */
      logger.info(
          "CRON: distributeReclamationsToTechnicians END - Sent {} reclamations to {} technicians",
          allSentReclamationIds.size(), successfullySent.size());

    } catch (Exception e) {
      logger.error("CRON ERROR distributeReclamationsToTechnicians", e);
    }
  }




    
    
  
  

  @Scheduled(cron = "00 04 05 * * ?") // // Run At 03h evry day
  @Transactional
  public void factureQAD() {
    try {
        logger.info("factureQAD start ");
       //for (int i = 24; i >= 0; i--) {

    	LocalDate yesterday = LocalDate.now().minusDays(1);
    	Date endDate = Date.from(
    	        yesterday.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
    	);
    	Date date = Date.from(
    	        yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant()
    	);

        List<Facture> entryFactures =
        		FactureRepository.findFactureByCreatedDateBetween(date ,endDate);
        List<AvoirClient> entryAvoir =
        		avoirRepository.findFactureByCreatedDateBetween(date,endDate);
    	logger.info("entryFactures.size()  " , entryFactures.size() );

if(entryFactures.size() > 0) {
  File  ficherGenerer  =  createExcelFileForEntryFacture(entryFactures , entryAvoir,pathPgh , yesterday.toString());
       
            
  testFstp(ficherGenerer.getAbsolutePath());
             
        
}
       
logger.info("factureQAD end ");



      
    }
    catch (Exception err){
        logger.error("cron validate paymee 2 catch" + err);

    }
    }
  
  
  public File createExcelFileForEntryFacture(List<Facture> facts , List<AvoirClient> avrs ,
		 String outputPath, String yesterday) throws IOException {
	/*    File file = new File("abonnements.xls");

	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("entryFactures");
	    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    DecimalFormat prixFormat = new DecimalFormat("00.000");
	    DecimalFormat tvaFormat = new DecimalFormat("00");
	    // Create Header Row
	    Row headerRow = sheet.createRow(0);
	    headerRow.createCell(0).setCellValue("Num Facture");
	    headerRow.createCell(1).setCellValue("Date Facture");
	    headerRow.createCell(2).setCellValue("Code Client");
	    headerRow.createCell(3).setCellValue("Nom Client ");
	   	headerRow.createCell(4).setCellValue("Identifiant");
	    headerRow.createCell(5).setCellValue("Adresse Client");
	    headerRow.createCell(6).setCellValue("Code Article QAD");
	    headerRow.createCell(7).setCellValue("Prix HT");
	    headerRow.createCell(8).setCellValue("Taux TVA");
	    headerRow.createCell(9).setCellValue("%remise");

	    // Add more headers as needed based on Abonnement fields

	    // Populate Data Rows
	    int rowNum = 1;
	    
	    for (Facture fact : facts) {
	    	 List<EntryFactures> entryFactures = fact.getEntriesFacture();
	    for (EntryFactures entry : entryFactures) {
	    	if(entry.getPrixTotalHT()>0.5) {
	      Row row = sheet.createRow(rowNum++);
	      row.createCell(0).setCellValue(fact.getRef_facture());
	      row.createCell(1).setCellValue(dateFormat.format(fact.getCreatedDate()));
	      row.createCell(2).setCellValue(fact.getAbonnement().getTelFixe());
	      row.createCell(3).setCellValue(fact.getAbonnement().getFirstName() + " " + fact.getAbonnement().getLastName());
	      row.createCell(4).setCellValue(fact.getAbonnement().getCin());
	      row.createCell(5).setCellValue(fact.getAbonnement().getVille().getVilleName() + " " + fact.getAbonnement().getGouvernorat().getGouvernoratName());
          String codeArticle = "";

	      if (entry.getPourcentageTva() == 19) {
              codeArticle = "ZZCH00000029";
          } else if (entry.getPourcentageTva() == 7) {
              codeArticle = "ZZCH00000031";
          }

	      row.createCell(6).setCellValue(codeArticle);
	      row.createCell(7).setCellValue(prixFormat.format(entry.getPrixTotalHT()));
	      row.createCell(8).setCellValue(tvaFormat.format(entry.getPourcentageTva()));
	      row.createCell(9).setCellValue(0);
	    	}
	      // Add more cells based on Abonnement fields
	    }
	    }
	    
	    for (AvoirClient avr : avrs) {
	    	 List<EntryAvoirClient> entryFactures = avr.getAvoiClientEntry();
	    for (EntryAvoirClient entry : entryFactures) {
	      Row row = sheet.createRow(rowNum++);
	      row.createCell(0).setCellValue(avr.getRefAvoirClient());
	      row.createCell(1).setCellValue(dateFormat.format(avr.getCreatedDate()));
	      row.createCell(2).setCellValue(avr.getAbonnement().getTelFixe());
	      row.createCell(3).setCellValue(avr.getAbonnement().getFirstName() + " " + avr.getAbonnement().getLastName());
	      row.createCell(4).setCellValue(avr.getAbonnement().getCin());
	      row.createCell(5).setCellValue(avr.getAbonnement().getVille().getVilleName() + " " + avr.getAbonnement().getGouvernorat().getGouvernoratName());
	      String codeArticle = "";

	      if (entry.getBaseTva() == 19) {
              codeArticle = "ZZCH00000029";
          } else if (entry.getBaseTva() == 7) {
              codeArticle = "ZZCH00000031";
          }
	      row.createCell(6).setCellValue(codeArticle);
	      row.createCell(7).setCellValue("-"+prixFormat.format(entry.getMontantHt()));
	      row.createCell(8).setCellValue(tvaFormat.format(entry.getBaseTva()));
	      row.createCell(9).setCellValue(0);

	      // Add more cells based on Abonnement fields
	    }
	    }
	    // Write to file
        File pathFolder = new File(outputPath+"/vente");

      if (!pathFolder.exists()) {
          pathFolder.mkdirs();
          pathFolder.setWritable(true);

        }
      StreamResult result = new StreamResult(pathFolder+   "/client"+ new Date()+".csv");
      

	    workbook.close();
	    */
	  DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
	  symbols.setDecimalSeparator('.');

	  DecimalFormat prixFormat = new DecimalFormat("00.000", symbols);
	  DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    DecimalFormat tvaFormat = new DecimalFormat("00");
	    File pathFolder = new File(outputPath + "/vente");

	    if (!pathFolder.exists()) {
	        pathFolder.mkdirs();
	    }

	    File file = new File(pathFolder, yesterday+"client_"  + ".csv");

	    BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	    // Header
	    writer.write("factureId;Num Facture;Date Facture;Code Client;Nom Client;Identifiant;Adresse Client;Code Article QAD;Prix HT;Taux TVA;Remise;Date echance ;Description ");
	    writer.newLine();

	    for (Facture fact : facts) {
	        for (EntryFactures entry : fact.getEntriesFacture()) {
	        	if(entry.getPrixTotalHT()>0.5) {  
	        	String codeArticle = "";

	     	      if (entry.getPourcentageTva() == 19) {
	                   codeArticle = "ZZCH00000029";
	               }
	     	     else if (entry.getPourcentageTva() == 7 && fact.getIsFactureResilation()) {
	                   codeArticle = "ZZCH00000053";
	               }
	     	    else if (entry.getPourcentageTva() == 7 && entry.getProductName().contains("Complément")) {
	                   codeArticle = "ZZCH00000052";
	               }
	     	      else if (entry.getPourcentageTva() == 7) {
	                   codeArticle = "ZZCH00000031";
	               }
	            writer.write( fact.getFactureId() +";"+
	                    fact.getRef_facture() + ";" +
	                    dateFormat.format(fact.getCreatedDate()) + ";" +
	                    fact.getAbonnement().getTelFixe() + ";" +
	                    fact.getAbonnement().getFirstName() + " " + fact.getAbonnement().getLastName() + ";" +
	                    fact.getAbonnement().getCin() + ";" +
	                    fact.getAbonnement().getGouvernorat().getGouvernoratName() + " " +
	                    fact.getAbonnement().getVille().getVilleName() + ";" +
	                    codeArticle + ";" +
	                    prixFormat.format(entry.getPrixTotalHT()) + ";" +
	                    tvaFormat.format(entry.getPourcentageTva()) + ";" +
	                    "0" + ";"+ dateFormat.format(fact.getDate_echeance()) + ";" +
	                    entry.getProductName()+ ";" 
	            );

	            writer.newLine();
	        }
	        }
	    }

	    for (AvoirClient avr : avrs) {
	        for (EntryAvoirClient entry : avr.getAvoiClientEntry()) {
	  	      String codeArticle = "";

	        	  if (entry.getBaseTva() == 19) {
	                  codeArticle = "ZZCH00000029";
	              } else if (entry.getBaseTva() == 7) {
	                  codeArticle = "ZZCH00000031";
	              }
	            writer.write(
	            		avr.getAvoirId()+";"+
	                    avr.getRefAvoirClient() + ";" +
	                    dateFormat.format(avr.getCreatedDate()) + ";"+
	                    avr.getAbonnement().getTelFixe() + ";" +
	                    avr.getAbonnement().getFirstName() + " " + avr.getAbonnement().getLastName() + ";" +
	                    avr.getAbonnement().getCin() + ";" +
	                    avr.getAbonnement().getGouvernorat().getGouvernoratName() + " " +
	                    avr.getAbonnement().getVille().getVilleName() + ";" +
	                    codeArticle + ";" +
	                    ("-"+prixFormat.format(entry.getMontantHt())) + ";" +
	                    tvaFormat.format(entry.getBaseTva()) + ";" +
	                    "0" + ";"+
	                    " "+";"+
	                    avr.getMotifAvoir() +";"
	            );

	            writer.newLine();
	        }
	    }

	    writer.close();

	  
	    return file;
	  }
  




      public static void testFstp(String filePath) {
          String host = "102.164.112.110"; //"172.16.16.205"; 
          int port = 22;
          String username = "chifco"; //"root" ;
          String password = "CHF@2025++"; //"Chifc@2022";

          String localFile = filePath;
          String remoteDir = "/Chifco/vente/";

          Session session = null;
          Channel channel = null;
          ChannelSftp sftp = null;

          try {
              JSch jsch = new JSch();
              session = jsch.getSession(username, host, port);
              session.setPassword(password);

              // éviter erreur known_hosts
              session.setConfig("StrictHostKeyChecking", "no");

              session.connect();

              channel = session.openChannel("sftp");
              channel.connect();

              sftp = (ChannelSftp) channel;

              // aller au dossier distant
              sftp.cd(remoteDir);

              // upload fichier
              File file = new File(localFile);
              sftp.put(new FileInputStream(file), file.getName());

              System.out.println("✅ Fichier upload avec succès !");

          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              if (sftp != null) sftp.exit();
              if (channel != null) channel.disconnect();
              if (session != null) session.disconnect();
          }
      }
  

}
