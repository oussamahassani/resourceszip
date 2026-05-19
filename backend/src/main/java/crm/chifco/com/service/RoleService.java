package crm.chifco.com.service;

import org.springframework.data.domain.Page;

import crm.chifco.com.model.Role;

public interface RoleService {
  Page<Role> findPaginated(int pageNo, int pageSize);

  Role findRoleByRoleName(String role);
}
