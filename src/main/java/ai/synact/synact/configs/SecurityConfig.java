package ai.synact.synact.configs;

import ai.synact.synact.services.CustomOAuth2UserService;
import ai.synact.synact.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor   // <-- Lombok will generate a constructor for all final fields
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService oauth2UserService;

    /**
     * Configures security rules, authentication, OAuth2 login, sessions, and logout.
     */
    @Bean
//    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Public and protected route settings
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/main/**",
                                "/auth/login",
                                "/auth/verify",            // e.g. GET /auth/verify?token=...
                                "/auth/verify/**",         // resend, etc.
                                "/importers/**"            // public registration flow
                        ).permitAll()

                        // Static assets
                        .requestMatchers(
                                "/css/**", "/js/**", "/images/**", "/fonts/**",
                                "/web/**", "/assets/**", "/favicon.ico", "/robots.txt", "/sitemap.xml",
                                "/error"                    // leave error page public
                        ).permitAll()

                        // Admin area
                        .requestMatchers(
                                "/app/**"
                        ).hasRole("IMPORTER")

                        // Everything else requires login
                        .anyRequest().authenticated()
                )
                // Login form setup
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/app/dashboard", true)
                        .permitAll()
                )
                // OAuth2 login setup
                .oauth2Login(oauth -> oauth
                        .loginPage("/auth/login")
                        .defaultSuccessUrl("/app/dashboard", true)
                        .userInfoEndpoint(u -> u.userService(oauth2UserService))
                )
                // Session management for CSRF, login, etc.
                .sessionManagement(session -> session
                        // Create sessions only if required (for CSRF, login, etc.)
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                // Logout settings
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")                 // POST (default)
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me") // if remember-me is enabled
                        .permitAll()
                )
                .sessionManagement(sm -> sm
                        .invalidSessionUrl("/auth/login?timeout")  // if session is already invalid
                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                        .maximumSessions(1)
                        .expiredUrl("/auth/login?expired")     // when kicked out due to concurrency or expiry
                );

        return http.build();
    }

    /** Ensures Spring is notified of session lifecycle (needed for expiredUrl to fire reliably). */
    @Bean
    public org.springframework.security.web.session.HttpSessionEventPublisher httpSessionEventPublisher() {
        return new org.springframework.security.web.session.HttpSessionEventPublisher();
    }

    // 2. User 전용 Security 설정
//    @Bean
//    @Order(2)
//    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/user/**")
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().hasRole("USER")
//                )
//                .formLogin(form -> form
//                        .loginPage("/user/login")
//                        .loginProcessingUrl("/user/process-login")
//                        .defaultSuccessUrl("/user/dashboard", true)
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/user/logout")
//                        .logoutSuccessUrl("/user/login?logout")
//                );
//        return http.build();
//    }

}
