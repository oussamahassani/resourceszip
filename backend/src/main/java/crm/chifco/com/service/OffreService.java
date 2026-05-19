package crm.chifco.com.service;

import java.util.HashMap;
import java.util.List;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.Offre;

public interface OffreService {
  List<Offre> findAllOffre();

  HashMap<String, Object> allMyOffres(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche);

  Page<Offre> findPaginatedwithfilter(int pageNo, int pageSize, String sortvar, String sorttype);

  void createNewOffre(String nom, String type, String isPromo, String isActive, Boolean isPrivate,
      String dateDebutPromo, String dateFinPromo, Long periodeValidPromo, Long idOffre,
      Boolean isRevSelected);

  Offre getOneOffre(Long offreId);

  List<Offre> findAllOffreByIdOffreBase(Long IdOffreBase);

  void updateOffre(Long offreId, String nom, String isPromo, String isActive, Boolean isPrivate,
      String dateDebutPromo, String dateFinPromo, Long periodeValidPromo, Long idOffreBase,
      String type);

  List<Offre> findAllOffreByIsActive(boolean isActive);

  List<Offre> findAllOffreExisteInPack();

  List<Offre> findAllOffreExisteInPackByVisibility();

  List<Offre> AllOffreByIdOffreBaseAndNotId(Long idOffreBase, Long id);

  List<Offre> findAllOffreExisteInPackByRevSelectedAndVisibility();


}
