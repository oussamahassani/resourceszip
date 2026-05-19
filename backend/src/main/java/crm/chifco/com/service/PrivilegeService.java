package crm.chifco.com.service;

import crm.chifco.com.model.Privilege;
import org.springframework.data.domain.Page;

public interface PrivilegeService {
  Page<Privilege> findPaginated(int pageNo, int pageSize);
}
