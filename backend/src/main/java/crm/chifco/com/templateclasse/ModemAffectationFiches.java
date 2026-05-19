package crm.chifco.com.templateclasse;

import java.util.Date;

public interface ModemAffectationFiches {

  Long getId_fiche();

  String getRef_fiche();

  String getAffectedToFirstName();

  String getAffectedToLastName();

  String getNom_commercial();

  String getAffectedToCode();

  String getAffectequantite();

  Date getCreatedDate();

  String getAffectedByFirstName();

  String getAffectedByLastName();

  String getAffectedByCode();

}
