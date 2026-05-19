package crm.chifco.com.security;

import crm.chifco.com.model.Privilege;
import crm.chifco.com.model.Role;
import crm.chifco.com.model.User;
import crm.chifco.com.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
  @Autowired
  private UserService userService;

  private final Logger logger = LogManager.getLogger(this.getClass());

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username.trim().isEmpty()) {
      logger.error("email is empty");
      throw new UsernameNotFoundException("email is empty");
    }

    User user = userService.findUsersByEmail(username);

    if (user == null) {
      logger.error("email : " + username + " not found");
      throw new UsernameNotFoundException("email : " + username + " not found");
    }
    if (!user.isEnabled()) {
      throw new DisabledException("user: " + username + " is disabled");
    }
    return new org.springframework.security.core.userdetails.User(user.getEmail(),
        user.getPassword(), getAuthority(user.getRole()));
  }

  private List<GrantedAuthority> getAuthority(Role role) {
    // return Collections.singletonList(new SimpleGrantedAuthority(role));
    return getGrantedAuthorities(getPrivileges(role));
  }

  private List<String> getPrivileges(Role role) {

    List<String> privileges = new ArrayList<>();
    privileges.add(role.getRoleName());
    List<Privilege> collection = new ArrayList<>(role.getPrivileges());

    for (Privilege item : collection) {
      privileges.add(item.getPrivilegeName());
    }
    return privileges;
  }

  private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    for (String privilege : privileges) {
      authorities.add(new SimpleGrantedAuthority(privilege));
    }
    return authorities;
  }
}
