package crm.chifco.com.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DBEtatTT {

  public static final String Etude = "Etude";
  public static final String Mise_en_service = "Mise en service TT";
  public static final String ConfirmationClient = "Confirmation Client";
  public static final String ConstructionLigne = "Construction Ligne";
  public static final String ConfirmationOK = "Confirmation Client OK";

  public static final String ConfirmationAnnuler = "Confirmation Client Annuler";
  public static final String Clôturée = "Clôturée";
  public static final String Enregister = "Enregistrée";
  public static final String Instance = "Instance";
  public static final String Reservation = "Reservation";
  public static final String Refused = "Annulée";
  public static final String Cancled = "Annulée par client";
  public static final String Resilation = "Résiliation";
  public static final Map<String, String> dbEtatTT;

  public static final String Execution = "Exécution";
  public static final String Attente_Construction = "Attente Construction";
  public static final String Instance_Commercial = "Instance Commercial";
  public static final String Migration = "Migration";
  public static final String Raccordement = "Raccordement";
  public static final String Affectation_Modem = "Affectation de modem";
  public static final String Rejected = "Rejetée";

  public static final String ActivationService = "Activation de service";

  static {
    Map<String, String> aMap = new HashMap<>();
    aMap.put("Enregister", "Enregistrée");
    aMap.put("Etude", "Etude");
    aMap.put("ConfirmationClient", "Confirmation Client");
    aMap.put("ConfirmationOK", "Confirmation Client OK");
    aMap.put("ConfirmationAnnuler", "Confirmation Client Annuler");
    aMap.put("ConstructionLigne", "Construction Ligne");
    dbEtatTT = Collections.unmodifiableMap(aMap);
  }
}
