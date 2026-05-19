package crm.chifco.com.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import crm.chifco.com.DTOclass.RecapFactureLimitDTO;
import crm.chifco.com.DTOclass.TopRevendeurCommissionDash;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.BordereaurRepository;
import crm.chifco.com.repository.CommissionRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.DemandeModemRepository;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.FicheRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.DashboardService;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.DateUtils;

@Service("DashboardService")
public class DashboardServiceImpl implements DashboardService {
  @Autowired
  AbonnementRepository abonnementRepository;
  @Autowired
  EncaissementRepository encaissementRepository;
  @Autowired
  DemandeAbonnementRepository demandeAbonnementRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  ModemRepository modemRepository;
  @Autowired
  CommissionRepository commissionRepository;
  @Autowired
  FactureRepository factureRepository;
  @Autowired
  AvoirRepository avoirRepository;
  @Autowired
  BordereaurRepository bordereaurRepository;
  @Autowired
  FicheRepository ficheRepository;
  @Autowired
  DemandeModemRepository demandeModemRepository;
  @Autowired
  GouvernoratRepository gouvernoratRepository;

  @Override
  public Model returnDashbordStatsAdmin(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> Administrateur = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasReadAdminsitPriv = Administrateur.contains("READ_ADMINISTRATEUR");
      model.addAttribute("hasReadAdminsitPriv", hasReadAdminsitPriv);
      List<String> isfinance = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean hasfinancePriv = isfinance.contains("VIEW_FINANCE_CHART");
      model.addAttribute("hasfinancePriv", hasfinancePriv);
      List<String> privreacard = user.getRole().getStringsRole(user.getRole().getPrivileges());
      Boolean isprivreadcard = privreacard.contains("VIEW_CARDS_ADMIN");
      model.addAttribute("isprivreadcard", isprivreadcard);
      Date today = new Date();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(today);
      Date startOfMonth = DateUtils.getStartOfMonth();
      Date endOfMonth = DateUtils.getEndOfMonth();
      Date startOfLastMonth = DateUtils.getStartOfLastMonth();
      Date endOfLastMonth = DateUtils.getEndOfLastMonth();
      List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
      model.addAttribute("villes", listGouvernorats);
      if (hasReadAdminsitPriv || isprivreadcard) {
        this.StatsCardForAdmin(model, startOfMonth, endOfMonth, startOfLastMonth, endOfLastMonth);
      }
      if (hasReadAdminsitPriv || hasfinancePriv) {
        this.StatsModemByModelFOrAdmin(model, today, startOfMonth, endOfMonth, user);
      }
    }
    return model;
  }



  public Model getTotaAbonnementByTypePayment(Model model, User user) {
    List<Map<String, Object>> payment = new ArrayList<Map<String, Object>>();
    if ((user.getTypeUser()).equals("DISTRIBUTEUR")) {
      payment = abonnementRepository.getTotalAbonnementStatsByTypePaiement(null, user.getUserid());
    } else if ((user.getTypeUser()).equals("REVENDEUR") || (user.getTypeUser()).equals("POS")) {
      payment = abonnementRepository.getTotalAbonnementStatsByTypePaiement(user.getUserid(), null);
    } else {
      payment = abonnementRepository.getTotalAbonnementStatsByTypePaiement(null, null);
    }
    List<Integer> abonnementTotal = new ArrayList<>();
    List<String> Typepayments = new ArrayList<>();
    payment.forEach(row -> {
      Typepayments.add((String) row.get("typepaiment"));
      abonnementTotal.add((Integer) row.get("abonnementCount"));

    });
    model.addAttribute("Typepayments", Typepayments);
    model.addAttribute("abonnementTotal", abonnementTotal);
    return model;
  }

  public Model StatsModemByModelFOrAdmin(Model model, Date today, Date startOfMonth,
      Date endOfMonth, User user) {
    List<Map<String, Object>> modelModemCounts = modemRepository.countModemsByModel();
    model.addAttribute("modelModemCounts", modelModemCounts);
    if ((user.getRole().getRoleName()).equals("ROLE_G.STOCK")) {
      List<Map<String, Object>> nbrModemAffected =
          ficheRepository.getTodayAndMonthSumModemAffectationByUser(today, startOfMonth, endOfMonth,
              user.getUserid());
      Integer nbrModemAffectedToday = ((Number) nbrModemAffected.get(0).get("todaySum")).intValue();
      Integer nbrModemAffectedCurrentMonth =
          ((Number) nbrModemAffected.get(0).get("monthSum")).intValue();
      List<Map<String, Object>> nbrDemandModem = demandeModemRepository
          .getTodayAndMonthSumOfDemandModem(today, startOfMonth, endOfMonth, null, null);
      Integer nbrDemandModemToday = ((Number) nbrDemandModem.get(0).get("todaySum")).intValue();
      Integer nbrDemandModemCurrentMonth =
          ((Number) nbrDemandModem.get(0).get("monthSum")).intValue();

      model.addAttribute("nbrModemAffectedToday", nbrModemAffectedToday + " Aujourd'hui");
      model.addAttribute("nbrModemAffectedCurrentMonth",
          nbrModemAffectedCurrentMonth + " pour mois courant");
      model.addAttribute("nbrDemandModemToday", nbrDemandModemToday + " Aujourd'hui");
      model.addAttribute("nbrDemandModemCurrentMonth",
          nbrDemandModemCurrentMonth + " pour mois courant");
    }
    return model;
  }

  private Model StatsCardForAdmin(Model model, Date startOfMonth, Date endOfMonth,
      Date startOfLastMonth, Date endOfLastMonth) {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedYesterday = yesterday.format(formatter);
    Object abonnementStats = abonnementRepository.getAbonnementSummary(formattedYesterday);
    Object[] abonnementStatsArray = (Object[]) abonnementStats;
    Long TotalRevendeurActifs = userRepository.countUsersByIsEnabled();
    Long NbreRevendeur = userRepository.countUsersByRole("ROLE_REVENDEUR_DESACTIVE");
    Long NbreRevendeurActive = userRepository.countUsersByRole("ROLE_REVENDEUR");
    Long activeAbonnement = (Long) abonnementStatsArray[0];
    Long resilierAbonnementThisMonth = (Long) abonnementStatsArray[1];
    Long resilierAbonnementLastMonth = (Long) abonnementStatsArray[2];
    Long totalAbonnement = (Long) abonnementStatsArray[3];
    Long nouvelleAbonnementsThisMonth = (Long) abonnementStatsArray[4];
    Long nouvelleAbonnementsLastMonth = (Long) abonnementStatsArray[5];
    List<Map<String, Object>> counts =
        demandeAbonnementRepository.countDemandeAbonnementTodayAndYesterday(formattedYesterday);
    if (counts != null && !counts.isEmpty()) {
      Map<String, Object> result = counts.get(0);
      Long todayCountDemande = (Long) result.get("todayCountDemande");
      Long yesterdayCountDemande = (Long) result.get("yesterdayCountDemande");
      model.addAttribute("todayCountDemande", todayCountDemande);
      model.addAttribute("yesterdayCountDemande", yesterdayCountDemande + " demande(s) pour hier ");
    }
    model.addAttribute("activeAbonnement", activeAbonnement);
    model.addAttribute("resilierAbonnement", resilierAbonnementThisMonth);
    model.addAttribute("totalAbonnement", totalAbonnement);
    model.addAttribute("nouvelleAbonnementsThisMonth", nouvelleAbonnementsThisMonth);
    model.addAttribute("TotalRevendeurActifs", TotalRevendeurActifs);
    model.addAttribute("percentageChange", nouvelleAbonnementsLastMonth + " clients(s) pour hier ");
    model.addAttribute("resilierAbonnementLastMonth", resilierAbonnementLastMonth + " pour hier");
    model.addAttribute("NbreRevendeur", NbreRevendeur);
    model.addAttribute("NbreRevendeur", NbreRevendeur);
    model.addAttribute("NbreRevendeurActive", NbreRevendeurActive);

    return model;
  }

  private String convertToString(Long x, Long y) {
    if (y != 0) {
      String percentageChange = String.format("%.2f", ((double) (x - y) / y) * 100);
      percentageChange = percentageChange.split("\\,")[0] + "%  par rapport à hier ";

      return percentageChange;
    } else {
      return "Aucune valeur pour hier";
    }

  }

  @Override
  public Model returnDashbordStatsRevPosDist(Model model) {
    Date today = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today);
    Integer yearInteger = Integer.valueOf(calendar.get(Calendar.YEAR));
    Date startOfMonth = DateUtils.getStartOfMonth();
    Date endOfMonth = DateUtils.getEndOfMonth();
    Date startOfLastMonth = DateUtils.getStartOfLastMonth();
    Date endOfLastMonth = DateUtils.getEndOfLastMonth();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    this.getActivModemByModelForOthers(model, user);
    this.getCardsForDashboardOthers(model, today, startOfMonth, endOfMonth, user);
    this.StatsDemandeAbonnementForOthers(model, yearInteger, startOfLastMonth, endOfLastMonth,
        user);
    this.getCommissionRevendeur(model, user, yearInteger, startOfLastMonth, endOfLastMonth);
    this.getLimitIntervalForRevDash(model, user);
    this.getTotaAbonnementByTypePayment(model, user);
    return model;
  }

  private Model StatsDemandeAbonnementForOthers(Model model, Integer yearInteger,
      Date startOfLastMonth, Date endOfLastMonth, User user) {
    List<Integer> distinctYears = demandeAbonnementRepository.getDistinctYears();
    List<Map<String, Object>> result = new ArrayList<>();
    List<String> yearForDemandAbon = new ArrayList<String>();
    if ((user.getTypeUser()).equals("REVENDEUR") || (user.getTypeUser()).equals("POS")) {
      List<Map<String, Object>> statsForYear = demandeAbonnementRepository
          .getDemandeAbonnementStatsForOthers(distinctYears, user.getUserid(), null);
      result.addAll(statsForYear);
    } else {
      List<Map<String, Object>> statsForYear = demandeAbonnementRepository
          .getDemandeAbonnementStatsForOthers(distinctYears, null, user.getUserid());
      result.addAll(statsForYear);
    }
    List<String> yearStrings =
        result.stream().map(stat -> String.valueOf(stat.get("year"))).collect(Collectors.toList());
    yearForDemandAbon.addAll(yearStrings);
    List<Integer> totalDemandeAbonnement = result.stream()
        .map(stat -> ((Long) stat.get("totalCount")).intValue()).collect(Collectors.toList());
    List<Integer> annuleeDemandeAbonnement = result.stream()
        .map(stat -> ((Long) stat.get("annuleeCount")).intValue()).collect(Collectors.toList());
    List<Integer> refuseDemandeAbonnement = result.stream()
        .map(stat -> ((Long) stat.get("refuseCount")).intValue()).collect(Collectors.toList());
    List<Integer> activeDemandeAbonnement = result.stream()
        .map(stat -> ((Long) stat.get("activeCount")).intValue()).collect(Collectors.toList());
    model.addAttribute("yearForDemandAbon", yearForDemandAbon);
    model.addAttribute("totalDemandeAbonnement", totalDemandeAbonnement);
    model.addAttribute("annuleeDemandeAbonnement", annuleeDemandeAbonnement);
    model.addAttribute("refuseDemandeAbonnement", refuseDemandeAbonnement);
    model.addAttribute("activeDemandeAbonnement", activeDemandeAbonnement);
    return model;
  }

  public Model getActivModemByModelForOthers(Model model, User user) {
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    Boolean StockModemForDist = StringsRole.contains("READ_GRAPH_MODEM_STOCK_DIST");
    Boolean StockModemForRevOrPOS = StringsRole.contains("READ_GRAPH_MODEM_STOCK_OTHERS");
    if (StockModemForDist) {
      List<Map<String, Object>> modelModemCounts =
          modemRepository.getModelModemCountsForDist(user.getUserid());
      model.addAttribute("modelModemCounts", modelModemCounts);
    } else if (StockModemForRevOrPOS) {
      if ((user.getTypeUser()).equals("REVENDEUR")) {
        List<Map<String, Object>> modelModemCounts =
            modemRepository.getModelModemCountsForRev(user.getUserid());
        model.addAttribute("modelModemCounts", modelModemCounts);
      } else {
        List<Map<String, Object>> modelModemCounts =
            modemRepository.getModelModemCountsForPOS(user.getUserid());
        model.addAttribute("modelModemCounts", modelModemCounts);
      }
    }
    return model;
  }

  public Model getCardsForDashboardOthers(Model model, Date today, Date startOfMonth,
      Date endOfMonth, User user) {
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    Boolean cardRevForDist = StringsRole.contains("READ_CARD_REV_OF_DIST");
    if (cardRevForDist) {
      Long NbreRevendeur =
          userRepository.countUsersByRoleForDist("ROLE_REVENDEUR_DESACTIVE", user.getUserid());
      Long NbreRevendeurActive =
          userRepository.countUsersByRoleForDist("ROLE_REVENDEUR", user.getUserid());
      model.addAttribute("NbreRevendeur", NbreRevendeur);
      model.addAttribute("NbreRevendeurActive", "Active Revendeurs : " + NbreRevendeurActive);
    }
    Boolean affectmod = StringsRole.contains("READ_CARD_AFFECT_MODEM_OTHERS");
    Boolean modemRecu = StringsRole.contains("READ_CARD_RECIEVED_MODEMS");
    if (affectmod) {
      List<Map<String, Object>> nbrModemAffected =
          ficheRepository.getTodayAndMonthSumModemAffectationByUser(today, startOfMonth, endOfMonth,
              user.getUserid());
      Integer nbrModemAffectedToday = ((Number) nbrModemAffected.get(0).get("todaySum")).intValue();
      Integer nbrModemAffectedCurrentMonth =
          ((Number) nbrModemAffected.get(0).get("monthSum")).intValue();
      model.addAttribute("nbrModemAffectedToday", nbrModemAffectedToday);
      model.addAttribute("nbrModemAffectedCurrentMonth",
          "Mois courant: " + nbrModemAffectedCurrentMonth);
    }
    if (modemRecu) {
      List<Map<String, Object>> nbrModemAffected =
          ficheRepository.getTodayAndMonthSumModemRecievedByUser(today, startOfMonth, endOfMonth,
              user.getUserid());
      Integer nbrModemRecievedToday = ((Number) nbrModemAffected.get(0).get("todaySum")).intValue();
      Integer nbrModemRecievedCurrentMonth =
          ((Number) nbrModemAffected.get(0).get("monthSum")).intValue();
      model.addAttribute("nbrModemRecievedToday", nbrModemRecievedToday);
      model.addAttribute("nbrModemRecievedCurrentMonth",
          "Mois courant: " + nbrModemRecievedCurrentMonth);
    }
    List<Map<String, Object>> nbrDemandModem =
        demandeModemRepository.getTodayAndMonthSumOfDemandModemDistPosRev(today, startOfMonth,
            endOfMonth, user.getUserid());
    if (nbrDemandModem.get(0).get("todaySum") != null
        && nbrDemandModem.get(0).get("monthSum") != null) {
      Integer nbrDemandModemToday = ((Number) nbrDemandModem.get(0).get("todaySum")).intValue();
      Integer nbrDemandModemCurrentMonth =
          ((Number) nbrDemandModem.get(0).get("monthSum")).intValue();
      model.addAttribute("nbrDemandModemToday", nbrDemandModemToday);
      model.addAttribute("nbrDemandModemCurrentMonth",
          "Mois courant: " + nbrDemandModemCurrentMonth);
    }
    if ((user.getTypeUser()).equals("DISTRIBUTEUR")) {
      Object abonnementStats = abonnementRepository.getAbonnementSummaryForOthers(startOfMonth,
          endOfMonth, startOfMonth, endOfMonth, user.getUserid(), null);
      Object[] abonnementStatsArray = (Object[]) abonnementStats;
      Long resilierAbonnementThisMonth = (Long) abonnementStatsArray[0];
      Long resilierAbonnementLastMonth = (Long) abonnementStatsArray[1];
      Long nouvelleAbonnementsThisMonth = (Long) abonnementStatsArray[2];
      Long nouvelleAbonnementsLastMonth = (Long) abonnementStatsArray[3];
      String percentageChange =
          this.convertToString(nouvelleAbonnementsThisMonth, nouvelleAbonnementsLastMonth);
      model.addAttribute("percentageChange", percentageChange);
      model.addAttribute("resilierAbonnementThisMonth", resilierAbonnementThisMonth);
      model.addAttribute("resilierAbonnementLastMonth",
          "Mois précédent:  " + resilierAbonnementLastMonth);
      model.addAttribute("nouvelleAbonnementsThisMonth", nouvelleAbonnementsThisMonth);
      model.addAttribute("nouvelleAbonnementsLastMonth",
          "Mois précédent:  " + nouvelleAbonnementsLastMonth);
    } else {
      Object abonnementStats = abonnementRepository.getAbonnementSummaryForOthers(startOfMonth,
          endOfMonth, startOfMonth, endOfMonth, null, user.getUserid());
      Object[] abonnementStatsArray = (Object[]) abonnementStats;
      Long resilierAbonnementThisMonth = (Long) abonnementStatsArray[0];
      Long resilierAbonnementLastMonth = (Long) abonnementStatsArray[1];
      Long nouvelleAbonnementsThisMonth = (Long) abonnementStatsArray[2];
      Long nouvelleAbonnementsLastMonth = (Long) abonnementStatsArray[3];
      String percentageChange =
          this.convertToString(nouvelleAbonnementsThisMonth, nouvelleAbonnementsLastMonth);
      model.addAttribute("percentageChange", percentageChange);
      model.addAttribute("resilierAbonnementThisMonth", resilierAbonnementThisMonth);
      model.addAttribute("resilierAbonnementLastMonth",
          "Mois précédent:  " + resilierAbonnementLastMonth);
      model.addAttribute("nouvelleAbonnementsThisMonth", nouvelleAbonnementsThisMonth);
      model.addAttribute("nouvelleAbonnementsLastMonth",
          "Mois précédent:  " + nouvelleAbonnementsLastMonth);
    }
    return model;
  }

  public Model getCommissionRevendeur(Model model, User user, Integer yearInteger,
      Date startOfLastMonth, Date endOfLastMonth) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startOfLastMonth);
    int lastYear = calendar.get(Calendar.YEAR);
    int lastMonth = calendar.get(Calendar.MONTH) + 1;
    List<Commission> coms = commissionRepository.findLast5comissionForRev(user.getUserid());
    List<TopRevendeurCommissionDash> topRevendeurs =
        coms.stream().map(TopRevendeurCommissionDash::fromEntity).collect(Collectors.toList());
    model.addAttribute("topRevendeurs", topRevendeurs);
    Map<String, Object> calculateCommissionSums = commissionRepository
        .calculateCommissionSums(yearInteger, lastMonth, lastYear, user.getUserid());
    model.addAttribute("totalPaidAllTime",
        String.format("%.3f", (Double) calculateCommissionSums.get("totalPaidAllTime")) + " TND");
    model.addAttribute("totalPaidCurrentYear",
        String.format("%.3f", (Double) calculateCommissionSums.get("totalPaidCurrentYear"))
            + " TND");
    model.addAttribute("totalPaidLastMonth",
        String.format("%.3f", (Double) calculateCommissionSums.get("totalPaidLastMonth")) + " TND");
    model.addAttribute("totalNotPaidCurrentYear",
        String.format("%.3f", (Double) calculateCommissionSums.get("totalNotPaidCurrentYear"))
            + " TND");
    model.addAttribute("totalNotPaidLastMonth",
        String.format("%.3f", (Double) calculateCommissionSums.get("totalNotPaidLastMonth"))
            + " TND");
    model.addAttribute("totalNotPaidAllTime",
        String.format("%.3f", (Double) calculateCommissionSums.get("totalNotPaidAllTime"))
            + " TND");
    model.addAttribute("totalCancelledAllTime",
        String.format("%.3f", (Double) calculateCommissionSums.get("totalCancelledAllTime"))
            + " TND");
    model.addAttribute("totalCancelledCurrentYear",
        String.format("%.3f", (Double) calculateCommissionSums.get("totalCancelledCurrentYear"))
            + " TND");
    model.addAttribute("totalCancelledLastMonth",
        String.format("%.3f", (Double) calculateCommissionSums.get("totalCancelledLastMonth"))
            + " TND");
    return model;
  }

  public Model getLimitIntervalForRevDash(Model model, User user) {
    if ((user.getTypeUser()).equals("REVENDEUR")) {
      Date CURRENT_DATE = new Date();
      RecapFactureLimitDTO res = encaissementRepository.getRevendeurWithSummaryForRevAlerte(
          user.getUserid(), CURRENT_DATE, null, null, null, null);
      model.addAttribute("ContHorsEcheance", res.getQuantitehorsecheance());
      model.addAttribute("ContLimit", res.getQuantiteonecheance());
      model.addAttribute("montantAverser", res.getMontanttotal());
      
      Double MontantVersee = 0.0 ;
      if (user.getPlafonRevendeur() >= 500 && user.getPlafonRevendeur() < 1000) {
    	  MontantVersee = user.getPlafonRevendeur() * 0.8;
        } else if (user.getPlafonRevendeur()  >= 1000) {
        	MontantVersee = user.getPlafonRevendeur() * 0.7;
        } else {
        	MontantVersee = user.getPlafonRevendeur() * 0.8; // Aucun versement requis si plafond < 500
        }
     Double montantNonVerseFacture =  encaissementRepository.montantNonVErseFactureeRevendeurtById(user.getUserid());

     if(montantNonVerseFacture !=null) {
         
         Double montantNonVerseAvoir=  encaissementRepository.montantNonVErseAvoirRevendeurtById(user.getUserid());
if(montantNonVerseAvoir != null ) {
	montantNonVerseFacture = montantNonVerseFacture -montantNonVerseAvoir ; 
}
    	   double montantX = CrmUtils.calculerMontantMinimum(user.getPlafonRevendeur(), montantNonVerseFacture);
    	      model.addAttribute("montantAverserAutorisee",Math.round(montantX * 1000.0) / 1000.0 );
    	      model.addAttribute("roleRevendeur", user.getRole().getRoleName());

     }
   
    }
    return model;

  }

}
