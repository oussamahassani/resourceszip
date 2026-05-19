package crm.chifco.com.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.model.Parinage;
import crm.chifco.com.ApiDTO.ParinageDTO;
public interface ParinageService {

  Boolean saveParinage(String cinParrain, String cinParinee, String prenomParrain,
      String prenomParinee, String telFixe, MultipartFile cinRecto, MultipartFile cinVerso,
      String email, String website);

  Page<Parinage> findPaginatedwithfilter(int pageNo, int pageSize, int sortvar, String sorttype,
      String filterrecherche);

  Parinage findParinageById(Long idParinage);

  Boolean findParinageByIdAndUpdtaeStatus(Long idParinage, String staus, String commentaire);

  ModelAndView extractEnMasse(HttpServletRequest request, HttpServletResponse response,
      String datedebut, String datefin, String statutfiltre);
  
  List<ParinageDTO> findListwithfilterForApplication( Map<String, Object> filterrecherche);
}
