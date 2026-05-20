package crm.chifco.com.controller;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import crm.chifco.com.model.Privilege;
import crm.chifco.com.model.Role;
import crm.chifco.com.repository.PrivilegeRepository;
import crm.chifco.com.repository.RoleRepository;

@RestController
@RequestMapping("role")
public class RoleController {

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PrivilegeRepository privilegeRepository;

  @GetMapping("/allroles")
  public ResponseEntity<List<Role>> getAllRoles() {
    return ResponseEntity.ok(roleRepository.findAll());
  }

  @GetMapping("/allprivileges")
  public ResponseEntity<List<Privilege>> getAllPrivileges() {
    return ResponseEntity.ok(privilegeRepository.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
    Role role = roleRepository.findRoleByRoleId(id);
    return role != null ? ResponseEntity.ok(role) : ResponseEntity.notFound().build();
  }

  @GetMapping("/privileges/{id}")
  public ResponseEntity<Privilege> getPrivilegeById(@PathVariable Long id) {
    Privilege p = privilegeRepository.findPrivilegeByprivilegeId(id);
    return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
  }

  @PreAuthorize("hasAuthority('ADD_ROLE')")
  @PostMapping
  public ResponseEntity<?> createRole(@RequestBody Role role) {
    Role existing = roleRepository.findRoleByRoleName(role.getRoleName());
    if (existing != null) {
      return ResponseEntity.badRequest().body("Rôle déjà existant: " + role.getRoleName());
    }
    return ResponseEntity.ok(roleRepository.save(role));
  }

  @PreAuthorize("hasAuthority('ADD_ROLE')")
  @PutMapping("/{id}")
  public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Role body) {
    Role existing = roleRepository.findRoleByRoleId(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    existing.setRoleName(body.getRoleName());
    if (body.getPrivileges() != null) {
      existing.setPrivileges(body.getPrivileges());
    }
    return ResponseEntity.ok(roleRepository.save(existing));
  }

  @PreAuthorize("hasAuthority('ADD_ROLE')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
    roleRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("hasAuthority('ADD_ROLE')")
  @PostMapping("/privileges")
  public ResponseEntity<?> createPrivilege(@RequestBody Privilege privilege) {
    Privilege existing =
        privilegeRepository.findPrivilegeByPrivilegeName(privilege.getPrivilegeName());
    if (existing != null) {
      return ResponseEntity.badRequest()
          .body("Privilege déjà existant: " + privilege.getPrivilegeName());
    }
    return ResponseEntity.ok(privilegeRepository.save(privilege));
  }

  @PreAuthorize("hasAuthority('ADD_ROLE')")
  @PutMapping("/privileges/{id}")
  public ResponseEntity<?> updatePrivilege(@PathVariable Long id, @RequestBody Privilege body) {
    Privilege existing = privilegeRepository.findPrivilegeByprivilegeId(id);
    if (existing == null) {
      return ResponseEntity.notFound().build();
    }
    existing.setPrivilegeName(body.getPrivilegeName());
    return ResponseEntity.ok(privilegeRepository.save(existing));
  }

  @PreAuthorize("hasAuthority('ADD_ROLE')")
  @DeleteMapping("/privileges/{id}")
  public ResponseEntity<Void> deletePrivilege(@PathVariable Long id) {
    privilegeRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }
}
