package crm.chifco.com.ApiDTO;

import java.util.Date;
import crm.chifco.com.utils.CrmUtils;

public class ListeFacturePayeDTO {
  private String ref_facture;
  private Double total_ttc;
  private Date dateDeDebut;
  private Date echeance;
  private Date dateDeFin;
  private String typeFacture;
  private Boolean isFactureResilation;
  private Date datePaiement;

  // getters and setters
  public String getRef_facture() {
    return ref_facture;
  }

  public void setRef_facture(String ref_facture) {
    this.ref_facture = ref_facture;
  }

  public String getDateDeDebut() {
    if (dateDeDebut != null) {
      String changeFormatdateDeDebut = CrmUtils.SmsFormatDateEchance(dateDeDebut);
      return changeFormatdateDeDebut;
    } else
      return "-------";
  }

  public void setDateDeDebut(Date dateDeDebut) {
    this.dateDeDebut = dateDeDebut;
  }

  public String getDateDeFin() {
    if (dateDeFin != null) {
      String changeFormatdateDeFin = CrmUtils.SmsFormatDateEchance(dateDeFin);
      return changeFormatdateDeFin;
    }
    return "-----------";
  }

  public void setDateDeFin(Date dateDeFin) {

    this.dateDeFin = dateDeFin;
  }

  public Double getTotal_ttc() {
    return total_ttc;
  }

  public void setTotal_ttc(Double total_ttc) {
    this.total_ttc = total_ttc;
  }

  public String getEcheance() {
    if (echeance != null) {
      String changeFormat = CrmUtils.SmsFormatDateEchance(echeance);

      return changeFormat;
    }
    return "------------";
  }

  public void setEcheance(Date echeance) {
    this.echeance = echeance;
  }



  public Date getDatePaiement() {
    return datePaiement;
  }

  public void setDatePaiement(Date datePaiement) {
    this.datePaiement = datePaiement;
  }

  public String getTypeFacture() {
    return typeFacture;
  }

  public Boolean getIsFactureResilation() {
    return isFactureResilation;
  }

  public void setIsFactureResilation(Boolean isFactureResilation) {
    this.isFactureResilation = isFactureResilation;
  }

  public void setTypeFacture(String typeFacture) {
    this.typeFacture = typeFacture;
  }

}
