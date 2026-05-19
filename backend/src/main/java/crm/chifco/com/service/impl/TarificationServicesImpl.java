package crm.chifco.com.service.impl;

import java.util.List;

import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.CategoryClient;
import crm.chifco.com.model.Tarification;
import crm.chifco.com.repository.TarificationRepository;
import crm.chifco.com.service.TarificationServices;

@Service
@Transactional
public class TarificationServicesImpl implements TarificationServices {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  @Autowired
  private TarificationRepository tarificationRepository;

  @Override
  public Tarification getTarificationBypackId(Long packid) {
    // TODO Auto-generated method stub
    return tarificationRepository.getTarificationBypackIdAndCategoryClient(packid, null);
  }

  @Override
  public Tarification getTarificationByProduitId(Long idProduit) {
    // TODO Auto-generated method stub
    return tarificationRepository.getTarificationByProduitIdAndCategoryClient(idProduit, null);
  }

  @Override
  public Tarification saveNewTarification(Long packId, Long produitId, Double prixUnitaire,
      Long taxe, String typeRemise, Double remise, Double prixTTc, CategoryClient categoryClient) {
    Tarification newTarification = new Tarification();
    newTarification.setCategoryClient(categoryClient);
    newTarification.setPackId(packId);
    newTarification.setPrixTTc(prixTTc);
    newTarification.setPrixUnitaire(prixUnitaire);
    newTarification.setProduitId(produitId);
    newTarification.setRemise(remise);
    newTarification.setTaxe(taxe);
    newTarification.setTypeRemise(typeRemise);
    tarificationRepository.save(newTarification);
    // TODO Auto-generated method stub
    return newTarification;
  }

  @Override
  public Tarification updateTarification(Long tarificationId, Double prixUnitaire, Long taxe,
      String typeRemise, Double remise, Double prixTTc, CategoryClient categoryClient) {
    Tarification tarificationToEdit =
        tarificationRepository.getTarificationByTarificationId(tarificationId);
    if (categoryClient != null) {
      tarificationToEdit.setCategoryClient(categoryClient);
    }

    tarificationToEdit.setPrixTTc(prixTTc);
    tarificationToEdit.setPrixUnitaire(prixUnitaire);
    tarificationToEdit.setRemise(remise);
    tarificationToEdit.setTaxe(taxe);
    if (typeRemise != null) {
      tarificationToEdit.setTypeRemise(typeRemise);
    }

    tarificationRepository.save(tarificationToEdit);


    return tarificationToEdit;
  }

@Override
public List<Tarification> findAllProduitPrix(List<Long> listIds) {
	// TODO Auto-generated method stub
	return tarificationRepository.getTarificationByProduitIdIn(listIds);
}



}
