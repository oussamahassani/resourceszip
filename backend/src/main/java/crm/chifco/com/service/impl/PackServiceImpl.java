package crm.chifco.com.service.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import crm.chifco.com.DTOclass.PackDto2;
import crm.chifco.com.model.CategorieProduitInternet;
import crm.chifco.com.model.Engagement;
import crm.chifco.com.model.EntryPack;
import crm.chifco.com.model.Offre;
import crm.chifco.com.model.Pack;
import crm.chifco.com.model.Produit;
import crm.chifco.com.model.Tarification;
import crm.chifco.com.repository.CategorieProduitInternetRepository;
import crm.chifco.com.repository.EngagementRepository;
import crm.chifco.com.repository.EntryPackRepository;
import crm.chifco.com.repository.OffreRepository;
import crm.chifco.com.repository.PackRepository;
import crm.chifco.com.repository.ProduitRepository;
import crm.chifco.com.repository.TarificationRepository;
import crm.chifco.com.service.PackService;
import crm.chifco.com.utils.CrmUtils;

@Service
@Transactional
public class PackServiceImpl implements PackService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  private PackRepository packRepository;

  @Autowired
  private OffreRepository offreRepository;

  @Autowired
  private CategorieProduitInternetRepository categorieProduitInternetRepository;

  @Autowired
  private EntryPackRepository entryPackRepository;

  @Autowired
  private ProduitRepository produitRepository;

  @Autowired
  private TarificationRepository tarificationRepository;

  @Autowired
  private EngagementRepository engagementRepository;

  @Override
  public List<Pack> getPackSByOffre_offreId(Long offreid) {
    // TODO Auto-generated method stub getPackSByOffre_offreIdOrderByDebitPackAsc
    List<Pack> packs = packRepository.getPackSByOffre_offreIdOrderByDebitPackAsc(offreid);
    Comparator<Pack> debitPackComparator = Comparator.nullsFirst(Comparator.comparingInt(pack -> {
      String debitPack = pack.getDebitPack();
      return debitPack != null ? Integer.parseInt(debitPack) : 0;
    }));
    List<Pack> sortedPacks =
        packs.stream().sorted(debitPackComparator).collect(Collectors.toList());

    return sortedPacks;
  }

  private Page<PackDto2> findPaginatedwithFilterDTO(int pageNo, int pageSize, String forfait,
      String titre, Long categories, String datedebut, String datefin, String sortvar,
      String sorttype) {
    // TODO Auto-generated method stub

    Sort sort = Sort.by("createdDate").descending();
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else {
      sort = Sort.by(sortvar).ascending();
    }
    Date datedebuts = null;
    Date datefins = null;

    if (datedebut != null) {
      datedebuts = CrmUtils.convertedFilterRechercheDate(datedebut);
    }

    if (datefin != null) {
      datefins = CrmUtils.convertedFilterRechercheDate(datefin);
    }

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    Page<PackDto2> ss = packRepository.findListPackFilter(forfait, titre, categories, datedebuts,
        datefins, pageable);
    return ss;
  }

  public Boolean checkFilterValue(JSONObject obj) {

    boolean categories = (obj.has("categories") && obj.getString("categories").trim() != "");
    boolean titre = (obj.has("titre") && obj.getString("titre").trim() != "");
    boolean forfait = (obj.has("forfait") && obj.getString("forfait").trim() != "");
    boolean datedebut = (obj.has("datedebut") && obj.getString("datedebut").trim() != "");
    boolean datefin = (obj.has("datefin") && obj.getString("datefin").trim() != "");
    if (categories || titre || forfait || datedebut || datefin) {
      return true;
    }
    return false;

  }


  @Override
  public Pack findPackBypackId(Long idpack) {
    // TODO Auto-generated method stub
    return packRepository.getById(idpack);
  }

  @Override
  public List<Pack> getPackSByCategoriePack_categorieProduitInternetId(Long categoryid) {
    // TODO Auto-generated method stub
    return packRepository.getPackSByCategoriePack_categorieProduitInternetId(categoryid);
  }

  @Override
  public Pack findPackByCodepack(String codePack) {
    // TODO Auto-generated method stub
    return packRepository.getPackSByCodePack(codePack);
  }

  @Override
  public Page<Pack> findPaginatedwithfilter(int pageNo, int pageSize, String sortvar,
      String sorttype) {

    Sort sort = Sort.by("createdDate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.packRepository.findAll(pageable);
  }

  @Override
  public HashMap<String, Object> allMyPack(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche) {
    // TODO Auto-generated method stub
    // Page<Pack> responseData = null;
    Page<PackDto2> responseData = null;
    HashMap<String, Object> myGreetings = new HashMap<>();
    int currentpage = start / length;
    // responseData = findPaginatedwithfilter(currentpage + 1, length, "createdDate", "asc");
    String sort = "";

    switch (ordercolumnaram) {
      case 1:
        sort = "createdDate";
        break;
      case 2:
        sort = "title";
        break;
      case 3:
        sort = "description";
        break;
      case 4:
        sort = "categorie";
        break;
      case 5:
        sort = "offre";
        break;
      case 6:
        sort = "prixTTc";
        break;
      default:
        sort = "createdDate";
    }
    Long categories = null;
    String forfait = null;
    String titre = null;
    String datedebuts = null;
    String datefins = null;
    if ((filterrecherche != null && !filterrecherche.equals(""))
        || (search != null && !search.equals(""))) {
      Boolean CheckFilterIfExiste = false;
      if (filterrecherche != null && !filterrecherche.equals("")) {
        JSONObject obj = new JSONObject(filterrecherche);
        CheckFilterIfExiste = this.checkFilterValue(obj);
        if (filterrecherche != null && !filterrecherche.equals("") && CheckFilterIfExiste) {

          if (!Objects.equals(obj.getString("titre"), "") && obj.getString("titre") != null) {
            titre = obj.getString("titre");
          }
          if (!Objects.equals(obj.getString("forfait"), "") && obj.getString("forfait") != null) {
            forfait = obj.getString("forfait");
          }
          if (!Objects.equals(obj.getString("categories"), "")
              && obj.getString("categories") != null) {
            categories = obj.getLong("categories");
          }
          if (!Objects.equals(obj.getString("datedebut"), "")
              && obj.getString("datedebut") != null) {
            datedebuts = obj.getString("datedebut") + "T00:00:00.000";
          }
          if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
            datefins = obj.getString("datefin") + "T23:59:59.999";
          }
        }
      }
    }
    responseData = this.findPaginatedwithFilterDTO(currentpage + 1, length, forfait, titre,
        categories, datedebuts, datefins, sort, orderdir);
    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;
  }


  @Override
  public void addNewPack(String nom, String description, Long offre, Long categorie,
      Long[] produits, String[] isShow, Double Prix, Double remise, Long pourcentTva,
      Long idPackBase, String debitPack, Boolean payLater, Long engagement) {
    Offre offreRecherched = offreRepository.findByOffreId(offre);
    CategorieProduitInternet categorieProduitInternet = categorieProduitInternetRepository
        .findCategorieProduitInternetByCategorieProduitInternetId(categorie);
    Engagement engagementObject = engagementRepository.getById(engagement);
    // TODO Auto-generated method stub
    Pack newPack = new Pack();
    newPack.setTitle(nom);
    newPack.setDebitPack(debitPack);
    newPack.setDescription(description);
    newPack.setOffre(offreRecherched);
    newPack.setEngagement(engagementObject);
    newPack.setCategoriePack(categorieProduitInternet);
    if (idPackBase != null) {
      newPack.setIdPackBase(idPackBase);
    }
    if (payLater != null) {
      newPack.setPayLater(true);
    }
    // String arr[] = nom.split(" ", 2);
    // newPack.setCodePack(arr[0] + "-" + categorieProduitInternet.getCategorieProduitInternetCode()
    // + "-" + debitPack);
    packRepository.save(newPack);

    if (produits != null) {
      for (int i = 0; i < produits.length; i++) {
        Produit findProduit = produitRepository.getById(produits[i]);
        EntryPack newEntryPack = new EntryPack();
        newEntryPack.setProduit(findProduit);
        newEntryPack.setPack(newPack);
        newEntryPack.setShowProduitFacture(Boolean.valueOf(isShow[i]));
        entryPackRepository.save(newEntryPack);

      }
    } ;
    Tarification newTarification = new Tarification();
    Double produitPrixTTC = ((Prix * (pourcentTva * 0.01)) + Prix);
    Double fromatedPrixTTc = CrmUtils.formatDoubleInput(produitPrixTTC);

    newTarification.setPrixUnitaire(Prix);
    newTarification.setRemise(remise);
    newTarification.setTypeRemise("montant");
    newTarification.setTaxe(pourcentTva);
    newTarification.setPrixTTc(fromatedPrixTTc);
    newTarification.setPackId(newPack.getPackId());
    tarificationRepository.save(newTarification);
  }

  @Override
  public Pack updatePack(Pack oldPack, String nom, String description, Long offre, Long categorie,
      Long[] produits, String[] isShow, Long idPackBase, String debitPack) {
    if (categorie != null) {
      CategorieProduitInternet categorieInternet = categorieProduitInternetRepository
          .findCategorieProduitInternetByCategorieProduitInternetId(categorie);
      oldPack.setCategoriePack(categorieInternet);
    }
    if (offre != null) {
      Offre findOffre = offreRepository.findByOffreId(offre);
      oldPack.setOffre(findOffre);
    }
    if (idPackBase != null) {
      oldPack.setIdPackBase(idPackBase);
    } else {
      oldPack.setIdPackBase(null);
    }
    oldPack.setDebitPack(debitPack);
    oldPack.setDescription(description);
    oldPack.setTitle(nom);
    packRepository.save(oldPack);
    entryPackRepository.deleteEntryPack(oldPack.getPackId());
    if (produits != null) {

      for (int i = 0; i < produits.length; i++) {
        Produit findProduit = produitRepository.getById(produits[i]);
        EntryPack newEntryPack = new EntryPack();
        newEntryPack.setProduit(findProduit);
        newEntryPack.setPack(oldPack);
        newEntryPack.setShowProduitFacture(Boolean.valueOf(isShow[i]));
        entryPackRepository.save(newEntryPack);

      }
    } ;



    return oldPack;
  }

  @Override
  public List<Pack> findPackByIdPackBase(Long idPackBase) {
    // TODO Auto-generated method stub
    return packRepository.findPackByIdPackBase(idPackBase);
  }

  @Override
  public List<EntryPack> findEntryPackBypack(Pack pack) {
    // TODO Auto-generated method stub
    return entryPackRepository.getEntryPackByPack(pack);
  }

}
