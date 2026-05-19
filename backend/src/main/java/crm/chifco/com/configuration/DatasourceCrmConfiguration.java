

package crm.chifco.com.configuration;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "crm.chifco.com.repository",
    entityManagerFactoryRef = "CrmEntityManagerFactory",
    transactionManagerRef = "CrmTransactionManager")
public class DatasourceCrmConfiguration {

  @Bean
  @Primary
  @ConfigurationProperties("app.datasource.crm")
  public DataSourceProperties DatasourceCrmProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Primary
  @ConfigurationProperties("app.datasource.crm.configuration")
  public DataSource crmDataSource() {
    return DatasourceCrmProperties().initializeDataSourceBuilder().type(HikariDataSource.class)
        .build();
  }

  @Primary
  @Bean(name = "CrmEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean crmEntityManagerFactory(
      EntityManagerFactoryBuilder builder) {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

    vendorAdapter.setShowSql(false);
    vendorAdapter.setGenerateDdl(true);
    vendorAdapter.setDatabase(Database.SQL_SERVER);
    vendorAdapter.setDatabasePlatform("org.hibernate.dialect.SQLServer2012Dialect");
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    Properties jpaProperties = new Properties();
    jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
    jpaProperties.put("hibernate.ddl-auto", "update");
    jpaProperties.put("hibernate.physical_naming_strategy",
        "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
    factory.setJpaProperties(jpaProperties);
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPackagesToScan("crm.chifco.com.model");
    factory.setDataSource(crmDataSource());
    return factory;

  }

  @Primary
  @Bean
  public PlatformTransactionManager CrmTransactionManager(
      final @Qualifier("CrmEntityManagerFactory") LocalContainerEntityManagerFactoryBean CrmEntityManagerFactory) {
    return new JpaTransactionManager(CrmEntityManagerFactory.getObject());
  }

}
