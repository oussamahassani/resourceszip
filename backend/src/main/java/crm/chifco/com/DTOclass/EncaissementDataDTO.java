package crm.chifco.com.DTOclass;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import crm.chifco.com.model.EntryFactures;
import crm.chifco.com.utils.CrmUtils;

public class EncaissementDataDTO {

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

  private Collection<EntryFactures> entriesFacture;



  public EncaissementDataDTO() {
    super();
    // TODO Auto-generated constructor stub
  }

  public EncaissementDataDTO(Long factureId, String ref_facture) {
    super();
    // TODO Auto-generated constructor stub
  }

  // list all facture
  public EncaissementDataDTO(Long factureId, String ref_facture, Date createdDate, Date date_echeance,
      String firstName, String lastName, Double montant_payer, Double montantHt, Double montantTva,
      Boolean etat_facture, String referenceClient, String clientFirstName, String clientLastName) {
    super();
    this.factureId = factureId;
    this.ref_facture = ref_facture;

    this.createdDate = createdDate;
    this.date_echeance = date_echeance;
    this.firstName = firstName;
    this.lastName = lastName;
    this.montant_payer = CrmUtils.formatDoubleInputToString(montant_payer);
    this.montantHt = CrmUtils.formatDoubleInputToString(montantHt);
    this.montantTva = CrmUtils.formatDoubleInputToString(montantTva);
    this.etat_facture = etat_facture;
    this.referenceClient = referenceClient;
    this.clientFirstName = clientFirstName;
    this.clientLastName = clientLastName;
  }

  // list all facture + date de payement
  public EncaissementDataDTO(Long factureId, String ref_facture, Date createdDate, Date date_echeance,
      String firstName, String lastName, Double montant_payer, Double montantHt, Double montantTva,
      Boolean etat_facture, String referenceClient, String clientFirstName, String clientLastName,
      Date dateDePayement) {
    super();
    this.factureId = factureId;
    this.ref_facture = ref_facture;

    this.createdDate = createdDate;
    this.date_echeance = date_echeance;
    this.firstName = firstName;
    this.lastName = lastName;
    this.montant_payer = CrmUtils.formatDoubleInputToString(montant_payer);
    this.montantHt = CrmUtils.formatDoubleInputToString(montantHt);
    this.montantTva = CrmUtils.formatDoubleInputToString(montantTva);
    this.etat_facture = etat_facture;
    this.referenceClient = referenceClient;
    this.clientFirstName = clientFirstName;
    this.clientLastName = clientLastName;
    this.dateDePayement = dateDePayement;
  }

  // numero fixe client
  public EncaissementDataDTO(Long factureId, String ref_facture, Double montant_payer, Double montantHt,
      Double montantTva, Double prixBaseTva, Double timbrefiscale, Boolean etat_facture,
      Boolean visibility, Boolean isDelete, Boolean isFirstFacture, Date createdDate,
      Date modifiedDate, Date date_echeance, String firstName, String lastName, String codeUser,
      String referenceClient, String clientLastName, String clientFirstName, Long numeroFixeClient,
      Date deteDeVersment, String DistribiteurfirstName, String DistribiteurlastName,
      String DistribiteurcodeUser) {
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

  }

  // extraction facture list with ids
  public EncaissementDataDTO(Long factureId, String ref_facture, Double montant_payer, Double montantHt,
      Double montantTva, Double prixBaseTva, Double timbrefiscale, Boolean etat_facture,
      Boolean visibility, Boolean isDelete, Boolean isFirstFacture, Date createdDate,
      Date modifiedDate, Date date_echeance, String firstName, String lastName, String codeUser,
      String referenceClient, String clientLastName, String clientFirstName, Long numeroFixeClient,
      Date deteDeVersment, String DistribiteurfirstName, String DistribiteurlastName,
      String DistribiteurcodeUser, List<EntryFactures> entriesFacture) {
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

  // date de payemet + without created date
  public EncaissementDataDTO(Long factureId, String ref_facture, Double montant_payer, Double montantHt,
      Double montantTva, Double prixBaseTva, Double timbrefiscale, Boolean etat_facture,
      Boolean visibility, Boolean isDelete, Boolean isFirstFacture, Date createdDate,
      Date modifiedDate, Date date_echeance, String firstName, String lastName, String codeUser,
      String referenceClient, String clientLastName, String clientFirstName, Date dateDePayement) {
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
    this.dateDePayement = dateDePayement;
  }

  public EncaissementDataDTO(Long factureId, String ref_facture, Double montant_payer, Double montantHt,
      Double montantTva, Double prixBaseTva, Double timbrefiscale, Boolean etat_facture,
      Boolean visibility, Boolean isDelete, Boolean isFirstFacture, Date createdDate,
      Date modifiedDate, Date date_echeance, String firstName, String lastName, String codeUser,
      String referenceClient, String clientLastName, String clientFirstName) {
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
  }


  public EncaissementDataDTO(Long factureId, String ref_facture, Double montant_payer, Double montantHt,
      Double montantTva, Double prixBaseTva, Double timbrefiscale, Boolean etat_facture,
      Boolean visibility, Boolean isDelete, Boolean isFirstFacture, Date createdDate,
      Date modifiedDate, Date date_echeance, String firstName, String lastName, String codeUser,
      String referenceClient, String clientLastName, String clientFirstName, Long numeroFixeClient,
      Date deteDeVersment) {
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

  public String getClientFirstName() {
    return clientFirstName;
  }

  public void setClientFirstName(String clientFirstName) {
    this.clientFirstName = clientFirstName;
  }

  public String getTimbrefiscale() {
    return timbrefiscale;
  }

  public void setTimbrefiscale(String timbrefiscale) {
    this.timbrefiscale = timbrefiscale;
  }

  public Long getNumeroFixeClient() {
    return numeroFixeClient;
  }

  public void setNumeroFixeClient(Long numeroFixeClient) {
    this.numeroFixeClient = numeroFixeClient;
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

  public void setEntriesFacture(Collection<EntryFactures> entriesFacture) {
    this.entriesFacture = entriesFacture;
  }

  public void setEntriesFacture(List<EntryFactures> entriesFacture) {
    this.entriesFacture = entriesFacture;
  }

  @Override
  public String toString() {
    return "FactureDataDTO [factureId=" + factureId + ", ref_facture=" + ref_facture
        + ", montant_payer=" + montant_payer + ", montantHt=" + montantHt + ", montantTva="
        + montantTva + ", prixBaseTva=" + prixBaseTva + ", etat_facture=" + etat_facture
        + ", visibility=" + visibility + ", isDelete=" + isDelete + ", isFirstFacture="
        + isFirstFacture + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate
        + ", date_echeance=" + date_echeance + ", firstName=" + firstName + ", lastName=" + lastName
        + ", codeUser=" + codeUser + ", referenceClient=" + referenceClient + ", clientLastName="
        + clientLastName + "]";
  }

}
