/*
 * created by hatem ghozzi on 15 11 2022
 */

package crm.chifco.com.model.jasper;

import crm.chifco.com.model.EntryFactures;
import crm.chifco.com.model.EntryTvaFacture;
import crm.chifco.com.model.Facture;

import java.util.Collection;

public class FactureDataSet {
  private Collection<EntryFactures> entryFactures;
  private Collection<EntryTvaFacture> entryTvaFactures;

  private Collection<Facture> factures;


  public Collection<Facture> getFactures() {
    return factures;
  }

  public void setFactures(Collection<Facture> factures) {
    this.factures = factures;
  }

  public Collection<EntryFactures> getEntriesfactures() {
    return entryFactures;
  }

  public void setEntriesfactures(Collection<EntryFactures> entryFactures) {
    this.entryFactures = entryFactures;
  }

  public Collection<EntryTvaFacture> getEntryTvaFactures() {
    return entryTvaFactures;
  }

  public void setEntryTvaFactures(Collection<EntryTvaFacture> entryTvaFactures) {
    this.entryTvaFactures = entryTvaFactures;
  }
}
