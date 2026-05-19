package crm.chifco.com.templateclasse;

import java.util.Date;
import crm.chifco.com.utils.CrmUtils;

public class ContratApiDTO {

  private String ref_contrat;
  private Date date_contrat;
  private String etat;
  private Boolean haveContrat;
  private String nameOffre;

  public ContratApiDTO(String ref_contrat, Date date_contrat, String etat, Boolean haveContrat,
      String nameOffre) {
    super();
    this.ref_contrat = ref_contrat;
    this.date_contrat = date_contrat;
    this.etat = etat;
    this.haveContrat = haveContrat;
    this.nameOffre = nameOffre;
  }

  public String getRef_contrat() {
    return ref_contrat;
  }

  public void setRef_contrat(String ref_contrat) {
    this.ref_contrat = ref_contrat;
  }

  public String getDate_contrat() {
    String formatedDate = CrmUtils.formatedDate(date_contrat);
    return formatedDate;
  }

  public void setDate_contrat(Date date_contrat) {
    this.date_contrat = date_contrat;
  }

  public String getEtat() {
    return etat;
  }

  public void setEtat(String etat) {
    this.etat = etat;
  }

  public Boolean getHaveContrat() {
    return haveContrat;
  }

  public void setHaveContrat(Boolean haveContrat) {
    this.haveContrat = haveContrat;
  }

  public String getNameOffre() {
    return nameOffre;
  }

  public void setNameOffre(String nameOffre) {
    this.nameOffre = nameOffre;
  }

}
