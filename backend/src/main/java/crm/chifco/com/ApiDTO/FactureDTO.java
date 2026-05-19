package crm.chifco.com.ApiDTO;

import java.util.Date;
import crm.chifco.com.utils.CrmUtils;

public class FactureDTO {

  private String ref_facture;
  private Date dateDeDebut;
  private Date dateDeFin;
  private Boolean etat_facture;
  private Double montant_payer;
  private Date date_echeance;
  private String typeFacture;

  public static FactureDTO createWithRefMontant(String ref_facture, Double montant_payer) {
    return new FactureDTO(ref_facture, montant_payer);
  }

  public FactureDTO(String ref_facture, Double montant_payer) {
    this.ref_facture = ref_facture;
    this.montant_payer = montant_payer;
  }

  public FactureDTO() {
    super();
    // TODO Auto-generated constructor stub
  }

  public FactureDTO(String ref_facture, Date dateDeDebut, Date dateDeFin, Boolean etat_facture,
      Double montant_payer, Date date_echeance) {
    super();
    this.ref_facture = ref_facture;
    this.dateDeDebut = dateDeDebut;
    this.dateDeFin = dateDeFin;
    this.etat_facture = etat_facture;
    this.montant_payer = montant_payer;
    this.date_echeance = date_echeance;
  }

  public String getRef_facture() {
    return ref_facture;
  }

  public void setRef_facture(String ref_facture) {
    this.ref_facture = ref_facture;
  }

  public String getDateDeDebut() {
    if (dateDeDebut != null) {

      return CrmUtils.formatedDate(dateDeDebut);
    }
    return "----------";
  }

  public void setDateDeDebut(Date dateDeDebut) {
    this.dateDeDebut = dateDeDebut;
  }

  public String getDateDeFin() {
    if (dateDeFin != null) {

      return CrmUtils.formatedDate(dateDeFin);
    }
    return "---------";
  }

  public void setDateDeFin(Date dateDeFin) {
    this.dateDeFin = dateDeFin;
  }

  public Boolean getEtat_facture() {
    return etat_facture;
  }

  public void setEtat_facture(Boolean etat_facture) {
    this.etat_facture = etat_facture;
  }

  public String getMontant_payer() {

    return CrmUtils.formatedDbouble(montant_payer);
  }

  public void setMontant_payer(Double montant_payer) {
    this.montant_payer = montant_payer;
  }

  public String getDate_echeance() {
    if (date_echeance != null) {

      return CrmUtils.formatedDate(date_echeance);
    }
    return "---------";
  }

  public void setDate_echeance(Date date_echeance) {
    this.date_echeance = date_echeance;
  }

  public String getTypeFacture() {
    return typeFacture;
  }

  public void setTypeFacture(String typeFacture) {
    this.typeFacture = typeFacture;
  }

}
