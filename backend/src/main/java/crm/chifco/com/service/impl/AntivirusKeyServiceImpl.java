package crm.chifco.com.service.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AntivirusKey;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.AbonnementRepository;
import crm.chifco.com.repository.AntivirusKeyRepository;
import crm.chifco.com.service.AntiVirusKeyExcelExport;
import crm.chifco.com.service.AntivirusKeyService;
import crm.chifco.com.service.DataFromCsv;
import crm.chifco.com.utils.CrmUtils;

@Service
public class AntivirusKeyServiceImpl implements AntivirusKeyService {
  private static final Logger logger = LogManager.getLogger(DataFromCsv.class);

  @Autowired
  private AntivirusKeyRepository antivirusKeyRepository;

  @Autowired
  private AbonnementRepository abonnementRepository;

  @Override
  public HashMap<String, Object> getAll(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche) {
    // TODO Auto-generated method stub

    String key = null;
    String referenceClient = null;
    Boolean etat = null;
    Boolean statut = null;
    Date startAffectationDate = null;
    Date endAffectationDate = null;
    Date startCreatedDate = null;
    Date endCreatedDate = null;
    String type = null;

    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (!Objects.equals(obj.getString("key"), "") && obj.getString("key") != null) {
        key = obj.getString("key").trim();
      }
      if (!Objects.equals(obj.getString("referenceClient"), "")
          && obj.getString("referenceClient") != null) {
        referenceClient = obj.getString("referenceClient").trim();
      }
      if (!Objects.equals(obj.getString("etat"), "") && obj.getString("etat") != null) {
        etat = obj.getString("etat").trim().equals("True") ? true : false;
      }
      if (!Objects.equals(obj.getString("statut"), "") && obj.getString("statut") != null) {
        statut = obj.getString("statut").trim().equals("True") ? true : false;
      }
      if (!Objects.equals(obj.getString("startAffectedDate"), "")
          && obj.getString("startAffectedDate") != null) {
        startAffectationDate = CrmUtils.convertStringToDate(obj.getString("startAffectedDate"));
      }
      if (!Objects.equals(obj.getString("endAffectedDate"), "")
          && obj.getString("endAffectedDate") != null) {
        endAffectationDate =
            CrmUtils.convertStringToLocalDateTime(obj.getString("endAffectedDate"));
      }
      if (!Objects.equals(obj.getString("startCreatedDate"), "")
          && obj.getString("startCreatedDate") != null) {
        startCreatedDate = CrmUtils.convertStringToDate(obj.getString("startCreatedDate"));
      }
      if (!Objects.equals(obj.getString("endCreatedDate"), "")
          && obj.getString("endCreatedDate") != null) {
        endCreatedDate = CrmUtils.convertStringToLocalDateTime(obj.getString("endCreatedDate"));
      }
      if (!Objects.equals(obj.getString("type"), "") && obj.getString("type") != null) {
        type = obj.getString("type");
      }
    }

    int currentpage = start / length;

    Page<AntivirusKey> responseData = null;
    HashMap<String, Object> myHmapData = new HashMap<>();

    Pageable pageable = PageRequest.of(currentpage, length);
    responseData = antivirusKeyRepository.findAllFiltrer(pageable, key, referenceClient, etat,
        statut, startAffectationDate, endAffectationDate, startCreatedDate, endCreatedDate, type);

    if (responseData != null) {
      myHmapData.put("data", responseData.getContent());
      myHmapData.put("recordsTotal", responseData.getTotalElements());
      myHmapData.put("recordsFiltered", responseData.getTotalElements());
    }
    myHmapData.put("draw", draw);
    myHmapData.put("start", start);

    return myHmapData;
  }

  public Map<String, List<AntivirusKey>> importerKey(MultipartFile file, User user) {

    List<AntivirusKey> antivirusKeys = new ArrayList<>();
    Map<String, List<AntivirusKey>> result = new HashMap<>();

    DataFormatter formatter = new DataFormatter();

    try {
      InputStream inputStream = new BufferedInputStream(file.getInputStream());

      Workbook workbook = new XSSFWorkbook(inputStream);
      Sheet sheet = workbook.getSheetAt(0);

      // liste des modems non valide
      List<AntivirusKey> antivirusKeyNonValide = new ArrayList<>();

      // Liste des modems valide
      List<AntivirusKey> antivirusKeyValide = new ArrayList<>();

      // liste modem non valide dans fichier excel (en cas doublon numéro de serie)
      List<AntivirusKey> antivirusKeyNotValidInExcel = new ArrayList<>();

      logger.info(
          "Démarrage de l'importation des clés antivirus depuis le fichier Excel par l'utilisateur : "
              + user.getCodeUser());

      Iterator<Row> rowIterator = sheet.iterator();
      while (rowIterator.hasNext()) {
        logger.info("Ligne suivante modem import excel : " + rowIterator.hasNext());
        Row row = rowIterator.next();
        if (row.getRowNum() == 0) {
          int i = 0;
          String[] expectedValues = {"Clé", "Durée de validité (mois)", "Type"};
          for (String expectedValue : expectedValues) {
            if (formatter.formatCellValue(row.getCell(i)).equals(expectedValue) == false) {
              result.put("EXCEL_TABLE_ERROR", antivirusKeys);
              return result;
            }
            i++;
          }
          continue;
        }

        AntivirusKey antivirusKey = new AntivirusKey();
        antivirusKey.setLicenseKey(formatter.formatCellValue(row.getCell(0)));

        // validation les données
        boolean antivirusKeyNotValid = false;
        if (formatter.formatCellValue(row.getCell(0)).equals("")) {
          antivirusKeyNotValid = true;
        }
        if (antivirusKeyNotValid == true) {
          logger.warn(
              "Clé antivirus non valide : " + formatter.formatCellValue(row.getCell(0)).equals(""));
          antivirusKeyNonValide.add(antivirusKey);
        } else {
          logger.info("Clé antivirus valide : " + antivirusKey.getLicenseKey());
          antivirusKey.setActive(true);
          antivirusKey.setCreatedBy(user);

          String cellValue = formatter.formatCellValue(row.getCell(1));
          try {
            int duree = Integer.parseInt(cellValue);
            antivirusKey.setDuree(duree);
          } catch (NumberFormatException e) {
            logger.error("Erreur de conversion : la durée n'est pas un entier valide.");
            result.put("EXCEL_TABLE_ERROR", antivirusKeys);
            return result;
          }
          antivirusKey.setDuree(Integer.parseInt(formatter.formatCellValue(row.getCell(1))));
          String type = formatter.formatCellValue(row.getCell(2));
          antivirusKey.setType(type);
          antivirusKeyValide.add(antivirusKey);
        }
      }

      // Nettoyez le fichier Excel en supprimant les doublons ( clé )
      List<AntivirusKey> listeCléSansDoublons = new ArrayList<>();
      for (AntivirusKey antivirusKey : antivirusKeyValide) {
        long count = antivirusKeyValide.stream()
            .filter(c -> c.getLicenseKey().equals(antivirusKey.getLicenseKey())).count();
        if (count == 1) {
          listeCléSansDoublons.add(antivirusKey);
        } else {
          logger.info("Clé antivirus a été trouvée en double dans le fichier Excel : "
              + antivirusKey.getLicenseKey());

          antivirusKeyNotValidInExcel.add(antivirusKey);
        }
      }

      if (antivirusKeyNotValidInExcel.size() > 0) {
        antivirusKeyValide = listeCléSansDoublons;
      }

      List<String> listClé =
          antivirusKeyValide.stream().map(AntivirusKey::getLicenseKey).collect(Collectors.toList());

      List<AntivirusKey> foundKeys = antivirusKeyRepository.findAllByListClé(listClé);

      antivirusKeyNonValide.addAll(foundKeys);

      if (foundKeys.size() == 0) {
        antivirusKeyRepository.saveAll(antivirusKeyValide);
        logger.info("Toutes les clés ont été sauvegardées avec succès !");
      } else {
        antivirusKeyValide.removeIf(akey -> foundKeys.stream()
            .anyMatch(avKey -> avKey.getLicenseKey().equals(akey.getLicenseKey())));
        antivirusKeyRepository.saveAll(antivirusKeyValide);
        logger.info("Les clés sans doublons ont été sauvegardées avec succès !");
      }

      logger.info("Importation des clés antivirus terminée.");

      workbook.close();

      result.put("antiVirusKeysValide", antivirusKeyValide);
      result.put("antiVirusKeysNonValide", antivirusKeyNonValide);
      result.put("antiVirusKeysNotValidInExcel", antivirusKeyNotValidInExcel);

      return result;

    } catch (Exception e) {
      logger.error(" Importation modem excel Exception: " + e.getMessage());
      return null;
    }
  }

  @Override
  public boolean changeEtat(Long id) {
    // TODO Auto-generated method stub
    logger.info("Changement d'état de clé antiVirus pour l'ID : " + id);
    this.antivirusKeyRepository.changeEtat(id);
    return true;
  }

  @Override
  public HashMap<String, String> affecter(Long keyId, String referenceClient, User user,
      String type) {
    // TODO Auto-generated method stub

    // Affichez les paramètres dans le journal
    logger.info("Début de l'affectation de la clé antivirus.");
    logger.info("keyId : " + keyId);
    logger.info("referenceClient : " + referenceClient);
    logger.info("user : " + user);

    HashMap<String, String> result = new HashMap<>();

    Abonnement abonnement = abonnementRepository.findAbonnementByReferenceClient(referenceClient);
    if (abonnement == null) {
      result.put("CLIENT_NOT_FOUND", "Le client n'a pas été trouvé.");
      logger.error("Le client avec la référence '" + referenceClient + "' n'a pas été trouvée.");
      return result;
    }

    // Pour vérifier si le client a déjà une clé ou non,
    AntivirusKey getByClient =
        antivirusKeyRepository.getKeyByClient(abonnement.getClientid(), type);
    if (getByClient != null) {
      result.put("CLIENT_ALREADY_HAS_KEY", "Le client a déjà une clé.");
      logger.error("Le client avec la référence '" + referenceClient + "' a déjà une clé.");
      return result;
    }

    AntivirusKey antivirusKey = antivirusKeyRepository.getById(keyId);
    if (antivirusKey == null) {
      result.put("KEY_NOT_FOUND", "La clé n'a pas été trouvé.");
      logger.error("La clé avec l'ID '" + keyId + "' n'a pas été trouvée.");
      return result;
    }

    if (antivirusKey.isActive() == false) {
      result.put("KEY_DISABLE", "La clé est désactiver.");
      logger.error("La clé avec l'ID '" + keyId + "' est désactivée.");
      return result;
    }

    if (antivirusKey.getAbonnement() != null) {
      result.put("KEY_ALREADY_USED", "La clé a déjà été utilisée.");
      logger.error("La clé avec l'ID '" + keyId + "' a déjà été utilisée.");
      return result;
    }

    antivirusKey.setAbonnement(abonnement);
    antivirusKey.setDateAffectation(new Date());
    antivirusKey.setAffectedBy(user);
    antivirusKeyRepository.save(antivirusKey);

    result.put("KEY_ASSIGN_SUCCESS", "La clé a été affectée avec succès.");
    logger
        .info("L'affectation de la clé avec l'ID '" + keyId + "' pour le client avec la référence '"
            + referenceClient + "' a été effectuée avec succès.");

    logger.info("Fin de l'affectation de la clé antivirus.");
    return result;

  }

  @Override
  public ModelAndView exportListClé(HttpServletRequest request, HttpServletResponse response,
      String key, String referenceClient, Boolean etat, Boolean statut, Date startAffectedDate,
      Date endAffectedDate, Date startCreatedDate, Date endCreatedDate, String type) {
    // TODO Auto-generated method stub
    ModelAndView mav = new ModelAndView();

    List<AntivirusKey> myList = new ArrayList<>();

    myList = antivirusKeyRepository.findAllExportExcel(key, referenceClient, etat, statut,
        startAffectedDate, endAffectedDate, startCreatedDate, endCreatedDate, type);
    if (myList.size() > 0) {
      mav.setView(new AntiVirusKeyExcelExport());
      mav.addObject("list", myList);
    } else {
      mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR); // Set the desired HTTP status code
      mav.addObject("errorMessage", "No data found");
      // Add an error message

      try {
        request.getRequestDispatcher("/antiVirus/antivirus_keys_page").forward(request, response);
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

  public List<String> getAllAntivirusTypes() {
    return antivirusKeyRepository.findDistinctTypes();
  }
}
