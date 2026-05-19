package crm.chifco.com.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import crm.chifco.com.model.Privilege;
import crm.chifco.com.model.Role;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.PrivilegeRepository;
import crm.chifco.com.repository.RoleRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.PrivilegeService;
import crm.chifco.com.service.RoleService;

@Controller
@RequestMapping(value = "role/*")
public class RoleController {
  @Autowired
  UserRepository userRepository;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  RoleService roleService;
  @Autowired
  PrivilegeRepository privilegeRepository;
  @Autowired
  PrivilegeService privilegeService;
  private final Logger logger = LogManager.getLogger(this.getClass());

  @GetMapping(value = "allroles/{pageNo}")
  public String Roles(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");
    }
    if (pageNo == null) {
      pageNo = 1;
    }
    int pageSize = 20;
    Page<Role> pages = roleService.findPaginated(pageNo, pageSize);
    model.addAttribute("roles", pages.getContent());

    int[] body;
    if (pages.getTotalPages() > 7) {
      int totalPages = pages.getTotalPages();
      int pageNumber = pages.getNumber() + 1;
      int[] head = (pageNumber > 4) ? new int[] {1, -1} : new int[] {1, 2, 3};
      int[] bodyBefore = (pageNumber > 4 && pageNumber < totalPages - 1)
          ? new int[] {pageNumber - 2, pageNumber - 1}
          : new int[] {};
      int[] bodyCenter =
          (pageNumber > 3 && pageNumber < totalPages - 2) ? new int[] {pageNumber} : new int[] {};
      int[] bodyAfter = (pageNumber > 2 && pageNumber < totalPages - 3)
          ? new int[] {pageNumber + 1, pageNumber + 2}
          : new int[] {};
      int[] tail = (pageNumber < totalPages - 3) ? new int[] {-1, totalPages}
          : new int[] {totalPages - 2, totalPages - 1, totalPages};
      body = Utils.merge(head, bodyBefore, bodyCenter, bodyAfter, tail);

    } else {
      body = new int[pages.getTotalPages()];
      for (int i = 0; i < pages.getTotalPages(); i++) {
        body[i] = 1 + i;
      }
    }
    model.addAttribute("body", body);
    model.addAttribute("page", pages);
    model.addAttribute("thisnumber", pages.getNumber() + 1);
    if ((pages.getNumber() + 2) <= pages.getTotalPages()) {
      model.addAttribute("next", pages.getNumber() + 2);
    } else {
      model.addAttribute("next", pages.getNumber() + 1);
    }
    if (pages.getNumber() <= 0) {
      model.addAttribute("previous", 1);
    } else {
      model.addAttribute("previous", pages.getNumber());
    }


    return "role/allroles";
  }

  @RequestMapping(method = RequestMethod.GET, value = "editrole/{roleid}")
  public String updateRole(@PathVariable("roleid") Long roleid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    Role role = roleRepository.findRoleByRoleId(roleid);
    List<Privilege> privileges = privilegeRepository.findAll();
    model.addAttribute("role", role);
    model.addAttribute("privileges", privileges);
    logger.info("role to edited: " + role.getRoleName());

    return "role/editrole";
  }

  @RequestMapping(method = RequestMethod.POST, value = "editrole/{roleid}")
  public String updateRole(@PathVariable("roleid") Long roleid, Role roleedit, Model mv) {

    Role roletoedit = roleRepository.findRoleByRoleName(roleedit.getRoleName());
    if (roletoedit != null && !roletoedit.getRoleId().equals(roleid)) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        mv.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        mv.addAttribute("userphoto", user.getPhoto());
        mv.addAttribute("userrole", user.getRole().getRoleName());
        mv.addAttribute("useremail", user.getEmail());
      }
      Role role = roleRepository.findRoleByRoleId(roleid);
      List<Privilege> privileges = privilegeRepository.findAll();
      mv.addAttribute("role", role);
      mv.addAttribute("privileges", privileges);
      mv.addAttribute("existedCode", role.getRoleName());
      return "role/editrole";
    } else {
      Role role = roleRepository.findRoleByRoleId(roleid);
      role.setRoleName(roleedit.getRoleName());
      role.setPrivileges(roleedit.getPrivileges());
      roleRepository.save(role);
      logger.info("role after edit: " + role.getRoleName());

      return "redirect:/role/allroles/1";
    }

  }

  @RequestMapping(method = RequestMethod.GET, value = "createrole")
  public String createRole(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    List<Privilege> privileges = privilegeRepository.findAll();
    model.addAttribute("privileges", privileges);
    return "role/addrole";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createrole")
  public String createRole(Role roleedit, Model model) {

    Role role = roleRepository.findRoleByRoleName(roleedit.getRoleName());
    if (role == null) {
      role = new Role(roleedit.getRoleName());
      role.setPrivileges(roleedit.getPrivileges());
      roleRepository.save(role);
      return "redirect:/role/allroles/1";
    } else {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        model.addAttribute("userphoto", user.getPhoto());
        model.addAttribute("userrole", user.getRole().getRoleName());
        model.addAttribute("useremail", user.getEmail());
      }
      List<Privilege> privileges = privilegeRepository.findAll();
      model.addAttribute("privileges", privileges);
      model.addAttribute("existedCode", roleedit.getRoleName());
      return "role/addrole";
    }

  }

  @GetMapping(value = "allprivileges/{pageNo}")
  public String Privileges(@PathVariable(value = "pageNo") Integer pageNo, Model model,
      HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");
    }
    if (pageNo == null) {
      pageNo = 1;
    }
    int pageSize = 20;
    Page<Privilege> pages = privilegeService.findPaginated(pageNo, pageSize);
    model.addAttribute("privileges", pages.getContent());
    int[] body;
    if (pages.getTotalPages() > 7) {
      int totalPages = pages.getTotalPages();
      int pageNumber = pages.getNumber() + 1;
      int[] head = (pageNumber > 4) ? new int[] {1, -1} : new int[] {1, 2, 3};
      int[] bodyBefore = (pageNumber > 4 && pageNumber < totalPages - 1)
          ? new int[] {pageNumber - 2, pageNumber - 1}
          : new int[] {};
      int[] bodyCenter =
          (pageNumber > 3 && pageNumber < totalPages - 2) ? new int[] {pageNumber} : new int[] {};
      int[] bodyAfter = (pageNumber > 2 && pageNumber < totalPages - 3)
          ? new int[] {pageNumber + 1, pageNumber + 2}
          : new int[] {};
      int[] tail = (pageNumber < totalPages - 3) ? new int[] {-1, totalPages}
          : new int[] {totalPages - 2, totalPages - 1, totalPages};
      body = Utils.merge(head, bodyBefore, bodyCenter, bodyAfter, tail);

    } else {
      body = new int[pages.getTotalPages()];
      for (int i = 0; i < pages.getTotalPages(); i++) {
        body[i] = 1 + i;
      }
    }
    model.addAttribute("body", body);
    model.addAttribute("page", pages);
    model.addAttribute("thisnumber", pages.getNumber() + 1);
    if ((pages.getNumber() + 2) <= pages.getTotalPages()) {
      model.addAttribute("next", pages.getNumber() + 2);
    } else {
      model.addAttribute("next", pages.getNumber() + 1);
    }
    if (pages.getNumber() <= 0) {
      model.addAttribute("previous", 1);
    } else {
      model.addAttribute("previous", pages.getNumber());
    }

    return "role/allprivileges";
  }

  @RequestMapping(method = RequestMethod.GET, value = "editprivilege/{privilegeid}")
  public String updatePrivilege(@PathVariable("privilegeid") Long privilegeid, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    Privilege privilege = privilegeRepository.findPrivilegeByprivilegeId(privilegeid);
    model.addAttribute("privilege", privilege);
    return "role/editprivilege";
  }

  @RequestMapping(method = RequestMethod.POST, value = "editprivilege/{privilegeid}")
  public String updatePrivilege(@PathVariable("privilegeid") Long privilegeid, Privilege privilege,
      Model mv) {

    Privilege privilegetoedit =
        privilegeRepository.findPrivilegeByPrivilegeName(privilege.getPrivilegeName());
    if (privilegetoedit != null && !privilegetoedit.getPrivilegeId().equals(privilegeid)) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        mv.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        mv.addAttribute("userphoto", user.getPhoto());
        mv.addAttribute("userrole", user.getRole().getRoleName());
        mv.addAttribute("useremail", user.getEmail());
      }
      Privilege privilegetoreedit = privilegeRepository.findPrivilegeByprivilegeId(privilegeid);
      mv.addAttribute("privilege", privilegetoreedit);
      mv.addAttribute("existedCode", privilegetoreedit.getPrivilegeName());
      return "role/editprivilege";
    } else {
      Privilege privilegetoredit = privilegeRepository.findPrivilegeByprivilegeId(privilegeid);
      privilegetoredit.setPrivilegeName(privilege.getPrivilegeName());
      privilegeRepository.save(privilegetoredit);
      return "redirect:/role/allprivileges/1";
    }

  }

  @RequestMapping(method = RequestMethod.GET, value = "createprivilege")
  public String createPrivilege(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
    }

    return "role/addprivilege";
  }

  @RequestMapping(method = RequestMethod.POST, value = "createprivilege")
  public String createPrivilege(Privilege privilegecreate, Model model) {

    Privilege privilege =
        privilegeRepository.findPrivilegeByPrivilegeName(privilegecreate.getPrivilegeName());
    if (privilege == null) {
      privilege = new Privilege(privilegecreate.getPrivilegeName());
      privilegeRepository.save(privilege);
      return "redirect:/role/allprivileges/1";
    } else {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        String currentUser = authentication.getName();
        User user = userRepository.findUsersByEmail(currentUser);
        model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
        model.addAttribute("userphoto", user.getPhoto());
        model.addAttribute("userrole", user.getRole().getRoleName());
        model.addAttribute("useremail", user.getEmail());
      }

      model.addAttribute("existedCode", privilegecreate.getPrivilegeName());
      return "role/addprivilege";
    }

  }

}
