package crm.chifco.com.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import crm.chifco.com.model.Commande;
import crm.chifco.com.model.User;
import crm.chifco.com.repository.CommandeRepository;
import crm.chifco.com.repository.UserRepository;
import crm.chifco.com.service.CommandeService;

@Controller
@RequestMapping(value = "commande/*")
public class CommandeController {
  private final Logger logger = LogManager.getLogger(this.getClass());
  @Autowired
  CommandeRepository commandeRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  CommandeService commandeService;

  @GetMapping(value = "allcommandes/{pageNo}/{pageSize}")
  public String clients(@PathVariable(value = "pageNo") Integer pageNo,
      @PathVariable(value = "pageSize") Integer pageSize, Model model, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      final int[] PAGE_SIZES = {20, 50, 100};

      String currentUser = authentication.getName();
      User user = userRepository.findUsersByEmail(currentUser);
      model.addAttribute("userfullname", user.getLastName() + " " + user.getFirstName());
      model.addAttribute("userphoto", user.getPhoto());
      model.addAttribute("userrole", user.getRole().getRoleName());
      model.addAttribute("useremail", user.getEmail());
      request.getSession().setAttribute("listedes_ids", "");

      if (pageNo == null) {
        pageNo = 1;
      }
      int evalPageSize;

      if (pageSize == null) {
        evalPageSize = 20;
      } else {
        evalPageSize = pageSize;
      }

      Page<Commande> pages = commandeService.findPaginated(pageNo, pageSize);

      int[] body;
      assert pages != null;
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
      model.addAttribute("selectedPageSize", evalPageSize);
      model.addAttribute("pageSizes", PAGE_SIZES);
      model.addAttribute("currentPage", pageNo);
      model.addAttribute("totalPages", pages.getTotalPages());
      model.addAttribute("totalElements", pages.getTotalElements());
      model.addAttribute("commandes", pages.getContent());
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
    }

    return "commande/allcommandes";
  }
}
