package crm.chifco.com.crmMobile;

import crm.chifco.com.model.User;

public class UserProfileDto {
  private Long userid;
  private String firstName;
  private String lastName;
  private String email;
  private String imageFile;
  private String activitePrincipale;
  private String coordonneesBancaires;
  private String telephone;

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

  public String getImageFile() {
    return imageFile;
  }

  public void setImageFile(String imageFile) {
    this.imageFile = imageFile;
  }

  public String getActivitePrincipale() {
    return activitePrincipale;
  }

  public void setActivitePrincipale(String activitePrincipale) {
    this.activitePrincipale = activitePrincipale;
  }

  public String getCoordonneesBancaires() {
    return coordonneesBancaires;
  }

  public void setCoordonneesBancaires(String coordonneesBancaires) {
    this.coordonneesBancaires = coordonneesBancaires;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }


  public static class UserProfileDtoBuilder {
    private Long userid;
    private String firstName;
    private String lastName;
    private String email;
    private String imageFile;
    private String activitePrincipale;
    private String coordonneesBancaires;
    private String telephone;


    public UserProfileDtoBuilder userid(Long userid) {
      this.userid = userid;
      return this;
    }

    public UserProfileDtoBuilder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public UserProfileDtoBuilder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public UserProfileDtoBuilder email(String email) {
      this.email = email;
      return this;
    }

    public UserProfileDtoBuilder telephone(String telephone) {
      this.telephone = telephone;
      return this;
    }

    public UserProfileDtoBuilder imageFile(String imageFile) {
      this.imageFile = imageFile;
      return this;
    }

    public UserProfileDtoBuilder activitePrincipale(String activitePrincipale) {
      this.activitePrincipale = activitePrincipale;
      return this;
    }

    public UserProfileDtoBuilder coordonneesBancaires(String coordonneesBancaires) {
      this.coordonneesBancaires = coordonneesBancaires;
      return this;
    }

    public UserProfileDto build() {
      UserProfileDto userDto = new UserProfileDto();
      userDto.setUserid(userid);
      userDto.setFirstName(firstName);
      userDto.setLastName(lastName);
      userDto.setEmail(email);
      userDto.setImageFile(imageFile);
      userDto.setTelephone(telephone);
      userDto.setActivitePrincipale(activitePrincipale);
      userDto.setCoordonneesBancaires(coordonneesBancaires);
      return userDto;
    }
  }

  public static UserProfileDtoBuilder builder() {
    return new UserProfileDtoBuilder();
  }

  public static UserProfileDto fromEntity(User user) {
    if (user == null) {
      return null;
    }
    return UserProfileDto.builder().userid(user.getUserid()).firstName(user.getFirstName())
        .lastName(user.getLastName()).email(user.getEmail()).imageFile(user.getPhoto())
        .telephone(user.getTelephone()).activitePrincipale(user.getActivitePrincipale())
        .coordonneesBancaires(user.getCoordonneesBancaires()).build();
  }
}
