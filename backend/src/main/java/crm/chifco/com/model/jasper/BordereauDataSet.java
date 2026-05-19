/*
 * created by hatem ghozzi on 18 11 2022
 */

package crm.chifco.com.model.jasper;

import crm.chifco.com.model.Bordereau;
import crm.chifco.com.model.EntryBordereau;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BordereauDataSet {


  private Collection<Bordereau> bordereaus;
  private Collection<EntryBordereau> entryBordereaus;


  public void setBordereaus(Bordereau oldBordereau) {
    List<Bordereau> listebordereau = new ArrayList<>();
    listebordereau.add(oldBordereau);
    if (listebordereau.isEmpty() == false) {
      this.setBordereau(Collections.synchronizedList(listebordereau));
    }
    if (oldBordereau.getEntry().isEmpty() == false) {
      this.setEntryBordereau(Collections.synchronizedList(oldBordereau.getEntry()));
    }

  }

  public Collection<Bordereau> getBordereau() {
    return bordereaus;
  }

  public void setBordereau(Collection<Bordereau> bordereau) {
    this.bordereaus = bordereau;
  }

  public Collection<EntryBordereau> getEntryBordereaus() {
    return entryBordereaus;
  }

  public void setEntryBordereau(Collection<EntryBordereau> entryBordereau) {
    this.entryBordereaus = entryBordereau;
  }

  public Collection<Bordereau> getBordereaus() {
    return bordereaus;
  }


}
