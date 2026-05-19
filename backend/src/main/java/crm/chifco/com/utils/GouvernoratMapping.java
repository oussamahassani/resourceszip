package crm.chifco.com.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GouvernoratMapping {

  // Constants for all Tunisian Gouvernorats
  public static final String MONASTIR = "Monastir";
  public static final String TUNIS = "Tunis";
  public static final String MAHDIA = "Mahdia";
  public static final String SFAX = "Sfax";
  public static final String GABES = "Gabes";
  public static final String MEDNINE = "Mednine";
  public static final String TATAOUINE = "Tataouine";
  public static final String KEBILI = "Kebili";
  public static final String GAFSA = "Gafsa";
  public static final String TOZEUR = "Tozeur";
  public static final String ARIANA = "Ariana";
  public static final String BEN_AROUS = "Ben Arous";
  public static final String MANOUBA = "Manouba";
  public static final String BEJA = "Beja";
  public static final String JENDOUBA = "Jandouba";
  public static final String EL_KEF = "El Kef";
  public static final String SIDI_BOUZID = "Sidi Bouzid";
  public static final String KASSERINE = "Kasserine";
  public static final String SELIANA = "Seliana";
  public static final String BIZERTE = "Bizerte";
  public static final String KAIROUAN = "Kairouan";
  public static final String NABEUL = "Nabeul";
  public static final String ZAGHOUAN = "Zaghouan";
  public static final String SOUSSE = "Sousse";

  public static final Map<String, String> dbGouvernoratMapping;

  static {
    Map<String, String> aMap = new HashMap<>();

    aMap.put("MONASTIR", MONASTIR);
    aMap.put("TUNIS", TUNIS);
    aMap.put("MAHDIA", MAHDIA);
    aMap.put("SFAX", SFAX);
    aMap.put("GABES", GABES);
    aMap.put("MEDENINE", MEDNINE);
    aMap.put("MEDNINE", MEDNINE);
    aMap.put("TATAOUINE", TATAOUINE);
    aMap.put("KEBILI", KEBILI);
    aMap.put("GAFSA", GAFSA);
    aMap.put("TOZEUR", TOZEUR);
    aMap.put("ARIANA", ARIANA);
    aMap.put("BEN AROUS", BEN_AROUS);
    aMap.put("BENAROUS", BEN_AROUS);
    aMap.put("MANOUBA", MANOUBA);
    aMap.put("BEJA", BEJA);
    aMap.put("JENDOUBA", JENDOUBA);
    aMap.put("JANDOUBA", JENDOUBA);
    aMap.put("EL KEF", EL_KEF);
    aMap.put("ELKEF", EL_KEF);
    aMap.put("KEF", EL_KEF);
    aMap.put("SIDI BOUZID", SIDI_BOUZID);
    aMap.put("SIDIBOUZID", SIDI_BOUZID);
    aMap.put("KASSERINE", KASSERINE);
    aMap.put("SELIANA", SELIANA);
    aMap.put("BIZERTE", BIZERTE);
    aMap.put("KAIROUAN", KAIROUAN);
    aMap.put("NABEUL", NABEUL);
    aMap.put("ZAGHOUAN", ZAGHOUAN);
    aMap.put("SOUSSE", SOUSSE);

    aMap.put("monastir", MONASTIR);
    aMap.put("tunis", TUNIS);
    aMap.put("mahdia", MAHDIA);
    aMap.put("sfax", SFAX);
    aMap.put("gabes", GABES);
    aMap.put("mednine", MEDNINE);
    aMap.put("tataouine", TATAOUINE);
    aMap.put("kebili", KEBILI);
    aMap.put("gafsa", GAFSA);
    aMap.put("tozeur", TOZEUR);
    aMap.put("ariana", ARIANA);
    aMap.put("ben arous", BEN_AROUS);
    aMap.put("manouba", MANOUBA);
    aMap.put("beja", BEJA);
    aMap.put("jendouba", JENDOUBA);
    aMap.put("el kef", EL_KEF);
    aMap.put("sidi bouzid", SIDI_BOUZID);
    aMap.put("kasserine", KASSERINE);
    aMap.put("seliana", SELIANA);
    aMap.put("bizerte", BIZERTE);
    aMap.put("kairouan", KAIROUAN);
    aMap.put("nabeul", NABEUL);
    aMap.put("zaghouan", ZAGHOUAN);
    aMap.put("sousse", SOUSSE);

    dbGouvernoratMapping = Collections.unmodifiableMap(aMap);
  }

  public static String getDatabaseGouvernorat(String inputName) {
    if (inputName == null)
      return null;
    String trimmedName = inputName.trim();

    String mappedName = dbGouvernoratMapping.get(trimmedName);
    if (mappedName != null) {
      return mappedName;
    }

    mappedName = dbGouvernoratMapping.get(trimmedName.toUpperCase());
    if (mappedName != null) {
      return mappedName;
    }

    return trimmedName;
  }

  public static boolean isValidGouvernorat(String inputName) {
    return getDatabaseGouvernorat(inputName) != null;
  }

  public static java.util.List<String> getAllDatabaseGouvernorats() {
    return java.util.Arrays.asList(MONASTIR, TUNIS, MAHDIA, SFAX, GABES, MEDNINE, TATAOUINE, KEBILI,
        GAFSA, TOZEUR, ARIANA, BEN_AROUS, MANOUBA, BEJA, JENDOUBA, EL_KEF, SIDI_BOUZID, KASSERINE,
        SELIANA, BIZERTE, KAIROUAN, NABEUL, ZAGHOUAN, SOUSSE);
  }
}
