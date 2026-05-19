package crm.chifco.com.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import crm.chifco.com.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Role findRoleByRoleName(String role);

  Role findRoleByRoleId(Long roleId);

  // recuperer la liste des roles en eliminant le role d'administrateur et du gestionnaire de stock
  @Query("select r from Role r WHERE r.roleName NOT IN ('ROLE_ADMINISTRATEUR', 'ROLE_G.STOCK')")
  public List<Role> findRoles();

}
