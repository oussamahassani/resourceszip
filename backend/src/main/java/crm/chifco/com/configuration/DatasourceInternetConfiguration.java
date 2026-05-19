

package crm.chifco.com.configuration;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "crm.chifco.com.radius.repository",
    entityManagerFactoryRef = "internetEntityManagerFactory", //
    transactionManagerRef = "internetTransactionManager") //
public class DatasourceInternetConfiguration {

  @Bean
  @ConfigurationProperties("app.datasource.internet")
  public DataSourceProperties DatasourceInternetProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("app.datasource.internet.configuration")
  public DataSource internetDataSource() {
    return DatasourceInternetProperties().initializeDataSourceBuilder().type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "internetEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean internetEntityManagerFactory(
      EntityManagerFactoryBuilder builder) {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

    vendorAdapter.setShowSql(false);
    vendorAdapter.setGenerateDdl(false);

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    Properties jpaProperties = new Properties();

    jpaProperties.put("hibernate.ddl-auto", "update");

    factory.setJpaProperties(jpaProperties);
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPackagesToScan("crm.chifco.com.radius.model");
    factory.setDataSource(internetDataSource());
    return factory;

  }

  @Bean(name = "internetTransactionManager")
  public PlatformTransactionManager InternetTransactionManager(
      final @Qualifier("internetEntityManagerFactory") LocalContainerEntityManagerFactoryBean InternetEntityManagerFactory) {
    return new JpaTransactionManager(InternetEntityManagerFactory.getObject());
  }

}
