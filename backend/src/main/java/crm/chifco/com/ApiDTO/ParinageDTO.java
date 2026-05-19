package crm.chifco.com.ApiDTO;

import java.util.Date;

public class ParinageDTO {

    private String referenceParinage;
    private String cinParrain;
    private String cinParinee;
    private String statut;
    private String nomParrain;
    private String nomParinee;
    private String telFixe;
    private Date createdDate;
    private String email;
    // Constructeur vide
    public ParinageDTO() {
    }

    // Constructeur avec paramètres
    public ParinageDTO(String referenceParinage,
                       String cinParrain,
                       String cinParinee,
                       String statut,
                       String nomParrain,
                       String nomParinee,
                       String telFixe,
                       Date createdDate , 
                       String email) {
        this.referenceParinage = referenceParinage;
        this.cinParrain = cinParrain;
        this.cinParinee = cinParinee;
        this.statut = statut;
        this.nomParrain = nomParrain;
        this.nomParinee = nomParinee;
        this.telFixe = telFixe;
        this.createdDate = createdDate;
        this.email = email ; 
    }

    // Getters & Setters
    public String getReferenceParinage() {
        return referenceParinage;
    }

    public void setReferenceParinage(String referenceParinage) {
        this.referenceParinage = referenceParinage;
    }

    public String getCinParrain() {
        return cinParrain;
    }

    public void setCinParrain(String cinParrain) {
        this.cinParrain = cinParrain;
    }

    public String getCinParinee() {
        return cinParinee;
    }

    public void setCinParinee(String cinParinee) {
        this.cinParinee = cinParinee;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNomParrain() {
        return nomParrain;
    }

    public void setNomParrain(String nomParrain) {
        this.nomParrain = nomParrain;
    }

    public String getNomParinee() {
        return nomParinee;
    }

    public void setNomParinee(String nomParinee) {
        this.nomParinee = nomParinee;
    }

    public String getTelFixe() {
        return telFixe;
    }

    public void setTelFixe(String telFixe) {
        this.telFixe = telFixe;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
    
}
