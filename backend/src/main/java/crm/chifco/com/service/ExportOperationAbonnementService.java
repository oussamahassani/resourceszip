package crm.chifco.com.service;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

public interface ExportOperationAbonnementService {
  void exportDemande(List<List<Long>> demandeIds, String type, HttpServletResponse response)
      throws IOException;
}
