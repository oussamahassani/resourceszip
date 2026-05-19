package crm.chifco.com.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.tool.schema.internal.GroupedSchemaMigratorImpl;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.DemandeCommission;
import crm.chifco.com.model.DemandeCommissionGroup;
import crm.chifco.com.model.EntryBordereau;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.CommissionRepository;
import crm.chifco.com.repository.DemandeCommissionGroupRepository;
import crm.chifco.com.repository.DemandeCommissionRepository;
import crm.chifco.com.service.DemandeCommissionExcelExport;
import crm.chifco.com.service.DemandeCommissionService;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.model.jasper.FactureCommisionDataSet;

@Service
public class DemandeCommissionServiceImpl implements DemandeCommissionService {
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  DemandeCommissionRepository demandeCommissionRepository;

  @Autowired
  CommissionRepository commissionRepository;

  @Value("${pathCommission}")
  private String pathCommission;

  @Autowired
  DemandeCommissionGroupRepository demandeCommissionGroupRepository;
  
  @Override
  public HashMap<String, Object> getAll(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche) {
    // TODO Auto-generated method stub
    int currentpage = start / length;
    Page<DemandeCommission> responseData = null;
    HashMap<String, Object> myHmapData = new HashMap<>();

    String codeRevendeur = null;
    Integer numMois = null;
    Integer annee = null;
    String statut = null;
    Date startCreatedDate = null;
    Date endCreatedDate = null;
    String reference = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("date"), "") && obj.getString("date") != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(obj.getString("date").trim(), formatter);

        annee = yearMonth.getYear();
        numMois = yearMonth.getMonth().getValue();
      }
      if (!Objects.equals(obj.getString("codeRevendeur"), "")
          && obj.getString("codeRevendeur") != null) {
        codeRevendeur = obj.getString("codeRevendeur").trim();
      }
      if (!Objects.equals(obj.getString("statut"), "") && obj.getString("statut") != null) {
        statut = obj.getString("statut").trim();
      }
      if (!Objects.equals(obj.getString("startCreatedDate"), "")
          && obj.getString("startCreatedDate") != null) {
        startCreatedDate = CrmUtils.convertStringToDate(obj.getString("startCreatedDate"));
      }
      if (!Objects.equals(obj.getString("endCreatedDate"), "")
          && obj.getString("endCreatedDate") != null) {
        endCreatedDate = CrmUtils.convertStringToLocalDateTime(obj.getString("endCreatedDate"));
      }
      if (!Objects.equals(obj.getString("reference"), "") && obj.getString("reference") != null) {
        reference = obj.getString("reference").trim();
      }
    }

    Sort sort = Sort.by(Sort.Direction.DESC, "id");
    Pageable pageable = PageRequest.of(currentpage, length, sort);
    logger.info("Appel de getAllDemandeCommission avec filtres - annee: {}, mois: {}, codeRevendeur: {}, statut: {}, reference: {}", 
        annee, numMois, codeRevendeur, statut, reference);
    responseData = demandeCommissionRepository.getAllDemandeCommission(pageable, annee, numMois,
        codeRevendeur, startCreatedDate, endCreatedDate, reference, statut);
    logger.info("Résultat getAllDemandeCommission: {} éléments trouvés", 
        responseData != null ? responseData.getTotalElements() : 0);

    if (responseData != null) {
      myHmapData.put("data", responseData.getContent());
      myHmapData.put("recordsTotal", responseData.getTotalElements());
      myHmapData.put("recordsFiltered", responseData.getTotalElements());
    }
    myHmapData.put("draw", draw);
    myHmapData.put("start", start);

    return myHmapData;
  }

  public String demandeCommissionParRev(Long id, MultipartFile imageFile, String commentaire,
      User user) {
    logger.info("Début de demandeCommissionParRev...");

    logger.debug("Parametres - id: {}, imageFile: {}, commentaire: {}, user: {}", id,
        imageFile != null ? imageFile.getOriginalFilename() : "null", commentaire,
        user != null ? user.getEmail() : "null");

    // Validation des données
    if (id == null) {
      logger.error("ID_REQUIS");
      return "ID_REQUIRED";
    }
    if (imageFile == null || imageFile.isEmpty()) {
      logger.error("FILE_REQUISE");
      return "FILE_REQUIRED";
    }

    if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
      logger.error("UTILISATEUR_NON_TROUVE");
      return "USER_NOT_FOUND";
    }

    if (demandeCommissionRepository.countDemandesByCommissionAndStatut(id) > 0) {
      logger.error("COMMISSION_ALREADY_HAS_ACTIVE_REQUEST");
      return "COMMISSION_ALREADY_HAS_ACTIVE_REQUEST";
    }

    logger.debug("Création des objets Commission et DemandeCommission...");
    Commission commission = commissionRepository.getById(id);

    DemandeCommission demandeCommission = new DemandeCommission();
    demandeCommission.setCommission(commission);
    demandeCommission.setStatut("IN_PROGRESS");
    demandeCommission.setDemandeBy(user);
    demandeCommission.setCommentaireRev(commentaire.length() > 0 ? commentaire : null);
    demandeCommission.setRefDemandeCommission(genererReference(commission));
    try {
      logger.debug("Sauvegarde de l'image...");
      LocalDateTime now = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

      CrmUtils.saveImage(imageFile, id.toString(), pathCommission, now.format(formatter) + "Rev");
      demandeCommission.setPhotoRecuRev(now.format(formatter) + "Rev"
          + CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      logger.error("Erreur lors de la sauvegarde de l'image : " + e.getMessage());
      e.printStackTrace();
    }

    demandeCommissionRepository.save(demandeCommission);
    commission.setStatut("IN_PROGRESS");
    commissionRepository.save(commission);
    logger.info("demandeCommissionParRev terminée avec succès.");
    return "SUCCESS";
  }

  String genererReference(Commission commission) {

    String refCommission = commission.getRefCommission();
    String r = refCommission.substring(2);
    int count = demandeCommissionRepository.countByCommissionId(commission.getId());

    String refDemande = "DP-" + r + "-" + (count + 1);
    return refDemande;
  }

  @Override
  public String validationCommission(String decision, MultipartFile image, String commentaire,
      String raison, Long id, User acceptedBy) {
    // TODO Auto-generated method stub

    logger.info("Début de validationCommission...");

    logger.debug(
        "Paramètres - decision: {}, image: {}, commentaire: {}, raison: {}, id: {}, acceptedBy: {}",
        decision, image != null ? image.getOriginalFilename() : "null", commentaire, raison, id,
        acceptedBy != null ? acceptedBy.getEmail() : "null");

    if (decision == null || decision.trim().isEmpty() || decision.equals("undefined")) {
      logger.error("DECISSION_REQUISE");
      return "DECISSION_REQUIRED";
    }
    if (decision.equals("accept")) {
      if (image == null || image.isEmpty()) {
        logger.error("IMAGE_REQUISE");
        return "IMAGE_REQUIRED";
      }
    } else {
      if (raison == null || raison.trim().isEmpty() || raison.equals("undefined")) {
        logger.error("RAISON_REQUISE");
        return "RAISON_REQUIRED";
      }
    }

    if (id == null) {
      logger.error("ID_REQUIS");
      return "ID_REQUIRED";
    }


    DemandeCommission demandeCommission = demandeCommissionRepository.findById(id).get();
    if (demandeCommission == null) {
      logger.error("DEMANDE_NON_TROUVE");
      return "DEMANDE_NOT_FOUND";
    }

    if (!(demandeCommission.getStatut().equals("IN_PROGRESS") ||demandeCommission.getStatut().equals("AWAINTING_INVOICING") )  ) {
      logger.error("UNCHANGEABLE_COMMISSION");
      return "UNCHANGEABLE_COMMISSION";
    }

    demandeCommission.setDateDecission(new Date());

    if (decision.equals("accept")) {
      logger.debug("Traitement de l'acceptation de la commission...");
      demandeCommission.setStatut("PAID");
      demandeCommission.setCommentaireAdmin(commentaire.length() > 0 ? commentaire : null);
      demandeCommission.setAcceptedBy(acceptedBy);
      try {
        logger.debug("Sauvegarde de l'image...");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        CrmUtils.saveImage(image, demandeCommission.getCommission().getId().toString(),
            pathCommission, now.format(formatter) + "Admin");
        demandeCommission.setPhotoRecuAdmin(now.format(formatter) + "Admin"
            + CrmUtils.noSpecialCharacters(image.getOriginalFilename()));

      } catch (Exception e) {
        // TODO Auto-generated catch block
        logger.error("Erreur lors de la sauvegarde de l'image : " + e.getMessage());
        e.printStackTrace();
      }

      logger.debug("Sauvegarde de demandeCommission...");
      demandeCommissionRepository.save(demandeCommission);

      logger.debug("Mise à jour du statut de commission...");
      commissionRepository.updateStatut(demandeCommission.getCommission().getId(), "PAID");

      logger.info("Validation de commission avec succès.");
      return "SUCCESS";
    } else {

      logger.debug("Traitement du refus de la commission...");
      demandeCommission.setStatut("REFUSED");
      demandeCommission.setCommentaireAdmin(raison);
      demandeCommission.setAcceptedBy(acceptedBy);
      demandeCommissionRepository.save(demandeCommission);

      logger.info("Mise à jour du statut de commission...");
      commissionRepository.updateStatut(demandeCommission.getCommission().getId(), "NOT_PAID");

      logger.info("Validation de commission avec succès.");
      return "SUCCESS";
    }
  }

  @Override
  public String demandeCommissionMultiple(List<Long> commissionIds, MultipartFile imageFile,
      String commentaire, User user) {
    logger.info("Début de demandeCommissionMultiple...");

    // Validation des données
    if (commissionIds == null || commissionIds.isEmpty()) {
      logger.error("COMMISSION_IDS_REQUIRED");
      return "COMMISSION_IDS_REQUIRED";
    }
    if (imageFile == null || imageFile.isEmpty()) {
      logger.error("FILE_REQUIRED");
      return "FILE_REQUIRED";
    }
    if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
      logger.error("USER_NOT_FOUND");
      return "USER_NOT_FOUND";
    }

    try {
        DemandeCommissionGroup group = new DemandeCommissionGroup();
        List<DemandeCommission> ListeDemandeCommission = new ArrayList<DemandeCommission>();

      

      for (Long commissionId : commissionIds) {
        // Vérifier si une demande existe déjà pour cette commission
        if (demandeCommissionRepository.countDemandesByCommissionAndStatut(commissionId) > 0) {
          logger.warn("Commission {} a déjà une demande active", commissionId);
          return "COMMISSION_ALREADY_HAS_ACTIVE_REQUEST";
        }

        Commission commission = commissionRepository.getById(commissionId);
        if (commission == null) {
          logger.warn("Commission {} non trouvée", commissionId);
          return "COMMISSION_NOT_FOUND";
        }
    
        // Associer chaque demande au groupe et changer le statut

        
        


        logger.info("createDemandeGroup terminée avec succès. Group ID: {}", group.getId());
        // Créer une demande pour chaque commission
        DemandeCommission demandeCommission = new DemandeCommission();
        demandeCommission.setCommission(commission);
        demandeCommission.setStatut("IN_PROGRESS");
        demandeCommission.setDemandeBy(user);
        demandeCommission.setCommentaireRev(commentaire.length() > 0 ? commentaire : null);
        demandeCommission.setRefDemandeCommission(genererReference(commission));

        // Sauvegarder l'image
        if (imageFile != null && !imageFile.isEmpty()) {
          LocalDateTime now = LocalDateTime.now();
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
          CrmUtils.saveImage(imageFile, commissionId.toString(), pathCommission,
              now.format(formatter) + "Rev");
          demandeCommission.setPhotoRecuRev(now.format(formatter) + "Rev"
              + CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
        }

        demandeCommissionRepository.save(demandeCommission);
      
          	ListeDemandeCommission.add(demandeCommission);
         
          
        commission.setStatut("IN_PROGRESS");
        commissionRepository.save(commission);
      }
      group.setRefGroup(genererReferenceGroup());
      group.setStatut("IN_PROGRESS");
      group.setCommentaire(commentaire);
      group.setCreatedByUserId(user.getUserid());
      group.setDemandeCommissions(ListeDemandeCommission);
      group.setInvoiceFilePath(imageFile.getOriginalFilename());
      group = demandeCommissionGroupRepository.save(group);
      logger.info("demandeCommissionMultiple terminée avec succès.");
      return "SUCCESS";
    } catch (Exception e) {
      logger.error("Erreur lors du traitement des demandes multiples : " + e.getMessage());
      e.printStackTrace();
      return "ERROR";
    }
  }

  @Override
  public String validerCommissionMultiple(List<Long> commissionIds, String motif, User user) {
    logger.info("Début de validerCommissionMultiple...");

    // Validation des données
    if (commissionIds == null || commissionIds.isEmpty()) {
      logger.error("COMMISSION_IDS_REQUIRED");
      return "COMMISSION_IDS_REQUIRED";
    }
    if (user == null) {
      logger.error("USER_NOT_FOUND");
      return "USER_NOT_FOUND";
    }

    try {
      for (Long commissionId : commissionIds) {
        Commission commission = commissionRepository.getById(commissionId);
        if (commission == null) {
          logger.warn("Commission {} non trouvée", commissionId);
          continue;
        }

        // Récupérer les demandes pour cette commission
        List<DemandeCommission> demandes = demandeCommissionRepository.findAllByCommissionId(commissionId);
        for (DemandeCommission demande : demandes) {
          if ("IN_PROGRESS".equals(demande.getStatut())) {
            demande.setStatut("PAID");
            demande.setDateDecission(new Date());
            demande.setCommentaireAdmin(motif);
            demande.setAcceptedBy(user);
            demandeCommissionRepository.save(demande);
          }
        }

        // Mettre à jour le statut de la commission
        commission.setStatut("PAID");
        commissionRepository.save(commission);
      }

      logger.info("validerCommissionMultiple terminée avec succès.");
      return "SUCCESS";
    } catch (Exception e) {
      logger.error("Erreur lors de la validation multiple : " + e.getMessage());
      e.printStackTrace();
      return "ERROR";
    }
  }

  @Override
  public HashMap<String, Object> getAllByRev(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, User user) {
    // TODO Auto-generated method stub
    int currentpage = start / length;
    Page<DemandeCommission> responseData = null;
    HashMap<String, Object> myHmapData = new HashMap<>();

    Integer numMois = null;
    Integer annee = null;
    String reference = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("date"), "") && obj.getString("date") != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(obj.getString("date").trim(), formatter);

        annee = yearMonth.getYear();
        numMois = yearMonth.getMonth().getValue();
      }
      if (!Objects.equals(obj.getString("reference"), "") && obj.getString("reference") != null) {
        reference = obj.getString("reference").trim();
      }
    }
    List<String> status = Arrays.asList("IN_PROGRESS", "AWAITING_INVOICING");
    Pageable pageable = PageRequest.of(currentpage, length);
    responseData = demandeCommissionRepository.getDemandeCommissionByRev(pageable, user.getUserid(),
        annee, numMois, reference);



    if (responseData != null) {
      myHmapData.put("data", responseData.getContent());
      myHmapData.put("recordsTotal", responseData.getTotalElements());
      myHmapData.put("recordsFiltered", responseData.getTotalElements());
    }
    myHmapData.put("draw", draw);
    myHmapData.put("start", start);

    return myHmapData;

  }

  @Override
  public ModelAndView exportListDemandeCommissionToExcel(HttpServletRequest request,
      HttpServletResponse response) {
    logger.info("Début de exportListDemandeCommissionToExcel...");
    ModelAndView mav = new ModelAndView();

    List<DemandeCommission> myList = new ArrayList<>();

    logger.info("Appel de demandeCommissionRepository.getAllDemandeCommission()...");
    // myList = demandeCommissionRepository.getAllDemandeCommission();
    myList = demandeCommissionRepository.findAll(); // Test avec findAll()
    logger.info("Résultat: {} demandes trouvées", myList.size());
    
    // Vérifier le nombre total d'enregistrements dans la table
    Long totalCount = demandeCommissionRepository.countAllDemandes();
    logger.info("Nombre total d'enregistrements dans la table demande_commission: {}", totalCount);
    
    if (myList.size() > 0) {
      logger.info("Export Excel avec {} demandes", myList.size());
      mav.setView(new DemandeCommissionExcelExport());
      mav.addObject("list", myList);
    } else {
      logger.warn("Aucune demande de commission trouvée pour l'export Excel");
      mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
      mav.addObject("errorMessage", "No data found");
      // Add an error message

      try {
        request.getRequestDispatcher("/demandeCommission/all_demande_commission_page")
            .forward(request, response);
      } catch (ServletException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return null;

    }
    return mav;
  }



  @Override
  public String createDemandeGroup(List<Long> demandeIds, String commentaire, User user) {
    logger.info("Début de createDemandeGroup...");

    // Validation des données
    if (demandeIds == null || demandeIds.isEmpty()) {
      logger.error("DEMANDE_IDS_REQUIRED");
      return "DEMANDE_IDS_REQUIRED";
    }
    if (user == null || user.getUserid() == null) {
      logger.error("USER_NOT_FOUND");
      return "USER_NOT_FOUND";
    }

    try {
      // Créer le groupe
      DemandeCommissionGroup group = new DemandeCommissionGroup();
      List<DemandeCommission> ListeDemandeCommission = new ArrayList<DemandeCommission>();

      group.setRefGroup(genererReferenceGroup());
      group.setStatut("IN_PROGRESS");
      group.setCommentaire(commentaire);
      group.setCreatedByUserId(user.getUserid());

      // Associer chaque demande au groupe et changer le statut
      for (Long demandeId : demandeIds) {
        DemandeCommission demande = demandeCommissionRepository.findById(demandeId).orElse(null);
        if (demande != null && "IN_PROGRESS".equals(demande.getStatut())) {
        	ListeDemandeCommission.add(demande);
        //  demande.setStatut("IN_PROGRESS");

          // Mettre à jour le statut de la commission
        /*  Commission commission = demande.getCommission();
          if (commission != null) {
            commission.setStatut("AWAINTING_INVOICING");
            commissionRepository.save(commission);
          }*/
        }
      }
      group.setDemandeCommissions(ListeDemandeCommission);
      group = demandeCommissionGroupRepository.save(group);

      logger.info("createDemandeGroup terminée avec succès. Group ID: {}", group.getId());
      return "SUCCESS";
    } catch (Exception e) {
      logger.error("Erreur lors de la création du groupe : " + e.getMessage());
      e.printStackTrace();
      return "ERROR";
    }
  }

  private String genererReferenceGroup() {
    // Générer une référence qui commence par DPM
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    return "DPM-" + now.format(formatter);
  }

  @Override
  public String validateDemandeGroup(Long groupId, String decision, String motif, String commentaire, MultipartFile decisionFile, User user) {
    logger.info("Début de validateDemandeGroup... Group ID: {}, Decision: {}", groupId, decision);

    if (groupId == null) {
      logger.error("GROUP_ID_REQUIRED");
      return "GROUP_ID_REQUIRED";
    }
    if (user == null || user.getUserid() == null) {
      logger.error("USER_NOT_FOUND");
      return "USER_NOT_FOUND";
    }

    try {
      DemandeCommissionGroup group = demandeCommissionGroupRepository.findById(groupId).orElse(null);
      if (group == null) {
        logger.error("GROUP_NOT_FOUND");
        return "GROUP_NOT_FOUND";
      }

      String newStatut = "accept".equals(decision) ? "PAID" : "REFUSED";
      group.setStatut(newStatut);
      group.setDateDecision(new Date());
      group.setValidatedByUserId(user.getUserid());

      if ("REFUSED".equals(newStatut)) {
        group.setMotifRejet(motif);
      }
	  LocalDateTime now = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

      String fileName = decisionFile.getOriginalFilename();
      // Handle file upload for accepted decisions
      if ("accept".equals(decision) && decisionFile != null && !decisionFile.isEmpty()) {
       
        CrmUtils.saveImage(decisionFile, groupId.toString(),
                pathCommission, now.format(formatter) + "Admin");
        group.setDecisionFileName(now.format(formatter) + "Admin"
                + CrmUtils.noSpecialCharacters(fileName));
      
      }

      // Set commentaire for accepted decisions
      if ("accept".equals(decision) && commentaire != null && !commentaire.trim().isEmpty()) {
        group.setCommentaire(commentaire);
      }

      demandeCommissionGroupRepository.save(group);

      // Mettre à jour toutes les demandes du groupe
      List<DemandeCommission> demandes =  group.getDemandeCommissions();
      for (DemandeCommission demande : demandes) {
        demande.setStatut(newStatut);
        demande.setDateDecission(new Date());
        demande.setCommentaireAdmin(motif);
        demande.setAcceptedBy(user);
        CrmUtils.saveImage(decisionFile, demande.getId().toString(),
                pathCommission, now.format(formatter) + "Admin");
        demande.setPhotoRecuAdmin(now.format(formatter) + "Admin"
                + CrmUtils.noSpecialCharacters(fileName));
        demandeCommissionRepository.save(demande);

        // Mettre à jour le statut de la commission
        Commission commission = demande.getCommission();
        if (commission != null) {
          commission.setStatut("PAID".equals(newStatut) ? "PAID" : "NOT_PAID");
          commissionRepository.save(commission);
        }
      }

      logger.info("validateDemandeGroup terminée avec succès.");
      return "SUCCESS";
    } catch (Exception e) {
      logger.error("Erreur lors de la validation du groupe : " + e.getMessage());
      e.printStackTrace();
      return "ERROR";
    }
  }

  @Override
  public HashMap<String, Object> getAllGroupedDemandes(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche, User user) {
    int currentpage = start / length;
    HashMap<String, Object> myHmapData = new HashMap<>();
    List<String> StringsRole = user.getRole().getStringsRole(user.getRole().getPrivileges());

    String statut = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("statut"), "") && obj.getString("statut") != null) {
        statut = obj.getString("statut").trim();
      }
    }

    Sort sort = Sort.by(Sort.Direction.DESC, "id");
    Pageable pageable = PageRequest.of(currentpage, length, sort);

    Page<DemandeCommissionGroup> responseData;
    if (user != null && user.getUserid() != null && (StringsRole.contains("COMMISSION_OWNER_FREELANCER")
    		|| StringsRole.contains("COMMISSION_OWNER")) ) {
      responseData = demandeCommissionGroupRepository.findByUserIdAndStatut(pageable, user.getUserid(), statut);
    } else {
      responseData = demandeCommissionGroupRepository.findAll(pageable);
    }

    if (responseData != null) {
      myHmapData.put("data", responseData.getContent());
      myHmapData.put("recordsTotal", responseData.getTotalElements());
      myHmapData.put("recordsFiltered", responseData.getTotalElements());
    }
    myHmapData.put("draw", draw);
    myHmapData.put("start", start);

    return myHmapData;
  }

  @Override
  public DemandeCommissionGroup getGroupedDemandeById(Long id) {
    return demandeCommissionGroupRepository.findById(id).orElse(null);
  }

  @Override
  public String uploadInvoiceForGroup(Long groupId, MultipartFile invoiceFile, User user) {
    logger.info("Début de uploadInvoiceForGroup... Group ID: {}", groupId);

    if (groupId == null) {
      logger.error("GROUP_ID_REQUIRED");
      return "GROUP_ID_REQUIRED";
    }
    if (invoiceFile == null || invoiceFile.isEmpty()) {
      logger.error("INVOICE_FILE_REQUIRED");
      return "INVOICE_FILE_REQUIRED";
    }
    if (user == null || user.getUserid() == null) {
      logger.error("USER_NOT_FOUND");
      return "USER_NOT_FOUND";
    }

    try {
      DemandeCommissionGroup group = demandeCommissionGroupRepository.findById(groupId).orElse(null);
      if (group == null) {
        logger.error("GROUP_NOT_FOUND");
        return "GROUP_NOT_FOUND";
      }

      // Vérifier que l'utilisateur est le propriétaire du groupe
      if (!user.getUserid().equals(group.getCreatedByUserId())) {
        logger.error("USER_NOT_AUTHORIZED");
        return "USER_NOT_AUTHORIZED";
      }

      // Vérifier que le groupe est en attente de facturation
      if (!"IN_PROGRESS".equals(group.getStatut())) {
        logger.error("INVALID_GROUP_STATUS");
        return "INVALID_GROUP_STATUS";
      }

      // Sauvegarder le fichier PDF
      LocalDateTime now = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
      CrmUtils.saveImage(invoiceFile, groupId.toString(), pathCommission,
              now.format(formatter) + "Rev");

      group.setInvoiceFilePath(now.format(formatter) + "Rev"
          + CrmUtils.noSpecialCharacters(invoiceFile.getOriginalFilename()));
      // Sauvegarder le fichier
     

      // Mettre à jour le groupe

      group.setStatut("IN_PROGRESS");
      group.setModifiedDate(new Date());
      demandeCommissionGroupRepository.save(group);

      logger.info("uploadInvoiceForGroup terminée avec succès. File: {}", group.getInvoiceFilePath());
      return "SUCCESS";
    } catch (Exception e) {
      logger.error("Erreur lors de l'upload de la facture : " + e.getMessage());
      e.printStackTrace();
      return "ERROR";
    }
  }

  @Override
  public void generateAndDownloadFactureMultiple(Long groupId, HttpServletResponse response) {
    logger.info("Début de generateAndDownloadFactureMultiple pour le groupe ID: {}", groupId);
    
    try {
      // Récupérer le groupe
      DemandeCommissionGroup group = demandeCommissionGroupRepository.findById(groupId)
          .orElseThrow(() -> new IllegalArgumentException("Groupe non trouvé avec l'ID: " + groupId));
      
      logger.info("Groupe trouvé: {}", group.getRefGroup());
      
      // Récupérer les demandes du groupe
      List<DemandeCommission> demandes = group.getDemandeCommissions();
      
      if (demandes == null || demandes.isEmpty()) {
        logger.warn("Aucune demande trouvée pour le groupe ID: {}", groupId);
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Aucune demande trouvée pour ce groupe");
        return;
      }
      
      // Extraire les commissions
      List<Commission> commissions = demandes.stream()
          .map(DemandeCommission::getCommission)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
      
      if (commissions.isEmpty()) {
        logger.warn("Aucune commission trouvée pour le groupe ID: {}", groupId);
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Aucune commission trouvée pour ce groupe");
        return;
      }
      
      // Préparer les données
      Collection<FactureCommisionDataSet> factureDataSets = new ArrayList<>();
      FactureCommisionDataSet factureDataSet = new FactureCommisionDataSet();
      
      User revendeur = commissions.get(0).getRevendeur();
      factureDataSet.setCommission(commissions);
      factureDataSet.setRevendeur(Collections.singletonList(revendeur));
      factureDataSets.add(factureDataSet);
      
      // Calculer les totaux
      double totalHt = commissions.stream()
          .mapToDouble(c -> c.getTotalHt() != null ? c.getTotalHt() : 0.0)
          .sum();
      
      double montantTva = commissions.stream()
          .mapToDouble(c -> c.getMontantTva() != null ? c.getMontantTva() : 0.0)
          .sum();
      
      double montantAvance = commissions.stream()
          .mapToDouble(c -> c.getMontantAvancePremiereFacture() != null ? c.getMontantAvancePremiereFacture() : 0.0)
          .sum();
      
      double totalTtc = commissions.stream()
          .mapToDouble(c -> c.getTotalTtc() != null ? c.getTotalTtc() : 0.0)
          .sum();
      
      double totalRetenu = commissions.stream()
          .mapToDouble(c -> c.getRetunuSource() != null ? c.getRetunuSource() : 0.0)
          .sum();
      
      // Préparer les paramètres JasperReports
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("isset_tva", true);
      parameters.put("matfiscale", revendeur != null ? " --" : "");
      parameters.put("numfact", group.getRefGroup());
      parameters.put("cbancaire", "");
     // parameters.put("createdDate",  new java.util.Date(group.getCreatedDate().getTime()) );
      parameters.put("totalHt", totalHt);
      parameters.put("totalTTC", totalTtc);
      parameters.put("totalTva", montantTva);
      parameters.put("totalRetenu", totalRetenu);
      parameters.put("montantAvancePremiereFacture", montantAvance);
      
      // Préparer la source de données
      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(factureDataSets);
      
      // Charger et compiler le template JasperReports
      File templateFile = ResourceUtils.getFile("classpath:reports/factureCommisionMultiplez.jrxml");
      JasperReport jasperReport = JasperCompileManager.compileReport(templateFile.getAbsolutePath());
      
      // Remplir le rapport
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
      
      // Préparer le répertoire de destination
      File outputFolder = new File(pathCommission);
      if (!outputFolder.exists()) {
        outputFolder.mkdirs();
        outputFolder.setWritable(true);
      }
      
      // Générer le fichier PDF
      String fileName = "FactureMultiple_" + group.getRefGroup() + "_" + System.currentTimeMillis() + ".pdf";
      String filePath = pathCommission + "/" + fileName;
      
      JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
      
      File pdfFile = new File(filePath);
      
      // Envoyer le fichier en téléchargement
      response.setContentType("application/pdf; charset=" + Charset.forName("utf-8").displayName());
      response.setHeader("Content-disposition", "attachment; filename=" + pdfFile.getName());
      
      InputStream targetStream = new FileInputStream(pdfFile);
      
      try {
        org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
        response.flushBuffer();
      } finally {
        targetStream.close();
      }
      
      logger.info("Facture multiple générée et téléchargée avec succès: {}", fileName);
      
    } catch (JRException e) {
      logger.error("Erreur JasperReports lors de la génération de la facture: " + e.getMessage());
      e.printStackTrace();
      try {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
            "Erreur lors de la génération de la facture: " + e.getMessage());
      } catch (IOException ioException) {
        logger.error("Erreur lors de l'envoi de l'erreur: " + ioException.getMessage());
      }
    } catch (Exception e) {
      logger.error("Erreur générale lors de la génération de la facture: " + e.getMessage());
      e.printStackTrace();
      try {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
            "Erreur lors de la génération de la facture");
      } catch (IOException ioException) {
        logger.error("Erreur lors de l'envoi de l'erreur: " + ioException.getMessage());
      }
    }
  }



}
