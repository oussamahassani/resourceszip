package crm.chifco.com.converter;

import java.util.ArrayList;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import crm.chifco.com.model.Facture;
import crm.chifco.com.templateclasse.ListeFactureNonPayeDTO;

@Component
public class FactureToListeFactureNonPayeDTOConverter
    implements Converter<Facture, ListeFactureNonPayeDTO> {

  @Override
  public ListeFactureNonPayeDTO convert(Facture facture) {
    ListeFactureNonPayeDTO listeFactureNonPayeDTO = new ListeFactureNonPayeDTO();
    listeFactureNonPayeDTO.setRef_facture(facture.getRef_facture());
    listeFactureNonPayeDTO.setTotal_ttc(facture.getMontant_payer());
    listeFactureNonPayeDTO.setDateDeDebut(facture.getDateDeDebut());
    listeFactureNonPayeDTO.setEcheance(facture.getDate_echeance());
    listeFactureNonPayeDTO.setDateDeFin(facture.getDateDeFin());
    return listeFactureNonPayeDTO;
  }


  public List<ListeFactureNonPayeDTO> convertToListDTO(List<Facture> factures) {
    List<ListeFactureNonPayeDTO> listeFactureNonPayeDTOs = new ArrayList<>();
    for (Facture facture : factures) {
      listeFactureNonPayeDTOs.add(convert(facture));
    }
    return listeFactureNonPayeDTOs;
  }
}
