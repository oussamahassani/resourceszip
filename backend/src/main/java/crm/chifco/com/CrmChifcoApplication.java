package crm.chifco.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan(basePackages = "crm.chifco.com", excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "crm\\.chifco\\.com\\.controller\\..*"))
@EnableScheduling
public class CrmChifcoApplication {

  public static void main(String[] args) {
    SpringApplication.run(CrmChifcoApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
