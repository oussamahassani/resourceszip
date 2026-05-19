package crm.chifco.com.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import crm.chifco.com.ApiDTO.PaymentDTOApi;
import crm.chifco.com.DTOclass.FactureDataDTO;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Payement;
import crm.chifco.com.model.RecuNumeroSequence;
import crm.chifco.com.model.Role;
import crm.chifco.com.model.Smstemplate;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.User;
import crm.chifco.com.model.jasper.PaymentDataSet;
import crm.chifco.com.radius.model.Radcheck;
import crm.chifco.com.radius.service.RadcheckService;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.PayementRepository;
import crm.chifco.com.repository.RecuNumeroSequenceRepository;
import crm.chifco.com.repository.SmstemplateRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.DemandeAbonnementService;
import crm.chifco.com.service.GenerateOtherReferenceAvoir;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.PayementsService;
import crm.chifco.com.service.RecuNumeroSequenceService;
import crm.chifco.com.service.RoleService;
import crm.chifco.com.service.StatutService;
import crm.chifco.com.service.UserHistoryService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.templateclasse.RevendeurRecap;
import crm.chifco.com.utils.ClassificationRevendeur;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.NomStatutChifco;
import crm.chifco.com.utils.PrefixDocument;
import crm.chifco.com.utils.RedchekConstant;
import crm.chifco.com.utils.TypeAbonnment;
import crm.chifco.com.utils.UserTypeConstant;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional
public class PayementServiceImpl implements PayementsService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  Notification notificationservice;
  @Autowired
  private PayementRepository payementtRepo;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private FactureRepository factureRepository;

  @Autowired
  AvoirRepository avoirRepository;

  @Value("${pathRecu}")
  private String pathRecu;
  
  @Value("${access.mail.modem.XDSL.nety}")
  private String admin ;
  

  @Autowired
  PayementProcess payementProcess;

  public List<Payement> createNewPaymentMultiple(List<String> factureids, List<String> avoirsIds,
      User user, String methodepayment, String Bankname, String NumeroCarte, String Numcheque,
      String transactionId, Boolean isChifcoPayed, HttpServletRequest request)
      throws ServletException, IOException {
    try {
      List<Long> sortedFactureIds = parseAndSortFactureIds(factureids);
      List<Facture> factures = factureRepository.findfactureByListFactureids(sortedFactureIds);
      List<Payement> payments = new ArrayList<>();
      List<String> factureReferences = new ArrayList<>();

      // Process invoice payments
      PayementProcess.PaymentProcessingParams params = new PayementProcess.PaymentProcessingParams(
          user, methodepayment, Bankname, NumeroCarte, Numcheque, transactionId, isChifcoPayed);
      
      payementProcess.processFacturePayments(factures, payments, factureReferences, params);

      // Process avoir payments
      List<Long> avoirIds = parseAvoirIds(avoirsIds);
      payementProcess.processAvoirPayments(avoirIds, payments, params);

      // Return payments if any were created (critical operation completed)
      if (payments.isEmpty()) {
        LOGGER.warn("No payments were created");
        notificationservice.sendSimpleMail(admin, 
                "No payments were created: "  + user.getCodeUser() + "fact "+factureids.toString() , "paiement is empty ");
        return payments;
      }

      // Common post-payment operations (non-blocking)
      try {
        payementProcess.checkStatusAndReactivate(payments.get(0).getFacture().getAbonnement());
      } catch (Exception e) {
        LOGGER.error("Error checking and reactivating subscription: {}", e.getMessage(), e);
        notificationservice.sendSimpleMail(admin, 
                "No payments were created: "  + user.getCodeUser() + "fact "+factureids.toString() , "Error checking and reactivating subscription ");
      }

      try {
        payementProcess.creationRecuPayement(payments, user);
      } catch (Exception e) {
        LOGGER.error("Error creating payment receipt: {}", e.getMessage(), e);
        notificationservice.sendSimpleMail(admin,  "Error creating payment receipt: "  + user.getCodeUser() + "fact "+factureids.toString() , "Error checking and reactivating subscription ");
      }

      // Non-blocking operations - don't propagate errors
      handleNonBlockingOperations(payments, factureReferences, factures, user, request);

      LOGGER.info("Payment process completed successfully with {} payments", payments.size());
      return payments;
      
    } catch (Exception e) {
      LOGGER.error("Critical error in createNewPaymentMultiple: {} - Stack: {}", 
          e.getMessage(), e);
      notificationservice.sendSimpleMail(admin, 
          "Critical payment error: " + e.getMessage() + user.getCodeUser() + "fact "+factureids.toString() , "Erreur critique paiement");
      throw new RuntimeException("Erreur lors du traitement du paiement: " + e.getMessage(), e);
    }
  }

  /**
   * Handle non-blocking post-payment operations that shouldn't block payment success
   */
  private void handleNonBlockingOperations(List<Payement> payments, 
      List<String> factureReferences, List<Facture> factures, User user, 
      HttpServletRequest request) {
    
    // Send SMS notifications
    try {
      payementProcess.sendPaymentNotifications(payments, factureReferences);
    } catch (Exception e) {
      LOGGER.warn("Warning: SMS notification failed: {}", e.getMessage() + "user :" + user.getCodeUser()  );
      notificationservice.sendSimpleMail(admin, 
              " payment notification failed: " + e.getMessage() + user.getCodeUser() + "payments "+payments.toString() , "Warning: SMS notification failed:");
    }

    // Update radcheck expiration
    try {
      payementProcess.updateRadcheckExpirationData(factures);
    } catch (Exception e) {
      LOGGER.warn("Warning: Radcheck update failed: {}", e.getMessage() + "user :" + user.getCodeUser() );
      notificationservice.sendSimpleMail(admin, 
              "Warning: Radcheck update failed: " + e.getMessage() + user.getCodeUser() + "payments "+payments.toString() , "Warning: Radcheck update failed::");
    }

    // Verify revendeur ceiling
    try {
      payementProcess.handleRevendeurCeilingCheck(user, payments, request);
    } catch (Exception e) {
      LOGGER.warn("Warning: Revendeur ceiling check failed: {}", e.getMessage() + "user :" + user.getCodeUser());
      notificationservice.sendSimpleMail(admin, 
              "Warning: Revendeur ceiling check failed: " + e.getMessage() + user.getCodeUser() + "payments "+payments.toString() , "Warning: Revendeur ceiling check failed");
    }
    
  }

  /**
   * Parses and sorts invoice IDs
   */
  private List<Long> parseAndSortFactureIds(List<String> factureids) {
    List<Long> ids = factureids.stream()
        .map(Long::parseLong)
        .collect(Collectors.toList());
    Collections.sort(ids);
    return ids;
  }

  /**
   * Parses avoir IDs
   */
  private List<Long> parseAvoirIds(List<String> avoirsIds) {
    List<Long> ids = avoirsIds.stream()
        .map(Long::parseLong)
        .collect(Collectors.toList());
    LOGGER.info("avoir ids: {}", ids);
    return ids;
  }


















  @Override
  public File createPDFRecuPaymentA4(String factureliste, Payement Payement)
      throws Exception, JRException {
    File file1 = null;

    Map<String, Object> parametes = new HashMap<>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      // String currentUser = authentication.getName();
      // User user = userRepository.findUsersByEmail(currentUser);

      parametes.put("image1",
          new ClassPathResource("reports/Logo_Plan_de_travail_2.png").getInputStream());
      List<String> factureids = new ArrayList<String>(Arrays.asList(factureliste.split(",")));

      List<Long> factureidsLong = new ArrayList<Long>();
      List<Long> listPayement = payementtRepo
          .factureByCodeRecuId(Payement.getRecuNumeroSequence().getRecuNumeroSequenceId());

      for (Long factureId : listPayement) {
        factureidsLong.add(factureId);
      }

      Double montant = factureRepository.findSumByListFactureIdS(factureids);
      List<Facture> ListFacture = factureRepository.findfactureByListFactureids(factureidsLong);
      List<Long> listeAvoirClientId = payementtRepo.FactureAvoirClientByCodeRecuId(
          Payement.getRecuNumeroSequence().getRecuNumeroSequenceId());

      List<AvoirClient> listeAvoirClient =
          avoirRepository.findAllAvoirbyListeId(listeAvoirClientId);
      // if (ListFacture.size() > 0) {
      Payement oldPayement = Payement;

      if (oldPayement != null) {

        /// payement avec detail
        Collection<Payement> paymentList = new ArrayList<>();
        paymentList.add(oldPayement);

        Collection<Encaissement> EncaissementList = new ArrayList<>();
        Encaissement Encaissement = new Encaissement();
        Encaissement.setMontantFacture(montant);
        EncaissementList.add(Encaissement);

        Collection<User> UserList = new ArrayList<>();
        UserList.add(oldPayement.getUser());

        Collection<PaymentDataSet> PaymentDataSetArrayList = new ArrayList<>();
        PaymentDataSet paymentDataSet = new PaymentDataSet();
        paymentDataSet.setFactures(ListFacture);
        paymentDataSet.setPayements(paymentList);
        paymentDataSet.setEncaissements(EncaissementList);
        paymentDataSet.setUsers(UserList);
        paymentDataSet.setAvoirClient(listeAvoirClient);
        Collection<Abonnement> abonnement = new ArrayList<>();
        if (listeAvoirClient.size() > 0) {
          abonnement.add(listeAvoirClient.get(0).getAbonnement());
        } else if (ListFacture.size() > 0) {
          abonnement.add(ListFacture.get(0).getAbonnement());
        }
        paymentDataSet.setAbonnements(abonnement);
        PaymentDataSetArrayList.add(paymentDataSet);
        ///

        ///

        File file = ResourceUtils.getFile("classpath:reports/ficherecupaiementA4.jrxml");
        JRBeanCollectionDataSource dataSource =
            new JRBeanCollectionDataSource(PaymentDataSetArrayList);
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametes, dataSource);

        File foldertocreate = new File(pathRecu + CrmUtils.getYear() + "/" + CrmUtils.getMonth());
        if (!foldertocreate.exists()) {
          foldertocreate.mkdirs();
          foldertocreate.setWritable(true);
        }

        String fileName = pathRecu + CrmUtils.getYear() + "/" + CrmUtils.getMonth() + "/"
            + PrefixDocument.NOMEFILE_RECU_PAYMENT
            + oldPayement.getRecuNumeroSequence().getCodePayement() + "-"
            + oldPayement.getRecuNumeroSequence().getRecuNumeroSequenceId() + ".pdf";
        JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);

        file1 = ResourceUtils.getFile(fileName);

      }
      // }

    }
    return file1;
  }

  public Payement returnPayementFromList(String factureliste) {
    List<String> factureids = new ArrayList<String>(Arrays.asList(factureliste.split(",")));
    List<Long> factureidsLong = new ArrayList<Long>();
    for (String onefacture : factureids) {
      factureidsLong.add(CrmUtils.convertStringToLong(onefacture));
    }
    List<FactureDataDTO> ListFacture =
        factureRepository.findFactureDataDTOByListFactureIds(factureidsLong);
    Payement oldPayement = null;
    if (ListFacture.size() > 0)
      oldPayement =
          payementtRepo.findPayementByfacture_factureId(ListFacture.get(0).getFactureId());
    return oldPayement;
  }

  @Override
  public Payement returnPayementFromoneAvoir(String AvoirId) {
    // TODO Auto-generated method stub
    return payementtRepo.findPayementByAvoirId(AvoirId);
  }

  @Override
  public List<PaymentDTOApi> findPaymentBydevice(String dateDebut, String dateFin,
      String transactionId, String frournisseur) {
    // TODO Auto-generated method stub
    Date datedebut = null;
    Date datefin = null;
    if (dateDebut != null && !dateDebut.isEmpty()) {
      datedebut = CrmUtils.convertStringToDate(dateDebut);
    }
    if (dateFin != null && !dateFin.isEmpty()) {
      datefin = CrmUtils.convertStringToLocalDateTime(dateFin);
    }
    return payementtRepo.findPayementByFilterApi(datedebut, datefin, transactionId, frournisseur);
  }

@Override
public void creationRecuPayement(List<Payement> listePayement, User user) {
	 payementProcess.creationRecuPayement(listePayement,user);
	
}

}
