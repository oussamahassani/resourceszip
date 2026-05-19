package crm.chifco.com.utils;

public class ClassificationRevendeur {
  public static final String Sactiver = "activer";
  public static final String Sdesactiver = "desactiver";
  public static final String Senrecouvrement = "recouvrement";
  public static final String suspendu = "Suspendu";
  public static final String precontentieux = "precontentieux";
  
  public static boolean isValid(String value) {
      return Sactiver.equalsIgnoreCase(value)
          || Sdesactiver.equalsIgnoreCase(value)
          || Senrecouvrement.equalsIgnoreCase(value)
          || suspendu.equalsIgnoreCase(value)
          || precontentieux.equalsIgnoreCase(value);
  }
}
