package crm.chifco.com.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import crm.chifco.com.configuration.MySimpleUrlAuthenticationSuccessHandler;
import crm.chifco.com.crmMobile.JwtService;
import crm.chifco.com.crmMobile.NewAppJwtAuthenticationFilter;
import crm.chifco.com.netyTv.JwtAuthenticationFilterNetyTV;
import crm.chifco.com.netyTv.JwtServiceNetyTv;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableJpaAuditing
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  @Qualifier("customUserDetailsService")
  private UserDetailsService customUserDetailsService;

  @Autowired
  private AccessDeniedHandler accessDeniedHandler;

  @Value("${security.enable-csrf}")
  private boolean csrfEnabled;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private JwtServiceNetyTv jwtServiceNetyTv;

  @Bean
  public NewAppJwtAuthenticationFilter newAppJwtAuthenticationFilter() {
    return new NewAppJwtAuthenticationFilter();
  }

  @Bean
  public JwtAuthenticationFilterNetyTV jwtAuthenticationFilterNetyTV() {
    return new JwtAuthenticationFilterNetyTV();
  }

  @Bean
  public WebJwtAuthFilter webJwtAuthFilter() {
    return new WebJwtAuthFilter();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:5000",
        "http://localhost:5173",
        "http://localhost:3000",
        "https://*.replit.dev",
        "https://*.repl.co",
        "https://*.replit.app"
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "X-Requested-With", "Accept",
        "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers",
        "X-CSRF-TOKEN", "X-XSRF-TOKEN"
    ));
    configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().configurationSource(corsConfigurationSource());

    if (!csrfEnabled) {
      http.csrf().disable();
    }

    http.sessionManagement().maximumSessions(1)
        .expiredUrl("/sessionExpired.html")
        .sessionRegistry(sessionRegistry());
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        .invalidSessionUrl("/login?invalidsession=true")
        .sessionFixation().migrateSession();

    http.addFilterBefore(newAppJwtAuthenticationFilter(),
        UsernamePasswordAuthenticationFilter.class).authorizeRequests()
        .antMatchers("/mobileapp/auth/**", "/mobileapp/test/public").permitAll()
        .antMatchers("/mobileapp/**").authenticated().and().sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(jwtAuthenticationFilterNetyTV(),
        UsernamePasswordAuthenticationFilter.class).authorizeRequests()
        .antMatchers("/netytv/auth/**", "/netytv/test/public").permitAll()
        .antMatchers("/netytv/**").authenticated().and().sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(webJwtAuthFilter(),
        UsernamePasswordAuthenticationFilter.class);

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    http.sessionManagement().sessionFixation().migrateSession();
    http.sessionManagement().invalidSessionUrl("/login?invalidsession=true").maximumSessions(1)
        .expiredUrl("/sessionExpired.html").sessionRegistry(sessionRegistry());

    http.authorizeRequests()
        .antMatchers("/actuator/**", "/css/**", "/js/**", "/img/**", "/vendor/**", "/wss/**",
            "/ws/**", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html", "/**/*.pdf")
        .permitAll()
        .antMatchers("/", "/validateToken").permitAll()
        .antMatchers("/api/auth/**").permitAll()
        .antMatchers("/auth/**").permitAll()
        .antMatchers("/api/**", "/acs/**", "/Smstemplate/**").permitAll()
        .antMatchers("/admin/**", "/role/**")
        .hasAnyAuthority("WRITE_ADMINISTRATEUR", "READ_USER_LIST", "ADD_USER", "VIEW_DASHBORD_ADMIN")
        .antMatchers("/payement/**")
        .hasAnyAuthority("READ_PAYED_INVOICE_ALL", "INVOICE_PAYMENT",
            "READ_RETAIL_SUMMARY_LIST_ALL", "READ_RETAIL_SUMMARY_LIST_AREA",
            "VIEW_MONTANT_ENCAISSE", "CREATE_SLIP")
        .antMatchers("/others/**").hasAnyAuthority("READ_DASHBORD_OTHER", "VIEW_DASHBORD_OTHER")
        .anyRequest().authenticated()
        .and().formLogin().loginPage("/login").loginProcessingUrl("/login")
        .successHandler(myAuthenticationSuccessHandler()).failureUrl("/login?error=true")
        .permitAll()
        .and().logout()
        .deleteCookies("JSESSIONID").logoutUrl("/logout")
        .logoutSuccessHandler(logoutSuccessHandler()).permitAll()
        .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler);
  }

  @Bean
  public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
    return new MySimpleUrlAuthenticationSuccessHandler();
  }

  @Bean
  public LogoutSuccessHandler logoutSuccessHandler() {
    return (request, response, authentication) -> response.sendRedirect("/login");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    authManagerBuilder.userDetailsService(customUserDetailsService)
        .passwordEncoder(bCryptPasswordEncoder());
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }
}
