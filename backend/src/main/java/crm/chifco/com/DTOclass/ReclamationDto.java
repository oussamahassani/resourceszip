package crm.chifco.com.DTOclass;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import crm.chifco.com.model.Reclamation;


public class ReclamationDto {
  private Long id_reclamation;
  private String ref_reclamation;
  private String category;
  private String description;
  private Map<String, String> servicetype;
  private Map<String, String> motif;
  private Map<String, Object> status;
  private Date date_add;
  private String source;
  private List<String> justificatifs;
  private UserDto user;
  private UserDto createdby;
  private UserDto treatedBy;
  private String email;
  private Long telfixe;
  private String identifiant;
  private String referencenety;

  public static ReclamationDto fromEntity(Reclamation reclamation) {
    ReclamationDto dto = new ReclamationDto();
    dto.setId_reclamation(reclamation.getReclamationid());
    dto.setRef_reclamation(reclamation.getRef_reclamation());
    dto.setDescription(reclamation.getDescription());
    dto.setDate_add(reclamation.getCreatedDate());
    dto.setSource(reclamation.getSource());
    dto.setCategory(reclamation.getCategory());
    dto.setJustificatifs(reclamation.getJustificatifs());
    if (reclamation.getClient() != null) {
      dto.setEmail(reclamation.getClient().getEmail());
      dto.setTelfixe(reclamation.getClient().getTelFixe());
      dto.setIdentifiant(reclamation.getClient().getCin());
      dto.setReferencenety(reclamation.getClient().getReferenceClient());
    }
    if (reclamation.getUser() != null) {
      dto.setUser(UserDto.fromEntity(reclamation.getUser()));
    }

    if (reclamation.getCreatedby() != null) {
      dto.setCreatedby(UserDto.fromEntity(reclamation.getCreatedby()));
    }

    if (reclamation.getTreatedBy() != null) {
      dto.setTreatedBy(UserDto.fromEntity(reclamation.getTreatedBy()));
    }

    if (reclamation.getServiceType() != null) {
      Map<String, String> serviceTypeMap = new HashMap<>();
      serviceTypeMap.put("servicetypeId",
          reclamation.getServiceType().getServicetypeId().toString());
      serviceTypeMap.put("categorytype", reclamation.getServiceType().getCategorytype());
      serviceTypeMap.put("categorytypear", reclamation.getServiceType().getCategorytypear());
      serviceTypeMap.put("categorytypeen", reclamation.getServiceType().getCategorytypeen());
      dto.setServicetype(serviceTypeMap);
    }

    if (reclamation.getMotif() != null) {
      Map<String, String> motifMap = new HashMap<>();
      motifMap.put("nomMotif", reclamation.getMotif().getNomMotif());
      motifMap.put("nomMotifar", reclamation.getMotif().getNomMotifar());
      motifMap.put("nomMotifen", reclamation.getMotif().getNomMotifen());
      dto.setMotif(motifMap);
    }

    if (reclamation.getStatus() != null) {
      Map<String, Object> statusMap = new HashMap<>();
      statusMap.put("nomStatut", reclamation.getStatus().getNomStatut());
      statusMap.put("nomStatutar", reclamation.getStatus().getNomStatutar()); // if exists
      statusMap.put("nomStatuten", reclamation.getStatus().getNomStatuten()); // if exists
      statusMap.put("couleur", reclamation.getStatus().getCouleur()); // if exists
      dto.setStatus(statusMap);
    }

    return dto;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Long getTelfixe() {
    return telfixe;
  }

  public void setTelfixe(Long telfixe) {
    this.telfixe = telfixe;
  }

  public String getIdentifiant() {
    return identifiant;
  }

  public void setIdentifiant(String identifiant) {
    this.identifiant = identifiant;
  }

  public String getReferencenety() {
    return referencenety;
  }

  public void setReferencenety(String referencenety) {
    this.referencenety = referencenety;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Long getId_reclamation() {
    return id_reclamation;
  }

  public void setId_reclamation(Long id_reclamation) {
    this.id_reclamation = id_reclamation;
  }

  public String getRef_reclamation() {
    return ref_reclamation;
  }

  public void setRef_reclamation(String ref_reclamation) {
    this.ref_reclamation = ref_reclamation;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, String> getServicetype() {
    return servicetype;
  }

  public void setServicetype(Map<String, String> servicetype) {
    this.servicetype = servicetype;
  }

  public Map<String, String> getMotif() {
    return motif;
  }

  public void setMotif(Map<String, String> motif) {
    this.motif = motif;
  }

  public Map<String, Object> getStatus() {
    return status;
  }

  public void setStatus(Map<String, Object> status) {
    this.status = status;
  }

  public Date getDate_add() {
    return date_add;
  }

  public void setDate_add(Date date_add) {
    this.date_add = date_add;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public List<String> getJustificatifs() {
    return justificatifs;
  }

  public void setJustificatifs(List<String> justificatifs) {
    this.justificatifs = justificatifs;
  }

  public UserDto getUser() {
    return user;
  }

  public void setUser(UserDto user) {
    this.user = user;
  }

  public UserDto getCreatedby() {
    return createdby;
  }

  public void setCreatedby(UserDto createdby) {
    this.createdby = createdby;
  }

  public UserDto getTreatedBy() {
    return treatedBy;
  }

  public void setTreatedBy(UserDto treatedBy) {
    this.treatedBy = treatedBy;
  }

}
