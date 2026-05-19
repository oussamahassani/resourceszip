package crm.chifco.com.ApiDTO;

import java.util.Date;

import crm.chifco.com.model.Commission;

//Classe représentant une facture
public class FactureCommisionPGH {
 private String entite;
 private String fournisseur;
 private String commercial;
 private String matricule;
 private String numFact;
 private Date dateFact;
 private Double montant;
 private Double montantHTSansFodec;
 private Double montantHTAvecFodec;
 private Double fodec;
 private Double tva;
 private String timbre;
 private String listAL;

 // Constructeur
 public FactureCommisionPGH(String entite, String fournisseur, String commercial, String matricule,
                String numFact, Date dateFact, Double montant, 
                Double montantHTSansFodec, Double montantHTAvecFodec,
                Double fodec, Double tva, String timbre, String listAL) {
     this.entite = entite;
     this.fournisseur = fournisseur;
     this.commercial = commercial;
     this.matricule = matricule;
     this.numFact = numFact;
     this.dateFact = dateFact;
     this.montant = montant;
     this.montantHTSansFodec = montantHTSansFodec;
     this.montantHTAvecFodec = montantHTAvecFodec;
     this.fodec = fodec;
     this.tva = tva;
     this.timbre = timbre;
     this.listAL = listAL;
 }
 public FactureCommisionPGH() {
	// TODO Auto-generated constructor stub
}
public  FactureCommisionPGH mapToFacture(Commission commission) {
     if (commission == null) {
         return null;
     }
     
     FactureCommisionPGH facture = new FactureCommisionPGH();
     
     facture.setEntite("d" + commission.getId());
     facture.setFournisseur(commission.getRevendeur().getCodePGHUser());
     facture.setCommercial(commission.getRevendeur().getLastName() + " " + commission.getRevendeur().getFirstName() );
     facture.setMatricule(commission.getRevendeur().getIdentificationFiscale());
     
     facture.setNumFact(commission.getRefCommission());
     
     // Formatage de la date
     if (commission.getCreatedDate() != null) {
         facture.setDateFact(commission.getCreatedDate());
     }
     
     // Mappage des montants
     facture.setMontant(commission.getTotalTtc());
     facture.setMontantHTSansFodec(commission.getTotalHt());
     facture.setMontantHTAvecFodec(commission.getTotalHt());
     facture.setFodec(0.0);
     facture.setTva(commission.getMontantTva());
     
     // Conversion du timbre
     facture.setTimbre("1");
     
     // Autres champs
     facture.setListAL("" );
   
     
     return facture;
 }
 
 // Getters
 public String getEntite() { return entite; }
 public String getFournisseur() { return fournisseur; }
 public String getCommercial() { return commercial; }
 public String getMatricule() { return matricule; }
 public String getNumFact() { return numFact; }
 public Date getDateFact() { return dateFact; }
 public Double getMontant() { return montant; }
 public Double getMontantHTSansFodec() { return montantHTSansFodec; }
 public Double getMontantHTAvecFodec() { return montantHTAvecFodec; }
 public Double getFodec() { return fodec; }
 public Double getTva() { return tva; }
 public String getTimbre() { return timbre; }
 public String getListAL() { return listAL; }
public void setEntite(String entite) {
	this.entite = entite;
}
public void setFournisseur(String fournisseur) {
	this.fournisseur = fournisseur;
}
public void setCommercial(String commercial) {
	this.commercial = commercial;
}
public void setMatricule(String matricule) {
	this.matricule = matricule;
}
public void setNumFact(String numFact) {
	this.numFact = numFact;
}
public void setDateFact(Date dateFact) {
	this.dateFact = dateFact;
}
public void setMontant(Double montant) {
	this.montant = montant;
}
public void setMontantHTSansFodec(Double montantHTSansFodec) {
	this.montantHTSansFodec = montantHTSansFodec;
}
public void setMontantHTAvecFodec(Double montantHTAvecFodec) {
	this.montantHTAvecFodec = montantHTAvecFodec;
}
public void setFodec(Double fodec) {
	this.fodec = fodec;
}
public void setTva(Double tva) {
	this.tva = tva;
}
public void setTimbre(String timbre) {
	this.timbre = timbre;
}
public void setListAL(String listAL) {
	this.listAL = listAL;
}
 
 
 
}
