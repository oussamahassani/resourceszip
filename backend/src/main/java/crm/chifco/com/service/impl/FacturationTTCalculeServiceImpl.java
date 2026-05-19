package crm.chifco.com.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import crm.chifco.com.repository.FraisTTAbonnementRepository;
import crm.chifco.com.repository.FraisTTAbonnementServicesRepository;
import crm.chifco.com.service.FacturationTTCalculeService;
import crm.chifco.com.service.FacturationTTCalculeServiceExcelExport;
import crm.chifco.com.templateclasse.FraisTTData;

@Service("FacturationTTCalculeService")
public class FacturationTTCalculeServiceImpl implements FacturationTTCalculeService {
  private final Logger Logger = LogManager.getLogger(this.getClass());

  @Autowired
  FraisTTAbonnementRepository fraisTTAbonnementRepository;

  @Autowired
  FraisTTAbonnementServicesRepository fraisTTAbonnementServicesRepository;

  @Override
  public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response,
      String date) {
    // TODO Auto-generated method stub
    ModelAndView mav = new ModelAndView();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    YearMonth yearMonth = YearMonth.parse(date.trim(), formatter);

    LocalDate startOfMonth = yearMonth.atDay(1);

    List<FraisTTData> fraisTTabonnement =
        fraisTTAbonnementRepository.getAllDataBycretedDate(startOfMonth.toString());

    List<FraisTTData> fraisTTabonnementService =
        fraisTTAbonnementServicesRepository.getAllDataBycretedDate(startOfMonth.toString());

    // Combine the two lists into one
    List<FraisTTData> combinedList = new ArrayList<>();
    combinedList.addAll(fraisTTabonnement);
    combinedList.addAll(fraisTTabonnementService);

    // Define a custom comparator to sort by userName
    Comparator<FraisTTData> userNameComparator = Comparator.comparing(FraisTTData::getuser_name);

    // Sort the combined list by userName
    Collections.sort(combinedList, userNameComparator);
    try {
      if (combinedList.size() > 0) {

        mav.setView(new FacturationTTCalculeServiceExcelExport());
        mav.addObject("list", combinedList);

        return mav;
      } else {
        request.getRequestDispatcher("/facturationTT/homeView").forward(request, response);
        return null;
      }
    } catch (Exception e) {
      Logger.error(" demandeabonnement.exportation client Error:" + e);

    }
    return mav;
  }

}
