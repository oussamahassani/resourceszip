package crm.chifco.com.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import crm.chifco.com.DTOclass.DemandeAbbonementaAndAffectedToUserObjectDataDTO;
import crm.chifco.com.DTOclass.DemandeAbbonmentDataDTO;
import crm.chifco.com.DTOclass.DemandeAbbonmentDataDTOv2;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.CategorieProduitInternet;
import crm.chifco.com.model.ClassificationDemande;
import crm.chifco.com.model.Commande;
import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.DemandeAbonnementHistory;
import crm.chifco.com.model.EntryCommande;
import crm.chifco.com.model.EntryDemandeAbonnement;
import crm.chifco.com.model.EntryPack;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Gouvernorat;
import crm.chifco.com.model.ImportXlsHistoryFile;
import crm.chifco.com.model.JsonResponseBody;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.ModemAccess;
import crm.chifco.com.model.Offre;
import crm.chifco.com.model.Pack;
import crm.chifco.com.model.PostalCode;
import crm.chifco.com.model.Produit;
import crm.chifco.com.model.Profession;
import crm.chifco.com.model.Smstemplate;
import crm.chifco.com.model.Statut;
import crm.chifco.com.model.Typepaiement;
import crm.chifco.com.model.User;
import crm.chifco.com.model.VerifCINJsonResponseBody;
import crm.chifco.com.model.Ville;
import crm.chifco.com.radius.service.RadcheckService;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.ClassificationDemandeRepository;
import crm.chifco.com.repository.CodePostaleRepository;
import crm.chifco.com.repository.DemandeAbonnementHistoryRepository;
import crm.chifco.com.repository.DemandeAbonnementRepository;
import crm.chifco.com.repository.EntryDemandeAbonnementRepository;
import crm.chifco.com.repository.EntryPackRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.GouvernoratRepository;
import crm.chifco.com.repository.ImportXlsHistoryFileRepository;
import crm.chifco.com.repository.ModemAccessRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.ProduitRepository;
import crm.chifco.com.repository.ProfessionRepository;
import crm.chifco.com.repository.SmstemplateRepository;
import crm.chifco.com.repository.StatutRepository;
import crm.chifco.com.repository.TarificationRepository;
import crm.chifco.com.repository.TypepaiementRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.repository.VilleRepository;
import crm.chifco.com.service.AbonnementHistoriqueService;
import crm.chifco.com.service.AbonnementService;
import crm.chifco.com.service.AvoirService;
import crm.chifco.com.service.ClientHistoryService;
import crm.chifco.com.service.DemandeAbonnementExcelExport;
import crm.chifco.com.service.DemandeAbonnementService;
import crm.chifco.com.service.FactureService;
import crm.chifco.com.service.ImportXlsHistoryFileService;
import crm.chifco.com.service.ImportXlsHistoryService;
import crm.chifco.com.service.ModemHistoryService;
import crm.chifco.com.service.Notification;
import crm.chifco.com.service.OffreService;
import crm.chifco.com.service.PackService;
import crm.chifco.com.service.StatutService;
import crm.chifco.com.service.TarificationServices;
import crm.chifco.com.service.TypePaiementService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.ClassificationCode;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.DBEtatTT;
import crm.chifco.com.utils.NomStatutChifco;
import crm.chifco.com.utils.PrefixDocument;
import crm.chifco.com.utils.RedchekConstant;
import crm.chifco.com.utils.StatutTTConstants;
import crm.chifco.com.utils.TypeAbonnment;
import crm.chifco.com.utils.UserTypeConstant;

@Service("demandeabonnementService")
public class DemandeAbonnementServiceimpl implements DemandeAbonnementService {

  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  @Autowired
  RadcheckService radcheckService;

  @Autowired
  private DemandeAbonnementRepository demandeAbonnementRepository;

  @Autowired
  private AbonnementRepository abonnementRepository;

  @Autowired
  AbonnementService AbonnementService;

  @Autowired
  UserService UserService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ModemRepository modemRepository;

  @Autowired
  private FactureService factureService;

  @Autowired
  private StatutRepository statutRepository;

  @Value("${pathuploadxlsx}")
  private String pathUploadXlsx;

  @Autowired
  private TypepaiementRepository typePayRepository;
  ////
  @Autowired
  private ProfessionRepository professionRepository;

  @Autowired
  private ProduitRepository produitRepository;

  @Autowired
  private GouvernoratRepository gouvernoratRepository;

  @Autowired
  private CodePostaleRepository codePostaleRepository;

  @Autowired
  private TypePaiementService typePaiementService;
  @Autowired
  private VilleRepository villeRepository;

  @Autowired
  private CategorieProduitInternetRepository categorieProduitInternetRepository;

  @Autowired
  private ModemHistoryService modemHistoryService;

  @Value("${pathDemandesAbonnement}")
  private String pathDemandesAbonnement;
  @Value("${access.mail.modem.XDSL.nety}")
  private String  EmailmodemXDSL;
  @Autowired
  private AbonnementHistoriqueService AbonnementHistoriqueservice;

  @Autowired
  private ClientHistoryService ClientHistoryService;

  @Autowired
  private ImportXlsHistoryFileRepository ImportXlsHistoryFileRepository;

  @Autowired
  ImportXlsHistoryFileService ImportXlsHistoryFileService;

  @Autowired
  private ImportXlsHistoryService ImportXlsHistoryService;

  @Autowired
  SmstemplateRepository templatesmsRepository;

  @Autowired
  Notification notificationservice;

  @Autowired
  StatutRepository StatutRepository;

  @Autowired
  StatutService statutService;

  @Autowired
  OffreService offreService;

  @Autowired
  PackService packService;

  @Autowired
  TarificationRepository tarificationRepository;

  @Autowired
  EntryDemandeAbonnementRepository entryDemandeAbonnementRepository;

  @Autowired
  EntryPackRepository entryPackRepository;
  @Autowired
  TarificationServices tarificationServices;

  @Autowired
  FactureRepository factureRepository;

  @Autowired
  AvoirRepository avoirRepository;

  @Autowired
  AvoirService avoirService;

  @Autowired
  DemandeAbonnementHistoryRepository demandeAbonnementHistoryRepository;

  @Autowired
  ClassificationDemandeRepository classificationDemandeRepository;

  @Autowired
  ModemAccessRepository modemAccessRepository;

  @Override
  public Page<DemandeAbbonmentDataDTO> findPaginatedByDistributeurWithSort(int pageNo, int pageSize,
      Long createdbyuserid, Long userid, String refChif, String refTT, String cin, String prenom,
      String nom, Long tel, Long villes, Long gouvernorat, Long professions, Long categories,
      Long produit, Long statutListfiltre, String statutTTListfiltre, String datedebut,
      String datefin, String dateDebutModification, String dateFinModification, Long creePar,
      Long AffecterTo, String datedebutMiseService, String datefinMiseService,String typeDabonnement, String sortvar,
      String sorttype) {
    Sort sort = Sort.by("createdDate").descending();
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Date dateDebutModifications = null;
    Date dateFinModifications = null;
    Date dateDebutCreation = null;
    Date dateFinCreation = null;
    Date dateDebutMiseService = null;
    Date dateFinMiseService = null;

    if (datedebutMiseService != null) {
      dateDebutMiseService = CrmUtils.convertedFilterRechercheDate(datedebutMiseService);
    }

    if (datefinMiseService != null) {
      dateFinMiseService = CrmUtils.convertedFilterRechercheDate(datefinMiseService);
    }

    if (dateDebutModification != null) {
      dateDebutModifications = CrmUtils.convertedFilterRechercheDate(dateDebutModification);
    }

    if (dateFinModification != null) {
      dateFinModifications = CrmUtils.convertedFilterRechercheDate(dateFinModification);
    }

    if (datedebut != null) {
      dateDebutCreation = CrmUtils.convertedFilterRechercheDate(datedebut);
    }

    if (datefin != null) {
      dateFinCreation = CrmUtils.convertedFilterRechercheDate(datefin);
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.demandeAbonnementRepository
        .findDemandeAbonnementsByKeywordbydistributeursearchparamsnotempty(refChif, refTT, cin,
            prenom, nom, tel, villes, gouvernorat, professions, categories, produit,
            statutListfiltre, statutTTListfiltre, dateDebutModifications, dateFinModifications,
            dateDebutCreation, dateFinCreation, createdbyuserid, userid, creePar, AffecterTo,
            dateDebutMiseService, dateFinMiseService,typeDabonnement, pageable);
  }

  @Override
  public Page<DemandeAbbonmentDataDTO> findPaginatedByRevendeurWithSearchParamsNotEmptyWithSort(
      int pageNo, int pageSize, Long roleid, Long userid, String refchif, String refTT, String cin,
      String prenom, String nom, Long tel, Long villeid, Long gouvernoratid, Long professionid,
      Long categorieid, Long produitid, Long statutid, String statutTTListfiltre, String datedebut,
      String datefin, String dateDebutModification, String dateFinModification, String sortvar,
      String sorttype) {
    Sort sort = Sort.by("modifieddate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

    Date dateDebutModifications = null;
    Date dateFinModifications = null;
    Date datedebuts = null;
    Date datefins = null;

    if (datedebut != null) {
      datedebuts = CrmUtils.convertedFilterRechercheDate(datedebut);
    }

    if (datefin != null) {
      datefins = CrmUtils.convertedFilterRechercheDate(datefin);
    }
    if (dateDebutModification != null) {
      dateDebutModifications = CrmUtils.convertedFilterRechercheDate(dateDebutModification);
    }

    if (dateFinModification != null) {
      dateFinModifications = CrmUtils.convertedFilterRechercheDate(dateFinModification);
    }

    return this.demandeAbonnementRepository
        .findDemandeAbonnementsByKeywordbyrevendeursearchparamsnotempty(refchif, refTT, cin, prenom,
            nom, tel, villeid, gouvernoratid, professionid, categorieid, produitid, statutid,
            statutTTListfiltre, datedebuts, datefins, dateDebutModifications, dateFinModifications,
            userid, null, null,null, pageable);
  }

  @Override
  public Page<DemandeAbonnement> findPaginatedWithSearchParamsNotemptyWithSort(int pageNo,
      int pageSize, String refchif, String refTT, String cin, String prenom, String nom, String tel,
      Long villeid, Long gouvernoratid, Long professionid, Long categorieid, Long produitid,
      Long statutid, String statutTTListfiltre, String datedebut, String datefin,
      String dateDebutModification, String dateFinModification, Long AffecterTo, Long CreePar,
      String sortvar, String sorttype) {

    Sort sort = Sort.by("modified_date");
    if (sortvar == null || sortvar == "") {
      sortvar = "modified_date";
    }
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.demandeAbonnementRepository.findDemandeAbonnementsByPramsnotempty(refchif, refTT,
        cin, prenom, nom, tel, villeid, gouvernoratid, professionid, categorieid, produitid,
        statutid, statutTTListfiltre, datedebut, datefin, dateDebutModification,
        dateFinModification, AffecterTo, CreePar, pageable);
  }

  public Boolean checkFilterValue(JSONObject obj) {
    boolean statutListfiltre =
        (obj.has("statutListfiltre") && obj.getString("statutListfiltre").trim() != "");
    boolean statutTTListfiltre =
        (obj.has("statutTTListfiltre") && obj.getString("statutTTListfiltre").trim() != "");
    boolean gouvernorat = (obj.has("gouvernorat") && obj.getString("gouvernorat").trim() != "");
    boolean professions = (obj.has("professions") && obj.getString("professions").trim() != "");
    boolean categories = (obj.has("categories") && obj.getString("categories").trim() != "");
    boolean produit = (obj.has("produit") && obj.getString("produit").trim() != "");
    boolean villes = (obj.has("villes") && obj.getString("villes").trim() != "");
    boolean refChif = (obj.has("refChif") && obj.getString("refChif").trim() != "");
    boolean refTT = (obj.has("refTT") && obj.getString("refTT").trim() != "");
    boolean cin = (obj.has("cin") && obj.getString("cin").trim() != "");
    boolean prenom = (obj.has("prenom") && obj.getString("prenom").trim() != "");
    boolean nom = (obj.has("nom") && obj.getString("nom").trim() != "");
    boolean tel = (obj.has("tel") && obj.getString("tel").trim() != "");
    boolean datedebut = (obj.has("datedebut") && obj.getString("datedebut").trim() != "");
    boolean datefin = (obj.has("datefin") && obj.getString("datefin").trim() != "");
    boolean dateDebutModification =
        (obj.has("dateDebutModification") && obj.getString("dateDebutModification").trim() != "");
    boolean dateFinModification =
        (obj.has("dateFinModification") && obj.getString("dateFinModification").trim() != "");

    if (statutListfiltre || gouvernorat || professions || categories || produit || villes || refChif
        || refTT || cin || prenom || nom || tel || datedebut || datefin || statutTTListfiltre
        || dateDebutModification || dateFinModification) {
      return true;
    }
    return false;

  }

  @Override
  public String editAbonnement(Statut statut, String modemfromlist, Long demandeAbonnementid,
      String fromlocation, MultipartFile file, Model model, RedirectAttributes redirectAttrs,
      Long telefixe, String referencett, String motifRefus ,String telemobile5g) {
    String resultchangmentstatut = "false";
    // try {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      List<String> StringsRole = new ArrayList<String>();
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

      LOGGER.info("id: " + statut + " " + modemfromlist + " " + demandeAbonnementid);
      DemandeAbonnement demandeAbonnement =
          demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeAbonnementid);

      if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.SIGNED_DOC)
          && (statut.getNomStatut().equals(NomStatutChifco.WAIT_TT))) {
   if (demandeAbonnement.getTypeAbonnment() != null &&    demandeAbonnement.getTypeAbonnment().equals(TypeAbonnment.Box)) {
       demandeAbonnement.setEtatTT(DBEtatTT.ConstructionLigne);
       updateStatutAndSave(statut, demandeAbonnement);

   }
   else {
        if (referencett != null && !referencett.equals("")) {

          List<DemandeAbonnement> checkReferenceExiste =
              demandeAbonnementRepository.findDemandeAbonnementByreferenceTT(referencett.trim());
          if (checkReferenceExiste.size() == 0) {
            demandeAbonnement.setReferenceTT(referencett.trim());
            demandeAbonnement.setEtatTT(DBEtatTT.Enregister);
            updateStatutAndSave(statut, demandeAbonnement);

            redirectAttrs.addFlashAttribute("message", "sendtt");

            AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
          } else {

            redirectAttrs.addFlashAttribute("message", "sendttReferenceExiste");
          }

        } else {

          redirectAttrs.addFlashAttribute("message", "sendttReferenceObligtoir");
        }
   }
      }

      else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.SIGNED_DOC)
          && (statut.getNomStatut().equals(NomStatutChifco.CANCELED))) {

        updateStatutAndSave(statut, demandeAbonnement);
        resultchangmentstatut = "true";

        ClassificationDemande classificationRefus = classificationDemandeRepository
            .findClassificationDemandeByCodeClassification(ClassificationCode.RCommercial);

        // historique de classification
        String message;
        if (demandeAbonnement.getDecisionDemande() == null) {
          message = "La classification a été changée à '" + classificationRefus.getValue() + "'";
        } else {
          message = "La classification a été changée de '"
              + demandeAbonnement.getDecisionDemande().getValue() + "' à '"
              + classificationRefus.getValue() + "'";
        }
        AbonnementHistoriqueservice.saveNewHistorique(user, demandeAbonnement.getDemandeId(),
            message);

        demandeAbonnement.setDecisionDemande(classificationRefus);
        demandeAbonnement.setDateDecisionDemande(new Date());
        demandeAbonnementRepository.save(demandeAbonnement);
        redirectAttrs.addFlashAttribute("message", "statutanuller");
        AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
      } else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.DRAFT)
          && (statut.getNomStatut().equals(NomStatutChifco.CANCELED))) {
        updateStatutAndSave(statut, demandeAbonnement);

        redirectAttrs.addFlashAttribute("message", "statutanuller");

        ClassificationDemande classificationRefus = classificationDemandeRepository
            .findClassificationDemandeByCodeClassification(ClassificationCode.RCommercial);

        // historique de classification

        String message;
        if (demandeAbonnement.getDecisionDemande() == null) {
          message = "La classification a été changée à '" + classificationRefus.getValue() + "'";

          demandeAbonnement.setDecisionDemande(classificationRefus);
          demandeAbonnement.setDateDecisionDemande(new Date());
          AbonnementHistoriqueservice.saveNewHistorique(user, demandeAbonnement.getDemandeId(),
              message);
        } else {
          message = "La classification a été changée de '"
              + demandeAbonnement.getDecisionDemande().getValue() + "' à '"
              + classificationRefus.getValue() + "'";
        }



        demandeAbonnementRepository.save(demandeAbonnement);
        resultchangmentstatut = "true";
        AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);

      } else if (((demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.DRAFT))
          || (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.WAIT_TT))
          || (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.SIGNED_DOC)))
          && (statut.getNomStatut().equals(NomStatutChifco.SAISIE_INFAISABLE))) {
        updateStatutAndSave(statut, demandeAbonnement);

        ClassificationDemande classificationRefus = classificationDemandeRepository
            .findClassificationDemandeByCodeClassification(ClassificationCode.RCommercial);

        // historique de classification
        String message;
        if (demandeAbonnement.getDecisionDemande() == null) {
          message = "La classification a été changée à '" + classificationRefus.getValue() + "'";
        } else {
          message = "La classification a été changée de '"
              + demandeAbonnement.getDecisionDemande().getValue() + "' à '"
              + classificationRefus.getValue() + "'";
        }
        AbonnementHistoriqueservice.saveNewHistorique(user, demandeAbonnement.getDemandeId(),
            message);

        demandeAbonnement.setDecisionDemande(classificationRefus);
        demandeAbonnement.setDateDecisionDemande(new Date());
        demandeAbonnementRepository.save(demandeAbonnement);
        redirectAttrs.addFlashAttribute("message", " saisie infaisable");

        resultchangmentstatut = "true";
        AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);

      } else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.WAIT_TT)
          && (statut.getNomStatut().equals(NomStatutChifco.INSTALLED)
              || (statut.getNomStatut().equals(NomStatutChifco.CANCELED))
              || (statut.getNomStatut().equals(NomStatutChifco.REFUSED)))) {

        if (statut.getNomStatut().equals(NomStatutChifco.CANCELED)
            || statut.getNomStatut().equals(NomStatutChifco.REFUSED)) {
          if (statut.getNomStatut().equals(NomStatutChifco.REFUSED)) {
            demandeAbonnement.setEtatTT(DBEtatTT.Refused);

            confirmeAbonnement(DBEtatTT.Refused, demandeAbonnement.getDemandeId(), user);
            /*
             * ClassificationDemande classificationDEnAttente = classificationDemandeRepository
             * .findClassificationDemandeByCodeClassification(ClassificationCode.DEnAttente);
             * 
             * // historique de classification String message;
             * 
             * if (demandeAbonnement.getDecisionDemande() == null) { message =
             * "La classification a été changée à '" + ClassificationCode.DEnAttente.toString() +
             * "'"; demandeAbonnement.setDecisionDemande(classificationDEnAttente);
             * demandeAbonnement.setDateDecisionDemande(new Date());
             * AbonnementHistoriqueservice.saveNewHistorique(user, demandeAbonnement.getDemandeId(),
             * message); }
             */
            /*
             * else { message = "La classification a été changée de '" +
             * demandeAbonnement.getDecisionDemande().getValue() + "' à '" +
             * ClassificationCode.DEnAttente.toString() + "'"; }
             */



          } else {
            confirmeAbonnement(DBEtatTT.Cancled, demandeAbonnement.getDemandeId(), user);
            demandeAbonnement.setEtatTT(DBEtatTT.Cancled);
            ClassificationDemande classificationRefus = classificationDemandeRepository
                .findClassificationDemandeByCodeClassification(ClassificationCode.RCommercial);

            // historique de classification
            String message;
            if (demandeAbonnement.getDecisionDemande() == null) {
              message =
                  "La classification a été changée à '" + ClassificationCode.RCommercial + "'";
            } else {
              message = "La classification a été changée de '"
                  + demandeAbonnement.getDecisionDemande().getValue() + "' à '"
                  + ClassificationCode.RCommercial + "'";
            }
            AbonnementHistoriqueservice.saveNewHistorique(user, demandeAbonnement.getDemandeId(),
                message);

            demandeAbonnement.setDecisionDemande(classificationRefus);
            demandeAbonnement.setDateDecisionDemande(new Date());
          }
          if (motifRefus != null && !motifRefus.equals("")) {
            demandeAbonnement.setMotifRefus(motifRefus);
          }

          updateStatutAndSave(statut, demandeAbonnement);

          redirectAttrs.addFlashAttribute("message", "statutanuller");
          resultchangmentstatut = "true";
          AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
        }

        // (statut.equals(Statut.INSTALLED))
        if (statut.getNomStatut().equals(NomStatutChifco.INSTALLED)) {
          addFixePhoneToAbonnement(telefixe,telemobile5g, demandeAbonnement, redirectAttrs);
          if (demandeAbonnement.getTelFixe() != null) {
            String referenceChifco = demandeAbonnement.getReferenceChifco();
            Long iduserCreted = demandeAbonnement.getUser().getUserid();

            String numeroUsercreated = demandeAbonnement.getAssignedTo().getTelephone();
            ArrayList<String> arrayTelephoneStock = new ArrayList<>();
            // arrayTelephoneStock.add(numerotelephoenereadmin);
            arrayTelephoneStock.add(numeroUsercreated);
            String nomCategoryInternet =
                demandeAbonnement.getCategorieProduitInternet().getCategorieProduitInternetNom();

            this.sendSmsIfNonStock(nomCategoryInternet, iduserCreted, arrayTelephoneStock,
                referenceChifco, false);
            demandeAbonnement.setEtatTT(DBEtatTT.Mise_en_service);
            demandeAbonnement.setStatut(statut);
            demandeAbonnement.setDateDecisionDemande(new Date());
            demandeAbonnement.setDateDeMiseEnService(new Date());
            ClassificationDemande classificationOk = classificationDemandeRepository
                .findClassificationDemandeByCodeClassification(ClassificationCode.ACCEPTATION);

            // historique de classification
            String message;
            if (demandeAbonnement.getDecisionDemande() == null) {
              message =
                  "La classification a été changée à '" + ClassificationCode.ACCEPTATION + "'";
            } else {
              message = "La classification a été changée de '"
                  + demandeAbonnement.getDecisionDemande().getValue() + "' à '"
                  + ClassificationCode.ACCEPTATION + "'";
            }
            AbonnementHistoriqueservice.saveNewHistorique(user, demandeAbonnement.getDemandeId(),
                message);

            demandeAbonnement.setDecisionDemande(classificationOk);
            demandeAbonnementRepository.save(demandeAbonnement);
            AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);

            Abonnement checkclientexist =
                AbonnementService.findAbonnementByCin(demandeAbonnement.getCin());
            if (checkclientexist == null) {
              checkclientexist = AbonnementService.saveNewAbonnement(demandeAbonnement);

              ClientHistoryService.insertNewHistoryclient(demandeAbonnement,
                  "Abonnement validé et installé par TT", user);
            } else {
            	if(!statut.getNomStatut().equals(NomStatutChifco.RESILIATION))
            	{
            	     checkclientexist.setStatut(statut);
                     abonnementRepository.save(checkclientexist);
            	}
         
            }
            if(telemobile5g != null && !telemobile5g.trim().isEmpty()) {
          Modem  mynumSim =    modemRepository.findByNumSerieOrEmail(telemobile5g , null);

            	   mynumSim.setAffecteClient(checkclientexist.getClientid());
            	   mynumSim.setStatus(false);

                   modemRepository.save(mynumSim);
            }
         
            
            Facture ChekIfIsFirstFactureExist =
                factureService.ChekIfIsFirstFactureExist(checkclientexist.getClientid());
            if (ChekIfIsFirstFactureExist == null) {
              Instant datenow = Instant.now();
              DateTimeFormatter formatter =
                  DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
              Instant echanceCommande = datenow.plus(30, ChronoUnit.DAYS);
              String echancecommandeDate = formatter.format(echanceCommande);
              // commande.setProduit(produits);

              List<EntryCommande> EntryCommande =
                  factureService.setEntriesCommande(checkclientexist, checkclientexist.getPack());
              Date nouvauDateFin = CrmUtils.calculeDateFin(
                  demandeAbonnement.getTypePaiement().getNombreMoisTypePaiement(), null);
              Date prochaineDateFacture = null;
              if (checkclientexist.getPack().getPayLater() != null
                  && checkclientexist.getPack().getPayLater()) {
                nouvauDateFin = Date.from(CrmUtils.dateLimitePromo().plusDays(1)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
                prochaineDateFacture = nouvauDateFin;
              }
              Commande commande = factureService.setCommande(checkclientexist,
                  nouvauDateFin.toString(), echancecommandeDate, user, EntryCommande);

              Facture premiereFacture =
                  factureService.generateFacture(commande, user, true, prochaineDateFacture, null);

              factureService.setEntryTvaFacture(premiereFacture);

            }

            Smstemplate findTemplateSmsClientMiseService =
                templatesmsRepository.findSmstemplateByname("clientmiseenservice");
            String TemplateSmsClientMiseService = findTemplateSmsClientMiseService.getTemplate();
            Map<String, Object> smsMessageTosend = new HashMap<String, Object>();
            ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
            if (demandeAbonnement.getTelFixe() != null && !demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {
           
            
              String NewfindTemplateSmsClientMiseService = TemplateSmsClientMiseService
                  .replace("{referencedemande}", demandeAbonnement.getTelFixe().toString());
              smsMessageTosend.put("number", demandeAbonnement.getTelMobile());

              smsMessageTosend.put("message", NewfindTemplateSmsClientMiseService );
              smsToSend.add(smsMessageTosend);
            }



            if (smsToSend.size() > 0) {
              notificationservice.sendsmsnotification(smsToSend);
            }

            redirectAttrs.addFlashAttribute("message", "INSTALLED");
          }

        }


      } else if ((demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.POROFORMA)
          || demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.INSTALLED))
          && (statut.getNomStatut().equals(NomStatutChifco.ASSIGNED))) {
        if (modemfromlist != null) {
          LOGGER.info("modemfromlist: " + modemfromlist + " " + demandeAbonnement.getCin());

          Modem modemType = modemRepository.findByNumSerieOrEmail(modemfromlist, null);
          Optional<Modem> modem = null;
          if (!StringsRole.contains("ASSIGN_MODEM_CHANGE_TECHNOLOGIES")
              && !modemType.getModelModem().equals("XDSL")) {
            if (StringsRole.contains("READ_MODEM")) {
              modem = modemRepository.findModemByNumSerieOptionalAdmin(modemfromlist, null);
            } else if (StringsRole.contains("READ_MODEM_LIST_AREA")) {
              modem = modemRepository.findModemByNumSerieOptionalDist(modemfromlist,
                  user.getUserid(), demandeAbonnement.getCategorieProduitInternet()
                      .getCategorieProduitInternetCode());
            } else if (StringsRole.contains("READ_MODEM_POS")) {
              modem = modemRepository.findModemByNumSerieOptionalPos(modemfromlist,
                  user.getUserid(), demandeAbonnement.getCategorieProduitInternet()
                      .getCategorieProduitInternetCode());
            } else if (StringsRole.contains("READ_MODEM_OWNER")) {
              modem = modemRepository.findModemByNumSerieOptionalRev(modemfromlist,
                  user.getUserid(), demandeAbonnement.getCategorieProduitInternet()
                      .getCategorieProduitInternetCode());
            }
          } else {
            // verification modem si existe ou non
            if (StringsRole.contains("READ_MODEM")) {
              modem = modemRepository.findModemByNumSerieOptionalAdmin(modemfromlist, null);
            } else if (StringsRole.contains("READ_MODEM_LIST_AREA")) {
              modem = modemRepository.findModemByNumSerieOptionalDist(modemfromlist,
                  user.getUserid(), null);
            } else if (StringsRole.contains("READ_MODEM_POS")) {
              modem = modemRepository.findModemByNumSerieOptionalPos(modemfromlist,
                  user.getUserid(), null);
            } else if (StringsRole.contains("READ_MODEM_OWNER")) {
              modem = modemRepository.findModemByNumSerieOptionalRev(modemfromlist,
                  user.getUserid(), null);
            }
          }
          if (!modem.isPresent()) {
            return "MODEM_NOT_FOUNT";
          }
          if(modem.get().getModelModem().equals("XDSL")) {
          long modemAccessCount =
                  modemAccessRepository.countByStatusAndModelModem(true,
                      demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode());
          if(modemAccessCount<20)
          {
        	  notificationservice.sendSimpleMail(EmailmodemXDSL,"Merci de nous envoyer la liste des accès des modems de ce type: " +demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode(),"Acces modem XDSL:affecation") ;
          }
          }
          ModemAccess modemAccess =
              modemAccessRepository.findFirstModemAccessByStatusAndModelModem(true,
                  demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode());
          if (modem.get().getEmail() == null || modem.get().getEmail().isEmpty()) {
            if (modemAccess != null) {
              modem.get().setEmail(modemAccess.getEmail());
              modem.get().setPassword(modemAccess.getPassword());
              modemRepository.save(modem.get());
              modemAccess.setStatus(false);
              modemAccess.setIdModem(modem.get().getModemId());
              modemAccessRepository.save(modemAccess);
            } else {
              redirectAttrs.addFlashAttribute("message", "modemAccessNotFound");
              
              
              return "MODEM_ACCESS_NOT_FOUND";

            }
          }
          
      
          
          Abonnement checkclientexist =
              AbonnementService.findAbonnementByCin(demandeAbonnement.getCin());

          if (checkclientexist != null) {
            if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.INSTALLED)) {
              // gjhjghjghj
              factureService.updateVisibilityFirstFacture(checkclientexist.getClientid());
            }
            modemHistoryService.save(
                "Affecter le modem au client " + demandeAbonnement.getReferenceChifco(), user,
                modem.get());
            checkclientexist.setStatut(statut);
            checkclientexist.setModem(modem.get());
            checkclientexist.setModemAffectedDate(new Date());
            abonnementRepository.save(checkclientexist);

            if (modem.get().getEmail() != null && !modem.get().getEmail().isEmpty()
            	&&	 !demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {
              String LoginASc = modem.get().getEmail(); //

              String RandemPassword = modem.get().getPassword();
              if (RandemPassword == null) {
                RandemPassword = CrmUtils.randemvaluePasswordModem();
              }
              checkclientexist.setPassword(RandemPassword);
              checkclientexist.setLoginModem(LoginASc);
              abonnementRepository.save(checkclientexist);
              Facture ChekIfIsFirstFactureExist =
                  factureService.ChekIfIsFirstFactureExist(checkclientexist.getClientid());
              if (ChekIfIsFirstFactureExist != null) {
                radcheckService.addNewRow(LoginASc, RedchekConstant.CleartextPassword,
                    RandemPassword);
                radcheckService.addNewRow(LoginASc, RedchekConstant.Expiration,
                    CrmUtils.RadusDateDexpiration(ChekIfIsFirstFactureExist.getDateDeFin()));
                radcheckService.AddNewradusergroup(LoginASc, checkclientexist.getPack()
                    .getCategoriePack().getCategorieProduitInternetCode());
                LOGGER.info("create new row  in raduse data base with email " + LoginASc);
              }
            }
          }



          modem.get().setAffecteClient(checkclientexist.getClientid());
          modemRepository.save(modem.get());
          demandeAbonnement.setModem(modem.get());

          updateStatutAndSave(statut, demandeAbonnement);

          AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);

          resultchangmentstatut = "true";
          Map<String, Object> smsMessageTosend = new HashMap<String, Object>();
          ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();

          LocalDate currentDate = LocalDate.now();
          LocalDate startDate = LocalDate.of(2025, 8, 26);
          LocalDate endDate = LocalDate.of(2026, 9, 26);
          if (currentDate.isEqual(startDate)
              || (currentDate.isAfter(startDate) && currentDate.isBefore(endDate))
              || currentDate.isEqual(endDate)) {
        	 	if(demandeAbonnement.getPack().getOffre().getOffreId() == 6248878 || 
            			demandeAbonnement.getPack().getOffre().getOffreId() == 6248958) {
            String codePromo = notificationservice.getCodePromo();
            String msgAffectationModem =
                "Bienvenue chez Nety ! Votre Green Flag est activée. Profitez de 50 DT de remise sur vos achats ≥100 DT sur notre e-shop disponible via http://www.nety.tn  en tapant le code "
               +codePromo  + " Et découvrez encore plus d’avantages en téléchargeant My Nety:https://s.nety.tn/nTvibb ";
           // msgAffectationModem += " Code coupon : " + codePromo;
            LOGGER.info("promocode:  " + codePromo + " pour le user " + demandeAbonnement.getReferenceChifco());
            smsMessageTosend.put("number", demandeAbonnement.getTelMobile());

            smsMessageTosend.put("message", msgAffectationModem);
            smsToSend.add(smsMessageTosend);
            if (smsToSend.size() > 0) {
              notificationservice.sendsmsnotification(smsToSend);
            }
        	 	}
          }
          
          if(demandeAbonnement.getPack().getOffre().getOffreId() == 7561910) {
    			
    	            String codePromo = CrmUtils.codes.get(0);
    	            String msgAffectationModem =
    	                "Bienvenue dans la famille NETY !"
    	                + "Profitez d’1 mois exclusif StarzPlay offert."
    	                + "Activez votre avantage via :"
    	                + "Code promo : "+codePromo
    	                + " Profitez-en vite et restez connecté avec nous !"
    	                 ;
    	           // msgAffectationModem += " Code coupon : " + codePromo;
    	            LOGGER.info("promocode starzplay:  " + codePromo + " pour le user " + demandeAbonnement.getReferenceChifco());
    	            smsMessageTosend.put("number", demandeAbonnement.getTelMobile());

    	            smsMessageTosend.put("message", msgAffectationModem);
    	            smsToSend.add(smsMessageTosend);
    	            CrmUtils.codes.remove(0);              // 2️⃣ supprimer

    	            if (smsToSend.size() > 0) {
    	              notificationservice.sendsmsnotification(smsToSend);
    	            }
    	        	 	}
          
          redirectAttrs.addFlashAttribute("message", "Modem est affecté à l'abonnement!");
        }
      } else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.ASSIGNED)
          && (statut.getNomStatut().equals(NomStatutChifco.VALID))) {
        // Facture premierefacture = new Facture();
        if (!file.isEmpty()) {
          try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();
            String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);

            String folder = pathDemandesAbonnement + demandeAbonnement.getCin();
            File uploadDir = new File(folder);
            if (!uploadDir.exists()) {
              uploadDir.mkdirs();
            }

            Path path = Paths.get(pathDemandesAbonnement + demandeAbonnement.getCin() + "/"
                + PrefixDocument.NOMEFILE_CONTRAT_SINGER + demandeAbonnement.getReferenceChifco()
                + "_" + dtf.format(now) + "." + extension);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            demandeAbonnement.setContratPdf(PrefixDocument.NOMEFILE_CONTRAT_SINGER
                + demandeAbonnement.getReferenceChifco() + "_" + dtf.format(now) + "." + extension);
            Statut activateStatut = statutService.findStatutByNomstatut(NomStatutChifco.VALID);
            demandeAbonnement.setStatut(activateStatut);

            demandeAbonnement.setDateFinContrat(
                CrmUtils.CalculatedateFinEngagement(demandeAbonnement.getPack()));
            demandeAbonnementRepository.save(demandeAbonnement);
            resultchangmentstatut = "true";

            Abonnement abonnement =
                AbonnementService.findAbonnementByCin(demandeAbonnement.getCin());

            redirectAttrs.addFlashAttribute("message", "statut contartsignee");

            LOGGER.info("activateStatut: " + activateStatut.getStatutId());
            abonnement.setStatut(activateStatut);
            abonnement.setDateFinContrat(
                CrmUtils.CalculatedateFinEngagement(demandeAbonnement.getPack()));
            abonnementRepository.save(abonnement);

            Facture firstFacture =
                factureRepository.findByvisibilityAndAbonnement_clientidAndIsFirstFacture(false,
                    abonnement.getClientid(), true);
            if (firstFacture != null) {
              firstFacture.setVisibility(true);
              factureRepository.save(firstFacture);
            }
            AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
            ClientHistoryService.insertNewHistoryclient(demandeAbonnement,
                "La signature de contrat", user);
          } catch (IOException e) {
            LOGGER.error(
                "DemandeAbonnementServiceimpl.editAbonnement IOException: " + e.getMessage());

          }
        }

      } else if (demandeAbonnement.getStatut().getNomStatut()
          .equals(NomStatutChifco.CLIENT_INJOIGNABLE)
          && (statut.getNomStatut().equals(NomStatutChifco.DRAFT)
              || statut.getNomStatut().equals(NomStatutChifco.SIGNED_DOC))) {
        updateStatutAndSave(statut, demandeAbonnement);
      }
    }

    return resultchangmentstatut;
  }

  @Override
  public String verificationTelFix(MultiValueMap<String, String> formData) {
    String telFixe = null;
    if (formData.getFirst("telFixe") != null && !formData.getFirst("telFixe").isEmpty()) {
      telFixe = formData.getFirst("telFixe");
      LOGGER.info("id: " + formData.getFirst("telFixe"));
      Long tel = Long.parseLong(telFixe);
      DemandeAbonnement demandeAbonnement =
          demandeAbonnementRepository.findDemandeAbonnementByTelfixeAndStatusAvaibled(tel);

      LOGGER.info("id: " + demandeAbonnement);
      VerifCINJsonResponseBody jrb = new VerifCINJsonResponseBody();
      if (demandeAbonnement != null) {
        LOGGER.info("id: " + demandeAbonnement);
        return "Demande d'abonnement existe deja avec ce CIN";
      } else if (demandeAbonnement == null) {
        jrb.setCode("Success");
        jrb.setMsg("Aucune demande d'abonnement existante avec ce CIN");
        return "true";
      } else {
        return "false";
      }

    } else {
      return "true";
    }
  }

  @Override
  public String verificationCin(MultiValueMap<String, String> formData) {
    try {
      LOGGER.info("id: " + formData.getFirst("cin"));
      String cin = null;
      if (formData.getFirst("cin") != null)
        cin = formData.getFirst("cin");
      List<DemandeAbonnement> demandeAbonnement =
          demandeAbonnementRepository.findDemandeAbonnementsByCinAndStatusAvaibled(cin);
      VerifCINJsonResponseBody jrb = new VerifCINJsonResponseBody();
      if (demandeAbonnement.size() != 0) {
        jrb.setCode("Erreur");
        jrb.setMsg("Demande d'abonnement existe deja avec ce CIN");
        return "Demande d'abonnement existe deja avec ce CIN";
      } else if (demandeAbonnement.size() == 0) {
        jrb.setCode("Success");
        jrb.setMsg("Aucune demande d'abonnement existante avec ce CIN");
        return "true";
      } else
        return "false";
    } catch (Exception e) {

      LOGGER.error("DemandeAbonnementServiceimpl.Verificationcin Acs api getlisteclient: " + e);
      return "false";
    }
  }

  @Override
  public List<Statut> getallStatusAbonnement() {
    List<crm.chifco.com.model.Statut> list = statutRepository.findAll();
    return list;
  }

  @Override
  public List<Modem> getModems(String codeProduit, Boolean getAllType) {

    List<String> StringsRole = new ArrayList<String>();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    if (getAllType != null && getAllType.equals(true) && !codeProduit.equals(TypeAbonnment.Box) ) {
      if (StringsRole.contains("READ_MODEM")) {
        return modemRepository.findAllModemsNotAffectedAdmin();
      }

      else if (StringsRole.contains("READ_MODEM_OWNER")) {
        return modemRepository.findAllModemNotAffectedRev(user.getUserid());
      } else if (StringsRole.contains("READ_MODEM_POS")) {
        return modemRepository.findALLModemNotAffectedPos(user.getUserid());
      } else if (StringsRole.contains("READ_MODEM_LIST_AREA")) {
        return modemRepository.findAllModemNotAffectedDistributeur(user.getUserid());
      }
    } else {
      if (StringsRole.contains("READ_MODEM")) {
        if (codeProduit.equals("GPON") || codeProduit.equals("Sim.5G") || codeProduit.equals(TypeAbonnment.Box)) {
          return modemRepository.findModemsNotAffectedAdminBYcategory(codeProduit);
        }
        return modemRepository.findModemsNotAffectedAdmin(codeProduit);
      }

      else if (StringsRole.contains("READ_MODEM_OWNER")) {
        if (codeProduit.equals("GPON")  || codeProduit.equals("Sim.5G") || codeProduit.equals(TypeAbonnment.Box)) {
          return modemRepository.findModemNotAffectedRevBYcategory(user.getUserid(), codeProduit);
        }
        return modemRepository.findModemNotAffectedRev(user.getUserid(), codeProduit);
      } else if (StringsRole.contains("READ_MODEM_POS")) {
        if (codeProduit.equals("GPON")  || codeProduit.equals("Sim.5G") ||  codeProduit.equals(TypeAbonnment.Box)) {
          return modemRepository.findModemNotAffectedPosBYcategory(user.getUserid(), codeProduit);
        }
        return modemRepository.findModemNotAffectedPos(user.getUserid(), codeProduit);
      } else if (StringsRole.contains("READ_MODEM_LIST_AREA")) {
        if (codeProduit.equals("GPON")  || codeProduit.equals("Sim.5G") ||  codeProduit.equals(TypeAbonnment.Box)) {
          return modemRepository.findModemNotAffectedDistributeurBYcategory(user.getUserid(),
              codeProduit);
        }
        return modemRepository.findModemNotAffectedDistributeur(user.getUserid(), codeProduit);
      }
    }

    return null;

  }

  @Override
  public JsonResponseBody addFiled(Long id, HttpServletRequest request) {
    LOGGER.info("id: " + id);
    Object checkliste = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checkliste);
    List<Long> listesdesIds = new ArrayList<>();
    if (checkliste == null || checkliste.equals("")) {
      if (id == null || id.equals("")) {
        request.getSession().setAttribute("listedes_ids", listesdesIds);
        LOGGER.debug("listedes_ids if: " + listesdesIds);
      } else {
        listesdesIds.add(id);
        request.getSession().setAttribute("listedes_ids", listesdesIds);
      }

    } else {
      listesdesIds = (List<Long>) request.getSession().getAttribute("listedes_ids");
      LOGGER.debug("listedes_ids else: " + listesdesIds.contains(id));
      if (listesdesIds.contains(id) == false) {
        listesdesIds.add(id);
      }

      request.getSession().setAttribute("listedes_ids", listesdesIds);
    }
    JsonResponseBody jrb = new JsonResponseBody();
    jrb.setCode(String.valueOf(200));
    jrb.setMsg("Ajout de l'id à liste avec succes");
    jrb.setResult(listesdesIds);
    return jrb;
  }

  @Override
  public List<Long> addAllIdToExport(String filterrecherche, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

    String refChif = null;
    String refTT = null;
    String cin = null;
    String prenom = null;
    String nom = null;
    Long tel = null;
    Long villes = null;
    Long gouvernorat = null;
    Long professions = null;
    Long categories = null;
    Long produit = null;
    Long statutListfiltre = null;
    String statutTTListfiltre = null;
    Date datedebut = null;
    Date datefin = null;
    Date dateDebutModification = null;
    Date dateFinModification = null;
    Long AffecterTo = null;
    Long CreePar = null;
    String Classification = null;
    Date datedebutMiseService = null;
    Date datefinMiseService = null;
    JSONArray motifInstance = null;
    Integer agentId = null;
    String source = null;
    String typeAbonnement = null ;
    JSONObject obj = new JSONObject(filterrecherche);
    if (!Objects.equals(obj.getString("prenom"), "") && obj.getString("prenom") != null) {
      prenom = obj.getString("prenom").trim();
    }
    if (!Objects.equals(obj.getString("nom"), "") && obj.getString("nom") != null) {
      nom = obj.getString("nom").trim().toLowerCase();
    }
    if (!Objects.equals(obj.getString("statutListfiltre"), "")
        && obj.getString("statutListfiltre") != null) {
      statutListfiltre = obj.getLong("statutListfiltre");
    }
    if (!Objects.equals(obj.getString("statutTTListfiltre"), "")
        && obj.getString("statutTTListfiltre") != null) {
      statutTTListfiltre = obj.getString("statutTTListfiltre");
    }
    if (!Objects.equals(obj.getString("villes"), "") && obj.getString("villes") != null) {
      villes = obj.getLong("villes");
    }
    if (!Objects.equals(obj.getString("gouvernorat"), "") && obj.getString("gouvernorat") != null) {
      gouvernorat = obj.getLong("gouvernorat");
    }
    if (!Objects.equals(obj.getString("cin"), "") && obj.getString("cin") != null) {
      cin = obj.getString("cin").trim().toLowerCase();
    }
    if (!Objects.equals(obj.getString("tel"), "") && obj.getString("tel") != null) {
      tel = obj.getLong("tel");
    }
    if (!Objects.equals(obj.getString("refChif"), "") && obj.getString("refChif") != null) {
      refChif = obj.getString("refChif").trim().toLowerCase();
    }
    if (!Objects.equals(obj.getString("refTT"), "") && obj.getString("refTT") != null) {
      refTT = obj.getString("refTT").trim().toLowerCase();
    }
    if (!Objects.equals(obj.getString("professions"), "") && obj.getString("professions") != null) {
      professions = obj.getLong("professions");
    }
    if (!Objects.equals(obj.getString("categories"), "") && obj.getString("categories") != null) {
      categories = obj.getLong("categories");
    }
    if (!Objects.equals(obj.getString("produit"), "") && obj.getString("produit") != null) {
      produit = obj.getLong("produit");
    }
    if (!Objects.equals(obj.getString("datedebut"), "") && obj.getString("datedebut") != null) {
      datedebut = CrmUtils.convertStringToDate(obj.getString("datedebut"));
    }
    if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
      datefin = CrmUtils.convertStringToLocalDateTime(obj.getString("datefin"));
    }
    if (!Objects.equals(obj.getString("datedebutMiseService"), "")
        && obj.getString("datedebutMiseService") != null) {
      datedebutMiseService = CrmUtils.convertStringToDate(obj.getString("datedebutMiseService"));
    }
    if (!Objects.equals(obj.getString("datefinMiseService"), "")
        && obj.getString("datefinMiseService") != null) {
      datefinMiseService =
          CrmUtils.convertStringToLocalDateTime(obj.getString("datefinMiseService"));
    }
    if (!Objects.equals(obj.getString("dateDebutModification"), "")
        && obj.getString("dateDebutModification") != null) {
      dateDebutModification = CrmUtils.convertStringToDate(obj.getString("dateDebutModification"));
    }
    if (!Objects.equals(obj.getString("dateFinModification"), "")
        && obj.getString("dateFinModification") != null) {
      dateFinModification =
          CrmUtils.convertStringToLocalDateTime(obj.getString("dateFinModification"));
    }
    if (!Objects.equals(obj.getString("AffecterTo"), "") && obj.getString("AffecterTo") != null) {
      AffecterTo = obj.getLong("AffecterTo");

    }

    if (!Objects.equals(obj.getString("CreePar"), "") && obj.getString("CreePar") != null) {
      CreePar = obj.getLong("CreePar");

    }

    if (!Objects.equals(obj.getString("classification"), "")
        && obj.getString("classification") != null) {
      Classification = obj.getString("classification");

    }
    if (!Objects.equals(obj.get("motifInstance"), "")
        && obj.getJSONArray("motifInstance") != null) {
      motifInstance = obj.getJSONArray("motifInstance");
    }
    if (!Objects.equals(obj.get("agentBO"), "") && obj.getString("agentBO") != null) {
      if (obj.getString("agentBO").equals("-10")) {
        agentId = 0; // Ensure this is an Integer, not Long
      } else {
        agentId = Long.valueOf(obj.getLong("agentBO")).intValue(); // Convert to Integer
      }
    }
    if (!Objects.equals(obj.getString("source"), "") && obj.getString("source") != null) {
      source = obj.getString("source");
    }
    if (!Objects.equals(obj.get("typeAbonnment"), "") && obj.getString("typeAbonnment") != null) {

  	  typeAbonnement = obj.getString("typeAbonnment");

      }
    List<Long> listesdesIdsfromrequest = new ArrayList<>();

    // ROLE_ADMINISTRATEUR || ROLE_POS
    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {

      List<String> arrayOfStrings = new ArrayList<>();
      String motifInstanceString = null;
      if (motifInstance != null && motifInstance.length() > 0) {
        for (int i = 0; i < motifInstance.length(); i++) {
          arrayOfStrings.add(motifInstance.getString(i).toString());

          // arrayOfStrings[i] = motifInstance.getString(i).toString();
        }
        motifInstanceString = String.valueOf(motifInstance.length());
      } else {
        arrayOfStrings = null;


      }
      listesdesIdsfromrequest = demandeAbonnementRepository.findAllToExportAdmin(prenom, nom,
          statutListfiltre, statutTTListfiltre, villes, gouvernorat, cin, tel, refChif, refTT,
          professions, datedebut, datefin, dateDebutModification, dateFinModification, categories,
          produit, AffecterTo, CreePar, Classification, arrayOfStrings, motifInstanceString,
          datedebutMiseService, datefinMiseService, agentId, source , typeAbonnement);

      // ajouter 0 à la fin d'une liste pour checked button checkbox 'select-all' dans front lorsque
      // 0 existe
      // listesdesIds.add((long) 0);

    } // ROLE_REVENDEUR
    else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_RETAIL")) {
      listesdesIdsfromrequest = demandeAbonnementRepository.findAllToExportRevendeur(prenom, nom,
          statutListfiltre, statutTTListfiltre, villes, gouvernorat, cin, tel, refChif, refTT,
          professions, datedebut, datefin, dateDebutModification, dateFinModification,
          user.getRole().getRoleId(), user.getUserid(), categories, produit , typeAbonnement);
      // ajouter 0 à la fin d'une liste pour checked button checkbox 'select-all' dans front lorsque
      // 0 existe
      // listesdesIds.add((long) 0);

    } // ROLE_DISTRIBUTEUR
    else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
      listesdesIdsfromrequest = demandeAbonnementRepository.findAllToExportDistributeur(prenom, nom,
          statutListfiltre, statutTTListfiltre, villes, gouvernorat, cin, tel, refChif, refTT,
          professions, datedebut, datefin, dateDebutModification, dateFinModification,
          user.getUserid(), user.getUserid(), categories, produit, AffecterTo, CreePar , typeAbonnement);
      // ajouter 0 à la fin d'une liste pour checked button checkbox 'select-all' dans front lorsque
      // 0 existe
      // listesdesIds.add((long) 0);
    }
    List<Long> listesdesIds = new ArrayList<>();
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      listesdesIds.addAll(listesdesIdsfromrequest);
    } else {
      listesdesIds = (List<Long>) request.getSession().getAttribute("listedes_ids");
      LOGGER.info("listedes_ids selected  from request: " + listesdesIdsfromrequest);
      for (Long id : listesdesIdsfromrequest) {

        if (listesdesIds.contains(id) == false) {
          listesdesIds.add(id);
        }
      }
    }
    request.getSession().setAttribute("listedes_ids", listesdesIds);
    LOGGER.info("listedes_ids else: " + listesdesIds);
    return listesdesIds;
  }

  @Override
  public HashMap<String, Object> getAlAbonnement(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();

    final String REF_CHIFCO = "refChif";
    final String REF_TT = "refTT";
    final String CIN = "cin";
    final String PRENOM = "prenom";
    final String NOM = "nom";
    User user = userRepository.findUsersByEmail(currentUser);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    int currentpage = start / length;
    String refChif = null;
    String refTT = null;
    String cin = null;
    String prenom = null;
    String nom = null;
    Long tel = null;
    Long villes = null;
    Long gouvernorat = null;
    Long professions = null;
    Long categories = null;
    Long produit = null;
    Long statutListfiltre = null;
    String statutTTListfiltre = null;
    String datedebut = null;
    String datefin = null;
    String dateDebutModification = null;
    String dateFinModification = null;
    Long AffecterTo = null;
    Long CreePar = null;
    Integer agentId = null;
    String classification = null;
    String datedebutMiseService = null;
    String datefinMiseService = null;
    JSONArray motifInstance = null;
    String source = null;
    String typeAbonnement = null;

    String sort = "";

    switch (ordercolumnaram) {

      case 1:
        sort = "referenceChifco";
        break;
      case 2:
        sort = "referenceTT";

        break;
      case 3:
        sort = "etatTT";
        break;
      case 4:
        sort = "statut";
        break;
      case 5:
        sort = "cin";
        break;
      case 6:
        sort = "firstName";
        break;
      case 7:
        sort = "telFixe";

        break;
      case 8:
        sort = "pack";
        break;
      case 9:
        sort = "user";
        break;
      case 10:
        sort = "createdDate";
        break;
      default:
        sort = "createdDate";
    }

    if ((filterrecherche != null && !filterrecherche.equals(""))
        || (search != null && !search.equals(""))) {

      if (search != null && search != "") {
        refChif = search;
      }
      Boolean CheckFilterIfExiste = false;
      if (filterrecherche != null && !filterrecherche.equals("")) {
    //	  String json = new String(Base64.getDecoder().decode(filterrecherche));
    	// decode base64
    	  byte[] decodedBytes = Base64.getDecoder().decode(filterrecherche);

    	  // UTF-8 string
    	  String decodedJson = new String(decodedBytes, StandardCharsets.UTF_8);
        JSONObject obj = new JSONObject(decodedJson);
        CheckFilterIfExiste = this.checkFilterValue(obj);

        if (filterrecherche != null && !filterrecherche.equals("") && CheckFilterIfExiste) {

          if (!Objects.equals(obj.getString(REF_CHIFCO), "") && obj.getString(REF_CHIFCO) != null) {
            refChif = obj.getString(REF_CHIFCO).trim().toLowerCase();
          }
          if (!Objects.equals(obj.getString(REF_TT), "") && obj.getString(REF_TT) != null) {
            refTT = obj.getString(REF_TT).trim().toLowerCase();
          }
          if (!Objects.equals(obj.getString(CIN), "") && obj.getString(CIN) != null) {
            cin = obj.getString(CIN).trim().toLowerCase();
          }
          if (!Objects.equals(obj.getString(PRENOM), "") && obj.getString(PRENOM) != null) {
            prenom = obj.getString(PRENOM).trim();
          }
          if (!Objects.equals(obj.getString(NOM), "") && obj.getString(NOM) != null) {
            nom = obj.getString(NOM).trim().toLowerCase();
          }
          if (!Objects.equals(obj.getString("tel"), "") && obj.getString("tel") != null) {
            tel = obj.getLong("tel");
          }
          if (!Objects.equals(obj.getString("villes"), "") && obj.getString("villes") != null) {
            villes = obj.getLong("villes");
          }
          if (!Objects.equals(obj.getString("gouvernorat"), "")
              && obj.getString("gouvernorat") != null) {
            gouvernorat = obj.getLong("gouvernorat");
          }
          if (!Objects.equals(obj.getString("professions"), "")
              && obj.getString("professions") != null) {
            professions = obj.getLong("professions");
          }
          if (!Objects.equals(obj.getString("categories"), "")
              && obj.getString("categories") != null) {
            categories = obj.getLong("categories");
          }
          if (!Objects.equals(obj.getString("produit"), "") && obj.getString("produit") != null) {
            produit = obj.getLong("produit");
          }
          if (!Objects.equals(obj.getString("statutListfiltre"), "")
              && obj.getString("statutListfiltre") != null) {
            statutListfiltre = obj.getLong("statutListfiltre");
          }
          if (!Objects.equals(obj.getString("datedebut"), "")
              && obj.getString("datedebut") != null) {
            datedebut = obj.getString("datedebut") + "T00:00:00.000";
          }
          if (!Objects.equals(obj.getString("datedebutMiseService"), "")
              && obj.getString("datedebutMiseService") != null) {
            datedebutMiseService = obj.getString("datedebutMiseService") + "T00:00:00.000";
          }
          if (!Objects.equals(obj.getString("datefinMiseService"), "")
              && obj.getString("datefinMiseService") != null) {
            datefinMiseService = obj.getString("datefinMiseService") + "T23:59:59.999";
          }

          if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
            datefin = obj.getString("datefin") + "T23:59:59.999";
          }
          if (!Objects.equals(obj.getString("statutTTListfiltre"), "")
              && obj.getString("statutTTListfiltre") != null) {
            statutTTListfiltre = obj.getString("statutTTListfiltre");
          }

          if (!Objects.equals(obj.getString("dateDebutModification"), "")
              && obj.getString("dateDebutModification") != null) {
            dateDebutModification = obj.getString("dateDebutModification") + "T00:00:00.000";
          }
          if (!Objects.equals(obj.getString("dateFinModification"), "")
              && obj.getString("dateFinModification") != null) {
            dateFinModification = obj.getString("dateFinModification") + "T23:59:59.999";
          }

          if (!Objects.equals(obj.get("AffecterTo"), "") && obj.getString("AffecterTo") != null) {
            AffecterTo = obj.getLong("AffecterTo");
          }
          if (!Objects.equals(obj.get("CreePar"), "") && obj.getString("CreePar") != null) {
            CreePar = obj.getLong("CreePar");
          }
          if (!Objects.equals(obj.get("classification"), "")
              && obj.getString("classification") != null) {
            classification = obj.getString("classification");
          }

          if (!Objects.equals(obj.get("motifInstance"), "")
              && obj.getJSONArray("motifInstance") != null) {
            motifInstance = obj.getJSONArray("motifInstance");
          }
          if (!Objects.equals(obj.get("agentBO"), "") && obj.getString("agentBO") != null) {
            if (obj.getString("agentBO").equals("-10")) {
              agentId = 0; // Ensure this is an Integer, not Long
            } else {
              agentId = Long.valueOf(obj.getLong("agentBO")).intValue(); // Convert to Integer
            }
          }
          if (!Objects.equals(obj.get("source"), "") && obj.getString("source") != null) {

            source = obj.getString("source");

          }
          if (obj.has("typeAbonnment") && 
        		    !obj.isNull("typeAbonnment") && 
        		    !obj.getString("typeAbonnment").isEmpty()) {

        		    typeAbonnement = obj.getString("typeAbonnment");
        		}


        }
      }
    }

    Page<DemandeAbbonmentDataDTO> responseData = null;
    Page<DemandeAbbonmentDataDTOv2> responseData2 = null;
    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {

      if (StringsRole.contains("READ_ASSIGN_SUBSCRIPTION_AGENT")) {
        responseData2 = this.findAbonnementByPaginatedAdminv2(currentpage + 1, length, refChif,
            refTT, cin, prenom, nom, tel, villes, gouvernorat, professions, categories, produit,
            statutListfiltre, statutTTListfiltre, datedebut, datefin, dateDebutModification,
            dateFinModification, AffecterTo, CreePar, classification, motifInstance,
            datedebutMiseService, datefinMiseService, agentId, source,typeAbonnement, sort, orderdir);
      } else {
        responseData = this.findAbonnementByPaginatedAdmin(currentpage + 1, length, refChif, refTT,
            cin, prenom, nom, tel, villes, gouvernorat, professions, categories, produit,
            statutListfiltre, statutTTListfiltre, datedebut, datefin, dateDebutModification,
            dateFinModification, AffecterTo, CreePar, classification, motifInstance,
            datedebutMiseService, datefinMiseService, source,typeAbonnement, sort, orderdir);
      }
    }
    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_RETAIL")) {

      responseData = this.findPaginatedByRevendeurDTO(currentpage + 1, length, user.getUserid(),
          refChif, refTT, cin, prenom, nom, tel, villes, gouvernorat, professions, categories,
          produit, statutListfiltre, statutTTListfiltre, datedebut, datefin, dateDebutModification,
          dateFinModification, datedebutMiseService, datefinMiseService,typeAbonnement, sort, orderdir);

    }

    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {

      responseData = this.findPaginatedByDistributeurWithSort(currentpage + 1, length,
          user.getUserid(), user.getUserid(), refChif, refTT, cin, prenom, nom, tel, villes,
          gouvernorat, professions, categories, produit, statutListfiltre, statutTTListfiltre,
          datedebut, datefin, dateDebutModification, dateFinModification, CreePar, AffecterTo,
          datedebutMiseService, datefinMiseService,typeAbonnement, sort, orderdir);
    }
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    if (StringsRole.contains("READ_ASSIGN_SUBSCRIPTION_AGENT")) {
      myGreetings.put("data", responseData2.getContent());
      myGreetings.put("draw", draw);
      myGreetings.put("start", start);
      myGreetings.put("recordsTotal", responseData2.getTotalElements());
      myGreetings.put("recordsFiltered", responseData2.getTotalElements());
    } else {

      if (responseData != null)
        myGreetings.put("data", responseData.getContent());
      myGreetings.put("draw", draw);
      myGreetings.put("start", start);
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    return myGreetings;

  }

  private Page<DemandeAbbonmentDataDTO> findPaginatedByRevendeurDTO(int pageNo, int pageSize,
      Long userid, String refChif, String refTT, String cin, String prenom, String nom, Long tel,
      Long villes, Long gouvernorat, Long professions, Long categories, Long produit,
      Long statutListfiltre, String statutTTListfiltre, String datedebut, String datefin,
      String dateDebutModification, String dateFinModification, String datedebutMiseService,
      String datefinMiseService,String typeAbonnement, String sortvar, String sorttype) {
    Sort sort = Sort.by("modifieddate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }

    Date dateDebutModifications = null;
    Date dateFinModifications = null;
    Date datedebuts = null;
    Date datefins = null;
    Date dateDebutMiseService = null;
    Date dateFinMiseService = null;

    if (datedebutMiseService != null) {
      dateDebutMiseService = CrmUtils.convertedFilterRechercheDate(datedebutMiseService);
    }

    if (datefinMiseService != null) {
      dateFinMiseService = CrmUtils.convertedFilterRechercheDate(datefinMiseService);
    }
    if (datedebut != null) {
      datedebuts = CrmUtils.convertedFilterRechercheDate(datedebut);
    }

    if (datefin != null) {
      datefins = CrmUtils.convertedFilterRechercheDate(datefin);
    }
    if (dateDebutModification != null) {
      dateDebutModifications = CrmUtils.convertedFilterRechercheDate(dateDebutModification);
    }

    if (dateFinModification != null) {
      dateFinModifications = CrmUtils.convertedFilterRechercheDate(dateFinModification);
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.demandeAbonnementRepository
        .findDemandeAbonnementsByKeywordbyrevendeursearchparamsnotempty(refChif, refTT, cin, prenom,
            nom, tel, villes, gouvernorat, professions, categories, produit, statutListfiltre,
            statutTTListfiltre, datedebuts, datefins, dateDebutModifications, dateFinModifications,
            userid, dateDebutMiseService, dateFinMiseService,typeAbonnement, pageable);
  }

  private Page<DemandeAbbonmentDataDTO> findPaginatedByRevendeurSuiviDesDemandesTransfDTO(
      int pageNo, int pageSize, Long userid, String refChif, String refTT, String cin,
      String prenom, String nom, Long tel, Long villes, Long gouvernorat, Long professions,
      Long categories, Long produit, Long statutListfiltre, String statutTTListfiltre,
      String datedebut, String datefin, String dateDebutModification, String dateFinModification,
      String datedebutMiseService, String datefinMiseService, String sortvar, String sorttype) {
    Sort sort = Sort.by("modifieddate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }

    Date dateDebutModifications = null;
    Date dateFinModifications = null;
    Date datedebuts = null;
    Date datefins = null;
    Date dateDebutMiseService = null;
    Date dateFinMiseService = null;

    if (datedebut != null) {
      dateDebutMiseService = CrmUtils.convertedFilterRechercheDate(datedebutMiseService);
    }

    if (datefin != null) {
      dateFinMiseService = CrmUtils.convertedFilterRechercheDate(datefinMiseService);
    }
    if (datedebut != null) {
      datedebuts = CrmUtils.convertedFilterRechercheDate(datedebut);
    }

    if (datefin != null) {
      datefins = CrmUtils.convertedFilterRechercheDate(datefin);
    }
    if (dateDebutModification != null) {
      dateDebutModifications = CrmUtils.convertedFilterRechercheDate(dateDebutModification);
    }

    if (dateFinModification != null) {
      dateFinModifications = CrmUtils.convertedFilterRechercheDate(dateFinModification);
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.demandeAbonnementRepository
        .findByRevendeurSuiviDesDemandesTransfByKeywordbyrevendeursearchparamsnotempty(refChif,
            refTT, cin, prenom, nom, tel, villes, gouvernorat, professions, categories, produit,
            statutListfiltre, statutTTListfiltre, datedebuts, datefins, dateDebutModifications,
            dateFinModifications, userid, dateDebutMiseService, dateFinMiseService, pageable);
  }

  private Page<DemandeAbbonmentDataDTOv2> findAbonnementByPaginatedAdminv2(int pageNo, int pageSize,
      String refChif, String refTT, String cin, String prenom, String nom, Long tel, Long villes,
      Long gouvernorat, Long professions, Long categories, Long produit, Long statutListfiltre,
      String statutTTListfiltre, String datedebut, String datefin, String dateDebutModification,
      String dateFinModification, Long affecterTo, Long creePar, String classification,
      JSONArray motifInstance, String datedebutMiseService, String datefinMiseService,
      Integer agentId, String source,String typeAbonnement, String sortvar, String sorttype) {
    Sort sort = Sort.by("createdDate").descending();
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else {
      sort = Sort.by(sortvar).ascending();
    }
    Date dateDebutModifications = null;
    Date dateFinModifications = null;
    Date datedebuts = null;
    Date datefins = null;
    Date dateDebutMiseService = null;
    Date dateFinMiseService = null;
    if (datedebutMiseService != null) {
      dateDebutMiseService = CrmUtils.convertedFilterRechercheDate(datedebutMiseService);
    }
    if (datefinMiseService != null) {
      dateFinMiseService = CrmUtils.convertedFilterRechercheDate(datefinMiseService);
    }
    if (datedebut != null) {
      datedebuts = CrmUtils.convertedFilterRechercheDate(datedebut);
    }
    if (datefin != null) {
      datefins = CrmUtils.convertedFilterRechercheDate(datefin);
    }
    if (dateDebutModification != null) {
      dateDebutModifications = CrmUtils.convertedFilterRechercheDate(dateDebutModification);
    }

    if (dateFinModification != null) {
      dateFinModifications = CrmUtils.convertedFilterRechercheDate(dateFinModification);
    }
    List<String> arrayOfStrings = new ArrayList<>();
    String motifInstanceString = null;
    if (motifInstance != null && motifInstance.length() > 0) {
      for (int i = 0; i < motifInstance.length(); i++) {
        arrayOfStrings.add(motifInstance.getString(i).toString());
      }
      motifInstanceString = String.valueOf(motifInstance.length());
    } else {
      arrayOfStrings = null;
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.demandeAbonnementRepository.findDemandeAbonnementsByAdminv2(refChif, refTT, cin,
        prenom, nom, statutTTListfiltre, villes, gouvernorat, affecterTo, creePar, statutListfiltre,
        categories, produit, professions, datedebuts, datefins, dateDebutModifications,
        dateFinModifications, tel, classification, arrayOfStrings, motifInstanceString,
        dateDebutMiseService, dateFinMiseService, agentId, source,typeAbonnement, pageable);
  }

  private Page<DemandeAbbonmentDataDTO> findAbonnementByPaginatedAdmin(int pageNo, int pageSize,
      String refChif, String refTT, String cin, String prenom, String nom, Long tel, Long villes,
      Long gouvernorat, Long professions, Long categories, Long produit, Long statutListfiltre,
      String statutTTListfiltre, String datedebut, String datefin, String dateDebutModification,
      String dateFinModification, Long affecterTo, Long creePar, String classification,
      JSONArray motifInstance, String datedebutMiseService, String datefinMiseService,
      String source,String typeAbonnement, String sortvar, String sorttype) {
    // TODO Auto-generated method stub

    Sort sort = Sort.by("createdDate").descending();
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else {
      sort = Sort.by(sortvar).ascending();
    }

    Date dateDebutModifications = null;
    Date dateFinModifications = null;
    Date datedebuts = null;
    Date datefins = null;
    Date dateDebutMiseService = null;
    Date dateFinMiseService = null;
    if (datedebutMiseService != null) {
      dateDebutMiseService = CrmUtils.convertedFilterRechercheDate(datedebutMiseService);
    }
    if (datefinMiseService != null) {
      dateFinMiseService = CrmUtils.convertedFilterRechercheDate(datefinMiseService);
    }
    if (datedebut != null) {
      datedebuts = CrmUtils.convertedFilterRechercheDate(datedebut);
    }

    if (datefin != null) {
      datefins = CrmUtils.convertedFilterRechercheDate(datefin);
    }
    if (dateDebutModification != null) {
      dateDebutModifications = CrmUtils.convertedFilterRechercheDate(dateDebutModification);
    }

    if (dateFinModification != null) {
      dateFinModifications = CrmUtils.convertedFilterRechercheDate(dateFinModification);
    }
    // Convert JSONArray to array of strings
    List<String> arrayOfStrings = new ArrayList<>();
    String motifInstanceString = null;
    if (motifInstance != null && motifInstance.length() > 0) {
      for (int i = 0; i < motifInstance.length(); i++) {
        arrayOfStrings.add(motifInstance.getString(i).toString());

        // arrayOfStrings[i] = motifInstance.getString(i).toString();
      }
      motifInstanceString = String.valueOf(motifInstance.length());
    } else {
      arrayOfStrings = null;


    }

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.demandeAbonnementRepository.findDemandeAbonnementsByAdmin(refChif, refTT, cin,
        prenom, nom, statutTTListfiltre, villes, gouvernorat, affecterTo, creePar, statutListfiltre,
        categories, produit, professions, datedebuts, datefins, dateDebutModifications,
        dateFinModifications, tel, classification, arrayOfStrings, motifInstanceString,
        dateDebutMiseService, dateFinMiseService, source,typeAbonnement, pageable);
  }

  @Override
  public HashMap<String, Object> getfiltredStatusAbonnemnt(int draw, int start, int length,
      String search, int ordercolumnaram, String orderdir, String filterrecherche, Long status) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    String refChif = null;
    String refTT = null;
    String cin = null;
    String prenom = null;
    String nom = null;
    Long tel = null;
    String datedebut = null;
    String datefin = null;
    String statutTTListfiltre = null;
    Long villes = null;
    Long gouvernorat = null;
    Long professions = null;
    Long categories = null;
    Long produit = null;
    String dateDebutModification = null;
    String dateFinModification = null;
    Long AffecterTo = null;
    Long CreePar = null;
    String source = null;
    Long statutListfiltre = (long) status;
    if (search != null && search != "")
      refChif = search;
    int page = start / length;

    String sort = "";
    switch (ordercolumnaram) {

      case 0:
        sort = "referenceChifco";
        break;
      case 1:
        sort = "referenceTT";
        break;
      case 2:
        sort = "etatTT";
        break;
      case 3:
        sort = "statut";
        break;
      case 4:
        sort = "cin";
        break;
      case 5:
        sort = "firstName";
        break;
      case 6:
        sort = "telFixe";
        break;
      case 7:
        sort = "user";
        break;
      case 8:
        sort = "createdDate";
        break;

    }

    if (filterrecherche != null && filterrecherche != "") {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("refChif"), "") && obj.getString("refChif") != null) {
        refChif = obj.getString("refChif").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("refTT"), "") && obj.getString("refTT") != null) {
        refTT = obj.getString("refTT").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("cin"), "") && obj.getString("cin") != null) {
        cin = obj.getString("cin").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("prenom"), "") && obj.getString("prenom") != null) {
        prenom = obj.getString("prenom").trim();
      }
      if (!Objects.equals(obj.getString("nom"), "") && obj.getString("nom") != null) {
        nom = obj.getString("nom").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("tel"), "") && obj.getString("tel") != null) {
        tel = obj.getLong("tel");
      }
      if (!Objects.equals(obj.getString("villes"), "") && obj.getString("villes") != null) {
        villes = obj.getLong("villes");
      }
      if (!Objects.equals(obj.getString("gouvernorat"), "")
          && obj.getString("gouvernorat") != null) {
        gouvernorat = obj.getLong("gouvernorat");
      }
      if (!Objects.equals(obj.getString("professions"), "")
          && obj.getString("professions") != null) {
        professions = obj.getLong("professions");
      }
      if (!Objects.equals(obj.getString("categories"), "") && obj.getString("categories") != null) {
        categories = obj.getLong("categories");
      }
      if (!Objects.equals(obj.getString("produit"), "") && obj.getString("produit") != null) {
        produit = obj.getLong("produit");
      }
      if (!Objects.equals(obj.getString("datedebut"), "") && obj.getString("datedebut") != null) {
        datedebut = obj.getString("datedebut") + "T00:00:00.000";
      }
      if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
        datefin = obj.getString("datefin") + "T23:59:59.999";
      }
      if (obj.has("statutTTListfiltre") && !Objects.equals(obj.getString("statutTTListfiltre"), "")
          && obj.getString("statutTTListfiltre") != null) {
        statutTTListfiltre = obj.getString("statutTTListfiltre");
      }
      if (obj.has("dateDebutModification")
          && !Objects.equals(obj.getString("dateDebutModification"), "")
          && obj.getString("dateDebutModification") != null) {
        dateDebutModification = obj.getString("dateDebutModification") + "T00:00:00.000";
      }
      if (obj.has("dateFinModification")
          && !Objects.equals(obj.getString("dateFinModification"), "")
          && obj.getString("dateFinModification") != null) {
        dateFinModification = obj.getString("dateFinModification") + "T23:59:59.999";
      }
      if (!Objects.equals(obj.getString("source"), "") && obj.getString("source") != null) {
        source = obj.getString("source");
      }

    }

    Page<DemandeAbbonmentDataDTO> responseData = null;
    // Page<DemandeAbbonmentDataDTOv2> responseData2 = null;

    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    // ROLE_ADMINISTRATEUR || ROLE_POS
    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {

      responseData = this.findAbonnementByPaginatedAdmin(page + 1, length, refChif, refTT, cin,
          prenom, nom, tel, villes, gouvernorat, professions, categories, produit, statutListfiltre,
          statutTTListfiltre, datedebut, datefin, dateDebutModification, dateFinModification,
          AffecterTo, CreePar, null, null, null, null,null, source, sort, orderdir);
    }
    // ROLE_REVENDEUR
    else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_RETAIL")) {

      responseData = this.findPaginatedByRevendeurDTO(page + 1, length, user.getUserid(), refChif,
          refTT, cin, prenom, nom, tel, villes, gouvernorat, professions, categories, produit,
          statutListfiltre, statutTTListfiltre, datedebut, datefin, dateDebutModification,
          dateFinModification, null, null,null, sort, orderdir);
    }
    // ROLE_DISTRIBUTEUR
    else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {

      responseData = this.findPaginatedByDistributeurWithSort(page + 1, length, user.getUserid(),
          user.getUserid(), refChif, refTT, cin, prenom, nom, tel, villes, gouvernorat, professions,
          categories, produit, statutListfiltre, statutTTListfiltre, datedebut, datefin,
          dateDebutModification, dateFinModification, CreePar, AffecterTo, null, null,null, sort,
          orderdir);
    }
    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());

    return myGreetings;
  }

  @Override
  public String getdemandeAbonnementToImprimer(Long demandeId, Model model) {
    try {
      Long idconnected = null;
      Boolean isadmin = false;
      Boolean isDistributeur = false;
      Boolean canAffectedModemisadmin = false;
      Long IdFirstFactureIfExiste = null;
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        model.addAttribute("userphoto", user.getPhoto());
        model.addAttribute("userrole", user.getRole().getRoleName());
        model.addAttribute("userwithstock", user.getWithStock());
        model.addAttribute("useremail", user.getEmail());
        idconnected = user.getUserid();
        // role = user.getRole().getRoleName();
        List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
        isadmin = StringsRole.contains("SEARCH_SUBSCRIPTION_REQUEST_ALL");
        isDistributeur = demandeAbonnementRepository.findDemandeAbonnementAffectedToUser(demandeId,
            user.getUserid());
        canAffectedModemisadmin = StringsRole.contains("AFFECTED_MODEM_BEFORE_PAYMENT");
        isDistributeur = demandeAbonnementRepository.findDemandeAbonnementAffectedToUser(demandeId,
            user.getUserid());
      }
      DemandeAbonnement demandeAbonnement =
          demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);

      Double montantFactureNonPayee = 0.0;
      if (demandeAbonnement != null) {
        Abonnement Abonnement = abonnementRepository
            .findAbonnementByReferenceClient(demandeAbonnement.getReferenceChifco());
        if (Abonnement != null) {
          IdFirstFactureIfExiste =
              factureRepository.getFirstFactureIdNotPayed(Abonnement.getClientid());

          montantFactureNonPayee = CrmUtils
              .formatDoubleInput(factureRepository.getSumFactureNonPayee(Abonnement.getClientid()));
          Double montantAvoirNonPayee =
              avoirRepository.getSumallAvoirNonPayeeByClient(Abonnement.getClientid());
          montantFactureNonPayee = montantFactureNonPayee - montantAvoirNonPayee;
        }
      }
      List<Statut> liststatus = new ArrayList<>();
      if (demandeAbonnement != null) {
        List<Object> combinedList = new ArrayList<>();
        Long clientid =
            AbonnementService.findIdClientByIdDemandeAbonnment(demandeAbonnement.getDemandeId());
        if (clientid != null) {
          List<Facture> factures = factureService.getAllFacturesByClient(clientid);
          List<AvoirClient> avoirClient = avoirService.getAllAvoirByClient(clientid);
          combinedList.addAll(factures);
          combinedList.addAll(avoirClient);
        }
        if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.SIGNED_DOC)) {
          Statut statutAnnuler = statutRepository.findStatutByNomStatut(NomStatutChifco.CANCELED);
          Statut statutevoieTT = statutRepository.findStatutByNomStatut(NomStatutChifco.WAIT_TT);
          Statut statutInfaisable =
              statutRepository.findStatutByNomStatut(NomStatutChifco.SAISIE_INFAISABLE);
          liststatus.add(statutevoieTT);
          liststatus.add(statutAnnuler);
          liststatus.add(statutInfaisable);

        } else if (demandeAbonnement.getStatut().getNomStatut().equals(
            NomStatutChifco.CLIENT_INJOIGNABLE) && demandeAbonnement.getDemandePdf() != null) {
          Statut statutAnnuler = statutRepository.findStatutByNomStatut(NomStatutChifco.CANCELED);

          Statut SIGNED_DOC = statutRepository.findStatutByNomStatut(NomStatutChifco.SIGNED_DOC);

          liststatus.add(statutAnnuler);
          liststatus.add(SIGNED_DOC);

        } else if (demandeAbonnement.getStatut().getNomStatut().equals(
            NomStatutChifco.CLIENT_INJOIGNABLE) && demandeAbonnement.getDemandePdf() == null) {
          Statut statutAnnuler = statutRepository.findStatutByNomStatut(NomStatutChifco.CANCELED);

          Statut statutdraft = statutRepository.findStatutByNomStatut(NomStatutChifco.DRAFT);

          liststatus.add(statutAnnuler);
          liststatus.add(statutdraft);

        } else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.DRAFT)) {
          Statut statutAnnuler = statutRepository.findStatutByNomStatut(NomStatutChifco.CANCELED);
          Statut statutInfaisable =
              statutRepository.findStatutByNomStatut(NomStatutChifco.SAISIE_INFAISABLE);
          liststatus.add(statutAnnuler);
          liststatus.add(statutInfaisable);

        } else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.WAIT_TT)
            && demandeAbonnement.getEtatTT().equals(DBEtatTT.ConstructionLigne)) {
          Statut statutAnnuler = statutRepository.findStatutByNomStatut(NomStatutChifco.CANCELED);
          Statut statutvaliderTT =
              statutRepository.findStatutByNomStatut(NomStatutChifco.INSTALLED);
          Statut statutRefuseTT = statutRepository.findStatutByNomStatut(NomStatutChifco.REFUSED);
          // Statut statutInfaisable =
          // statutRepository.findStatutByNomStatut(NomStatutChifco.SAISIE_INFAISABLE);
          liststatus.add(statutAnnuler);
          liststatus.add(statutvaliderTT);
          liststatus.add(statutRefuseTT);
          // liststatus.add(statutInfaisable);

        }

        else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.INSTALLED)) {


          Abonnement abonnementValider = AbonnementService
              .findAbonnementByReferenceClient(demandeAbonnement.getReferenceChifco());
          if (abonnementValider != null) {
            Facture firstfacture =
                factureService.findFirstByAbonnement_clientid(abonnementValider.getClientid());
            model.addAttribute("factureId", firstfacture.getFactureId());
            if (canAffectedModemisadmin) {
              Statut Modemaffetcter =
                  statutRepository.findStatutByNomStatut(NomStatutChifco.ASSIGNED);
              liststatus.add(Modemaffetcter);
            }
          }


        }

        else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.POROFORMA)) {


          Statut Modemaffetcter = statutRepository.findStatutByNomStatut(NomStatutChifco.ASSIGNED);
          liststatus.add(Modemaffetcter);

        }



        else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.ASSIGNED)) {
          Statut Modemvalider = statutRepository.findStatutByNomStatut(NomStatutChifco.VALID);
          liststatus.add(Modemvalider);

        } else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.ACTIVE)) {
          Statut nonpayer = statutRepository.findStatutByNomStatut(NomStatutChifco.UNPAID);
          liststatus.add(nonpayer);

        } else if (demandeAbonnement.getStatut().getNomStatut().equals(NomStatutChifco.VALID)) {


        }

        List<DemandeAbonnementHistory> DemandeAbonnementHistoryList = AbonnementHistoriqueservice
            .findDemandeAbonnementHistoryByCin(demandeAbonnement.getCin());
        User AssignedToUser = null;
        if (demandeAbonnement.getAssignedTo() != null) {
          AssignedToUser = demandeAbonnement.getAssignedTo(); // userRepository.getById(demandeAbonnement.getAssignedTo());
        }
        User duplicatedUser = null;
        if (demandeAbonnement.getEditedBy() != null) {
          duplicatedUser = UserService.findUsersByIduser(demandeAbonnement.getEditedBy()); // userRepository.getById(demandeAbonnement.getAssignedTo());
        }
        if (demandeAbonnement.getDecisionDemande() != null) {
          ClassificationDemande classification = demandeAbonnement.getDecisionDemande();

          model.addAttribute("classification", classification);
        } else {
          model.addAttribute("classification", null);
        }
        model.addAttribute("historylist", DemandeAbonnementHistoryList);
        model.addAttribute("statut", liststatus);
        model.addAttribute("demande", demandeAbonnement);
        model.addAttribute("AssignedToUser", AssignedToUser);
        model.addAttribute("DuplicatedUser", duplicatedUser);
        model.addAttribute("idconnected", idconnected);
        model.addAttribute("isadmin", isadmin);

        model.addAttribute("isDistribiteur", isDistributeur);
        model.addAttribute("montantFactureNonPayee",
            CrmUtils.formatDoubleInputToString(montantFactureNonPayee));
        model.addAttribute("DBEtatTT", DBEtatTT.dbEtatTT);
        model.addAttribute("IdFirstFactureIfExiste", IdFirstFactureIfExiste);
        model.addAttribute("factures", combinedList);
        // LOGGER.info("getdemandeAbonnementToImprimer : demande: " + demandeAbonnement);
        // LOGGER.info("getdemandeAbonnementToImprimer : liststatus: " + liststatus);
      } else {
        model.addAttribute("message", "demande abonnement non existant");
      }
    } catch (Exception e) {
      LOGGER.error("get info demande abonnement:" + e.getMessage());
    }
    return "demandeabonnement/getdemandeabonnementtoimprimer";

  }

  @Override
  public String getDemandeAbonnement(Long demandeId, Model model) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      setInfoUserTomodel(model, user);
    }
    DemandeAbonnement demandeAbonnement =
        demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
    model.addAttribute("demande", demandeAbonnement);
    model.addAttribute("statut", NomStatutChifco.dbStatutChifco);
    return "demandeabonnement/getdemandeabonnement";
  }

  @Override
  public JsonResponseBody removeFiled(Long id, HttpServletRequest request) {
    LOGGER.info("removefiled from session attribut id: " + id);
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      LOGGER.info("liste des ids est vide ");
      List<Long> listesdesIds = new ArrayList<>();
      JsonResponseBody jrb = new JsonResponseBody();
      jrb.setCode(String.valueOf(200));
      jrb.setMsg("liste des ids est vide");
      jrb.setResult(listesdesIds);
      return jrb;
    } else {
      List<Long> listedesIds = (List<Long>) request.getSession().getAttribute("listedes_ids");
      int pos = listedesIds.indexOf(id);
      listedesIds.remove(pos);
      LOGGER.info("nouvelle liste session attribut des id: " + listedesIds);
      request.getSession().setAttribute("listedes_ids", listedesIds);

      JsonResponseBody jrb1 = new JsonResponseBody();
      jrb1.setCode(String.valueOf(200));
      jrb1.setMsg("Suppression de l'id de liste avec succes");
      jrb1.setResult(listedesIds);
      return jrb1;
    }
  }

  @Override
  public JsonResponseBody removeAllFromListExport(HttpServletRequest request) {
    Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
    LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
    if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
      LOGGER.info("liste des ids est vide ");
      List<Long> listesdesIds = new ArrayList<>();
      JsonResponseBody jrb = new JsonResponseBody();
      jrb.setCode(String.valueOf(200));
      jrb.setMsg("liste des ids est vide");
      jrb.setResult(listesdesIds);
      return jrb;
    } else {
      List<Long> listedesIds = (List<Long>) request.getSession().getAttribute("listedes_ids");
      listedesIds.clear();
      LOGGER.info("nouvelle liste session attribut des id: " + listedesIds);
      request.getSession().setAttribute("listedes_ids", listedesIds);
      JsonResponseBody jrb1 = new JsonResponseBody();
      jrb1.setCode(String.valueOf(200));
      jrb1.setMsg("Vider liste avec succes");
      jrb1.setResult(listedesIds);
      return jrb1;
    }
  }

  @Override
  public void alldemandesNonSigneer(Model model, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      setInfoUserTomodel(model, user);

      Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
      LOGGER.info(
          "alldemandesNonSigneer : checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
      if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
        String[] listesvides = {};
        model.addAttribute("listedes_ids", listesvides);
      } else {
        Object listedesIds = request.getSession().getAttribute("listedes_ids");
        model.addAttribute("listedes_ids", listedesIds);
      }
      List<Gouvernorat> listvilles = gouvernoratRepository.findAll();
      List<Profession> listprofessions = professionRepository.findByisActive(true);
      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("professions", listprofessions);
      model.addAttribute("villes", listvilles);
      model.addAttribute("categories", categorieProduitInternets);
    }

  }

  @Override
  public String allDemandesValider(Model model, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
      LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
      if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
        String[] listesvides = {};
        model.addAttribute("listedes_ids", listesvides);
      } else {
        Object listedesIds = request.getSession().getAttribute("listedes_ids");
        model.addAttribute("listedes_ids", listedesIds);
      }
      List<Gouvernorat> listvilles = gouvernoratRepository.findAll();
      List<Profession> listprofessions = professionRepository.findByisActive(true);
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      setInfoUserTomodel(model, user);
      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      model.addAttribute("professions", listprofessions);
      model.addAttribute("villes", listvilles);
      model.addAttribute("categories", categorieProduitInternets);
    }
    return "demandeabonnement/alldemandesabonnementValider";
  }

  @Override
  public String allDemandesAbonnement(Integer pageNo, Model model, String keyword, Long villes,
      Long gouvernorat, Long professions, Long categories, Long produit, Long statutListfiltre,
      String datedebut, String datefin, Integer pageSize, HttpServletRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {

      LOGGER.info("method alldemandesabonnement");
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
      // privilege too change status
      model.addAttribute("UPDATE_SUBSCRIPTION_REQUEST_ADMIN",
          StringsRole.contains("UPDATE_SUBSCRIPTION_REQUEST_ADMIN"));

      // privilege pour changement classification
      model.addAttribute("CLASSIFICATION_APPLICATION_SUBSCRIPTION",
          StringsRole.contains("CLASSIFICATION_APPLICATION_SUBSCRIPTION"));
      // privilege assignement de demande d'abonnement
      model.addAttribute("ASSIGN_SUBSCRIPTION_AGENT",
          StringsRole.contains("ASSIGN_SUBSCRIPTION_AGENT"));
      model.addAttribute("READ_ASSIGN_SUBSCRIPTION_AGENT",
          StringsRole.contains("READ_ASSIGN_SUBSCRIPTION_AGENT"));

      // privilage to update demende
      Boolean hasediterole = StringsRole.contains("UPDATE_SUBSCRIPTION_REQUEST");
      model.addAttribute("hasediterole", hasediterole);
      // model.addAttribute("useremail", user.getEmail());

      Object checklisteDesIdsAExporter = request.getSession().getAttribute("listedes_ids");
      LOGGER.info("checkliste_des_ids_a_exporter: " + checklisteDesIdsAExporter);
      if (checklisteDesIdsAExporter == null || checklisteDesIdsAExporter.equals("")) {
        String[] listesvides = {};
        model.addAttribute("listedes_ids", listesvides);
      } else {
        Object listedesIds = request.getSession().getAttribute("listedes_ids");
        model.addAttribute("listedes_ids", listedesIds);
      }
      List<User> User = new ArrayList<User>();
      if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_ALL")) {
        User = userRepository.findUsersByTypeUserNotIn(
            Arrays.asList(UserTypeConstant.ADMINISTRATEUR, UserTypeConstant.SYSTEM));
      } else if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_AREA")) {
        User = userRepository.findUsersByAffectedTo(user.getUserid());
      }
      List<String> motifInstance = demandeAbonnementRepository.findMotifInstance();
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());

      model.addAttribute("useremail", user.getEmail());
      List<Gouvernorat> listGouvernorats = gouvernoratRepository.findAll();
      List<Profession> listprofessions = professionRepository.findByisActive(true);
      List<CategorieProduitInternet> categorieProduitInternets =
          categorieProduitInternetRepository.findAll();
      List<User> agents = userRepository.findUsersByPrivilegeName("LIST_AGENT_TO_ASIGNTO");// userRepository.findEnabledUsersByRole("ROLE_BO");
      model.addAttribute("agents", agents);
      model.addAttribute("professions", listprofessions);
      model.addAttribute("villes", listGouvernorats);
      model.addAttribute("AffectedTo", User);
      model.addAttribute("motifInstance", motifInstance);
      model.addAttribute("categories", categorieProduitInternets);

    }
    return "demandeabonnement/alldemandesabonnement";
  }

  @Override
  public List<DemandeAbonnement> rechercherFicherClient(String reftt, String refchifco,
      String numfixe, String numcin, RedirectAttributes redirectAttrs) {
    String refChif = null;
    String refTT = null;
    String cin = null;
    Long numerofixe = null;

    List<DemandeAbonnement> list = new ArrayList<>();
    if (refchifco.isEmpty() && reftt.isEmpty() && numcin.isEmpty() && numfixe.isEmpty()) {
      return list;
    }

    if (!refchifco.isEmpty()) {
      refChif = refchifco.trim();
    } else if (!reftt.isEmpty()) {
      refTT = reftt.trim();
    } else if (!numcin.isEmpty()) {

      cin = numcin.trim();

    } else if (!numfixe.isEmpty()) {

      numerofixe = Long.parseLong(numfixe.trim());

    }

    list = demandeAbonnementRepository.findoneDemandeAbonnementsbykeyword(refChif, cin, refTT,
        numerofixe);
    //

    /*
     * if (list.size() > 0) { return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" +
     * Idelement; }
     */
    /*
     * redirectAttrs.addFlashAttribute("message", "demade non existe"); return
     * "redirect:/demandeabonnement/recherchefichedemande?usernotfound=true";
     */
    return list;
  }

  @Override
  public String getAddDemandeAbonnement(Model model) {

    UserService.returnInfoUserConnected(model);
    List<Gouvernorat> villes = gouvernoratRepository.findAll();
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    List<Profession> professions = professionRepository.findByisActive(true);
    model.addAttribute("villes", villes);
    model.addAttribute("professions", professions);
    model.addAttribute("categories", categorieProduitInternets);
    return "demandeabonnement/adddemandeabonnement";
  }

  @Override
  public String getVerifClient(Model model) {
    UserService.returnInfoUserConnected(model);
    return "demandeabonnement/verificationcin";
  }

  @Override
  public String getUpdateDemandeAbonnement(Long demandeId, Model model) {

    UserService.returnInfoUserConnected(model);

    DemandeAbonnement demandeAbonnement =
        demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
    List<Gouvernorat> gouvernorats = gouvernoratRepository.findAll();
    List<Profession> professions = professionRepository.findByisActive(true);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = UserService.findUsersByEmail(currentUser);
    UserService.returnInfoUserConnected(model);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    List<Offre> offrePackInternets = null;
    if (StringsRole.contains("ADD_SUBSCRIPTION_REQUEST_ALL")) {
      offrePackInternets = offreService.findAllOffreExisteInPack();
    }

    else {
      offrePackInternets = offreService.findAllOffreExisteInPackByVisibility();
    }

    List<Ville> villes = villeRepository.findGouvernoratsByGouvernerat_GouvernoratId(
        demandeAbonnement.getGouvernorat().getGouvernoratId());

    List<Pack> packs =
        packService.getPackSByOffre_offreId(demandeAbonnement.getPack().getOffre().getOffreId());

    List<PostalCode> codePostaleList = codePostaleRepository
        .findPostalCodeByVille_VilleId(demandeAbonnement.getVille().getVilleId());

    List<Typepaiement> typepaiements = typePayRepository.findAll();

    model.addAttribute("datedenaissancess", demandeAbonnement.getDateNaissance());
    model.addAttribute("demande", demandeAbonnement);
    model.addAttribute("typepaiements", typepaiements);
    model.addAttribute("villes", villes);
    model.addAttribute("offres", offrePackInternets);
    model.addAttribute("gouvernorats", gouvernorats);

    model.addAttribute("packs", packs);

    model.addAttribute("codePostaleList", codePostaleList);

    model.addAttribute("professions", professions);
    return "demandeabonnement/editdemandeabonnement";
  }


  @Override
  public String updateDemandeAbonnement(Long demandeId, Gouvernorat gouvernorat,
      PostalCode postalCode, Profession profession, Ville ville, Offre offres,
      MultipartFile imageFile, MultipartFile imageFile2, String datedenaissancess,
      Typepaiement typepaiement, DemandeAbonnement demandeAbonnement, MultipartFile pdfcontrat,
      String situationFamiliale, Model model, RedirectAttributes redirectAttrs, Pack pack,
      Boolean houseHolder, Boolean hasBankCard, Boolean residence) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      setInfoUserTomodel(model, user);
      DemandeAbonnement demandeAbonnementtoedit =
          demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
      Abonnement clientToEdit = abonnementRepository.findAbonnementByClientid(demandeId);
      String oldcin = demandeAbonnementtoedit.getCin();
      demandeAbonnementtoedit.setCategorieProduitInternet(pack.getCategoriePack());
      demandeAbonnementtoedit.setPack(pack);
      if(pack.getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {
    	  demandeAbonnementtoedit.setTypeAbonnment(TypeAbonnment.Box);
      }
      else {
    	  demandeAbonnementtoedit.setTypeAbonnment(TypeAbonnment.Internet);
 
      }
      demandeAbonnementtoedit.setLastName(demandeAbonnement.getLastName());
      demandeAbonnementtoedit.setFirstName(demandeAbonnement.getFirstName());
      demandeAbonnementtoedit.setSituationFamiliale(situationFamiliale);
      demandeAbonnementtoedit.setHouseHolder(houseHolder);
      demandeAbonnementtoedit.setHasBankCard(hasBankCard);
      demandeAbonnementtoedit.setTelMobile2(demandeAbonnement.getTelMobile2());
      // LOGGER.info("xwx" + dbDemande.getFirstName());
      if (demandeAbonnement.getEmail() != null) {
        demandeAbonnementtoedit.setEmail(demandeAbonnement.getEmail());
      }


      if (!demandeAbonnementtoedit.getCin().equals(demandeAbonnement.getCin())) {
        AbonnementHistoriqueservice.updateCinHistory(demandeAbonnementtoedit.getCin(),
            demandeAbonnement.getCin());
        try {
          Path oldFolderPath = Paths.get(pathDemandesAbonnement + oldcin);
          Path newFolderPath = Paths.get(pathDemandesAbonnement + demandeAbonnement.getCin());
          Files.move(oldFolderPath, newFolderPath);
          LOGGER.info("Folder renamed successfully.");

        } catch (IOException e) {
          LOGGER.info("Failed to rename folder: " + e.getMessage());
        }
        demandeAbonnementtoedit.setCin(demandeAbonnement.getCin());


      }

      demandeAbonnementtoedit.setVille(ville);
      demandeAbonnementtoedit.setCodePostale(postalCode);
      if (!datedenaissancess.equals("")) {
        demandeAbonnementtoedit.setDateNaissance(CrmUtils.convertStringToDate(datedenaissancess));
      }

      if (demandeAbonnement.getReferenceTT() != null
          && !demandeAbonnement.getReferenceTT().equals("")
          && !demandeAbonnementtoedit.getReferenceTT().equals(demandeAbonnement.getReferenceTT())) {

        DemandeAbonnement VerifierReferenceTT = demandeAbonnementRepository
            .findDemandeAbonnementsByuniquereferencett(demandeAbonnement.getReferenceTT());
        if (VerifierReferenceTT == null) {
          demandeAbonnementtoedit.setReferenceTT(demandeAbonnement.getReferenceTT());
        } else {
          redirectAttrs.addFlashAttribute("reftt",
              "Reference (" + demandeAbonnement.getReferenceTT() + ") deja utilisé");
          return "redirect:/demandeabonnement/editabonnement/" + demandeAbonnement.getDemandeId();
        }
      }

      if (profession != null) {
        demandeAbonnementtoedit.setProfession(profession);
      }
      if (typepaiement != null &&  !demandeAbonnementtoedit.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {
        demandeAbonnementtoedit.setTypePaiement(typepaiement);
      }
      demandeAbonnementtoedit.setGouvernorat(gouvernorat);
      demandeAbonnementtoedit.setAdresse(demandeAbonnement.getAdresse());
      demandeAbonnementtoedit.setTelMobile(demandeAbonnement.getTelMobile());

      if (demandeAbonnement.getTelFixe() != null) {

        if (String.valueOf(demandeAbonnement.getTelFixe()).length() == 8
            && (String.valueOf(demandeAbonnement.getTelFixe()).startsWith("7"))) {

          if (demandeAbonnementtoedit.getStatut().getNomStatut().equals(NomStatutChifco.DRAFT)
              || demandeAbonnementtoedit.getStatut().getNomStatut()
                  .equals(NomStatutChifco.SIGNED_DOC)
              || demandeAbonnementtoedit.getStatut().getNomStatut()
                  .equals(NomStatutChifco.WAIT_TT)) {
            demandeAbonnementtoedit.setTelFixe(demandeAbonnement.getTelFixe());
            demandeAbonnementtoedit.setHasRaccordement(false);

          } else if (demandeAbonnementtoedit.getStatut().getNomStatut()
              .equals(NomStatutChifco.INSTALLED)) {
            demandeAbonnementtoedit.setTelFixe(demandeAbonnement.getTelFixe());
          }
        } else {
          redirectAttrs.addFlashAttribute("message", "numeroTelephoneNotConforme");
          return "redirect:/demandeabonnement/editabonnement/" + demandeAbonnement.getDemandeId();
        }

      } else {
        if ((demandeAbonnementtoedit.getStatut().getNomStatut().equals(NomStatutChifco.DRAFT)
            || demandeAbonnementtoedit.getStatut().getNomStatut().equals(NomStatutChifco.SIGNED_DOC)
            || demandeAbonnementtoedit.getStatut().getNomStatut().equals(NomStatutChifco.WAIT_TT))
            && demandeAbonnementtoedit.getTelFixe() == null
            && demandeAbonnement.getTelFixe() == null) {
          // payer raccordement par tranche
          demandeAbonnementtoedit.setHasRaccordement(true);
          demandeAbonnementtoedit
              .setNbFaisApayeReccardement(demandeAbonnement.getNbFaisApayeReccardement());

        } else if (demandeAbonnementtoedit.getStatut().getNomStatut()
            .equals(NomStatutChifco.WAIT_TT)) {
          demandeAbonnementtoedit.setHasRaccordement(true);
          if (demandeAbonnement.getNbFaisApayeReccardement() != null) {
            demandeAbonnementtoedit
                .setNbFaisApayeReccardement(demandeAbonnement.getNbFaisApayeReccardement());
          } else {
            demandeAbonnementtoedit.setNbFaisApayeReccardement(1);
          }
        }
        demandeAbonnementtoedit.setTelFixe(demandeAbonnement.getTelFixe());
      }
      if (demandeAbonnement.getFax() != null) {
        demandeAbonnementtoedit.setFax(demandeAbonnement.getFax());
      }
      if (demandeAbonnement.getTelMobile2() != null) {
        demandeAbonnementtoedit.setTelMobile2(demandeAbonnement.getTelMobile2());
      }
      if (demandeAbonnement.getPositionxy() != null) {
        demandeAbonnementtoedit.setPositionxy(demandeAbonnement.getPositionxy());
      }
      if (!imageFile.isEmpty()) {
        try {
          updateImage(imageFile, oldcin, demandeAbonnement.getCin(),
              demandeAbonnementtoedit.getPhotoCin1());
          demandeAbonnementtoedit
              .setPhotoCin1(CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
        } catch (Exception e) {

          LOGGER.error(
              "DemandeAbonnementServiceimpl.Updatedemandeabonnement Exception: " + e.getMessage());

        }
      }
      if (!imageFile2.isEmpty()) {
        try {
          updateImage(imageFile2, oldcin, demandeAbonnement.getCin(),
              demandeAbonnementtoedit.getPhotoCin2());
          demandeAbonnementtoedit
              .setPhotoCin2(CrmUtils.noSpecialCharacters(imageFile2.getOriginalFilename()));
        } catch (Exception e) {

          LOGGER.error(
              "DemandeAbonnementServiceimpl.Updatedemandeabonnement Exception: " + e.getMessage());
        }
      }
      if (pdfcontrat != null && !pdfcontrat.isEmpty()) {
        try {
          updateImage(pdfcontrat, oldcin, demandeAbonnement.getCin(),
              demandeAbonnement.getContratPdf());
          demandeAbonnementtoedit
              .setContratPdf(CrmUtils.noSpecialCharacters(pdfcontrat.getOriginalFilename()));
        } catch (Exception e) {

          LOGGER.error(
              "DemandeAbonnementServiceimpl.Updatedemandeabonnement Exception: " + e.getMessage());
        }
      }

      demandeAbonnementtoedit.setProprietaire(residence);
      AbonnementHistoriqueservice.insertNewEditedHistory(user, demandeAbonnementtoedit);
      demandeAbonnementRepository.save(demandeAbonnementtoedit);
      List<DemandeAbonnement> verififExiteOtherDemandeXithCin =
          demandeAbonnementRepository.findDemandeAbonnementByCin(oldcin);
      if (verififExiteOtherDemandeXithCin.size() > 0) {
        verififExiteOtherDemandeXithCin.forEach(el -> {
          el.setCin(demandeAbonnement.getCin());
          demandeAbonnementRepository.save(el);
        });
      }
      if (clientToEdit != null) {

        if (clientToEdit.getCin().equals(demandeAbonnement.getCin())) {
          clientToEdit.setCin(demandeAbonnement.getCin());
          ClientHistoryService.updateCinHistory(demandeAbonnement.getCin(), clientToEdit.getCin());
        }
        clientToEdit.setTelMobile2(demandeAbonnement.getTelMobile2());
        clientToEdit.setTelFixe(demandeAbonnement.getTelFixe());

        abonnementRepository.save(clientToEdit);
      }
      redirectAttrs.addFlashAttribute("message", "numserieexiste");

    }
    return "redirect:/demandeabonnement/alldemandesabonnement/" + 1 + "/" + 20;
  }

  public void updateImage(MultipartFile imageFile, String oldcin, String newcin, String oldimg)
      throws Exception {

    String newfolder = pathDemandesAbonnement + newcin + "/";
    LOGGER.info("oldcin: " + oldcin);
    LOGGER.info("newcin: " + newcin);
    File fileToDelete = new File(pathDemandesAbonnement + oldcin + "/" + oldimg);
    LOGGER.info("path to delete: " + fileToDelete);
    File uploadDir = new File(newfolder);
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }
    FileSystemUtils.deleteRecursively(fileToDelete);
    byte[] bytes = imageFile.getBytes();
    Path path =
        Paths.get(newfolder + CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));

    Files.write(path, bytes);
  }

  @Override
  public String getUrlDemandeAbonnementWithSteps(Model model) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = UserService.findUsersByEmail(currentUser);
    UserService.returnInfoUserConnected(model);
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    // privilege too change status
    List<Offre> offrePackInternets = null;
    if (StringsRole.contains("ADD_SUBSCRIPTION_REQUEST_ALL")) {
      offrePackInternets = offreService.findAllOffreExisteInPack();
    } else if (StringsRole.contains("ADD_SUBSCRIPTION_REQUEST_ByRevSelected")) {
      offrePackInternets = offreService.findAllOffreExisteInPackByRevSelectedAndVisibility();
    } else {
      offrePackInternets = offreService.findAllOffreExisteInPackByVisibility();
    }
    List<Gouvernorat> gouvernorats = gouvernoratRepository.findAll();
    List<Produit> ProduitInternets = produitRepository.findDistinctByIsExtraAndIsActive(true, true);
    List<Object[]> produitsAndTarifications = new ArrayList<>();

    for (Produit produit : ProduitInternets) {
      produitsAndTarifications.add(
          new Object[] {produit, tarificationRepository.findByProduitId(produit.getProduitId())});

    }

    List<Profession> professions = professionRepository.findByisActive(true);
    List<Typepaiement> typepaiements = typePaiementService.getalltypepaiements();
    model.addAttribute("subProduitList", ProduitInternets);
    model.addAttribute("produitsAndTarifications", produitsAndTarifications);
    model.addAttribute("gouvernorats", gouvernorats);
    model.addAttribute("professions", professions);
    model.addAttribute("offreService", offrePackInternets);
    model.addAttribute("typepaiements", typepaiements);
    return "demandeabonnement/adddemandeabonnementwithsteps";
  }

  @Override
  public Statut getStatusAbonnement(Long demandeId) {
    DemandeAbonnement demandeAbonnement =
        demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
    return demandeAbonnement.getStatut();
  }

  @Override
  public String verifClient(String cin, Model model) {

    UserService.returnInfoUserConnected(model);
    Abonnement clienttosearch = AbonnementService.findAbonnementByCin(cin);
    List<CategorieProduitInternet> categorieProduitInternets =
        categorieProduitInternetRepository.findAll();
    model.addAttribute("categories", categorieProduitInternets);
    if (clienttosearch != null) {
      DemandeAbonnement demandeAbonnement = getDemandeAbonnement(clienttosearch);
      model.addAttribute("demande", demandeAbonnement);
      return "demandeabonnement/addanotherdemandeabonnement";
    } else {
      List<Gouvernorat> villes = gouvernoratRepository.findAll();
      List<Profession> professions = professionRepository.findByisActive(true);
      model.addAttribute("villes", villes);
      model.addAttribute("professions", professions);
      return "demandeabonnement/adddemandeabonnement";
    }
  }

  private DemandeAbonnement getDemandeAbonnement(Abonnement clienttosearch) {
    DemandeAbonnement demandeAbonnement = new DemandeAbonnement();
    demandeAbonnement.setFirstName(clienttosearch.getFirstName());
    demandeAbonnement.setLastName(clienttosearch.getLastName());
    demandeAbonnement.setCin(clienttosearch.getCin());
    demandeAbonnement.setEmail(clienttosearch.getEmail());
    demandeAbonnement.setVille(clienttosearch.getVille());
    demandeAbonnement.setGouvernorat(clienttosearch.getGouvernorat());
    demandeAbonnement.setAdresse(clienttosearch.getAdresse());
    demandeAbonnement.setCodePostale(clienttosearch.getCodePostale());
    demandeAbonnement.setTelFixe(clienttosearch.getTelFixe());
    demandeAbonnement.setTelMobile(clienttosearch.getTelMobile());
    demandeAbonnement.setPhotoCin1(clienttosearch.getPhotoCin1());
    demandeAbonnement.setPhotoCin2(clienttosearch.getPhotoCin2());
    demandeAbonnement.setFax(clienttosearch.getFax());
    demandeAbonnement.setProfession(clienttosearch.getProfession());
    return demandeAbonnement;
  }

  @Override
  public String uploadAddReferenceAbonnementEnMasse(MultipartFile file,
      RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    int addedreference = 0;
    int notfound = 0;
    int countrefttexist = 0;
    int errorRow = 0;
    int successRow = 0;

    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      LOGGER.info("user: " + user);

      if (!file.isEmpty()) {
        String extension =
            file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (extension.equals("xlsx") || extension.equals("xls")) {
          try {

            Workbook workbook; // Declare XSSF WorkBook
            Sheet worksheet = null; // sheet can be used as common for XSSF and HSSF WorkBook

            if (extension.equalsIgnoreCase("xls")) {
              workbook = new HSSFWorkbook(file.getInputStream());

              worksheet = workbook.getSheetAt(0);
            } else if (extension.equals("xlsx")) {
              workbook = new XSSFWorkbook(file.getInputStream());
              worksheet = workbook.getSheetAt(0);
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();
            String folder = pathUploadXlsx;
            File uploadDir = new File(folder);
            if (!uploadDir.exists()) {
              uploadDir.mkdirs();
            }

            String namefile = "ChangerReference_" + dtf.format(now) + ".xlsx";
            Path path = Paths.get(pathUploadXlsx + namefile);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            // XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            // XSSFSheet worksheet = workbook.getSheetAt(0);
            LOGGER.info("worksheet: " + worksheet);
            if (worksheet.getRow(0) != null) {

              String cincheck = String.valueOf(worksheet.getRow(0).getCell(14));
              String referencecheck = String.valueOf(worksheet.getRow(0).getCell(0));

              LOGGER.info("worksheet: " + cincheck + referencecheck);
              if (Objects.equals(cincheck, "CIN")
                  && Objects.equals(referencecheck, "Réf. Demande")) {
                ImportXlsHistoryFile HistoryFileXls = new ImportXlsHistoryFile();
                ImportXlsHistoryFileRepository.save(HistoryFileXls);
                for (int index = 1; index < worksheet.getPhysicalNumberOfRows(); index++) {
                  Row row = worksheet.getRow(index);
                  if (row != null) {
                    String cin = "";
                    if (row.getCell(14) != null) {
                      DataFormatter formatter = new DataFormatter();
                      cin = formatter.formatCellValue(row.getCell(14));
                    }
                    DemandeAbonnement demandeAbonnement = demandeAbonnementRepository
                        .findDemandeAbonnementByCinAndStatusAvaibled(cin);
                    Statut statutinstalled = statutRepository.findStatutByStatutId(278L);

                    LOGGER.info("uploadaddreferenceabonnementenmasse cin: " + cin);
                    if (demandeAbonnement != null) {
                      LOGGER.info("demandeAbonnement not null");
                      if (demandeAbonnement.getStatut().getNomStatut()
                          .equals(NomStatutChifco.SIGNED_DOC)) {

                        String reference = "";
                        if (row.getCell(0) != null && row.getCell(14) != null) {
                          if (row.getCell(0) != null
                              && row.getCell(0).getCellType() == CellType.NUMERIC) {
                            notfound++;
                          } else {
                            LOGGER.info("type non demandeAbonnement not null");
                            LOGGER.info(row.getCell(0).getCellType());

                            reference = row.getCell(0).getStringCellValue();
                          }

                        }
                        String etat = row.getCell(8).getStringCellValue();
                        boolean StatutTTetude =
                            Arrays.stream(StatutTTConstants.statutttetude).anyMatch(etat::equals);
                        boolean StatutTTEnregister = Arrays
                            .stream(StatutTTConstants.statuttEnregister).anyMatch(etat::equals);
                        if (row.getCell(0) != null && !reference.equals("")) {

                          List<DemandeAbonnement> ChekifReferenceTTeXIST =
                              demandeAbonnementRepository
                                  .findDemandeAbonnementByreferenceTT(reference);
                          Boolean iscancled = false;

                          if (ChekifReferenceTTeXIST.size() == 0) {
                            LOGGER.info("ChekifReferenceTTeXIST" + ChekifReferenceTTeXIST);
                          } else {
                            int comparecancled = 0;
                            for (int indexref = 0; indexref < ChekifReferenceTTeXIST
                                .size(); indexref++) {
                              Statut demandewithrefrence =
                                  ChekifReferenceTTeXIST.get(indexref).getStatut();
                              if (demandewithrefrence.getStatutId().equals(283L)
                                  || demandewithrefrence.getStatutId().equals(284L)) {
                                comparecancled += 1;
                              }

                            }

                            if (comparecancled == ChekifReferenceTTeXIST.size()) {
                              iscancled = true;
                            }
                          }

                          if ((StatutTTetude == true
                              || (StatutTTetude == false && StatutTTEnregister == false))
                              && !Objects.equals(reference, "")
                              && (!Objects.equals(reference, "TT"))
                              && (ChekifReferenceTTeXIST.size() == 0
                                  || (ChekifReferenceTTeXIST.size() != 0 && iscancled == true))) {
                            demandeAbonnement.setReferenceTT(reference);
                            demandeAbonnement.setStatut(statutinstalled);
                            demandeAbonnement.setEtatTT(DBEtatTT.Etude);
                            AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
                            demandeAbonnementRepository.save(demandeAbonnement);
                            successRow += 1;
                            ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                demandeAbonnement.getStatut().getDesignation(),
                                "Référence TT est ajoutée avec succès", demandeAbonnement,
                                HistoryFileXls.getXlsHistoriqueFile());
                          } else if (StatutTTEnregister == true && !Objects.equals(reference, "")
                              && (!Objects.equals(reference, "TT"))
                              && (ChekifReferenceTTeXIST.size() == 0
                                  || (ChekifReferenceTTeXIST.size() != 0 && iscancled == true))) {
                            demandeAbonnement.setReferenceTT(reference);
                            demandeAbonnement.setStatut(statutinstalled);
                            demandeAbonnement.setEtatTT(DBEtatTT.Enregister);

                            demandeAbonnementRepository.save(demandeAbonnement);
                            AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
                            successRow += 1;
                            ImportXlsHistoryService.insertNewImportXlsHistory("Success",
                                demandeAbonnement.getStatut().getDesignation(),
                                "Référence TT est ajoutée avec succès", demandeAbonnement,
                                HistoryFileXls.getXlsHistoriqueFile());
                          }

                          else {
                            countrefttexist += 1;
                            errorRow += 1;
                            ImportXlsHistoryService.insertNewImportXlsHistory("Erreur",
                                demandeAbonnement.getStatut().getDesignation(),
                                "Cette référence TT concerne un abonnement différent.",
                                demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());
                          }

                        } else {
                          notfound += 1;
                          errorRow += 1;
                          ImportXlsHistoryService.insertNewImportXlsHistory("Erreur",
                              demandeAbonnement.getStatut().getDesignation(),
                              "Référence TT est vide.", demandeAbonnement,
                              HistoryFileXls.getXlsHistoriqueFile());
                        }
                        LOGGER.info("reference" + reference);

                      } else {
                        errorRow += 1;
                        ImportXlsHistoryService.insertNewImportXlsHistory("Erreur",
                            demandeAbonnement.getStatut().getDesignation(),
                            "Pour ajouter une référence TT, la demande d'abonnement doit être sauvegardée et signée.",
                            demandeAbonnement, HistoryFileXls.getXlsHistoriqueFile());
                      }

                    }

                  }

                }
                redirectAttrs.addFlashAttribute("message", "uploadaddreferenceabonnementenmasse");
                ImportXlsHistoryFileService.insertNewFileImportXlsHistory(
                    Integer.toString(errorRow), Integer.toString(successRow),
                    file.getOriginalFilename(), namefile,
                    Integer.toString(worksheet.getLastRowNum()), HistoryFileXls, user);
              } else {
                redirectAttrs.addFlashAttribute("message", "erreurreferenceabonnementenmasse");
              }
              if (notfound > 0) {
                redirectAttrs.addFlashAttribute("notfoundcalcul", notfound);
              }
              if (addedreference > 0) {
                redirectAttrs.addFlashAttribute("etudecalcul", addedreference);
              }
              if (countrefttexist > 0) {
                redirectAttrs.addFlashAttribute("anullercalcul", countrefttexist);
              }

            }

          } catch (IOException e) {
            LOGGER.error(
                "DemandeAbonnementServiceimpl.uploadaddreferenceabonnementenmasse IOException: "
                    + e.getMessage());

          }
        } else
          redirectAttrs.addFlashAttribute("message", "fileempty");
      }
    }
    return "redirect:/demandeabonnement/alldemandesabonnement/" + 1 + "/" + 20;
  }

  @Override
  public ModelAndView exportToExcel(HttpServletRequest request) {

    ModelAndView mav = new ModelAndView();
    List<DemandeAbbonementaAndAffectedToUserObjectDataDTO> list = new ArrayList<>();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    try {
      if (request.getSession().getAttribute("listedes_ids") != null) {
        if (!request.getSession().getAttribute("listedes_ids").equals("")) {
          List<Long> listedes_ids = (List<Long>) request.getSession().getAttribute("listedes_ids");

          // Supprimer l'element == 0 (dernier element dans la liste contient 0 si exporter toutes
          // les demandes abonnement)
          if (listedes_ids.get(listedes_ids.size() - 1) == 0) {
            listedes_ids.remove(listedes_ids.size() - 1);
          }
          for (Long id : listedes_ids) {
            DemandeAbbonementaAndAffectedToUserObjectDataDTO abonnement =
                demandeAbonnementRepository
                    .findDemandeAbonnementByDemandeIdLeftJoinDistribiteur(id);
            list.add(abonnement);
          }
          mav.setView(new DemandeAbonnementExcelExport());


          // send to excelImpl class
          mav.addObject("list", list);
          mav.addObject("user", user);
          // request.getSession().setAttribute("listedes_ids", "");
        }
      }


    } catch (Exception e) {

      LOGGER.error(
          "DemandeAbonnementServiceimpl.exportToExcel demande exportToExcel " + e.getMessage());
    }


    return mav;
  }

  @Override
  public String uploadFile(MultipartFile file, Long demandeId, String cin, MultipartFile imageFile,
      MultipartFile imageFile2, RedirectAttributes redirectAttrs, HttpServletResponse response) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    LOGGER.info("imageFile: " + imageFile);
    LOGGER.info("imageFile2: " + imageFile2);
    // check if file is empty

    try {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
      LocalDateTime now = LocalDateTime.now();
      String folder = pathDemandesAbonnement + cin + "/";
      File uploadDir = new File(folder);
      if (!uploadDir.exists()) {
        uploadDir.mkdirs();
      }

      DemandeAbonnement demandeAbonnement =
          demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
      if (file != null && file.isEmpty() && !demandeAbonnement.getIsSmsVerification()) {
        redirectAttrs.addFlashAttribute("message", "fileempty");
        return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + demandeId;
      } else if (file != null && !file.isEmpty() && !demandeAbonnement.getIsSmsVerification()) {
        String extension =
            file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        Path path = Paths.get(pathDemandesAbonnement + cin + "/"
            + PrefixDocument.NOMEFILE_DEMANDE_ABONNEMENT + cin + "_" + dtf.format(now) + ".pdf");
        if (!"pdf".equals(extension)) {
          path = Paths
              .get(pathDemandesAbonnement + cin + "/" + PrefixDocument.NOMEFILE_DEMANDE_ABONNEMENT
                  + cin + "_" + dtf.format(now) + "." + extension);
        }

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        if (!"pdf".equals(extension)) {
          demandeAbonnement.setDemandePdf(PrefixDocument.NOMEFILE_DEMANDE_ABONNEMENT + cin + "_"
              + dtf.format(now) + "." + extension);
        } else {
          demandeAbonnement.setDemandePdf(
              PrefixDocument.NOMEFILE_DEMANDE_ABONNEMENT + cin + "_" + dtf.format(now) + ".pdf");
        }
      }

      String chekexistecin1 = demandeAbonnement.getPhotoCin1();
      String chekexistecin2 = demandeAbonnement.getPhotoCin2();
      LOGGER.info("uploadFile demandeAbonnement chekexistecin1: " + chekexistecin1);
      LOGGER.info("uploadFile demandeAbonnement chekexistecin2: " + chekexistecin2);
      if ((chekexistecin1 == null && imageFile == null)
          || (chekexistecin2 == null && imageFile2 == null)) {
        LOGGER.info("dossier manquante: " + demandeAbonnement.getReferenceChifco());
        redirectAttrs.addFlashAttribute("message", "Dossier manquant");
        return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + demandeId
            + "?uplodsuccess=false";
      }

      // SIGNED_DOC,
      // id
      // =277
      String caractaireDestingtion = "";
      if (imageFile != null) {
        caractaireDestingtion = "recto";
        try {
          CrmUtils.saveImage(imageFile, cin, pathDemandesAbonnement, caractaireDestingtion);

          demandeAbonnement.setPhotoCin1(caractaireDestingtion
              + CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));

        } catch (Exception e) {

          LOGGER.error("DemandeAbonnementServiceimpl.uploadFile IOException: " + e.getMessage());
        }
      }
      if (imageFile2 != null) {
        try {
          caractaireDestingtion = "verso";
          CrmUtils.saveImage(imageFile2, cin, pathDemandesAbonnement, caractaireDestingtion);
          demandeAbonnement.setPhotoCin2(

              caractaireDestingtion
                  + CrmUtils.noSpecialCharacters(imageFile2.getOriginalFilename()));

        } catch (Exception e) {

          LOGGER.error("DemandeAbonnementServiceimpl.uploadFile IOException: " + e.getMessage());
        }
      }
      demandeAbonnement
          .setStatut(statutRepository.findStatutByNomStatut(NomStatutChifco.SIGNED_DOC));// statut
      demandeAbonnementRepository.save(demandeAbonnement);

      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
    } catch (IOException e) {

      LOGGER.error(" DemandeAbonnementServiceimpl.uploadFile IOException: " + e.getMessage());
    }

    redirectAttrs.addFlashAttribute("message", "uplod image success");

    return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/" + demandeId
        + "?uplodsuccess=true";
  }

  @Override
  public void downloadContrat(Long demandeId, HttpServletResponse response) {

    DemandeAbonnement demandeAbonnement =
        demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);

    File file = new File(pathDemandesAbonnement + demandeAbonnement.getCin() + "/"
        + demandeAbonnement.getContratPdf());

    if (file.exists()) {
      if (demandeAbonnement.getContratPdf() != null) {
        String extension = demandeAbonnement.getContratPdf()
            .substring(demandeAbonnement.getContratPdf().lastIndexOf(".") + 1);
        if ("pdf".equals(extension))
          response.setContentType("application/pdf");
        else
          response.setContentType("image/" + extension);
        response.addHeader("Content-Disposition",
            "attachment; filename=" + demandeAbonnement.getContratPdf());
      } else {
        response.setContentType("application/pdf");
      }
      try {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        FileCopyUtils.copy(inputStream, response.getOutputStream());
        response.getOutputStream().close();
      } catch (IOException ignored) {

        LOGGER.error(
            " DemandeAbonnementServiceimpl.downloadcontrat IOException: " + ignored.getMessage());
      }
    }
  }

  @Override
  public void downloadReport(Long demandeId, HttpServletResponse response) {

    DemandeAbonnement demandeAbonnement =
        demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
    if (demandeAbonnement.getDemandePdf() != null) {
      File file = new File(pathDemandesAbonnement + demandeAbonnement.getCin() + "/"
          + demandeAbonnement.getDemandePdf());

      if (file.exists()) {

        String extension = demandeAbonnement.getDemandePdf()
            .substring(demandeAbonnement.getDemandePdf().lastIndexOf(".") + 1);
        if ("pdf".equals(extension))
          response.setContentType("application/pdf");
        else
          response.setContentType("image/" + extension);

        response.addHeader("Content-Disposition",
            "attachment; filename=" + demandeAbonnement.getDemandePdf());
        try {
          InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
          FileCopyUtils.copy(inputStream, response.getOutputStream());
          response.getOutputStream().close();
        } catch (IOException ignored) {

          LOGGER.error(
              " DemandeAbonnementServiceimpl.downloadReport IOException: " + ignored.getMessage());
        }
      }
    }
  }

  @Override
  public String addDemandeAbonnementWithSteps(int raccordementtranche, String Nom, String Prenom,

      String Email, String cin, Gouvernorat gouvernorat, Long gouvernoratid, String adresse,

      PostalCode codepostale, Long telMobile, Long telMobile2, Long telFixe, Long fax,
      String positionxy, Profession profession, Typepaiement typepaiement,
      Long categorieProduitInternet, Long produitid, String datedenaissance, Boolean residence,
      MultipartFile imageFile, MultipartFile imageFile2, Long[] multipleSubproduct,
      String situationFamiliale, Boolean houseHolder, Boolean hasBankCard, String origin,
      Model model, RedirectAttributes redirectAttrs) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      setInfoUserTomodel(model, user);

      Ville ville = villeRepository.findVilleByVilleId(gouvernoratid);
      Pack pack = packService.findPackBypackId(produitid);
      List<EntryDemandeAbonnement> entryDemandeAbonnement = new ArrayList<EntryDemandeAbonnement>();
      if (multipleSubproduct != null) {
        for (int i = 0; i < multipleSubproduct.length; i++) {
          Produit findProduit = produitRepository.getById(multipleSubproduct[i]);
          EntryDemandeAbonnement newEntryDemandeAbonnement = new EntryDemandeAbonnement();
          newEntryDemandeAbonnement.setProduit(findProduit);
          entryDemandeAbonnementRepository.save(newEntryDemandeAbonnement);
          entryDemandeAbonnement.add(newEntryDemandeAbonnement);

        }

      } ;

      DemandeAbonnement demandeAbonnement = new DemandeAbonnement();
      if (entryDemandeAbonnement.size() > 0) {
        demandeAbonnement.setEntriesDemandeAbonnement(entryDemandeAbonnement);
      }

      demandeAbonnement.setFirstName(Prenom);
      demandeAbonnement.setLastName(Nom);
      demandeAbonnement.setProprietaire(residence);
      demandeAbonnement.setHasBankCard(hasBankCard);
      demandeAbonnement.setHouseHolder(houseHolder);
      demandeAbonnement.setPack(pack);
      demandeAbonnement.setSituationFamiliale(situationFamiliale);
      if (Email != null)
        demandeAbonnement.setEmail(Email);
      demandeAbonnement.setCin(cin);
      demandeAbonnement.setVille(ville);
      demandeAbonnement.setGouvernorat(gouvernorat);
      demandeAbonnement.setAdresse(adresse);
      demandeAbonnement.setCodePostale(codepostale);

      demandeAbonnement.setTelMobile(telMobile);

      if (profession != null)
        demandeAbonnement.setProfession(profession);
      if (telFixe != null) {
        demandeAbonnement.setTelFixe(telFixe);

        demandeAbonnement.setHasRaccordement(false);

      } else {
        // payer raccordement par tranche


        List<EntryPack> entyPackAbonnement = entryPackRepository.getEntryPackByPack(pack);
        entyPackAbonnement.forEach(entrypack -> {
          if (entrypack.getProduit().getProduitCode().equals("Raccordement")) {

            demandeAbonnement.setHasRaccordement(false);
          }
        });
        if ((demandeAbonnement.getPack().getPayLater() != null
            && demandeAbonnement.getPack().getPayLater()) || demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {
          demandeAbonnement.setNbFaisApayeReccardement(1);

        } else {
          demandeAbonnement.setNbFaisApayeReccardement(raccordementtranche);

        }
      }

      if (fax != null)
        demandeAbonnement.setFax(fax);
      if (telMobile2 != null)
        demandeAbonnement.setTelMobile2(telMobile2);
      if (positionxy != null)
        demandeAbonnement.setPositionxy(positionxy);
      demandeAbonnement.setUser(user);
      demandeAbonnement.setEditedBy(user.getUserid());
      demandeAbonnement.setAssignedTo(user);
      demandeAbonnement.setCategorieProduitInternet(pack.getCategoriePack());
      String caractaireDestingtion = "";
      if (!imageFile.isEmpty()) {
        try {
          caractaireDestingtion = "recto";
          CrmUtils.saveImage(imageFile, cin, pathDemandesAbonnement, caractaireDestingtion);
          demandeAbonnement

              .setPhotoCin1(caractaireDestingtion
                  + CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));

        } catch (Exception e) {

          LOGGER.error("DemandeAbonnementServiceimpl.adddemandeabonnementwithsteps Exception: "
              + e.getMessage());

        }
      }
      if (!imageFile2.isEmpty()) {
        try {
          caractaireDestingtion = "verso";
          CrmUtils.saveImage(imageFile2, cin, pathDemandesAbonnement, caractaireDestingtion);

          demandeAbonnement

              .setPhotoCin2(caractaireDestingtion
                  + CrmUtils.noSpecialCharacters(imageFile2.getOriginalFilename()));

        } catch (Exception e) {

          LOGGER.error("DemandeAbonnementServiceimpl.adddemandeabonnementwithsteps Exception: "
              + e.getMessage());

        }
      }

      if (!datedenaissance.isEmpty() || !datedenaissance.equals("")) {
        demandeAbonnement.setDateNaissance(CrmUtils.convertStringToDate(datedenaissance));
      }
      if (demandeAbonnement.getPack().getPayLater() != null
          && demandeAbonnement.getPack().getPayLater() || demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().equals("5G")) {
        Typepaiement oneMothTypePaiement =
            typePayRepository.findTypepaiementByreferenceTypePaiement("ref_1mois");
        demandeAbonnement.setTypePaiement(oneMothTypePaiement);


      } else {
        demandeAbonnement.setTypePaiement(typepaiement);

      }
      if(demandeAbonnement.getPack().getCategoriePack().getCategorieProduitInternetCode().equals("5G")) {
    	  demandeAbonnement.setTypeAbonnment(TypeAbonnment.Box);
      }
      else {
    	  demandeAbonnement.setTypeAbonnment(TypeAbonnment.Internet);

      }
      demandeAbonnement.setOrigin(origin);
      demandeAbonnement.setStatut(statutRepository.findStatutByNomStatut(NomStatutChifco.DRAFT)); // statut
                                                                                                  // draft
                                                                                                  // 276
      ClassificationDemande classificationEnAttent = classificationDemandeRepository
          .findClassificationDemandeByCodeClassification(ClassificationCode.DEnAttente);

      demandeAbonnement.setDecisionDemande(classificationEnAttent);

      DemandeAbonnement demandeAbonnement1 = demandeAbonnementRepository.save(demandeAbonnement);
      AbonnementHistoriqueservice.insertNewHistory(demandeAbonnement, user);
      redirectAttrs.addAttribute("id", demandeAbonnement1).addFlashAttribute("message",
          "nouvaux demande d'abonement");
      return "redirect:/demandeabonnement/getdemandeabonnementtoimprimer/"
          + demandeAbonnement1.getDemandeId() + "?news=true";

    } else {
      return "error/403";
    }
  }

  private void updateStatutAndSave(Statut statut, DemandeAbonnement demandeAbonnement) {
    demandeAbonnement.setStatut(statut);
    demandeAbonnementRepository.save(demandeAbonnement);
  }

  private void setInfoUserTomodel(Model model, User user) {
    model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
    model.addAttribute("userphoto", user.getPhoto());
    model.addAttribute("userrole", user.getRole().getRoleName());
    model.addAttribute("useremail", user.getEmail());
  }

  public void sendSmsConstructionLigne(ArrayList numerosTelephone, String referenceChifco) {
    Smstemplate findtemplatesms = templatesmsRepository.findSmstemplateByname("Constructionligne");
    String Template = findtemplatesms.getTemplate();
    String NewTemplate = Template.replace("{referencedemande}", referenceChifco);

    ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
    for (int i = 0; i < numerosTelephone.size(); i++) {
      Map<String, Object> Message = new HashMap<String, Object>();
      Message.put("number", numerosTelephone.get(i));
      Message.put("message", NewTemplate);
      smsToSend.add(Message);
    }

    notificationservice.sendsmsnotification(smsToSend);
  }

  public ArrayList<Map<String, Object>> sendSmsIfNonStock(String model, Long iduserCreted,
      ArrayList numerosTelephone, String referenceChifco, Boolean isMultiple) {
    List<Modem> modemdisponible = modemRepository.listmodemRevendeur(model, iduserCreted);
    ArrayList<Map<String, Object>> smsToSend = new ArrayList<Map<String, Object>>();
    if (modemdisponible.size() == 0) {

      Smstemplate findtemplatesmsStock =
          templatesmsRepository.findSmstemplateByname("TirageJarretirestockverification");
      String templatesmsStock = findtemplatesmsStock.getTemplate();
      String NewtemplatesmsStock = templatesmsStock.replace("{referencedemande}", referenceChifco);

      for (int i = 0; i < numerosTelephone.size(); i++) {
        Map<String, Object> Message = new HashMap<String, Object>();
        Message.put("number", numerosTelephone.get(i));
        Message.put("message", NewtemplatesmsStock);
        smsToSend.add(Message);
      }

    } else {
      Modem firstmodam = modemdisponible.get(0);

      firstmodam.setStatutReservation("reserver");
      modemRepository.save(firstmodam);

    }

    if (!isMultiple) {
      notificationservice.sendsmsnotification(smsToSend);
      smsToSend.clear();
    }
    return smsToSend;

  }

  public String generateLoginRadus(Abonnement getclient) {
    String login = "";
    if (getclient.getPack().getCategoriePack().getCategorieProduitInternetCode().equals("VDSL")
        && !getclient.getWithIpFix()) {
      login = "vdsl" + getclient.getCodeClient() + "@nety.net";
    } else if (getclient.getPack().getCategoriePack().getCategorieProduitInternetCode()
        .equals("VDSL") && getclient.getWithIpFix()) {
      login = "vdsl" + getclient.getCodeClient() + "@netyip.net";
    } else if (getclient.getPack().getCategoriePack().getCategorieProduitInternetCode()
        .equals("ADSL") && getclient.getWithIpFix()) {
      login = "adsl" + getclient.getCodeClient() + "@netyip.tn";
    } else if (getclient.getPack().getCategoriePack().getCategorieProduitInternetCode()
        .equals("ADSL") && !getclient.getWithIpFix()) {
      login = "adsl" + getclient.getCodeClient() + "@nety.tn";
    } else {
      login = "nety" + getclient.getCodeClient() + "@nety.tn";
    }
    return login;
  }

  public void addFixePhoneToAbonnement(Long telefixe, String Mobile5G, DemandeAbonnement demandeAbonnement,
      RedirectAttributes redirectAttrs) {
    if ((telefixe != null && !telefixe.equals("")) || (demandeAbonnement.getTelFixe() != null) || (Mobile5G != null && !Mobile5G.equals(""))) {
      if (telefixe != null && !telefixe.equals("") || (Mobile5G != null && !Mobile5G.equals(""))) {

        String telefixeString = String.valueOf(telefixe);
        boolean startsWithSeven = telefixeString.startsWith("7");
        Modem mynumSim = null;
       
        if ((String.valueOf(telefixe).length() == 8 && startsWithSeven) || (demandeAbonnement.getTypeAbonnment() != null &&  demandeAbonnement.getTypeAbonnment().equals(TypeAbonnment.Box) && Mobile5G != null && !Mobile5G.trim().isEmpty())  ) {
          DemandeAbonnement finddemande =
              demandeAbonnementRepository.findDemandeAbonnementByTelfixeAndStatusAvaibled(telefixe);
             if(Mobile5G != null && !Mobile5G.trim().isEmpty()) {
            	 mynumSim =    modemRepository.findByNumSerieOrEmail(Mobile5G , null);
            	 
            	 Long number = Long.valueOf(mynumSim.getEmail()); // retourne un objet Long

                      finddemande =
                         demandeAbonnementRepository.findDemandeAbonnementByTelfixeAndStatusAvaibled(number);
             }
          if (finddemande == null) {
        	 if(telefixe != null && !telefixe.equals("")) {
                 demandeAbonnement.setTelFixe(telefixe);

        	 }
        	 if(Mobile5G != null && !Mobile5G.equals("")) {
            	 Long number = Long.valueOf(mynumSim.getEmail()); // retourne un objet Long

                 demandeAbonnement.setTelFixe(number);

        	 }
            demandeAbonnementRepository.save(demandeAbonnement);
       
          } else {

            redirectAttrs.addFlashAttribute("message", "numeroTelephoneExiste");
          }
        } else {
          redirectAttrs.addFlashAttribute("message", "numeroTelephoneNotConforme");
        }
      }

    } else {
      redirectAttrs.addFlashAttribute("message", "numeroTelephoneObligatoir");
    }
  }

  @Override
  public String confirmeAbonnement(String confirmation, Long idabon, User user) {
    String etatTT = null;
    // TODO Auto-generated method stub

    switch (confirmation) {
      case "etude": {
        etatTT = DBEtatTT.Etude;

        break;
      }
      case "confirmationok": {
        etatTT = DBEtatTT.ConfirmationOK;

        break;
      }
      case "confirmationNonok": {
        etatTT = DBEtatTT.ConfirmationAnnuler;

        break;
      }
      case DBEtatTT.ConstructionLigne: {
        etatTT = DBEtatTT.ConstructionLigne;
        break;
      }
      case "constructionLigne": {
        etatTT = DBEtatTT.ConstructionLigne;
        break;
      }

      case DBEtatTT.Cancled: {
        etatTT = DBEtatTT.Cancled;
        break;
      }
      case DBEtatTT.Refused: {
        etatTT = DBEtatTT.Refused;
        break;
      }
      case DBEtatTT.Instance: {
        etatTT = DBEtatTT.Instance;
        break;
      }
      case DBEtatTT.Clôturée: {
        etatTT = DBEtatTT.Clôturée;
        break;
      }
    }
    if (etatTT != null) {
      DemandeAbonnement finddemande =
          demandeAbonnementRepository.findDemandeAbonnementByDemandeId(idabon);

      if (etatTT.equals(DBEtatTT.ConstructionLigne)
          && finddemande.getEtatTT().equals(DBEtatTT.ConfirmationOK)) {
        ArrayList<String> arrayTelephoneConstructionLigne = new ArrayList<>();
         if(!finddemande.getPack().getCategoriePack().getCategorieProduitInternetCode().equals(TypeAbonnment.Box)) {
        arrayTelephoneConstructionLigne.add(finddemande.getTelMobile().toString());
         }
        sendSmsConstructionLigne(arrayTelephoneConstructionLigne, finddemande.getReferenceChifco());
      }


      // historique de classification
      // AbonnementHistoriqueservice.saveNewHistorique(user, finddemande.getDemandeId(),
      // "La classification a été changée de '" + finddemande.getDecisionDemande().getValue()
      // + "' à '" + classificationRefus.getValue() + "'");

      // finddemande.setDateDecisionDemande(new Date());
      // finddemande.setDecisionDemande(classificationRefus);
      // }
      /*
       * if (etatTT.equals(DBEtatTT.Refused) && finddemande.getDecisionDemande() != null) {
       * ClassificationDemande classificationRefus = classificationDemandeRepository
       * .findClassificationDemandeByCodeClassification(ClassificationCode.DEnAttente);
       * 
       * // historique de classification AbonnementHistoriqueservice.saveNewHistorique(user,
       * finddemande.getDemandeId(), "La classification a été changée de '" +
       * finddemande.getDecisionDemande().getValue() + "' à '" + classificationRefus.getValue() +
       * "'");
       * 
       * finddemande.setDateDecisionDemande(new Date());
       * finddemande.setDecisionDemande(classificationRefus); }
       */

      finddemande.setEtatTT(etatTT);
      AbonnementHistoriqueservice.insertNewHistory(finddemande, user);
      demandeAbonnementRepository.save(finddemande);
    }
    return etatTT;
  }

  @Override
  public DemandeAbonnement findDemandeAbonnementByReferencechifco(String refchifco) {
    // TODO Auto-generated method stub
    return demandeAbonnementRepository.findDemandeAbonnementByReferenceChifco(refchifco);
  }

  @Override
  public DemandeAbonnement saveStatutDemande(DemandeAbonnement demandeAbonnementFactures) {
    return demandeAbonnementRepository.save(demandeAbonnementFactures);

  }

  @Override
  public List<DemandeAbonnement> findDemandeAbonnementsByCinAndStatusAvaibled(String Cin) {
    // TODO Auto-generated method stub
    return this.demandeAbonnementRepository.findDemandeAbonnementsByCinAndStatusAvaibled(Cin);
  }

  @Override
  public String verificationTelFixEdit(MultiValueMap<String, String> formData) {
    // TODO Auto-generated method stub
    if (formData.getFirst("telFixe") != null && !formData.getFirst("telFixe").isEmpty()
        && formData.getFirst("idDemandeAbonnement") != null
        && !formData.getFirst("idDemandeAbonnement").isEmpty()) {
      String telFixe = formData.getFirst("telFixe");
      DemandeAbonnement demandeAbonnement =
          demandeAbonnementRepository.findDemandeAbonnementByDemandeId(
              Long.parseLong(formData.getFirst("idDemandeAbonnement")));
      if (demandeAbonnement.getTelFixe() != null
          && demandeAbonnement.getTelFixe().equals(Long.parseLong(telFixe)))
        return "true";
      else
        return verificationTelFix(formData);

    } else
      return "true";

  }

  @Override
  public String smsVerification(Long demandeId, User user) {
    DemandeAbonnement demandeAbonnement =
        demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
    demandeAbonnement.setIsSmsVerification(true);
    DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
    demandeAbonnementHistory.setAdresse(demandeAbonnement.getAdresse());
    demandeAbonnementHistory.setCin(demandeAbonnement.getCin());
    demandeAbonnementHistory.setContratpdf(demandeAbonnement.getContratPdf());
    if (demandeAbonnement.getPhotoCin1() != null && demandeAbonnement.getPhotoCin2() != null)
      demandeAbonnementHistory
          .setDescription("Demande sauvegardée et signée avec une confirmation par code SMS."
              + demandeAbonnement.getTelMobile());
    else {
      demandeAbonnementHistory.setDescription(
          "La confirmation du client a été effectuée par un SMS envoyé à son numéro de téléphone suivant "
              + demandeAbonnement.getTelMobile());
    }
    demandeAbonnementHistory.setFirstName(demandeAbonnement.getFirstName());
    demandeAbonnementHistory.setLastName(demandeAbonnement.getLastName());
    demandeAbonnementHistory.setCreatedBy(user);
    demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);

    if (demandeAbonnement.getPhotoCin1() == null || demandeAbonnement.getPhotoCin2() == null) {
      demandeAbonnementRepository.save(demandeAbonnement);
      return "cin missing";
    } else {
      Statut singedStatut = statutService.findStatutByNomstatut(NomStatutChifco.SIGNED_DOC);
      updateStatutAndSave(singedStatut, demandeAbonnement);
      return "statut updated";

    }
  }

  @Override
  public DemandeAbonnement duplicatedDemande(Long demandeId, String cin, User user) {
    // TODO Auto-generated method stub
    List<DemandeAbonnement> chekIfExistingDemande =
        findDemandeAbonnementsByCinAndStatusAvaibled(cin.toString());
    if (chekIfExistingDemande.size() > 0) {
      return null;
    }

    else {
      DemandeAbonnement demandeAbonnement =
          demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);

      return demandeAbonnement;

    }

  }

  @Override
  public DemandeAbonnement getDemandeAbonnementBydemandeId(Long demandeId) {
    // TODO Auto-generated method stub
    return demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
  }

  @Override
  public Map<String, Object> saveDuplicatedDemande(MultipartFile cinRecto, MultipartFile cinVerso,
      String datedenaissancess, DemandeAbonnement demandeAbonnement, User user,
      Typepaiement typePaiement, Gouvernorat gouvernorat, Profession profession, Ville ville,
      PostalCode postalCode, Boolean residence, Boolean houseHolder, Boolean hasBankCard,
      Long telFixe, int raccordementtranche, Long idPack, Long[] multipleSubproduct) {
    Map<String, Object> validation = new HashMap<>();
    DemandeAbonnement mydemandeAbonnement = demandeAbonnementRepository
        .findDemandeAbonnementByDemandeId(demandeAbonnement.getDemandeId());
    DemandeAbonnement duplicatedDemande = new DemandeAbonnement();
    Statut singedStatut = statutService.findStatutByNomstatut(NomStatutChifco.SIGNED_DOC);
    duplicatedDemande.setStatut(singedStatut);
    duplicatedDemande.setFirstName(demandeAbonnement.getFirstName());
    duplicatedDemande.setLastName(demandeAbonnement.getLastName());
    duplicatedDemande.setEmail(demandeAbonnement.getEmail());
    duplicatedDemande.setCin(demandeAbonnement.getCin());
    duplicatedDemande.setAdresse(demandeAbonnement.getAdresse());
    duplicatedDemande.setGouvernorat(gouvernorat);
    duplicatedDemande.setCodePostale(postalCode);
    duplicatedDemande.setVille(ville);
    duplicatedDemande.setTelFixe(demandeAbonnement.getTelFixe());
    duplicatedDemande.setTelMobile(demandeAbonnement.getTelMobile());
    duplicatedDemande.setTelMobile2(demandeAbonnement.getTelMobile2());
    duplicatedDemande.setProprietaire(residence);
    duplicatedDemande.setTypePaiement(typePaiement);
    duplicatedDemande.setProfession(profession);
    duplicatedDemande.setHouseHolder(houseHolder);
    duplicatedDemande.setHasBankCard(hasBankCard);
    duplicatedDemande.setTypeAbonnment(demandeAbonnement.getTypeAbonnment());
    if (!cinRecto.isEmpty()) {
      try {
        updateImage(cinRecto, demandeAbonnement.getCin(), demandeAbonnement.getCin(),
            demandeAbonnement.getPhotoCin1());
        duplicatedDemande
            .setPhotoCin1(CrmUtils.noSpecialCharacters(cinRecto.getOriginalFilename()));
      } catch (Exception e) {

        LOGGER.error(
            "DemandeAbonnementServiceimpl.Updatedemandeabonnement Exception: " + e.getMessage());

      }
    } else {
      if (mydemandeAbonnement.getPhotoCin1() == null) {
        validation.put("erreurCin1", "Le champ 'cin recto' est obligatoire.");
      } else {
        duplicatedDemande.setPhotoCin1(mydemandeAbonnement.getPhotoCin1());
      }
    }
    if (!cinVerso.isEmpty()) {
      try {
        updateImage(cinVerso, demandeAbonnement.getCin(), demandeAbonnement.getCin(),
            demandeAbonnement.getPhotoCin2());
        duplicatedDemande
            .setPhotoCin2(CrmUtils.noSpecialCharacters(cinVerso.getOriginalFilename()));
      } catch (Exception e) {

        LOGGER.error(
            "DemandeAbonnementServiceimpl.Updatedemandeabonnement Exception: " + e.getMessage());

      }
    } else {
      if (mydemandeAbonnement.getPhotoCin1() == null) {
        validation.put("erreurCin2", "Le champ 'cin verso' est obligatoire.");
      } else {
        duplicatedDemande.setPhotoCin2(mydemandeAbonnement.getPhotoCin2());
      }
    }
    if (!validation.isEmpty()) {
      return validation;
    }
    ClassificationDemande DemandeenAttentClasification = classificationDemandeRepository
        .findClassificationDemandeByCodeClassification(ClassificationCode.DEnAttente);
    duplicatedDemande.setDecisionDemande(DemandeenAttentClasification);
    duplicatedDemande.setUser(mydemandeAbonnement.getUser());
    duplicatedDemande.setEditedBy(user.getUserid());
    duplicatedDemande.setAssignedTo(mydemandeAbonnement.getAssignedTo());
    duplicatedDemande.setCategorieProduitInternet(demandeAbonnement.getCategorieProduitInternet());
    duplicatedDemande.setFax(demandeAbonnement.getFax());
    duplicatedDemande.setPositionxy(demandeAbonnement.getPositionxy());
    if (datedenaissancess != null) {
      duplicatedDemande.setDateNaissance(CrmUtils.convertStringToDate(datedenaissancess.trim()));
    }
    if (telFixe != null) {
      duplicatedDemande.setTelFixe(telFixe);
      duplicatedDemande.setHasRaccordement(false);

    } else {
      // payer raccordement par tranche

      duplicatedDemande.setNbFaisApayeReccardement(raccordementtranche);
    }
    Pack packes = packService.findPackBypackId(idPack);
    if (packes != null) {
      duplicatedDemande.setCategorieProduitInternet(packes.getCategoriePack());
      duplicatedDemande.setPack(packes);
    }
    duplicatedDemande.setSituationFamiliale(demandeAbonnement.getSituationFamiliale());
    if (multipleSubproduct != null) {
      List<EntryDemandeAbonnement> entryDemandeAbonnement = new ArrayList<EntryDemandeAbonnement>();
      for (int i = 0; i < multipleSubproduct.length; i++) {
        Produit findProduit = produitRepository.getById(multipleSubproduct[i]);
        EntryDemandeAbonnement newEntryDemandeAbonnement = new EntryDemandeAbonnement();
        newEntryDemandeAbonnement.setProduit(findProduit);
        entryDemandeAbonnementRepository.save(newEntryDemandeAbonnement);
        entryDemandeAbonnement.add(newEntryDemandeAbonnement);

      }

      if (entryDemandeAbonnement.size() > 0) {
        duplicatedDemande.setEntriesDemandeAbonnement(entryDemandeAbonnement);
      }

    } ;
    duplicatedDemande.setDemandeAbonnementParents(mydemandeAbonnement.getDemandeId());
    duplicatedDemande.setIsSmsVerification(mydemandeAbonnement.getIsSmsVerification());


    demandeAbonnementRepository.save(duplicatedDemande);
    ClassificationDemande duplicationClasification = classificationDemandeRepository
        .findClassificationDemandeByCodeClassification(ClassificationCode.DDEMANDE);
    DemandeAbonnement demandeSuccess = new DemandeAbonnement();
    if (duplicationClasification != null) {
      mydemandeAbonnement.setDateDecisionDemande(new Date());
      mydemandeAbonnement.setDecisionDemande(duplicationClasification);
      demandeSuccess = demandeAbonnementRepository.save(mydemandeAbonnement);
    }

    DemandeAbonnementHistory demandeAbonnementHistory = new DemandeAbonnementHistory();
    demandeAbonnementHistory.setAdresse(duplicatedDemande.getAdresse());
    demandeAbonnementHistory.setCin(mydemandeAbonnement.getCin());
    demandeAbonnementHistory.setContratpdf(duplicatedDemande.getContratPdf());
    demandeAbonnementHistory.setDescription(
        "Duplication de la demande sous référence nety " + duplicatedDemande.getReferenceChifco());
    demandeAbonnementHistory.setFirstName(duplicatedDemande.getFirstName());
    demandeAbonnementHistory.setLastName(duplicatedDemande.getLastName());
    demandeAbonnementHistory.setCreatedBy(user);
    demandeAbonnementHistoryRepository.save(demandeAbonnementHistory);

    validation.put("success", demandeSuccess);
    return validation;
  }

  @Override
  public Boolean affectRevendeur(Long demandeId, String codeRevendeur, String emailRevendeur,
      String identificationFiscale) {
    // TODO Auto-generated method stub
    String CodeRevendeur = null;
    String EmailRevendeur = null;
    String IdentificationFiscale = null;
    if (!codeRevendeur.isEmpty()) {
      CodeRevendeur = codeRevendeur;
    }
    if (!emailRevendeur.isEmpty()) {
      EmailRevendeur = emailRevendeur;
    }
    if (!identificationFiscale.isEmpty()) {
      IdentificationFiscale = identificationFiscale;
    }
    List<User> ListeAbonement = userRepository.findUserByEmailOrEmailOrIdentification(CodeRevendeur,
        EmailRevendeur, IdentificationFiscale);
    if (ListeAbonement.size() > 1 || ListeAbonement.size() < 1) {
      return false;
    } else {
      DemandeAbonnement myDemandeAbonnement =
          demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
      myDemandeAbonnement.setAssignedTo(ListeAbonement.get(0));
      demandeAbonnementRepository.save(myDemandeAbonnement);

      Abonnement myAbonnement = abonnementRepository
          .findAbonnementByReferenceClient(myDemandeAbonnement.getReferenceChifco());
      if (myAbonnement != null) {
        myAbonnement.setAssignedTo(ListeAbonement.get(0));
        abonnementRepository.save(myAbonnement);
      }
      return true;
    }

  }

  @Override
  public Boolean affectOneRevendeur(String idRevendeurToAffected, Long demandeId) {
    // TODO Auto-generated method stub
    User userToAffected = userRepository.getById(Long.parseLong(idRevendeurToAffected));
    DemandeAbonnement myDemandeAbonnement =
        demandeAbonnementRepository.findDemandeAbonnementByDemandeId(demandeId);
    if (myDemandeAbonnement != null && userToAffected != null) {
      myDemandeAbonnement.setAssignedTo(userToAffected);
      demandeAbonnementRepository.save(myDemandeAbonnement);

      Abonnement myAbonnement = abonnementRepository
          .findAbonnementByReferenceClient(myDemandeAbonnement.getReferenceChifco());
      if (myAbonnement != null) {
        myAbonnement.setAssignedTo(userToAffected);
        abonnementRepository.save(myAbonnement);
      }
      return true;
    } else
      return false;
  }

  @Override
  public Page<DemandeAbonnement> findPaginatedByRevendeurWithSort(int pageNo, int pageSize,
      Long roleid, Long userid, String sortvar, String sorttype) {
    // TODO Auto-generated method stub
    Sort sort = Sort.by("createdDate").descending();
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.demandeAbonnementRepository
        .findDemandeAbonnementsByAssignedTo_Role_RoleIdAndAssignedTo_Userid(roleid, userid,
            pageable);
  }

  @Override
  public String verificationCinFixEdit(MultiValueMap<String, String> formData) {
    // TODO Auto-generated method stub
    if (formData.getFirst("cin") != null && !formData.getFirst("cin").isEmpty()
        && formData.getFirst("idAbonnement") != null
        && !formData.getFirst("idAbonnement").isEmpty()) {
      String cin = formData.getFirst("cin");
      DemandeAbonnement demandeAbonnement = demandeAbonnementRepository
          .findDemandeAbonnementByDemandeId(Long.parseLong(formData.getFirst("idAbonnement")));
      if (demandeAbonnement.getCin() != null && demandeAbonnement.getCin().equals(cin))
        return "true";
      else {
        DemandeAbonnement demandeAbonnementByCin =
            demandeAbonnementRepository.findDemandeAbonnementByCinAndStatusAvaibled(cin);
        if (demandeAbonnementByCin == null)
          return "true";
        else
          return "false";
      }

    } else
      return "true";
  }

  @Override
  public String confirmeAbonnementInjoignable(String confirmation, Long idabon, User user) {

    // TODO Auto-generated method stub



    if (confirmation != null) {

      DemandeAbonnement finddemande =
          demandeAbonnementRepository.findDemandeAbonnementByDemandeId(idabon);


      Statut singedStatut = statutService.findStatutByNomstatut(NomStatutChifco.CLIENT_INJOIGNABLE);
      finddemande.setStatut(singedStatut);;
      AbonnementHistoriqueservice.insertNewHistory(finddemande, user);
      demandeAbonnementRepository.save(finddemande);
    }
    return confirmation;
  }

  @Override
  public List<Map<String, Object>> getFullYearDemandeAbonnementCounts(int year, Long revId,
      Long chefSecteurId) {
    int currentYear = LocalDate.now().getYear();
    int currentMonth = LocalDate.now().getMonthValue();
    int maxMonth = (year == currentYear) ? currentMonth : 12;
    List<Map<String, Object>> results = demandeAbonnementRepository
        .getDemandeAbonnementCountByMonthForYear(year, revId, chefSecteurId);
    Map<Integer, Long> monthlyCounts = new HashMap<>();
    for (int month = 1; month <= maxMonth; month++) {
      monthlyCounts.put(month, 0L);
    }
    for (Map<String, Object> result : results) {
      Integer month = (Integer) result.get("month");
      Long count = (Long) result.get("demandeAbonnementCount");
      if (month <= maxMonth) {
        monthlyCounts.put(month, count);
      }
    }
    List<Map<String, Object>> fullYearResults = new ArrayList<>();
    for (int month = 1; month <= maxMonth; month++) {
      Map<String, Object> monthData = new HashMap<>();
      monthData.put("month", month);
      monthData.put("demandeAbonnementCount", monthlyCounts.get(month));
      fullYearResults.add(monthData);
    }
    return fullYearResults;
  }

  @Override
  public List<Map<String, Object>> getFullYearDemandeAbonnementRealiserCounts(int year, Long revId,
      Long chefSecteurId) {

    int currentYear = LocalDate.now().getYear();
    int currentMonth = LocalDate.now().getMonthValue();
    int maxMonth = (year == currentYear) ? currentMonth : 12;
    List<Map<String, Object>> results = demandeAbonnementRepository
        .getDemandeAbonnementRealiserCountByMonthForYear(year, revId, chefSecteurId);
    Map<Integer, Long> monthlyCounts = new HashMap<>();
    for (int month = 1; month <= maxMonth; month++) {
      monthlyCounts.put(month, 0L);
    }
    for (Map<String, Object> result : results) {
      Integer month = (Integer) result.get("month");
      Long count = (Long) result.get("demandeAbonnementCount");
      if (month <= maxMonth) {
        monthlyCounts.put(month, count);
      }
    }
    List<Map<String, Object>> fullYearResults = new ArrayList<>();
    for (int month = 1; month <= maxMonth; month++) {
      Map<String, Object> monthData = new HashMap<>();
      monthData.put("month", month);
      monthData.put("demandeAbonnementCount", monthlyCounts.get(month));
      fullYearResults.add(monthData);
    }
    return fullYearResults;
  }

  public HashMap<String, Object> getfiltredstatusabonnemnSuividesDemandesTransferees(int draw,
      int start, int length, String search, int ordercolumnaram, String orderdir,
      String filterrecherche, Long status) {
    // TODO Auto-generated method stub
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();
    User user = userRepository.findUsersByEmail(currentUser);
    String refChif = null;
    String refTT = null;
    String cin = null;
    String prenom = null;
    String nom = null;
    Long tel = null;
    String datedebut = null;
    String datefin = null;
    String statutTTListfiltre = null;
    Long villes = null;
    Long gouvernorat = null;
    Long professions = null;
    Long categories = null;
    Long produit = null;
    String dateDebutModification = null;
    String dateFinModification = null;
    Long AffecterTo = null;
    Long CreePar = null;
    Long statutListfiltre = (Long) status;
    if (search != null && search != "")
      refChif = search;
    int page = start / length;

    String sort = "";
    switch (ordercolumnaram) {

      case 0:
        sort = "referenceChifco";
        break;
      case 1:
        sort = "referenceTT";
        break;
      case 2:
        sort = "etatTT";
        break;
      case 3:
        sort = "statut";
        break;
      case 4:
        sort = "cin";
        break;
      case 5:
        sort = "firstName";
        break;
      case 6:
        sort = "telFixe";
        break;
      case 7:
        sort = "user";
        break;
      case 8:
        sort = "createdDate";
        break;

    }

    if (filterrecherche != null && filterrecherche != "") {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("refChif"), "") && obj.getString("refChif") != null) {
        refChif = obj.getString("refChif").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("refTT"), "") && obj.getString("refTT") != null) {
        refTT = obj.getString("refTT").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("cin"), "") && obj.getString("cin") != null) {
        cin = obj.getString("cin").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("prenom"), "") && obj.getString("prenom") != null) {
        prenom = obj.getString("prenom").trim();
      }
      if (!Objects.equals(obj.getString("nom"), "") && obj.getString("nom") != null) {
        nom = obj.getString("nom").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("tel"), "") && obj.getString("tel") != null) {
        tel = obj.getLong("tel");
      }
      if (!Objects.equals(obj.getString("villes"), "") && obj.getString("villes") != null) {
        villes = obj.getLong("villes");
      }
      if (!Objects.equals(obj.getString("gouvernorat"), "")
          && obj.getString("gouvernorat") != null) {
        gouvernorat = obj.getLong("gouvernorat");
      }
      if (!Objects.equals(obj.getString("professions"), "")
          && obj.getString("professions") != null) {
        professions = obj.getLong("professions");
      }
      if (!Objects.equals(obj.getString("categories"), "") && obj.getString("categories") != null) {
        categories = obj.getLong("categories");
      }
      if (!Objects.equals(obj.getString("produit"), "") && obj.getString("produit") != null) {
        produit = obj.getLong("produit");
      }
      if (!Objects.equals(obj.getString("datedebut"), "") && obj.getString("datedebut") != null) {
        datedebut = obj.getString("datedebut") + "T00:00:00.000";
      }
      if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
        datefin = obj.getString("datefin") + "T23:59:59.999";
      }
      if (obj.has("statutTTListfiltre") && !Objects.equals(obj.getString("statutTTListfiltre"), "")
          && obj.getString("statutTTListfiltre") != null) {
        statutTTListfiltre = obj.getString("statutTTListfiltre");
      }
      if (obj.has("dateDebutModification")
          && !Objects.equals(obj.getString("dateDebutModification"), "")
          && obj.getString("dateDebutModification") != null) {
        dateDebutModification = obj.getString("dateDebutModification") + "T00:00:00.000";
      }
      if (obj.has("dateFinModification")
          && !Objects.equals(obj.getString("dateFinModification"), "")
          && obj.getString("dateFinModification") != null) {
        dateFinModification = obj.getString("dateFinModification") + "T23:59:59.999";
      }

    }

    Page<DemandeAbbonmentDataDTO> responseData = null;
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());
    // ROLE_ADMINISTRATEUR || ROLE_POS

    // ROLE_REVENDEUR
    if (StringsRole.contains("READ_SUBSCRIPTION_REQUEST_RETAIL")) {

      responseData = this.findPaginatedByRevendeurSuiviDesDemandesTransfDTO(page + 1, length,
          user.getUserid(), refChif, refTT, cin, prenom, nom, tel, villes, gouvernorat, professions,
          categories, produit, statutListfiltre, statutTTListfiltre, datedebut, datefin,
          dateDebutModification, dateFinModification, null, null, sort, orderdir);
    }
    // ROLE_DISTRIBUTEUR

    HashMap<String, Object> myGreetings = new HashMap<String, Object>();

    myGreetings.put("data", responseData.getContent());
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    myGreetings.put("recordsTotal", responseData.getTotalElements());
    myGreetings.put("recordsFiltered", responseData.getTotalElements());
    return myGreetings;


  }



}
