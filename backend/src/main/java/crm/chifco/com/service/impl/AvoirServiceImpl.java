package crm.chifco.com.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.ApiDTO.entryAvoirClient;
import crm.chifco.com.DTOclass.AvoirDto;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.EntryAvoirClient;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Payement;
import crm.chifco.com.model.RecuNumeroSequence;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AvoirRepository;
import crm.chifco.com.repository.EncaissementRepository;
import crm.chifco.com.repository.EntryAvoirClientRepository;
import crm.chifco.com.repository.FactureRepository;
import crm.chifco.com.repository.PayementRepository;
import crm.chifco.com.repository.RecuNumeroSequenceRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.AvoirClientExcelExport;
import crm.chifco.com.service.AvoirService;
import crm.chifco.com.service.GenerateReferenceAvoir;
import crm.chifco.com.service.RecuNumeroSequenceService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.UserTypeConstant;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import crm.chifco.com.service.AvoirHistoryService;

@Service("AvoirService")
public class AvoirServiceImpl implements AvoirService {
  private final Logger logger = LogManager.getLogger(this.getClass());
  @Autowired
  AvoirRepository avoirRepository;

  @Autowired
  AbonnementRepository abonnementRepository;

  @Autowired
  private PayementRepository payementRepository;

  @Autowired
  private EncaissementRepository encaissementRepository;

  @Autowired
  RecuNumeroSequenceRepository recuNumeroSequenceRepository;

  @Autowired
  RecuNumeroSequenceService recuNumeroSequenceService;

  @Autowired
  private UserService userService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  EntryAvoirClientRepository EntryAvoirClientRepository;

  @Autowired
  GenerateReferenceAvoir generateReferenceAvoir;

  @Value("${pathAvoir}")
  private String pathAvoir;

  @Value("${timbrefiscale}")
  Double timbrefiscale;

  @Autowired
  FactureRepository factureRepository;
  
   @Autowired
   AvoirHistoryService avoirHistoryService ; 
   
  @Override
  public HashMap<String, Object> getAllAvoir(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche , Boolean isPublich) {
    // TODO Auto-generated method stub
    int currentpage = start / length;
    String sort = "";
    String abonnement = null;
    String motifAvoir = null;
    Double montantAvoir = null;
    Boolean avoirStatut = null;
    Date startDate = null;
    Date endDate = null;
    String reference = null;
    Long usedBy = null;
    Boolean authorizationAdd = null;
    Long createdBy = null;
    Date datePayementDebut = null;
    Date datePayementFin = null;
    Boolean typeAVr = null ; 
    Page<AvoirDto> responseData = null;
    HashMap<String, Object> myHmapData = new HashMap<>();
    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);

      if (!Objects.equals(obj.getString("abonnement"), "") && obj.getString("abonnement") != null) {
        abonnement = obj.getString("abonnement").trim();
      }
      if (!Objects.equals(obj.getString("motifAvoir"), "") && obj.getString("motifAvoir") != null) {
        motifAvoir = obj.getString("motifAvoir").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("montantAvoir"), "")
          && obj.getString("montantAvoir") != null) {
        montantAvoir = obj.getDouble("montantAvoir");
      }
      if (!Objects.equals(obj.getString("avoirStatut"), "")
          && obj.getString("avoirStatut") != null) {
        avoirStatut = obj.getBoolean("avoirStatut");
      }
      if (!Objects.equals(obj.getString("startDate"), "") && obj.getString("startDate") != null) {
        startDate = CrmUtils.convertStringToDate(obj.getString("startDate"));
      }
      if (!Objects.equals(obj.getString("endDate"), "") && obj.getString("endDate") != null) {
        endDate = CrmUtils.convertStringToLocalDateTime(obj.getString("endDate"));
      }
      if (!Objects.equals(obj.getString("reference"), "") && obj.getString("reference") != null) {
        reference = obj.getString("reference").trim().toLowerCase();
      }
      if (!Objects.equals(obj.getString("usedBy"), "") && obj.getString("usedBy") != null) {
        usedBy = obj.getLong("usedBy");
      }
      if (!Objects.equals(obj.getString("createdBy"), "") && obj.getString("createdBy") != null) {
        createdBy = obj.getLong("createdBy");
      }
      if (!Objects.equals(obj.getString("authorizationAdd"), "")
          && obj.getString("authorizationAdd") != null) {
        authorizationAdd = obj.getBoolean("authorizationAdd");
      }
      if (!Objects.equals(obj.getString("datePayementDebut"), "")
          && obj.getString("datePayementDebut") != null) {
        datePayementDebut =
            CrmUtils.convertStringToLocalDataTimeStart(obj.getString("datePayementDebut"));
      }
      if (!Objects.equals(obj.getString("datePayementFin"), "")
          && obj.getString("datePayementFin") != null) {
        datePayementFin = CrmUtils.convertStringToLocalDateTime(obj.getString("datePayementFin"));
      }
      if (obj.getString("typeAVr") != null   &&!Objects.equals(obj.getString("typeAVr"), "")
             ) {
    	  typeAVr =
                obj.getBoolean("typeAVr");
          }
      
    }
    switch (ordercolumnaram) {

      case 3:
        sort = "montantAvoir";
        break;
      case 4:
        sort = "motifAvoir";

        break;
      case 7:
        sort = "isClientPayed";
        break;
      case 6:
        sort = "canRevendeurViewed";
        break;
      case 8:
        sort = "has_bordereau";
        break;
      case 2:
        sort = "createdDate";
        break;
    }
    responseData = this.findAllAvoirAdminWithFilter(currentpage + 1, length, sort, orderdir,
        abonnement, motifAvoir, montantAvoir, avoirStatut, startDate, endDate, reference, usedBy,
        authorizationAdd, createdBy, datePayementDebut, datePayementFin , isPublich , typeAVr);
    if (responseData != null) {
      myHmapData.put("data", responseData.getContent());
      myHmapData.put("recordsTotal", responseData.getTotalElements());
      myHmapData.put("recordsFiltered", responseData.getTotalElements());
    }
    myHmapData.put("draw", draw);
    myHmapData.put("start", start);

    return myHmapData;
  }

  private Page<AvoirDto> findAllAvoirAdminWithFilter(int pageNo, int pageSize, String sortvar,
      String orderdir, String abonnement, String motifAvoir, Double montantAvoir,
      Boolean avoirStatut, Date startDate, Date endDate, String reference, Long usedBy,
      Boolean authorizationAdd, Long createdBy, Date datePayementDebut, Date datePayementFin , Boolean isPublish ,Boolean typeAVr) {
    // TODO Auto-generated method stub
    Sort sort = Sort.by("modifieddate");
    if (orderdir.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!orderdir.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    if(isPublish!= null && isPublish) {
        return this.avoirRepository.findAvoirDetailsWithFilter(pageable, abonnement, motifAvoir,
                montantAvoir, avoirStatut, startDate, endDate, reference, usedBy, authorizationAdd,
                createdBy, datePayementDebut, datePayementFin,typeAVr);
    }
    else if (isPublish != null && isPublish == false) {
        return this.avoirRepository.findAvoirDetailsWithFilterAndNotPublish(pageable, abonnement, motifAvoir,
                montantAvoir, avoirStatut, startDate, endDate, reference, usedBy, authorizationAdd,
                createdBy, datePayementDebut, datePayementFin,typeAVr);
    }
    else {
    	 return this.avoirRepository.findAvoirDetailsWithFilterAndRefused(pageable, abonnement, motifAvoir,
                 montantAvoir, avoirStatut, startDate, endDate, reference, usedBy, authorizationAdd,
                 createdBy, datePayementDebut, datePayementFin);
    }

  }

  @Override
  public ModelAndView exportListAvoirToExcel(Date startDate, Date endDate, String reference,
      Long usedBy, Boolean avoirStatut, Boolean authorizationAdd, Long createdBy,
      Double montantAvoir, String motifAvoir, String abonnement, Date DateDebutPayement,
      Date DateFinPayement,String isNotPublic , HttpServletRequest request, HttpServletResponse response) {
    // TODO Auto-generated method stub
    ModelAndView mav = new ModelAndView();

    List<AvoirClient> myListAvoir = new ArrayList<>();
    if(isNotPublic != null) {
    	  myListAvoir = avoirRepository.findAllAvoirToExportisNotPublic(startDate, endDate, reference, usedBy,
    		        avoirStatut, authorizationAdd, createdBy, montantAvoir, motifAvoir, abonnement,
    		        DateDebutPayement, DateFinPayement);
    }
    else {
        myListAvoir = avoirRepository.findAllAvoirToExport(startDate, endDate, reference, usedBy,
                avoirStatut, authorizationAdd, createdBy, montantAvoir, motifAvoir, abonnement,
                DateDebutPayement, DateFinPayement);
    }
  



    if (myListAvoir.size() > 0) {
      mav.setView(new AvoirClientExcelExport());
      mav.addObject("list", myListAvoir);
    } else {
      mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR); // Set the desired HTTP status code
      mav.addObject("errorMessage", "No data found");
      // Add an error message

      try {
        request.getRequestDispatcher("/AvoirClient/AllAvoirClient").forward(request, response);
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
  public List<AvoirClient> getAllAvoirByClient(Long clientid) {
    // TODO Auto-generated method stub
    return this.avoirRepository.findAllAvoirbyClient(clientid);
  }

  @Override
  public List<AvoirClient> findnonpayerfacture(String recherche, Long telephone) {
    // TODO Auto-generated method stub
    return this.avoirRepository.findAllAvoirbyPayementRecherche(recherche, telephone);
  }

  @Override
  public void downloadAvoir(Long avoirId, HttpServletResponse response) {
    // TODO Auto-generated method stub
    try {
      AvoirClient monAvoir = this.avoirRepository.getById(avoirId);

      File fileAvoir = new File(pathAvoir + CrmUtils.getYear() + "/" + CrmUtils.getMonth() + "/"
          + monAvoir.getRefAvoirClient() + ".pdf");
      if (!fileAvoir.exists()) {

        fileAvoir = createPDFAvoirA4(monAvoir);
      }
      // set file facure

      response
          .setContentType("application/x-pdf ; charset=" + Charset.forName("utf-8").displayName());
      response.setHeader("Content-disposition", "inline; filename=" + fileAvoir.getName());
      // get your file as InputStream
      InputStream targetStream = new FileInputStream(fileAvoir);
      // copy it to response's OutputStream
      org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
      response.flushBuffer();

      // close input stream file
      targetStream.close();
      // delete file
      // CrmUtils.deleteFile(file);

    } catch (IOException ex) {

      logger.error("FactureController.downloadPDFFactureA4 IOException: " + ex.getMessage());
      throw new RuntimeException("IOError writing file to output stream");
    }
  }

  @Override
  public File createPDFAvoirA4(AvoirClient monAvoir) {
    // TODO Auto-generated method stub
    File file1 = null;
    String nomfile = "";
    if (monAvoir != null && monAvoir.getAvoirId() != null) {
      try {
        List<AvoirClient> AvoirList = new ArrayList<>();
        AvoirList.add(monAvoir);
        file1 = ResourceUtils.getFile("classpath:reports/ficheAvoirA4.jrxml");
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(AvoirList);
        JasperReport jasperReport = JasperCompileManager.compileReport(file1.getAbsolutePath());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
        File pathFolder = new File(pathAvoir + CrmUtils.getYear() + "/" + CrmUtils.getMonth());
        if (!pathFolder.exists()) {
          pathFolder.mkdirs();
          pathFolder.setWritable(true);

        }

        nomfile = pathAvoir + CrmUtils.getYear() + "/" + CrmUtils.getMonth() + "/"
            + monAvoir.getRefAvoirClient() + ".pdf";

        JasperExportManager.exportReportToPdfFile(jasperPrint, nomfile);
        file1 = ResourceUtils.getFile(nomfile);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        logger.warn("error creation avoir: " + e.getMessage());

      }

    }
    return file1;
  }

  @Override
  public Map<String, Object> ajouterAvoir(Long clientId, String motifAvoir,
      List<entryAvoirClient> entryAvoirClient, String codeUser, String isClientPayed,
      String isUsedBrd, String hasRaccordement, String commentaireAvoir,
      Boolean  typeAVr ,String RefReclamation,List<String> RefFacture , String dateDebutCoupur,String dateFinCoupur ,
      String dateMiseService, String validePar) {
	  List<Facture>  myfact = null;
    logger.info(
        "Ajouter Avoir : Methode ajouterAvoir appelee avec les parametres : clientId={}, motifAvoir={}, montantHt={}, "
            + "baseTva={}, codeUser={}, isClientPayed={}, isUsedBrd={}",
        clientId, motifAvoir, codeUser, isClientPayed, isUsedBrd);

    Map<String, Object> response = new HashMap<>();
    Map<String, String> erreurMap = new HashMap<>();

    AvoirClient saveNewAvoir = new AvoirClient();

    Abonnement abonnmentAvoir = abonnementRepository.findAbonnementByClientid(clientId);
    if (abonnmentAvoir == null) {
      erreurMap.put("CLIENT_NOT_FOUND", "Client introuvable.");

      response.put("erreur", erreurMap);
      return response;
    }

    if (motifAvoir == null || motifAvoir.isEmpty()) {
      erreurMap.put("MOTIF_REQUIRED", "Le motif est requis pour cette opération.");
    }

    if (entryAvoirClient == null || entryAvoirClient.size() == 0) {
      erreurMap.put("DATA_NOT_VALID", "Les données fournies ne sont pas valides.");
    }

    if (isClientPayed.equals("True")) {
      if (codeUser.isEmpty() || codeUser == null) {
        erreurMap.put("USER_NOT_FOUND", "Utilisateur introuvable. Le code utilisateur est requis.");
      }
      if (isUsedBrd == null) {
        erreurMap.put("USED_BRD_NOT_SELECTED",
            "Vous devez choisir si l'avoir est utilisé dans bordureau ou non.");
      }
    }
    if(RefFacture != null  ) {
    	myfact =  factureRepository.getFactureByRef_facture(RefFacture);
    	  for (Facture fact : myfact) {
    	if(fact != null && fact.getEtat_facture()) {
    		   erreurMap.put("USED_BRD_NOT_SELECTED",
    		            "le facture choisir est deja payé");
    	}
    	if(fact == null){
    		   erreurMap.put("USED_BRD_NOT_SELECTED",
   		            "le facture choisir n'existe pas");
    	}
    }
    }
    if (erreurMap.size() > 0) {
      erreurMap.put("AVOIR_NOT_VALID", "AVOIR_NOT_VALID");
      logger.warn("Ajouter Avoir : La validation de l'avoir a echoue avec les erreurs : {}",
          erreurMap);

      response.put("statut", "false");
      response.put("erreur", erreurMap);
      return response;
    }

    if (abonnmentAvoir != null) {
      boolean isPayed = (isClientPayed != null && isClientPayed.equalsIgnoreCase("True"));

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userService.findUsersByEmail(currentUser);
   
      
        Optional<User> rev = null;

        AtomicReference<Double> TotalmontantTva = new AtomicReference<>(0.0);
        AtomicReference<Double> TotalmontantAvoir = new AtomicReference<>(0.0);
        AtomicReference<Double> TotalmontantHt = new AtomicReference<>(0.0);
        List<EntryAvoirClient> ListOfEntryAvoirClient = new ArrayList<EntryAvoirClient>();
        entryAvoirClient.forEach(el -> {
          EntryAvoirClient newEntryAvoirClient = new EntryAvoirClient();
          newEntryAvoirClient.setBaseTva(el.getBaseTva());
          Double montantTva = el.getMontantHt() * (el.getBaseTva() / 100);
          newEntryAvoirClient.setMontantTva(montantTva);

          newEntryAvoirClient.setMontantHt(el.getMontantHt());
          Double montantAvoir = montantTva + el.getMontantHt();
          newEntryAvoirClient.setMontantTTc(montantAvoir);
          newEntryAvoirClient.setLibele(commentaireAvoir);
          EntryAvoirClientRepository.save(newEntryAvoirClient);
          TotalmontantHt.getAndUpdate(value -> value + el.getMontantHt());
          TotalmontantTva.getAndUpdate(value -> value + montantTva);
          TotalmontantAvoir.getAndUpdate(value -> value += montantAvoir);
          ListOfEntryAvoirClient.add(newEntryAvoirClient);
        });
        saveNewAvoir.setAbonnement(abonnmentAvoir);
        saveNewAvoir.setTimbrefiscale(timbrefiscale);
        saveNewAvoir.setValidePar(validePar);

        if(entryAvoirClient.size()>0) {
            saveNewAvoir.setBaseTva(entryAvoirClient.get(0).getBaseTva());
        }
        saveNewAvoir.setMontantAvoir(TotalmontantAvoir.get() + timbrefiscale);
        saveNewAvoir.setMontantHt(TotalmontantHt.get());
        String refAvoir = generateReferenceAvoir.generateWithPrefix();
        saveNewAvoir.setRefAvoirClient(refAvoir);
        saveNewAvoir.setMontantTva(TotalmontantTva.get());
        saveNewAvoir.setMotifAvoir(motifAvoir);
        saveNewAvoir.setHas_bordereau(false);
        
        if(dateDebutCoupur != null && !dateDebutCoupur.isEmpty()) {
            saveNewAvoir.setDateDebutCoupure(CrmUtils.convertStringToDate(dateDebutCoupur));

        }
        if(dateMiseService != null && !dateMiseService.isEmpty()) {
            saveNewAvoir.setDateMiseService(CrmUtils.convertStringToDate(dateMiseService));

        }
        if(dateFinCoupur != null && !dateFinCoupur.isEmpty())
        {
            saveNewAvoir.setDateFinCoupure(CrmUtils.convertStringToDate(dateFinCoupur));

        }
        
        saveNewAvoir.setCreePar(user);
        saveNewAvoir.setCommentaireAvoir(commentaireAvoir);
        saveNewAvoir.setRefReclamation(RefReclamation);
        saveNewAvoir.setIsJestCo(typeAVr);
        saveNewAvoir.setFacture(String.join(",", RefFacture));
        if (isPayed) {
          if (codeUser != null && !codeUser.isEmpty() && isUsedBrd != null) {
            rev = userRepository.findByCodeUser(codeUser);
            if (rev.isPresent() == false) {
              response.put("statut", "false");
              erreurMap.put("USER_NOT_FOUND", "Utilisateur avec le code fourni non trouvé.");
              response.put("erreur", erreurMap);
              return response;
            }
            saveNewAvoir.setUsedBy(rev.get());
          }
          saveNewAvoir.setIsClientPayed(true);
          if (isUsedBrd != null && isUsedBrd.equals("False")) {
            saveNewAvoir.setCanRevendeurViewed(false);
          }
        }
        if (hasRaccordement.equals("True")) {
          saveNewAvoir.setHasRaccordment(true);
        }
        saveNewAvoir.setAvoiClientEntry(ListOfEntryAvoirClient);
        AvoirClient avoir = avoirRepository.save(saveNewAvoir);

   
        if (hasRaccordement.equals("True")) {
          Integer trancheRaccordement = abonnmentAvoir.getTrancheRaccordement() + 1;
          abonnmentAvoir.setTrancheRaccordement(trancheRaccordement);
          abonnementRepository.save(abonnmentAvoir);
        }
      }
    }

    response.put("statut", "true");
    logger.info("L'avoir a été ajouté avec succès : {}", saveNewAvoir);

    return response;
  }

  RecuNumeroSequence genererCodeRecu(User user, Double montant) {
    RecuNumeroSequence recuPayementSequence = new RecuNumeroSequence();
    String codeRecu = recuNumeroSequenceService.generateCode(user);
    recuPayementSequence.setCodePayement(codeRecu);
    recuPayementSequence.setMontantTotal(montant);
    recuPayementSequence.setUser(user);
    recuNumeroSequenceRepository.save(recuPayementSequence);
    return recuPayementSequence;
  }

  @Override
  public HashMap<String, Object> EditAvoirToPayed(Long avoirId, String codeUser,
      String isClientPayed, String isUsedBrd) {
    // TODO Auto-generated method stub
    HashMap<String, Object> response = new HashMap<>();
    Map<String, String> erreurMap = new HashMap<>();

    if (isClientPayed.equals("True")) {
      if (codeUser.isEmpty() || codeUser == null) {
        erreurMap.put("erreur", "Utilisateur introuvable. Le code utilisateur est requis.");
        response.put("statut", "false");
      }
      if (isUsedBrd == null) {
        erreurMap.put("erreur", "Vous devez choisir si l'avoir est utilisé dans bordureau ou non.");
        response.put("statut", "false");

      }
    }

    if (erreurMap.size() > 0) {

      logger.warn("Ajouter Avoir : La validation de l'avoir a echoue avec les erreurs : {}",
          erreurMap);

      response.put("statut", "false");
      response.put("erreur", erreurMap);
      return response;
    }

    boolean isPayed = (isClientPayed != null && isClientPayed.equalsIgnoreCase("True"));
    AvoirClient avoir = avoirRepository.findAvoirClientByAvoirId(avoirId);
    Optional<User> rev = null;
    rev = userRepository.findByCodeUser(codeUser);
    if (isPayed) {
      if (codeUser != null && !codeUser.isEmpty() && isUsedBrd != null) {



        if (rev.isPresent() == false) {
          response.put("statut", "false");
          erreurMap.put("USER_NOT_FOUND", "Utilisateur avec le code fourni non trouvé.");
          response.put("erreur", erreurMap);
          return response;
        }
        avoir.setUsedBy(rev.get());
      }
      avoir.setIsClientPayed(true);
      if (isUsedBrd != null && isUsedBrd.equals("False")) {
        avoir.setCanRevendeurViewed(false);
      }



      Payement payement = new Payement();
      payement.setUser(rev.get());
      payement.setRecuNumeroSequence(genererCodeRecu(rev.get(), -avoir.getMontantAvoir()));

      payement.setIschifcoPayed(false);
      payement.setAvoirClient(avoir);
      payement.setMontant(avoir.getMontantAvoir());
      payement.setTypePayment("espèce");
      payementRepository.save(payement);

      if (rev != null && (rev.get().getTypeUser().equals("REVENDEUR")
          || rev.get().getTypeUser().equals(UserTypeConstant.POS))) {
        Encaissement encaissement = new Encaissement();
        encaissement.setTypeDePayment("espèce");
        encaissement.setUser(rev.get());
        encaissement.setPayement(payement);
        encaissement.setMontantFacture(avoir.getMontantAvoir());
        encaissement.setAvoirClient(avoir);
        encaissement.setClient(avoir.getAbonnement().getClientid());
        encaissement.setDateDebutFacturation(avoir.getCreatedDate());
        encaissementRepository.save(encaissement);
      }
      response.put("statut", "true");
      logger.info("L'avoir a été ajouté avec succès : {}");

      return response;
    }
    return response;
  }


  @Override
  public List<AvoirClient> findnonpayerfactureAvoirNotgreatherThenFacture(String recherche,
      Long telephone, Double montantTotalFacture) {
    // TODO Auto-generated method stub
    List<AvoirClient> nonpayerfactureAvoir = findnonpayerfacture(recherche, telephone);
    List<AvoirClient> result = new ArrayList<>();
    double currentSum = 0;

    // Parcours de la liste des avoirs pour sélectionner ceux qui ne dépassent pas le montant total
    for (AvoirClient avoir : nonpayerfactureAvoir) {
      if ((currentSum + avoir.getMontantAvoir()) <= montantTotalFacture) {
        result.add(avoir);
        currentSum += avoir.getMontantAvoir();
      }
    }

    return result;
  }

@Override
public void avoirPublish(Long avoirId) {
	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (!(authentication instanceof AnonymousAuthenticationToken)) {
	      String currentUser = authentication.getName();
	      User user = userService.findUsersByEmail(currentUser);
	AvoirClient avoir=	avoirRepository.findAvoirClientByAvoirId(avoirId);
	avoir.setIsPublish(true);
	avoir.setPublierPar(user);
	
    if (avoir.getIsClientPayed()) {
    	  Payement payement = new Payement();
          Encaissement encaissement = new Encaissement();
        payement.setUser(avoir.getUsedBy());
        payement.setRecuNumeroSequence(genererCodeRecu(avoir.getUsedBy(), -avoir.getMontantAvoir()));

        payement.setIschifcoPayed(false);
        payement.setAvoirClient(avoir);
        payement.setMontant(avoir.getMontantAvoir());
        payement.setTypePayment("espèce");
        payementRepository.save(payement);

        if (avoir.getUsedBy() != null && (avoir.getUsedBy().getTypeUser().equals("REVENDEUR")
            || avoir.getUsedBy().getTypeUser().equals(UserTypeConstant.POS))) {
          encaissement.setTypeDePayment("espèce");
          encaissement.setUser(avoir.getUsedBy());
          encaissement.setPayement(payement);
          encaissement.setMontantFacture(avoir.getMontantAvoir());
          encaissement.setAvoirClient(avoir);
          encaissement.setClient(avoir.getAbonnement().getClientid());
          encaissement.setDateDebutFacturation(avoir.getCreatedDate());
          encaissementRepository.save(encaissement);
        }
      }
	avoirRepository.save(avoir);
	    }
}


@Override
public HashMap<String, Object> verifMontantAndFactureExiste(String montant, List<String> referenceFacture,
		String referenceReclamation, boolean typeAvr) {
	HashMap<String, Object> response = new HashMap<>();
   String erreur="";

	  List<AvoirClient>  listAvoirReference = null ;
	if(erreur.trim().isEmpty() &&   referenceReclamation != null && !referenceReclamation.isEmpty()) {
		 listAvoirReference  =	avoirRepository.findAllAvoirByRefReclamation(referenceReclamation);
		 if(listAvoirReference != null &&  listAvoirReference.size() > 0) {
			 erreur ="la reference reclamation deja utiliser.";
             
		}


	}
	 if (erreur.trim().isEmpty()  && referenceFacture != null && !referenceFacture.isEmpty()) {
		 listAvoirReference  =	avoirRepository.findAllAvoirByFactureInAndIsJestCoAndIsPublishNot(referenceFacture , typeAvr );
		 if(listAvoirReference != null &&  listAvoirReference.size() > 0) {
			 erreur= "La référence de cette facture est déjà utilisée pour ce type";
            
		}
		 List<Facture>  myfact =  factureRepository.getFactureByRef_facture(referenceFacture);
		if(erreur.trim().isEmpty() && myfact == null ) {
			erreur= "la  facture n'existe pas ";
		}
	 
		if(erreur.trim().isEmpty()) {
			  List<AvoirClient>  listAvoirByFactureReference =	avoirRepository.findAllAvoirByFactureIn(referenceFacture);
           
              
			Double sommefact   = myfact.stream()
			        .mapToDouble(Facture::getMontant_payer)
			        .sum();
			if(listAvoirByFactureReference != null ) {
				Double sommefactAvoir   = listAvoirByFactureReference.stream()
				        .mapToDouble(AvoirClient::getMontantAvoir)
				        .sum();	
				if((sommefact - sommefactAvoir)   < Double.parseDouble(montant) ) {
					erreur= "le montant de l'avoir est supérieur au  montant  des factures   ..";

				}
			}

			if(myfact!= null && sommefact -1 < Double.parseDouble(montant)   ) {
		
			erreur= "le montant de l'avoir est supérieur au montant  des factures";
		} 
		}
	}
	
if(!erreur.trim().isEmpty()) {
    response.put("statut",false);

}
else {
    response.put("statut",true);

}
    response.put("erreur", erreur);
	return response;
}


@Override
public void avoirRefused(Long avoirId , String commentaire) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userService.findUsersByEmail(currentUser);
	AvoirClient myavoir=	avoirRepository.findAvoirClientByAvoirId(avoirId);
	myavoir.setIsPublish(null);
	myavoir.setRaisonAannulation(commentaire);
	myavoir.setPublierPar(user);
	avoirRepository.save(myavoir);
    }
}

@Override
public void updateAvoirClientNotPublic(AvoirClient dto, List<String> refFacture , String dateMiseService ,String dateDebutCoupur , String dateFinCoupur) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userService.findUsersByEmail(currentUser);
	AvoirClient myavoir=	avoirRepository.findAvoirClientByAvoirId(dto.getAvoirId());
	myavoir.setMontantAvoir(dto.getMontantAvoir() + timbrefiscale);
	myavoir.setMontantHt(dto.getMontantHt());
    myavoir.setMontantTva(dto.getMontantTva());
    myavoir.setBaseTva(dto.getBaseTva());
    myavoir.setMotifAvoir(dto.getMotifAvoir());
    myavoir.setCommentaireAvoir(dto.getCommentaireAvoir());
    myavoir.setIsJestCo(dto.getIsJestCo());
    
    if(dateDebutCoupur != null && !dateDebutCoupur.isEmpty()) {
    	myavoir.setDateDebutCoupure(CrmUtils.convertStringToDate(dateDebutCoupur));

    }
    if(dateMiseService != null && !dateMiseService.isEmpty()) {
    	myavoir.setDateMiseService(CrmUtils.convertStringToDate(dateMiseService));

    }
    if(dateFinCoupur != null && !dateFinCoupur.isEmpty())
    {
    	myavoir.setDateFinCoupure(CrmUtils.convertStringToDate(dateFinCoupur));

    }
   
    myavoir.setFacture(refFacture.toString());

	avoirRepository.save(myavoir);

	avoirHistoryService.saveHistoryToDataBase(user ,myavoir.getAvoirId() , "avoir editer ");
    }
}

}
