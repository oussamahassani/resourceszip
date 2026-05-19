package crm.chifco.com.templateclasse;

import java.util.Date;

public interface ListeBordereau {

  String getBordereau_id();

  Double getMontant();

  Long getNumfacure();

  String getStatus();

  String getTelephone();

  String getAdresse();

  String getFirst_name();

  String getLast_name();

  String getConfirmedByfirstName();

  String getConfirmedByLastName();

  String getReference_bordereau();

  String getCommentaire();

  Date getCreated_date();

  String getTypeDePayement();

}
