package com.rabbitfarm.config;

import com.rabbitfarm.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    // ===== Encodage des mots de passe =====
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ===== Provider d'authentification =====
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ===== AuthenticationManager =====
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ===== Règles de sécurité HTTP =====
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // CORRECTION : Ajout de "/" pour rendre la Landing Page accessible publiquement
                        .requestMatchers("/", "/auth/login", "/auth/register", "/css/**", "/js/**", "/images/**").permitAll()
                        // Pages admin uniquement
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Tout le reste nécessite une connexion
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")           // Page de login personnalisée
                        .loginProcessingUrl("/auth/login")  // URL de traitement du formulaire
                        .usernameParameter("email")         // Champ email au lieu de username
                        .passwordParameter("motDePasse")
                        .successHandler(authSuccessHandler())
                        .failureUrl("/auth/login?erreur=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?deconnecte=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)               // Une seule session par utilisateur
                        .expiredUrl("/auth/login?expire=true")
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    // ===== Handler succès : mise à jour dernière connexion =====
    @Bean
    public AuthenticationSuccessHandler authSuccessHandler() {
        return (request, response, authentication) -> {
            // Redirection vers le dashboard après login
            response.sendRedirect("/dashboard");
        };
    }
}