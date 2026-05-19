package crm.chifco.com.security;

import crm.chifco.com.configuration.MySimpleUrlAuthenticationSuccessHandler;
import crm.chifco.com.crmMobile.JwtService;
import crm.chifco.com.crmMobile.NewAppJwtAuthenticationFilter;
import crm.chifco.com.netyTv.JwtAuthenticationFilterNetyTV;
import crm.chifco.com.netyTv.JwtServiceNetyTv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Value("${security.enable-csrf:false}")
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

    http.exceptionHandling()
        .authenticationEntryPoint(unauthorizedHandler)
        .accessDeniedHandler(accessDeniedHandler)
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .antMatchers("/api/auth/**", "/mobileapp/auth/**", "/netytv/auth/**", "/actuator/**").permitAll()
        .antMatchers("/admin/**").permitAll()

        .antMatchers("/api/**").authenticated()
        .anyRequest().permitAll();

    http.addFilterBefore(newAppJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(jwtAuthenticationFilterNetyTV(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(webJwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
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
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}
