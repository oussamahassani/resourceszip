package crm.chifco.com.service.impl;

import crm.chifco.com.model.Privilege;
import crm.chifco.com.repository.PrivilegeRepository;
import crm.chifco.com.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("privilegeService")
public class PrivilegeServiceImpl implements PrivilegeService {
  @Autowired
  PrivilegeRepository privilegeRepository;

  @Override
  public Page<Privilege> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.privilegeRepository.findAll(pageable);
  }
}
