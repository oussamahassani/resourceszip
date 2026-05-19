package crm.chifco.com.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import crm.chifco.com.ApiDTO.PaymentDTOApi;
import crm.chifco.com.model.Payement;
import crm.chifco.com.model.User;
import net.sf.jasperreports.engine.JRException;

@Service
@Transactional
public interface PayementsService {

  List<Payement> createNewPaymentMultiple(List<String> factureids, List<String> avoirsIds,
      User user, String methodepayment, String Bankname, String NumeroCarte, String Numcheque,
      String transactionId, Boolean isChifcoPayed, HttpServletRequest request)
      throws ServletException, IOException;

  public File createPDFRecuPaymentA4(String factureliste, Payement Payement)
      throws Exception, JRException;

  public Payement returnPayementFromList(String factureliste);

  public Payement returnPayementFromoneAvoir(String AvoirId);

  void creationRecuPayement(List<Payement> listePayement, User user);

  List<PaymentDTOApi> findPaymentBydevice(String dateDebut, String dateFin, String transactionId,
      String string);
}
