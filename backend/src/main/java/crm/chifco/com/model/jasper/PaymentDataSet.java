/*
 * created by hatem ghozzi on 24 11 2022
 */

package crm.chifco.com.model.jasper;

import java.util.Collection;
import crm.chifco.com.model.Abonnement;
import crm.chifco.com.model.AvoirClient;
import crm.chifco.com.model.Encaissement;
import crm.chifco.com.model.Facture;
import crm.chifco.com.model.Payement;
import crm.chifco.com.model.User;

public class PaymentDataSet {

  private Collection<Facture> factures;
  private Collection<User> users;
  private Collection<Facture> facturesDatails;
  private Collection<Encaissement> encaissements;
  private Collection<Payement> payements;
  private Collection<AvoirClient> avoirClient;
  private Collection<Abonnement> abonnements;

  public Collection<Facture> getFactures() {
    return factures;
  }

  public void setFactures(Collection<Facture> factures) {
    // Collection<Facture> facturesDatails = new ArrayList<>();
    // facturesDatails.add(factures.iterator().next());
    // this.setFacturesDatails(facturesDatails);
    this.facturesDatails = factures;
    this.factures = factures;
  }

  public Collection<Encaissement> getEncaissements() {
    return encaissements;
  }

  public void setEncaissements(Collection<Encaissement> encaissements) {
    this.encaissements = encaissements;
  }

  public Collection<Payement> getPayements() {
    return payements;
  }

  public void setPayements(Collection<Payement> payements) {
    this.payements = payements;
  }

  public Collection<Facture> getFacturesDatails() {
    return facturesDatails;
  }

  public void setFacturesDatails(Collection<Facture> facturesDatails) {
    this.facturesDatails = facturesDatails;
  }

  public Collection<User> getUsers() {
    return users;
  }

  public void setUsers(Collection<User> users) {
    this.users = users;
  }

  public Collection<Abonnement> getAbonnements() {
    return abonnements;
  }

  public void setAbonnements(Collection<Abonnement> abonnements) {
    this.abonnements = abonnements;
  }

  public Collection<AvoirClient> getAvoirClient() {
    return avoirClient;
  }

  public void setAvoirClient(Collection<AvoirClient> avoirClient) {
    this.avoirClient = avoirClient;
  }

}
