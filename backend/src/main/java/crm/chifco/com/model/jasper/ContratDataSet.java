package crm.chifco.com.model.jasper;

import java.util.Collection;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.EntryAbonnement;

public class ContratDataSet {

  private Collection<Abonnement> abonnement;
  private Collection<EntryAbonnement> entryAbonnement;

  public Collection<Abonnement> getAbonnement() {
    return abonnement;
  }

  public void setAbonnement(Collection<Abonnement> abonnement) {
    this.abonnement = abonnement;
  }

  public Collection<EntryAbonnement> getEntryAbonnement() {
    return entryAbonnement;
  }

  public void setEntryAbonnement(Collection<EntryAbonnement> entryAbonnement) {
    this.entryAbonnement = entryAbonnement;
  }



}
