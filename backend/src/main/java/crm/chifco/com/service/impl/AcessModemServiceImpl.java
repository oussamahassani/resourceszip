package crm.chifco.com.service.impl;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import crm.chifco.com.model.ModemAccess;
import crm.chifco.com.model.ModemHistory;
import crm.chifco.com.model.ModemHistoryImport;
import crm.chifco.com.model.ModemHistoryImportFile;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ModemAccessRepository;
import crm.chifco.com.service.AcessModemService;
import crm.chifco.com.service.DataFromCsv;

@Service("acessModemService")
public class AcessModemServiceImpl implements AcessModemService {
  private static final Logger logger = LogManager.getLogger(DataFromCsv.class);

  @Autowired
  private ModemAccessRepository modemAccessRepository;

  @Override
  public Page<ModemAccess> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo, pageSize);
    return this.modemAccessRepository.findAll(pageable);
  }

  @Override
  public ModemAccess findStatutByNomstatut(Boolean statut) {
    // TODO Auto-generated method stub
    return this.modemAccessRepository.findFirstModemAccessByStatus(statut);
  }

  @Override
  public Map<String, List<ModemAccess>> importerModemfromExcel(MultipartFile file, User user) {

    List<ModemAccess> modems = new ArrayList<>();
    Map<String, List<ModemAccess>> result = new HashMap<>();

    DataFormatter formatter = new DataFormatter();

    try {
      InputStream inputStream = new BufferedInputStream(file.getInputStream());

      Workbook workbook = new XSSFWorkbook(inputStream);
      Sheet sheet = workbook.getSheetAt(0);

      // nombre de ligne
      int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;
      if (rowCount > 700) {
        result.put("EXCEL_ROW_LIMIT_ERROR", modems);
        return result;
      }

      // liste des modems non valide
      List<ModemAccess> modemNonValide = new ArrayList<>();

      // Liste des modems valide
      List<ModemAccess> modemValide = new ArrayList<>();

      // liste modem non valide dans fichier excel (en cas doublon numéro de serie)
      List<ModemAccess> modemNotValidInExcel = new ArrayList<>();

      // liste pour historique modem

      String action = "Creation";
      List<ModemHistory> modemHistories = new ArrayList<>();

      // creation objet modemImportXlsHistory : historique de l'importation
      ModemHistoryImport modemImportXlsHistory = new ModemHistoryImport();
      modemImportXlsHistory.setOrigineNameFile(file.getOriginalFilename());
      modemImportXlsHistory.setTotalRow(sheet.getLastRowNum());
      modemImportXlsHistory.setUser(user);

      // Creation ligne modem hisorique
      List<ModemHistoryImportFile> listLigneModemImportXlsHistories = new ArrayList<>();

      Iterator<Row> rowIterator = sheet.iterator();
      while (rowIterator.hasNext()) {
        logger.info("ligne suivant modem import excel : " + rowIterator.hasNext());
        Row row = rowIterator.next();
        if (row.getRowNum() == 0) {
          int i = 0;
          String[] expectedValues = {"Modéle", "Email", "Password"};
          for (String expectedValue : expectedValues) {
            if (formatter.formatCellValue(row.getCell(i)).equals(expectedValue) == false) {
              result.put("EXCEL_TABLE_ERROR", modems);
              return result;
            }
            i++;
          }
          continue;
        }

        ModemAccess modem = new ModemAccess();;
        modem.setModelModem(formatter.formatCellValue(row.getCell(0)));
        modem.setEmail(formatter.formatCellValue(row.getCell(1)));
        modem.setPassword(formatter.formatCellValue(row.getCell(2)));

        // validation les données
        String erreurMsg = "";
        boolean modemNotValid = false;

        if (modem.getModelModem().equals("")) {
          erreurMsg += "( model )";
          modemNotValid = true;
        }
        if (modem.getEmail().equals("")) {
          erreurMsg += "( email )";
          modemNotValid = true;
        }
        if (modem.getPassword().equals("")) {
          erreurMsg += "( mot de passe )";
          modemNotValid = true;
        }
        logger.info("import modem ligne " + row.getRowNum() + " : " + modem);
        if (modemNotValid == true) {
          ModemHistoryImportFile ligneModemImportXlsHistory = new ModemHistoryImportFile();
          ligneModemImportXlsHistory.setStatus("Erreur");
          ligneModemImportXlsHistory.setDescription(erreurMsg);
          listLigneModemImportXlsHistories.add(ligneModemImportXlsHistory);
          modemNonValide.add(modem);
        } else {
          modems.add(modem);
        }
      }
      logger.info("nombre modem dans excel" + modems.size());

      // Nettoyez le fichier Excel en supprimant les doublons login modem et de numéro de série
      List<ModemAccess> listeModemSansDoublons = new ArrayList<>();
      for (ModemAccess modem : modems) {
        long count = modems.stream()
            .filter(
                m -> m.getEmail().equals(modem.getEmail()) || m.getEmail().equals(modem.getEmail()))
            .count();
        if (count == 1) {
          listeModemSansDoublons.add(modem);
        } else {
          ModemHistoryImportFile ligneModemImportXlsHistory = new ModemHistoryImportFile();
          ligneModemImportXlsHistory.setNumSerie(modem.getEmail());
          ligneModemImportXlsHistory.setStatus("erreur Excel");
          ligneModemImportXlsHistory.setDescription("Ce modem est en double dans le fichier Excel");
          listLigneModemImportXlsHistories.add(ligneModemImportXlsHistory);
          modemNotValidInExcel.add(modem);
          logger.info("modem en double dans excel" + modem.getModemAccessId());
        }
      }

      if (modemNotValidInExcel.size() > 0) {
        modems = listeModemSansDoublons;
      }

      List<String> listNumSerie =
          modems.stream().map(ModemAccess::getEmail).collect(Collectors.toList());

      List<String> listLogin =
          modems.stream().map(ModemAccess::getEmail).collect(Collectors.toList());

      // liste de modems existants
      List<ModemAccess> listeModem = modemAccessRepository.findListModemAccessByEmail(listLogin);

      if (listeModem.size() == 0) {
        modemValide = modems;
      } else {

        for (ModemAccess m : modems) {
          long count = listeModem.stream().filter(
              base -> base.getEmail().equals(m.getEmail()) || base.getEmail().equals(m.getEmail()))
              .count();

          if (count > 0) {
            ModemHistoryImportFile ligneModemImportXlsHistory = new ModemHistoryImportFile();
            ligneModemImportXlsHistory.setNumSerie(m.getEmail());
            ligneModemImportXlsHistory.setStatus("Erreur");
            ligneModemImportXlsHistory.setDescription(
                "Il y a modem enregistrés avec le même numéro de série ou la méme email");
            listLigneModemImportXlsHistories.add(ligneModemImportXlsHistory);
            modemNonValide.add(m);
            logger.info("modem not valid : " + m);

          } else {
            // historique de l'importation excel
            ModemHistoryImportFile ligneModemImportXlsHistory = new ModemHistoryImportFile();
            ligneModemImportXlsHistory.setNumSerie(m.getEmail());
            ligneModemImportXlsHistory.setStatus("Succés");
            ligneModemImportXlsHistory.setDescription("Ajout effectué avec succès");
            listLigneModemImportXlsHistories.add(ligneModemImportXlsHistory);

            // historique modem
            /*
             * ModemHistory modemHistory = new ModemHistory(); modemHistory.setAction(action);
             * modemHistory.setModem(m); modemHistory.setUser(user);
             * modemHistories.add(modemHistory);
             */
            modemValide.add(m);
            logger.info("modem valid : " + m);
          }
        }

      }

      // copier le ficher excel dans serveur
      /*
       * DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm_ss"); LocalDateTime
       * now = LocalDateTime.now(); String folder = pathModemxlsx; File uploadDir = new
       * File(folder); if (!uploadDir.exists()) { uploadDir.mkdirs(); } String nameFile =
       * "ImportationModem_" + dtf.format(now) + ".xlsx"; Path path = Paths.get(pathModemxlsx +
       * nameFile); Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
       * modemImportXlsHistory.setNameFile(nameFile);
       */
      modemAccessRepository.saveAll(modemValide);
      /*
       * if (modemValide.size() > 0) { if (modemHistories.size() > 0) {
       * modemHistoryRepository.saveAll(modemHistories); } else { modemValide.forEach(m -> {
       * ModemHistory modemHistory = new ModemHistory(); modemHistory.setAction(action);
       * modemHistory.setModem(m); modemHistory.setUser(user); modemHistories.add(modemHistory); });
       * modemHistoryRepository.saveAll(modemHistories); } }
       * 
       * modemImportXlsHistory.setSuccessRow(modemValide.size());
       * modemImportXlsHistory.setErrorRow(modemNonValide.size() + modemNotValidInExcel.size());
       * modemImportXlsHistory.setLigneModemImportXlsHistories(listLigneModemImportXlsHistories);
       * modemImportXlsHistoryRepository.save(modemImportXlsHistory);
       * logger.info("historique modem :  " + modemImportXlsHistory.getIdModemImportXlsHistory());
       */

      workbook.close();

      result.put("modemValide", modemValide);
      result.put("modemNonValide", modemNonValide);
      result.put("modemNotValidInExcel", modemNotValidInExcel);

      logger.info("Importation modem depuis excel a été effectuée avec succès : " + modemValide);
      logger.info("la liste des modems qui n'ajoute pas : " + modemNonValide);
      logger.info("la liste des modems doublan dans exel : " + modemNotValidInExcel);

    } catch (Exception e) {
      logger.error(" Importation modem excel Exception: " + e.getMessage());
      return null;
    }
    return result;


  }
}
