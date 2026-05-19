package crm.chifco.com.service;

import java.util.HashMap;
import java.util.List;
import org.springframework.data.domain.Page;
import crm.chifco.com.model.EntryPack;
import crm.chifco.com.model.Pack;

public interface PackService {

  List<Pack> getPackSByOffre_offreId(Long offreid);

  Pack findPackBypackId(Long idpack);

  List<Pack> getPackSByCategoriePack_categorieProduitInternetId(Long categoryid);

  Pack findPackByCodepack(String first);

  HashMap<String, Object> allMyPack(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche);

  Page<Pack> findPaginatedwithfilter(int pageNo, int pageSize, String sortvar, String sorttype);

  void addNewPack(String nom, String description, Long offre, Long categorie, Long[] produits,
      String[] isShow, Double Prix, Double remise, Long pourcentTva, Long idPackBase,
      String debitPack, Boolean payLater, Long idEngagement);

  List<Pack> findPackByIdPackBase(Long object);

  List<EntryPack> findEntryPackBypack(Pack pack);

  Pack updatePack(Pack oldPack, String nom, String description, Long offre, Long categorie,
      Long[] produits, String[] isShow, Long idPackBase, String debitPack);

}
