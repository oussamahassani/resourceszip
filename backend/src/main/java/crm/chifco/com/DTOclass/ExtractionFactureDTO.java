package crm.chifco.com.DTOclass;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import crm.chifco.com.model.EntryFactures;
import crm.chifco.com.model.Facture;
import crm.chifco.com.utils.CrmUtils;

public class ExtractionFactureDTO {

  private Long factureId;

  private String ref_facture;

  private String montant_payer;

  private String montantHt;
  private String montantTva;
  private String prixBaseTva;
  private String timbrefiscale;
  private Boolean etat_facture = false;
  private Boolean visibility = false;
  private Boolean isDelete = false;
  private Boolean isFirstFacture = false;

  private Long numeroFixeClient;
  @CreatedDate
  private Date createdDate;

  @LastModifiedDate
  private Date modifiedDate;

  @Temporal(TemporalType.DATE)
  private Date date_echeance;

  private String firstName;

  private String lastName;

  private String codeUser;

  private String referenceClient;

  private String clientLastName;

  private String clientFirstName;

  private Date dateDePayement;

  private Date dateDeVersment;


  private String DistribiteurfirstName;

  private String DistribiteurlastName;

  private String DistribiteurcodeUser;

  Collection<EntryFactures> entriesFacture;



  public ExtractionFactureDTO(Long factureId, String ref_facture, Double montant_payer,
      Double montantHt, Double montantTva, Double prixBaseTva, Double timbrefiscale,
      Boolean etat_facture, Boolean visibility, Boolean isDelete, Boolean isFirstFacture,
      Date createdDate, Date modifiedDate, Date date_echeance, String firstName, String lastName,
      String codeUser, String referenceClient, String clientLastName, String clientFirstName,
      Long numeroFixeClient, Date deteDeVersment, String DistribiteurfirstName,
      String DistribiteurlastName, String DistribiteurcodeUser,
      Collection<EntryFactures> entriesFacture) {
    super();
    this.factureId = factureId;
    this.ref_facture = ref_facture;
    this.montant_payer = CrmUtils.formatDoubleInputToString(montant_payer);
    this.montantHt = CrmUtils.formatDoubleInputToString(montantHt);
    this.montantTva = CrmUtils.formatDoubleInputToString(montantTva);
    this.prixBaseTva = CrmUtils.formatDoubleInputToString(prixBaseTva);
    this.timbrefiscale = CrmUtils.formatDoubleInputToString(timbrefiscale);
    this.etat_facture = etat_facture;
    this.visibility = visibility;
    this.isDelete = isDelete;
    this.isFirstFacture = isFirstFacture;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.date_echeance = date_echeance;
    this.firstName = firstName;
    this.lastName = lastName;
    this.codeUser = codeUser;
    this.referenceClient = referenceClient;
    this.clientLastName = clientLastName;
    this.clientFirstName = clientFirstName;
    this.numeroFixeClient = numeroFixeClient;
    this.dateDeVersment = deteDeVersment;

    this.DistribiteurfirstName = DistribiteurfirstName;
    this.DistribiteurlastName = DistribiteurlastName;
    this.DistribiteurcodeUser = DistribiteurcodeUser;
    this.entriesFacture = entriesFacture;


  }

  public ExtractionFactureDTO(Facture facture, long factureId, String refFacture,
      double montantPayer, double montantHt, double montantTva, double prixBaseTva,
      double timbreFiscale, boolean etatFacture, boolean visibility, boolean isDelete,
      boolean isFirstFacture, Date createdDate, Date modifiedDate, Date dateEcheance,
      String userFirstName, String userLastName, String userCodeUser, String referenceClient,
      String abonnementFirstName, String abonnementLastName, Long abonnementTelFixe,
      Date dateDeVersement, String affectedToFirstName, String affectedToLastName,
      String affectedToCodeUser) {
    this.factureId = factureId;
    this.ref_facture = refFacture;
    this.montant_payer = CrmUtils.formatDoubleInputToString(montantPayer);
    this.montantHt = CrmUtils.formatDoubleInputToString(montantHt);
    this.montantTva = CrmUtils.formatDoubleInputToString(montantTva);
    this.prixBaseTva = CrmUtils.formatDoubleInputToString(prixBaseTva);
    this.timbrefiscale = CrmUtils.formatDoubleInputToString(timbreFiscale);
    this.etat_facture = etatFacture;
    this.visibility = visibility;
    this.isDelete = isDelete;
    this.isFirstFacture = isFirstFacture;
    this.createdDate = createdDate;
    this.modifiedDate = modifiedDate;
    this.date_echeance = dateEcheance;
    this.firstName = userFirstName;
    this.lastName = userLastName;
    this.codeUser = userCodeUser;
    this.referenceClient = referenceClient;
    this.clientLastName = abonnementFirstName;
    this.clientFirstName = abonnementLastName;
    this.numeroFixeClient = abonnementTelFixe;
    this.dateDeVersment = dateDeVersement;

    this.DistribiteurfirstName = affectedToFirstName;
    this.DistribiteurlastName = affectedToLastName;
    this.DistribiteurcodeUser = affectedToCodeUser;
    this.entriesFacture = facture.getEntriesFacture();
  }

  public Long getFactureId() {
    return factureId;
  }


  public void setFactureId(Long factureId) {
    this.factureId = factureId;
  }


  public String getRef_facture() {
    return ref_facture;
  }


  public void setRef_facture(String ref_facture) {
    this.ref_facture = ref_facture;
  }


  public String getMontant_payer() {
    return montant_payer;
  }


  public void setMontant_payer(String montant_payer) {
    this.montant_payer = montant_payer;
  }


  public String getMontantHt() {
    return montantHt;
  }


  public void setMontantHt(String montantHt) {
    this.montantHt = montantHt;
  }


  public String getMontantTva() {
    return montantTva;
  }


  public void setMontantTva(String montantTva) {
    this.montantTva = montantTva;
  }


  public String getPrixBaseTva() {
    return prixBaseTva;
  }


  public void setPrixBaseTva(String prixBaseTva) {
    this.prixBaseTva = prixBaseTva;
  }


  public String getTimbrefiscale() {
    return timbrefiscale;
  }


  public void setTimbrefiscale(String timbrefiscale) {
    this.timbrefiscale = timbrefiscale;
  }


  public Boolean getEtat_facture() {
    return etat_facture;
  }


  public void setEtat_facture(Boolean etat_facture) {
    this.etat_facture = etat_facture;
  }


  public Boolean getVisibility() {
    return visibility;
  }


  public void setVisibility(Boolean visibility) {
    this.visibility = visibility;
  }


  public Boolean getIsDelete() {
    return isDelete;
  }


  public void setIsDelete(Boolean isDelete) {
    this.isDelete = isDelete;
  }


  public Boolean getIsFirstFacture() {
    return isFirstFacture;
  }


  public void setIsFirstFacture(Boolean isFirstFacture) {
    this.isFirstFacture = isFirstFacture;
  }


  public Long getNumeroFixeClient() {
    return numeroFixeClient;
  }


  public void setNumeroFixeClient(Long numeroFixeClient) {
    this.numeroFixeClient = numeroFixeClient;
  }


  public Date getCreatedDate() {
    return createdDate;
  }


  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }


  public Date getModifiedDate() {
    return modifiedDate;
  }


  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  public Date getDate_echeance() {
    return date_echeance;
  }


  public void setDate_echeance(Date date_echeance) {
    this.date_echeance = date_echeance;
  }


  public String getFirstName() {
    return firstName;
  }


  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }


  public String getLastName() {
    return lastName;
  }


  public void setLastName(String lastName) {
    this.lastName = lastName;
  }


  public String getCodeUser() {
    return codeUser;
  }


  public void setCodeUser(String codeUser) {
    this.codeUser = codeUser;
  }


  public String getReferenceClient() {
    return referenceClient;
  }


  public void setReferenceClient(String referenceClient) {
    this.referenceClient = referenceClient;
  }


  public String getClientLastName() {
    return clientLastName;
  }


  public void setClientLastName(String clientLastName) {
    this.clientLastName = clientLastName;
  }


  public String getClientFirstName() {
    return clientFirstName;
  }


  public void setClientFirstName(String clientFirstName) {
    this.clientFirstName = clientFirstName;
  }


  public Date getDateDePayement() {
    return dateDePayement;
  }


  public void setDateDePayement(Date dateDePayement) {
    this.dateDePayement = dateDePayement;
  }


  public Date getDateDeVersment() {
    return dateDeVersment;
  }


  public void setDateDeVersment(Date dateDeVersment) {
    this.dateDeVersment = dateDeVersment;
  }


  public String getDistribiteurfirstName() {
    return DistribiteurfirstName;
  }


  public void setDistribiteurfirstName(String distribiteurfirstName) {
    DistribiteurfirstName = distribiteurfirstName;
  }


  public String getDistribiteurlastName() {
    return DistribiteurlastName;
  }


  public void setDistribiteurlastName(String distribiteurlastName) {
    DistribiteurlastName = distribiteurlastName;
  }


  public String getDistribiteurcodeUser() {
    return DistribiteurcodeUser;
  }


  public void setDistribiteurcodeUser(String distribiteurcodeUser) {
    DistribiteurcodeUser = distribiteurcodeUser;
  }


  public Collection<EntryFactures> getEntriesFacture() {
    return entriesFacture;
  }


  public void setEntriesFacture(List<EntryFactures> entriesFacture) {
    this.entriesFacture = entriesFacture;
  }



}
