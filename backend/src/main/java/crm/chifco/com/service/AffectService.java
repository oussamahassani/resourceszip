/*
 * created by hatem ghozzi on 18 10 2022
 */
package crm.chifco.com.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.crmMobile.ModemVerificationResult;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.User;

public interface AffectService {

  Boolean affecter(List<Modem> modems, User user, String role_name, Long idUserConnected);

  void confirmerAffectation(Model model, String roleName, String usermail, String modele,
      Integer quantite);

  String verificationAffectModem(List<Long> modemIds, String codeUser, Model model,
      HttpServletRequest request, List<String> roles, Long idUserConnected);

  String confirmAffectModem(Model model, RedirectAttributes redirectAttrs,
      HttpServletRequest request, Long idUserConnected);

  List<Modem> getModemsForVerification(List<String> roles, Long idUserConnected,
      List<Long> modemIds);

  String desaffecterMdeom(Long revendeurId, Long distId, Long posId, Long modemId, User user);

  public ModemVerificationResult verificationAffectModemRest(List<Long> modemIds, String codeUser,
      List<String> roles, Long idUserConnected);

  String confirmAffectModemRest(List<Modem> modemList, User userConnected, String selectedCodeUser);
}
