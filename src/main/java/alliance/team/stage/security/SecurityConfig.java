package alliance.team.stage.security;

import alliance.team.stage.token.JWTUtil;
import alliance.team.stage.token.JWTUtilFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.Properties;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return
                http
                        .csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(
                                authorize ->
                                        authorize.requestMatchers(POST, "/user/inscription").hasRole("ADMIN")
                                                .requestMatchers(DELETE, "/user/deleteUser/{mail}").hasRole("ADMIN")
                                                .requestMatchers(POST, "/user/passwordForgeted/{email}").hasRole("ADMIN")
                                                .requestMatchers(POST, "/user/activation").permitAll()
                                                .requestMatchers(POST, "/user/connexion").permitAll()
                                                .requestMatchers(POST, "/annonces/ajoutAnnonce").hasRole("ADMIN")
                                                .requestMatchers(DELETE, "/annonces/{id}").permitAll()
                                                .requestMatchers(POST, "/user/initialisePassword").permitAll()
                                                .requestMatchers(POST, "/user/deactivate").permitAll()
                                                .requestMatchers(GET, "/annonces").permitAll()
                                                .requestMatchers("/reservation/**").hasAnyRole("GESTIONNAIRE","ADMIN")
                                                .requestMatchers("/salle/**").hasAnyRole("GESTIONNAIRE","ADMIN")
                                                .anyRequest().authenticated()
                        )
                        .addFilterBefore(new JWTUtilFilter(jwtUtil()), UsernamePasswordAuthenticationFilter.class) // Ajout du filtre
                        .httpBasic(Customizer.withDefaults())
                        .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(this.passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("cn7061611@gmail.com");
        mailSender.setPassword("gezi qied xwkv nwls");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}