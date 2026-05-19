package crm.chifco.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import crm.chifco.com.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
  Privilege findPrivilegeByPrivilegeName(String Name);

  Privilege findPrivilegeByprivilegeId(Long id);
}
