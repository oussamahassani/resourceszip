package crm.chifco.com.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import crm.chifco.com.model.Role;
import crm.chifco.com.repository.RoleRepository;
import crm.chifco.com.service.RoleService;

@Service("roleService")
public class RoleServiceImpl implements RoleService {
  @Autowired
  RoleRepository roleRepository;

  @Override
  public Page<Role> findPaginated(int pageNo, int pageSize) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    return this.roleRepository.findAll(pageable);
  }

  public Role findRoleByRoleName(String role) {
    return this.roleRepository.findRoleByRoleName(role);
  }
}
