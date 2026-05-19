package crm.chifco.com.DTOclass;

import java.util.Date;

public class AvoirDto {

  private String refAvoirClient;
  private String abonnementReferenceClient;
  private Date createdDate;
  private Double montantAvoir;
  private String motifAvoir;
  private String usedByFirstName;
  private String usedByLastName;
  private Boolean canRevendeurViewed;
  private Boolean isClientPayed;
  private Boolean hasBordereau;
  private String creeParFirstName;
  private String creeParLastName;
  private Long avoirId;
  private Date dateDePaiement;
  private Boolean isJestCo = false;
  private String commentaireAvoir ;
  private String facture; 
  
  public AvoirDto() {
    super();
  }
 
  public AvoirDto(String refAvoirClient, String abonnementReferenceClient, Date createdDate,
      Double montantAvoir, String motifAvoir, String usedByFirstName, String usedByLastName,
      Boolean canRevendeurViewed, Boolean isClientPayed, Boolean hasBordereau,
      String creeParFirstName, String creeParLastName, Long avoirId, Date dateDePaiement , Boolean is_jestCo , String commentaireAvoir , String facture) {
    super();
    this.refAvoirClient = refAvoirClient;
    this.abonnementReferenceClient = abonnementReferenceClient;
    this.createdDate = createdDate;
    this.montantAvoir = montantAvoir;
    this.motifAvoir = motifAvoir;
    this.usedByFirstName = usedByFirstName;
    this.usedByLastName = usedByLastName;
    this.canRevendeurViewed = canRevendeurViewed;
    this.isClientPayed = isClientPayed;
    this.hasBordereau = hasBordereau;
    this.creeParFirstName = creeParFirstName;
    this.creeParLastName = creeParLastName;
    this.avoirId = avoirId;
    this.dateDePaiement = dateDePaiement;
    this.isJestCo = is_jestCo;
    this.commentaireAvoir=commentaireAvoir;
    this.facture = facture;
  }

  public String getRefAvoirClient() {
    return refAvoirClient;
  }

  public void setRefAvoirClient(String refAvoirClient) {
    this.refAvoirClient = refAvoirClient;
  }

  public String getAbonnementReferenceClient() {
    return abonnementReferenceClient;
  }

  public void setAbonnementReferenceClient(String abonnementReferenceClient) {
    this.abonnementReferenceClient = abonnementReferenceClient;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Double getMontantAvoir() {
    return montantAvoir;
  }

  public void setMontantAvoir(Double montantAvoir) {
    this.montantAvoir = montantAvoir;
  }

  public String getMotifAvoir() {
    return motifAvoir;
  }

  public void setMotifAvoir(String motifAvoir) {
    this.motifAvoir = motifAvoir;
  }

  public String getUsedByFirstName() {
    return usedByFirstName;
  }

  public void setUsedByFirstName(String usedByFirstName) {
    this.usedByFirstName = usedByFirstName;
  }

  public String getUsedByLastName() {
    return usedByLastName;
  }

  public void setUsedByLastName(String usedByLastName) {
    this.usedByLastName = usedByLastName;
  }

  public Boolean getCanRevendeurViewed() {
    return canRevendeurViewed;
  }

  public void setCanRevendeurViewed(Boolean canRevendeurViewed) {
    this.canRevendeurViewed = canRevendeurViewed;
  }

  public Boolean getIsClientPayed() {
    return isClientPayed;
  }

  public void setIsClientPayed(Boolean isClientPayed) {
    this.isClientPayed = isClientPayed;
  }

  public Boolean getHasBordereau() {
    return hasBordereau;
  }

  public void setHasBordereau(Boolean hasBordereau) {
    this.hasBordereau = hasBordereau;
  }

  public String getCreeParFirstName() {
    return creeParFirstName;
  }

  public void setCreeParFirstName(String creeParFirstName) {
    this.creeParFirstName = creeParFirstName;
  }

  public String getCreeParLastName() {
    return creeParLastName;
  }

  public void setCreeParLastName(String creeParLastName) {
    this.creeParLastName = creeParLastName;
  }

  public Long getAvoirId() {
    return avoirId;
  }

  public void setAvoirId(Long avoirId) {
    this.avoirId = avoirId;
  }

  public Date getDateDePaiement() {
    return dateDePaiement;
  }

  public void setDateDePaiement(Date dateDePaiement) {
    this.dateDePaiement = dateDePaiement;
  }

public Boolean getIsJestCo() {
	return isJestCo;
}

public void setIsJestCo(Boolean isJestCo) {
	this.isJestCo = isJestCo;
}

public String getCommentaireAvoir() {
	return commentaireAvoir;
}

public void setCommentaireAvoir(String commentaireAvoir) {
	this.commentaireAvoir = commentaireAvoir;
}

public String getFacture() {
	return facture;
}

public void setFacture(String facture) {
	this.facture = facture;
}



}
