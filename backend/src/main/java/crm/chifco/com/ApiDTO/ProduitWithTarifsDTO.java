package crm.chifco.com.ApiDTO;

import java.util.List;

import crm.chifco.com.model.Produit;
import crm.chifco.com.model.Tarification;

public class ProduitWithTarifsDTO {
    private Produit produit;
    private List<Tarification> tarifications;

    public ProduitWithTarifsDTO(Produit produit, List<Tarification> tarifications) {
        this.produit = produit;
        this.tarifications = tarifications;
    }

    public Produit getProduit() {
        return produit;
    }

    public List<Tarification> getTarifications() {
        return tarifications;
    }
}
