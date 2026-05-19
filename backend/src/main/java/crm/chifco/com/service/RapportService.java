package crm.chifco.com.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.VisiteRepository;

@Service
public class RapportService {
  @Autowired
  private DemandeAbonnementRepository demandeRepository;

  @Autowired
  private FactureRepository factureRepository;

  @Autowired
  private VisiteRepository visiteRepository;

  public Map<String, Object> getDashboardData(int annee) {
    Map<String, Object> result = new HashMap<>();

    List<Object[]> globalStats = demandeRepository.getGlobalStatisticsByYearForReport(annee);

    List<Object[]> chefSecteurStats = demandeRepository.getChefSecteurStatisticsByYearReport(annee);

    List<Object[]> facturesStats =
        factureRepository.getFacturesPayeesApres3JoursByYearReport(annee);
    List<Object[]> visitesStats = visiteRepository.getVisitesByYear(annee);

    List<Map<String, Object>> chefsFormatted = transformChefSecteurData(chefSecteurStats);

    result.put("statistiques_globales", globalStats);
    result.put("statistiques_par_chef", chefsFormatted);
    result.put("factures_payees_apres_3jrs", facturesStats);
    result.put("visites", visitesStats);
    result.put("annee", annee);

    return result;
  }

  public Map<String, Object> getDashboardDataByMonth(int year, int month) {
    Map<String, Object> result = new HashMap<>();

    List<Object[]> globalStats = demandeRepository.getGlobalStatisticsByMonthAndYear(year, month);

    List<Object[]> chefSecteurStats =
        demandeRepository.getChefSecteurStatisticsByMonthAndYear(year, month);

    List<Object[]> facturesStats =
        factureRepository.getFacturesPayeesApres3JoursByMonthAndYear(year, month);

    List<Object[]> visitesStats = visiteRepository.getVisitesByMonthAndYear(year, month);

    result.put("statistiques_globales", globalStats);
    result.put("statistiques_par_chef", chefSecteurStats);
    result.put("factures_payees_apres_3jrs", facturesStats);
    result.put("visites", visitesStats);
    result.put("annee", year);
    result.put("mois", month);

    return result;
  }

  private List<Map<String, Object>> transformChefSecteurData(List<Object[]> chefSecteurStats) {
    Map<Long, Map<String, Object>> chefMap = new LinkedHashMap<>();

    if (chefSecteurStats != null) {
      for (Object[] row : chefSecteurStats) {
        if (row.length >= 7) {
          Integer mois = safeGetInteger(row, 0);
          String chefNom = safeGetString(row, 2);
          String chefCode = safeGetString(row, 3);
          Long chefId = safeGetLong(row, 4);
          Double nouvellesDemandes = safeGetDouble(row, 5);
          Double misesEnService = safeGetDouble(row, 6);

          if (chefId != null && mois != null) {
            Map<String, Object> chefData = chefMap.computeIfAbsent(chefId, k -> {
              Map<String, Object> data = new LinkedHashMap<>();
              data.put("chefId", chefId);
              data.put("chefNom", chefNom);
              data.put("chefCode", chefCode);

              double[] nouvellesDemandesArray = new double[12];
              double[] misesEnServiceArray = new double[12];

              data.put("nouvellesDemandesParMois", nouvellesDemandesArray);
              data.put("misesEnServiceParMois", misesEnServiceArray);

              return data;
            });

            double[] nouvellesDemandesArray = (double[]) chefData.get("nouvellesDemandesParMois");
            double[] misesEnServiceArray = (double[]) chefData.get("misesEnServiceParMois");

            nouvellesDemandesArray[mois - 1] = nouvellesDemandes;
            misesEnServiceArray[mois - 1] = misesEnService;
          }
        }
      }
    }

    return new ArrayList<>(chefMap.values());
  }

  private Double safeGetDouble(Object[] array, int index) {
    if (array != null && index < array.length && array[index] instanceof Number) {
      return ((Number) array[index]).doubleValue();
    }
    return 0.0;
  }

  private Integer safeGetInteger(Object[] array, int index) {
    if (array != null && index < array.length && array[index] instanceof Number) {
      return ((Number) array[index]).intValue();
    }
    return null;
  }

  private Long safeGetLong(Object[] array, int index) {
    if (array != null && index < array.length && array[index] instanceof Number) {
      return ((Number) array[index]).longValue();
    }
    return null;
  }

  private String safeGetString(Object[] array, int index) {
    if (array != null && index < array.length && array[index] != null) {
      return array[index].toString();
    }
    return "";
  }
}
