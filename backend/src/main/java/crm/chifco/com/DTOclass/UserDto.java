package crm.chifco.com.DTOclass;


import crm.chifco.com.model.User;

public class UserDto {
  private Long userid;
  private String firstName;
  private String lastName;
  private String email;
  private String photo;
  private String telephone;
  private String codeUser;

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

  public static class UserDtoBuilder {
    private Long userid;
    private String firstName;
    private String lastName;
    private String email;
    private String photo;
    private String telephone;
    private String codeUser;

    public UserDtoBuilder userid(Long userid) {
      this.userid = userid;
      return this;
    }

    public UserDtoBuilder codeUser(String codeUser) {
      this.codeUser = codeUser;
      return this;
    }

    public UserDtoBuilder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public UserDtoBuilder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public UserDtoBuilder email(String email) {
      this.email = email;
      return this;
    }

    public UserDtoBuilder photo(String photo) {
      this.photo = photo;
      return this;
    }

    public UserDtoBuilder telephone(String telephone) {
      this.telephone = telephone;
      return this;
    }

    public UserDto build() {
      UserDto userDto = new UserDto();
      userDto.setUserid(userid);
      userDto.setFirstName(firstName);
      userDto.setLastName(lastName);
      userDto.setEmail(email);
      userDto.setPhoto(photo);
      userDto.setTelephone(telephone);
      userDto.setCodeUser(codeUser);
      return userDto;
    }
  }

  public static UserDtoBuilder builder() {
    return new UserDtoBuilder();
  }

  public static UserDto fromEntity(User user) {
    if (user == null) {
      return null;
    }

    return UserDto.builder().userid(user.getUserid()).firstName(user.getFirstName())
        .lastName(user.getLastName()).codeUser(user.getCodeUser()).email(user.getEmail())
        .photo(user.getPhoto()).telephone(user.getTelephone()).build();
  }
}
