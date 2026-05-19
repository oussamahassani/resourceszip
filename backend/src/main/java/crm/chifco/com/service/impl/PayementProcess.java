package crm.chifco.com.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
import crm.chifco.com.service.RecuNumeroSequenceService;
import crm.chifco.com.service.RoleService;
import crm.chifco.com.service.StatutService;
import crm.chifco.com.service.UserHistoryService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.templateclasse.RevendeurRecap;
import crm.chifco.com.utils.ClassificationRevendeur;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.NomStatutChifco;
import crm.chifco.com.utils.RedchekConstant;
import crm.chifco.com.utils.TypeAbonnment;
import crm.chifco.com.utils.UserTypeConstant;

@Service
public class PayementProcess {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  @Autowired
  SmstemplateRepository templatesmsRepository;

  @Autowired
  Notification notificationservice;

  @Autowired
  private PayementRepository payementtRepo;

  @Autowired
  private EncaissementRepository encaisementRepo;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FactureRepository factureRepository;

  @Autowired
  RoleService roleService;

  @Autowired
  AbonnementRepository abonnementRepository;

  @Autowired
  RecuNumeroSequenceService recuNumeroSequenceService;

  @Autowired
  AvoirRepository avoirRepository;

  @Autowired
  UserHistoryService userHistoryService;

  @Autowired
  UserService userService;

  @Value("${access.mail.modem.XDSL.nety}")
  private String admin;

  @Autowired
  RadcheckService radcheckService;

  @Autowired
  private DemandeAbonnementService demandeAbonnementService;

  @Autowired
  StatutService statutService;

  @Autowired
  GenerateOtherReferenceAvoir generateOtherReferenceAvoir;

  @Autowired
  private SessionRegistry sessionRegistry;

  /**
   * Inner class to encapsulate payment processing parameters
   */
  static class PaymentProcessingParams {
    final User user;
    final String methodepayment;
    final String bankname;
    final String numerocarte;
    final String numcheque;
    final String transactionId;
    final Boolean isChifcoPayed;

    PaymentProcessingParams(User user, String methodepayment, String bankname, String numerocarte,
        String numcheque, String transactionId, Boolean isChifcoPayed) {
      this.user = user;
      this.methodepayment = methodepayment;
      this.bankname = bankname;
      this.numerocarte = numerocarte;
      this.numcheque = numcheque;
      this.transactionId = transactionId;
      this.isChifcoPayed = isChifcoPayed;
    }
  }

  /**
   * Process invoice payments - Each invoice is processed in its own transaction
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processFacturePayments(List<Facture> factures, List<Payement> payments,
      List<String> factureReferences, PaymentProcessingParams params) {
    for (Facture facture : factures) {
      try {
        Payement payment = createPaymentForFacture(facture, params);

        if (isRevenueUser(params.user)) {
          Encaissement encaissement = createEncaissementForFacture(facture, payment, params);
          encaisementRepo.save(encaissement);
          LOGGER.info("Encaissement saved - ID: {} by: {} for facture: {}",
              encaissement.getEncaissementId(), params.user.getCodeUser(), facture.getRef_facture());
        }

        updateFactureAfterPayment(facture, params.isChifcoPayed);
        factureRepository.save(facture);
        LOGGER.info("Facture paid: {}", facture.getRef_facture());

        payments.add(payment);
        factureReferences.add(facture.getRef_facture());

        // Handle first invoice special processing
        processFirstFactureSpecialHandling(facture, params.user);

      } catch (Exception e) {
        LOGGER.error("Error processing facture {}: {}", facture.getFactureId(), e.getMessage(), e);
        // Continue processing other factures - don't stop the whole process
        notificationservice.sendSimpleMail(admin, e.getMessage(), "erreur payement :avoir payment batch processing  :  " + facture.getFactureId());

      }
    }
  }

  /**
   * Process avoir (credit note) payments - Each avoir is processed independently
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processAvoirPayments(List<Long> avoirIds, List<Payement> payments,
      PaymentProcessingParams params) {
    if (avoirIds.isEmpty()) {
      return;
    }

    try {
      List<AvoirClient> avoirs = avoirRepository.findAllAvoirbyClientNotPayedInListe(avoirIds);

      if (avoirs != null && !avoirs.isEmpty()) {
        for (AvoirClient avoir : avoirs) {
          try {
            Payement payment = createPaymentForAvoir(avoir, params);
            payementtRepo.save(payment);

            if (isRevenueUser(params.user)) {
              Encaissement encaissement = createEncaissementForAvoir(avoir, payment, params);
              encaisementRepo.save(encaissement);
              LOGGER.info("Encaissement saved for avoir - ID: {} by: {}",
                  encaissement.getEncaissementId(), params.user.getCodeUser());
            }

            markAvoirAsPaid(avoir, params.user);
            avoirRepository.save(avoir);

            payments.add(payment);
            LOGGER.info("Avoir payment processed successfully - ID: {}", avoir.getAvoirId());
          } catch (Exception e) {
            LOGGER.error("Error processing avoir {}: {}", avoir.getAvoirId(), e.getMessage(), e);
            // Continue to next avoir - don't stop the whole process
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error in avoir payment batch processing: {}", e.getMessage(), e);
      notificationservice.sendSimpleMail(admin, e.getMessage(), "erreur payement :avoir payment batch processing  :  ");

    }
  }

  /**
   * Process first facture special handling - non-blocking operation
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processFirstFactureSpecialHandling(Facture facture, User user) {
    try {
      if (!facture.getIsFirstFacture()) {
        if (facture.getAbonnement().getLoginModem() == null
            && facture.getAbonnement().getModem() != null) {
          facture.getAbonnement().setLoginModem(facture.getAbonnement().getModem().getEmail());
          facture.getAbonnement().setPassword(facture.getAbonnement().getModem().getPassword());
          abonnementRepository.save(facture.getAbonnement());
        }
        return;
      }

      Abonnement abonnement = facture.getAbonnement();
      Statut proformaStatus = statutService.findStatutByNomstatut(NomStatutChifco.POROFORMA);

      DemandeAbonnement demandeAbonnement = demandeAbonnementService
          .findDemandeAbonnementByReferencechifco(abonnement.getReferenceClient());

      if (demandeAbonnement != null) {
        demandeAbonnement.setStatut(proformaStatus);
        demandeAbonnementService.saveStatutDemande(demandeAbonnement);
      }
    } catch (Exception e) {
      LOGGER.error("Error in first facture special handling: {}", e.getMessage(), e);
    }
  }

  /**
   * Send payment notifications via SMS - non-blocking operation
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void sendPaymentNotifications(List<Payement> payments, List<String> factureReferences) {
    if (payments.isEmpty()) {
      return;
    }

    try {
      Smstemplate smsTemplate = templatesmsRepository.findSmstemplateByname("paymentClient");
      if (smsTemplate == null) {
        LOGGER.warn("SMS template 'paymentClient' not found");
        return;
      }

      String template = smsTemplate.getTemplate();
      String joinedReferences = String.join(" ", factureReferences);
      String finalMessage = template.replace("{facture}", joinedReferences);

      if (factureReferences.size() > 1) {
        finalMessage = finalMessage.replace("votre facture", "vos factures");
        finalMessage = finalMessage.replace("a été effectué", "ont été effectués");
      }

      Abonnement abonnement = payments.get(0).getFacture().getAbonnement();
      if (abonnement.getTelMobile() != null && !abonnement.getTelMobile().toString().isEmpty()) {
        ArrayList<Map<String, Object>> smsToSend = new ArrayList<>();
        Map<String, Object> smsMessage = new HashMap<>();
        smsMessage.put("number", abonnement.getTelMobile().toString());
        smsMessage.put("message", finalMessage);
        smsToSend.add(smsMessage);

        Boolean resultSendSms = notificationservice.sendsmsnotification(smsToSend);
        LOGGER.info("Payment SMS result: {} for SMS list: {}", resultSendSms, smsToSend);
      }
    } catch (Exception e) {
      LOGGER.warn("Warning: Error sending payment notifications: {}", e.getMessage(), e);
    }
  }

  /**
   * Update Radcheck expiration data for subscriptions - non-blocking operation
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateRadcheckExpirationData(List<Facture> factures) {
    for (Facture facture : factures) {
      if (!facture.getIsFirstFacture()
          && facture.getAbonnement().getLoginModem() != null
          && !facture.getAbonnement().getLoginModem().isEmpty()
          && !facture.getAbonnement().getPack().getCategoriePack()
              .getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {
        try {
          updateRadcheckForModem(facture);
        } catch (Exception e) {
          LOGGER.warn("Warning: Error updating radcheck for modem {}: {}",
              facture.getAbonnement().getLoginModem(), e.getMessage());
          notificationservice.sendSimpleMail(admin, e.getMessage(), "erreur payement :Error  updating radcheck for modem  :  " + facture.getRef_facture());

        }
      }
    }
  }

  /**
   * Handle revendeur ceiling verification - non-blocking operation
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleRevendeurCeilingCheck(User user, List<Payement> payments,
      HttpServletRequest request) {
    if (!isRevenueUser(user)) {
      return;
    }

    try {
      RevendeurRecap revendeurRecap = encaisementRepo.findByusernotpayedgrouby(user.getUserid());
      if (revendeurRecap == null || revendeurRecap.getPlafon_revendeur() == null) {
        return;
      }

      Double difference = calculateRevendeurCeilingDifference(revendeurRecap);
      LOGGER.info("Revendeur {} ceiling difference: {} (ceiling: {})",
          user.getUserid(), difference, user.getPlafonRevendeur());

      if (difference <= 0) {
        suspendRevendeurForExceededCeiling(user, request);
      }
    } catch (Exception e) {
      LOGGER.warn("Warning: Error verifying revendeur ceiling for user {}: {}",
          user.getUserid(), e.getMessage());
      notificationservice.sendSimpleMail(admin, e.getMessage(), "erreur payement :Error verifying revendeur ceiling :  " + user.getCin() + ":" + user.getCodeUser());

    }
  }

  /**
   * Suspends revendeur when ceiling is exceeded - non-blocking operation
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void suspendRevendeurForExceededCeiling(User user, HttpServletRequest request) {
    try {
      LOGGER.info("Suspending revendeur due to exceeded ceiling: {}", user.getUserid());

      Role suspendedRole = roleService.findRoleByRoleName(user.getRole().getRoleName() + "_DESACTIVE");

      if (suspendedRole != null) {
        user.setRole(suspendedRole);
        user.setLocked(!user.isLocked());

        if (user.getClassification() == null
            || !user.getClassification().equals(ClassificationRevendeur.precontentieux)) {
          user.setClassification(ClassificationRevendeur.suspendu);
          user.setDesactivationDate(new Date());
          user.setDateUpdateclassification(new Date());
        }

        userRepository.save(user);

        userHistoryService.addHistoryEntry(user.getUserid(),
            "Utilisateur suspendu en raison du dépassement du plafond",
            userService.findTop1UsersByTypeuser("SYSTEM"));

        // Expire user sessions
        try {
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          if (authentication != null) {
            String currentUser = authentication.getName();
            for (SessionInformation session : sessionRegistry.getAllSessions(currentUser, false)) {
              session.expireNow();
            }
          }
          request.logout();
        } catch (Exception e) {
          LOGGER.warn("Warning: Error expiring user sessions: {}", e.getMessage());
        }
      }
    } catch (Exception e) {
      notificationservice.sendSimpleMail(admin, e.getMessage(), "erreur payement :revendeur suspension process:  " + user.getCin() + ":" + user.getCodeUser());

      LOGGER.warn("Warning: Error in revendeur suspension process: {}", e.getMessage());
    }
  }

  /**
   * Check and reactivate subscription if all invoices are paid
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void checkStatusAndReactivate(Abonnement ab) {
    try {
      if (ab.getStatut().getNomStatut().equals(NomStatutChifco.UNPAID)) {
        Integer numFactureNonPayee =
            factureRepository.getNumFactureNonPayee(ab.getClientid(), new Date());
        if (numFactureNonPayee == 0) {
          Statut statut = statutService.findStatutByNomstatut(NomStatutChifco.ACTIVE);
          ab.setStatut(statut);
          ab.setEnabled(true);
          abonnementRepository.save(ab);
          LOGGER.info("Subscription {} reactivated", ab.getReferenceClient());
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error checking/reactivating subscription: {}", e.getMessage(), e);
      notificationservice.sendSimpleMail(admin, e.getMessage(), "erreur payement :subscription:  " + ab.getCin());

    }
  }

  /**
   * Creates a receipt/recu for payment with receipt number sequence
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void creationRecuPayement(List<Payement> listePayement, User user) {
    try {
      if (listePayement.isEmpty()) {
        LOGGER.warn("No payments to create receipt for");
        return;
      }

      Double montantTotal = calculatePaymentTotal(listePayement);
      LOGGER.info("Total payment amount: {}", montantTotal);

      RecuNumeroSequence recuSequence = new RecuNumeroSequence();
      recuSequence.setCodePayement(recuNumeroSequenceService.generateCode(user));
      recuSequence.setMontantTotal(montantTotal);
      recuSequence.setUser(user);

      recuSequence = recuNumeroSequenceService.save(recuSequence);

      // Associate all payments with the receipt sequence
      for (Payement payment : listePayement) {
        payment.setRecuNumeroSequence(recuSequence);
        payementtRepo.save(payment);
      }

      LOGGER.info("Receipt created successfully with code: {}", recuSequence.getCodePayement());
    } catch (Exception e) {
      LOGGER.error("Error creating payment receipt: {}", e.getMessage(), e);
      try {
        if (!listePayement.isEmpty() && listePayement.get(0).getFacture() != null) {
          String reference = listePayement.get(0).getFacture().getAbonnement().getReferenceClient();
          notificationservice.sendSimpleMail(admin, e.getMessage() + " "+ listePayement.toString() + " payer par  "+ user.getCodeUser(), "Erreur création reçu " + reference);

        }
      } catch (Exception ex) {
        LOGGER.error("Error creating payment receipt error: {}", ex.getMessage());
        notificationservice.sendSimpleMail(admin, e.getMessage() + " "+ listePayement.toString() + " payer par  "+ user.getCodeUser(), "erreur payement :payment receipt " + listePayement.get(0).getFacture().getFactureId());


      }
    }
  }

  /**
   * Creates a payment for a facture
   */
  private Payement createPaymentForFacture(Facture facture, PaymentProcessingParams params) {
    Payement payment = new Payement();
    payment.setMontant(facture.getMontant_payer());
    payment.setUser(params.user);
    payment.setFacture(facture);
    payment.setTypePayment(params.methodepayment);
    payment.setNumeroCheque(params.numcheque);
    payment.setNumeroCarte(params.numerocarte);
    payment.setNomBank(params.bankname);
    payment.setTransactionId(params.transactionId);
    payment.setIschifcoPayed(determineChifcoPaymentStatus(params.user, params.isChifcoPayed));
    payementtRepo.save(payment);
    return payment;
  }

  /**
   * Creates a payment for an avoir
   */
  private Payement createPaymentForAvoir(AvoirClient avoir, PaymentProcessingParams params) {
    Payement payment = new Payement();
    payment.setMontant(avoir.getMontantAvoir());
    payment.setUser(params.user);
    payment.setAvoirClient(avoir);
    payment.setTypePayment(params.methodepayment);
    payment.setNumeroCheque(params.numcheque);
    payment.setNumeroCarte(params.numerocarte);
    payment.setNomBank(params.bankname);
    payment.setTransactionId(params.transactionId);
    payment.setIschifcoPayed(determineChifcoPaymentStatus(params.user, params.isChifcoPayed));
    return payment;
  }

  /**
   * Creates an encaissement for a facture
   */
  private Encaissement createEncaissementForFacture(Facture facture, Payement payment,
      PaymentProcessingParams params) {
    Encaissement encaissement = new Encaissement();
    encaissement.setCommande(facture.getCommande());
    encaissement.setClient(facture.getAbonnement().getClientid());
    encaissement.setDateDebutFacturation(facture.getCreatedDate());
    encaissement.setUser(params.user);
    encaissement.setTypeDePayment(params.methodepayment);
    encaissement.setNumeroCheque(params.numcheque);
    encaissement.setNumeroCarte(params.numerocarte);
    encaissement.setNomBank(params.bankname);
    encaissement.setFacture(facture);
    encaissement.setPayement(payment);
    encaissement.setMontantFacture(facture.getMontant_payer());
    return encaissement;
  }

  /**
   * Creates an encaissement for an avoir
   */
  private Encaissement createEncaissementForAvoir(AvoirClient avoir, Payement payment,
      PaymentProcessingParams params) {
    Encaissement encaissement = new Encaissement();
    encaissement.setCommande(null);
    encaissement.setClient(avoir.getAbonnement().getClientid());
    encaissement.setDateDebutFacturation(avoir.getCreatedDate());
    encaissement.setUser(params.user);
    encaissement.setTypeDePayment(params.methodepayment);
    encaissement.setNumeroCheque(params.numcheque);
    encaissement.setNumeroCarte(params.numerocarte);
    encaissement.setNomBank(params.bankname);
    encaissement.setAvoirClient(avoir);
    encaissement.setPayement(payment);
    encaissement.setMontantFacture(avoir.getMontantAvoir());
    return encaissement;
  }

  /**
   * Updates facture after payment
   */
  private void updateFactureAfterPayment(Facture facture, Boolean isChifcoPayed) {
    facture.setEtat_facture(true);
    facture.setDateDePayement(new Date());
    if (Boolean.TRUE.equals(isChifcoPayed)) {
      facture.setDateDeVersement(new Date());
    }
  }

  /**
   * Marks avoir as paid
   */
  private void markAvoirAsPaid(AvoirClient avoir, User user) {
    avoir.setIsClientPayed(true);
    avoir.setCanRevendeurViewed(true);
    avoir.setUsedBy(user);
    avoir.setDateDePaiement(new Date());
  }

  /**
   * Determines if payment is marked as Chifco paid based on user type
   */
  private Boolean determineChifcoPaymentStatus(User user, Boolean isChifcoPayed) {
    if (isRevenueUser(user)) {
      return false;
    }
    return Boolean.TRUE.equals(isChifcoPayed) ? true : true;
  }

  /**
   * Checks if user is a revenue type (DISTRIBUTEUR, REVENDEUR, POS)
   */
  private boolean isRevenueUser(User user) {
    return user.getTypeUser().equals(UserTypeConstant.DISTRIBUTEUR)
        || user.getTypeUser().equals(UserTypeConstant.REVENDEUR)
        || user.getTypeUser().equals(UserTypeConstant.POS);
  }

  /**
   * Updates radcheck for a specific modem/subscription
   */
  private void updateRadcheckForModem(Facture facture) {
    String loginModem = facture.getAbonnement().getLoginModem();
    String expirationDate = CrmUtils.RadusDateDexpiration(facture.getDateDeFin());

    LOGGER.info("Updating radcheck expiration for: {}", loginModem);

    Radcheck existingRadcheck = radcheckService.getRadchecksByUsernameAndAttribute(
        loginModem, RedchekConstant.Expiration);

    Radcheck passwordRadcheck = radcheckService.getRadchecksByUsernameAndAttribute(
        loginModem, RedchekConstant.CleartextPassword);

    if (existingRadcheck != null) {
      radcheckService.updateDateExpiration(expirationDate, loginModem);
    } else if (passwordRadcheck == null) {
      radcheckService.addNewRow(loginModem, RedchekConstant.CleartextPassword,
          facture.getAbonnement().getPassword());
      radcheckService.addNewRow(loginModem, RedchekConstant.Expiration, expirationDate);
      String categorie = facture.getAbonnement().getPack().getCategoriePack()
          .getCategorieProduitInternetCode();
      radcheckService.AddNewradusergroup(loginModem, categorie);
      LOGGER.info("New radcheck entry added for: {}", loginModem);
    }
  }

  /**
   * Calculates the remaining ceiling for revendeur
   */
  private Double calculateRevendeurCeilingDifference(RevendeurRecap recap) {
    Double unpaidAmount = recap.getMontantnonpayer();
    Double avoirDifference = recap.getTotalAvoir() - recap.getAvoirConsomme();
    return recap.getPlafon_revendeur() - (unpaidAmount - avoirDifference);
  }

  /**
   * Calculates the total payment amount (invoices - credits)
   */
  private Double calculatePaymentTotal(List<Payement> payments) {
    Double total = 0.0;
    for (Payement payment : payments) {
      if (payment.getAvoirClient() == null) {
        total += payment.getMontant();
      } else {
        total -= payment.getMontant();
      }
    }
    return total;
  }
}