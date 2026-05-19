package crm.chifco.com.DTOclass;

import java.util.Date;

public class DemandeAbbonmentDataDTOv2 {

  private Long demandeId;

  private String firstName;

  private String lastName;

  private Long telFixe;

  private Long telMobile;

  private Date createdDate;

  private String referenceTT;

  private String etatTT;

  private String nomStatut;

  private String couleurStatut;

  private String titlePack;

  private String referenceChifco;

  private Long statutId;

  private String statutDesignation;

  private String cin;

  private String createdByfirstName;
  private String createdBylastName;

  private String motifRefus;

  private String classementCode;

  private String classementValue;
  private String treatedByfirstName;
  private String treatedBylaststName;
  private String origin;

  public DemandeAbbonmentDataDTOv2() {
    super();
  }

  public DemandeAbbonmentDataDTOv2(Long demandeId, String firstName, String lastName, Long telFixe,
      Long telMobile, Date createdDate, String referenceTT, String etatTT, String nomStatut,
      String titlePack, String referenceChifco, String couleurStatut, Long statutId,
      String statutDesignation, String Cin, String createdByfirstName, String createdBylastName,
      String motifRefus, String classementCode, String classementValue, String treatedByfirstName,
      String treatedBylaststName, String origin) {
    super();
    this.demandeId = demandeId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.telFixe = telFixe;
    this.telMobile = telMobile;
    this.createdDate = createdDate;
    this.referenceTT = referenceTT;
    this.etatTT = etatTT;
    this.nomStatut = nomStatut;
    this.titlePack = titlePack;
    this.referenceChifco = referenceChifco;
    this.couleurStatut = couleurStatut;
    this.statutId = statutId;
    this.statutDesignation = statutDesignation;
    this.cin = Cin;
    this.createdByfirstName = createdByfirstName;
    this.createdBylastName = createdBylastName;
    this.motifRefus = motifRefus;
    this.classementCode = classementCode;
    this.classementValue = classementValue;
    this.treatedByfirstName = treatedByfirstName;
    this.treatedBylaststName = treatedBylaststName;
    this.origin = origin;
  }


  public Long getDemandeId() {
    return demandeId;
  }

  public void setDemandeId(Long demandeId) {
    this.demandeId = demandeId;
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

  public Long getTelFixe() {
    return telFixe;
  }

  public void setTelFixe(Long telFixe) {
    this.telFixe = telFixe;
  }

  public Long getTelMobile() {
    return telMobile;
  }

  public void setTelMobile(Long telMobile) {
    this.telMobile = telMobile;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getReferenceTT() {
    return referenceTT;
  }

  public void setReferenceTT(String referenceTT) {
    this.referenceTT = referenceTT;
  }

  public String getEtatTT() {
    return etatTT;
  }

  public void setEtatTT(String etatTT) {
    this.etatTT = etatTT;
  }

  public String getNomStatut() {
    return nomStatut;
  }

  public void setNomStatut(String nomStatut) {
    this.nomStatut = nomStatut;
  }

  public String getTitlePack() {
    return titlePack;
  }

  public void setTitlePack(String titlePack) {
    this.titlePack = titlePack;
  }

  public String getReferenceChifco() {
    return referenceChifco;
  }

  public void setReferenceChifco(String referenceChifco) {
    this.referenceChifco = referenceChifco;
  }

  public String getCouleurStatut() {
    return couleurStatut;
  }

  public void setCouleurStatut(String couleurStatut) {
    this.couleurStatut = couleurStatut;
  }

  public Long getStatutId() {
    return statutId;
  }

  public void setStatutId(Long statutId) {
    this.statutId = statutId;
  }

  public String getCin() {
    return cin;
  }

  public void setCin(String cin) {
    this.cin = cin;
  }

  public String getCreatedByfirstName() {
    return createdByfirstName;
  }

  public void setCreatedByfirstName(String createdByfirstName) {
    this.createdByfirstName = createdByfirstName;
  }

  public String getCreatedBylastName() {
    return createdBylastName;
  }

  public void setCreatedBylastName(String createdBylastName) {
    this.createdBylastName = createdBylastName;
  }

  public String getStatutDesignation() {
    return statutDesignation;
  }

  public void setStatutDesignation(String statutDesignation) {
    this.statutDesignation = statutDesignation;
  }

  public String getMotifRefus() {
    return motifRefus;
  }

  public void setMotifRefus(String motifRefus) {
    this.motifRefus = motifRefus;
  }

  public String getClassementCode() {
    return classementCode;
  }

  public void setClassementCode(String classementCode) {
    this.classementCode = classementCode;
  }

  public String getClassementValue() {
    return classementValue;
  }

  public void setClassementValue(String classementValue) {
    this.classementValue = classementValue;
  }

  public String getTreatedByfirstName() {
    return treatedByfirstName;
  }

  public void setTreatedByfirstName(String treatedByfirstName) {
    this.treatedByfirstName = treatedByfirstName;
  }

  public String getTreatedBylaststName() {
    return treatedBylaststName;
  }

  public void setTreatedBylaststName(String treatedBylaststName) {
    this.treatedBylaststName = treatedBylaststName;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @Override
  public String toString() {
    return "DemandeAbbonmentDataDTO [demandeId=" + demandeId + ", firstName=" + firstName
        + ", lastName=" + lastName + ", telFixe=" + telFixe + ", telMobile=" + telMobile
        + ", createdDate=" + createdDate + ", referenceTT=" + referenceTT + ", etatTT=" + etatTT
        + ", nomStatut=" + nomStatut + ", titlePack=" + titlePack + "]";
  }

}
