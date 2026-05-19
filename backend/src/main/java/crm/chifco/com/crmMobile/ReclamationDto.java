package crm.chifco.com.crmMobile;

import java.util.Date;
import java.util.List;
import crm.chifco.com.model.Reclamation;

public class ReclamationDto {
  private Long reclamationId;
  private String refReclamation;
  private String description;
  private String status;
  private String serviceType;
  private String autre;
  private Date createdDate;
  private Date modifiedDate;
  private UserDtoApp createdBy;
  private UserDtoApp editedBy;
  private Long clientId;
  private String clientName;
  private UserDtoApp user;
  private String category;
  private String motif;
  private String referenceTt;
  private List<String> justificatifs;

  public Long getReclamationId() {
    return reclamationId;
  }

  public void setReclamationId(Long reclamationId) {
    this.reclamationId = reclamationId;
  }

  public String getRefReclamation() {
    return refReclamation;
  }

  public void setRefReclamation(String refReclamation) {
    this.refReclamation = refReclamation;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  public String getAutre() {
    return autre;
  }

  public void setAutre(String autre) {
    this.autre = autre;
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

  public UserDtoApp getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(UserDtoApp createdBy) {
    this.createdBy = createdBy;
  }

  public UserDtoApp getEditedBy() {
    return editedBy;
  }

  public void setEditedBy(UserDtoApp editedBy) {
    this.editedBy = editedBy;
  }

  public Long getClientId() {
    return clientId;
  }

  public void setClientId(Long clientId) {
    this.clientId = clientId;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public UserDtoApp getUser() {
    return user;
  }

  public void setUser(UserDtoApp user) {
    this.user = user;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getMotif() {
    return motif;
  }

  public void setMotif(String motif) {
    this.motif = motif;
  }

  public String getReferenceTt() {
    return referenceTt;
  }

  public void setReferenceTt(String referenceTt) {
    this.referenceTt = referenceTt;
  }

  public List<String> getJustificatifs() {
    return justificatifs;
  }

  public void setJustificatifs(List<String> justificatifs) {
    this.justificatifs = justificatifs;
  }

  public static ReclamationDto fromEntity(Reclamation reclamation) {
    if (reclamation == null)
      return null;

    ReclamationDto dto = new ReclamationDto();
    dto.setReclamationId(reclamation.getReclamationid());
    dto.setRefReclamation(reclamation.getRef_reclamation());
    dto.setDescription(reclamation.getDescription());
    dto.setStatus(
        reclamation.getStatus() != null ? reclamation.getStatus().getDesignation() : null);
    dto.setServiceType(
        reclamation.getServiceType() != null ? reclamation.getServiceType().getCategorytype()
            : null);
    dto.setAutre(reclamation.getAutre());
    dto.setCreatedDate(reclamation.getCreatedDate());
    dto.setModifiedDate(reclamation.getModifiedDate());
    dto.setCreatedBy(UserDtoApp.fromEntity(reclamation.getCreatedby()));
    dto.setEditedBy(UserDtoApp.fromEntity(reclamation.getEditedby()));
    dto.setClientId(reclamation.getClient() != null ? reclamation.getClient().getClientid() : null);
    dto.setClientName(reclamation.getClient() != null
        ? reclamation.getClient().getFirstName() + " " + reclamation.getClient().getLastName()
        : null);
    dto.setUser(UserDtoApp.fromEntity(reclamation.getUser()));
    dto.setCategory(reclamation.getCategory());
    dto.setMotif(reclamation.getMotif() != null ? reclamation.getMotif().getNomMotif() : null);
    dto.setReferenceTt(reclamation.getReferencett());
    dto.setJustificatifs(reclamation.getJustificatifs());
    return dto;
  }

}
