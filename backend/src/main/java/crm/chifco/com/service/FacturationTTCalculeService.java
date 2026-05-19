package crm.chifco.com.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

public interface FacturationTTCalculeService {

  ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response, String date);
}
