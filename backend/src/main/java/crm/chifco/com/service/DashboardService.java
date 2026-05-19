package crm.chifco.com.service;

import org.springframework.ui.Model;

public interface DashboardService {
  public Model returnDashbordStatsAdmin(Model model);

  public Model returnDashbordStatsRevPosDist(Model model);
}
