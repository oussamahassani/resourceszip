package crm.chifco.com.service;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

public interface ExportReclamationService {
  void exportReclamations(List<Long> reclamationIds, HttpServletResponse response)
      throws IOException;
}
