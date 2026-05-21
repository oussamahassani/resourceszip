
package crm.chifco.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import crm.chifco.com.model.DemandeAbonnementHistory;
import crm.chifco.com.service.AbonnementHistoriqueService;
import crm.chifco.com.service.ClientHistoryService;

@Controller
public class DemandeAbonnementHistoriqueController {
  @Autowired
  AbonnementHistoriqueService AbonnementHistoriqueService;

  @Autowired
  ClientHistoryService clientHistoryService;

  @PreAuthorize("hasAuthority('READ_HISTORIQUE_ABONNEMENT')")
  @RequestMapping(value = "/historiqueAbonnement")
  public String historiqueAbonnement(Model model) {
    List<DemandeAbonnementHistory> DemandeAbonnementHistoryList =
        AbonnementHistoriqueService.findDemandeAbonnementHistory();
    model.addAttribute("listehistorique", DemandeAbonnementHistoryList);
    return "historique/historiqueAbonnement";
  }

  /*
   * @PreAuthorize("hasAuthority('READ_HISTORIQUE_CLIENT')")
   * 
   * @RequestMapping(value = "/historiqueClient") public String historiqueClient(Model model) {
   * List<ClientHistory> ClientHistory = clientHistoryService.findClientHistory();
   * model.addAttribute("ClientHistory", ClientHistory); return "historique/historiqueClient"; }
   */
}
