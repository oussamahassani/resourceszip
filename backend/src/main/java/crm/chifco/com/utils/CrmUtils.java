package crm.chifco.com.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import crm.chifco.com.model.Pack;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public final class CrmUtils {
  private static final Logger logger = LogManager.getLogger(CrmUtils.class);

  @Value("${delaiDePaiement}")
  private static String delaiDePaiement;

  private CrmUtils() {
    throw new java.lang.UnsupportedOperationException("Utility class and cannot be instantiated");
  }

  static DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

  private static final String CURRENCY = " TND";
  private static final String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
  private static Random rnd = new Random();

  public static String noSpecialCharacters(String input) {
    if (input.length() > 45) {
      String extension = input.substring(input.lastIndexOf(".") + 1);
      input = input.substring(0, 15).concat("." + extension);
    }

    return input.trim().replaceAll("[^A-Za-z0-9.]", "-");

  }

  public static String fixNBZero(Double number) {
    return fixNBZero(number, true);
  }

  public static String fixNBZero(Double number, Boolean withcurrency) {
    if (number != null) {
      if (withcurrency)
        return (new BigDecimal(number).setScale(3, BigDecimal.ROUND_HALF_UP)).toString() + CURRENCY;
      else
        return (new BigDecimal(number).setScale(3, BigDecimal.ROUND_HALF_UP)).toString();
    }
    return "0.000";
  }

  public static Double convertStringToDouble(String input) {
    Double foo;
    try {
      foo = Double.parseDouble(input);
    } catch (NumberFormatException e) {
      logger.info("CrmUtils convertStringToDouble  NumberFormatException " + e.getMessage());
      foo = -999.99;
    }
    return foo;
  }

  public static Long convertStringToLong(String str) {
    try {
      return Long.valueOf(str);
    } catch (NumberFormatException e) {
      logger.info("CrmUtils convertStringToLong NumberFormatException " + e.getMessage());
      return null;
    }
  }

  public static Double formatDoubleInput(Double input) {

    DecimalFormat formatter = new DecimalFormat("#0.000");
    DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.FRANCE);
    dfs.setDecimalSeparator('.');
    formatter.setDecimalFormatSymbols(dfs);
    dfs.setDecimalSeparator('.');
    formatter.setDecimalFormatSymbols(dfs);
    String formatted = formatter.format(input);

    return new Double(formatted);
  }

  public static String formatDoubleInputToString(Double input) {

    DecimalFormat formatter = new DecimalFormat("#0.000");
    DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.FRANCE);
    dfs.setDecimalSeparator('.');
    formatter.setDecimalFormatSymbols(dfs);
    dfs.setDecimalSeparator('.');
    formatter.setDecimalFormatSymbols(dfs);

    return formatter.format(input);
  }

  public static void saveImage(MultipartFile imageFile, String idphoto, String pathphoto,
      String caractaireDestingtion) throws Exception {

    String folder = pathphoto + idphoto + "/";
    File uploadDir = new File(folder);
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }
    byte[] bytes = imageFile.getBytes();
    Path path = Paths.get(folder + caractaireDestingtion
        + CrmUtils.noSpecialCharacters(imageFile.getOriginalFilename()));
    Files.write(path, bytes);
  }

  public static void saveImageReclamation(MultipartFile imageFile, String idphoto, String pathphoto,
      String fileName) throws Exception {
    String folder = (idphoto == null || idphoto.isEmpty()) ? pathphoto
        : pathphoto + (pathphoto.endsWith("/") ? "" : "/") + idphoto + "/";

    File uploadDir = new File(folder);
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }

    Path path = Paths.get(folder, fileName);
    Files.write(path, imageFile.getBytes());
  }


  public static int sequentailvalueLoginModem(int count) {
    int value = 100000;
    return value + count;
  }

  public static String randemvaluePasswordModem() {

    StringBuilder salt = new StringBuilder();

    while (salt.length() < 9) { // length of the random string.
      int index = (int) (rnd.nextInt() * SALTCHARS.length());
      salt.append(SALTCHARS.charAt(index));
    }

    return salt.toString().toLowerCase();
  }

  public static Date calculeDateFin(int typdedepaymentmonth, Date factureRecurentDebut) {

    LocalDate nowDate = null;
    if (factureRecurentDebut == null) {
      nowDate = LocalDate.now().plusDays(Long.parseLong("2"));
    } else {
      Date safeDate = new Date(factureRecurentDebut.getTime());
      nowDate = safeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    nowDate = nowDate.plusMonths(typdedepaymentmonth);

    return Date.from(nowDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

  }

  public static Date addDayTocurentDate(Long numbersDay) {
    LocalDate nowDate = LocalDate.now().plusDays(numbersDay);

    return Date.from(nowDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date addDaysToGivenDate(Date givenDate, Long numberOfDays) {
    LocalDate localDate = givenDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate updatedDate = localDate.plusDays(numberOfDays);
    return Date.from(updatedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public static String dateAujourdhui() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu_MM_dd__HH_mm_ss");
    LocalDateTime now = LocalDateTime.now();
    return dtf.format(now);
  }

  public static String getReasonForFileDeletionFailureInPlainEnglish(File file) {
    try {
      if (!file.exists())
        return "It doesn't exist in the first place.";
      else if (file.isDirectory() && file.list().length > 0)
        return "It's a directory and it's not empty.";
      else
        return "Somebody else has it open, we don't have write permissions, or somebody stole my disk.";
    } catch (SecurityException e) {

      return "We're sandboxed and don't have filesystem access.";
    }
  }

  public static void deleteFile(File file1) {
    if (!file1.delete()) {
      logger.error(getReasonForFileDeletionFailureInPlainEnglish(file1));
    }
  }

  public static String getYear() {
    Date date = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy");

    return df.format(date);
  }

  public static String getMonth() {
    Date date = new Date();
    SimpleDateFormat df = new SimpleDateFormat("MM");

    return df.format(date);
  }

  public static String CalculatedateFinEngagement(Pack pack) {
    Instant datenow = Instant.now();

    Instant datefin =
        datenow.plus(Long.parseLong(pack.getEngagement().getNombre()), ChronoUnit.DAYS);
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
    return formatter.format(datefin);
  }

  public static Boolean Isnumber(String str) {
    return str.matches("[0-9]+");
  }

  public static String calculeDateSendsms(Long Addday) {
    Instant datenow = Instant.now();

    Instant datefin = datenow.plus(Addday, ChronoUnit.DAYS);
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
    return formatter.format(datefin);
  }

  public static String calculeDateSendsmsNegativeNumber(Long Addday) {
    Instant datenow = Instant.now();

    Instant datefin = datenow.plus(-Addday, ChronoUnit.DAYS);
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
    return formatter.format(datefin);
  }

  public static LocalDateTime toInstant(final String timeStr) {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

    return LocalDateTime.parse(timeStr, formatter);
  }

  public static boolean isNumeric(String str) {
    if (str == null) {
      return false;
    }
    int sz = str.length();
    for (int i = 0; i < sz; i++) {
      if (Character.isDigit(str.charAt(i)) == false) {
        return false;
      }
    }
    return true;
  }

  public static String RadusDateDexpiration(Date RadusDateDexpiration) {

    Date safeDate = new Date(RadusDateDexpiration.getTime());
    LocalDate DateDexpiration = safeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    DateDexpiration = DateDexpiration.plusDays(Long.parseLong("12"));
    Date dateConverted =
        Date.from(DateDexpiration.atStartOfDay(ZoneId.systemDefault()).toInstant());
    SimpleDateFormat sm = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
    // sm is the java.util.Date in MMM dd yyyy format
    // Converting it into String using formatter

    return sm.format(dateConverted);
  }

  public static String ChangeRadusDateDexpiration(Date RadusDateDexpiration) {

    Date safeDate = new Date(RadusDateDexpiration.getTime());
    LocalDate DateDexpiration = safeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    Date dateConverted =
        Date.from(DateDexpiration.atStartOfDay(ZoneId.systemDefault()).toInstant());
    SimpleDateFormat sm = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
    // sm is the java.util.Date in MMM dd yyyy format
    // Converting it into String using formatter

    return sm.format(dateConverted);
  }

  public static String SmsFormatDateEchance(Date date) {
    SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");

    return sm.format(date);
  }

  public static String xlsFormatDate(Date date) {
    SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");

    // sm is the java.util.Date in MMM dd yyyy format

    return sm.format(date);
  }

  public static Date convertStringToDate(String date) {
    ZoneId defaultZoneId = ZoneId.systemDefault();
    LocalDate datedenaissanceinstant = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);

    return Date.from(datedenaissanceinstant.atStartOfDay(defaultZoneId).toInstant());
  }

  public static Date convertStringToLocalDateTime(String dateString) {
    String input = dateString + "T23:59:59.999";
    LocalDateTime ldt = LocalDateTime.parse(input);

    return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date convertStringToLocalDataTimeStart(String dateString) {
    String input = dateString + "T00:00:00.000";
    LocalDateTime ldt = LocalDateTime.parse(input);

    return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date convertedFilterRechercheDate(String Stringdate) {
    LocalDateTime ldt = LocalDateTime.parse(Stringdate);

    return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

  }

  public static Date addOneDayToSmsSecondReminder(Date yourDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(yourDate);

    // Add one day to the date
    calendar.add(Calendar.DAY_OF_MONTH, 1);

    // Get the updated date

    return calendar.getTime();
  }

  public static int getYearFromDate(Date date) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int year = calendar.get(Calendar.YEAR);

    return year;
  }

  public static int getMonthFromDate(Date date) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int month = calendar.get(Calendar.MONTH) + 1;

    return month;
  }

  public static String formatedDate(Date date) {
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    return formatter.format(date);
  }

  public static String formatedDbouble(Double number) {
    DecimalFormat decimalFormat = new DecimalFormat("0.000");
    return decimalFormat.format(number);
  }


  public static long getRemainingDaysOfMonth(String dateString) {
    try {
      // Convertir la chaîne de date en LocalDate
      LocalDate date = LocalDate.parse(dateString,
          java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));

      // Obtenir la fin du mois
      LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

      // Calculer le nombre de jours restants
      long remainingDays = ChronoUnit.DAYS.between(date, endOfMonth);

      return remainingDays;

    } catch (Exception e) {
      e.printStackTrace();
      return -1; // Retourne -1 pour indiquer une erreur lors de la conversion de la date
    }
  }

  public static long getDaysOfMonth(String dateString) {
    // TODO Auto-generated method stub
    // Convertir la chaîne de date en LocalDate
    try {
      LocalDate date = LocalDate.parse(dateString,
          java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));

      // Obtenir la fin du mois
      return date.getDayOfMonth();
    } catch (Exception e) {
      e.printStackTrace();
      return -1; // Retourne -1 pour indiquer une erreur lors de la conversion de la date
    }
  }

  public static long getLastDaysOfMonth(String dateString) {
    // TODO Auto-generated method stub
    LocalDate date = LocalDate.parse(dateString,
        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
    // Obtenir la fin du mois
    LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());
    return endOfMonth.getDayOfMonth();
  }


  public static String addKeyValuePair(String jsonString, String key, String value) {
    try {
      // Parse the existing JSON string into a Map
      ObjectMapper objectMapper = new ObjectMapper();
      if (jsonString != null && !jsonString.equals("")) {
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        HashMap<String, Object> data = objectMapper.convertValue(jsonNode, HashMap.class);

        // Add the new key-value pair
        data.put(key, value);
        // Convert the Map back to a JSON string
        return objectMapper.writeValueAsString(data);
      } else {
        HashMap<String, Object> data = new HashMap<>();
        data.put(key, value);
        return objectMapper.writeValueAsString(data);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return jsonString; // Return the original string in case of an error
    }
  }


  public static boolean estPlusDe10Jours(LocalDate date) {
    LocalDate aujourdHui = LocalDate.now();
    long differenceJours = ChronoUnit.DAYS.between(date, aujourdHui);
    return differenceJours > 10;
  }

  public static boolean estPlusDeNombreDeJours(Date date, int nombreDeJours) {
    Date aujourdHui = new Date();
    // int comparison = date.compareTo(aujourdHui);
    long diff = aujourdHui.getTime() - date.getTime();
    long diffDays = diff / (24 * 60 * 60 * 1000);

    return diffDays >= nombreDeJours;

  }

  public static Long diffJoursBetween(Date date) {
    Date aujourdHui = new Date();
    long diff = aujourdHui.getTime() - date.getTime();
    long diffDays = diff / (24 * 60 * 60 * 1000);

    return diffDays;
  }

  public static Date addDaysToDate(long timestamp, int daysToAdd) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timestamp); // Set the calendar to the specified timestamp

    calendar.add(Calendar.DAY_OF_MONTH, daysToAdd); // Add the specified number of days

    return calendar.getTime(); // Return the updated Date object
  }

  public static Comparator<String> numericComparator = (str1, str2) -> {
    int num1 = Integer.parseInt(str1);
    int num2 = Integer.parseInt(str2);
    return num1 - num2;
  };


  public static Long DateDifference(Date oldDate, Date newDate) {


    long diffInMillies = newDate.getTime() - oldDate.getTime();
    long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);

    return diffInDays;
  }

  public static Double CalculePrixPackConsomeParJours(Double prixPack, Long dayConsomee,
      Long MonthLength) {
    double result = (double) dayConsomee / MonthLength;


    Double prixConsomeeJours = prixPack * result;

    return prixConsomeeJours;
  }

  public static Date stringToDate(String dateString) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
    LocalDate localDate = LocalDate.parse(dateString, formatter);
    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDate dateLimitePromo() {
    LocalDate dateLimitePromo = LocalDate.of(2024, 12, 31);
    return dateLimitePromo;
  }

  public static double calculerMontantMinimum(double plafond, double montantNonVerse) {
    double pourcentage = (plafond >= 0 && plafond <= 999) ? 0.20 : (plafond >= 1000) ? 0.30 : 0.2;

    // Si le plafond est < 500, aucune condition de réactivation ne s’applique
    if (pourcentage == 0.0)
      return 0.0;

    double maxDetteAutorisee = plafond * (1 - pourcentage);
    double montantARegler = montantNonVerse - maxDetteAutorisee;

    // Si montantARegler <= 0, pas besoin de paiement, sinon on retourne le montant requis
    return (montantARegler > 0) ? montantARegler : 0.0;
  }

  public static double truncatedDouble(double value) {
    double truncated = ((int) (value * 1000)) / 1000.0;
    return truncated;
  }
  
  public  static  List<String> codes = new ArrayList<>(Arrays.asList(

		  "M01MDT251LKNDU9ZE",
		  "M01MDT251RQM1JWEN",
		  "M01MDT2517OF0KLJK",
		  "M01MDT251D8EJWYAK",
		  "M01MDT251DB33W7DZ",
		  "M01MDT251LOCIKSZ5",
		  "M01MDT251OQ8GJLOR",
		  "M01MDT251ZAWJ2I1A",
		  "M01MDT251YYJ2ADRC",
		  "M01MDT251JNP4BAZG",
		  "M01MDT251JUYPMLQY",
		  "M01MDT251H7BNKFA3",
		  "M01MDT25186UGDQ3K",
		  "M01MDT251MHPOSBMZ",
		  "M01MDT251DRB9TD6C",
		  "M01MDT2517ENOF0UB",
		  "M01MDT251CMTGI6HW",
		  "M01MDT251OCTTEQ6A",
		  "M01MDT251S8JNAY5A",
		  "M01MDT251AGRUUUUR",
		  "M01MDT251AJJ956PV",
		  "M01MDT251UVO8WDE4",
		  "M01MDT251LHJK6CWB",
		  "M01MDT251IRIGNW9L",
		  "M01MDT251D9GO5ZCW",
		  "M01MDT251AKC5V9TP",
		  "M01MDT251YYNORPWL",
		  "M01MDT2514SQMCL3L",
		  "M01MDT251UMLVGRXM",
		  "M01MDT2510XZ0APWC",
		  "M01MDT251IXSOXPVC",
		  "M01MDT251QKFPJV0M",
		  "M01MDT251GXJKTZCD",
		  "M01MDT251AXWPACSE",
		  "M01MDT251AFTZ8YBZ",
		  "M01MDT2510Y3YDXC9",
		  "M01MDT251EH0YBGOY",
		  "M01MDT251O4ONVCQR",
		  "M01MDT251KI728JMC",
		  "M01MDT2514VH6H7VH",
		  "M01MDT251RNXNHRQU",
		  "M01MDT251EAZV4PSU",
		  "M01MDT2511X6MHSM5",
		  "M01MDT25166U5UOP2",
		  "M01MDT251CMZWTJL8",
		  "M01MDT251ICZHOFYL",
		  "M01MDT251B2WRFHEF",
		  "M01MDT251VYCYEOAL",
		  "M01MDT251QWWYQ3XP",
		  "M01MDT251OVRAUZDW",
		  "M01MDT251LJL0RQ54",
		  "M01MDT251VJIDF0IE",
		  "M01MDT251KSINPDIU",
		  "M01MDT251OYF8TBLE",
		  "M01MDT251LU0YMLUQ",
		  "M01MDT251U27SVHAU",
		  "M01MDT25101TTWHT3",
		  "M01MDT251KWDQTAAG",
		  "M01MDT2512SAZVXOT",
		  "M01MDT2514TNKJYZK",
		  "M01MDT251IPKEXXFG",
		  "M01MDT251KZSNYUQ4",
		  "M01MDT251PWGSQS4L",
		  "M01MDT251QQUODQ1H",
		  "M01MDT251TQ4GLZO7",
		  "M01MDT251BDHPO1OT",
		  "M01MDT251M09TJUXN",
		  "M01MDT2516STAMP4S",
		  "M01MDT251VNTGRHTU",
		  "M01MDT251EO9ZJDPP",
		  "M01MDT251RP8XTMI1",
		  "M01MDT251HOZ1SS1N",
		  "M01MDT251HRM85BJ6",
		  "M01MDT251UZZILKAC",
		  "M01MDT2511HUCNPGM",
		  "M01MDT251G8M1KOWD",
		  "M01MDT251AJLAXX8U",
		  "M01MDT251W1AFBK59",
		  "M01MDT251JATOID5M",
		  "M01MDT251A74AVQZS",
		  "M01MDT251NVDOMOOF",
		  "M01MDT251TACEPGQ9",
		  "M01MDT251WBC3JAJ6",
		  "M01MDT251NRE66W7Y",
		  "M01MDT251RCV2RBGQ",
		  "M01MDT251NE58OBLY",
		  "M01MDT251V5I0A6L9",
		  "M01MDT251FGMQFBOK",
		  "M01MDT25196B9KUQA",
		  "M01MDT251RJKQ6BNV",
		  "M01MDT251UN8RQOFK",
		  "M01MDT2517WVAEITI",
		  "M01MDT251EEHFK4ZT",
		  "M01MDT251TY82AULU",
		  "M01MDT251OBW2UYQQ",
		  "M01MDT251DAL9EQQL",
		  "M01MDT2510DTYHV1L",
		  "M01MDT251LI5HPFYV",
		  "M01MDT251KQSPDT78",
		  "M01MDT2517EZTY7ZF",
		  "M01MDT2517CJCTF9E",
		  "M01MDT251ROUMLS7W",
		  "M01MDT251AXLVXLDI",
		  "M01MDT251TOQ8ORVX",
		  "M01MDT251FPNY0SWH",
		  "M01MDT251PRTDF4LM",
		  "M01MDT251HXFEJICY",
		  "M01MDT251PCCIX2IJ",
		  "M01MDT251NFVSIVFL",
		  "M01MDT251SOV13BIK",
		  "M01MDT251TILKNYUW",
		  "M01MDT251FEJK4SHE",
		  "M01MDT251S5FPOGM3",
		  "M01MDT251ZAKI4IEU",
		  "M01MDT251XTWNXWWB",
		  "M01MDT251FRNZZYMI",
		  "M01MDT251YOCBGK4A",
		  "M01MDT251RAZXZR30",
		  "M01MDT25128V3VOCP",
		  "M01MDT251HRXNAGJN",
		  "M01MDT251PHWTXNTW",
		  "M01MDT251OFLRSRT3",
		  "M01MDT251ORDEMVGP",
		  "M01MDT251NMI4VHDO",
		  "M01MDT251THKGXQM6",
		  "M01MDT251D4TT5NNJ",
		  "M01MDT251W2ZFXZGC",
		  "M01MDT251RPZDXJYZ",
		  "M01MDT251CKU1SMKG",
		  "M01MDT251CQSNN5C0",
		  "M01MDT251BCIQ3JA6",
		  "M01MDT251K69I0QCR",
		  "M01MDT251QFZME0PX",
		  "M01MDT2519CYFKFJS",
		  "M01MDT2513BVXVTEP",
		  "M01MDT2513WPY3WRU",
		  "M01MDT251UOL0LJFH",
		  "M01MDT251JYBU9ZNF",
		  "M01MDT251V5FVWWNW",
		  "M01MDT2512XFQIM7Q",
		  "M01MDT251BTEEDWAD",
		  "M01MDT251C2ITSXLQ",
		  "M01MDT251EKAPTXPW",
		  "M01MDT251K474IDGT",
		  "M01MDT251ORID3J5S",
		  "M01MDT251PNDRYHWW",
		  "M01MDT251RV6ENYBY",
		  "M01MDT251Q6TKPCCC",
		  "M01MDT2514ANF6XFX",
		  "M01MDT25170QD1AMU",
		  "M01MDT251TJDKYQWD",
		  "M01MDT251FSD5TRGA",
		  "M01MDT251J9UNEIWF",
		  "M01MDT251W7ANYVYN",
		  "M01MDT251TSA7W77X",
		  "M01MDT2511PLDBAUL",
		  "M01MDT251W5ZYXIPC",
		  "M01MDT251NLAOZA6L",
		  "M01MDT25151WXV443",
		  "M01MDT251Z66B1FNU",
		  "M01MDT251DQON5AXZ",
		  "M01MDT2515FNOQXVY",
		  "M01MDT25162URECNQ",
		  "M01MDT251JVMFONAM",
		  "M01MDT251L6PZTV71",
		  "M01MDT251WR4DMAPE",
		  "M01MDT2512YRLVVZF",
		  "M01MDT251HY6ESHWM",
		  "M01MDT251NKISZPIK",
		  "M01MDT251EHPGUK4H",
		  "M01MDT251QE7Z3VY9",
		  "M01MDT251CFKO254T",
		  "M01MDT251J9TQ2GBF",
		  "M01MDT2513A0Z3DF4",
		  "M01MDT251HHQSKOQN",
		  "M01MDT251TDHXCZWI",
		  "M01MDT251URIDMOIX",
		  "M01MDT251F1O2SNKX",
		  "M01MDT251H1OI8INP",
		  "M01MDT251G5NWLBFA",
		  "M01MDT251ON7EICQV",
		  "M01MDT251IZTPPTWT",
		  "M01MDT251FEHYFBLA",
		  "M01MDT251RT8UK1T5",
		  "M01MDT251EM91QU4G",
		  "M01MDT251RHKOKOSI",
		  "M01MDT251PK4I1PN1",
		  "M01MDT251DEAC7GVJ",
		  "M01MDT251FWTJGQP0",
		  "M01MDT251SIQCTMLY",
		  "M01MDT2516BNV37UN",
		  "M01MDT251OIZM2PBX",
		  "M01MDT2515GIKGUFZ",
		  "M01MDT251UYLZ5JKH",
		  "M01MDT251HAM8QWM3",
		  "M01MDT251UT3ZQZGP",
		  "M01MDT2510WFYF4GI",
		  "M01MDT25198NZOQ21",
		  "M01MDT251FNMSYNYQ",
		  "M01MDT251HWWDA0MN",
		  "M01MDT251OEIGIQHC",
		  "M01MDT251YYDIGLPU",
		  "M01MDT251NV5SFXAR",
		  "M01MDT251NEORI6KI",
		  "M01MDT2515B0M0CII",
		  "M01MDT2513SO2MASN",
		  "M01MDT251WP62AOH7",
		  "M01MDT251C4BTVSXW",
		  "M01MDT251J3EXWDET",
		  "M01MDT2517RREGOU5",
		  "M01MDT251LOIRMT3M",
		  "M01MDT251KGELIGRU",
		  "M01MDT251LUSB7XHQ",
		  "M01MDT251CNSR6BFB",
		  "M01MDT2517KPDGU0X",
		  "M01MDT251VNNDH8CE",
		  "M01MDT251Z5D2FPWL",
		  "M01MDT251ZB9MMWZS",
		  "M01MDT251D2WEJ3UC",
		  "M01MDT2515RCOUHXG",
		  "M01MDT251SP2GNMD9",
		  "M01MDT251OI3GCTTW",
		  "M01MDT2512GXBYVW2",
		  "M01MDT251W4IIVZDP",
		  "M01MDT25187EIFR03",
		  "M01MDT251TB8TLSLJ",
		  "M01MDT251EWUQPU8D",
		  "M01MDT251KYB1QIFL",
		  "M01MDT251BPFK9EIQ",
		  "M01MDT251TPO4V5FV",
		  "M01MDT251UDBQ6K28",
		  "M01MDT2519CEABCV3",
		  "M01MDT2516ORUZTAV",
		  "M01MDT251FIDJ6DM7",
		  "M01MDT251OVFNOLIB",
		  "M01MDT251VG0MQQYN",
		  "M01MDT251JSFF0BMZ",
		  "M01MDT251BG3HDGS8",
		  "M01MDT251F6WOE71P",
		  "M01MDT251QU7QQWJT",
		  "M01MDT251DBNYIBBA",
		  "M01MDT251FBZFBGK4",
		  "M01MDT2514BEXMQTN",
		  "M01MDT251Y5TREVIN",
		  "M01MDT251GMVBR2CF",
		  "M01MDT251SAMEJQ42",
		  "M01MDT251YDWQCTJA",
		  "M01MDT251BBFTYZWA",
		  "M01MDT251G9XORDLM",
		  "M01MDT251XWF4FIBT",
		  "M01MDT251UM1NZHLK",
		  "M01MDT251JKRLDIVM",
		  "M01MDT251V8BHKZ36",
		  "M01MDT251TJJ0KIVN",
		  "M01MDT2511NSYPG3B",
		  "M01MDT251ZN1DSFIM",
		  "M01MDT251SYRVY685",
		  "M01MDT251L5MHDHGA",
		  "M01MDT251IGX1GBNV",
		  "M01MDT251XSGNZYMK",
		  "M01MDT2515JGPP7TZ",
		  "M01MDT251TGQUHG4F",
		  "M01MDT251JGMY3BKE",
		  "M01MDT251QYPFEJPS",
		  "M01MDT2516WD1TWTH",
		  "M01MDT251ZEOWJQCV",
		  "M01MDT251JSWGW30W",
		  "M01MDT251PNB1OQWR",
		  "M01MDT251Z0KZUH4X",
		  "M01MDT251X0EKFUUK",
		  "M01MDT251MFOFGQFX",
		  "M01MDT251DYXDRRFR",
		  "M01MDT251CPVLYP7S",
		  "M01MDT251R9H1UJFW",
		  "M01MDT251D7ZQD7EQ",
		  "M01MDT251PNNIJSYE",
		  "M01MDT2513CDUJKIF",
		  "M01MDT251TSBKIA4T",
		  "M01MDT251RLRMI6QL",
		  "M01MDT2510YD1XJRI",
		  "M01MDT25175K0OX3H",
		  "M01MDT251P8KWBUHD",
		  "M01MDT251CKMCMUDZ",
		  "M01MDT251QQGWF1UR",
		  "M01MDT251URZTDLDU",
		  "M01MDT251YTC8WDHK",
		  "M01MDT251P1XBPZJZ",
		  "M01MDT251OBHRQOO6",
		  "M01MDT251FFB21FWI",
		  "M01MDT251QBUKVD3G",
		  "M01MDT251DSQAJGIN",
		  "M01MDT251BPOGTYNI",
		  "M01MDT251470MCB8F",
		  "M01MDT251ZTIDVWW3",
		  "M01MDT251C3XIFDTK",
		  "M01MDT251OU6GRUFD",
		  "M01MDT251UVIPRZFW",
		  "M01MDT251T61PQI7N",
		  "M01MDT251AHLUUSP4",
		  "M01MDT251GHRIJIUE",
		  "M01MDT251LQ1BV5BT",
		  "M01MDT251A6LVRS3E",
		  "M01MDT251VXEQDYM0",
		  "M01MDT251QDMTRAGL",
		  "M01MDT2510RJ3F1EG",
		  "M01MDT251RDOV9VS2",
		  "M01MDT251ZND88QRC",
		  "M01MDT251AZAYDNJX",
		  "M01MDT2518VZFISZ8",
		  "M01MDT251YY2DLTMA",
		  "M01MDT251SYCAST68",
		  "M01MDT251ROG2CKV0",
		  "M01MDT251GH44JHVX",
		  "M01MDT251TABXMDSW",
		  "M01MDT2515U7OD4JN",
		  "M01MDT251BZPOKPVR",
		  "M01MDT251DJJMKDF3",
		  "M01MDT251YMTGOLUB",
		  "M01MDT251N4E8U89A",
		  "M01MDT251ETJWKOQZ",
		  "M01MDT2514QVUMOOU",
		  "M01MDT251OL6RX11X",
		  "M01MDT251MJHN93N9",
		  "M01MDT251PK2XQWZP",
		  "M01MDT251LM7BQAYH",
		  "M01MDT251P6PFUPCU",
		  "M01MDT251GY5HGPSC",
		  "M01MDT251JIZY9B8N",
		  "M01MDT251K5CMDKTW",
		  "M01MDT2514CQMIZF8",
		  "M01MDT2514EP5UQYL",
		  "M01MDT251NHCXLRUL",
		  "M01MDT251IMUARWTK",
		  "M01MDT251BXZ3D0CO",
		  "M01MDT251IBYXMANY",
		  "M01MDT2514JYLL0GC",
		  "M01MDT251N62O0L8W",
		  "M01MDT251KDZXCPYO",
		  "M01MDT251Z9QMRHDM",
		  "M01MDT2510G4N2MPM",
		  "M01MDT251OQ7ZSXCZ",
		  "M01MDT251CHANC3KC",
		  "M01MDT251Y4XLG7CY",
		  "M01MDT2512J9G0EEF",
		  "M01MDT251XNISDQXM",
		  "M01MDT251L2EZNKTL",
		  "M01MDT251B5TRESGU",
		  "M01MDT25117RYRT9E",
		  "M01MDT251JMNNSYX3",
		  "M01MDT2511WS6BZB8",
		  "M01MDT251UVBJ3RFA",
		  "M01MDT2514LDYE16M",
		  "M01MDT251XRFMBUR1",
		  "M01MDT251OZHBBBMF",
		  "M01MDT251LFHLV9N1",
		  "M01MDT251ZHRWAUVN",
		  "M01MDT251LE1XUJ2V",
		  "M01MDT251CEFVFO7H",
		  "M01MDT251VTI2LOBV",
		  "M01MDT251SES8FPNX",
		  "M01MDT251OX20JUIJ",
		  "M01MDT2515ZBKWDBA",
		  "M01MDT251EM5TAGXE",
		  "M01MDT2512LQTUP2Q",
		  "M01MDT251K7AJGO2G",
		  "M01MDT251ZOEKXSCX",
		  "M01MDT251U7BDMSR8",
		  "M01MDT251TZEO9USB",
		  "M01MDT25191KO3FHN",
		  "M01MDT251HDPVFIIY",
		  "M01MDT251SYVSIODH",
		  "M01MDT251D99Y103P",
		  "M01MDT2510AIUAAK8",
		  "M01MDT251IKTQNFQI",
		  "M01MDT251CXTZ4FQ9",
		  "M01MDT251DUP95JAZ",
		  "M01MDT251XRAVFQAS",
		  "M01MDT251FUPPDGVB",
		  "M01MDT251FXK9CGRO",
		  "M01MDT2516AN6K2FS",
		  "M01MDT251NNVW9NZ1",
		  "M01MDT251SUYUYOEJ",
		  "M01MDT251ZQYEIMHL",
		  "M01MDT2511KV3RALU",
		  "M01MDT251QQLSFLAU",
		  "M01MDT251Z7IFOBQV",
		  "M01MDT251YPJRGPIB",
		  "M01MDT251PO4GG607",
		  "M01MDT251BXYI6LEA",
		  "M01MDT251JJRONXYH",
		  "M01MDT251MPGHEYIA",
		  "M01MDT25185QPR0JS",
		  "M01MDT251MBSOGAXP",
		  "M01MDT2516WFOY6K0",
		  "M01MDT251VMQLMTPV",
		  "M01MDT251JGRWJRDA",
		  "M01MDT251RHXRARKL",
		  "M01MDT251A1QFNTUK",
		  "M01MDT251F8HXTAH7",
		  "M01MDT251MYNRW0RU",
		  "M01MDT25158LL19R3",
		  "M01MDT251IVKQNGJT",
		  "M01MDT251XJTOEEVP",
		  "M01MDT251SRYDGBZR",
		  "M01MDT251LLRI8QSV",
		  "M01MDT251CBJCAMCE",
		  "M01MDT251SERD3HVE",
		  "M01MDT251OFFGKXR0",
		  "M01MDT251U4TK8ODI",
		  "M01MDT251WXLH9USB",
		  "M01MDT2510ALONKSI",
		  "M01MDT251N52WEUWE",
		  "M01MDT251RBPB5TKR",
		  "M01MDT251WS3D8SMQ",
		  "M01MDT251PQTRAY1J",
		  "M01MDT251IDWJNEUP",
		  "M01MDT251CIIRK1N4",
		  "M01MDT251VVDPPF21",
		  "M01MDT251OVTVP7XF",
		  "M01MDT251BMTVZZFZ",
		  "M01MDT251QQO1BGTH",
		  "M01MDT251NLIUPCUB",
		  "M01MDT251RAM72IGO",
		  "M01MDT251T9ZVVBE3",
		  "M01MDT251KTNYJ5UI",
		  "M01MDT251BTTTZF1U",
		  "M01MDT251JRQAB312",
		  "M01MDT25129BEIWTA",
		  "M01MDT251WDP5O7TZ",
		  "M01MDT251OWIFXYPH",
		  "M01MDT251K9JMBCRI",
		  "M01MDT251LTNP0ODH",
		  "M01MDT251W8CPATHR",
		  "M01MDT2511HQJBCYO",
		  "M01MDT251UD1UAUNG",
		  "M01MDT2519WQGMIEU",
		  "M01MDT251VSEVUZK8",
		  "M01MDT251M1VICTDP",
		  "M01MDT251N4TSE2F3",
		  "M01MDT251E4RDM1AS",
		  "M01MDT251SWOFEPAZ",
		  "M01MDT251XJMTCWWJ",
		  "M01MDT251CLHNDPVJ",
		  "M01MDT251QFMEQGSN",
		  "M01MDT251WHGOGYAU",
		  "M01MDT251PM7BIJCD",
		  "M01MDT251LEGTETR5",
		  "M01MDT2515JBLCY4V",
		  "M01MDT251ZVU7OWAZ",
		  "M01MDT251EJ7NZUPQ",
		  "M01MDT251XFWHHDBI",
		  "M01MDT251EBGQQX0O",
		  "M01MDT251YLQQJPNR",
		  "M01MDT251MCFMOC1J",
		  "M01MDT251QBTI8P7P",
		  "M01MDT251JFCR2CH7",
		  "M01MDT251YSOAB6TN",
		  "M01MDT251KRRHEGKM",
		  "M01MDT2518LONSEDX",
		  "M01MDT251UWOYWYR8",
		  "M01MDT251WGJQBMXW",
		  "M01MDT251N5FREXAV",
		  "M01MDT251UJFJVATC",
		  "M01MDT251EORXWKMB",
		  "M01MDT251S1U2UVNZ",
		  "M01MDT251YJOBH6XT",
		  "M01MDT251RGOAXWA2",
		  "M01MDT251GV01IWIF",
		  "M01MDT251HVSH27KH",
		  "M01MDT251FNVKU4HL",
		  "M01MDT251VTAU4SKZ",
		  "M01MDT251LDRZJY9R",
		  "M01MDT251JPEDTB0M",
		  "M01MDT251HS9ASZZI",
		  "M01MDT251UV29ZBG0",
		  "M01MDT2516GVFJSB0",
		  "M01MDT2510PQUIAFW",
		  "M01MDT251SFJKLSQ2",
		  "M01MDT251EIWDVSW1",
		  "M01MDT251XVJFLFBT",
		  "M01MDT251NKKUIQDX",
		  "M01MDT251REIUCJRM",
		  "M01MDT251H9NAWMDM",
		  "M01MDT251PQ2W2CCR",
		  "M01MDT251X3AURHBG",
		  "M01MDT251BEFIPNJI",
		  "M01MDT251VO6SWDMH",
		  "M01MDT251ZSZBZOFD",
		  "M01MDT251IQUOUEVR",
		  "M01MDT251UPAT4MYC",
		  "M01MDT2517HEB9SWI",
		  "M01MDT251KZ5QEMHL",
		  "M01MDT251NEVKD70H",
		  "M01MDT251C7IGQMJT",
		  "M01MDT251BTVYPPWK",
		  "M01MDT251GNMC9LT2",
		  "M01MDT2517HWERAVR",
		  "M01MDT251Z3LXHUCP",
		  "M01MDT251HLPOMRMK",
		  "M01MDT251ITX5E1E9",
		  "M01MDT251AUJFG5NO",
		  "M01MDT251S8MYQGQ2",
		  "M01MDT251LTQ0T1KU",
		  "M01MDT251TKEQGCCE",
		  "M01MDT251NFNVHJAA",
		  "M01MDT2513SYJCBZ6",
		  "M01MDT251WDBOTVEF",
		  "M01MDT251SIEOMT4I",
		  "M01MDT251CFYTDDFB",
		  "M01MDT251HBZTH5XA",
		  "M01MDT251GNBFFTSG",
		  "M01MDT251RKGEJJZA",
		  "M01MDT251V2AGMSQ1",
		  "M01MDT251CCUNBCI6",
		  "M01MDT251A5WIEGT2",
		  "M01MDT251OHATBQDU",
		  "M01MDT251PGYSH1GO",
		  "M01MDT251MCICB8V4",
		  "M01MDT2515ASCIKK5",
		  "M01MDT251IFXWL2NY",
		  "M01MDT2511KDAXN4I",
		  "M01MDT251OI6EASXU",
		  "M01MDT251CDFIWWSW",
		  "M01MDT251GS1RUTSU",
		  "M01MDT2511K10QVXB",
		  "M01MDT251VJBCMETQ",
		  "M01MDT251UWRHUO2N",
		  "M01MDT251FA3B8M8Z",
		  "M01MDT251NVXADR05",
		  "M01MDT2511DG9TWSG",
		  "M01MDT251JASPYUON",
		  "M01MDT251Q2ZXUDAY",
		  "M01MDT251VLWVZNMZ",
		  "M01MDT251ZZOTKD5U",
		  "M01MDT251VJEWRVOD",
		  "M01MDT251ERTSUPGH",
		  "M01MDT251UFA8LO4M",
		  "M01MDT251XRTV36VD",
		  "M01MDT251PVYUZ8F4",
		  "M01MDT2517SKNIPHM",
		  "M01MDT251HWRRDTLO",
		  "M01MDT251MZCF9ANY",
		  "M01MDT251P0B3R8SJ",
		  "M01MDT251GJYWS3JO",
		  "M01MDT251WLB0WPYZ",
		  "M01MDT251WKDPM4NR",
		  "M01MDT251DXCH4SW0",
		  "M01MDT251YT6RTBQT",
		  "M01MDT251LRVXDJFF",
		  "M01MDT251BIR4TFJE",
		  "M01MDT251GG7JZOIS",
		  "M01MDT251E8RPESXG",
		  "M01MDT2513DOEME7U",
		  "M01MDT251QZQN8JRB",
		  "M01MDT251NJYKG7JB",
		  "M01MDT251J1BWSF5K",
		  "M01MDT251NOWL8BRP",
		  "M01MDT251CNY4VYD2",
		  "M01MDT251QRYZSEQ1",
		  "M01MDT2514L6Y0B3J",
		  "M01MDT251WT9GDWVM",
		  "M01MDT251A2A7JSNL",
		  "M01MDT251FJF5ED3X",
		  "M01MDT251ISTU21IF",
		  "M01MDT251NVSGC8AU",
		  "M01MDT251MZLPE2FO",
		  "M01MDT251WUKYBWGR",
		  "M01MDT251T6RN5DZO",
		  "M01MDT251CJMWJMEF",
		  "M01MDT25112WRIOCR",
		  "M01MDT2512RB4IGNE",
		  "M01MDT251GFEQICVM",
		  "M01MDT251XN4GJD84",
		  "M01MDT2511TFOYE3V",
		  "M01MDT251VAM4QOLR",
		  "M01MDT251SJAGSYNF",
		  "M01MDT251W05S8S46",
		  "M01MDT251RODINZFW",
		  "M01MDT2517IAN99W0",
		  "M01MDT251PMIIJMIO",
		  "M01MDT251SYKGUHEQ",
		  "M01MDT25199INKUQB",
		  "M01MDT251COYQAS3N",
		  "M01MDT2519BRMJ2C7",
		  "M01MDT251ROXWLFMP",
		  "M01MDT251ROPZVXZY",
		  "M01MDT251QNGPIN80",
		  "M01MDT251EEQKJQN4",
		  "M01MDT251YY2RDFEI",
		  "M01MDT251YPMDEJXK",
		  "M01MDT251KHW2MUEL",
		  "M01MDT251863SDPVO",
		  "M01MDT251FYW1OJZN",
		  "M01MDT251EFS4A2EL",
		  "M01MDT251Z0PZ5GET",
		  "M01MDT251MTQZXWGM",
		  "M01MDT251HR8SVCKP",
		  "M01MDT251UNYMTT3F",
		  "M01MDT251R2YEN6ZF",
		  "M01MDT2517NIIITWW",
		  "M01MDT251EXQL0YTT",
		  "M01MDT251XVHX5RYE",
		  "M01MDT251C5ZC2YNJ",
		  "M01MDT251RGLQQQ4N",
		  "M01MDT251HOYQIZ5A",
		  "M01MDT251SMDJ1LSF",
		  "M01MDT251HLH0UAUD",
		  "M01MDT251FY6YYWRM",
		  "M01MDT251HL6HU0OO",
		  "M01MDT2518SBO9BDI",
		  "M01MDT251QR1VDT8Y",
		  "M01MDT251A6YUTMXM",
		  "M01MDT251I7MV7VPF",
		  "M01MDT25173AXCM7C",
		  "M01MDT2510A6KMUEQ",
		  "M01MDT251B4HEM4SX",
		  "M01MDT251EJ9UYNMZ",
		  "M01MDT251SNP7YNPU",
		  "M01MDT251UTE7HSCL",
		  "M01MDT251B8GUOYWV",
		  "M01MDT251WXFLHLMM",
		  "M01MDT251QYEZYE12",
		  "M01MDT251AETMPXCP",
		  "M01MDT251O8AK0M83",
		  "M01MDT251E9SNO8LY",
		  "M01MDT251PM1LF89U",
		  "M01MDT251BVFXMVEY",
		  "M01MDT251V9THUNG2",
		  "M01MDT251PURGMVMX",
		  "M01MDT251RTMJXAQ2",
		  "M01MDT251RLOCZODF",
		  "M01MDT251YRLEB3FH",
		  "M01MDT251BZ58JMQJ",
		  "M01MDT251EWDLBI1A",
		  "M01MDT251HSHMPMBW",
		  "M01MDT251AR27VF05",
		  "M01MDT251R18GKG2X",
		  "M01MDT251TZXFHRYS",
		  "M01MDT251NZCEVLC9",
		  "M01MDT251CLBEK02W",
		  "M01MDT251P0H0T92A",
		  "M01MDT251FX381ZLS",
		  "M01MDT251QSQPGIYD",
		  "M01MDT251XEKWQCBV",
		  "M01MDT251BG20RPLZ",
		  "M01MDT25199I36H6W",
		  "M01MDT251OFA94IAU",
		  "M01MDT251XNFYLLXX",
		  "M01MDT251KI37HBBJ",
		  "M01MDT251BPBQPOQV",
		  "M01MDT251RPGOB7TT",
		  "M01MDT251BO4KWCOD",
		  "M01MDT251G8JGXRCT",
		  "M01MDT2513UY8VQLY",
		  "M01MDT251TZAOGMAB",
		  "M01MDT251NMIGFJAH",
		  "M01MDT251NOHOLBA1",
		  "M01MDT251XEKO9DMK",
		  "M01MDT251X3ZKUOYG",
		  "M01MDT251LYDE8VG8",
		  "M01MDT251EMPNGIY7",
		  "M01MDT251V0HNIW2U",
		  "M01MDT251VISYH0UE",
		  "M01MDT2514SIQSASM",
		  "M01MDT251FTGNRU8X",
		  "M01MDT251T19WGSV5",
		  "M01MDT251SNPPO5RI",
		  "M01MDT251U8IK8H12",
		  "M01MDT251MTKQP57C",
		  "M01MDT2514CATV6OS",
		  "M01MDT251AEFKEVQF",
		  "M01MDT25173OSW4VO",
		  "M01MDT251R7MFBSRA",
		  "M01MDT251WUGVCIRQ",
		  "M01MDT251OIDN8SZB",
		  "M01MDT251T7UTLWMT",
		  "M01MDT251ZPDJOE3X",
		  "M01MDT251MRT7R3XO",
		  "M01MDT251M1XGFEWQ",
		  "M01MDT2517PUGBHD5",
		  "M01MDT251HK4TVBGE",
		  "M01MDT251LEPTFSAR",
		  "M01MDT2518VJGJ2L7",
		  "M01MDT251Z0FNUURF",
		  "M01MDT251MBUJ4QIA",
		  "M01MDT251U4JEED0M",
		  "M01MDT251V2PXEEV7",
		  "M01MDT251Z4UOYZ4D",
		  "M01MDT251J5LYOQTY",
		  "M01MDT251G3MFSTWP",
		  "M01MDT25159BZQP6L",
		  "M01MDT251V7HEPOJ2",
		  "M01MDT251CWC9QFIE",
		  "M01MDT251LFXZXJCN",
		  "M01MDT251MINBYCGI",
		  "M01MDT251IY1K0ZC8",
		  "M01MDT251UTEQHH9U",
		  "M01MDT251U2URD9JA",
		  "M01MDT251KS1FPM7I",
		  "M01MDT251GE6XPVWQ",
		  "M01MDT251E0WAR5IR",
		  "M01MDT251OCWPY8IS",
		  "M01MDT2513ZJ60OVP",
		  "M01MDT251I1Q9NLXP",
		  "M01MDT251ZWC8APO9",
		  "M01MDT251ALVB4VZW",
		  "M01MDT251XKJVDD8L",
		  "M01MDT251OCQBUXJL",
		  "M01MDT251KKEEYIHJ",
		  "M01MDT251WOZJOJWJ",
		  "M01MDT251UZS4C1OH",
		  "M01MDT2513QFRHLIS",
		  "M01MDT251C2KJMZ7Y",
		  "M01MDT2515Y1TAJ8J",
		  "M01MDT25112SXU9ZA",
		  "M01MDT251WTTW70SF",
		  "M01MDT251ICJHNARC",
		  "M01MDT251OOB1J1NS",
		  "M01MDT2519QADI5MV",
		  "M01MDT251CA2QJDO1",
		  "M01MDT25146ALOJ5C",
		  "M01MDT251AJYRNG9S",
		  "M01MDT251HKPWTSHD",
		  "M01MDT251LYXF88EX",
		  "M01MDT251001KFEN4",
		  "M01MDT2513RNBCXMV",
		  "M01MDT251ZDA56M6Q",
		  "M01MDT251VFOOVZJR",
		  "M01MDT251U2IAOXT6",
		  "M01MDT251DLJJJGPL",
		  "M01MDT251W5G57LFF",
		  "M01MDT251NHEJIQLW",
		  "M01MDT251A9THOYAR",
		  "M01MDT251RZQMCA40",
		  "M01MDT251UCELDUIP",
		  "M01MDT251A48RI4WH",
		  "M01MDT2518EBALKN1",
		  "M01MDT251U4JCI6QW",
		  "M01MDT251NQERQIER",
		  "M01MDT251QYX4VNOE",
		  "M01MDT2510E66I6I0",
		  "M01MDT251DFZDQRXL",
		  "M01MDT251XEV8SP5F",
		  "M01MDT251MWZDHCBP",
		  "M01MDT251T1LMTHQU",
		  "M01MDT2515CX4CIQA",
		  "M01MDT251CYYY6NXC",
		  "M01MDT251MTVUSTHT",
		  "M01MDT251ZZ6Q1ASH",
		  "M01MDT251MIGCVMRR",
		  "M01MDT251BPCSAWZX",
		  "M01MDT251NPGRFUA8",
		  "M01MDT251SCIGLJZB",
		  "M01MDT251LLZHE3T6",
		  "M01MDT251L8SHMG6F",
		  "M01MDT2512NNX7NCD",
		  "M01MDT251RZIYQFOY",
		  "M01MDT2512RZDIQLA",
		  "M01MDT251YFSYMOYZ",
		  "M01MDT251P4EAPHNW",
		  "M01MDT251WHHKIL8Z",
		  "M01MDT2516XLSX8HA",
		  "M01MDT251LFKLHBCB",
		  "M01MDT251QBSV53FV",
		  "M01MDT2511VR4E3B7",
		  "M01MDT251KHVI5X3N",
		  "M01MDT25137HEPLRJ",
		  "M01MDT251T2LMGWPA",
		  "M01MDT251T6VMLTI9",
		  "M01MDT251YFUCHAJC",
		  "M01MDT251HSKM7X70",
		  "M01MDT251LRTIUPBT",
		  "M01MDT251XO5KJ3DI",
		  "M01MDT2518KGHVCBH",
		  "M01MDT251FHTE7JFZ",
		  "M01MDT251IFHR7ARD",
		  "M01MDT251PIYTLTKP",
		  "M01MDT251VSZH2D5K",
		  "M01MDT251T88IOU2H",
		  "M01MDT2513BYALY5T",
		  "M01MDT251AQFKCMX0",
		  "M01MDT251Z9GXK5HF",
		  "M01MDT251MIVDY1W2",
		  "M01MDT251RQYMH4EU",
		  "M01MDT25187RUAEVQ",
		  "M01MDT251KNF08HZQ",
		  "M01MDT251SNO5E5IK",
		  "M01MDT251OC128FIX",
		  "M01MDT251XLORLS3S",
		  "M01MDT251YGIBFAG1",
		  "M01MDT2515G9U5EHT",
		  "M01MDT251CG5UZNQL",
		  "M01MDT251W6DPUEIF",
		  "M01MDT2510IKG3PP1",
		  "M01MDT2519SHQWATD",
		  "M01MDT251PXN7D1MC",
		  "M01MDT2514PEDKRWZ",
		  "M01MDT251QQ7GCWWW",
		  "M01MDT251VMFTW3ND",
		  "M01MDT251KKKCXDXG",
		  "M01MDT251JR7QEF7Z",
		  "M01MDT251OEUR6TK8",
		  "M01MDT251P09R5RK9",
		  "M01MDT251FMVQQOCZ",
		  "M01MDT251TYKFSB89",
		  "M01MDT2518VJR4J66",
		  "M01MDT251PNNVMH7K",
		  "M01MDT251EEFDMOG4",
		  "M01MDT251QEUS8ZWX",
		  "M01MDT2518OUAATXU",
		  "M01MDT251BU1FEFTJ",
		  "M01MDT2513OZ3CSAI",
		  "M01MDT251IMOMEFJL",
		  "M01MDT251UG9Q4GVZ",
		  "M01MDT251UGCGHWTQ",
		  "M01MDT251XHXESXH2",
		  "M01MDT251LTRYRX2J",
		  "M01MDT251C5HM0DSH",
		  "M01MDT251SSXKKUAA",
		  "M01MDT251GBISPQCW",
		  "M01MDT251B36RCU2R",
		  "M01MDT251WQDGNNJ2",
		  "M01MDT251HJDNJOL0",
		  "M01MDT251MHRGHSOE",
		  "M01MDT251PXB0KS81",
		  "M01MDT2510989VJYG",
		  "M01MDT251XUCROHZN",
		  "M01MDT2519GDXAFQG",
		  "M01MDT251ILKXGCID",
		  "M01MDT251DRKR1MZW",
		  "M01MDT251NIZTICTP",
		  "M01MDT251RS8U1UIG",
		  "M01MDT251ONUR9325",
		  "M01MDT251BP2JQ7AS",
		  "M01MDT251JB8ACXIH",
		  "M01MDT251KLELEW60",
		  "M01MDT2513IX8RJ0M",
		  "M01MDT251L1CIGHMY",
		  "M01MDT2513LIDBG7X",
		  "M01MDT251OJVF4K8O",
		  "M01MDT251R96PDGZ1",
		  "M01MDT251DJT9RQNI",
		  "M01MDT251CN2FI9KU",
		  "M01MDT251XHFZTXG6",
		  "M01MDT251OFOJODGT",
		  "M01MDT251HDHDMKKF",
		  "M01MDT251IJ9USY2N",
		  "M01MDT251IA5GNVGB",
		  "M01MDT251CE5BLJXQ",
		  "M01MDT251YO3ZSTTZ",
		  "M01MDT251E7JO90LN",
		  "M01MDT251NFIJPHHS",
		  "M01MDT251CGVTCCSV",
		  "M01MDT251YSFXDDOU",
		  "M01MDT251LHRUTNOT",
		  "M01MDT251HCP3TFDB",
		  "M01MDT251HQ2MPPZ3",
		  "M01MDT251IZX2QRCF",
		  "M01MDT251PUJSZOFJ",
		  "M01MDT251SHRN6CGA",
		  "M01MDT251PD8JM3BT",
		  "M01MDT251Y0VQAD5X",
		  "M01MDT251GYFZFZZL",
		  "M01MDT2512MZNCOME",
		  "M01MDT251EVK1VCSL",
		  "M01MDT251COCLIYTW",
		  "M01MDT251HDYNO850",
		  "M01MDT251EZ2DFR5L",
		  "M01MDT251JXYIZ2JZ",
		  "M01MDT251MV5VV7LP",
		  "M01MDT2515GVYBLXP",
		  "M01MDT25142Q3YQAZ",
		  "M01MDT251RWYI0KPN",
		  "M01MDT251CJKWHKPX",
		  "M01MDT251UWRF08RW",
		  "M01MDT251F3NOSY6C",
		  "M01MDT251DOPHRDFT",
		  "M01MDT251IDAGV10D",
		  "M01MDT251A5CVHZTD",
		  "M01MDT2511NH8PNMB",
		  "M01MDT251ZIXXLCJE",
		  "M01MDT251P1QHUBBU",
		  "M01MDT251JBBTHNWJ",
		  "M01MDT251AYHDLEFK",
		  "M01MDT2519GEE8DWD",
		  "M01MDT251BJRYB6ZA",
		  "M01MDT251IJEGOKCO",
		  "M01MDT251LQOSKD6N",
		  "M01MDT251QSIARSVE",
		  "M01MDT251BRIKSOXQ",
		  "M01MDT251MJU035LD",
		  "M01MDT2511HFKV8UN",
		  "M01MDT251TS6VLACB",
		  "M01MDT251KAZY8PCO",
		  "M01MDT251ITODRDRY",
		  "M01MDT251VLXMJXLX",
		  "M01MDT251BL3WY07C",
		  "M01MDT251C6RMIDFP",
		  "M01MDT251KEIZAAGD",
		  "M01MDT2510OP1UAFA",
		  "M01MDT251OBW1NFTZ",
		  "M01MDT251FNG7HVQV",
		  "M01MDT2513RKBGRY5",
		  "M01MDT251EVH9GTT4",
		  "M01MDT251D4O4VXNV",
		  "M01MDT251PVJRIGL2",
		  "M01MDT251GOIPPZVE",
		  "M01MDT251VUP1TX4F",
		  "M01MDT251VAFSOTAG",
		  "M01MDT251IHGQHFVK",
		  "M01MDT251JA6BX05L",
		  "M01MDT251PAQJGHL7",
		  "M01MDT251IGQDQV9X",
		  "M01MDT2512PB28S5N",
		  "M01MDT25163TKQXO4",
		  "M01MDT251UHQ7A3VG",
		  "M01MDT2518JJIF2JQ",
		  "M01MDT251WMQPP6AG",
		  "M01MDT251D0HRQL9J",
		  "M01MDT251MC05GNS0",
		  "M01MDT251PRYWDPUW",
		  "M01MDT251QSODQBJC",
		  "M01MDT251VOVUZGOV",
		  "M01MDT251QV9H6AIT",
		  "M01MDT251TV6H7SO0",
		  "M01MDT2512DJUC857",
		  "M01MDT251JCHLEXIS",
		  "M01MDT251AO1XYLWM",
		  "M01MDT251MGLKP7UJ",
		  "M01MDT251GHAK2IVC",
		  "M01MDT251LL9GCABV",
		  "M01MDT251MMRPCYEU",
		  "M01MDT251YI9DAHJS",
		  "M01MDT251VUS7SC3M",
		  "M01MDT251OEX8DPC7",
		  "M01MDT251DRQEBSPT",
		  "M01MDT251OJO0N68V",
		  "M01MDT251SIPFMKBB",
		  "M01MDT251J4HAIPGO",
		  "M01MDT251SEQV1ZTQ",
		  "M01MDT251I9LKLZQO",
		  "M01MDT251GJSRD5WB",
		  "M01MDT251IJK6YGKC",
		  "M01MDT251RPPQJPSU",
		  "M01MDT251HKZRFRHK",
		  "M01MDT251YHBYDBIJ",
		  "M01MDT251QF735ASV",
		  "M01MDT251ADAAL9YR",
		  "M01MDT2517ZFSOJX1",
		  "M01MDT251GFNTRLIR",
		  "M01MDT251XCZTWQSR",
		  "M01MDT251PAEWJDOZ",
		  "M01MDT2517144H04U",
		  "M01MDT251SMT24YH5",
		  "M01MDT251RMJO3JWB",
		  "M01MDT251KUNBWZFS",
		  "M01MDT251EL4TWHN2",
		  "M01MDT2512FGX4RQ0",
		  "M01MDT251LYW3MH4Y",
		  "M01MDT251BR7KK7GB",
		  "M01MDT251HQSKPYLG",
		  "M01MDT251AYOUC5T1",
		  "M01MDT251K9ZDGKMX",
		  "M01MDT251S5TPYCDM",
		  "M01MDT251YGZYYYKG",
		  "M01MDT251ZDQNKKBY",
		  "M01MDT251UQM8SMYX",
		  "M01MDT251PYF0XMF1",
		  "M01MDT251GMGWSBCL",
		  "M01MDT2510LURXAGH",
		  "M01MDT251YANXP8LM",
		  "M01MDT2515DBRITBN",
		  "M01MDT251GPODFKXX",
		  "M01MDT251O6RC7CWD",
		  "M01MDT2515WC29K46",
		  "M01MDT2512AZUTZZD",
		  "M01MDT251HPIY3VWQ",
		  "M01MDT251TWB2TX2T",
		  "M01MDT251GUYT0EUM",
		  "M01MDT251FNYTXWY3",
		  "M01MDT251O8KJSK3H",
		  "M01MDT251OWSXEE62",
		  "M01MDT251VMWWU5T6",
		  "M01MDT251N3GDEUUX",
		  "M01MDT251DVTSK7MV",
		  "M01MDT251YS7IWZK3",
		  "M01MDT251WJ3GVCYK",
		  "M01MDT251PSS5DDHH",
		  "M01MDT251FP8MFOGF",
		  "M01MDT251YM09AWML",
		  "M01MDT251SMJPHGIH",
		  "M01MDT251MSMV81UY",
		  "M01MDT251U3GTSTAL",
		  "M01MDT2518GZXT9CQ",
		  "M01MDT251ZXWKYNOT",
		  "M01MDT251QVQQBPPC",
		  "M01MDT2516CILITNT",
		  "M01MDT251ACQQCS69",
		  "M01MDT251U9XPBA1N",
		  "M01MDT251WWE7ACWD"
		  ));

}
