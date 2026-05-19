package crm.chifco.com.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
  @Value("${pathPhoto}")
  private String pathPhoto;
  @Value("${pathFacture}")
  private String pathFacture;
  @Value("${pathDemandesAbonnement}")
  private String pathDemandesAbonnement;
  @Value("${pathFicheStock}")
  private String pathFicheStock;
  @Value("${pathBordereau}")
  private String pathBordereau;
  @Value("${pathuploadxlsx}")
  private String pathuploadxlsx;

  @Value("${pathRecu}")
  private String pathRecu;

  @Value("${pathModemxlsx}")
  private String pathModemxlsx;

  @Value("${pathCommission}")
  private String pathCommission;

  @Value("${pathParinage}")
  private String pathParinage;
  @Value("${pathReclamation}")
  private String pathReclamation;

  private static final String FILE = "file:"; // Compliant

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry.addResourceHandler("/photos/**").addResourceLocations(FILE + pathPhoto);
    registry.addResourceHandler("/facture/**").addResourceLocations(FILE + pathFacture);
    registry.addResourceHandler("/demandeabonnement/**")
        .addResourceLocations(FILE + pathDemandesAbonnement);
    registry.addResourceHandler("/fichestock/**").addResourceLocations(FILE + pathFicheStock);
    registry.addResourceHandler("/bordereau/**").addResourceLocations(FILE + pathBordereau);
    registry.addResourceHandler("/xlsfile/**").addResourceLocations(FILE + pathuploadxlsx);
    registry.addResourceHandler("/xlsModeme/**").addResourceLocations(FILE + pathModemxlsx);
    registry.addResourceHandler("/recu/**").addResourceLocations(FILE + pathRecu);
    registry.addResourceHandler("/commission/**").addResourceLocations(FILE + pathCommission);
    registry.addResourceHandler("/parinage/**").addResourceLocations(FILE + pathParinage);
    registry.addResourceHandler("/reclamation/**").addResourceLocations(FILE + pathReclamation);

  }
}
