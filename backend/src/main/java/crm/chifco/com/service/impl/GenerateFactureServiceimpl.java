package crm.chifco.com.service.impl;

import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.EntryFactures;
import crm.chifco.com.model.Facture;
import crm.chifco.com.service.GenerateFactureService;

@Service("GenerateFactureService")
public class GenerateFactureServiceimpl implements GenerateFactureService {
  

  Double prixTotalTTC;
  Double prixTotalHt;
  Double prixTotalTva;
  Double prixBasTva;

  // ****************************************************** methode pour calculer
  // le montant final en comptant le taux TVA et TIMBRE

  public Facture calculeMontantFacture(Facture premierefacture) {
    List<EntryFactures> listeEntry = premierefacture.getEntriesFacture();
    HashMap<String, Object> montantFacture = new HashMap<String, Object>();
    prixTotalTTC = 0.0;
    prixTotalHt = 0.0;
    prixTotalTva = 0.0;
    prixBasTva = 0.0;
    listeEntry.forEach(factureEntry -> {

      prixTotalTTC += factureEntry.getPrixTtc();
      prixTotalHt += factureEntry.getPrixTotalHT();
      prixTotalTva += factureEntry.getPrixTva();

      if (!factureEntry.getPourcentageTva().equals(0L))
        prixBasTva += factureEntry.getPrixTotalHT();
    });

    premierefacture.setMontantHt(prixTotalHt);
    premierefacture.setMontantTva(prixTotalTva);
    premierefacture.setPrixBaseTva(prixBasTva);
    premierefacture.setMontant_payer(prixTotalTTC + premierefacture.getTimbrefiscale());
    return premierefacture;
  }

}
