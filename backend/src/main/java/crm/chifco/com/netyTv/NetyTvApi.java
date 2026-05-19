package crm.chifco.com.netyTv;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/netytv")
public class NetyTvApi {
  @Autowired
  private JwtServiceNetyTv jwtServiceNetyTv;

  @PostMapping("/refresh-token")
  public Map<String, Object> refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    return jwtServiceNetyTv.refreshToken(request, response);
  }
}
