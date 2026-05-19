package crm.chifco.com.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NomStatutChifco {

  public static final String DRAFT = "DRAFT";

  public static final String SIGNED_DOC = "SIGNED_DOC";
  public static final String WAIT_TT = "WAIT_TT";
  public static final String INSTALLED = "INSTALLED";
  public static final String ASSIGNED = "ASSIGNED";
  public static final String VALID = "VALID";
  public static final String ACTIVE = "ACTIVE";
  public static final String REFUSED = "REFUSED";
  public static final String CANCELED = "CANCELED";
  public static final String UNPAID = "UNPAID";
  public static final String SAISIE_INFAISABLE = "SAISIE_INFAISABLE";
  public static final String RESILIATION = "RESILIATION";
  public static final String RECOUVREMENT = "RECOUVREMENT";
  public static final String POROFORMA = "POROFORMA";
  public static final String CLIENT_INJOIGNABLE = "CLIENT_INJOIGNABLE";
  public static final Map<String, String> dbStatutChifco;



  static {
    Map<String, String> aMap = new HashMap<>();
    aMap.put("DRAFT", "DRAFT");
    aMap.put("SIGNED_DOC", "SIGNED_DOC");
    aMap.put("WAIT_TT", "Confirmation Client");
    aMap.put("INSTALLED", "INSTALLED");
    aMap.put("ASSIGNED", "ASSIGNED");
    aMap.put("VALID", "VALID");
    aMap.put("ACTIVE", "ACTIVE");
    aMap.put("REFUSED", "REFUSED");
    aMap.put("CANCELED", "CANCELED");
    aMap.put("UNPAID", "UNPAID");
    aMap.put("POROFORMA", "POROFORMA");
    aMap.put("RESILIATION", "RESILIATION");
    dbStatutChifco = Collections.unmodifiableMap(aMap);
  }
}
