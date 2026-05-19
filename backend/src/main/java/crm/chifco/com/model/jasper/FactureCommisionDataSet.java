/*
 * created by hatem ghozzi on 15 11 2022
 */

package crm.chifco.com.model.jasper;

import java.util.Collection;
import crm.chifco.com.model.Commission;
import crm.chifco.com.model.User;

public class FactureCommisionDataSet {


  private Collection<Commission> commission;
  private Collection<User> revendeur;

  public Collection<Commission> getCommission() {
    return commission;
  }

  public void setCommission(Collection<Commission> commission) {
    this.commission = commission;
  }

public Collection<User> getRevendeur() {
	return revendeur;
}

public void setRevendeur(Collection<User> revendeur) {
	this.revendeur = revendeur;
}



}
