package crm.chifco.com.DTOclass;

import crm.chifco.com.model.DemandeAbonnement;
import crm.chifco.com.model.User;

public class DemandeAbbonementaAndAffectedToUserObjectDataDTO {

  private DemandeAbonnement demandeAbonnement;
  private User affectedTo;
  private String createdByNom;
  private String createdByPrenom;
  private String createdByCode;

  public DemandeAbbonementaAndAffectedToUserObjectDataDTO(DemandeAbonnement demandeAbonnement,
      User affectedToId, String createdByNom, String createdByPrenom, String createdByCode) {

    this.demandeAbonnement = demandeAbonnement;
    this.affectedTo = affectedToId;
    this.createdByNom = createdByNom;
    this.createdByCode = createdByCode;
    this.createdByPrenom = createdByPrenom;
  }

  public DemandeAbonnement getDemandeAbonnement() {
    return demandeAbonnement;
  }

  public void setDemandeAbonnement(DemandeAbonnement demandeAbonnement) {
    this.demandeAbonnement = demandeAbonnement;
  }

  public User getAffectedTo() {
    return affectedTo;
  }

  public void setAffectedTo(User affectedTo) {
    this.affectedTo = affectedTo;
  }

  public String getCreatedByNom() {
    return createdByNom;
  }

  public void setCreatedByNom(String createdByNom) {
    this.createdByNom = createdByNom;
  }

  public String getCreatedByCode() {
    return createdByCode;
  }

  public void setCreatedByCode(String createdByCode) {
    this.createdByCode = createdByCode;
  }

  public String getCreatedByPrenom() {
    return createdByPrenom;
  }

  public void setCreatedByPrenom(String createdByPrenom) {
    this.createdByPrenom = createdByPrenom;
  }



}
