package crm.chifco.com.DTOclass;

import crm.chifco.com.model.Commission;


public class TopRevendeurCommissionDash {
  private Double totalTtc;
  private Integer mois;
  private Integer annee;
  private UserDto revendeur;
  private String formattedTotalTtc;
  private String monthName;

  public String getMonthName() {
    return monthName;
  }

  public String getMonthNameFormat(Integer mois) {
    if (mois != null && mois >= 1 && mois <= 12) {
      String[] moisNoms = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août",
          "Septembre", "Octobre", "Novembre", "Décembre"};
      return moisNoms[mois - 1];
    }
    return "";
  }

  public void setMonthName(String monthName) {
    this.monthName = monthName;
  }

  public String getFormattedTotalTtc() {
    return formattedTotalTtc;
  }

  public void setFormattedTotalTtc(String formattedTotalTtc) {
    this.formattedTotalTtc = formattedTotalTtc;
  }

  public UserDto getRevendeur() {
    return revendeur;
  }

  public void setRevendeur(UserDto revendeur) {
    this.revendeur = revendeur;
  }


  public Double getTotalTtc() {
    return totalTtc;
  }

  public void setTotalTtc(Double totalTtc) {
    this.totalTtc = totalTtc;
    this.formattedTotalTtc = String.format("%.3f TND", totalTtc);
  }

  public Integer getMois() {
    return mois;
  }

  public void setMois(Integer mois) {
    this.mois = mois;
    this.monthName = this.getMonthNameFormat(mois);
  }

  public Integer getAnnee() {
    return annee;
  }

  public void setAnnee(Integer annee) {
    this.annee = annee;
  }

  public TopRevendeurCommissionDash(Double totalTtc, Integer mois, Integer annee,
      UserDto revendeur) {
    super();
    this.totalTtc = totalTtc;
    this.mois = mois;
    this.annee = annee;
    this.revendeur = revendeur;

  }

  public TopRevendeurCommissionDash() {
    super();
  }

  public static TopRevendeurCommissionDash fromEntity(Commission commission) {
    if (commission == null) {
      return null;
    }

    TopRevendeurCommissionDash dto = new TopRevendeurCommissionDash();
    dto.setTotalTtc(commission.getTotalTtc());
    dto.setAnnee(commission.getAnnee());
    dto.setMois(commission.getMois());
    dto.setRevendeur(UserDto.fromEntity(commission.getRevendeur()));
    return dto;
  }

}
