package crm.chifco.com.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RapportExportExcelService {

  @Autowired
  private RapportService rapportService;

  private static final String[] MOIS = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
      "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};

  private static final int[] JOURS_PAR_MOIS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  public byte[] exportRapportToExcel(int annee) throws IOException {
    Map<String, Object> dashboardData = rapportService.getDashboardData(annee);

    List<Object[]> globalStats = (List<Object[]>) dashboardData.get("statistiques_globales");
    List<Map<String, Object>> chefSecteurStats =
        (List<Map<String, Object>>) dashboardData.get("statistiques_par_chef");
    List<Object[]> facturesStats = (List<Object[]>) dashboardData.get("factures_payees_apres_3jrs");
    List<Object[]> visitesStats = (List<Object[]>) dashboardData.get("visites");

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Rapport " + annee);
      Map<String, CellStyle> styles = createStyles(workbook);
      int rowNum = 0;

      Row headerRow = sheet.createRow(rowNum++);
      Cell dateCell = headerRow.createCell(0);
      dateCell.setCellValue("Date");
      dateCell.setCellStyle(styles.get("header"));

      for (int i = 1; i <= 12; i++) {
        Cell monthCell = headerRow.createCell(i);
        monthCell.setCellValue(MOIS[i - 1]);
        monthCell.setCellStyle(styles.get("header"));
      }
      // rowNum++;

      Map<Integer, Object[]> globalMap = convertToMap(globalStats);

      rowNum = addSectionHeader(sheet, rowNum, "Nombre de nouvelles demandes reçues (Total)",
          styles, 12);
      Row globalNouvellesDemandes = sheet.createRow(rowNum++);
      Cell totalCell = globalNouvellesDemandes.createCell(0);
      totalCell.setCellValue("Total Global");
      totalCell.setCellStyle(styles.get("total"));

      for (int mois = 1; mois <= 12; mois++) {
        Object[] stats = globalMap.get(mois);
        Cell cell = globalNouvellesDemandes.createCell(mois);
        cell.setCellValue(stats != null ? safeGetDouble(stats, 2) : 0);
        cell.setCellStyle(styles.get("boldNumber"));
      }

      if (chefSecteurStats != null && !chefSecteurStats.isEmpty()) {
        for (Map<String, Object> chefData : chefSecteurStats) {
          Row chefRow = sheet.createRow(rowNum++);
          String chefNom = (String) chefData.get("chefNom");
          Cell chefCell = chefRow.createCell(0);
          chefCell.setCellValue(chefNom != null ? chefNom : "Chef Secteur");
          chefCell.setCellStyle(styles.get("chef"));

          double[] nouvellesDemandesArray = (double[]) chefData.get("nouvellesDemandesParMois");
          for (int mois = 1; mois <= 12; mois++) {
            Cell cell = chefRow.createCell(mois);
            cell.setCellValue(
                nouvellesDemandesArray != null ? nouvellesDemandesArray[mois - 1] : 0);
            cell.setCellStyle(styles.get("number"));
          }
        }
      }
      rowNum++;

      rowNum = addSectionHeader(sheet, rowNum, "Nombre de mise en service (Total)", styles, 12);
      Row globalMisesService = sheet.createRow(rowNum++);
      totalCell = globalMisesService.createCell(0);
      totalCell.setCellValue("Total Global");
      totalCell.setCellStyle(styles.get("total"));

      for (int mois = 1; mois <= 12; mois++) {
        Object[] stats = globalMap.get(mois);
        Cell cell = globalMisesService.createCell(mois);
        cell.setCellValue(stats != null ? safeGetDouble(stats, 3) : 0);
        cell.setCellStyle(styles.get("boldNumber"));

      }

      if (chefSecteurStats != null && !chefSecteurStats.isEmpty()) {
        for (Map<String, Object> chefData : chefSecteurStats) {
          Row chefRow = sheet.createRow(rowNum++);
          String chefNom = (String) chefData.get("chefNom");
          Cell chefCell = chefRow.createCell(0);
          chefCell.setCellValue(chefNom != null ? chefNom : "Chef Secteur");
          chefCell.setCellStyle(styles.get("chef"));

          double[] misesEnServiceArray = (double[]) chefData.get("misesEnServiceParMois");
          for (int mois = 1; mois <= 12; mois++) {
            Cell cell = chefRow.createCell(mois);
            cell.setCellValue(misesEnServiceArray != null ? misesEnServiceArray[mois - 1] : 0);
            cell.setCellStyle(styles.get("number"));
          }
        }
      }
      rowNum++;

      String[] metrics = {"Demandes injoignables", "Demandes saisies infaisables",
          "Annulées par client", "Nombre de demandes saisies (avec référence TT)",
          "Nombre de demandes résiliées", "Nombre de modems récupérés"};
      int[] metricIndices = {5, 6, 7, 8, 9, 4};

      for (int i = 0; i < metrics.length; i++) {
        Row metricRow = sheet.createRow(rowNum++);
        Cell metricCell = metricRow.createCell(0);
        metricCell.setCellValue(metrics[i]);
        metricCell.setCellStyle(styles.get("metric"));

        for (int mois = 1; mois <= 12; mois++) {
          Object[] stats = globalMap.get(mois);
          Cell cell = metricRow.createCell(mois);
          cell.setCellValue(stats != null ? safeGetDouble(stats, metricIndices[i]) : 0);
          cell.setCellStyle(styles.get("number"));
        }
      }
      rowNum++;

      Row facturesRow = sheet.createRow(rowNum++);
      Cell facturesCell = facturesRow.createCell(0);
      facturesCell.setCellValue("Montant des factures payées (paiement>=échéance +3J)");
      facturesCell.setCellStyle(styles.get("secondaryMetric"));


      Map<Integer, Object[]> facturesMap = convertToMap(facturesStats);
      for (int mois = 1; mois <= 12; mois++) {
        Object[] stats = facturesMap.get(mois);
        Cell cell = facturesRow.createCell(mois);
        cell.setCellValue(stats != null ? safeGetDouble(stats, 2) : 0);
        cell.setCellStyle(styles.get("currency"));
      }

      Row instancesRow = sheet.createRow(rowNum++);
      Cell instancesCell = instancesRow.createCell(0);
      instancesCell.setCellValue("Nombre d'instances reprises");
      instancesCell.setCellStyle(styles.get("secondaryMetric"));

      for (int mois = 1; mois <= 12; mois++) {
        Object[] stats = globalMap.get(mois);
        Cell cell = instancesRow.createCell(mois);
        cell.setCellValue(stats != null ? safeGetDouble(stats, 10) : 0);
        cell.setCellStyle(styles.get("number"));
      }

      Row visitesRow = sheet.createRow(rowNum++);
      Cell visitesCell = visitesRow.createCell(0);
      visitesCell.setCellValue("Nombre de visites enregistrées");
      visitesCell.setCellStyle(styles.get("secondaryMetric"));

      Map<Integer, Object[]> visitesMap = convertToMap(visitesStats);
      for (int mois = 1; mois <= 12; mois++) {
        Object[] stats = visitesMap.get(mois);
        Cell cell = visitesRow.createCell(mois);
        cell.setCellValue(stats != null ? safeGetDouble(stats, 2) : 0);
        cell.setCellStyle(styles.get("number"));
      }

      for (int i = 0; i <= 12; i++) {
        sheet.autoSizeColumn(i);
      }

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  public byte[] exportRapportToExcelByMonth(int annee, int mois) throws IOException {
    Map<String, Object> dashboardData = rapportService.getDashboardDataByMonth(annee, mois);

    List<Object[]> globalStats = (List<Object[]>) dashboardData.get("statistiques_globales");
    List<Object[]> chefSecteurStats = (List<Object[]>) dashboardData.get("statistiques_par_chef");
    List<Object[]> facturesStats = (List<Object[]>) dashboardData.get("factures_payees_apres_3jrs");
    List<Object[]> visitesStats = (List<Object[]>) dashboardData.get("visites");

    try (Workbook workbook = new XSSFWorkbook()) {
      String moisNom = MOIS[mois - 1];
      Sheet sheet = workbook.createSheet("Rapport " + moisNom + " " + annee);
      Map<String, CellStyle> styles = createStyles(workbook);
      int rowNum = 0;

      int nbJours = getNbJoursDansMois(annee, mois);

      Row headerRow = sheet.createRow(rowNum++);
      Cell dateCell = headerRow.createCell(0);
      dateCell.setCellValue("Date");
      dateCell.setCellStyle(styles.get("header"));

      for (int jour = 1; jour <= nbJours; jour++) {
        Cell jourCell = headerRow.createCell(jour);
        jourCell.setCellValue(String.valueOf(jour));
        jourCell.setCellStyle(styles.get("header"));
      }
      // rowNum++;

      Map<Integer, Object[]> globalMap = convertToMap(globalStats);
      Map<Long, Map<Integer, double[]>> chefDataMap =
          convertChefStatsForMonth(chefSecteurStats, nbJours);
      rowNum = addSectionHeader(sheet, rowNum, "Nombre de nouvelles demandes reçues (Total)",
          styles, nbJours);
      Row globalNouvellesDemandes = sheet.createRow(rowNum++);
      Cell totalCell = globalNouvellesDemandes.createCell(0);
      totalCell.setCellValue("Total Global");
      totalCell.setCellStyle(styles.get("total"));

      for (int jour = 1; jour <= nbJours; jour++) {
        Object[] stats = globalMap.get(jour);
        Cell cell = globalNouvellesDemandes.createCell(jour);
        cell.setCellValue(stats != null ? safeGetDouble(stats, 1) : 0);
        cell.setCellStyle(styles.get("boldNumber"));

      }

      for (Map.Entry<Long, Map<Integer, double[]>> entry : chefDataMap.entrySet()) {
        Long chefId = entry.getKey();
        Map<Integer, double[]> chefData = entry.getValue();
        double[] nouvellesDemandes = chefData.get(0);

        Row chefRow = sheet.createRow(rowNum++);
        String chefNom = getChefNomFromData(chefSecteurStats, chefId);
        Cell chefCell = chefRow.createCell(0);
        chefCell.setCellValue(chefNom != null ? chefNom : "Chef Secteur " + chefId);
        chefCell.setCellStyle(styles.get("chef"));

        for (int jour = 1; jour <= nbJours; jour++) {
          Cell cell = chefRow.createCell(jour);
          cell.setCellValue(nouvellesDemandes != null ? nouvellesDemandes[jour - 1] : 0);
          cell.setCellStyle(styles.get("number"));
        }
      }
      rowNum++;
      rowNum =
          addSectionHeader(sheet, rowNum, "Nombre de mise en service (Total)", styles, nbJours);
      Row globalMisesService = sheet.createRow(rowNum++);
      totalCell = globalMisesService.createCell(0);
      totalCell.setCellValue("Total Global");
      totalCell.setCellStyle(styles.get("total"));

      for (int jour = 1; jour <= nbJours; jour++) {
        Object[] stats = globalMap.get(jour);
        Cell cell = globalMisesService.createCell(jour);
        cell.setCellValue(stats != null ? safeGetDouble(stats, 2) : 0);
        cell.setCellStyle(styles.get("boldNumber"));
      }

      for (Map.Entry<Long, Map<Integer, double[]>> entry : chefDataMap.entrySet()) {
        Long chefId = entry.getKey();
        Map<Integer, double[]> chefData = entry.getValue();
        double[] misesEnService = chefData.get(1);

        Row chefRow = sheet.createRow(rowNum++);
        String chefNom = getChefNomFromData(chefSecteurStats, chefId);
        Cell chefCell = chefRow.createCell(0);
        chefCell.setCellValue(chefNom != null ? chefNom : "Chef Secteur " + chefId);
        chefCell.setCellStyle(styles.get("chef"));

        for (int jour = 1; jour <= nbJours; jour++) {
          Cell cell = chefRow.createCell(jour);
          cell.setCellValue(misesEnService != null ? misesEnService[jour - 1] : 0);
          cell.setCellStyle(styles.get("number"));
        }
      }
      rowNum++;
      String[] metrics = {"Demandes injoignables", "Demandes saisies infaisables",
          "Annulées par client", "Nombre de demandes saisies (avec référence TT)",
          "Nombre de demandes résiliées", "Nombre de modems récupérés"};
      int[] metricIndices = {4, 5, 6, 7, 8, 3};

      for (int i = 0; i < metrics.length; i++) {
        Row metricRow = sheet.createRow(rowNum++);
        Cell metricCell = metricRow.createCell(0);
        metricCell.setCellValue(metrics[i]);
        metricCell.setCellStyle(styles.get("metric"));

        for (int jour = 1; jour <= nbJours; jour++) {
          Object[] stats = globalMap.get(jour);
          Cell cell = metricRow.createCell(jour);
          cell.setCellValue(stats != null ? safeGetDouble(stats, metricIndices[i]) : 0);
          cell.setCellStyle(styles.get("number"));
        }
      }
      rowNum++;
      Row facturesRow = sheet.createRow(rowNum++);
      Cell facturesCell = facturesRow.createCell(0);
      facturesCell.setCellValue("Montant des factures payées (paiement>=échéance +3J)");
      facturesCell.setCellStyle(styles.get("secondaryMetric"));
      Map<Integer, Object[]> facturesMap = convertToMap(facturesStats);
      for (int jour = 1; jour <= nbJours; jour++) {
        Object[] stats = facturesMap.get(jour);
        Cell cell = facturesRow.createCell(jour);
        cell.setCellValue(stats != null ? safeGetDouble(stats, 1) : 0);
        cell.setCellStyle(styles.get("currency"));
      }
      Row instancesRow = sheet.createRow(rowNum++);
      Cell instancesCell = instancesRow.createCell(0);
      instancesCell.setCellValue("Nombre d'instances reprises");
      instancesCell.setCellStyle(styles.get("secondaryMetric"));

      for (int jour = 1; jour <= nbJours; jour++) {
        Object[] stats = globalMap.get(jour);
        Cell cell = instancesRow.createCell(jour);
        cell.setCellValue(stats != null ? safeGetDouble(stats, 9) : 0);
        cell.setCellStyle(styles.get("number"));
      }

      Row visitesRow = sheet.createRow(rowNum++);
      Cell visitesCell = visitesRow.createCell(0);
      visitesCell.setCellValue("Nombre de visites enregistrées");
      visitesCell.setCellStyle(styles.get("secondaryMetric"));

      Map<Integer, Object[]> visitesMap = convertToMap(visitesStats);
      for (int jour = 1; jour <= nbJours; jour++) {
        Object[] stats = visitesMap.get(jour);
        Cell cell = visitesRow.createCell(jour);
        cell.setCellValue(stats != null ? safeGetDouble(stats, 1) : 0);
        cell.setCellStyle(styles.get("number"));
      }

      for (int i = 0; i <= nbJours; i++) {
        sheet.autoSizeColumn(i);
      }

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  private Map<Long, Map<Integer, double[]>> convertChefStatsForMonth(List<Object[]> chefStats,
      int nbJours) {
    Map<Long, Map<Integer, double[]>> result = new LinkedHashMap<>();

    if (chefStats != null) {
      for (Object[] row : chefStats) {
        if (row.length >= 6) {
          Integer jour = safeGetInteger(row, 0);
          Long chefId = safeGetLong(row, 3);
          Double nouvellesDemandes = safeGetDouble(row, 4);
          Double misesEnService = safeGetDouble(row, 5);

          if (chefId != null && jour != null && jour <= nbJours) {
            Map<Integer, double[]> chefData = result.computeIfAbsent(chefId, k -> {
              Map<Integer, double[]> data = new HashMap<>();
              data.put(0, new double[nbJours]);
              data.put(1, new double[nbJours]);
              return data;
            });

            double[] nouvellesArray = chefData.get(0);
            double[] misesArray = chefData.get(1);

            if (nouvellesArray != null && jour - 1 < nouvellesArray.length) {
              nouvellesArray[jour - 1] = nouvellesDemandes;
            }
            if (misesArray != null && jour - 1 < misesArray.length) {
              misesArray[jour - 1] = misesEnService;
            }
          }
        }
      }
    }

    return result;
  }

  private String getChefNomFromData(List<Object[]> chefStats, Long chefId) {
    if (chefStats != null) {
      for (Object[] row : chefStats) {
        Long id = safeGetLong(row, 3);
        if (id != null && id.equals(chefId)) {
          String nom = safeGetString(row, 1);
          if (nom != null && !nom.isEmpty()) {
            return nom;
          }
        }
      }
    }
    return null;
  }

  private int getNbJoursDansMois(int annee, int mois) {
    if (mois == 2 && estBissextile(annee)) {
      return 29;
    }
    return JOURS_PAR_MOIS[mois - 1];
  }

  private boolean estBissextile(int annee) {
    return (annee % 4 == 0 && annee % 100 != 0) || (annee % 400 == 0);
  }

  private Map<String, CellStyle> createStyles(Workbook workbook) {
    Map<String, CellStyle> styles = new HashMap<>();

    CellStyle headerStyle = workbook.createCellStyle();
    Font headerFont = workbook.createFont();
    headerFont.setBold(true);
    headerFont.setFontHeightInPoints((short) 11);
    headerFont.setColor(IndexedColors.BLACK.getIndex());
    headerStyle.setFont(headerFont);
    headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    headerStyle.setBorderBottom(BorderStyle.THIN);
    headerStyle.setBorderTop(BorderStyle.THIN);
    headerStyle.setBorderLeft(BorderStyle.THIN);
    headerStyle.setBorderRight(BorderStyle.THIN);
    headerStyle.setAlignment(HorizontalAlignment.CENTER);
    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    styles.put("header", headerStyle);

    CellStyle sectionStyle = workbook.createCellStyle();
    Font sectionFont = workbook.createFont();
    sectionFont.setBold(true);
    sectionFont.setFontHeightInPoints((short) 12);
    sectionFont.setColor(IndexedColors.WHITE.getIndex());
    sectionStyle.setFont(sectionFont);
    sectionStyle.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
    sectionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    styles.put("section", sectionStyle);

    CellStyle totalStyle = workbook.createCellStyle();
    Font totalFont = workbook.createFont();
    totalFont.setBold(true);
    totalFont.setFontHeightInPoints((short) 11);
    totalFont.setColor(IndexedColors.BLACK.getIndex());
    totalStyle.setFont(totalFont);

    if (workbook instanceof XSSFWorkbook) {
      XSSFColor customColor = new XSSFColor(new java.awt.Color(230, 230, 250), null);
      ((XSSFCellStyle) totalStyle).setFillForegroundColor(customColor);
    } else {
      totalStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
    }
    totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    totalStyle.setAlignment(HorizontalAlignment.CENTER);
    totalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    totalStyle.setBorderBottom(BorderStyle.THIN);
    totalStyle.setBorderTop(BorderStyle.THIN);
    totalStyle.setBorderLeft(BorderStyle.THIN);
    totalStyle.setBorderRight(BorderStyle.THIN);
    styles.put("total", totalStyle);
    CellStyle boldNumberStyle = workbook.createCellStyle();
    boldNumberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
    boldNumberStyle.setAlignment(HorizontalAlignment.RIGHT);
    boldNumberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    boldNumberStyle.setBorderBottom(BorderStyle.THIN);
    boldNumberStyle.setBorderTop(BorderStyle.THIN);
    boldNumberStyle.setBorderLeft(BorderStyle.THIN);
    boldNumberStyle.setBorderRight(BorderStyle.THIN);
    Font boldNumberFont = workbook.createFont();
    boldNumberFont.setBold(true);
    boldNumberFont.setFontHeightInPoints((short) 11);
    boldNumberStyle.setFont(boldNumberFont);
    styles.put("boldNumber", boldNumberStyle);
    CellStyle chefStyle = workbook.createCellStyle();
    Font chefFont = workbook.createFont();
    chefFont.setBold(true);
    chefFont.setFontHeightInPoints((short) 11);
    chefFont.setColor(IndexedColors.BLACK.getIndex());
    chefStyle.setFont(chefFont);

    if (workbook instanceof XSSFWorkbook) {
      XSSFColor customColor = new XSSFColor(new java.awt.Color(237, 234, 255), null);
      ((XSSFCellStyle) chefStyle).setFillForegroundColor(customColor);
    } else {
      chefStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
    }
    chefStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    chefStyle.setAlignment(HorizontalAlignment.LEFT);
    chefStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    chefStyle.setBorderBottom(BorderStyle.THIN);
    chefStyle.setBorderTop(BorderStyle.THIN);
    chefStyle.setBorderLeft(BorderStyle.THIN);
    chefStyle.setBorderRight(BorderStyle.THIN);
    styles.put("chef", chefStyle);
    CellStyle metricStyle = workbook.createCellStyle();
    Font metricFont = workbook.createFont();
    metricFont.setBold(true);
    metricFont.setFontHeightInPoints((short) 11);
    metricFont.setColor(IndexedColors.BLACK.getIndex());
    metricStyle.setFont(metricFont);

    if (workbook instanceof XSSFWorkbook) {
      XSSFColor customColor = new XSSFColor(new java.awt.Color(255, 208, 215), null);
      ((XSSFCellStyle) metricStyle).setFillForegroundColor(customColor);
    } else {
      metricStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
    }
    metricStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    metricStyle.setAlignment(HorizontalAlignment.LEFT);
    metricStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    metricStyle.setBorderBottom(BorderStyle.THIN);
    metricStyle.setBorderTop(BorderStyle.THIN);
    metricStyle.setBorderLeft(BorderStyle.THIN);
    metricStyle.setBorderRight(BorderStyle.THIN);
    styles.put("metric", metricStyle);
    CellStyle secondaryMetricStyle = workbook.createCellStyle();
    Font secondaryMetricFont = workbook.createFont();
    secondaryMetricFont.setBold(true);
    secondaryMetricFont.setFontHeightInPoints((short) 11);
    secondaryMetricFont.setColor(IndexedColors.BLACK.getIndex());
    secondaryMetricStyle.setFont(secondaryMetricFont);

    if (workbook instanceof XSSFWorkbook) {
      XSSFColor customColor = new XSSFColor(new java.awt.Color(247, 244, 190), null);
      ((XSSFCellStyle) secondaryMetricStyle).setFillForegroundColor(customColor);
    } else {
      secondaryMetricStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
    }
    secondaryMetricStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    secondaryMetricStyle.setAlignment(HorizontalAlignment.LEFT);
    secondaryMetricStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    secondaryMetricStyle.setBorderBottom(BorderStyle.THIN);
    secondaryMetricStyle.setBorderTop(BorderStyle.THIN);
    secondaryMetricStyle.setBorderLeft(BorderStyle.THIN);
    secondaryMetricStyle.setBorderRight(BorderStyle.THIN);
    styles.put("secondaryMetric", secondaryMetricStyle);
    CellStyle numberStyle = workbook.createCellStyle();
    numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
    numberStyle.setAlignment(HorizontalAlignment.RIGHT);
    numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    numberStyle.setBorderBottom(BorderStyle.THIN);
    numberStyle.setBorderTop(BorderStyle.THIN);
    numberStyle.setBorderLeft(BorderStyle.THIN);
    numberStyle.setBorderRight(BorderStyle.THIN);
    styles.put("number", numberStyle);
    CellStyle currencyStyle = workbook.createCellStyle();
    currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
    currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
    currencyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    currencyStyle.setBorderBottom(BorderStyle.THIN);
    currencyStyle.setBorderTop(BorderStyle.THIN);
    currencyStyle.setBorderLeft(BorderStyle.THIN);
    currencyStyle.setBorderRight(BorderStyle.THIN);
    styles.put("currency", currencyStyle);

    return styles;
  }

  private int addSectionHeader(Sheet sheet, int rowNum, String title, Map<String, CellStyle> styles,
      int nbColonnes) {
    Row headerRow = sheet.createRow(rowNum);

    Cell headerCell = headerRow.createCell(0);
    headerCell.setCellValue(title);
    headerCell.setCellStyle(styles.get("section"));

    for (int i = 1; i <= nbColonnes; i++) {
      Cell emptyCell = headerRow.createCell(i);
      emptyCell.setCellStyle(styles.get("section"));
    }

    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, nbColonnes));
    return rowNum + 1;
  }

  private Map<Integer, Object[]> convertToMap(List<Object[]> list) {
    Map<Integer, Object[]> map = new HashMap<>();
    if (list != null) {
      for (Object[] row : list) {
        Integer index = safeGetInteger(row, 0);
        if (index != null) {
          map.put(index, row);
        }
      }
    }
    return map;
  }

  private Double safeGetDouble(Object[] array, int index) {
    if (array != null && index < array.length && array[index] instanceof Number) {
      return ((Number) array[index]).doubleValue();
    }
    return 0.0;
  }

  private Integer safeGetInteger(Object[] array, int index) {
    if (array != null && index < array.length && array[index] instanceof Number) {
      return ((Number) array[index]).intValue();
    }
    return null;
  }

  private Long safeGetLong(Object[] array, int index) {
    if (array != null && index < array.length && array[index] instanceof Number) {
      return ((Number) array[index]).longValue();
    }
    return null;
  }

  private String safeGetString(Object[] array, int index) {
    if (array != null && index < array.length && array[index] != null) {
      return array[index].toString();
    }
    return "";
  }

  public byte[] exportClientsFacturesImpayeesToExcel(List<Map<String, Object>> clients)
      throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Clients factures impayées");
      Map<String, CellStyle> styles = createStyles(workbook);
      Map<String, CellStyle> levelStyles = createLevelStyles(workbook);
      CellStyle baseStyleWithBg = createBaseStyleWithBackground(workbook);
      CellStyle currencyStyleWithBg = createCurrencyStyleWithBackground(workbook); // Ajout

      int rowNum = 0;

      Row headerRow = sheet.createRow(rowNum++);
      String[] headers = {"ID Client", "Prénom", "Nom", "CIN", "Téléphone", "Référence Client",
          "Nb Factures Impayées", "Montant Total TTC", "Date Échéance", "Retard (jours)",
          "Niveau de retard"};

      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(styles.get("header"));
      }

      for (Map<String, Object> client : clients) {
        Row row = sheet.createRow(rowNum++);

        String niveauRetard =
            client.get("niveauRetard") != null ? client.get("niveauRetard").toString() : "";
        CellStyle levelStyle = getRowStyleForLevel(levelStyles, niveauRetard);

        for (int i = 0; i < headers.length; i++) {
          Cell cell = row.createCell(i);
          if (i == 7) {
            cell.setCellStyle(currencyStyleWithBg);
          } else {
            cell.setCellStyle(baseStyleWithBg);
          }
        }

        row.getCell(0).setCellValue(
            client.get("clientId") != null ? ((Number) client.get("clientId")).longValue() : 0);
        row.getCell(1).setCellValue(
            client.get("firstName") != null ? client.get("firstName").toString() : "");
        row.getCell(2)
            .setCellValue(client.get("lastName") != null ? client.get("lastName").toString() : "");
        row.getCell(3).setCellValue(client.get("cin") != null ? client.get("cin").toString() : "");
        row.getCell(4).setCellValue(
            client.get("telMobile") != null ? client.get("telMobile").toString() : "");
        row.getCell(5).setCellValue(
            client.get("referenceClient") != null ? client.get("referenceClient").toString() : "");
        row.getCell(6)
            .setCellValue(client.get("nbFacturesImpayees") != null
                ? ((Number) client.get("nbFacturesImpayees")).intValue()
                : 0);

        Double montant = client.get("montantTotalTTCImpaye") != null
            ? ((Number) client.get("montantTotalTTCImpaye")).doubleValue()
            : 0.0;
        row.getCell(7).setCellValue(montant);

        row.getCell(8)
            .setCellValue(client.get("dateEcheancePlusAncienne") != null
                ? client.get("dateEcheancePlusAncienne").toString()
                : "");
        row.getCell(9)
            .setCellValue(client.get("retardEnJours") != null
                ? ((Number) client.get("retardEnJours")).intValue()
                : 0);

        Cell levelCell = row.getCell(10);
        levelCell.setCellValue(niveauRetard);
        levelCell.setCellStyle(levelStyle);
      }

      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return outputStream.toByteArray();
    }
  }

  private CellStyle createBaseStyleWithBackground(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();

    if (workbook instanceof XSSFWorkbook) {
      XSSFColor bgColor = new XSSFColor(new java.awt.Color(245, 242, 247), null);
      ((XSSFCellStyle) style).setFillForegroundColor(bgColor);
    } else {
      style.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
    }
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setAlignment(HorizontalAlignment.RIGHT);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    Font font = workbook.createFont();
    font.setColor(IndexedColors.BLACK.getIndex());
    style.setFont(font);
    style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
    return style;
  }

  private Map<String, CellStyle> createLevelStyles(Workbook workbook) {
    Map<String, CellStyle> levelStyles = new HashMap<>();
    CellStyle lightStyle = workbook.createCellStyle();
    Font lightFont = workbook.createFont();
    lightFont.setColor(IndexedColors.BLACK.getIndex());
    lightFont.setBold(true);
    lightStyle.setFont(lightFont);
    lightStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
    lightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    lightStyle.setAlignment(HorizontalAlignment.LEFT);
    lightStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    lightStyle.setBorderBottom(BorderStyle.THIN);
    lightStyle.setBorderTop(BorderStyle.THIN);
    lightStyle.setBorderLeft(BorderStyle.THIN);
    lightStyle.setBorderRight(BorderStyle.THIN);
    levelStyles.put("Retard léger (3-6j)", lightStyle);
    CellStyle moderateStyle = workbook.createCellStyle();
    Font moderateFont = workbook.createFont();
    moderateFont.setColor(IndexedColors.BLACK.getIndex());
    moderateFont.setBold(true);
    moderateStyle.setFont(moderateFont);
    if (workbook instanceof XSSFWorkbook) {
      XSSFColor orangeColor = new XSSFColor(new java.awt.Color(255, 200, 100), null);
      ((XSSFCellStyle) moderateStyle).setFillForegroundColor(orangeColor);
    } else {
      moderateStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
    }
    moderateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    moderateStyle.setAlignment(HorizontalAlignment.LEFT);
    moderateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    moderateStyle.setBorderBottom(BorderStyle.THIN);
    moderateStyle.setBorderTop(BorderStyle.THIN);
    moderateStyle.setBorderLeft(BorderStyle.THIN);
    moderateStyle.setBorderRight(BorderStyle.THIN);
    levelStyles.put("Retard modéré (7-14j)", moderateStyle);
    CellStyle importantStyle = workbook.createCellStyle();
    Font importantFont = workbook.createFont();
    importantFont.setColor(IndexedColors.BLACK.getIndex());
    importantFont.setBold(true);
    importantStyle.setFont(importantFont);
    if (workbook instanceof XSSFWorkbook) {
      XSSFColor darkOrangeColor = new XSSFColor(new java.awt.Color(255, 140, 0), null);
      ((XSSFCellStyle) importantStyle).setFillForegroundColor(darkOrangeColor);
    } else {
      importantStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
    }
    importantStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    importantStyle.setAlignment(HorizontalAlignment.LEFT);
    importantStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    importantStyle.setBorderBottom(BorderStyle.THIN);
    importantStyle.setBorderTop(BorderStyle.THIN);
    importantStyle.setBorderLeft(BorderStyle.THIN);
    importantStyle.setBorderRight(BorderStyle.THIN);
    levelStyles.put("Retard important (15-30j)", importantStyle);
    CellStyle criticalStyle = workbook.createCellStyle();
    Font criticalFont = workbook.createFont();
    criticalFont.setColor(IndexedColors.BLACK.getIndex());
    criticalFont.setBold(true);
    criticalStyle.setFont(criticalFont);
    if (workbook instanceof XSSFWorkbook) {
      XSSFColor redColor = new XSSFColor(new java.awt.Color(255, 100, 100), null);
      ((XSSFCellStyle) criticalStyle).setFillForegroundColor(redColor);
    } else {
      criticalStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
    }
    criticalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    criticalStyle.setAlignment(HorizontalAlignment.LEFT);
    criticalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    criticalStyle.setBorderBottom(BorderStyle.THIN);
    criticalStyle.setBorderTop(BorderStyle.THIN);
    criticalStyle.setBorderLeft(BorderStyle.THIN);
    criticalStyle.setBorderRight(BorderStyle.THIN);
    levelStyles.put("Retard critique (>30j)", criticalStyle);
    CellStyle defaultStyle = workbook.createCellStyle();
    Font defaultFont = workbook.createFont();
    defaultFont.setColor(IndexedColors.BLACK.getIndex());
    defaultStyle.setFont(defaultFont);
    defaultStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
    defaultStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    defaultStyle.setAlignment(HorizontalAlignment.LEFT);
    defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    defaultStyle.setBorderBottom(BorderStyle.THIN);
    defaultStyle.setBorderTop(BorderStyle.THIN);
    defaultStyle.setBorderLeft(BorderStyle.THIN);
    defaultStyle.setBorderRight(BorderStyle.THIN);
    levelStyles.put("default", defaultStyle);

    return levelStyles;
  }

  private CellStyle getRowStyleForLevel(Map<String, CellStyle> levelStyles, String niveauRetard) {
    if (niveauRetard != null && levelStyles.containsKey(niveauRetard)) {
      return levelStyles.get(niveauRetard);
    }
    return levelStyles.get("default");
  }

  private CellStyle createCurrencyStyleWithBackground(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();

    if (workbook instanceof XSSFWorkbook) {
      XSSFColor bgColor = new XSSFColor(new java.awt.Color(245, 242, 247), null);
      ((XSSFCellStyle) style).setFillForegroundColor(bgColor);
    } else {
      style.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
    }
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setAlignment(HorizontalAlignment.RIGHT);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    Font font = workbook.createFont();
    font.setColor(IndexedColors.BLACK.getIndex());
    style.setFont(font);
    style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.000"));
    return style;
  }
}
