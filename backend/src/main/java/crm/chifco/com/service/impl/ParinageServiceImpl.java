package crm.chifco.com.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.model.Parinage;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.ParinageRepository;
import crm.chifco.com.service.ExportExcelparinage;
import crm.chifco.com.service.ParinageService;
import crm.chifco.com.service.UserService;
import crm.chifco.com.utils.CrmUtils;
import crm.chifco.com.utils.StatusParinage;
import crm.chifco.com.ApiDTO.ParinageDTO;
@Service
public class ParinageServiceImpl implements ParinageService {
  private final Logger LOGGER = LogManager.getLogger(this.getClass());

  @Value("${pathParinage}")
  private String pathParinage;

  @Autowired
  ParinageRepository parinageRepository;

  @Autowired
  UserService userservice;

  @Override
  public Boolean saveParinage(String cinParrain, String cinParinee, String prenomParrain,
      String prenomParinee, String telFixe, MultipartFile cinRecto, MultipartFile cinVerso,
      String email, String website) {
    // TODO Auto-generated method stub
    try {
      Parinage newParinage = new Parinage();
      newParinage.setCinParinee(cinParinee);
      newParinage.setCinParrain(cinParrain);
      newParinage.setNomParinee(prenomParinee);
      newParinage.setNomParrain(prenomParrain);
      newParinage.setStatut(StatusParinage.ATTENTE);
      newParinage.setTelFixe(telFixe);
      newParinage.setEmail(email);
      User user = null;
      if (website != null) {
        // user
        user = userservice.findTop1UsersByEmail(website);
        if (user != null)
          LOGGER.info("parinage user affecter est : " + user.getUserid());
        else {
          user = userservice.findTop1UsersByEmail("demandeAbonnementWebSite@nety.tn");
        }
      } else {
        user = userservice.findTop1UsersByEmail("demandeAbonnementWebSite@nety.tn");
      }
      newParinage.setCreatedBy(user);
      if (!cinVerso.isEmpty()) {
        try {

          CrmUtils.saveImage(cinVerso, "", pathParinage, "");
          newParinage.setCinVerso(CrmUtils.noSpecialCharacters(cinVerso.getOriginalFilename()));



        } catch (Exception e) {

          LOGGER.error("parinage cinverso Exception: " + e.getMessage());

        }
      }
      if (!cinRecto.isEmpty()) {
        try {

          CrmUtils.saveImage(cinRecto, "", pathParinage, "");
          newParinage.setCinRecto(CrmUtils.noSpecialCharacters(cinRecto.getOriginalFilename()));


        } catch (Exception e) {

          LOGGER.error("parinage cinRecto Exception: " + e.getMessage());

        }
      }
      parinageRepository.save(newParinage);
      return true;
    } catch (Exception exption) {
      LOGGER.error("parinage cinRecto Exception: " + exption.getMessage());

      return false;
    }
  }

  @Override
  public Page<Parinage> findPaginatedwithfilter(int pageNo, int pageSize, int sortvar,
      String sorttype, String filterrecherche) {
    int currentpage = pageNo / pageSize;
    Date datedebut = null;
    Date datefin = null;
    String status = null;
    // TODO Auto-generated method stub
    if (filterrecherche != null && !filterrecherche.equals("")) {
      JSONObject obj = new JSONObject(filterrecherche);
      if (obj.keySet().contains("datedebut") && !Objects.equals(obj.getString("datedebut"), "")
          && obj.getString("datedebut") != null) {
        datedebut = CrmUtils.convertStringToDate(obj.getString("datedebut").trim());
      }
      if (obj.keySet().contains("datefin") && !Objects.equals(obj.getString("datefin"), "")
          && obj.getString("datefin") != null) {
        datefin = CrmUtils.convertStringToLocalDateTime(obj.getString("datefin").trim());
      }
   
      if (obj.keySet().contains("statutfiltre")
          && !Objects.equals(obj.getString("statutfiltre"), "")
          && obj.getString("statutfiltre") != null) {
        status = obj.getString("statutfiltre").trim();
      }
    }
    Pageable pageable = PageRequest.of(currentpage, pageSize);
    return parinageRepository.findAllDemandeParinageWithFilter(pageable, datedebut, datefin,
        status);
  }
  @Override
  public List<ParinageDTO> findListwithfilterForApplication( Map<String, Object>   filterrecherche) {
   
    Date datedebut = null;
    Date datefin = null;
    String status = null;
    String cin = null;
    // TODO Auto-generated method stub
    if (filterrecherche != null && !filterrecherche.equals("")) {
    
      if (filterrecherche.keySet().contains("datedebut") && !Objects.equals(filterrecherche.get("datedebut"), "")
          && filterrecherche.get("datedebut") != null) {
        datedebut = CrmUtils.convertStringToDate(filterrecherche.get("datedebut").toString().trim());
      }
      if (filterrecherche.keySet().contains("datefin") && !Objects.equals(filterrecherche.get("datefin"), "")
          && filterrecherche.get("datefin") != null) {
        datefin = CrmUtils.convertStringToLocalDateTime(filterrecherche.get("datefin").toString().trim());
      }
   
      if (filterrecherche.keySet().contains("statut")
          && !Objects.equals(filterrecherche.get("statut"), "")
          && filterrecherche.get("statut") != null) {
        status = filterrecherche.get("statut").toString().trim();
      }
      if (filterrecherche.keySet().contains("cin")
              && !Objects.equals(filterrecherche.get("cin"), "")
              && filterrecherche.get("cin") != null) {
    	  cin = filterrecherche.get("cin").toString().trim();
          }
    }
   
    return parinageRepository.findAllDemandeParinageWithFilterForApp( datedebut, datefin,
    		cin, status);
  }
  @Override
  public Parinage findParinageById(Long idParinage) {
    // TODO Auto-generated method stub
    Optional<Parinage> parinage = parinageRepository.findById(idParinage);
    return parinage.get();
  }

  @Override
  public Boolean findParinageByIdAndUpdtaeStatus(Long idParinage, String status,
      String commentaire) {
    Optional<Parinage> parinage = parinageRepository.findById(idParinage);
    if (parinage.isPresent()) {
      if ("CANCAL".equals(status) && (commentaire == null || commentaire.trim().isEmpty())) {
        return false;
      }
      parinage.get().setStatut(status);
      parinage.get().setCommentaire(commentaire);
      parinageRepository.save(parinage.get());
      return true;
    }
    return false;
  }

  @Override
  public ModelAndView extractEnMasse(HttpServletRequest request, HttpServletResponse response,
      String datedebut, String datefin, String statutfiltre) {
    // TODO Auto-generated method stub
    ModelAndView mav = new ModelAndView();
    Date dateD = null;
    Date dateF = null;
    String statutFiltre = null;
    if (datedebut != null && !datedebut.isEmpty()) {
      dateD = CrmUtils.convertStringToLocalDateTime(datedebut);

    }
    if (datefin != null && !datefin.isEmpty()) {
      dateF = CrmUtils.convertStringToLocalDateTime(datefin);

    }
    if (statutfiltre != null && !statutfiltre.isEmpty()) {
      statutFiltre = statutfiltre;

    }
    List<Parinage> parinage =
        parinageRepository.findAllDemandeParinageWithFilterXls(dateD, dateF, statutFiltre);
    if (parinage.size() > 0) {

      mav.setView(new ExportExcelparinage());
      mav.addObject("list", parinage);
      return mav;
    } else {
      try {
        request.getRequestDispatcher("/parinage/allparinage").forward(request, response);
      } catch (ServletException e) {
        // TODO Auto-generated catch block

        LOGGER.error("msg1 : xls client non connecter Error:" + e);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        LOGGER.error("msg2 : xls client non connecter Error:" + e);
      }
      return mav;
    }
  }

}
