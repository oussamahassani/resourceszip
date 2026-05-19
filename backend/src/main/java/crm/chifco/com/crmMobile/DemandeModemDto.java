package crm.chifco.com.crmMobile;


import java.util.Date;

public class DemandeModemDto {
  private Long idDemandeModem;
  private Date createdDate;
  private Date modifiedDate;
  private String quantiter;
  private String typeModem;
  private UserDtoApp user;

  public Long getIdDemandeModem() {
    return idDemandeModem;
  }

  public void setIdDemandeModem(Long idDemandeModem) {
    this.idDemandeModem = idDemandeModem;
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

  public String getQuantiter() {
    return quantiter;
  }

  public void setQuantiter(String quantiter) {
    this.quantiter = quantiter;
  }

  public String getTypeModem() {
    return typeModem;
  }

  public void setTypeModem(String typeModem) {
    this.typeModem = typeModem;
  }

  public UserDtoApp getUser() {
    return user;
  }

  public void setUser(UserDtoApp user) {
    this.user = user;
  }

  public static DemandeModemDto fromEntity(crm.chifco.com.model.DemandeModem entity) {
    if (entity == null)
      return null;

    DemandeModemDto dto = new DemandeModemDto();
    dto.setIdDemandeModem(entity.getIdDemandeModem());
    dto.setCreatedDate(entity.getCreatedDate());
    dto.setModifiedDate(entity.getModifiedDate());
    dto.setQuantiter(entity.getQuantiter());
    dto.setTypeModem(entity.getTypeModem());
    dto.setUser(UserDtoApp.fromEntity(entity.getUser()));
    return dto;
  }
}
