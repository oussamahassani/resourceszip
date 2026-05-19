package crm.chifco.com.service;

import java.util.List;

import crm.chifco.com.model.CategoryClient;
import crm.chifco.com.model.Produit;
import crm.chifco.com.model.Tarification;

public interface TarificationServices {

  public Tarification getTarificationBypackId(Long packId);

  public Tarification getTarificationByProduitId(Long idProduit);

  public Tarification saveNewTarification(Long packId, Long produitId, Double prixUnitaire,
      Long taxe, String typeRemise, Double remise, Double prixTTc, CategoryClient categoryClient);

  public Tarification updateTarification(Long tarificationId, Double prixUnitaire, Long taxe,
      String typeRemise, Double remise, Double prixTTc, CategoryClient categoryClient);
  
  List<Tarification> findAllProduitPrix(List<Long> listIds );

}
