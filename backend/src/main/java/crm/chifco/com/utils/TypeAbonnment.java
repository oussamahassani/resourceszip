package crm.chifco.com.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TypeAbonnment {

	  public static final String Box = "5G";

	  public static final String Internet = "ABBONEMENT_INTERNET";
	  
	  public static final String SIM = "Sim.5G";

	  
	  public static final Map<String, String> dbStatutChifco;



	  static {
	    Map<String, String> aMap = new HashMap<>();
	    aMap.put("Box", "5G");
	    aMap.put("Internet", "ABBONEMENT_INTERNET");

	    dbStatutChifco = Collections.unmodifiableMap(aMap);
	  }
}
