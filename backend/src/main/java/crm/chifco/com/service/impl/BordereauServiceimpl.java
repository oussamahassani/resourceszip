/*
 * created by hatem ghozzi on 19 10 2022
 */

package crm.chifco.com.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.lowagie.text.DocumentException;
import crm.chifco.com.model.AvanceCommissionAcquisition;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.Bordereau;
import crm.chifco.com.model.CommisionAvanceAcquisitionFacture;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.EntryBordereau;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Payement;
import crm.chifco.com.model.Role;
import crm.chifco.com.model.User;
import crm.chifco.com.model.jasper.BordereauDataSet;
import crm.chifco.com.repository.AvanceCommissionAcquisitionRepository;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.CommisionAvanceAcquisitionFactureRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.BordereauService;
import crm.chifco.com.service.EncaissementService;
import crm.chifco.com.service.RoleService;
import crm.chifco.com.service.UserHistoryService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.templateclasse.ListeBordereau;
import crm.chifco.com.utils.ClassificationRevendeur;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.PrefixDocument;
import crm.chifco.com.utils.StatutAvanceBordereau;
import crm.chifco.com.utils.StatutBordereau;
import crm.chifco.com.utils.typePayementBordereau;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service("BordereauService")
public class BordereauServiceimpl implements BordereauService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Value("${pathBordereau}")
  private String pathbordaurau;

  @Autowired
  private crm.chifco.com.repository.EncaissementRepository EncaissementRepository;

  @Autowired
  private crm.chifco.com.repository.BordereaurRepository BordereaurRepository;

  @Autowired
  private crm.chifco.com.repository.PayementRepository PayementRepository;

  @Autowired
  EncaissementService encaissementService;
  @Autowired
  private crm.chifco.com.repository.EntryBordereauRepository EntryBordereauRepository;
  @Autowired
  UserService userservice;

  @Autowired
  AvoirRepository avoirRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleService roleService;

  @Autowired
  FactureRepository factureRepository;

  @Autowired
  UserHistoryService userHistoryService;

  @Autowired
  AvanceCommissionAcquisitionRepository avanceCommissionAcquisitionRepository;

  @Autowired
  CommisionAvanceAcquisitionFactureRepository commisionAvanceAcquisitionFactureRepository;


  @Value("${commision.acquisition.avance.echance}")
  String commisionAvanceAcquisitionEchance;

  @Value("${commision.acquisition.avance.hors.echance}")
  String commisionAvanceAcquisitionHoreEchance;


  public Long createnewBordereau(List<String> Encaismentliste, User user, Double montant)
      throws IOException, DocumentException {
    List<Facture> Facturelist = new ArrayList<Facture>();
    List<EntryBordereau> EntryBordereau = new ArrayList<EntryBordereau>();

    LOGGER.info(
        "Méthode createnewBordereau appelée avec les paramètres : Encaismentliste={}, user={}, montant={}",
        Encaismentliste, user, montant);
    AtomicReference<Double> commisionavance =
        new AtomicReference<>(Double.parseDouble(commisionAvanceAcquisitionHoreEchance));

    List<EntryBordereau> entryBd =
        EntryBordereauRepository.findByEncaissement_Facture_encaissementId(Encaismentliste);

    if (entryBd.size() == 0) {
      Bordereau newBordereau = new Bordereau();
      newBordereau.setmontant(montant);
      newBordereau.setnumfacure((long) Encaismentliste.size());
      newBordereau.setUser(user);
      updateStatus(newBordereau, StatutBordereau.ATTENTE_JUSTIFICATION);

      AtomicReference<Double> montantCommision = new AtomicReference<>(0.0);
      AtomicReference<Integer> nbrFirstFacture = new AtomicReference<>(0);
      List<CommisionAvanceAcquisitionFacture> ListecommisionAvanceAcquisitionFacture =
          new ArrayList<CommisionAvanceAcquisitionFacture>();
      Encaismentliste.forEach(el -> {
        Encaissement encaisment = EncaissementRepository.findByencaissementId(Long.parseLong(el));

        EntryBordereau newentryBordereau = new EntryBordereau();
        newentryBordereau.setEncaissement(encaisment);
        newentryBordereau.setCreatedDate(newBordereau.getCreatedDate());
        if (encaisment.getFacture() != null && encaisment.getFacture().getIsFirstFacture()
            && encaisment.getFacture().getAbonnement().getUser().getUserid()
                .equals(user.getUserid())) {
          newentryBordereau.setEligibleCommision(true);

        }
        EntryBordereau.add(newentryBordereau);
        EntryBordereauRepository.save(newentryBordereau);

        encaisment.setHasBordereau(true);
        encaisment.setIdbordaureau(newBordereau);
        EncaissementRepository.save(encaisment);
        Facturelist.add(encaisment.getFacture());

        // Modifier avoir pour dire que cette avoir a été utilisée dans bordureau
        if (encaisment.getAvoirClient() != null) {
          AvoirClient avoir = encaisment.getAvoirClient();
          avoir.setHas_bordereau(true);
          avoirRepository.save(avoir);
        }


        if (encaisment.getFacture() != null) {
          if (encaisment.getFacture().getIsFirstFacture() && encaisment.getFacture().getAbonnement()
              .getUser().getUserid().equals(user.getUserid())) {
            /*
             * long differenceInMilliseconds = Math .abs(new Date().getTime() -
             * encaisment.getFacture().getDateDePayement().getTime()); long differenceInDays =
             * TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);
             * 
             * if (differenceInDays <= 15) {
             * commisionavance.set(Double.parseDouble(commisionAvanceAcquisitionEchance)); }
             */
            montantCommision.updateAndGet(value -> value + commisionavance.get());
            nbrFirstFacture.updateAndGet(value -> value + 1);
            CommisionAvanceAcquisitionFacture facturecommision =
                new CommisionAvanceAcquisitionFacture();

            facturecommision.setReferenceFacture(encaisment.getFacture().getRef_facture());
            commisionAvanceAcquisitionFactureRepository.save(facturecommision);
            ListecommisionAvanceAcquisitionFacture.add(facturecommision);
          }
        }
      });
      newBordereau.setEntry(EntryBordereau);
      newBordereau.setReferenceBordereau(generateBordereauSequence(user.getCodeUser()));
      LOGGER.info("Création d'un nouveau bordereau pour l'utilisateur : {}", user.getCodeUser());
      Long id = BordereaurRepository.save(newBordereau).getBordereauId();

      if (montantCommision.get() > 0.0) {
        if (!ListecommisionAvanceAcquisitionFacture.isEmpty()) {
          System.out.println("La liste  est  non vide");

          AvanceCommissionAcquisition avanceBordereau = new AvanceCommissionAcquisition();
          avanceBordereau.setBordereau(id);
          avanceBordereau.setRevendeur(user);
          avanceBordereau.setMontantCommissionPremiereFacture(montantCommision.get());
          avanceBordereau.setNbrPermiereFacture(nbrFirstFacture.get());
          avanceCommissionAcquisitionRepository.save(avanceBordereau);
          for (CommisionAvanceAcquisitionFacture element : ListecommisionAvanceAcquisitionFacture) {
            element.setReferenceDemandeDeCommision(avanceBordereau.getReferenceCommisionAvance());
            commisionAvanceAcquisitionFactureRepository.save(element);
          }
        }
      }

      return id;
    } else {
      List<Long> idsEntryBd =
          entryBd.stream().map(u -> u.getEntryId()).collect(Collectors.toList());

      LOGGER.info("Liste des encaissements trouvés : {}", idsEntryBd);
      return null;
    }

  }

  private void updateStatus(Bordereau newBordereau, String Statut) {
    newBordereau.setstatus(Statut);
    BordereaurRepository.save(newBordereau);
  }

  public String parseThymeleafTemplate(Double Montant, User revendeur, List<Facture> facturelist) {
    StringBuilder html = new StringBuilder();
    html.append("<html><body>");
    html.append("<h2>Bordereau de paiement</h2>");
    html.append("<p><strong>Revendeur :</strong> ").append(revendeur.getFirstName()).append("</p>");
    html.append("<p><strong>Adresse :</strong> ").append(revendeur.getAdresse()).append("</p>");
    html.append("<p><strong>Code postal :</strong> ").append(revendeur.getCodePostale()).append("</p>");
    html.append("<p><strong>Total payé :</strong> ").append(Montant).append("</p>");
    html.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse;width:100%;'>");
    html.append("<thead><tr><th>Référence facture</th><th>Date échéance</th><th>Montant</th></tr></thead>");
    html.append("<tbody>");

    for (Facture facture : facturelist) {
      html.append("<tr>");
      html.append("<td>").append(facture.getRef_facture()).append("</td>");
      html.append("<td>").append(facture.getDate_echeance()).append("</td>");
      html.append("<td>").append(facture.getMontant_payer()).append("</td>");
      html.append("</tr>");
    }

    html.append("</tbody>");
    html.append("</table>");
    html.append("</body></html>");
    return html.toString();
  }

  public Double calculepaymentmultiple(List<String> factureids) {
	   Double montantbordauraux = 0.0;


    List<Long> ids = factureids.stream().map(Long::parseLong).collect(Collectors.toList());

    List<Encaissement> encaissements = EncaissementRepository.findByencaissementByListIds(ids);

    for(Encaissement el : encaissements){
   	 if (el.getFacture() != null) {
   	        montantbordauraux += el.getMontantFacture();
   	      } else {
   	        montantbordauraux -= el.getMontantFacture();
   	      }
   }


    return montantbordauraux;
  }

  public Page<ListeBordereau> findPaginatedbordereauxadmin(int pageNo, int pageSize, Long ville,
      Long governorate) {
    Sort sort = Sort.by("modified_date");
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return BordereaurRepository.findBordereaubyadmin(pageable, StatutBordereau.VERSEMENT_INSTENCE,
        ville, governorate);
  }

  @Override
  public Page<ListeBordereau> findPaginatedbordereauxRevendeur(int pageNo, int pageSize, User user,
      String statut, Long ville, Long governorate) {
    Sort sort = Sort.by("modified_date");
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    if (statut == null && ville == null && governorate == null)
      return this.BordereaurRepository.findByuser(pageable, user.getUserid().toString());
    else if (statut != null && ville == null && governorate == null)
      return this.BordereaurRepository.findByuserAndStatus(pageable, user.getUserid().toString(),
          statut);

    else
      return this.BordereaurRepository.findPaginatedbordereauxRevendeurbyDataUser(pageable,
          user.getUserid(), statut, governorate, ville);
  }

  @Override
  public File createPDFBordereauA4(Long id) throws JRException, IOException, DocumentException {
    File file1 = null;
    String nomfile = "";
    Map<String, Object> parametes = new HashMap<>();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      Optional<Bordereau> oldBordereauoptn = BordereaurRepository.findById(id);
      if (oldBordereauoptn.isPresent()) {
        Bordereau oldBordereau = oldBordereauoptn.get();

        /// bordereau avec detail facture
        Collection<BordereauDataSet> bordereauDataSetArrayList = new ArrayList<>();
        BordereauDataSet bordereauDataSet = new BordereauDataSet();
        bordereauDataSet.setBordereaus(oldBordereau);
        bordereauDataSetArrayList.add(bordereauDataSet);
        ///

        File file = ResourceUtils.getFile("classpath:reports/BordereauDeVersement.jrxml");
        JRBeanCollectionDataSource dataSource =
            new JRBeanCollectionDataSource(bordereauDataSetArrayList);
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametes, dataSource);

        ////////////////////////////
        /*
         * String html = this.parseThymeleafTemplate(montant, user, listefactures); String path =
         * this.generatePdfFromHtml(html);// methode convertit une page html en pdf
         */
        ////////////////////////////

        String folder = pathbordaurau + oldBordereau.getReferenceBordereau() + "/";
        File uploadDir = new File(folder);
        if (!uploadDir.exists()) {
          uploadDir.mkdirs();
          uploadDir.setWritable(true);
        }

        nomfile = folder + PrefixDocument.NOMEFILE_BORDEREAU + oldBordereau.getReferenceBordereau()
            + ".pdf";

        JasperExportManager.exportReportToPdfFile(jasperPrint, nomfile);
        file1 = ResourceUtils.getFile(nomfile);

      }

    }
    return file1;
  }

  @Override
  public Bordereau findBordereauxById(Long id) {
    return this.BordereaurRepository.findBybordereauId(id);
  }

  public void addJustificatifBordereaux(MultipartFile imageFile, Long idbordaurau) {
    Bordereau Bordereau = findBordereauxById(idbordaurau);
    try {
      CrmUtils.saveImage(imageFile, idbordaurau.toString(), pathbordaurau, "");
      Bordereau.setDateUplodeJustificatif(new Date());
      Bordereau.setDateVersement(new Date());
      Bordereau.setPhotoRecu(CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
      updateStatus(Bordereau, StatutBordereau.VERSEMENT_INSTENCE);
    } catch (Exception e) {
      // TODO Auto-generated catch block

      LOGGER.error("addJustificatifBordereaux" + e.getMessage());
    }
  }

  @Override
  public void accpetBordereauByAdmin(Bordereau bordereau, User checkby, String commentaire,
      String dateVersement, String typedePayement, RedirectAttributes redirectAttrs) {
    try {

      List<Encaissement> encaismentliste =
          EncaissementRepository.findByidbordaureau_bordereauId(bordereau.getBordereauId());
      double totalcommisionNotPayed = 0.0;
      double totalBrd = 0.0;
      encaismentliste.forEach(el -> {
        el.setIsChifcoPayed(true);
        EncaissementRepository.save(el);
        Payement Payement = el.getPayement(); // PayementRepository.findPayementByfacture(el.getPayement().getFacture());
        Payement.setIschifcoPayed(true);
        PayementRepository.save(Payement);
        if (el.getFacture() != null) {
          if (dateVersement != null && !dateVersement.isEmpty()) {
            Date dateVersementReele = CrmUtils.convertStringToDate(dateVersement);
            el.getFacture().setDateDeVersement(dateVersementReele);
          } else {
            el.getFacture().setDateDeVersement(new Date());
          }
          factureRepository.save(el.getFacture());
        }
      });
      Date dateVersementReelle = null;
      if (dateVersement != null && !dateVersement.isEmpty()) {
        dateVersementReelle = CrmUtils.convertStringToDate(dateVersement);
      }
      try {

        totalBrd = encaismentliste.stream()

            .mapToDouble(encaisment -> encaisment.getMontantFacture()).sum();
        List<AvanceCommissionAcquisition> listCommisionEnCours =
            avanceCommissionAcquisitionRepository.findByStatutAndRevendeur_useridAndIdBrdDemande(
                StatutAvanceBordereau.AVANCE_INSTENCE, bordereau.getUser().getUserid(),
                bordereau.getBordereauId());

        if (listCommisionEnCours != null && listCommisionEnCours.size() > 0) {
          totalcommisionNotPayed = listCommisionEnCours.stream()
              .mapToDouble(commision -> commision.getMontantCommissionPremiereFacture()).sum();
          int totalcommisionNotPayedFacture = listCommisionEnCours.stream()
              .mapToInt(commision -> commision.getNbrPermiereFacture()).sum();
          if (totalBrd - totalcommisionNotPayed > 0) {
            final Date dateVersementReelle1 = dateVersementReelle;
            listCommisionEnCours.forEach(el -> {
              el.setStatut(StatutAvanceBordereau.AVANCE_TREAT);
              el.setBrdValidated(true);
              el.setDateVersementBrd(dateVersementReelle1);


              avanceCommissionAcquisitionRepository.save(el);
            });
            AvanceCommissionAcquisition avanceBordereau = new AvanceCommissionAcquisition();
            avanceBordereau.setBordereau(bordereau.getBordereauId());
            avanceBordereau.setRevendeur(bordereau.getUser());
            avanceBordereau.setMontantCommissionPremiereFacture(totalcommisionNotPayed);
            avanceBordereau.setNbrPermiereFacture(totalcommisionNotPayedFacture);
            avanceBordereau.setStatut(StatutAvanceBordereau.AVANCE_PAYED);
            avanceBordereau.setBrdValidated(true);
            avanceBordereau.setDateVersementBrd(dateVersementReelle);


            avanceCommissionAcquisitionRepository.save(avanceBordereau);
          } else {
            AvanceCommissionAcquisition avanceBordereau = new AvanceCommissionAcquisition();
            avanceBordereau.setBordereau(bordereau.getBordereauId());
            avanceBordereau.setRevendeur(bordereau.getUser());
            avanceBordereau.setMontantCommissionPremiereFacture(totalBrd);
            avanceBordereau.setNbrPermiereFacture(totalcommisionNotPayedFacture);
            avanceBordereau.setStatut(StatutAvanceBordereau.AVANCE_PAYED);
            avanceBordereau.setBrdValidated(true);
            avanceBordereau.setDateVersementBrd(dateVersementReelle);
            avanceCommissionAcquisitionRepository.save(avanceBordereau);
            final Date dateVersementReelle1 = dateVersementReelle;

            listCommisionEnCours.forEach(el -> {
              el.setStatut(StatutAvanceBordereau.AVANCE_TREAT);
              el.setBrdValidated(true);
              el.setDateVersementBrd(dateVersementReelle1);


              avanceCommissionAcquisitionRepository.save(el);
            });
            double difference = totalcommisionNotPayed - totalBrd;

            AvanceCommissionAcquisition avanceBordereauRest = new AvanceCommissionAcquisition();
            avanceBordereauRest.setBordereau(bordereau.getBordereauId());
            avanceBordereauRest.setRevendeur(bordereau.getUser());
            avanceBordereauRest.setMontantCommissionPremiereFacture(difference);
            avanceBordereauRest.setNbrPermiereFacture(totalcommisionNotPayedFacture);
            avanceBordereauRest.setStatut(StatutAvanceBordereau.CEREATE_AVANCE);


            avanceCommissionAcquisitionRepository.save(avanceBordereauRest);

          }
        }
      } catch (Exception e) {
        LOGGER.error("accpetBordereauByAdmin" + e);
      }



      if (encaismentliste.size() > 0) {
        Encaissement firstEncaisment = encaismentliste.get(0);
        if (firstEncaisment.getUser().getRole().getRoleName().contains("DESACTIVE")) {
          Double montantNonEncorePayer = EncaissementRepository
              .sumFactureByUserAndIschifcopayed(firstEncaisment.getUser().getUserid(), false);
          Long montantAvoirNonPayer = EncaissementRepository
              .sumAvoirByUserAndIschifcopayed(firstEncaisment.getUser().getUserid(), false);
          LocalDate currentDate = LocalDate.now();


          LocalDate resultDateFaterRemoveDay = currentDate.minusDays(25);
          Date date =
              Date.from(resultDateFaterRemoveDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
          List<Encaissement> paymentExceeding15Days = EncaissementRepository
              .findEncaismentNotChifcoPayedAndUser(date, firstEncaisment.getUser().getUserid());

          Long plafond = firstEncaisment.getUser().getPlafonRevendeur();
          Double montantAReverser;

          if (plafond >= 500 && plafond < 1000) {
            montantAReverser = plafond * 0.8;
          } else if (plafond >= 1000) {
            montantAReverser = plafond * 0.7;
          } else {
            montantAReverser = plafond * 0.8; // Aucun versement requis si plafond < 500
          }
          LOGGER.error("accpetBordereauByAdmin" + bordereau.getReferenceBordereau());
          LOGGER.error("accpetBordereauByAdmin montantAvoirNonPayer" + montantAvoirNonPayer);

          // Vérification si le montant requis est payé
          boolean aPayeMontantRequis =
              ((montantNonEncorePayer - montantAvoirNonPayer)) <= montantAReverser;
          System.out.println((montantNonEncorePayer - montantAvoirNonPayer));
          System.out.println((totalBrd - totalcommisionNotPayed));
          System.out.println((montantAReverser));
          System.out.print(aPayeMontantRequis);

          if ((aPayeMontantRequis
              && (montantNonEncorePayer - montantAvoirNonPayer) <= firstEncaisment.getUser()
                  .getPlafonRevendeur()
                  && firstEncaisment.getUser().getDesactivatedByCron() == false)
                  || (firstEncaisment.getUser().getDesactivatedByCron() == true
                      && paymentExceeding15Days.size() == 0 && aPayeMontantRequis)) {
            String namerole = firstEncaisment.getUser().getRole().getRoleName().substring(0,
                firstEncaisment.getUser().getRole().getRoleName().length() - 10);

            Role newrole = roleService.findRoleByRoleName(namerole);
            firstEncaisment.getUser().setRole(newrole);
            firstEncaisment.getUser().setLocked(false);
            firstEncaisment.getUser().setDesactivationDate(null);
            if( firstEncaisment.getUser().getClassification() != null && 
            		!firstEncaisment.getUser().getClassification().equals(ClassificationRevendeur.precontentieux)) {
                firstEncaisment.getUser().setClassification(ClassificationRevendeur.Sactiver);
                firstEncaisment.getUser().setDateUpdateclassification(new Date());
            }

            firstEncaisment.getUser().setDesactivatedByCron(false);
            userRepository.save(firstEncaisment.getUser());

            // historique user
            if (firstEncaisment.getUser() != null
                && firstEncaisment.getUser().getUserid() != null) {

              userHistoryService.addHistoryEntry(firstEncaisment.getUser().getUserid(),
                  "Cet utilisateur a été activé en raison d'un dépôt de fonds.", checkby);

            }

          }
        }
      }

      AvanceCommissionAcquisition commisionEnNonPayee = avanceCommissionAcquisitionRepository
          .findAvanceCommissionByBordereauAndStatutAndRevendeur_userid(bordereau.getBordereauId(),
              StatutAvanceBordereau.CEREATE_AVANCE, bordereau.getUser().getUserid());

      if (commisionEnNonPayee != null) {
        commisionEnNonPayee.setBrdValidated(true);
        commisionEnNonPayee.setDateVersementBrd(dateVersementReelle);
        avanceCommissionAcquisitionRepository.save(commisionEnNonPayee);
      }
      updateStatus(bordereau, StatutBordereau.VERSEMENT_CONFIRME);
      bordereau.setCheckBy(checkby);
      bordereau.setCommentaire(commentaire);
      bordereau.setDateVersement(dateVersementReelle);
      bordereau.setDateValidationBrd(new Date());
      if (typedePayement == null) {
        bordereau.setTypeDePayement(typePayementBordereau.PayementEspece);

      } else {
        bordereau.setTypeDePayement(typedePayement);

      }
      BordereaurRepository.save(bordereau);
      if (redirectAttrs != null) {
        redirectAttrs.addFlashAttribute("message", "bordorauxaccepted");

      }
    } catch (Exception e) {
      LOGGER.error("accpetBordereauByAdmin" + e);
    }

  }

  public void annulationtbordorauxbyadmin(Bordereau bordereau, User user, String commentaire) {
    try {
      if (!user.getTypeUser().equals("ADMINISTRATEUR")) {
        bordereau.setstatus(StatutBordereau.ANNULER);

      } else {
        bordereau.setstatus(StatutBordereau.VERSEMENT_ANOMALIE);
        bordereau.setCommentaire(commentaire);
        bordereau.setCheckBy(user);
      }
      BordereaurRepository.save(bordereau);
      List<Encaissement> encaismentliste =
          EncaissementRepository.findByidbordaureau_bordereauId(bordereau.getBordereauId());

      encaismentliste.forEach(el -> {
        el.setHasBordereau(false);
        el.setIsChifcoPayed(false);
        el.setIdbordaureau(null);
        EncaissementRepository.save(el);
        if (el.getAvoirClient() != null) {
          el.getAvoirClient().setHas_bordereau(false);
          avoirRepository.save(el.getAvoirClient());
        }
        Payement Payement = el.getPayement(); // PayementRepository.findPayementByfacture(el.getPayement().getFacture());
        Payement.setIschifcoPayed(false);
        PayementRepository.save(Payement);
      });
      AvanceCommissionAcquisition avanceBrd =
          avanceCommissionAcquisitionRepository.findByBordereauAndStatut(bordereau.getBordereauId(),
              StatutAvanceBordereau.AVANCE_INSTENCE);
      if (avanceBrd != null) {

        avanceCommissionAcquisitionRepository.DeleteAvanceCommision(avanceBrd.getId(),
            StatutAvanceBordereau.AVANCE_INSTENCE);
      }
      List<AvanceCommissionAcquisition> listedecommisionencoursUser =
          avanceCommissionAcquisitionRepository.findByStatutAndRevendeur_useridAndIdBrdDemande(
              StatutAvanceBordereau.AVANCE_INSTENCE, bordereau.getUser().getUserid(),
              bordereau.getBordereauId());
      if (listedecommisionencoursUser.size() > 0) {
        listedecommisionencoursUser.forEach(el -> {
          el.setStatut(StatutAvanceBordereau.CEREATE_AVANCE);
          avanceCommissionAcquisitionRepository.save(el);
        });
      }

    } catch (Exception e) {
      LOGGER.error("annulationtbordorauxbyadmin" + user.getUserid() + e.getMessage());
    }

  }

  @Override
  public Page<ListeBordereau> findBordereaubyDistributeur(int pageNo, int pageSize, String userId,
      String numeroBordereau, String userCode, String affecterTo, Date dateDebut, Date dateFin,
      Date datevalideDebut, Date datevalideFin, String sortvar, String sorttype) {
    Sort sort = Sort.by("modified_date");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return BordereaurRepository.findBordereaubyDistributeur(pageable, userId, numeroBordereau,
        userCode, affecterTo, dateDebut, dateFin, StatutBordereau.VERSEMENT_INSTENCE , datevalideDebut ,datevalideFin);
  }

  @Override

  public Long BordereauCreation(String factureliste) throws IOException, DocumentException {
    Long Bordereauid = 0L;
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = this.findUsersByEmail(currentUser);

        LOGGER.info(
            "Méthode BordereauCreation appelée avec les paramètres : factureliste={}, currentUser={}",
            factureliste, currentUser);

        List<String> payementslists = new ArrayList<String>(Arrays.asList(factureliste.split(",")));

        Double montant = this.calculepaymentmultiple(payementslists);
        Bordereauid = this.createnewBordereau(payementslists, user, montant);

      }

      return Bordereauid;

    } catch (Exception e) {
      LOGGER.error("annulationtbordorauxbyadmin" + e.getMessage());
      return Bordereauid;
    }
  }

  @Override
  public HashMap<String, Object> listepathBordereau(int draw, int start, int length, String search,
      String filterrecherche) {

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();
    int currentpage = start / length;
    String statut = null;
    Long ville = null;
    Long governorate = null;

    String currentUser = this.getCurrentUser();
    User user = this.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    Page<ListeBordereau> responseData = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {

      JSONObject obj = new JSONObject(filterrecherche);
      if (obj.getString("statut") != null && !Objects.equals(obj.getString("statut"), "")) {
        statut = obj.getString("statut").trim();
      }
      if (obj.getString("villes") != null && !Objects.equals(obj.getString("villes"), "")) {
        ville = obj.getLong("villes");
      }
      if (obj.getString("gouvernorats") != null
          && !Objects.equals(obj.getString("gouvernorats"), "")) {
        governorate = obj.getLong("gouvernorats");
      }

    }
    if (StringsRole.contains("READ_SLIP_LIST")) {
      responseData = this.findPaginatedbordereauxadmin(currentpage + 1, length, ville, governorate);
    }
    // ROLE_REVENDEUR
    else if (StringsRole.contains("READ_SLIP_LIST_RETAIL")) {

      responseData = this.findPaginatedbordereauxRevendeur(currentpage + 1, length, user, statut,
          ville, governorate);
    } else if (StringsRole.contains("READ_SLIP_HISTORY_AREA")) {
      responseData = this.findPaginatedbordereauxByDistributeur(currentpage + 1, length, user,
          statut, ville, governorate);
    }

    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    } else {
      myGreetings.put("data", null);
      myGreetings.put("recordsTotal", 0);
      myGreetings.put("recordsFiltered", 0);
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);

    return myGreetings;
  }

  private Page<ListeBordereau> findPaginatedbordereauxByDistributeur(int i, int length, User user,
      String statut, Long ville, Long governorate) {
    // TODO Auto-generated method stub
    Sort sort = Sort.by("modified_date");
    Pageable pageable = PageRequest.of(i - 1, length, sort);
    return BordereaurRepository.findPaginatedbordereauxByDistributeur(pageable, user.getUserid(),
        StatutBordereau.VERSEMENT_INSTENCE, ville, governorate);

  }

  @Override
  public void getBordereau(Long id, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = findUsersByEmail(currentUser);
      Bordereau bordereau = this.findBordereauxById(id);
      List<EntryBordereau> entryBordereau = bordereau.getEntry();
      LocalDate currentDate = LocalDate.now();
      LocalDate startDate = currentDate.withDayOfMonth(1); // Start date of the current month
      LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
      List<AvanceCommissionAcquisition> restTotalCommision = avanceCommissionAcquisitionRepository
          .findByStatutAndRevendeur_useridAndBrdValidatedAndDateVersementBrdBetween(
              StatutAvanceBordereau.CEREATE_AVANCE, user.getUserid(), true,
              Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
              Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
      AvanceCommissionAcquisition avance = avanceCommissionAcquisitionRepository
          .findByBordereauAndStatut(id, StatutAvanceBordereau.CEREATE_AVANCE);
      List<AvanceCommissionAcquisition> avanceEnCours =
          avanceCommissionAcquisitionRepository.findByStatutAndRevendeur_useridAndIdBrdDemande(
              StatutAvanceBordereau.AVANCE_INSTENCE, bordereau.getUser().getUserid(), id);
      AvanceCommissionAcquisition avancePayee = avanceCommissionAcquisitionRepository
          .findByBordereauAndStatut(id, StatutAvanceBordereau.AVANCE_PAYED);
      double totalCommission = restTotalCommision.stream()
          .mapToDouble(commision -> commision.getMontantCommissionPremiereFacture()).sum();
      if (avance != null) {
        totalCommission = totalCommission + avance.getMontantCommissionPremiereFacture();
      }
      if (avanceEnCours.size() > 0) {
        boolean existBrd = true;
        for (AvanceCommissionAcquisition s : avanceEnCours) {
          if (s.getBordereau().equals(id)) {
            existBrd = true;
            break;

          }

        }
        if (existBrd) {
          double totalCommissionEnCours = avanceEnCours.stream()
              .mapToDouble(commision -> commision.getMontantCommissionPremiereFacture()).sum();
          Double resteApayee = bordereau.getMontant() - totalCommissionEnCours;
          if (resteApayee < 0) {
            resteApayee = 0.0;
            totalCommissionEnCours = bordereau.getMontant();
          }


          model.addAttribute("avanceEnCours", true);
          model.addAttribute("resteApayee", CrmUtils.formatDoubleInputToString(resteApayee));
          model.addAttribute("monatantAvanceValide",
              CrmUtils.formatDoubleInputToString(totalCommissionEnCours));


        }
      }

      if (avancePayee != null) {
        Double resteApayee =
            bordereau.getMontant() - avancePayee.getMontantCommissionPremiereFacture();
        if (resteApayee < 0) {
          resteApayee = 0.0;
          resteApayee = bordereau.getMontant();
        }
        model.addAttribute("resteApayee", CrmUtils.formatDoubleInputToString(resteApayee));

        model.addAttribute("avancepayee", avancePayee.getMontantCommissionPremiereFacture());

      }
      model.addAttribute("bordereau", bordereau);
      // model.addAttribute("listeenciasment", listeenciasment);
      model.addAttribute("listeentryBordereau", entryBordereau);

      model.addAttribute("avance", avance);
      model.addAttribute("totalCommission", totalCommission);
      model.addAttribute("idbordereau", id);


      List<AvoirClient> avList =
          avoirRepository.getAvoirsToAddInBordureauByIdUser(bordereau.getUser().getUserid());
      model.addAttribute("avoirList", avList);

      if (bordereau.getPhotoRecu() != null) {
        model.addAttribute("urljustification", "bordereau/" + id + "/" + bordereau.getPhotoRecu());
      }
    }
  }

  @Override
  public void anulationPathBordereaux(Long idbordereau, String commentaire) {

    try {
      String currentUser = getCurrentUser();
      User user = this.findUsersByEmail(currentUser);
      LOGGER.info("idbordereau" + idbordereau);
      Bordereau bordereau = this.findBordereauxById(idbordereau);
      this.annulationtbordorauxbyadmin(bordereau, user, commentaire);
    } catch (Exception e) {
      LOGGER.error("annulationtbordoraux" + e.getMessage());

    }

  }

  private User findUsersByEmail(String email) {
    return userservice.findUsersByEmail(email);
  }

  private String getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName();
  }

  public String generateBordereauSequence(String codeRevendeur) {
    int getSequence = BordereaurRepository.countbyReferenceBordereau(codeRevendeur) + 1;
    return "BV" + getSequence + "-" + codeRevendeur;
  }

  @Override
  public Void deleteAvoirByBordereau(Long bordereauId, Long avoirId) {

    LOGGER.info(
        "Méthode deleteAvoirByBordereau appelée avec les paramètres : bordereauId={}, avoirId={}",
        bordereauId, avoirId);

    Bordereau bordereau = BordereaurRepository.getById(bordereauId);

    Double montantAvoir = 0.0;

    List<EntryBordereau> entryList = bordereau.getEntry();

    List<EntryBordereau> entriesToRemove = new ArrayList<>();
    for (EntryBordereau entryBordereau : entryList) {
      if (entryBordereau.getEncaissement().getAvoirClient() != null
          && entryBordereau.getEncaissement().getAvoirClient().getAvoirId().equals(avoirId)) {
        montantAvoir = entryBordereau.getEncaissement().getAvoirClient().getMontantAvoir();
        entriesToRemove.add(entryBordereau);
        break;
      }
    }

    entryList.removeAll(entriesToRemove);

    bordereau.setnumfacure(bordereau.getNumfacure() - 1);
    bordereau.setmontant(bordereau.getMontant() + montantAvoir);

    BordereaurRepository.save(bordereau);

    avoirRepository.updateHasBordereauByAvoirId(avoirId);

    LOGGER.info("Suppression d'un avoir du bordereau avec avoirId={}, montantAvoir={}", avoirId,
        montantAvoir);

    return null;
  }

  @Override
  public Page<ListeBordereau> AdminHistoriqueBordereau(int pageNo, int pageSize,
      String numeroBordereau, String status, String userCode, String affecterTo, Date dateDebut,
      Date dateFin, Date datevalideDebut,Date datevalideFin,String sortvar, String sorttype) {
    // TODO Auto-generated method stub

    Sort sort = Sort.by("modified_date");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return BordereaurRepository.findBordereaubyStatusadmin(pageable,
        StatutBordereau.VERSEMENT_ANOMALIE, numeroBordereau, userCode, affecterTo, dateDebut,
        dateFin , datevalideDebut,datevalideFin);
  }

  @Override
  public String demandeAvanceBordereau(User user, Long idbordereau) {
    // TODO Auto-generated method stub

    AvanceCommissionAcquisition avance = avanceCommissionAcquisitionRepository
        .findByBordereauAndStatut(idbordereau, StatutAvanceBordereau.CEREATE_AVANCE);
    LocalDate currentDate = LocalDate.now();
    LocalDate startDate = currentDate.withDayOfMonth(1); // Start date of the current month
    LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
    List<AvanceCommissionAcquisition> listCommisionValiderNonPayee =
        avanceCommissionAcquisitionRepository
            .findByStatutAndRevendeur_useridAndBrdValidatedAndDateVersementBrdBetween(
                StatutAvanceBordereau.CEREATE_AVANCE, user.getUserid(), true,
                Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    if (avance != null || listCommisionValiderNonPayee.size() > 0) {
      if (avance != null) {
        avance.setStatut(StatutAvanceBordereau.AVANCE_INSTENCE);
        avance.setIdBrdDemande(idbordereau);
        avanceCommissionAcquisitionRepository.save(avance);
      }


      listCommisionValiderNonPayee.forEach(el -> {
        el.setIdBrdDemande(idbordereau);
        el.setStatut(StatutAvanceBordereau.AVANCE_INSTENCE);
        avanceCommissionAcquisitionRepository.save(el);

      });

      return "true";
    } else
      return "false";
  }

  @Override
  public Page<Map<String, Object>> bordereauListBychefsecteur(Boolean isEnInstance,
      Boolean isConfirmed, Long userid, Boolean isAnomalie, Boolean isJustificatif, Long revId,
      Pageable pageable) {
    Page<Map<String, Object>> listBordereaux = null;
    String status = null;
    if (isConfirmed != null && isConfirmed) {
      status = StatutBordereau.VERSEMENT_CONFIRME;
      listBordereaux = BordereaurRepository.findListbordereauxByDistributeurMobile(userid, status,
          null, null, revId, pageable);
    } else if (isEnInstance != null && isEnInstance) {
      status = StatutBordereau.VERSEMENT_INSTENCE;
      listBordereaux = BordereaurRepository.findListbordereauxByDistributeurMobile(userid, status,
          null, null, revId, pageable);
    } else if (isAnomalie != null && isAnomalie) {
      status = StatutBordereau.VERSEMENT_ANOMALIE;
      listBordereaux = BordereaurRepository.findListbordereauxByDistributeurMobile(userid, status,
          null, null, revId, pageable);
    } else if (isJustificatif != null && isJustificatif) {
      status = StatutBordereau.ATTENTE_JUSTIFICATION;
      listBordereaux = BordereaurRepository.findListbordereauxByDistributeurMobile(userid, status,
          null, null, revId, pageable);
    } else {
      listBordereaux = BordereaurRepository.findListbordereauxByDistributeurMobile(userid, null,
          null, null, revId, pageable);
    }
    return listBordereaux;
  }

}
