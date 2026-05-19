package crm.chifco.com.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
import crm.chifco.com.model.Offre;
import crm.chifco.com.repository.OffreRepository;
import crm.chifco.com.service.OffreService;
import crm.chifco.com.utils.CrmUtils;

@Service
@Transactional
public class OffreServiceImpl implements OffreService {

  private final Logger LOGGER = LogManager.getLogger(this.getClass());
  @Autowired
  private OffreRepository offreRepository;

  @Override
  public List<Offre> findAllOffre() {
    // TODO Auto-generated method stub
    return offreRepository.findAll();
  }

  @Override
  public Page<Offre> findPaginatedwithfilter(int pageNo, int pageSize, String sortvar,
      String sorttype) {

    Sort sort = Sort.by("createdDate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    return this.offreRepository.findAll(pageable);
  }

  @Override
  public HashMap<String, Object> allMyOffres(int draw, int start, int length, String search,
      int ordercolumnaram, String orderdir, String filterrecherche) {
    // TODO Auto-generated method stub

    Page<Offre> responseData = null;
    HashMap<String, Object> myGreetings = new HashMap<>();
    int currentpage = start / length;

    // responseData = findPaginatedwithfilter(currentpage + 1, length, "createdDate", "asc");
    String sort = "";

    switch (ordercolumnaram) {
      case 0:
        sort = "title";
        break;
      case 1:
        sort = "isPromo";
        break;
      case 2:
        sort = "isActive";
        break;
      case 3:
        sort = "createdDate";
        break;
      default:
        sort = "createdDate";
    }
    String nomOffre = null;
    Boolean Etat = null;
    Boolean Promotion = null;
    String datedebuts = null;
    String datefins = null;
    String datedebutsPromo = null;
    String datefinsPromo = null;
    if ((filterrecherche != null && !filterrecherche.equals(""))
        || (search != null && !search.equals(""))) {
      Boolean CheckFilterIfExiste = false;
      if (filterrecherche != null && !filterrecherche.equals("")) {
        JSONObject obj = new JSONObject(filterrecherche);
        CheckFilterIfExiste = this.checkFilterValue(obj);
        if (filterrecherche != null && !filterrecherche.equals("") && CheckFilterIfExiste) {

          if (!Objects.equals(obj.getString("offre"), "") && obj.getString("offre") != null) {
            nomOffre = obj.getString("offre");
          }
          if (!Objects.equals(obj.getString("etat"), "")) {
            Etat = obj.getBoolean("etat");
          }
          if (!Objects.equals(obj.getString("promotion"), "")) {
            Promotion = obj.getBoolean("promotion");
          }
          if (!Objects.equals(obj.getString("datedebut"), "")
              && obj.getString("datedebut") != null) {
            datedebuts = obj.getString("datedebut") + "T00:00:00.000";
          }
          if (!Objects.equals(obj.getString("datefin"), "") && obj.getString("datefin") != null) {
            datefins = obj.getString("datefin") + "T23:59:59.999";
          }
          if (!Objects.equals(obj.getString("dateStartPromo"), "")
              && obj.getString("dateStartPromo") != null) {
            datedebutsPromo = obj.getString("dateStartPromo") + "T00:00:00.000";
          }
          if (!Objects.equals(obj.getString("dateEndPromo"), "")
              && obj.getString("dateEndPromo") != null) {
            datefinsPromo = obj.getString("dateEndPromo") + "T23:59:59.999";
          }
        }
      }
    }

    // end of added code
    // responseData = findPaginatedwithfilter(currentpage + 1, length, "createdDate", "asc");
    responseData = this.findPaginatedwithfilterrrr(currentpage + 1, length, nomOffre, Etat,
        Promotion, datedebuts, datefins, datedebutsPromo, datefinsPromo, sort, orderdir);

    if (responseData != null) {
      myGreetings.put("data", responseData.getContent());
      myGreetings.put("recordsTotal", responseData.getTotalElements());
      myGreetings.put("recordsFiltered", responseData.getTotalElements());
    }
    myGreetings.put("draw", draw);
    myGreetings.put("start", start);
    return myGreetings;
  }

  private Boolean checkFilterValue(JSONObject obj) {
    boolean offre = (obj.has("offre") && obj.getString("offre").trim() != "");
    boolean etat = (obj.has("etat") && obj.getString("etat").trim() != "");
    boolean promotion = (obj.has("promotion") && obj.getString("promotion").trim() != "");
    boolean datedebut = (obj.has("datedebut") && obj.getString("datedebut").trim() != "");
    boolean datefin = (obj.has("datefin") && obj.getString("datefin").trim() != "");
    boolean datedebutPro =
        (obj.has("dateStartPromo") && obj.getString("dateStartPromo").trim() != "");
    boolean datefinPro = (obj.has("dateEndPromo") && obj.getString("dateEndPromo").trim() != "");
    if (offre || etat || promotion || datedebut || datefin || datedebutPro || datefinPro) {
      return true;
    }
    return false;
  }

  @Override
  public void createNewOffre(String nom, String type, String isPromo, String isActive,
      Boolean isPrivate, String dateDebutPromo, String dateFinPromo, Long periodeValidPromo,
      Long idOffre, Boolean isRevSelected) {
    // TODO Auto-generated method stub
    Offre newOffre = new Offre();

    newOffre.setTitle(nom);
    newOffre.setType(type);
    if (idOffre != null) {
      Offre getOneOffre = this.getOneOffre(idOffre);
      if (getOneOffre != null) {
        newOffre.setIdOffreBase(idOffre);
      }
    }
    if (isPromo != null) {
      newOffre.setIsPromo(true);
      if (dateDebutPromo != null && !dateDebutPromo.equals("")) {
        newOffre.setDateDebutPromo(CrmUtils.convertStringToDate(dateDebutPromo));
      }
      if (dateFinPromo != null && !dateFinPromo.equals("")) {
        newOffre.setDateFinPromo(CrmUtils.convertStringToDate(dateFinPromo));
      }
      if (periodeValidPromo != null) {
        newOffre.setPeriodeValidPromo(periodeValidPromo);
      }
    }
    if (isActive != null) {
      newOffre.setIsActive(true);
    }
    if (isRevSelected != null) {
      newOffre.setIsRevSelected(true);
    }
    if (isPrivate != null) {
      newOffre.setIsPrivate(true);
    }

    this.offreRepository.save(newOffre);
  }

  @Override
  public Offre getOneOffre(Long offreId) {
    // TODO Auto-generated method stub
    return this.offreRepository.getById(offreId);
  }

  @Override
  public List<Offre> findAllOffreByIdOffreBase(Long IdOffreBase) {
    // TODO Auto-generated method stub
    return this.offreRepository.findAllOffreByIdOffreBase(IdOffreBase);
  }

  @Override
  public void updateOffre(Long offreId, String nom, String isPromo, String isActive,
      Boolean isPrivate, String dateDebutPromo, String dateFinPromo, Long periodeValidPromo,
      Long idOffreBase, String type) {
    // TODO Auto-generated method stub
    Offre dbOffre = offreRepository.findByOffreId(offreId);

    if (isActive != null) {
      dbOffre.setIsActive(true);
    } else {
      dbOffre.setIsActive(false);
    }
    if (idOffreBase != null) {
      Offre getOneOffre = this.getOneOffre(idOffreBase);
      if (getOneOffre != null) {
        dbOffre.setIdOffreBase(idOffreBase);
      }
    }

    if (isPrivate == null) {
      dbOffre.setIsPrivate(false);
    } else {
      dbOffre.setIsPrivate(true);
    }
    if (nom != null) {
      dbOffre.setTitle(nom);
    }
    if (isPromo != null) {
      dbOffre.setIsPromo(true);
    } else {
      dbOffre.setIdOffreBase(null);
    }
    if (isPromo == null) {
      dbOffre.setIsPromo(false);
    }
    if (dateDebutPromo != null && !dateDebutPromo.equals("")) {
      dbOffre.setDateDebutPromo(CrmUtils.convertStringToDate(dateDebutPromo));
    }
    if (dateDebutPromo == null) {
      dbOffre.setDateDebutPromo(null);
    }
    if (dateFinPromo != null && !dateFinPromo.equals("")) {
      dbOffre.setDateFinPromo(CrmUtils.convertStringToDate(dateFinPromo));
    }
    if (dateFinPromo == null) {
      dbOffre.setDateFinPromo(null);
    }
    dbOffre.setIdOffreBase(idOffreBase);
    dbOffre.setType(type);
    dbOffre.setPeriodeValidPromo(periodeValidPromo);
    this.offreRepository.save(dbOffre);

  }

  @Override
  public List<Offre> findAllOffreByIsActive(boolean isActive) {
    // TODO Auto-generated method stub
    return this.offreRepository.findAllOffreByIsActive(true);
  }

  @Override
  public List<Offre> findAllOffreExisteInPack() {
    // TODO Auto-generated method stub
    return this.offreRepository.findAllOffreExisteInPack();
  }

  @Override
  public List<Offre> findAllOffreExisteInPackByVisibility() {
    // TODO Auto-generated method stub
    return this.offreRepository.findAllOffreExisteInPackByVisibility();
  }

  @Override
  public List<Offre> AllOffreByIdOffreBaseAndNotId(Long IdOffreBase, Long idOffre) {
    // TODO Auto-generated method stub
    return this.offreRepository.findAllOffreByIdOffreBaseAndOffreIdNot(IdOffreBase, idOffre);
  }

  public Page<Offre> findPaginatedwithfilterrrr(int pageNo, int pageSize, String nomOffre,
      Boolean etat, Boolean Promotion, String datedebut, String datefin, String datedebutsPromo,
      String datefinsPromo, String sortvar, String sorttype) {

    Sort sort = Sort.by("createdDate");
    if (sorttype.equals("desc")) {
      sort = Sort.by(sortvar).descending();
    } else if (!sorttype.equals("desc")) {
      sort = Sort.by(sortvar).ascending();
    }
    Date datedebuts = null;
    Date datefins = null;
    Date datefinsPromos = null;
    Date datedebutsPromos = null;
    if (datedebut != null) {
      datedebuts = CrmUtils.convertedFilterRechercheDate(datedebut);
    }

    if (datefin != null) {
      datefins = CrmUtils.convertedFilterRechercheDate(datefin);
    }
    if (datedebutsPromo != null) {
      datedebutsPromos = CrmUtils.convertedFilterRechercheDate(datedebutsPromo);
    }

    if (datefinsPromo != null) {
      datefinsPromos = CrmUtils.convertedFilterRechercheDate(datefinsPromo);
    }
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
    // return this.offreRepository.findAll(pageable);
    Page<Offre> offre = this.offreRepository.findListPackFilter(nomOffre, etat, Promotion,
        datedebuts, datefins, datedebutsPromos, datefinsPromos, pageable);
    return offre;
  }

  @Override
  public List<Offre> findAllOffreExisteInPackByRevSelectedAndVisibility() {
    // TODO Auto-generated method stub
    return this.offreRepository.findAllOffreExisteInPackByRevSelectedAndVisibility();
  }


}
