package cmmteam.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/animals", "/api/animals/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/animals").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/animals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/animals/**").authenticated()
                        .requestMatchers("/api/users/profile", "/api/users/password").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/adoption-applications").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/adoption-applications/{id}/review").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/adoption-applications/{id}/cancel").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/adoption-applications/my-applications").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/adoption-applications/admin/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/adoption-applications/{id}").authenticated()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}