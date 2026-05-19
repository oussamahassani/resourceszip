package crm.chifco.com.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import crm.chifco.com.model.FicheStock;
import crm.chifco.com.model.Modem;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.FicheRepository;
import crm.chifco.com.repository.ModemRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.templateclasse.ModemAffectationFiches;

@Service
@Transactional
public class FicheStockService {

  @Autowired
  private FicheRepository ficheRepository;
  // @Autowired
  // private FicheStock fs ;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ModemRepository modemRepository;


  // ******************************************************* enregistrer une fiche

  public void saveStock(FicheStock stock) {
    ficheRepository.save(stock);
  }

  // ***************************************************** afficher tout les fiches

  public Page<ModemAffectationFiches> getFiches(int pageNo, int pageSize, Long idconnected,
      String datedebut, String datefin, Long affectA) {
    Pageable pageable =
        PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "id_fiche"));
    return ficheRepository.getFicheByUserID(pageable, idconnected, datedebut, datefin, affectA);
  }

  // ********************************************** get fiche by ref or id
  public FicheStock getFiche(String refFiche) {
    return ficheRepository.getFicheByRef(refFiche);

  }


  // ************************************************ recuperer la liste des modems par leurs
  // references

  public List<Modem> listmodemfiche(String ref) {

    FicheStock f = ficheRepository.getFicheByRef(ref); // recuperer la fiche par sa reference
    List<Modem> p = modemRepository.findByListNumSerie(f.getNumSerieModem());

    return p;
  }

  // ***************************************************************** recuperer utilisateur
  // (destinataire de la fiche ) par la reference de la fiche
  public User usersfiche(Long id) {

    User u = userRepository.getById(id); // recuperer l Users de la fiche

    return u;
  }

}

