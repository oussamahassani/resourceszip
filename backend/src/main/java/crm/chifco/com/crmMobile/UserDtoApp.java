package crm.chifco.com.crmMobile;

import crm.chifco.com.model.User;

public class UserDtoApp {
  private Long userid;
  private String firstName;
  private String lastName;
  private String email;
  private String photo;
  private String telephone;
  private String codeUser;
  private String typeuser;
  private String roleName;
  private String identificationFiscale;
  private String nomCommercial;

  public String getNomCommercial() {
    return nomCommercial;
  }

  public void setNomCommercial(String nomCommercial) {
    this.nomCommercial = nomCommercial;
  }

  public String getIdentificationFiscale() {
    return identificationFiscale;
  }

  public void setIdentificationFiscale(String identificationFiscale) {
    this.identificationFiscale = identificationFiscale;
  }

  public String getTypeuser() {
    return typeuser;
  }

  public void setTypeuser(String typeuser) {
    this.typeuser = typeuser;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public String getCodeUser() {
    return codeUser;
  }

  public void setCodeUser(String codeUser) {
    this.codeUser = codeUser;
  }

  public Long getUserid() {
    return userid;
  }

  public void setUserid(Long userid) {
    this.userid = userid;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public static class UserDtoAppBuilder {
    private Long userid;
    private String firstName;
    private String lastName;
    private String email;
    private String photo;
    private String telephone;
    private String codeUser;
    private String typeuser;
    private String roleName;
    private String identificationFiscale;
    private String nomCommercial;

    public UserDtoAppBuilder nomCommercial(String nomCommercial) {
      this.nomCommercial = nomCommercial;
      return this;
    }

    public UserDtoAppBuilder identificationFiscale(String identificationFiscale) {
      this.identificationFiscale = identificationFiscale;
      return this;
    }

    public UserDtoAppBuilder userid(Long userid) {
      this.userid = userid;
      return this;
    }

    public UserDtoAppBuilder codeUser(String codeUser) {
      this.codeUser = codeUser;
      return this;
    }

    public UserDtoAppBuilder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public UserDtoAppBuilder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public UserDtoAppBuilder email(String email) {
      this.email = email;
      return this;
    }

    public UserDtoAppBuilder telephone(String telephone) {
      this.telephone = telephone;
      return this;
    }

    public UserDtoAppBuilder photo(String photo) {
      this.photo = photo;
      return this;
    }

    public UserDtoAppBuilder typeuser(String typeuser) {
      this.typeuser = typeuser;
      return this;
    }

    public UserDtoAppBuilder roleName(String roleName) {
      this.roleName = roleName;
      return this;
    }

    public UserDtoApp build() {
      UserDtoApp userDto = new UserDtoApp();
      userDto.setUserid(userid);
      userDto.setFirstName(firstName);
      userDto.setLastName(lastName);
      userDto.setEmail(email);
      userDto.setPhoto(photo);
      userDto.setTelephone(telephone);
      userDto.setCodeUser(codeUser);
      userDto.setTypeuser(typeuser);
      userDto.setRoleName(roleName);
      userDto.setIdentificationFiscale(identificationFiscale);
      userDto.setNomCommercial(nomCommercial);
      return userDto;
    }


  }

  public static UserDtoAppBuilder builder() {
    return new UserDtoAppBuilder();
  }

  public static UserDtoApp fromEntity(User user) {
    if (user == null) {
      return null;
    }

    return UserDtoApp.builder().userid(user.getUserid()).firstName(user.getFirstName())
        .lastName(user.getLastName()).codeUser(user.getCodeUser()).email(user.getEmail())
        .photo(user.getPhoto()).telephone(user.getTelephone()).typeuser(user.getTypeUser())
        .roleName(user.getRole().getRoleName())
        .identificationFiscale(user.getIdentificationFiscale())
        .nomCommercial(user.getNomCommercial()).build();
  }
}
