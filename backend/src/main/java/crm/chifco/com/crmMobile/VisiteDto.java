package crm.chifco.com.crmMobile;

import java.util.Date;
import crm.chifco.com.model.Visite;


public class VisiteDto {
  private Long id;
  private String typeVisite;
  private String revendeur;
  private int dureeVisiteHeures;
  private int dureeVisiteMinutes;
  private String commentaire;
  private Date createdDate;
  private Date modifiedDate;
  private UserDtoApp createdBy;
  private UserDtoApp editedBy;
  private Long revndeurId;
  private String status;
  private Double longitude;
  private Double latitude;
  private String reference_visite;
  private String autreType;
  private String autreLieu;

  public String getAutreType() {
    return autreType;
  }

  public void setAutreType(String autreType) {
    this.autreType = autreType;
  }

  public String getAutreLieu() {
    return autreLieu;
  }

  public void setAutreLieu(String autreLieu) {
    this.autreLieu = autreLieu;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public String getReference_visite() {
    return reference_visite;
  }

  public void setReference_visite(String reference_visite) {
    this.reference_visite = reference_visite;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTypeVisite() {
    return typeVisite;
  }

  public void setTypeVisite(String typeVisite) {
    this.typeVisite = typeVisite;
  }

  public String getRevendeur() {
    return revendeur;
  }

  public void setRevendeur(String revendeur) {
    this.revendeur = revendeur;
  }

  public int getDureeVisiteHeures() {
    return dureeVisiteHeures;
  }

  public void setDureeVisiteHeures(int dureeVisiteHeures) {
    this.dureeVisiteHeures = dureeVisiteHeures;
  }

  public int getDureeVisiteMinutes() {
    return dureeVisiteMinutes;
  }

  public void setDureeVisiteMinutes(int dureeVisiteMinutes) {
    this.dureeVisiteMinutes = dureeVisiteMinutes;
  }

  public String getCommentaire() {
    return commentaire;
  }

  public void setCommentaire(String commentaire) {
    this.commentaire = commentaire;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public Long getRevndeurId() {
    return revndeurId;
  }

  public void setRevndeurId(Long revndeurId) {
    this.revndeurId = revndeurId;
  }

  public static class VisiteDtoBuilder {
    private Long id;
    private String typeVisite;
    private String revendeur;
    private int dureeVisiteHeures;
    private int dureeVisiteMinutes;
    private String commentaire;
    private Date createdDate;
    private Date modifiedDate;
    private UserDtoApp createdBy;
    private UserDtoApp editedBy;
    private Long revndeurId;
    private String status;
    private Double longitude;
    private Double latitude;
    private String reference_visite;
    private String autreType;
    private String autreLieu;

    public VisiteDtoBuilder autreType(String autreType) {
      this.autreType = autreType;
      return this;
    }

    public VisiteDtoBuilder autreLieu(String autreLieu) {
      this.autreLieu = autreLieu;
      return this;
    }

    public VisiteDtoBuilder longitude(Double longitude) {
      this.longitude = longitude;
      return this;
    }

    public VisiteDtoBuilder latitude(Double latitude) {
      this.latitude = latitude;
      return this;
    }

    public VisiteDtoBuilder reference_visite(String reference_visite) {
      this.reference_visite = reference_visite;
      return this;
    }

    public VisiteDtoBuilder status(String status) {
      this.status = status;
      return this;
    }

    public VisiteDtoBuilder typeVisite(String typeVisite) {
      this.typeVisite = typeVisite;
      return this;
    }

    public VisiteDtoBuilder revendeur(String revendeur) {
      this.revendeur = revendeur;
      return this;
    }

    public VisiteDtoBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public VisiteDtoBuilder dureeVisiteHeures(int dureeVisiteHeures) {
      this.dureeVisiteHeures = dureeVisiteHeures;
      return this;
    }

    public VisiteDtoBuilder dureeVisiteMinutes(int dureeVisiteMinutes) {
      this.dureeVisiteMinutes = dureeVisiteMinutes;
      return this;
    }

    public VisiteDtoBuilder commentaire(String commentaire) {
      this.commentaire = commentaire;
      return this;
    }

    public VisiteDtoBuilder createdDate(Date createdDate) {
      this.createdDate = createdDate;
      return this;
    }

    public VisiteDtoBuilder modifiedDate(Date modifiedDate) {
      this.modifiedDate = modifiedDate;
      return this;
    }

    public VisiteDtoBuilder createdBy(UserDtoApp createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public VisiteDtoBuilder editedBy(UserDtoApp editedBy) {
      this.editedBy = editedBy;
      return this;
    }

    public VisiteDtoBuilder revndeurId(Long revndeurId) {
      this.revndeurId = revndeurId;
      return this;
    }

    public VisiteDto build() {
      VisiteDto visiteDto = new VisiteDto();
      visiteDto.setId(id);
      visiteDto.setTypeVisite(typeVisite);
      visiteDto.setCommentaire(commentaire);
      visiteDto.setCreatedBy(createdBy);
      visiteDto.setCreatedDate(createdDate);
      visiteDto.setDureeVisiteHeures(dureeVisiteHeures);
      visiteDto.setDureeVisiteMinutes(dureeVisiteMinutes);
      visiteDto.setEditedBy(editedBy);
      visiteDto.setModifiedDate(modifiedDate);
      visiteDto.setRevendeur(revendeur);
      visiteDto.setRevndeurId(revndeurId);
      visiteDto.setStatus(status);
      visiteDto.setLongitude(longitude);
      visiteDto.setLatitude(latitude);
      visiteDto.setReference_visite(reference_visite);
      visiteDto.setAutreLieu(autreLieu);
      visiteDto.setAutreType(autreType);
      return visiteDto;
    }
  }

  public static VisiteDtoBuilder builder() {
    return new VisiteDtoBuilder();
  }

  public static VisiteDto fromEntity(Visite visite) {
    if (visite == null) {
      return null;
    }

    return VisiteDto.builder().id(visite.getId()).typeVisite(visite.getTypeVisite().getNomType())
        .commentaire(visite.getCommentaire()).createdDate(visite.getCreatedDate())
        .dureeVisiteHeures(visite.getDureeVisiteHeures())
        .dureeVisiteMinutes(visite.getDureeVisiteMinutes()).modifiedDate(visite.getModifiedDate())
        .revendeur(visite.getRevendeur()).revndeurId(visite.getRevndeurId())
        .createdBy(UserDtoApp.fromEntity(visite.getCreatedBy()))
        .reference_visite(visite.getReference_visite()).longitude(visite.getLongitude())
        .latitude(visite.getLatitude()).editedBy(UserDtoApp.fromEntity(visite.getEditedBy()))
        .autreLieu(visite.getAutreLieu()).autreType(visite.getAutreType())
        .status(visite.getStatus()).build();
  }
}
